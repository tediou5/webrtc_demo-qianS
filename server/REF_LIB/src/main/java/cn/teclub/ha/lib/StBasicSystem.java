package cn.teclub.ha.lib;

import org.apache.log4j.Logger;

import cn.teclub.common.ChuyuFamily;


/**
 * Update: [Theodore: 2014-1-14] <br/><br/>
 * 
 * Application Basic Framework. <br/><br/>
 * 
 * System object must be created as soon as possible! <br/>
 * Because it does some initialization. <br/>
 * e.g. load configuration parameters, initialize log4j, etc. <br/><br/>
 * 
 * DO NOT integrate any run-time object, which is created or initialized by <br/>
 * application when necessary.<br/><br/>
 * 
 * Add method 'stopEventPump' to stop the event pump thread <br/><br/>
 * 
 * @author mancook
 * 
 * @deprecated needed?
 *
 * @param <C>
 * @param <G>
 */
public abstract class StBasicSystem
				<C extends StBasicSystemConfig, G extends StBasicSystemGlobal> 
				extends ChuyuFamily 
{
	protected C config;
	protected G global;
	protected StEventHandler 	eventHandler;
	protected Logger stLog = null;
	
	
	/**
	 * Constructor  <br/>
	 * 
	 * @throws StExpUserError 
	 */
	protected StBasicSystem (Class<C> clazzC, Class<G> clazzG, String evt_hdl)
			throws  StExpUserError 
	{
		try{
			this.config = clazzC.newInstance();
			this.global = clazzG.newInstance();
			
			// 2015-1-14  Load config and global
			this.config.load();
			this.global.load();
			this.eventHandler = new StEventHandler(evt_hdl, StConst.SYS_PULSE_PERIOD_MS/2);
			
			// after config object is created, log4j should be initialized! 
			this.stLog = Logger.getLogger(this.getClass());
		}catch(Exception e){
			String err_msg = "[chuyu] Exception when constructing StBasicSystem object";
			StringBuffer sbuf = new StringBuffer(512);
			sbuf.append(err_msg);
			sbuf.append("\n\t [chuyu] Exception:  "); sbuf.append(e.toString());
			sbuf.append("\n\t [chuyu] Message:    "); sbuf.append(e.getMessage());
			sbuf.append("\n\t [chuyu] Cause:      "); sbuf.append(e.getCause());
			for(StackTraceElement st : e.getStackTrace()){
				sbuf.append("\n\t\t [chuyu] StackTrace: "); sbuf.append(st.toString());
			}
			
			System.out.println(sbuf);
			throw new StExpUserError(sbuf.toString());
		}
	}

	
	public C getConfig(){
		return this.config;
	}
	public G getGlobal(){
		return this.global;
	}
	public StEventHandler getEventHanlder(){
		return this.eventHandler;
	}
	
	
    public void addListener(StEventListener lis){
    	this.eventHandler.addListener(lis);
    }
    
    public void delListener(StEventListener lis){
    	this.eventHandler.delListener(lis);
    }
    
    public void addNewEvent(StEvent event){
    	this.eventHandler.addNewEvent(event);
    }
    
    
    /**
     * Stop Event Handler Thread <br/>
     * Add a shutdown event into event handler to stop the event pump thread. <br/><br/>
     * 
     */
    public void stopEventPump(){
    	this.addNewEvent(new StEvent.SystemShutdown());
    }
}
