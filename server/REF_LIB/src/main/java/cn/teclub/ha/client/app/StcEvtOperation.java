package cn.teclub.ha.client.app;


/**
 * <h1> Application Operation </h1>
 * 
 * <pre>
 * An operation is performed in Operation-Pulse in APP layer. 
 * 
 * Any time, there is ONLY ONE operation being performed. 
 * </pre>
 * 
 * @author mancook
 * 
 * @deprecated UnImplemented
 *
 */
public abstract class StcEvtOperation extends StcEvtApp {
	private  boolean processed = false;
	
	public StcEvtOperation(final String name, final String dscp) 
	{
		super(name, "[APP OPT] " + dscp);
	}
	
	protected void prc(){
		util.assertTrue(!processed, "DO NOT process operation, again -- " + this.getEventName() );
		processed = true;
		perform();
	}
	
	/**
	 * Called by Operation Pulse
	 */
	protected abstract void perform();
	
	public void trigger(){
		StcPulseOperation.getInstance().addNewEvent(this);
	}
}



