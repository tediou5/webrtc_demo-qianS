package cn.teclub.ha3.request;

import java.io.Serializable;
import cn.teclub.ha3.model.StFriendRole;

/**
 * friend of a client.
 *
 * @author  Guilin Cao
 */
public class StAppFriend extends StBasicClient implements Serializable, Cloneable {

    // TODO; get role from tb_client_has:type
    private StFriendRole role;

    /**
     * @deprecated  missing friend rold
     */
    public StAppFriend(StBasicClient bc ) {
        this(bc, StFriendRole.NONE);
    }

    public StAppFriend(StBasicClient bc, StFriendRole role) {
        super(bc);
        this.role = role;
    }


    public String toString(){
        return super.getLabel();
    }


    @SuppressWarnings("unused")
    public String dumpSimple(){
        return super.toString();
    }


    public StFriendRole getRole() {
        return role;
    }

    public void setRole(StFriendRole role) {
        this.role = role;
    }
}


