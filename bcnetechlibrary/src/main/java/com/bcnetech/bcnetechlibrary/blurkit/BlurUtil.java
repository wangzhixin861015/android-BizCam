package com.bcnetech.bcnetechlibrary.blurkit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;

import java.lang.reflect.Method;

/**
 * author: wsBai
 * date: 2019/2/20
 */
public class BlurUtil {


    public static Bitmap getViewCache(Activity activity) {
        /**
         * 获取当前窗口快照，相当于截屏
         */
        View view = activity.getWindow().getDecorView();
//        view.setDrawingCacheEnabled(true);
//        view.buildDrawingCache(true);
//        view.destroyDrawingCache();
//        Bitmap bmp1 = view.getDrawingCache();

        Bitmap bmp1 = loadBitmapFromView(view, activity);

        int height = getOtherHeight(activity);
        int virtualbar = getVirtualBarHeigh(activity);
        /**
         * 除去状态栏和标题栏
         */
        Bitmap bmp2 = Bitmap.createBitmap(bmp1, 0, height, bmp1.getWidth(), bmp1.getHeight()-height-virtualbar);

        bmp1.recycle();
        return bmp2;
    }

    public static Bitmap loadBitmapFromView(View v, Activity activity) {
        Bitmap b = Bitmap.createBitmap(ContentUtil.getScreenWidth(activity), ContentUtil.getScreenHeight3(activity) , Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(b);
//        v.layout(0, getOtherHeight(activity), ContentUtil.getScreenWidth(activity), ContentUtil.getScreenHeight(activity));
        v.draw(c);
        return b;
    }

    /**
     * 获取系统状态栏和软件标题栏，部分软件没有标题栏，看自己软件的配置；
     *
     * @return
     */
    private static int getOtherHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int contentTop = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentTop - statusBarHeight;
        return statusBarHeight + titleBarHeight;
    }

    /**
     * 获取虚拟功能键高度
     */
    private static int getVirtualBarHeigh(Activity activity) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }
}
