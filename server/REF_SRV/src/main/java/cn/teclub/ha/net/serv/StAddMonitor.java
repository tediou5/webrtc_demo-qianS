package cn.teclub.ha.net.serv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.net.StClientType;


/**
 * 
 * 
 * @author mancook
 */
public class StAddMonitor{
    public static void main(String[] args) throws IOException{
    	if(args.length != 2) {
    		System.out.println(
    				"Usage: <CMD> <NUMBER> <OUT_DIR> \n" + 
    				"\n" +
    				"NUMBER: number of newly created monitor account \n" + 
    				"OUT_DIR: output file with monitor name & password \n");
    		System.exit(-1);
    	}
    	final StAddMonitorWorker obj = new StAddMonitorWorker(args[0], args[1]);
    	obj.process();
    }
}



class StAddMonitorWorker extends ChuyuObj  
{
	final int monNum;

	private static final String FAKE_MON_NAME = "Mon-FakeName-????";
	private final StDBObject.ObjectMgr	dbObjMgr = StDBObject.ObjectMgr.getInstance();

	
	private StDBObject dbObj;
	private final File outputFile;
	private final Random rand = new Random();
	
	StAddMonitorWorker(String arg0, String arg1){
		this.monNum = util.numberFunc.parseInt(arg0);
		this.outputFile = new File(arg1 + "/new-mon_"+ monNum + "_" +  util.getTimeStampForFile() +".out");
		
    	try {
			this.dbObj = dbObjMgr.getNextObject();
		} catch (InterruptedException e) {
			stLog.error(util.getExceptionDetails(e, "FATAL"));
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	void process() throws IOException { 
		final BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		int cnt = 0;
		for(int i=0; i< monNum; i++){
			deleteFakeMonitor();
			StModelClient mc0 = addNewMonitor();
			stLog.debug("Added Monitor Success: " + mc0.getName() + ", " + mc0.getClientID() + ", " + mc0.getSipId() + ", " + mc0.getFlag_ClientType());
			writer.write(mc0.getName() + ", " + mc0.getLabel() +  ",   "+ mc0.getPasswd() +" \n" );
			cnt++;
		}
		writer.close();
		
		stLog.info("Add Monitor: " + monNum + "; Success: " + cnt);
	}
	
	
	
    private void deleteFakeMonitor(){
    	final StModelClient mc = dbObj.queryClientByName(FAKE_MON_NAME);
    	if (mc == null ) return;
    	stLog.warn("Delete Fake Monitor: " + mc + "...");
    	dbObj.deleteClient(mc.getClientID());
    }
	
    
    
    final int PASS_LEN = 8;
    
    private String genRandomPassword(){
    	
    	// use following ASCII characters
    	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    	// from: 0x30 (0)
    	// to  : 0x7D (})
    	// exclude: 0x40 (@), 0x60 (`), 0x7C (|)
    	// include: 0x21 (!), 0x24 ($), 0x2D (-)
    	StringBuffer sbuf = new StringBuffer(PASS_LEN);
    	
    	for(int i=0; i< PASS_LEN; i++){
    		final int R = rand.nextInt();
    		
        	int v = (R > 0 ? R : (R * -1)) % 0x4E + 0x30;
        	if(v == 0x40){
        		v = 0x21;
        	}
          	if(v == 0x60){
        		v = 0x24;
        	}
          	if(v == 0x7C){
        		v = 0x2D;
        	}
          	// stLog.info("#### v: "+ v + ", char: " + (char)v);
          	sbuf.append((char)v);
    	}
    	
    	return sbuf.toString();
    }
    
    
    private StModelClient addNewMonitor(){
		final StModelClient mc0 = new StModelClient(StClientType.MONITOR);
	   	mc0.setName(FAKE_MON_NAME);
    	mc0.setPasswd(genRandomPassword());
    	mc0.setLabel("Monitor ????");
    	// mc0.setMacAddr(mac_addr);
    	mc0.setDscp("<主人很懒，什么信息都没写...>");
    	
    	final StModelSipAcct m_sip = dbObj.getModelSipAcctFree(true);
    	m_sip.setFlag(0x01);	 // set ASSIGN bit
    	mc0.setSipAcct(m_sip);

    	dbObj.addRecord(mc0); 
    	
    	// [2017-1-14] Update SIP account AFTER adding client record! 
    	// Abort updating SIP, if fail to add client record.
    	dbObj.updateRecord(m_sip);
    	stLog.debug("add new client:" + mc0.dump());
    	
    	final String gw_id = "" + mc0.getClientID().getId();
    	mc0.setName("m" + gw_id);
    	mc0.setLabel("手机摄像头");
    	dbObj.updateRecord(mc0);
    	stLog.debug("update gateway name & label: " + mc0);
    	
    	return mc0;
    }
}
