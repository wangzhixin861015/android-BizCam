package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.R;
import com.lansosdk.videoeditor.LanSoEditor;
import com.lansosdk.videoplayer.VPlayer;
import com.lansosdk.videoplayer.VideoPlayer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by a1234 on 17/3/29.
 */

public class ViewPagerVideoView extends BaseRelativeLayout {
    private RelativeLayout video_type;
    private TextureView textureView;
    private VideoBottomView bottom_view;
    private ImageView video_surface;
    private VideoInter videoInter;
    private ImageData imageData;
    private Context context;
    //private MediaPlayer mediaPlayer;
    private boolean isChanging;//false:开始播放,true,停止播放
    private Timer mTimer;
    private TimerTask mTimerTask;
    private int duration;
    private ImageView videoIcon;
    private Surface mSurface;
    private VPlayer vPlayer;

    public ViewPagerVideoView(Context context) {
        super(context);
    }

    public ViewPagerVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewPagerVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageData(ImageData imageData) {
        this.imageData = imageData;
        video_surface.setBackgroundColor(getResources().getColor(R.color.backgroud_new));
        video_surface.setVisibility(VISIBLE);
        ImageUtil.EBizImageLoad(video_surface, imageData.getSmallLocalUrl());
        if (imageData.getRecoderTime() == 0) {
            getRecoderInfo();
        } else {
            bottom_view.setSeekbarMax(imageData.getRecoderTime());
            bottom_view.setDuration(imageData.getRecoderTime());
        }
        //getRecoderInfo();
    }


    @Override
    protected void initView() {
        inflate(getContext(), R.layout.layout_video, this);
        video_type = (RelativeLayout) findViewById(R.id.video_type);
        textureView = (TextureView) findViewById(R.id.msurfaceview);
        bottom_view = (VideoBottomView) findViewById(R.id.bottom_view);
        video_surface = (ImageView) findViewById(R.id.video_surface);
        videoIcon = (ImageView) findViewById(R.id.videoIcon);
    }

    @Override
    protected void initData() {
        bottom_view.showVideoButton(false);
        LanSoEditor.initSDK(getContext());

    }

    private void initVideo() {
       /* if (mediaPlayer != null) {
            mediaPlayer.reset();
        } else {
            mediaPlayer = new MediaPlayer();
        }*/
        if (vPlayer != null) {
            vPlayer.release();
            vPlayer = new VPlayer(getContext());
        } else {
            vPlayer = new VPlayer(getContext());
        }

        if (imageData != null) {
            textureView.setLayoutParams(getRecoderInfo());
            if (mSurface == null) {
                mSurface = new Surface(textureView.getSurfaceTexture());
            }
            readyMedia(mSurface);
        }
    }

    public void setSurfaceTextureListener(TextureView.SurfaceTextureListener surfaceTextureListener) {
        if (textureView != null) {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    private RelativeLayout.LayoutParams getRecoderInfo() {
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(imageData.getLocalUrl());
        String height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); // 视频高度
        String width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH); // 视频宽度
        String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION); // 视频旋转方向
        String duration = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 视频时间
        bottom_view.setSeekbarMax(Integer.valueOf(duration));
        bottom_view.setDuration(Integer.valueOf(duration));
        this.duration = Integer.valueOf(duration);
        LogUtil.d(width + "  " + height + "  " + rotation + "  " + duration);
        int w = Integer.valueOf(width);
        int h = Integer.valueOf(height);

        if (w == h) {
            return initRecoderParms();
        } else {
            return initRecoderParms(w, h, Integer.valueOf(rotation));
        }
    }

    private RelativeLayout.LayoutParams initRecoderParms() {
        int w = ContentUtil.getScreenWidth(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, w);
        params.addRule(CENTER_IN_PARENT);
        return params;
    }

    private RelativeLayout.LayoutParams initRecoderParms(int width, int height, int rotation) {
        float h = height;
        float w = width;
        if (rotation == 90 || rotation == 270) {
            width = (int) h;
            height = (int) w;
        }

        float surfaceViewWidth = ContentUtil.getScreenWidth(getContext());
        float surfaceViewHeight = ContentUtil.getScreenHeight(getContext()) - ContentUtil.dip2px(getContext(), 75) - ContentUtil.getStatusBarHeight(getContext()) - 10;
        //视频大小以长边为主
        if (width < height) {
            h = surfaceViewHeight;
            w = width / (height / surfaceViewHeight);
        } else {
            w = surfaceViewWidth;
            h = height / (width / surfaceViewWidth);
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) w, (int) h);
        params.addRule(CENTER_IN_PARENT);
        return params;

    }

    //释放资源
    public void onViewPagerVideoViewGone() {
        bottom_view.setSeekbarProgress(0);
        video_surface.setVisibility(VISIBLE);
        videoIcon.setVisibility(VISIBLE);
        stopRunnable();
       /* if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }*/
        if (vPlayer != null) {
            vPlayer.release();
            vPlayer = null;
        }
        mSurface = null;
//        surfaceView.getHolder().removeCallback(surfaceCallback);
    }

    @Override
    protected void onViewClick() {
        textureView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playClicked();
            }
        });
        video_surface.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //暂停状态,开始播放,第一次播放(未初始化mediaplayer),第一次之后播放(已初始化mediaplayer)
                playClicked();
            }
        });
        bottom_view.setVideoBottomListener(new VideoBottomView.VideoBottomListener() {
            @Override
            public void onseekbarTracking(boolean istracking, int progress) {
                onSeekbarTracking(istracking, progress);
            }

            @Override
            public void onPlayClicked() {
                playClicked();
            }
        });

        videoIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playClicked();
            }
        });
    }

    public void setSurface(Surface mSurface) {
        this.mSurface = mSurface;
    }

    public void playClicked() {
        //暂停状态,开始播放,第一次播放(未初始化mediaplayer),第一次之后播放(已初始化mediaplayer)
        video_surface.setVisibility(GONE);
        // if (mediaPlayer != null) {
        // if (mediaPlayer.isPlaying()) {
        if (vPlayer != null) {
            if (vPlayer.isPlaying()) {
                pauseMediaPaly();
            } else {
                startMedia();
            }
        } else {
            initVideo();
        }
    }

    public void readyMedia(final Surface mSurface) {
        if (vPlayer != null) {
            if (vPlayer.isPlaying()) {
                vPlayer.stop();
            }
            vPlayer.setVideoPath(imageData.getLocalUrl());
            vPlayer.setLooping(false);
            vPlayer.prepareAsync();
            vPlayer.setOnPreparedListener(new VideoPlayer.OnPlayerPreparedListener() {
                @Override
                public void onPrepared(VideoPlayer mp) {
                    vPlayer.setSurface(mSurface);
                    isChanging = false;
                    vPlayer.seekTo(0);
                    bottom_view.setSeekbarProgress(0);
                    startRunnable();
                    vPlayer.setLooping(false);
                    vPlayer.start();
                    videoIcon.setVisibility(GONE);
                }
            });

            vPlayer.setOnCompletionListener(new VideoPlayer.OnPlayerCompletionListener() {
                @Override
                public void onCompletion(VideoPlayer mp) {
                    isChanging = true;
                    mp.seekTo(0);
                    bottom_view.setSeekbarProgress(0);
                    stopRunnable();
                    videoIcon.setVisibility(VISIBLE);
                }
            });
        }
    }

    public void pauseMediaPaly() {
        videoIcon.setVisibility(VISIBLE);
        videoIcon.bringToFront();
     /*   if (mediaPlayer != null) {
            mediaPlayer.pause();
            isChanging = true;
        }*/
        if (vPlayer != null) {
            vPlayer.pause();
            isChanging = true;
        }
    }

    public void startMedia() {
        videoIcon.setVisibility(GONE);
       /* if (mediaPlayer != null) {
            mediaPlayer.start();
            isChanging = false;
            if (mTimer == null || mTimerTask == null) {
                startRunnable();
            }
        }*/
        if (vPlayer != null) {
            vPlayer.start();
            isChanging = false;
            if (mTimer == null || mTimerTask == null) {
                startRunnable();
            }
        }
    }

    public void onSeekbarTracking(boolean istracking, int progress) {
       /* if (mediaPlayer != null) {
            if (istracking) {
                isChanging = true;
            } else {
                if (progress > 0 && progress <= this.duration)
                    mediaPlayer.seekTo(progress);
                isChanging = false;
            }
        }*/
        if (vPlayer != null) {
            if (istracking) {
                isChanging = true;
            } else {
                if (progress > 0 && progress <= this.duration)
                    vPlayer.seekTo(progress);
                isChanging = false;
            }
        }
    }

    private void startRunnable() {
        mTimer = new Timer();
       /* mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer != null && isChanging == false) {
                    bottom_view.setSeekbarProgress(mediaPlayer.getCurrentPosition());
                }
            }
        };*/
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (vPlayer != null && isChanging == false) {
                    bottom_view.setSeekbarProgress(vPlayer.getCurrentPosition());
                }
            }
        };
        mTimer.schedule(mTimerTask, 0, 10);
    }

    private void stopRunnable() {
        isChanging = true;
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimerTask = null;
        mTimer = null;
    }

    public interface VideoInter {
        void onClose();

    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == GONE) {
           /* if (mediaPlayer != null) {
                mediaPlayer.reset();
            }*/
            if (vPlayer != null) {
                vPlayer.release();
                vPlayer = new VPlayer(getContext());
            }
        }
    }

    public void onPause() {
       /* if (mediaPlayer != null) {
            onViewPagerVideoViewGone();
            setImageData(imageData);
        }*/
        if (vPlayer != null) {
            vPlayer.release();
            vPlayer = new VPlayer(getContext());
        }
    }


    public void setVideoInter(VideoInter videoInter) {
        this.videoInter = videoInter;
    }

}
