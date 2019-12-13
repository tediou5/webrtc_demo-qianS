package cn.teclub.common;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.Map.Entry;

public class TestStringOpt extends ChuyuObj {
	
	public static void main(String[] args){
		TestStringOpt t = new TestStringOpt();
		//t.testString("aabbcc 中国馆 上海浦东");
		t.testString2("AABBCC");
		t.testString2("AABB中");
		t.testString2("AABB中国");
		t.testString2("AABB曹郁铗");
	}
	
	
	public void testString2(final String label){
		ByteBuffer buf = util.stringFunc.toBuffer(label);
		System.out.println("==== Dump Buffer ====:" + util.dumpFunc.addDumpCharHex(null, buf));
		
		String str = util.stringFunc.fromBuffer(buf);
		System.out.println("Source String: " + label);
		System.out.println("Dst    String: " + str);
	}
	
	
	
	
	public void testString(String str){
		System.out.println("#### intput str: '" + str + "'");
		System.out.println("str length: " + str.length() );
		
		char[] char_array = str.toCharArray();
		
		System.out.println("#### char array: " + char_array.toString() );
		System.out.println("char array length: " + char_array.length );
		int i=0;
		for(char cc : char_array){
			System.out.println("char["+ i++  +"]: " + cc);
		}
		
		System.out.println("#### all available charsets ");
		SortedMap<String, Charset> cs_list = Charset.availableCharsets();
		Iterator<Entry<String, Charset>> iter = cs_list.entrySet().iterator();
		for (;iter.hasNext();){
			Charset cs = iter.next().getValue();
			System.out.println("avaiable charset: " + cs.toString());
		}
		
		getStringByteArray(str, null);
		getStringByteArray(str, Charset.defaultCharset());
		getStringByteArray(str, Charset.forName("GBK"));
	}
	
	
	static void getStringByteArray(String str, Charset cs){
		ChuyuUtil util = ChuyuUtil.getInstance();
		
		byte[] byte_array2 = null;
		if(cs == null){
			byte_array2 = str.getBytes();
			//System.out.println("charset name: <default>");
		}else{
			byte_array2 = str.getBytes(cs);
			//System.out.println("charset name: " + cs.toString() );
		}
		System.out.println("\n#### BYTE ARRAY encoded with CHARSET: " + (cs==null ? "<default>": cs.toString()));
		System.out.println("byte array length: " + byte_array2.length );
		int i = 0;
		for(byte bb : byte_array2){
			System.out.print("  ["+ i++  +"]" + util.to2CharHex(bb));
		}
		System.out.println("");
		
		if(cs == null){
			return;
		}
		String s2 = new String(byte_array2, cs);
		System.out.println("Back to String: " + s2 + "\n");
	}
}