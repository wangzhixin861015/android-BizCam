package com.bcnetech.hyphoto.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


/**
 * Created by wenbin on 16/6/24.
 */
public abstract class UploadAIReceiver extends BroadcastReceiver {
    private final static String ACTION="BizCam_app_UploadAIReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if(action.equals(ACTION)) {
            String url=intent.getStringExtra("url");
            String fileId=intent.getStringExtra("fileId");
            String fileHash=intent.getStringExtra("fileHash");
            onUploadData(url,fileId,fileHash);
        }
    }

    public abstract void onUploadData(String url,String fileId,String fileHash);

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
    public static void notifyModifyUsername(Context context,String url,String fileId,String fileHash){
        Intent intent = new Intent(ACTION);
        intent.putExtra("url",url);
        intent.putExtra("fileId",fileId);
        intent.putExtra("fileHash",fileHash);
        context.sendBroadcast(intent);
    }
}
