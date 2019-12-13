package cn.teclub.ha.lib;

import cn.teclub.common.ChuyuObj;

public interface StEventListener {
	String getEvtLisName();
	void handleEvent(StEvent event);
}


abstract class StEventHBLis extends ChuyuObj implements StEventListener { }
