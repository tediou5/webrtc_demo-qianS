package cn.teclub.ha.lib;

/**
 * <h1> Runtime Errors. </h1>
 * 
 * @author mancook
 *
 */
public class StErrFamily extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6790723475567424748L;

	public StErrFamily(String exp){
		super(exp);
	}
	
	public StErrFamily(){
		super();
	}
}
