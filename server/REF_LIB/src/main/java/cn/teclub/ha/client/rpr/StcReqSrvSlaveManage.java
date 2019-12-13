package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;



/**
 * a master sends this request to manage its slave.
 * 
 * @author mancook
 *
 */
public class StcReqSrvSlaveManage 
		extends StcReqSrv
{
    ////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS AND METHODS
	////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	
	final StClientInfo slave;
	
	
	/**
	 * the master edits client-info of its slave
	 * 
	 * @param slave_ci
	 */
	public StcReqSrvSlaveManage( final StClientInfo slave_ci)
	{
		super(StNetPacket.Command.SlaveManage, 
				StNetPacket.Code.SlaveManage.REQ_EDIT_INFO, 
				slave_ci.toBuffer(false),
				5000, 
				"Edit slave:" + slave_ci );
		this.slave = slave_ci;
	}

	
	@Override
	protected void onTimeout() {
	}

	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		StClientInfo new_slave = new StClientInfo(data);
		resResult = new_slave;
		stLog.info("update new slave: " + new_slave);
		sharedVar.updateRemoteClient(new_slave.getClientID(),  new_slave);
	}

	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		stLog.warn("fail to edit slave to: " + slave );
		resResult = null;
	}
}
