package cn.teclub.common;

import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * <h1>super class for a general object. </h1>
 *  
 * @author mancook
 */
public class ChuyuObj extends ChuyuFamily 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBER
	////////////////////////////////////////////////////////////////////////////	
	
	////////////////////////////////////////////////////////////////////////////
    // Inner Class
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Member
	////////////////////////////////////////////////////////////////////////////

	
	
	
	
	
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBER
	////////////////////////////////////////////////////////////////////////////	
	protected static ChuyuUtil util = ChuyuUtil.getInstance();
	
	////////////////////////////////////////////////////////////////////////////
    // Inner Class
	////////////////////////////////////////////////////////////////////////////
	
	public interface DumpAttribute {
		
		/**
		 * <h2>Set up dump utility.</h2>
		 * 
		 * <p> In this method, set dump parameters which are used by dump() methods.
		 * <ol>
		 * <li>call dumpAddLine() 		to add lines into dumpLines list;
		 * <li>call dumpAddObj 			to add a dumped object;
		 * <li>call dumpSetTitle() 		to set title;
		 * <li>call dumpSetBufferSize 	to set buffer size;
		 * </ol>
		 * 
		 * e.g. 
		 * <p>  
		 * 		dumpSetTitle(" #### StTask.State #### ");		<br/>
		 * 		dumpAddLine("  ID: " + this.id");				<br/>
		 *   	dumpAddLine("  Name: " + this.name");			<br/>
		 *   	dumpAddObj(chuyu_obj);							<br/>
		 *   	dumpSetBufferSize(512);							<br/>
		 * 
		 */
        void dumpSetup() ;
	}
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Member
	////////////////////////////////////////////////////////////////////////////	
	
	// [cook: 2015-12-6] use fake logger to print nothing in release version
	// DO NOT delete. For debug purpose!
	protected Logger stLog = Logger.getLogger(this.getClass());
	//protected ChuyuFakeLogger stLog = ChuyuFakeLogger.getInstance();
	
	
    public void setLogLevel(Level level){
		stLog.setLevel(level);
	}
	
	
	private String				dumpTitle = null;
	private ArrayList<String> 	dumpLines = null;
	private ArrayList<ChuyuObj>	dumpObjects = null;
	private int dumpBufferSize = 256;
	
	
	synchronized public StringBuffer dump(){
		return this.dump("=");
	}
	
	
	synchronized public StringBuffer dump(String hdr_char){
		if(this instanceof DumpAttribute){
			DumpAttribute da = (DumpAttribute)this;
			dumpLines = new ArrayList<>();
			dumpObjects = new ArrayList<>();
			da.dumpSetup();
			
			StringBuffer sbuf = new StringBuffer(dumpBufferSize);
			util.dumpFunc.addDumpHeaderLine(sbuf, 
					( dumpTitle==null? getClass().toString() : dumpTitle),  hdr_char);

			for(String line: dumpLines){
				util.dumpFunc.addDumpLine(sbuf, line);
			}
			if(dumpObjects.size() > 0){
				util.dumpFunc.addDumpHeaderLine(sbuf, "", "-");
				for(ChuyuObj obj : dumpObjects){
					if(obj != null){
						sbuf.append(obj.dump("~"));
					}else{
						util.dumpFunc.addDumpLine(sbuf, "<Null Obj>");
					}
				}
			}
			util.dumpFunc.addDumpHeaderLine(sbuf, "", hdr_char);
			sbuf.append("\n");
			return sbuf;
		}else{
			return new StringBuffer(this.toString());
		}
	}
	
	protected void dumpSetTitle(String title){
		dumpTitle = title;
	}
	
	protected void dumpAddLine(String line){
		dumpLines.add(line);
	}
	protected void dumpAddObj(ChuyuObj obj){
		dumpObjects.add(obj);
	}
	
	protected void dumpSetBufferSize(int buf_size){
		dumpBufferSize = buf_size;
	}
}
