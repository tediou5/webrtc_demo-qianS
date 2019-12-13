package cn.teclub.ha.client.rpr;

import java.io.File;

import cn.teclub.ha.client.StFtpClient;
import cn.teclub.ha.lib.StTask;



/**
 * <h1>Send a file to FTP cache server. </h1>
 * 
 * 
 * @author mancook
 * 
 * @deprecated [Theodore: 2016-08-29] use upload pulse
 */
abstract class StTaskFtpSendFile extends StTask
{
	/**
	 * Relative pathname of the local file, which is to be uploaded.
	 */
    final String	localFilePath;
    final String 	remoteFtpDir1;
    final String 	remoteFtpDir2;
    private final StFtpClient 	ftpClient;
    
    
    /**
     * <p> In file RELAY mode, this pathname will be sent in the ALLOW packet by client-B. 
     * So that clinet-A can download the cached file from FTP server.
     */
    final String 	fileOnCache;
    
    /**
     * <h2> Constructor </h2>
     * 
     * @param local_file_path - The relative pathname of the local file
     * @param cache_dir
     */
    public StTaskFtpSendFile( 
    		String local_file_path, 
    		String cache_dir ) 
    {
    	super("FTP sends local file: " + local_file_path );
    	this.ftpClient = new StFtpClient();
        this.localFilePath = local_file_path;
        
        remoteFtpDir1 = "__" + cache_dir + "__" ;
        remoteFtpDir2 = "task__" + util.getTimeStampForFile() + "__" + util.to16CharHex(this.getTaskId().getId());
        
        File local_file = new File(local_file_path);
        fileOnCache = remoteFtpDir1 + "/" + remoteFtpDir2 +"/" + local_file.getName();
        stLog.debug("EOF Constuctor");
    }
    
    
    public String toString(){
    	return super.toString() + ", [TASK] Send local file '" + this.localFilePath + "' to FTP cache " ;
    }
    
    
    @Override
    public void taskRun() {
        stLog.warn("TODO: Check disk space on FTP cache server ...");
        if( null == ftpClient.uploadFile(this.localFilePath, this.remoteFtpDir1, this.remoteFtpDir2, StFtpClient.TYPE_TimeLapse) ){
        	// default end code is 0, which means SUCCESS. See StTask constructor.
        	this.endCode = -1;
        }
    }
    
}//EOF StTaskFtpSendFile




