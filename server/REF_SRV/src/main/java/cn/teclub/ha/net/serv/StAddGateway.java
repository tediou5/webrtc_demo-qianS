package cn.teclub.ha.net.serv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.net.StClientType;


/**
 * <pre>
 * Input file format: 
 *   ---------------------------------------------
 *   77-7A-8B-8D-00-01
 *   77-7A-8B-8D-00-02
 *   77-7A-8B-8D-00-03
 *   ---------------------------------------------
 * 
 * </pre>
 * @author mancook
 *
 */
public class StAddGateway{
    public static void main(String[] args) throws IOException{
    	if(args.length < 1){
    		System.out.println("Usage: <CMD> <INPUT-FILE>");
    		System.out.println(
    				"Usage: <CMD> <INPUT-FILE> \n" + 
    				"\n" +
    				"INPUT-FILE: Just put mac address in this file. \n" +
    				"            One mac address in one line. \n");
    		System.exit(-1);
    	}
    	final StAddGatewayWorker obj = new StAddGatewayWorker(args[0]);
    	obj.process();
    }
}



class StAddGatewayWorker extends ChuyuObj 
{

	@SuppressWarnings("unused")
	private final StSrvConfig			cfg = StSrvConfig.getInstance();
	@SuppressWarnings("unused")
	private final StDbHiberMgr			hiberMgr = StDbHiberMgr.getInstance();
	private final StDBObject.ObjectMgr	dbObjMgr = StDBObject.ObjectMgr.getInstance();

	
	private StDBObject dbObj;
	private final File inputFile;
	private final File outputFile;
   
	StAddGatewayWorker(final String input_file){
		this.inputFile = new File(input_file);
		this.outputFile = new File(inputFile.getAbsoluteFile() + ".out");
		
    	try {
			this.dbObj = dbObjMgr.getNextObject();
		} catch (InterruptedException e) {
			stLog.error(util.getExceptionDetails(e, "FATAL"));
			e.printStackTrace();
			System.exit(-1);
		}
    }
    
    
	private boolean verifyMacAddr(final String mac_addr0){
		final String mac_addr = mac_addr0.trim();
		if(mac_addr == null || mac_addr.length() != 17) {
			return false;
		}
		final String[] str = mac_addr.split("-");
		if(str.length != 6) {
			stLog.error("mac-addr format error: '" + mac_addr + "'");
			return false;
		}
		for(String s: str){
			try{
				//final String hex_str = "0x" + s;
				Integer.parseInt(s, 16);
			}catch(NumberFormatException e){
				stLog.error(util.getExceptionDetails(e, "Parse HEX part Error!"));
				stLog.error("mac-addr format error: '" + mac_addr + "'");
				return false;
			}
		}
		return true;
	}
	
	
	void process() throws IOException{
		if(!inputFile.exists()){
			stLog.warn("No Input file: " + inputFile.getAbsolutePath());
			return;
		}
		final BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		final BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		String line  = reader.readLine();
		for(;line != null; line = reader.readLine()){
			final String mac_addr = line.trim().toUpperCase();
			if(!verifyMacAddr(mac_addr)){
				continue;
			}
			deleteFakeGateway();
			
			final StModelClient mc_old = findGatewayByMac(mac_addr, false);
			if(mc_old != null){
				final String err_msg = "MAC-ADDR is used by client: " + mc_old.getName() + "/"+ mc_old.getClientID() + "/" + mc_old.getMacAddr();
				stLog.error(err_msg);
				writer.write(mc_old.getMacAddr() + ", [ERROR] " + err_msg + "\n" );
				continue;
			}
			final StModelClient mc0 = addNewGateway(mac_addr);
			stLog.info("Added GW Success: " + mc0.getName() + ", " + mc0.getClientID() + ", " + mc0.getMacAddr() + ", " + mc0.getSipId() + ", " + mc0.getFlag_ClientType());
			writer.write(mc0.getMacAddr() +", " + mc0.getName() + ", " + mc0.getLabel() + "\n");
		}
		reader.close();
		writer.close();
	}
	
	
    private void deleteFakeGateway(){
    	final StModelClient mc = dbObj.queryClientByName(StSrvConfig.SIGNUP_GW_TEMP_NAME);
    	if (mc == null ) return;
    	stLog.warn("Delete Fake GW: " + mc + "...");
    	dbObj.deleteClient(mc.getClientID());
    }
    
    
    private StModelClient findGatewayByMac(final String mac_addr, final boolean del_gw){
    	final StModelClient mc_gw = dbObj.queryModelClientByMacAddr(mac_addr);
    	if(mc_gw == null){
    		stLog.debug("NO Client with MacAddr: " + mac_addr);
    		return null;
    	}
    	
    	if(del_gw){
    		dbObj.deleteClient(mc_gw.getClientID());
    		stLog.warn("Delete GW with mac-addr: " + mac_addr );
    		stLog.debug("Dump Deleted GW: " +  mc_gw.dump());
    	}
    	return mc_gw;
    }
    
    
    
    private StModelClient addNewGateway(final String mac_addr){
		final StModelClient mc0 = new StModelClient(StClientType.GATEWAY);
	   	mc0.setName(StSrvConfig.SIGNUP_GW_TEMP_NAME);
    	mc0.setPasswd(StSrvConfig.SIGNUP_GW_PASSWD);
    	mc0.setLabel("Gateway ????");
    	mc0.setMacAddr(mac_addr);
    	mc0.setDscp("TODO...");
    	
    	final StModelSipAcct m_sip = dbObj.getModelSipAcctFree(true);
    	m_sip.setFlag(0x01);	 // set ASSIGN bit
    	mc0.setSipAcct(m_sip);

    	dbObj.addRecord(mc0); 
    	
    	// [2017-1-14] Update SIP account AFTER adding client record! 
    	// Abort updating SIP, if fail to add client record.
    	dbObj.updateRecord(m_sip);
    	stLog.debug("add new client:" + mc0.dump());
    	
    	final String gw_id = "" + mc0.getClientID().getId();
    	mc0.setName("GW" + gw_id);
    	mc0.setLabel("Gateway " + gw_id);
    	dbObj.updateRecord(mc0);
    	stLog.debug("update gateway name & label: " + mc0);
    	
    	return mc0;
    }

}
