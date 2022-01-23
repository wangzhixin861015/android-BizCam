package com.bcnetech.bcnetechlibrary.util;

import android.widget.Toast;

import com.bcnetech.bcnetechlibrary.BcnetechAppInstance;


/**
 * Created by wenbin on 16/5/5.
 */
public class ToastUtil {
    private static Toast toast;



    /**
     * 吐司
     *
     * @param text
     */
    public static void toast(String text) {
        if (toast==null){
            toast = Toast.makeText(BcnetechAppInstance.getApplicationContext(), text, Toast.LENGTH_SHORT);
        }else {
            cencelToast();
            toast = Toast.makeText(BcnetechAppInstance.getApplicationContext(), text, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    /**
     * 吐司
     *
     * @param text
     */
    public static void toast2(String text) {
        if (toast==null){
            toast = Toast.makeText(BcnetechAppInstance.getApplicationContext(), text, Toast.LENGTH_LONG);
        }else {
            cencelToast();
            toast = Toast.makeText(BcnetechAppInstance.getApplicationContext(), text, Toast.LENGTH_LONG);
        }
        toast.show();
    }

    public static void cencelToast(){
        if(toast!=null){
            toast.cancel();
            toast=null;
        }
    }
}
