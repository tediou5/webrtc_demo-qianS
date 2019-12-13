package cn.teclub.ha.lib;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import cn.teclub.common.ChuyuObj;



/**
 * TODO: encode the string
 * 
 * @author mancook
 *
 */
public class StCoder extends ChuyuObj 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS
	////////////////////////////////////////////////////////////////////////////
    private static StCoder 		_Instance = null;
    
    public static StCoder getInstance(){
        if(null == _Instance ){
            _Instance = new StCoder();
        }
        return _Instance;
    }

    
	//public static final int SD_LOGIN_MSG_GAP = 64;
	public static final int N_STR_MAX_LEN  = 32;
	public static final int N_ENC_STR_LEN  = 64;
	public static final String	CHAR_SET = "UTF-8";
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////
	
	public StCoder() {
	}
	

	/**
	 * encode a string to a 64-byte array.
	 * 
	 * @param str, a string of a max StConstant.
	 * @return
	 */
	public ByteBuffer encString64(String str){
		util.assertTrue(str.length() <= N_STR_MAX_LEN, "input string is too long!");
		ByteBuffer buffer = ByteBuffer.allocate(N_ENC_STR_LEN);
		byte[] array = str.getBytes(Charset.forName(CHAR_SET));
		
		buffer.put((byte)array.length);
		buffer.put(array);
		buffer.position(0); 			// restore the position to '0'!!!
		return buffer;
	}
	
	
	/**
	 * decode a 64-byte array into a string. The remaining array is used.
	 * 
	 * @param buffer
	 * @return
	 */
	public String decString64(ByteBuffer buffer) {
		util.assertTrue(buffer.remaining() == N_ENC_STR_LEN, "input buffer limit is NOT: " + N_ENC_STR_LEN);
		int str_len = buffer.get();
		byte[] str_arr = new byte[str_len];
		buffer.get(str_arr);
		return new String(str_arr, Charset.forName(CHAR_SET));
	}
    
}//EOF: StCoder
