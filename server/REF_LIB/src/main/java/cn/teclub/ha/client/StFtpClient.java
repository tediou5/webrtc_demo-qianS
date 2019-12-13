package cn.teclub.ha.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StExpBreak;


public class StFtpClient extends ChuyuObj
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	public static final int TYPE_TimeLapse 	= 0x01;
	public static final int TYPE_LOGS 		= 0x02;
	
	private static StcParams p = StcParams.getInstance();
	
    public static class FtpObject{
    	FtpObject(){
    		// Params p = Params.getInstance();
    		this.serv = p.ftpSrv;
    		this.port = p.ftpPort;
    		this.user = p.ftpUser;
    		this.passwd =p.ftpPasswd;
    		this.uploadHomeDir = p.ftpUploadHomeDir;
    	}
    	
    	final String serv ;
        final int    port ;
        final String user ;
        final String passwd ;
        final String uploadHomeDir ;
    }
    
 	
 	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
 	////////////////////////////////////////////////////////////////////////////	

    private FtpObject ftpObj ;


	public StFtpClient() {
		ftpObj = new FtpObject();
	}
	
	public void mkdir(FTPClient ftp, String dir) throws IOException, StExpBreak{
		boolean ret = ftp.makeDirectory(dir);
		int reply = ftp.getReplyCode();
    	if(! ret ){
    		stLog.error("Fail to make directory: '" + dir + "'. Reply Code: " + reply);
    		throw new StExpBreak();
    	}
    	stLog.debug("mkdir " + dir);
	}
	
	public void cd(FTPClient ftp, String path) throws IOException, StExpBreak{
    	boolean ret = ftp.changeWorkingDirectory(path);
    	int reply = ftp.getReplyCode();
    	if(!ret){
    		// [2016-9-16] DO NOT log error if 'cd' fails!
    		stLog.warn("Fail to change workdir: '" + path + "'. Reply Code: " + reply);
    		throw new StExpBreak();
    	}
    	stLog.debug("cd " + path);
    }
    
    
	public void doCommand(FTPClient ftp, String cmd, String params) throws IOException, StExpBreak{
    	boolean ret = ftp.doCommand(cmd, params);
    	int reply = ftp.getReplyCode();
    	if(!ret){
    		stLog.error("Fail to do FTP command: '" + cmd +  " " + params + "'. Reply Code: " + reply);
    		throw new StExpBreak();
    	}
    	stLog.debug("do command:  " + cmd + " " + params);
    }
	
	
	public void retrieveFile(FTPClient ftp, String remote_file, FileOutputStream os) throws IOException, StExpBreak{
		boolean ret = ftp.retrieveFile(remote_file, os);
		int reply = ftp.getReplyCode();
		if(!ret){
			stLog.error("Fail to download file: '" + remote_file + "'. Reply Code: " + reply);
			throw new StExpBreak();
		}
		stLog.debug("Download File: " + remote_file);
	}


	/**
	 * On Android, an output-stream with Context.MODE_WORLD_READABLE is created,
	 * when this app downloads a file to the private storage and shares it with other app.
	 *
	 * For Android 6.0 and below, this method is used to download the latest version APP and share
	 * it with APP installer.
	 */
	public void downloadFile(String remote_dir, String filename, final FileOutputStream out_strem )
	throws StExpBreak, IOException
	{
		FTPClient 	ftp = new FTPClient();
		ftp.setBufferSize(1024*1024);
		int reply;

		try {
			//stLog.warn("## 1. Login FTP server");
			final long t0 = System.currentTimeMillis();

			ftp.connect(ftpObj.serv, ftpObj.port);
			ftp.login(ftpObj.user, ftpObj.passwd);
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				throw new StExpBreak("NOT Positive Complete! Reply Code: " + reply);
			}

			stLog.debug("Use PASSIVE mode");
			ftp.enterLocalPassiveMode();

			this.cd(ftp, this.ftpObj.uploadHomeDir);
			this.cd(ftp, remote_dir);
			FTPFile[] fs = ftp.listFiles();
			reply = ftp.getReplyCode();
			stLog.debug("replay code after listFiles():  " + reply);

			boolean file_exist = false;
			for (FTPFile ff : fs) {
				stLog.debug("checking file: " + util.stringFunc.wrap(ff.getName()));
				if (ff.getName().equals(filename)) {
					file_exist = true;
					break;
				}
			}
			if (!file_exist) {
				throw new StExpBreak("File '" + filename + "' is NOT found on FTP server!");
			}

			//stLog.warn("## 2. start downloading " + filename + " ... ");
			final long t1 = System.currentTimeMillis();
			retrieveFile(ftp, filename, out_strem);
			ftp.logout();
			out_strem.flush();

			stLog.info("[2/2] Download/Total Cost: " + util.getCostStr(t1) + "/" + util.getCostStr(t0) );
		} finally {
			disconnect(ftp);
		}
	}



	/**
	 * <h2>Download a file from FTP server.</h2>
	 * 
	 * 
	 * @param remote_dir relative pathname on FTP server, under {FtpUploadHomeDir}
	 * 
	 * @param filename  file name on FTP server
	 * 
	 * @param local_dir  The relative pathname of local directory, in {AppHomeDir}  s<br/>
	 * 					 e.g. video/remote/gw02/date_2016-01-23
	 * 
	 * @return  true on success
	 * 
	 */
	public boolean downloadFile(final String remote_dir, final String filename, final String local_dir){
		final File file 	= new File(p.homeDir + File.separator + local_dir+ File.separator + filename);
		final File tmp_file = new File(file.getAbsolutePath() + "__" + util.getTimeStampForFileMS() + "__tmp");

		FileOutputStream os = null;
		try{
			//stLog.warn("## download tmp file: " +  tmp_file );
			os = new FileOutputStream(tmp_file);
			downloadFile(remote_dir, filename, os);
			//stLog.warn("## rename tmp file to: " + file );
			if( ! tmp_file.renameTo(file)){
				stLog.error("fail to rename: " + tmp_file + " --> " + file );
				return false;
			}
			return true;
		}catch (IOException| StExpBreak e){
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "download failure!"));
			return false;
		}finally{
			//stLog.warn("## close file output stream");
			util.close(os);
		}
	}



    /**
     * Upload a file to remote FTP server. Remote file is save to directory
     *  {ftpUploadHomeDir}/{remote_dir1}/{remote_dir2}
     * 
     * @param local_file_path - relative pathname of the local file;
     * @param remote_dir1 	- sub-folder on FTP server;
     * @param remote_dir2 	- sub-sub-folder on FTP server;
	 * @param type			- Possible Values: TYPE_TimeLapse,  TYPE_LOGS;
	 *
	 * @return file name on cache. "{remote_dir1}/{remote_dir2}/{file_name}";
	 *
	 */
	public String uploadFile(String local_file_path, String remote_dir1, String remote_dir2, int type){
    	stLog.debug(">>>>");
    	FTPClient ftp = new FTPClient();
    	ftp.setBufferSize(1024*1024);
    	String endMessage;
    	FileInputStream fins = null;
    	
    	try { 
    		boolean ret;
    		
    	   	// get local file name
    	   	File local_file = new File(p.homeDir + File.separator + local_file_path);
        	String file_name = local_file.getName();
        	if(file_name.length() == 0 ){
        		stLog.error("Fail to get file name!");
        		throw new StExpBreak();
        	}
        	stLog.trace("Get local file name: " + file_name);
        	fins = new FileInputStream(local_file);
        	stLog.trace("1. Access local file: " + local_file_path);
        	
            int reply;  
            stLog.debug("Connect to FTP server: '" + ftpObj.serv + ":" + ftpObj.port + "' ...");
            ftp.connect(ftpObj.serv, ftpObj.port);

            ret = ftp.login(ftpObj.user, ftpObj.passwd);
            reply = ftp.getReplyCode();  
            if(!ret){
            	stLog.error("FTP login fails!  Reply Code: " + reply);
            	throw new StExpBreak();
            }
            
            if( ! ftp.setFileType(FTP.BINARY_FILE_TYPE)){
				throw new StExpBreak("fail to set binary file type");
			}
            reply = ftp.getReplyCode();  
            if (!FTPReply.isPositiveCompletion(reply)) {  
            	stLog.error("NOT Positive Complete! Reply Code: " + reply);
            	stLog.error("Just disconnect FTP!");
            	throw new StExpBreak();
            }


            stLog.trace("2. Login FTP server");
            stLog.trace("Use PASSIVE mode");
    		ftp.enterLocalPassiveMode();
        	stLog.trace("Make remote folder '" + remote_dir1 + "/" + remote_dir2 + "', and enter this folder. ");
        	this.cd(ftp, ftpObj.uploadHomeDir);
        	
        	if(type == TYPE_LOGS){
        		this.cd(ftp, "logs");
        	}
        	
        	try{
        		this.cd(ftp, remote_dir1);
        	}catch(StExpBreak e){
        		this.mkdir(ftp, remote_dir1);
        		this.cd(ftp, remote_dir1);
        	}
        	try{
        		this.cd(ftp, remote_dir2);
        	}catch(StExpBreak e){
        		this.mkdir(ftp, remote_dir2);
        		this.cd(ftp, remote_dir2);
        	}
        	
        	final long ms_start = System.currentTimeMillis();
            ret = ftp.storeFile(file_name, fins);     
            reply = ftp.getReplyCode();
            if(! ret){
            	stLog.error("Fail to upload file: '" + file_name + "'. Reply Code: " + reply);
            	throw new StExpBreak();
            }
            final long upload_cost = System.currentTimeMillis() - ms_start;
            
            final String file_on_cache = remote_dir1 + "/" + remote_dir2 + "/" + file_name;
            stLog.trace("3. Upload finishes. " +
            		"\n\t Upload local file " + util.stringFunc.wrap(local_file_path) + 
            		"\n\t to FTP cache as "  + util.stringFunc.wrap(file_on_cache) );
            stLog.info("Upload Cost (ms): " + upload_cost + "ms");
            
            ftp.logout();  
            return file_on_cache;    
        } catch (IOException e) {
			e.printStackTrace();
			endMessage = "IOException when uploading a file!";
        	stLog.error(util.getExceptionDetails(e, endMessage));
        } catch (StExpBreak e) {
			e.printStackTrace();
			endMessage = "Upload Abort!";
        	stLog.error(util.getExceptionDetails(e, endMessage));
		} finally {
			util.close(fins);
			disconnect(ftp);
            stLog.debug("<<<<");
        } 
    	return null;
    }


    private void disconnect(FTPClient ftp){
		if(ftp == null || ! ftp.isConnected()){
			return;
		}
		try {
			ftp.disconnect();
			//stLog.warn("## disconnect with FTP server!");
		} catch (IOException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "fail to disconnect with FTP server!"));
		}
	}
}

