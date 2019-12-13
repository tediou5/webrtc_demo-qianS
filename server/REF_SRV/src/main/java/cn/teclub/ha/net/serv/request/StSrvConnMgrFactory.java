package cn.teclub.ha.net.serv.request;

import cn.teclub.common.ChuyuObj;

public class StSrvConnMgrFactory extends ChuyuObj 
{
	public enum Type{
		LIGHT_CONN, 
		PULSE_CONN
	}
	
	
	private static StSrvConnMgrFactory _ins = new StSrvConnMgrFactory();
	public static StSrvConnMgrFactory getInstance() {
		return _ins;
	}
	
	private StSrvConnMgrFactory() {  }
	
	public StSrvConnMgr createConnMgr(Type t){
		if(t == Type.LIGHT_CONN){
			return StSrvLightConnMgr.getInstance();
		}else{
			return StSrvPulseConnMgr.getInstance();
		}
	}
}
