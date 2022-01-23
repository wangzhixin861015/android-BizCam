package com.bcnetech.hyphoto.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.model.BleConnectModel;
import com.bcnetech.hyphoto.ui.view.clickanimview.BamImageView;
import com.bcnetech.hyphoto.utils.PermissionUtil;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCameraLoader;
import com.bcnetech.bluetoothlibarary.bluetoothagreement.CommendManage;

/**
 * 相机底部按键
 * Created by a1234 on 2017/11/21.
 */

public class CameraBottomView extends BaseRelativeLayout {

    private int current_type = GPUImageCameraLoader.CAMER_TYPE;
    private CameraBottomViewInter cameraBottomViewInter;

    private BamImageView camera_main_btn;
    private ImageView camera_second_btn;
    private VideoButtonView video_button_btn;
    private ImageView record_pause_btn;
    private TranslateAnimation translateAnimationMain, translateAnimationSec;
    private AlphaAnimation alphaAnimation;
    private AnimationSet animationSetMain, animationSetSec;
    private int width;
    private Drawable CameraDrawable, VideoDrawable;
    private boolean isAniming = false;
    private boolean isRecording = false;
    private boolean isPause = false;
    private int recordTime = 9;
    private Activity activity;

    private BamImageView camera_auto;
    private BamImageView camera_ls;
    private BamImageView camera_pro;
    private BamImageView camera_m;
    //蓝牙关闭
    public static final int BLUE_TOUCH_CLOSE = 1;
    //蓝牙开启
    public static final int BLUE_TOUCH_OPEN = 2;
    //蓝牙连接
    public static final int BLUE_TOUCH_CONNECTION = 3;
    //蓝牙断开
    public static final int BLUE_TOUCH_CONNECTION_ERROR = 4;

    private int connectionType = BLUE_TOUCH_CLOSE;

    private int clickType = 0;

    private int CLICK_AUTO = 0;

    private int CLICK_PRESET = 1;


    private boolean isCamera2Support = false;

    public CameraBottomView(Context context) {
        super(context);
    }

    public CameraBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        inflate(getContext(), R.layout.camera_bottom_layout, this);
        camera_auto = (BamImageView) findViewById(R.id.camera_auto);
        camera_main_btn = (BamImageView) findViewById(R.id.camera_main_btn);
        camera_second_btn = (ImageView) findViewById(R.id.camera_second_btn);
        video_button_btn = (VideoButtonView) findViewById(R.id.video_button_btn);
        record_pause_btn = (ImageView) findViewById(R.id.record_pause_btn);
        camera_ls = (BamImageView) findViewById(R.id.camera_ls);
        camera_pro = (BamImageView) findViewById(R.id.camera_pro);
        camera_m = (BamImageView) findViewById(R.id.camera_m);

    }

    @Override
    protected void initData() {
        CameraDrawable = camera_main_btn.getDrawable();
        VideoDrawable = camera_second_btn.getDrawable();
        initAnim();
    }

    public ImageView getCamera_second_btn(){
        return camera_second_btn;
    }

    public void changeType() {
        if (current_type == GPUImageCameraLoader.CAMER_TYPE) {
            current_type = GPUImageCameraLoader.VIDE0_TYPE;
        } else {
            current_type = GPUImageCameraLoader.CAMER_TYPE;
        }
        cameraBottomViewInter.onChangeClick(current_type);
    }

    @Override
    protected void onViewClick() {
        camera_main_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAniming) {
                    if (current_type == GPUImageCameraLoader.CAMER_TYPE) {
                        cameraBottomViewInter.onCameraClick();
                    } else {
                        if (!isRecording) {
                            video_button_btn.setVisibility(VISIBLE);
                            video_button_btn.startArcAnim(recordTime);

                        } else {
                           /* if (record_pause_btn.getVisibility()==GONE) {
                                video_button_btn.Pause();
                                cameraBottomViewInter.onVideoPause(true);
                                record_pause_btn.setVisibility(VISIBLE);
                            }else{
                                video_button_btn.Resume();
                                record_pause_btn.setVisibility(GONE);
                                cameraBottomViewInter.onVideoPause(false);
                            }*/
                        }
                    }
                }
            }
        });
        camera_second_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionUtil.getRecordPermission((Activity) getContext())) {
                    return;
                }

                if (!isAniming && !isRecording) {
                    startTransform();
                }
            }
        });
        camera_auto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAniming && !isRecording && isCamera2Support) {
                    camera_auto.setVisibility(GONE);
                    camera_ls.setVisibility(GONE);
                    camera_m.setVisibility(VISIBLE);
                    camera_pro.setVisibility(GONE);
                    cameraBottomViewInter.onStatusClick();
                } else {
                    if (BleConnectModel.getBleConnectModelInstance().isCurrentConnect() && CommendManage.getInstance().getVersion() == CommendManage.VERSION_BOX) {
                        camera_auto.setVisibility(GONE);
                        camera_pro.setVisibility(VISIBLE);
                        cameraBottomViewInter.onPro();
                    }
                }
            }
        });

        camera_ls.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != cameraBottomViewInter) {
                    if (!isAniming && !isRecording && isCamera2Support) {
                        camera_ls.setVisibility(GONE);
                        camera_pro.setVisibility(VISIBLE);
                        cameraBottomViewInter.onPro();
                    } else if (!isAniming && !isRecording && !isCamera2Support) {
                        camera_ls.setVisibility(GONE);
                        camera_pro.setVisibility(VISIBLE);
                        cameraBottomViewInter.onPro();
                    }
                }
            }
        });

        camera_pro.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != cameraBottomViewInter) {
                    if (!isAniming && !isRecording) {
                        if (connectionType == BLUE_TOUCH_CONNECTION) {
                            if (CommendManage.getInstance().getVersion() != CommendManage.VERSION_BOX) {
                                camera_ls.setVisibility(VISIBLE);
                                camera_pro.setVisibility(GONE);
                                cameraBottomViewInter.onPreset();

                            } else {
                                camera_auto.setVisibility(VISIBLE);
                                camera_pro.setVisibility(GONE);
                                cameraBottomViewInter.onStatusClick();

                            }
                        }
                    }
                }
            }
        });

        camera_m.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionType == BLUE_TOUCH_CONNECTION_ERROR || connectionType == BLUE_TOUCH_CLOSE) {
                    if (!isAniming && !isRecording) {
                        camera_auto.setVisibility(VISIBLE);
                        camera_ls.setVisibility(GONE);
                        camera_pro.setVisibility(GONE);
                        camera_m.setVisibility(GONE);
                        cameraBottomViewInter.onAutoCamera();
                    }
                }
            }
        });

        video_button_btn.setVideoButtonInter(new VideoButtonView.VideoButtonInter() {
            @Override
            public void startVideo() {
                isAniming = true;
                cameraBottomViewInter.onVideoClick();
            }

            @Override
            public void CountdownFin() {
                isAniming = false;
                isRecording = true;
                cameraBottomViewInter.onCountDownFin();
            }

            @Override
            public void videoFin() {
                // isRecording = false;
                cameraBottomViewInter.onVideoFin();
            }
        });

    }

    public void onResume() {
        isRecording = false;
    }

    public interface CameraBottomViewInter {
        void onCameraClick();

        void onVideoClick();

        void onCountDownFin();

        void onVideoFin();

        void onChangeClick(int type);

        void onStatusClick();

        void onVideoPause(boolean ispause);

        void onPreset();

        void onAutoCamera();

        void onPro();
    }

    public void setCameraBottomViewInter(CameraBottomViewInter cameraBottomViewInter) {
        this.cameraBottomViewInter = cameraBottomViewInter;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = this.getMeasuredWidth();
    }

    public void initAnim() {
        translateAnimationMain = new TranslateAnimation(0, -(this.width / 2 - ContentUtil.dip2px(getContext(), 110)), 0, 0);
        translateAnimationSec = new TranslateAnimation(0, (this.width / 2 - ContentUtil.dip2px(getContext(), 110)), 0, 0);
        alphaAnimation = new AlphaAnimation(1, 0);

        animationSetMain = new AnimationSet(true);
        animationSetMain.addAnimation(alphaAnimation);
        animationSetMain.addAnimation(translateAnimationMain);
        animationSetMain.setDuration(300);

        animationSetSec = new AnimationSet(true);
        animationSetSec.addAnimation(alphaAnimation);
        animationSetSec.addAnimation(translateAnimationSec);
        animationSetSec.setDuration(300);
        animationSetMain.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAniming = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (current_type == GPUImageCameraLoader.CAMER_TYPE) {
                    camera_main_btn.setImageDrawable(CameraDrawable);
                } else {
                    camera_main_btn.setImageDrawable(VideoDrawable);
                }
                isAniming = false;
                camera_main_btn.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationSetSec.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAniming = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (current_type == GPUImageCameraLoader.CAMER_TYPE) {
                    camera_second_btn.setImageDrawable(VideoDrawable);
                } else {
                    camera_second_btn.setImageDrawable(CameraDrawable);
                }
                isAniming = false;
                camera_second_btn.clearAnimation();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setRecordTime(int time) {
        this.recordTime = time;
    }

    public void resetVideo() {
        video_button_btn.setVisibility(GONE);
        video_button_btn.resetArcAnim();
    }

    public void startTransform() {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                camera_main_btn.clearAnimation();
                camera_second_btn.clearAnimation();
                camera_main_btn.startAnimation(animationSetMain);
                camera_second_btn.startAnimation(animationSetSec);
            }
        });
        changeType();
    }

    public void setBlueTouchType(int type) {
        this.connectionType = type;
        if (type == BLUE_TOUCH_CONNECTION) {
            if (CommendManage.getInstance().getVersion() != CommendManage.VERSION_BOX) {
                camera_ls.setVisibility(VISIBLE);
                camera_auto.setVisibility(GONE);
            } else {
                camera_ls.setVisibility(GONE);
                camera_auto.setVisibility(VISIBLE);
            }
            camera_pro.setVisibility(GONE);
            camera_m.setVisibility(GONE);
        } else if (type == BLUE_TOUCH_CONNECTION_ERROR || type == BLUE_TOUCH_CLOSE) {
            camera_ls.setVisibility(GONE);
            camera_auto.setVisibility(VISIBLE);
            camera_pro.setVisibility(GONE);
            camera_m.setVisibility(GONE);
        }
    }

    private void setShowType() {

    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public boolean isCamera2Support() {
        return isCamera2Support;
    }

    public void setCamera2Support(boolean camera2Support) {
        isCamera2Support = camera2Support;
    }
}

