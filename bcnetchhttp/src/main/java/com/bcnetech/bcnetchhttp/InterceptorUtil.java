package com.bcnetech.bcnetchhttp;

import android.util.Log;

import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.uuid.UUIDUtils;
import com.bcnetech.bcnetechlibrary.util.StringUtil;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yhf on 2018/9/11.
 */

public class InterceptorUtil {


    public static Interceptor HeadInterceptor() {

        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                String secret = "32=tMC;0;L}W]&8x?rfgYiQ%p^dOo>v_i5a[+=piT;8/S3=UvmFSN>ziUSt:0mt.>*zYhpmGxl6i*=;yT";
                String requestId = UUIDUtils.createId();
                String requestTime = String.valueOf(getGMTUnixTimeByCalendar()).substring(0, 10);//秒级别

                Request.Builder request = original
                        .newBuilder()
                        .addHeader("X-Request-Id", requestId)
                        .addHeader("X-Request-Time", requestTime)
                        //头通用参数
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/vnd.alloy+json; version=1")
                        .addHeader("X-Client-Type", "APP")
                        .addHeader("Accept-Language", "zh_cn");
                if(!StringUtil.isBlank(LoginedUser.getLoginedUser().getTokenid())){
                    if(original.url().toString().contains("/user/login")){
                        request.addHeader("Authorization", LoginedUser.getLoginedUser().getJwt());
                    }else {
                        request.addHeader("Authorization", "Token " + LoginedUser.getLoginedUser().getTokenid());
                    }
                }

                return chain.proceed(request.build());
            }
        };

    }

    private static String getSignature(String method, String requestId, String requestTime, String secret, String dataTxt) {
        StringBuilder sb = new StringBuilder();

        sb.append(method);
        sb.append("," + requestId);
        sb.append("," + requestTime);
        sb.append("," + secret);
        sb.append("," + dataTxt);
        Log.d("getSignature", sb.toString());
        String str = (SecurityUtil.encodeByMD5(sb.toString())).replaceAll("\n", "");
        Log.d("getSignature", str);
        return str;
    }

    /**
     * 获取当下时间戳
     */
    private static long getGMTUnixTimeByCalendar() {
        Calendar calendar = Calendar.getInstance();
        // 获取当前时区下日期时间对应的时间戳
        long unixTime = calendar.getTimeInMillis();
        // 获取标准格林尼治时间下日期时间对应的时间戳
        long unixTimeGMT = unixTime - TimeZone.getDefault().getRawOffset();
        return unixTimeGMT;
    }
}
