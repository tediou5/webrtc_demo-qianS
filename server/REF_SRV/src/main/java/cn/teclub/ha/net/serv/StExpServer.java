package cn.teclub.ha.net.serv;

import cn.teclub.ha.net.StExpNet;


/**
 * <h1> Network Server Exception </h1>
 * 
 * Used by: server
 * 
 * @author mancook
 *
 */
@SuppressWarnings("serial")
public class StExpServer extends StExpNet {
	public StExpServer(String msg){
		super("[StExpServer]" + msg);
	}
	
	public StExpServer(){
		super("[StExpServer]");
	}
}


