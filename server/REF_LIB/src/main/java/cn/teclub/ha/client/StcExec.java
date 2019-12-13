package cn.teclub.ha.client;

import cn.teclub.ha.lib.StEventHandler;
import cn.teclub.ha.lib.StEventListener;

import static cn.teclub.ha.client.StcExec.State.FINISHED;
import static cn.teclub.ha.client.StcExec.State.INIT;
import static cn.teclub.ha.client.StcExec.State.RUNNING;
import static cn.teclub.ha.client.StcExec.State.STARTED;


public abstract  class StcExec extends StcEvtClient
{
	public enum State {
		INIT, STARTED, RUNNING, FINISHED, TIMEOUT,
	}

	private final StEventHandler target;
	private State state;



	public StcExec(final String evt_name, final String evt_dscp, final StEventHandler target_hdl){
		super(evt_name, evt_dscp);
		this.target = target_hdl;
		this.state = INIT;
	}


	public String toString(){
		return "[Exec:0x" + Long.toHexString(id) + "]" + eventName ;
	}



	private void assertStateIs(final State s){
		util.assertTrue(state == s, "State Error -- Expected: " + s + ", Current: " + state);
	}


	public void trigger(){
		assertStateIs(INIT);
		state = STARTED;
		target.addNewEvent(this);

	}


	/**
	 * <h2> called in target pulse. </h2>
	 *
	 * NOTE: make sure an execution is ONLY processed in ONE listener!
	 *
	 * @param lis - the event listener which processes this execution.
	 */
	public void prc(final StEventListener lis){
		assertStateIs(STARTED);
		state = RUNNING;
		perform(lis);
		state = FINISHED;
	}


	public boolean isFinished(){
		return state == FINISHED;
	}


	/**
	 *
	 * @param timeout - wait timeout
	 * @return - execution cost in ms
	 *
	 * @throws StcExpTimeout - execution timeout
	 */
	public int waitFinish(int timeout) throws StcExpTimeout {
		final long T0 = System.currentTimeMillis();
		long t = T0 ;
		for(; t - T0 < timeout; t = System.currentTimeMillis()){
			if(isFinished()){
				stLog.debug("Execution Cost: " + (t - T0) + " ms -- " + this);
				return (int)(t - T0);
			}
			util.sleep(50);
		}
		throw new StcExpTimeout();
	}


	public int waitFinishOrInterrupted(int timeout) throws StcExpTimeout, InterruptedException {
		final long T0 = System.currentTimeMillis();
		long t = T0 ;
		for(; t - T0 < timeout; t = System.currentTimeMillis()){
			if(isFinished()){
				stLog.debug("Execution Cost: " + (t - T0) + " ms -- " + this);
				return (int)(t - T0);
			}
			Thread.sleep(50);
		}
		throw new StcExpTimeout();
	}


	/**
	 * Called by TL4R player Pulse
	 */
	protected abstract void perform(final StEventListener lis);
}
