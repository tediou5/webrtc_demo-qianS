package cn.teclub.ha.client.rpr;



/**
 * Execute in RPR Main Pulse
 * 
 * @author mancook
 *
 */
public abstract class StcRprExecution extends StcEvtRpr
{
	protected final StcSharedVar 	 	sharedVar = StcSharedVar.getInstance();

	/**
	 *todo:  [2017-10-26] do not reference main-pulse object!!!!
	 */
	protected final StcRprMainPulse  	mainPulse = StcRprObject.getInstance().mainPulse;
	private  boolean 					processed = false;
	
	public StcRprExecution(final String name, final String dscp) 
	{
		super(name, "[RPR EX]" + dscp);
	}
	
	public StcRprExecution()  { }
	
	
	protected StcRprObject.Info getRprInfo(){
		return StcRprObject.getInstance().info;
	}


	protected void sendRequest(StClientRequest req){
		StcRprObject.getInstance().sendRequest(req);
	}


//	protected void sendMessageToApp(int what, int arg1, int arg2, Object obj) {
//		StcRprObject.getInstance().sendMessage(what, arg1, arg2, obj);
//	}
//
//	protected void sendMessageToApp(int what) {
//		StcRprObject.getInstance().sendMessage(what);
//	}


	
	protected void prc(){
		util.assertTrue(!processed, "DO NOT process execution, again -- " + this.getClass());
		processed = true;
		perform();
	}
	
	/**
	 * Called by Main Pulse
	 */
	protected abstract void perform();
	
	public void trigger(){
		mainPulse.addNewEvent(this);
	}

}
