package cn.teclub.ha3.server.model;

import cn.teclub.ha3.net.StClientID;
import cn.teclub.ha3.net.StClientInfo;
import cn.teclub.ha3.net.StClientType;

import java.util.Date;
import java.util.List;

public class StClient extends StClientInfo {

    public StClient (final StClientType clt_type) {
        super(clt_type);
    }

    public StClient(){

    }
    private String passwd;

    private String localIp;

    private Integer localPort;

    private Date createTime;

    private Date lastLogin;

    private Date lastLogoff;

    private Integer onlineTime;

    private String avatar;

    private Date birthday;

    private List<StClientHas> list1 ,list2;

    public long getId() {
        return getClientID().getId();
    }

    public void setId(long id) {
        setClientID(new StClientID(id));
    }


    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd == null ? null : passwd.trim();
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp == null ? null : localIp.trim();
    }

    public Integer getLocalPort() {
        return localPort;
    }

    public void setLocalPort(Integer localPort) {
        this.localPort = localPort;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getLastLogoff() {
        return lastLogoff;
    }

    public void setLastLogoff(Date lastLogoff) {
        this.lastLogoff = lastLogoff;
    }

    public Integer getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Integer onlineTime) {
        this.onlineTime = onlineTime;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<StClientHas> getList1() {
        return list1;
    }

    public void setList1(List<StClientHas> list1) {
        this.list1 = list1;
    }

    public List<StClientHas> getList2() {
        return list2;
    }

    public void setList2(List<StClientHas> list2) {
        this.list2 = list2;
    }
}