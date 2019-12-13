package cn.teclub.ha.lib;

/**
 * Exception class for all Chuyu's exceptions
 * 
 * @author mancook
 *
 */
public class StExpFamily extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public StExpFamily(String exp){
		super(exp);
	}
	
	public StExpFamily(){
		super();
	}
}
