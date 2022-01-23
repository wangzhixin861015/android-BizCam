package com.bcnetech.bcnetchhttp.config;

/**
 * Created by wb on 2016/5/3.
 */

public class UrlConstantsTest {

    public static boolean isTest_180 = true;
    /**
     * 测试
     */

//     public static String BASE="http://192.168.0.118:8080/bimage-app";

    /**
     * 线上wwwz.bceasy.com/V1/1.0/user/login
     */
    public static String BASE = isTest_180 ? "http://192.168.0.180:60004/" : "http://192.168.0.118:8080/bimage-app/";
//    public static String BASE = isTest_180 ? "http://192.168.6.54:60004/" : "http://192.168.6.54:60004/";

    public static String WORLD_BASE=isTest_180?"http://192.168.0.180:20002/":"http://192.168.0.180:20002/";

    public static String WORLD_BASE_RELOGIN=isTest_180?"http://192.168.0.180:10002/":"http://192.168.0.180:10002/";

    public static String WORLD_BASE_INSTALLED=isTest_180?"http://192.168.0.180:10003/":"http://192.168.0.180:10003/";

    public static String MALL="http://192.168.0.180:8088/";
    /**
     * 接口
     */
    public static String DEFAUL_WEB_SITE = BASE;
    public static String DEFAUL_WEB_SITE_UPLOAD = isTest_180 ?  "http://192.168.0.180:8081/": "http://192.168.0.118:8081/";
    /**
     * 图片下载地址
     */
    public static String DOWNLOAD_PHOTO = isTest_180 ? "http://192.168.0.180:60004/V1/1.0/file/" : "http://192.168.0.118:8080/bimage-app/V1/1.0/file/";


    /**
     * 下载文件
     */
    public static String DOWNLOAD_FILE  = isTest_180 ? "http://192.168.0.180:60004/fileDownload/getFile/download/" : "http://192.168.0.118:8080/bimage-app/V1/1.0/video/file/";

    /**
     * 25d
     */
    public static String SHARE25DURL =  "http://www.dev.com/25d/index.html?fileId=";
    /**
     * 视频下载
     */
//    public static String DOWNLOAD_VIDEO = isTest_180 ? "http://192.168.0.180:60004/V1/1.0/video/file/" : "http://192.168.0.118:8080/bimage-app/V1/1.0/video/file/";

}
