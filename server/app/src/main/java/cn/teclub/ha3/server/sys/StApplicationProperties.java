package cn.teclub.ha3.server.sys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Properties for configuration.
 */
@Configuration
@PropertySource("classpath:application.properties")
public class StApplicationProperties {
    private static StApplicationProperties _ins = null;

    public static StApplicationProperties instance() {
        return _ins;
    }
    public StApplicationProperties(){
        if(_ins != null){
            throw new RuntimeException("do not construct >1 config object!");
        }
        _ins = this;
    }

    @Value("${feisuo.authcode.smssender}")
    private String authCodeSmssender;

/*not use    @Value("${feisuo.authcode.key}")
    private String authCodeKey;*/

    @Value("${feisuo.authcode.expired}")
    private String authCodeExpired;

    @Value("${feisuo.authcode.interval}")
    private String authCodeInterval;

    @Value("${feisuo.token.expired}")
    private String tokenExpired;
    @Value("${feisuo.file.uploader}")
    private String uploader;


    public String getAuthCodeExpired() {
        return authCodeExpired;
    }

    public String getAuthCodeInterval() {
        return authCodeInterval;
    }


    public String getAuthCodeSmssender() {
        return authCodeSmssender;
    }


    public String getTokenExpired() {
        return tokenExpired;
    }

    public String getUploader() {
        return uploader;
    }


}
