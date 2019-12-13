package cn.teclub.ha.lib;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cn.teclub.common.ChuyuObj;




public class StBasicCipher extends ChuyuObj 
{
	private static StBasicCipher _ins;
	public static StBasicCipher instance(){
		if(_ins == null){
			try {
				_ins = new StBasicCipher();
			} catch (UnsupportedEncodingException | GeneralSecurityException e) {
				e.printStackTrace();
				throw new StErrUserError("fail to create cipher!");
			}
		}
		return _ins;
	}
	
	public static final String CHARSET_NAME="UTF-8";
	
	private static String genKey128(){
		// 
		// 
		// todo-in-future: use a more secure method to generate a key
		// 
		//   return "Bar12345Bar12345";  // 128 bit key
		//   return "JiaLanDe13071307";  
		//
		return "JiaLanDe13071307";    
	}
	
	
	private final String key;
	private final String initVector;
	private final Cipher ciEnc, ciDec;
	
	
	private StBasicCipher() throws 
			UnsupportedEncodingException, 
			GeneralSecurityException 
	{
		this(  genKey128(), 		// 128 bit key
			  "RandomInitVector" 	// 16 bytes IV
		);
	}
	
	
	
	public StBasicCipher(String k, String iv_name)  throws 
			UnsupportedEncodingException, 
			GeneralSecurityException
	{
		this.key = k;
		this.initVector = iv_name;
		
		// stLog.warn("## craete enc/dec cipher: key=" + k + ", iv=" + iv_name);
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(CHARSET_NAME));
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(CHARSET_NAME), "AES");
        this.ciEnc = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        this.ciDec = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        
        ciEnc.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        ciDec.init(Cipher.DECRYPT_MODE, skeySpec, iv);
	}
	
	
    public byte[] encrypt( byte[] raw, int offset, int len) throws GeneralSecurityException  {
    	final long T0 = System.currentTimeMillis();
    	//stLog.warn("## encrypting bytes: offset/len=" + offset + "/" + len  + "\n" + util.toCharHexBuf(raw, offset, len) );
        byte[] bb =  ciEnc.doFinal(raw, offset, len);
        //stLog.warn("## encrypted  bytes: len = "+ bb.length  + "\n" + util.toCharHexBuf(bb) );
        stLog.debug("encrypt success -- in/out/cost: " + len + "/" + bb.length + "/" + util.getCostStr(T0));
        return bb;
    }

    
    public byte[] decrypt( byte[] ci_data, int offset, int len) throws GeneralSecurityException  {
    	final long T0 = System.currentTimeMillis();
    	//stLog.warn("## decrypting bytes: offset/len=" + offset + "/" + len  + "... \n" + util.toCharHexBuf(ci_data, offset, len) );
    	byte[] bb =   ciDec.doFinal(ci_data, offset, len);
    	//stLog.warn("## decrypted  bytes: \n" + util.toCharHexBuf(bb) );
    	 stLog.debug("decrypt success -- in/out/cost: " + len + "/" + bb.length + "/" + util.getCostStr(T0));
    	return bb;
    }
	
}
