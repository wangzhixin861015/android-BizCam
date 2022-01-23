package com.bcnetech.bcnetechlibrary;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.bcnetech.bcnetechlibrary.util.LogUtil;

import java.util.List;

/**
 * Created by wenbin on 16/6/6.
 */
public abstract class BcnetechAppInstance {
    private static Context application;

    public BcnetechAppInstance() {
    }

    public static void init(Context var0) {
        if(null == var0) {
            LogUtil.e("Bigapple can not init. Cause context is null.");
        } else {
            if(application instanceof Activity) {
                LogUtil.d("Bigapple init by Activity.");
                application = var0.getApplicationContext();
            } else if(var0 instanceof Application) {
                LogUtil.d("Bigapple init by Application.");
                application = var0;
            } else {
                LogUtil.e("Bigapple can not be init. Cause context is wrong type.");
            }

        }
    }

    public static Context getApplicationContext() {
        return application;
    }
    /**
     * 获取当前应用程序的包名
     * @param context 上下文对象
     * @return 返回包名
     */
    public static String getAppProcessName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }
}
