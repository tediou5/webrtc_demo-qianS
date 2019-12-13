package cn.teclub.ha.client.rpr;


import cn.teclub.ha.lib.StEvent;


/**
 * User of rpr (Android app-obj) implements this interface.
 */
public interface StBridgeToUser
{
    /**
     * Send message to GUI activity/fragment
     */
    //void sendMessageToGui(int what, int arg1, int arg2, Object obj);


    /**
     * add a new event to app-pulse
     */
    void addNewEvent(StEvent event);
}
