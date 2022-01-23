package com.bcnetech.bizcamerlibrary.camera.gpuimagecamera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.bean.CameraParm;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bizcamerlibrary.camera.CameraTextureView;
import com.bcnetech.bizcamerlibrary.camera.OrientationSensorListener;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;
import com.bcnetech.bizcamerlibrary.camera.utils.Camera2Helper;
import com.bcnetech.bizcamerlibrary.camera.utils.CameraFileUtil;
import com.bcnetech.bizcamerlibrary.camera.utils.CameraHelper;
import com.bcnetech.bizcamerlibrary.camera.utils.CameraHelperObj;
import com.bcnetech.bizcamerlibrary.camera.utils.CameraSizeModel;
import com.bcnetech.bizcamerlibrary.camera.utils.qrcode.BizPreviewCallback;
import com.bcnetech.bizcamerlibrary.camera.utils.qrcode.CameraSurfaceHandler;
import com.bcnetech.bizcamerlibrary.camera.utils.zxing.decode.DecodeFormatManager;
import com.google.zxing.Result;
import com.lansosdk.videoeditor.VideoEditor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.gpumanage.GPUImageFilterTools;
import jp.co.cyberagent.android.gpuimage.reocder.media_encoder.BaseMediaEncoderRunable;
import jp.co.cyberagent.android.gpuimage.reocder.media_encoder.MediaVideoEncoderRunable;
import jp.co.cyberagent.android.gpuimage.reocder.media_muxer.SohuMediaMuxerManager;

/**
 * Created by wenbin on 16/11/3.
 */

public class GPUImageCameraLoader extends GPUImageCamerLoaderBase implements Camera.PreviewCallback {
    private int TOP = 270;
    private int BOTTOM = 90;
    private int LEFT = 0;
    private int RIGHT = 180;
    public int currentRatio = BOTTOM;

    private int mCurrentCameraId = 0;
    private Camera mCameraInstance;
    private com.bcnetech.bizcamerlibrary.camera.utils.CameraHelperObj mCameraHelper;
    private Context context;
    private GPUImage mGPUImage;
    private GPUImageCamerLoaderBase.GPUImageCamreraListener gpuImageCamreraListener;
    private GPUImageCamerLoaderBase.GetQRResultListener getQRResultListener;
    private GPUImageCamerLoaderBase.CameraLoaderListener cameraLoaderListener;
    private GPUImageCamerLoaderBase.CameraDataListener cameraDataListener;
    private GPUImageFilter mFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
    private ChangeOrientationHandler changeOrientationHandler;
    private Sensor sensor;
    // 定义真机的Sensor管理器
    private SensorManager mSensorManager;
    private OrientationSensorListener listener;
    //private RecorderManage recorderManage;
    private GPUImageCamerLoaderBase.GetParamsListener getParamsListener;
    private Rect rect;
    private SohuMediaMuxerManager mMediaMuxerManager;
    private String bitmapUrl;
    private String vidioUrl;
    private boolean isVidioComplete;
    private boolean previewing;
    private boolean supports_camera2 = false;
    /**
     * 360相机和智拍模式：该模式下拍照回调jpeg编码后的原始byte[]数据，视频录制不进行裁剪
     */
    private boolean isAiCamera = false;
    private VidioInfer vidioInfer;
    private CameraSizeModel cameraSizeModel;
    /**
     * 执行FFMPEG命令保存路径
     */
    private String saveVideoPath = Flag.FFMPEG_CACHE;
    public static final int CAMER_TYPE = 631;
    public static final int VIDE0_TYPE = 632;


    /**
     * Camera API
     *
     * @param context
     * @param glSurfaceView
     */
    public GPUImageCameraLoader(Context context, GLSurfaceView glSurfaceView) {
        this.context = context;
        supports_camera2 = false;
        mCameraHelper = new com.bcnetech.bizcamerlibrary.camera.utils.CameraHelper();
        mGPUImage = new GPUImage(context);
        mGPUImage.setGLSurfaceView(glSurfaceView);
        // 获取真机的传感器管理服务
        mSensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor == null) {
            ToastUtil.toast2("您的手机不支持\n加速传感器，\n可能影响照片朝向");
        }
        changeOrientationHandler = new ChangeOrientationHandler();
        listener = new OrientationSensorListener(changeOrientationHandler);
    }

    /**
     * Camera2 API
     *
     * @param context
     */
    public GPUImageCameraLoader(Context context) {
        this.context = context;
        supports_camera2 = true;
        // this.imageView=imageView;
        // 获取真机的传感器管理服务
        mSensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor == null) {
            ToastUtil.toast2("您的手机不支持\n加速传感器，\n可能影响照片朝向");
        }

    }

    /**
     * 扫码界面
     *
     * @param textureView
     */
    public void setTextureView(CameraTextureView textureView) {
        mCameraHelper = new Camera2Helper(context, textureView);
        mCameraHelper.setCameraDataListener(new CameraHelperObj.CameraDataListener() {

            @Override
            public void getCameraData(long sec, int iso, int wb, float focus) {
                //cameraDataListener.getCameraData(sec, iso, wb, focus);
            }

            @Override
            public void getPreSize(ArrayList<Size> sizes) {
                //cameraDataListener.getPreSize(sizes);
            }
        });
        mCameraHelper.setGetQRInter(new CameraHelperObj.getQRInterface() {
            @Override
            public void getQR(String result) {
                getQRResultListener.onGetQRResult(result);
            }
        });

        mCameraHelper.setCameraStateInter(new CameraHelperObj.CameraStateInter() {
            @Override
            public void onOpened() {
                camera_open_state = CameraOpenState.CAMERAOPENSTATE_OPENED;
            }
        });
    }

    public Camera getmCameraInstance() {
        return mCameraInstance;
    }

    public Camera.Size getPreviewSize() {
        return mCameraHelper.getPreviewSize(mCameraInstance);
    }

    public Camera.Size getPictureSize() {
        return mCameraHelper.getAdapterSize(mCameraInstance);
    }


    public void pointFocus(float x, float y, int w, int h) {
        ArrayList<CameraHelperObj.Area> areas = getAreas(x, y, w, h);

        if (supports_camera2) {
            mCameraHelper.pointFocus(areas);
        } else {
            mCameraHelper.pointFocus(mCameraInstance, areas);
        }
    }

    private final Matrix camera_to_preview_matrix = new Matrix();
    private final Matrix preview_to_camera_matrix = new Matrix();


    private ArrayList<CameraHelperObj.Area> getAreas(float x, float y, int w, int h) {
        float[] coords = {x, y};
        calculatePreviewToCameraMatrix(w, h);
        preview_to_camera_matrix.mapPoints(coords);
        float focus_x = coords[0];
        float focus_y = coords[1];

        int focus_size = 50;

        Rect rect = new Rect();
        rect.left = (int) focus_x - focus_size;
        rect.right = (int) focus_x + focus_size;
        rect.top = (int) focus_y - focus_size;
        rect.bottom = (int) focus_y + focus_size;
        if (rect.left < -1000) {
            rect.left = -1000;
            rect.right = rect.left + 2 * focus_size;
        } else if (rect.right > 1000) {
            rect.right = 1000;
            rect.left = rect.right - 2 * focus_size;
        }
        if (rect.top < -1000) {
            rect.top = -1000;
            rect.bottom = rect.top + 2 * focus_size;
        } else if (rect.bottom > 1000) {
            rect.bottom = 1000;
            rect.top = rect.bottom - 2 * focus_size;
        }

        ArrayList<CameraHelperObj.Area> areas = new ArrayList<>();
        areas.add(new CameraHelperObj.Area(rect, 1000));
        return areas;
    }

    private void calculatePreviewToCameraMatrix(int w, int h) {

        calculateCameraToPreviewMatrix(w, h);
        if (!camera_to_preview_matrix.invert(preview_to_camera_matrix)) {

        }
    }

    private void calculateCameraToPreviewMatrix(int w, int h) {

        camera_to_preview_matrix.reset();
        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            // from http://developer.android.com/reference/android/hardware/Camera.Face.html#rect
            // Need mirror for front camera
            boolean mirror = mCameraHelper.isFrontFacing();
            camera_to_preview_matrix.setScale(mirror ? -1 : 1, 1);
            // This is the value for android.hardware.Camera.setDisplayOrientation.
            int display_orientation = mCameraHelper.getCameraOrientation();

            camera_to_preview_matrix.postRotate(display_orientation);
        } else {
            // Unfortunately the transformation for Android L API isn't documented, but this seems to work for Nexus 6.
            // This is the equivalent code for android.hardware.Camera.setDisplayOrientation, but we don't actually use setDisplayOrientation()
            // for CameraController2, so instead this is the equivalent code to https://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation(int),
            // except testing on Nexus 6 shows that we shouldn't change "result" for front facing camera.
            boolean mirror = mCameraHelper.isFrontFacing();
            camera_to_preview_matrix.setScale(1, mirror ? -1 : 1);
            int degrees = getDisplayRotationDegrees();
            int result = (mCameraHelper.getCameraOrientation() - degrees + 360) % 360;

            camera_to_preview_matrix.postRotate(result);
        }
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        camera_to_preview_matrix.postScale(w / 2000f, h / 2000f);
        camera_to_preview_matrix.postTranslate(w / 2f, h / 2f);
    }

    private int getDisplayRotationDegrees() {

        int rotation = getDisplayRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                break;
        }
        return degrees;
    }

    /**
     * Returns the ROTATION_* enum of the display relative to the natural device orientation.
     */
    public int getDisplayRotation() {

        int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();


        return rotation;
    }

    public void setAf(boolean isAutoFous) {
        mCameraHelper.setAutoFous(getmCameraInstance(), isAutoFous);
        // mCameraHelper.setAF(isAutoFous);
    }

    public boolean onResume() {
        if (supports_camera2) {
            if (null != mCameraHelper)
                return mCameraHelper.onResume();
            return false;
        } else {
            cameraSurfaceHandler = null;
            Camera camera = setUpCamera(mCurrentCameraId);
            return camera != null;
        }
    }

    public void resumeSenser() {
        mSensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(listener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void onPause() {
        if (supports_camera2) {
            if (mCameraHelper != null) {
                mCameraHelper.onPause();
            }
        } else {
            releaseCamera();
            if (cameraSurfaceHandler != null) {
                cameraSurfaceHandler.quitSynchronously();
                cameraSurfaceHandler = null;
            }
        }
        mSensorManager.unregisterListener(listener);
    }


    public void onDestroy() {
        if (supports_camera2) {
            if (mCameraHelper != null) {
                mCameraHelper.onDestroy();
                mCameraHelper.closeCamera();
            }
        } else {
            releaseCamera();
            CameraFileUtil.clearFolders(saveVideoPath);

            if (mGPUImage != null) {
                mGPUImage.deleteImage();
            }
            mGPUImage = null;
            mFilterAdjuster = null;
            if (savebit != null) {
                savebit.recycle();
            }

        }
        mCameraHelper = null;
        changeOrientationHandler = null;
    }

    /**
     * Camera2 OpenCamera
     *
     * @param hashMap
     * @param textureView
     */
    public void openCamera(HashMap<String, String> hashMap, TextureView textureView) {
        if (mCameraHelper != null) {
            mCameraHelper.onDestroy();
            mCameraHelper.closeCamera();
            mCameraHelper = null;
        }
        String preview = hashMap.get(Flag.PREVIEW);
        String picSize = hashMap.get(Flag.PICSIZE + "");
        String flash = hashMap.get(Flag.FLASH + "");
        boolean isflashon = false;
        if (flash != null && !TextUtils.isEmpty(flash)) {
            isflashon = flash.equals("true") ? true : false;
        }
        if (preview.equals("0")) {
            if (CameraStatus.getCameraStatus() != null && CameraStatus.getCameraStatus().getRecordSize() != null)
                mCameraHelper = new Camera2Helper(context, textureView, Integer.parseInt(preview), Integer.parseInt(picSize), isflashon, CameraStatus.getCameraStatus().getRecordSize());
        } else {
            mCameraHelper = new Camera2Helper(context, textureView, Integer.parseInt(preview), Integer.parseInt(picSize), isflashon, null);
        }

        changeOrientationHandler = new ChangeOrientationHandler();
        listener = new OrientationSensorListener(changeOrientationHandler);
        camera_open_state = CameraOpenState.CAMERAOPENSTATE_OPENING;
        // mCameraHelper.setPicSaveFile(mFile);
        cameraLoaderListener.isCamera2Support(supports_camera2);

        mCameraHelper.setCameraDataListener(new CameraHelperObj.CameraDataListener() {

            @Override
            public void getCameraData(long sec, int iso, int wb, float focus) {
                cameraDataListener.getCameraData(sec, iso, wb, focus);
            }

            @Override
            public void getPreSize(ArrayList<Size> sizes) {
                cameraDataListener.getPreSize(sizes);
            }
        });

        mCameraHelper.setCameraStateInter(new CameraHelperObj.CameraStateInter() {
            @Override
            public void onOpened() {
                camera_open_state = CameraOpenState.CAMERAOPENSTATE_OPENED;
            }
        });
    }


    public Camera setUpCamera(final int id) {
        mCameraInstance = getCameraInstance(id);
        if (mCameraInstance == null) {
            // ToastUtil.toast("相机未开启!\n请打开相机权限,\n或相机已损坏");
            return null;
        }
        mCameraHelper.setCameraParms(mCameraInstance);
        HashMap<String, String> map = mCameraHelper.getCameraParams(mCameraInstance);
        if (map != null && getParamsListener != null) {
            getParamsListener.onGetParams(map);
        }
        int orientation = mCameraHelper.getCameraDisplayOrientation(mCurrentCameraId);
        com.bcnetech.bizcamerlibrary.camera.utils.CameraHelper.CameraInfo2 cameraInfo = new CameraHelper.CameraInfo2();
        mCameraHelper.getCameraInfo(mCurrentCameraId, cameraInfo);
        boolean flipHorizontal = cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
        mCameraInstance.enableShutterSound(false);
        mGPUImage.setUpCamera(mCameraInstance, orientation, flipHorizontal, false);
        mCameraHelper.setCameraDataListener(new CameraHelperObj.CameraDataListener() {

            @Override
            public void getCameraData(long sec, int iso, int wb, float focus) {
            }

            @Override
            public void getPreSize(ArrayList<Size> sizes) {
                if (cameraDataListener != null) {
                    cameraDataListener.getPreSize(sizes);
                }
            }
        });
        return mCameraInstance;
    }

    @Override
    public Camera.Parameters getCurrentparameter() {
        return mCameraHelper.getCurrentParameters();
    }


    /**
     * A safe way to get an instance of the Camera object.
     */
    public Camera getCameraInstance(final int id) {
        Camera c = null;
        try {
            c = mCameraHelper.openCamera(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    @Override
    public CameraHelperObj getmCameraHelper() {
        return this.mCameraHelper;
    }

    public void prepareRecordModel() {
        if (!supports_camera2) {
            CameraFileUtil.clearFolders(saveVideoPath);
        } else {
            mCameraHelper.prepareRecordModel();
        }
    }


    @Override
    public void prepareRecord() {
        File file = new File(Flag.APP_CAMERAL, System.currentTimeMillis() + ".mp4");
        mCameraHelper.setRecordUrl(file.getAbsolutePath());
        //mCameraHelper.prepareRecordModel();
    }

    @Override
    public void setAe(int ae) {
        mCameraHelper.setAe(ae);
    }

    @Override
    public void stopPreview() {
        if (mCameraHelper != null)
            mCameraHelper.stopPreView();
    }

    @Override
    public ArrayList<Size> getPreSize() {
        return mCameraHelper.getPreSize();
    }

    @Override
    public void notifyPreRatio(int preType) {

        mCameraHelper.notifyPreRatio(preType);
    }

    /**
     * 适配于AI相机
     *
     * @param hashMap
     */
    public void setCameraParamsAI(HashMap<String, String> hashMap) {
        if (mCameraHelper == null && hashMap != null)
            return;
        if (hashMap.get(Flag.CAMERATYPE) != null) {
            mCameraHelper.setisAi360(true);
            boolean cameraType = Integer.parseInt(hashMap.get(Flag.CAMERATYPE)) == GPUImageCameraLoader.VIDE0_TYPE;
            mCameraHelper.setCurrentType(cameraType);
        }
        Camera.Size previewS = findBestPreviewResolution(mCameraInstance, 1200, 1600);
        Camera.Size pictureS = findBestPictureResolution(mCameraInstance, 1200, 1600);
        mCameraHelper.setMPreViewSize2(new Size(previewS.width, previewS.height), new Size(pictureS.width, pictureS.height));

        String flash = hashMap.get(Flag.FLASH);
        if (flash != null) {
            boolean flashType = Boolean.parseBoolean(flash);
            mCameraHelper.setFlash2(flashType, mCameraInstance);
            //mCameraHelper.setFlash(flashType, mCameraInstance);
        }

        mCameraHelper.setCameraParms(mCameraInstance);
    }

    /**
     * 拍照图片分辨率
     *
     * @param cameraInst
     * @param screenWidth
     * @param screenHeight
     * @return
     */
    private Camera.Size findBestPictureResolution(Camera cameraInst, int screenWidth, int screenHeight) {
        Camera.Size supportSize = null;
        final int allPix = screenWidth * screenHeight;
        Camera.Parameters cameraParameters = cameraInst.getParameters();
        List<Camera.Size> supportedPicResolutions = cameraParameters.getSupportedPictureSizes(); // 至少会返回一个值
        StringBuilder picResolutionSb = new StringBuilder();
        for (Camera.Size supportedPicResolution : supportedPicResolutions) {
            picResolutionSb.append(supportedPicResolution.width).append('x')
                    .append(supportedPicResolution.height).append(" ");
        }

        Camera.Size defaultPictureResolution = cameraParameters.getPictureSize();

        // 排序
        List<Camera.Size> sortedSupportedPicResolutions = new ArrayList<Camera.Size>(
                supportedPicResolutions);
        Collections.sort(sortedSupportedPicResolutions, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (aPixels >= allPix && bPixels >= allPix) {
                    if (bPixels > aPixels) {
                        return -1;
                    }
                    if (bPixels < aPixels) {
                        return 1;
                    }
                    return 0;
                } else if (aPixels < allPix && bPixels < allPix) {
                    if (bPixels < aPixels) {
                        return -1;
                    }
                    if (bPixels > aPixels) {
                        return 1;
                    }
                    return 0;
                } else {
                    if (bPixels < aPixels) {
                        return -1;
                    }
                    if (bPixels > aPixels) {
                        return 1;
                    }
                    return 0;
                }

            }
        });

        ArrayList sizeList = new ArrayList<>();
        for (int i = 0; i < supportedPicResolutions.size(); i++) {
            sizeList.add(new Size(supportedPicResolutions.get(i).width, supportedPicResolutions.get(i).height));
            //LogUtil.d("pictureAllSizes: " + supportedPicResolutions.get(i).width + "x" + supportedPicResolutions.get(i).height);
        }

        // 移除不符合条件的分辨率
        double screenAspectRatio = screenWidth
                / (double) screenHeight;
        Iterator<Camera.Size> it = sortedSupportedPicResolutions.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewResolution = it.next();
            int width = supportedPreviewResolution.width;
            int height = supportedPreviewResolution.height;
            // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
            // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
            // 因此这里要先交换然后在比较宽高比
            boolean isCandidatePortrait = width > height;
            int maybeFlippedWidth = isCandidatePortrait ? height : width;
            int maybeFlippedHeight = isCandidatePortrait ? width : height;
            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);

            // 找到与屏幕分辨率完全匹配的预览界面分辨率直接返回
            if (maybeFlippedWidth == screenWidth
                    && maybeFlippedHeight == screenHeight) {
                supportSize = supportedPreviewResolution;
            }

            if (width < CameraHelper.MINSIZE && height < CameraHelper.MINSIZE || distortion > CameraHelper.MAX_ASPECT_DISTORTION) {
                it.remove();
            }
        }
        Collections.sort(sortedSupportedPicResolutions, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });
        if (cameraDataListener != null) {
            ArrayList preList = new ArrayList<>();
            for (int i = 0; i < sortedSupportedPicResolutions.size(); i++) {
                preList.add(new Size(sortedSupportedPicResolutions.get(i).width, sortedSupportedPicResolutions.get(i).height));
            }
            cameraDataListener.getPreSize(preList);
        }

        return sortedSupportedPicResolutions.get(0);
    }

    /**
     * 找出最适合的预览界面分辨率
     *
     * @return
     */
    private Camera.Size findBestPreviewResolution(Camera cameraInst, int screenWidth, int screenHeight) {
        Size deviceSize = new Size(ContentUtil.getScreenWidth(context), ContentUtil.getScreenHeight(context));
        final int allPix = screenWidth * screenHeight;
        Camera.Parameters cameraParameters = cameraInst.getParameters();
        List<Camera.Size> supportedPreviewSizes = cameraParameters.getSupportedPreviewSizes(); // 至少会返回一个值
        StringBuilder PreviewResolutionSb = new StringBuilder();
        for (Camera.Size supportedPreviewResolution : supportedPreviewSizes) {
            PreviewResolutionSb.append(supportedPreviewResolution.width).append('x')
                    .append(supportedPreviewResolution.height).append(" ");
        }

        Camera.Size defaultPictureResolution = cameraParameters.getPictureSize();

        // 排序
        List<Camera.Size> sortedSupportedPreviewResolutions = new ArrayList<Camera.Size>(
                supportedPreviewSizes);
        Collections.sort(sortedSupportedPreviewResolutions, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (aPixels >= allPix && bPixels >= allPix) {
                    if (bPixels > aPixels) {
                        return -1;
                    }
                    if (bPixels < aPixels) {
                        return 1;
                    }
                    return 0;
                } else if (aPixels < allPix && bPixels < allPix) {
                    if (bPixels < aPixels) {
                        return -1;
                    }
                    if (bPixels > aPixels) {
                        return 1;
                    }
                    return 0;
                } else {
                    if (bPixels < aPixels) {
                        return -1;
                    }
                    if (bPixels > aPixels) {
                        return 1;
                    }
                    return 0;
                }

            }
        });

        ArrayList previewList = new ArrayList<>();
        for (int i = 0; i < sortedSupportedPreviewResolutions.size(); i++) {
            previewList.add(new Size(sortedSupportedPreviewResolutions.get(i).width, sortedSupportedPreviewResolutions.get(i).height));
            //LogUtil.d("previewAllSizes: " + sortedSupportedPreviewResolutions.get(i).width + "x" + sortedSupportedPreviewResolutions.get(i).height);
        }

        // 移除不符合条件的分辨率
        double screenAspectRatio = screenWidth
                / (double) screenHeight;
        Iterator<Camera.Size> it = sortedSupportedPreviewResolutions.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewResolution = it.next();
            int width = supportedPreviewResolution.width;
            int height = supportedPreviewResolution.height;
            // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
            // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
            // 因此这里要先交换然后在比较宽高比
            boolean isCandidatePortrait = width > height;
            int maybeFlippedWidth = isCandidatePortrait ? height : width;
            int maybeFlippedHeight = isCandidatePortrait ? width : height;
            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (maybeFlippedHeight > deviceSize.getHeight() || maybeFlippedWidth > deviceSize.getWidth()) {
                it.remove();
            } else if (distortion > CameraHelper.MAX_ASPECT_DISTORTION) {
                it.remove();
            }
            // 找到与屏幕分辨率完全匹配的预览界面分辨率直接返回
            if (maybeFlippedWidth == screenWidth
                    && maybeFlippedHeight == screenHeight) {
                return supportedPreviewResolution;
            }
        }
        Collections.sort(sortedSupportedPreviewResolutions, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });
        return sortedSupportedPreviewResolutions.get(0);
    }


    /**
     * 手动相机数据
     *
     * @param hashMap
     */
    public void setCameraParams(HashMap<String, String> hashMap) {
        if (mCameraHelper == null && hashMap != null)
            return;
        if (!supports_camera2) {
            String wb = hashMap.get(Flag.WHITEBALANCE);
            if (wb != null) {
                mCameraHelper.setWhiteBalance(wb, mCameraInstance);
            }
            String iso = hashMap.get(Flag.ISO);
            if (iso != null) {
                mCameraHelper.setIso(iso, mCameraInstance);
            }
            String focus = hashMap.get(Flag.FOCUS);
            if (focus != null) {
                mCameraHelper.setFocus(mCameraInstance, focus);
            }
            String ev = hashMap.get(Flag.EV);
            if (ev != null) {
                mCameraHelper.setEV(ev, mCameraInstance);
            }
            int preview = Integer.parseInt(hashMap.get(Flag.PREVIEW));
            int picSize = Integer.parseInt(hashMap.get(Flag.PICSIZE + ""));
            if (preview != 0 && picSize != 0) {
                if (mCameraInstance == null) {
                    return;
                }
                if (!LoginedUser.getLoginedUser().isSupportCamera2()) {
                    boolean cameraType = Integer.parseInt(hashMap.get(Flag.CAMERATYPE)) == GPUImageCameraLoader.VIDE0_TYPE;
                    selectCameraSize(preview, cameraType);
                   /* if (preview != 0 && picSize != 0) {
                        mCameraHelper.setMPreViewSize(preview + "", picSize + "");
                    }*/
                }
            }
            String flash = hashMap.get(Flag.FLASH);
            if (flash != null) {
                boolean flashType = Boolean.parseBoolean(flash);
                mCameraHelper.setFlash2(flashType, mCameraInstance);
                //mCameraHelper.setFlash(flashType, mCameraInstance);
            }
            mCameraHelper.setCameraParms(mCameraInstance);
        } else {
            String wb = hashMap.get(Flag.WHITEBALANCE);
            if (wb != null) {
                mCameraHelper.setWhiteBalance(Integer.valueOf(wb));
            }
            String iso = hashMap.get(Flag.ISO);
            if (iso != null) {
                mCameraHelper.setIso(Integer.valueOf(iso));
            }


            String focus = hashMap.get(Flag.FOCUS);
            if (focus != null) {
                if (focus.equals(Flag.TYPE_AUTO)) {
                    mCameraHelper.setFocusDistance(true, 0);
                } else {
                    mCameraHelper.setFocusDistance(false, Float.valueOf(focus));
                }
            }
            String sec = hashMap.get(Flag.SEC);
            if (sec != null) {
                mCameraHelper.setSec(Long.valueOf(sec));
            }
            if (hashMap != null) {
                int preview = Integer.parseInt(hashMap.get(Flag.PREVIEW));
                int picSize = Integer.parseInt(hashMap.get(Flag.PICSIZE + ""));
                if (preview != 0 && picSize != 0) {
                    mCameraHelper.setMPreViewSize(preview + "", picSize + "");
                }
            }
        }
        //setPreviewCallback();
    }

    private void selectCameraSize(int preview, boolean isVideo) {
        mCameraHelper.setCurrentType(isVideo);
        if (mCameraInstance == null) {
            mCameraInstance = getCameraInstance(mCurrentCameraId);
        }
        try {
            cameraSizeModel = CameraSizeModel.getCameraSizeModel(mCameraInstance.getParameters());
        } catch (Exception e) {
            e.printStackTrace();
            mCameraInstance.release();
            mCameraInstance = getCameraInstance(mCurrentCameraId);
            cameraSizeModel = CameraSizeModel.getCameraSizeModel(mCameraInstance.getParameters());
        }
        adapterSize = cameraSizeModel.getCameraSizeData(preview).getCameraSizeBean(/*isVideo ? Flag.TYPE_VIDEO : */Flag.TYPE_PIC).getselectSize(true,/*isVideo ? CameraStatus.getCameraStatus().getVideoSize() : */CameraStatus.getCameraStatus().getPictureSize());
        previewSize = cameraSizeModel.getCameraSizeData(preview).getCameraSizeBean(/*isVideo ? Flag.TYPE_VIDEO : Flag.TYPE_PIC*/Flag.TYPE_VIDEO).getselectSize(true, isVideo ? CameraStatus.getCameraStatus().getVideoSize() : CameraStatus.getCameraStatus().getPictureSize());
        mCameraHelper.setMPreViewSize2(previewSize, adapterSize);
    }

    Size adapterSize, previewSize;


    public void releaseCamera() {
        if (mCameraInstance != null) {
            try {
                mCameraInstance.stopPreview();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            mCameraInstance.setPreviewCallback(null);
            mCameraInstance.release();
            mCameraInstance = null;

        }
        mGPUImage.releaseCamera();
    }

    /**
     * 拍照
     */
    public void takePhoto() {
        if (!supports_camera2) {
            try {
                getmCameraInstance().takePicture(shutterCallback, rawCallback, jpegCallback);
            } catch (Throwable t) {
                t.printStackTrace();
                if (gpuImageCamreraListener != null) {
                    gpuImageCamreraListener.takePhonoErr();
                }
                try {
                    getmCameraInstance().startPreview();
                } catch (RuntimeException e) {

                }
            }
        } else {
            mCameraHelper.takePicture(new CameraHelperObj.TackPhotoCallback() {
                @Override
                public void tackPhotoSuccess(String photoPath) {

                }

                @Override
                public void tackPhotoError(Exception e) {
                }

                @Override
                public void getPhotoSuccess(Bitmap bitmap, CameraParm cameraParm) {
                    gpuImageCamreraListener.takePhotoListenr(bitmap, currentRatio, cameraParm);
                }

                @Override
                public void getPhotoByteSuccess(byte[] data, CameraParm cameraParm) {
                    gpuImageCamreraListener.takePhotoByteListener(data, currentRatio, cameraParm);
                }
            });
        }
    }


    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };
    private Bitmap savebit;
    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            if (isAiCamera) {
                if (gpuImagePreviewCallback != null) {
                    gpuImagePreviewCallback.onTakePhoto(data, camera);
                    camera.startPreview();
                }
            } else {
                CameraFileUtil.getOutPutData(data);
                savebit = BitmapFactory.decodeByteArray(data, 0, data.length);
                camera.startPreview();
                float length = camera.getParameters().getFocalLength();
                //拍照数据
                CameraParm cameraParm = new CameraParm();
                if (length != 0) {
                    cameraParm.setFocalLength(String.valueOf(length));
                }
                String miso = camera.getParameters().get("iso");
                String iso = camera.getParameters().get("cur-iso");
                if (iso != null) {
                    if (!miso.equals("auto")) {
                        iso = miso.substring(3);
                    } else {

                    }
                    cameraParm.setIso(iso);
                }
                String wb = camera.getParameters().getWhiteBalance();
                if (wb != null) {
                    cameraParm.setWhiteBalance(wb);
                }
                String et = camera.getParameters().get("cur-exposure-time");
                if (et != null) {
                    cameraParm.setSec(et);
                }
                Log.d("cameraParams:", "焦距:" + length + " " + "iso:" + iso + " 白平衡:" + wb + " 曝光时间:" + et + " " + miso + " ");
                if (gpuImageCamreraListener != null) {
                    gpuImageCamreraListener.takePhotoListenr(savebit, currentRatio, cameraParm);
                }
            }
        }
    };


    public Bitmap getGPUPhoto(Bitmap bitmap) {
        Bitmap mGPUBitmap = null;
        if (mGPUImage != null) {
            mGPUBitmap = mGPUImage.getBitmapWithFilterApplied(bitmap);
            mGPUImage.requestRender();
        }
        return mGPUBitmap;
    }

    @Override
    public void setEv(int ev) {
        mCameraHelper.setEV2(ev, mCameraInstance);
    }

    @Override
    public void setFlash(boolean isFlashOn) {
        mCameraHelper.setFlash2(isFlashOn, mCameraInstance);
    }

    public void switchFilterTo(final GPUImageFilter filter) {
        if (filter != null && mGPUImage != null) {
            mFilter = filter;
            mGPUImage.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
            mGPUImage.requestRender();
        }
    }

    public GPUImage getGpuimage() {
        return this.mGPUImage;
    }


    @Override
    public void setCameraLoaderListener(GPUImageCamerLoaderBase.CameraLoaderListener cameraLoaderListener) {
        this.cameraLoaderListener = cameraLoaderListener;
    }


    @Override
    public GPUImageCamerLoaderBase.GPUImageCamreraListener getGpuImageCamreraListener() {
        return gpuImageCamreraListener;
    }

    @Override
    public void setGpuImageCamreraListener(GPUImageCamerLoaderBase.GPUImageCamreraListener gpuImageCamreraListener) {
        this.gpuImageCamreraListener = gpuImageCamreraListener;
    }

    class ChangeOrientationHandler extends Handler {
        public ChangeOrientationHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 888) {
                int orientation = msg.arg1;
                if (orientation != -1 && gpuImageCamreraListener != null) {
                    gpuImageCamreraListener.getOrientation(orientation);
                }
                if (orientation > 45 && orientation < 135) {
                    currentRatio = RIGHT;
                } else if (orientation > 135 && orientation < 225) {
                    currentRatio = TOP;
                } else if (orientation > 225 && orientation < 315) {
                    currentRatio = LEFT;
                } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                    currentRatio = BOTTOM;
                }
                if (mCameraHelper != null)
                    mCameraHelper.setCurrentRatio(currentRatio);
            } else if (msg.what == 999) {
                float xAngle = msg.getData().getFloat("xAngle");
                float yAngle = msg.getData().getFloat("yAngle");
                float zAngle = msg.getData().getFloat("zAngle");
                if (null != gpuImageCamreraListener) {
                    gpuImageCamreraListener.getyAngle(xAngle, yAngle, zAngle);
                }
            } else if (msg.what == 777) {
                float xAngle = msg.getData().getFloat("xAngle");
                float yAngle = msg.getData().getFloat("yAngle");
                float zAngle = msg.getData().getFloat("zAngle");

                double vector = Math.sqrt(Math.pow(xAngle, 2)
                        + Math.pow(yAngle, 2)
                        + Math.pow(zAngle, 2));

                float X_AXIS = (float) (xAngle / vector);
                float Y_AXIS = (float) (yAngle / vector);

                float xDegrees = (float) Math.toDegrees(Math.atan2(X_AXIS, Y_AXIS));
                float yDegrees = Math.round(Math.toDegrees(Math.atan2(Y_AXIS, X_AXIS)));
                if (null != gpuImageCamreraListener) {
                    gpuImageCamreraListener.getAngle(xDegrees, yDegrees);
                }
            }
            super.handleMessage(msg);
        }
    }

    public void readyStart(final GPUImageCameraLoader.VidioInfer vidioInfer, GPUImageFilter gpuImageFilter) {
        this.vidioInfer = vidioInfer;
        CameraFileUtil.isNeedFile(Flag.APP_CAMERAL);
        File file = new File(Flag.APP_CAMERAL, System.currentTimeMillis() + ".mp4");
        vidioUrl = file.getAbsolutePath();
        bitmapUrl = "";
        isVidioComplete = false;


        /**
         * 视频、音频 开始与结束录制的回调
         */
        BaseMediaEncoderRunable.MediaEncoderListener mMediaEncoderListener = new BaseMediaEncoderRunable.MediaEncoderListener() {

            @Override
            public void onPrepared(final BaseMediaEncoderRunable encoder) {
                if (encoder instanceof MediaVideoEncoderRunable) {
                    mGPUImage.setVideoEncoder((MediaVideoEncoderRunable) encoder);
                }
            }


            @Override
            public void onStopped(final BaseMediaEncoderRunable encoder) {
                if (encoder instanceof MediaVideoEncoderRunable) {
                    mGPUImage.setVideoEncoder(null);
                }
                final Camera.Size size = mCameraInstance.getParameters().getPreviewSize();
                final Size cutSize;
                Point startPoint = new Point();
                startPoint.y = (Math.max(size.width, size.height) - Math.min(size.width, size.height)) / 2;
                startPoint.x = 0;
                final CameraStatus.Size currentSelect_size = CameraStatus.getCameraStatus().getVideoRatio();
                switch (currentSelect_size) {
                    case TYPE_11:
                        cutSize = new Size(Math.min(size.width, size.height), Math.min(size.width, size.height));
                        break;
                    default:
                        cutSize = new Size(size.width, size.height);
                        break;
                }
                String vidioUrlFin;
                //16：9，不进行裁剪
                if (cutSize.getWidth() != size.width || cutSize.getHeight() != size.height) {
                    VideoEditor videoEditor = new VideoEditor();
                    vidioUrlFin = videoEditor.executeCropVideoFrame(vidioUrl, cutSize.getHeight(), cutSize.getWidth(), startPoint.x, startPoint.y);
                    //裁剪完成，删除之前的视频文件
                    ThreadPoolUtil.execute(new Runnable() {
                        @Override
                        public void run() {
                            File file = new File(vidioUrl);
                            file.delete();
                        }
                    });
                } else {
                    vidioUrlFin = vidioUrl;

                }
                //生成视屏封面图片
                VideoEditor vd = new VideoEditor();
                Bitmap bitmap = vd.createVideoThumbnail(vidioUrlFin, 0, 0);
                if (bitmap != null) {
                    try {
                        bitmapUrl = CameraFileUtil.cacheSaveBitmap(bitmap, System.currentTimeMillis() + "", false);
                    } catch (IOException e) {
                        bitmapUrl = "";
                    }
                }
                //录制视频结束回调
                if (vidioInfer != null) {
                    vidioInfer.isReadyComplete(vidioUrlFin, bitmapUrl);
                }
            }
        };
        try {

            try {
                mMediaMuxerManager = new SohuMediaMuxerManager(vidioUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            MediaVideoEncoderRunable mediaVideoEncoderRunable = new MediaVideoEncoderRunable(mMediaMuxerManager, mMediaEncoderListener, mGPUImage.getFrameWidth(), mGPUImage.getFrameHeight(), gpuImageFilter);
            mediaVideoEncoderRunable.setMPreViewSize(mCameraInstance.getParameters().getPreviewSize().height, mCameraInstance.getParameters().getPreviewSize().width);
            mediaVideoEncoderRunable.setRotation(currentRatio);
            mMediaMuxerManager.prepare();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startRecoder() {
        if (!supports_camera2) {
            // mstartRecord();
            //mGPUImage.getCurrentGPUBitmap();
            setPreviewCallback();
            mMediaMuxerManager.startRecording();
        } else {
            mCameraHelper.startRecordingVideo();
        }
    }


    public void endRecording() {
        if (!supports_camera2) {
            if (mMediaMuxerManager != null) {
                mMediaMuxerManager.stopRecording();
                mMediaMuxerManager = null;
            }
            // mstopRecord();
            if (isVidioComplete) {
            } else {
                isVidioComplete = true;
            }
            cancelPreviewCallback();
        } else {
            mCameraHelper.stopRecordingVideo();
        }
    }

    @Override
    public void pauseRecording() {
        // setStopDate();
        if (mMediaMuxerManager != null) {
            mMediaMuxerManager.pauseRecording();
        }
    }

    @Override
    public void ResumeRecording() {
        // mstartRecord();
        if (mMediaMuxerManager != null) {
            mMediaMuxerManager.resumeRecording();
        }
    }

    public void setCropRect(Rect rect) {
        this.rect = rect;
    }

    public Rect getCropRect() {
        return this.rect;
    }

    public CameraSurfaceHandler getCameraSurfaceHandler() {
        return cameraSurfaceHandler;
    }

    private CameraSurfaceHandler cameraSurfaceHandler;
    private BizPreviewCallback previewCallback;

    public void handleDecode(Result rawResult, Bundle bundle) {
        if (rawResult.getText() != null && !TextUtils.isEmpty(rawResult.getText())) {
            getQRResultListener.onGetQRResult(rawResult.getText());
        }
    }

    public synchronized void requestPreviewFrame(Handler handler, int message) {
        if (mCameraInstance != null && previewing) {
            previewCallback.setHandler(handler, message);
            //mCameraInstance.setPreviewCallbackWithBuffer(previewCallback);
            mCameraInstance.setOneShotPreviewCallback(previewCallback);
        }
    }

    public void setQRCode(boolean startQRCode) {
        if (startQRCode) {
            previewing = true;
            if (previewCallback == null) {
                previewCallback = new BizPreviewCallback(getPreviewSize().width, getPreviewSize().height);
            }
            if (cameraSurfaceHandler == null) {
                cameraSurfaceHandler = new CameraSurfaceHandler(this);
                cameraSurfaceHandler.init(DecodeFormatManager.ALL_MODE);
            } else {
                cameraSurfaceHandler.restartErWeima();
            }
        } else {
            if (mCameraInstance != null) {
                if (previewCallback != null) {
                    previewCallback.setHandler(null, 0);
                }
                previewing = false;
                if (cameraSurfaceHandler != null) {
                    cameraSurfaceHandler.quitSynchronously();
                    cameraSurfaceHandler = null;
                }
            }
        }
    }

    @Override
    public void setQRResultListener(GetQRResultListener getQRResultListener) {
        this.getQRResultListener = getQRResultListener;
    }


    @Override
    public void setGetParamsListener(GPUImageCamerLoaderBase.GetParamsListener getParamsListener) {
        this.getParamsListener = getParamsListener;
    }

    @Override
    public void setCameraDataListenerListener(CameraDataListener cameraDataListener) {
        this.cameraDataListener = cameraDataListener;
    }


    @Override
    public String getVidioUrl() {
        if (!supports_camera2) {
            return vidioUrl;
        } else {
            return mCameraHelper.getRecordUrl();
        }
    }

    @Override
    public String getVidioBitmapUrl() {
        return bitmapUrl;
    }


    public interface VidioInfer {
        void isReadyComplete(String videoUrl, String VideoCoverUrl);

    }

    public void setRecordSize(Size size) {
        mCameraHelper.setRecordSize(size);
    }

    public ArrayList<Size> getAllSize() {
        return mCameraHelper.getAllSize();
    }

    public Camera.Parameters getParameters() {
        return mCameraHelper.getCurrentParameters();
    }


    public void lockAllCameraParam() {
        if (mCameraHelper != null)
            mCameraHelper.lockAllCameraParam();
    }

    public void unlockAllCameraParam() {
        if (mCameraHelper != null) {
            mCameraHelper.unlockAllCameraParam();
        }
    }

    public void lockPresetCameraParam(String sec, String iso, String wb, String focus) {
        mCameraHelper.lockPresetCameraParam(sec, iso, wb, focus);
    }

    public void lockPresetCameraParam(String sec, String iso, String wb, String focus, String mRed, String mGreenEven, String mGreenOdd, String mBlue) {
        mCameraHelper.lockPresetCameraParam(sec, iso, wb, focus, mRed, mGreenEven, mGreenOdd, mBlue);
    }

    @Override
    public int getCameraId() {
        return mCurrentCameraId;
    }

    public enum CameraOpenState {
        CAMERAOPENSTATE_CLOSED, // have yet to attempt to open the camera (either at all, or since the camera was closed)
        CAMERAOPENSTATE_OPENING, // the camera is currently being opened (on a background thread)
        CAMERAOPENSTATE_OPENED, // either the camera is open (if camera_controller!=null) or we failed to open the camera (if camera_controller==null)
        CAMERAOPENSTATE_CLOSING // the camera is currently being closed (on a background thread)
    }

    CameraOpenState camera_open_state = CameraOpenState.CAMERAOPENSTATE_CLOSED;

    public CameraOpenState getCamera_open_state() {
        return camera_open_state;
    }


    /**
     * 设置回调
     */

    public void setPreviewCallback() {
        getmCameraInstance().setPreviewCallback(this);
    }

    public void cancelPreviewCallback() {
        if (getmCameraInstance() != null)
            getmCameraInstance().setPreviewCallback(null);
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (gpuImagePreviewCallback != null)
            gpuImagePreviewCallback.onPreViewFrame(data, camera);
    }

    public interface GpuImagePreviewCallback {
        void onPreViewFrame(byte[] data, Camera camera);

        void onTakePhoto(byte[] data, Camera camera);
    }

    GpuImagePreviewCallback gpuImagePreviewCallback;

    /**
     * @param gpuimagePreviewCallback
     */
    public void setGpuimagePreviewCallback(GpuImagePreviewCallback gpuimagePreviewCallback, boolean isAiCamera) {
        this.gpuImagePreviewCallback = gpuimagePreviewCallback;
        this.isAiCamera = isAiCamera;
    }
}
