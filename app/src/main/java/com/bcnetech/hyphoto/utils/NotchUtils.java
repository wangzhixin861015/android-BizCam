package com.bcnetech.hyphoto.utils;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * 用于获取刘海屏信息
 * Created by a1234 on 2018/8/23.
 */

public class NotchUtils {
    /*----------------------------华为手机------------------------------*/

    /**
     * 华为手机判断是否为刘海屏
     *
     * @param context
     * @return
     */
    public static boolean hasNotchInScreenHW(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("test", "hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("test", "hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            Log.e("test", "hasNotchInScreen Exception");
        } finally {
            return ret;
        }
    }

    /**
     * 华为手机获取刘海屏高度
     *
     * @param context
     * @return
     */
    public static int[] getNotchSizeHW(Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("test", "getNotchSize ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("test", "getNotchSize NoSuchMethodException");
        } catch (Exception e) {
            Log.e("test", "getNotchSize Exception");
        } finally {
            return ret;
        }
    }
     /*----------------------------OPPO手机------------------------------*/

    /**
     * OPPO判断是否为刘海屏
     *
     * @param context
     * @return
     */
    public static boolean hasNotchInScreenOPPO(Context context) {
        try {
            return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        } catch (Exception e) {
            Log.e("test", "hasNotchInScreen Exception");
        } finally {
            return false;
        }
    }
      /*----------------------------VIVO手机------------------------------*/

    /**
     * VIVO手机判断是否为刘海屏
     *
     * @param context
     * @return
     */
    public static final int NOTCH_IN_SCREEN_VOIO = 0x00000020;//是否有凹槽
    public static final int ROUNDED_IN_SCREEN_VOIO = 0x00000008;//是否有圆角

    public static boolean hasNotchInScreenVIVO(Context context, int type) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class mclass = cl.loadClass("android.util.FtFeature");
            Method get = mclass.getMethod("isFeatureSupport", int.class);
            ret = (boolean) get.invoke(mclass, type);
        } catch (ClassNotFoundException e) {
            Log.e("test", "hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("test", "hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            Log.e("test", "hasNotchInScreen Exception");
        } finally {
            return ret;
        }
    }
}
