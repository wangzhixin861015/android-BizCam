package com.bcnetech.hyphoto.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by wenbin on 2017/1/15.
 */

public abstract class HttpChangReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(null!=connectivityManager){
            NetworkInfo  mobNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo  wifiNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(null!=wifiNetInfo&&wifiNetInfo.isConnected()){
                httpWifiConnection();
            }
            else if(null!=mobNetInfo&&mobNetInfo.isConnected()){
                httpModNetConnection();
            }else{
                httpDisConnection();
            }
        }else {
            httpDisConnection();

        }


    }  //如果无网络连接activeInfo为null



    public abstract void httpWifiConnection();

    public abstract void httpModNetConnection();

    public abstract void httpDisConnection();




    //注册
    public void register(Context context){
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this, filter);
    }
    //解绑
    public void unregister(Context context){
        context.unregisterReceiver(this);
    }

}