package com.bcnetech.hyphoto.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by wenbin on 16/8/17.
 */

public abstract  class DelPicReceiver extends BroadcastReceiver {
    private final static String ACTION_DEL="BizCam_app_uploadRece";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if(action.equals(ACTION_DEL)){
            onDelUrl(intent.getStringExtra("url"));
        }
    }


    public abstract void onDelUrl(String url);
    //注册
    public void register(Context context){
        IntentFilter filterStart=new IntentFilter(ACTION_DEL);
        context.registerReceiver(this, filterStart);
    }
    //解绑
    public void unregister(Context context){
        context.unregisterReceiver(this);
    }

    //通知
    public static void notifyModifyDel(Context context,String url){
        Intent intent=new Intent(ACTION_DEL);
        intent.putExtra("url",url);
        context.sendBroadcast(intent);

    }
}
