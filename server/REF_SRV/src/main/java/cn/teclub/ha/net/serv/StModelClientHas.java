package cn.teclub.ha.net.serv;

import cn.teclub.ha.net.StClientHas;
import cn.teclub.ha.net.StClientID;

/**
 * <h1>DB table: Client Relationship.</h1>
 * 
 * @author mancook
 *
 */
public class StModelClientHas 
		extends StClientHas 
		implements  StDbTable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private long  id;
	
	
	public StModelClientHas(){ }

	
	public StModelClientHas(StClientID clt_a, StClientID clt_b, int flag){
		setCltA(clt_a);
		setCltB(clt_b);
		setFlag(flag);
	}
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	

	public long getCltARawId() {
		return getCltA().getId();
	}
	public void setCltARawId(long id) {
		setCltA( new StClientID(id) );
	}
	public long getCltBRawId() {
		return getCltB().getId();
	}
	public void setCltBRawId(long id) {
		setCltB( new StClientID(id));
	}
	
}


