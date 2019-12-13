package cn.teclub.ha.net;

import java.util.ArrayList;


/**
 * Implemented in Android App.
 * 
 * @author mancook
 *
 */
public interface StObjectMgrInterface 
{
	////////////////////////////////////////////////////////////////////////////
    // File Cache Manager
	////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * @param local_file The local file pathname, which may be uploaded to cache server.
	 * @return The file on cache server.
	 */
	String 	getCacheName(String local_file);
	
	
	/**
	 * <p> DO NOT use absolute pathname, use path name under {AppHomeDir} and {FtpHomeDir}
	 * 
	 * @param local_file The local file pathname.
	 * @param cache_path The file on cache server.
	 */
	void		setCacheName(String local_file, String cache_path);
	
	
	
	////////////////////////////////////////////////////////////////////////////
    // Message Manager
	////////////////////////////////////////////////////////////////////////////	

	
	/**
	 * Get all messages which are created since 'create_time'.
	 * 
	 * @param all - FALSE, only APPLY messages; TRUE: APPLY, REJECT, APPROVED messages;
	 */
	ArrayList<StMessage> getApplyMessage(boolean all, long create_time);
	
	
	/**
	 * Save a message into DB.
	 *
	 */
	void saveMessage(StMessage msg);
	
}
