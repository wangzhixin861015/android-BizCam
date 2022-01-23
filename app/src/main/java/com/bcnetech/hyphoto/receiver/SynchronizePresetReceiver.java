package com.bcnetech.hyphoto.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by yhf on 2017/5/17.
 */
public abstract class SynchronizePresetReceiver extends BroadcastReceiver {

    private final static String ACTION="BizCam_app_ReLoginReceiver";



    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if(action.equals(ACTION)) {
            onGetData();
        }
    }

    public abstract void onGetData();

    //注册
    public void register(Context context){
        IntentFilter filterStart=new IntentFilter(ACTION);
        context.registerReceiver(this, filterStart);
    }
    //解绑
    public void unregister(Context context){
        context.unregisterReceiver(this);
    }

    //通知
    public static void notifyModifyPreset(Context context){
        Intent intent = new Intent(ACTION);
        context.sendBroadcast(intent);
    }
}
