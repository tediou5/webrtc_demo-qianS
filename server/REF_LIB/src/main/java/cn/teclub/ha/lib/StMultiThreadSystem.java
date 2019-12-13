package cn.teclub.ha.lib;

import org.apache.log4j.Logger;

import cn.teclub.common.ChuyuFamily;
import cn.teclub.common.ChuyuUtil;

/**
 * A StMultiThreadSystem has several threads, which has a event handler 
 * to process its own events. 
 * 
 * @author mancook
 *
 * @param <C>
 * @param <G>
 * 
 * @deprecated due to StEventPool has thread safety issue!
 * 
 */
public abstract class StMultiThreadSystem <C extends StBasicSystemConfig, 
			G extends StBasicSystemGlobal> 
						extends ChuyuFamily 
{
	protected Logger stLog = null;
	protected ChuyuUtil util = null;
	
	protected C config;
	protected G global;
	protected StEventPool 		eventPool;
	
	
	/**
	 * Constructor
	 */
	protected StMultiThreadSystem (Class<C> clazzC, Class<G> clazzG)  {
		try {
			this.config = clazzC.newInstance();
			this.global = clazzG.newInstance();
			this.config.load();
			this.global.load();
			this.eventPool = StEventPool.getInstance();
			
			// after config object is created, log4j should be initialized! 
			this.stLog = Logger.getLogger(this.getClass());
			this.util = ChuyuUtil.getInstance();
		} catch(Exception e){
			String err_msg = "[chuyu] Exception when constructing StMultiThreadSystem object";
			StringBuffer sbuf = new StringBuffer(512);
			sbuf.append(err_msg);
			sbuf.append("\n\t [chuyu] Exception:  "); sbuf.append(e.toString());
			sbuf.append("\n\t [chuyu] Message:    "); sbuf.append(e.getMessage());
			sbuf.append("\n\t [chuyu] Cause:      "); sbuf.append(e.getCause());
			for(StackTraceElement st : e.getStackTrace()){
				sbuf.append("\n\t\t [chuyu] StackTrace: "); sbuf.append(st.toString());
			}
			System.out.println(sbuf);
			throw new StErrUserError(sbuf.toString());
		}

	}
	
	public C getConfig(){
		return this.config;
	}
	
	public G getGlobal(){
		return this.global;
	}
	
	public void broadcastEvent(StEvent event){
		this.eventPool.broadcastEvent(event);
	}
	
	public void registerHanlder(StEventHandler hdl) 
			throws StExpUserError {
		this.eventPool.registerHanlder(hdl);
	}
}
