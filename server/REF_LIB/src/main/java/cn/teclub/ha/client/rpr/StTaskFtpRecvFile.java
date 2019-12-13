package cn.teclub.ha.client.rpr;

import java.io.File;

import cn.teclub.ha.client.StFtpClient;
import cn.teclub.ha.lib.StTask;


/**
 * <h1>Receive a file from FTP cache server. </h1>
 * 
 * @author mancook
 * 
 *
 */
public abstract class StTaskFtpRecvFile extends StTask
{
    final String	localDir;
    final String	remoteFilePath;
    private final StFtpClient 	ftpClient;
    
    
    /**
     * <h2> Constructor </h2>
     * 
     * @param local_dir The relative pathname of the directory, under {AppHomeDir}
     * @param file_path_on_cache  The relative pathname of the file on FTP cache, under {ftpUploadHomeDir} 
     */
	public StTaskFtpRecvFile(
			String local_dir, 
			String file_path_on_cache ) 
	{
		super("Download from FTP cache: " + file_path_on_cache );
		this.localDir = local_dir;
		this.remoteFilePath = file_path_on_cache;
		this.ftpClient = new StFtpClient();
	}

	
	@Override
	protected void taskRun() {
		if(remoteFilePath == null || remoteFilePath.length() < 1){
			stLog.error("Error remote file path: " + util.stringFunc.wrap(remoteFilePath));
    		this.endCode = -1;
    		return;
		}
		
		// get the relative path of cache directory
		File remote_file = new File(remoteFilePath);
    	String file_name = remote_file.getName();
    	if(file_name.length() == 0 ){
    		stLog.error("Fail to get name from remote filepath: " + util.stringFunc.wrap(remoteFilePath));
    		this.endCode = -1;
    		return;
    	}
    	// NOTE: remote_dir should be RELATIVE to {FtpUploadHomeDir}.
    	// However, it works if you pass the full pathname to method ftpClient.downloadFile()
    	String remote_dir = remote_file.getParent();
    	
    	stLog.debug(
    			"\n\t Downloading file " + util.stringFunc.wrap(file_name) + 
    			"\n\t from cache " + util.stringFunc.wrap(remote_dir) +
    			"\n\t into local storage " + util.stringFunc.wrap(this.localDir));
    	if(! ftpClient.downloadFile(remote_dir, file_name, this.localDir)){
    		stLog.error("Fail to download cache file: " + util.stringFunc.wrap(file_name) );
    		this.endCode = -1;
    		return;
    	}
    	stLog.debug("Downloaded file " + util.stringFunc.wrap(file_name) );
	}
}// EOF StTaskFtpRecvFile
