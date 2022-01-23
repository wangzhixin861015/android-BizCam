package com.bcnetech.hyphoto.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;

import java.io.File;

/**
 * Created by wangzhixin on 2017/6/1.
 */

public class UpdataService extends Service {
    public static final int HANDLE_DOWNLOAD = 0x001;
    private String DOWNLOADPATH = "/apk/";//下载路径，如果不定义自己的路径，6.0的手机不自动安装
    public static final String BUNDLE_KEY_DOWNLOAD_URL = "http://www.bcyun.com/bizcam.apk";
    public static final float UNBIND_SERVICE = 1.0F;

    private DownloadBinder binder;


    private OnProgressListener onProgressListener;


    @Override
    public void onCreate() {
        super.onCreate();
        binder = new DownloadBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        downloadApk(BUNDLE_KEY_DOWNLOAD_URL);
        return binder;
    }

    /**
     * 下载最新APK
     */
    private void downloadApk(String url) {
        final String ApkName = System.currentTimeMillis() + ".apk";
       /* FileDownloader.setup(this);
        FileDownloader.getImpl().create(url).setPath(Flag.APKSAVE_PATH + ApkName).setListener(new FileDownloadLargeFileListener() {
            @Override
            protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {

            }

            @Override
            protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                onProgressListener.onProgress(soFarBytes / (float) totalBytes);
            }

            @Override
            protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

            }

            @Override
            protected void completed(BaseDownloadTask task) {
                onProgressListener.onProgress(UNBIND_SERVICE);
                installNormal(UpdataService.this,Flag.APKSAVE_PATH + ApkName);
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
            }

            @Override
            protected void warn(BaseDownloadTask task) {
            }
        }).start();*/

        DownloadTask task = new DownloadTask.Builder(url, Flag.APKSAVE_PATH,ApkName)
                .setMinIntervalMillisCallbackProcess(300)
                .build();
        task.enqueue(new DownloadListener1() {
            @Override
            public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {

            }

            @Override
            public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {

            }

            @Override
            public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {

            }

            @Override
            public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
                onProgressListener.onProgress(currentOffset / (float) totalLength);
            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {
                installNormal(UpdataService.this, Flag.APKSAVE_PATH + ApkName);
                task.cancel();
            }
        });
    }

    //普通安装
    private static void installNormal(Context context, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW); //版本在7.0以上是不能直接通过uri访问的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File file = new File(apkPath);
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, "com.bcnetech.hyphoto.FileProvider", file);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }


    public class DownloadBinder extends Binder {
        /**
         * 返回当前服务的实例
         *
         * @return
         */
        public UpdataService getService() {
            return UpdataService.this;
        }

    }

    public interface OnProgressListener {
        /**
         * 下载进度
         *
         * @param fraction 已下载/总大小
         */
        void onProgress(float fraction);
    }

    /**
     * 对外开发的方法
     *
     * @param onProgressListener
     */
    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i("下载任务服务销毁");
    }

}
