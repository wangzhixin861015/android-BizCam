package com.bcnetech.hyphoto.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.bcnetech.bcnetchhttp.bean.response.CloudPicData;
import com.bcnetech.hyphoto.task.manage.DownloadListHolder;

import java.util.List;

/**
 * Created by yhf on 2017/5/17.
 */
public abstract class DownloadPicReceiver extends BroadcastReceiver {

    private final static String ACTION="BizCam_app_DownloadPicReceiver";
    private final static String DOWNLOADPIC_DATA="BizCam_app_DownloadPic_Receiver_DOWNLOADPIC_DATA";



    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();

        if(action.equals(ACTION)) {
            List<CloudPicData> cloudPicDatas=DownloadListHolder.getInstance().getCloudPicData();

            onGetData(cloudPicDatas);
        }
    }

    public abstract void onGetData(List<CloudPicData> cloudPicDatas);

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
    public static void notifyModifyPreset(Context context,List<CloudPicData> cloudPicDatas){
        Intent intent = new Intent(ACTION);

        DownloadListHolder.getInstance().saveCloudPicData(cloudPicDatas);
//        intent.putExtra(DOWNLOADPIC_DATA,(Serializable)cloudPicDatas);
        context.sendBroadcast(intent);
    }
}
