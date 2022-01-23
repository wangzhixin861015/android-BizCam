package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.data.SqlControl.CameraSettingData;
import com.bcnetech.hyphoto.databinding.CameraSettingNewLayoutBinding;
import com.bcnetech.hyphoto.ui.popwindow.CameraSettingPop;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;
import com.bcnetech.hyphoto.R;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by a1234 on 2017/11/22.
 */

public class CameraSettingNewView extends BaseRelativeLayout {

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
    private ArrayList<Size> sizeList;
    //视频尺寸
    private ArrayList<Size> videoList;
    private ArrayList<Size> squareList;

    private boolean supportSquareVideo = false;


    public CameraSettingNewView(Context context) {
        super(context);
    }

    public CameraSettingNewView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraSettingNewView(Context context, AttributeSet attrs, int defStyleAttr) {
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

    public void supportCamera2(boolean isCamera2){
        this.isCamera2 = isCamera2;
        if (!isCamera2){
            cameraSettingNewLayoutBinding.videoSize.setVisibility(GONE);
        }
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

        cameraSettingNewLayoutBinding.picRatio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setList(Flag.PICRATIO);
                showCameraPop(list);
                cameraSettingPop.isShowSelect(false);
                currentSelect = Flag.PICRATIO;
            }
        });

        cameraSettingNewLayoutBinding.picSize.setOnClickListener(new View.OnClickListener() {
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
                if (cameraStatus.getPictureRatio() == CameraStatus.Size.TYPE_11) {
                    if (supportSquareVideo) {
                        videoList = squareList;
                    } /*else {
                        return;
                    }*/
                }
                list = new ArrayList<>();
                if (cameraStatus.getRecordSize() == null && videoList.size() != 0) {
                    cameraStatus.setRecordSize(videoList.get(0));
                }
                for (int i = 0; i < videoList.size(); i++) {
                    list.add(new CameraSettingData(cameraStatus.getRecordSize() == null ? false : cameraStatus.getRecordSize() == videoList.get(i), CameraStatus.Size.LARGE, Flag.VIDEOSIZE, videoList.get(i)));
                }
                showCameraPop(list);
                cameraSettingPop.isShowSelect(false);
                currentSelect = Flag.VIDEOSIZE;
            }
        });

        cameraSettingNewLayoutBinding.videoDuration.setOnClickListener(new View.OnClickListener() {
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
                    case Flag.PICRATIO:
                        cameraStatus.setPictureRatio(list.get(type).getSize());
                        //改变宽高比重新设置视屏尺寸
                        cameraSettingInter.onPreRatioNotify(list.get(type).getSize().getIndex());
                        break;
                    case Flag.VIDEOSIZE:
                        cameraStatus.setRecordSize(videoList.get(type));
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

    private void setList(int type) {
        switch (type) {
            case Flag.PICSIZE:
                list = new ArrayList<>();
                list.add(new CameraSettingData(cameraStatus.getPictureSize() == CameraStatus.Size.LARGE, CameraStatus.Size.LARGE, Flag.PICSIZE, null));
                list.add(new CameraSettingData(cameraStatus.getPictureSize() == CameraStatus.Size.MIDDLE, CameraStatus.Size.MIDDLE, Flag.PICSIZE, null));
                list.add(new CameraSettingData(cameraStatus.getPictureSize() == CameraStatus.Size.SMALL, CameraStatus.Size.SMALL, Flag.PICSIZE, null));
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
        }
    }

    private void showCameraPop(ArrayList arrayList) {
        cameraSettingPop.setList(arrayList);
        cameraSettingPop.showPop(this);
        //cameraSettingPop.setPreList(sizeList);
    }

    public void setPreSize(ArrayList<Size> preSize) {
        this.sizeList = preSize;
        if (supportSquareVideo && cameraStatus.getPictureRatio() == CameraStatus.Size.TYPE_11) {
            videoList = squareList;
        } else {
            videoList = new ArrayList<>(sizeList);
            selectVideoSize(videoList);
        }
        if (videoList.get(0) != null/*&&cameraStatus.getRecordSize()==null*/)
            cameraStatus.setRecordSize(videoList.get(0));
        setCurrentType(cameraStatus);
    }
    /**
     * Camera2 拍摄尺寸以拍照尺寸为主，通过拍照尺寸判断是否支持1：1录像
     * @param allSize
     */
    public void setAllSize(ArrayList<Size> allSize) {
        Iterator<Size> iterator = allSize.iterator();
        while (iterator.hasNext()) {
            Size size = iterator.next();
            if (size.getWidth() != size.getHeight()) {
                iterator.remove();
            }
        }
        if (LoginedUser.getLoginedUser().isSupportCamera2()) {
            selectVideoSize(allSize);
            //Camera2中，预览分辨率尺寸如果大于手机分辨率，即使有合适尺寸依然判断不支持1：1录像
            ArrayList msizelist = new ArrayList();
            msizelist.addAll(allSize);
            Iterator<Size>iterator1 = msizelist.iterator();
            while (iterator1.hasNext()){
                Size sizes = iterator1.next();
                int ScreenMin = Math.min(ContentUtil.getScreenWidth(getContext()),ContentUtil.getScreenHeight(getContext()));
                if (sizes.getWidth()>ScreenMin||sizes.getHeight()>ScreenMin){
                    iterator1.remove();
                }
            }

            supportSquareVideo = !msizelist.isEmpty();
            if (supportSquareVideo) {
                squareList = allSize;
            }
        }
    }

    /**
     * Camera1 拍摄尺寸以预览尺寸为主，通过预览尺寸判断是否支持1：1录像
     * @param allSize
     */
    public void setAllPreviewSize(ArrayList<Size> allSize) {
       if(!LoginedUser.getLoginedUser().isSupportCamera2()) {
           Iterator<Size> iterator = allSize.iterator();
           while (iterator.hasNext()) {
               Size size = iterator.next();
               if (size.getWidth() != size.getHeight()) {
                   iterator.remove();
               }
           }

           selectVideoSize(allSize);
           supportSquareVideo = !allSize.isEmpty();
           if (supportSquareVideo) {
               squareList = allSize;
           }
       }
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
                CameraSettingNewView.this.setVisibility(GONE);
                cameraSettingInter.onClose(cameraStatus);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                CameraSettingNewView.this.setVisibility(GONE);
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
                CameraSettingNewView.this.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                CameraSettingNewView.this.setVisibility(GONE);
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

        if (cameraStatus.getCameraStatus().getPictureRatio().getName().equals(CameraStatus.Size.TYPE_11.getName())) {
            if (cameraStatus.getPictureSize().getName().equals(CameraStatus.Size.LARGE.getName())) {
                cameraSettingNewLayoutBinding.picSize.setSetting_parm(cameraStatus.getPictureSize().getName() + "(" + sizeList.get(0).getHeight() + "×" + sizeList.get(0).getHeight() + ")");
            } else if (cameraStatus.getPictureSize().getName().equals(CameraStatus.Size.MIDDLE.getName())) {
                cameraSettingNewLayoutBinding.picSize.setSetting_parm(cameraStatus.getPictureSize().getName() + "(" + sizeList.get(sizeList.size() / 2).getHeight() + "×" + sizeList.get(sizeList.size() / 2).getHeight() + ")");
            } else if (cameraStatus.getPictureSize().getName().equals(CameraStatus.Size.SMALL.getName())) {
                cameraSettingNewLayoutBinding.picSize.setSetting_parm(cameraStatus.getPictureSize().getName() + "(" + sizeList.get(sizeList.size() - 1).getHeight() + "×" + sizeList.get(sizeList.size() - 1).getHeight() + ")");
            }
            //判断是否有支持1：1比例的尺寸
            if (supportSquareVideo) {
                cameraSettingNewLayoutBinding.videoSize.setSetting_parm(cameraStatus.getRecordSize().getWidth() + "x" + cameraStatus.getRecordSize().getHeight());
            } else {
                cameraSettingNewLayoutBinding.videoSize.setSetting_parm(cameraStatus.getRecordSize().getWidth() + "x" + cameraStatus.getRecordSize().getHeight());
               // cameraSettingNewLayoutBinding.videoSize.setSetting_parm("不支持该比例下的视频录制");
            }
        } else {

            if (cameraStatus.getPictureSize().getName().equals(CameraStatus.Size.LARGE.getName())) {
                cameraSettingNewLayoutBinding.picSize.setSetting_parm(cameraStatus.getPictureSize().getName() + "(" + sizeList.get(0).getWidth() + "×" + sizeList.get(0).getHeight() + ")");
            } else if (cameraStatus.getPictureSize().getName().equals(CameraStatus.Size.MIDDLE.getName())) {
                cameraSettingNewLayoutBinding.picSize.setSetting_parm(cameraStatus.getPictureSize().getName() + "(" + sizeList.get(sizeList.size() / 2).getWidth() + "×" + sizeList.get(sizeList.size() / 2).getHeight() + ")");
            } else if (cameraStatus.getPictureSize().getName().equals(CameraStatus.Size.SMALL.getName())) {
                cameraSettingNewLayoutBinding.picSize.setSetting_parm(cameraStatus.getPictureSize().getName() + "(" + sizeList.get(sizeList.size() - 1).getWidth() + "×" + sizeList.get(sizeList.size() - 1).getHeight() + ")");
            }
            cameraSettingNewLayoutBinding.videoSize.setSetting_parm(cameraStatus.getRecordSize().getWidth() + "x" + cameraStatus.getRecordSize().getHeight());
        }


        cameraSettingNewLayoutBinding.picRatio.setSetting_parm(cameraStatus.getPictureRatio().getName());

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

    private void selectVideoSize(ArrayList<Size> mlist) {
        Iterator<Size> it = mlist.iterator();
        while (it.hasNext()) {
            Size size = it.next();
            int size_long = Math.max(size.getWidth(), size.getHeight());
            if (size_long > MAXSIZE)
                it.remove();
            if (size_long < MINSIZE)
                it.remove();
        }

        Log.d("accord Size:", mlist.toString());
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


}
