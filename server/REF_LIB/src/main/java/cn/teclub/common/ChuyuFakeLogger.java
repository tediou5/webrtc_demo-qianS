package cn.teclub.common;


public class ChuyuFakeLogger {
	private static ChuyuFakeLogger _instance = new ChuyuFakeLogger();
	public static ChuyuFakeLogger getInstance(){
		return _instance;
	}
	
	private ChuyuFakeLogger(){}
	public void debug(CharSequence e){}
	public void info (CharSequence e){}
	public void warn (CharSequence e){}
	public void error(CharSequence e){}
}
