package com.bcnetech.hyphoto.data;

import java.io.Serializable;

/**
 *图片实体类
 */
public class SDCardMedia implements Serializable {
    public static int VIDEO = 1;
    private String path;//图片地址
    private String thumb_path;//图片缩略图地址
    private long time;
    private String name;
    private int isVideo;
    private String video_duration;

    public SDCardMedia(String path,String thumb_path, long time, String name,int isVideo,String video_duration) {
        this.path = path;
        this.thumb_path = thumb_path;
        this.time = time;
        this.name = name;
        this.isVideo = isVideo;
        this.video_duration = video_duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int isVideo() {
        return isVideo;
    }

    public void setVideo(int video) {
        isVideo = video;
    }

    public String getVideo_duration() {
        return video_duration;
    }

    public void setVideo_duration(String video_duration) {
        this.video_duration = video_duration;
    }

    public String getThumb_path() {
        return thumb_path;
    }

    public void setThumb_path(String thumb_path) {
        this.thumb_path = thumb_path;
    }



}
