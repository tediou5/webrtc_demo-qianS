package cn.teclub.ha3.request;



public class StLoginRes extends StUserResponse
{
    private String      token;
    private StAppClient client;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public StAppClient getClient() {
        return client;
    }

    public void setClient(StAppClient client) {
        this.client = client;
    }

}
