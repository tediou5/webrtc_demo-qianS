package cn.teclub.ha.client.rpr;

import java.io.File;
import java.nio.ByteBuffer;

import cn.teclub.common.ChuyuObjSer;
import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.client.StcParams;
import cn.teclub.ha.lib.StErrUserError;

/**
 * <h1></h1>
 * 
 * <p> Represent a remote file.
 * 
 * <p> Pathnames of following files are stored: 
 * <ol>
 * <li> file on remote device.
 * <li> file on FTP cache;
 * <li> file in local buffer;
 * <li> file name;
 * <ol/>
 * 
 * <p>NTOE: All pathnames are RELATIVE to the {AppHomeDir} or {FtpCacheDir}. <br/>
 *     e.g. video/remote/gw02/data_2016-01-28/TimeLapse_20160128_141000.mp4
 * 
 * @author Mancook
 *
 */
public class StRemoteFile extends ChuyuObjSer implements ChuyuObj.DumpAttribute
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	private static final long serialVersionUID = -4605319438349968708L;

	public static final String NULL_CACHE = "-";
	private static final StcParams  params = StcParams.getInstance(); 

	
	////////////////////////////////////////////////////////////////////////////
    // Inner Classes
	////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	

	/**
	 * <p> APP pathname of the file on remote device. 
	 * e.g. 'video/local/date_2015-12-26/TimeLapse_20151226141000.mp4'
	 */
	public final String fileOnDevice;
	
	/**
	 * Set file-on-cache after monitor upload the file to cache.
	 */
	private String 		fileOnCache;

	
	/**
	 * <p> APP pathname of in local buffer.  <br/>
	 * e.g. video/remote/gw02/data_2016-01-28/TimeLapse_20160128_141000.mp4
	 * 
	 */
	private String		fileInLocalBuffer = null;
	
	
	// ------------------------------
	// NOT saved in DB
	// ------------------------------
	
	/**
	 * <p> Only the file name.  e.g. 'TimeLapse_20151226141000.mp4'
	 */
	public final String filename;
	
	/**
	 * <p> Local File object.
	 */
	private File		localFile = null;

	
	
	/**
	 * <h2>Constructor.</h2>
	 * 
	 * @param file_on_device
	 * @param file_on_cache
	 */
	public StRemoteFile(String file_on_device, String file_on_cache){
		this.fileOnDevice = file_on_device;
		if(file_on_cache !=null && file_on_cache.equalsIgnoreCase(NULL_CACHE)){
			file_on_cache = null;
		}
		this.fileOnCache = file_on_cache;
		
		File f = new File(File.separator + file_on_device);
		filename = f.getName();
	}
	
	
	public StRemoteFile(ByteBuffer buf){
		String[] list = util.stringFunc.fromBufferToArray(buf, 3);
		this.fileOnDevice 	= list[0];
		this.fileOnCache 	= list[1];
		this.setFileInLocalBuffer(list[2]);
		
		File f = new File(File.separator + fileOnDevice);
		filename = f.getName();
	}
	
	
	public ByteBuffer toBuffer(){
		String[] list = new String[]{
				this.fileOnDevice, 
				this.fileOnCache,
				this.fileInLocalBuffer
		};
		return util.stringFunc.toBuffer(list);
	}
	
	
	public boolean hasCache(){
		return (this.fileOnCache != null && this.fileOnCache.length() > 0);
	}
	
	
	/**
	 * <h2>Check if the remote file is downloaded.</h2>
	 * 
	 * <p> TRUE: if 'localFile' (File) is set and the file on local file system exist!
	 * 
	 * @return
	 */
	public boolean isDownloaded(){
		if (localFile != null){
			return localFile.exists();
		}
		return false;
	}
	
	
	public void setFileOnCache(String file_on_cache){
		this.fileOnCache = file_on_cache;
	}
	
	
	public String getFileOnCache(){
		return this.fileOnCache;
	}
	
	
	/**
	 * <h2> Set file in local buffer, if it exists. </h2>
	 * 
	 * <p> NOTE: If the input file does not exist, 
	 * both 'localFile' and 'fileInLocalBuffer' are reset to NULL!
	 * 
	 * @param local_filepath
	 */
	synchronized public void setFileInLocalBuffer(final String local_filepath){
		if(local_filepath == null){
			return;
		}
		String file_path = local_filepath.trim();
		if(file_path.length() < 1 || file_path.equals(".") || file_path.equals("..")){
			return;
		}
		
		this.fileInLocalBuffer = file_path;
		this.localFile = new File(params.homeDir + File.separator + file_path);
		if(this.localFile.isDirectory()){
			throw new StErrUserError("Name of buffered file is used by a diretory: " + localFile.getAbsolutePath() );
		}
		if(!localFile.exists()) {
			stLog.debug("Buffer file '" + file_path +"' does NOT exist!");
			localFile = null;
			fileInLocalBuffer = null;
		}
	}
	
	
	/**
	 * APP pathname of on local cache.  <br/>
	 * e.g. video/remote/gw02/data_2016-01-28/TimeLapse_20160128_141000.mp4
	 * 
	 * @return the file pathname in local buffer.
	 */
	synchronized public String getFileInLocalBuffer(){
		return this.fileInLocalBuffer;
	}
	
	
	/**
	 * <p> If no local buffer, this method returns NULL. 
	 * 
	 * @return the File object in local buffer. 
	 */
	synchronized public File getLocalFile(){
		return this.localFile;
	}
	

	/**
	 * <p> Delete files in local buffer.
	 * 
	 */
	synchronized public void deleteLocalBuffer(){
		if(localFile == null){
			return;
		}
		if(!localFile.delete()){
			stLog.warn("Fail to delete file: " + localFile.getAbsolutePath());
			return;
		}
		stLog.info("Delete buffer file: " + localFile.getName() );
		localFile = null;
		fileInLocalBuffer = null;
	}

	
	public void dumpSetup(){ 
		dumpAddLine("File  : " + this.fileOnDevice );
		dumpAddLine("Cache : " + this.fileOnCache );
		dumpAddLine("Buffer: " + this.fileInLocalBuffer );
		dumpAddLine("Filename: " + this.filename );
	}
	
	
	public String toString(){
		return dump().toString();
	}
}
