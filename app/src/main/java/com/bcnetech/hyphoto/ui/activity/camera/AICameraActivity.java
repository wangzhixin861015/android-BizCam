package com.bcnetech.hyphoto.ui.activity.camera;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.bcnetech.bcnetchhttp.config.PreferenceConstants;
import com.bcnetech.bcnetechlibrary.dialog.ChoiceDialog;
import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.util.sharepreference.SharePreferences;
import com.bcnetech.hyphoto.data.CameraParamType;
import com.bcnetech.hyphoto.data.PresetItem;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.PresetParmsSqlControl;
import com.bcnetech.hyphoto.databinding.ActivityCameraLayoutBinding;
import com.bcnetech.hyphoto.model.BleConnectModel;
import com.bcnetech.hyphoto.presenter.SurfViewCameraPresenter;
import com.bcnetech.hyphoto.presenter.iview.ISurfViewCameraView;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.ui.activity.BaseMvpActivity;
import com.bcnetech.hyphoto.ui.adapter.PresetHorizontalListAdapter;
import com.bcnetech.hyphoto.ui.view.AIHintView;
import com.bcnetech.hyphoto.ui.view.BlueToothListNewView;
import com.bcnetech.hyphoto.ui.view.CoBoxChooseTopView;
import com.bcnetech.hyphoto.ui.view.FocusAlphaview;
import com.bcnetech.hyphoto.ui.view.scaleview.BaseScaleView;
import com.bcnetech.hyphoto.utils.PermissionUtil;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCamerLoaderBase;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCameraLoader;
import com.bcnetech.bluetoothlibarary.bluetoothagreement.CommendManage;
import com.bcnetech.hyphoto.R;
import com.example.imageproc.Process;
import com.example.imageproc.jni.ProcessByte;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class AICameraActivity extends BaseMvpActivity<ISurfViewCameraView, SurfViewCameraPresenter> implements ISurfViewCameraView, PresetHorizontalListAdapter.PresetHolderInterFace {
    public static final int REQUEST_CAMERA_PERMISSION = 1;
    public static final int REQUEST_LOCATION_PERMISSION = 2;
    private ActivityCameraLayoutBinding activityCameraLayoutBinding;
    private GPUImageCameraLoader mCamera;

    private List<PresetItem> presetItems;
    private PresetParmsSqlControl presetParmsSqlControl;
    //曝光补偿滑动条值
    private int mScale;
    private ChoiceDialog mchoiceDialog;
    //判断蓝牙状态（连接成功，断开，蓝牙关闭）
    private boolean BTStatus = true;
    private boolean isCanClick = false;
    private boolean isCanShot = false;
    private int[] params;
    private int count;
    private CameraEV cameraEv;
    private Camera.Size previewSize;
    private DGProgressDialog circledialog;
    private ValueAnimator alphaAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_camera_layout);
        presenter.initBleConnectModel();
        initView();
        initData();
        onViewClick();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isCanClick = true;
            }
        }, 1000);

    }

    @Override
    public SurfViewCameraPresenter initPresenter() {
        return new SurfViewCameraPresenter();
    }

    @Override
    protected void initData() {
        presenter.setLightRationListData(AICameraActivity.this, null);

        presetParmsSqlControl = new PresetParmsSqlControl(this);
        startQueryByShowType();

        if (CommendManage.getInstance().getVersion() == CommendManage.VERSION_BOX) {
            activityCameraLayoutBinding.presetBottomView.setVisibility(View.GONE);
            activityCameraLayoutBinding.llBottom.setGravity(Gravity.CENTER);
        }

        activityCameraLayoutBinding.blueToothListView.initParm(presenter.getAdapter(), new BlueToothListNewView.BlueToothListInterface() {
            @Override
            public void onBlueToothDissmiss(Object... params) {
                dismissBTConnectView();
                presenter.getBleConnectModel().stopSearch();
                if (!BTStatus) {
                    showBtDialog();
                }
            }

            @Override
            public void onBlueToothClick(Object... params) {
                showLoading();
                dismissBTConnectView();
                presenter.choiceDeivce((int) params[0]);
            }

            @Override
            public void onListConnection(Object... params) {
                int i = 0;
            }

            @Override
            public void onScanConnection(Object... params) {
                dismissBTConnectView();
                String result = (String) params[0];
                ToastUtil.toast(result);
                if (result.contains(CommendManage.COBOX_NAME) || result.contains(CommendManage.CBEDU_NAME) || result.contains(CommendManage.COLINK_NAME)) {
                    presenter.scanDevice(result);
                }
            }
        });


        if (!BleConnectModel.getBleConnectModelInstance().isCurrentConnect()) {
            BTStatus = false;
            showBTConnet();
        }
        //设置预览比例为3：4
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ContentUtil.getScreenWidth(AICameraActivity.this), (int) (ContentUtil.getScreenWidth(AICameraActivity.this) * (4f / 3f)));
        activityCameraLayoutBinding.surfaceview.setLayoutParams(layoutParams);
        activityCameraLayoutBinding.focusCenter.setLayoutParams(layoutParams);
        activityCameraLayoutBinding.focusAlpha.setLayoutParams(layoutParams);
        //其余部分以剩余高度平均分配,14为提示所占高度，10为预留高度
        int restHeight = ContentUtil.getScreenHeight(AICameraActivity.this) - layoutParams.height - activityCameraLayoutBinding.clTop.getHeight() - ContentUtil.dip2px(AICameraActivity.this, 14) - ContentUtil.dip2px(AICameraActivity.this, 10);
        ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, restHeight);
        layoutParams2.bottomToBottom = 0;
        activityCameraLayoutBinding.llBottom.setLayoutParams(layoutParams2);
        SharePreferences preferences = SharePreferences.instance();
        int Count = preferences.getInt(PreferenceConstants.LASTAICOUNT, 20);
        if (Count != 0) {
            Count--;
            activityCameraLayoutBinding.aiHintView.setVisibility(VISIBLE);
            preferences.putInt(PreferenceConstants.LASTAICOUNT, Count);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initCamera();
                }
            }, 500);
            activityCameraLayoutBinding.aiHintView.setVisibility(GONE);
        }
        activityCameraLayoutBinding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        activityCameraLayoutBinding.ivCobox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBTConnet();
            }
        });

        activityCameraLayoutBinding.blueToothListView.setCloseConnecrtion(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                activityCameraLayoutBinding.blueToothListView.setBlueTouchType(BlueToothListNewView.BLUE_TOUCH_CONNECTION_ERROR);
                presenter.getBleConnectModel().disConnectCurrent();
//                presenter.choiceDeivce(presenter.getSelectPosition());
            }
        });
        activityCameraLayoutBinding.presetBottomView.setPresetInterface(this);
        final ViewTreeObserver v = activityCameraLayoutBinding.focusCenter.getViewTreeObserver();
        v.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (params == null) {
                    params = new int[4];
                    params[0] = (int) activityCameraLayoutBinding.focusCenter.getX() + activityCameraLayoutBinding.focusCenter.getPadding();
                    params[1] = (int) activityCameraLayoutBinding.focusCenter.getY() + activityCameraLayoutBinding.title.getHeight() + activityCameraLayoutBinding.clTop.getHeight() + activityCameraLayoutBinding.focusCenter.getPadding();
                    params[2] = activityCameraLayoutBinding.focusCenter.getRadiusLength() * 2;
                    params[3] = activityCameraLayoutBinding.focusCenter.getRadiusLength() * 2;
                    activityCameraLayoutBinding.focusCenter.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return true;
            }
        });
        activityCameraLayoutBinding.aiHintView.bringToFront();
        activityCameraLayoutBinding.aiHintView.setType(AIHintView.AICAMERA);
        activityCameraLayoutBinding.aiHintView.setInterface(new AIHintView.AIHintCallback() {

            @Override
            public void onClose() {
                finish();
            }

            @Override
            public void onClick() {
                activityCameraLayoutBinding.aiHintView.dismiss();
                initCamera();
            }

            @Override
            public void onCobox() {

            }
        });
    }

    /**
     * @param v1
     * @param v2
     * @param scale 小数点后的位数
     * @return
     */
    private float div(float v1, float v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_DOWN).floatValue();
    }

    public static byte[] bitmap2RGB(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();  //返回可用于储存此位图像素的最小字节数

        ByteBuffer buffer = ByteBuffer.allocate(bytes); //  使用allocate()静态方法创建字节缓冲区
        bitmap.copyPixelsToBuffer(buffer); // 将位图的像素复制到指定的缓冲区

        byte[] rgba = buffer.array();
        byte[] pixels = new byte[(rgba.length / 4) * 3];

        int count = rgba.length / 4;

        //Bitmap像素点的色彩通道排列顺序是RGBA
        for (int i = 0; i < count; i++) {

            pixels[i * 3] = rgba[i * 4];        //R
            pixels[i * 3 + 1] = rgba[i * 4 + 1];    //G
            pixels[i * 3 + 2] = rgba[i * 4 + 2];       //B

        }

        return pixels;
    }


    public static byte[] bitmap2RGBA(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();  //返回可用于储存此位图像素的最小字节数

        ByteBuffer buffer = ByteBuffer.allocate(bytes); //  使用allocate()静态方法创建字节缓冲区
        bitmap.copyPixelsToBuffer(buffer); // 将位图的像素复制到指定的缓冲区

        byte[] rgba = buffer.array();

        return rgba;
    }


    @Override
    protected void onViewClick() {
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
                activityCameraLayoutBinding.presetBottomView.addData(presetItems);
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

        activityCameraLayoutBinding.horizontalscalescrollview.setOnScrollListener(new BaseScaleView.OnScrollListener() {
            @Override
            public void onScaleScroll(int scale) {
                if (mScale == scale) {
                    return;
                }
                mScale = scale;
                float currentAeNum = (cameraEv.maxEv / (float) activityCameraLayoutBinding.horizontalscalescrollview.getMax() * scale);
                mCamera.setEv((int) currentAeNum);
                // cameraController.setAe((int) currentAeNum);
            }
        });
        activityCameraLayoutBinding.focusCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCanShot) {
                    takePhoto();
                }
            }
        });
    }

    @Override
    protected void initView() {

        activityCameraLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera_layout);


    }

    private void showBTConnet() {
        if (!presenter.checkGPSIsOpen()) {
            ToastUtil.toast(getResources().getString(R.string.request_gps));
            return;
        }
        if (!BleConnectModel.getBleConnectModelInstance().isBlueEnable()) {
            activityCameraLayoutBinding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CLOSE);
        } else {
            activityCameraLayoutBinding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_OPEN);
        }
        if (BleConnectModel.getBleConnectModelInstance().isCurrentConnect()) {
            activityCameraLayoutBinding.blueToothListView.setConnectBluetoothName(BleConnectModel.getBleConnectModelInstance().getCurrentBlueToothDevice().getName());
            activityCameraLayoutBinding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CONNECTION);
        }
        presenter.startScanBlueTooth();
        if (PermissionUtil.getFindLocationPermmission(AICameraActivity.this)) {
            showBTConnectView();
        } else {
            ToastUtil.toast(AICameraActivity.this.getResources().getString(R.string.please_location_permissions));
        }
        activityCameraLayoutBinding.blueToothListView.bringToFront();

    }

    private void initCamera() {
        activityCameraLayoutBinding.surfaceview.setVisibility(VISIBLE);
        class CameraLoaderListener implements GPUImageCamerLoaderBase.CameraLoaderListener {
            @Override
            public void isCamera2Support(boolean isc2Support) {
            }
        }

        mCamera = new GPUImageCameraLoader(AICameraActivity.this, activityCameraLayoutBinding.surfaceview);
        mCamera.setCameraLoaderListener(new CameraLoaderListener());
        //activityCameraLayoutBinding.focusAlpha.isCanshot(FocusCenterView.STATUS_DETECTING);
        mCamera.setGpuimagePreviewCallback(new GPUImageCameraLoader.GpuImagePreviewCallback() {
            @Override
            public synchronized void onPreViewFrame(byte[] data, Camera camera) {
                if (isCanClick) {
                    count = count + 1;
                    //识别图片
                    if (count % 5 == 0) {
                        // FileUtil.save2Txt(data,"");
                        //size width:1080,size height 1920
                        if (camera != null && camera.getParameters() != null) {
                            if (previewSize == null) {
                                previewSize = camera.getParameters().getPreviewSize();
                            }
                            float a = div(previewSize.height, activityCameraLayoutBinding.surfaceview.getWidth(), 4);
                            int parm[] = new int[6];
                            //裁剪坐标
                            parm[0] = (int) (params[0] * a);
                            parm[1] = (int) (params[1] * a);
                            parm[2] = (int) (params[2] * a);
                            parm[3] = (int) (params[3] * a);
                            //半径
                            parm[4] = activityCameraLayoutBinding.focusCenter.getRadiusLength();
                            //屏幕宽度
                            parm[5] = ContentUtil.getScreenWidth(AICameraActivity.this);

                            final ProcessByte jin = new ProcessByte();
                            jin.iparams = parm;
                            jin.img_width = previewSize.width;
                            jin.img_height = previewSize.height;
                            jin.srcbuf = data;
                            jin.api_method = com.example.imageproc.Process.JNIAPI_METHOD_AUTOPHOTOGRAPH;
                            long time = System.currentTimeMillis();
                            final ProcessByte res = (ProcessByte) com.example.imageproc.Process.byteJniApiMethod(jin);
                            LogUtil.d("jinapi_duration is: " + (System.currentTimeMillis() - time));
                            // 返回值int[当前状态,偏移等级]
                            if (res != null && res.retval == 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (res.iparams != null && res.iparams.length != 0) {
                                            // activityCameraLayoutBinding.focusCenter.isCanshot(FocusCenterView.STATUS_DETECTING);
                                            if (res.dparams != null && res.dparams.length != 0) {
                                                double angle = res.dparams[0];
                                                int biasLevel = res.iparams[1];
                                                activityCameraLayoutBinding.focusCenter.getIdentfyDirect(angle, biasLevel);
                                            }
                                            switch (res.iparams[0]) {
                                                case Process.AIResult.TARGET_STATUS_AREA_BIG:
                                                    isCanShot = false;
                                                    activityCameraLayoutBinding.title.setText(getResources().getString(R.string.ai_big));
                                                    if (alphaAnimator == null)
                                                        setAlphaAnimator();
                                                    showHander(false);
                                                    activityCameraLayoutBinding.focusAlpha.setRadius(activityCameraLayoutBinding.focusCenter.getRadiusLength(), FocusAlphaview.STATUS_FAIL);
                                                    break;
                                                case Process.AIResult.TARGET_STATUS_AREA_SMALL:
                                                    isCanShot = false;
                                                    activityCameraLayoutBinding.title.setText(getResources().getString(R.string.ai_small));
                                                    if (alphaAnimator == null)
                                                        setAlphaAnimator();
                                                    showHander(false);
                                                    activityCameraLayoutBinding.focusAlpha.setRadius(activityCameraLayoutBinding.focusCenter.getRadiusLength(), FocusAlphaview.STATUS_FAIL);
                                                    break;
                                                case Process.AIResult.TARGET_STATUS_OK:
                                                    isCanShot = true;
                                                    activityCameraLayoutBinding.title.setText(getResources().getString(R.string.ai_ok));
                                                    if (alphaAnimator == null)
                                                        setAlphaAnimator();
                                                    showHander(true);
                                                    activityCameraLayoutBinding.focusAlpha.setRadius(activityCameraLayoutBinding.focusCenter.getRadiusLength(), FocusAlphaview.STATUS_SHOT);
                                                    break;
                                                case Process.AIResult.TARGET_STATUS_FAILED_DETECT_TARGET:
                                                    isCanShot = false;
                                                    activityCameraLayoutBinding.title.setText(getResources().getString(R.string.ai_empty));
                                                    if (alphaAnimator == null)
                                                        setAlphaAnimator();
                                                    showHander(false);
                                                    activityCameraLayoutBinding.focusAlpha.setRadius(activityCameraLayoutBinding.focusCenter.getRadiusLength(), FocusAlphaview.STATUS_FAIL);
                                                    break;
                                                case Process.AIResult.TARGET_STATUS_SHIFT:
                                                    isCanShot = true;
                                                    activityCameraLayoutBinding.title.setText(getResources().getString(R.string.ai_shift));
                                                    if (alphaAnimator != null && alphaAnimator.isRunning())
                                                        alphaAnimator.cancel();
                                                    alphaAnimator = null;
                                                    showHander(false);
                                                    activityCameraLayoutBinding.focusAlpha.setRadius(activityCameraLayoutBinding.focusCenter.getRadiusLength(), FocusAlphaview.STATUS_DETECTING);
                                                    break;
                                            }
                                        } else {
                                            activityCameraLayoutBinding.focusCenter.getIdentfyDirect(0, 0);
                                        }

                                    }
                                });
                            } else {
                                isCanShot = false;
                                activityCameraLayoutBinding.title.setText(getResources().getString(R.string.ai_empty));
                                if (alphaAnimator == null)
                                    setAlphaAnimator();
                                showHander(false);
                                activityCameraLayoutBinding.focusAlpha.setRadius(activityCameraLayoutBinding.focusCenter.getRadiusLength(), FocusAlphaview.STATUS_FAIL);
                                activityCameraLayoutBinding.focusCenter.getIdentfyDirect(0, 0);
                            }
                        }
                    }
                }
            }

            @Override
            public void onTakePhoto(final byte[] data, final Camera camera) {
                ThreadPoolUtil.execute(new Runnable() {
                    @Override
                    public void run() {
                       /* Bitmap savebit = BitmapFactory.decodeByteArray(data, 0, data.length);
                        byte[] datas = bitmap2RGBA(savebit);*/
                        Camera.Size size = camera.getParameters().getPictureSize();
                        float a = div(size.height, activityCameraLayoutBinding.surfaceview.getWidth(), 4);
                        int parm[] = new int[7];
                        parm[0] = (int) (params[0] * a);
                        parm[1] = (int) (params[1] * a);
                        parm[2] = (int) (params[2] * a);
                        parm[3] = (int) (params[3] * a);
                        //半径
                        parm[4] = activityCameraLayoutBinding.focusCenter.getRadiusLength();
                        //屏幕宽度
                        parm[5] = ContentUtil.getScreenWidth(AICameraActivity.this);
                        switch ((int) presenter.CurrentCOBOXVer) {
                            case CommendManage.VERSION1_1:
                                parm[6] = 1;
                                break;
                            case CommendManage.VERSION2_1:
                                parm[6] = 1;
                                break;
                            case CommendManage.VERSION_BOX:
                                parm[6] = 2;
                                break;
                            default:
                                parm[6] = 1;
                        }
                        final ProcessByte jin = new ProcessByte();
                        jin.iparams = parm;
                        jin.img_width = size.width;
                        jin.img_height = size.height;
                        jin.srcbuf = data;
                        jin.api_method = com.example.imageproc.Process.JNIAPI_METHOD_AUTOPHOTOGRAPH_PROCESS;
                        final ProcessByte res = (ProcessByte) com.example.imageproc.Process.byteJniApiMethod(jin);
                        int[] buffer = res.dstbuf;
                        final Bitmap Img = Bitmap.createBitmap(parm[2], parm[3], Bitmap.Config.ARGB_8888);
                        Img.copyPixelsFromBuffer(IntBuffer.wrap(buffer));
                        // Img.setPixels(buffer, 0, parm[2], 0, 0, parm[2], parm[3]);
                        if (Img != null) {
                            presenter.getCameraPhoto(Img, Img, false, 0, null, false, true);
                        }
                    }
                });
            }
        }, true);
        resumeMainCamera();
    }


    private void takePhoto() {
        isCanClick = false;
        activityCameraLayoutBinding.focusCenter.startTakePhotoAnim();
        activityCameraLayoutBinding.focusAlpha.setPaintColor(FocusAlphaview.STATUS_DETECTING);
        mCamera.pointFocus(activityCameraLayoutBinding.focusCenter.getFocusCenter().x, activityCameraLayoutBinding.focusCenter.getFocusCenter().y, activityCameraLayoutBinding.surfaceview.getWidth(), activityCameraLayoutBinding.surfaceview.getHeight());
        Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (isCanShot) {
                    isCanShot = false;
                    mCamera.takePhoto();
                    ThreadPoolUtil.execute(new Runnable() {
                        @Override
                        public void run() {
                            mCamera.getmCameraInstance().autoFocus(null);
                        }
                    });
                }
            }
        };
        mCamera.getmCameraInstance().autoFocus(autoFocusCallback);
        // mCamera.takePhoto();
    }

    /**
     * 提示闪烁动画
     */
    private void setAlphaAnimator() {
        if (alphaAnimator == null) {
            alphaAnimator = ValueAnimator.ofFloat(1, 0);
            alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
            alphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
            alphaAnimator.setDuration(1000);
            alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    activityCameraLayoutBinding.focusAlpha.setAlpha((float) animation.getAnimatedValue());
                }
            });
            if (!alphaAnimator.isRunning())
                alphaAnimator.start();
        }

    }

    private void showHander(boolean isShow) {
        activityCameraLayoutBinding.ivHander.setVisibility(isShow ? VISIBLE : GONE);
    }

    private void showBTConnectView() {
        if (isCanClick) {
            if (!BleConnectModel.getBleConnectModelInstance().isBlueEnable()) {
                activityCameraLayoutBinding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CLOSE);
            } else {
                activityCameraLayoutBinding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_OPEN);
                if (BleConnectModel.getBleConnectModelInstance().isCurrentConnect()) {
                    activityCameraLayoutBinding.blueToothListView.setConnectBluetoothName(BleConnectModel.getBleConnectModelInstance().getCurrentBlueToothDevice().getName());
                    activityCameraLayoutBinding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CONNECTION);
                } else {
                    BleConnectModel.getBleConnectModelInstance().startScanBlueTooth();
                }
            }
            if (mCamera != null)
                mCamera.onPause();
            activityCameraLayoutBinding.focusAlpha.setVisibility(View.GONE);
            activityCameraLayoutBinding.surfaceview.setVisibility(View.GONE);
            activityCameraLayoutBinding.focusCenter.setVisibility(View.GONE);
            activityCameraLayoutBinding.title.setVisibility(View.GONE);
            activityCameraLayoutBinding.rlScale.setVisibility(View.GONE);
            activityCameraLayoutBinding.presetBottomView.setVisibility(View.GONE);
            activityCameraLayoutBinding.blueToothListView.setApplyBlur();
            activityCameraLayoutBinding.blueToothListView.setVisibility(VISIBLE);
        }
    }

    private void dismissBTConnectView() {
        resumeMainCamera();
        activityCameraLayoutBinding.focusAlpha.setVisibility(View.VISIBLE);
        activityCameraLayoutBinding.surfaceview.setVisibility(View.VISIBLE);
        activityCameraLayoutBinding.focusCenter.setVisibility(View.VISIBLE);
        activityCameraLayoutBinding.title.setVisibility(View.VISIBLE);
        activityCameraLayoutBinding.presetBottomView.setVisibility(VISIBLE);
        if (CommendManage.getInstance().getVersion() == CommendManage.VERSION_BOX) {
            activityCameraLayoutBinding.presetBottomView.setVisibility(View.GONE);
            activityCameraLayoutBinding.llBottom.setGravity(Gravity.CENTER);
        }
        activityCameraLayoutBinding.rlScale.setVisibility(View.VISIBLE);
        activityCameraLayoutBinding.blueToothListView.setApplyBlur();
        activityCameraLayoutBinding.blueToothListView.setVisibility(View.GONE);
        //cameraController.setIdentfyStatus(true);
    }

    /**
     * 蓝牙断开的提示
     */
    private void showBtDialog() {
        if (activityCameraLayoutBinding.blueToothListView.getVisibility() == VISIBLE)
            return;
        if(null==mchoiceDialog){
            mchoiceDialog = ChoiceDialog.createInstance(AICameraActivity.this);
        }
        mchoiceDialog.setTouchOutside(false);
        mchoiceDialog.isNeedBlur(false);
        mchoiceDialog.setAblumTitle(this.getResources().getString(R.string.bt_disconnected));
        mchoiceDialog.setAblumMessage(getResources().getString(R.string.reconnect));
        mchoiceDialog.setOk(getResources().getString(R.string.confirm));
        mchoiceDialog.setCancel(getResources().getString(R.string.cancel));
        mchoiceDialog.setChoiceCallback(new ChoiceDialog.CardDialogCallback() {
            @Override
            public void onOKClick() {
                dismissBTDialog();
                showBTConnectView();
            }

            @Override
            public void onCancelClick() {
                dismissBTDialog();
                finish();
               /* new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showBtDialog();
                    }
                }, 1000);*/
            }

            @Override
            public void onDismiss() {

            }
        });
        mchoiceDialog.show();
    }

    private void showPicturePreview(String url) {
        Bundle b = new Bundle();
        b.putString("url", url);
        Intent intent = new Intent(AICameraActivity.this, AiPreviewActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }


    private void dismissBTDialog() {
        if (mchoiceDialog != null) {
            mchoiceDialog.dismiss();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION || requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                LogUtil.d("need camera permission");
            }
        }
    }

    @Override
    public List getItemList() {
        return null;
    }

    @Override
    public void initRotationAnim(int currentRotation, int rotation) {

    }

    @Override
    public void setWaitPicShow(boolean b) {

    }

    @Override
    public void showToast(ImageData imageData, String str) {
        isCanClick = true;
        mCamera.setAf(true);
        activityCameraLayoutBinding.focusCenter.stopTakePhotoAnim();
        if (imageData != null && imageData.getLocalUrl() != null && !TextUtils.isEmpty(imageData.getLocalUrl())) {
            showPicturePreview(imageData.getLocalUrl());
        }
    }

    @Override
    public void initRotationSubline(int currentRotation, float xAngle, float yAngle) {

    }

    @Override
    public void rotationSubline(float xDegrees, float yDegrees, float rotation) {

    }

    @Override
    public void inTakePhoto() {

    }

    @Override
    public void outTakePhoto() {

    }

    @Override
    public void inVedio() {

    }

    @Override
    public void outVedio() {

    }

    @Override
    public void inPreset() {

    }

    @Override
    public void outPreset() {

    }

    @Override
    public void inPro() {

    }

    @Override
    public void outPro() {

    }

    @Override
    public void setCameraParams(HashMap<String, String> hashMap) {
        mCamera.setCameraParams(hashMap);
    }

    @Override
    public void lockAllCameraParam() {

    }

    @Override
    public void unlockAllCameraParam() {

    }

    @Override
    public void outVedioInPreset() {

    }

    @Override
    public void inVedioOutPreset() {

    }

    @Override
    public void disMissBlueView() {

    }

    @Override
    public void setConnectBluetoothName(String name) {

    }

    @Override
    public void setErrorConnectPresetSelect() {

    }

    @Override
    public void setBlueTouchType(int type) {
        hideLoading();
        switch (type) {
            case CoBoxChooseTopView.BLUE_TOUCH_CLOSE:
                BTStatus = false;
                presenter.refreshLightBlueTouch();
                //蓝牙关闭
                showBtDialog();
                break;
            case CoBoxChooseTopView.BLUE_TOUCH_OPEN:
                presenter.refreshLightBlueTouch();
                break;
            case CoBoxChooseTopView.BLUE_TOUCH_CONNECTION:
                BTStatus = true;
//                activityCameraLayoutBinding.blueToothListView.close();
                if (presenter.getCurrentCoboxVer() == CommendManage.VERSION_BOX) {
                    activityCameraLayoutBinding.presetBottomView.setVisibility(View.GONE);
                    activityCameraLayoutBinding.llBottom.setGravity(Gravity.CENTER);
                    //当前连接设备为商拍魔盒时，将灯光调整到最亮
                    presenter.writeAllLightNumber(100, 100, 100, 100, 100, 100, 0, 0);
                }
                break;
            case CoBoxChooseTopView.BLUE_TOUCH_CONNECTION_ERROR:
                BTStatus = false;
                presenter.refreshLightBlueTouch();
                //蓝牙断开
                showBtDialog();
                break;
        }
        activityCameraLayoutBinding.blueToothListView.setBlueTouchType(type);

    }

    @Override
    public void surfviewSwitchFilterTo(GPUImageFilter gpuImageFilter) {

    }

    @Override
    public void setSeekBarNum(int num) {

    }

    @Override
    public void setProgressAndDefaultPoint(int num, float defaultPoint, CameraParamType
            cameraParamType) {

    }

    @Override
    public void setXYZRenderer(GLSurfaceView.Renderer renderer) {

    }

    @Override
    public void onRecodeShow(int w, int h) {

    }

    @Override
    public boolean isShowRect() {
        return false;
    }

    @Override
    public GPUImage getgpuimage() {
        return mCamera.getGpuimage();
    }

    @Override
    public GPUImageCamerLoaderBase getCamera() {
        return null;
    }

    @Override
    public void takePhotoAnimation() {

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

    @Override
    public void setExposure(int ae) {

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

    }

    @Override
    public Handler getHandler() {
        return super.getHandler();
    }

    @Override
    protected void onPause() {
        // cameraController.onPause();
        if (mCamera != null)
            mCamera.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (activityCameraLayoutBinding.blueToothListView.getVisibility() == VISIBLE) {
            dismissBTConnectView();
        }
        resumeMainCamera();
        //cameraController.onResume();
        super.onResume();
    }

    private void resumeMainCamera() {
        if (mCamera != null) {
            mCamera.onResume();
            HashMap<String, String> hashMap = new HashMap<>();
            mCamera.setCameraParamsAI(hashMap);
            Camera.Parameters parameters = mCamera.getCurrentparameter();
            if (cameraEv == null) {
                cameraEv = new CameraEV();
                cameraEv.currentEv = parameters.getExposureCompensation();
                cameraEv.evStep = parameters.getExposureCompensationStep();
                cameraEv.maxEv = parameters.getMaxExposureCompensation();
                cameraEv.minEv = parameters.getMinExposureCompensation();
            }
            mCamera.setPreviewCallback();
        }
    }

    public class CameraEV {
        float currentEv;
        float evStep;
        float maxEv;
        float minEv;
    }

    @Override
    protected void onDestroy() {
        if (mCamera != null)
            mCamera.onDestroy();
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void presetItemClick(int position, PresetHorizontalListAdapter.
            ViewHolder viewHolder) {
        activityCameraLayoutBinding.presetBottomView.setSelect(position);
        ToastUtil.toast(presetItems.get(position).getName());
        presenter.setGPUImageFilter(presetItems.get(position).getPresetParm());
        presenter.getImageData().setPresetParms(presetItems.get(position).getPresetParm());
    }

    @Override
    public void setMotorStatus(int status) {

    }
}
