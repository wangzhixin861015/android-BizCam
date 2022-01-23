/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bcnetech.bizcamerlibrary.camera.utils;

import android.hardware.Camera;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Camera-related utility functions.
 */
public class CameraUtils {
    private static final String TAG = "CameraUtils";
    private  static DecimalFormat decimal_format_1dp = new DecimalFormat("#.#");
    private static DecimalFormat decimal_format_2dp = new DecimalFormat("#.##");
    /**
     * Attempts to find a preview size that matches the provided width and height (which
     * specify the dimensions of the encoded video).  If it fails to find a match it just
     * uses the default preview size for video.
     * <p>
     * TODO: should do a best-fit match, e.g.
     * https://github.com/commonsguy/cwac-camera/blob/master/camera/src/com/commonsware/cwac/camera/CameraUtils.java
     */
    public static void choosePreviewSize(Camera.Parameters parms, int width, int height) {
        // We should make sure that the requested MPEG size is less than the preferred
        // size, and has the same aspect ratio.
        Camera.Size ppsfv = parms.getPreferredPreviewSizeForVideo();
        if (ppsfv != null) {
            Log.d(TAG, "Camera preferred preview size for video is " +
                    ppsfv.width + "x" + ppsfv.height);
        }

        for (Camera.Size size : parms.getSupportedPreviewSizes()) {
            Log.d(TAG, "supported: " + size.width + "x" + size.height);
        }

        for (Camera.Size size : parms.getSupportedPreviewSizes()) {
            if (size.width == width && size.height == height) {
                parms.setPreviewSize(width, height);
                return;
            }
        }

        Log.w(TAG, "Unable to set preview size to " + width + "x" + height);
        if (ppsfv != null) {
            parms.setPreviewSize(ppsfv.width, ppsfv.height);
        }
        // else use whatever the default size is
    }

    public  static boolean supportSquareType(Camera.Parameters parms){
        for (Camera.Size size : parms.getSupportedPreviewSizes()) {
          if (size.width == size.height)
              return true;
        }
        return false;
    }

    /**
     * Attempts to find a fixed preview frame rate that matches the desired frame rate.
     * <p>
     * It doesn't seem like there's a great deal of flexibility here.
     * <p>
     * TODO: follow the recipe from http://stackoverflow.com/questions/22639336/#22645327
     *
     * @return The expected frame rate, in thousands of frames per second.
     */
    public static int chooseFixedPreviewFps(Camera.Parameters parms, int desiredThousandFps) {
        List<int[]> supported = parms.getSupportedPreviewFpsRange();

        for (int[] entry : supported) {
            //Log.d(TAG, "entry: " + entry[0] + " - " + entry[1]);
            if ((entry[0] == entry[1]) && (entry[0] == desiredThousandFps)) {
                parms.setPreviewFpsRange(entry[0], entry[1]);
                return entry[0];
            }
        }

        int[] tmp = new int[2];
        parms.getPreviewFpsRange(tmp);
        int guess;
        if (tmp[0] == tmp[1]) {
            guess = tmp[0];
        } else {
            guess = tmp[1] / 2;     // shrug
        }

        Log.d(TAG, "Couldn't find match for " + desiredThousandFps + ", using " + guess);
        return guess;
    }

    public static String getExposureTimeString(long exposure_time) {


        double exposure_time_s = exposure_time/1000000000.0;
        String string;
        if( exposure_time >= 500000000 ) {
            // show exposure times of more than 0.5s directly
            string = decimal_format_1dp.format(exposure_time_s) + "s";
        }
        else {
            double exposure_time_r = 1.0/exposure_time_s;
            string = " 1/" + decimal_format_1dp.format(exposure_time_r) + "s";
        }
        return string;
    }

    public static double exponentialScaling(double frac, double min, double max) {
		/* We use S(frac) = A * e^(s * frac)
		 * We want S(0) = min, S(1) = max
		 * So A = min
		 * and Ae^s = max
		 * => s = ln(max/min)
		 */
        double s = Math.log(max / min);
        return min * Math.exp(s * frac);
    }

    public static double seekbarScaling(double frac) {
        // For various seekbars, we want to use a non-linear scaling, so user has more control over smaller values
        return (Math.pow(100.0, frac) - 1.0) / 99.0;
    }



    public static String getFocusDistance(float new_focus_distance){
        String focus_distance_s;
        if( new_focus_distance > 0.0f ) {
            float real_focus_distance = 1.0f / new_focus_distance;
            focus_distance_s = decimal_format_2dp.format(real_focus_distance) + "m";
        }
        else {
            focus_distance_s = "infinite";
        }
        return focus_distance_s;
    }
}
