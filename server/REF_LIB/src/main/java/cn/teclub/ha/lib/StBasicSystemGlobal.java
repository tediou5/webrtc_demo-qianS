package cn.teclub.ha.lib;

import cn.teclub.common.ChuyuObj;




/**
 * Update: [Theodore: 2014-11-13]
 * Abstract System Global Classs.
 * 
 * Globals are NOT necessary for a system to start up.  Globals describe the
 * runtime status of the system. In our applications, they are ususally used to
 * cache DB data on GW. SD may not use it at all!
 *
 * As globals are runtime data, usually performance-related. There is no need
 * to save such global values to external storage. 
 *
 * @author mancook
 */
public abstract class StBasicSystemGlobal extends ChuyuObj {

	protected StBasicSystemGlobal () {
	}
	
	public abstract void load();   		// load data from external storage into RAM
	public abstract void refresh();   	// load only the updated data on external stroage into RAM
}
