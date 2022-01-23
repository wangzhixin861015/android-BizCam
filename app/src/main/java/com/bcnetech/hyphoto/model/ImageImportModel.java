package com.bcnetech.hyphoto.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog3;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.SDCardMedia;
import com.bcnetech.hyphoto.data.SqlControl.ImageDataSqlControl;
import com.bcnetech.hyphoto.receiver.AddPicReceiver;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Queue;

/**
 * 导入图片model
 * Created by a1234 on 2018/8/13.
 */
public class ImageImportModel {
    private Context context;
    private ImageDataSqlControl imageDataSqlControl;
    private DGProgressDialog3 dgProgressDialog3;
    private static ImageImportModel imageSelectorModel;
    private Queue<SDCardMedia> SDCardMediaQueue;
    private ImageSelectCallBack imageSelectCallBack;

    public static ImageImportModel getImageSelectInstance() {
        return imageSelectorModel == null ? initImageSelectorModel() : imageSelectorModel;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setImageDataSqlControl(ImageDataSqlControl imageDataSqlControl) {
        this.imageDataSqlControl = imageDataSqlControl;
    }

    private static ImageImportModel initImageSelectorModel() {
        return new ImageImportModel();
    }

    /**
     * 存储导入的图片
     */

    private class SaveIntoPicTask extends AsyncTask<Void, Void, ImageData> {
        private SDCardMedia sdCardMedia;
        private ImageData imageData;
        private Bitmap bitmap;

        SaveIntoPicTask(SDCardMedia sdCardMedia) {
            this.sdCardMedia = sdCardMedia;

        }

        protected void onPreExecute() {
            // canClick = false;
//            showDialog();
        }

        @Override
        protected ImageData doInBackground(Void... params) {
            final long time = System.currentTimeMillis();
            final long time2 = time - 1;
            String pathApp_Cameral, pathNATIVESDFILE;
            int index = sdCardMedia.getPath().lastIndexOf(".");  //获取出现key字符串的第一个位置，这里要保证前面没有跟KEY重复
            if(index==-1){
                return null;
            }
            String right = sdCardMedia.getPath().substring(index); //后面的字符串，
            final String suffix = right.toLowerCase();
            if (sdCardMedia.isVideo() == SDCardMedia.VIDEO) {
                //视频：拷贝视频文件，生成封面图片
                pathApp_Cameral = FileUtil.copyFile(sdCardMedia.getPath(), Flag.APP_CAMERAL, time + suffix);
                String videoname = System.currentTimeMillis() + "";
                Bitmap video = getVideoBitmap(pathApp_Cameral);
                try {
                    pathNATIVESDFILE = "file://" + FileUtil.saveBitmaptoNative(video, videoname, false);
                } catch (Exception e) {
                    pathNATIVESDFILE = "";
                }
            } else {
                //图片：gif:转换成jpg
                if (sdCardMedia.getPath().endsWith("gif")) {
                    try {
                        String url = "file://" + sdCardMedia.getPath();
                        bitmap = Picasso.get().load(url).get();
                        pathNATIVESDFILE = "file://" + FileUtil.saveBitmaptoNative(bitmap, time + "", false);
                        pathApp_Cameral = "file://" + FileUtil.saveBitmap(bitmap, time2 + "", false);
                    } catch (IOException e) {
                        pathNATIVESDFILE = "";
                        pathApp_Cameral = "";
                    }
                } else {
                    //png或jpg：直接拷贝
                    pathNATIVESDFILE = "file://" + FileUtil.copyFile(sdCardMedia.getPath(), Flag.NATIVESDFILE, time + suffix);
                    pathApp_Cameral = "file://" + FileUtil.copyFile(sdCardMedia.getPath(), Flag.APP_CAMERAL, time2 + suffix);
                }
            }

            imageData = new ImageData();
            if (StringUtil.isBlank(pathNATIVESDFILE) || StringUtil.isBlank(pathApp_Cameral)) {
                return null;
            } else {
                //未连接蓝牙,光比值为空
                LightRatioData mCurrentLightRatio = new LightRatioData();
                mCurrentLightRatio.initData();
                imageData.setLightRatioData(mCurrentLightRatio);
                if (sdCardMedia.isVideo() == SDCardMedia.VIDEO) {
                    imageData.setType(Flag.TYPE_VIDEO);
                    imageData.setRecoderTime(Integer.parseInt(sdCardMedia.getVideo_duration()));
                } else {
                    imageData.setType(Flag.TYPE_PIC);
                }
                imageData.setLocalUrl(pathApp_Cameral);
                imageData.setSmallLocalUrl(pathNATIVESDFILE);
                imageData.setCurrentPosition(0);
                imageData.setTimeStamp(time);

                imageDataSqlControl.insertImg(imageData);
               /* String imageDataJson = JsonUtil.Object2Json(imageData);
                try {
                    FileUtil.saveImageDataJson(time + "", imageDataJson);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                return imageData;
            }
        }

        @Override
        protected void onPostExecute(ImageData result) {
            super.onPostExecute(result);
            if (bitmap != null)
                bitmap.recycle();
            if (result != null) {
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(result.getSmallLocalUrl())));
                StringBuffer sb = new StringBuffer();
                if (null != dgProgressDialog3) {
                    sb.append(context.getResources().getString(R.string.importing));
                    sb.append(" ");
                    sb.append("(");
                    sb.append(context.getResources().getString(R.string.rest_count));
                    sb.append(" ");
                    sb.append(SDCardMediaQueue.size());
                    sb.append(")");
                    sb.append(" ");
                    sb.append("...");
                    dgProgressDialog3.setTitle(sb.toString());
                }
                if (imageSelectCallBack != null)
                    imageSelectCallBack.onImageImporting(SDCardMediaQueue.size());
                startImport(SDCardMediaQueue);
            } else {
                ToastUtil.toast(context.getResources().getString(R.string.into_fail));
            }
            AddPicReceiver.notifyModifyUsername(context, "add");
        }
    }

    private Bitmap getVideoBitmap(String url) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(url);
        return media.getFrameAtTime();
    }

    /**
     * 复制队列
     *
     * @param queue
     */
    public synchronized void startImport(final Queue<SDCardMedia> queue) {
        if (queue != null && !queue.isEmpty()) {
            this.SDCardMediaQueue = queue;
            if (null == dgProgressDialog3) {
                StringBuffer sb = new StringBuffer();
                sb.append(context.getResources().getString(R.string.importing));
                sb.append(" ");
                sb.append("(");
                sb.append(context.getResources().getString(R.string.rest_count));
                sb.append(" ");
                sb.append(queue.size());
                sb.append(")");
                sb.append(" ");
                sb.append("...");
                dgProgressDialog3 = new DGProgressDialog3(context, sb.toString());
                dgProgressDialog3.show();
            } else {
                dgProgressDialog3.show();
            }
            final SDCardMedia SDCardMedia = queue.poll();
            SDCardMedia.getPath();
            SaveIntoPicTask saveIntoPicTask = new SaveIntoPicTask(SDCardMedia);
            saveIntoPicTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            startHintTime();
            AddPicReceiver.notifyModifyUsername(context, "");

        }
    }

    private ClassHint editCut;
    private Handler mHandler = new Handler();//全局handler
    private int errorTime = 1;//错误倒计时

    /**
     * 开启倒计时
     */
    public void startHintTime() {
        errorTime = 1;
        editCut = new ClassHint();
        editCut.start();
    }

    class ClassHint extends Thread implements Runnable {//倒计时逻辑子线程


        @Override
        public void run() {
            while (errorTime > 0) {//整个倒计时执行的循环
                errorTime--;

                try {
                    Thread.sleep(100);//线程休眠一秒钟     这个就是倒计时的间隔时间
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            //下面是倒计时结束逻辑
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    dgProgressDialog3.dismiss();
                    if (imageSelectCallBack != null)
                        imageSelectCallBack.onImageImportOver();
                    // finish();
                }
            });
            errorTime = 1;//修改倒计时剩余时间变量为3秒
        }
    }

    public interface ImageSelectCallBack {
        void onImageImporting(int restCount);

        void onImageImportOver();
    }

    public void setImageSelectCallBack(ImageSelectCallBack imageSelectCallBack) {
        this.imageSelectCallBack = imageSelectCallBack;
    }


}
