package cn.teclub.ha.lib;

import cn.teclub.common.ChuyuObj;



/**
 * Instance of this class has several threads, which has a event handler 
 * to process its own events. 
 * 
 * @author mancook
 * 
 * @deprecated due to StEventPool has thread safety issue!
 */
public abstract class StEventPoolSubsystem extends ChuyuObj
{
	protected StEventPool 		eventPool;
	
	/**
	 * Constructor.
	 */
	protected StEventPoolSubsystem () {
		this.eventPool = StEventPool.getInstance();
	}
	
	public void broadcastEvent(StEvent event){
		this.eventPool.broadcastEvent(event);
	}
	
	public void registerHanlder(StEventHandler hdl) 
			throws StExpUserError {
		this.eventPool.registerHanlder(hdl);
	}
}
