package cn.teclub.ha.client.rpr;

import cn.teclub.ha.request.StNetPacket;


/**
 *
 * @deprecated
 */
public abstract class StcEvtContinueRequest extends StcRprExecution
{
	protected final StcSharedVar 	sharedVar = StcSharedVar.getInstance();
	protected  StcService service;
	
	
	public StcEvtContinueRequest( final StcService service) 
	{
		super("Continue Request "+ service.getCommand(), null);
		this.service = service;
	}
	
	protected void sendResponse(final StNetPacket pkt){
		service.sendResponse(pkt);
	}
}
