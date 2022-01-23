package com.bcnetech.bcnetchhttp.config;

/**
 * Created by wb on 2016/5/3.
 */

public class UrlConstantsOnline {

    /**
     * 线上wwwz.bceasy.com/V1/1.0/user/login
     */
    public static String BASE = UrlConstants.isEnglish ? "https://www.bceasy.com/media-api/" : "https://cn.bcyun.com/media-api/";

    /**
     * 国际化接口
     */
    public static String WORLD_BASE = "https://cn.bcyun.com/user-api/";

    /**
     * 二次登陆
     */
    public static String WORLD_BASE_RELOGIN = UrlConstants.isEnglish ? "https://www.bceasy.com/user-login-api/" : "https://cn.bcyun.com/user-login-api/";

    /**
     * 初始化服务
     */
    public static String WORLD_BASE_INSTALLED = UrlConstants.isEnglish ? "https://www.bceasy.com/app-api/" : "https://cn.bcyun.com/app-api/";

    //【链接】全部商品
    public static String MALL = "https://static.bcyun.com/html/app_store/index.html";

    /**
     * 图片上传
     */
    public static String DEFAUL_WEB_SITE_UPLOAD = UrlConstants.isEnglish ? "https://www.bceasy.com/file-api/" : "https://cn.bcyun.com/file-api/";

    /**
     * 文件下载
     */
    public static String DOWNLOAD_FILE = UrlConstants.isEnglish ? "https://www.bceasy.com/media-api/fileDownload/getFile/download/" : "https://cn.bcyun.com/media-api/fileDownload/getFile/download/";

    /**
     * 图片显示
     */
    public static String DOWNLOAD_PHOTO = UrlConstants.isEnglish ? "https://file.bceasy.com/file/" : "https://cnfile.bcyun.com/file/";
    /**
     * 25d
     */
    public static String SHARE25DURL = UrlConstants.isEnglish ? "https://www.bceasy.com/25d/index.html?fileId=" : "https://cn.bcyun.com/25d/index.html?fileId=";

    //    /**
//     * 视频下载
//     */
//    public static String DOWNLOAD_VIDEO = UrlConstants.isEnglish ? "http://www.bceasy.com/V1/1.0/video/file/" : "https://cnfile.bcyun.com/V1/1.0/video/file/";

//
//    public static String DOWNLOAD_AI360  = UrlConstants.isEnglish  ? "http://www.bceasy.com/fileDownload/getFile/download/" : "https://cnfile.bcyun.com/fileDownload/getFile/download/";


}
