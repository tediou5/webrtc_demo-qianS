package cn.teclub.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


/**
 * Ancestor of all Chuyu objects
 * 
 * @author mancook
 *
 */
public class ChuyuFamily 
{
	public String toString(){
		return this.toStringXml();
	}
	
	
	public String toStringXml(){
		return toStringXml(" ---- ");
	}
	
	
	/**
	 * <h2> Dump fields in this class in XML format. </h2>
	 * 
	 * NOTE: inherited filed is NOT dumped! Reason: 'getDeclaredFields' does NOT 
	 * return the inherited fields. 
	 * 
	 * @return an xml string
	 */
	public String toStringXml(String prefix){
		final StringBuffer sb = new StringBuffer(256);
		String clz_name = this.getClass().getName();
		Class<?> clz_type = this.getClass();
		Field[] listField = clz_type.getDeclaredFields();
		
		sb.append(prefix).append("<chuyu_obj TYPE=\"").append(clz_name).append("\"> \n");
		dumpSuperFields(sb, prefix + "  ");
		dumpFields(sb, listField, prefix);
		sb.append(prefix).append("</chuyu_obj> \n");
		
		return sb.toString();
	}
	
	
	private void dumpSuperFields(StringBuffer sbuf, String prefix){
		Class<?> clz_type = this.getClass().getSuperclass();
		if( clz_type.equals(ChuyuObj.class) || clz_type.equals(ChuyuFamily.class)  ){
			return;
		}
		
		Field[] listField = clz_type.getDeclaredFields();
		sbuf.append(prefix).append("<SUPER TYPE=\"").append(clz_type.getName()).append("\"> \n");
		dumpFields(sbuf, listField, prefix);
		sbuf.append(prefix).append("</SUPER> \n");
	}
	
	
	private void dumpFields(StringBuffer str_buf, Field[] listField, String prefix ){
		final StringBuilder sb_tmp = new StringBuilder(64);
		final int FIELD_NAME_WIDTH = 16;
		final String FILED_BLANK = "                "; // length is FIELD_NAME_WIDTH
		
		for(Field field: listField){
			sb_tmp.setLength(0);
			int m = field.getModifiers();
			
			////////////////////////////////////////////////////////////////////
			// [2015-3-10] 'static' field MUST be ignored! Otherwise, stack 
			// overflow occurs in following loop.
			// If using old loop, i.e. access filed by getXXX method,
			// stack overflow will not happen. 
			//
			// [2016-11-9] For safety, just dump primitive properties. 
			// And DO NOT loop dump ChuyuFamily members!
			////////////////////////////////////////////////////////////////////
			
			if ( (m & Modifier.STATIC) != 0 ){
				continue;
			}
			
			try {
				field.setAccessible(true); 
				Object value = field.get(this);

				if(value == null || value instanceof Number || value instanceof String || value instanceof Boolean ){
					str_buf.append(prefix).append("    ");
					
					String mod = (m&Modifier.FINAL)!=0 ? "[F]":"";
					sb_tmp.append(mod).append(field.getName());
					if(sb_tmp.length() < FIELD_NAME_WIDTH) {
						final int n  = FIELD_NAME_WIDTH - sb_tmp.length();
						sb_tmp.append(FILED_BLANK.substring(0, n));
					}
					sb_tmp.append(" = ").append(value);
					sb_tmp.append("\n");
					
					str_buf.append(sb_tmp.toString());
				}
				
				
				/* [2016-11-9] This may cause dead loop!
				 * 
				strbuf.append(prefix);
				strbuf.append("  " + ( (m&Modifier.FINAL)!=0 ? "[F]":"") + fieldName + " = ");
					
				if(value instanceof List){
					strbuf.append("\n");
					List value2 = (List)value;
					for (int i = 0; i < value2.size(); i++) {
						Object e = value2.get(i);
						if(e instanceof ChuyuFamily){
							ChuyuFamily vv = (ChuyuFamily) e;
							strbuf.append(vv.toStringXml(prefix + " \t"));
						}else{
							strbuf.append(e == null? "" : e.toString());
						}
					}
					strbuf.append(prefix);
					strbuf.append(" \n");
				}else if(value instanceof ChuyuFamily){
					ChuyuFamily vv = (ChuyuFamily) value;
					strbuf.append("\n");
					strbuf.append(vv.toStringXml(prefix + " \t"));
					strbuf.append(prefix);
					strbuf.append(" \n");
				}else{
					strbuf.append(value == null ? "" : value.toString());
					strbuf.append(" \n");
				}
				*/
			} catch (Exception e) {
				System.out.println("DEBUG: " + e.toString() + ", " +  e.getMessage() + ", " + e.getCause());
			}
		}//for
	}
	

	//	protected String getExpStackTrace(Exception e, String info){
	//		return ChuyuUtil.getInstance().getExceptionDetails(e, info);
	//	}
}
