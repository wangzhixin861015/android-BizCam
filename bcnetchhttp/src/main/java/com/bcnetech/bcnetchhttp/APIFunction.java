package com.bcnetech.bcnetchhttp;


import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.bean.request.ChangePasswordBody;
import com.bcnetech.bcnetchhttp.bean.request.CheckParamDeleteBody;
import com.bcnetech.bcnetchhttp.bean.request.FeedBackBody;
import com.bcnetech.bcnetchhttp.bean.request.FileCheckBody;
import com.bcnetech.bcnetchhttp.bean.request.ForgetResetBody;
import com.bcnetech.bcnetchhttp.bean.request.LoginUserBody;
import com.bcnetech.bcnetchhttp.bean.request.ModifyScope;
import com.bcnetech.bcnetchhttp.bean.request.ParamsDownloadBody;
import com.bcnetech.bcnetchhttp.bean.request.ParamsListBody;
import com.bcnetech.bcnetchhttp.bean.request.PresetLoadBody;
import com.bcnetech.bcnetchhttp.bean.request.RegisterEmailBody;
import com.bcnetech.bcnetchhttp.bean.request.RegisterMobileBody;
import com.bcnetech.bcnetchhttp.bean.request.ResetHeadBody;
import com.bcnetech.bcnetchhttp.bean.request.ResetNameBody;
import com.bcnetech.bcnetchhttp.bean.request.SendCodeBody;
import com.bcnetech.bcnetchhttp.bean.request.SendEmailBody;
import com.bcnetech.bcnetchhttp.bean.request.UserIsExistBody;
import com.bcnetech.bcnetchhttp.bean.request.ValidateCodeBody;
import com.bcnetech.bcnetchhttp.bean.response.BimageUploadingChunk;
import com.bcnetech.bcnetchhttp.bean.response.CloudPicData;
import com.bcnetech.bcnetchhttp.bean.response.LoginReceiveData;
import com.bcnetech.bcnetchhttp.bean.response.MarketParamsIndexListData;
import com.bcnetech.bcnetchhttp.bean.response.MarketParamsListData;
import com.bcnetech.bcnetchhttp.bean.response.Preinstail;
import com.bcnetech.bcnetchhttp.bean.response.PresetDownManageData;
import com.bcnetech.bcnetchhttp.bean.response.ShareOwnerParmsData;
import com.bcnetech.bcnetchhttp.config.UrlConstants;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by yhf on 2018/9/12.
 */

public interface APIFunction {
    //登录
    @POST
    Observable<BaseResult<LoginReceiveData>> login(@Body LoginUserBody requestBody,@Url String url);

    //云图库
    @GET(UrlConstants.GALLERY_PHOTOS_ALL)
    Observable<BaseResult<List<CloudPicData>>> getCloud();

    //预设参数下载记录列表
    @POST(UrlConstants.PARAMS_DOWNLOAD_LOG)
    Observable<BaseResult<PresetDownManageData>> userParamPage(@Body PresetLoadBody presetLoadBody);

    //获取参数详情
    @GET(UrlConstants.GET_PARAMS)
    Observable<Preinstail> getLightData(@Query("fileId") String fileId);

    //发送验证码
    @POST(UrlConstants.SEND_CODE)
    Observable<BaseResult<Object>> sendCode(@Body SendCodeBody sendCodeBody);

    //手机号注册
    @POST(UrlConstants.REGISTER_MOBILE)
    Observable<BaseResult<Object>> registerMoble(@Body RegisterMobileBody registerMobleBody);
    //国外注册
    @POST(UrlConstants.REGISTER_MOBILE)
    Observable<BaseResult<Object>> registerAlien(@Body RegisterEmailBody registerEmailBody);
    //忘记密码
    @POST(UrlConstants.FORGET_RESET)
    Observable<BaseResult<Object>> forgetReset(@Body ForgetResetBody forgetResetBody);

    //用户是否存在
    @GET(UrlConstants.USER_ISEXIT)
    Observable<BaseResult<Object>> userIsExist(@Query("login") String login);

    //用户是否存在(国际化)
    @POST(UrlConstants.USER_ISEXIT_World)
    Observable<BaseResult<Object>> userIsExistWorld(@Body UserIsExistBody userIsExistBody);

    //验证验证码
    @POST(UrlConstants.VALIDATE_CODE)
    Observable<BaseResult<Object>> vaildateCode(@Body ValidateCodeBody validateCodeBody);

    //市场预设参数总分类列表
    @POST(UrlConstants.MARKET_PARAMETAER_CATALOG)
    Observable<BaseResult<MarketParamsIndexListData>> marketParamCatalog();

    //市场参数列表
    @POST(UrlConstants.PARAMS_LIST)
    Observable<BaseResult<MarketParamsListData>> paramsList(@Body ParamsListBody paramsListBody);

    //检查预设参数是否删除
    @POST(UrlConstants.CHECK_PARAM_DELETE)
    Observable<BaseResult<MarketParamsListData>> checkParamDelete(@Body CheckParamDeleteBody checkParamDeleteBody);

    //删除云图库图片
    @GET(UrlConstants.PHOTO_DELETES)
    Observable<BaseResult> deleteCloud(@Query("files") String file);

    //下载文件
    @GET()
    Observable<ResponseBody> downloadFile(@Url String fileUrl);

    //下载预设参数保存到个人空间下
    @POST(UrlConstants.PARAMS_DOWNLOAD)
    Observable<BaseResult<ShareOwnerParmsData>> paramsDownload(@Body ParamsDownloadBody paramsDownloadBody);

    //意见反馈
    @POST(UrlConstants.FEED_BACK)
    Observable<BaseResult<Object>> feedBack(@Body FeedBackBody feedBackBody);

    //修改密码
    @POST(UrlConstants.CHANGE_PASSWORD)
    Observable<BaseResult<Object>> changePassword(@Body ChangePasswordBody changePasswordBody);

    //光影参数删除
    @GET(UrlConstants.SHARE_PARAM_DELETE)
    Observable<BaseResult<Object>> shareParamDelete(@Query("fileId") String fileId);

    //分片检查
    @POST(UrlConstants.FILE_CHECK)
    Observable<BaseResult<BimageUploadingChunk>> fileCheck(@Body FileCheckBody fileCheckBody);

    //上传文件
    @POST
    Observable<BaseResult<Object>> uploadFile(@Url String url, @Body MultipartBody body);

    //上传参数
    @POST(UrlConstants.FILE_UPDATEINFO)
    Observable<BaseResult<Object>> uploadInfo(@Body HashMap<String, Object> hashMap);

    //修改头像
    @POST(UrlConstants.RESET_USERINFO)
    Observable<BaseResult<Object>> resetUserInfoHead(@Body ResetHeadBody resetHeadBody);

    //修改昵称
    @POST(UrlConstants.RESET_USERINFO)
    Observable<BaseResult<Object>> resetUserInfoName(@Body ResetNameBody resetNameBody);

    //上传COBOX信息
    @POST(UrlConstants.DEVICE_CONNECT)
    Observable<BaseResult<Object>> uploadCoboxInfo(@Body HashMap<String, String> hashMap);



    //发送找回密码邮件
    @POST(UrlConstants.SEND_EMAIL)
    Observable<BaseResult<Object>> sendEmail(@Body SendEmailBody sendEmailBody);

    //商拍优图扫码登录
    @POST
    Observable<BaseResult<Object>> bizCamUToLogin(@Url String url);

    //检查服务是否开通
    @POST
    Observable<BaseResult<Object>> settingCheck(@Url String url);

    //开通服务
    @POST
    Observable<BaseResult<Object>> settingInit(@Url String url);

    //上传到商拍优图
    @POST(UrlConstants.UPLOADYOUTU)
    Observable<BaseResult<Object>> bizCamUToUpload(@Body ModifyScope modifyScope);
}
