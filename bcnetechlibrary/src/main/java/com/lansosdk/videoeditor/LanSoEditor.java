package com.lansosdk.videoeditor;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;


/**
 * 杭州蓝松科技有限公司
 * www.lansongtech.com
 */
public class LanSoEditor {


    private static boolean isLoaded = false;

    public static void initSDK(Context context) {
        loadLibraries();
        LanSoEditor.initSo(context,null);
        VideoEditor.logEnable(context);  //使能;
    }

    public static void initSDK(Context context, String str) {
        loadLibraries();
        LanSoEditor.initSo(context, str);
    }


    private static synchronized void loadLibraries() {
        if (isLoaded)
            return;

        Log.d("lansongeditor", "lansongSDK load libraries. www.lansongtech.com");

        System.loadLibrary("LanSongffmpeg");
        System.loadLibrary("LanSongplayer");
        System.loadLibrary("LanSongdisplay");

        isLoaded = true;
    }

    public static void initSo(Context context, String argv) {
        nativeInit(context, context.getAssets(), argv);
    }

    public static void unInitSo() {
        nativeUninit();
    }

    public static native void nativeInit(Context ctx, AssetManager ass, String filename);

    public static native void nativeUninit();


}
