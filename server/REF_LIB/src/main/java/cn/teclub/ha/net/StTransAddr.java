package cn.teclub.ha.net;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import cn.teclub.common.ChuyuObj;



/**
 * <h1>Transport Address: IP:Port. </h1>
 * 
 * @author mancook
 *
 */
public class StTransAddr extends ChuyuObj {
	static final int OBJLEN = 8;
	
	private byte[] 	ip = new byte[4];
	private int		port;
	
	/**
	 * Construct from integer IP and port.
	 * 
	 * @param ip
	 * @param port
	 * @throws StMakeSureFailure
	 */
	public StTransAddr(int ip, int port) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(ip);
		buffer.position(0);
		buffer.get(this.ip);
		this.port = port;
	}
	
	
	/**
	 * Constructor.
	 * @param ip
	 * @param port
	 * @throws StMakeSureFailure
	 */
	public StTransAddr(byte[] ip, int port) {
		util.assertTrue(ip!=null && ip.length == 4, "");
		for(int i=0; i<4; i++){
			this.ip[i] = ip[i];
		}
		this.port = port;
	}
	
	
	/**
	 * <h2>Construct from buffer. </h2>
	 * 
	 * @param buffer
	 * @throws StMakeSureFailure
	 */
	public StTransAddr(ByteBuffer buffer) {
		util.assertTrue(buffer.remaining() >= OBJLEN, "NO enough bytes left!");
		buffer.get(this.ip);
		this.port = buffer.getInt();
	}
	
	
	public ByteBuffer toBuffer(){
		ByteBuffer buffer = ByteBuffer.allocate(OBJLEN);
		buffer.put(this.ip);
		buffer.putInt(this.port);
		buffer.rewind();
		return buffer;
	}
	
	public byte[] getIp(){
		//		byte[] ip2 = new byte[4];
		//		for(int i=0; i<4; i++){
		//			ip2[i] = this.ip[i];
		//		}
		//		return ip2;
		//
		// 2015-3-8: clone it? 
		return this.ip;
	}
	
	public int getPort(){
		return this.port;
	}
	
	public String getIpStr() {
		String ip_str = null;
		ip_str =  util.getIPv4AddressStr(this.ip);
		return ip_str;
	}
	
	
	public InetAddress getInetAddress() {
		return util.getIPv4Address(this.ip);
	}
	
	
	public String getAddrStr() {
		return (this.getIpStr() + ":" + this.port);
	}
	
	public boolean equals (StTransAddr addr2){
		if(this.port != addr2.port){
			return false;
		}
		for(int i=0; i<4; i++){
			if(this.ip[i] != addr2.ip[i]){
				return false;
			}
		}
		return true;
	}
	
	
	public String toString(){
		return this.getAddrStr();
	}
	
}// EOF: StTransAddr