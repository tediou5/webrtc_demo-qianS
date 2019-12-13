package cn.teclub.ha.request;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.lib.StExpUserError;
import cn.teclub.ha.net.StExpConnectionLoss;
import cn.teclub.ha.request.StNetPacket.ConstructPacketFailure;
import cn.teclub.ha.request.StNetPacket.ExpReceiveTooFewBytes;


/**
 * 
 * <pre>
 * NOT-BLOCKING Socket IO.
 * 
 * Used by HA server to handle several clients in ONE thread. 
 * Non-blocking TCP socket, SocketChannel, is used. 
 * 
 * </pre>
 * 
 * @author mancook
 *
 */
public class StSocketChannel 
		extends ChuyuObj 
		implements ChuyuObj.DumpAttribute, StSocket4Pkt
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	public static final int MAX_RECV_BUF = StConst.PKT_MAX_LENGTH;
	public static final int MAX_SEND_BUF = StConst.PKT_MAX_LENGTH;
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	
	
	protected final SocketChannel 	sockChannelRemote;
	protected final ByteBuffer 		recvBuf;
	protected final ByteBuffer 		sendBuf;
	protected final LinkedList<StNetPacket> recvList;
    
	/**
	 * Constructor
	 * @param sockClient
	 * @throws IOException 
	 */
    public StSocketChannel(SocketChannel sockChannelRemote) throws IOException{
		this.sockChannelRemote = sockChannelRemote;
		this.sockChannelRemote.configureBlocking(false);
		
		this.recvBuf = ByteBuffer.allocate(MAX_RECV_BUF);
		this.sendBuf = ByteBuffer.allocate(MAX_SEND_BUF);
		this.recvList= new LinkedList<StNetPacket>();
		stLog.trace("==== NON-BLOCK Socket is Used ====");
	}
	
    
    private boolean closeFlag = false;
    
	@Override
	public boolean isClosed() {
		return closeFlag;
	}
	
    public void close() {
    	stLog.debug("Close socket channel to: " + getSrcAddress().getHostAddress() + ":" + getSrcPort());
    	util.closeSocketCh(sockChannelRemote);
    	closeFlag = true;
    }
    
    
    public void sendPacket(StNetPacket pkt) throws IOException {
    	util.assertTrue(pkt.isFrozen(), "CANNOT send a un-frozen packet");
        ByteBuffer buffer = pkt.getBuffer();
        buffer.rewind(); 

		final int CNT = buffer.remaining();
		int n = 0;
		for(int i=0; i<100; i++){
			int count = this.sockChannelRemote.write(buffer);
			n += count; 
			stLog.trace(  "[CS] has sent "+ count +" B to remote client");
			if(n>= CNT){
				break;
			}
			stLog.debug("[CS] sent "+ n +" B ( total: "+CNT+" B ); send left in next loop...");
			util.sleep(50);
		}
		if(n < CNT){
			throw new IOException("[S.T] fail to send the whole packet " + 
					"-- sent "+ n +" B ( total: "+CNT+" B )");
		}
		stLog.trace(pkt.makeSocketFlow(true, true));
		if(!pkt.isStatusChecking()){
			stLog.debug(pkt.makeSocketFlow(true, false));
		}
    }
    
    
    /**
     * @throws IOException
     * @throws StExpUserError
     * @throws StExpConnectionLoss 
     */
    private void recvAll() throws IOException, StExpConnectionLoss
    {
    	// clear receive buffer
    	// - position at zero;
    	// - limit to capacity;
		recvBuf.clear();
		
		// NON-BLOCK Socket Reading
		// if no date in this socket channel, 'read' returns 0 immediately. 
		int recv_num  = sockChannelRemote.read(recvBuf);
		if(recv_num == 0){
			return;
		}
		if(recv_num == -1 ){
			throw new StExpConnectionLoss("Fail to read from socket channel. END OF STREAM is reached.");
		}
		stLog.trace(">> Got: " + recv_num + " bytes from socket");
		util.assertTrue(recv_num > 0, "Unexpected Value. recv_num: " + recv_num);
		
		recvBuf.limit(recv_num);
		recvBuf.position(0);
		
		try{
			while(true){
				if(recvBuf.remaining() <= 0){
					break;
				}
				StNetPacket pkt = StNetPacket.buildFromBuffer(recvBuf);
				//stLog.debug(pkt.makeSocketFlow(false, false));
				if(!pkt.isStatusChecking()){
					stLog.debug(pkt.makeSocketFlow(false, false));
				}
				stLog.trace(pkt.makeSocketFlow(false, true));
				recvList.add(pkt);
			}
		}catch(ExpReceiveTooFewBytes e){
			e.printStackTrace();
	        stLog.error(util.getExceptionDetails(e, "Not Enough Bytes to Build a Packet!"));
	        
	        // [2016-8-19] 
	        // Q: Will this happen? A NetPacket is cut into several TCP packet.
	        // A: It happens on mobile phone in mobile network. It may happen on Ethernet! 
	        //    As a result, wait for more bytes like StSocket does.
	        // 
	        stLog.fatal("These bytes are dropped. A Packet is Lost!");
	        stLog.warn("todo: wait for more bytes incoming...");
		}catch (ConstructPacketFailure e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Stop recv from this socket channel!"));
			sockChannelRemote.close();
			throw new IOException("[cook] " + e.getMessage() );
		}
    }
    
    
    /**
     * NON-BLOCKING Socket IO, try to receive an HA packet. 
     * 
     * @return received HA packet
     * 
     * @throws IOException
     * @throws StExpConnectionLoss 
     */
    public StNetPacket recvPacket() throws IOException, StExpConnectionLoss
    {
    	//stLog.debug(">>>> try to get an HA Packet"); 
    	this.recvAll();
		StNetPacket pkt= this.recvList.poll();
		if(null == pkt){
			return null;
		}
		
		stLog.trace("return the oldest received packet");
		return pkt;
	}
    
    
    /**
     * Get Internet address of remote client.
     * @return
     */
    public InetAddress getSrcAddress(){
    	Socket sock = this.sockChannelRemote.socket();
    	return sock.getInetAddress();
    }
    
    /**
     * Get TCP port of remote client.
     * @return
     */
    public int getSrcPort(){
      	Socket sock = this.sockChannelRemote.socket();
      	return sock.getPort();
    }
    
    /**
     * get local address
     * @return
     */
    public InetAddress getDstAddress(){
    	Socket sock = this.sockChannelRemote.socket();
    	return sock.getLocalAddress();
    }
    public int getDstPort(){
      	Socket sock = this.sockChannelRemote.socket();
      	return sock.getLocalPort();
    }

    
	@Override
	public void dumpSetup() {
		this.dumpSetTitle(" {StSocketChannel} ");
    	this.dumpAddLine("Remote Address: " + this.getSrcAddress().getHostAddress());
    	this.dumpAddLine("Remote Port:   "  + this.getSrcPort());
    	this.dumpAddLine("Local  Address: " + this.getDstAddress().getHostAddress());
    	this.dumpAddLine("Local  Port:   "  + this.getDstPort());
	}

}
