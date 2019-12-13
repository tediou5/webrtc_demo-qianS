package cn.teclub.ha.client.app;


import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.lib.StEventPulse;


public class StcPulseLbr extends StEventPulse 
{
	public static final int HB_INIT_MS 		= 10*1000;
	public static final int HB_PERIOD_MS 	= StConst.CLT_LBR_PULSE_PERIOD_MS;
	StcPulseLbr() {
		super("Lbr-Pulse", HB_INIT_MS, HB_PERIOD_MS);
	}
}

