package cn.teclub.ha3.request;

import cn.teclub.ha3.utils.StObject;


//@SuppressWarnings("ALL")
@SuppressWarnings("unused")
public class StWsMessage extends StObject
{
    public static final long ID_INVALID = -1;
    public static final long ID_SERVER  = 0;

    public enum Command {
        NULL(0x00),
        APPLY_ADD_DEVICE(0x30),
        GRANT_ADD_DEVICE(0x31),
        ECHO(0x40),
        UPDATE_FRIEND(0x41),
        MAKE_CALL(0x42),
        ACCEPT_CALL(0x43),
        HANDUP_CALL(0x44),
        PING(0x45),
        GET_CALL_INFO(0x46),
        GET_DEV_INFO(0x47),
        RECORD_CAPTURE(0X48),
        REQ_MAX(0x50),

        CALL_OFFER(0x51),
        CALL_ANSWER(0x52),
        CALL_CANDIDATE(0x53),
        CALL_BYE(0x54),
        CALL_CANDIDATE_RM(0x55),
        SEND_DATA(0x60),



        @Deprecated
        VIDEO_CALL(0x0A01),
        MAX(0xFFFF);

        private short index;

        Command(int idx) {
            this.index = (short) idx;
        }

        public short getIndex() {
            return index;
        }
    }

    // [Theodor: 2019-04-10] currently not used
    public enum Type{
        None, Request, Allow, Deny
    }

    private short   cmd;
    private Type    type;
    private long    ssid;

    private long    src, dst;
    private int   len;
    private String  info;
    private String  opt; // for debug & test


    /*
    public static StWsMessage buildRequest( final Command cmd, final String info, final long from) {
        // todo: check cmd is valid
        return new StWsMessage(cmd, Type.Request, info, from, ID_SERVER);
    }


    public static StWsMessage buildRequest( final Command cmd, final String info, final long from, final long to) {
        return new StWsMessage(cmd, Type.Request, info, from, to );
    }*/


    public StWsMessage buildResponse(final Type t, final String info){
        return new StWsMessage(this.ssid, getCommand(), t, info, dst, src );
    }


    // -----------------------------------------------------------------------------
    // constructors
    // -----------------------------------------------------------------------------

    /**
     * default constructor: required by json
     */
    public StWsMessage(){
    }


    /**
     * constructor: called by application
     */
    public StWsMessage(final long ssid, final Command cmd, final Type t, final String info, final long from, final long to) {
        this.ssid = ssid;
        this.cmd = cmd.getIndex();
        this.type = t;
        this.src = from;
        this.dst = to;
        if(info == null) {
            this.info = "";

        }else{
            this.len = info.length();
            this.info = info;
        }
        this.opt = "";
        this.opt += "TODO";
    }


    public StWsMessage(final long ssid, final String info, final long from, final long to) {
        this(ssid, Command.NULL, Type.None, info, from, to);
    }


    public StWsMessage(final long ssid, final Command cmd, final Type t, final String info){
        this(ssid, cmd, t, info, 0x00, 0x01);
    }



    // -----------------------------------------------------------------------------
    // methods & properties
    // -----------------------------------------------------------------------------

    // for debug & test
    public void addNote(String note){
        this.opt += note;
    }


    public String toString(){
        return "[" +  getClass().getSimpleName() +  "]" + getCommand() + "("+
                "0x" + util.to4CharHex(cmd) + ")," + type + "," +
                ",ssid:" + util.to16CharHex(ssid) +
                "," + util.to16CharHex(src) + "->" + util.to16CharHex(dst) +
                "," + len + "B," + info + "[opt]" + opt;

    }


    public long getSsid() {
        return ssid;
    }

    public void setSsid(long ssid) {
        this.ssid = ssid;
    }


    public short getCmd() {
        return cmd;
    }


    public Command getCommand(){
        Command[] list = Command.values();
        for(Command c : list) {
            if(c.getIndex() == this.cmd) {
                return c;
            }
        }
        return Command.NULL;
    }


    public void setCmd(short cmd) {
        this.cmd = cmd;
    }

        public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public long getSrc() {
        return src;
    }

    public void setSrc(long src) {
        this.src = src;
    }

    public long getDst() {
        return dst;
    }

    public void setDst(long dst) {
        this.dst = dst;
    }


    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }
}
