package cn.teclub.ha3.coco_server.sys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Properties for configuration.
 * @author Tao Zhang
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

    @Value("${rtc.authcode.smssender}")
    private String authCodeSmssender;

/*not use    @Value("${feisuo.authcode.key}")
    private String authCodeKey;*/

    @Value("${rtc.authcode.expired}")
    private String authCodeExpired;

    @Value("${rtc.authcode.interval}")
    private String authCodeInterval;

    @Value("${rtc.token.expired}")
    private String tokenExpired;

    @Value("${rtc.file.uploader}")
    private String uploader;

    @Value("${spring.profiles.active}")
    private String active;

    @Value("${aws-bucket-name}")
    private  String awsBucketName;

    @Value("${aws-clientRegion}")
    private  String awsClientRegion;

    @Value("${rtc.s3configPath}")
    private  String s3configPath;

    @Value("${rtc.authCode.sms_url_sendx}")
    private  String authCodeSmsUrlSendx;

    @Value("${rtc.authCode.sms_user}")
    private  String authCodeSmsUser;

    @Value("${rtc.authCode.sms_key}")
    private  String authCodeSmsKey;

    @Value("${rtc.authCode.template_id}")
    private  String authCodeTemplateId;


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

    public String getActive() {
        return active;
    }

    public String getAwsBucketName() {
        return awsBucketName;
    }

    public String getAwsClientRegion() {
        return awsClientRegion;
    }

    public String getS3configPath() {
        return s3configPath;
    }

    public String getAuthCodeSmsUrlSendx() {
        return authCodeSmsUrlSendx;
    }

    public String getAuthCodeSmsUser() {
        return authCodeSmsUser;
    }

    public String getAuthCodeSmsKey() {
        return authCodeSmsKey;
    }

    public String getAuthCodeTemplateId() {
        return authCodeTemplateId;
    }
}
