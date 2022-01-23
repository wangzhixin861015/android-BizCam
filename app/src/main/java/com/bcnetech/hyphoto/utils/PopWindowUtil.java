package com.bcnetech.hyphoto.utils;

import android.app.Activity;
import android.view.View;
import android.widget.BaseAdapter;

import com.bcnetech.bcnetechlibrary.popwindow.BlueToothListPop;
import com.bcnetech.bcnetechlibrary.popwindow.EditPop;
import com.bcnetech.bcnetechlibrary.popwindow.QRCodePop;
import com.bcnetech.bcnetechlibrary.popwindow.TopListPop;
import com.bcnetech.bcnetechlibrary.popwindow.interfaces.BlueToothListInterface;
import com.bcnetech.bcnetechlibrary.popwindow.interfaces.PopWindowInterface;
import com.bcnetech.hyphoto.ui.popwindow.PartPaintPop;


/**
 * Created by wb on 2016/5/4.
 */
public class PopWindowUtil {


    public static PartPaintPop partPaintPop;
    public synchronized static void showPartPaintPop(Activity activity, PopWindowInterface popWindowInterface, View view) {
        disPartPaintPop();
        if (partPaintPop == null) {
            partPaintPop = new PartPaintPop(activity,popWindowInterface);
        }
        partPaintPop.showPop(view);
    }


    public static void disPartPaintPop() {
        if (partPaintPop != null) {
            partPaintPop.dismiss();
            partPaintPop = null;
        }
    }


    private static TopListPop topListPop;
    public synchronized static void showTopListPop(Activity activity, BaseAdapter adapter, PopWindowInterface popWindowInterface, View view){
        disTopListPop();
        if(topListPop==null){
            topListPop=new TopListPop(activity,adapter,popWindowInterface);
        }
        topListPop.showPop(view);
    }


    public static void disTopListPop(){
        if(topListPop!=null){
            topListPop.dismiss();
            topListPop=null;
        }
    }

    private static BlueToothListPop blueToothListPop;
    public synchronized static void showBlueToothListPop(Activity activity, BaseAdapter adapter, BlueToothListInterface blueToothListInterface, View view){
        disBlueToothListPop();
        if(blueToothListPop==null){
            blueToothListPop=new BlueToothListPop(activity,adapter,blueToothListInterface);
        }
        blueToothListPop.showPop(view);
    }

    public static void disBlueToothListPop(){
        if(blueToothListPop!=null){
            blueToothListPop.dismiss();
            blueToothListPop=null;
        }
    }

    private  static EditPop editPop;
    public synchronized static void showEditPop(Activity activity, PopWindowInterface popWindowInterface, View view){
        disEditPop();
        if(editPop==null){
            editPop=new EditPop(activity,popWindowInterface);
        }
        editPop.showPop(view);

    }

    private  static QRCodePop qrCodePop;
    public synchronized static void showQRCodePopPop(Activity activity, PopWindowInterface popWindowInterface, View view){
        disQrCodePop();
        if(qrCodePop==null){
            qrCodePop=new QRCodePop(activity,popWindowInterface);
        }
        qrCodePop.showPop(view);

    }

    public static void disQrCodePop(){
        if(qrCodePop!=null){
            qrCodePop.dismiss();
            qrCodePop=null;
        }
    }

    public static void disEditPop(){
        if(editPop!=null){
            editPop.dismiss();
            editPop=null;
        }
    }


}
