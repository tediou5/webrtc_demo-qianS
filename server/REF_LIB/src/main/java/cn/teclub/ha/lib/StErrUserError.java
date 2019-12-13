package cn.teclub.ha.lib;

import cn.teclub.common.ChuyuRuntimeExp;


/**
 * <h1>User Error. Fatal Error </h1>
 * 
 * @author mancook
 *
 */
public class StErrUserError extends ChuyuRuntimeExp {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8538272093627917156L;

	public StErrUserError(String msg){
		super(msg);
	}
	
	public StErrUserError(){
		super();
	}
}
