package cn.teclub.ha.net.serv;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.net.StNetUtil;


/**
 * <h1>DB table: Dynamic SIP account.</h1>
 * 
 * @author mancook
 */
public class StModelSipAcct 
	extends ChuyuObj 
	implements ChuyuObj.DumpAttribute, StDbTable
{
	private int		  	id;
	private String 		sipId;
	private String		sipPasswd;
	private String		sipDomain;
	private String		dscp;
	private int			flag;
	
	
	StModelSipAcct(){
	}
	
	void setId(int id){
		this.id = id;
	}
	
	int getId(){
		return this.id;
	}

	String getDscp() {
		return dscp;
	}

	void setDscp(String dscp) {
		this.dscp = dscp;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	public String getFlagValueStr(){
		return StNetUtil.flagValueStr(this.flag);
	}
	
	
	public String getSipId() {
		return sipId;
	}

	public void setSipId(String sipId) {
		this.sipId = sipId;
	}

	public String getSipPasswd() {
		return sipPasswd;
	}

	public void setSipPasswd(String sipPasswd) {
		this.sipPasswd = sipPasswd;
	}

	public String getSipDomain() {
		return sipDomain;
	}

	public void setSipDomain(String sipDomain) {
		this.sipDomain = sipDomain;
	}

	@Override
	public void dumpSetup() {
		dumpAddLine("ID:   " + util.to8CharHex(id) );
		dumpAddLine("SIP Account: " + sipId );
		dumpAddLine("SIP Domain : " + sipDomain );
		dumpAddLine("Flag: " + this.getFlagValueStr());
		dumpAddLine("Dscp: " + this.dscp );
	}
}


