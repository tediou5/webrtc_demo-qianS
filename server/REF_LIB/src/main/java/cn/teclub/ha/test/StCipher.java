package cn.teclub.ha.test;

import java.net.URL;
import cn.teclub.common.ChuyuLog;
import cn.teclub.common.ChuyuObj;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;



class StConfigLog 
{
	public static final String BUDDLE_NAME = "st_test";
	
	static StConfigLog instance(){
		return _ins;
	}
	
	private static StConfigLog _ins = new StConfigLog();
	
	private StConfigLog(){
		System.out.println("[Client Test Driver] Loading client's resource bundle '" + BUDDLE_NAME  + "' in CLASSPATH ...");
		//ResourceBundle res  = ResourceBundle.getBundle(BUDDLE_NAME, Locale.getDefault());
		URL location = getClass().getClassLoader().getResource(BUDDLE_NAME + ".properties");
		if(location == null){
			throw new RuntimeException("fail to get buddle location: " + BUDDLE_NAME);
		}
		String log4j_cfg		= location.getFile() ;

    	System.out.println("[Client Test Driver] INFO: initialize log4j with: " + log4j_cfg);
    	ChuyuLog.setLog4jConf(log4j_cfg);
    	ChuyuLog.getInstance();
	}
}



/**
 * <pre>
 * Ref: 
 * - example: https://stackoverflow.com/questions/15554296/simple-java-aes-encrypt-decrypt-example
 * - apache codec: https://commons.apache.org/proper/commons-codec/download_codec.cgi
 * 
 * </pre>
 * 
 * @author mancook
 *
 */
public class StCipher extends ChuyuObj 
{
	public static final String CHARSET_NAME="UTF-8";
	
	private final String key;
	private final String initVector;
	
	public StCipher(String k, String iv){
		this.key = k;
		this.initVector = iv;
	}
	
	
	
    public String encrypt( String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(CHARSET_NAME));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(CHARSET_NAME), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string: "
                    + Base64.encodeBase64String(encrypted));

            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    
    
    public String decrypt( String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(CHARSET_NAME));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(CHARSET_NAME), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    
    
    
    private void test01(){
        String s0 = "Hi Momo & Lanlan!";
        String s1 = this.encrypt(s0);
        String s2 = this.decrypt(s1);
        
        stLog.info("original   (s0, len:"+ s0.length() +"): '" + s0 + "'");
        stLog.info("encrypted  (s1, len:"+ s1.length() +"): '" + s1 + "'");
        stLog.info("decrypted  (s2, len:"+ s2.length() +"): '" + s2 + "'");
    }
    
    
   

    public static void main(String[] args) {
    	StConfigLog.instance();
   
        String key = "Bar12345Bar12345"; // 128 bit key
        String initVector = "RandomInitVector"; // 16 bytes IV
        StCipher ci = new StCipher(key, initVector);    
        ci.test01();
    }
}


