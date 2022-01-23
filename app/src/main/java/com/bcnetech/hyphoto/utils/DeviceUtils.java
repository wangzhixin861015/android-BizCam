package com.bcnetech.hyphoto.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.os.Build;

/**
 * Created by zs on 2016/7/7.
 */
public class DeviceUtils {
    /*
     * 获取应用名
     */
    public static String getVersionName(Context context) {
        String versionName = null;
        try {
            //获取包管理者
            PackageManager pm = context.getPackageManager();
            //获取packageInfo
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            //获取versionName
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return versionName;
    }

    /*
     * 获取应用版本
     */
    public static int getVersionCode(Context context) {

        int versionCode = 0;
        try {
            //获取包管理者
            PackageManager pm = context.getPackageManager();
            //获取packageInfo
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            //获取versionCode
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getDeviceBrand() {
        try {
            BuildProperties prop = BuildProperties.newInstance();
            String str = prop.getProperty("ro.miui.ui.version.name") + ":" + prop.getProperty("ro.miui.ui.version.code");
            return Build.MANUFACTURER;
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isCamera2Support(Activity activity) {
        boolean supports_camera2 = false;
        int back_CameraID = 0;
        //版本大于19
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supports_camera2 = true;
            //获取相机总数量大于0
            if (getNumberOfCameras(activity) == 0) {
                supports_camera2 = false;
            }
            //获取后置相机id
            for (int i = 0; i < getNumberOfCameras(activity) && supports_camera2; i++) {
                android.hardware.Camera.CameraInfo info =
                        new android.hardware.Camera.CameraInfo();
                android.hardware.Camera.getCameraInfo(i, info);

                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    back_CameraID = i;
                    break;
                }
            }
            if (!allowCamera2Support(activity, back_CameraID)) {
                supports_camera2 = false;
            }
        }
        return supports_camera2;
    }

    /**
     * 获取设备摄像头个数
     *
     * @return
     */
    public static int getNumberOfCameras(Activity activity) {
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            return manager.getCameraIdList().length;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (AssertionError e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean allowCamera2Support(Activity activity, int cameraId) {
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            //相机支持程度:在CameraMetadata中定义，其中FULL值为1，LEGACY值为2，LIMITED值为0，其支持程度为FULL > LIMITED > LEGACY。
            String cameraIdS = manager.getCameraIdList()[Integer.valueOf(cameraId)];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIdS);
            int support = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            /*if( support == CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY )
                Log.d(TAG, "Camera " + cameraId + " has LEGACY Camera2 support");
            else if( support == CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED )
                Log.d(TAG, "Camera " + cameraId + " has LIMITED Camera2 support");
            else if( support == CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL )
                Log.d(TAG, "Camera " + cameraId + " has FULL Camera2 support");
            else
                Log.d(TAG, "Camera " + cameraId + " has unknown Camera2 support?!");
           */
            //support在limit和full时表示支持camera2
            return support == CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL||support==3;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

}
