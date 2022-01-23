package com.bcnetech.hyphoto.presenter.iview;

import android.opengl.GLSurfaceView;
import android.os.Handler;

import com.bcnetech.hyphoto.data.CameraParamType;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCamerLoaderBase;

import java.util.HashMap;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by wenbin on 2017/2/24.
 */

public interface ISurfViewCameraView extends BaseIView {
    List getItemList();
    void initRotationAnim(int currentRotation,int rotation);
    void setWaitPicShow(boolean b);
    void showToast(ImageData imageData, String str);
    void initRotationSubline(int currentRotation,float xAngle,float yAngle);

    void rotationSubline(float xDegrees, float yDegrees,float rotation);

    void inTakePhoto();
    void outTakePhoto();

    void inVedio();
    void outVedio();

    //预设参数动画
    void inPreset();
    void outPreset();

    //专业模式动画
    void inPro();
    void outPro();
    void setCameraParams(HashMap<String, String> hashMap);
    void lockAllCameraParam();
    void unlockAllCameraParam();

    void outVedioInPreset();
    void inVedioOutPreset();

    void disMissBlueView();



    //设置蓝牙名称
    void setConnectBluetoothName(String name);



    //蓝牙连接中断
    void setErrorConnectPresetSelect();

    //设置蓝牙状态
    void setBlueTouchType(int type);

    //设置滤镜
    void surfviewSwitchFilterTo(GPUImageFilter gpuImageFilter);

    //设置滑动条数值
    void setSeekBarNum(int num);
    //设置滑动条数值
    void setProgressAndDefaultPoint(int num,float defaultPoint,CameraParamType cameraParamType);

    Handler getHandler();

    void setXYZRenderer(GLSurfaceView.Renderer renderer);

    void onRecodeShow(int w,int h);

    boolean isShowRect();

    GPUImage getgpuimage();

    //获取相机
    GPUImageCamerLoaderBase getCamera();


    void takePhotoAnimation();

    void startQueryByShowType();

    void setCameraType(int cameraType);

    int getCameraType();

    void setExposure(int ae);

    //设置转盘状态
    void setMotorStatus(int status);
}
