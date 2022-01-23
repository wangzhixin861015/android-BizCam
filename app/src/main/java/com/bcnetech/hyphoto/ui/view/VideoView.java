package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bcnetech.hyphoto.sql.dao.ImageData;

/**
 * Created by a1234 on 2018/5/14.
 */

public class VideoView extends FrameLayout implements  TextureView.SurfaceTextureListener {
    private FrameLayout mContainer;
    private ViewPagerVideoView viewPagerVideoView;
    private Surface mSurface;

    public VideoView(@NonNull Context context) {
        super(context);
        init();
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        mContainer = new FrameLayout(getContext());
        mContainer.setBackgroundColor(Color.WHITE);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        initTextureView();
        this.addView(mContainer, params);
    }

    private void initTextureView() {
        if (viewPagerVideoView == null) {
            viewPagerVideoView = new ViewPagerVideoView(getContext());
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            viewPagerVideoView.setLayoutParams(params);
            viewPagerVideoView.setSurfaceTextureListener(this);
        }
    }

    /**
     * surface生命周期
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        viewPagerVideoView.setSurface(mSurface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        viewPagerVideoView.setSurface(mSurface);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    /**\
     * videoplayer操作
     */
    public void pauseMediaPaly(){
        viewPagerVideoView.pauseMediaPaly();
    }

    public void startMedia(){
        viewPagerVideoView.startMedia();
    }

   public void onSeekbarTracking(boolean istracking, int progress) {
        viewPagerVideoView.onSeekbarTracking(istracking,progress);
   }
    public void onPause(){
        //viewPagerVideoView.onPause();
        if (this.getVisibility() == VISIBLE) {
            viewPagerVideoView.onPause();
            mContainer.removeView(viewPagerVideoView);
        }
    }

    public void onResume(){
        if (this.getVisibility() == VISIBLE){
            showVideoView();
        }
    }
    public void setVideoInter(ViewPagerVideoView.VideoInter videoInter) {
        viewPagerVideoView.setVideoInter(videoInter);
    }

    public void onViewPagerVideoViewGone() {
        mContainer.removeView(viewPagerVideoView);
        viewPagerVideoView.onViewPagerVideoViewGone();
    }

    public void showVideoView(){
        mContainer.removeView(viewPagerVideoView);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        mContainer.addView(viewPagerVideoView, 0, params);
    }

    public void setSurfaceTextureListener( TextureView.SurfaceTextureListener surfaceTextureListener){
        viewPagerVideoView.setSurfaceTextureListener(surfaceTextureListener);
    }

    public void setImageData(ImageData imageData) {
        viewPagerVideoView.setImageData(imageData);
    }
}
