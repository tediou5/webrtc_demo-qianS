package cn.teclub.ha.net.serv.request;

import java.util.concurrent.ConcurrentHashMap;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.request.StNetPacket;



public class StSrvServiceMgr extends ChuyuObj 
{
	private static StSrvServiceMgr _ins = new StSrvServiceMgr();
	public static StSrvServiceMgr getInstance(){
		return _ins;
	}
	
	private final ConcurrentHashMap<StNetPacket.Command, StSrvService> mapSrvService;
	

	/**
	 * Constructor
	 */
	private StSrvServiceMgr(){
    	this.mapSrvService = new ConcurrentHashMap<StNetPacket.Command, StSrvService>();
    	
    	mapSrvService.put(StNetPacket.Command.AdminGetInfo, 	new StSrvService_AdminGetInfo());
    	
    	mapSrvService.put(StNetPacket.Command.Signup, 			new StSrvService_Signup());
    	mapSrvService.put(StNetPacket.Command.Signout, 			new StSrvService_Signout());
    	mapSrvService.put(StNetPacket.Command.QueryGwInWifi, 	new StSrvService_QueryGwInWifi());
    	mapSrvService.put(StNetPacket.Command.ApplyForMaster, 	new StSrvService_ApplyForMaster() );
    	
    	mapSrvService.put(StNetPacket.Command.Login, 			new StSrvService_Login());
    	mapSrvService.put(StNetPacket.Command.ClientAQueryB, 	new StSrvService_ClientAQueryB() );
    	mapSrvService.put(StNetPacket.Command.CltStatus, 		new StSrvService_CltStatus() );
    	
    	mapSrvService.put(StNetPacket.Command.SearchContact, 	new StSrvService_SearchContact());
    	mapSrvService.put(StNetPacket.Command.AddContact, 		new StSrvService_AddContact());
    	mapSrvService.put(StNetPacket.Command.DelContact, 		new StSrvService_DelContact());
    	mapSrvService.put(StNetPacket.Command.EditInfo, 		new StSrvService_EditInfo());
    	
    	mapSrvService.put(StNetPacket.Command.MessageToSrv, 	new StSrvService_MessageToSrv());
    	mapSrvService.put(StNetPacket.Command.QueryFriends, 	new StSrvService_QueryFriends());
    	mapSrvService.put(StNetPacket.Command.SlaveDelContact, 	new StSrvService_SlaveDelContact());
    	
    	mapSrvService.put(StNetPacket.Command.QueryDevInWifi, 	new StSrvService_QueryDevInWifi());    	
    	mapSrvService.put(StNetPacket.Command.SlaveManage, 		new StSrvService_SlaveManage() );

    	stLog.info("==== Created Service Manager! Count:" + mapSrvService.size() + " ====");
	}
	
	
	public StSrvService getService(StNetPacket.Command cmd){
		return mapSrvService.get(cmd);
	}
}
