package com.bcnetech.hyphoto.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by yhf on 2017/4/12.
 */
public abstract class PresetReceiver extends BroadcastReceiver{

    private final static String ACTION="BizCam_app_PresetReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if(action.equals(ACTION)) {
            int count=intent.getIntExtra("count",1);
            onGetData(count);
        }
    }

    public abstract void onGetData(int count);

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
    public static void notifyModifyPreset(Context context,int count){
        Intent intent = new Intent(ACTION);
        intent.putExtra("count",count);
        context.sendBroadcast(intent);
    }

}
