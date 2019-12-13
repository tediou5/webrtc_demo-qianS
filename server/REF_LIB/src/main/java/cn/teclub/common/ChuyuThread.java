package cn.teclub.common;

import org.apache.log4j.Logger;


public class ChuyuThread extends Thread
{
	protected Logger stLog = Logger.getLogger(this.getClass());
	protected ChuyuUtil util = ChuyuUtil.getInstance();
}
