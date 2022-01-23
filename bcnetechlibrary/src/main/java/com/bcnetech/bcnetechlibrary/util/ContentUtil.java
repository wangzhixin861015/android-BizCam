package com.bcnetech.bcnetechlibrary.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.util.List;

/**
 * Created by wenbin on 16/5/5.
 */
public class ContentUtil {
    private static int width = 0;
    private static int height = 0;
    private static float density = 0;
    private static int statusHeight = 0;

    private static int realyHeight = 0;


    public static int getScreenWidth(Context context) {
        if (width == 0) {
            WindowManager manager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            width = display.getWidth();
        }
        return width;
    }

    /**
     * 减去状态栏
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        if (height == 0) {
            WindowManager manager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            height = display.getHeight();
        }
        return height + getNavigationBarHeight(context) - getStatusBarHeight(context);
    }


    /**
     * 全长：不包含虚拟按键he
     *
     * @param context
     * @return
     */
    public static int getScreenHeight2(Context context) {
        if (height == 0) {
            WindowManager manager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            height = display.getHeight();
        }
        return height;
    }


    /**
     * 实际显示区域指定包含系统装饰的内容的显示部分
     *
     * @param context
     * @return
     */
    public static int getScreenHeight3(Context context) {

        if(realyHeight==0){
            DisplayMetrics outMetrics = new DisplayMetrics();
            WindowManager manager =(WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);

            manager.getDefaultDisplay().getRealMetrics(outMetrics);
            int heightPixel = outMetrics.heightPixels;
            realyHeight=heightPixel;
        }



        return realyHeight;
    }

    public static float getScreenDensity(Context context) {
        if (density == 0) {
            try {
                DisplayMetrics dm = new DisplayMetrics();
                WindowManager manager = (WindowManager) context
                        .getSystemService(Context.WINDOW_SERVICE);
                manager.getDefaultDisplay().getMetrics(dm);
                density = dm.density;
            } catch (Exception ex) {
                ex.printStackTrace();
                density = 1.0f;
            }
        }
        return density;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        if (statusHeight > 0) {
            return statusHeight;
        }
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusHeight = context.getResources().getDimensionPixelSize(x);
            return statusHeight;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取底部导航栏高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        try {
            return context.getResources().getDisplayMetrics();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    private static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    public static void fileDelet(String str) {
        File file = new File(str);
        if (file == null || !file.exists() || file.isDirectory()) {
            return;
        }
        file.delete();

    }

    /***
     * 判断文件路径是否存在 不存在创建路径
     * @param path
     */
    public static void isFileHad(String path) {
        File filePath = new File(path);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
    }

    /**
     * 获取当前应用程序的包名
     *
     * @param context 上下文对象
     * @return 返回包名
     */
    public static String getAppProcessName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        android.app.ActivityManager manager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<android.app.ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (android.app.ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }
}
