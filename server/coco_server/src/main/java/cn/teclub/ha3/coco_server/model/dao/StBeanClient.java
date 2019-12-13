package cn.teclub.ha3.coco_server.model.dao;

import cn.teclub.ha3.net.StClientID;
import cn.teclub.ha3.net.StClientType;
import cn.teclub.ha3.request.StBasicClient;

import java.util.Date;
import java.util.List;

/**
 * client model: mapped to a record in DB table tb_client
 *
 * [Theodor: 2019/11/27] NOTE: this class should NOT be accessed by controller.
 * It is mapped to a DB record, some fields should not be updated by controller or even server.
 * e.g. createTime. It is easier to make access control in model than in controller.
 *
 * @author Tao Zhang
 */
public class StBeanClient extends StBasicClient {

    public StBeanClient(final StClientType clt_type) {
        super(clt_type);
    }

    public StBeanClient(){

    }

    private String passwd;

    private String localIp;

    private Integer localPort;

    private Date createTime;

    private Date lastLogin;

    private Date lastLogoff;

    private Integer onlineTime;

    private Date birthday;

    private long   iconTS;

    private String  macAddr;

    private String  publicIP;

    private int publicPort;

    /**
     * [Theodor: 2019/11/27] TODO: delete friend list
     */
    private List<StBeanClientHas> list1 ,list2;

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

    public List<StBeanClientHas> getList1() {
        return list1;
    }

    public void setList1(List<StBeanClientHas> list1) {
        this.list1 = list1;
    }

    public List<StBeanClientHas> getList2() {
        return list2;
    }

    public void setList2(List<StBeanClientHas> list2) {
        this.list2 = list2;
    }

    public void setIconTS(long iconTS) {
        this.iconTS = iconTS;
    }

    public long getIconTS() {
        return iconTS;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setPublicIP(String publicIP) {
        this.publicIP = publicIP;
    }

    public String getPublicIP() {
        return publicIP;
    }

    public void setClientId(long id){
        setId(new StClientID(id));
    }

    public long getClientId(){
        return  getId().getId();
    }

    public void setPublicPort(int publicPort) {
        this.publicPort = publicPort;
    }

    public int getPublicPort() {
        return publicPort;
    }
}