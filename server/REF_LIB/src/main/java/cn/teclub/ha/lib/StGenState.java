package cn.teclub.ha.lib;

import java.util.Vector;

import cn.teclub.common.ChuyuObjSer;

/**
 * <h1>Generic State</h1>
 * 
 * <p> Examples: StExecute.State
 * 
 * @author mancook
 *
 */
public abstract class StGenState extends ChuyuObjSer
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7653582857452384808L;

	@SuppressWarnings("rawtypes")
	protected static StringBuffer dumpElements(Vector values, String title){
		StringBuffer sbuf = new StringBuffer(256);
		sbuf.append("==== "+ (null == title ? " All Generic States " : title) +"  ====");
		for(int i=0; i< values.size(); i++){
			StGenState s = (StGenState)values.get(i);
			sbuf.append("\n    ").append("[" + s.value + "] ").append(s.toString());
		}
		sbuf.append("\n");
		return sbuf;
	}
	
	
	public static StGenState fromInt(Vector<?> values, int v){
		for (int i=0; i<values.size();i++) {
			StGenState s = (StGenState) values.get(i);
			if(s.value == v) return s;
		}
		throw new RuntimeException("value [" + v + "] is NOT found !");
	}
	

	
	////////////////////////////////////////////////////////////////////////////
    // Inner Classes
	////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////	
	public final int value;
	public final String name;
	
	protected StGenState(int v, String n){
		value =v;
		name = n;
		addElement(this);
	}

	public String toString(){
		return name;
	}
	
	abstract protected  void addElement(StGenState s);
}// EOF StGenState

