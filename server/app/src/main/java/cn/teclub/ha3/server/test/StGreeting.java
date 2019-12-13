package cn.teclub.ha3.server.test;

public class StGreeting {

    private final long id;
    private final String content;

    public StGreeting(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
