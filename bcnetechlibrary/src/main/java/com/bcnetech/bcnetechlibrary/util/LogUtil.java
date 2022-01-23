package com.bcnetech.bcnetechlibrary.util;

import android.util.Log;


/**
 * Created by wb on 2016/5/3.
 */
public class LogUtil {

    /**
     * ERROR级别日志
     *
     * @param e
     */
    public static void error(Throwable e) {
        Log.e(e.getMessage(), e.toString());
    }



    /**
     * DEBUG级别日志
     *
     * @param content
     */
    public static void debug(String content) {
        if (StringUtil.isBlank(content)) {
            return;
        }
    }

    public static String TAG = "eBizTop";
    public static boolean allowD =false;
    public static boolean allowE = false;
    public static boolean allowI = false;
    public static boolean allowV = false;
    public static boolean allowW = false;
    public static boolean allowWtf = false;


    public static void d(String var0) {
        //Log.d(TAG, var0);
        if(allowD && !StringUtil.isBlank(var0)) {
            //StackTraceElement var1 = getCallerMethodName();
            //String var2 = generateTag(var1);
            Log.d(TAG, var0);
        }
    }

    public static void d(String var0, Throwable var1) {
        if(allowD && !StringUtil.isBlank(var0)) {
            StackTraceElement var2 = getCallerMethodName();
            String var3 = generateTag(var2);
            Log.d(var3, var0, var1);
        }
    }

    public static void e(String var0) {
        if(allowE && !StringUtil.isBlank(var0)) {
            StackTraceElement var1 = getCallerMethodName();
            String var2 = generateTag(var1);
            Log.e(var2, var0);
        }
    }

    public static void e(String var0, Throwable var1) {
        if(allowE && !StringUtil.isBlank(var0)) {
            StackTraceElement var2 = getCallerMethodName();
            String var3 = generateTag(var2);
            Log.e(var3, var0, var1);
        }
    }

    public static void i(String var0) {
        if(allowI && !StringUtil.isBlank(var0)) {
            StackTraceElement var1 = getCallerMethodName();
            String var2 = generateTag(var1);
            Log.i(var2, var0);
        }
    }

    public static void i(String var0, Throwable var1) {
        if(allowI && !StringUtil.isBlank(var0)) {
            StackTraceElement var2 = getCallerMethodName();
            String var3 = generateTag(var2);
            Log.i(var3, var0, var1);
        }
    }

    public static void v(String var0) {
        if(allowV && !StringUtil.isBlank(var0)) {
            StackTraceElement var1 = getCallerMethodName();
            String var2 = generateTag(var1);
            Log.v(var2, var0);
        }
    }

    public static void v(String var0, Throwable var1) {
        if(allowV && !StringUtil.isBlank(var0)) {
            StackTraceElement var2 = getCallerMethodName();
            String var3 = generateTag(var2);
            Log.v(var3, var0, var1);
        }
    }

    public static void w(String var0) {
        if(allowW && !StringUtil.isBlank(var0)) {
            StackTraceElement var1 = getCallerMethodName();
            String var2 = generateTag(var1);
            Log.w(var2, var0);
        }
    }

    public static void w(String var0, Throwable var1) {
        if(allowW && !StringUtil.isBlank(var0)) {
            StackTraceElement var2 = getCallerMethodName();
            String var3 = generateTag(var2);
            Log.w(var3, var0, var1);
        }
    }

    public static void w(Throwable var0) {
        if(allowW) {
            StackTraceElement var1 = getCallerMethodName();
            String var2 = generateTag(var1);
            Log.w(var2, var0);
        }
    }

    public static void wtf(String var0) {
        if(allowWtf && !StringUtil.isBlank(var0)) {
            StackTraceElement var1 = getCallerMethodName();
            String var2 = generateTag(var1);
            Log.wtf(var2, var0);
        }
    }

    public static void wtf(String var0, Throwable var1) {
        if(allowWtf && !StringUtil.isBlank(var0)) {
            StackTraceElement var2 = getCallerMethodName();
            String var3 = generateTag(var2);
            Log.wtf(var3, var0, var1);
        }
    }

    public static void wtf(Throwable var0) {
        if(allowWtf) {
            StackTraceElement var1 = getCallerMethodName();
            String var2 = generateTag(var1);
            Log.wtf(var2, var0);
        }
    }

    private static StackTraceElement getCallerMethodName() {
        StackTraceElement[] var0 = Thread.currentThread().getStackTrace();
        return var0[4];
    }

    private static String generateTag(StackTraceElement var0) {
        return TAG;
    }
}
