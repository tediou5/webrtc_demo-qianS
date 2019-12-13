package cn.teclub.ha.test;


import java.nio.ByteBuffer;

import cn.teclub.common.ChuyuObj;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * <pre>
 * 
 * 
 * </pre>
 * 
 * @author mancook
 *
 */
public class StCipher2 extends ChuyuObj 
{
	public static final String CHARSET_NAME="UTF-8";
	
	private final String key;
	private final String initVector;
	private final Cipher ciEnc, ciDec;
	
	
	public StCipher2(String k, String iv_name) throws Exception {
		this.key = k;
		this.initVector = iv_name;
		
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(CHARSET_NAME));
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(CHARSET_NAME), "AES");
        this.ciEnc = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        this.ciDec = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        
        ciEnc.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        ciDec.init(Cipher.DECRYPT_MODE, skeySpec, iv);
	}
	
	
	
    public byte[] encrypt( byte[] raw, int offset, int len) throws Exception  {
        return ciEnc.doFinal(raw, offset, len);
    }

    
    public byte[] decrypt( byte[] ci_data, int offset, int len) throws Exception  {
        return ciDec.doFinal(ci_data, offset, len);
    }
    
   
    
    private void test01() throws Exception  {
        String s0 = "Hi Momo & Lanlan!";
        int age1 = 1;
        int age2 = 4;
        
        stLog.info("====  Raw Data  =========================================");
        stLog.info("momo   age: " + age1 );
        stLog.info("Lanlan age: " + age2 );
        stLog.info("message   : '" +  s0 + "' (len:" + s0.length() + ")" );
        
        
        ByteBuffer data = ByteBuffer.allocate(1024);
        data.putInt(age1); 
        data.putInt(age2);
        data.put(util.stringFunc.toBuffer(s0));
        data.limit(data.position());
        data.rewind();
        
        byte[] raw = data.array();
        int len = data.limit();
        
        byte[] enc_data = encrypt(raw, 0, len);
        int enc_len = enc_data.length;
        
        byte[] dec_data = decrypt(enc_data, 0, enc_len);
        int dec_len = dec_data.length;
       
        stLog.info("raw len: " + len);
        stLog.info("enc len: " + enc_len);
        stLog.info("dec len: " + dec_len);
        
        
        ByteBuffer data2 = ByteBuffer.wrap(dec_data);
        stLog.info("====  Decrypted  =========================================");
        stLog.info("momo   age: " + data2.getInt());
        stLog.info("Lanlan age: " + data2.getInt());
        String s1 = util.stringFunc.fromBuffer(data2);
        stLog.info("message   : '" +  s1 + "' (len:" + s1.length() + ")" );
    }
    
    
    
    private boolean testEncDec() throws Exception {
        String s0 = "Hi Momo & Lanlan!";
        int age1 = 1;
        int age2 = 4;
        
        /*
        stLog.info("====  Raw Data  =========================================");
        stLog.info("momo   age: " + age1 );
        stLog.info("Lanlan age: " + age2 );
        stLog.info("message   : '" +  s0 + "' (len:" + s0.length() + ")" );
        */
        
        ByteBuffer data = ByteBuffer.allocate(1024);
        data.putInt(age1); 
        data.putInt(age2);
        data.put(util.stringFunc.toBuffer(s0));
        data.limit(data.position());
        data.rewind();
        
        byte[] raw = data.array();
        int raw_len = data.limit();
        
        byte[] enc_data = encrypt(raw, 0, raw_len);
        int enc_len = enc_data.length;
        
        byte[] dec_bytes = decrypt(enc_data, 0, enc_len);
        return dec_bytes.length == raw_len;

        /*
         
     	int dec_len = dec_data.length;
        ByteBuffer data2 = ByteBuffer.wrap(dec_data);
        
        stLog.info("raw len: " + len);
        stLog.info("enc len: " + enc_len);
        stLog.info("dec len: " + dec_len);
       
        
        stLog.info("====  Decrypted  =========================================");
        stLog.info("momo   age: " + data2.getInt());
        stLog.info("Lanlan age: " + data2.getInt());
        String s1 = util.stringFunc.fromBuffer(data2);
        stLog.info("message   : '" +  s1 + "' (len:" + s1.length() + ")" );
        */
    }
    
    
    /**
     * test cost of 1M operations.
     * 
     * @throws Exception
     */
    private void test02() throws Exception  {
    	final int M = 1024;
    	final int N = 1024;
    
    	stLog.info("====  Test Cipher Cost  =========================================");
    	
    	final long T0 = System.currentTimeMillis();
    	int c = 0;
    	for(int i=0; i<M; i++){
    		for(int j=0; j<N; j++){
    			c++;
    			if(!testEncDec()){
    				stLog.error("faiure!");
    				break;
    			}
    		}
    	}
    	
    	final long ms_cost = util.getCostMillis(T0);
    	stLog.info("Cipher Count: " + c );
    	stLog.info("Cipher Cost : " + ms_cost +"ms" );
    }
    
    
   

    public static void main(String[] args) throws Exception {
    	StConfigLog.instance();
   
        String key = "Bar12345Bar12345"; // 128 bit key
        String initVector = "RandomInitVector"; // 16 bytes IV
        StCipher2 ci = new StCipher2(key, initVector);    
        ci.test01();
        ci.test02();
    }
}


