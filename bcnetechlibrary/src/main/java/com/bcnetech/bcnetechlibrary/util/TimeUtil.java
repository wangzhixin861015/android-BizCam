package com.bcnetech.bcnetechlibrary.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wenbin on 16/10/13.
 * java中生成的时间戳精确到毫秒级别，而unix中精确到秒级别
 */

public class TimeUtil {
    //unix
    private static SimpleDateFormat sLoggingFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatForTime(long time) {
        if (time <= 0) {
            return "";
        }
        String times = time * 1000 + "";
        times = times.substring(0, 13);
        LogUtil.d("" + times);
        time = Long.valueOf(times);
        return sLoggingFormat.format(new Date(time));

    }

    /**
     * 获取当前时间戳
     *
     * @return
     */
    public static Long getBeiJingTimeGMT() {
        return System.currentTimeMillis();
    }

    public static boolean isSameDay(long date1 ,long date2){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(new Date(date1)).equals(fmt.format(new Date(date2)));
    }

    /**
     * 格式化
     *
     * @param millis
     * @return
     */
    public static String formatBeiJingTime(Long millis) {
        Date date = new Date(millis);
        return sLoggingFormat.format(date);
    }

    public static String getYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String year = sdf.format(new Date());
        return year;
    }

    public static String getWeek(String sdate) {
        // 再转换为时间
        Date date = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return new SimpleDateFormat("EEEE").format(c.getTime());
    }

    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }


}
