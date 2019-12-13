package cn.teclub.ha.lib;

import cn.teclub.common.ChuyuFamily;




/**
 * Update: [Theodore: 2014-11-13]
 *
 * The Abstract System Configuration Class.
 *
 * Config class is used to store system parameters, which are loaded at
 * start-up.  These parameters are usually digits or strings. No much memory is
 * used.  System CAN NOT start up properly, if the config parameters are not
 * set correctly or missing. 
 *
 * In near future, user can change the config parameters in APP GUI and save it
 * to external storage. Saved parameters take effective at next boot.
 * 
 * 
 * [2014-12-5] DO NOT use these default values: 
 * -- stunServerIP/stunServerPort;
 * -- csServerIP/csServerPort;
 * 
 * @author mancook
 *
 */
public abstract class StBasicSystemConfig extends ChuyuFamily {
	public abstract void load();
	public abstract void save(); // save data in RAM into external storage

}
