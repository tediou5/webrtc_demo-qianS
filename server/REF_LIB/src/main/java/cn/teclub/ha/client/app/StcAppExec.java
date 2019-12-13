package cn.teclub.ha.client.app;


import cn.teclub.ha.client.StcExec;


public abstract class StcAppExec extends StcExec
{
    public StcAppExec(String evt_name, String evt_dscp) {
        super(evt_name, evt_dscp, StcAppComp.getInstance().appPulse);
    }

    public StcAppExec() {
        this(null, null);
    }


    @SuppressWarnings("unused")
	private StcAppComp appComp = StcAppComp.getInstance();


}
