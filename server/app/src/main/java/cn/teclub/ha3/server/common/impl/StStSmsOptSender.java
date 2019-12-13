package cn.teclub.ha3.server.common.impl;


import cn.teclub.ha3.server.common.StISMSSender;
import cn.teclub.ha3.server.exceptions.StException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;


/**
 * <h1> Send a short message from java </h1>
 * 
 * Smoke Test of SMS Module.
 * 
 * TODO: [Theodore: 2018-01-13] add a new class for sms sending. 
 * 
 * @author mancook
 *
 */
public class StStSmsOptSender implements StISMSSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(StStSmsOptSender.class);
	static StStSmsConfig config  = StStSmsConfig.getInstance();

    @Override
    public void init() {
    }

    @Override
    public void sendSMS(String mobile, String sms) throws StException {
        String template_id = config.TEMPLATE_ID;

        // 填充参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("smsUser", config.SMS_USER);
        //params.put("templateId", config.TEMPLATE_ID);
        params.put("templateId",  template_id);

        params.put("msgType", "0");
        params.put("tos", "[{\"phone\":\""+ mobile +"\",\"vars\":{\"%rand_code%\": \""+ sms +"\"}}]");

        // 对参数进行排序
        Map<String, String> sortedMap = new TreeMap<String, String>(new Comparator<String>() {
            public int compare(String arg0, String arg1) {
                // 忽略大小写
                return arg0.compareToIgnoreCase(arg1);
            }
        });
        sortedMap.putAll(params);

        // 计算签名
        StringBuilder sb = new StringBuilder();
        sb.append(config.SMS_KEY).append("&");
        for (String s : sortedMap.keySet()) {
            sb.append(String.format("%s=%s&", s, sortedMap.get(s)));
        }
        sb.append(config.SMS_KEY);
        String sig = DigestUtils.md5Hex(sb.toString());

        // 将所有参数和签名添加到post请求参数数组里
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
            System.out.println("Result: " + EntityUtils.toString(entity));
            EntityUtils.consume(entity);
        } catch (IOException e) {
            LOGGER.error("Failed to send sms.", e);
        } finally {
            httpPost.releaseConnection();
        }
    }
}
