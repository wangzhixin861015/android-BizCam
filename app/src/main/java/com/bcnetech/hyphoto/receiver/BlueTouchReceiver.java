package com.bcnetech.hyphoto.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by wenbin on 16/9/18.
 */

public abstract class BlueTouchReceiver extends BroadcastReceiver {
    private final static String ACTION="BizCam_Reveiver_BlueTouch_Close";
    private final static String ACTION_LIGHT="BizCam_Reveiver_BlueTouch_Light";
    public final static int LEFT_LIGHT=1;
    public final static int RIGHT_LIGHT=2;
    public final static int MOVE_LIGHT=3;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if(action.equals(ACTION)) {
            onGetData();
        }
        else if(action.equals(ACTION_LIGHT)){
            int type=intent.getIntExtra("type",0);
            int values=intent.getIntExtra("values",-1);
            onGetLight(type,values);
        }
    }

    public abstract void onGetData();
    public abstract void onGetLight(int type,int values);

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
    public static void notifyModifyUsername(Context context){
        Intent intent = new Intent(ACTION);
        context.sendBroadcast(intent);
    }
    //通知
    public static void notifyModifyLight(Context context,int type,int value){
        Intent intent = new Intent(ACTION_LIGHT);
        intent.putExtra("type",type);
        intent.putExtra("values",value);
        context.sendBroadcast(intent);
    }
}
