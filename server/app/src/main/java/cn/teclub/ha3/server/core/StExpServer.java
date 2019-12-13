package cn.teclub.ha3.server.core;


import cn.teclub.ha3.net.StExpNet;

/**
 * <h1> Network Server Exception </h1>
 * 
 * Used by: server
 * 
 * @author mancook
 *
 */
@SuppressWarnings("ALL")
public class StExpServer extends StExpNet {
	public StExpServer(String msg){
		super("[StExpServer]" + msg);
	}
	
	public StExpServer(){
		super("[StExpServer]");
	}
}


