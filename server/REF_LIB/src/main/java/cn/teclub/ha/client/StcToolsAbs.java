package cn.teclub.ha.client;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.client.StcException.ExpPublicAddrFail;
import cn.teclub.ha.client.rpr.StRemoteFile;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.net.StTransAddr;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StNetPacket.ConstructPacketFailure;
import cn.teclub.ha.request.StPktPublicAddr;
import cn.teclub.ha.request.StNetPacket.Command;
import cn.teclub.ha.request.StNetPacket.ExpReceiveTooFewBytes;



/**
 * [2016-9-16] Why use abstract tool class? 
 * 
 * 
 * @author mancook
 *
 */
public abstract class StcToolsAbs  extends ChuyuObj  
{
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////	
	protected final StcParams 	params = StcParams.getInstance();

	public String arrayToString(String[] array){
		if(array == null || array.length < 1){
			return null;
		}
		StringBuffer sbuf = new StringBuffer(512);
		for(String s: array){
			sbuf.append(s).append(";");
		}
		return sbuf.toString();
	}
	
	public String getDataDir(){
		return ("data");
	}
	
	public String getDataDirRelease(){
		return ("data/release");
	} 
	
	
	/**
	 * <p> Get the full pathname of the serialized object, which stored the shared core-var. 
	 * 
	 * @return
	 */
	public String getFilePath_SerObj(){
			String file_path  = params.homeDir + File.separator  +  this.getDataDir() + File.separator + "client_buffered_object.ser";
			return file_path;
	}
	
	
	public String getVideoDir(){
		return ("video");
	}
	
	public String getVideoDirLocal(){
		return ("video" + File.separator + "local");
	}
	
	
	public String getVideoDirRemote(){
		return ( "video" + File.separator + "remote");
	}
	
	
	/**
	 * Upload logs to FTP server. Remote folder is: 
	 *   __{dir1}__/LOGS__YYMMDD_hhmmss/
	 * 
	 * @param dir1 - remote dir on FTP server
	 * @param logs - list of relative pathname of log files
	 */
	public void uploadLogs(final String dir1, final ArrayList<String> logs){
    	final String remoteFtpDir1 = "__" + dir1 + "__" ;
        final String remoteFtpDir2 = "LOGS__" + util.getTimeStampForFile() ;
        final StFtpClient ftpClient = new  StFtpClient();
        
        for(final String f: logs){
        	stLog.trace("Uploading Log: '" + f + "'...");
			final String file_on_cache = ftpClient.uploadFile(f, remoteFtpDir1, remoteFtpDir2, StFtpClient.TYPE_LOGS);
			if(file_on_cache != null){
				stLog.info("Upload Success:  ~~~~> '" + file_on_cache + "'");
			}else{
				stLog.error("Fail to Upload Log: '" + f + "' !!!!");
			}
        }
	}
	
	
	/**
	 * <p> Used in query-timelapse method
	 * 
	 * @deprecated StTimeLapse object is received when querying time lapse;
	 * 
	 * @param files
	 * @return
	 */
	public StRemoteFile[] fromStringList(String[] files){
		if(files == null || files.length <1){
			stLog.error("No intput file names!");
			return null;
		}
		if(files.length % 2 != 0){
			throw new StErrUserError("Number of file names must be 2*n");
		}
		int count  = files.length/2;
		StRemoteFile[] remote_list = new StRemoteFile[count];
		for(int i=0; i<count; i++){
			remote_list[i] = new StRemoteFile(files[i*2], files[i*2+1]);
		}
		return remote_list;
	}
	

	/**
	 * Download a file from a FTP server.
	 * 
	 * @param loc_dir
	 * @param file_path_on_cache
	 * 
	 * @return
	 * - null: Failure
	 * - String: the pathname of the download file
	 * 
	 *  @deprecated 
	 */
	public String ftpRecvFile(final String loc_dir,  final String file_path_on_cache){
		final StFtpClient ftpClient = new StFtpClient();
		if(file_path_on_cache == null || file_path_on_cache.length() < 1){
			stLog.error("Error remote file path: " + util.stringFunc.wrap(file_path_on_cache));
    		return null;
		}
		
		// get the relative path of cache directory
		File remote_file = new File(file_path_on_cache);
    	String file_name = remote_file.getName();
    	if(file_name.length() == 0 ){
    		stLog.error("Fail to get name from remote filepath: " + util.stringFunc.wrap(file_path_on_cache));
    		return null;
    	}
    	// NOTE: remote_dir should be RELATIVE to {FtpUploadHomeDir}.
    	// However, it works if you pass the full pathname to method ftpClient.downloadFile()
    	String remote_dir = remote_file.getParent();
    	
    	stLog.debug(
    			"\n\t Downloading file " + util.stringFunc.wrap(file_name) + 
    			"\n\t from cache " + util.stringFunc.wrap(remote_dir) +
    			"\n\t into local storage " + util.stringFunc.wrap(loc_dir));
    	if(! ftpClient.downloadFile(remote_dir, file_name, loc_dir)){
    		stLog.error("Fail to download cache file: " + util.stringFunc.wrap(file_name) );
    		return null;
    	}
    	stLog.debug("Downloaded file " + util.stringFunc.wrap(file_name) );
		return loc_dir + "/" + file_name;
	}

	
	
	/**
	 * Upload a file to FTP server.
	 * 
	 * @param local_file_path
	 * @param cache_dir
	 * 
	 * @return
	 * - null: Failure
	 * - String: the pathname of uploaded file on cache
	 * 
	 *  @deprecated 
	 */
	public String ftpSendFile(final String local_file_path,  final String cache_dir){
		final StFtpClient ftp_client = new StFtpClient();
        final String rem_ftp_dir1 = "__" + cache_dir + "__" ;
        final String rem_ftp_dir2 = "task__" + util.getTimeStampForFile() ; //+ "__" + util.to16CharHex(this.getTaskId().getId());
        stLog.warn("TODO: Check disk space on FTP cache server ...");
        return ftp_client.uploadFile(local_file_path, rem_ftp_dir1, rem_ftp_dir2, StFtpClient.TYPE_TimeLapse);
	}
	
	
	/**
     * 
     * @param stun_srv_ip
     * @param stun_srv_port
     * @return
     * @throws ExpPublicAddrFail
     * 
     * 
     * @deprecated 
     * 
     */
	public StTransAddr queryPublicAddr(String stun_srv_ip, int stun_srv_port) 
			throws ExpPublicAddrFail
	{
		try{
			stLog.debug("Send CMD_PUBLIC_ADDR request ...");
			StNetPacket pkt = StPktPublicAddr.buildReq();
			byte[] snd_bytes = pkt.getBuffer().array();
			DatagramPacket udp_pkt_snd = new DatagramPacket(
					snd_bytes, 
					snd_bytes.length, 
					InetAddress.getByName(stun_srv_ip), 
					stun_srv_port );
			DatagramSocket udp_socket = new DatagramSocket();
			udp_socket.send(udp_pkt_snd);
			
			stLog.debug("Receive CMD_PUBLIC_ADDR ALLOW");
			byte[] recv_bytes = new byte[1024];
			ByteBuffer recv_buf = ByteBuffer.wrap(recv_bytes);
			DatagramPacket udp_recv_pkt = new DatagramPacket(recv_bytes, recv_bytes.length);
			udp_socket.receive(udp_recv_pkt);
			udp_socket.close();
			StNetPacket recv_pkt;
			try {
				recv_pkt = StNetPacket.buildFromBuffer(recv_buf);
			} catch (ExpReceiveTooFewBytes | ConstructPacketFailure e) {
				e.printStackTrace();
				throw new StcException.ExpPublicAddrFail();
			}
			if(recv_pkt.getCmd() != Command.PublicAddr){
				stLog.error("Received Packet is NOT CMD_PUBLIC_ADDR!!!");
				throw new StcException.ExpPublicAddrFail();
			}
			if(!recv_pkt.isTypeResponseAllow() || !recv_pkt.isTypeResponseAllow()){
				stLog.error("Type of received packe is NOT correct!");
				throw new StcException.ExpPublicAddrFail();
			}
			StPktPublicAddr addr_pkt = (StPktPublicAddr)recv_pkt;
			
			stLog.info("Get public address("+ addr_pkt.getDataPublicAddr() +") from STUN server: " + stun_srv_ip+":"+stun_srv_port);
			return addr_pkt.getDataPublicAddr();
		}catch(IOException e){
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "IO Exception"));
			throw new StcException.ExpPublicAddrFail();
		}
	}

}
