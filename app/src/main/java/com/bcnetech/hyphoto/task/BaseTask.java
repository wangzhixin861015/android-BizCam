package com.bcnetech.hyphoto.task;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog3;
import com.bcnetech.bcnetechlibrary.util.HttpUtil;
import com.bcnetech.hyphoto.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by wb on 2016/5/3.
 */
public class BaseTask<T> extends NetAsyncTask<T> {
    protected static final int SUCCESS = 200;
    Activity activity;
    public BaseTask(Activity activity, boolean showprogress) {
        super(activity);
        this.activity = activity;
        if (showprogress) {
            setProgressDialog3(new DGProgressDialog3(activity ,false,progressTip()));
            // setProgressDialog(dgProgressDialog);
        }
    }

    public BaseTask(Activity activity) {
        super(activity);
        this.activity = activity;
        setProgressDialog3(new DGProgressDialog3(activity, true,progressTip(),false));
    }

    @Override
    protected Result<T> onHttpRequestBase(Object... params) {
        // 访问前先判断是否有网络
        if (!HttpUtil.isNetworkAvailable(activity, LoginedUser.getLoginedUser().isonlywifi(), false)) {
            Result result = new Result();
            result.setSuccess(false);
            result.setMessage(activity.getResources().getString(R.string.network_disconn));
            return result;
        }
        // 正真的网络操作
        return onHttpRequest(params);
    }

    protected Result<T> onHttpRequest(Object... params) {
        // 正真的网络操作
        Result result = new Result();
        result.setSuccess(false);
        result.setMessage(activity.getResources().getString(R.string.network_conn_fail));
        return result;
    }

    /**
     * 加载中的提示语
     *
     * @return
     */
    protected String progressTip() {
        return activity.getResources().getString(R.string.waiting_please);
    }


    //=========================================================================
    //  业务逻辑get、post方法
    //=========================================================================

    /**
     * Json-get参数方式提交
     *
     * @param url
     * @param getParam
     * @return
     */
    protected Result<T> jsonGet(String url, Map<String, String> getParam, String token) {
        if (null == getParam) {
            getParam = new HashMap<String, String>();
        }
//        OkHttpUtils.ResponseResult response = OkHttpUtils.jsonGet(UrlConstants.DEFAUL_WEB_SITE
//                , url, getParam, token, null);

        return setResult(null);
    }

    /**
     * Json-get参数方式提交
     *
     * @param url
     * @param getParam
     * @return
     */
    protected Result<T> jsonGet2(String url, Map<String, String> getParam, String token) {
        if (null == getParam) {
            getParam = new HashMap<String, String>();
        }
//        OkHttpUtils.ResponseResult response = OkHttpUtils.jsonGet(UrlConstants.DEFAUL_WEB_SITE_UPLOAD
//                , url, getParam, token, null);

        return setResult(null);
    }

    /**
     * Json-post参数方式提交
     *
     * @param url
     * @param getParam
     * @param postParam
     * @return
     */
    protected Result<T> jsonPost(String url, Map<String, String> getParam,
                                 Map<String, String> postParam, String secret, String tokenid, String workspaceid) {
        String jsonStr;
        if (null == postParam) {
            jsonStr = "";
        } else {
            jsonStr = toBodyJsonByMap(postParam);
        }
        return jsonPost2(url, getParam, jsonStr, secret, tokenid, workspaceid);
    }


    protected Result<T> jsonPostObject(String url, Map<String, String> getParam, Object searchData, String secret, String tokenid, String workspaceid) {
        String jsonStr;
        if (null == searchData) {
            jsonStr = "";
        } else {
            jsonStr = toBodyJsonByObject(searchData);
        }
        return jsonPost2(url, getParam, jsonStr, secret, tokenid, workspaceid);
    }

    protected Result<T> jsonPostObjectUpload(String url, Map<String, String> getParam, Object searchData, String secret, String tokenid, String workspaceid) {
        String jsonStr;
        if (null == searchData) {
            jsonStr = "";
        } else {
            jsonStr = toBodyJsonByObject(searchData);
        }
        return jsonPostUpload(url, getParam, jsonStr, secret, tokenid, workspaceid);
    }

    /**
     * Json-post参数方式提交(无封装JsonStr)
     *
     * @param url
     * @param getParam
     * @param jsonStr
     * @return
     */
    protected Result<T> jsonPost2(String url, Map<String, String> getParam,
                                  String jsonStr, String secret, String tokenid, String workspaceid) {
        if (null == getParam) {
            getParam = new HashMap<String, String>();
        }
//        OkHttpUtils.ResponseResult response = OkHttpUtils.jsonPost(UrlConstants.DEFAUL_WEB_SITE
//                , url, getParam, jsonStr, secret, tokenid, workspaceid);
        return setResult(null);
    }

    protected Result<T> jsonPostUpload(String url, Map<String, String> getParam,
                                       String jsonStr, String secret, String tokenid, String workspaceid) {
        if (null == getParam) {
            getParam = new HashMap<String, String>();
        }
//        OkHttpUtils.ResponseResult response = OkHttpUtils.jsonPost(UrlConstants.DEFAUL_WEB_SITE_UPLOAD
//                , url, getParam, jsonStr, secret, tokenid, workspaceid);
        return setResult(null);
    }


    //=========================================================================
    //  文件上传方法
    //=========================================================================

    /**
     * okHTTP --上传文件
     *
     * @param url
     * @param filePathName
     * @return
     */
    protected Result<T> okHttpUpload(String url, String filePathName, String tokenid) {
//        OkHttpUtils.ResponseResult response = OkHttpUtils.upload(url, filePathName, tokenid);
        return setResult(null);
    }

    /**
     * okHTTP --上传多文件
     *
     * @param url
     * @param filePathNames
     * @param fileKeys
     * @return
     */

    protected Result<T> okHttpUpload(String url, Map<String, Object> map, String[] filePathNames, String[] fileKeys, String tokenid) {
//        OkHttpUtils.ResponseResult response = OkHttpUtils.upload2(UrlConstants.DEFAUL_UPLOAD_PIC + url, map, filePathNames, fileKeys, tokenid);
        return setResult(null);
    }

    private Result<T> setResult(String a) {
//        Result<T> result = new Result<T>();
//        if (-1 == response.statusCode) {
//            result.setSuccess(false);
//            result.setMessage(response.reasonPhrase);
//            result.setCode(response.statusCode);
//        } else if (200 == response.statusCode) {
//            // 正常200
//            result.setSuccess(true);
//            result.setMessage(response.resultStr);
//            result.setCode(response.statusCode);
//
//            if (response.resultStr != null) {
//                HttpBaseData httpBaseData = JsonUtil.Json2T(result.getMessage(), HttpBaseData.class);
//                if (httpBaseData != null) {
//                    if (httpBaseData.getCode() == 10010) {
//                        result.setSuccess(false);
//                        result.setMessage(activity.getResources().getString(R.string.token_fail));
//                        SharePreferences sp = SharePreferences.instance();
//                        sp.putBoolean("isFirstIn", true);
//                        //      CostomToastUtil.toast(activity.getResources().getString(R.string.token_fail));
////                        LoginPresenter.startAction(activity, Flag.TYPE_LOGIN);
//                        getLoginedUser().setLogined(false);
//                        getLoginedUser().quitLogin();
//                        UploadManager.getInstance().dellAllRunTask();
//                        Intent intent;
//                        if (Flag.isEnglish) {
//                            intent = new Intent(activity, MainUsActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra("isToken", true);
//                        } else {
//                            intent = new Intent(activity, MainActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra("isToken", true);
//                        }
//                        activity.startActivity(intent);
//                    }
//                }
//            }
//        } else {
//
//            result.setCode(response.statusCode);
//            // 非200的状态码
//            result.setSuccess(false);
//            result.setMessage("错误[" + response.reasonPhrase
//                    + "]状态码[" + response.statusCode + "]");
//        }
//        LogUtil.debug(result.getMessage());
        return null;
    }


    /**
     * Map参数转成Json串
     *
     * @param bodyParamMap
     * @return
     */
    public String toBodyJsonByMap(Map<String, String> bodyParamMap) {
        if (null == bodyParamMap || bodyParamMap.isEmpty()) {
            return "{}";
        }

        JSONObject ret = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : bodyParamMap.entrySet()) {
                ret.put(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            return null;
        }
        return ret.toString();
    }


    public String toBodyJsonByObject(Object searchData) {
        if (null == searchData) {
            return "{}";
        }

        return JSON.toJSONString(searchData);
    }

}
