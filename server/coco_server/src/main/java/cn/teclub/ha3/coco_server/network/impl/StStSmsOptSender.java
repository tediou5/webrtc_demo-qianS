package cn.teclub.ha3.coco_server.network.impl;


import cn.teclub.ha3.coco_server.controller.exception.StException;
import cn.teclub.ha3.coco_server.network.StISMSSender;
import cn.teclub.ha3.coco_server.network.StServicesProvider;
import cn.teclub.ha3.coco_server.sys.StApplicationProperties;
import cn.teclub.ha3.utils.StObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;


/**
 * <h1> Send a short message from java </h1>
 * 
 * Smoke Test of SMS Module.
 * 
 * @author Tao Zhang
 *
 */
public class StStSmsOptSender extends StObject implements StISMSSender {


    private static StStSmsOptSender instance;

	public StStSmsOptSender(StApplicationProperties properties){
        config  = StStSmsConfig.getInstance(properties);
    }

    private static StStSmsConfig config;
    public static StStSmsOptSender getInstance(StApplicationProperties properties){
        if(instance == null){
            instance = new StStSmsOptSender(properties);
        }

        return instance;
    }

    @Override
    public void init() {
    }

    @Override
    public void sendSMS(String mobile, String sms) throws StException {
        String template_id = config.TEMPLATE_ID;

        // fill parameter
        Map<String, String> params = new HashMap<String, String>();
        params.put("smsUser", config.SMS_USER);
        //params.put("templateId", config.TEMPLATE_ID);
        params.put("templateId",  template_id);

        params.put("msgType", "0");
        params.put("tos", "[{\"phone\":\""+ mobile +"\",\"vars\":{\"%rand_code%\": \""+ sms +"\"}}]");

        // parameter sort
        Map<String, String> sortedMap = new TreeMap<String, String>(new Comparator<String>() {
            public int compare(String arg0, String arg1) {
                return arg0.compareToIgnoreCase(arg1);
            }
        });
        sortedMap.putAll(params);

        // calculation signature
        StringBuilder sb = new StringBuilder();
        sb.append(config.SMS_KEY).append("&");
        for (String s : sortedMap.keySet()) {
            sb.append(String.format("%s=%s&", s, sortedMap.get(s)));
        }
        sb.append(config.SMS_KEY);
        String sig = DigestUtils.md5Hex(sb.toString());

        // add all parameters and signatures to the post request parameter array
        List<NameValuePair> postparams = new ArrayList<NameValuePair>();
        for (String s : sortedMap.keySet()) {
            postparams.add(new BasicNameValuePair(s, sortedMap.get(s)));
        }

        postparams.add(new BasicNameValuePair("signature", sig));
        HttpPost httpPost = new HttpPost(config.SMS_URL_SENDX);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(postparams, "utf8"));
            CloseableHttpClient httpClient;
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000).setSocketTimeout(100000).build();
            httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            log.debug("Result: " + EntityUtils.toString(entity));
            EntityUtils.consume(entity);
        } catch (IOException e) {
            log.error("Failed to send sms.", e);
        } finally {
            httpPost.releaseConnection();
        }
    }
}
