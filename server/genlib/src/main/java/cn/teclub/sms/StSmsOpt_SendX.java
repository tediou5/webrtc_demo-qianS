package cn.teclub.sms;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;


/**
 * [Theodor: 2019/6/10]
 * TODO_in_future: update sending code based on new example.
 *
 * This is a very old implementation.
 * New example is much simpler. see: https://soundcloud.com/
 *
 */
@SuppressWarnings("WeakerAccess")
public class StSmsOpt_SendX {
    @SuppressWarnings("unused")
    public enum SmsType{
        None, Signup, ResetPasswd
    }



    static StSmsConfig config  = StSmsConfig.getInstance();

    public static void main(String[] args) throws IOException {
        if(args.length != 2) {
            System.out.println("Usage: <CMD> <PHONE_NUMBER> <RANDOM_CODE> \n");
            System.out.println("Example: <CMD> 18964895454  FF008964");
            System.exit(-1);
        }
        new StSmsOpt_SendX(args[0], args[1]);
    }


    ////////////////////////////////////////////////////////////////////////////

    public StSmsOpt_SendX(final String dst_phone, final String rand_code)
            throws IOException
    {
        this(dst_phone, rand_code, SmsType.Signup);
    }


    public StSmsOpt_SendX(final String dst_phone, final String rand_code, final SmsType t)
            throws IOException
    {
        final String template_id;
        switch(t){
            case Signup:
                // TODO: rename to 'TEMPLATE_ID_SIGNUP'
                template_id = config.TEMPLATE_ID;
                break;

            case ResetPasswd:
                // TODO: add a new template: TEMPLATE_ID_RESET_PASSWD
                // temple_id = config.TEMPLATE_ID_RESET_PASSWD;
                template_id = config.TEMPLATE_ID;
                break;

            default:
                throw new RuntimeException("[cook] unsupported Sms Type!");
        }


        System.out.println("Send testing SMS to " + dst_phone);
        // 填充参数
        Map<String, String> params = new HashMap<>();
        params.put("smsUser", config.SMS_USER);
        //params.put("templateId", config.TEMPLATE_ID);
        params.put("templateId",  template_id);

        params.put("msgType", "0");
        params.put("tos", "[{\"phone\":\""+ dst_phone +"\",\"vars\":{\"%rand_code%\": \""+ rand_code +"\"}}]");
        // params.put("tos", "[{\"phone\":\"13818218694\",\"vars\":{\"%rand_code%\": \"66778002\"}}]");

        // 对参数进行排序
        // 忽略大小写
        Map<String, String> sortedMap = new TreeMap<>(String::compareToIgnoreCase);
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
        List<NameValuePair> postparams = new ArrayList<>();
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
        } finally {
            httpPost.releaseConnection();
        }
    }



}
