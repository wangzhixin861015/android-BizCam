package com.bcnetech.bizcamerlibrary.camera.utils;

import android.hardware.Camera;
import android.util.Size;

import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bizcamerlibrary.camera.dao.CameraSizeData;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 通过当前设置获取所需的拍照和预览尺寸集合
 * Created by a1234 on 2018/7/24.
 */
public class CameraSizeModel {
    public static CameraSizeModel cameraSizeModel;
    private Size size34 = new Size(1080, 1440);
    private Size size169 = new Size(1080, 1920);
    private Camera.Parameters cameraParameters;
    private CameraSizeData cameraSizeData11, cameraSizeData34, cameraSizeData916;
    /**
     * 最大宽高比差
     */
    private static final double MAX_ASPECT_DISTORTION = 0;

    private static final int MAXSIZE = 2800;
    private static final int MAXSIZE2 = 3800;
    private static final int MAXPREVIEWSIZE = 1080;
    private static final int MINSIZE_VIDEO = 480;
    private static final int MINSIZE_PIC = 800;

    public static CameraSizeModel getCameraSizeModel(Camera.Parameters parameters) {
        if (cameraSizeModel == null) {
            cameraSizeModel = new CameraSizeModel(parameters);
        }
        return cameraSizeModel;
    }

    public CameraSizeModel(Camera.Parameters cameraParameters) {
        this.cameraParameters = cameraParameters;
        cameraSizeData34 = new CameraSizeData(CameraStatus.Size.TYPE_34);
        selectCurrentSize(cameraSizeData34);
        cameraSizeData916 = new CameraSizeData(CameraStatus.Size.TYPE_916);
        selectCurrentSize(cameraSizeData916);
        cameraSizeData11 = new CameraSizeData(CameraStatus.Size.TYPE_11);
        selectCurrentSize(cameraSizeData11);
        LogUtil.d("selectSize:" + cameraSizeData34 + " " + cameraSizeData916 + " " + cameraSizeData11);
    }

    public CameraSizeData getCameraSizeData11() {
        return cameraSizeData11;
    }

    public CameraSizeData getCameraSizeData34() {
        return cameraSizeData34;
    }

    public CameraSizeData getCameraSizeData916() {
        return cameraSizeData916;
    }

    public CameraSizeData getCameraSizeData(int size) {
        CameraSizeData cameraSizeData;
        switch (size) {
            case CameraHelperObj.PREVIEW_11:
                cameraSizeData = cameraSizeData11;
                break;
            case CameraHelperObj.PREVIEW_43:
                cameraSizeData = cameraSizeData34;
                break;
            default:
                cameraSizeData = cameraSizeData916;
                break;
        }
        return cameraSizeData;
    }

    private void selectCurrentSize(CameraSizeData cameraSizeData) {

        if (cameraSizeData.getSizeRatio() == CameraStatus.Size.TYPE_34) {
            ArrayList<Size> sizeArrayList2 = findBestPreviewResolution(true, size34.getWidth(), size34.getHeight());
            ArrayList<Size> sizeArrayList = findBestPreviewResolution(false, size34.getWidth(), size34.getHeight());

            cameraSizeData.getPictureSizeBean().setRealsizeLarge(sizeArrayList.get(0));
            cameraSizeData.getPictureSizeBean().setRealsizeMiddle(sizeArrayList.get(1));
            cameraSizeData.getPictureSizeBean().setRealsizeSmall(sizeArrayList.get(2));
            cameraSizeData.getVideoSizeBean().setRealsizeLarge(sizeArrayList2.get(0));
            cameraSizeData.getVideoSizeBean().setRealsizeMiddle(sizeArrayList2.get(1));
            cameraSizeData.getVideoSizeBean().setRealsizeSmall(sizeArrayList2.get(2));
            setSizeBeans(cameraSizeData34, sizeArrayList2, sizeArrayList);
            //1：1------->3：4截取
        } else if (cameraSizeData.getSizeRatio() == CameraStatus.Size.TYPE_11) {
            ArrayList<Size> sizeArrayList = findBestPreviewResolution(true, size34.getWidth(), size34.getHeight());
            cameraSizeData.getVideoSizeBean().setRealsizeLarge(sizeArrayList.get(0));
            cameraSizeData.getVideoSizeBean().setRealsizeMiddle(sizeArrayList.get(1));
            cameraSizeData.getVideoSizeBean().setRealsizeSmall(sizeArrayList.get(2));
            ArrayList<Size> squarePreviewList = new ArrayList<>();
            for (Size size : sizeArrayList) {
                squarePreviewList.add(new Size(Math.min(size.getWidth(), size.getHeight()), Math.min(size.getWidth(), size.getHeight())));
            }
            ArrayList<Size> sizeArrayList2 = findBestPreviewResolution(false, size34.getWidth(), size34.getHeight());
            cameraSizeData.getPictureSizeBean().setRealsizeLarge(sizeArrayList2.get(0));
            cameraSizeData.getPictureSizeBean().setRealsizeMiddle(sizeArrayList2.get(1));
            cameraSizeData.getPictureSizeBean().setRealsizeSmall(sizeArrayList2.get(2));
            ArrayList<Size> squarePictureList = new ArrayList<>();
            for (Size size : sizeArrayList2) {
                squarePictureList.add(new Size(Math.min(size.getWidth(), size.getHeight()), Math.min(size.getWidth(), size.getHeight())));
            }
            setSizeBeans(cameraSizeData11, squarePreviewList, squarePictureList);
        } else {
            ArrayList<Size> sizeArrayList2 = findBestPreviewResolution(true, size169.getWidth(), size169.getHeight());
            ArrayList<Size> sizeArrayList = findBestPreviewResolution(false, size169.getWidth(), size169.getHeight());

            if(sizeArrayList.size()>0){
                cameraSizeData.getPictureSizeBean().setRealsizeLarge(sizeArrayList.get(0));
            }
            if(sizeArrayList.size()>1){
                cameraSizeData.getPictureSizeBean().setRealsizeMiddle(sizeArrayList.get(1));
            }
            if(sizeArrayList.size()>2){
                cameraSizeData.getPictureSizeBean().setRealsizeMiddle(sizeArrayList.get(2));
            }

            if(sizeArrayList2.size()>0){
                cameraSizeData.getVideoSizeBean().setRealsizeLarge(sizeArrayList2.get(0));
            }
            if(sizeArrayList2.size()>1){
                cameraSizeData.getVideoSizeBean().setRealsizeLarge(sizeArrayList2.get(1));
            }
            if(sizeArrayList2.size()>2){
                cameraSizeData.getVideoSizeBean().setRealsizeLarge(sizeArrayList2.get(2));
            }

            setSizeBeans(cameraSizeData916, sizeArrayList2, sizeArrayList);

        }
    }

    private void setSizeBeans(CameraSizeData cameraSizeData, ArrayList<Size> preivewList, ArrayList<Size> pictureList) {
        ///拍照
        if(pictureList.size()>0){
            cameraSizeData.getPictureSizeBean().setSizeLarge(pictureList.get(0));
        }
        if(pictureList.size()>1){
            cameraSizeData.getPictureSizeBean().setSizeLarge(pictureList.get(1));
        }
        if(pictureList.size()>2){
            cameraSizeData.getPictureSizeBean().setSizeLarge(pictureList.get(2));
        }

        //默认为中尺寸
        if(pictureList.size()>1){
            cameraSizeData.getPictureSizeBean().setSizeLarge(pictureList.get(1));
        }

        ///录像
        if(preivewList.size()>0){
            cameraSizeData.getVideoSizeBean().setSizeLarge(preivewList.get(0));
        }
        if(preivewList.size()>1){
            cameraSizeData.getVideoSizeBean().setSizeLarge(preivewList.get(1));
        }
        if(preivewList.size()>2){
            cameraSizeData.getVideoSizeBean().setSizeLarge(preivewList.get(2));
        }

        //默认为中尺寸
        if(preivewList.size()>1){
            cameraSizeData.getVideoSizeBean().setSizeLarge(preivewList.get(1));
        }


    }

    /**
     * 找出最适合的预览界面分辨率(list中包含大中小三个Size)
     *
     * @return
     */
    private ArrayList<Size> findBestPreviewResolution(boolean isPreview, int screenWidth, int screenHeight) {
        ArrayList<Size> msizeList;
        final int allPix = screenWidth * screenHeight;
        List<Camera.Size> supportedPreviewSizes;
        int minSize = isPreview ? MINSIZE_VIDEO : MINSIZE_PIC;
        if (isPreview) {
            supportedPreviewSizes = cameraParameters.getSupportedPreviewSizes(); // 所有预览尺寸
        } else {
            supportedPreviewSizes = cameraParameters.getSupportedPictureSizes(); // 所有拍照尺寸
        }
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

        // 移除不符合条件的分辨率
        double screenAspectRatio = screenWidth
                / (double) screenHeight;
        Iterator<Camera.Size> it = sortedSupportedPreviewResolutions.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewResolution = it.next();
            int width = supportedPreviewResolution.width;
            int height = supportedPreviewResolution.height;
            LogUtil.d("AllSize: " + width + " " + height);
            // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
            // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
            // 因此这里要先交换然后在比较宽高比
            boolean isCandidatePortrait = width > height;
            int maybeFlippedWidth = isCandidatePortrait ? height : width;
            int maybeFlippedHeight = isCandidatePortrait ? width : height;
            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            //尺寸不符合要求，小于最小值，大于最大值
            if (distortion > MAX_ASPECT_DISTORTION || Math.min(maybeFlippedHeight, maybeFlippedWidth) < minSize || (maybeFlippedHeight > MAXSIZE && maybeFlippedWidth > MAXSIZE) ){
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


        msizeList = new ArrayList<>();
        if(sortedSupportedPreviewResolutions.size()>0){
            msizeList.add(new Size(sortedSupportedPreviewResolutions.get(0).width, sortedSupportedPreviewResolutions.get(0).height));
            msizeList.add(new Size(sortedSupportedPreviewResolutions.get(sortedSupportedPreviewResolutions.size() / 2).width, sortedSupportedPreviewResolutions.get(sortedSupportedPreviewResolutions.size() / 2).height));
            msizeList.add(new Size(sortedSupportedPreviewResolutions.get(sortedSupportedPreviewResolutions.size() - 1).width, sortedSupportedPreviewResolutions.get(sortedSupportedPreviewResolutions.size() - 1).height));
        }

        LogUtil.d("selectSizeList:" + msizeList.toString());
        return msizeList;
    }
}
