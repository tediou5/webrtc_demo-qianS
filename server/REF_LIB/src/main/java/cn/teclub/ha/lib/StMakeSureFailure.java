package cn.teclub.ha.lib;


/**
 * Assert Failure are user-error
 * 
 * @author mancook
 *
 * @deprecated use runtime exception for assert. DO NOT use make-user utilities!
 */
public class StMakeSureFailure extends StExpUserError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public StMakeSureFailure(String msg){
		super(msg);
	}
	
	public StMakeSureFailure(){
		super();
	}
}
