package cn.teclub.ha3.server.test;

public class StHelloMessage {

    private String name;

    public StHelloMessage() {
    }

    public StHelloMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String toString(){
        return "[StHelloMessage]" + name ;
    }
}


