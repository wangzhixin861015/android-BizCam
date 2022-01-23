package com.bcnetech.hyphoto.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


/**
 * Created by wenbin on 16/6/24.
 */
public abstract class AddPicReceiver extends BroadcastReceiver {
    private final static String ACTION="BizCam_app_AddPicReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if(action.equals(ACTION)) {
            String type=intent.getStringExtra("type");
            onGetData(type);
        }
    }

    public abstract void onGetData(String type);

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
    public static void notifyModifyUsername(Context context,String type){
        Intent intent = new Intent(ACTION);
        intent.putExtra("type",type);
        context.sendBroadcast(intent);
    }
}
