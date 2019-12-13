package cn.teclub.ha.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.net.StExpConnectionLoss;
import cn.teclub.ha.request.StNetPacket.ConstructPacketFailure;
import cn.teclub.ha.request.StNetPacket.ExpReceiveTooFewBytes;


/**
 * Socket Wrapper to receive & send a net packet.
 * 
 * @author mancook
 *
 */
public class StSocket 
	extends ChuyuObj 
	implements ChuyuObj.DumpAttribute, StSocket4Pkt
{
	private final static int RECV_BUF_SIZE = 1024*64;
	
	private final Socket 		sock;
	private final InputStream 	ins;
	private final OutputStream 	ous;
	private final byte[]  		recvBufArr 	= new byte[RECV_BUF_SIZE];
	private final ByteBuffer  	recvBuf 	= ByteBuffer.allocate(RECV_BUF_SIZE * 2);
	private final ByteBuffer  	leftBuffer 	= ByteBuffer.allocate(RECV_BUF_SIZE * 2);

	
	/**
	 * Constructor
	 * @param ssl_sock
	 * @throws IOException
	 */
	public StSocket(Socket sock) throws IOException{
		this.sock = sock;
		this.ins = sock.getInputStream();
		this.ous = sock.getOutputStream();
	}
	
	
    @SuppressWarnings("unused")
	private StringBuffer dumpBuffers(){
    	StringBuffer sbuf = new StringBuffer(256);
    	sbuf.append("\n\t Buffer Name      Capacity  Position,Limit,Remaining ");
    	sbuf.append("\n\t ~~~~~~~~~~~      ~~~~~~~~  ~~~~~~~~~~~~~~~~~~~~~~~~ ");
    	sbuf.append("\n\t recvBuffer       " + recvBuf.capacity() + ",    " + recvBuf.position() + "," + recvBuf.limit() + "," + recvBuf.remaining());
       	sbuf.append("\n\t leftBuffer       " + leftBuffer.capacity() + ",    " + leftBuffer.position() + "," + leftBuffer.limit() + "," + leftBuffer.remaining());
    	sbuf.append("\n");
    	return sbuf;
    }
    
    
    public boolean isClosed(){
    	return sock.isClosed();
    }
	
    
	public void close() {
		try {
			if(sock.isClosed()){
				stLog.warn("Socket has been closed!");
				return;
			}
			sock.close();
			stLog.info("Socket is closed!");
		} catch (IOException e) {
			e.printStackTrace();
			stLog.fatal(util.getExceptionDetails(e, "IO exception when closing SSL socket!"));
			throw new StErrUserError("IO exception when closing socket!");
		}
	}
	
	protected final LinkedList<StNetPacket> recvList = new  LinkedList<StNetPacket>();
	
    private void recvAll() throws IOException, StExpConnectionLoss
    {
		// BLOCK Socket Reading
		// if no date in this socket channel, read() blocks
		int n = ins.read(recvBufArr);
		if(n == 0){
			stLog.warn("NO Bytes is Received!");
			return;
		}
		if(n == -1 ){
			throw new StExpConnectionLoss("Fail to read from socket. END OF STREAM is reached.");
		}
		stLog.trace(">> Got: " + n + " bytes from socket");
		util.assertTrue(n > 0, "Unexpected Value. recv_num: " + n);
		util.assertTrue(n <= RECV_BUF_SIZE, "[TODO] recv-bytes " + n + " exceeds recv-buf size " + RECV_BUF_SIZE);
		
		//stLog.info("##### 1) recvBuff: " + recvBuf.position() + "/" + recvBuf.limit() + "/" + recvBuf.capacity());
		recvBuf.put(recvBufArr, 0, n);
		int buf_pos = recvBuf.position();
		recvBuf.position(0);
		recvBuf.limit(buf_pos); // limit to "previous + current" received bytes
		//stLog.info("##### 2) recvBuff: " + recvBuf.position() + "/" + recvBuf.limit() + "/" + recvBuf.capacity());

		try{
			while(true){
				if(recvBuf.remaining() <= 0){
					break;
				}
				buf_pos = recvBuf.position();
				//stLog.info("##### 3) recvBuff: " + recvBuf.position() + "/" + recvBuf.limit() + "/" + recvBuf.capacity());
				StNetPacket pkt = StNetPacket.buildFromBuffer(recvBuf);
				stLog.debug(pkt.makeSocketFlow(false, false));
				stLog.trace(pkt.makeSocketFlow(false, true));
				/*
				if(!pkt.isStatusChecking()){
					stLog.debug(pkt.makeSocketFlow(false, false));
				}*/
				recvList.add(pkt);
			}
			
			stLog.debug("All bytes are used to build packet!");
			recvBuf.rewind();
			recvBuf.limit(recvBuf.capacity());
		}catch(ExpReceiveTooFewBytes e){
	        // [Theodore: 2016-08-20] This is NOT Error!
			//			 e.printStackTrace();
			//	         stLog.error(util.getExceptionDetails(e, "Not Enough Bytes to Build a Packet!"));
			//
			// This often happens on Android Device! 
	        // On MacBook PC, this never happens during Ping100 test cases.
			//
			stLog.warn(util.getExceptionDetails(e, "Too Few Bytes"));
			stLog.warn("==== No enough bytes for a net-packet. Receive more ...");
			recvBuf.position(buf_pos); // restore to last position
			leftBuffer.rewind();
			leftBuffer.limit(recvBuf.remaining());
			leftBuffer.put(recvBuf);
			leftBuffer.rewind();
			
			recvBuf.rewind();
			recvBuf.limit(recvBuf.capacity());
			recvBuf.put(leftBuffer);
		} catch (ConstructPacketFailure e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Stop recv from this socket!"));
			ins.close();
			throw new IOException("[cook] " + e.getMessage() );
		}
    }
	
	
	/**
	 * Block until a packet arrives.
	 * 
	 * @return
	 * @throws IOException 
	 * @throws StExpConnectionLoss 
	 */
	public  StNetPacket recvPacket() throws IOException, StExpConnectionLoss{
		final StNetPacket pkt0 = this.recvList.poll();
		if(pkt0 != null){
			stLog.trace("Get Previously Received Packet: " + pkt0 );
			return pkt0;
		}
		
		stLog.trace("Receive New Packet...");
		recvAll();
		
		StNetPacket pkt= recvList.poll();
		if(null == pkt){
			return null;
		}
		
		stLog.trace("Return the oldest received packet");
		return pkt;
		
		/*
		 * 2016-8-19: Following cause bug: 
		 * 		B16AUG1801
		 * 		- Client does not receive PING Packet, even tcpdump shows the packet arrives.
		 * 
		while(true){
			int n = ins.read(recvBufArr);
			stLog.trace("Byte number from (SSL) socket: " + n);
			if(n == -1){
				 throw new StExpConnectionLoss("Fail to read from socket channel. END OF STREAM is reached.");
			}
			if(n == 0){
				// TODO: a packet may be ready in previous receive loop!
				// JUST DO IT!
				
				continue;
			}
			if(n > 0){
				//stLog.info(dumpBuffers());
				recvBuf.put(recvBufArr, 0, n);
				int total_bytes = recvBuf.position();
				recvBuf.position(0);
				recvBuf.limit(total_bytes);
				try {
					StNetPacket pkt = StNetPacket.buildFromBuffer(recvBuf);
					
					// move left bytes to the beginning of recv-buffer
					leftBuffer.rewind();
					leftBuffer.limit(recvBuf.remaining());
					leftBuffer.put(recvBuf);
					leftBuffer.rewind();
					recvBuf.rewind();
					recvBuf.limit(recvBuf.capacity());
					recvBuf.put(leftBuffer);
					
					stLog.debug("Recv Packet: " + pkt.makeSocketFlow(false, false) );
					stLog.trace(pkt.makeSocketFlow(false, true));
					return pkt;
				} catch (ExpReceiveTooFewBytes e) {
					stLog.warn("==== No enough bytes for a net-packet. Receive more ...");
					recvBuf.limit(recvBuf.capacity());
					recvBuf.position(total_bytes);
				}
			}
		}//while
		*/
	}
	 
	
	public  void sendPacket(final StNetPacket pkt) throws IOException{
    	util.assertTrue(pkt.isFrozen(), "CANNOT send a un-frozen packet");
        ByteBuffer buffer = pkt.getBuffer();
        buffer.rewind(); 
        ous.write(buffer.array(),  0,  buffer.limit() );
        ous.flush(); // try to fix bug B15041401, but it helps nothing! B15041401 may NOT be a bug!
		stLog.debug("Sent Packet: " + pkt.makeSocketFlow(true, false) );
		stLog.trace(pkt.makeSocketFlow(true, true));
	}
	
	
	
    /**
     * Get Internet address of remote client.
     * @return
     */
    public InetAddress getSrcAddress(){
    	return sock.getInetAddress();
    }
    
    /**
     * Get TCP port of remote client.
     * @return
     */
    public int getSrcPort(){
      	return sock.getPort();
    }
    
    /**
     * get local address
     * @return
     */
    public InetAddress getDstAddress(){
    	return sock.getLocalAddress();
    }
    public int getDstPort(){
      	return sock.getLocalPort();
    }

    
	@Override
	public void dumpSetup() {
		this.dumpSetTitle(" {Packet Socket} ");
    	this.dumpAddLine("Remote Address: " + this.getSrcAddress().getHostAddress());
    	this.dumpAddLine("Remote Port:   "  + this.getSrcPort());
    	this.dumpAddLine("Local  Address: " + this.getDstAddress().getHostAddress());
    	this.dumpAddLine("Local  Port:   "  + this.getDstPort());
	}
	
	
}// EOF StSSLSocket

