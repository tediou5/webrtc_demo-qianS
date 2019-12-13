package cn.teclub.ha3.coco_server.sys;

public class StSrvGlobal
{

    /**
     * [Theodor: 2019/5/10] manually keep VERSION_NAME same to "version" in app/build.gradle;
     * todo_in_future: get version defined in module's or project's build.gradle;
     */
    private static final int		VERSION_CODE = 50000;
    private static final String 	VERSION_NAME = "v0.5_SNAPSHOT";
    public  static final String  	VERSION_INFO = VERSION_NAME + ",n" + VERSION_CODE;

    private static StSrvGlobal   _ins = new StSrvGlobal();

    public static StSrvGlobal instance(){
        return _ins;
    }

}
