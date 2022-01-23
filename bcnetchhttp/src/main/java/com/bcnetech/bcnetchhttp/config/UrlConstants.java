package com.bcnetech.bcnetchhttp.config;

/**
 * Created by wb on 2016/5/3.
 */


public class UrlConstants {

    public static final boolean isEnglish = false;

    public static boolean isTestType = false;
    //是否使用国际化接口
    public static final boolean isWorld_Interface = true;

    /**
     * 测试
     */
//     public static String BASE="http://192.168.0.118:8080/bimage-app";

    /**
     * 线上wwwz.bceasy.comV1/1.0/user/login
     */
    //  public static String BASE = isTestType ? "http://192.168.0.180:8080/bimage-app" : "http://cn.bcyun.com";
    public static String BASE = isTestType ? UrlConstantsTest.BASE : UrlConstantsOnline.BASE;

    /**
     * 商城
     */
    public static String MALL = isTestType ? UrlConstantsTest.MALL : UrlConstantsOnline.MALL;
    /**
     * 全球化
     */
    public static String BASE_WORLD = isTestType ? UrlConstantsTest.WORLD_BASE : UrlConstantsOnline.WORLD_BASE;

    /**
     * 二次登陆
     */
    public static String BASE_RELOGIN_URL = isTestType ? UrlConstantsTest.WORLD_BASE_RELOGIN : UrlConstantsOnline.WORLD_BASE_RELOGIN;

    /**
     * 服务是否开通
     */
    public static String BASE_INSTALLED_APP = isTestType ? UrlConstantsTest.WORLD_BASE_INSTALLED : UrlConstantsOnline.WORLD_BASE_INSTALLED;

    /**
     * 二次登陆
     */
    public static String DEFAUL_WEB_SITE_RELOGIN = BASE_RELOGIN_URL;


    /**
     * 国际化接口
     */
    public static String DEFAUL_INTERNATIONAL_WEB_SITE = isWorld_Interface ? BASE_WORLD : BASE;

    /**
     * 基础接口
     */
    public static String DEFAUL_WEB_SITE = BASE;

    /**
     * 文件上传
     */
    public static String DEFAUL_WEB_SITE_UPLOAD = isTestType ? UrlConstantsTest.DEFAUL_WEB_SITE_UPLOAD : UrlConstantsOnline.DEFAUL_WEB_SITE_UPLOAD;

    /**
     * 图片显示
     */
    public static String DOWNLOAD_PHOTO = isTestType ? UrlConstantsTest.DOWNLOAD_PHOTO : UrlConstantsOnline.DOWNLOAD_PHOTO;

    /**
     * 下载文件
     */
    public static String DOWNLOAD_FILE = isTestType ? UrlConstantsTest.DOWNLOAD_FILE : UrlConstantsOnline.DOWNLOAD_FILE;

    /**
     * 提交注册手机号
     */
    public static final String REGISTER_MOBILE = isWorld_Interface ? "user/register" : "V1/1.0/user/register";

    /**
     * 输入验证码
     */
    public static final String SEND_CODE = isWorld_Interface ? "code/sendCodeNoAuth" : "V1/1.0/user/sendCode";


    /**
     * 提交用户信息
     */
    public static final String USER_INFO = "V1/1.0/register/user_info";

    /**
     * 注册用户手机号是否存在验证
     */
    public static final String EXISTS_MOBILE = "V1/1.0/register/exists/mobile";

    /**
     * 重取验证码
     */
    public static final String SEND_CHECKCODE = "V1/1.0/register/send_checkcode";

    /**
     * 提交手机号
     */
    public static final String FORGET_PASSWD = "V1/1.0/passport/forget_passwd";

    /**
     * 提交验证码
     */
    public static final String FORGET_VERIFY = "V1/1.0/sendCode";

    /**
     * 忘记密码
     */
    public static final String FORGET_RESET = isWorld_Interface ? "user/refindPwd" : "V1/1.0/user/forgetPassword";
    /**
     * 修改密码
     */
    public static final String CHANGE_PASSWORD = isWorld_Interface ? "user/resetPwd" : "V1/1.0/user/modifyPassword";
    /**
     * 登陆
     */
    public static final String LOGIN = isWorld_Interface ? "user/login" : "V1/1.0/user/login";
    /**
     * 登出
     */
    public static final String LOGINOUT = "V1/1.0/logout";
    /**
     * 修改昵称和头像
     */
    public static final String RESET_USERINFO = "user/updatePassport";

    /**
     * 修改昵称
     */
    public static final String RESET_USERINFO_NAME = "V1/1.0/user/modifyNickName";

    /**
     * 修改头像
     */
    public static final String RESET_USERINFO_HEAD = "V1/1.0/user/modifyAvatar";


    /**
     * 上传头像
     */
    public static String UPLOAD_HEAD = "V1/1.0/file/upload";
    /**
     * 上传云图库
     */
    public static String PICS = "V1/1.0/photo/upload";
    /**
     * 上传云图库_视频
     */
    public static String VIDEOS = "V1/1.0/video/upload";
    /**
     * 添加分组到指定分组
     */
    public static String GALLERY = "V1/2.0/catalog/list";
    /**
     * 共享文件夹列表
     */
    public static String EDU_CATALOG_GALLERY = "V1/1.0/catalog/eduCatalogList";
    /**
     * 添加新文件夹
     */
    public static String ADDNEWKIND = "V1/1.0/catalog/add";
    /**
     * 分类下的所有图片
     */
    public static String GALLERY_PHOTOS = "V1/1.0/photo/list";

    /**
     * 分类下的所有图片
     */
    public static final String GALLERY_PHOTOS_ALL = "V1/1.0/storage/all";
    /**
     * 分类下的所有视频
     */
    public static String GALLERY_VIDEOS = "V1/1.0/video/listPaging";
    /**
     * 分享参数
     */
    public static String SHARE_PARMS = "V1/1.0/share/upload";

    /**
     * 意见反馈
     */
    public static final String FEED_BACK = "V1/1.0/feedback/add";
    /**
     * 模版中心list
     */
    public static String TEMPLATE_LIST = "V1/1.0/layout/findList";
    /**
     * 模版中心保存模版
     */
    public static String TEMPLATE_ADD = "V1/1.0/userLayout/add";
    /**
     * 模版中心作者
     */
    public static String TEMPLATE_USER = "V1/1.0/user/findOne";
    /**
     * 用户模版list
     */
    public static String USER_LIST = "V1/1.0/userLayout/findList";
    /**
     * 高大上 新版本(V1/2.0/params/shareParamPage)
     * 旧版本(V1/1.0/params/userParamPage)
     * 分享参数中心list
     */
    //public static String PARAMS_LIST = "/params/findList";
    public static final String PARAMS_LIST = "V1/2.0/params/shareParamPage";

    public static String PARAMS_INFO = "V1/1.0/params/shareParamDetail";
    /**
     * 高大上新版本 (/file/getLightData)
     * 旧版本 (/file/getParams)
     */
    public static final String GET_PARAMS = "file/getLightData";

    /**
     * 高大上 新版本(V1/2.0/params/userParamPage)
     * 旧版本(V1/1.0/params/userParamPage)
     * 预设参数下载记录列表
     */
    public static final String PARAMS_DOWNLOAD_LOG = "V1/2.0/params/userParamPage";

    /**
     * 高大上 新版本(2.0)V1/2.0/params/downloadParams
     * 旧版本(1.0)V1/1.0/params/downloadParams
     * 下载预设参数
     */
    public static final String PARAMS_DOWNLOAD = "V1/2.0/params/downloadParams";

    /**
     * 个人预设参数删除
     */
    public static final String SHARE_PARAM_DELETE = "V1/2.0/params/delete";
    /**
     * 检查预设参数是否删除
     */
    public static final String CHECK_PARAM_DELETE = "V1/1.0/params/checkDeleted";

    /**
     * 验证原来的手机
     */
    public static String VERIFY_OLD_NUM = "V1/1.0/user/validatePhone";
    /**
     * 更换账号
     */
    public static String CHANGE_ACCOUNT = "V1/1.0/user/changeAccount";
    /**
     * 用户协议
     */
    public static String USER_PROTOCOL = "V1/1.0/user/protocol";

    /**
     * 保存个人预设参数
     */
    public static String SHARE_OWNERUP_PARMS = "V1/1.0/share/ownerUpload";
    /**
     * 市场预设参数总分类列表
     */
    public static final String MARKET_PARAMETAER_CATALOG = "V1/1.0/catalog/paramCatalog";
    /**
     * 市场预设参数删除列表
     */
    public static String MARKET_PARAMS_CHECK_DELETED = "V1/1.0/params/checkDeleted";

    /**
     * 设备连接
     */
    public static final String DEVICE_CONNECT = "V1/1.0/device/connect";

    /**
     * 国外用户注册
     */
    public static String REGISTER_ALIEN = "V1/1.0/user/registerAlien";

    /**
     * 发送邮箱验证码
     */
    public static final String SEND_EMAIL = isWorld_Interface ? "user/sendEmail" : "V1/1.0/user/sendEmail";


    /**
     * 删除图片
     */
    public static String PHOTO_DELETE = "V1/1.0/photo/delete";

    /**
     * 批量删除图片
     */
    public static final String PHOTO_DELETES = "V1/1.0/photo/deletes";

    /**
     * 国外用户注册2
     */
    public static final String REGISTER_ALIEN2 = "V1/1.0/user/registerAlien2";

    /**
     * 查询用户是否注册
     */
    public static final String USER_ISEXIT = "V1/1.0/user/isExist";

    /**
     * 查询用户是否注册(国际化)
     */
    public static final String USER_ISEXIT_World = "user/isExist";


    /**
     * 校验验证码
     */
    public static final String VALIDATE_CODE = isWorld_Interface ? "code/validateCode" : "V1/1.0/user/validateCode";

    /**
     * 找回密码
     */
    public static final String REFIND_PWD = "V1/1.0/user/refindPwd";

    /**
     * 获取推荐光影参数
     */
    public static String PARAMS_SEARCH = "V1/1.0/params/search";

    /**
     * 上传视频封面
     */
    public static String UPLOAD_CORVER = "V1/1.0/cover/upload";

    /**
     * 上传商拍光影参数
     */
    public static String UPLOAD_PARAMS = "V1/1.0/params/uploadParam";
    /**
     * 上传的图片的check
     */
    public final static String FILE_CHECK = "file/check";
    /**
     * 上传的分片列表
     */
    public static String FILE_UPLOAD = "file/upload";

    /**
     * 上传云图库信息
     */
    public static final String FILE_UPDATEINFO = "file/updateInfo";

    /**
     * 云图是否初始化
     */
    public static final String SETTING_CHECK = "setting/check";


    /**
     * 云图是否初始化
     */
    public static final String SETTING_INIT = "setting/init";

    /**
     * 上传到商拍优图
     */
    public static final String UPLOADYOUTU = "scope/modifyFileScope";

    /**
     * 25d
     */
    public static String SHARE25DURL = isTestType ? UrlConstantsTest.SHARE25DURL : UrlConstantsOnline.SHARE25DURL;


}
