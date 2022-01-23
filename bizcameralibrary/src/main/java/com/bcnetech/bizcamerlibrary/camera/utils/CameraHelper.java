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

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.util.Log;
import android.util.Size;

import com.bcnetech.bcnetechlibrary.util.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CameraHelper implements CameraHelperObj {

    private int previewW = PREVIEW_WIDTH;
    private int previewH = PREVIEW_HEIGHT;

    private int adapterW = PHOTO_WIDTH_M;
    private int adapterH = PHOTO_HEIGHT_M;

    private int ps, pv;

    private boolean isChangePreViewSize = false;
    private int currentType = SIZEMIDDLE;

    private Camera.CameraInfo camera_info = new Camera.CameraInfo();


    /**
     * 最大宽高比差
     */
    public static final double MAX_ASPECT_DISTORTION = 0;

    /**
     * 图片质量
     */
    private static final int QUARY_PIC = 100;

    private CameraDataListener cameraDataListener;


    private boolean isAutoFous;
    private String focusMode = "continuous-picture";
    private String isoKey;
    private String isoValue;
    private String wb;
    private String focus;
    private String flash;
    private int ev = -999;
    private Camera.Parameters currentParameters;

    private Size adapterSize = null;
    private Size previewSize = null;

    private ArrayList<Size> sizeList;
    private ArrayList<Size> preList;
    private ArrayList<Size> previewList;
    private int FpsRate = 24;
    private boolean isAi360 = false;

    /**
     * 设置最小的拍照尺寸（避免320*240的图片出现）
     */
    public static final int MINSIZE = 800;

    public CameraHelper() {
        isAutoFous = true;
        Camera.getCameraInfo(0, camera_info);
    }

    public interface CameraHelperImpl {
        int getNumberOfCameras();

        Camera openCamera(int id);

        Camera openDefaultCamera();

        Camera openCameraFacing(int facing);

        boolean hasCamera(int cameraFacingFront);

        void getCameraInfo(int cameraId, CameraInfo2 cameraInfo);
    }


    public int getNumberOfCameras() {
        return Camera.getNumberOfCameras();
    }

    public Camera openCamera(final int id) {
        return Camera.open(id);
    }

    public void getCameraInfo(final int cameraId, final CameraInfo2 cameraInfo) {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        cameraInfo.facing = info.facing;
        cameraInfo.orientation = info.orientation;
    }

    public int getCameraDisplayOrientation(final int cameraId) {

        int degrees = 0;
        int result;
        CameraInfo2 info = new CameraInfo2();
        getCameraInfo(cameraId, info);
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    public void setisAi360(boolean isAi360) {
        this.isAi360 = isAi360;
    }

    @Override
    public boolean onResume() {
        return true;
    }

    /**
     * 当前相机拥有适合1：1的分辨率
     */
    public boolean supportTypeSquare() {
        return CameraUtils.supportSquareType(currentParameters);
    }

    /**
     * 获取当前的相机参数
     *
     * @return
     */
    public HashMap<String, String> getCameraParams(Camera mCameraInstance) {
        HashMap<String, String> paramMap = new HashMap<>();
        if (mCameraInstance != null) {
            Camera.Parameters parameters = mCameraInstance.getParameters();
            if(null!=parameters){
                String currentiso = parameters.get("iso");
                if (currentiso != null) {
                    paramMap.put("ISO", currentiso);
                }
                String currentWb = parameters.getWhiteBalance();
                if (currentWb != null) {
                    paramMap.put("whitebalance", currentWb);
                }
                String currentfocus = parameters.getFocusMode();
                if (currentfocus != null) {
                    paramMap.put("focus", currentfocus);
                }
            }
        }
        return paramMap;
    }

    /**
     * 获取支持的preview帧数
     *
     * @param parameters
     * @return
     */
    public List<int[]> getSupportedPreviewFpsRange(Camera.Parameters parameters) {
        try {
            return parameters.getSupportedPreviewFpsRange();
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过拍照选择preview的帧数
     *
     * @param fps_ranges
     * @return
     */
    public int[] chooseBestPreviewFps(List<int[]> fps_ranges) {

        // find value with lowest min that has max >= 30; if more than one of these, pick the one with highest max
        int selected_min_fps = -1, selected_max_fps = -1;
        for (int[] fps_range : fps_ranges) {
            int min_fps = fps_range[0];
            int max_fps = fps_range[1];
            if (max_fps >= 30000) {
                if (selected_min_fps == -1 || min_fps < selected_min_fps) {
                    selected_min_fps = min_fps;
                    selected_max_fps = max_fps;
                } else if (min_fps == selected_min_fps && max_fps > selected_max_fps) {
                    selected_min_fps = min_fps;
                    selected_max_fps = max_fps;
                }
            }
        }

        if (selected_min_fps != -1) {
        } else {
            // just pick the widest range; if more than one, pick the one with highest max
            int selected_diff = -1;
            for (int[] fps_range : fps_ranges) {
                int min_fps = fps_range[0];
                int max_fps = fps_range[1];
                int diff = max_fps - min_fps;
                if (selected_diff == -1 || diff > selected_diff) {
                    selected_min_fps = min_fps;
                    selected_max_fps = max_fps;
                    selected_diff = diff;
                } else if (diff == selected_diff && max_fps > selected_max_fps) {
                    selected_min_fps = min_fps;
                    selected_max_fps = max_fps;
                    selected_diff = diff;
                }
            }
        }
        return new int[]{selected_min_fps, selected_max_fps};
    }


    private int[] getPreviewFPS(Camera.Parameters cameraParameters, int fps) {

        List<int[]> fpsRanges = cameraParameters.getSupportedPreviewFpsRange();

        int expectedFps =fps* 1000;
        int[] closestRange = fpsRanges.get(0);
        int measure = Math.abs(closestRange[0] - expectedFps) + Math.abs(closestRange[1] - expectedFps);
        for (int[] range : fpsRanges) {
            if (range[0] <= expectedFps && range[1] >= expectedFps) {
                int curMeasure = Math.abs(range[0] - expectedFps) + Math.abs(range[1] - expectedFps);
                if (curMeasure < measure) {
                    closestRange = range;
                    measure = curMeasure;
                }
            }
        }
        return closestRange;
    }



    /**
     * 通过视频录制选择合适的帧数
     *
     * @param fps_ranges
     * @param video_frame_rate
     * @return
     */
    public int[] matchPreviewFpsToVideo(List<int[]> fps_ranges, int video_frame_rate) {
        int selected_min_fps = -1, selected_max_fps = -1, selected_diff = -1;
        for (int[] fps_range : fps_ranges) {
            int min_fps = fps_range[0];
            int max_fps = fps_range[1];
            if (min_fps <= video_frame_rate && max_fps >= video_frame_rate) {
                int diff = max_fps - min_fps;
                if (selected_diff == -1 || diff < selected_diff) {
                    selected_min_fps = min_fps;
                    selected_max_fps = max_fps;
                    selected_diff = diff;
                }
            }
        }
        if (selected_min_fps != -1) {
        } else {
            selected_diff = -1;
            int selected_dist = -1;
            for (int[] fps_range : fps_ranges) {
                int min_fps = fps_range[0];
                int max_fps = fps_range[1];
                int diff = max_fps - min_fps;
                int dist;
                if (max_fps < video_frame_rate)
                    dist = video_frame_rate - max_fps;
                else
                    dist = min_fps - video_frame_rate;
                if (selected_dist == -1 || dist < selected_dist || (dist == selected_dist && diff < selected_diff)) {
                    selected_min_fps = min_fps;
                    selected_max_fps = max_fps;
                    selected_dist = dist;
                    selected_diff = diff;
                }
            }
        }
        return new int[]{selected_min_fps, selected_max_fps};
    }


    @Override
    public void setFpsRate(int rate) {
        FpsRate = rate;
    }

    public int[] setCameraFPS(boolean isVideo) {
        int[] selected_fps;
        if (isVideo) {
            selected_fps = matchPreviewFpsToVideo(getSupportedPreviewFpsRange(getCurrentParameters()), FpsRate * 1000);
        } else {
            selected_fps = getPreviewFPS(getCurrentParameters(),FpsRate);
        }
        return selected_fps;
    }

    boolean isVideo = false;

    @Override
    public void setCurrentType(boolean isVideo) {
        this.isVideo = isVideo;
    }

    public void setCameraParms(Camera mCameraInstance) {
        if (mCameraInstance == null) {
            return;
        }
        Camera.Parameters parameters = mCameraInstance.getParameters();
        this.currentParameters = parameters;
        parameters.setPictureFormat(PixelFormat.JPEG);// 设置照片输出的格式
        parameters.setJpegQuality(QUARY_PIC);// 设置照片质量

        if (isoKey != null && isoValue != null) {
            parameters.set(isoKey, isoValue);
        }
        if (wb != null) {
            parameters.setWhiteBalance(wb);
        }
        if (flash != null) {
            if (currentParameters != null && !currentParameters.getFlashMode().equals(flash)) {
                parameters.setFlashMode(flash);
            }
        }
        if (ev != -999) {
            parameters.setExposureCompensation(ev);
        }
        if (focus != null) {
            parameters.setFocusMode(focus);
        } else {
            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
        }

        if (adapterSize == null) {
            adapterSize = new Size(findBestPictureResolution(mCameraInstance, adapterW, adapterH).width, findBestPictureResolution(mCameraInstance, adapterW, adapterH).height);
        }
        if (adapterSize != null) {
            parameters.setPictureSize(adapterSize.getWidth(), adapterSize.getHeight());// 获得保存图片的大小
        }

        if (previewSize == null) {
            previewSize = new Size(findBestPreviewResolution(mCameraInstance, previewW, previewH).width, findBestPreviewResolution(mCameraInstance, previewW, previewH).height);
        }

        if (isChangePreViewSize) {
            mCameraInstance.stopPreview();
        }

        if (previewSize != null) {
            parameters.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
        }
        LogUtil.d("currentPreViewSize:" + previewSize.getWidth() + "*" + previewSize.getHeight());
        LogUtil.d("currentPictureSize:" + adapterSize.getWidth() + "*" + adapterSize.getHeight());
        //setCameraType(false);
        int[] fpss = setCameraFPS(isVideo);
        Log.d("yhf","psd:" + fpss[0] + "  " + fpss[1]);
      //  parameters.setPreviewFpsRange(24000, 24000);
        if (fpss != null && fpss.length == 2) {
            LogUtil.d("psd:" + fpss[0] + "  " + fpss[1]);
            //设置视屏录制：每秒30贞
            parameters.setPreviewFpsRange(fpss[0], fpss[1]);
        }
        // 设置输出视频流尺寸，采样率
        parameters.setPreviewFormat(ImageFormat.YV12);
        mCameraInstance.setParameters(parameters);
        this.currentParameters = parameters;
        //CameraUtils.supportSquareType(parameters);
        if (isChangePreViewSize) {
            mCameraInstance.startPreview();
            isChangePreViewSize = false;
        }
    }

    /**
     * 找出最适合的预览界面分辨率
     *
     * @return
     */
    private Camera.Size findBestPreviewResolution(Camera cameraInst, int screenWidth, int screenHeight) {
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

        previewList = new ArrayList<>();
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

            // 找到与屏幕分辨率完全匹配的预览界面分辨率直接返回
            if (maybeFlippedWidth == screenWidth
                    && maybeFlippedHeight == screenHeight) {
                return supportedPreviewResolution;
            }

            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
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

        if (currentType == SIZELARGE) {
            return sortedSupportedPreviewResolutions.get(0);
        }

        // 如果没有找到合适的，并且还有候选的像素，对于照片，则取其中最大比例的，而不是选择与屏幕分辨率相同的
        if (!sortedSupportedPreviewResolutions.isEmpty()) {
            return sortedSupportedPreviewResolutions.get(0);
        }

        // 没有找到合适的，就返回默认的
        return defaultPictureResolution;
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

        sizeList = new ArrayList<>();
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

            if (width < MINSIZE && height < MINSIZE || distortion > MAX_ASPECT_DISTORTION) {
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
            preList = new ArrayList<>();
            for (int i = 0; i < sortedSupportedPicResolutions.size(); i++) {
                preList.add(new Size(sortedSupportedPicResolutions.get(i).width, sortedSupportedPicResolutions.get(i).height));
            }
            cameraDataListener.getPreSize(preList);
        }

        if (currentType == SIZELARGE) {
            return sortedSupportedPicResolutions.get(0);
        } else if (currentType == SIZESMALL) {
            return sortedSupportedPicResolutions.get(sortedSupportedPicResolutions.size() - 1);
        } else {
            return sortedSupportedPicResolutions.get((int) sortedSupportedPicResolutions.size() / 2);
        }
    }


    //定点对焦的代码
    public void pointFocus(Camera mCameraInstance, float x, float y) {
        mCameraInstance.cancelAutoFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//            focusOnTouch(mCameraInstance, x, y);
        }
        //   autoFocus(mCameraInstance);
    }

    @Override
    public void pointFocus(Camera mCameraInstance, List<Area> areas) {
        mCameraInstance.cancelAutoFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            List<Camera.Area> camera_areas = new ArrayList<>();
            for (Area area : areas) {
                camera_areas.add(new Camera.Area(area.rect, area.weight));
            }
            focusOnTouch(mCameraInstance, camera_areas);

        }

    }

    @Override
    public void pointFocus(List<Area> areas) {

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void focusOnTouch(Camera mCameraInstance, List<Camera.Area> focusAreas) {
//        Rect focusRect = calculateTapArea(mCameraInstance, x, y, 1f);
//        Rect meteringRect = calculateTapArea(mCameraInstance, x, y, 1.5f);

        Camera.Parameters parameters = mCameraInstance.getParameters();

        //触摸点自动对焦
        if (parameters.getMaxNumFocusAreas() > 0) {
//            List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
//            focusAreas.add(new Camera.Area(focusRect, 1000));
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            parameters.setFocusAreas(focusAreas);
        }
        //触摸点自动测光
        if (parameters.getMaxNumMeteringAreas() > 0) {

            parameters.setMeteringAreas(focusAreas);
        }
//        parameters.setExposureCompensation(0);
        mCameraInstance.setParameters(parameters);
        focusMode = "continuous-picture";
        if (isAutoFous) {
            mCameraInstance.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {

                        /*Camera.Parameters parameters = camera.getParameters();
                        List<Camera.Area> area = parameters.getFocusAreas();
                        for (int i = 0; i < area.size(); i++) {
                            Rect rect = area.get(i).rect;
                        }

                       // parameters.setFocusMode(focusMode);
                        if (isoKey != null && isoValue != null) {
                            parameters.set(isoKey, isoValue);
                        }
                        if (wb != null) {
                            parameters.setWhiteBalance(wb);
                        }
                        camera.setParameters(parameters);*/
                    }
                }
            });
        } else {
            mCameraInstance.cancelAutoFocus();
        }
        isAutoFous = true;
    }

    /**
     * 自动对焦
     *
     * @param mCameraInstance
     * @param isAutoFous
     */
    public void setAutoFous(Camera mCameraInstance, boolean isAutoFous) {
        Camera.Parameters cameraParameters = mCameraInstance.getParameters();
        cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCameraInstance.setParameters(cameraParameters);
        this.isAutoFous = isAutoFous;
    }

    public void setAF(boolean isAutoFous) {
        this.isAutoFous = isAutoFous;
    }

    //实现自动对焦
    public void autoFocus(final Camera mCameraInstance) {
        new Thread() {
            @Override
            public void run() {

                if (mCameraInstance == null) {
                    return;
                }
                if (isAutoFous) {
                    mCameraInstance.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            if (success) {
                                Camera.Parameters parameters = camera.getParameters();
                                focusMode = "continuous-picture";
                                parameters.setFocusMode(focusMode);
                                if (isoKey != null && isoValue != null) {
                                    parameters.set(isoKey, isoValue);
                                }
                                if (wb != null) {
                                    parameters.setWhiteBalance(wb);
                                }
                                camera.setParameters(parameters);
                            }
                        }
                    });
                } else {
                    mCameraInstance.cancelAutoFocus();
                }
            }
        };
    }

    //设置白平衡
    public void setWhiteBalance(String wb, Camera mCameraInstance) {
        if (mCameraInstance == null) {
            return;
        }
        if (wb == null) {
            return;
        }
        Camera.Parameters cameraParameters = mCameraInstance.getParameters();
        List<String> wbList = cameraParameters.getSupportedWhiteBalance();
        for (int i = 0; i < wbList.size(); i++) {
            if (wbList.get(i).contains(wb)) {
                // cameraParameters.setWhiteBalance(wb);
                this.wb = wb;
            } else {
            }
        }
        //mCameraInstance.setParameters(cameraParameters);
        //setCameraParms(mCameraInstance);
    }

    //设置焦距
    public void setFocus(Camera mCameraInstance, String focus) {
        if (mCameraInstance == null) {
            return;
        }
        if (focus == null) {
            return;
        }
        Camera.Parameters cameraParameters = mCameraInstance.getParameters();
        List<String> focusList = cameraParameters.getSupportedFocusModes();
        for (int i = 0; i < focusList.size(); i++) {
            if (focusList.get(i).contains(focus)) {
                // cameraParameters.setFocusMode(focus);
                focusMode = focus;
                this.focus = focus;
            } else {
            }
        }
        //mCameraInstance.setParameters(cameraParameters);
        // setCameraParms(mCameraInstance);
    }

    //设置是否打开补光灯
    public void setFlash(boolean isflash, Camera mCameraInstance) {
        if (mCameraInstance == null) {
            return;
        }
        Camera.Parameters cameraParameters = mCameraInstance.getParameters();
        List<String> flashlist = cameraParameters.getSupportedFlashModes();
        if (isflash) {
            if (flashlist.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                this.flash = Camera.Parameters.FLASH_MODE_TORCH;
            }
        } else {
            if (flashlist.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                this.flash = Camera.Parameters.FLASH_MODE_OFF;
            }
        }
        //setCameraParms(mCameraInstance);
    }

    //设置曝光补偿
    public void setEV(String ev, Camera mCameraInstance) {
        if (mCameraInstance == null) {
            return;
        }
        if (ev == null) {
            return;
        }
        this.ev = Integer.valueOf(ev);
        //setCameraParms(mCameraInstance);
    }

    public void setEV2(int ev, Camera mCameraInstance) {
        if (mCameraInstance == null) {
            return;
        }
        if (ev == -999) {
            return;
        }
        this.ev = ev;
        Camera.Parameters parameters = mCameraInstance.getParameters();
        parameters.setExposureCompensation(this.ev);
        mCameraInstance.setParameters(parameters);
    }

    public void setFlash2(boolean isflashon, Camera mCameraInstance) {
        if (mCameraInstance == null) {
            return;
        }

        Camera.Parameters cameraParameters = mCameraInstance.getParameters();
        List<String> flashlist = cameraParameters.getSupportedFlashModes();
        if (flashlist == null || flashlist.size() == 0) {
            return;
        }
        if (isflashon) {
            if (flashlist.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                this.flash = Camera.Parameters.FLASH_MODE_TORCH;
            }
        } else {
            if (flashlist.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                this.flash = Camera.Parameters.FLASH_MODE_OFF;
            }
        }
        Camera.Parameters parameters = mCameraInstance.getParameters();
        parameters.setFlashMode(this.flash);
        mCameraInstance.setParameters(parameters);
    }

    public Camera.Parameters getCurrentParameters() {
        return this.currentParameters;
    }


    //设置iso
    public void setIso(String iso, Camera mCameraInstance) {
        if (mCameraInstance == null) {
            return;
        }
        if (iso == null) {
            return;
        }
        Camera.Parameters cameraParameters = mCameraInstance.getParameters();
        String flat = cameraParameters.flatten();
        //支持的iso
        List<String> isoValues;
        String values_keyword = null;
        String iso_keyword = null;
        if (flat.contains("iso-values")) {
            // most used keywords
            values_keyword = "iso-values";
            iso_keyword = "iso";
        } else if (flat.contains("iso-mode-values")) {
            // google galaxy nexus keywords
            values_keyword = "iso-mode-values";
            iso_keyword = "iso";
        } else if (flat.contains("iso-speed-values")) {
            // micromax a101 keywords
            values_keyword = "iso-speed-values";
            iso_keyword = "iso-speed";
        } else if (flat.contains("nv-picture-iso-values")) {
            // LG dual p990 keywords
            values_keyword = "nv-picture-iso-values";
            iso_keyword = "nv-picture-iso";
        }
        if (iso_keyword == null) {
            // ToastUtil.toast("您的手机不支持此项功能");
        }
// add other eventual keywords here...
        if (iso_keyword != null) {
            // flatten contains the iso key!!
            String isovalue = flat.substring(flat.indexOf(values_keyword));
            isovalue = isovalue.substring(isovalue.indexOf("=") + 1);
            isovalue.replace(" ", "");
            if (isovalue.contains(";")) isovalue = isovalue.substring(0, isovalue.indexOf(";"));

            if (isovalue.contains(iso)) {
                // cameraParameters.set(iso_keyword, iso);
                this.isoValue = iso;
                this.isoKey = iso_keyword;
                // mCameraInstance.setParameters(cameraParameters);
            } else {
                //  ToastUtil.toast("您的手机不支持此项功能");
            }
        }
        //setCameraParms(mCameraInstance);
    }

    @Override
    public void setMPreViewSize2(Size preview, Size pciture) {
        adapterSize = pciture;
        previewSize = preview;
        isChangePreViewSize = true;
    }

    //设置预览分辨率和拍照分辨率
    @Override
    public void setMPreViewSize(String preview, String picSize) {
        pv = Integer.parseInt(preview);
        ps = Integer.parseInt(picSize);
        currentType = ps;
        if (pv == PREVIEW_169) {
            switch (ps) {
                case SIZELARGE:
                    previewW = 9000;
                    previewH = 16000;
                    adapterW = 9000;
                    adapterH = 16000;
                    break;
                case SIZEMIDDLE:
                    adapterW = PHOTO_WIDTH2_M;
                    adapterH = PHOTO_HEIGHT2_M;
                    previewW = PHOTO_WIDTH2_M;
                    previewH = PHOTO_HEIGHT2_M;
                    break;
                case SIZESMALL:
                    adapterW = PHOTO_WIDTH2_S;
                    adapterH = PHOTO_HEIGHT2_S;
                    previewW = PHOTO_WIDTH2_S;
                    previewH = PHOTO_HEIGHT2_S;
                    break;
            }

        } else if (pv == PREVIEW_43) {
            switch (ps) {
                case SIZELARGE:
                    adapterW = 3000;
                    adapterH = 4000;
                    previewW = 1200;
                    previewH = 1600;
                    break;
                case SIZEMIDDLE:
                    adapterW = PHOTO_WIDTH_M;
                    adapterH = PHOTO_HEIGHT_M;
                    previewW = 1200;
                    previewH = 1600;
                    break;
                case SIZESMALL:
                    adapterW = PHOTO_WIDTH_S;
                    adapterH = PHOTO_HEIGHT_S;
                    previewW = 1200;
                    previewH = 1600;
                    break;
            }
        } else {
            //视屏1：1
            previewW = getSupportSquareSize(currentParameters).x;
            previewH = getSupportSquareSize(currentParameters).y;
        }
        previewSize = null;
        adapterSize = null;
        isChangePreViewSize = true;
        //setCameraParms(mCameraInstance);
    }

    public Point getSupportSquareSize(Camera.Parameters parms) {
        for (Camera.Size size : parms.getSupportedPreviewSizes()) {
            if (size.width == size.height)
                return new Point(size.width, size.height);
        }
        return new Point(previewW, previewH);
    }


    public Camera.Size getPreviewSize(Camera mCameraInstance) {
        Camera.Parameters params = mCameraInstance.getParameters();
        Camera.Size s = params.getPreviewSize();
        return s;
    }

    public Camera.Size getAdapterSize(Camera mCameraInstance) {
        Camera.Parameters params = mCameraInstance.getParameters();
        Camera.Size s = params.getPictureSize();
        return s;
    }

    @Override
    public Size getmPreviewSize() {
        return null;
    }


    @Override
    public void setCameraDataListener(CameraDataListener cameraDataListener) {
        this.cameraDataListener = cameraDataListener;
    }

    @Override
    public void closeCamera() {

    }

    @Override
    public void setGetQRInter(getQRInterface getQRInter) {

    }

    @Override
    public void setWhiteBalance(int supported_values) {

    }

    @Override
    public void setIso(int iso) {

    }

    @Override
    public void setFocusDistance(boolean isAutoFocus, float focus_distance) {

    }

    @Override
    public void setSec(long sec) {

    }

    @Override
    public void lockAllCameraParam() {

    }

    @Override
    public void takePicture(TackPhotoCallback tackPhotoCallback) {

    }

    @Override
    public void startRecordingVideo() {

    }

    @Override
    public void setRecordUrl(String recordUrl) {

    }

    @Override
    public void prepareRecordModel() {

    }

    @Override
    public void setRecordSize(Size size) {

    }

    @Override
    public void stopRecordingVideo() {

    }

    @Override
    public void stopPreView() {

    }

    @Override
    public String getRecordUrl() {
        return null;
    }

    @Override
    public void setAe(int ae) {

    }

    @Override
    public ArrayList<Size> getPreSize() {
        return preList;
    }

    @Override
    public ArrayList<Size> getAllSize() {
        return new ArrayList<>(sizeList);
    }

    @Override
    public ArrayList<Size> getAllPreviewSize() {
        return new ArrayList<>(previewList);
    }

    @Override
    public void notifyPreRatio(int preType) {
        Size mSize = null;
        if (preType == PREVIEW_169) {
            mSize = new Size(PHOTO_WIDTH2_S, PHOTO_HEIGHT2_S);

        } else if (preType == PREVIEW_43) {
            mSize = new Size(PHOTO_WIDTH_S, PHOTO_HEIGHT_S);
        } else if (preType == PREVIEW_11) {
            mSize = new Size(PHOTO_WIDTH_S, PHOTO_HEIGHT_S);
        }

        // 移除不符合条件的分辨率
        double screenAspectRatio = mSize.getWidth()
                / (double) mSize.getHeight();
        ArrayList<Size> mlist = new ArrayList<>();
        mlist.addAll(sizeList);
        Iterator<Size> it = mlist.iterator();
        while (it.hasNext()) {
            Size supportedPreviewResolution = it.next();
            int width = supportedPreviewResolution.getWidth();
            int height = supportedPreviewResolution.getHeight();
            // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
            // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
            // 因此这里要先交换然后在比较宽高比
            boolean isCandidatePortrait = width > height;
            int maybeFlippedWidth = isCandidatePortrait ? height : width;
            int maybeFlippedHeight = isCandidatePortrait ? width : height;
            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (width < MINSIZE && height < MINSIZE || distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
            }
        }
        Collections.sort(mlist, new Comparator<Size>() {
            @Override
            public int compare(Size a, Size b) {
                int aPixels = a.getHeight() * a.getWidth();
                int bPixels = b.getHeight() * b.getWidth();
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
            preList = mlist;
            cameraDataListener.getPreSize(preList);
        }
    }

    @Override
    public boolean allowCamera2Support(int cameraId) {
        return false;
    }

    @Override
    public CameraCharacteristics getmCameraCharacteristics() {
        return null;
    }

    @Override
    public void unlockAllCameraParam() {
    }

    @Override
    public void lockPresetCameraParam(String sec, String iso, String wb, String focus) {

    }

    @Override
    public void lockPresetCameraParam(String sec, String iso, String wb, String focus, String mRed, String mGreenEven, String mGreenOdd, String mBlue) {

    }

    @Override
    public void setCameraStateInter(CameraStateInter cameraStateInter) {


    }

    @Override
    public void cancelAutoFocus() {

    }

    @Override
    public int getCameraOrientation() {
        return camera_info.orientation;
    }

    @Override
    public boolean isFrontFacing() {
        return (camera_info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    public void setCurrentRatio(int ratio) {

    }

}

