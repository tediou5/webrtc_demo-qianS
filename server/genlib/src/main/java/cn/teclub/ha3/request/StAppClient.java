package cn.teclub.ha3.request;

import java.util.ArrayList;


/**
 * client bean used by mobile app
 *
 * @author Guilin Cao
 */
public class StAppClient extends StBasicClient
{
    private ArrayList<StAppFriend>  friends;


    @SuppressWarnings("unused")
    public StAppClient() {}


    public StAppClient(StBasicClient bc ) {
        super(bc);
    }


    public String toString(){
        return super.toString() +
                "," + (friends == null ? "0" : friends.size()) + " friends";
    }


    @SuppressWarnings("unused")
    public ArrayList<StAppFriend> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<StAppFriend> friends) {
        this.friends = friends;
    }
}
