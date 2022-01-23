package com.bcnetech.hyphoto.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.bcnetech.hyphoto.sql.dao.UploadPicData;

/**
 * Created by a1234 on 2017/8/18.
 */

public abstract class UploadPicReceiver extends BroadcastReceiver{
    private final static String ACTION_ON="BizCam_app_UploadPicReceiver_On";
    private final static String UPLOADPIC_DATA="BizCam_app_UploadPicReceiver_UploadPic_Data";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if(action.equals(ACTION_ON)){
            UploadPicData uploadPicData=(UploadPicData) intent.getSerializableExtra(UPLOADPIC_DATA);
            addPic(uploadPicData);
        }
    }

    public abstract void addPic(UploadPicData uploadPicData);



    //注册
    public void register(Context context){
        IntentFilter filterStart=new IntentFilter(ACTION_ON);
        context.registerReceiver(this, filterStart);
    }
    //解绑
    public void unregister(Context context){
        context.unregisterReceiver(this);
    }

    //通知
    public static void notifyModifyAddPic(Context context, UploadPicData uploadPicData){
        Intent intent = new Intent(ACTION_ON);
        intent.putExtra(UPLOADPIC_DATA, uploadPicData);
        context.sendBroadcast(intent);

    }
}
