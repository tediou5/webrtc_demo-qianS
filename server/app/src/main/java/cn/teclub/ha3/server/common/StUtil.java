package cn.teclub.ha3.server.common;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;


/**
 * todo: move all utility method and class into this one!
 */
@Component
public class StUtil {
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
        return n;
    }

    public String formatTimestamp(Long ft) {
        return simpleDateFormat.format(ft);
    }

    public String formatDate(Date date){
        return  simpleDateFormat.format(date);
    }

    public Date parseString(String str){
        Date date = null;
        try {
            date = simpleDateFormat.parse(str);

        } catch (ParseException e) {
            e.printStackTrace();
        }
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
     * 统一double的精度
     * @param dSrc
     * @return
     */
    public double trans(double dSrc){
        BigDecimal bgLat = new BigDecimal(dSrc);
        double tranLat = bgLat.setScale(9, BigDecimal.ROUND_HALF_UP).doubleValue();
        return tranLat;
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


