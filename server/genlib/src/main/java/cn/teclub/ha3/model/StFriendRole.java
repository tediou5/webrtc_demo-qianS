package cn.teclub.ha3.model;

/**
 * Role of a friend;
 * type of client_b in tb_client_has.
 *
 * @author Guilin Cao
 */
public enum StFriendRole {
    NONE, MASTER, SLAVE;

    public static StFriendRole getRole(int index){
        return StFriendRole.values()[index];
    }
}
