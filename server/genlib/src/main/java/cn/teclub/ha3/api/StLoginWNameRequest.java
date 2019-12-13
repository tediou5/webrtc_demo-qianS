package cn.teclub.ha3.api;

/**
 *
 * StLoginWNameRequest
 * @author Tao Zhang
 */
public class StLoginWNameRequest extends StRequestBody {


    private String name;


    private String passwd;

    /**
     * type：
     *     0  phone
     *     1  name
     *     2  MAC address
     */
    private StLoginType type;


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public void setType(StLoginType type) {
        this.type = type;
    }

    public StLoginType getType() {
        return type;
    }

    public enum StLoginType{
        PHONE, NAME, MACADDR;

        public static StLoginType getLoginType(int index){
            return StLoginType.values()[index];
        }
    }
}