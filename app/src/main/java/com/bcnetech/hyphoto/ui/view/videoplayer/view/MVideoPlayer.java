package com.bcnetech.hyphoto.ui.view.videoplayer.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.RelativeLayout;

import com.bcnetech.hyphoto.R;


/**
 * ============================================================
 * Copyright：JackChan和他的朋友们有限公司版权所有 (c) 2017
 * Author：   JackChan
 * Email：    815712739@qq.com
 * GitHub：   https://github.com/JackChan1999
 * GitBook：  https://www.gitbook.com/@alleniverson
 * CSDN博客： http://blog.csdn.net/axi295309066
 * 个人博客： https://jackchan1999.github.io/
 * 微博：     AndroidDeveloper
 * <p>
 * Project_Name：VideoPlayer
 * Package_Name：com.jackchan.videoplayer
 * Version：1.0
 * time：2017/5/24 18:05
 * des ：对于视频播放界面的一个封装类
 * gitVersion：2.12.0.windows.1
 * updateAuthor：AllenIverson
 * updateDate：2017/5/24 18:05
 * updateDes：${TODO}
 * ============================================================
 */

public class MVideoPlayer extends RelativeLayout {
    private static final String TAG = "VideoPlayer";

    public TextureView videoView;
    public VideoMediaController mediaController;

    public MediaPlayer mPlayer;
    private Surface mSurface;

    public boolean hasPlay;//是否播放了
    private Uri uri;

    public MVideoPlayer(Context context) {
        this(context, null);
    }

    public MVideoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = View.inflate(getContext(), R.layout.video_play, this);
        videoView=view.findViewById(R.id.tv_video);
        mediaController=view.findViewById(R.id.mediaController);

        //把VideoPlayer对象传递给VideoMediaController
        mediaController.setVideoPlayer(this);

        //进行TextureView控件创建的监听
        videoView.setSurfaceTextureListener(surfaceTextureListener);
    }


    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {

        //创建完成  TextureView才可以进行视频画面的显示
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
           // Log.i(TAG,"onSurfaceTextureAvailable");
            mSurface = new Surface(surface);//连接对象（MediaPlayer和TextureView）
            play(uri);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
           // Log.i(TAG,"onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
           // Log.i(TAG,"onSurfaceTextureDestroyed");
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
           // Log.i(TAG,"onSurfaceTextureUpdated");
        }
    };

    //视频播放（视频的初始化）
    private void play(Uri uri){
        try {
            mPlayer =new MediaPlayer();
            mPlayer.reset();
            mPlayer.setDataSource(getContext(),uri);
            //让MediaPlayer和TextureView进行视频画面的结合
            mPlayer.setSurface(mSurface);
            //设置监听
            mPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
            mPlayer.setOnCompletionListener(onCompletionListener);
            mPlayer.setOnErrorListener(onErrorListener);
            mPlayer.setOnPreparedListener(onPreparedListener);
            mPlayer.setScreenOnWhilePlaying(true);//在视频播放的时候保持屏幕的高亮
            //异步准备
            mPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //准备完成监听
    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {

            //隐藏视频加载进度条
            //进行视频的播放
            mPlayer.start();
            hasPlay = true;
            mediaController.setCoverGone();

        }
    };

    //错误监听
    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return true;
        }
    };

    //完成监听
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //视频播放完成
            mp.start();
        }
    };

    //缓冲的监听
    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
//            Log.i(TAG,"percent:"+percent);
        }
    };


    //初始化控件的显示状态
    public void initViewDisplay(Uri uri) {
        videoView.setVisibility(View.GONE);
        mediaController.initViewDisplay(uri);
    }

    public void stopVideo(){
        if(null!=mPlayer){
            mPlayer.pause();
            mPlayer.seekTo(0);
        }
        mediaController.setCoverVisible();
        videoView.setVisibility(View.GONE);
    }

    public void playVideo(){
        videoView.setVisibility(View.VISIBLE);
        if(null!=mPlayer){
            mPlayer.start();
        }
        mediaController.setCoverGone();
    }

    public void stopPlay(){
        if(mPlayer.isPlaying()){
            mPlayer.pause();
            mediaController.setivPlayVisible();

        }else {
            mPlayer.start();
            mediaController.setivPlayGone();

        }
    }

    public void playFirst(){
        mediaController.play();
    }

    //设置视频播放界面的显示
    public void setVideoViewVisiable(int visible) {
        videoView.setVisibility(View.VISIBLE);
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
