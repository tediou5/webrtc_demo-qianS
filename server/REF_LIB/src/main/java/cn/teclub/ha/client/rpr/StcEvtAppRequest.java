package cn.teclub.ha.client.rpr;


import cn.teclub.ha.request.StNetPacket;


/**
 * <h1> App Request Event </h1>
 *
 * <p> Triggered by:  main-pulse
 * <p> Handled by: 	  app-pulse
 */
public class StcEvtAppRequest extends StcEvtRpr
{
    public final StNetPacket packet;

    public StcEvtAppRequest(StNetPacket pkt) {
        this.packet = pkt;
    }


    public String toString(){
        return super.toString() + " {PKT}" + packet;
    }


    public void dumpSetup() {
        super.dumpSetup();
        this.dumpAddLine(" ---- ---- ---- ----");
        this.dumpAddObj(packet);
    }
}
