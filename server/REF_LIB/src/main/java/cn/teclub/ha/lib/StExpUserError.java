package cn.teclub.ha.lib;


/**
 * User Error. Fatal Error <br/><br/>
 * 
 * User error exception indicates a error using StGenLib or other classes, <br/>
 * which are located in 'cn.teclub'. <br/>
 * 
 * @author mancook
 *
 */
public class StExpUserError extends StExpFamily {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public StExpUserError(String msg){
		super(msg);
	}
	
	public StExpUserError(){
		super();
	}
}
