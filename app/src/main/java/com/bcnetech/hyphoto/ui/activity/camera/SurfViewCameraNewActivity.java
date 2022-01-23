package com.bcnetech.hyphoto.ui.activity.camera;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.util.Size;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.listener.OnLayoutInflatedListener;
import com.app.hubert.guide.model.GuidePage;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.bean.CameraParm;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.EventStatisticsUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.EventCommon;
import com.bcnetech.hyphoto.data.CameraParamType;
import com.bcnetech.hyphoto.data.PresetItem;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.PresetParmsSqlControl;
import com.bcnetech.hyphoto.databinding.SurfviewCameraNewLayoutBinding;
import com.bcnetech.hyphoto.listener.BizGestureListener;
import com.bcnetech.hyphoto.model.BleConnectModel;
import com.bcnetech.hyphoto.presenter.SurfViewCameraPresenter;
import com.bcnetech.hyphoto.presenter.WaterMarkPresenter;
import com.bcnetech.hyphoto.presenter.iview.ISurfViewCameraView;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.ui.activity.BaseMvpActivity;
import com.bcnetech.hyphoto.ui.activity.market.MarketFragmentActivity;
import com.bcnetech.hyphoto.ui.adapter.PresetHorizontalListAdapter;
import com.bcnetech.hyphoto.ui.adapter.PresetScanListAdapter;
import com.bcnetech.hyphoto.ui.view.BlueToothListNewView;
import com.bcnetech.hyphoto.ui.view.CameraBottomView;
import com.bcnetech.hyphoto.ui.view.CameraParamAdjustView;
import com.bcnetech.hyphoto.ui.view.CameraSettingView;
import com.bcnetech.hyphoto.ui.view.CoBoxChooseTopView;
import com.bcnetech.hyphoto.ui.view.FocusView;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.PermissionUtil;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCamerLoaderBase;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCameraLoader;
import com.bcnetech.bizcamerlibrary.camera.utils.CameraHelper;
import com.bcnetech.bluetoothlibarary.bluetoothagreement.CommendManage;
import com.bcnetech.hyphoto.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

import static android.view.View.VISIBLE;

/**
 * Created by wenbin on 2017/2/24.
 */
public class SurfViewCameraNewActivity extends BaseMvpActivity<ISurfViewCameraView, SurfViewCameraPresenter> implements ISurfViewCameraView, PresetHorizontalListAdapter.PresetHolderInterFace, PresetScanListAdapter.PresetHolderInterFace {
    private static int TITLE_HEIGHT = 60;
    private static int BOTTOM_CONTROL_HEIGHT = 80;
    private static int CAMERAPARMS_HEIGHT = 60;
    SurfviewCameraNewLayoutBinding surfviewCameraNewLayoutBinding;
    private static final String QRCode = "qrcode";
    private int focusType = 0;
    private GPUImageCameraLoader mCamera;
    private boolean isShowRect = false;
    private boolean invideo = false;

    private PresetParmsSqlControl presetParmsSqlControl;

    private List<PresetItem> presetItems;
    private BizGestureListener bizGestureListener;
    private HashMap<String, String> hashMap;
    private boolean isc2Support = false;
    private boolean canClick = true;
    private boolean isQROn = false;
    private ConstraintLayout.LayoutParams fake_param, camer_adjust_param;
    private double bottom_height;
    private int count = 0;
    private Camera.Size previewSize;

    //能否连续滑动
    private boolean canMove = true;
    //选中的预设参数下标
    private int selectedPresetPosition = 0;
    //是否选中过预算参数
    private boolean isSelectedPreset = false;
    //最大转过的角度
    private int MAX_ANGLE = 90;
    private int MAX_LIGHT = 255;
    private boolean isAniming = false;
    private CameraEV cameraEv;
    private int currentType = GPUImageCameraLoader.CAMER_TYPE;
    public static final int REQUESE = 11;

    //曝光补偿滑动条值
    private int mScale;
    private int cameraType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.surfview_camera_new_layout);
        initView();
        presenter.initBleConnectModel();
        initData();
        onViewClick();
        if (null != savedInstanceState) {
            isQROn = savedInstanceState.getBoolean(QRCode);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(QRCode, isQROn);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && surfviewCameraNewLayoutBinding.coBoxChooseTopView.getTopViewType() == CoBoxChooseTopView.TYPE_PRO) {
            outPro();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && surfviewCameraNewLayoutBinding.coBoxChooseTopView.getTopViewType() == CoBoxChooseTopView.TYPE_CAMERA) {
            outVedio();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && surfviewCameraNewLayoutBinding.coBoxChooseTopView.getTopViewType() == CoBoxChooseTopView.TYPE_PRESET) {
            outPreset();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && surfviewCameraNewLayoutBinding.coBoxChooseTopView.getTopViewType() == CoBoxChooseTopView.TYPE_PRESET_CAMERA) {
            outPreset();
            return true;
        } else if (canClick == false) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && surfviewCameraNewLayoutBinding.blueToothListView.isShow()) {
            surfviewCameraNewLayoutBinding.blueToothListView.close();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        surfviewCameraNewLayoutBinding.cameraBottomView.onResume();
        canClick = true;

        if (mCamera.onResume()) {
            setUpCamera();
        } else {
            finish();
        }
        if (isQROn) {
            mCamera.setQRCode(true);
        }

        mCamera.resumeSenser();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (surfviewCameraNewLayoutBinding.blueToothListView.getVisibility() == VISIBLE) {
            surfviewCameraNewLayoutBinding.blueToothListView.close();
            //surfviewCameraNewLayoutBinding.blueToothListView.setVisibility(View.GONE);
            //初始曝光补偿
            mCamera.setEv(0);
            //重置滑动条
            surfviewCameraNewLayoutBinding.cameraParamAdjustView.reset();
        }
        mCamera.onPause();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        // BleManager.getInstance().destroy();
        //BlueToothLoad.getInstance().closeBlueTooth(this);
        mCamera.onDestroy();
        mCamera = null;

        surfviewCameraNewLayoutBinding.blueToothListView.destroy();

        super.onDestroy();
    }

    @Override
    public void showLoading() {
        showDialog();
    }

    @Override
    public void hideLoading() {
        dissmissDialog();
    }

    @Override
    public void finishView(int resultCode, Intent intent) {
        if (resultCode != Flag.NULLCODE) {
            setResult(resultCode, intent);
        }
        finish();
    }

    @Override
    public SurfViewCameraPresenter initPresenter() {
        return new SurfViewCameraPresenter();
    }

    @Override
    protected void initView() {
        surfviewCameraNewLayoutBinding = DataBindingUtil.setContentView(this, R.layout.surfview_camera_new_layout);
    }

    @Override
    protected void initData() {
        surfviewCameraNewLayoutBinding.cameraWelcomeView.bringToFront();
        surfviewCameraNewLayoutBinding.cameraWelcomeView.startAnim();
        if (hashMap == null) {
            hashMap = new HashMap<String, String>();
        }
        surfviewCameraNewLayoutBinding.cameraSettingView.setCameraParamters(null);
        surfviewCameraNewLayoutBinding.cameraParamAdjustView.setCamera2Support(LoginedUser.getLoginedUser().isSupportCamera2());
        // setLight(this, MAX_LIGHT);
        surfviewCameraNewLayoutBinding.cameraSettingView.supportCamera2(false);
        presenter.setLightRationListData(SurfViewCameraNewActivity.this, surfviewCameraNewLayoutBinding.cameraParamAdjustView.getRecycler_lightParam());
        mCamera = new GPUImageCameraLoader(this, surfviewCameraNewLayoutBinding.surfaceview);
        surfviewCameraNewLayoutBinding.cameraParamAdjustView.setPresetInterface(this);
        surfviewCameraNewLayoutBinding.cameraParamAdjustView.setScanInterface(this);
        presetParmsSqlControl = new PresetParmsSqlControl(this);
        mCamera.setGetParamsListener(new GPUImageCamerLoaderBase.GetParamsListener() {
            @Override
            public void onGetParams(HashMap map) {

            }
        });
        mCamera.setCameraDataListenerListener(new GPUImageCamerLoaderBase.CameraDataListener() {
            @Override
            public void getCameraData(long sec, int iso, int wb, float focus) {
                presenter.getCameraData(sec, iso, wb, focus);
            }

            @Override
            public void getPreSize(ArrayList<Size> sizes) {
                //surfviewCameraNewLayoutBinding.cameraSettingView.setCameraParamters(mCamera.getCurrentparameter());
            }
        });
        presenter.initRenderer();

        surfviewCameraNewLayoutBinding.cameraBottomView.setActivity(this);
        surfviewCameraNewLayoutBinding.cameraBottomView.setCamera2Support(LoginedUser.getLoginedUser().isSupportCamera2());


        surfviewCameraNewLayoutBinding.blueToothListView.initParm(presenter.getAdapter(), new BlueToothListNewView.BlueToothListInterface() {
            @Override
            public void onBlueToothDissmiss(Object... params) {
                surfviewCameraNewLayoutBinding.surfaceview.setVisibility(View.VISIBLE);
                mCamera.onResume();
                mCamera.resumeSenser();
                if (presenter.getBleConnectModel().isSearching()) {
                    presenter.getBleConnectModel().stopSearch();
                }
                if (presenter.getBleConnectModel().isCurrentConnect())
                    initGuide();
            }

            @Override
            public void onBlueToothClick(Object... params) {
//                        showDialog();
                presenter.choiceDeivce((int) params[0]);
                surfviewCameraNewLayoutBinding.blueToothListView.setVisibility(View.GONE);
                showLoading();
                surfviewCameraNewLayoutBinding.surfaceview.setVisibility(View.VISIBLE);
                mCamera.onResume();
                mCamera.resumeSenser();
//                        surfaceView.setVisibility(View.VISIBLE);
//                        mCamera.onResume();
//                        blueToothListView.setVisibility(View.GONE);
            }

            @Override
            public void onListConnection(Object... params) {
                int i = 0;
            }

            @Override
            public void onScanConnection(Object... params) {
                String result = (String) params[0];
                ToastUtil.toast(result);
                if (result.contains(CommendManage.COBOX_NAME) || result.contains(CommendManage.CBEDU_NAME) || result.contains(CommendManage.COLINK_NAME)) {
                    presenter.scanDevice(result);
                }
                surfviewCameraNewLayoutBinding.surfaceview.setVisibility(View.VISIBLE);
                mCamera.onResume();
                mCamera.resumeSenser();
            }
        });
    }


    @Override
    public void startQueryByShowType() {
        String mtype = "android";
        presetParmsSqlControl.startQueryByShowType(mtype);
    }

    @Override
    public void setCameraType(int cameraType) {

    }

    @Override
    public int getCameraType() {
        return 0;
    }

    private void setUpCamera() {
        hashMap.put(Flag.FLASH, CameraStatus.getCameraStatus().isFlashOn() + "");
        if (currentType == GPUImageCameraLoader.CAMER_TYPE) {
            setCameraPerViewSize(CameraStatus.getCameraStatus().getPictureRatio());
        } else {
            setCameraPerViewSize(CameraStatus.getCameraStatus().getVideoRatio());
        }
        if (CameraStatus.getCameraStatus().getRecordTime() != CameraStatus.Size.RECORD_CUSTOM) {
            surfviewCameraNewLayoutBinding.cameraBottomView.setRecordTime(CameraStatus.getCameraStatus().getRecordTime().getIndex());
        } else {
            surfviewCameraNewLayoutBinding.cameraBottomView.setRecordTime(CameraStatus.getCameraStatus().getCostomRecordTime());
        }
    }

    /**
     * 16：9-->使用1920：1080预览尺寸
     * 4：3 -->使用1600：1200预览尺寸
     * 1：1 -->在16：9尺寸上遮盖，达到1：1
     *
     * @param size
     */
    private void setCameraPerViewSize(CameraStatus.Size size) {
        ConstraintLayout.LayoutParams layoutParams;
        surfviewCameraNewLayoutBinding.surfaceview.setVisibility(View.GONE);
        double h;
        double w;
        double x;
        double margin;
        surfviewCameraNewLayoutBinding.surfaceview.setVisibility(View.VISIBLE);
        switch (size) {
            case TYPE_11:
                //上下半遮
                hashMap.put(Flag.PREVIEW, CameraHelper.PREVIEW_43 + "");
                h = ContentUtil.getScreenHeight2(SurfViewCameraNewActivity.this) - ContentUtil.dip2px(SurfViewCameraNewActivity.this, TITLE_HEIGHT) - ContentUtil.dip2px(SurfViewCameraNewActivity.this, CAMERAPARMS_HEIGHT) - ContentUtil.dip2px(SurfViewCameraNewActivity.this, BOTTOM_CONTROL_HEIGHT) - ContentUtil.dip2px(SurfViewCameraNewActivity.this, 10);
                w = h * 3 / 4;
                layoutParams = (ConstraintLayout.LayoutParams) surfviewCameraNewLayoutBinding.cameraLayout.getLayoutParams();
                layoutParams.width = (int) w;
                layoutParams.dimensionRatio = "w,4:3";
                bottom_height = (w * 4 / 3 - w) / 2;
                showFakeView(true, (int) bottom_height);
                isShowRect = true;
                break;
            case TYPE_34:
                hashMap.put(Flag.PREVIEW, CameraHelper.PREVIEW_43 + "");
                h = ContentUtil.getScreenHeight2(SurfViewCameraNewActivity.this) - ContentUtil.dip2px(SurfViewCameraNewActivity.this, TITLE_HEIGHT) - ContentUtil.dip2px(SurfViewCameraNewActivity.this, CAMERAPARMS_HEIGHT) - ContentUtil.dip2px(SurfViewCameraNewActivity.this, BOTTOM_CONTROL_HEIGHT) - ContentUtil.dip2px(SurfViewCameraNewActivity.this, 10);
                w = h * 3 / 4;
                layoutParams = (ConstraintLayout.LayoutParams) surfviewCameraNewLayoutBinding.cameraLayout.getLayoutParams();
                layoutParams.width = (int) w;
                layoutParams.dimensionRatio = "w,4:3";
                showFakeView(false, 1);
                surfviewCameraNewLayoutBinding.cameraLayout.setLayoutParams(layoutParams);
                isShowRect = false;
                break;
            case TYPE_916:
                hashMap.put(Flag.PREVIEW, CameraHelper.PREVIEW_169 + "");
                h = ContentUtil.getScreenHeight2(SurfViewCameraNewActivity.this) - ContentUtil.dip2px(SurfViewCameraNewActivity.this, TITLE_HEIGHT) - ContentUtil.dip2px(SurfViewCameraNewActivity.this, CAMERAPARMS_HEIGHT) - ContentUtil.dip2px(SurfViewCameraNewActivity.this, BOTTOM_CONTROL_HEIGHT) - ContentUtil.dip2px(SurfViewCameraNewActivity.this, 10);
                w = h * 9 / 16;
                layoutParams = (ConstraintLayout.LayoutParams) surfviewCameraNewLayoutBinding.cameraLayout.getLayoutParams();
                layoutParams.width = (int) w;
                layoutParams.dimensionRatio = "w,16:9";
                showFakeView(false, 1);
                surfviewCameraNewLayoutBinding.cameraLayout.setLayoutParams(layoutParams);
                isShowRect = false;
                break;
        }
        hashMap.put(Flag.CAMERATYPE, currentType + "");
        hashMap.put(Flag.PICSIZE + "", CameraStatus.getCameraStatus().getPictureSize().getIndex() + "");
        mCamera.setCameraParams(hashMap);
        Camera.Parameters parameters = mCamera.getCurrentparameter();
        if (cameraEv == null) {
            cameraEv = new CameraEV();
            cameraEv.currentEv = parameters.getExposureCompensation();
            cameraEv.evStep = parameters.getExposureCompensationStep();
            cameraEv.maxEv = parameters.getMaxExposureCompensation();
            cameraEv.minEv = parameters.getMinExposureCompensation();
        }
        if (CameraStatus.getCameraStatus().isSubLineOn()) {
            showDottedLine();
        } else {
            dismissDottedLine();
        }
        canClick = true;
    }

    private void showFakeView(boolean isShow, int fakeHeight) {
        if (isShow) {
            surfviewCameraNewLayoutBinding.fakeViewTop.setVisibility(VISIBLE);
            surfviewCameraNewLayoutBinding.fakeViewBottom.setVisibility(VISIBLE);
            ConstraintLayout.LayoutParams layoutParamsTop = (ConstraintLayout.LayoutParams) surfviewCameraNewLayoutBinding.fakeViewTop.getLayoutParams();
            layoutParamsTop.height = fakeHeight;
            surfviewCameraNewLayoutBinding.fakeViewTop.setLayoutParams(layoutParamsTop);
            ConstraintLayout.LayoutParams layoutParamsBottom = (ConstraintLayout.LayoutParams) surfviewCameraNewLayoutBinding.fakeViewBottom.getLayoutParams();
            layoutParamsBottom.height = fakeHeight;
            surfviewCameraNewLayoutBinding.fakeViewBottom.setLayoutParams(layoutParamsBottom);
        } else {
            surfviewCameraNewLayoutBinding.fakeViewTop.setVisibility(View.GONE);
            surfviewCameraNewLayoutBinding.fakeViewBottom.setVisibility(View.GONE);
        }
    }

    /**
     * 计算上下margin距离
     *
     * @param bottom_height 底部遮挡的高度
     * @param margin        距离底部margin
     * @param h             surfaceView实际高度
     * @return
     */
    private double calculateMargin(double bottom_height, double margin, double h) {
        double marginTop = ContentUtil.getScreenHeight(SurfViewCameraNewActivity.this) - ContentUtil.dip2px(SurfViewCameraNewActivity.this, TITLE_HEIGHT) - margin - h;
        double marginBottom = bottom_height + margin - (ContentUtil.dip2px(SurfViewCameraNewActivity.this, BOTTOM_CONTROL_HEIGHT) + ContentUtil.dip2px(SurfViewCameraNewActivity.this, CAMERAPARMS_HEIGHT));
        double marginPlus = 0;
        if (marginTop != marginBottom) {
            marginPlus = (marginTop - marginBottom) / 2;
        }
        return marginPlus;
    }

    /**
     * 通过屏幕宽度算出能放下16：9比例的最大尺寸
     *
     * @return
     */
    private double getSupportSurfaceViewHeight(int reserved) {
        double x = 0;
        boolean height_appropriate = false;
        double maxHeight = ContentUtil.getScreenHeight2(SurfViewCameraNewActivity.this) - ContentUtil.dip2px(SurfViewCameraNewActivity.this, (int) TITLE_HEIGHT);
        while (!height_appropriate) {
            double w = ContentUtil.getScreenWidth(SurfViewCameraNewActivity.this) + ContentUtil.dip2px(SurfViewCameraNewActivity.this, reserved) - x;
            double h = w * 16 / 9;
            if (h > maxHeight) {
                x++;
                height_appropriate = false;
            } else {
                height_appropriate = true;
            }
        }
        return x;
    }

    /**
     * 通过屏幕宽度算出能放下4：3比例的最大尺寸
     *
     * @return
     */
    private double getSupportSurfaceViewHeight2(int reserved) {
        double x = 0;
        boolean height_appropriate = false;
        double maxHeight = ContentUtil.getScreenHeight2(SurfViewCameraNewActivity.this) - ContentUtil.dip2px(SurfViewCameraNewActivity.this, (int) TITLE_HEIGHT);
        while (!height_appropriate) {
            double w = ContentUtil.getScreenWidth(SurfViewCameraNewActivity.this) + ContentUtil.dip2px(SurfViewCameraNewActivity.this, reserved) - x;
            double h = w * 4 / 3;
            if (h > maxHeight) {
                x++;
                height_appropriate = false;
            } else {
                height_appropriate = true;
            }
        }
        return x;
    }

    /**
     * 预览界面与左右间距
     *
     * @return
     */
    private double getsurfaceViewLRMargin() {
        double marginRatio = 300d / 375d;
        double ScreenWidth = ContentUtil.getScreenWidth(SurfViewCameraNewActivity.this);
        double needSurfacewidth = marginRatio * ScreenWidth;
        double margin = (ScreenWidth - needSurfacewidth) / 2;
        if (margin > 0) {
            return margin;
        } else {
            return 0;
        }
    }

    private void setProLayout(double height) {
      /*  if (height > ContentUtil.dip2px(SurfViewCameraNewActivity.this, 60))
            height = ContentUtil.dip2px(SurfViewCameraNewActivity.this, 60);
        camer_adjust_param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) height);
        camer_adjust_param.addRule(RelativeLayout.BELOW,surfviewCameraNewLayoutBinding.surfaceview.getId());
       // camer_adjust_param.addRule(RelativeLayout.ABOVE, surfviewCameraNewLayoutBinding.bottomControl.getId());
        surfviewCameraNewLayoutBinding.cameraParamAdjustView.setLayoutParams(camer_adjust_param);*/
    }


    @Override
    protected void onViewClick() {

        surfviewCameraNewLayoutBinding.cameraSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // camera_setting_view.startViweType();
                surfviewCameraNewLayoutBinding.cameraSettingView.show();
                surfviewCameraNewLayoutBinding.cameraCloseBtn.setEnabled(false);
            }
        });


        surfviewCameraNewLayoutBinding.cameraCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canClick) {
                    finishView(Flag.NULLCODE, null);
                }
            }
        });

        surfviewCameraNewLayoutBinding.cameraSettingView.setCameraSettingInter(new CameraSettingView.CameraSettingInter() {
            @Override
            public void onClose(final CameraStatus cameraStatus) {
                CameraStatus.saveToFile(cameraStatus);
                canClick = false;
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switchCamera();
                        if (hashMap == null) {
                            hashMap = new HashMap<String, String>();
                        }
                        hashMap.put(Flag.FLASH, cameraStatus.isFlashOn() + "");
                        if (currentType == GPUImageCameraLoader.CAMER_TYPE) {
                            setCameraPerViewSize(cameraStatus.getPictureRatio());
                        } else {
                            setCameraPerViewSize(cameraStatus.getVideoRatio());
                        }
                        if (cameraStatus.isSubLineOn()) {
                            showDottedLine();
                        } else {
                            dismissDottedLine();
                        }
                        if (cameraStatus.getRecordTime() != CameraStatus.Size.RECORD_CUSTOM) {
                            surfviewCameraNewLayoutBinding.cameraBottomView.setRecordTime(cameraStatus.getRecordTime().getIndex());
                        } else {
                            surfviewCameraNewLayoutBinding.cameraBottomView.setRecordTime(cameraStatus.getCostomRecordTime());
                        }
                        surfviewCameraNewLayoutBinding.cameraCloseBtn.setEnabled(true);
                    }
                }, 200);

            }

            @Override
            public void onWaterMarkClick() {
                WaterMarkPresenter.startAction(SurfViewCameraNewActivity.this, REQUESE, false);
                // startActivityForResult(new Intent(SurfViewCameraNewActivity.this, WaterMarkSettingActivity.class), REQUESE);
            }

            @Override
            public void onPreRatioNotify(int preType) {
                mCamera.notifyPreRatio(preType);
            }

            @Override
            public void onFlashClick(boolean isflashon) {
                mCamera.setFlash(isflashon);
            }
        });

        surfviewCameraNewLayoutBinding.cameraBottomView.setCameraBottomViewInter(new CameraBottomView.CameraBottomViewInter() {
            @Override
            public void onCameraClick() {
                if (canClick && presenter.getCanClick()) {
                    EventStatisticsUtil.event(SurfViewCameraNewActivity.this, EventCommon.CAMERA_TAKEPHOTO);

                    takePhotoAnimation();
                    mCamera.takePhoto();

                }
            }

            @Override
            public void onChangeClick(int type) {
                currentType = type;
                if (type == GPUImageCameraLoader.CAMER_TYPE) {
                    switchCamera();
                    setCameraPerViewSize(CameraStatus.getCameraStatus().getPictureRatio());
                } else {
                    switchCamera();
                    setCameraPerViewSize(CameraStatus.getCameraStatus().getVideoRatio());
                }
            }

            @Override
            public void onStatusClick() {
                surfviewCameraNewLayoutBinding.cameraParamAdjustView.setClickShowType(CameraParamAdjustView.TYPE_AUTO);
                cameraType = CameraParamAdjustView.TYPE_AUTO;
            }

            @Override
            public void onVideoClick() {
                if(null!=mCamera){
                    mCamera.prepareRecordModel();
                }
                surfviewCameraNewLayoutBinding.cameraSettingBtn.setEnabled(false);
                surfviewCameraNewLayoutBinding.cameraSettingBtn.setAlpha(0.5f);
                surfviewCameraNewLayoutBinding.coBoxChooseTopView.setCoboxConnectClickEnabled(false);
                if (canClick) {
                    canClick = false;
                    mCamera.readyStart(new GPUImageCameraLoader.VidioInfer() {
                        @Override
                        public void isReadyComplete(String video, String bitmap) {
                            saveVideo(video, bitmap);
                        }
                    }, presenter.switchVidioFilter());
                }
            }

            @Override
            public void onCountDownFin() {
                if(null!=mCamera){
                    mCamera.startRecoder();
                }
            }

            @Override
            public void onVideoFin() {
                if(null!=mCamera){
                    mCamera.endRecording();
                    showDialog();
                }else {
                    ToastUtil.toast(getResources().getString(R.string.record_error));
                }

                surfviewCameraNewLayoutBinding.cameraSettingBtn.setAlpha(1f);
                surfviewCameraNewLayoutBinding.cameraSettingBtn.setEnabled(true);
                surfviewCameraNewLayoutBinding.coBoxChooseTopView.setCoboxConnectClickEnabled(true);
                // presenter.setRecord(false);
            }

            @Override
            public void onVideoPause(boolean ispause) {
            }

            @Override
            public void onPreset() {
                surfviewCameraNewLayoutBinding.cameraParamAdjustView.setClickShowType(CameraParamAdjustView.TYPE_PRESET);
                cameraType = CameraParamAdjustView.TYPE_PRESET;
                presenter.refreshLightBlueTouch();
                surfviewCameraNewLayoutBinding.scaleView.setParamProgressAndDefaultPoint2(100, presenter.getmExposure() * 2);

            }

            @Override
            public void onPro() {
                surfviewCameraNewLayoutBinding.cameraParamAdjustView.setClickShowType(CameraParamAdjustView.TYPE_PRO);
                cameraType = CameraParamAdjustView.TYPE_PRO;
            }

            @Override
            public void onAutoCamera() {

            }
        });

        surfviewCameraNewLayoutBinding.cameraParamAdjustView.setCameraParamAdjustInter(new CameraParamAdjustView.CameraParamAdjustInter() {
            @Override
            public void onScaleScroll(int scale) {
                if (mScale == scale) {
                    return;
                }
                //#0.0
                mScale = scale;
                setExposure(mScale);
            }
        });


        /**
         * 加曝光
         */
        surfviewCameraNewLayoutBinding.cameraParamAdjustView.setAddListner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mScale == surfviewCameraNewLayoutBinding.cameraParamAdjustView.getMax()) {
                    return;
                }
                mScale++;
                surfviewCameraNewLayoutBinding.cameraParamAdjustView.scrollToScale(mScale);
//                float currentAeNum = (cameraEv.maxEv / (float) surfviewCameraNewLayoutBinding.cameraParamAdjustView.getMax() * mScale);
                setExposure(mScale);

            }
        });


        /**
         * 减曝光
         */
        surfviewCameraNewLayoutBinding.cameraParamAdjustView.setReduceListner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScale == surfviewCameraNewLayoutBinding.cameraParamAdjustView.getMin()) {
                    return;
                }
                mScale--;
                surfviewCameraNewLayoutBinding.cameraParamAdjustView.scrollToScale(mScale);
//                float currentAeNum = (cameraEv.maxEv / (float) surfviewCameraNewLayoutBinding.cameraParamAdjustView.getMax() * mScale);

                setExposure(mScale);
            }
        });


        /**
         * 预设参数SQL回调
         */
        presetParmsSqlControl.setListener(new BaseSqlControl.SqlControlListener() {
            @Override
            public void queryListener(Object... parms) {
                List<PresetParm> presetParms = (List<PresetParm>) parms[0];

                if (presetItems == null) {
                    presetItems = new ArrayList<>();
                }
                presetItems.clear();
                presetItems.add(new PresetItem(getResources().getString(R.string.none), "", ""));
                //presetItems.add(new PresetItem(getResources().getString(R.string.none), "scan", ""));
                for (int i = 0; i < presetParms.size(); i++) {
                    if (null != presetParms.get(i).getLightRatioData() && (presetParms.get(i).getLightRatioData().getVersion().equals(
                            String.valueOf(CommendManage.getInstance().getVersion())) || presetParms.get(i).getId().equals("1"))
                            || presetParms.get(i).getId().equals("2")
                            || presetParms.get(i).getId().equals("3")) {
                        PresetItem presetItem = new PresetItem(presetParms.get(i).getName(), presetParms.get(i).getTextSrc(), presetParms.get(i).getPresetId());
                        presetItem.setPresetParm(presetParms.get(i));
                        presetItems.add(presetItem);
                    }
                }
//                presetItems.add(new PresetItem("help", "help"));
                surfviewCameraNewLayoutBinding.cameraParamAdjustView.addData(presetItems);
            }

            @Override
            public void insertListener(Object... parms) {

            }

            @Override
            public void deletListener(Object... parms) {

            }

            @Override
            public void upDataListener(Object... parms) {

            }
        });

        surfviewCameraNewLayoutBinding.blueToothListView.setCloseConnecrtion(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BleConnectModel.getBleConnectModelInstance().getCurrentBlueToothDevice() != null)
                    surfviewCameraNewLayoutBinding.blueToothListView.setConnectBluetoothName(BleConnectModel.getBleConnectModelInstance().getCurrentBlueToothDevice().getName());
                presenter.getBleConnectModel().disConnectCurrent();

                //presenter.choiceDeivce(presenter.getSelectPosition());
            }
        });

        /**
         * 关闭蓝牙选择界面
         */
        surfviewCameraNewLayoutBinding.coBoxChooseTopView.setDownLeftBtnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (surfviewCameraNewLayoutBinding.coBoxChooseTopView.getTopViewType() == CoBoxChooseTopView.TYPE_CAMERA) {
                    outVedio();
                } else {
                    outPro();
                }
            }
        });

        /**
         * 跳转到预设参数市场
         */
        surfviewCameraNewLayoutBinding.coBoxChooseTopView.setMarketLeftBtnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canClick) {
                    //TemplateActivcity.actionStart(SurfViewCameraActivity.this,-1);
//                    MarketPresetActicity.actionStart(SurfViewCameraActivity.this);
                    EventStatisticsUtil.event(SurfViewCameraNewActivity.this, EventCommon.CAMERA_PRESET_MARKET);

                    Intent intent = new Intent(SurfViewCameraNewActivity.this, MarketFragmentActivity.class);
                    startActivity(intent);
//                    MarketFragmentActivity.
                }
            }
        });


//        /**
//         * 保存光比
//         */
//        coBoxChooseTopView.setLightPenRightBtnClick(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (coBoxChooseTopView.getTopViewType() == CoBoxChooseTopView.TYPE_PRO && BlueToothLoad.getInstance().blueToothIsConnection()) {
//                    EventStatisticsUtil.event(SurfViewCameraNewActivity.this, EventCommon.CAMERA_PRO_SAVE_LIGHT_PEN);
//
//                    presenter.showLightPenPop();
//                }
//            }
//        });

        /**
         * 打开蓝牙列表
         */
        surfviewCameraNewLayoutBinding.coBoxChooseTopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!presenter.checkGPSIsOpen()) {
                    ToastUtil.toast(getResources().getString(R.string.request_gps));
                    return;
                }
                if (!BleConnectModel.getBleConnectModelInstance().isBlueEnable()) {
                    surfviewCameraNewLayoutBinding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CLOSE);
                } else {
                    surfviewCameraNewLayoutBinding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_OPEN);
                    if (BleConnectModel.getBleConnectModelInstance().isCurrentConnect()) {
                        surfviewCameraNewLayoutBinding.blueToothListView.setConnectBluetoothName(BleConnectModel.getBleConnectModelInstance().getCurrentBlueToothDevice().getName());
                        surfviewCameraNewLayoutBinding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CONNECTION);
                    }
                }

                surfviewCameraNewLayoutBinding.blueToothListView.setApplyBlur();
                //showQRCodePop(v);
                if (PermissionUtil.getFindLocationPermmission(SurfViewCameraNewActivity.this)) {
                    surfviewCameraNewLayoutBinding.blueToothListView.bringToFront();
                    surfviewCameraNewLayoutBinding.blueToothListView.setVisibility(VISIBLE);
                } else {
                    ToastUtil.toast(SurfViewCameraNewActivity.this.getResources().getString(R.string.please_location_permissions));
                }

                presenter.startScanBlueTooth();
                mCamera.onPause();
                surfviewCameraNewLayoutBinding.surfaceview.setVisibility(View.GONE);
//                blueToothListView.setVisibility(VISIBLE);


            }
        });

        /**
         * 设置左右滑动手势监听
         */
        bizGestureListener = new BizGestureListener(this);


        /**
         * 上下滑动手势
         */
        bizGestureListener.setGestureUpDownDetector(new BizGestureListener.GestureUpDownDetector() {
            @Override
            public void onStartUpDown() {

                if (cameraType == CameraParamAdjustView.TYPE_PRO) {
                    int selectedLightNum = presenter.getSelectedLightNum();
                    if (selectedLightNum >= 0 && selectedLightNum < 6) {
                        presenter.getLightNum();
                        surfviewCameraNewLayoutBinding.scaleView.setVisibility(VISIBLE);
                    }
                } else if (cameraType == CameraParamAdjustView.TYPE_PRESET) {
                    surfviewCameraNewLayoutBinding.scaleView.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onUpDown(float addNum) {
                if (cameraType == CameraParamAdjustView.TYPE_PRO) {
                    int selectedLightNum = presenter.getSelectedLightNum();
                    if (selectedLightNum >= 0 && selectedLightNum < 6) {
                        presenter.changeLightNew((int) addNum);
                    }
                } else if (cameraType == CameraParamAdjustView.TYPE_PRESET) {
                    presenter.changeExpusure((int) addNum);
                }
            }

            @Override
            public void onEndUpDown() {
                if (cameraType == CameraParamAdjustView.TYPE_PRO) {
                    int selectedLightNum = presenter.getSelectedLightNum();
                    if (selectedLightNum >= 0 && selectedLightNum < 6) {
                        surfviewCameraNewLayoutBinding.scaleView.setVisibility(View.GONE);
                    }
                    if (selectedLightNum > 0) {
                        setErrorConnectPresetSelect();
                    }
                } else if (cameraType == CameraParamAdjustView.TYPE_PRESET) {
                    presenter.onEndUpDown();
                    surfviewCameraNewLayoutBinding.scaleView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onUpDown(MotionEvent motionEvent) {

            }

            @Override
            public void onEndUpDown(MotionEvent motionEvent) {
            }

            @Override
            public void onStartUpDown(MotionEvent motionEvent) {

            }

            @Override
            public void onDown(MotionEvent motionEvent) {


            }
        });
        /**
         * 左右滑动和点击手势
         */
        bizGestureListener.setGestureLeftRightDetector(new BizGestureListener.GestureLeftRightDetector() {

            @Override
            public void onStartLeftRight() {
            }

            @Override
            public void onClick(MotionEvent event) {

                int px = ImageUtil.Dp2Px(SurfViewCameraNewActivity.this, 60);
                int marginW = (int) event.getX();
                int marginH = (int) event.getY();
                if (isShowRect && !invideo) {
                    // marginH -= (height - width) / 2;
                    if (marginH > surfviewCameraNewLayoutBinding.focusview.getMeasuredHeight() - bottom_height)
                        return;
                }
                surfviewCameraNewLayoutBinding.focusview.setRoundArcListener(new FocusView.RoundArcListener() {
                    @Override
                    public void onAnimEnd() {
                        isAniming = false;
                    }

                    @Override
                    public void onAnimStart() {
                        isAniming = true;
                    }

                    @Override
                    public void onRectAnimEnd() {
                    }
                });
                if (!isAniming) {
                    // focusView.bringToFront();
                    surfviewCameraNewLayoutBinding.focusview.invalidate();
                    surfviewCameraNewLayoutBinding.focusview.setCenterX(marginW, marginH, false);
                    surfviewCameraNewLayoutBinding.focusview.setScaleAnim();
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            mCamera.pointFocus(event.getX(), event.getY(), surfviewCameraNewLayoutBinding.surfaceview.getWidth(), surfviewCameraNewLayoutBinding.surfaceview.getHeight());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (surfviewCameraNewLayoutBinding.coBoxChooseTopView.getTopViewType() == CoBoxChooseTopView.TYPE_PRESET) {
                    outPreset();
                } else if (surfviewCameraNewLayoutBinding.coBoxChooseTopView.getTopViewType() == CoBoxChooseTopView.TYPE_PRESET_CAMERA) {
                    outVedioInPreset();
                }
            }

            @Override
            public void onLongClick(MotionEvent event) {
            }

            //左右滑动
            @Override
            public void onLeftRight(int pickerViewVType) {

                //预设参数页面
                /*if (coBoxChooseTopView.getTopViewType() == CoBoxChooseTopView.TYPE_PRESET) {
                    leftOrRight(pickerViewVType);
                    //拍照页面且选中过预设参数
                } else if (coBoxChooseTopView.getTopViewType() == CoBoxChooseTopView.TYPE_NORMAL && isSelectedPreset) {
                    leftOrRight(pickerViewVType);
                }*/
            }

            @Override
            public void onEndLeftRight() {
            }
        });

//        /**
//         * 关闭预算参数
//         */
//        presetBottomView.closePresetClick(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                outPreset();
//            }
//        });

        surfviewCameraNewLayoutBinding.focusview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return bizGestureListener.onTouchEvent(event);
            }
        });

        //传感器回调
        class GPUImageCamreraListener implements GPUImageCamerLoaderBase.GPUImageCamreraListener {
            @Override
            public void takePhotoListenr(Bitmap bitmap, int ratio, CameraParm cameraParm) {
                presenter.takePhoto(bitmap, SurfViewCameraNewActivity.this.isShowRect, ratio, cameraParm, CameraStatus.getCameraStatus().getWaterMark().isWaterMarkOn());
            }

            @Override
            public void takePhonoErr() {
                Toast.makeText(SurfViewCameraNewActivity.this, getResources().getString(R.string.take_photo_fail), Toast.LENGTH_LONG).show();

            }

            @Override
            public void takePhotoByteListener(byte[] data, int ratio, CameraParm cameraParm) {

            }

            @Override
            public void getOrientation(int orientation) {
                presenter.rotationViewAnim(orientation);
            }

            /**
             *
             * @param xAngle 仰俯角 -180≤values[1]≤180
             * @param yAngle 翻转角 90≤values[2]≤90
             * @param zAngle 方位角
             */
            @Override
            public void getyAngle(float xAngle, float yAngle, float zAngle) {
                presenter.rotationSubline(yAngle, xAngle);
                presenter.setRendererXYZ(xAngle, yAngle, zAngle);
            }

            @Override
            public void getAngle(float xDegrees, float yDegrees) {
                presenter.rotation(xDegrees, yDegrees);
            }
        }
        mCamera.setGpuImageCamreraListener(new GPUImageCamreraListener());
    }

   /* private void leftOrRight(int pickerViewVType) {
        //左滑
        if (pickerViewVType == -1) {
            if (selectedPresetPosition == 0 && selectedPresetPosition == 1) {
                return;
            }
            if (canMove == true) {

                canMove = false;
                selectedPresetPosition = selectedPresetPosition - 1;
                (getHandler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        canMove = true;
                    }
                }, 250);

                if (selectedPresetPosition != 0 && selectedPresetPosition != 1 && !BleManager.getInstance().isCurrentConnect()*//*!BlueToothLoad.getInstance().blueToothIsConnection()*//*) {
                    selectedPresetPosition++;
                    return;
                }
                isSelectedPreset = true;
                ToastUtil.toast(presetItems.get(selectedPresetPosition).getName());
                surfviewCameraNewLayoutBinding.presetBottomView.setSelect(selectedPresetPosition);
                presenter.getImageData().setPresetParms(presetItems.get(selectedPresetPosition).getPresetParm());
                presenter.setGPUImageFilter(presetItems.get(selectedPresetPosition).getPresetParm());
            }
        }

        //右滑
        if (pickerViewVType == 1) {
            if (selectedPresetPosition == presetItems.size() - 1) {
                return;
            }
            if (canMove == true) {

                canMove = false;
                selectedPresetPosition = selectedPresetPosition + 1;
                (getHandler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        canMove = true;
                    }
                }, 250);
                if (selectedPresetPosition != 0 && selectedPresetPosition != 1 && !BleManager.getInstance().isCurrentConnect()*//*!BlueToothLoad.getInstance().blueToothIsConnection()*//*) {
                    selectedPresetPosition--;
                    return;
                }
                isSelectedPreset = true;
                presenter.getImageData().setPresetParms(presetItems.get(selectedPresetPosition).getPresetParm());
                surfviewCameraNewLayoutBinding.presetBottomView.setSelect(selectedPresetPosition);
                ToastUtil.toast(presetItems.get(selectedPresetPosition).getName());
                presenter.setGPUImageFilter(presetItems.get(selectedPresetPosition).getPresetParm());
            }
        }
    }*/

    @Override
    public void disMissBlueView() {
        surfviewCameraNewLayoutBinding.blueToothListView.close();
        //初始曝光补偿
        mCamera.setEv(0);
        //重置滑动条
        surfviewCameraNewLayoutBinding.cameraParamAdjustView.reset();
    }

    /**
     * 显示辅助线
     */
    public void showDottedLine() {
        EventStatisticsUtil.event(SurfViewCameraNewActivity.this, EventCommon.CAMERA_DOTTEDLINE);
       /* ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) surfviewCameraNewLayoutBinding.dottedLine.getLayoutParams();

        layoutParams.bottomMargin = ContentUtil.dip2px(SurfViewCameraNewActivity.this, BOTTOM_CONTROL_HEIGHT) + ContentUtil.dip2px(SurfViewCameraNewActivity.this, CAMERAPARMS_HEIGHT);
        surfviewCameraNewLayoutBinding.dottedLine.setLayoutParams(layoutParams);
*/
        surfviewCameraNewLayoutBinding.dottedLine.setVisibility(View.VISIBLE);
    }

    public void dismissDottedLine() {
        EventStatisticsUtil.event(SurfViewCameraNewActivity.this, EventCommon.CAMERA_UNDOTTEDLINE);

        surfviewCameraNewLayoutBinding.dottedLine.setVisibility(View.GONE);
    }


    @Override
    public void initRotationAnim(final int currentRotation, final int rotation) {
        presenter.startFloatAnim(new AnimFactory.FloatListener() {
            @Override
            public void floatValueChang(float f) {
//                float fr = currentRotation + f * (rotation - currentRotation);
//                lineImageView.setRotation(fr);
            }
        });
    }

    @Override
    public void initRotationSubline(final int currentRotation, float xAngle, float yAngle) {
        if (currentRotation == 90 || currentRotation == 270) {

            if (xAngle <= -90 && yAngle < 0) {
                float x = 90 + yAngle;
                yAngle = -90 - x;
            } else if (xAngle > 90 && yAngle < 0) {
                float x = 90 + yAngle;
                yAngle = -90 - x;
            } else if (xAngle >= 90 && yAngle > 0) {
                float x = 90 - yAngle;
                yAngle = 90 + x;
            } else if (xAngle < -90 && yAngle < 0) {
                float x = 90 - yAngle;
                yAngle = 90 + x;
            }
            int blueLineHight = (int) (surfviewCameraNewLayoutBinding.lineImageView.getHeight() / 2 * (yAngle * 2) / 180);

//            int blueLineHight = (int) (lineImageView.getHeight() / 2 * (xAngle*2) / 300);
            //每一弧度的长度
            surfviewCameraNewLayoutBinding.lineImageView.blueLineHight = blueLineHight;
            surfviewCameraNewLayoutBinding.lineImageView.invalidate();
        } else {
            int blueLineHight = (int) (surfviewCameraNewLayoutBinding.lineImageView.getHeight() / 2 * xAngle / MAX_ANGLE);
            //每一弧度的长度
            surfviewCameraNewLayoutBinding.lineImageView.blueLineHight = blueLineHight;
            surfviewCameraNewLayoutBinding.lineImageView.invalidate();
        }

    }

    @Override
    public void rotationSubline(final float xDegrees, float yDegrees, final float rotation) {

        surfviewCameraNewLayoutBinding.ivSubline.setRotation(rotation);
        surfviewCameraNewLayoutBinding.ivSubline.invalidate();
        if (Math.abs(xDegrees) == 180f || Math.abs(xDegrees) == 0f || Math.abs(xDegrees) == 90f) {
            surfviewCameraNewLayoutBinding.ivSubline.setImageResource(R.drawable.subline_blue);
        } else {
            surfviewCameraNewLayoutBinding.ivSubline.setImageResource(R.drawable.subline_white);
        }

    }

    @Override
    public void inTakePhoto() {
        presenter.startFloatAnim(new AnimFactory.FloatListener() {
            @Override
            public void floatValueChang(float f) {

            }
        });

    }

    @Override
    public void outTakePhoto() {
        presenter.startFloatAnim(new AnimFactory.FloatListener() {
            @Override
            public void floatValueChang(float f) {

            }
        });
    }

    //录像
    @Override
    public void inVedio() {

    }

    @Override
    public void inPro() {

    }

    @Override
    public void outPro() {

    }

    @Override
    public void setCameraParams(HashMap<String, String> hashMap) {

    }

    @Override
    public void lockAllCameraParam() {

    }

    @Override
    public void unlockAllCameraParam() {

    }

    //退出录像
    @Override
    public void outVedio() {

    }

    @Override
    public void inVedioOutPreset() {
    }

    @Override
    public void outVedioInPreset() {
    }

    //预设参数
    @Override
    public void inPreset() {
    }

    //退出预设参数
    @Override
    public void outPreset() {

    }

    @Override
    public void takePhotoAnimation() {
        presenter.startFloatAnim(new AnimFactory.FloatListener() {
            @Override
            public void floatValueChang(float f) {
                surfviewCameraNewLayoutBinding.surfaceview.setAlpha(f);
            }
        }, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }, 150);
    }

    /**
     * 设置连接的蓝牙名称
     *
     * @param name 蓝牙名称
     */
    @Override
    public void setConnectBluetoothName(String name) {
        surfviewCameraNewLayoutBinding.coBoxChooseTopView.setConnectBluetoothName(name);
//        surfviewCameraNewLayoutBinding.blueToothListView.setConnectBluetoothName(name);
    }

    /**
     * 连接中断时设置预设参数为无
     */
    @Override
    public void setErrorConnectPresetSelect() {
        surfviewCameraNewLayoutBinding.presetBottomView.setSelect(0);
        presenter.setGPUImageFilter(null);
        presenter.getImageData().setPresetParms(null);
        selectedPresetPosition = 0;
    }

    /**
     * 点击预设参数
     *
     * @param position   预设参数下标
     * @param viewHolder
     */
    @Override
    public void presetItemClick(int position, PresetHorizontalListAdapter.ViewHolder viewHolder) {

        if (/*!BlueToothLoad.getInstance().blueToothIsConnection()*/ !BleConnectModel.getBleConnectModelInstance().isCurrentConnect()) {
            return;
        }
       /* if (position == 1) {
            startScan();
            return;
        }*/
        selectedPresetPosition = position;
        isSelectedPreset = true;
        surfviewCameraNewLayoutBinding.cameraParamAdjustView.setSelect(position);
        ToastUtil.toast(presetItems.get(position).getName());
        presenter.setGPUImageFilter(presetItems.get(position).getPresetParm());
        presenter.getImageData().setPresetParms(presetItems.get(position).getPresetParm());
    }

    /**
     * 点击识别预设参数
     *
     * @param position   预设参数下标
     * @param viewHolder
     */
    @Override
    public void presetItemClick(int position, PresetScanListAdapter.ViewHolder viewHolder) {
        if (/*!BlueToothLoad.getInstance().blueToothIsConnection()*/ !BleConnectModel.getBleConnectModelInstance().isCurrentConnect()) {
            return;
        }
        if (position == 0) {
            surfviewCameraNewLayoutBinding.cameraParamAdjustView.addData(presetItems);
            return;
        }
    }

    /**
     * 设置滤镜
     *
     * @param gpuImageFilter 滤镜
     */
    @Override
    public void surfviewSwitchFilterTo(GPUImageFilter gpuImageFilter) {
        mCamera.switchFilterTo(gpuImageFilter);
    }

    /**
     * 设置滚动条数值
     *
     * @param num
     */
    @Override
    public void setSeekBarNum(int num) {
        surfviewCameraNewLayoutBinding.scaleView.setProgress(num * 2);
    }

    /**
     * 设置中心
     *
     * @param num
     * @param defaultPoint
     * @param cameraParamType
     */
    @Override
    public void setProgressAndDefaultPoint(int num, float defaultPoint, CameraParamType cameraParamType) {
        surfviewCameraNewLayoutBinding.scaleView.setProgressAndDefaultPoint(defaultPoint, num * 2, cameraParamType);
    }


    @Override
    public Handler getHandler() {
        return super.getHandler();
    }

    @Override
    public void setXYZRenderer(GLSurfaceView.Renderer renderer) {
        surfviewCameraNewLayoutBinding.rectView.setZOrderOnTop(true);
        surfviewCameraNewLayoutBinding.rectView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        surfviewCameraNewLayoutBinding.rectView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        surfviewCameraNewLayoutBinding.rectView.setRenderer(renderer); // Use a custom renderer
    }

    @Override
    public void setBlueTouchType(int type) {
        switch (type) {
            case CoBoxChooseTopView.BLUE_TOUCH_CLOSE:
                presenter.refreshLightBlueTouch();
                break;
            case CoBoxChooseTopView.BLUE_TOUCH_OPEN:
                presenter.refreshLightBlueTouch();
                break;
            case CoBoxChooseTopView.BLUE_TOUCH_CONNECTION:
                if (surfviewCameraNewLayoutBinding.blueToothListView.getVisibility() == VISIBLE)
                    surfviewCameraNewLayoutBinding.blueToothListView.close();
                //初始曝光补偿
                mCamera.setEv(0);
                //重置滑动条
                surfviewCameraNewLayoutBinding.cameraParamAdjustView.reset();
                break;
            case CoBoxChooseTopView.BLUE_TOUCH_CONNECTION_ERROR:
                presenter.refreshLightBlueTouch();
                presenter.setmExposure(50);
                break;
        }
        if (type == CoBoxChooseTopView.BLUE_TOUCH_CONNECTION && CommendManage.getInstance().getVersion() != CommendManage.VERSION_BOX) {
            cameraType = CameraParamAdjustView.TYPE_PRESET;
            surfviewCameraNewLayoutBinding.scaleView.setParamProgressAndDefaultPoint2(100, 100);
        } else {
            cameraType = CameraParamAdjustView.TYPE_AUTO;
        }
        surfviewCameraNewLayoutBinding.coBoxChooseTopView.setBlueTouchType(type);
        surfviewCameraNewLayoutBinding.blueToothListView.setBlueTouchType(type);
        surfviewCameraNewLayoutBinding.cameraBottomView.setBlueTouchType(type);
        surfviewCameraNewLayoutBinding.cameraParamAdjustView.setConnectionShowType(type);

        dissmissDialog();

    }

    @Override
    public void setWaitPicShow(boolean b) {
        if (!b) {
            this.canClick = false;
            showLoading();
        } else {
            this.canClick = true;
            hideLoading();
        }
    }

    @Override
    public List getItemList() {
        return null;
    }

    @Override
    public void showToast(ImageData i, String str) {
        ToastUtil.toast(str);
    }


    @Override
    public void onRecodeShow(int w, int h) {

    }

    /**
     * 设置最大亮度
     *
     * @param context
     * @param brightness
     */
    private void setLight(Activity context, int brightness) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        context.getWindow().setAttributes(lp);
    }

    @Override
    public boolean isShowRect() {
       /* if (rect_top.getVisibility() == VISIBLE) {
            return true;
        } else {
            return false;
        }*/
        return true;
    }

    public class CameraEV {
        float currentEv;
        float evStep;
        float maxEv;
        float minEv;
    }


    private void saveVideo(final String video, final String videocover) {
        dissmissDialog();
        presenter.goToRecoderActivity(video, videocover);
    }


    @Override
    public GPUImage getgpuimage() {
        return mCamera.getGpuimage();
    }

    @Override
    public GPUImageCamerLoaderBase getCamera() {
        return mCamera;
    }


    private void resetLightRatio() {
        presenter.writeAllLightNumber(50, 50, 50, 50, 50, 50, 0, 0);
    }

    /**
     * 切换相机
     */
    private void switchCamera() {
        int radius = 10;//模糊程度
        // final Bitmap blurbitmap = ImageUtil.doBlur(bitmap, radius, false);
        surfviewCameraNewLayoutBinding.focusview.setBackgroundColor(getResources().getColor(R.color.black));
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                canClick = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                surfviewCameraNewLayoutBinding.focusview.setBackgroundColor(getResources().getColor(R.color.translucent));
                canClick = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                surfviewCameraNewLayoutBinding.focusview.setBackgroundColor(getResources().getColor(R.color.translucent));
                canClick = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(400);
        animator.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        if (requestCode == resultCode && resultCode == REQUESE) {
            surfviewCameraNewLayoutBinding.cameraSettingView.setWatermark();
        }
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void setExposure(int mScale) {
        if (cameraType == CameraParamAdjustView.TYPE_PRESET) {
            float currentAeNum = (cameraEv.maxEv / (float) 50 * mScale);
            mCamera.setEv((int) currentAeNum);
        } else {
            float currentAeNum = (cameraEv.maxEv / (float) surfviewCameraNewLayoutBinding.cameraParamAdjustView.getMax() * mScale);
            mCamera.setEv((int) currentAeNum);
        }
    }


    @Override
    public void initGuide() {
        //initGuide在onResume方法中，点击添加水印会跳转activity，返回后会调用onResume方法，造成显示不正确
        if (surfviewCameraNewLayoutBinding.cameraSettingView.getVisibility() == VISIBLE) {
            return;
        }
        final RectF rect = new RectF();
        ViewTreeObserver vto = surfviewCameraNewLayoutBinding.cameraParamAdjustView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (rect.bottom == 0) {
                    rect.left = surfviewCameraNewLayoutBinding.cameraParamAdjustView.getX();
                    rect.right = surfviewCameraNewLayoutBinding.cameraParamAdjustView.getMeasuredWidth() + rect.left;
                    rect.top = surfviewCameraNewLayoutBinding.cameraParamAdjustView.getY();
                    rect.bottom = surfviewCameraNewLayoutBinding.cameraParamAdjustView.getMeasuredHeight() + rect.top;
                    return false;
                }
                return true;
            }

        });
        if (!presenter.getBleConnectModel().isCurrentConnect()) {
            //未连接设备的提示
            NewbieGuide.with(this)
                    .setShowCounts(1)
                    .setLabel("pageCamera")//设置引导层标示区分不同引导层，必传！否则报错
                    .setOnGuideChangedListener(new OnGuideChangedListener() {
                        @Override
                        public void onShowed(Controller controller) {
                            //引导层显示

                        }

                        @Override
                        public void onRemoved(Controller controller) {
                            //引导层消失（多页切换不会触发）
                            surfviewCameraNewLayoutBinding.coBoxChooseTopView.setVisibility(View.VISIBLE);
                        }
                    })
                    .addGuidePage(//添加一页引导页
                            GuidePage.newInstance()//创建一个实例
                                    .setBackgroundColor(getResources().getColor(R.color.trans_backgroud))
                                    .setLayoutRes(R.layout.guide_camera)//设置引导页布局
                                    .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {

                                        @Override
                                        public void onLayoutInflated(View view, Controller controller) {
                                            ImageView cobox = (ImageView) view.findViewById(R.id.cobox);
                                            cobox.setImageDrawable(getResources().getDrawable(R.drawable.guide_camera_cobox));
                                            cobox.setVisibility(View.VISIBLE);
                                            surfviewCameraNewLayoutBinding.coBoxChooseTopView.setVisibility(View.INVISIBLE);
                                        }

                                    })
                                    .setEnterAnimation(enterAnimation)//进入动画
                                    .setExitAnimation(exitAnimation)//退出动画
                    )
                    .addGuidePage(GuidePage.newInstance()
                            // .addHighLight(tvBottom, HighLight.Shape.RECTANGLE, 20)
                            .setBackgroundColor(getResources().getColor(R.color.trans_backgroud))
                            .setLayoutRes(R.layout.guide_camera)//引导页布局，点击跳转下一页或者消失引导层的控件id
                            .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                @Override
                                public void onLayoutInflated(View view, final Controller controller) {
                                    ImageView setting = (ImageView) view.findViewById(R.id.setting);
                                    setting.setVisibility(View.VISIBLE);
                                }
                            })
                            .setBackgroundColor(getResources().getColor(R.color.translucent))//设置背景色，建议使用有透明度的颜色
                            .setEnterAnimation(enterAnimation)//进入动画
                            .setExitAnimation(exitAnimation)//退出动画
                    )
                    .addGuidePage(GuidePage.newInstance()
                            .setLayoutRes(R.layout.guide_camera)
                            .addHighLight(rect)
                            .setBackgroundColor(getResources().getColor(R.color.trans_backgroud))
                            .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                @Override
                                public void onLayoutInflated(View view, final Controller controller) {
                                    ImageView exposure = (ImageView) view.findViewById(R.id.exposure);
                                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) exposure.getLayoutParams();
                                    int[] location = new int[2];
                                    surfviewCameraNewLayoutBinding.clAdjust.getLocationOnScreen(location);
                                    int height = (int) (ContentUtil.getScreenHeight2(SurfViewCameraNewActivity.this) /*+ ContentUtil.getStatusBarHeight(SurfViewCameraNewActivity.this)*/ - location[1]);
                                    layoutParams.bottomMargin = height;
                                    exposure.setLayoutParams(layoutParams);
                                    exposure.setVisibility(View.VISIBLE);
                                }
                            })
                    ).addGuidePage(GuidePage.newInstance()
                    .setLayoutRes(R.layout.guide_camera)
                    .setBackgroundColor(getResources().getColor(R.color.trans_backgroud))
                    .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                        @Override
                        public void onLayoutInflated(View view, final Controller controller) {
                            ImageView record = (ImageView) view.findViewById(R.id.record);
                            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) record.getLayoutParams();
                            int[] location = new int[2];
                            surfviewCameraNewLayoutBinding.cameraBottomView.getCamera_second_btn().getLocationOnScreen(location);
                            int height = (int) (ContentUtil.getScreenHeight2(SurfViewCameraNewActivity.this) - location[1] - surfviewCameraNewLayoutBinding.cameraBottomView.getCamera_second_btn().getHeight());
                            layoutParams.bottomMargin = height;
                            record.setVisibility(View.VISIBLE);
                        }
                    })
            )
                    .addGuidePage(//添加一页引导页
                            GuidePage.newInstance()//创建一个实例
                                    .setBackgroundColor(getResources().getColor(R.color.trans_backgroud))
                                    .setLayoutRes(R.layout.guide_camera)//设置引导页布局
                                    .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {

                                        @Override
                                        public void onLayoutInflated(View view, Controller controller) {
                                            surfviewCameraNewLayoutBinding.cameraParamAdjustView.setVisibility(View.VISIBLE);
                                            ImageView cobox = (ImageView) view.findViewById(R.id.cobox);
                                            cobox.setImageDrawable(getResources().getDrawable(R.drawable.guide_camera_coboxoff));
                                            cobox.setVisibility(View.VISIBLE);
                                            //引导页布局填充后回调，用于初始化
                                       /* TextView tv = view.findViewById(R.id.textView2);
                                        tv.setText("我是动态设置的文本");*/
                                        }
                                    })
                                    .setEnterAnimation(enterAnimation)//进入动画
                                    .setExitAnimation(exitAnimation)//退出动画
                    )
                    .show();//显示引导层(至少需要一页引导页才能显示)
        } else {
            //连接设备的提示
            NewbieGuide.with(this)
                    .setShowCounts(1)
                    .setLabel("pageCameraCobox")//设置引导层标示区分不同引导层，必传！否则报错
                    .setOnGuideChangedListener(new OnGuideChangedListener() {
                        @Override
                        public void onShowed(Controller controller) {
                            //引导层显示
                        }

                        @Override
                        public void onRemoved(Controller controller) {
                            //引导层消失（多页切换不会触发）
                        }
                    })
                    .addGuidePage(//添加一页引导页
                            GuidePage.newInstance()//创建一个实例
                                    .setBackgroundColor(getResources().getColor(R.color.trans_backgroud))
                                    .setLayoutRes(R.layout.guide_camera)//设置引导页布局
                                    .addHighLight(rect)
                                    .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {

                                        @Override
                                        public void onLayoutInflated(View view, Controller controller) {
                                            ImageView cobox = (ImageView) view.findViewById(R.id.cobox);
                                            cobox.setImageDrawable(getResources().getDrawable(R.drawable.guide_camera_preset));
                                            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                            layoutParams.setMargins(0, 0, 0, surfviewCameraNewLayoutBinding.bottomControl.getHeight() + surfviewCameraNewLayoutBinding.cameraParamAdjustView.getHeight());
                                            layoutParams.bottomToBottom = 0;
                                            layoutParams.leftToLeft = 0;
                                            layoutParams.rightToRight = 0;
                                            cobox.setLayoutParams(layoutParams);
                                            cobox.setVisibility(View.VISIBLE);
                                            // surfviewCameraNewLayoutBinding.cameraAdjustLayout.setVisibility(View.INVISIBLE);
                                            //引导页布局填充后回调，用于初始化
                                       /* TextView tv = view.findViewById(R.id.textView2);
                                        tv.setText("我是动态设置的文本");*/
                                        }
                                    })
                                    .setEnterAnimation(enterAnimation)//进入动画
                                    .setExitAnimation(exitAnimation)//退出动画
                    )
                    .addGuidePage(GuidePage.newInstance()
                            // .addHighLight(tvBottom, HighLight.Shape.RECTANGLE, 20)
                            .setBackgroundColor(getResources().getColor(R.color.trans_backgroud))
                            .setLayoutRes(R.layout.guide_camera)//引导页布局，点击跳转下一页或者消失引导层的控件id
                            .addHighLight(rect)
                            .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                @Override
                                public void onLayoutInflated(View view, final Controller controller) {
                                    ImageView cobox = (ImageView) view.findViewById(R.id.cobox);
                                    cobox.setImageDrawable(getResources().getDrawable(R.drawable.guide_camera_light));
                                    ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    int[] location = new int[2];
                                    surfviewCameraNewLayoutBinding.clAdjust.getLocationOnScreen(location);
                                    int height = (int) (ContentUtil.getScreenHeight2(SurfViewCameraNewActivity.this) - location[1]);
                                    layoutParams.bottomMargin = height;
                                    layoutParams.bottomToBottom = 0;
                                    layoutParams.leftToLeft = 0;
                                    layoutParams.rightToRight = 0;
                                    cobox.setLayoutParams(layoutParams);
                                    cobox.setVisibility(View.VISIBLE);
                                    //surfviewCameraNewLayoutBinding.cameraAdjustLayout.setVisibility(View.VISIBLE);
                                    surfviewCameraNewLayoutBinding.cameraParamAdjustView.setClickShowType(CameraParamAdjustView.TYPE_PRO);
                                    cameraType = CameraParamAdjustView.TYPE_PRO;
                                }
                            })
                            .setBackgroundColor(getResources().getColor(R.color.translucent))//设置背景色，建议使用有透明度的颜色
                            .setEnterAnimation(enterAnimation)//进入动画
                            .setExitAnimation(exitAnimation)//退出动画
                    )
                    .addGuidePage(GuidePage.newInstance()
                            //.addHighLight(tvBottom)
                            .setLayoutRes(R.layout.guide_camera)
                            .setBackgroundColor(getResources().getColor(R.color.trans_backgroud))
                            .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                @Override
                                public void onLayoutInflated(View view, final Controller controller) {
                                    ImageView cobox = (ImageView) view.findViewById(R.id.cobox);
                                    cobox.setImageDrawable(getResources().getDrawable(R.drawable.guide_camera_vertical));
                                    ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    layoutParams.topToTop = 0;
                                    layoutParams.bottomToBottom = 0;
                                    layoutParams.leftToLeft = 0;
                                    layoutParams.rightToRight = 0;
                                    cobox.setLayoutParams(layoutParams);
                                    cobox.setVisibility(View.VISIBLE);
                                }
                            })
                    ).show();
        }
    }

    @Override
    public void setMotorStatus(int status) {

    }
}


