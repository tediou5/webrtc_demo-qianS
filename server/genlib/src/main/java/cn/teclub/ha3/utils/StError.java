package cn.teclub.ha3.utils;



public class StError extends  RuntimeException {
    public StError(String msg){
        super(msg);
    }

    public StError(){
        super();
    }
}
