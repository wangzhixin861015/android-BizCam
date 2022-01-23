package com.bcnetech.hyphoto.utils;

import com.bcnetech.hyphoto.data.CameraParamType;
import com.bcnetech.bizcamerlibrary.camera.utils.CameraUtils;

/**
 * Created by wangzhixin on 2018/3/27.
 */

public class CameraParamUtil {

    public static String getIso(CameraParamType cameraParamType, int size){
        long min = cameraParamType.getMin();
        long max = cameraParamType.getMax();
        double frac = size/(double)100;
        long iso = (long) CameraUtils.exponentialScaling(frac, min, max);
        if( iso < min )
            iso = min;
        else if( iso > max )
            iso = max;
        return iso+"";
    }

    public static String getExposureTime(CameraParamType cameraParamType,int size){
        long min = cameraParamType.getMin();
        long max = cameraParamType.getMax();
        double frac = (size)/(double)100;
        long exposure_time = (long) CameraUtils.exponentialScaling(frac, min, max);
        return CameraUtils.getExposureTimeString(exposure_time);
    }

    public static String getWHITEBALANCE(CameraParamType cameraParamType,int size){
        long min = cameraParamType.getMin();
        long max = cameraParamType.getMax();
        double frac = size/(double)100;
        int whitebalance = (int)CameraUtils.exponentialScaling(frac, min, max);
        return whitebalance+"";
    }
    public static String getFOCUS(CameraParamType cameraParamType,int size){
        float min = cameraParamType.getFocusMin();

        double frac = size/(double)100;
        double scaling = CameraUtils.seekbarScaling(frac);
        float focus_distance = (float)(scaling *min);

        return CameraUtils.getFocusDistance(focus_distance);
    }

    public static int setProgressSeekbarExponential( double min_value, double max_value, double value) {
        double frac = exponentialScalingInverse(value, min_value, max_value);
        int new_value = (int)(frac*100 + 0.5); // add 0.5 for rounding
        if( new_value < 0 )
            new_value = 0;
        else if( new_value > 100 )
            new_value = 100;
        return new_value;
    }


    public static int getFOCUSInverse(double min_value, double max_value, double value) {
        double scaling = (value - min_value)/(max_value - min_value);
        double frac = seekbarScalingInverse(scaling);
        int new_value = (int)(frac*100 + 0.5); // add 0.5 for rounding
        if( new_value < 0 )
            new_value = 0;
        else if( new_value > 100 )
            new_value = 100;
        return new_value;
    }

    private static double exponentialScalingInverse(double value, double min, double max) {
        double s = Math.log(max / min);
        return Math.log(value / min) / s;
    }

    private static double seekbarScalingInverse(double scaling) {
        return Math.log(99.0*scaling + 1.0) / Math.log(100.0);
    }

}
