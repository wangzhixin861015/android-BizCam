package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.data.SqlControl.CameraSettingData;
import com.bcnetech.hyphoto.databinding.CameraSettingNewLayoutBinding;
import com.bcnetech.hyphoto.ui.popwindow.CameraSettingPop;
import com.bcnetech.bizcamerlibrary.camera.dao.CameraSizeData;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;
import com.bcnetech.bizcamerlibrary.camera.utils.CameraSizeModel;
import com.bcnetech.hyphoto.R;

import java.util.ArrayList;

/**
 * Created by a1234 on 2018/07/24.
 * 用于camera1的相机设置界面
 */

public class CameraSettingView extends BaseRelativeLayout {

    private static final int MAXSIZE = 3000;
    private static final int MIDDLESIZE = 1280;
    private static final int MINSIZE = 600;
    public final static int SHOW = 11;
    public final static int CLOSE = 21;
    private int type = CLOSE;
    private int hight;
    private boolean isCamera2 = false;

    CameraSettingNewLayoutBinding cameraSettingNewLayoutBinding;

    //录制时间
    private ValueAnimator outAnim, inAnim;
    private CameraSettingInter cameraSettingInter;

    private CameraSettingPop cameraSettingPop;
    private CameraStatus cameraStatus;
    private int currentSelect = Flag.PICSIZE;

    private ArrayList<CameraSettingData> list;
    private CameraSizeModel cameraSizeModel;
    private CameraSizeData cameraSizeData34, cameraSizeData916, cameraSizeData11;


    private boolean supportSquareVideo = false;


    public CameraSettingView(Context context) {
        super(context);
    }

    public CameraSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraSettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        cameraSettingNewLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.camera_setting_new_layout, this, true);
    }

    @Override
    protected void initData() {
        initAnim();
        cameraStatus = CameraStatus.getCameraStatus();
        cameraSettingNewLayoutBinding.picSize.setSetting_name(getResources().getString(R.string.pic_size));
        cameraSettingNewLayoutBinding.picSize.isShowButton(false);
        cameraSettingNewLayoutBinding.picRatio.setSetting_name(getResources().getString(R.string.pic_ratio));
        cameraSettingNewLayoutBinding.picRatio.isShowButton(false);

        cameraSettingNewLayoutBinding.videoRatio.setSetting_name(getResources().getString(R.string.video_proportion));
        cameraSettingNewLayoutBinding.videoRatio.isShowButton(false);

        cameraSettingNewLayoutBinding.videoSize.setSetting_name(getResources().getString(R.string.video_size));
        cameraSettingNewLayoutBinding.videoSize.isShowButton(false);
        cameraSettingNewLayoutBinding.videoDuration.setSetting_name(getResources().getString(R.string.video_duration));
        cameraSettingNewLayoutBinding.videoDuration.isShowButton(false);
        cameraSettingNewLayoutBinding.subline.setSetting_name(getResources().getString(R.string.subline));
        cameraSettingNewLayoutBinding.subline.isShowButton(true);
        cameraSettingNewLayoutBinding.subline.setSettingInstructions(getResources().getString(R.string.level_line));
        cameraSettingNewLayoutBinding.watermark.setSetting_name(getResources().getString(R.string.watermark));
        cameraSettingNewLayoutBinding.watermark.isShowButton(false);
        cameraSettingNewLayoutBinding.watermark.setSettingInstructions(getResources().getString(R.string.watermarked_when_opened));
        cameraSettingNewLayoutBinding.watermark.setSetting_parm(getResources().getString(R.string.water_hint));
        cameraSettingNewLayoutBinding.blackWhite.setSetting_name(getResources().getString(R.string.blackwhite));
        cameraSettingNewLayoutBinding.blackWhite.isShowButton(true);
        cameraSettingNewLayoutBinding.compress.setSetting_name(getResources().getString(R.string.compress));
        cameraSettingNewLayoutBinding.compress.isShowButton(true);

        cameraSettingNewLayoutBinding.flash.setSetting_name(getResources().getString(R.string.light));
        cameraSettingNewLayoutBinding.flash.isShowButton(true);
        cameraSettingNewLayoutBinding.flash.setSettingInstructions(getResources().getString(R.string.flash_when_turned_on));


        cameraSettingPop = new CameraSettingPop((Activity) getContext(), null);
    }

    public void supportCamera2(boolean isCamera2) {
        this.isCamera2 = isCamera2;
       /* if (!isCamera2) {
            cameraSettingNewLayoutBinding.videoSize.setVisibility(GONE);
        }*/
    }

    @Override
    protected void onViewClick() {
        cameraSettingNewLayoutBinding.cameraCloseBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startViweType();
            }
        });

        cameraSettingNewLayoutBinding.subline.setCurrentCheckedListener(new CameraSettingItemView.CurrentChecked() {
            @Override
            public void onCurrentCheck(boolean currentcheck) {
                cameraStatus.setSubLineOn(currentcheck);
                CameraStatus.saveToFile(cameraStatus);
            }
        });

        cameraSettingNewLayoutBinding.flash.setCurrentCheckedListener(new CameraSettingItemView.CurrentChecked() {
            @Override
            public void onCurrentCheck(boolean isflashON) {
                if (cameraSettingInter != null) {
                    cameraSettingInter.onFlashClick(isflashON);
                }
                cameraStatus.setFlashOn(isflashON);
                CameraStatus.saveToFile(cameraStatus);
            }
        });

        cameraSettingNewLayoutBinding.watermark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSettingInter.onWaterMarkClick();
            }
        });

        cameraSettingNewLayoutBinding.picRatio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setList(Flag.PICRATIO);
                showCameraPop(list);
                cameraSettingPop.isShowSelect(false);
                currentSelect = Flag.PICRATIO;
            }
        });

        cameraSettingNewLayoutBinding.picSize.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setList(Flag.PICSIZE);
                showCameraPop(list);
                cameraSettingPop.isShowSelect(false);
                currentSelect = Flag.PICSIZE;
            }
        });

        cameraSettingNewLayoutBinding.videoSize.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setList(Flag.VIDEOSIZE);
                showCameraPop(list);
                cameraSettingPop.isShowSelect(false);
                currentSelect = Flag.VIDEOSIZE;
            }
        });

        cameraSettingNewLayoutBinding.videoRatio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setList(Flag.VIDEORATIO);
                showCameraPop(list);
                cameraSettingPop.isShowSelect(false);
                currentSelect = Flag.VIDEORATIO;
            }
        });

        cameraSettingNewLayoutBinding.videoDuration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setList(Flag.VIDEODURATION);
                showCameraPop(list);
                if (cameraStatus.getCostomRecordTime() > 0) {
                    cameraSettingPop.setCostom();
                }
                if (cameraStatus.getRecordTime() == CameraStatus.Size.RECORD_CUSTOM) {
                    cameraSettingPop.onCoustomSelect();
                }
                cameraSettingPop.isShowSelect(true);
                currentSelect = Flag.VIDEODURATION;
            }
        });

        cameraSettingPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                int type = cameraSettingPop.getSelectType();
                switch (currentSelect) {
                    case Flag.PICSIZE:
                        cameraStatus.setPictureSize(list.get(type).getSize());
                        break;
                    case Flag.VIDEORATIO:
                        if (!cameraStatus.getVideoRatio().equals(list.get(type).getSize())) {
                            cameraStatus.setVideoRatio(list.get(type).getSize());
                            cameraStatus.setVideoSize(CameraStatus.Size.MIDDLE);
                        }
                        break;
                    case Flag.PICRATIO:
                        if (!cameraStatus.getPictureRatio().equals(list.get(type).getSize())) {
                            cameraStatus.setPictureRatio(list.get(type).getSize());
                            cameraStatus.setPictureSize(CameraStatus.Size.MIDDLE);
                        }
                        //改变宽高比重新设置视屏尺寸
                        //cameraSettingInter.onPreRatioNotify(list.get(type).getSize().getIndex());
                        break;
                    case Flag.VIDEOSIZE:
                        cameraStatus.setVideoSize(list.get(type).getSize());
                        break;
                    case Flag.VIDEODURATION:
                        int customTime = cameraSettingPop.getRecordTime_Custom();
                        cameraStatus.setCostomRecordTime(customTime);
                        if (type != -1) {
                            cameraStatus.setRecordTime(list.get(type).getSize());
                        } else {
                            cameraStatus.setRecordTime(CameraStatus.Size.RECORD_CUSTOM);
                        }
                        break;
                }
                setCurrentType(cameraStatus);
            }
        });
    }

    private void accordRatioGetSize(CameraStatus.Size ratio, boolean isVideoSize) {
        CameraSizeData cameraSizeData;
        switch (ratio) {
            case TYPE_11:
                cameraSizeData = cameraSizeData11;
                break;
            case TYPE_34:
                cameraSizeData = cameraSizeData34;
                break;
            default:
                cameraSizeData = cameraSizeData916;
                break;
        }
        list = new ArrayList<>();
        list.add(new CameraSettingData(isVideoSize ? cameraStatus.getVideoSize() == CameraStatus.Size.LARGE : cameraStatus.getPictureSize() == CameraStatus.Size.LARGE, CameraStatus.Size.LARGE, Flag.PICSIZE, isVideoSize ? cameraSizeData.getVideoSizeBean().getSizeLarge() : cameraSizeData.getPictureSizeBean().getSizeLarge()));
        list.add(new CameraSettingData(isVideoSize ? cameraStatus.getVideoSize() == CameraStatus.Size.MIDDLE : cameraStatus.getPictureSize() == CameraStatus.Size.MIDDLE, CameraStatus.Size.MIDDLE, Flag.PICSIZE, isVideoSize ? cameraSizeData.getVideoSizeBean().getSizeMiddle() : cameraSizeData.getPictureSizeBean().getSizeMiddle()));
        list.add(new CameraSettingData(isVideoSize ? cameraStatus.getVideoSize() == CameraStatus.Size.SMALL : cameraStatus.getPictureSize() == CameraStatus.Size.SMALL, CameraStatus.Size.SMALL, Flag.PICSIZE, isVideoSize ? cameraSizeData.getVideoSizeBean().getSizeSmall() : cameraSizeData.getPictureSizeBean().getSizeSmall()));
    }

    private void setList(int type) {
        switch (type) {
            case Flag.PICSIZE:
                accordRatioGetSize(cameraStatus.getPictureRatio(), false);
                break;
            case Flag.VIDEOSIZE:
                accordRatioGetSize(cameraStatus.getVideoRatio(), true);
                break;
            case Flag.VIDEODURATION:
                list = new ArrayList<>();
                list.add(new CameraSettingData(cameraStatus.getRecordTime() == CameraStatus.Size.RECORD_9, CameraStatus.Size.RECORD_9, Flag.VIDEODURATION, null));
                list.add(new CameraSettingData(cameraStatus.getRecordTime() == CameraStatus.Size.RECORD_30, CameraStatus.Size.RECORD_30, Flag.VIDEODURATION, null));
                list.add(new CameraSettingData(cameraStatus.getRecordTime() == CameraStatus.Size.RECORD_60, CameraStatus.Size.RECORD_60, Flag.VIDEODURATION, null));
                break;
            case Flag.PICRATIO:
                list = new ArrayList<>();
                list.add(new CameraSettingData(cameraStatus.getPictureRatio() == CameraStatus.Size.TYPE_11, CameraStatus.Size.TYPE_11, Flag.PICRATIO, null));
                list.add(new CameraSettingData(cameraStatus.getPictureRatio() == CameraStatus.Size.TYPE_34, CameraStatus.Size.TYPE_34, Flag.PICRATIO, null));
                list.add(new CameraSettingData(cameraStatus.getPictureRatio() == CameraStatus.Size.TYPE_916, CameraStatus.Size.TYPE_916, Flag.PICRATIO, null));
                break;
            case Flag.VIDEORATIO:
                list = new ArrayList<>();
                list.add(new CameraSettingData(cameraStatus.getVideoRatio() == CameraStatus.Size.TYPE_11, CameraStatus.Size.TYPE_11, Flag.VIDEORATIO, null));
                list.add(new CameraSettingData(cameraStatus.getVideoRatio() == CameraStatus.Size.TYPE_34, CameraStatus.Size.TYPE_34, Flag.VIDEORATIO, null));
                list.add(new CameraSettingData(cameraStatus.getVideoRatio() == CameraStatus.Size.TYPE_916, CameraStatus.Size.TYPE_916, Flag.VIDEORATIO, null));
                break;
        }
    }

    private void showCameraPop(ArrayList arrayList) {
        cameraSettingPop.setList(arrayList);
        cameraSettingPop.showPop(this);
    }


    public void setCameraParamters(Camera.Parameters paramters) {
        Camera camera = Camera.open(0);
        selectSizeList(camera.getParameters());
        camera.release();
    }

    private void selectSizeList(Camera.Parameters paramters) {
        cameraSizeModel = CameraSizeModel.getCameraSizeModel(paramters);
        cameraSizeData34 = cameraSizeModel.getCameraSizeData34();
        cameraSizeData11 = cameraSizeModel.getCameraSizeData11();
        cameraSizeData916 = cameraSizeModel.getCameraSizeData916();
        setCurrentType(cameraStatus);
    }

    public CameraStatus getCameraStatus() {
        return this.cameraStatus;
    }

    public void startViweType() {
        if (type == CLOSE) {
            type = SHOW;
            inAnim.start();
        } else {
            type = CLOSE;
            outAnim.start();
        }
    }

    public void show() {
        if (type == CLOSE) {
            type = SHOW;
            inAnim.start();
        }
    }

    public void setWatermark() {
        cameraStatus = CameraStatus.getCameraStatus();
        if (cameraStatus.getWaterMark().isWaterMarkOn()) {
            cameraSettingNewLayoutBinding.watermark.setSetting_parm(cameraStatus.getWaterMark().getWaterMarkStatus().getName());
        } else {
            cameraSettingNewLayoutBinding.watermark.setSetting_parm(CameraStatus.Size.WATERMARK_OFF.getName());
        }
    }


    public int getType() {
        return this.type;
    }


    private void initAnim() {
        hight = ContentUtil.getScreenHeight2(getContext());
        outAnim = AnimFactory.tranYBottomOutAnim(this, hight);
        outAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                CameraSettingView.this.setVisibility(GONE);
                cameraSettingInter.onClose(cameraStatus);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                CameraSettingView.this.setVisibility(GONE);
                cameraSettingInter.onClose(cameraStatus);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        inAnim = AnimFactory.tranYBottomInAnim(this, hight);
        inAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                CameraSettingView.this.setVisibility(VISIBLE);
                CameraSettingView.this.bringToFront();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                CameraSettingView.this.setVisibility(GONE);
                cameraSettingInter.onClose(cameraStatus);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void setCurrentType(CameraStatus cameraStatus) {
        cameraSettingNewLayoutBinding.subline.setSwitchButton(cameraStatus.isSubLineOn());
        cameraSettingNewLayoutBinding.blackWhite.setSwitchButton(cameraStatus.isBlackAndWhite());
        cameraSettingNewLayoutBinding.compress.setSwitchButton(cameraStatus.isPicCompress());
        //比例
        int PicRatio = cameraStatus.getCameraStatus().getPictureRatio().getIndex();
        int VideoRatio = cameraStatus.getCameraStatus().getVideoRatio().getIndex();
        //大小
        CameraStatus.Size PicSize = cameraStatus.getCameraStatus().getPictureSize();
        CameraStatus.Size VideoSize = cameraStatus.getCameraStatus().getVideoSize();

        Size picSize = cameraSizeModel.getCameraSizeData(PicRatio).getCameraSizeBean(Flag.TYPE_PIC).getselectSize(false, PicSize);
        cameraSettingNewLayoutBinding.picSize.setSetting_parm(cameraStatus.getPictureSize().getName() + "(" + picSize + "px)");

        Size videoSize = cameraSizeModel.getCameraSizeData(VideoRatio).getCameraSizeBean(Flag.TYPE_VIDEO).getselectSize(false, VideoSize);
        cameraSettingNewLayoutBinding.videoSize.setSetting_parm(cameraStatus.getVideoSize().getName() + "(" + videoSize + "px)");


        cameraSettingNewLayoutBinding.picRatio.setSetting_parm(cameraStatus.getPictureRatio().getName());
        cameraSettingNewLayoutBinding.videoRatio.setSetting_parm(cameraStatus.getVideoRatio().getName());

        if (cameraStatus.getRecordTime() != CameraStatus.Size.RECORD_CUSTOM) {
            cameraSettingNewLayoutBinding.videoDuration.setSetting_parm(cameraStatus.getRecordTime().getName());
        } else {
            cameraSettingNewLayoutBinding.videoDuration.setSetting_parm(cameraStatus.getCostomRecordTime() + "s");
        }
        if (cameraStatus.getWaterMark().isWaterMarkOn()) {
            cameraSettingNewLayoutBinding.watermark.setSetting_parm(cameraStatus.getWaterMark().getWaterMarkStatus().getName());
        } else {
            cameraSettingNewLayoutBinding.watermark.setSetting_parm(CameraStatus.Size.WATERMARK_OFF.getName());
        }
        cameraSettingNewLayoutBinding.flash.setSwitchButton(cameraStatus.isFlashOn());

        CameraStatus.saveToFile(cameraStatus);
    }


    public interface CameraSettingInter {
        void onClose(CameraStatus cameraStatus);

        void onFlashClick(boolean isflashon);

        void onWaterMarkClick();

        void onPreRatioNotify(int preType);

    }

    public void setCameraSettingInter(CameraSettingInter cameraSettingInter) {
        this.cameraSettingInter = cameraSettingInter;
    }

    public boolean isSupportSquareVideo() {
        return supportSquareVideo;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
