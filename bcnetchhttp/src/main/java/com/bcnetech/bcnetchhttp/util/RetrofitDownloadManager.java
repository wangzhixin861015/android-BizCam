package com.bcnetech.bcnetchhttp.util;

import com.bcnetech.bcnetchhttp.RetrofitUploadFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by a1234 on 2018/9/14.
 */

public class RetrofitDownloadManager {
    private static RetrofitDownloadManager downloadManager;
    private DownloadListener downloadListener;
    private String savePath;
    private String suffix;

    public RetrofitDownloadManager() {
    }


    public static RetrofitDownloadManager Instance() {
        if (downloadManager == null) {
            downloadManager = new RetrofitDownloadManager();
        }
        return downloadManager;
    }

    public synchronized void download(final String url) {
        RetrofitUploadFactory.getUPloadInstence()
                .API()
                .downloadFile(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            InputStream is = responseBody.byteStream();

                            File file = new File(savePath, System.currentTimeMillis() + suffix);
                            FileOutputStream fos = new FileOutputStream(file);
                            BufferedInputStream bis = new BufferedInputStream(is);
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = bis.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                                fos.flush();
                            }
                            fos.close();
                            bis.close();
                            is.close();
                            downloadListener.onResponse(responseBody, file.getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                            downloadListener.onError(e);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        downloadListener.onError(e);

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void setFileInfo(String savePath, String suffix) {
        this.savePath = savePath;
        this.suffix = suffix;
    }

    /**
     * 根据url获取后缀名
     *
     * @param url
     * @return
     */
    private String getFileName(String url) {
        String localUrl = "";
        if (url.contains("?code=0")) {
            String temp[] = url.split("\\?");
            if (temp.length > 1) {
                localUrl = temp[0];
            }
        } else {
            localUrl = url;
        }
        int separatorIndex = localUrl.lastIndexOf("/");
        return (separatorIndex < 0) ? localUrl : localUrl.substring(separatorIndex + 1, localUrl.length());
    }

    public interface DownloadListener {
        void onResponse(ResponseBody responseBody, String savePath);

        void onError(Throwable e);
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }
}
