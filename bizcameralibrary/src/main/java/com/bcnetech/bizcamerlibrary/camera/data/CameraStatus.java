package com.bcnetech.bizcamerlibrary.camera.data;

import com.bcnetech.bcnetchhttp.config.PreferenceConstants;
import com.bcnetech.bcnetechlibrary.util.sharepreference.SharePreferences;
import com.google.gson.Gson;


/**
 * Created by a1234 on 2017/11/22.
 */

public class CameraStatus {
    public static CameraStatus cameraStatus;
    public static final int MAXTIME = 60;
    private boolean isFlashOn;//闪光灯
    private WaterMark WaterMark;//水印
    private boolean isSubLineOn;//辅助线
    private Size recordTime;//录像时长

    private Size pictureRatio;//拍照比例
    private Size videoRatio;//录像比例

    private Size pictureSize;//拍照尺寸
    private Size videoSize;//录像尺寸

    private boolean BlackAndWhite;//标准黑白对照图
    private boolean picCompress;//导出图像压缩
    private int costomRecordTime;//自定义录像时间
    private android.util.Size recordSize;//录制尺寸(f)

    public CameraStatus() {
    }

    public CameraStatus(boolean isFlashOn, WaterMark WaterMark, boolean isSubLineOn, Size recordTime, Size pictureRatio, Size pictureSize, Size videoRatio, Size videoSize, boolean BlackAndWhite, boolean picCompress, int costomRecordTime, android.util.Size recordSize) {
        this.isFlashOn = isFlashOn;
        this.WaterMark = WaterMark;
        this.isSubLineOn = isSubLineOn;
        this.recordTime = recordTime;
        this.pictureSize = pictureSize;
        this.videoSize = videoSize;
        this.pictureRatio = pictureRatio;
        this.videoRatio = videoRatio;
        this.BlackAndWhite = BlackAndWhite;
        this.picCompress = picCompress;
        this.costomRecordTime = costomRecordTime;
        this.recordSize = recordSize;
    }

    public static CameraStatus getCameraStatus() {
        if (null == cameraStatus) {
            //cameraStatus = new CameraStatus(false, new WaterMark(false, Size.WATERMARK_BIZCAM, null), false, Size.RECORD_9, Size.TYPE_11, Size.LARGE, true, true);
            cameraStatus = getLastSettingInfo();
        }
        return cameraStatus;
    }


    public boolean isFlashOn() {
        return isFlashOn;
    }

    public void setFlashOn(boolean flashOn) {
        isFlashOn = flashOn;
    }

    public WaterMark getWaterMark() {
        return WaterMark;
    }

    public void setWaterMark(WaterMark waterMark) {
        WaterMark = waterMark;
    }

    public boolean isSubLineOn() {
        return isSubLineOn;
    }

    public void setSubLineOn(boolean subLineOn) {
        isSubLineOn = subLineOn;
    }

    public Size getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Size recordTime) {
        this.recordTime = recordTime;
    }

    public Size getPictureSize() {
        return pictureSize;
    }

    public void setPictureSize(Size pictureSize) {
        this.pictureSize = pictureSize;
    }

    public Size getVideoRatio() {
        return videoRatio;
    }

    public void setVideoRatio(Size videoRatio) {
        this.videoRatio = videoRatio;
    }

    public Size getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(Size videoSize) {
        this.videoSize = videoSize;
    }

    public boolean isBlackAndWhite() {
        return BlackAndWhite;
    }

    public void setBlackAndWhite(boolean blackAndWhite) {
        BlackAndWhite = blackAndWhite;
    }

    public boolean isPicCompress() {
        return picCompress;
    }

    public int getCostomRecordTime() {
        return costomRecordTime;
    }

    public void setCostomRecordTime(int costomRecordTime) {
        this.costomRecordTime = costomRecordTime;
    }

    public void setPicCompress(boolean picCompress) {
        this.picCompress = picCompress;
    }

    public Size getPictureRatio() {
        return pictureRatio;
    }

    public void setPictureRatio(Size pictureRatio) {
        this.pictureRatio = pictureRatio;
    }

    public android.util.Size getRecordSize() {
        return recordSize;
    }

    public void setRecordSize(android.util.Size recordSize) {
        this.recordSize = recordSize;
    }

    public enum Size {
        //
//        LARGE("High", CameraHelper.SIZELARGE), MIDDLE("Medium", CameraHelper.SIZEMIDDLE), SMALL("Standard", CameraHelper.SIZESMALL), TYPE_11("1:1", CameraHelper.PREVIEW_11), TYPE_34("3:4", CameraHelper.PREVIEW_43), TYPE_916("9:16", CameraHelper.PREVIEW_169), RECORD_9("9s", 9), RECORD_30("29s", 29), RECORD_60("59s", 59),
//        RECORD_CUSTOM("User-defined", 1), WATERMARK_OFF("Close", 4512), WATERMARK_BIZCAM("BizCam", 4513), WATERMARK_CUSTOM("Custom", 4514);


        LARGE("大", 101), MIDDLE("中", 102), SMALL("小", 103), TYPE_11("1:1", 4771), TYPE_34("3:4", 4772), TYPE_916("9:16", 4773), RECORD_9("9s", 9), RECORD_30("29s", 29), RECORD_60("59s", 59),
        RECORD_CUSTOM("自定义", 1), WATERMARK_OFF("关", 4512), WATERMARK_BIZCAM("商拍", 4513), WATERMARK_CUSTOM("自定义", 4514);
        // 成员变量
        private String name;
        private int index;

        // 构造方法
        private Size(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

    }

    public static class WaterMark {
        boolean isWaterMarkOn;
        Size waterMarkStatus;
        String watermarkUrl;

        public WaterMark(boolean isWaterMarkOn, Size waterMarkStatus, String watermarkUrl) {
            this.isWaterMarkOn = isWaterMarkOn;
            this.waterMarkStatus = waterMarkStatus;
            this.watermarkUrl = watermarkUrl;
        }

        public boolean isWaterMarkOn() {
            return isWaterMarkOn;
        }

        public void setWaterMarkOn(boolean waterMarkOn) {
            isWaterMarkOn = waterMarkOn;
        }

        public String getWatermarkUrl() {
            return watermarkUrl;
        }

        public void setWatermarkUrl(String watermarkUrl) {
            this.watermarkUrl = watermarkUrl;
        }

        public Size getWaterMarkStatus() {
            return waterMarkStatus;
        }

        public void setWaterMarkStatus(Size waterMark) {
            this.waterMarkStatus = waterMark;
        }
    }


    public static CameraStatus getLastSettingInfo() {
        SharePreferences preferences = SharePreferences.instance();
        String temp = (String) preferences.getString(PreferenceConstants.LAST_SETTING, "{}");

        CameraStatus cameraStatus = new CameraStatus(false, new WaterMark(false, Size.WATERMARK_BIZCAM, null), false, Size.RECORD_9, Size.TYPE_11, Size.MIDDLE, Size.TYPE_11, Size.MIDDLE, true, true, 1, null);
        Gson gson = new Gson();
        CameraStatus tempcamerastatus = gson.fromJson(temp, CameraStatus.class);
        if (tempcamerastatus != null) {
            cameraStatus.setSubLineOn(tempcamerastatus.isSubLineOn());
            cameraStatus.setPicCompress(tempcamerastatus.isPicCompress());
            cameraStatus.setFlashOn(tempcamerastatus.isFlashOn());
            cameraStatus.setBlackAndWhite(tempcamerastatus.isBlackAndWhite());
            if (tempcamerastatus.getWaterMark() != null) {
                cameraStatus.setWaterMark(tempcamerastatus.getWaterMark());
            }
            if (tempcamerastatus.getPictureRatio() != null) {
                cameraStatus.setPictureRatio(tempcamerastatus.getPictureRatio());
            }
            if (tempcamerastatus.getPictureSize() != null) {
                cameraStatus.setPictureSize(tempcamerastatus.getPictureSize());
            }
            if (tempcamerastatus.getVideoRatio() != null) {
                cameraStatus.setVideoRatio(tempcamerastatus.getVideoRatio());
            }
            if (tempcamerastatus.getVideoSize() != null) {
                cameraStatus.setVideoSize(tempcamerastatus.getVideoSize());
            }
            if (tempcamerastatus.getPictureSize() != null) {
                cameraStatus.setPictureSize(tempcamerastatus.getPictureSize());
            }
            if (tempcamerastatus.getRecordTime() != null) {
                if (tempcamerastatus.getCostomRecordTime() > 0) {
                    if (tempcamerastatus.getCostomRecordTime() > MAXTIME) {
                        cameraStatus.setCostomRecordTime(MAXTIME);
                    } else {
                        cameraStatus.setCostomRecordTime(tempcamerastatus.getCostomRecordTime());
                    }
                }
                cameraStatus.setRecordTime(tempcamerastatus.getRecordTime());
            }
            if (tempcamerastatus.getRecordSize() != null) {
                cameraStatus.setRecordSize(tempcamerastatus.getRecordSize());
            }
        }

        return cameraStatus;
    }

    public static void saveToFile(CameraStatus cameraStatus) {
        SharePreferences preferences = SharePreferences.instance();
        Gson gson = new Gson();
        gson.toJson(cameraStatus);
        preferences.putString(PreferenceConstants.LAST_SETTING, gson.toJson(cameraStatus));
        CameraStatus.cameraStatus = cameraStatus;
    }

    @Override
    public String toString() {
        return "CameraStatus{" +
                "isFlashOn=" + isFlashOn +
                ", WaterMark=" + WaterMark +
                ", isSubLineOn=" + isSubLineOn +
                ", recordTime=" + recordTime +
                ", pictureRatio=" + pictureRatio +
                ", videoRatio=" + videoRatio +
                ", pictureSize=" + pictureSize +
                ", videoSize=" + videoSize +
                ", BlackAndWhite=" + BlackAndWhite +
                ", picCompress=" + picCompress +
                ", costomRecordTime=" + costomRecordTime +
                ", recordSize=" + recordSize +
                '}';
    }
}
