package cn.teclub.common;

import java.nio.ByteBuffer;

/**
 * <h1>Generic Long ID.</h1>
 * 
 * @author mancook
 */
public class ChuyuLongID 
	extends ChuyuObj 
	implements Cloneable, java.io.Serializable 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	

	/**
	 * 
	 */
	private static final long 	serialVersionUID = -3818237401298616958L;
	public  static final int 	OBJLEN = 8;
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	

	private final long  id;
	
	/**
	 * Constructor
	 * 
	 * @param id raw ID
	 */
	public ChuyuLongID(long id) {
		this.id = id;
	}
	
	
	public long getId(){
		return this.id;
	}
	
	public String getHex(){
		return "0x" + Long.toHexString(id);
	}
	
	
	public boolean equalWith(ChuyuLongID longID) {
		return longID != null && this.id == longID.id;
	}
	
	public boolean equals(Object longID){
		if(longID instanceof ChuyuLongID){
			if(this.id == ((ChuyuLongID)longID).id){
				return true;
			}
		}
		return false;
	}
	
	public int hashCode(){
		Long ll = this.id;
		return ll.hashCode();
	}
	
	
	
	public ByteBuffer toBuffer(){
		ByteBuffer buffer = ByteBuffer.allocate(OBJLEN);
		buffer.putLong(this.id);
		buffer.position(0);
		return buffer;
	}
	
	public String toString(){
		return "0x" + ChuyuUtil.getInstance().to16CharHex(this.id);
	}

	public String toStringXml(){
		return super.toStringXml("#### ");
	}
	
	public String toStringXml(String prefix){
		return (prefix + this.toString() + "\n");
	}
	
	
	public Object clone() {
		try {
			return super.clone();
		}catch (CloneNotSupportedException e) {
			throw new RuntimeException(util.getExceptionDetails(e, "Fail to clone ChuyuLongID object"));
		}
	}

}// EOF ChuyuLongID