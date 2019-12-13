package cn.teclub.ha.net.serv;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

import cn.teclub.common.ChuyuLog;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.lib.StExpBreak;
import cn.teclub.ha.lib.StExpFamily;
import cn.teclub.ha.lib.StExpUserError;
import cn.teclub.ha.lib.StLoopThread;
import cn.teclub.ha.net.StExpNet;
import cn.teclub.ha.net.StTransAddr;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StNetPacket.Command;


/**
 * runs in a independent thread or process
 * 
 * @author mancook
 *
 */
public class StStunUdpSrv extends StLoopThread 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS AND METHODS
	////////////////////////////////////////////////////////////////////////////	
	
    public static final int DEFAULT_STUN_PORT = 17703;
    
    public static void main(String[] args) throws IOException{
       	System.out.println("user.dir:  " + System.getProperty("user.dir") );
    	System.out.println("user.name: " + System.getProperty("user.name") );
    	System.out.println("user.home: " + System.getProperty("user.home") );
    	
    	// initialize log4j with specific config file
    	String log4j_cfg = System.getProperty("user.dir")  + "/local-runtime/conf/chuyu_log4j.cfg";
    	ChuyuLog.setLog4jConf(log4j_cfg);
    	ChuyuLog.getInstance();

    	StStunUdpSrv stun = new StStunUdpSrv(DEFAULT_STUN_PORT);
    	stun.start();
    }


	////////////////////////////////////////////////////////////////////////////
    // Instance members and methods
	////////////////////////////////////////////////////////////////////////////	

	DatagramSocket serverSocket 	= null;
	byte[] 			recvData	 	= new byte[StConst.PKT_MAX_LENGTH]; 
	ByteBuffer 		recvBuffer 		= ByteBuffer.wrap(recvData);

    /**
     * Constructor. <br/>
     * 
     * @throws SocketException
     */
    public StStunUdpSrv(int stun_port) throws SocketException {
    	serverSocket = new DatagramSocket(stun_port);
    }
	
	public void clear(){
		stLog.info(">>>>");
		serverSocket.close();
		try {
			this.stop();
		} catch (ExpLoopTimeout e) {
			stLog.error(util.getExceptionDetails(e, "Timeout when stopping loop"));
		}
	}
	

	/**
	 * Thread Loop
	 * 
	 * @throws IOException
	 * @throws StExpUserError 
	 * @throws StExpNet
	 */
	protected void loopOnce() throws StExpFamily {
		//
		// For simple UDP source code, See more at:
		//     http://systembash.com/content/a-simple-java-udp-server-and-udp-client/#sthash.yvE8scni.dpuf
		// 
		stLog.info("[STUN] listening on port: " + DEFAULT_STUN_PORT);
		
		this.recvBuffer.clear();
		DatagramPacket udpPacket= new DatagramPacket(recvData, recvData.length);
		try {
			serverSocket.receive(udpPacket);
		} catch (IOException e) {
			stLog.error(util.getExceptionDetails(e, "IO Exeption when receiving. "));
			// end the thread loop. i.e. end this simple-STUN server(UDP).
			throw new StExpBreak("IO Exeption when receiving."); 
		}
		this.recvBuffer.limit(udpPacket.getLength());
		InetAddress     clt_addr = udpPacket.getAddress();
		int             clt_port = udpPacket.getPort();
		
	    while(this.recvBuffer.remaining() > 0){
	    	StNetPacket recv_pkt = StNetPacket.buildFromBuffer(this.recvBuffer);
			//stLog.debug("#### Received a UDP packet on STURN port from remote address " + 
			//					udpPacket.getSocketAddress());
	    	util.assertTrue(
	    			recv_pkt.isTypeFlowFromClientToSrv() && recv_pkt.isTypeRequest(), 
	    			"Unexpected Packet: " + recv_pkt.dumpSimple());
	    	//this.assertTrue(recv_pkt.getCmd() == StNetPacket.Const.CMD_PUBLIC_ADDR, "Unexpected Packet: " + recv_pkt.dumpSimple());
	    	util.assertTrue(
	    			recv_pkt.getCmd() == Command.PublicAddr, 
	    			"Unexpected Packet: " + recv_pkt.dumpSimple());
	    	StTransAddr addr 	= new StTransAddr(clt_addr.getAddress(), clt_port);
	    	StNetPacket ack_pkt = recv_pkt.buildAlw(addr.toBuffer());
			DatagramPacket udp_pkt2 = 
					new DatagramPacket( 
							ack_pkt.getBuffer().array(),ack_pkt.getLen(), clt_addr, clt_port);
			try {
				serverSocket.send(udp_pkt2);
				stLog.info("Send public address: " + addr.getAddrStr() );
			} catch (IOException e) {
				stLog.error(util.getExceptionDetails(e, "IOExeption when sending."));
				// end the thread loop. i.e. end this simple-STUN server(UDP).
				throw new StExpBreak("IO Exeption when sending."); 
			}
			
			// ONLY process ONE request for each peer
			break; 
	    }//while
	}//loopOnce

}// end of class: 
