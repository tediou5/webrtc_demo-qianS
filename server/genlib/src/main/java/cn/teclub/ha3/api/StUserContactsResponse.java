package cn.teclub.ha3.api;

import cn.teclub.ha3.request.StAppFriend;

import java.util.List;


/**
 * used by search & contact APIs.
 *
 * @author  Tao Zhang
 */
public class StUserContactsResponse {

    private List<StAppFriend> contacts;

    public void setContacts(List<StAppFriend> contacts) {
        this.contacts = contacts;
    }

    public List<StAppFriend> getContacts() {
        return contacts;
    }
}

