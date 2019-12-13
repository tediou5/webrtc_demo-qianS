package cn.teclub.ha.client;



/**
 * <p> Utility methods in client. It is used by client object/component/listeners. 
 * 
 * <p> NOTE: DO NOT use call client object/compoment/listners in Tools!
 * 
 * @author Mancook
 *
 */
public class StcTools extends StcToolsAbs 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	private static StcTools 	_ins = new StcTools();
	
	public  static StcTools getInstance()  { 
		return _ins; 
	}
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////	
	
	private StcTools(){
	}
	
	

}//EOF: StClientTools