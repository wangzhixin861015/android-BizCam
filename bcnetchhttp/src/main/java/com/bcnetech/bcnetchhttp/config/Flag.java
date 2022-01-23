package com.bcnetech.bcnetchhttp.config;

import android.os.Environment;

import com.bcnetech.bcnetchhttp.R;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;

import java.io.File;


/**
 * Created by wb on 2016/5/3.
 */
public class Flag {
    public static String USER_APK_LOCAL = Environment
            .getExternalStorageDirectory() + "/BizCam/Downloader/apk/";
    public static final String BaseData = "/Android/data/com.bcnetech.hyphoto/files";
    public static final String BaseCache = "/Android/data/com.bcnetech.hyphoto/cache";

    /**
     *
     */
    public static final boolean isEnglish=false;

    /**
     * 图片本地缓存地址
     */
    public static final String LOCAL_IMAGE_PATH = Environment.getExternalStorageDirectory() + BaseCache + "/local_img/";//图片本地缓存地址

    public static final String APP_CAMERAL = Environment.getExternalStorageDirectory() + BaseData + "/AppCameral/";//拍照地址和编辑图片地址

    public static final String AI360_PATH=Environment.getExternalStorageDirectory() + BaseData + "/Panoramas/"; //360地址

    public static final String UPLOAD_PATH = Environment.getExternalStorageDirectory() + BaseData + "/Upload/";// 上传的小图


    public static final String SHARE_PATH = Environment.getExternalStorageDirectory() + BaseData + "/Share/";// 分享的小图


    public static final String ANDROID_CAMERAL = Environment.getExternalStorageDirectory() + "/DCIM/Camera/";//android  相册地址

    public static final String NATIVESDFILE = Environment.getExternalStorageDirectory() + "/Bizcam/";//单独保存文件夹
    public static final String FENPIAN = Environment.getExternalStorageDirectory() +BaseData+ "/FenPian/";//分片保存文件夹

    public static final String FFMPEG_CACHE =Environment.getExternalStorageDirectory() + Flag.BaseCache + File.separator + "ffmpeg_record" + File.separator;//FFmpeg录制缓存地址
    /**
     * 个人头像
     */
    public static final String PERSION_IMAGE = Environment.getExternalStorageDirectory() + BaseCache + "/head/" + LoginedUser.getLoginedUser().getNickname() + "/";
    /**
     * 意见反馈
     */
    public static final String FEEDBACK_IMAGE = Environment.getExternalStorageDirectory() + BaseCache + "/feedback/" + LoginedUser.getLoginedUser().getNickname() + "/";


    public static final String NATIVESDFILE2 = Environment.getExternalStorageDirectory() + "/Pictures/Bizcam/";//导出文件夹

    public static final String PRESET_IMAGE_PATH = Environment.getExternalStorageDirectory() + BaseData + "/PresetImage/";//预设参数缓存地址

    public static final String WATERMARK_PATH = Environment.getExternalStorageDirectory() + BaseData + "/WaterMark/";//水印地址

    public static final String APKSAVE_PATH = Environment.getExternalStorageDirectory() + BaseData + "/File/";//下载apk地址

    /**
     * 图片下载默认图片
     */
    public static final int DEFAULT_AVT = R.color.default_loading;

    /**
     * 下载失败图片
     */
    public final static int APP_CAMERAL_TYPE = 11;//app相册
    public final static int PHONE_CAMERAL_TYPE = 12;//手机相册


    public final static String LANGUAGE_KEY = "language";
    public final static String ENGLISH_US = "en_rUS";
    public final static String CHINAE = "zh_rCN";
    public final static String CHINAE_HK = "zh_rHK";
    public final static String CHINAE_TW = "zh_rTW";


    public final static int NULLCODE = -1;

    public final static int IMAGE_PARMS_ACTIVITY = 10001;//ImageParmsActivity
    public final static int PART_PARMS_ACTIVITY = 10002;//PartParmsActivity
    public final static int MATTING_PARMS_ACTIVITY = 10003;//BizMattingNewActivity
    public final static int IMAGESELECTOR_ACTIVITY = 10004;//ImageSelectorActivity

    public final static String IMAGESELECTOR = "imageSelector";//导入图片
    public final static String IMAGE_DATA = "imageparms";//图片配置文件
    public final static String PRESET_PARMS = "PresetParms";//预设参数
    public final static String PRESET_PARMS_DOWNLOAD = "PresetParms_download";//预设参数下载
    public final static String PARMS = "parms";//参数调整
    public final static String CAMERA_TYPE = "cameraType";//相机类型
    public final static String PART_PARMS = "part_parms";//参数调整
    public final static String PARMS_TYPE = "parms_type";//参数类型
    public final static String RECODER_TYPE = "recoder_type";//视频播放类型

    public final static String CURRENT_PART_POSITION = "current_part_position";//当前部分修改操作节点

    public final static String HISTORY_IMG_UPLOAD = "history_img_upload";
    /**
     * 白平衡
     */
    public final static String WHITEBALANCE = "whitebalance";
    public final static String TYPE_AUTO = "auto";
    public final static String TYPE_INCANDESCENT = "incandescent";
    public final static String TYPE_FLUORESCENT = "fluorescent";
    public final static String TYPE_DAYLIGHT = "daylight";
    public final static String TYPE_CLOUD = "cloudy-daylight";
    public final static String TYPE_MANUAL = "manual";
    /**
     * iso
     */
    public final static String ISO = "ISO";
    public final static String ISO100 = "ISO100";
    public final static String ISO200 = "ISO200";
    public final static String ISO400 = "ISO400";
    public final static String ISO800 = "ISO800";
    public final static String ISO1600 = "ISO1600";
    public final static String ISO3200 = "ISO3200";

    /**
     * focus
     */
    public final static String FOCUS = "focus";
    public final static String FOCUS_INFINITY = "infinity";
    public final static String FOCUS_MACRO = "macro";
    public final static String FOCUS_CONTINUOUS = "continuous-picture";
    /**
     * sec
     */
    public final static String SEC = "sec";

    /**
     * 闪光灯
     */
    public final static String FLASH = "flash";

    /**
     * 曝光补偿
     */
    public final static String EV = "ev";

    /**
     * 预览尺寸
     */
    public final static String PREVIEW = "preview";

    /**
     * 图片类型
     */
    public final static int TYPE_PIC = 1;
    public final static int TYPE_VIDEO = 2;
    public final static int TYPE_AI360 = 3;
    /**
     *预览类型
     */
    public final static int VIDEO_PREVIEW = 1200;

    /**
     * 注册登录类型
     */
    public final static String LOGIN_TYPE = "login_type";
    public final static int TYPE_LOGIN = 21;//登录
    public final static int TYPE_REGIST = 22;//注册
    public final static int TYPE_FIND_PSW = 23;//找回密码
    public final static int TYPE_CHANGE_NUM1 = 24;//修改手机号码
    public final static int TYPE_CHANGE_NUM2 = 25;//修改手机号码
    public final static int TYPE_APPEAL = 26;//申诉
    public final static int TYPE_CHANGE_PSW = 27;//修改密码
    public final static int TYPE_WELCOME_FIRST_LOGIN = 28; //引导页登录
    public final static int TYPE_WELCOME_FIRST_REGIST = 29; //引导页注册
    public final static int LOGIN_MAIN = 101;//登录后跳转用户中心
    public final static String SHAREPARAMS = "shareParms";

    public final static int CLOUDWATERMARK = 7;
    public final static int CLOUDFILE = 9;


    //抠图引导页次数
    public final static String KEY_MATTING_GUIDE_COUNT = "matting_guide_count";

    //欢迎页引导
    public final static String KEY_WELCOME_GUIDE = "welcome_guide";

    //背景修复引导页次数
    public final static String KEY_REPAIR_GUIDE_COUNT = "repair_guide_count";

    //拍照引导页次数
    public final static String KEY_CAMERA_GUIDE_COUNT = "camera_guide_count";

    public final static String SAVE_ERROR_DATA = "save_error_data";

    public final static String CACHE_TITLE = "cache_title";

    public final static String CACHE_CLOUD = "cache_cloud";

    //最大的图片选择数
    public final static String MAX_SELECT_COUNT = "max_select_count";
    //是否单选
    public final static String IS_SINGLE = "is_single";
    //初始位置
    public final static String POSITION = "position";

    //初始位置
    public final static String IS_CONFIRM = "is_confirm";

    public final static int SDCARD_RESULT_CODE = 0x00000012;

    //
    public final static int PICSIZE = 3160;
    public final static int VIDEOSIZE = 3161;
    public final static int PICRATIO = 3162;
    public final static int VIDEODURATION = 3163;
    public final static int SETTINGFLASH = 3164;
    public final static int SUBLINE = 3165;
    public final static int WATERMARK = 3166;
    public final static int BLACKANDWHITE = 3167;
    public final static int PICCOMPRESS = 3168;
    public final static int VIDEORATIO = 3169;

    public final static int SIZE_L = 101;
    public final static int SIZE_M = 102;
    public final static int SIZE_S = 103;

    public final static String CAMERATYPE = "CAMERA_TYPE";


    //没有上传任务
    public static int UPLOAD_NONE=0;
    //上传中
    public static int UPLOAD_UPLOADING=1;
    //上传完成
    public static int UPLOAD_SUCCESS=2;
    //上传失败
    public static int UPLOAD_FAIL=3;
    //空间已满
    public static int UPLOAD_FULL=4;
    //网络状态改变
    public static int UPLOAD_NETCHANGE=5;
    //准备上传(非WIFI状态下)
    public static int UPLOAD_REUPLOAD=6;

    //智拍-商拍酷宝
    public static int BIZCAM_HELP_AI_COBOX=1000;
    //智拍-商拍魔盒
    public static int BIZCAM_HELP_AI_COLINK=1001;
    //画笔
    public static int BIZCAM_HELP_PAINT=1002;
    //抠图
    public static int BIZCAM_HELP_MATTING=1003;
    //修复
    public static int BIZCAM_HELP_REPAIR=1004;


}

