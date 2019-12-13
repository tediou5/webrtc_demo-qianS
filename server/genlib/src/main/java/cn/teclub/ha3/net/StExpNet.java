package cn.teclub.ha3.net;

import cn.teclub.ha3.utils.StException;


/**
 * <h1>Exception in network module. </h1>
 * 
 * Used by: client and server
 * 
 * @author mancook
 *
 */

@SuppressWarnings("ALL")
public abstract class StExpNet extends StException
{
	public static class StExpSessionTimeout extends StException {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3949115383173584805L;

		public StExpSessionTimeout(String msg){
			super(msg);
		}
		
		public StExpSessionTimeout(){
			super();
		}
	}
	
	
	/**
	 * @deprecated
	 * 
	 * @author mancook
	 *
	 */
	public static class StExpExecutionTimeout extends StException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4329899042413711276L;

		public StExpExecutionTimeout(){
			super("Client Execution Timeout");
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public StExpNet(String msg){
		super(msg);
	}
	
	public StExpNet(){
		super();
	}
}



