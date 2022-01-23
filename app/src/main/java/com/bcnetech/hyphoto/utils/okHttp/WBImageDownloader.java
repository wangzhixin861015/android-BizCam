package com.bcnetech.hyphoto.utils.okHttp;

import android.content.Context;
import android.net.Uri;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by wb on 2016/3/23.
 */
public class WBImageDownloader extends BaseImageDownloader {

    public WBImageDownloader(Context context) {
        super(context);
        //initSSL(context);
    }

    public WBImageDownloader(Context context, int connectTimeout, int readTimeout) {
        super(context, connectTimeout, readTimeout);
    }

    @Override
    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
        //trustAllHosts();
        HttpURLConnection conn;
        String encodedUrl = Uri.encode(url, "@#&=*+-_.,:!?()/~\'%");
        URL httpUrl = (new URL(encodedUrl));
        //HttpsURLConnection https = (HttpsURLConnection) httpUrl.openConnection();
    /*    if (httpUrl.getProtocol().toLowerCase().equals("https")) {
            https.setHostnameVerifier(DO_NOT_VERIFY);
            conn = https;
        } else {*/
            conn = (HttpURLConnection) httpUrl.openConnection();
      //  }

        //HttpURLConnection conn = (HttpURLConnection)(new URL(encodedUrl)).openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/vnd.alloy+json;version=1");
        conn.setRequestProperty("Accept-Language", "zh_cn");
        /*if (LoginedUser.getLoginedUser().getLoginData() != null) {
            conn.setRequestProperty("Authorization", "Token " + LoginedUser.getLoginedUser().getLoginData().getTokenid());
        }*/
        conn.setRequestProperty("X-Client-Type", "APP");

       /* String workspaceId = "";
        if (LoginedUser.getLoginedUser().getLoginData() != null) {
            workspaceId = LoginedUser.getLoginedUser().getLoginData().getWorkspace_id();
            conn.setRequestProperty("X-Workspace-Id", workspaceId);
        }*/
        // String requestId = UUIDUtils.createId();
        // conn.setRequestProperty("X-Request-Id", requestId);

        /*String requestTime = String.valueOf(HttpUtil.getGMTUnixTimeByCalendar()).substring(0, 10);
        conn.setRequestProperty("X-Request-Time", requestTime);*/

        /*String secret = "";
        if (LoginedUser.getLoginedUser().getLoginData() != null) {
            secret = LoginedUser.getLoginedUser().getLoginData().getSecret();
        }
        String signature = HttpUtil.getSignature(workspaceId, "POST", requestId, requestTime, secret, url.substring(url.indexOf("/",7),url.length()));
        conn.setRequestProperty("X-Request-Sign", signature);*/

        conn.setConnectTimeout(this.connectTimeout);
        conn.setReadTimeout(this.readTimeout);
        return conn;
    }

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate
     */
 /*   private static void trustAllHosts() {
        final String TAG = "trustAllHosts";
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                Log.i(TAG, "checkClientTrusted");
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                Log.i(TAG, "checkServerTrusted");
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initSSL(Context context) {
        SSLSocketFactory sslSocketFactory = HttpsUtils.initHttpsSSL(context);
        if (sslSocketFactory == null) {
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslParams.sSLSocketFactory);
        } else {
            HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
        }

    }*/
}

