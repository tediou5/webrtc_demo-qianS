package cn.teclub.ha3.request;
import java.io.Serializable;

import cn.teclub.ha3.net.StClientID;
import cn.teclub.ha3.net.StClientType;
import cn.teclub.ha3.net.StJniNatType;
import cn.teclub.ha3.utils.StObject;

/**
 * super class for a client & friend
 *
 * @author Guilin Cao
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class StBasicClient  extends StObject implements Serializable, Cloneable {

    private StClientID  id;
    private String      name, label, dscp;
    private String      phone, avatar;
    private int		    flag;

    public StBasicClient(){ }

    /**
     * Constructor
     */
    public StBasicClient (final StClientType clt_type) {
        this.flag = Util.setFlag_ClientType(0, clt_type);
    }


    public StBasicClient(StBasicClient bc){
        this.id     = bc.id;
        this.name   = bc.name;
        this.label  = bc.label;
        this.dscp   = bc.dscp;
        this.flag   = bc.flag;
        this.phone  = bc.phone;
        this.avatar = bc.avatar;
    }


    public boolean isFlag_Online(){
        return Util.isFlag_Online(this.flag);
    }


    public StClientType getFlag_ClientType(){
        return Util.getFlag_ClientType(flag);
    }

    public String toString(){
        return name + "/" + label +
                "/" + (id == null ? "<NULL>" : id.getHex()) +
                "/" + Util.flagStrLine(flag);
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StClientID getId() {
        return id;
    }

    public void setId(StClientID id) {
        this.id = id;
    }

    public String getDscp() {
        return dscp;
    }

    public void setDscp(String dscp) {
        this.dscp = dscp;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public static class Util
    {
        public static int setFlag_ClientType(int flag, StClientType clt_type){
            flag &= ~0x07;
            flag |= (clt_type.ordinal() & 0x07);
            return flag;
        }
        public static StClientType getFlag_ClientType(int flag){
            int oridnal = (flag  & 0x07 );
            return StClientType.values()[oridnal];
        }

        public static boolean isFlag_Online(int flag){
            return (flag & 0x0008) == 0x0008;
        }
        public static int setFlag_Online(int flag, boolean b){
            if(b){
                flag |= 0x0008;
            }else{
                flag &= ~0x0008;
            }
            return flag;
        }

        public static int setFlag_NatType(int flag, StJniNatType nat_type){
            flag &= ~0xF0;
            flag |= ((nat_type.ordinal() <<4) & 0xF0 );
            return flag;
        }
        public static StJniNatType getFlag_NatType(int flag){
            int oridnal = ((flag >>4) & 0x0F );
            return StJniNatType.values()[oridnal];
        }


        public static boolean isFlag_CustomerService(int flag){
            return (flag & 0x0100) == 0x0100;
        }
        public static int setFlag_CustomerServvice(int flag, boolean b){
            if(b){
                flag |= 0x0100;
            }else{
                flag &= ~0x0100;
            }
            return flag;
        }


        public static String flagStrLine(int flag){
            @SuppressWarnings("StringBufferReplaceableByString")
            StringBuilder sbuf = new StringBuilder();
            sbuf.append((isFlag_Online(flag) ? " ONLINE" : "OFFLINE" ));
            sbuf.append(",").append(getFlag_ClientType(flag));
            sbuf.append(",").append(getFlag_NatType(flag));
            sbuf.append(",").append(isFlag_CustomerService(flag) ? "CusSrv" : "-");
            return sbuf.toString();
        }

    }//EOF Util


}
