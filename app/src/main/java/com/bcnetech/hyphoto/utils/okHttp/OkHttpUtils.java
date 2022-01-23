//package com.bcnetech.bizcam.utils.okHttp;
//
//import android.util.Log;
//
//import com.bcnetech.bcnetechlibrary.util.LogUtil;
//import com.bcnetech.bcnetechlibrary.util.SecurityUtil;
//import com.bcnetech.bcnetechlibrary.util.StringUtil;
//import com.bcnetech.bcnetechlibrary.util.okHttp.OkHttpClientManager;
//import com.bcnetech.bcnetechlibrary.util.uuid.UUIDUtils;
//import com.squareup.okhttp.MediaType;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.RequestBody;
//import com.squareup.okhttp.Response;
//
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.ConnectException;
//import java.net.SocketTimeoutException;
//import java.net.URLEncoder;
//import java.util.Calendar;
//import java.util.Map;
//import java.util.TimeZone;
//
///**
// * Created by wb on 2016/5/3.
// */
//public class OkHttpUtils {
//
//    /*public static void setHeaders(Req request, String method, String workId) {
//        request.addHeader("Content-Type", "application/json");
//        request.addHeader("Accept", "application/vnd.alloy+json;version=1");
//        request.addHeader("Accept-Language", "zh_cn");
//        request.addHeader("app_version", AppVersion.app_version);
//        if (null != LoginedUser.getLoginedUser() && null != LoginedUser.getLoginedUser().tokenid) {
//            request.addHeader("Authorization", "Token " + LoginedUser.getLoginedUser().tokenid);
//
//        }
//        request.addHeader("X-Client-Type", "APP");
//        String workspaceId = "";
//        if(!StringUtil.isEmpty(workId)){
//            workspaceId = workId;
//            request.addHeader("X-Workspace-Id", workspaceId);
//        }else{
//            if (null != LoginedUser.getLoginedUser() && null != LoginedUser.getLoginedUser().workspace_id) {
//                workspaceId = LoginedUser.getLoginedUser().workspace_id;
//                request.addHeader("X-Workspace-Id", workspaceId);
//            }
//        }
//        String requestId = UuidUtil.getUUID();
//        request.addHeader("X-Request-Id", requestId);
//        String requestTime = String.valueOf(getGMTUnixTimeByCalendar()).substring(0, 10);//秒级别
//        request.addHeader("X-Request-Time", requestTime);
//        String secret = "32=tMC;0;L}W]&8x?rfgYiQ%p^dOo>v_i5a[+=piT;8/S3=UvmFSN>ziUSt:0mt.>*zYhpmGxl6i*=;yT";
//        if (null != LoginedUser.getLoginedUser() && null != LoginedUser.getLoginedUser().secret) {
//            secret = LoginedUser.getLoginedUser().secret;
//        }
//        String dataTxt;
//        if (!StringUtil.isEmpty(request.getStringParams())){
//            dataTxt = request.getUri() + "," + request.getStringParams();
//        }else {
//            if (null != request.getBodyParams() && request.getBodyParams().size() == 0) {
//                dataTxt = buildUrl(request.getUri(), request.getUrlParams());
//            } else {
//                dataTxt = buildUrl(request.getUri(), request.getUrlParams()) + "," + getParamJson(request.getBodyParams());
//            }
//        }
//        String signature = getSignature(workspaceId, method, requestId, requestTime, secret, dataTxt);
//        request.addHeader("X-Request-Sign", signature);
//    }*/
//    public static ResponseResult upload(String url, String filePathName, String tokenid) {
//        File[] files = {new File(filePathName)};
//        LogUtil.d(url + ":" + filePathName);
//        String[] fileKeys = {"file"};
//        OkHttpClientManager.Param[] headers = new OkHttpClientManager.Param[9];
//        headerAddParms(headers, 0, "Authorization", "Token " + tokenid);
//        headerAddParms(headers, 1, "Content-Type", "multipart/form-data");
//        headerAddParms(headers, 2, "Content-Type", "application/json");
//        headerAddParms(headers, 3, "Accept", "application/vnd.alloy+json;version=1");
//        headers = headerAddParms(headers, 4, "X-Client-Type", "APP");
//        OkHttpClientManager.Param param = new OkHttpClientManager.Param();
//        param.setKey("Authorization");
//        param.setValue("Token " + tokenid);
//        param.setKey("Content-Type");
//        //param.setValue("application/json");
//        param.setValue("multipart/form-data");
//        //=====
//       /* param.setKey("Content-Type");
//        param.setValue("application/json");*/
//        //=====
//        param.setKey("Accept");
//        param.setValue("application/vnd.alloy+json;version=1");
//        //=====
//        param.setKey("Accept-Language");
//        param.setValue("zh_cn");
//        //=====
//        param.setKey("app_version");
//        param.setValue("");//
//        //=====
//        param.setKey("X-Client-Type");
//        param.setValue("APP");
//        //=====
//       /* param.setKey("X-Workspace-Id");
//        param.setValue("");*/
//        //=====
//      /*  String requestId = UUIDUtils.createId();
//        param.setKey("X-Request-Id");
//        param.setValue(requestId);*/
//        //=====
//       /* String requestTime = String.valueOf(getGMTUnixTimeByCalendar()).substring(0, 10);//秒级别
//        param.setKey("X-Request-Time");
//        param.setValue( requestTime);*/
//        //=====
//        /*String dataTxt;
//        if (!StringUtil.isEmpty(request.getStringParams())){
//            dataTxt = request.getUri() + "," + request.getStringParams();
//        }else {
//            if (null != request.getBodyParams() && request.getBodyParams().size() == 0) {
//                dataTxt = buildUrl(request.getUri(), request.getUrlParams());
//            } else {
//                dataTxt = buildUrl(request.getUri(), request.getUrlParams()) + "," + getParamJson(request.getBodyParams());
//            }
//        }*/
//
//
//        //=====
//  /*      param.setKey("X-Client-Type");
//        param.setValue("APP");*/
//        //=====
//
//
//       /* headers[0] = param;
//        OkHttpClientManager.Param param1 = new OkHttpClientManager.Param();
//        param1.setKey("Accept");
//        param1.setValue("text/html");
//        headers[1] = param1;*/
//        Request request = OkHttpClientManager.getInstance()
//                .getUploadDelegate()
//                .buildMultipartFormRequest(url, files, fileKeys, headers, null);
//        Response response = null;
//        ResponseResult responseResult = new ResponseResult();
//        //这里设置一下header
//        try {
//            response = OkHttpClientManager.getInstance().getUploadDelegate().post(request);
//            responseResult = setResponese(response);
//        } catch (Exception e) {
//
//            if(e instanceof SocketTimeoutException){
//                //判断超时异常
//
//                LogUtil.error(e);
//                responseResult.statusCode = 504;
//            }
//            if(e instanceof ConnectException){
//                //判断连接异常，我这里是报Failed to connect to 10.7.5.144
//                LogUtil.error(e);
//                responseResult.statusCode = -1;
//            }
//            responseResult.reasonPhrase = e.getMessage();
//        }
//        return responseResult;
//    }
//
//
//    public static ResponseResult upload2(String url, Map<String, Object> map, String[] filePathNames, String[] fileKeys, String tokenid) {
//        File[] files = new File[filePathNames.length];
//        for (int i = 0; i < filePathNames.length; i++) {
//            files[i] = new File(filePathNames[i]);
//        }
//        OkHttpClientManager.Param[] headers = new OkHttpClientManager.Param[9];
//        headerAddParms(headers, 0, "Authorization", "Token " + tokenid);
//        headerAddParms(headers, 1, "Content-Type", "multipart/form-data");
//        headerAddParms(headers, 2, "Content-Type", "application/json");
//        headerAddParms(headers, 3, "Accept", "application/vnd.alloy+json;version=1");
//        headers = headerAddParms(headers, 4, "X-Client-Type", "APP");
//        Request request = OkHttpClientManager.getInstance()
//                .getUploadDelegate()
//                .buildMultipartFormRequest(url, map, files, fileKeys, headers, null, 5);
//        Response response = null;
//        ResponseResult responseResult = new ResponseResult();
//        //这里设置一下header
//        try {
//            response = OkHttpClientManager.getInstance().getUploadDelegate().post(request);
//            responseResult = setResponese(response);
//        } catch (Exception e) {
//
//            if(e instanceof SocketTimeoutException){
//                //判断超时异常
//
//                LogUtil.error(e);
//                responseResult.statusCode = 504;
//            }
//            if(e instanceof ConnectException){
//                //判断连接异常，我这里是报Failed to connect to 10.7.5.144
//                LogUtil.error(e);
//                responseResult.statusCode = -1;
//            }
//            responseResult.reasonPhrase = e.getMessage();
//        }
//        return responseResult;
//    }
//
//    /**
//     * 获取秘钥
//     *
//     * @param workspaceId
//     * @param method
//     * @param requestId
//     * @param requestTime
//     * @param secret
//     * @param dataTxt
//     * @return
//     */
//   /* public static String getSignature(String workspaceId, String method, String requestId, String requestTime, String secret, String dataTxt) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(workspaceId);
//        sb.append("," + method);
//        sb.append("," + requestId);
//        sb.append("," + requestTime);
//        sb.append("," + secret);
//        sb.append("," + dataTxt);
//        LogUtil.debug("签名原串：" + sb.toString());
//        String str = (SecurityUtil.encodeByMD5(sb.toString())).replaceAll("\n", "");
//        LogUtil.debug("签名：" + str);
//        return str;
//    }*/
//    private static String getSignature(String workspaceId, String method, String requestId, String requestTime, String secret, String dataTxt) {
//        StringBuilder sb = new StringBuilder();
//        if (!StringUtil.isBlank(workspaceId)) {
//            sb.append(workspaceId + ",");
//        }
//        sb.append(method);
//        sb.append("," + requestId);
//        sb.append("," + requestTime);
//        sb.append("," + secret);
//        sb.append("," + dataTxt);
//        Log.d("getSignature", sb.toString());
//        String str = (SecurityUtil.encodeByMD5(sb.toString())).replaceAll("\n", "");
//        Log.d("getSignature", str);
//        return str;
//    }
//
//    public static ResponseResult jsonGet(String host, String url,
//                                         Map<String, String> getParam, String tokenid, String secret) {
//        // 拼接参数到Url后面
//        if (null != getParam && !getParam.isEmpty()) {
//            StringBuilder sb = new StringBuilder();
//            for (Map.Entry<String, String> e : getParam.entrySet()) {
//                sb.append(e.getKey()).append("=")
//                        .append(URLEncoder.encode(e.getValue().trim()))
//                        .append("&");
//            }
//
//            sb.deleteCharAt(sb.length() - 1);
//            url = url + "?" + sb.toString();
//        }
//        LogUtil.d("Json GET请求地址：" + host + url);
//
//        Request.Builder builder = new Request.Builder();
//        builder.url(host + url);
//        //头通用参数
//        // builder.addHeader("Content-Type", "application/json");
//      /*  String workspaceId = "";
//        String method = "GET";
//        String requestId = UUIDUtils.createId();
//
//        String requestTime = String.valueOf(getGMTUnixTimeByCalendar()).substring(0, 10);//秒级别
//        String secret = "32=tMC;0;L}W]&8x?rfgYiQ%p^dOo>v_i5a[+=piT;8/S3=UvmFSN>ziUSt:0mt.>*zYhpmGxl6i*=;yT";
//        String dataTxt;
//        String dataurl = "/" + url.substring(url.lastIndexOf("/") + 1);
//        dataTxt = dataurl + ",";
//        String signature = getSignature(workspaceId, method, requestId, requestTime, secret, dataTxt);
//        if (tokenid != null) {
//            builder.addHeader("Authorization", "Token " + tokenid);
//        }
//        builder.addHeader("X-Request-Sign", signature);
//        builder.addHeader("X-Request-Id", requestId);
//        builder.addHeader("X-Request-Time", requestTime);
//        builder.addHeader("Accept", "application/vnd.alloy+json;version=1");*/
//        String method = "POST";
//        String requestId = UUIDUtils.createId();
//
//        String requestTime = String.valueOf(getGMTUnixTimeByCalendar()).substring(0, 10);//秒级别
//        if (secret == null) {
//            secret = "32=tMC;0;L}W]&8x?rfgYiQ%p^dOo>v_i5a[+=piT;8/S3=UvmFSN>ziUSt:0mt.>*zYhpmGxl6i*=;yT";
//        }
//        String dataTxt;
//
//      /*  dataTxt = url + "," + jsonStr;
//        String signature = getSignature(workspaceid, method, requestId, requestTime, secret, dataTxt);*/
//      /*  builder.addHeader("X-Request-Sign", signature);*/
//        builder.addHeader("X-Request-Id", requestId);
//        builder.addHeader("X-Request-Time", requestTime);
//        //头通用参数
//        builder.addHeader("Content-Type", "application/json");
//        builder.addHeader("Accept", "application/vnd.alloy+json; version=1");
//        if (tokenid != null) {
//            builder.addHeader("Authorization", "Token " + tokenid);
//        }
//
//        builder.addHeader("X-Client-Type", "APP");
//        builder.addHeader("Accept-Language", "zh_cn");
//        builder.addHeader("method", "GET");
//        builder.get();
//        //发送请求
//        Response response = null;
//        ResponseResult responseResult = new ResponseResult();
//        try {
//            response = OkHttpClientManager.getInstance().getGetDelegate().get(builder.build());
//            responseResult = setResponese(response);
//        } catch (Exception e) {
//            if(e instanceof SocketTimeoutException){
//                //判断超时异常
//                LogUtil.error(e);
//                responseResult.statusCode = 504;
//               // responseResult.reasonPhrase = e.getMessage();
//            }else if(e instanceof ConnectException){
//                //判断连接异常，我这里是报Failed to connect to 10.7.5.144
//                LogUtil.error(e);
//                responseResult.statusCode = -1;
//               // responseResult.reasonPhrase = e.getMessage();
//            }
//            responseResult.reasonPhrase = e.getMessage();
//        }
//        return responseResult;
//    }
//
//    /**
//     * 以Json-Post的方式提交参数
//     *
//     * @param url
//     * @param getParam
//     * @param jsonStr
//     * @return
//     */
//    public static ResponseResult jsonPost(String host, String url,
//                                          Map<String, String> getParam, String jsonStr, String secret, String tokenid, String workspaceid) {
//        // 拼接参数到Url后面
//        if (null != getParam && !getParam.isEmpty()) {
//            StringBuilder sb = new StringBuilder();
//            for (Map.Entry<String, String> e : getParam.entrySet()) {
//                sb.append(e.getKey()).append("=")
//                        .append(URLEncoder.encode(e.getValue().trim()))
//                        .append("&");
//            }
//            sb.deleteCharAt(sb.length() - 1);
//            url = url + "?" + sb.toString();
//        }
//
//        Request.Builder builder = new Request.Builder();
//        builder.url(host + url);
//        Log.d("","Json Post请求地址：" + host + url + ":" + jsonStr);
//        LogUtil.debug("Json Post请求地址：" + host + url + ":" + jsonStr);
//        String method = "POST";
//        String requestId = UUIDUtils.createId();
//        if (workspaceid != null) {
//            builder.addHeader("X-Workspace-Id", workspaceid);
//        }
//
//        String requestTime = String.valueOf(getGMTUnixTimeByCalendar()).substring(0, 10);//秒级别
//        if (secret == null) {
//            secret = "32=tMC;0;L}W]&8x?rfgYiQ%p^dOo>v_i5a[+=piT;8/S3=UvmFSN>ziUSt:0mt.>*zYhpmGxl6i*=;yT";
//        }
//        String dataTxt;
//
//        dataTxt = url + "," + jsonStr;
//        String signature = getSignature(workspaceid, method, requestId, requestTime, secret, dataTxt);
//        builder.addHeader("X-Request-Sign", signature);
//        builder.addHeader("X-Request-Id", requestId);
//        builder.addHeader("X-Request-Time", requestTime);
//        //头通用参数
//        builder.addHeader("Content-Type", "application/json");
//        builder.addHeader("Accept", "application/vnd.alloy+json; version=1");
//        if (tokenid != null) {
//            builder.addHeader("Authorization", "Token " + tokenid);
//        }
//
//        builder.addHeader("X-Client-Type", "APP");
//        builder.addHeader("Accept-Language", "zh_cn");
//        builder.addHeader("method", "POST");
//        //body
//        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        RequestBody body = RequestBody.create(JSON, jsonStr);
//        builder.post(body);
//        //发送请求
//        Response response;
//        ResponseResult responseResult = new ResponseResult();
//        try {
//            response = OkHttpClientManager.getInstance().getGetDelegate().get(builder.build());
//            responseResult = setResponese(response);
//        } catch (Exception e) {
//            if(e instanceof SocketTimeoutException){
//                //判断超时异常
//
//                LogUtil.error(e);
//                responseResult.statusCode = 504;
//                //responseResult.reasonPhrase = e.getMessage();
//            }
//            if(e instanceof ConnectException){
//                //判断连接异常，我这里是报Failed to connect to 10.7.5.144
//                LogUtil.error(e);
//                responseResult.statusCode = -1;
//                //responseResult.reasonPhrase = e.getMessage();
//            }
//            responseResult.reasonPhrase = e.getMessage();
//        }
//        return responseResult;
//    }
//
//    private static ResponseResult setResponese(Response response) throws IOException {
//        ResponseResult responseResult = new ResponseResult();
//        responseResult.statusCode = response.code();
//        responseResult.reasonPhrase = response.message() + "";
//        responseResult.resultStr = response.body().string() + "";
//        Log.d("BigApp", "响应" + response.code() + "，状态码[:" + responseResult.resultStr + "]");
//        return responseResult;
//    }
//
//
//    /**
//     * 获取当下时间戳
//     *
//     * @return
//     */
//    public static long getGMTUnixTimeByCalendar() {
//        Calendar calendar = Calendar.getInstance();
//        // 获取当前时区下日期时间对应的时间戳
//        long unixTime = calendar.getTimeInMillis();
//        // 获取标准格林尼治时间下日期时间对应的时间戳
//        long unixTimeGMT = unixTime - TimeZone.getDefault().getRawOffset();
//        return unixTimeGMT;
//    }
//
//
//    /**
//     * 响应结果
//     *
//     * @author xuan
//     */
//    public static class ResponseResult {
//        /**
//         * HTTP响应代码200是对的
//         */
//        public int statusCode;
//        /**
//         * 响应码的简单描述
//         */
//        public String reasonPhrase;
//        /**
//         * 返回结果串
//         */
//        public String resultStr;
//    }
//
//    private Request.Builder setHttpCache(Request.Builder builder) {
//        return builder.addHeader("Cache-Control", "max-age=86400,must-revalidate");
//
//    }
//
//
//    /*将请求参数转为json字符串
//     */
//    public static String getParamJson(Map<String, String> params) {
//        if (null == params || params.isEmpty()) {
//            return "";
//        }
//        JSONObject ret = new JSONObject();
//        try {
//            for (Map.Entry<String, String> entry : params.entrySet()) {
//                ret.put(entry.getKey(), entry.getValue());
//            }
//        } catch (Exception e) {
//            return null;
//        }
//        return ret.toString();
//    }
//
//    private static OkHttpClientManager.Param[] headerAddParms(OkHttpClientManager.Param[] headers, int position, String key, String value) {
//        OkHttpClientManager.Param param = new OkHttpClientManager.Param();
//        param.setKey(key);
//        param.setValue(value);
//        headers[position] = param;
//        return headers;
//    }
//}
//
