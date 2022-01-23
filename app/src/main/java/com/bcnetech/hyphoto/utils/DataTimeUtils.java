package com.bcnetech.hyphoto.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataTimeUtils {
    //字符串转时间戳
    public static String getTime(String timeString){
        String timeStamp = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        Date d;
        try{
            d = sdf.parse(timeString);
            long l = d.getTime();
            timeStamp = String.valueOf(l);
        } catch(ParseException e){
            e.printStackTrace();
        }
        return timeStamp;
    }

    //时间戳转字符串
    public static String getStrTime(String timeStamp){
        String timeString = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        long  l = Long.valueOf(timeStamp);
        timeString = sdf.format(new Date(l));//单位秒
        return timeString;
    }
}