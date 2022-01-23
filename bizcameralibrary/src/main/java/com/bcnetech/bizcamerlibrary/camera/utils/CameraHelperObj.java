/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bcnetech.bizcamerlibrary.camera.utils;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.util.Size;

import com.bcnetech.bcnetechlibrary.bean.CameraParm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public interface CameraHelperObj {

    int PREVIEW_169 = 4773;
    int PREVIEW_43 = 4772;
    int PREVIEW_11 = 4771;

    int SIZELARGE = 101;
    int SIZEMIDDLE = 102;
    int SIZESMALL = 103;

    /**
     * 照片尺寸
     */
    //3:4中
    int PHOTO_WIDTH_M = 1536;
    int PHOTO_HEIGHT_M = 2048;
    //16:9中
    int PHOTO_WIDTH2_M = 1440;
    int PHOTO_HEIGHT2_M = 2560;
    //3:4小
    int PHOTO_WIDTH_S = 720;
    int PHOTO_HEIGHT_S = 960;
    //16:9小
    int PHOTO_WIDTH2_S = 720;
    int PHOTO_HEIGHT2_S = 1280;

    /**
     * 预览（录像）尺寸
     */
    int PREVIEW_WIDTH = 1080;
    int PREVIEW_HEIGHT = 1920;

    String PREVIEW_SIZE = "viewSize";
    String PICTURE_SIZE = "picSize";

    void onPause();

    void onDestroy();

    boolean onResume();


    void setisAi360(boolean isAi360);




    /**
     * 设置预览分辨率
     */
    void setMPreViewSize(String preview, String picSize);

    Size getmPreviewSize();


    void pointFocus(List<Area> areas);

    void pointFocus(Camera mCameraInstance, List<Area> areas);

    void setAutoFous(Camera mCameraInstance,boolean b);

    void setAF(boolean b);

    interface CameraDataListener {

        void getPreSize(ArrayList<Size> sizes);

        void getCameraData(long sec, int iso, int wb, float focus);

    }

    interface getQRInterface {
        void getQR(String result);
    }

    interface TackPhotoCallback {
        void tackPhotoSuccess(String photoPath);

        void getPhotoSuccess(Bitmap bitmap, CameraParm cameraParm);

        void getPhotoByteSuccess(byte[] data, CameraParm cameraParm);

        void tackPhotoError(Exception e);
    }

    void setCameraDataListener(CameraDataListener cameraDataListener);

    void closeCamera();

    void setGetQRInter(getQRInterface getQRInter);

    void setWhiteBalance(int supported_values);

    void setIso(int iso);

    void setFocusDistance(boolean isAutoFocus, float focus_distance);

    void setSec(long sec);

    void lockAllCameraParam();

    void takePicture(TackPhotoCallback tackPhotoCallback);

    void startRecordingVideo();

    void setRecordUrl(String recordUrl);

    void prepareRecordModel();

    void setRecordSize(Size size);

    void stopRecordingVideo();

    void stopPreView();

    String getRecordUrl();

    void setAe(int ae);

    CameraCharacteristics getmCameraCharacteristics();

    ArrayList<Size> getPreSize();

    ArrayList<Size> getAllSize();

    ArrayList<Size> getAllPreviewSize();

    void notifyPreRatio(int preType);

    int getNumberOfCameras();

    boolean allowCamera2Support(int cameraId);

    void unlockAllCameraParam();

    void lockPresetCameraParam(String sec, String iso, String wb, String focus);

    void lockPresetCameraParam(String sec, String iso, String wb, String focus, String mRed, String mGreenEven, String mGreenOdd, String mBlue);

    interface CameraStateInter {
        void onOpened();
    }

    void setCameraStateInter(CameraStateInter cameraStateInter);


    /**
     * Camera API
     *
     * @return
     */

    void setFlash2(boolean isflashon, Camera mCameraInstance);

    void setEV2(int ev, Camera mCameraInstance);

    void setWhiteBalance(String wb, Camera mCameraInstance);

    void setIso(String iso, Camera mCameraInstance);

    void setFocus(Camera mCameraInstance, String focus);

    void setEV(String ev, Camera mCameraInstance);

    void setCurrentType(boolean isVideo);

    void setFlash(boolean isflash, Camera mCameraInstance);

    Camera openCamera(final int id);

    Camera.Parameters getCurrentParameters();

    void getCameraInfo(final int cameraId, final CameraHelper.CameraInfo2 cameraInfo);

    int getCameraDisplayOrientation(final int cameraId);

    HashMap<String, String> getCameraParams(Camera mCameraInstance);

    void setCameraParms(Camera mCameraInstance);

    void pointFocus(Camera mCameraInstance, float x, float y);

    Camera.Size getPreviewSize(Camera mCameraInstance);

    Camera.Size getAdapterSize(Camera mCameraInstance);

    int getCameraOrientation();

    boolean isFrontFacing();

    void cancelAutoFocus();

    void setCurrentRatio(int ratio);

    //void setCameraType(Camera camera,boolean isVideo);

    void setFpsRate(int rate);


    class CameraInfo2 {
        public int facing;
        public int orientation;
    }

    class Area {
        final Rect rect;
        final int weight;

        public Area(Rect rect, int weight) {
            this.rect = rect;
            this.weight = weight;
        }
    }

    void setMPreViewSize2(Size preview, Size pciture);


}
