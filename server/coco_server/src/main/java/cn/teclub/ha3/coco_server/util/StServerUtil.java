package cn.teclub.ha3.coco_server.util;

import cn.teclub.ha3.coco_server.controller.StRequestTimeModel;
import cn.teclub.ha3.utils.StObject;
import org.springframework.stereotype.Component;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;


/**
 * @author zhangtao
 */
@Component
public class StServerUtil extends StObject {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    /*@date 2018/12/25*/
    public String randomLetter(int num) {
        String str = "abcdefghijklmnopqrstuvwxyz";
        Random rd = new Random();
        int m = 0;
        String n = "";
        for (int i = 0; i < num; i++) {
            m = rd.nextInt(26);
            n = n + str.charAt(m);
        }
        log.debug(n);
        return n;
    }

    /*@date 2018/12/25*/
    public String randomNumber(int num) {
        Random rd = new Random();
        int m = 0;
        String n = "";
        for (int i = 0; i < num; i++) {
            m = rd.nextInt(10);
            n = n + m;
        }
        log.debug(n);
        return n;
    }

    public String formatTimestamp(Long ft) {
        return simpleDateFormat.format(ft);
    }

    public String formatDate(Date date){
        return  simpleDateFormat.format(date);
    }

    public Date parseString(String str){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date date = format.parse(str, pos);
        return date;
    }
    public boolean isMobile(String mobile) {
        if (mobile == null || mobile.isEmpty()) {
            return false;
        }
        return Pattern.compile("^[1][0-9]{10}$").matcher(mobile).matches();
    }

    public String random(final int min, final int max) {
        Random rand = new Random();
        int tmp = Math.abs(rand.nextInt());
        return String.valueOf(tmp % (max - min + 1) + min);
    }


    /**
     * YYYYMMDDnn
     *
     * @param num
     */
    public String getVersionCode(int num) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String format = simpleDateFormat.format(new Date());
        return format + "-" + Integer.toString(num);
    }
}


