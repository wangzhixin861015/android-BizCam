package com.bcnetech.hyphoto.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by yhf on 2017/4/17.
 */
public abstract class UploadManagerReceiver extends BroadcastReceiver {

    private final static String ACTION="BizCam_app_UploadManageReceiver";
    private final static String ACTION_DELETE="BizCam_app_UploadManageReceiver_DELETE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if(action.equals(ACTION)) {
            String type=intent.getStringExtra("type");
            onGetData(type);
        }else if(action.equals(ACTION_DELETE)){
            int count=intent.getIntExtra("count",0);
            onDeleteData(count);
        }
    }

    public abstract void onGetData(String type);

    public abstract void onDeleteData(int count);
    //注册
    public void register(Context context){
        IntentFilter filterStart=new IntentFilter();
        filterStart.addAction(ACTION);
        filterStart.addAction(ACTION_DELETE);
        context.registerReceiver(this, filterStart);
    }
    //解绑
    public void unregister(Context context){
        context.unregisterReceiver(this);
    }

    //通知
    public static void notifyModifyPreset(Context context,String type){
        Intent intent = new Intent(ACTION);
        intent.putExtra("type",type);
        context.sendBroadcast(intent);
    }

    public static void deletePreset(Context context,int count){
        Intent intent=new Intent(ACTION_DELETE);
        intent.putExtra("count",count);
        context.sendBroadcast(intent);
    }
}
