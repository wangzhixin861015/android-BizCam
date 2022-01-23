package com.bcnetech.hyphoto.ui.activity.camera;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.RectF;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetchhttp.config.PreferenceConstants;
import com.bcnetech.bcnetechlibrary.dialog.ChoiceDialog;
import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog;
import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog3;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.util.sharepreference.SharePreferences;
import com.bcnetech.hyphoto.data.CameraParamType;
import com.bcnetech.hyphoto.data.PresetItem;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.PresetParmsSqlControl;
import com.bcnetech.hyphoto.databinding.ActivityAi360Binding;
import com.bcnetech.hyphoto.model.BleConnectModel;
import com.bcnetech.hyphoto.presenter.SurfViewCameraPresenter;
import com.bcnetech.hyphoto.presenter.iview.ISurfViewCameraView;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.ui.activity.BaseMvpActivity;
import com.bcnetech.hyphoto.ui.activity.GoodsMarketActivity;
import com.bcnetech.hyphoto.ui.adapter.PresetHorizontalListAdapter;
import com.bcnetech.hyphoto.ui.view.AIHintView;
import com.bcnetech.hyphoto.ui.view.BlueToothListNewView;
import com.bcnetech.hyphoto.ui.view.CoBoxChooseTopView;
import com.bcnetech.hyphoto.ui.view.FocusView;
import com.bcnetech.hyphoto.ui.view.PanoramaEditView;
import com.bcnetech.hyphoto.ui.view.VideoButtonView;
import com.bcnetech.hyphoto.ui.view.clickanimview.BamImageView;
import com.bcnetech.hyphoto.ui.view.scaleview.BaseScaleView;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.PermissionUtil;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCamerLoaderBase;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCameraLoader;
import com.bcnetech.bluetoothlibarary.bluetoothagreement.CommendManage;
import com.bcnetech.hyphoto.R;
import com.example.imageproc.Process;
import com.example.imageproc.jni.ProcessByte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * 生成商品360度预览
 * author: wsBai
 * date: 2018/11/27
 */
public class AI360CameraActivity extends BaseMvpActivity<ISurfViewCameraView, SurfViewCameraPresenter> implements ISurfViewCameraView, PresetHorizontalListAdapter.PresetHolderInterFace {
    public static final int REQUEST_CAMERA_PERMISSION = 1;
    public static final int REQUEST_LOCATION_PERMISSION = 2;
    private static final int DURATION = 21;
    //生成25d图片的总数
    private static final int TOTALPIC = 36;

    private ActivityAi360Binding activityAi360Binding;
    private GPUImageCameraLoader mCamera;

    private List<PresetItem> presetItems;
    private PresetParmsSqlControl presetParmsSqlControl;
    //曝光补偿滑动条值
    private int mScale;
    private ChoiceDialog mchoiceDialog;
    //判断蓝牙状态（连接成功，断开，蓝牙关闭）
    private boolean BTStatus = true;
    private boolean isCanClick = true;
    private boolean isRecording = false;
    private int[] params;
    private int count;
    private CameraEV cameraEv;
    private Camera.Size previewSize;
    private DGProgressDialog circledialog;
    private ValueAnimator alphaAnimator;
    private AI360Thread ai360Thread;
    //
    private int[] cutrect;
    private RectF showcutRect;
    private long startRecordTime = -1;
    private CountDownTimer countDownTimer;
    private long countDown = 0;
    private SaveCount saveCount;
    private boolean isAniming = false;
    public String SAVEPANORAMAS = Flag.BaseData + "/Panoramas/" + System.currentTimeMillis() + "/";
    private ValueAnimator valueAnimator;
    private DGProgressDialog3 dgProgressDialog3;
    private int savePoint = 0;
    private int currentStatus = CommendManage.MOTOROFFLINE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.initBleConnectModel();
        initView();
        initData();
        onViewClick();

    }

    @Override
    protected void initView() {
        activityAi360Binding = DataBindingUtil.setContentView(this, R.layout.activity_ai360);

    }

    private void reset(int motorStatus){
        if (!BleConnectModel.getBleConnectModelInstance().isCurrentConnect()) {
            BTStatus = false;
            showBTConnet();
        }
        if (motorStatus == CommendManage.MOTOROFFLINE) {
            currentStatus = CommendManage.MOTOROFFLINE;
            //转盘离线
            activityAi360Binding.aiHintView.setType(AIHintView.AI360_ERROR);
            // activityAi360Binding.aiHintView.setType(AIHintView.AI360_SUCCESS);
            activityAi360Binding.aiHintView.setVisibility(VISIBLE);
        } else {
            currentStatus = CommendManage.MOTORONLINE;
            activityAi360Binding.aiHintView.setType(AIHintView.AI360_SUCCESS);
            SharePreferences preferences = SharePreferences.instance();
            int Count = preferences.getInt(PreferenceConstants.AI360COUNT, 20);
            if (Count != 0) {
                Count--;
                activityAi360Binding.aiHintView.setVisibility(VISIBLE);
                preferences.putInt(PreferenceConstants.AI360COUNT, Count);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initCamera();
                    }
                }, 500);
                activityAi360Binding.aiHintView.setVisibility(GONE);
            }
        }
    }


    /**
     * 跳转商城页面
     */
    private void setMarket() {
        Intent intent = new Intent(AI360CameraActivity.this, GoodsMarketActivity.class);
        startActivity(intent);
        this.finish();
    }

    /**
     * 连接蓝牙界面
     */
    private void showBTConnet() {
        if (!BleConnectModel.getBleConnectModelInstance().isBlueEnable()) {
            ToastUtil.toast(getResources().getString(R.string.open_bt));
            return;
        }
        presenter.startScanBlueTooth();
        if (PermissionUtil.getFindLocationPermmission(AI360CameraActivity.this)) {
            showBTConnectView();
        } else {
            ToastUtil.toast(AI360CameraActivity.this.getResources().getString(R.string.please_location_permissions));
        }
        activityAi360Binding.blueToothListView.bringToFront();

    }

    private void set360Hint(final int type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case 0:
                        activityAi360Binding.iv360hint.setVisibility(GONE);
                        activityAi360Binding.tv360hint.setText("");
                        if (valueAnimator != null && valueAnimator.isRunning())
                            valueAnimator.cancel();
                        break;
                    case 1:
                        activityAi360Binding.iv360hint.setVisibility(GONE);
                        activityAi360Binding.tv360hint.setText(getResources().getString(R.string.hint360_1));
                        if (valueAnimator != null && valueAnimator.isRunning())
                            valueAnimator.cancel();
                        break;
                    case 2:
                        activityAi360Binding.iv360hint.setVisibility(VISIBLE);
                        activityAi360Binding.tv360hint.setText(getResources().getString(R.string.hint360_2));
                        set360hintAnim();
                        break;
                    case 3:
                        activityAi360Binding.iv360hint.setVisibility(GONE);
                        activityAi360Binding.tv360hint.setText(getResources().getString(R.string.hint360_3));
                        if (valueAnimator != null && valueAnimator.isRunning())
                            valueAnimator.cancel();
                        break;
                }
            }
        });

    }

    private void set360hintAnim() {
        valueAnimator = ValueAnimator.ofInt(0, 255);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                activityAi360Binding.iv360hint.setImageAlpha((int) animation.getAnimatedValue());
            }
        });
        valueAnimator.setDuration(500);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();
    }

    private void initCamera() {
        activityAi360Binding.surfaceview.setVisibility(VISIBLE);
        class CameraLoaderListener implements GPUImageCamerLoaderBase.CameraLoaderListener {
            @Override
            public void isCamera2Support(boolean isc2Support) {
            }
        }

        mCamera = new GPUImageCameraLoader(AI360CameraActivity.this, activityAi360Binding.surfaceview);
        mCamera.setCameraLoaderListener(new CameraLoaderListener());
        resumeMainCamera();
    }


    private class AI360Thread extends Thread {
        private byte[] data;

        @Override
        public synchronized void run() {
            int parm[] = new int[3];
            parm[0] = (Process.USAGE_PROCESS);
            parm[1] = (Process.BC_PLATEFORMINFO_ANDROID);
            parm[2] = (Process.BC_C0MB0X_VERSI0N_C2);
            final ProcessByte jin = new ProcessByte();
            jin.iparams = parm;
            jin.srcbuf = data;
            jin.img_width = previewSize.width;
            jin.img_height = previewSize.height;
            jin.api_method = com.example.imageproc.Process.JNIAPI_METHOD_FAKE3D;

            try {
                final ProcessByte res = (ProcessByte) Process.byteJniApiMethod(jin);
                if (res.retval == 0 && res.iparams != null && res.iparams[3] != 0) {
                    drawRect(res.iparams, previewSize);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    class SaveCount {
        long countDown;
    }

    private void drawRect(int[] rect/*Rect rect*/, Camera.Size size) {
        float width = activityAi360Binding.surfaceview.getWidth();
        float scale = width / (float) size.height;
        showcutRect = new RectF(rect[0] * scale, rect[1] * scale, (rect[0] + rect[2]) * scale, (rect[1] + rect[3]) * scale);
        cutrect = new int[4];
        cutrect[0] = rect[0];
        cutrect[1] = rect[1];
        cutrect[2] = rect[2];
        cutrect[3] = rect[3];
        // activityAi360Binding.drawRect.bringToFront();
        //activityAi360Binding.drawRect.setDrawRect(showcutRect);
    }

    private void showBTConnectView() {
        if (isCanClick) {
            if (!presenter.checkGPSIsOpen()) {
                ToastUtil.toast(getResources().getString(R.string.request_gps));
                return;
            }
            if (!BleConnectModel.getBleConnectModelInstance().isBlueEnable()) {
                activityAi360Binding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CLOSE);
            } else {
                activityAi360Binding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_OPEN);
                if (BleConnectModel.getBleConnectModelInstance().isCurrentConnect()) {
                    activityAi360Binding.blueToothListView.setConnectBluetoothName(BleConnectModel.getBleConnectModelInstance().getCurrentBlueToothDevice().getName());
                    activityAi360Binding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CONNECTION);
                } else {
                    BleConnectModel.getBleConnectModelInstance().startScanBlueTooth();
                }
            }

            if (mCamera != null)
                mCamera.onPause();
            activityAi360Binding.surfaceview.setVisibility(View.GONE);
            activityAi360Binding.presetBottomView.setVisibility(View.GONE);
            activityAi360Binding.blueToothListView.setApplyBlur();
            activityAi360Binding.blueToothListView.setVisibility(VISIBLE);
        }
    }

    private void dismissBTConnectView() {
        if (activityAi360Binding.aiHintView.getVisibility() != VISIBLE) {
            resumeMainCamera();
            // cameraController.onResume();
            activityAi360Binding.surfaceview.setVisibility(View.VISIBLE);
            activityAi360Binding.presetBottomView.setVisibility(VISIBLE);
            activityAi360Binding.blueToothListView.setApplyBlur();
            activityAi360Binding.blueToothListView.setVisibility(View.GONE);
        } else {
            if (activityAi360Binding.aiHintView.getCurrentType() == AIHintView.AI360_ERROR) {
                //setMarket();
            } else {
                initCamera();
                activityAi360Binding.aiHintView.dismiss();
            }
        }
        //cameraController.setIdentfyStatus(true);
    }

    /**
     * 蓝牙断开的提示
     */
    private void showBtDialog() {
        if (activityAi360Binding.blueToothListView.getVisibility() == VISIBLE)
            return;
        if(null==mchoiceDialog){
            mchoiceDialog = ChoiceDialog.createInstance(AI360CameraActivity.this);
        }
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

    @Override
    protected void onDestroy() {
        if (mCamera != null)
            mCamera.onDestroy();
        resetJni();
        presenter.onDestroy();
        activityAi360Binding.panoramaEdit.onDestroy();
        if (ai360Thread != null) {
            Thread dummy = ai360Thread;
            ai360Thread = null;
            dummy.interrupt();
        }
        super.onDestroy();
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

    /**
     * 黑色遮罩
     */
    private void setShade() {
        int deviceWidth = ContentUtil.getScreenWidth(AI360CameraActivity.this);
        int deviceHeight = ContentUtil.getScreenHeight(AI360CameraActivity.this) - ContentUtil.getStatusBarHeight(AI360CameraActivity.this);
        int titleHeight = ContentUtil.dip2px(AI360CameraActivity.this, 58);//titleview的高度
        int padding = ContentUtil.dip2px(AI360CameraActivity.this, 27) * 2;
        int square = deviceHeight - deviceWidth - titleHeight + padding;

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, square);
        params.bottomToBottom = 0;
        activityAi360Binding.llBottom.setLayoutParams(params);
        ConstraintLayout.LayoutParams pre360param = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pre360param.topToBottom = 0;
        pre360param.setMargins(0, titleHeight, 0, square);
        activityAi360Binding.ai360preview.setLayoutParams(pre360param);

        ConstraintLayout.LayoutParams bottomShadeParams = (ConstraintLayout.LayoutParams) activityAi360Binding.bottomShade.getLayoutParams();
        bottomShadeParams.height = (square / 3) * 2 - ContentUtil.dip2px(AI360CameraActivity.this, 10);
        activityAi360Binding.bottomShade.setLayoutParams(bottomShadeParams);
    }

    @Override
    protected void initData() {
//        initCamera();
        presenter.setLightRationListData(AI360CameraActivity.this, null);

        presetParmsSqlControl = new PresetParmsSqlControl(this);
        startQueryByShowType();

        if (CommendManage.getInstance().getVersion() == CommendManage.VERSION_BOX) {

            activityAi360Binding.presetBottomView.setVisibility(View.GONE);
        }
        BamImageView.TouchInter touchInter = new BamImageView.TouchInter() {
            @Override
            public void onClick() {
                if (isCanClick) {
                    activityAi360Binding.videoButtonBtn.setVisibility(View.VISIBLE);
                    activityAi360Binding.videoButtonBtn.bringToFront();
                    activityAi360Binding.videoButtonBtn.startArcAnim(DURATION);
                }
            }
        };
        activityAi360Binding.cameraMainBtn.setOnTouchInterface(touchInter);

        activityAi360Binding.focusView.setRoundArcListener(new FocusView.RoundArcListener() {
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
        activityAi360Binding.focusView.setFouceListener(new FocusView.FouceListener() {
            @Override
            public void endMove(MotionEvent event) {

            }

            @Override
            public void onDown(MotionEvent event) {
                int marginW = (int) event.getX();
                int marginH = (int) event.getY();

                if (!isAniming) {
                    // focusView.bringToFront();
                    activityAi360Binding.focusView.invalidate();
                    activityAi360Binding.focusView.setCenterX(marginW, marginH, false);
                    activityAi360Binding.focusView.setScaleAnim();
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            mCamera.pointFocus(event.getX(), event.getY(), activityAi360Binding.surfaceview.getWidth(), activityAi360Binding.surfaceview.getHeight());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartUpDown(MotionEvent motionEvent) {

            }

            @Override
            public void onEndUpDown(MotionEvent motionEvent) {

            }

            @Override
            public void onUpDown(MotionEvent motionEvent, float addNum) {

            }

            @Override
            public void onClick(MotionEvent event) {

            }

            @Override
            public void onLongClick(MotionEvent event) {

            }

            @Override
            public void onLongClickCancel() {

            }
        });


        activityAi360Binding.aiHintView.bringToFront();

        activityAi360Binding.blueToothListView.initParm(presenter.getAdapter(), new BlueToothListNewView.BlueToothListInterface() {
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
                dismissBTConnectView();
                showLoading();
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
        if (BleConnectModel.getBleConnectModelInstance().getMotorStatus() == CommendManage.MOTOROFFLINE) {
            currentStatus = CommendManage.MOTOROFFLINE;
            //转盘离线
            activityAi360Binding.aiHintView.setType(AIHintView.AI360_ERROR);
            // activityAi360Binding.aiHintView.setType(AIHintView.AI360_SUCCESS);
            activityAi360Binding.aiHintView.setVisibility(VISIBLE);
        } else {
            currentStatus = CommendManage.MOTORONLINE;
            activityAi360Binding.aiHintView.setType(AIHintView.AI360_SUCCESS);
            SharePreferences preferences = SharePreferences.instance();
            int Count = preferences.getInt(PreferenceConstants.AI360COUNT, 20);
            if (Count != 0) {
                Count--;
                activityAi360Binding.aiHintView.setVisibility(VISIBLE);
                preferences.putInt(PreferenceConstants.AI360COUNT, Count);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initCamera();
                    }
                }, 500);
                activityAi360Binding.aiHintView.setVisibility(GONE);
            }
        }
        activityAi360Binding.aiHintView.setInterface(new AIHintView.AIHintCallback() {

            @Override
            public void onClose() {
                finish();
            }

            @Override
            public void onClick() {
                if (activityAi360Binding.aiHintView.getCurrentType() == AIHintView.AI360_ERROR) {
                    setMarket();
                } else {
                    initCamera();
                    activityAi360Binding.aiHintView.dismiss();
                }
            }

            @Override
            public void onCobox() {
                if (activityAi360Binding.aiHintView.getCurrentType() == AIHintView.AI360_ERROR) {
                    showBTConnet();
                }
            }
        });
        activityAi360Binding.panoramaEdit.setFilterWait();
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ContentUtil.getScreenWidth(AI360CameraActivity.this), (int) (ContentUtil.getScreenWidth(AI360CameraActivity.this) * (4f / 3f)));
        activityAi360Binding.photoViewlayout.setLayoutParams(layoutParams);
        setShade();

        activityAi360Binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCanClick)
                    finish();
            }
        });
        activityAi360Binding.ivCobox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBTConnet();
            }
        });

        activityAi360Binding.blueToothListView.setCloseConnecrtion(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getBleConnectModel().disConnectCurrent();
//                presenter.choiceDeivce(presenter.getSelectPosition());
            }
        });
        activityAi360Binding.presetBottomView.setPresetInterface(this);

        set360Hint(0);

    }


    @Override
    protected void onResume() {
        if (activityAi360Binding.blueToothListView.getVisibility() == VISIBLE) {
            dismissBTConnectView();
        }
        resumeMainCamera();
        //cameraController.onResume();
        super.onResume();
    }

    @Override
    protected void onViewClick() {
        activityAi360Binding.panoramaEdit.setActivity(this);
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
                            String.valueOf(CommendManage.getInstance().getVersion())) || presetParms.get(i).getId().equals("1"))) {
                        PresetItem presetItem = new PresetItem(presetParms.get(i).getName(), presetParms.get(i).getTextSrc(), presetParms.get(i).getPresetId());
                        presetItem.setPresetParm(presetParms.get(i));
                        presetItems.add(presetItem);
                    }
                }
//                presetItems.add(new PresetItem("help", "help"));
                activityAi360Binding.presetBottomView.addData(presetItems);
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

        activityAi360Binding.horizontalscalescrollview.setOnScrollListener(new BaseScaleView.OnScrollListener() {
            @Override
            public void onScaleScroll(int scale) {
                if (mScale == scale) {
                    return;
                }
                mScale = scale;
                float currentAeNum = (cameraEv.maxEv / (float) activityAi360Binding.horizontalscalescrollview.getMax() * scale);
                mCamera.setEv((int) currentAeNum);
                // cameraController.setAe((int) currentAeNum);
            }
        });

        activityAi360Binding.panoramaEdit.setPanoramaListener(new PanoramaEditView.PanoramaListener() {
            @Override
            public void onProcessCrop() {
                //yuv转位图
                if (!isRecording)
                    showLoading();
            }

            @Override
            public void onProcessOver() {
                if (!isRecording) {
                    set360Hint(0);
                    isCanClick = true;
                    activityAi360Binding.panoramaEdit.setAdapter();
                    //转换完毕
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCamera.onPause();
                            activityAi360Binding.panoramaEdit.show(true);
                            activityAi360Binding.panoramaEdit.setCoverFilter();
                            activityAi360Binding.panoramaEdit.initFilter();
                            hideLoading();
                        }
                    });
                }
            }

            @Override
            public void onClose() {
                isCanClick = true;
                if (!activityAi360Binding.panoramaEdit.isSaveFile())
                    deleteCacheFolder();
                creatNewFolder();
                activityAi360Binding.panoramaEdit.onDestroy();
            }

            @Override
            public void saveAndExit(final String smallUrl, final String localUrl, final String title) {
                isCanClick = true;
                dgProgressDialog3 = new DGProgressDialog3(AI360CameraActivity.this, true, "后台系统正在生成图片中…");
                dgProgressDialog3.setShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        presenter.saveAI360Camera(smallUrl, localUrl, title);
                    }
                });
                dgProgressDialog3.showDialog();
            }

            @Override
            public void onPanoramaEditViewGone() {
                isCanClick = true;
                if (mCamera != null)
                    mCamera.onResume();
            }

            @Override
            public void FilterBitmapFin() {
                if (!isRecording) {
                    isCanClick = true;
                    //渲染图片完毕
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activityAi360Binding.panoramaEdit.disMissFilterWait();
                        }
                    });
                }
            }
        });

        activityAi360Binding.videoButtonBtn.setVideoButtonInter(new VideoButtonView.VideoButtonInter() {
            @Override
            public void startVideo() {
                //重置算法
                resetJni();
                savePoint = 0;
                if (isCanClick) {
                    isCanClick = false;
                }
                mCamera.setPreviewCallback();
                set360Hint(1);
                AI360CameraActivity.this.countDown = 0;
            }

            @Override
            public void CountdownFin() {
                saveCount = new SaveCount();
                //开启转盘
                set360Hint(2);
                presenter.setMotorStatus(true);
                /**
                 * 21秒36张图片，平均21*1000/36获取一张
                 */
                countDownTimer = new CountDownTimer(DURATION * 1000, DURATION * 1000 / TOTALPIC) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        countDown = millisUntilFinished;
                    }

                    @Override
                    public void onFinish() {
                        activityAi360Binding.videoButtonBtn.resetArcAnim();
                        isRecording = false;
                        //重置算法
                        resetJni();
                        countDown = DURATION * 1000;
                        //停止转盘
                        presenter.setMotorStatus(false);
                    }
                };
                countDownTimer.start();
                isRecording = true;
                startRecordTime = System.currentTimeMillis();
                activityAi360Binding.drawRect.setVisibility(VISIBLE);
                activityAi360Binding.drawRect.setLayoutParams(activityAi360Binding.surfaceview.getLayoutParams());
                activityAi360Binding.llBottom.bringToFront();

            }

            @Override
            public void videoFin() {
                // showDialog();
            }
        });
    }

    private void resetJni() {
        int parm[] = new int[3];
        parm[0] = (Process.USAGE_UINIT);
        parm[1] = (Process.BC_PLATEFORMINFO_ANDROID);
        parm[2] = (Process.BC_C0MB0X_VERSI0N_C2);
        final ProcessByte jin = new ProcessByte();
        jin.iparams = parm;
        jin.img_width = 1920;
        jin.img_height = 1080;
        jin.srcbuf = new byte[64];
        jin.api_method = com.example.imageproc.Process.JNIAPI_METHOD_FAKE3D;
        final ProcessByte res = (ProcessByte) com.example.imageproc.Process.byteJniApiMethod(jin);
    }

    private void dismissBTDialog() {
        if (mchoiceDialog != null) {
            mchoiceDialog.dismiss();
        }
    }

    private void deleteCacheFolder() {
        FileUtil.deleteDir(Environment.getExternalStorageDirectory() + SAVEPANORAMAS);
        creatNewFolder();
        savePoint = 0;
    }

    private void creatNewFolder() {
        //创建新目录
        SAVEPANORAMAS = Flag.BaseData + "/Panoramas/" + System.currentTimeMillis() + "/";
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRecording) {
            isRecording = false;
            activityAi360Binding.videoButtonBtn.resetArcAnim();
            resetJni();
            startRecordTime = -1;
            countDownTimer.cancel();
            deleteCacheFolder();
        }
        if (mCamera != null)
            mCamera.onPause();
    }

    private void resumeMainCamera() {
        if (mCamera != null) {
            mCamera.onResume();
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Flag.CAMERATYPE, GPUImageCameraLoader.VIDE0_TYPE + "");
            mCamera.setCameraParamsAI(hashMap);
            Camera.Parameters parameters = mCamera.getCurrentparameter();
            if (cameraEv == null) {
                cameraEv = new CameraEV();
                cameraEv.currentEv = parameters.getExposureCompensation();
                cameraEv.evStep = parameters.getExposureCompensationStep();
                cameraEv.maxEv = parameters.getMaxExposureCompensation();
                cameraEv.minEv = parameters.getMinExposureCompensation();
            }
            mCamera.setGpuimagePreviewCallback(new GPUImageCameraLoader.GpuImagePreviewCallback() {
                @Override
                public synchronized void onPreViewFrame(byte[] data, Camera camera) {
                    count++;
                    //识别图片
                    if (count % 3 == 0 && isRecording && data != null) {
                        if (previewSize == null) {
                            previewSize = camera.getParameters().getPreviewSize();
                        }
                        if (ai360Thread == null) {
                            ai360Thread = new AI360Thread();
                        }
                        ai360Thread.data = data;
                        ai360Thread.run();
                    }
                    if (startRecordTime != -1 && saveCount != null && data != null) {
                        if (previewSize == null) {
                            previewSize = camera.getParameters().getPreviewSize();
                        }
                        if (saveCount.countDown != AI360CameraActivity.this.countDown) {
                            saveCount.countDown = AI360CameraActivity.this.countDown;
                            savePoint++;
                            FileUtil.save2Txts(data, SAVEPANORAMAS, "IMG_" + savePoint + ".yuv");
                            if (ai360Thread == null) {
                                ai360Thread = new AI360Thread();
                            }
                            ai360Thread.data = data;
                            ai360Thread.run();
                            if (AI360CameraActivity.this.countDown == DURATION * 1000) {
                                startRecordTime = -1;
                                if (cutrect == null || cutrect[3] == 0) {
                                    ToastUtil.toast("检测失败，请重试！");
                                    isCanClick = true;
                                    set360Hint(0);
                                    deleteCacheFolder();
                                } else {
                                    //开始yuv转rgb
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            activityAi360Binding.panoramaEdit.getFileName(Environment.getExternalStorageDirectory() + SAVEPANORAMAS, new Size(previewSize.height, previewSize.width), cutrect);
                                        }
                                    }, 1000);
                                }

                                // activityAi360Binding.panoramaEdit.getFileName(Environment.getExternalStorageDirectory() + SAVEPANORAMAS, new Size(previewSize.height, previewSize.width), cutrect);
                                activityAi360Binding.drawRect.setVisibility(GONE);
                            }
                        }
                    }
                }

                @Override
                public void onTakePhoto(byte[] data, Camera camera) {

                }
            }, true);

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
        if (b) {
            dgProgressDialog3.dismiss();
        }
       /* if (!b) {
            showLoading();
        } else {
            hideLoading();
        }*/
    }

    @Override
    public void showToast(ImageData imageData, String str) {

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
//                activityAi360Binding.blueToothListView.close();
              /*  if (presenter.getCurrentCoboxVer() == CommendManage.VERSION_BOX) {
                    activityAi360Binding.presetBottomView.setVisibility(View.GONE);
                    activityAi360Binding.llBottom.setGravity(Gravity.CENTER);
                    //当前连接设备为商拍魔盒时，将灯光调整到最亮
                    presenter.writeAllLightNumber(100, 100, 100, 100, 100, 100, 0, 0);
                }*/
                break;
            case CoBoxChooseTopView.BLUE_TOUCH_CONNECTION_ERROR:
                BTStatus = false;
                presenter.refreshLightBlueTouch();
                //蓝牙断开
                showBtDialog();
                break;
        }
        activityAi360Binding.blueToothListView.setBlueTouchType(type);

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
    public Handler getHandler() {
        return super.getHandler();
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
    public void setMotorStatus(final int status) {
        if (currentStatus == CommendManage.MOTOROFFLINE) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    reset(status);
                }
            });
        }
        currentStatus = status;
        switch (status) {
            case CommendManage.MOTORON:
                break;
            case CommendManage.MOTOROFF:
                break;
            case CommendManage.MOTOROFFLINE:
                break;
        }
    }

    @Override
    public SurfViewCameraPresenter initPresenter() {
        return new SurfViewCameraPresenter();
    }

    @Override
    public void onBackPressed() {
        if (activityAi360Binding.panoramaEdit.getVisibility() == VISIBLE) {
            activityAi360Binding.panoramaEdit.onBackPress();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void presetItemClick(int position, PresetHorizontalListAdapter.
            ViewHolder viewHolder) {
        activityAi360Binding.presetBottomView.setSelect(position);
        ToastUtil.toast(presetItems.get(position).getName());
        presenter.setGPUImageFilter(presetItems.get(position).getPresetParm());
        presenter.getImageData().setPresetParms(presetItems.get(position).getPresetParm());
    }


    public class CameraEV {
        float currentEv;
        float evStep;
        float maxEv;
        float minEv;
    }

}
