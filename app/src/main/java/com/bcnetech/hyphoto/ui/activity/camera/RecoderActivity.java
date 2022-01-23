package com.bcnetech.hyphoto.ui.activity.camera;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;

import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.databinding.RecoderLayoutBinding;
import com.bcnetech.hyphoto.presenter.RecoderPresenter;
import com.bcnetech.hyphoto.presenter.WaterMarkPresenter;
import com.bcnetech.hyphoto.presenter.iview.IRecoderView;
import com.bcnetech.hyphoto.ui.activity.BaseMvpActivity;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;
import com.bcnetech.hyphoto.R;

/**
 * Created by wenbin on 2017/3/20.
 */

public class RecoderActivity extends BaseMvpActivity<IRecoderView, RecoderPresenter> implements IRecoderView {
    private RecoderLayoutBinding recoderLayoutBinding;
    private boolean isplaying = false;
    private boolean canClick = true;
    private Bitmap coverbitmap;
    int rotateCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        onViewClick();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (coverbitmap != null) {
            recoderLayoutBinding.ivCover.setVisibility(View.VISIBLE);
            recoderLayoutBinding.ivCover.setLayoutParams(presenter.getRecoderInfo(false));
            recoderLayoutBinding.ivCover.setImageBitmap(coverbitmap);
            recoderLayoutBinding.ivCover.bringToFront();
        }
    }

    @Override
    protected void initView() {
        recoderLayoutBinding = DataBindingUtil.setContentView(this, R.layout.recoder_layout);
    }

    @Override
    protected void initData() {
        recoderLayoutBinding.videoTexture.setLayoutParams(presenter.getRecoderInfo(false));
        presenter.initData();
        recoderLayoutBinding.ivCover.setVisibility(View.VISIBLE);
        recoderLayoutBinding.ivCover.setLayoutParams(presenter.getRecoderInfo(false));
        //进行修改，重新保存封面和视频
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(presenter.getImageData().getLocalUrl());
        coverbitmap = media.getFrameAtTime();
        recoderLayoutBinding.ivCover.setImageBitmap(coverbitmap);
        CameraStatus cameraStatus = CameraStatus.getCameraStatus();
        recoderLayoutBinding.ivWm.setImageDrawable(cameraStatus.getWaterMark().isWaterMarkOn() ? getResources().getDrawable(R.drawable.new_wm_blue) : getResources().getDrawable(R.drawable.new_wm_gray));
    }

    @Override
    protected void onViewClick() {
        recoderLayoutBinding.delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canClick) {
                    presenter.deletRecoder();
                }
            }
        });

        recoderLayoutBinding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canClick) {
                    presenter.saveRecoder();
                    canClick = false;
                }
            }
        });

        //底部状态栏操作：播放／暂停
        recoderLayoutBinding.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoderLayoutBinding.ivCover.setVisibility(View.GONE);
                if (canClick)
                    presenter.startMediaPaly();
            }
        });
        //底部状态栏操作：旋转
        recoderLayoutBinding.ivRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateSurface();
            }
        });
        //底部状态栏操作：添加水印
        recoderLayoutBinding.ivWm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WaterMarkPresenter.startAction(RecoderActivity.this,CameraSettingActivity.REQUESE,true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        if (requestCode == resultCode && resultCode == CameraSettingActivity.REQUESE) {
            CameraStatus cameraStatus = CameraStatus.getCameraStatus();
            if (cameraStatus.getWaterMark().isWaterMarkOn()) {
                recoderLayoutBinding.ivWm.setImageDrawable(getResources().getDrawable(R.drawable.new_wm_blue));
            } else {
                recoderLayoutBinding.ivWm.setImageDrawable(getResources().getDrawable(R.drawable.new_wm_gray));
            }
        }
        recoderLayoutBinding.ivCover.setVisibility(View.VISIBLE);
        recoderLayoutBinding.ivCover.bringToFront();
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void rotateSurface() {
        rotateCount++;
        if (rotateCount == 4)
            rotateCount = 0;
        //recoderLayoutBinding.textureview.setLayoutParams(presenter.getRecoderInfo(rotateCount % 2 != 0));
        if (recoderLayoutBinding.ivCover.getVisibility() == View.VISIBLE)
            recoderLayoutBinding.ivCover.setRotation(rotateCount * 90);
        recoderLayoutBinding.videoTexture.setRotation(rotateCount * 90);
    }


    @Override
    public RecoderPresenter initPresenter() {
        return new RecoderPresenter();
    }

    @Override
    public TextureView getTextureView() {
        return recoderLayoutBinding.videoTexture;
    }


    @Override
    public void setSeeBar(int number) {
        //recoderLayoutBinding.bottomView.setSeekbarProgress(number);
    }

    @Override
    public void setSeeBarMax(int number) {
        //recoderLayoutBinding.bottomView.setSeekbarMax(number);
    }

    @Override
    public void showLoading() {
        showTransformDialog();
    }

    @Override
    public void hideLoading() {
        dissmissDialog();
    }

    @Override
    public void showToast(String toast) {
        ToastUtil.toast(toast);
    }

    @Override
    public void finishView(int resultCode, Intent intent) {
        finish();
    }

    @Override
    /**
     * true:开始播放，false：暂停播放
     */
    public void setVideoStatus(boolean videoStatus) {
        recoderLayoutBinding.ivPlay.setImageDrawable(videoStatus ? getResources().getDrawable(R.drawable.video_pause_seleter) : getResources().getDrawable(R.drawable.video_play_seleter));
        //recoderLayoutBinding.bottomView.setVideoStatus(videoStatus);
    }

    @Override
    public int getRotateCount() {
        return rotateCount;
    }


}
