package com.bcnetech.hyphoto.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Size;
import android.view.Surface;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.ImageDataSqlControl;
import com.bcnetech.hyphoto.presenter.iview.IRecoderView;
import com.bcnetech.hyphoto.receiver.AddPicReceiver;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.ui.activity.camera.RecoderActivity;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;
import com.bcnetech.hyphoto.R;
import com.lansosdk.videoeditor.LanSoEditor;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;
import com.lansosdk.videoplayer.VPlayer;
import com.lansosdk.videoplayer.VideoPlayer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by wenbin on 2017/3/20.
 */

public class RecoderPresenter extends BasePresenter<IRecoderView> {
    private ImageData imageData;
    private boolean isPlaying;//true：正在播放，false：停止播放
    private int durtion;
    private ImageDataSqlControl imageDataSqlControl;
    private android.os.Handler handler;
    //private TextureVideoPlayer textureVideoPlayer;
    private VPlayer vPlayer;
    private VideoEditor videoEditor;
    private Surface surface;

    /***
     * @param activity
     * @param imageData
     * @param recoderType 视频录制  RECODER_TYPE：视频播放 Flag.NULLCODE：原始类型
     * @param code        Flag.NULLCODE:不返回
     */
    public static void startAction(Activity activity, ImageData imageData, String recoderType, int code) {
        Intent intent = new Intent(activity, RecoderActivity.class);
        intent.putExtra(Flag.IMAGE_DATA, imageData);
        intent.putExtra(Flag.CAMERA_TYPE, recoderType);
        if (code == Flag.NULLCODE) {
            activity.startActivity(intent);
        } else {
            activity.startActivityForResult(intent, code);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Flag.IMAGE_DATA, imageData);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageData = (ImageData) savedInstanceState.getSerializable(Flag.IMAGE_DATA);
    }

    @Override
    public void onCreate(Bundle bundle) {
        if (bundle != null) {
            imageData = (ImageData) bundle.getSerializable(Flag.IMAGE_DATA);
        }else {
            imageData = (ImageData) activity.getIntent().getSerializableExtra(Flag.IMAGE_DATA);
        }
        LanSoEditor.initSDK(activity);
        initRecoderSql();
        handler = new android.os.Handler();
    }

    @Override
    public void onResume() {
        super.onResume();
        // initData();
        if (mView.getTextureView().getSurfaceTexture() != null) {
            if (vPlayer.isPlaying()) {
                vPlayer.start();
            }
           /* vPlayer.prepareAsync();
            surface = new Surface(mView.getTextureView().getSurfaceTexture());
            vPlayer.setSurface(surface);*/
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (vPlayer != null) {
            vPlayer.pause();
            isPlaying = false;
            if (mView != null)
                mView.setVideoStatus(isPlaying);
        }
    }

    public void initData() {
        vPlayer = new VPlayer(activity);
        vPlayer.setVideoPath(imageData.getLocalUrl());
        vPlayer.setLooping(false);
        vPlayer.prepareAsync();
        vPlayer.setOnCompletionListener(new VideoPlayer.OnPlayerCompletionListener() {
            @Override
            public void onCompletion(VideoPlayer mp) {
                isPlaying = false;
                if (mView != null)
                    mView.setVideoStatus(isPlaying);
            }
        });

        vPlayer.setOnPreparedListener(new VideoPlayer.OnPlayerPreparedListener() {
            @Override
            public void onPrepared(VideoPlayer mp) {
                if (mView.getTextureView().getSurfaceTexture() != null) {
                    surface = new Surface(mView.getTextureView().getSurfaceTexture());
                    vPlayer.setSurface(surface);
                    isPlaying = false;
                    vPlayer.seekTo(0);
                }
            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onDestroy() {
        if (vPlayer != null) {
            vPlayer.release();
        }
    }

    public RelativeLayout.LayoutParams getRecoderInfo(boolean isreverse) {
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(imageData.getLocalUrl());
        String height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); // 视频高度
        String width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH); // 视频宽度
        String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION); // 视频旋转方向
        String duration = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 视频时间
        LogUtil.d(width + "  " + height + "  " + rotation + "  " + duration);
        this.durtion = Integer.valueOf(duration);
        if (width != null && height != null) {
            int w = Integer.valueOf(width);
            int h = Integer.valueOf(height);
            int oration = Integer.valueOf(rotation);
            if (!isreverse) {
                return calculateVideoOutput(w, h, oration);
            } else {
                return calculateVideoOutput(h, w, oration);
            }
        } else {
            return initRecoderParms();
        }
    }

    private RelativeLayout.LayoutParams calculateVideoOutput(int width, int height, int rotation) {
        int new_w, new_h;
        if (rotation == 90) {
            new_w = height;
            new_h = width;
        } else {
            new_w = width;
            new_h = height;
        }
        RelativeLayout.LayoutParams layoutParams;
        if (new_w > new_h) {
            int w = ContentUtil.getScreenWidth(activity);
            int h = new_h * w / new_w;
            layoutParams = new RelativeLayout.LayoutParams((int) w, (int) h);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else if (new_w == new_h) {
            int w = ContentUtil.getScreenWidth(activity);
            layoutParams = new RelativeLayout.LayoutParams(w, w);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else {
            int h = ContentUtil.getScreenWidth(activity);
            int w = new_w * h / new_h;
            layoutParams = new RelativeLayout.LayoutParams((int) w, (int) h);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        }
        return layoutParams;
    }

    private RelativeLayout.LayoutParams initRecoderParms() {
        int w = ContentUtil.getScreenWidth(activity);
        return new RelativeLayout.LayoutParams(w, w);
    }

    public void startMediaPaly() {
        if (vPlayer != null) {
            if (surface == null) {
                surface = new Surface(mView.getTextureView().getSurfaceTexture());
                vPlayer.setSurface(surface);
            }
            if (vPlayer.isPlaying()) {
                vPlayer.pause();
                isPlaying = false;

            } else {

                isPlaying = true;
                vPlayer.start();
            }
            if (mView != null)
                mView.setVideoStatus(isPlaying);
        }
    }

    public void stopMediaPaly() {
        if (vPlayer != null) {
            vPlayer.stop();
        }

       /* if (textureVideoPlayer != null && textureVideoPlayer.isPlaying()) {
            textureVideoPlayer.stop();
        }*/
    }


    public void releaseMediaPaly() {

    }

    public ImageData getImageData() {
        return imageData;
    }


    public void deletRecoder() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                releaseMediaPaly();
                File file = new File(imageData.getLocalUrl());
                file.delete();
                File fileimg = new File(imageData.getSmallLocalUrl());
                fileimg.delete();
                // FileUtil.clearFolders(Flag.FFMPEG_CACHE);
                mView.finishView(0, null);
            }
        });

    }

    private class FFmpegSaveTask extends AsyncTask<Void, Void, HashMap<String, String>> {
        private String videoUrl;
        private String bitmapUrl;

        FFmpegSaveTask(String videoUrl, String bitmapUrl) {
            this.videoUrl = videoUrl;
            this.bitmapUrl = bitmapUrl;
        }

        @Override
        protected HashMap<String, String> doInBackground(Void... params) {
            String newvideo = FileUtil.copyFile(videoUrl, Flag.APP_CAMERAL, System.currentTimeMillis() + ".mp4");
            String newbitmap = FileUtil.copyFile(bitmapUrl, Flag.NATIVESDFILE, System.currentTimeMillis() + ".jpg");
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("video", newvideo);
            hashMap.put("bitmap", newbitmap);
            return hashMap;

        }


        @Override
        protected void onPostExecute(HashMap<String, String> result) {
            super.onPostExecute(result);
            if (result.get("video").contains("file:/"))
                videoUrl = result.get("video").substring(7);
            imageData.setLocalUrl(videoUrl);
          /*  if (!result.get("bitmap").contains("file:/"))
                bitmapUrl = "file:/" + result.get("bitmap");*/
            bitmapUrl = result.get("bitmap");
            if (!bitmapUrl.contains("file:/")) {
                bitmapUrl = "file://" + bitmapUrl;
            }
            imageData.setSmallLocalUrl(bitmapUrl);
            imageData.setRecoderTime(RecoderPresenter.this.durtion);
            imageDataSqlControl.insertImg(imageData);
            AddPicReceiver.notifyModifyUsername(activity, "");
            activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(imageData.getLocalUrl())));
            mView.hideLoading();
        }
    }

    String targetUrl;
    Bitmap watermarkRotate;

    public void saveRecoder() {
        videoEditor = new VideoEditor();
        final BitmapUtils.BitmapSize VideoSize = BitmapUtils.getBitmapSize(imageData.getSmallLocalUrl().contains("file:") ? imageData.getSmallLocalUrl().substring(7) : imageData.getSmallLocalUrl());
        mView.showLoading();
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                //强制设置视频播放方向为0度
                targetUrl = videoEditor.executeSetVideoMetaAngle(imageData.getLocalUrl(), 0);
                imageData.setLocalUrl(targetUrl);
                if (CameraStatus.getCameraStatus().getWaterMark().isWaterMarkOn()) {
                    //加水印和旋转视频
                    targetUrl = processOverLayAndRotate(VideoSize);
                } else if (mView.getRotateCount() != 0) {
                    //仅旋转视频
                    targetUrl = videoEditor.executeRotateAngle2(imageData.getLocalUrl(), 90 * mView.getRotateCount());
                } else {
                    // 不进行修改
                    targetUrl = "";
                }
                if (!TextUtils.isEmpty(targetUrl)) {
                    //进行修改，重新保存封面和视频
                    MediaMetadataRetriever media = new MediaMetadataRetriever();
                    media.setDataSource(targetUrl);
                    Bitmap bitmap = media.getFrameAtTime();
                    String bitmapUrl = imageData.getSmallLocalUrl();
                    if (bitmap != null) {
                        bitmapUrl = FileUtil.saveBitmap2New(bitmap, 0, System.currentTimeMillis() + "", CameraStatus.Size.TYPE_916, false, false, null,false);
                    }
                    if (!bitmapUrl.contains("file")) {
                        bitmapUrl = "file://" + bitmapUrl;
                    }
                    imageData.setSmallLocalUrl(bitmapUrl);
                    imageData.setLocalUrl(targetUrl);
                    imageData.setRecoderTime(RecoderPresenter.this.durtion);
                    imageDataSqlControl.insertImg(imageData);
                    AddPicReceiver.notifyModifyUsername(activity, "");
                    activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(imageData.getLocalUrl())));
                    mView.hideLoading();
                } else {
                    //不进行修改，直接保存
                    FFmpegSaveTask fFmpegSaveTask = new FFmpegSaveTask(imageData.getLocalUrl(), imageData.getSmallLocalUrl());
                    fFmpegSaveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

        videoEditor.setOnProgessListener(new onVideoEditorProgressListener() {
            @Override
            public void onProgress(VideoEditor v, int percent) {
            }
        });
    }

    /**
     * 进行加水印和旋转的操作
     *
     * @param VideoSize
     * @return
     */
    private String processOverLayAndRotate(BitmapUtils.BitmapSize VideoSize) {
        Bitmap wm_bitcam;
        if (CameraStatus.getCameraStatus().getWaterMark().getWatermarkUrl() == null) {
            wm_bitcam = BitmapFactory.decodeResource(activity.getResources(), R.drawable.wm_bizcam);
        } else {
            wm_bitcam = BitmapFactory.decodeFile(CameraStatus.getCameraStatus().getWaterMark().getWatermarkUrl());
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(mView.getRotateCount() * 270);
        watermarkRotate = Bitmap.createBitmap(wm_bitcam, 0, 0, wm_bitcam.getWidth(), wm_bitcam.getHeight(), matrix, false);
        final Size overLayPosition = calculateOverLayPosition(mView.getRotateCount(), VideoSize.width, VideoSize.height, watermarkRotate);
        String bitmapUrl;
        try {
            bitmapUrl = FileUtil.cacheSaveBitmap(watermarkRotate, "wmrotate", true);
        } catch (IOException e) {
            bitmapUrl = "";
        }
        return videoEditor.executeRotateOverLayVideo(imageData.getLocalUrl(), bitmapUrl, overLayPosition.getWidth(), overLayPosition.getHeight(), 90 * mView.getRotateCount());
    }

    /**
     * 根据视频旋转角度判断水印应该添加的位置（水印已经随角度旋转过了）
     *
     * @param rotate
     * @param Videowidth
     * @param Videoheight
     * @param watermark
     * @return
     */
    private Size calculateOverLayPosition(int rotate, int Videowidth, int Videoheight, Bitmap watermark) {
        Size size;
        float paddingLeft, paddingTop, watermarkWidth, watermarkHeight, padding;
        RectF rectf = judgeOverLayPosition(watermark, Videowidth, Videoheight);
        watermarkWidth = rectf.left;
        watermarkHeight = rectf.top;
        padding = rectf.right;//水印距离底边的高度
        paddingLeft = Videowidth - watermarkWidth - padding;
        paddingTop = Videoheight - watermarkHeight - padding;
        switch (rotate) {
            case 1:
                //旋转90度:水印绘制在右上角
                size = new Size((int) paddingLeft, (int) padding);//底部过高
                break;
            case 2:
                //旋转180度：水印绘制在左上角
                size = new Size((int) padding, (int) padding);
                break;
            case 3:
                //旋转270度：水印绘制在左下角
                size = new Size((int) padding, (int) paddingTop);//右部过低
                break;
            default:
                //不旋转：水印绘制在右下角
                size = new Size((int) paddingLeft, (int) paddingTop);
                break;
        }
        return size;
    }

    public Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    /**
     * 计算水印距离底边的留白空间（视频宽高的1／40）
     *
     * @param watermark
     * @param Videowidth
     * @param Videoheight
     * @return
     */
    private RectF judgeOverLayPosition(Bitmap watermark, int Videowidth, int Videoheight) {
        int padding;
        if (Videowidth > Videoheight) {
            padding = Videoheight / 40;
        } else {
            padding = Videowidth / 40;
        }

        float paddingLeft = 0;
        float paddingTop = 0;
        float watermarkWidth = 0;
        float watermarkHeight = 0;
        if (watermark.getWidth() > watermark.getHeight())

        {
            watermarkWidth = 8 * padding;
            watermarkHeight = watermarkWidth * ((float) watermark.getHeight() / (float) watermark.getWidth());
        } else

        {
            watermarkHeight = 8 * padding;
            watermarkWidth = watermarkHeight * ((float) watermark.getWidth() / (float) watermark.getHeight());
        }
        watermarkRotate = resizeImage(watermark, (int) watermarkWidth, (int) watermarkHeight);
        return new RectF(watermarkWidth, watermarkHeight, padding, 0);
    }


    public Integer getDuration() {
        return this.durtion;
    }

    /**
     * 初始化数据控制
     */
    private void initRecoderSql() {
        imageDataSqlControl = new ImageDataSqlControl(activity);
        imageDataSqlControl.setListener(new BaseSqlControl.SqlControlListener() {
            @Override
            public void queryListener(Object... parms) {

            }

            @Override
            public void insertListener(Object... parms) {
                mView.showToast(activity.getResources().getString(R.string.save_ok));
                mView.finishView(0, null);
            }

            @Override
            public void deletListener(Object... parms) {

            }

            @Override
            public void upDataListener(Object... parms) {

            }
        });
    }
}