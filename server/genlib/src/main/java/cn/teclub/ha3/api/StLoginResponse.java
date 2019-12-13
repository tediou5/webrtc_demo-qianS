package cn.teclub.ha3.api;

import cn.teclub.ha3.request.StAppClient;

/**
 * StLoginResponse
 * @author Tao Zhang
 */
public class StLoginResponse {


    private StAppClient client;

    private String token;

    public void setClient(StAppClient client) {
        this.client = client;
    }

    public StAppClient getClient() {
        return client;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}