package com.bcnetech.bizcamerlibrary.camera.data;

import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.RggbChannelVector;
import android.location.Location;

/**
 * Created by yhf on 2018/4/18.
 */

public class CameraSettings {

    private static CameraSettings cameraSettings=null;

    private CameraSettings(){

    }

    public static CameraSettings getCameraSettings(){
        if(null==cameraSettings){
            cameraSettings=new CameraSettings();
        }
        return cameraSettings;
    }

    public static synchronized void onDestroy() {
        cameraSettings = null;
    }

    // keys that we need to store, to pass to the stillBuilder, but doesn't need to be passed to previewBuilder (should set sensible defaults)
    public int rotation = 0;
    public Location location = null;
    public byte jpeg_quality = 90;
    //使用字段key控制
    // keys that we have passed to the previewBuilder, that we need to store to also pass to the stillBuilder (should set sensible defaults, or use a has_ boolean if we don't want to set a default)
    public int white_balance = CameraMetadata.CONTROL_AWB_MODE_AUTO;
    public boolean has_iso = false;

    public int iso = 200;
    public int valueWB=0;
    public RggbChannelVector rggbChannelVector;
    //曝光补偿
    public int valueAE=0;

    public long exposure_time =  1000000000l / 30;
    public boolean has_ae_exposure_compensation = false;
    public int ae_exposure_compensation = 0;
    public boolean has_af_mode = false;
    public int af_mode = CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE;
    public float focus_distance = 0.0f; // actual value passed to camera device (set to 0.0 if in infinity mode)
    public boolean misAutoCamera = true;
    public boolean misAutoFocus = true;
    public boolean misFlashOn = false;

    public MeteringRectangle[] af_regions; // no need for has_scalar_crop_region, as we can set to null instead


    public void setupBuilder(CaptureRequest.Builder builder, boolean is_still) {

        setExposureCompensation(builder);
        setFocusMode(builder);
        setFocusDistance(builder);

        setAEmodeOn(builder);
        setExposure_time(builder);
        setIso(builder);

        if (is_still) {
            if (location != null) {
                builder.set(CaptureRequest.JPEG_GPS_LOCATION, location);
            }
            builder.set(CaptureRequest.JPEG_ORIENTATION, rotation);
            builder.set(CaptureRequest.JPEG_QUALITY, jpeg_quality);
        }
    }




    /**
     * 设置AE模式
     *
     * @param builder
     */
    public void setAEmodeOn(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
//            builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
    }



    //设置曝光补偿
    public boolean setExposureCompensation(CaptureRequest.Builder builder) {
        if (!has_ae_exposure_compensation)
            return false;
        if (has_iso) {
            return false;
        }
        if (builder.get(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION) == null || ae_exposure_compensation != builder.get(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION)) {
            builder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, ae_exposure_compensation);
            return true;
        }
        return false;
    }

    //设置对焦模式
    public void setFocusMode(CaptureRequest.Builder builder) {
        if (has_af_mode) {
            builder.set(CaptureRequest.CONTROL_AF_MODE, af_mode);
        }
    }


    //SENSOR_EXPOSURE_TIME
    //这种控制只有在android.control.aeMode or android.control.mode
    //模式设置为OFF;否则，自动曝光算法将覆盖这个值。
    //设置曝光时间
    public void setExposure_time(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_OFF);
        builder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposure_time);
        //  builder.set(CaptureRequest.SENSOR_SENSITIVITY, default_iso);
    }

    /**
     * 设置闪光灯
     * @param builder
     */
    public void setFlash(CaptureRequest.Builder builder){
        if (misFlashOn) {
            builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
        }else{
            builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
        }
    }

    //SENSOR_SENSITIVITY
    //这种控制只有在android.control.aeMode or android.control.mode
    //模式设置为OFF;否则，自动曝光算法将覆盖这个值。
    //iso
    public void setIso(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_OFF);
        //  builder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, default_et);
        builder.set(CaptureRequest.SENSOR_SENSITIVITY, iso);
    }

    public void setWB(CaptureRequest.Builder builder) {
        RggbChannelVector rggb = colorTemperature(valueWB);
        builder.set(CaptureRequest.CONTROL_AWB_MODE, white_balance);
        builder.set(CaptureRequest.COLOR_CORRECTION_MODE, CaptureRequest.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX);
        builder.set(CaptureRequest.COLOR_CORRECTION_GAINS, rggb);
    }


    public void setWB(CaptureRequest.Builder builder,RggbChannelVector rggb) {
       // RggbChannelVector rggb = colorTemperature(valueWB);
        builder.set(CaptureRequest.CONTROL_AWB_MODE, white_balance);
        builder.set(CaptureRequest.COLOR_CORRECTION_MODE, CaptureRequest.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX);
        builder.set(CaptureRequest.COLOR_CORRECTION_GAINS, rggb);
    }
    public void setWBRGB(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_AWB_MODE, white_balance);
        builder.set(CaptureRequest.COLOR_CORRECTION_MODE, CaptureRequest.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX);
        builder.set(CaptureRequest.COLOR_CORRECTION_GAINS, rggbChannelVector);
    }

    //设置焦距
    //LENS_FOCUS_DISTANCE；调节焦距
    public void setFocusDistance(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);
        builder.set(CaptureRequest.LENS_FOCUS_DISTANCE, focus_distance);
    }

    /**
     * 调整自动曝光(AE)目标图像亮度。
     * 这个请求在android.control.aeMode!= OFF生效。
     * 或者即使在手动模式 android.control.aeLock == true。操作时也会生效。
     *
     */
    public void setAe(CaptureRequest.Builder builder){
        builder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, valueAE);

    }


    //色温从低到高逐渐由暖色到冷色,白平衡调整需要反之
    public RggbChannelVector colorTemperature(int whiteBalance) {


        float temperature = whiteBalance / 100.0f;
        float red;
        float green;
        float blue;

        //Calculate red
        if (temperature <= 66)
            red = 255;
        else {
            red = temperature - 60;
            red = (float) (329.698727446 * (Math.pow((double) red, -0.1332047592)));
            //red = (float) (351.97690566805693+0.114206453784165*red+(-40.25366309332127*Math.log(red)));
            if (red < 0)
                red = 0;
            if (red > 255)
                red = 255;
        }


        //Calculate green
        if (temperature <= 66) {
            green = temperature;
            green = (float) (99.4708025861 * Math.log(green) - 161.1195681661);
            // green = (float) ( -155.25485562709179+(-0.44596950469579133*green)+(104.49216199393888*Math.log(green)));
            if (green < 0)
                green = 0;
            if (green > 255)
                green = 255;
        } else {
            green = temperature - 60;
            green = (float) (288.1221695283 * (Math.pow((double) green, -0.0755148492)));
            //green = (float) (  325.4494125711974+(0.07943456536662342*green)+( -28.0852963507957*Math.log(green)));
            if (green < 0)
                green = 0;
            if (green > 255)
                green = 255;
        }

        //calculate blue
        if (temperature >= 66)
            blue = 255;
        else if (temperature <= 19)
            blue = 0;
        else {
            blue = temperature - 10;
            blue = (float) (138.5177312231 * Math.log(blue) - 305.0447927307);
            // blue = (float) (  -254.76935184120902+(0.8274096064007395*blue)+( 115.67994401066147*Math.log(blue)));;
            if (blue < 0)
                blue = 0;
            if (blue > 255)
                blue = 255;
        }

        return new RggbChannelVector((red / 255) * 2, (green / 255), (green / 255), (blue / 255) * 2);
    }

    @Override
    public String toString() {
        return "CameraSettings{" +
                "rotation=" + rotation +
                ", location=" + location +
                ", jpeg_quality=" + jpeg_quality +
                ", white_balance=" + white_balance +
                ", has_iso=" + has_iso +
                ", iso=" + iso +
                ", valueWB=" + valueWB +
                ", rggbChannelVector=" + rggbChannelVector +
                ", valueAE=" + valueAE +
                ", exposure_time=" + exposure_time +
                ", has_ae_exposure_compensation=" + has_ae_exposure_compensation +
                ", ae_exposure_compensation=" + ae_exposure_compensation +
                ", has_af_mode=" + has_af_mode +
                ", af_mode=" + af_mode +
                ", focus_distance=" + focus_distance +
                ", misAutoCamera=" + misAutoCamera +
                ", misAutoFocus=" + misAutoFocus +
                ", misFlashOn=" + misFlashOn +
                '}';
    }
}
