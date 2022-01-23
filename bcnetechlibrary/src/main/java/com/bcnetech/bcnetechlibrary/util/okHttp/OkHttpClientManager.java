//package com.bcnetech.bcnetechlibrary.util.okHttp;
//
//import android.content.Context;
//import android.os.Handler;
//import android.os.Looper;
//
//import com.bcnetech.bcnetechlibrary.util.LogUtil;
//import com.squareup.okhttp.Call;
//import com.squareup.okhttp.Callback;
//import com.squareup.okhttp.FormEncodingBuilder;
//import com.squareup.okhttp.Headers;
//import com.squareup.okhttp.MediaType;
//import com.squareup.okhttp.MultipartBuilder;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.RequestBody;
//import com.squareup.okhttp.Response;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.FileNameMap;
//import java.net.URLConnection;
//import java.security.KeyStore;
//import java.security.SecureRandom;
//import java.security.cert.CertificateFactory;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.TimeUnit;
//
//import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSocketFactory;
//import javax.net.ssl.TrustManagerFactory;
//
///**
// * Created by wb on 2016/5/3.
// */
//public class OkHttpClientManager {
//    private static OkHttpClientManager mInstance;
//    private OkHttpClient mOkHttpClient;
//    private Handler mDelivery;
//
//    private DownloadDelegate mDownloadDelegate = new DownloadDelegate();
//    private GetDelegate mGetDelegate = new GetDelegate();
//    private UploadDelegate mUploadDelegate = new UploadDelegate();
//    private PostDelegate mPostDelegate = new PostDelegate();
//
//
//
//    private OkHttpClientManager() {
//        mOkHttpClient = new OkHttpClient();
//
//        mOkHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
//        mOkHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
//        mOkHttpClient.setWriteTimeout(30, TimeUnit.SECONDS);
//        mDelivery = new Handler(Looper.getMainLooper());
//    }
//
//    public static OkHttpClientManager getInstance() {
//        if (mInstance == null) {
//            synchronized (OkHttpClientManager.class) {
//                if (mInstance == null) {
//                    mInstance = new OkHttpClientManager();
//                }
//            }
//        }
//        return mInstance;
//    }
//
//    /**
//     * 设置
//     * @param context
//     */
//    public void initSSL(Context context){
//        //setHttps();
//        SSLSocketFactory sslSocketFactory=null;//HttpsUtils.initHttpsSSL(context);
//        if(sslSocketFactory==null){
//            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
//            SSLSocketFactory sSLSocketFactory = sslParams.sSLSocketFactory;
//            mOkHttpClient.setSslSocketFactory(sSLSocketFactory);
//            HttpsURLConnection.setDefaultSSLSocketFactory(sSLSocketFactory);
//        }
//        else{
//            mOkHttpClient.setSslSocketFactory(sslSocketFactory);
//            HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
//        }
//
//    }
//
//
//
//    public UploadDelegate getUploadDelegate() {
//        return mUploadDelegate;
//    }
//
//    public PostDelegate getPostDelegate() {
//        return mPostDelegate;
//    }
//
//    public GetDelegate getGetDelegate() {
//        return mGetDelegate;
//    }
//
//    public DownloadDelegate getDownloadDelegate() {
//        return mDownloadDelegate;
//    }
//
//    /**
//     * Get方式请求提交
//     */
//    public class GetDelegate {
//        private Request buildGetRequest(String url, Object tag) {
//            Request.Builder builder = new Request.Builder()
//                    .url(url);
//            if (null != tag) {
//                builder.tag(tag);
//            }
//            return builder.build();
//        }
//
//        /**
//         * 通用的方法
//         */
//        public Response get(Request request) throws IOException {
//            Call call = mOkHttpClient.newCall(request);
//            Response execute = call.execute();
//            return execute;
//        }
//
//        /**
//         * 同步的Get请求
//         */
//        public Response get(String url) throws IOException {
//            return get(url, null);
//        }
//
//        public Response get(String url, Object tag) throws IOException {
//            final Request request = buildGetRequest(url, tag);
//            return get(request);
//        }
//
//        /**
//         * 同步的Get请求
//         */
//        public String getAsString(String url) throws IOException {
//            return getAsString(url, null);
//        }
//
//        public String getAsString(String url, Object tag) throws IOException {
//            Response execute = get(url, tag);
//            return execute.body().string();
//        }
//    }
//
//    /**
//     * 上传模块
//     */
//    public class UploadDelegate {
//        /**
//         * POST请求
//         *
//         * @param request
//         * @return
//         * @throws IOException
//         */
//        public Response post(Request request) throws IOException {
//            return mOkHttpClient.newCall(request).execute();
//        }
//
//        /**
//         * 上传单个文件
//         *
//         * @param url
//         * @param fileKey 文件key
//         * @param file    文件
//         * @param tag     标记
//         * @return
//         * @throws IOException
//         */
//        public Response post(String url, String fileKey, File file, Object tag) throws IOException {
//            return post(url, new String[]{fileKey}, new File[]{file}, null, tag);
//        }
//
//        /**
//         * 上传多个文件
//         *
//         * @param url
//         * @param fileKeys 文件key
//         * @param files    文件
//         * @param params   参数
//         * @param tag      标记
//         * @return
//         * @throws IOException
//         */
//        public Response post(String url, String[] fileKeys, File[] files, Param[] params, Object tag) throws IOException {
//            Request request = buildMultipartFormRequest(url, files, fileKeys, params, tag);
//            return post(request);
//        }
//
//        /**
//         * 上传多个文件
//         *
//         * @param url
//         * @param fileKey
//         * @param file
//         * @param params
//         * @param tag
//         * @return
//         * @throws IOException
//         */
//        public Response post(String url, String fileKey, File file, Param[] params, Object tag) throws IOException {
//            return post(url, new String[]{fileKey}, new File[]{file}, params, tag);
//        }
//
//
//        public Request buildMultipartFormRequest(String url, File[] files,
//                                                 String[] fileKeys, Param[] params, Object tag) {
//            params = validateParam(params);
//
//            MultipartBuilder builder = new MultipartBuilder()
//                    .type(MultipartBuilder.FORM);
//
////            for (Param param : params) {
////                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
////                        RequestBody.create(null, param.value));
////            }
//            if (files != null) {
//                RequestBody fileBody = null;
//                for (int i = 0; i < files.length; i++) {
//                    File file = files[i];
//                    String fileName = file.getName();
//                    fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
//                    //TODO 根据文件名设置contentType
//                    builder.addPart(Headers.of("Content-Disposition",
//                            "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
//                            fileBody);
//                }
//            }
//
//            RequestBody requestBody = builder.build();
//            return new Request.Builder()
//                    .url(url)
//                    .post(requestBody)
//                    .tag(tag)
//                    .addHeader(params[0].getKey(), params[0].getValue())
//                    .addHeader(params[1].getKey(), params[1].getValue())
//                   /* .addHeader(params[2].getKey(),params[2].getValue())
//                    .addHeader(params[3].getKey(),params[3].getValue())
//                    .addHeader(params[4].getKey(),params[4].getValue())
//                    .addHeader(params[5].getKey(),params[5].getValue())
//                    .addHeader(params[6].getKey(),params[6].getValue())
//                    .addHeader(params[7].getKey(),params[7].getValue())
//                    .addHeader(params[8].getKey(),params[8].getValue())*/
//                    .build();
//        }
//
//        public Request buildMultipartFormRequest(String url, Map<String, Object> map, File[] files,
//                                                 String[] fileKeys, Param[] params, Object tag, int position) {
//            params = validateParam(params);
//            LogUtil.d("Url:"+url);
//            MultipartBuilder builder = new MultipartBuilder()
//                    .type(MultipartBuilder.FORM);
//
////            for (Param param : params) {
////                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
////                        RequestBody.create(null, param.value));
////            }
//
//            if (map != null) {
//                // map 里面是请求中所需要的 key 和 value
//                for (Map.Entry entry : map.entrySet()) {
//                    builder.addFormDataPart(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
//                    LogUtil.d("key:"+String.valueOf(entry.getKey())+"  value:"+String.valueOf(entry.getValue()));
//                }
//            }
//            if (files != null) {
//                RequestBody fileBody = null;
//                for (int i = 0; i < files.length; i++) {
//                    File file = files[i];
//                    LogUtil.d("fileSize:"+file.length());
//                    String fileName = file.getName();
//                    fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
//                    //TODO 根据文件名设置contentType
//                    builder.addPart(Headers.of("Content-Disposition",
//                            "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
//                            fileBody);
//                }
//            }
//
//
//            RequestBody requestBody = builder.build();
//            Request.Builder builder1 = new Request.Builder()
//                    .url(url)
//                    .post(requestBody)
//                    .tag(tag);
//            for (int i = 0; i < position; i++) {
//                builder1.addHeader(params[i].getKey(), params[i].getValue());
//            }
//            return builder1.build();
//        }
//
//    }
//
//    /**
//     * 单向https验证
//     *
//     * @param certificates
//     */
//    public void setCertificates(InputStream... certificates) {
//        try {
//            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(null);
//            int index = 0;
//            for (InputStream certificate : certificates) {
//                String certificateAlias = Integer.toString(index++);
//                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
//
//                try {
//                    if (certificate != null)
//                        certificate.close();
//                } catch (IOException e) {
//                }
//            }
//
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//
//            TrustManagerFactory trustManagerFactory =
//                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//
//            trustManagerFactory.init(keyStore);
//            sslContext.init
//                    (
//                            null,
//                            trustManagerFactory.getTrustManagers(),
//                            new SecureRandom()
//                    );
//            mOkHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * 下载相关的模块
//     */
//    public class DownloadDelegate {
//        /**
//         * 异步下载文件
//         *
//         * @param url
//         * @param destFileDir 本地文件存储的文件夹
//         * @param callback
//         */
//        public void downloadAsyn(final String newname,final String url, final String destFileDir, final ResultCallback callback, Object tag) {
//            final Request request = new Request.Builder()
//                    .url(url)
//                    .tag(tag)
//                    .build();
//            final Call call = mOkHttpClient.newCall(request);
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(final Request request, final IOException e) {
//                    sendFailedStringCallback(request, e, callback);
//                }
//
//                @Override
//                public void onResponse(Response response) {
//                    InputStream is = null;
//                    byte[] buf = new byte[2048];
//                    int len = 0;
//                    FileOutputStream fos = null;
//                    try {
//                        is = response.body().byteStream();
//
//                        File dir = new File(destFileDir);
//                        if (!dir.exists()) {
//                            dir.mkdirs();
//                        }
//                        String localUrl="";
//
//                        if(url.contains("?code=0")){
//                            String temp[] = url.split("\\?");
//                            if(temp.length>1){
//                                localUrl=temp[0];
//                            }
//                        }else {
//                            localUrl=url;
//                        }
//                        File file = new File(dir, getFileName(localUrl+newname));
//
//                        fos = new FileOutputStream(file);
//                        while ((len = is.read(buf)) != -1) {
//                            fos.write(buf, 0, len);
//                        }
//                        fos.flush();
//                        //如果下载文件成功，第一个参数为文件的绝对路径
//                        sendSuccessResultCallback(file.getAbsolutePath(), callback);
//                    } catch (IOException e) {
//                        sendFailedStringCallback(response.request(), e, callback);
//                    } finally {
//                        try {
//                            if (is != null) is.close();
//                        } catch (IOException e) {
//                        }
//                        try {
//                            if (fos != null) fos.close();
//                        } catch (IOException e) {
//                        }
//                    }
//
//                }
//            });
//        }
//
//
//        public void downloadAsyn(final String newname,final String url, final String destFileDir, final ResultCallback callback) {
//            downloadAsyn(newname,url, destFileDir, callback, null);
//        }
//    }
//
//    /**
//     * Post请求
//     */
//    public class PostDelegate {
//        private final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream;charset=utf-8");
//        private final MediaType MEDIA_TYPE_STRING = MediaType.parse("text/plain;charset=utf-8");
//
//        public Response post(String url, Param[] params) throws IOException {
//            return post(url, params, null);
//        }
//
//        /**
//         * 同步的Post请求
//         */
//        public Response post(String url, Param[] params, Object tag) throws IOException {
//            Request request = buildPostFormRequest(url, params, tag);
//            Response response = mOkHttpClient.newCall(request).execute();
//            return response;
//        }
//
//        public Response post(Request request) throws IOException {
//            return mOkHttpClient.newCall(request).execute();
//        }
//
//        public String postAsString(String url, Param[] params) throws IOException {
//            return postAsString(url, params, null);
//        }
//
//        /**
//         * 同步的Post请求
//         */
//        public String postAsString(String url, Param[] params, Object tag) throws IOException {
//            Response response = post(url, params, tag);
//            return response.body().string();
//        }
//
//        /**
//         * 同步的Post请求:直接将bodyStr以写入请求体
//         */
//        public Response post(String url, String bodyStr) throws IOException {
//            return post(url, bodyStr, null);
//        }
//
//        public Response post(String url, String bodyStr, Object tag) throws IOException {
//            RequestBody body = RequestBody.create(MEDIA_TYPE_STRING, bodyStr);
//            Request request = buildPostRequest(url, body, tag);
//            Response response = mOkHttpClient.newCall(request).execute();
//            return response;
//        }
//
//        /**
//         * 同步的Post请求:直接将bodyFile以写入请求体
//         */
//        public Response post(String url, File bodyFile) throws IOException {
//            return post(url, bodyFile, null);
//        }
//
//        public Response post(String url, File bodyFile, Object tag) throws IOException {
//            RequestBody body = RequestBody.create(MEDIA_TYPE_STREAM, bodyFile);
//            Request request = buildPostRequest(url, body, tag);
//            Response response = mOkHttpClient.newCall(request).execute();
//            return response;
//        }
//
//        /**
//         * 同步的Post请求
//         */
//        public Response post(String url, byte[] bodyBytes) throws IOException {
//            return post(url, bodyBytes, null);
//        }
//
//        public Response post(String url, byte[] bodyBytes, Object tag) throws IOException {
//            RequestBody body = RequestBody.create(MEDIA_TYPE_STREAM, bodyBytes);
//            Request request = buildPostRequest(url, body, tag);
//            Response response = mOkHttpClient.newCall(request).execute();
//            return response;
//        }
//
//        /**
//         * post构造Request的方法
//         *
//         * @param url
//         * @param body
//         * @return
//         */
//        private Request buildPostRequest(String url, RequestBody body, Object tag) {
//            Request.Builder builder = new Request.Builder()
//                    .url(url)
//                    .post(body);
//            if (tag != null) {
//                builder.tag(tag);
//            }
//            Request request = builder.build();
//            return request;
//        }
//
//    }
//
//    /**
//     * 参数
//     */
//    public static class Param {
//        private String key;
//        private String value;
//
//        public Param() {
//        }
//
//        public Param(String key, String value) {
//            this.key = key;
//            this.value = value;
//        }
//
//        public String getKey() {
//            return key;
//        }
//
//        public void setKey(String key) {
//            this.key = key;
//        }
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//    }
//
//    /**
//     * 下载回调
//     *
//     * @param <T>
//     */
//    public static abstract class ResultCallback<T> {
//        public void onBefore(Request request) {
//        }
//
//        public void onAfter() {
//        }
//
//        public abstract void onError(Request request, Exception e);
//
//        public abstract void onResponse(T response);
//    }
//
//
//    //=========================内部方法======================================
//    private Param[] validateParam(Param[] params) {
//        if (params == null) {
//            return new Param[0];
//        } else {
//            return params;
//        }
//    }
//
//    private String guessMimeType(String path) {
//        FileNameMap fileNameMap = URLConnection.getFileNameMap();
//        String contentTypeFor = fileNameMap.getContentTypeFor(path);
//        if (contentTypeFor == null) {
//            contentTypeFor = "application/octet-stream";
//        }
//        return contentTypeFor;
//    }
//
//    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback) {
//        mDelivery.post(new Runnable() {
//            @Override
//            public void run() {
//                callback.onError(request, e);
//                callback.onAfter();
//            }
//        });
//    }
//
//    private void sendSuccessResultCallback(final Object object, final ResultCallback callback) {
//        mDelivery.post(new Runnable() {
//            @Override
//            public void run() {
//                callback.onResponse(object);
//                callback.onAfter();
//            }
//        });
//    }
//
//    private String getFileName(String path) {
//        int separatorIndex = path.lastIndexOf("/");
//        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
//    }
//
//    private Param[] map2Params(Map<String, String> params) {
//        if (params == null) return new Param[0];
//        int size = params.size();
//        Param[] res = new Param[size];
//        Set<Map.Entry<String, String>> entries = params.entrySet();
//        int i = 0;
//        for (Map.Entry<String, String> entry : entries) {
//            res[i++] = new Param(entry.getKey(), entry.getValue());
//        }
//        return res;
//    }
//
//    private Request buildPostFormRequest(String url, Param[] params, Object tag) {
//        if (params == null) {
//            params = new Param[0];
//        }
//        FormEncodingBuilder builder = new FormEncodingBuilder();
//        for (Param param : params) {
//            builder.add(param.key, param.value);
//        }
//        RequestBody requestBody = builder.build();
//
//
//        Request.Builder reqBuilder = new Request.Builder();
//        reqBuilder.url(url)
//                .post(requestBody);
//
//        if (tag != null) {
//            reqBuilder.tag(tag);
//        }
//        return reqBuilder.build();
//    }
//
//}
