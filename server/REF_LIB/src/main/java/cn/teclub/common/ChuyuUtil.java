package cn.teclub.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;


/**
 * Utility Class
 * 
 * @author mancook
 *
 */
public class ChuyuUtil extends ChuyuFamily 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS
	////////////////////////////////////////////////////////////////////////////

    private static ChuyuUtil _Instance = null;

    /*
     * used to test its methods
     * 
     */
    public static void main(String[] args) throws SocketException{
    	ChuyuUtil util = getInstance();
    	for(String mac: util.getMacAddress()){
    		System.out.println("mac address: " + mac);
    	}
    }

    
    public static ChuyuUtil getInstance(){
        if(null == _Instance ){
            _Instance = new ChuyuUtil();
        }
        return _Instance;
    }


	////////////////////////////////////////////////////////////////////////////
    // Inner Classes
	////////////////////////////////////////////////////////////////////////////
	
    
	public class StringFunc{
    	public String wrap(String s){
    		if(s == null) return "'<null>'";
			return "'" + s + "'";
    	}
    	
    	
    	
    	
    	public final int 		STRLEN_SMALL 		= 256  	- 2;
    	public final int 		STRLEN_MIDDLE		= 512  	- 2;
    	public final int 		STRLEN_BIG 			= 1024 	- 2;
    	public final int 		STRLEN_HUGE 		= 1024*32 -2;
    	public final String		CHAR_SET			= "UTF-8";
    	
    	
    	/**
    	 * <h2>Convert a string into a buffer with its length. </h2>
    	 * 
    	 * <pre> 
    	 * ATTENTION: This method NEVER returns null!
    	 * If input string is null or empty, the 2-byte buffer is returned,
    	 * which contains only the array length. 
    	 * 
    	 * The revert method is fromByteArray(ByteBuffer). 
    	 * 
    	 * Converted byte[] array:
    	 * 
    	 * 	[0] [1] [2] [3] [4] [5] .... ....  [n] 	
    	 * 	|-----|<----- UTF8 byte array ------>|	
    	 *  |     |                              |
    	 *     \										
    	 *      \-----> length of byte[] array				
    	 *    
    	 * NOTE: The starting 2 bytes are for the string length.    
    	 * 
    	 * </pre>
    	 * 
    	 * @param str input string
    	 * @return byte buffer of the string in UTF8
    	 */
    	public ByteBuffer toBuffer(final String str){
    		if(str ==null || str.length() == 0){
    			ByteBuffer buffer = ByteBuffer.allocate(2);
    			buffer.putShort((short)0);
    			buffer.rewind();
    			return buffer;
    		}
    		
    		String s2 = str;
    		if(str.length() > STRLEN_HUGE){
    			stLog.warn("string length ("+ str.length()  +") is larger than max value ( " + STRLEN_HUGE + ")");
    			s2 = str.substring(0, STRLEN_HUGE);
    		}

    		byte[] array = s2.getBytes(Charset.forName(CHAR_SET));
    		ByteBuffer buffer = ByteBuffer.allocate( 2 + array.length);
    		buffer.putShort((short) array.length );
    		buffer.put(array);
    		buffer.rewind();
    		return buffer;
    	}
    	
    	
       	/**
    	 * Convert a string array into a ByteBuffer, which rewinds (position to 0).
    	 * 
    	 * <pre> 
    	 * [2016-10-17] TODO: save array length in the starting bytes!
    	 * 
    	 * This methods returns null if: 
    	 *   - str_list is NULL;
    	 *   - it has NO members;
    	 * </pre>
    	 * 
    	 * @param str_list a list of string
    	 * @return byte buffer

    	 */
    	public ByteBuffer toBuffer(final String[] str_list){
    		if(str_list == null || str_list.length < 1){
    			return null;
    		}
    		final int COUNT = str_list.length;
    		int  buf_size = 0;
    		byte[][] str_bytes  =new byte[COUNT][];
    		
    		for(int i=0; i < COUNT; i++){
    			buf_size += 2;
    			if(str_list[i] == null){
    				str_bytes[i] = null;
    			}else{
	    			str_bytes[i] = str_list[i].getBytes(Charset.forName(CHAR_SET));
	    			buf_size += str_bytes[i].length;
    			}
    		}
    		
    		ByteBuffer buf = ByteBuffer.allocate(buf_size);
			for (String str : str_list) {
				if (str == null) {
					buf.putShort((short) 0);
				} else {
					byte[] bytes = str.getBytes();
					buf.putShort((short) bytes.length);
					buf.put(bytes);
				}
			}
    		buf.rewind();
    		return buf;
    	}
    	
    	
     	
    	/**
    	 * <h2>Create a string from byte-array</h2>
    	 * 
    	 * <pre>
    	 * This is the revert method of toByteArray(String). 
    	 * NOTE: ByteBuffer position is NOT reset after this method.
    	 * </pre>
    	 * 
    	 * @param buffer byte buffer of a string encoded in UTF8
    	 * @return a string
    	 */
    	public String fromBuffer(ByteBuffer buffer){
    		if(buffer == null || buffer.remaining() < 2){
    			stLog.error("Input Buffer is NULL or has < 2 bytes ");
    			return null;
    		}
    		
    		// for UTF-8 string, bytes length is about 2 times of the string length
    		final int MAX_BYTE_ARRAY_LEN = 2 + STRLEN_HUGE*2; 	
    		int array_len = (0xFFFF & buffer.getShort());
    		int n_left = buffer.remaining();
    		assertTrue(array_len >=0);
    		assertTrue(array_len <= n_left, 
    				"Try to get " + array_len + " bytes from buffer. " +
	    			"But buffer has only " + n_left + " bytes remaining!!!");
    		if(array_len == 0){
    			//stLog.debug("String length is: " + array_len);
    			return "";
    		}
    		
    		if(array_len > MAX_BYTE_ARRAY_LEN){
    			stLog.error("bytes-array-length ("+ array_len +") > MAX-ARRAY-LENGTH ( " + MAX_BYTE_ARRAY_LEN + ")");
    			stLog.error("bytes array is truncated to " +  MAX_BYTE_ARRAY_LEN);
    			array_len = MAX_BYTE_ARRAY_LEN;
    		}
    		//stLog.debug("Build a string with bytes: " + array_len);
	    	byte[] array = new byte[array_len];
    		buffer.get(array);
    		return new String(array, Charset.forName(CHAR_SET));
    	}
  
    	
    	
    	/**
    	 * Create a string array from a byte buffer.
    	 * 
    	 * <pre> 
    	 * This is the revert method of toByteArray(String[]). 
    	 * NOTE: The byte buffer position is NOT rewind in this method!
    	 * </pre>
    	 * 
		 * @param buf input byte buffer
		 * @param count bytes to read
		 * @return a string
		 */
		public String[] fromBufferToArray(final ByteBuffer buf, final int count ) {
    		if(buf == null || buf.remaining() < 2){
    			stLog.error("Input Buffer is NULL or has < 2 bytes ");
    			return null;
    		}
    		if(count < 1){
    			stLog.error("Invalid Count Value: " + count);
    			return null;
    		}
    		final String[] array = new String[count];
    		for(int i=0; i<count; i++){
    			if(buf.remaining() < 1){
    				stLog.error("Missed some strings!  Expected:" + count + ", Result:" + (i+1));
    				break;
    			}
				array[i] = fromBuffer(buf);
    		}
    		return array;
    	}
    	
    	
    	public int getVisibleLength(final String str){
    		if(str == null){
    			return 0;
    		}
			int str_visible_length = str.length();
			for(int i=0; i<str.length(); i++){
				char c = str.charAt(i);
				if(c > 256){
					str_visible_length++;
				}
			}
			return str_visible_length;
    	}
    	
    	
    	public String format(String s, int width){
    		if(s.length() >= width || width < 1){
    			return s;
    		}
    		int n = width - s.length();
    		StringBuilder sbuf = new StringBuilder(width);
    		sbuf.append(s);
    		for(int i=0; i < n; i++){
    			sbuf.append(" ");
    		}
    		return sbuf.toString();
    	}
    	
    	
    	/**
    	 * <pre>
    	 * Valid MAC address:
    	 *     7c-d1-c3-91-f6-9c
    	 * </pre>
    	 * 
    	 * @param mac_addr_str input MAC address
    	 * @return true if correct
    	 */
    	public boolean isMacAddr(final String mac_addr_str){
    		final String mac_addr = mac_addr_str.trim().toUpperCase();
    		return mac_addr.matches("^([0-9A-F][0-9A-F]-){5}[0-9A-F][0-9A-F]$");
    	}
    	
    	
    	public boolean isPhoneNumber(final String phone_num_str){
    		final String phone_number = phone_num_str.trim();
    		return phone_number.matches("^1[\\d]{10}$");
    	}
    }
    
    
	public class TimeFunc
	{
		public long convertToMS(int year, int month, int day, int hour_of_day, int min, int sec){
			// month values: 0 ~ 11
	    	GregorianCalendar gc = new GregorianCalendar();
	    	gc.set(year, month, day, hour_of_day, min, sec);
	    	return gc.getTimeInMillis();
		}
		
		
		/**
		 * <p> Supported formats: 
		 * <li> "yyyy-MM-dd" 	e.g. '2016-01-02'
		 * <li>"yyyyMMdd_hh" 	e.g. '20160312-14'
		 * 
		 * @param date_str - input date-time string. e.g. '2016-01-02'
		 * @return timestamp in MS
		 */
		public long convertDateStringToMS(String date_str){
			SimpleDateFormat[] date_patterns = {
					new SimpleDateFormat("yyyy-MM-dd"),
					new SimpleDateFormat("yyyyMMdd-HH"), 
					new SimpleDateFormat("yyyyMMdd_HHmmss"), 
					new SimpleDateFormat("yyyyMMdd_HH"), 
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			};
			
			for(SimpleDateFormat format: date_patterns){
				try {
					Date date = format.parse(date_str);
					return date.getTime();
				} catch (ParseException e) {
                    // [2017-4-6] formats are tested one-by-one.
                    // This exception occurs until the correct format.
                    //
                    //System.out.println("Fail on pattern: " + format.toPattern());
					//e.printStackTrace();
				}
			}//for
			return 0;
		}
	}
	
	
	public class NumberFunc 
	{
		
		/**
		 * <p> If parsing fails, DO NOT throw runtime exception. Just return 0.
		 * 
		 * @param s Int String
		 * @return Int
		 */
		public int parseInt(String s){
			try{
				return Integer.parseInt(s );
			}catch(NumberFormatException e){
				stLog.error(getExceptionDetails(e, "Parse Integer Error!"));
				return 0;
			}
		}

		public float parseFloat(String s) {
			try{
				return Float.parseFloat(s );
			}catch(NumberFormatException e){
				stLog.error(getExceptionDetails(e, "Parse Integer Error!"));
				return 0;
			}
		}

		

		public long parseLong(String s) {
			try{
				return Long.parseLong(s);
			}catch(NumberFormatException e){
				stLog.error(getExceptionDetails(e, "Parse Long Error!"));
				return 0;
			}
		}
	}
	
	
	public class DumpFunc
	{
		private final static int 		DUMP_LINE_LENGTH 		= 100;
		private final static int 		BORDER_CHAR_NUM =  2;  // starting and ending '|' char
		private final static String 	DUMP_LINE_PRE_CHAR 		= "\n\t";
		private final static int 		HEADER_PRE_CHAR_NUM = 8;
		
		public void addCharArray(StringBuffer sbuf, String ch, int dup_num){
			for(int i=0; i<dup_num; i++){
				sbuf.append(ch);
			}
		}
		
		public StringBuffer addDumpLine(StringBuffer sbuf, String str){
			// check if there is '\n' in the string
			String[] list  = str.split("\n");
			for(String s: list){
				this.addDumpLineNoBreak(sbuf, s);
			}
			return sbuf;
		}
		
		
		private StringBuffer addDumpLineNoBreak(final StringBuffer sbuf, final String str){
			final int MAX_LINE_LEN = DUMP_LINE_LENGTH - BORDER_CHAR_NUM;
			if(str == null){
				stLog.error("DO NOT dump a NULL object!");
				return sbuf;
			}
			
			final int str_visible_length = stringFunc.getVisibleLength(str);
			if(str_visible_length <= MAX_LINE_LEN){
				return addDumpLineSingle(sbuf, str);
			}
			
			// divide a long line
			ArrayList<String> lines = new ArrayList<>();
			for(int start_i=0; start_i < str.length(); start_i += MAX_LINE_LEN ){
				int end_i = start_i + MAX_LINE_LEN;
				if( end_i > str.length()){
					end_i = str.length();
				}
				String s = str.substring(start_i, end_i);
				//stLog.debug("Get sub-string: " + s);
				lines.add(s);
			}
			for(String s: lines){
				addDumpLineSingle(sbuf, s);
			}
			return sbuf;
		}
		
		
		private StringBuffer addDumpLineSingle(StringBuffer sbuf, String str){
			final int MAX_LINE_LEN = DUMP_LINE_LENGTH - BORDER_CHAR_NUM;
			final int str_vis_len  = stringFunc.getVisibleLength(str);
			final int LEFT_CHAR = MAX_LINE_LEN -  (str == null ? 0 : str_vis_len);
			sbuf.append(DUMP_LINE_PRE_CHAR);
			sbuf.append("|");
			sbuf.append(str);
			this.addCharArray(sbuf, " ", (LEFT_CHAR>0? LEFT_CHAR : 0) );
			sbuf.append("|");
			return sbuf;
		}
		
		
		
		
		public void addDumpLines(StringBuffer sbuf, ArrayList<String> list){
			for(Object obj: list){
				dumpFunc.addDumpLine(sbuf, obj.toString());
			}
		}
		
		public void addDumpAttributeString(StringBuffer sbuf, StringBuffer attr_sbuf){
			String ss = attr_sbuf.toString();
			//String ss2 = ss.replaceAll("\\|", "\\|#");
			//String ss3 = ss2.replaceAll("\\|#\n", "#\\|\n");
			String ss2 = ss.replaceAll("\\|", "\\|\\|");
			sbuf.append(ss2);
		}

		
		public void addDumpSuperString(StringBuffer sbuf, StringBuffer super_sbuf){
			String ss = super_sbuf.toString();
			String ss2 = ss.replaceAll("\n\t", "\n\t |>>");
			sbuf.append(ss2);
			dumpFunc.addDumpLine(sbuf, "              /\\   ");
			dumpFunc.addDumpLine(sbuf, "             /  \\  ");
			dumpFunc.addDumpLine(sbuf, "              II    ");
			dumpFunc.addDumpLine(sbuf, "              II    ");
			
			this.addDumpSectionLine(sbuf);
		}
		
		
		/**
		 * Header Line: ONE line like: <br/>
		 * ++++++++  HEADER LINE +++++++++++++++++++++++++++++++++++++++++ <br/>
		 * 
		 * @param sbuf string buffer
		 * @param hdr_str header string
		 * @param mark_char must be ONLY ONE character! e.g. "#" or "*" 
		 */
		public void addDumpHeaderLine(StringBuffer sbuf, String hdr_str, String mark_char){
			//StringBuffer line_buf = new StringBuffer(DUMP_LINE_LENGTH);
			StringBuffer hdr_line = makeHeaderLine(hdr_str, (DUMP_LINE_LENGTH-2), HEADER_PRE_CHAR_NUM,  mark_char);
			dumpFunc.addDumpLine(sbuf, hdr_line.toString());
		}
		
		public void addDumpHeaderLine(StringBuffer sbuf, String header){
			this.addDumpHeaderLine(sbuf, header, "#");
		}
		
		
		public void addDumpStartLine(StringBuffer sbuf, String header){
			//  Start Line: TWO lines.
			this.addDumpHeaderLine(sbuf, null);
			this.addDumpHeaderLine(sbuf, header);
		}
		
		public void addDumpEndLine(StringBuffer sbuf){
			this.addDumpHeaderLine(sbuf, null, "=");
		}
		
		public void addDumpSectionLine(StringBuffer sbuf){
			this.addDumpHeaderLine(sbuf, null, "-");
		}
		
		
		public StringBuffer addDumpCharHex(StringBuffer sbuf, final ByteBuffer buf){
			if(null == sbuf){
				sbuf = new StringBuffer(512);
			}
			addDumpSectionLine(sbuf);
			addDumpLine(sbuf, "  Buffer Capacity: " + buf.capacity());
			addDumpLine(sbuf, "  Buffer Remaining: " + buf.remaining());
			addDumpLine(sbuf, "  Buffer Position: " + buf.position());
			addDumpHeaderLine(sbuf, null, " ");
			sbuf.append(toCharHexBuf(buf.array()));
			addDumpSectionLine(sbuf);
			return sbuf;
		}



		
		public StringBuffer dumpByteBuffer(ByteBuffer buffer){
			//			StringBuffer sbuf = new StringBuffer(256);
			//			sbuf.append("\n\t Buffer Capacity:  " + buffer.capacity());
			//			sbuf.append("\n\t Buffer Limit:     " + buffer.limit());
			//			sbuf.append("\n\t Buffer Position:  " + buffer.position());
			//			sbuf.append("\n\t Buffer Remaining: " + buffer.remaining());
			//			return sbuf;
			return addDumpCharHex(null, buffer);
		}
		
	}
	
	
	
	public class FileFunc
	{
    	/**
    	 * <p> Get a list of file names, only. NOT the full pathname.
    	 * 
    	 * @param files file objects
    	 * @return file name list
    	 */
    	
		public String[] getFilenames(File[] files){
    		if(files == null || files.length < 1){
    			return null;
    		}
    		int i=0;
    		String[] list = new String[files.length];
    		for(File f: files){
    			list[i++] = f==null ? "<null>" : f.getName();
    		}
    		return list;
    	}
    	
    	
    	public String getFilenamesString(File[] files){
    		if(files == null || files.length < 1){
    			return null;
    		}
    		StringBuilder sb = new StringBuilder(128);
    		for(File f: files){
    			sb.append(f==null ? "<null>" : f.getName());
    			sb.append(";");
    		}
    		return sb.toString();
    	}
    	
		
		/**
		 * <p> Search files in directory 'dir', which start with 'file_prefix'
		 * 
		 * @param dir file object of the searching directory
		 * @param file_prefix filename prefix
		 * @return found files
		 */
		public File[] findFiles(final File dir,  final String file_prefix){
			if(!dir.isDirectory()){
				stLog.error("The 1st argument is NOT a directory -- " + stringFunc.wrap(dir.getAbsolutePath()));
				return null;
			}
			final String PREFIX = file_prefix == null ? null : file_prefix.trim();
			stLog.debug("Searching dir " + stringFunc.wrap(dir.getAbsolutePath()) +  
					" for files starting with " + stringFunc.wrap(PREFIX)) ;
			return dir.listFiles(new FileFilter() {
			    public boolean accept(File pathname) {
					return PREFIX == null || PREFIX.length() == 0 || (pathname.getName().indexOf(PREFIX) == 0);
				}
			});
		}
		

		
		/**
		 * <p> Search files in directory 'dir_path', which starts with 'file_prefix', 
		 * excluding those whose name is 'exclude_name'
		 * 
		 * <p> Return files must have READ and WRITE right.
		 * 
		 * @param dir_path searching directory
		 * @param file_prefix file prefix
		 * @param exclude_name  excluded file name
		 * @return found files
		 */
		public File[] findFiles(String dir_path,  final String file_prefix, final String exclude_name){
			File dir = new File(dir_path );
			if(!dir.isDirectory()){
				stLog.error("The 1st argument is NOT a directory -- " + stringFunc.wrap(dir.getAbsolutePath()));
				return null;
			}
			stLog.debug("Search dir " + stringFunc.wrap(dir.getAbsolutePath()) + 
					"\n\t for files starting with " + stringFunc.wrap(file_prefix)  + 
					"\n\t excluding " + stringFunc.wrap(exclude_name));
			return dir.listFiles(new FileFilter() {
			    public boolean accept(File f) {
			    	stLog.trace("check file: " + f.getName());
			    	return ((f.getName().indexOf(file_prefix) == 0) && (!f.getName().equals(exclude_name))  && f.canRead() && f.canWrite());
			    }
			});
		}
		
		
		
		/**
		 * <p> Search files in directory 'dir_path', which starts with 'file_prefix'.
		 * 
		 * <p> Return files must have READ and WRITE right.
		 * 
		 * @param dir_path earching directory
		 * @param file_prefix file prefix
		 * @return found files
		 */
		public File[] findFiles(String dir_path,  final String file_prefix){
			return this.findFiles(dir_path, file_prefix, null);
		}
		
	}
	
	

	////////////////////////////////////////////////////////////////////////////
    // Instance Members 
	////////////////////////////////////////////////////////////////////////////
    
	protected final Logger 	stLog = Logger.getLogger(this.getClass());
	public final TimeFunc	timeFunc = new TimeFunc();
	public final FileFunc 	fileFunc = new FileFunc();
	public final StringFunc stringFunc = new StringFunc();
	public final NumberFunc numberFunc = new NumberFunc();
	public final DumpFunc 	dumpFunc = new DumpFunc();
	
	/*public final StBasicCipher cipher;
	
	public ChuyuUtil() {
		 try {
			cipher = new StBasicCipher();
		} catch (UnsupportedEncodingException | GeneralSecurityException  e) {
			e.printStackTrace();
			throw new StErrUserError("fail to create basic cipher: " + e );
		}
	}*/
	
	
	
	
	public StringBuffer putObjectArray(Object[] arr){
		StringBuffer sbuf = new StringBuffer();
		for(Object obj: arr){
			sbuf.append(obj.toString());
		}
		return sbuf;
	}
	
	
	public String getExceptionDetails(Throwable e, String info){
		StringBuilder sbuf = new StringBuilder(512);
		sbuf.append(info);
		sbuf.append("\n\t [chuyu] Throwable:  "); sbuf.append(e.toString());
		sbuf.append("\n\t [chuyu] Message:    "); sbuf.append(e.getMessage());
		sbuf.append("\n\t [chuyu] Cause:      "); sbuf.append(e.getCause());


		for(StackTraceElement st : e.getStackTrace()){
			sbuf.append("\n\t\t [chuyu] StackTrace: "); sbuf.append(st.toString());
		}
		
		////// [theodor:2014-11-10] show stack trace on stdout 
		//e.printStackTrace();
		//
		return sbuf.toString();
	}


	public String getThrowableInfo(Throwable e, String info){
		return info +
				"\n\t [chuyu] Throwable:  " + e.toString() +
				"\n\t [chuyu] Message:    " + e.getMessage() +
				"\n\t [chuyu] Cause:      " + e.getCause();
	}



	public String getThrowableStack(Throwable e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();   // stack trace as a string
    }



	
	public String dumpCallStackCurrent(){
		StringBuilder sbuf = new StringBuilder(256);
    	StackTraceElement [] stack_elem_array = Thread.currentThread().getStackTrace();
        sbuf.append("\n#### Current Stack Trace ####" );
        for (StackTraceElement element: stack_elem_array) {
            sbuf.append("\n\t").append(element);
        }
		return sbuf.toString();
	}


	
	public String dumpCallStackAll(){
		StringBuilder sbuf = new StringBuilder(256);
        for (Map.Entry <Thread, StackTraceElement []> entry:  Thread.getAllStackTraces().entrySet ())
        {
            //System.out.println (entry.getKey ().getName () + ":");
            sbuf.append("\n#### Stack Trace").append(entry.getKey().getName()).append(" ####");
            for (StackTraceElement element: entry.getValue ()) {
                // System.out.println ("\t" + element);
                sbuf.append("\n\t").append(element);
            }
        }
		
		return sbuf.toString();
	}
	

	// -----------------------------------------------------------------------------
	// utilities to make headlines
	// -----------------------------------------------------------------------------
	
	
	/**
	 * <p> Header Line: ONE line like: <br/>
	 * ========  HEADER STRING ========================================= <br/>
	 * 
	 * @param header header string
	 * @param length header line length
	 * @return string buffer
	 */
	public StringBuffer makeHeaderLine(String header, int length){
		return this.makeHeaderLine(header, length, 8, "=");
	}


    
	public StringBuffer makeHeaderLine(String header, int length, char mark_ch){
		return this.makeHeaderLine(header, length, 8, "" + mark_ch);
	}

	
	/**
	 * <h2>Add a header line into input string buffer. </h2>
	 * 
	 * Header Line: ONE line like: <br/>
	 * ++++++++  HEADER STRING +++++++++++++++++++++++++++++++++++++++++ <br/>
	 * 
	 * @param header		header string;
	 * @param length		header line length;
	 * @param pos			header string position in the header line;
	 * @param mark_char		mark character in the head line, e.g. '#', or '~';
	 * @return string buffer
	 */
	public StringBuffer makeHeaderLine( String header, int length, int pos,  String mark_char ){
		final int LEFT_CHAR_NUM = length - pos - (header == null ? 0 :header.length());
		
		StringBuffer sbuf = new StringBuffer(128);
		
		/*
		// DEBUG:
		sbuf.append("\n");
		for(int i=0;i<length; i++){
			sbuf.append(i%10);
		} */
		
		//sbuf.append("\n");
		dumpFunc.addCharArray(sbuf, mark_char, pos);
		if(header != null) {
			sbuf.append(header);
		}
		dumpFunc.addCharArray(sbuf, mark_char, (LEFT_CHAR_NUM>0? LEFT_CHAR_NUM : 0) );
		
		return sbuf;
	}
	
	
	// -----------------------------------------------------------------------------
	// network utilities 
	// -----------------------------------------------------------------------------
	
	public InetAddress getIPv4Address(final byte[] ip){
		if(ip == null){
			stLog.error("Input ip argument is NULL!");
			return null;
		}
		if(ip.length != 4){
			stLog.error("IPv4 bytes is NOT 4!");
			return null;
		}
		
		try {
			return InetAddress.getByAddress(ip);
		} catch (UnknownHostException e) {
			throw new ChuyuRuntimeExp(this.getExceptionDetails(e, "UnknownHostException when converting to InetAddress"));
		}
	}
	
	
	public String getIPv4AddressStr(final byte[] ip){
		InetAddress addr = this.getIPv4Address(ip);
		if(addr == null){
			return null;
		}
		return addr.getHostAddress();
	}
	
	
	public ArrayList<String> getMacAddress() {
		try{
			Enumeration<NetworkInterface> e2 = NetworkInterface.getNetworkInterfaces();
			ArrayList<String> list = new ArrayList<>();
			
			while(e2.hasMoreElements()){
				NetworkInterface n = e2.nextElement();
			    System.out.println("DEBUG: Network Interface: " + n.getName()  + ", "+ n.getDisplayName());
			    if(n.isLoopback() || n.isVirtual()){
			    	continue;
			    }
			    byte[] mac = n.getHardwareAddress();
				if(mac == null){
					System.out.println("DEBUG:  NO MAC address");
					continue;
				}
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < mac.length; i++) {
					sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
				}
				System.err.println("DEBUG: get mac addr: " + sb.toString());
				list.add(sb.toString());
			}//while
			return list;
		}catch(SocketException e){
			stLog.error(getExceptionDetails(e, "Fail to get MAC address"));
			e.printStackTrace();
			return null;
		}
	}
	
	
	
    public InetAddress getIPv4LocalAddress()  throws SocketException{
		this.stLog.warn("TODO: Make sure get the CORRECT local address!");
		//System.out.println("\n\nEnumerate All Interfaces to get local IPv4 address: ");
		Enumeration<NetworkInterface> e2 = NetworkInterface.getNetworkInterfaces();
		while(e2.hasMoreElements()){
			NetworkInterface n = e2.nextElement();
		    //System.out.println("\n==== Enumerate interface: " + n.getDisplayName() );
		    if(!n.isUp()){
		    	//System.out.println("ignore DOWN interface: " + n.getDisplayName() );
		    	continue;
		    }
		    if(n.isLoopback()){
		    	//System.out.println("ignore Loopback interface: " + n.getDisplayName() );
		    	continue;
		    }
		    if(n.isVirtual()){
		    	//System.out.println("ignore Virtual interface: " + n.getDisplayName() );
		    	continue;
		    }
		    
		    Enumeration<InetAddress> ee = n.getInetAddresses();
		    while (ee.hasMoreElements())
		    {
		    	InetAddress i = ee.nextElement();
		    	
		    	//System.out.println("\n---- Enumerate Internet address: " + 
		    	//				i.getHostAddress() + " -- Host Name: " + i.getHostName() ); 
		        
		    	if(i.isLoopbackAddress()){
		    		//System.out.println("ignore Loopback address: " + i.getHostAddress());
		    		continue;
		    	}
		    	
		    	if(i instanceof Inet6Address){
		    		//System.out.println("ignore IPv6 address: " + i.getHostAddress());
		    		continue;
		    	}
		    	
		        //System.out.println("**** IPv4 Internet Address:" + i.getHostAddress());
		        this.stLog.debug("Use Local IPv4 Internet Address: " + i.getHostAddress());
		        return i;
		    }//while
		}//while
		
		return null;
	}
	
	
	// -----------------------------------------------------------------------------
	// other utilities 
	// -----------------------------------------------------------------------------

	public void sleep(int millisec){
    	try {
			Thread.sleep(millisec);
		} catch (InterruptedException e) {
			this.stLog.error(this.getExceptionDetails(e, "Exception when sleeping"));
		}
	}
	
	
	private static final String TEST_MILESTONE_PREFIX = "$$$$ TEST MILESTONE $$$$  ";
	
	public String testMilestoneLog(StringBuffer msg){
		return this.testMilestoneLog(msg.toString());
	}
	
	/**
	 * Test Milestone is logged as TRACE level by ChuyuLog
	 * 
	 * @param msg message
	 * @return input message
	 */
	public String testMilestoneLog(final String msg){
		StringBuilder sbuf = new StringBuilder();
		final String[] list  = msg.split("\n");
		for(final String s: list){
			sbuf.append(TEST_MILESTONE_PREFIX).append(s).append("\n");
		}
		ChuyuLog.getInstance().trace(sbuf.toString());
		return msg;
	}
	
	
	public void assertTrue(boolean v, String msg){
		if(!v){
			throw new ChuyuRuntimeExp("[Assert Failure] " + msg);
		}
	}
	
	public void assertTrue(boolean v){
		if(!v){
			throw new ChuyuRuntimeExp("[Assert Failure]");
		}
	}
	
	public void assertNotNull(Object obj) {
		if(null == obj){
			throw new ChuyuRuntimeExp(
					"obj reference is NULL!");
		}
	}
	
	public void assertNotNull(Object obj, String err_msg){
		if(null == obj){
			throw new ChuyuRuntimeExp(
					"obj reference is NULL! -- Error Message: " + err_msg);
		}
	}
	
	
	
	
	//	/**
	//	 * <p> NOTE: some android class need this method! As they are not sub-class of ChuyuObj!
	//	 * 
	//	 * @param msg
	//	 * @param value
	//	 * @throws StMakeSureFailure
	//	 */
	//	public void makeSureTrue(boolean value, String msg) throws StMakeSureFailure{
	//		if(value != true){
	//			String error_msg = "Checked value is FALSE! -- Error Message: " + msg;
	//			throw new StMakeSureFailure(error_msg);
	//		}
	//	}
	
	
	
    public void assertNotEqual(
			final String msg, 
			final int ret, 
			final int not_expected) 
	{
		if(ret == not_expected){
			String error_msg = msg + " -- Have gotton the unexpected value: " + ret ;
			throw new ChuyuRuntimeExp(error_msg);
		}
	}


    
	public void assertNotEqual(
			final String msg, 
			final long ret, 
			final long not_expected) 
	{
		if(ret == not_expected){
			String error_msg = msg + " -- Have gotton the unexpected value: " + ret ;
			throw new ChuyuRuntimeExp(error_msg);
		}
	}



    
	public byte[] getBytesFromShort(short value){
		byte[] buf = new byte[2];
		buf[0] = (byte)((value   & 0x0000FF00)>>>8) ;
		buf[1] = (byte)( value   & 0x000000FF) ;
		return buf;
	}


    
	public byte[] setOperCode(int value){
		byte[] buf = new byte[2];
		buf[0] = (byte) ( ( value & 0xFF000000)  >>> 24 ); // unsigned right shit
		buf[1] = (byte) ( ( value & 0x00FF0000)  >>> 16); // unsigned right shit
		buf[2] = (byte) ( ( value & 0x0000FF00)  >>> 8 ); // unsigned right shit
		buf[3] = (byte) (   value & 0x000000FF);
		return buf;
	}

    
	public byte firstByte(short value){
		return  (byte)((value  & 0x0000FF00)>>>8);
	}

    
	public byte secondByte(short value){
		return (byte)( value & 0x000000FF) ;
	}


    
	public byte getChecksumByte(byte[] buf, int start, int lenvi){
		byte cksum = 0;
		stLog.debug("TODO: Implement CHECKSUM");
		return cksum;
	}
	
	
	public String to2CharHex(byte b){
		int value = b & 0x000000FF;
		if (0 == value) return "00";
		if( value < 16) return "0" + Integer.toHexString(value).toUpperCase(Locale.US);
		return Integer.toHexString(value).toUpperCase(Locale.US	); 
	}
	
	public String to4CharHex(short b){
		int value = b & 0x0000FFFF;
		
		if (0 == value)   		return "0000";
		if( value < 16)   		return ("000" + Integer.toHexString(value).toUpperCase(Locale.US));
		if( value < 256)  		return "00" + Integer.toHexString(value).toUpperCase(Locale.US);
		if( value < (256*16))  	return "0" + Integer.toHexString(value).toUpperCase(Locale.US);
		return Integer.toHexString(value).toUpperCase(Locale.US);
	}
	
	public String to8CharHex(int b){
		int lower = b & 0x0000FFFF;
		int higher = ( (b & 0xFFFF0000) >>> 16) ;
		return  (this.to4CharHex((short)higher) + "-" + this.to4CharHex((short)lower) );
	}


    
	public String to8CharHexPure(int b){
		int lower = b & 0x0000FFFF;
		int higher = ( (b & 0xFFFF0000) >>> 16) ;
		return  (this.to4CharHex((short)higher) + this.to4CharHex((short)lower) );
	}
	

	
	public StringBuffer toCharHexBuf(byte[] array){
		return toCharHexBuf(array, 0, array.length);
	}
	
	
	public StringBuffer toCharHexBuf(byte[] array, int length){
		return toCharHexBuf(array, 0, length);
	}
	
	
	public StringBuffer toCharHexBuf(byte[] array, int offset, int length){
		//String prefix = "AAAA     "	;
		final String PREFIX = "\n\t| "	;
		
		// error checking
		if(offset >= array.length ){
			stLog.error("input offset error!");
			offset = array.length - 1;
		}
		if(array.length < offset + length){
			stLog.error("input length error!");
			length = array.length;
		}
		
		StringBuffer sbuf = new StringBuffer(32);
		if(length < 1){
			return sbuf;
		}
		
		String str_char ="";
		for(int i =0; i<length; i++){
			if(i%16 == 0){
				if(i>0){
					sbuf.append("    ").append(str_char).append(" |");
					str_char = "";
				}
                int i_end = (length < (i+16) ? length : (i+16) ) -1 ;
				sbuf.append(PREFIX + "0x").append(this.to4CharHex((short) i)).append("~").append(this.to4CharHex((short) i_end)).append(" ");
			}
			if( i%4 == 0){
				sbuf.append(" ");
			}
			if( i%2 == 0){
				sbuf.append(" ");
			}
			
			int value = array[offset + i] & 0x000000FF;
			if(value==0){
				sbuf.append("00");
			}else if(value< 16){
				sbuf.append("0");
				sbuf.append(Integer.toHexString(value));
			}else{
				sbuf.append(Integer.toHexString(value));
			}
			if(0x20 <= value && value <= 0x7f) {
				str_char += (char) value;
			}else{
				str_char += '.'; // invisible character
			}
		}
		
		final int b = length % 16;
		StringBuilder last_space = new StringBuilder(16);
		if(b > 0){
			for(int j=b; j<16; j++){
				if( j%4 == 0){
					sbuf.append(" ");
				}
				if( j%2 == 0){
					sbuf.append(" ");
				}
				sbuf.append("  ");
				last_space.append(' ');
			}
		}

		sbuf.append("    ").append(str_char).append(last_space).append(" |");
		return sbuf;
	}

	public String toBinaryString(int a){
    	String s2 = Integer.toBinaryString(a);
    	//System.out.println("\n\t Binary String: '" + s2 +"', length: " + s2.length());
    	
    	int zero_count = 32 - s2.length();
    	StringBuilder sbuf = new StringBuilder(zero_count);
    	for(int i=0; i<zero_count; i++){
    		sbuf.append("0");
    	}
    	sbuf.append(s2);
    	
    	StringBuilder sbuf2 = new StringBuilder();
    	for(int i=0; i<32; i++){
    		if(i%8 == 0 && i > 0){
    			sbuf2.append(" ");
    		}
    		sbuf2.append(sbuf.charAt(i));
    	}
    	//System.out.println("\n\t Binary String: '" + sbuf2.toString() +"', length: " + sbuf2.length());
    	return sbuf2.toString();
	}
	
	public String to16CharHex(long l){
		int lower =   (int) (l & 0xFFFFFFFFL);
		int higher =  (int) ((l & 0xFFFFFFFF00000000L) >>> 32) ;
		return  (this.to8CharHex(higher) + "-" + this.to8CharHex(lower) );
	}
	
	

	
	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static final int TS_TYPE_TIME_ONLY 		= 0;
	private static final int TS_TYPE_DATE_ONLY		= 1;
	private static final int TS_TYPE_DATE_TIME 		= 2;			// 	2015-12-12_12:30:23
	private static final int TS_TYPE_DATE_TIME_2 	= 3;			// 	12:30 PM Oct. 12
	private static final int TS_TYPE_DATE_TIME_MS 	= 4;			// 	2015-12-12_12:30:23_879
	private static final int TS_TYPE_DATE_TIME_SIMPLE = 10;			// 20151215_095820
	private static final int TS_TYPE_DATE_TIME_FOR_FILE = 11;		// same to above
	private static final int TS_TYPE_DATE_TIME_FOR_FILE_MS = 12;	// 20151215_095820_012
	
	

	private StringBuffer getTimeStamp(long ts, int type) {
	    java.text.DecimalFormat df	= new java.text.DecimalFormat("00");	// for month, day, hour, min, second
	    java.text.DecimalFormat df3	= new java.text.DecimalFormat("000"); 	// for ms
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(new java.sql.Date(ts)); 
	    StringBuffer time_stamp = new StringBuffer();
	    switch(type){
	    case TS_TYPE_TIME_ONLY:
	    	time_stamp	.append(df.format(cal.get(Calendar.HOUR_OF_DAY)) )
						.append(":")
						.append(df.format(cal.get(Calendar.MINUTE)))
						.append(":")
						.append(df.format(cal.get(Calendar.SECOND)));
	    	break;
	    	
	    case TS_TYPE_DATE_ONLY:
	    	time_stamp	.append(cal.get(Calendar.YEAR))
						.append("-")
						.append(df.format(1 + cal.get(Calendar.MONTH)))
						.append("-")
						.append(df.format(cal.get(Calendar.DAY_OF_MONTH)));
	    	break;
	    	
	    case TS_TYPE_DATE_TIME_2:
	    	String month = new SimpleDateFormat("MMM", Locale.getDefault()).format(cal.getTime());
	    	time_stamp	.append(cal.get(Calendar.HOUR_OF_DAY))
						.append(":")
						.append(df.format(cal.get(Calendar.MINUTE)))
						.append(" ")                               
						.append(month)
						.append(df.format(cal.get(Calendar.DAY_OF_MONTH)));
	    	break; 

	    case TS_TYPE_DATE_TIME:
	    	time_stamp	.append(cal.get(Calendar.YEAR))
						.append("-")
						.append(df.format(1 + cal.get(Calendar.MONTH)))
						.append("-")
						.append(df.format(cal.get(Calendar.DAY_OF_MONTH)));
	    	time_stamp.append("_");
	    	time_stamp	.append(df.format(cal.get(Calendar.HOUR_OF_DAY)) )
						.append(":")
						.append(df.format(cal.get(Calendar.MINUTE)))
						.append(":")
						.append(df.format(cal.get(Calendar.SECOND)));
	    	break;
	    	
	    case TS_TYPE_DATE_TIME_MS:
	    	time_stamp	.append(cal.get(Calendar.YEAR))
						.append("-")
						.append(df.format(1 + cal.get(Calendar.MONTH)))
						.append("-")
						.append(df.format(cal.get(Calendar.DAY_OF_MONTH)))
						.append("_")
	    				.append(df.format(cal.get(Calendar.HOUR_OF_DAY)) )
						.append(":")
						.append(df.format(cal.get(Calendar.MINUTE)))
						.append(":")
						.append(df.format(cal.get(Calendar.SECOND)))
						.append("_")
						.append(df.format(cal.get(Calendar.MILLISECOND)));
	    	break;
	    	
	    case TS_TYPE_DATE_TIME_FOR_FILE_MS:
	    	time_stamp	.append(cal.get(Calendar.YEAR))
						.append(df.format(1 + cal.get(Calendar.MONTH)))
						.append(df.format(cal.get(Calendar.DAY_OF_MONTH)))
						.append("_")
	    				.append(df.format(cal.get(Calendar.HOUR_OF_DAY)) )
						.append(df.format(cal.get(Calendar.MINUTE)))
						.append(df.format(cal.get(Calendar.SECOND)))
						.append(df3.format(cal.get(Calendar.MILLISECOND)));
	    	break;
	    	
	    case TS_TYPE_DATE_TIME_FOR_FILE:
	    case TS_TYPE_DATE_TIME_SIMPLE:   // also the time stamp for most files
	    	time_stamp	.append(cal.get(Calendar.YEAR))
						.append(df.format(1 + cal.get(Calendar.MONTH)))
						.append(df.format(cal.get(Calendar.DAY_OF_MONTH)))
						.append("_")
	    				.append(df.format(cal.get(Calendar.HOUR_OF_DAY)) )
						.append(df.format(cal.get(Calendar.MINUTE)))
						.append(df.format(cal.get(Calendar.SECOND)));
	    	break;	
	    default:
	    	time_stamp.append("<UnknownTimeStampType>");
	    }
	    return time_stamp;
	}
	
	
	/**
	 * @return  a time stamp string like '13:34:02'
	 */
	public String getTimeOnly(){
		return this.getTimeStamp(System.currentTimeMillis(), TS_TYPE_TIME_ONLY).toString();
	}
	
	/**
	 * @return  a time stamp string like '13:34:02'
	 */
	public String getTimeOnly(final long ms_time){
		return this.getTimeStamp(ms_time, TS_TYPE_TIME_ONLY).toString();
	}
	
	/**
	 * 
	 * @return  a time stamp string like '2015-12-14'
	 */
    
	public String getTimeStampOnlyDate(){
		return this.getTimeStamp(System.currentTimeMillis(), TS_TYPE_DATE_ONLY).toString();
	}
	
	
	/**
	 * 
	 * @param ts  milliseconds since 1970-1-1 00:00 UTC  
	 * 
	 * @return  a time stamp string like '2015-12-14'
	 */
	public String getTimeStampOnlyDate(long ts){
		return this.getTimeStamp(ts, TS_TYPE_DATE_ONLY).toString();
	}
	
	
	/**
	 * 
	 * @return  a time stamp string like '2015-12-14_13:34:02'
	 */
	public String getTimeStamp(){
		return getTimeStamp(System.currentTimeMillis());
	}
	
	
	/**
	 * @param ms - milliseconds since 1970-1-1 00:00 UTC
	 * @return  a time stamp string like '2015-12-14_13:34:02'
	 */
	public String getTimeStamp(long ms){
		return this.getTimeStamp(ms, TS_TYPE_DATE_TIME).toString();
	}
	
	
	/**
	 * 
	 * @return  a time stamp string like '2015-12-14_13:34:02_250'
	 */
    
	public String getTimeStampMS(){
		return getTimeStampMS(System.currentTimeMillis());
	}

	
	/**
	 * 
	 * @return  a time stamp string like '2015-12-14_13:34:02_250'
	 */
	public String getTimeStampMS(final long ts){
		return this.getTimeStamp(ts, TS_TYPE_DATE_TIME_MS).toString();
	}
	
	
	/**
	 * @return a time stamp string like '13:34 Oct 21'
	 */
    
	public String getTimeStamp2(){
		return this.getTimeStamp(System.currentTimeMillis(), TS_TYPE_DATE_TIME_2).toString();
	}
	
	
	/**
	 * @return  a time stamp looks like 'YYYYMMDD_hhmmss_SSS'
	 */
	public String getTimeStampForFileMS(){
	    return this.getTimeStamp(System.currentTimeMillis(), TS_TYPE_DATE_TIME_FOR_FILE_MS).toString();
	}
	
	
	/**
	 * @return a time stamp looks like 'YYYYMMDD_hhmmss'
	 */
	public String getTimeStampForFile(){
		return this.getTimeStamp(System.currentTimeMillis(), TS_TYPE_DATE_TIME_SIMPLE).toString();
	}
	
	/**
	 * @return a time stamp looks like 'YYYYMMDD_hhmmss'
	 */
	public String getTimeStampForFile(long ts){
		return this.getTimeStamp(ts, TS_TYPE_DATE_TIME_SIMPLE).toString();
	}
	
	
	
	/**
	 * <h2>Get a BufferedImage object with current time stamp.  </h2>
	 * 
	 * @param msg  message before the time stamp
	 * @param font  e.g. new Font("Arial", Font.PLAIN, 24);
	 * @return Buffer Image ???
	 */
    
	public BufferedImage getTimeStampImage(String msg, Font font){
		String text = (msg == null? "" : msg) + " " + this.getTimeStamp();
		
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = (int) ( fm.stringWidth(text) * 1.2 );
        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, 0, fm.getAscent());
        g2d.dispose();
        
		return img;
	}


    
	public int makeRandInt(int min, int max) {
	    Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
	}


    
	public byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(8);
	    buffer.putLong(x);
	    return buffer.array();
	}
	
	public byte[] IntToBytes(int x) {
	    ByteBuffer buffer = ByteBuffer.allocate(4);
	    buffer.putInt(x);
	    return buffer.array();
	}


    
	public String intToIpString(int ip){
		byte[] bb = this.IntToBytes(ip);
		StringBuilder sbuf = new StringBuilder(16);
		int i=0;
		for(byte b: bb){
			short b2 = (short)(0x00FF & b);
			sbuf.append(b2);
			if(++i<4){
				sbuf.append(".");
			}
		}
		return sbuf.toString();
	}


    
	public long bytesToLong(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.allocate(8);
	    buffer.put(bytes);
	    buffer.flip();//need flip 
	    return buffer.getLong();
	}


    
	public int bytesToInt(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.allocate(4);
	    buffer.put(bytes);
	    buffer.flip();//need flip 
	    return buffer.getInt();
	}


    
	public short bytesToShort(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.allocate(2);
	    buffer.put(bytes);
	    buffer.flip();//need flip 
	    return buffer.getShort();
	}
	
	
	public void close(Closeable f){
		if(null == f) return;
		try {
			f.close();
		} catch (IOException e) {
			throw new RuntimeException(
					this.getExceptionDetails(e, "IOException when closing the closeable object."));
		}
	}


    
	public void closeSocket(ServerSocket sock){
		if(sock == null || sock.isClosed()){
			return;
		}
		try {
			sock.close();
	        final int SLEEP_MS=100;
			this.stLog.debug("sleep "+ SLEEP_MS  +" ms after closing a TCP socket.");
	        this.sleep(SLEEP_MS);  // sleep a while after closing the TCP socket.
		} catch (IOException e) {
			throw new RuntimeException(
					this.getExceptionDetails(e, "IOException when closing the TCP server-socket."));
		}
	}
	
	public void closeSocketCh(SocketChannel sock_ch){
		if(sock_ch == null){
			return;
		}
		try {
			sock_ch.close();
			// [2016-11-6] why sleep after closing?
			// 
	        //	final int SLEEP_MS=100;
			//	this.stLog.debug("sleep "+ SLEEP_MS  +" ms after closing a TCP socket.");
	        //	this.sleep(SLEEP_MS);  // sleep a while after closing the TCP socket.
		} catch (IOException e) {
			throw new RuntimeException(
					this.getExceptionDetails(e, "IOException when closing the TCP socket channel."));
		}
	}


    
	public void closeSocketCh(ServerSocketChannel sock_ch){
		if(sock_ch == null ){
			return;
		}
		try {
			sock_ch.close();
	        final int SLEEP_MS=100;
			this.stLog.debug("sleep "+ SLEEP_MS  +" ms after closing a TCP socket.");
	        this.sleep(SLEEP_MS);  // sleep a while after closing the TCP socket.
		} catch (IOException e) {
			throw new RuntimeException(
					this.getExceptionDetails(e, "IOException when closing the TCP server socket channel."));
		}
	}


    
	public void closeSocket(Socket sock){
		if(sock == null || sock.isClosed()){
			return;
		}
		try {
			sock.close();
	        final int SLEEP_MS=100;
			this.stLog.debug("sleep "+ SLEEP_MS  +" ms after closing a TCP socket.");
	        this.sleep(SLEEP_MS);  // sleep a while after closing the TCP socket.
		} catch (IOException e) {
			throw new RuntimeException(
					this.getExceptionDetails(e, "IOException when closing the TCP socket."));
		}
	}


	public String getCostStr(final long MS_START) {
		 return "" + (System.currentTimeMillis() - MS_START) + "ms ";
	}
	
	public long getCostMillis(final long MS_START) {
		 return (System.currentTimeMillis() - MS_START) ;
	}


	public int mode(int value, int base) {
		return value % base;
	}
}

