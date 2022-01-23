package com.bcnetech.bizcamerlibrary.camera.utils.qrcode;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.bcnetech.bcnetechlibrary.util.LogUtil;


/**
 * Created by wenbin on 16/8/26.
 */

public class BizPreviewCallback implements Camera.PreviewCallback {


    private Handler previewHandler;
    private int previewMessage;
    private int width,height;

    public BizPreviewCallback(int width,int height) {
        this.width = width;
                this.height = height;
    }

    public void setHandler(Handler previewHandler, int previewMessage) {
        this.previewHandler = previewHandler;
        this.previewMessage = previewMessage;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Handler thePreviewHandler = previewHandler;
        if (width!=0&&height!=0 && thePreviewHandler != null) {
            Message message = thePreviewHandler.obtainMessage(previewMessage, width,
                   height, data);
            message.sendToTarget();
            previewHandler = null;
        } else {
            LogUtil.d("Got preview callback, but no handler or resolution available");
        }
    }

    class PreviewCallbackThread extends Thread{
        @Override
        public void run() {
            Looper.prepare();
            Looper.loop();
        }
    }

}
