package cn.teclub.ha.request;

import java.io.IOException;
import java.net.InetAddress;

import cn.teclub.ha.net.StExpConnectionLoss;

public interface StSocket4Pkt {
	public boolean isClosed();
	
	public void close();
	
	
	/**
	 * <pre>
	 * NON-Blocking Socket (Socket Channel): this method may return NULL!
	 * 
	 * Blocking Socket: this method blocks until a packet is received.
	 * </pre>
	 * 
	 * @return
	 * @throws IOException
	 * @throws StExpConnectionLoss
	 */
	public  StNetPacket recvPacket() throws IOException, StExpConnectionLoss;
	
	public  void sendPacket(final StNetPacket pkt) throws IOException;
	
	public InetAddress getSrcAddress();
	
	public int getSrcPort();
	
	public InetAddress getDstAddress();
	
	public int getDstPort();
}
