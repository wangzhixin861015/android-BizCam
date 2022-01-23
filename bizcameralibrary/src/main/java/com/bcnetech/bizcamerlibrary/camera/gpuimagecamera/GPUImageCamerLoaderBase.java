package com.bcnetech.bizcamerlibrary.camera.gpuimagecamera;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Size;

import com.bcnetech.bcnetechlibrary.bean.CameraParm;
import com.bcnetech.bizcamerlibrary.camera.utils.CameraHelperObj;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.HashMap;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by a1234 on 17/3/22.
 */

public abstract class GPUImageCamerLoaderBase {

    public void pointFocus(float x, float y,int w,int h) {

    }

    public void setAf(boolean isAutoFous) {

    }

    public void setFlash(boolean isFlashOn) {

    }

    public abstract boolean onResume() ;



    public void onPause() {

    }

    public void onDestroy() {

    }

    public abstract Camera getmCameraInstance();

    public abstract Camera.Size getPreviewSize();

    public abstract Camera.Size getPictureSize();

    public abstract Camera getCameraInstance(int id);

    public abstract CameraHelperObj getmCameraHelper();



    public abstract Object setUpCamera(int id);



    public void releaseCamera() {

    }


    public void takePhoto() {

    }


    public void endRecording() {

    }

    public void pauseRecording(){

    }

    public void ResumeRecording(){

    }

    public void startRecoder() {

    }

    public void prepareRecord(){

    }

    public static interface CameraLoaderListener {
        abstract void isCamera2Support(boolean isc2Support);
    }

    public static interface GetQRResultListener{
        abstract void onGetQRResult(String result);
    }

    public void setQRResultListener(GetQRResultListener getQRResultListener){}


    public void setCameraLoaderListener(CameraLoaderListener cameraLoaderListener) {
    }

    public static interface GPUImageCamreraListener {
        abstract void takePhotoListenr(Bitmap bitmap, int ratio, CameraParm cameraParm);

        abstract void takePhotoByteListener(byte[] data,int ratio,CameraParm cameraParm);

        abstract void takePhonoErr();

        abstract void getOrientation(int orientation);

        abstract void getyAngle(float xAngle, float yAngle, float zAngle);

        abstract void getAngle(float xDegrees, float yDegrees);

    }

    public static interface GetParamsListener {
        abstract void onGetParams(HashMap map);
    }


    public void setGetParamsListener(final GPUImageCamerLoaderBase.GetParamsListener getParamsListener) {
    }

    public abstract GPUImageCamreraListener getGpuImageCamreraListener();

    public void setGpuImageCamreraListener(GPUImageCamreraListener gpuImageCamreraListener) {

    }

    public void setCameraParams(HashMap<String,String> hashMap) {
    }

    public void   lockAllCameraParam(){};


    public void switchFilterTo(GPUImageFilter gpuImageFilter) {
    }

    public void readyStart(GPUImageCameraLoader.VidioInfer vidioInfer,GPUImageFilter gpuImageFilter){}


    public String getVidioUrl(){
        return "";
    }
    public String getVidioBitmapUrl(){
        return "";
    }

    public Camera.Parameters getCurrentparameter(){
        return null;
    }

    public void setEv(int ev){
    }

    public boolean supportTypeSquare(){
        return false;
    }

    public int getCameraId(){
        return 0;
    }

    public GPUImage getGpuimage(){
        return null;
    }

    public void setAe(int ae){

    }

    public interface CameraDataListener{
        void getCameraData(long sec,int iso,int wb,float focus);
        void getPreSize(ArrayList<Size> sizes);
    }

    public void setCameraDataListenerListener(CameraDataListener cameraDataListener) {
    }


    public void stopPreview(){

    }

    public ArrayList<Size> getPreSize(){
        return null;
    }

    public void notifyPreRatio(int preType){

    }

    public void handleDecode(Result rawResult, Bundle bundle) {

    }

    public synchronized void requestPreviewFrame(Handler handler, int message) {

    }

    public void setQRCode(boolean startQRCode) {

    }

    public void setCropRect(Rect rect){

    }
}
