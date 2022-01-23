package com.bcnetech.bcnetechlibrary.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by wenbin on 16/5/4.
 */
public class HttpUtil{
    /**
     * 检查当前网络是否可用
     *
     * @return
     */

    public static boolean isNetworkAvailable(Context context,boolean onlyWifi,boolean isUpload)
    {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());


                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        if(isUpload){
                            if(networkInfo[i].getType()==ConnectivityManager.TYPE_WIFI){
                                return true;
                            }
                        }
                        else {
                            if(networkInfo[i].getType()==ConnectivityManager.TYPE_MOBILE||networkInfo[i].getType()==ConnectivityManager.TYPE_WIFI){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
