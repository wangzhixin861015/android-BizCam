package com.bcnetech.hyphoto.ui.activity.camera;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Size;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.bean.CameraParm;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.EventStatisticsUtil;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.EventCommon;
import com.bcnetech.hyphoto.data.CameraParamType;
import com.bcnetech.hyphoto.data.PresetItem;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.PresetParmsSqlControl;
import com.bcnetech.hyphoto.databinding.SurfviewCameraLayoutBinding;
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
import com.bcnetech.hyphoto.ui.view.BlueToothListCamera2View;
import com.bcnetech.hyphoto.ui.view.CameraBottomView;
import com.bcnetech.hyphoto.ui.view.CameraParamAdjustView;
import com.bcnetech.hyphoto.ui.view.CameraSettingNewView;
import com.bcnetech.hyphoto.ui.view.CoBoxChooseTopView;
import com.bcnetech.hyphoto.ui.view.FocusView;
import com.bcnetech.hyphoto.utils.DeviceUtils;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.bizcamerlibrary.camera.data.CameraSettings;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCamerLoaderBase;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCameraLoader;
import com.bcnetech.bizcamerlibrary.camera.utils.CameraHelper;
import com.bcnetech.bluetoothlibarary.bluetoothagreement.CommendManage;
import com.bcnetech.hyphoto.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

import static android.view.View.VISIBLE;

/**
 * Created by wenbin on 2017/2/24.
 */
public class SurfViewCameraActivity extends BaseMvpActivity<ISurfViewCameraView, SurfViewCameraPresenter> implements ISurfViewCameraView, PresetHorizontalListAdapter.PresetHolderInterFace, PresetScanListAdapter.PresetHolderInterFace {
    private static final String QRCode = "qrcode";
    private SurfviewCameraLayoutBinding surfviewCameraLayoutBinding;
    private GPUImageCameraLoader mCamera;
    private boolean isShowRect = false;
    private PresetParmsSqlControl presetParmsSqlControl;
    private List<PresetItem> presetItems;
    private List<PresetItem> presetScanItems;
    private HashMap<String, String> hashMap;
    private TranslateAnimation animation;
    private boolean canClick = true;
    private boolean isQROn = false;
    private boolean isScanON = false;
    private RelativeLayout.LayoutParams fake_param, camer_adjust_param;
    private int bottom_height;
    //能否连续滑动
    private boolean canMove = true;
    //选中的预设参数下标
    private int selectedPresetPosition = 0;
    //最大转过的角度
    private int MAX_ANGLE = 90;
    private int MAX_LIGHT = 255;
    private boolean isAniming = false;
    private int currentType = GPUImageCameraLoader.CAMER_TYPE;
    public static final int REQUESE = 11;
    private boolean canTouch = true;
    private boolean isRecording = false;
    private Rect rect;
    private Size size11;

    int h;
    int w;

    //对焦区域边界
    private int left;
    private int top;
    private int right;
    private int bottom;

    private int cameraType;

    //自动
    public static final int TYPE_AUTO = 0;
    //手动模式
    public static final int TYPE_M = 1;
    //预设参数
    public static final int TYPE_PRESET = 2;
    //专业模式
    public static final int TYPE_PRO = 3;
    //灯光调整
    public static final int TYPE_PRO_L = 4;
    //参数调整
    public static final int TYPE_PRO_M = 5;

    private HideControl hideControl;


    private TextureView.SurfaceTextureListener mSurfaceTextureListener = null;

    //曝光补偿滑动条值
    private int mScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
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
        if (keyCode == KeyEvent.KEYCODE_BACK && surfviewCameraLayoutBinding.coBoxChooseTopView.getTopViewType() == CoBoxChooseTopView.TYPE_PRO) {
            outPro();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && surfviewCameraLayoutBinding.coBoxChooseTopView.getTopViewType() == CoBoxChooseTopView.TYPE_CAMERA) {
            outVedio();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && surfviewCameraLayoutBinding.coBoxChooseTopView.getTopViewType() == CoBoxChooseTopView.TYPE_PRESET) {
            outPreset();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && surfviewCameraLayoutBinding.blueToothListView.getVisibility() == VISIBLE) {
            surfviewCameraLayoutBinding.blueToothListView.close();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && surfviewCameraLayoutBinding.coBoxChooseTopView.getTopViewType() == CoBoxChooseTopView.TYPE_PRESET_CAMERA) {
            outPreset();
            return true;
        } else if (canClick == false) {
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isRecording)
            canClick = true;
        surfviewCameraLayoutBinding.cameraSettingBtn.setAlpha(1f);
        surfviewCameraLayoutBinding.cameraSettingBtn.setEnabled(true);
        surfviewCameraLayoutBinding.coBoxChooseTopView.setCoboxConnectClickEnabled(true);
        surfviewCameraLayoutBinding.cameraBottomView.onResume();
        if (surfviewCameraLayoutBinding.blueToothListView.getVisibility() == VISIBLE) {
            return;
        }
        if (currentType == GPUImageCameraLoader.VIDE0_TYPE) {
            setUpCamera(false);
            if (CameraStatus.getCameraStatus().getRecordSize() != null)
                mCamera.setRecordSize(CameraStatus.getCameraStatus().getRecordSize());
            // mCamera.prepareRecord();
            //在录像模式下且支持1：1录像，切换比例为真实1：1
            if (CameraStatus.getCameraStatus().getPictureRatio() == CameraStatus.Size.TYPE_11) {
                if (surfviewCameraLayoutBinding.cameraSettingView.isSupportSquareVideo()) {
                    w = ContentUtil.getScreenWidth(SurfViewCameraActivity.this) - 2 * ContentUtil.dip2px(SurfViewCameraActivity.this, 25);
                    hashMap.put(Flag.PREVIEW, CameraHelper.PREVIEW_11 + "");
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(w, w);
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    surfviewCameraLayoutBinding.focusview.setLayoutParams(layoutParams);
                    surfviewCameraLayoutBinding.fakeView.setVisibility(VISIBLE);
                    surfviewCameraLayoutBinding.fakeView.setBackgroundColor(Color.BLACK);
                    setProLayout(bottom_height);
                    surfviewCameraLayoutBinding.cameraTexture.setLayoutParams(layoutParams);
                } else {
                    hashMap.put(Flag.PREVIEW, CameraHelper.PREVIEW_43 + "");
                    h = ContentUtil.getScreenHeight2(SurfViewCameraActivity.this) - ContentUtil.dip2px(SurfViewCameraActivity.this, 60) - ContentUtil.dip2px(SurfViewCameraActivity.this, 70) - ContentUtil.dip2px(SurfViewCameraActivity.this, 90);
                    w = h * 3 / 4;
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(w, h);
                    layoutParams.addRule(RelativeLayout.BELOW, surfviewCameraLayoutBinding.titleLayout.getId());
                    layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    surfviewCameraLayoutBinding.focusview.setLayoutParams(layoutParams);
                    surfviewCameraLayoutBinding.fakeView.setVisibility(View.GONE);
                    bottom_height = ContentUtil.dip2px(SurfViewCameraActivity.this, 60);
                    setProLayout(bottom_height);
                    surfviewCameraLayoutBinding.cameraTexture.setLayoutParams(layoutParams);
                }
            }
        } else {
            if (!surfviewCameraLayoutBinding.cameraTexture.isAvailable()) {
                mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {

                    @Override
                    public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
                        //mCamera.openCamera();
                        setUpCamera(true);

                    }

                    @Override
                    public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
                        return true;
                    }

                    @Override
                    public void onSurfaceTextureUpdated(SurfaceTexture texture) {
                    }

                };

                surfviewCameraLayoutBinding.cameraTexture.setSurfaceTextureListener(mSurfaceTextureListener);
            } else {
                mCamera.onResume();

            }
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
        if (surfviewCameraLayoutBinding.blueToothListView.getVisibility() == VISIBLE) {
            surfviewCameraLayoutBinding.blueToothListView.destroy();
        } else {
            mCamera.onPause();
        }
        if (isRecording) {
            surfviewCameraLayoutBinding.cameraBottomView.resetVideo();
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        // BlueToothLoad.getInstance().closeBlueTooth(this);
        mCamera.onDestroy();
        mCamera = null;
        CameraSettings.onDestroy();
        surfviewCameraLayoutBinding.blueToothListView.destroy();
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
        surfviewCameraLayoutBinding = DataBindingUtil.setContentView(this, R.layout.surfview_camera_layout);
    }

    @Override
    protected void initData() {
        if (hashMap == null) {
            hashMap = new HashMap<String, String>();
        }
        cameraType = TYPE_AUTO;
        //setLight(this, MAX_LIGHT);
        surfviewCameraLayoutBinding.cameraSettingView.supportCamera2(true);
        presenter.setLightRationListData(SurfViewCameraActivity.this, surfviewCameraLayoutBinding.cameraParamAdjustView.getRecycler_lightParam());

        surfviewCameraLayoutBinding.cameraParamAdjustView.setClickShowType(CameraParamAdjustView.TYPE_AUTO);
        surfviewCameraLayoutBinding.cameraParamAdjustView.setCamera2Support(LoginedUser.getLoginedUser().isSupportCamera2());
        surfviewCameraLayoutBinding.cameraTexture.setVisibility(View.VISIBLE);
        //surfaceView.setVisibility(View.GONE);
        //mCamera = new GPUImageCameraLoader2(SurfViewCameraActivity.this, cameratexture);
        mCamera = new GPUImageCameraLoader(SurfViewCameraActivity.this);


        rect = new Rect();
        class CameraLoaderListener implements GPUImageCamerLoaderBase.CameraLoaderListener {
            @Override
            public void isCamera2Support(boolean isc2Support) {
            }
        }
        mCamera.setCameraLoaderListener(new CameraLoaderListener());
        surfviewCameraLayoutBinding.cameraParamAdjustView.setPresetInterface(this);
        surfviewCameraLayoutBinding.cameraParamAdjustView.setScanInterface(this);
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
                surfviewCameraLayoutBinding.cameraSettingView.setAllSize(mCamera.getAllSize());
                surfviewCameraLayoutBinding.cameraSettingView.setPreSize(sizes);
            }
        });
        presenter.initRenderer();

        animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation
                .RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.9f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);

       /* mCompressor = new Compressor(this);
        mCompressor.loadBinary(new InitListener() {
            @Override
            public void onLoadSuccess() {
                Log.d("ffmpeg:", "loadSuccess");
            }

            @Override
            public void onLoadFail(String reason) {
                Log.d("ffmpeg:", "loadFail " + reason);
            }
        });
*/
        hideControl = new HideControl();
        surfviewCameraLayoutBinding.cameraBottomView.setActivity(this);
        surfviewCameraLayoutBinding.cameraBottomView.setCamera2Support(LoginedUser.getLoginedUser().isSupportCamera2());
    }

    private void setUpCamera(boolean isInit) {
        /*if (mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_OPENING || mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_CLOSING) {
            return;
        }*/
        hashMap.put(Flag.FLASH, CameraStatus.getCameraStatus().isFlashOn() + "");
        setCameraPerViewSize(CameraStatus.getCameraStatus().getPictureRatio(), isInit);
        mCamera.setRecordSize(CameraStatus.getCameraStatus().getRecordSize());
        if (CameraStatus.getCameraStatus().getRecordTime() != CameraStatus.Size.RECORD_CUSTOM) {
            surfviewCameraLayoutBinding.cameraBottomView.setRecordTime(CameraStatus.getCameraStatus().getRecordTime().getIndex());
        } else {
            surfviewCameraLayoutBinding.cameraBottomView.setRecordTime(CameraStatus.getCameraStatus().getCostomRecordTime());
        }
    }

    @Override
    public void startQueryByShowType() {
        String mtype = "android";
        presetParmsSqlControl.startQueryByShowType(mtype);
    }

    @Override
    public void setCameraType(int cameraType) {
        this.cameraType = cameraType;
    }

    @Override
    public int getCameraType() {
        return cameraType;
    }

    /**
     * 16：9-->使用1920：1080预览尺寸
     * 4：3 -->使用1600：1200预览尺寸
     * 1：1 -->在4：3尺寸上遮盖，达到1：1
     *
     * @param size
     */
    private void setCameraPerViewSize(CameraStatus.Size size, boolean isInit) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ContentUtil.getScreenWidth(SurfViewCameraActivity.this), ContentUtil.getScreenWidth(SurfViewCameraActivity.this));
        switch (size) {
            case TYPE_11:
                if (currentType == GPUImageCameraLoader.VIDE0_TYPE) {
                    if (CameraStatus.getCameraStatus().getPictureRatio() == CameraStatus.Size.TYPE_11) {
                        if (!surfviewCameraLayoutBinding.cameraSettingView.isSupportSquareVideo()) {
                            //当前状态为录像且设备并不支持1：1时，录像界面设置3：4
                            hashMap.put(Flag.PREVIEW, CameraHelper.PREVIEW_43 + "");
                            h = ContentUtil.getScreenHeight2(SurfViewCameraActivity.this) - ContentUtil.dip2px(SurfViewCameraActivity.this, 60) - ContentUtil.dip2px(SurfViewCameraActivity.this, 70) - ContentUtil.dip2px(SurfViewCameraActivity.this, 90);
                            w = h * 3 / 4;
                            layoutParams = new RelativeLayout.LayoutParams(w, h);
                            layoutParams.addRule(RelativeLayout.BELOW, surfviewCameraLayoutBinding.titleLayout.getId());
                            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            surfviewCameraLayoutBinding.focusview.setLayoutParams(layoutParams);
                            surfviewCameraLayoutBinding.fakeView.setVisibility(View.GONE);
                            bottom_height = ContentUtil.dip2px(SurfViewCameraActivity.this, 60);
                            setProLayout(bottom_height);
                            isShowRect = false;
                            right = w;
                            bottom = w;
                            rect.set(left, top, right, bottom);
                            surfviewCameraLayoutBinding.cameraTexture.setLayoutParams(layoutParams);
                            break;
                        } else {
                            //当前状态为录像且设备并支持1：1时，录像界面设置1：1
                            w = ContentUtil.getScreenWidth(SurfViewCameraActivity.this) - 2 * ContentUtil.dip2px(SurfViewCameraActivity.this, 25);
                            hashMap.put(Flag.PREVIEW, CameraHelper.PREVIEW_11 + "");
                            bottom_height = w * 4 / 3 - w;
                            surfviewCameraLayoutBinding.fakeView.setVisibility(VISIBLE);
                            surfviewCameraLayoutBinding.fakeView.setBackgroundColor(Color.BLACK);
                            fake_param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, w * 4 / 3 - w);
                            fake_param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            surfviewCameraLayoutBinding.fakeView.setLayoutParams(fake_param);
                            setProLayout(bottom_height);
                            layoutParams = new RelativeLayout.LayoutParams(w, w);
                            layoutParams.addRule(RelativeLayout.ABOVE, surfviewCameraLayoutBinding.fakeView.getId());
                            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            surfviewCameraLayoutBinding.focusview.setLayoutParams(layoutParams);
                            surfviewCameraLayoutBinding.cameraTexture.setLayoutParams(layoutParams);
                            break;
                        }
                    }
                }
                hashMap.put(Flag.PREVIEW, CameraHelper.PREVIEW_43 + "");
                w = ContentUtil.getScreenWidth(this) - 2 * ContentUtil.dip2px(SurfViewCameraActivity.this, 25);
                layoutParams = new RelativeLayout.LayoutParams(w, w * 4 / 3);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                surfviewCameraLayoutBinding.focusview.setLayoutParams(layoutParams);
                surfviewCameraLayoutBinding.fakeView.setVisibility(VISIBLE);
                surfviewCameraLayoutBinding.fakeView.setBackgroundColor(Color.BLACK);
                bottom_height = w * 4 / 3 - w;
                fake_param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, w * 4 / 3 - w);
                fake_param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                surfviewCameraLayoutBinding.fakeView.setLayoutParams(fake_param);
                setProLayout(bottom_height);
                isShowRect = true;
                left = 0;
                top = 0;
                right = w;
                bottom = w;
                rect.set(left, top, right, bottom);
                surfviewCameraLayoutBinding.cameraTexture.setLayoutParams(layoutParams);
                break;
            case TYPE_34:
                hashMap.put(Flag.PREVIEW, CameraHelper.PREVIEW_43 + "");
                h = ContentUtil.getScreenHeight2(SurfViewCameraActivity.this) - ContentUtil.dip2px(SurfViewCameraActivity.this, 60) - ContentUtil.dip2px(SurfViewCameraActivity.this, 70) - ContentUtil.dip2px(SurfViewCameraActivity.this, 90);
                w = h * 3 / 4;
                layoutParams = new RelativeLayout.LayoutParams(w, h);
                layoutParams.addRule(RelativeLayout.BELOW, surfviewCameraLayoutBinding.titleLayout.getId());
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                surfviewCameraLayoutBinding.focusview.setLayoutParams(layoutParams);
                surfviewCameraLayoutBinding.fakeView.setVisibility(View.GONE);
                bottom_height = ContentUtil.dip2px(SurfViewCameraActivity.this, 60);
                setProLayout(bottom_height);
                isShowRect = false;
                right = w;
                bottom = w;
                rect.set(left, top, right, bottom);
                surfviewCameraLayoutBinding.cameraTexture.setLayoutParams(layoutParams);
                break;
            case TYPE_916:
                hashMap.put(Flag.PREVIEW, CameraHelper.PREVIEW_169 + "");
                h = ContentUtil.getScreenHeight2(SurfViewCameraActivity.this) - ContentUtil.dip2px(SurfViewCameraActivity.this, 60) - ContentUtil.dip2px(SurfViewCameraActivity.this, 70) - ContentUtil.dip2px(SurfViewCameraActivity.this, 90);
                w = h * 9 / 16;
                layoutParams = new RelativeLayout.LayoutParams(w, h);
                layoutParams.addRule(RelativeLayout.BELOW, surfviewCameraLayoutBinding.titleLayout.getId());
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                surfviewCameraLayoutBinding.focusview.setLayoutParams(layoutParams);
                surfviewCameraLayoutBinding.fakeView.setVisibility(View.GONE);
                bottom_height = ContentUtil.dip2px(SurfViewCameraActivity.this, 60);
                setProLayout(bottom_height);
                isShowRect = false;
                right = w;
                bottom = w;
                rect.set(left, top, right, bottom);
                surfviewCameraLayoutBinding.cameraTexture.setLayoutParams(layoutParams);
                break;
        }
        switchCamera();
        hashMap.put(Flag.PICSIZE + "", CameraStatus.getCameraStatus().getPictureSize().getIndex() + "");
      /*  if (currentSelectSize == null) {
            currentSelectSize = size;
            mCamera.openCamera(hashMap);
        } else {
            cameratexture.setVisibility(View.GONE);
            mCamera.openCamera(hashMap);
            currentSelectSize = size;
            cameratexture.setVisibility(VISIBLE);
            // mCamera.setCameraParams(hashMap);
        }*/
        //单独设置视频比例，比例保存在CameraStatus的recordSize中
        if (currentType == GPUImageCameraLoader.VIDE0_TYPE) {
            hashMap.put(Flag.PREVIEW, "0");
        }
        mCamera.openCamera(hashMap, surfviewCameraLayoutBinding.cameraTexture);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW, R.id.top_layout);
        params.addRule(RelativeLayout.ABOVE, R.id.camera_adjust_layout);
//        surfviewCameraLayoutBinding.scaleView.setLayoutParams(params);
//        surfviewCameraLayoutBinding.verticalScaleView.setLayoutParams(params);
//        surfviewCameraLayoutBinding.verticalFocusView.setLayoutParams(params);

        if (isInit) {
            presenter.setCameraParamMListData(SurfViewCameraActivity.this, surfviewCameraLayoutBinding.cameraParamAdjustView.getRecycler_cameraParam());
        }
        if (CameraStatus.getCameraStatus().isSubLineOn()) {
            showDottedLine();
        } else {
            dismissDottedLine();
        }
    }

    @Override
    public void setCameraParams(HashMap<String, String> hashMap) {
        //this.hashMap = hashMap;
        mCamera.setCameraParams(hashMap);

    }

    @Override
    public void lockAllCameraParam() {
        mCamera.lockAllCameraParam();
    }

    @Override
    public void unlockAllCameraParam() {
        mCamera.unlockAllCameraParam();
    }

    private void setProLayout(int height) {
        camer_adjust_param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        camer_adjust_param.addRule(RelativeLayout.ABOVE, surfviewCameraLayoutBinding.bottomControl.getId());
        camer_adjust_param.addRule(RelativeLayout.CENTER_IN_PARENT);
        surfviewCameraLayoutBinding.cameraAdjustLayout.setLayoutParams(camer_adjust_param);
        presenter.cameraParamNotifyDataSetChanged();
    }


    @Override
    protected void onViewClick() {

        surfviewCameraLayoutBinding.fakeView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        surfviewCameraLayoutBinding.cameraSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surfviewCameraLayoutBinding.cameraSettingView.setAllSize(mCamera.getAllSize());
                surfviewCameraLayoutBinding.cameraSettingView.setPreSize(mCamera.getPreSize());
                surfviewCameraLayoutBinding.cameraSettingView.show();
                surfviewCameraLayoutBinding.cameraCloseBtn.setEnabled(false);

            }
        });


        surfviewCameraLayoutBinding.cameraCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canClick) {
                    if (mCamera != null) {
                        if (mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_CLOSING || mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_OPENING) {
                            return;
                        }
                    }
                    finishView(Flag.NULLCODE, null);
                }
            }
        });


        surfviewCameraLayoutBinding.cameraParamAdjustView.setCameraParamAdjustInter(new CameraParamAdjustView.CameraParamAdjustInter() {
            @Override
            public void onScaleScroll(int scale) {
                if (mScale == scale) {
                    return;
                }
                mScale = scale;
                float currentAeNum = (presenter.getMaxNumAe() / (float) surfviewCameraLayoutBinding.cameraParamAdjustView.getMax() * scale);
                mCamera.setAe((int) currentAeNum);
            }
        });

        /**
         * 加曝光
         */
        surfviewCameraLayoutBinding.cameraParamAdjustView.setAddListner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScale == surfviewCameraLayoutBinding.cameraParamAdjustView.getMax()) {
                    return;
                }
                mScale++;
                surfviewCameraLayoutBinding.cameraParamAdjustView.scrollToScale(mScale);
                float currentAeNum = (presenter.getMaxNumAe() / (float) surfviewCameraLayoutBinding.cameraParamAdjustView.getMax() * mScale);
                mCamera.setAe((int) currentAeNum);
            }
        });

        /**
         * 减曝光
         */
        surfviewCameraLayoutBinding.cameraParamAdjustView.setReduceListner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScale == surfviewCameraLayoutBinding.cameraParamAdjustView.getMin()) {
                    return;
                }
                mScale--;
                surfviewCameraLayoutBinding.cameraParamAdjustView.scrollToScale(mScale);
                float currentAeNum = (presenter.getMaxNumAe() / (float) surfviewCameraLayoutBinding.cameraParamAdjustView.getMax() * mScale);
                mCamera.setAe((int) currentAeNum);
            }
        });


        surfviewCameraLayoutBinding.cameraSettingView.setCameraSettingInter(new CameraSettingNewView.CameraSettingInter() {
            @Override
            public void onClose(final CameraStatus cameraStatus) {
                canClick = false;
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (hashMap == null) {
                            hashMap = new HashMap<String, String>();
                        }
                        hashMap.put(Flag.FLASH, cameraStatus.isFlashOn() + "");
                        setCameraPerViewSize(cameraStatus.getPictureRatio(), false);
                        if (cameraStatus.isSubLineOn()) {
                            showDottedLine();
                        } else {
                            dismissDottedLine();
                        }
                        if (cameraStatus.getRecordTime() != CameraStatus.Size.RECORD_CUSTOM) {
                            surfviewCameraLayoutBinding.cameraBottomView.setRecordTime(cameraStatus.getRecordTime().getIndex());
                        } else {
                            surfviewCameraLayoutBinding.cameraBottomView.setRecordTime(cameraStatus.getCostomRecordTime());
                        }
                        surfviewCameraLayoutBinding.cameraCloseBtn.setEnabled(true);
                        mCamera.setRecordSize(cameraStatus.getRecordSize());
                        if (currentType == GPUImageCameraLoader.VIDE0_TYPE) {
                            // mCamera.prepareRecord();
                        }
                    }
                }, 200);

            }

            @Override
            public void onFlashClick(boolean isflashon) {
                mCamera.setFlash(isflashon);
            }

            @Override
            public void onWaterMarkClick() {
                WaterMarkPresenter.startAction(SurfViewCameraActivity.this,REQUESE,false);
                //startActivityForResult(new Intent(SurfViewCameraActivity.this, WaterMarkSettingActivity.class), REQUESE);

            }

            @Override
            public void onPreRatioNotify(int preType) {
                mCamera.notifyPreRatio(preType);
            }
        });

        surfviewCameraLayoutBinding.cameraBottomView.setCameraBottomViewInter(new CameraBottomView.CameraBottomViewInter() {
            @Override
            public void onCameraClick() {
                if (canClick) {
                    if (mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_CLOSING || mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_OPENING) {
                        return;
                    }
                    EventStatisticsUtil.event(SurfViewCameraActivity.this, EventCommon.CAMERA_TAKEPHOTO);

                    takePhotoAnimation();
                    mCamera.takePhoto();

                }
            }

            @Override
            public void onChangeClick(int type) {
                surfviewCameraLayoutBinding.cameraSettingView.setAllSize(mCamera.getAllSize());
                currentType = type;
                // if (CameraStatus.getCameraStatus().getRecordSize() == null)
                surfviewCameraLayoutBinding.cameraSettingView.setPreSize(mCamera.getPreSize());
                setUpCamera(false);
                if (currentType == GPUImageCameraLoader.VIDE0_TYPE) {
                    //surfviewCameraLayoutBinding.cameraSettingView.setPreSize(mCamera.getPreSize());
                    mCamera.setRecordSize(CameraStatus.getCameraStatus().getRecordSize());
                    // mCamera.prepareRecord();
                   /* if (CameraStatus.getCameraStatus().getPictureRatio() == CameraStatus.Size.TYPE_11) {
                        if (surfviewCameraLayoutBinding.cameraSettingView.isSupportSquareVideo()) {
                            w = ContentUtil.getScreenWidth(SurfViewCameraActivity.this) - 2 * ContentUtil.dip2px(SurfViewCameraActivity.this, 25);
                            hashMap.put(Flag.PREVIEW, CameraHelper.PREVIEW_11 + "");
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(w, w);
                            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                            surfviewCameraLayoutBinding.focusview.setLayoutParams(layoutParams);
                            surfviewCameraLayoutBinding.fakeView.setVisibility(VISIBLE);
                            surfviewCameraLayoutBinding.fakeView.setBackgroundColor(Color.BLACK);
                            setProLayout(bottom_height);
                            surfviewCameraLayoutBinding.cameraTexture.setLayoutParams(layoutParams);
                        } else {
                            hashMap.put(Flag.PREVIEW, CameraHelper.PREVIEW_43 + "");
                            h = ContentUtil.getScreenHeight2(SurfViewCameraActivity.this) - ContentUtil.dip2px(SurfViewCameraActivity.this, 60) - ContentUtil.dip2px(SurfViewCameraActivity.this, 70) - ContentUtil.dip2px(SurfViewCameraActivity.this, 90);
                            w = h * 3 / 4;
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(w, h);
                            layoutParams.addRule(RelativeLayout.BELOW, surfviewCameraLayoutBinding.titleLayout.getId());
                            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            surfviewCameraLayoutBinding.focusview.setLayoutParams(layoutParams);
                            surfviewCameraLayoutBinding.fakeView.setVisibility(View.GONE);
                            bottom_height = ContentUtil.dip2px(SurfViewCameraActivity.this, 60);
                            setProLayout(bottom_height);
                            surfviewCameraLayoutBinding.cameraTexture.setLayoutParams(layoutParams);

                        }
                    }*/
                }

            }


            @Override
            public void onStatusClick() {
                if (!canClick)
                    return;
                if (mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_CLOSING || mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_OPENING) {
                    return;
                }
                presenter.lockAllCameraParam();
                cameraType = TYPE_M;
                surfviewCameraLayoutBinding.cameraParamAdjustView.setClickShowType(CameraParamAdjustView.TYPE_M);
                presenter.itemClick();
            }

            @Override
            public void onVideoClick() {
                if (mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_CLOSING || mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_OPENING || mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_CLOSED) {
                    return;
                }
                if (canClick) {
                    canClick = false;
                    mCamera.prepareRecordModel();
                    // mCamera.prepareRecord();
                    surfviewCameraLayoutBinding.cameraSettingBtn.setEnabled(false);
                    surfviewCameraLayoutBinding.cameraSettingBtn.setAlpha(0.5f);
                    surfviewCameraLayoutBinding.coBoxChooseTopView.setCoboxConnectClickEnabled(false);
                }

            }

            @Override
            public void onCountDownFin() {
                canClick = false;
                isRecording = true;
               /* if (camera_setting_view.isSupportSquareVideo() && CameraStatus.getCameraStatus().getPictureRatio() == CameraStatus.Size.TYPE_11) {
                    w = ContentUtil.getScreenWidth(SurfViewCameraActivity.this) - 2 * ContentUtil.dip2px(SurfViewCameraActivity.this, 25);
                    hashMap.put(Flag.PREVIEW, CameraHelper.PREVIEW_11 + "");
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(w, w);
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    focusView.setLayoutParams(layoutParams);
                    fake_view.setVisibility(VISIBLE);
                    fake_view.setBackgroundColor(Color.BLACK);
                    setProLayout(bottom_height);
                    cameratexture.setLayoutParams(layoutParams);
                }*/
                mCamera.startRecoder();
            }

            @Override
            public void onVideoFin() {
                isRecording = false;
                surfviewCameraLayoutBinding.cameraSettingBtn.setAlpha(1f);
                surfviewCameraLayoutBinding.cameraSettingBtn.setEnabled(true);
                surfviewCameraLayoutBinding.coBoxChooseTopView.setCoboxConnectClickEnabled(true);
                mCamera.endRecording();
                showDialog();
                Bitmap b = getVideoBitmap(mCamera.getVidioUrl());
                SavePicTask savePicTask = new SavePicTask(b, false);
                savePicTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onVideoPause(boolean ispause) {
                /*if (ispause) {
                    mCamera.pauseRecording();
                } else {
                    mCamera.ResumeRecording();
                }*/
            }

            @Override
            public void onPreset() {
                if (!canClick)
                    return;
                if (mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_CLOSING || mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_OPENING) {
                    return;
                }

                cameraType = TYPE_PRESET;
                surfviewCameraLayoutBinding.cameraParamAdjustView.setClickShowType(CameraParamAdjustView.TYPE_PRESET);


            }

            @Override
            public void onPro() {
                if (!canClick)
                    return;
                if (mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_CLOSING || mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_OPENING)
                    return;
                if (selectedPresetPosition == 0 || selectedPresetPosition == 1) {
                    presenter.lockAllCameraParam();
                }
                cameraType = TYPE_PRO;
                surfviewCameraLayoutBinding.cameraParamAdjustView.setClickShowType(CameraParamAdjustView.TYPE_PRO);
                presenter.itemClick();
            }

            @Override
            public void onAutoCamera() {
                if (!canClick)
                    return;
                if (mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_CLOSING || mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_OPENING) {
                    return;
                }
                cameraType = TYPE_AUTO;

                surfviewCameraLayoutBinding.cameraParamAdjustView.setClickShowType(CameraParamAdjustView.TYPE_AUTO);

                presenter.unLockCameraParam("auto");
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
                presetItems.add(new PresetItem("无", "", ""));
                //商拍光影推荐入口
                // presetItems.add(new PresetItem(getResources().getString(R.string.none), "scan", ""));
                for (int i = 0; i < presetParms.size(); i++) {
                    if (null != presetParms.get(i).getLightRatioData() && presetParms.get(i).getLightRatioData().getVersion().equals(
                            String.valueOf(CommendManage.getInstance().getVersion()))) {
                        PresetItem presetItem = new PresetItem(presetParms.get(i).getName(), presetParms.get(i).getTextSrc(), presetParms.get(i).getPresetId());
                        presetItem.setPresetParm(presetParms.get(i));
                        presetItems.add(presetItem);
                    }
                }
//                presetItems.add(new PresetItem("help", "help",""));
                surfviewCameraLayoutBinding.cameraParamAdjustView.addData(presetItems);
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

        surfviewCameraLayoutBinding.blueToothListView.setCloseConnecrtion(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                presenter.choiceDeivce(presenter.getSelectPosition());
            }
        });

        /**
         * 关闭蓝牙选择界面
         */
        surfviewCameraLayoutBinding.coBoxChooseTopView.setDownLeftBtnClick(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (surfviewCameraLayoutBinding.coBoxChooseTopView.getTopViewType() == CoBoxChooseTopView.TYPE_CAMERA) {
                    outVedio();
                } else {
                    outPro();
                }
            }
        });

        /**
         * 跳转到预设参数市场
         */
        surfviewCameraLayoutBinding.coBoxChooseTopView.setMarketLeftBtnClick(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_CLOSING || mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_OPENING) {
                    return;
                }
                if (canClick) {
                    //TemplateActivcity.actionStart(SurfViewCameraActivity.this,-1);
//                    MarketPresetActicity.actionStart(SurfViewCameraActivity.this);
                    EventStatisticsUtil.event(SurfViewCameraActivity.this, EventCommon.CAMERA_PRESET_MARKET);

                    Intent intent = new Intent(SurfViewCameraActivity.this, MarketFragmentActivity.class);
                    startActivity(intent);
//                    MarketFragmentActivity.
                }
            }
        });


        /**
         * 打开蓝牙列表
         */
        surfviewCameraLayoutBinding.coBoxChooseTopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BleConnectModel.getBleConnectModelInstance().isBlueEnable()) {
                    ToastUtil.toast(getResources().getString(R.string.open_bt));
                    return;
                }
                if (BleConnectModel.getBleConnectModelInstance().isCurrentConnect()) {
                    surfviewCameraLayoutBinding.blueToothListView.setConnectBluetoothName(BleConnectModel.getBleConnectModelInstance().getCurrentBlueToothDevice().getName());
                    surfviewCameraLayoutBinding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CONNECTION);
                }
                presenter.startScanBlueTooth();
               /* if (BlueToothLoad.getInstance().blueToothIsClose()) {
                    ToastUtil.toast(getResources().getString(R.string.open_bt));
                    return;
                }*/
                //BlueToothLoad.getInstance().startDiscover();
                mCamera.stopPreview();
                surfviewCameraLayoutBinding.blueToothListView.setCamera(mCamera);
                surfviewCameraLayoutBinding.cameraTexture.setVisibility(View.GONE);
                surfviewCameraLayoutBinding.blueToothListView.setApplyBlur();
                surfviewCameraLayoutBinding.blueToothListView.initParm(presenter.getAdapter(), new BlueToothListCamera2View.BlueToothListInterface() {
                    @Override
                    public void onBlueToothDissmiss(Object... params) {
                        surfviewCameraLayoutBinding.cameraTexture.setVisibility(View.VISIBLE);
                        //mCamera.onResume();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setUpCamera(false);
                            }
                        });

                    }

                    @Override
                    public void onBlueToothClick(Object... params) {
                        presenter.choiceDeivce((int) params[0]);
                        surfviewCameraLayoutBinding.cameraTexture.setVisibility(View.VISIBLE);
                        //mCamera.onResume();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setUpCamera(false);
                            }
                        });
                    }

                    @Override
                    public void onListConnection(Object... params) {

                    }

                    @Override
                    public void onScanConnection(final Object... params) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                surfviewCameraLayoutBinding.blueToothListView.close();
                                String result = (String) params[0];
                                setUpCamera(false);
                                ToastUtil.toast(result);
                                if (result.indexOf("COBOX") >= 0 || result.indexOf("CBEDU") >= 0) {
                                    presenter.scanDevice(result);
                                }
                            }
                        });
                    }
                });

            }
        });

        /**
         * 手势
         */
        surfviewCameraLayoutBinding.focusview.setFouceListener(new FocusView.FouceListener() {

            @Override
            public void endMove(MotionEvent event) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        float x = (float) surfviewCameraLayoutBinding.cameraTexture.getWidth() - (float) event.getRawX();
                           /* if (hashMap == null) {
                                hashMap = new HashMap<String, String>();
                            }
                            hashMap.put(Flag.FOCUS, Flag.FOCUS_CONTINUOUS);
                            mCamera.setCameraParams(hashMap);*/
//                        mCamera.pointFocus(x / (float) surfviewCameraLayoutBinding.cameraTexture.getWidth(), ((float) event.getRawY() / (float) surfviewCameraLayoutBinding.cameraTexture.getHeight()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDown(MotionEvent motionEvent) {
                if (!canClick)
                    return;
                if (mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_CLOSING || mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_OPENING) {
                    return;
                }
                if (cameraType == TYPE_M) {
                    int selectedParamNum = presenter.getSelectedParamNum();
                    if (selectedParamNum >= 0 && selectedParamNum < 6) {
                        presenter.getParamNum();
                    }
                }

                if (cameraType == TYPE_PRO_M) {
                    int selectedParamNum = presenter.getSelectedParamNum();
                    if (selectedParamNum >= 0 && selectedParamNum < 6) {
                        presenter.getParamNum();
                    }
                }


                if (cameraType == TYPE_PRO_L) {
                    int selectedLightNum = presenter.getSelectedLightNum();
                    if (selectedLightNum >= 0 && selectedLightNum < 6) {
                        presenter.getLightNum();
                    }
                }

                if (cameraType == TYPE_AUTO) {
                }

            }

            @Override
            public void onStartUpDown(MotionEvent motionEvent) {
                if (!canClick)
                    return;
                if (mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_CLOSING || mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_OPENING) {
                    return;
                }
                if (cameraType == TYPE_M) {
                    int selectedParamNum = presenter.getSelectedParamNum();
                    if (selectedParamNum >= 0 && selectedParamNum < 6) {
                        surfviewCameraLayoutBinding.scaleView.setVisibility(VISIBLE);
                    }
                }

                if (cameraType == TYPE_PRO_M) {
                    int selectedParamNum = presenter.getSelectedParamNum();
                    if (selectedParamNum >= 0 && selectedParamNum < 6) {
                        surfviewCameraLayoutBinding.scaleView.setVisibility(VISIBLE);
                    }
                }
                if (cameraType == TYPE_PRO_L) {
                    int selectedLightNum = presenter.getSelectedLightNum();
                    if (selectedLightNum >= 0 && selectedLightNum < 6) {
                        surfviewCameraLayoutBinding.scaleView.setVisibility(VISIBLE);
                    }
                }


            }

            @Override
            public void onUpDown(MotionEvent motionEvent, float addNum) {
                if (!canClick)
                    return;
                if (mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_CLOSING || mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_OPENING) {
                    return;
                }
                if (cameraType == TYPE_M) {
                    int selectedParamNum = presenter.getSelectedParamNum();
                    if (selectedParamNum >= 0 && selectedParamNum < 6) {
                        presenter.changeCameraParam((int) addNum);
                    }
                }

                if (cameraType == TYPE_PRO_M) {
                    int selectedParamNum = presenter.getSelectedParamNum();
                    if (selectedParamNum >= 0 && selectedParamNum < 6) {
                        presenter.changeCameraParam((int) addNum);
                    }
                }

                if (cameraType == TYPE_PRO_L) {
                    int selectedLightNum = presenter.getSelectedLightNum();
                    if (selectedLightNum >= 0 && selectedLightNum < 6) {
                        presenter.changeLightNew((int) addNum);
                    }
                }
//
            }

            @Override
            public void onEndUpDown(MotionEvent motionEvent) {
                if (!canClick)
                    return;
                if (mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_CLOSING || mCamera.getCamera_open_state() == GPUImageCameraLoader.CameraOpenState.CAMERAOPENSTATE_OPENING) {
                    return;
                }
                if (cameraType == TYPE_M) {
                    int selectedParamNum = presenter.getSelectedParamNum();
                    if (selectedParamNum >= 0 && selectedParamNum < 6) {

                        surfviewCameraLayoutBinding.scaleView.setVisibility(View.GONE);
                        presenter.setItemCameraParamNum();
                    }
                }

                if (cameraType == TYPE_PRO_M) {
                    int selectedParamNum = presenter.getSelectedParamNum();
                    if (selectedParamNum >= 0 && selectedParamNum < 6) {
                        surfviewCameraLayoutBinding.scaleView.setVisibility(View.GONE);
                        presenter.setItemCameraParamNum();
                    }
                }

                if (cameraType == TYPE_PRO_L) {
                    int selectedLightNum = presenter.getSelectedLightNum();
                    if (selectedLightNum >= 0 && selectedLightNum < 6) {
                        surfviewCameraLayoutBinding.scaleView.setVisibility(View.GONE);
                        presenter.setItemLightNum();
                    }
                }


            }


            @Override
            public void onClick(MotionEvent event) {
                int marginW = (int) event.getX();
                int marginH = (int) event.getY();

                if (cameraType == TYPE_AUTO) {
                    if (canClick) {
                        hideControl.startHideTimer();
                    }
                } else if (cameraType == TYPE_M || cameraType == TYPE_PRO_M) {
                    presenter.unLockFocusCameraParam("auto");
                }


                surfviewCameraLayoutBinding.focusview.setRoundArcListener(new FocusView.RoundArcListener() {
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

//                    focusView.bringToFront();
                    surfviewCameraLayoutBinding.focusview.invalidate();
                    surfviewCameraLayoutBinding.focusview.setCenterX(marginW, marginH, false);
                    surfviewCameraLayoutBinding.focusview.setScaleAnim();
//                    surfviewCameraLayoutBinding.cameraParamAdjustView.reset();
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            float x = (float) surfviewCameraLayoutBinding.cameraTexture.getWidth() - (float) event.getRawX();
                           /* if (hashMap == null) {
                                hashMap = new HashMap<String, String>();
                            }
                            hashMap.put(Flag.FOCUS, Flag.FOCUS_CONTINUOUS);
                            mCamera.setCameraParams(hashMap);*/
                            LogUtil.d(surfviewCameraLayoutBinding.cameraTexture.getWidth() + "aaaaa" + surfviewCameraLayoutBinding.cameraTexture.getHeight());
                            mCamera.pointFocus(event.getX(), event.getY(), surfviewCameraLayoutBinding.cameraTexture.getWidth(), surfviewCameraLayoutBinding.cameraTexture.getHeight());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (surfviewCameraLayoutBinding.coBoxChooseTopView.getTopViewType() == CoBoxChooseTopView.TYPE_PRESET) {
                    outPreset();
                } else if (surfviewCameraLayoutBinding.coBoxChooseTopView.getTopViewType() == CoBoxChooseTopView.TYPE_PRESET_CAMERA) {
                    outVedioInPreset();
                }
            }

            @Override
            public void onLongClick(MotionEvent event) {
                int px = ImageUtil.Dp2Px(SurfViewCameraActivity.this, 60);
                int marginW = (int) event.getX();
                int marginH = (int) event.getY();
//                if (isShowRect && !invideo) {
//                    marginH -= (height - width) / 2;
//                }
                surfviewCameraLayoutBinding.focusview.setRoundArcListener(new FocusView.RoundArcListener() {
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
                    int[] screenLocation = new int[2];
                    surfviewCameraLayoutBinding.focusview.getLocationOnScreen(screenLocation);


//                    focusView.bringToFront();
                    surfviewCameraLayoutBinding.focusview.invalidate();
                    surfviewCameraLayoutBinding.focusview.setCenterX(marginW, marginH, false);
                    surfviewCameraLayoutBinding.focusview.setLongClickScaleAnim();
                    surfviewCameraLayoutBinding.focusview.setFouceWH(rect);
                    surfviewCameraLayoutBinding.cameraParamAdjustView.reset();
                    canTouch = false;
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            float x = (float) surfviewCameraLayoutBinding.cameraTexture.getWidth() - (float) event.getRawX();
                           /* if (hashMap == null) {
                                hashMap = new HashMap<String, String>();
                            }
                            hashMap.put(Flag.FOCUS, Flag.FOCUS_CONTINUOUS);
                            mCamera.setCameraParams(hashMap);*/
                            mCamera.pointFocus(event.getX(), event.getY(), surfviewCameraLayoutBinding.cameraTexture.getWidth(), surfviewCameraLayoutBinding.cameraTexture.getHeight());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onLongClickCancel() {
                isAniming = false;
                surfviewCameraLayoutBinding.focusview.setAniming(false);
            }
        });

        //传感器回调
        class GPUImageCamreraListener implements GPUImageCamerLoaderBase.GPUImageCamreraListener {
            @Override
            public void takePhotoListenr(Bitmap bitmap, int ratio, CameraParm cameraParm) {
                bitmap = rotatePic(bitmap);
                presenter.takePhoto(bitmap, SurfViewCameraActivity.this.isShowRect, ratio, cameraParm, CameraStatus.getCameraStatus().getWaterMark().isWaterMarkOn());
            }

            @Override
            public void takePhonoErr() {
                Toast.makeText(SurfViewCameraActivity.this, "拍照失败，请重试！", Toast.LENGTH_LONG).show();

            }


            @Override
            public void getOrientation(int orientation) {
                presenter.rotationViewAnim(orientation);
            }

            /**
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

            @Override
            public void takePhotoByteListener(byte[] data, int ratio, CameraParm cameraParm) {

            }
        }
        mCamera.setGpuImageCamreraListener(new

                GPUImageCamreraListener());
    }

    private Bitmap rotatePic(Bitmap bitmap) {
        String device = DeviceUtils.getDeviceBrand();
        String model = android.os.Build.MODEL;
        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        if (device.equals("samsung")) {
            if (model.equals("SM-G9350")) {
                matrix = new Matrix();
            }
        }
        if (model.equals("Pixel 2")) {
            matrix = new Matrix();
        }
        bitmap = Bitmap.createBitmap(bitmap,
                0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }


    /**
     * 显示辅助线
     */
    public void showDottedLine() {
        EventStatisticsUtil.event(SurfViewCameraActivity.this, EventCommon.CAMERA_DOTTEDLINE);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) surfviewCameraLayoutBinding.dottedLine.getLayoutParams();
        layoutParams.bottomMargin = bottom_height;
        surfviewCameraLayoutBinding.dottedLine.setLayoutParams(layoutParams);

        surfviewCameraLayoutBinding.dottedLine.setVisibility(View.VISIBLE);
    }

    public void dismissDottedLine() {
        EventStatisticsUtil.event(SurfViewCameraActivity.this, EventCommon.CAMERA_UNDOTTEDLINE);

        surfviewCameraLayoutBinding.dottedLine.setVisibility(View.GONE);
    }

    private void dismissQR() {
//        blueToothListNewPop.dismiss();
//        qrcode_content.setVisibility(View.GONE);
        mCamera.onResume();

    }


    @Override
    public void initRotationAnim(final int currentRotation, final int rotation) {

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
            int blueLineHight = (int) (surfviewCameraLayoutBinding.lineImageView.getHeight() / 2 * (yAngle * 2) / 180);

//            int blueLineHight = (int) (lineImageView.getHeight() / 2 * (xAngle*2) / 300);
            //每一弧度的长度
            surfviewCameraLayoutBinding.lineImageView.blueLineHight = blueLineHight;
            surfviewCameraLayoutBinding.lineImageView.invalidate();
        } else {
            int blueLineHight = (int) (surfviewCameraLayoutBinding.lineImageView.getHeight() / 2 * xAngle / MAX_ANGLE);
            //每一弧度的长度
            surfviewCameraLayoutBinding.lineImageView.blueLineHight = blueLineHight;
            surfviewCameraLayoutBinding.lineImageView.invalidate();
        }

    }

    @Override
    public void rotationSubline(final float xDegrees, float yDegrees, final float rotation) {
//        presenter.startSublineValueAnimator(new AnimFactory.FloatListener() {
//            @Override
//            public void floatValueChang(float f) {
//
//                float fr = rotation + f * (rotation - xDegrees);
//                sublineImageView.setRotation(fr);
//                sublineImageView.invalidate();
//                if (Math.abs(Math.abs(xDegrees)) >= 178 || Math.abs(Math.abs(xDegrees)) <= 2 || Math.abs(Math.abs(xDegrees) - 90) <= 2) {
//                    sublineImageView.rotation = true;
//                    lineImageView.rotation = true;
//                    sublineImageView.invalidate();
//                } else {
//                    sublineImageView.rotation = false;
//                    lineImageView.rotation = false;
//                    sublineImageView.invalidate();
//                }
//            }
//        });

//        sublineImageView.setRotation(rotation);
//        sublineImageView.invalidate();
//        if (Math.abs(xDegrees) == 180f || Math.abs(xDegrees) == 0f || Math.abs(xDegrees) == 90f) {
//            sublineImageView.rotation = true;
//            lineImageView.rotation = true;
//            sublineImageView.invalidate();
//        } else {
//            sublineImageView.rotation = false;
//            lineImageView.rotation = false;
//            sublineImageView.invalidate();
//        }

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
                //surfaceview_layout.setAlpha(f);
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
        surfviewCameraLayoutBinding.coBoxChooseTopView.setVisibility(VISIBLE);
        surfviewCameraLayoutBinding.coBoxChooseTopView.setConnectBluetoothName(name);
        surfviewCameraLayoutBinding.blueToothListView.setConnectBluetoothName(name);
    }

    @Override
    public void disMissBlueView() {
        surfviewCameraLayoutBinding.blueToothListView.close();
    }

    /**
     * 连接中断时设置预设参数为无
     */
    @Override
    public void setErrorConnectPresetSelect() {
        presenter.setGPUImageFilter(null);
        presenter.setPresetCameraParams(null);
        if (presetItems != null)
            presenter.getImageData().setPresetParms(presetItems.get(0).getPresetParm());
        selectedPresetPosition = 0;
        mCamera.unlockAllCameraParam();

    }

    /**
     * 点击预设参数
     *
     * @param position   预设参数下标
     * @param viewHolder
     */
    @Override
    public void presetItemClick(int position, PresetHorizontalListAdapter.ViewHolder viewHolder) {

      /*  if (!BlueToothLoad.getInstance().blueToothIsConnection()) {
            return;
        }*/
        if (!BleConnectModel.getBleConnectModelInstance().isCurrentConnect()) {
            return;
        }
        //点击商拍光影推荐
       /* if (position == 1) {
            startScan();
            return;
        }*/
        selectedPresetPosition = position;
        surfviewCameraLayoutBinding.cameraParamAdjustView.setSelect(position);
        ToastUtil.toast(presetItems.get(position).getName());
        presenter.setGPUImageFilter(presetItems.get(position).getPresetParm());
        presenter.setPresetCameraParams(presetItems.get(selectedPresetPosition).getPresetParm());
        presenter.getImageData().setPresetParms(presetItems.get(position).getPresetParm());
        if (null != presetItems.get(position).getPresetParm()) {
            CameraParm cameraParm = presetItems.get(position).getPresetParm().getCameraParm();
            if (null != cameraParm) {


                if (cameraParm.getRed() != null) {
                    mCamera.lockPresetCameraParam(cameraParm.getSec(), cameraParm.getIso(), cameraParm.getWhiteBalance(), cameraParm.getFocalLength(), cameraParm.getRed(), cameraParm.getGreenEven(), cameraParm.getGreenOdd(), cameraParm.getBlue());
                } else {
                    mCamera.lockPresetCameraParam(cameraParm.getSec(), cameraParm.getIso(), cameraParm.getWhiteBalance(), cameraParm.getFocalLength());
                }


                //mCamera.lockPresetCameraParam(cameraParm.getSec(), cameraParm.getIso(), cameraParm.getWhiteBalance(), cameraParm.getFocalLength());
            } else {
                mCamera.unlockAllCameraParam();
            }
        } else {
            mCamera.unlockAllCameraParam();
        }

    }

    @Override
    public void setExposure(int ae) {

    }

    /**
     * 点击扫描预设参数
     *
     * @param position   预设参数下标
     * @param viewHolder
     */
    @Override
    public void presetItemClick(int position, PresetScanListAdapter.ViewHolder viewHolder) {
       /* if (!BlueToothLoad.getInstance().blueToothIsConnection()) {
            return;
        }*/
        if (!BleConnectModel.getBleConnectModelInstance().isCurrentConnect()) {
            return;
        }
        //点击商拍光影推荐
      /*  if (position == 1) {
            startScan();
            return;
        }*/
        isScanON = true;
        selectedPresetPosition = position;
        surfviewCameraLayoutBinding.cameraParamAdjustView.setSelect(position);
        ToastUtil.toast(presetItems.get(position).getName());
        presenter.setGPUImageFilter(presetItems.get(position).getPresetParm());
        presenter.setPresetCameraParams(presetItems.get(selectedPresetPosition).getPresetParm());
        presenter.getImageData().setPresetParms(presetItems.get(position).getPresetParm());

        if (null != presetItems.get(position).getPresetParm()) {
            CameraParm cameraParm = presetItems.get(position).getPresetParm().getCameraParm();
            if (null != cameraParm) {

                if (cameraParm.getRed() != null) {
                    mCamera.lockPresetCameraParam(cameraParm.getSec(), cameraParm.getIso(), cameraParm.getWhiteBalance(), cameraParm.getFocalLength(), cameraParm.getRed(), cameraParm.getGreenEven(), cameraParm.getGreenOdd(), cameraParm.getBlue());
                } else {
                    mCamera.lockPresetCameraParam(cameraParm.getSec(), cameraParm.getIso(), cameraParm.getWhiteBalance(), cameraParm.getFocalLength());
                }
                //mCamera.lockPresetCameraParam(cameraParm.getSec(), cameraParm.getIso(), cameraParm.getWhiteBalance(), cameraParm.getFocalLength());
            } else {
                mCamera.unlockAllCameraParam();
            }
        } else {
            mCamera.unlockAllCameraParam();
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

        surfviewCameraLayoutBinding.scaleView.setProgress(num * 2);
    }

    @Override
    public void setProgressAndDefaultPoint(int num, float defaultPoint, CameraParamType cameraParamType) {
        surfviewCameraLayoutBinding.scaleView.setProgressAndDefaultPoint(defaultPoint, num * 2, cameraParamType);
    }


    @Override
    public Handler getHandler() {
        return super.getHandler();
    }

    @Override
    public void setXYZRenderer(GLSurfaceView.Renderer renderer) {
     /*   rect_view.setZOrderOnTop(true);
        rect_view.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        rect_view.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        rect_view.setRenderer(renderer); // Use a custom renderer*/
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
                surfviewCameraLayoutBinding.blueToothListView.close();
                break;
            case CoBoxChooseTopView.BLUE_TOUCH_CONNECTION_ERROR:
                presenter.refreshLightBlueTouch();
                break;
        }
        surfviewCameraLayoutBinding.coBoxChooseTopView.setBlueTouchType(type);
        surfviewCameraLayoutBinding.blueToothListView.setBlueTouchType(type);
        surfviewCameraLayoutBinding.cameraBottomView.setBlueTouchType(type);
        surfviewCameraLayoutBinding.cameraParamAdjustView.setConnectionShowTypeCamera2(type);

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

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
//        qrcode_content.setVisibility(VISIBLE);
//        capture_scan_line.startAnimation(animation);

        int dp200 = ContentUtil.dip2px(this, 200);
        int cameraWidth = mCamera.getPreviewSize().height;
        int cameraHeight = mCamera.getPreviewSize().width;

        /** 获取布局容器的宽高 */
        int containerWidth = surfviewCameraLayoutBinding.qrcodeContent.getMeasuredWidth();
        int containerHeight = surfviewCameraLayoutBinding.qrcodeContent.getMeasuredHeight();

        /** 获取布局中扫描框的位置信息 */
        int cropLeft = (containerWidth - dp200) / 2;
        int cropTop = (containerHeight - dp200) / 2;

        int cropWidth = dp200;
        int cropHeight = dp200;

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        Rect mCropRect = new Rect(x, y, width + x, height + y);
        mCamera.setCropRect(mCropRect);
        mCamera.setQRCode(true);
        isQROn = true;
    }

    @Override
    public void onRecodeShow(int w, int h) {

    }

    @Override
    public GPUImageCamerLoaderBase getCamera() {
        return mCamera;
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
        return true;
    }


    private Bitmap getVideoBitmap(String url) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(url);
        return media.getFrameAtTime();
    }

    /**
     * 保存视屏图片
     */
    private class SavePicTask extends AsyncTask<Void, Void, String> {
        private Bitmap bitmap;
        private String bitmapUrl;
        private boolean isshowRect;

        SavePicTask(Bitmap bitmap, boolean isshowRect) {
            this.bitmap = bitmap;
            this.isshowRect = isshowRect;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (!isshowRect) {
                try {
                    bitmapUrl = FileUtil.saveVideoCover(bitmap, System.currentTimeMillis() + "");
                    // bitmapUrl = FileUtil.saveBitmaptoNative(Flag.NATIVESDFILE, bitmap, System.currentTimeMillis() + "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                bitmapUrl = FileUtil.saveBitmap2(bitmap, true, 0, System.currentTimeMillis() + "", false, false, null);
            }
            return bitmapUrl;

        }


        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            bitmapUrl = result;
            dissmissDialog();
            // canClick = true;
            mCamera.onPause();
            presenter.goToRecoderActivity(mCamera.getVidioUrl(), bitmapUrl);
        }
    }

    public void deletRecoder(final String url) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                File file = new File(url);
                file.delete();
            }
        });

    }

    @Override
    public GPUImage getgpuimage() {
        return null;
    }

    public class HideControl {
        public final static int MSG_HIDE = 0x01;

        private HideHandler mHideHandler;

        public HideControl() {
            mHideHandler = new HideHandler();
        }

        public class HideHandler extends Handler {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_HIDE:
                        break;
                }

            }
        }

        private Runnable hideRunable = new Runnable() {

            @Override
            public void run() {
                mHideHandler.obtainMessage(MSG_HIDE).sendToTarget();
            }
        };

        public void startHideTimer() {//开始计时,1.5秒后执行runable
            mHideHandler.removeCallbacks(hideRunable);

            mHideHandler.postDelayed(hideRunable, 600);
        }

        public void endHideTimer() {//移除runable,将不再计时
            mHideHandler.removeCallbacks(hideRunable);
        }

        public void resetHideTimer() {//重置计时
            mHideHandler.removeCallbacks(hideRunable);
            mHideHandler.postDelayed(hideRunable, 600);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;

        if (requestCode == resultCode && resultCode == REQUESE) {
            surfviewCameraLayoutBinding.cameraSettingView.setWatermark();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Layout Anim
     */
    private static class ViewWrapper {
        private View mTarget;

        public ViewWrapper(View target) {
            mTarget = target;
        }

        public int getWidth() {
            return mTarget.getLayoutParams().width;
        }

        public int getHeight() {
            return mTarget.getLayoutParams().height;
        }

        public void setHeight(int height) {
            mTarget.getLayoutParams().height = height;
            mTarget.requestLayout();
        }

        public void setWidth(int width) {
            mTarget.getLayoutParams().width = width;
            mTarget.requestLayout();
        }

    }

    private ObjectAnimator widthAnim, heightAnim;
    private AnimatorSet layoutAnim;

    private void performAnimate(int width, int height) {
        final ViewWrapper wrapper = new ViewWrapper(surfviewCameraLayoutBinding.cameraTexture);
        if (layoutAnim != null && layoutAnim.isRunning()) {
            layoutAnim.cancel();
        }
        layoutAnim = new AnimatorSet();
        widthAnim = ObjectAnimator.ofInt(wrapper, "width", wrapper.getWidth(), width).setDuration(300);
        widthAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private IntEvaluator mEvaluator = new IntEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                wrapper.mTarget.requestLayout();
            }
        });
        heightAnim = ObjectAnimator.ofInt(wrapper, "height", wrapper.getHeight(), height).setDuration(300);
        layoutAnim.playTogether(widthAnim, heightAnim);
        layoutAnim.start();
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
        surfviewCameraLayoutBinding.focusview.setBackgroundColor(getResources().getColor(R.color.black));
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                canClick = false;
                surfviewCameraLayoutBinding.cameraTexture.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                surfviewCameraLayoutBinding.focusview.setBackgroundColor(getResources().getColor(R.color.translucent));
                surfviewCameraLayoutBinding.cameraTexture.setVisibility(View.VISIBLE);
                canClick = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                surfviewCameraLayoutBinding.focusview.setBackgroundColor(getResources().getColor(R.color.translucent));
                surfviewCameraLayoutBinding.cameraTexture.setVisibility(View.VISIBLE);
                canClick = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(600);
        animator.start();
    }

    @Override
    public void setMotorStatus(int status) {

    }
}