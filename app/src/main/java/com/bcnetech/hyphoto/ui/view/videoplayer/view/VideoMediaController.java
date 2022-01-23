package com.bcnetech.hyphoto.ui.view.videoplayer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.ui.adapter.BizCamHelpDetailAdapter;
import com.bcnetech.hyphoto.ui.view.videoplayer.utils.MediaHelper;


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
 * des ：对应视频播放控制界面的封装
 * gitVersion：2.12.0.windows.1
 * updateAuthor：AllenIverson
 * updateDate：2017/5/24 18:05
 * updateDes：${TODO}
 * ============================================================
 */

public class VideoMediaController extends RelativeLayout {

    private static final String TAG = "VideoMediaController";
    ImageView ivPlay;
    ImageView iv_cover;

    private boolean hasPause;//是否暂停

    private static final int MSG_HIDE_TITLE = 0;
    private static final int MSG_UPDATE_TIME_PROGRESS = 1;
    private static final int MSG_HIDE_CONTROLLER = 2;


//    public void delayHideTitle(){
//        //移除消息
//        mHandler.removeMessages(MSG_HIDE_TITLE);
//        //发送一个空的延时2秒消息
//        mHandler.sendEmptyMessageDelayed(MSG_HIDE_TITLE,2000);
//    }

    public VideoMediaController(Context context) {
        this(context, null);
    }

    public VideoMediaController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    //初始化控件
    private void initView() {
        View view = View.inflate(getContext(), R.layout.video_controller, this);

        iv_cover = view.findViewById(R.id.iv_cover);
        ivPlay = view.findViewById(R.id.iv_play);
//        initViewDisplay();
        //设置视频播放时的点击界面
    }








    private int position;

    public void setPosition(int position) {
        this.position = position;
    }

    private BizCamHelpDetailAdapter adapter;

    public void setAdapter(BizCamHelpDetailAdapter bizCamHelpDetailAdapter) {
        this.adapter = bizCamHelpDetailAdapter;
    }

    //初始化控件的显示状态
    public void initViewDisplay(Uri uri) {
        ivPlay.setVisibility(View.VISIBLE);
        ivPlay.setImageResource(R.drawable.video);

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(getContext(), uri);
        Bitmap bitmap = mmr.getFrameAtTime(500);//获取第一帧图片
        iv_cover.setImageBitmap(bitmap);
        mmr.release();//释放资源
    }

    public void play() {
//            case R.id.iv_replay:
//                //隐藏播放完成界面
//                rlPlayFinish.setVisibility(View.GONE);
//                //隐藏时间
//                tvAllTime.setVisibility(View.GONE);
//                tvUseTime.setText("00:00");
//                //进度条
//                seekBar.setProgress(0);
//                //把媒体播放器的位置移动到开始的位置
//                MediaHelper.getInstance().seekTo(0);
//                //开始播放
//                MediaHelper.play();
//                //延时隐藏标题
//                delayHideTitle();
//                break;

        //点击一个新的条目进行播放
        //点击的条目下标是否是之前播放的条目下标
        if (position != adapter.currentPosition && adapter.currentPosition != -1) {
            Log.i(TAG, "点击了其他的条目");

            //让其他的条目停止播放(还原条目开始的状态)
            MediaHelper.release();
            //把播放条目的下标设置给适配器
            adapter.setCurrentPosition(position);
            //刷新显示
            adapter.notifyDataSetChanged();
            //播放
            ivPlay.setVisibility(View.GONE);
            //视频播放界面也需要显示
            myMVideoPlayer.setVideoViewVisiable(View.VISIBLE);
            return;
        }

        if (MediaHelper.getInstance().isPlaying()) {
            //暂停
            MediaHelper.pause();

            ivPlay.setVisibility(VISIBLE);
            hasPause = true;
        } else {
            if (hasPause) {
                //继续播放
                MediaHelper.play();
                hasPause = false;
            } else {
                //播放
                //视频播放界面也需要显示
                myMVideoPlayer.setVideoViewVisiable(View.VISIBLE);
                //把播放条目的下标设置给适配器
                adapter.setCurrentPosition(position);
            }
            ivPlay.setVisibility(GONE);
        }
    }

    private MVideoPlayer myMVideoPlayer;

    public void setVideoPlayer(MVideoPlayer myMVideoPlayer) {
        this.myMVideoPlayer = myMVideoPlayer;
    }


    public void setCoverGone(){
        iv_cover.setVisibility(GONE);
        ivPlay.setVisibility(GONE);
    }

    public void setCoverVisible(){
        iv_cover.setVisibility(VISIBLE);
        ivPlay.setVisibility(VISIBLE);
    }

    public void setivPlayGone(){
        ivPlay.setVisibility(GONE);

    }

    public void setivPlayVisible(){
        ivPlay.setVisibility(VISIBLE);

    }
}
