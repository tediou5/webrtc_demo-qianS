package cn.teclub.ha.client.rpr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import cn.teclub.common.ChuyuObjSer;
import cn.teclub.common.ChuyuUtil;
import cn.teclub.ha.client.StcTools;
import cn.teclub.ha.client.StcException.ExpRemoteClientNoFound;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.net.StFriend;


/**
 * <h1>Serialized Object in File System</h1>
 * 
 * <p> This object is serialized to and de-serialized from file system. <br/>
 * For easy coding, they are not saved into DB. <br/>
 * Only store objects that do not change a lot during the application running. 
 * 
 * <p> e.g. The FRIEND LIST which is received at last running. <br/>
 * So that they can be displayed if your smart-phone is disconnected with server. 
 * 
 * 
 * 
 * @author mancook
 *
 */
class StSerializedObject extends ChuyuObjSer
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	private static final long serialVersionUID = -4100669279410430751L;
	
	private static final Logger 		ssLog = Logger.getLogger(StSerializedObject.class);
	private static final StcTools 	tools = StcTools.getInstance();
	private static StSerializedObject 	_ins = null;
	
	static StSerializedObject getInstance() {  
		if(null == _ins){
			deserialize();
			if(null == _ins){
				ssLog.warn("Fail to de-serialize the bufferd object! Create an empty one.");
				_ins = new StSerializedObject();
			}
		}
		return _ins; 
	}
	
	
	/**
	 * <p> Create the object from file system by de-serialization.
	 * 
	 */
	synchronized static void deserialize(){
		if(_ins != null){
			throw new StErrUserError("DO NOT de-serialize again!");
		}
		ChuyuUtil sutil = ChuyuUtil.getInstance();
		long t_start  = System.currentTimeMillis();
		StSerializedObject buf_obj = null;
		ObjectInputStream ois = null;
		try {
			final String ser_file = tools.getFilePath_SerObj();
			ssLog.trace("De-Serialize from file: " + ser_file );
			final FileInputStream fis = new FileInputStream(new File(ser_file));
    		ois = new ObjectInputStream(fis);
    		buf_obj = (StSerializedObject) ois.readObject();
    		ssLog.info("De-Serialize Success");
		} catch (IOException | ClassNotFoundException e) {
			// e.printStackTrace();
			ssLog.debug(sutil.getExceptionDetails(e, "De-Serialize Failure. Ignore This Issue & Continue..."));
			ssLog.warn("fail to de-serialize: " + e );
		}finally{
			if(ois !=null){
				sutil.close(ois);
			}
		}
		ssLog.debug("De-Serialization cost (ms): " + (System.currentTimeMillis() - t_start) );
		_ins = buf_obj;
	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////	
	private StcSharedVar				coreVar = null;
	
	
	/**
	 * Constructor
	 */
	private StSerializedObject() { 
		//mapTimeLapse = new  ConcurrentHashMap<StTimeLapse.ID, StTimeLapse4Remote> ();
		this.coreVar = StcSharedVar.getInstance();
	}
	
	
	synchronized public void setCoreVar(final StcSharedVar var){
		this.coreVar = var;
	}
	
	
	synchronized public StClientInfo getLocalCopy22(){
		return coreVar.getLocalCopy();
	}
	
	synchronized public StClientInfo getRemoteClientInfo(StClientID clt_id) 
			throws ExpRemoteClientNoFound 
	{
		return coreVar.getRemoteClientInfo(clt_id);
	}
	
	synchronized public StClientInfo getRemoteClientInfo(String name) 
			throws ExpRemoteClientNoFound
	{
		return coreVar.getRemoteClientInfo(name);
	}
	
	synchronized public StClientID getClientID() {
		return coreVar.getClientID();
	}
	
	
	/**
	 * <h2> Get all friend list from buffer </h2>
	 * 
	 * @return
	 */
	synchronized public ArrayList<StClientInfo> getFriendList() {
		ArrayList<StClientInfo> f_list = new ArrayList<StClientInfo>();
		if(coreVar == null ){
			stLog.error("core-var in buffered object is NULL!");
			return f_list;
		}
		try{
			for(StClientID f_id : this.coreVar.getFriendIDList() ){
				StClientInfo f_ci = getRemoteClientInfo(f_id);
				if(f_ci == null){
					stLog.warn("friend (" + f_id +") client-info is NOT found!");
					continue;
				}
				f_list.add(f_ci);
			}
		} catch (ExpRemoteClientNoFound e) {
			e.printStackTrace();
			stLog.fatal(util.getExceptionDetails(e, "Impossible!"));
			throw new StErrUserError("Impossilbe!");
		}
		return f_list;
	} 
	
	
	
	/**
	 * <h2> Get Gateway List from buffer </h2>
	 * 
	 * @return
	 */
	synchronized public ArrayList<StFriend> getGatewayList(boolean include_monitor)  {
		return this.coreVar.getRemoteGatewayList(include_monitor);
	}
	
	
	/**
	 * <h2> Get User (Family Member) List from buffer </h2>
	 * 
	 * @return
	 * @throws ExpLocalClientOffline 
	 */
	synchronized public ArrayList<StClientInfo> getUserList() {
		return this.coreVar.getRemoteUserList();
	}
	
	
	/**
	 * <p> Save the object into file system.
	 * 
	 */
	synchronized void serialize(){
		if(coreVar == null){
			stLog.info("Some attribute is NULL! Stop serialization!");
			return;
		}
		
		ObjectOutputStream oos = null;
		try {
			final long t_start  = System.currentTimeMillis();
			final String file_path = tools.getFilePath_SerObj();
			final FileOutputStream fos  = new FileOutputStream(new File(file_path));
			stLog.debug("Serialize Object into file: " + file_path);
			oos =  new ObjectOutputStream(fos);
			oos.writeObject(this);
			stLog.info(util.testMilestoneLog("Serialize Success -- File: " + file_path));
			stLog.debug("Serialization Cost: " + (System.currentTimeMillis() - t_start)  + "ms");
		} catch (IOException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to serialize buffered object"));
		}finally{
			if(oos !=null){
				util.close(oos);
			}
		}
	}
	
	
	/**
	 * <p> Serialize this object into file system. 
	 */
	synchronized void flush(){
		serialize();
		// NO need to de-serialize back!
		// deserialize();
	}

}
