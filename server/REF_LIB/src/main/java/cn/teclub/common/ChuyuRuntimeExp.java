package cn.teclub.common;

/**
 * Chuyu Runtime Exception
 * 
 * @author mancook
 *
 */
public class ChuyuRuntimeExp extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3392733445134742529L;
	
	public ChuyuRuntimeExp(String exp){
		super(exp);
	}
	
	public ChuyuRuntimeExp(){
		super();
	}
}
