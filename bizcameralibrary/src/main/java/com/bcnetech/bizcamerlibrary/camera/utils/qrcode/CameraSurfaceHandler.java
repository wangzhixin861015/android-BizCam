package com.bcnetech.bizcamerlibrary.camera.utils.qrcode;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.bcnetech.bcnetechlibrary.R;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCameraLoader;
import com.google.zxing.Result;
/**
 * Created by wenbin on 16/8/26.
 */

public class CameraSurfaceHandler extends Handler {

    private GPUImageCameraLoader gpuImageCameraLoader;
    private BizDecodeThread decodeThread;
    private State state;

    public CameraSurfaceHandler(GPUImageCameraLoader gpuImageCameraLoader) {
        this.gpuImageCameraLoader = gpuImageCameraLoader;
    }

    public void init( int decodeMode){
        decodeThread = new BizDecodeThread(gpuImageCameraLoader.getCameraSurfaceHandler(),gpuImageCameraLoader.getCropRect(), decodeMode);
        decodeThread.start();
        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        // cameraSurfaceView.startPreview();
        restartPreviewAndDecode();
    }


    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.restart_preview) {
            restartPreviewAndDecode();

        } else if (message.what == R.id.decode_succeeded) {
            state = State.SUCCESS;
            Bundle bundle = message.getData();
            gpuImageCameraLoader.handleDecode((Result) message.obj,bundle);
          //  cameraSurfaceView.stopPreview();

        } else if (message.what == R.id.decode_failed) {// We're decoding as fast as possible, so when one
            // decode fails,
            // start another.
            state = State.PREVIEW;
            gpuImageCameraLoader.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);

        }
    }

    public void quitSynchronously() {
        state = State.DONE;
       // cameraSurfaceView.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            // Wait at most half a second; should be enough time, and onPause()
            // will timeout quickly
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    public  void restartErWeima(){
        restartPreviewAndDecode();
    }
    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            gpuImageCameraLoader.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
        }
    }

    private enum State {
        PREVIEW, SUCCESS, DONE
    }

}
