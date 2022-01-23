package com.bcnetech.hyphoto.presenter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Range;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetchhttp.RetrofitFactory;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.GLModel.MyGLRenderer;
import com.bcnetech.bcnetechlibrary.bean.CameraParm;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.bcnetechlibrary.util.TimeUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.util.sharepreference.SharePreferences;
import com.bcnetech.hyphoto.App;
import com.bcnetech.hyphoto.data.CameraParamType;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.ImageDataSqlControl;
import com.bcnetech.hyphoto.data.UploadCloudData;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.listener.RecyclerItemClickListener;
import com.bcnetech.hyphoto.model.BleConnectModel;
import com.bcnetech.hyphoto.model.imodel.ISurfViewCameraModel;
import com.bcnetech.hyphoto.presenter.iview.ISurfViewCameraView;
import com.bcnetech.hyphoto.receiver.AddCloudReceiver;
import com.bcnetech.hyphoto.receiver.AddPicReceiver;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.ui.activity.camera.SurfViewCameraActivity;
import com.bcnetech.hyphoto.ui.activity.camera.SurfViewCameraNewActivity;
import com.bcnetech.hyphoto.ui.adapter.CameraParamTouchAdapter;
import com.bcnetech.hyphoto.ui.adapter.SurfBlueToothPopAdapter;
import com.bcnetech.hyphoto.ui.adapter.SurfLightBlueTouchAdapter;
import com.bcnetech.hyphoto.ui.popwindow.LightPenPop;
import com.bcnetech.hyphoto.ui.view.CoBoxChooseTopView;
import com.bcnetech.hyphoto.utils.CameraParamUtil;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;
import com.bcnetech.bizcamerlibrary.camera.utils.CameraUtils;
import com.bcnetech.bluetoothlibarary.bluetoothagreement.CommendManage;
import com.bcnetech.bluetoothlibarary.bluetoothbroadcast.BlueToothStatusReceiver;
import com.bcnetech.bluetoothlibarary.data.CommendItem;
import com.bcnetech.hyphoto.R;
import com.lansosdk.videoeditor.LanSoEditor;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageCameraFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;

/**
 * Created by wenbin on 2017/2/24.
 */

public class SurfViewCameraPresenter extends BasePresenter<ISurfViewCameraView> implements ISurfViewCameraModel {

    private final static int RECODER_SIZE_W = 600;
    private final static int RECODER_SIZE_H = 800;

    private final static double CAMERA_SIZE = 4.0 / 3.0;//长：宽
    private final static int ROTATION_MIN = 10;//最小限制
    private final static int ROTATION_MAX = 80;//最大限制
    private final static int ROTATION_LIMIT = 90;//旋转角度
    private final static int MAX_ROTATION_LIMIT = 360;//旋转最大角度
    private final static int MIN_ROTATION_LIMIT = 0;//旋转最小角度
    //指针当前度数
    private float currentRotation_sublien = 0;
    private int currentRotation = 0;
    private static final boolean STARTCHANG = true;
    private static final boolean CLOSECHANG = false;
    public static final int NUL_DATA = 255;//在线状态  为不在线时数据
    public static final int ONLINE_ON = 1;//在线
    public static final int ONLINE_OFF = 0;//不在线
    private boolean canClick;
    private int currentNum;
    private int currentParamNum;
    private LightRatioData mCurrentLightRatio;
    private ImageDataSqlControl imageDataSqlControl;
    //  private LightRatioSqlControl lightRatioSqlControl;
    // private int selectPosition = -1;
    private ImageData imageData;

    private MyGLRenderer myGLRenderer;

    //蓝牙列表
    // private List<BlueToothItemData> deviceList;
    //蓝牙列表adapter
    private SurfBlueToothPopAdapter adapter;

    private ValueAnimator valueAnimator, sublineValueAnimator;

    private boolean startChang;
    private List<SurfLightBlueTouchAdapter.Item> itemList;
    private SurfLightBlueTouchAdapter lightBlueTouchAdapter;
    private List<CameraParamTouchAdapter.Item> cameraItemList;
    private CameraParamTouchAdapter cameraParamTouchAdapter;
    private RecyclerView lightRecyclerView;
    private RecyclerView cameraParamView;
    //当前调节灯
    private int selectedLightNum = -1;
    private int selectedParamNum = -1;
    private MyHandler handler;
    private MyFocusHandler myFocusHandler;

    private GPUImage gpuImage;
    // private EditDialog editDialog;
    private LightPenPop lightPenPop;
    private List<LightRatioData> light_list;

    private boolean isFirstIn;

    //地址
    private String locations = "";
    //经度
    private String longitude = "";
    //纬度
    private String latitude = "";

    private LinearLayoutManager layoutManager;
    private LinearLayoutManager cameraLayoutManager;


    private HashMap<String, String> hashMap;


    private static CameraParamType cameraParamExposureTime;
    private static CameraParamType cameraParamISO;
    private static CameraParamType cameraParamWHITEBALANCE;
    private static CameraParamType cameraParamFOCUS;

    private boolean isLockCamera = false;
    private boolean isLockFocusCamera = false;
    private Bitmap scabitmap;
    public static int sec_num;
    public double CurrentCOBOXVer = 0;
    private BleConnectModel bleConnectModel;
    private BlueToothStatusReceiver blueToothStatusReceiver;

    /***
     * @param activity
     */
    public static void startAction(Activity activity/*, int code*/) {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // your code using Camera API here - is between 1-20
            intent = new Intent(activity, SurfViewCameraNewActivity.class);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // your code using Camera2 API here - is api 21 or higher
            if (LoginedUser.getLoginedUser().isSupportCamera2()) {
                intent = new Intent(activity, SurfViewCameraActivity.class);
            } else {
                intent = new Intent(activity, SurfViewCameraNewActivity.class);
            }
        }
        activity.startActivity(intent);
       /* intent.putExtra(Flag.CAMERA_TYPE, cameraType);
        intent.putExtra("currneturl", currenturl);*/
      /*  if (code == Flag.NULLCODE) {
            activity.startActivity(intent);
        } else {
            activity.startActivityForResult(intent, code);
        }*/
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Flag.IMAGE_DATA, imageData);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageData = (ImageData) savedInstanceState.getSerializable(Flag.IMAGE_DATA);
    }

    @Override
    public void onCreate(Bundle bundle) {
        newCameraControl();
        if (bundle != null) {
            imageData = (ImageData) bundle.getSerializable(Flag.IMAGE_DATA);
        }
        if (imageData == null) {
            imageData = new ImageData();
        }

        if (light_list == null) {
            light_list = new ArrayList<>();
        }
        //lightRatioSqlControl.startQuery();
        LanSoEditor.initSDK(activity);

        SharePreferences preferences = SharePreferences.instance();
        isFirstIn = preferences.getBoolean("isFirstIn", true);

        if (LoginedUser.getLoginedUser().isSupportCamera2()) {
            handler = new MyHandler((SurfViewCameraActivity) activity);
            myFocusHandler = new MyFocusHandler((SurfViewCameraActivity) activity);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        activity.registerReceiver(blueToothStatusReceiver
                , BleConnectModel.makeFilter());
        canClick = true;
    }




    public ImageData getImageData() {
        return imageData;
    }

    public void setImageData(ImageData imageData) {
        this.imageData = imageData;
    }


    @Override
    public void onDestroy() {
//        model.dettach();
        if (bleConnectModel != null) {
            bleConnectModel.stopSearch();
            bleConnectModel.onDestroy();
        }
        if (blueToothStatusReceiver != null)
            activity.unregisterReceiver(blueToothStatusReceiver);
        stopBlueToothService();


        if (gpuImage != null) {
            gpuImage.releaseCamera();
        }
    }

    public RelativeLayout.LayoutParams initCameraView() {
        int w = ContentUtil.getScreenWidth(activity);
        int h = (int) (w * CAMERA_SIZE);
        return new RelativeLayout.LayoutParams(w, h);
    }

    public void rotationViewAnim(int orientation) {
        int orientationValue = Math.abs(orientation - currentRotation);
//        LogUtil.d("orientationValue"+orientationValue);
        if ((orientationValue % ROTATION_LIMIT > ROTATION_MAX
                || orientationValue % ROTATION_LIMIT < ROTATION_MIN)
                && orientationValue > ROTATION_MAX
                && orientationValue < MAX_ROTATION_LIMIT - ROTATION_MAX
        ) {
            orientation += ROTATION_MIN;
            orientation = orientation / ROTATION_LIMIT * ROTATION_LIMIT;
            if (orientation == MAX_ROTATION_LIMIT) {
                orientation = MIN_ROTATION_LIMIT;
            }
            if(null!=mView){
                mView.initRotationAnim(-currentRotation, -orientation);
            }
            currentRotation = orientation;
        }
    }

    float angele = 0;

    public void rotation(float xDegrees, float yDegrees) {
        if (mView == null)
            return;
        if (currentRotation_sublien == 0) {
            if (currentRotation == 0) {
                xDegrees = 180f;
            } else if (currentRotation == 90) {
                xDegrees = 90f;
            } else if (currentRotation == 180) {
                xDegrees = 0f;
            } else if (currentRotation == 270) {
                xDegrees = 90f;
            }
        } else {
            if (Math.abs(Math.abs(xDegrees)) >= 178) {
                xDegrees = 180f;
            }
            if (Math.abs(Math.abs(xDegrees)) <= 2) {
                xDegrees = 0f;
            }
            if (Math.abs(Math.abs(xDegrees) - 90) <= 2) {
                xDegrees = 90f;
            }
        }
        mView.rotationSubline(xDegrees, yDegrees, angele);
        angele = xDegrees;
    }


    public void rotationSubline(float orientation_subline, float xAngele) {
        if (mView != null) {
            mView.initRotationSubline(currentRotation, xAngele, orientation_subline);
            currentRotation_sublien = (int) xAngele;
        }
    }

    public void goToRecoderActivity(String url, String bitmapUrl) {
        if (imageData == null) {
            imageData = new ImageData();
        }
        getLightRation();
        imageData.setLightRatioData(mCurrentLightRatio);
        imageData.setType(Flag.TYPE_VIDEO);
        if (url.contains("file:/"))
            url = url.substring(7);
        imageData.setLocalUrl(url);
        if (!bitmapUrl.contains("file:/"))
            bitmapUrl = "file:/" + bitmapUrl;
        imageData.setSmallLocalUrl(bitmapUrl);
        imageData.setTimeStamp(System.currentTimeMillis());
        if (activity != null)
            RecoderPresenter.startAction(activity, imageData, Flag.RECODER_TYPE, Flag.NULLCODE);
    }

    public void resetLightRationListData() {
        itemList = new ArrayList<>();
        switch ((int) CurrentCOBOXVer) {
            case CommendManage.VERSION1_1:
            case CommendManage.VERSION2_1:
                itemList.add(new SurfLightBlueTouchAdapter.Item(App.getInstance().getString(R.string.left_light), -1));
                itemList.add(new SurfLightBlueTouchAdapter.Item(App.getInstance().getString(R.string.right_light), -1));
                itemList.add(new SurfLightBlueTouchAdapter.Item(App.getInstance().getString(R.string.bottom_light), -1));
                itemList.add(new SurfLightBlueTouchAdapter.Item(App.getInstance().getString(R.string.background_light), -1));
                itemList.add(new SurfLightBlueTouchAdapter.Item(App.getInstance().getString(R.string.head_light), -1));
                itemList.add(new SurfLightBlueTouchAdapter.Item(App.getInstance().getString(R.string.top_light), -1));
                break;
            case CommendManage.VERSION_BOX:
                itemList.add(new SurfLightBlueTouchAdapter.Item("L1", -1));
                itemList.add(new SurfLightBlueTouchAdapter.Item("L2", -1));
                itemList.add(new SurfLightBlueTouchAdapter.Item("L3", -1));
                itemList.add(new SurfLightBlueTouchAdapter.Item("L4", -1));
                break;

        }
        lightBlueTouchAdapter.setData(itemList);
        lightBlueTouchAdapter.notifyDataSetChanged();
    }

    public void setLightRationListData(Context context, RecyclerView recyclerView) {
        itemList = new ArrayList<>();
        lightRecyclerView = recyclerView;
        itemList.add(new SurfLightBlueTouchAdapter.Item(context.getResources().getString(R.string.left_light), -1));
        itemList.add(new SurfLightBlueTouchAdapter.Item(context.getResources().getString(R.string.right_light), -1));
        itemList.add(new SurfLightBlueTouchAdapter.Item(context.getResources().getString(R.string.bottom_light), -1));
        itemList.add(new SurfLightBlueTouchAdapter.Item(context.getResources().getString(R.string.background_light), -1));
        itemList.add(new SurfLightBlueTouchAdapter.Item(context.getResources().getString(R.string.head_light), -1));
        itemList.add(new SurfLightBlueTouchAdapter.Item(context.getResources().getString(R.string.top_light), -1));

        lightBlueTouchAdapter = new SurfLightBlueTouchAdapter(App.getInstance(), itemList, new SurfLightBlueTouchAdapter.ClickListener() {
            @Override
            public void click(int position) {

                if (mView.getCameraType() == SurfViewCameraActivity.TYPE_PRO_M) {
                    refreshCameraParamTouch();
                }

                for (int i = 0; i < itemList.size(); i++) {
                    if (position == i) {
                        itemList.get(i).setClick(true);
                    } else {
                        itemList.get(i).setClick(false);
                    }
                }
                lightBlueTouchAdapter.notifyDataSetChanged();
                selectedLightNum = position;
                mView.setCameraType(SurfViewCameraActivity.TYPE_PRO_L);

                mView.setProgressAndDefaultPoint(itemList.get(selectedLightNum).getNumber(), 0, null);
//                changeLight(itemList.get(selectedLightNum).getNumber());
            }


        });
        layoutManager = new LinearLayoutManager(App.getInstance(), LinearLayoutManager.HORIZONTAL, false);
        if (lightRecyclerView != null) {
            lightRecyclerView.setLayoutManager(layoutManager);
            lightRecyclerView.setHasFixedSize(true);
            lightRecyclerView.setAdapter(lightBlueTouchAdapter);
        }
    }


    public void setCameraParamMListData(Context context, RecyclerView recyclerView) {
        if (null == cameraItemList) {
            cameraItemList = new ArrayList<>();
            initCameraParamType();

            cameraParamView = recyclerView;
            cameraItemList.add(new CameraParamTouchAdapter.Item(context.getResources().getString(R.string.shutter), 0, cameraParamExposureTime));
            cameraItemList.add(new CameraParamTouchAdapter.Item(context.getResources().getString(R.string.iso), 0, cameraParamISO));
            cameraItemList.add(new CameraParamTouchAdapter.Item(context.getResources().getString(R.string.white_balance), 0, cameraParamWHITEBALANCE));
            cameraItemList.add(new CameraParamTouchAdapter.Item(context.getResources().getString(R.string.focus_len), 0, cameraParamFOCUS));


            cameraParamTouchAdapter = new CameraParamTouchAdapter(context, cameraItemList, new CameraParamTouchAdapter.ClickListener() {
                @Override
                public void click(int position) {
                    itemClick(position);
                }


            });
            cameraLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(cameraLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(cameraParamTouchAdapter);
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(new RecyclerItemClickListener.OnItemClickListener() {


                @Override
                public void onItemLongClick(View view, int position) {

                }

                @Override
                public void onItemDoubleClick(View view, int position) {


                    if (selectedParamNum == 3) {
                        // selectedParamNum = -1;
                        unLockFocusCameraParam("auto");
                    }
                }
            }));
        }

    }


    public void itemClick(int position) {
        if (mView.getCameraType() == SurfViewCameraActivity.TYPE_PRO_L) {
            refreshLightBlueTouch();
        }

        for (int i = 0; i < cameraItemList.size(); i++) {
            if (position == i) {
                cameraItemList.get(i).setClick(true);
            } else {
                cameraItemList.get(i).setClick(false);
            }
        }

        cameraParamTouchAdapter.notifyDataSetChanged();
        selectedParamNum = position;

        if (mView.getCameraType() != SurfViewCameraActivity.TYPE_M) {
            mView.setCameraType(SurfViewCameraActivity.TYPE_PRO_M);
        }
        int num = cameraItemList.get(selectedParamNum).getNumber();

        if (selectedParamNum == 0) {
            mView.setProgressAndDefaultPoint(num, num, cameraParamExposureTime);
//            mView.setAeNum(100, 0, cameraParamExposureTime, num);
        } else if (selectedParamNum == 1) {
            mView.setProgressAndDefaultPoint(num, num, cameraParamISO);
//            mView.setAeNum(100, 0, cameraParamISO, num);
        } else if (selectedParamNum == 2) {
            mView.setProgressAndDefaultPoint(num, num, cameraParamWHITEBALANCE);
//            mView.setAeNum(100, 0, cameraParamWHITEBALANCE, num);
        } else if (selectedParamNum == 3) {
            mView.setProgressAndDefaultPoint(num, num, cameraParamFOCUS);
//            mView.setAeNum(100, 0, cameraParamFOCUS, num);
            lockFocusCameraParam();
        }
    }


    public void itemClick() {
        if (mView.getCameraType() == SurfViewCameraActivity.TYPE_PRO) {
            refreshCameraParam();
            refreshLightBlueTouch();
        }
        cameraItemList.get(0).setClick(true);

        cameraParamTouchAdapter.notifyDataSetChanged();
        selectedParamNum = 0;

        if (mView.getCameraType() != SurfViewCameraActivity.TYPE_M) {
            mView.setCameraType(SurfViewCameraActivity.TYPE_PRO_M);
        }
        int num = sec_num;

        if (selectedParamNum == 0) {
            mView.setProgressAndDefaultPoint(cameraItemList.get(selectedParamNum).getNumber(), num, cameraParamExposureTime);

//            mView.setAeNum(100, 0, cameraParamExposureTime, num);
        }
    }


    public void startFloatAnim(AnimFactory.FloatListener floatListener) {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator = AnimFactory.rotationAnim(floatListener);
        valueAnimator.addListener(new Animator.AnimatorListener() {
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
        });
        valueAnimator.start();

    }

    public void startSublineValueAnimator(AnimFactory.FloatListener floatListener) {
        if (sublineValueAnimator != null && sublineValueAnimator.isRunning()) {
            sublineValueAnimator.cancel();
        }
        sublineValueAnimator = AnimFactory.rotationAnim(floatListener);
        sublineValueAnimator.addListener(new Animator.AnimatorListener() {
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
        });
        sublineValueAnimator.start();
    }

    public void startFloatAnim(AnimFactory.FloatListener floatListener, Animator.AnimatorListener animationListener) {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator = AnimFactory.rotationAnim(floatListener);
        valueAnimator.addListener(animationListener);
        valueAnimator.start();
    }

    public void startFloatAnim(AnimFactory.FloatListener floatListener, Animator.AnimatorListener animationListener, long time) {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator = AnimFactory.cameraAnim(floatListener, time);
        valueAnimator.addListener(animationListener);
        valueAnimator.start();
    }


    /**
     * 连接蓝牙
     *
     * @param position 下标
     */
    public void choiceDeivce(final int position) {
        if(null!=bleConnectModel&&null!=bleConnectModel.getDeviceList()&&bleConnectModel.getDeviceList().size()>0){

            if(position>=0&&position<bleConnectModel.getDeviceList().size()){

                bleConnectModel.choiceDeivce(position);
                mView.setConnectBluetoothName(bleConnectModel.getDeviceList().get(bleConnectModel.getSelectPosition()).getName());
            }else {
                ToastUtil.toast("连接失败");
                bleConnectModel.disConnectCurrent();
            }

        }else {
            ToastUtil.toast("连接失败");
            activity.finish();
        }
    }

    public double getCurrentCoboxVer() {
        return CurrentCOBOXVer;
    }

    public BleConnectModel getBleConnectModel() {
        return bleConnectModel;
    }

    /**
     * 蓝牙
     */
    public void initBleConnectModel() {
        bleConnectModel = BleConnectModel.getBleConnectModelInstance(new BleConnectModel.BleConnectStatus() {
           /* @Override
            public void onBleStatus(boolean isBleOpen) {
                if (!isBleOpen) {
                    SurfViewCameraPresenter.this.hasConnected = false;
                    mView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CLOSE);
                    ToastUtil.toast(activity.getResources().getString(R.string.open_bt));
                }
            }*/

            @Override
            public void onBleConnect(boolean isConnected, String deviceName) {
                if (isConnected) {
                    if (!SurfViewCameraPresenter.this.hasConnected) {
                        try {
                            if (null != mView) {
                                mView.setConnectBluetoothName(bleConnectModel.getDeviceList().get(bleConnectModel.getSelectPosition()).getName());
                                mView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CONNECTION);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        SurfViewCameraPresenter.this.hasConnected = true;
                    }
                } else {
                    SurfViewCameraPresenter.this.hasConnected = false;
                    setDeviceDisConnected();
                }
            }

            @Override
            public void onBleConnectError() {
                SurfViewCameraPresenter.this.hasConnected = false;
                if(null!=bleConnectModel){
                    bleConnectModel.disConnectCurrent();
                }
            }

            @Override
            public void onGetDeviceInfo(int useTime) {
                uploadDeviceConnect(useTime);
            }

            @Override
            public void onBleReceive(List<CommendItem> list) {
                try {
                    getLightNumInfo(list);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCOBOXVersion(int COBOXVer) {
                SurfViewCameraPresenter.this.CurrentCOBOXVer = COBOXVer;
                try {
                    if(null!=mView){
                        mView.startQueryByShowType();
                        resetLightRationListData();
                        mView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CONNECTION);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMotorStatus(int motorStatus) {
                try {
                    LogUtil.d("AAAAAAAAAAAAAAA");
                    if(null!=mView){
                        mView.setMotorStatus(motorStatus);
                    }
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
                // ToastUtil.toast(isMotorOnline + "");
            }

            @Override
            public void onNotifyList() {
                if (adapter == null)
                    adapter = new SurfBlueToothPopAdapter(activity);
                adapter.setData(bleConnectModel.getDeviceList());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onToast(int status) {

            }

            @Override
            public void onScanError() {
                if(null!=activity){
                    ToastUtil.toast(activity.getResources().getString(R.string.scan_error));
                }
            }
        });
        bleConnectModel.init();
        adapter = new SurfBlueToothPopAdapter(activity, bleConnectModel.getDeviceList());
        blueToothStatusReceiver = new BlueToothStatusReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (intent.getAction()) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        LogUtil.d("BlueToothStatus" + blueState);
                        switch (blueState) {
                            case BluetoothAdapter.STATE_TURNING_ON:
                            case BluetoothAdapter.STATE_ON:
                                LogUtil.d("BlueToothStatus" + "STATE_ON");
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                break;
                            case BluetoothAdapter.STATE_OFF:
                                LogUtil.d("BlueToothStatus :" + "STATE_OFF");
                                onBlueToothOff();
                                break;
                        }
                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        if (bleConnectModel != null && bleConnectModel.isCurrentConnect())
                            bleConnectModel.disConnectCurrent();
                        break;
                }


            }
        };

    }

    /**
     * 蓝牙关闭时调用方法
     */
    private void onBlueToothOff() {
        bleConnectModel.onBlueToothOff();
        SurfViewCameraPresenter.this.hasConnected = false;
        mView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CLOSE);
        ToastUtil.toast(activity.getResources().getString(R.string.open_bt));
        mView.setConnectBluetoothName(activity.getResources().getString(R.string.cobox_choose));

    }


    public boolean isHasConnected() {
        return hasConnected;
    }

    private void setDeviceDisConnected() {
        if (null == mView) {
            return;
        }
        mView.setConnectBluetoothName(activity.getResources().getString(R.string.cobox_choose));
        ToastUtil.toast(activity.getResources().getString(R.string.conn_interrupt));
        mView.setErrorConnectPresetSelect();
        CurrentCOBOXVer = 0;
        mView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CONNECTION_ERROR);
        startChang = CLOSECHANG;
        mView.hideLoading();
    }

    /**
     * 扫码连接蓝牙
     *
     * @param address
     */
    public void scanDevice(String address) {
        boolean scanResult=bleConnectModel.scanDevice(address);
        if(scanResult){
            if(null!=mView){
                if(!hasConnected){
                    mView.showLoading();
                }
                mView.setConnectBluetoothName(address);
            }
        }else {
            if(!hasConnected){
                ToastUtil.toast(activity.getResources().getString(R.string.scan_is_connection));
//                setDeviceDisConnected();
            }
        }
    }

    public void stopBlueToothService() {
        //当前没有连接蓝牙：停止蓝牙搜索
        if (!bleConnectModel.isCurrentConnect()) {
            bleConnectModel.stopSearch();
        }
    }

    private boolean hasConnected = false;

    /**
     * 第一次页面打开 开始扫描蓝牙
     */
    public void startScanBlueTooth() {
        if (bleConnectModel != null) {
            if (!bleConnectModel.isCurrentConnect())
                bleConnectModel.startScanBlueTooth();
            adapter.notifyDataSetChanged();
        }
    }


    /**
     * 获取所有灯数值
     */
    private void getLightNumInfo(List<CommendItem> list) {
        setLightNum(list);
    }

    /**
     * 发送所有灯数值
     */
    public void writeAllLightNumber(final int left, final int right, final int bottom, final int background, final int move1, final int move2, final int motor, final int dataparm) {
        bleConnectModel.WriteBleConnection(CommendManage.getInstance().getWriteParm(left, right, background, bottom, move1, move2, motor, dataparm));
        // WriteBleConnection(CommendManage.getInstance().getWriteParm(left, right, background, bottom, move1, move2, motor, dataparm), true);
        // BlueToothLoad.getInstance().writeByte(CommendManage.getInstance().getWriteParm(left, right, background, bottom, move1, move2, motor, dataparm));
        mView.hideLoading();
    }

    public void setMotorStatus(boolean isStart) {
        bleConnectModel.WriteBleConnection(CommendManage.getInstance().setMotorStatus(isStart));
    }

    /**
     * 发送单个灯数值
     *
     * @param typeLight 灯名
     * @param value     数值
     */
    private void writeNumber(final int typeLight, final int value) {
        mView.getHandler().postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                switch (typeLight) {
                    case 0:
                        bleConnectModel.WriteBleConnection(CommendManage.getInstance().getWriteLeftLand(value));
                        //  BlueToothLoad.getInstance().writeByte(CommendManage.getInstance().getWriteLeftLand(value));
                        break;
                    case 1:
                        bleConnectModel.WriteBleConnection(CommendManage.getInstance().getWriteRightLand(value));
                        // BlueToothLoad.getInstance().writeByte(CommendManage.getInstance().getWriteRightLand(value));
                        break;
                    case 2:
                        bleConnectModel.WriteBleConnection(CommendManage.getInstance().getWriteBottomLand(value));
                        //BlueToothLoad.getInstance().writeByte(CommendManage.getInstance().getWriteBottomLand(value));
                        break;
                    case 3:
                        bleConnectModel.WriteBleConnection(CommendManage.getInstance().getWriteBackGroundLand(value));
                        // BlueToothLoad.getInstance().writeByte(CommendManage.getInstance().getWriteBackGroundLand(value));
                        break;
                    case 4:
                        bleConnectModel.WriteBleConnection(CommendManage.getInstance().getWriteMoveLand(value));
                        // BlueToothLoad.getInstance().writeByte(CommendManage.getInstance().getWriteMoveLand(value));
                        break;
                    case 5:
                        bleConnectModel.WriteBleConnection(CommendManage.getInstance().getWriteMove2Land(value));
                        // BlueToothLoad.getInstance().writeByte(CommendManage.getInstance().getWriteMove2Land(value));
                        break;
                }
            }
        });
    }

    //当前灯值
    private int downLightNum;
    //当前曝光值
    private int mExposure = 50;

    private int mChangeExposure;

    public int getmExposure() {
        return mExposure;
    }

    public void setmExposure(int mExposure) {
        this.mExposure = mExposure;
    }

    /**
     * 获取当前灯值
     */
    public void getLightNum() {
        downLightNum = (itemList.get(selectedLightNum).getNumber());
    }

    public void changeExpusure(int changeNum) {

        mChangeExposure = mExposure + changeNum;
        if (mChangeExposure > 100) {
            mChangeExposure = 100;
        } else if (mChangeExposure < 0) {
            mChangeExposure = 0;
        }
        mView.setSeekBarNum(mChangeExposure);
        mView.setExposure(mChangeExposure - 50);
    }

    public void onEndUpDown() {
        mExposure = mChangeExposure;
    }


    /**
     * 改变调节灯显示的灯值
     *
     * @param changeNum 当前的灯值
     */
    public void changeLightNew(int changeNum) {
        currentNum = (downLightNum + changeNum);
//        LogUtil.d("downLightNum +changeNum"+(downLightNum +changeNum)+"downLightNum -changeNum"+(downLightNum -changeNum));
        int ccurrentNum = (downLightNum - changeNum);
        if (currentNum > 100) {
            currentNum = 100;
        } else if (currentNum < 0) {
            currentNum = 0;
        }
//        int firstItemPosition = layoutManager.findFirstVisibleItemPosition();
//        if (selectedLightNum - firstItemPosition >= 0) {
//            //得到要更新的item的view
//            View view = lightRecyclerView.getChildAt(selectedLightNum - firstItemPosition);
//            if (null!=view&&null != lightRecyclerView.getChildViewHolder(view)) {
//                SurfLightBlueTouchAdapter.ViewHolder viewHolder = (SurfLightBlueTouchAdapter.ViewHolder) lightRecyclerView.getChildViewHolder(view);
//                viewHolder.getLight_number_text().setText(currentNum + "");
//            }
//        }
        if (itemList.size() != 0) {
            itemList.get(selectedLightNum).setNumber(currentNum);
            lightBlueTouchAdapter.notifyItemChanged(selectedLightNum);
            mView.setSeekBarNum(currentNum);
            //发送指令
            writeNumber(selectedLightNum, currentNum);
        }
    }


    public void lockAllCameraParam() {
        mView.lockAllCameraParam();
    }


    public void lockFocusCameraParam() {

        hashMap = new HashMap<>();

        double frac = cameraItemList.get(3).getNumber() / (double) 100;
        double scaling = CameraUtils.seekbarScaling(frac);
        float focus_distance = (float) (scaling * getMinimumFocusDistance());
        hashMap.put(Flag.FOCUS, String.valueOf(focus_distance));
        mView.setCameraParams(hashMap);

        isLockFocusCamera = true;
        cameraParamTouchAdapter.setLockFocusCamera(isLockFocusCamera);
    }


    public void unLockFocusCameraParam(String number) {
        if (number.equals("auto") && isLockFocusCamera) {
            hashMap = new HashMap<>();
            hashMap.put(Flag.FOCUS, number);
            mView.setCameraParams(hashMap);
            isLockFocusCamera = false;
            cameraParamTouchAdapter.setLockFocusCamera(isLockFocusCamera);
            cameraParamTouchAdapter.notifyDataSetChanged();
        }
    }


    public void unLockCameraParam(String number) {

        for (int i = 0; i < cameraItemList.size(); i++) {
            cameraItemList.get(i).setClick(false);
        }
        cameraParamTouchAdapter.notifyDataSetChanged();

        selectedParamNum = -1;
        isLockFocusCamera = false;

        mView.unlockAllCameraParam();
        cameraParamTouchAdapter.setLockFocusCamera(isLockFocusCamera);

    }

    public boolean isLockCamera() {
        return isLockCamera;
    }

    public void setLockCamera(boolean lockCamera) {
        isLockCamera = lockCamera;
    }

    //当前灯值
    private int downParamNum;

    /**
     * 获取当前灯值
     */
    public void getParamNum() {
        downParamNum = (cameraItemList.get(selectedParamNum).getNumber());
    }


    /**
     * 改变参数调节值
     *
     * @param changeNum 参数值
     */
    public void changeCameraParam(int changeNum) {
//        int changeNumber=number-itemList.get(selectedLightNum).getNumber();
        currentParamNum = downParamNum + changeNum;
//        currentParamNum = Integer.parseInt(number);
        if (currentParamNum > 100) {
            currentParamNum = 100;
        } else if (currentParamNum < 0) {
            currentParamNum = 0;
        }

//        int firstItemPosition = cameraLayoutManager.findFirstVisibleItemPosition();
//        if (selectedParamNum - firstItemPosition >= 0) {
//            //得到要更新的item的view
//            View view = cameraParamView.getChildAt(selectedParamNum - firstItemPosition);
//            if (null!=view&&null != cameraParamView.getChildViewHolder(view)) {
//                CameraParamTouchAdapter.ViewHolder viewHolder = (CameraParamTouchAdapter.ViewHolder) cameraParamView.getChildViewHolder(view);
//
//                if (selectedParamNum == 0) {
//                    viewHolder.getLight_number_text().setText(CameraParamUtil.getExposureTime(cameraParamExposureTime, currentParamNum));
//
//                } else if (selectedParamNum == 1) {
//                    viewHolder.getLight_number_text().setText(CameraParamUtil.getIso(cameraParamISO, currentParamNum));
//
//                } else if (selectedParamNum == 2) {
//                    viewHolder.getLight_number_text().setText(CameraParamUtil.getWHITEBALANCE(cameraParamWHITEBALANCE, currentParamNum));
//                } else if (selectedParamNum == 3) {
//                    viewHolder.getLight_number_text().setText(CameraParamUtil.getFOCUS(cameraParamFOCUS, currentParamNum));
//                } else {
//                    viewHolder.getLight_number_text().setText(currentParamNum + "");
//                }
//
//            }
//        }

        cameraItemList.get(selectedParamNum).setNumber(currentParamNum);
        cameraParamTouchAdapter.notifyItemChanged(selectedParamNum);

        hashMap = new HashMap<String, String>();

        if (selectedParamNum == 0) {
            long min_exposure_time = getMinimumExposureTime();
            long max_exposure_time = getMaximumExposureTime();
            double frac = (100 - currentParamNum) / (double) 100;
            long exposure_time = (long) CameraUtils.exponentialScaling(frac, min_exposure_time, max_exposure_time);
            hashMap.put(Flag.SEC, String.valueOf(exposure_time));
            mView.setCameraParams(hashMap);


        } else if (selectedParamNum == 1) {
            int max = getMaximumISO();
            int min = getMinimumISO();
            double frac = currentParamNum / (double) 100;
            int iso = (int) CameraUtils.exponentialScaling(frac, min, max);
            if (iso < min)
                iso = min;
            else if (iso > max)
                iso = max;
            hashMap.put(Flag.ISO, String.valueOf(iso));
            mView.setCameraParams(hashMap);
        } else if (selectedParamNum == 2) {
            int min_white_balance_temperature_c = 1000;
            int max_white_balance_temperature_c = 15000;
            double frac = currentParamNum / (double) 100;
            int whitebalance = (int) CameraUtils.exponentialScaling(frac, min_white_balance_temperature_c, max_white_balance_temperature_c);
            hashMap.put(Flag.WHITEBALANCE, String.valueOf(whitebalance));
            mView.setCameraParams(hashMap);
        } else if (selectedParamNum == 3) {
            double frac = currentParamNum / (double) 100;
            double scaling = CameraUtils.seekbarScaling(frac);
            float focus_distance = (float) (scaling * getMinimumFocusDistance());
            hashMap.put(Flag.FOCUS, String.valueOf(focus_distance));
            mView.setCameraParams(hashMap);
        }

        mView.setSeekBarNum(currentParamNum);


    }


    /**
     * 改变调节灯的灯值
     */
    public void setItemLightNum() {
        itemList.get(selectedLightNum).setNumber(currentNum);
    }

    public void setItemCameraParamNum() {
        cameraItemList.get(selectedParamNum).setNumber(currentParamNum);
    }

    public void setPresetCameraParams(PresetParm presetParm) {
        if (null != presetParm && null != presetParm.getCameraParm()) {
            updateCameraData(Integer.parseInt(presetParm.getCameraParm().getSec()), Integer.parseInt(presetParm.getCameraParm().getIso()), Integer.parseInt(presetParm.getCameraParm().getWhiteBalance()));
        }
    }

    /*
     * 判断定位是否打开
     * @return
     */
    public boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    /**
     * 设置滤镜
     *
     * @param presetParm 预设参数
     */
    public void setGPUImageFilter(PresetParm presetParm) {
        if (null == presetParm) {
            return;
        }
        if (presetParm.getLightRatioData() != null) {
            writeAllLightNumber(returnWriteLight(presetParm.getLightRatioData().getLeftLight()),
                    returnWriteLight(presetParm.getLightRatioData().getRightLight()),
                    returnWriteLight(presetParm.getLightRatioData().getBottomLight()),
                    returnWriteLight(presetParm.getLightRatioData().getBackgroudLight()),
                    returnWriteLight(presetParm.getLightRatioData().getMoveLight()),
                    returnWriteLight(presetParm.getLightRatioData().getTopLight()),
                    0,
                    0);
           /* if (presetParm.getLightRatioData().getLeftLight() != -1) {
                writeNumber(0, returnWriteLight(presetParm.getLightRatioData().getLeftLight()));

                writeAllLightNumber(returnWriteLight(presetParm.getLightRatioData().getLeftLight()),
                        returnWriteLight(presetParm.getLightRatioData().getRightLight()),
                        returnWriteLight(presetParm.getLightRatioData().getBottomLight()),
                        returnWriteLight(presetParm.getLightRatioData().getBackgroudLight()),
                        returnWriteLight(presetParm.getLightRatioData().getMoveLight()),
                        returnWriteLight(presetParm.getLightRatioData().getTopLight()),
                        0,
                        0);
            }
            if (presetParm.getLightRatioData().getRightLight() != -1) {
                writeNumber(1, returnWriteLight(presetParm.getLightRatioData().getRightLight()));

            }
            if (presetParm.getLightRatioData().getBottomLight() != -1) {
                writeNumber(2, returnWriteLight(presetParm.getLightRatioData().getBottomLight()));

            }
            if (presetParm.getLightRatioData().getBackgroudLight() != -1) {
                writeNumber(3, returnWriteLight(presetParm.getLightRatioData().getBackgroudLight()));

            }
            if (presetParm.getLightRatioData().getMoveLight() != -1) {
                writeNumber(4, returnWriteLight(presetParm.getLightRatioData().getMoveLight()));

            }
            if (presetParm.getLightRatioData().getTopLight() != -1) {
                writeNumber(5, returnWriteLight(presetParm.getLightRatioData().getTopLight()));

            }
*/
            if (presetParm.getLightRatioData().getLeftLight() != -1) {
                //左灯
                itemList.get(0).setNumber(returnWriteLight(presetParm.getLightRatioData().getLeftLight()));

//                writeAllLightNumber(returnWriteLight(presetParm.getLightRatioData().getLeftLight()),
//                        returnWriteLight(presetParm.getLightRatioData().getRightLight()),
//                        returnWriteLight(presetParm.getLightRatioData().getBottomLight()),
//                        returnWriteLight(presetParm.getLightRatioData().getBackgroudLight()),
//                        returnWriteLight(presetParm.getLightRatioData().getMoveLight()),
//                        returnWriteLight(presetParm.getLightRatioData().getTopLight()),
//                        0,
//                        0);
            }
            if (presetParm.getLightRatioData().getRightLight() != -1) {
                //右灯
                itemList.get(1).setNumber(returnWriteLight(presetParm.getLightRatioData().getRightLight()));
            }
            if (presetParm.getLightRatioData().getBottomLight() != -1) {

                //底灯
                itemList.get(2).setNumber(returnWriteLight(presetParm.getLightRatioData().getBottomLight()));

            }
            if (presetParm.getLightRatioData().getBackgroudLight() != -1) {
                //背灯
                itemList.get(3).setNumber(returnWriteLight(presetParm.getLightRatioData().getBackgroudLight()));

            }
            if (presetParm.getLightRatioData().getMoveLight() != -1) {
                itemList.get(4).setNumber(returnWriteLight(presetParm.getLightRatioData().getMoveLight()));
            }
            if (presetParm.getLightRatioData().getTopLight() != -1) {
                itemList.get(5).setNumber(returnWriteLight(presetParm.getLightRatioData().getTopLight()));
            }


            lightBlueTouchAdapter.setData(itemList);

        }

        if (null == presetParm || presetParm.getParmlists() == null) {
            mView.surfviewSwitchFilterTo(new GPUImageFilter());
            return;
        }
        GPUImageFilter gpuImageFilter = switchFilter(presetParm.getParmlists());
        mView.surfviewSwitchFilterTo(gpuImageFilter);

    }

    public GPUImageFilter switchFilter(List<PictureProcessingData> lists) {
        GPUImageFilter gpuImageFilter;
        List<GPUImageFilter> filters = null;
        filters = BizImageMangage.getInstance().getPresetParms(activity, null, lists);
        if (filters != null && filters.size() != 0) {
            gpuImageFilter = new GPUImageFilterGroup(filters);
        } else {
            gpuImageFilter = new GPUImageFilter();
        }
        return gpuImageFilter;
    }

    public GPUImageFilter switchVidioFilter() {
        if (imageData == null
                || imageData.getPresetParms() == null
                || imageData.getPresetParms().getParmlists() == null
                || imageData.getPresetParms().getParmlists().size() == 0) {
            return new GPUImageCameraFilter();
        } else {
            List<GPUImageFilter> filters = null;
            filters = BizImageMangage.getInstance().getCameraPresetParms(activity, null, imageData.getPresetParms().getParmlists());
            return new GPUImageFilterGroup(filters);
        }
    }

    /**
     * 设置初始灯值
     */
    public void setLightNum(List<CommendItem> list) {
        if (itemList == null) {
            itemList = new ArrayList<SurfLightBlueTouchAdapter.Item>();
        }
        switch ((int) CurrentCOBOXVer) {
            case CommendManage.VERSION1_1:
            case CommendManage.VERSION2_1:
                for (int i = 0; list != null && i < list.size(); i++) {
                    CommendItem commendItem = setChangOnline(list.get(i));
                    switch (commendItem.getType()) {
                        case CommendManage.LEFT_LAND:
                            if (list.size() < 1) break;
                            itemList.get(0).setNumber(commendItem.getNum());
                            break;
                        case CommendManage.RIGHT_LAND:
                            if (list.size() < 2) break;
                            itemList.get(1).setNumber(commendItem.getNum());
                            break;
                        case CommendManage.BOTTOM_LAND:
                            if (list.size() < 3) break;
                            itemList.get(2).setNumber(commendItem.getNum());
                            break;
                        case CommendManage.BACK_LAND:
                            if (list.size() < 4) break;
                            itemList.get(3).setNumber(commendItem.getNum());
                            break;
                        case CommendManage.MOVE_LAND:
                            if (list.size() < 5) break;
                            itemList.get(4).setNumber(commendItem.getNum());
                            break;
                        case CommendManage.MOVE2_LAND:
                            if (list.size() < 6) break;
                            itemList.get(5).setNumber(commendItem.getNum());
                            break;
                        case CommendManage.ONLINE_STATUS:
                            break;
                        case CommendManage.MOTOR:
                            if (list.size() < 7) break;
                            if (null != mView) {
                                mView.setMotorStatus(commendItem.getNum());
                            } else {
                                ToastUtil.toast("内存不足，请退出当前页面...");
                            }
                            break;
                    }
                }
                break;
            case CommendManage.VERSION_BOX:
                for (int i = 0; list != null && i < list.size(); i++) {
                    CommendItem commendItem = setChangOnline(list.get(i));
                    switch (commendItem.getType()) {
                        case CommendManage.L1:
                            if (list.size() < 1) break;
                            itemList.get(0).setNumber(commendItem.getNum());
                            break;
                        case CommendManage.L2:
                            if (list.size() < 2) break;
                            itemList.get(1).setNumber(commendItem.getNum());
                            break;
                        case CommendManage.L3:
                            if (list.size() < 3) break;
                            itemList.get(2).setNumber(commendItem.getNum());
                            break;
                        case CommendManage.L4:
                            if (list.size() < 4) break;
                            itemList.get(3).setNumber(commendItem.getNum());
                            break;
                    }
                }
                break;
        }
        lightBlueTouchAdapter.setData(itemList);
    }


    /**
     * 显示光比
     */
    public void showLightPenPop() {
        if (lightPenPop == null) {
            lightPenPop = new LightPenPop(activity, new LightPenPop.LightPenInterface() {
                @Override
                public void onSave() {
                    showEditDialog();
                }

                @Override
                public void onClick(int position) {
                    LightRatioData ratioData = light_list.get(position);
                    if (ratioData != null) {
                        writeAllLightNumber(returnWriteLight(ratioData.getLeftLight()),
                                returnWriteLight(ratioData.getRightLight()),
                                returnWriteLight(ratioData.getBottomLight()),
                                returnWriteLight(ratioData.getBackgroudLight()),
                                returnWriteLight(ratioData.getMoveLight()),
                                returnWriteLight(ratioData.getTopLight()),
                                0,
                                0);

                        //左灯
                        itemList.get(0).setNumber(returnWriteLight(ratioData.getLeftLight()));
                        //右灯
                        itemList.get(1).setNumber(returnWriteLight(ratioData.getRightLight()));
                        //底灯
                        itemList.get(2).setNumber(returnWriteLight(ratioData.getBottomLight()));
                        //背灯
                        itemList.get(3).setNumber(returnWriteLight(ratioData.getBackgroudLight()));
                        itemList.get(4).setNumber(returnWriteLight(ratioData.getMoveLight()));
                        itemList.get(5).setNumber(returnWriteLight(ratioData.getTopLight()));
                        lightBlueTouchAdapter.setData(itemList);

                        lightPenPop.dismissPop();
                    }
                }
            });
        }
        lightPenPop.setData(light_list);
        lightPenPop.showPop(activity.getWindow().getDecorView());

    }

    /**
     * 显示输入框
     */
    public void showEditDialog() {
     /*   if (editDialog == null) {
            editDialog = new EditDialog(activity, R.style.Dialog);
            editDialog.setClickInterFace(new EditDialog.ClickInterFace() {
                @Override
                public void ok(String text) {
                    saveLightRatio(text);
//                                            BlueToothLoad.getInstance().writeByte(CommendManage.getInstance().getWriteBlueTouchNameLand(text));
                    editDialog.dismiss();
                }

                @Override
                public void cencel() {
                    editDialog.dismiss();
                }
            });
        }


        editDialog.show();*/
    }


    /**
     * 保存光比
     */
   /* public void saveLightRatio(String text) {
        getLightRation();
        mCurrentLightRatio.setName(text);
        mCurrentLightRatio.setNum(itemList.get(0).getNumber() +
                itemList.get(1).getNumber() +
                itemList.get(2).getNumber() +
                itemList.get(3).getNumber() +
                itemList.get(4).getNumber());
      //  lightRatioSqlControl.startInsert(mCurrentLightRatio);
    }
*/
    public void refreshLightBlueTouch() {
        if (null == itemList)
            return;
        for (int i = 0; i < itemList.size() - 1; i++) {
            if (itemList.get(i) != null)
                itemList.get(i).setClick(false);
        }
        selectedLightNum = -1;
        lightBlueTouchAdapter.setData(itemList);
    }


    public void refreshCameraParamTouch() {
        if (null == itemList)
            return;
        cameraItemList.get(0).setClick(false);
        cameraItemList.get(1).setClick(false);
        cameraItemList.get(2).setClick(false);
        cameraItemList.get(3).setClick(false);
        selectedParamNum = -1;
        cameraParamTouchAdapter.setData(cameraItemList);
    }


    public void refreshCameraParam() {
        if (null == itemList)
            return;
        cameraItemList.get(1).setClick(false);
        cameraItemList.get(2).setClick(false);
        cameraItemList.get(3).setClick(false);

    }


    public int getSelectedLightNum() {
        return selectedLightNum;
    }

    public int getSelectedParamNum() {
        return selectedParamNum;
    }

    public CommendItem setChangOnline(CommendItem commendItem) {
        if (commendItem.getType() == CommendManage.ONLINE_STATUS) {
            if (commendItem.getNum() != ONLINE_ON) {
                commendItem.setType(commendItem.getPassageway());
                commendItem.setNum(NUL_DATA);
            }
        }
        return commendItem;
    }

    public List getLightItem() {
        return itemList;
    }

    /**
     * @param srcBitmap   原图
     * @param smallBitmap 结果图
     * @param isRect      是否需要裁剪
     * @param rotate      旋转角度
     * @param cameraParm  相机参数
     * @param iswatermark 是否需要加水印
     * @param isAiCamera  是否为智能拍照的图片
     */
    public void getCameraPhoto(Bitmap srcBitmap, Bitmap smallBitmap, boolean isRect, int rotate, CameraParm cameraParm, boolean iswatermark, boolean isAiCamera) {
        getLightRation();
        mCurrentLightRatio.setName(null);
        mCurrentLightRatio.setNum(0);
        new SavePicTask(srcBitmap, smallBitmap, isRect, rotate, cameraParm, iswatermark, isAiCamera).execute();
    }

    /**
     * 保存360度文件
     *
     * @param smallUrl
     * @param localUrl
     */
    public void saveAI360Camera(String smallUrl, String localUrl, String title) {
        getLightRation();
        new SaveAiPicTask(smallUrl, localUrl, title).execute();
    }

    private void getLightRation() {
        if (mCurrentLightRatio == null) {
            mCurrentLightRatio = new LightRatioData();
        }
        for (int i = 0; itemList != null && i < itemList.size(); i++) {
            if (itemList.get(i).getName().equals(App.getInstance().getResources().getString(R.string.left_light))) {
                mCurrentLightRatio.setLeftLight(returnWriteLight(itemList.get(i).getNumber()));
            } else if (itemList.get(i).getName().equals(App.getInstance().getResources().getString(R.string.right_light))) {
                mCurrentLightRatio.setRightLight(returnWriteLight(itemList.get(i).getNumber()));
            } else if (itemList.get(i).getName().equals(App.getInstance().getResources().getString(R.string.bottom_light))) {
                mCurrentLightRatio.setBottomLight(returnWriteLight(itemList.get(i).getNumber()));
            } else if (itemList.get(i).getName().equals(App.getInstance().getResources().getString(R.string.background_light))) {
                mCurrentLightRatio.setBackgroudLight(returnWriteLight(itemList.get(i).getNumber()));
            } else if (itemList.get(i).getName().equals(App.getInstance().getResources().getString(R.string.head_light))) {
                mCurrentLightRatio.setMoveLight(returnWriteLight(itemList.get(i).getNumber()));
            } else if (itemList.get(i).getName().equals(App.getInstance().getResources().getString(R.string.top_light))) {
                mCurrentLightRatio.setTopLight(returnWriteLight(itemList.get(i).getNumber()));
            }
        }
        mCurrentLightRatio.setVersion(String.valueOf(CommendManage.getInstance().getVersion()));

    }


    private int returnWriteLight(int number) {
        if (number >= 0 && number <= 100) {
            return number;
        }
        return 0;
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }


    public boolean getCanClick() {
        return canClick;
    }

    /**
     * 初始化数据控制
     */
    private void newCameraControl() {
        imageDataSqlControl = new ImageDataSqlControl(activity);
        imageDataSqlControl.setListener(new BaseSqlControl.SqlControlListener() {
            @Override
            public void queryListener(Object... parms) {
               /* List<ImageData> list = (List<ImageData>) parms[0];
                LogUtil.d("----" + list.get(0).getSmallLocalUrl());
                mView.setAblumImg(list.get(0).getSmallLocalUrl());*/
            }

            @Override
            public void insertListener(Object... parms) {
                if(null!=mView){
                    mView.showToast(imageData, activity.getResources().getString(R.string.save_ok));
                }            }

            @Override
            public void deletListener(Object... parms) {

            }

            @Override
            public void upDataListener(Object... parms) {

            }
        });
//
       /* lightRatioSqlControl = new LightRatioSqlControl(activity);
        lightRatioSqlControl.setListener(new BaseSqlControl.SqlControlListener() {
            @Override
            public void queryListener(Object... parms) {

                List<LightRatioData> list = (List) parms[0];

                light_list = (List) parms[0];
                Collections.sort(light_list, new Comparator<LightRatioData>() {
                    @Override
                    public int compare(LightRatioData lhs, LightRatioData rhs) {
                        if (Long.valueOf(lhs.getLightId()) < Long.valueOf(rhs.getLightId())) {
                            return 1;


                        } else if (Long.valueOf(lhs.getLightId()) == Long.valueOf(rhs.getLightId())) {
                            return 0;
                        }


                        return -1;
                    }
                });

            }

            @Override
            public void insertListener(Object... parms) {
                lightRatioSqlControl.startQuery();
                mView.showToast(imageData, activity.getResources().getString(R.string.save_ok));
                if (null != lightPenPop) {
                    lightPenPop.dismissPop();
                }
            }

            @Override
            public void deletListener(Object... parms) {

            }

            @Override
            public void upDataListener(Object... parms) {

            }
        });*/
    }

    public void getScanBitmap(Bitmap scabitmap) {
        this.scabitmap = scabitmap;
    }

    /**
     * 存储拍照图片
     */
    private class SavePicTask extends AsyncTask<Void, Void, ImageData> {
        private Bitmap bitmap, smallBitmap, watermark;
        private boolean isRect;
        private boolean isAiCamera;
        private boolean iswatermark;
        private int rotate;
        private CameraParm cameraParm;

        SavePicTask(Bitmap bitmap, Bitmap smallBitmap, boolean isRect, int rotate, CameraParm cameraParm, boolean iswatermark, boolean isAiCamera) {
            this.bitmap = bitmap;
            this.smallBitmap = smallBitmap;
            this.isRect = isRect;
            this.rotate = rotate;
            this.iswatermark = iswatermark;
            this.cameraParm = cameraParm;
            this.isAiCamera = isAiCamera;

        }

        protected void onPreExecute() {
            canClick = false;
            if (mView != null)
                mView.setWaitPicShow(canClick);
        }

        @Override
        protected ImageData doInBackground(Void... params) {
            if (iswatermark) {
                if (CameraStatus.getCameraStatus().getWaterMark().getWaterMarkStatus() == CameraStatus.Size.WATERMARK_BIZCAM) {
                    watermark = BitmapFactory.decodeResource(activity.getResources(), R.drawable.wm_bizcam);
                } else {
                    watermark = BitmapFactory.decodeFile(CameraStatus.getCameraStatus().getWaterMark().getWatermarkUrl());
                }
            }

            final long time = System.currentTimeMillis();
            final long time2 = time - 1;
            CameraStatus cameraStatus = CameraStatus.getCameraStatus();
            final CameraStatus.Size size = cameraStatus.getPictureRatio();
            ThreadPoolUtil.execute(new Runnable() {
                @Override
                public void run() {
                    FileUtil.saveBitmapNew(bitmap, rotate, time - 1 + "", size, false, iswatermark, watermark, isAiCamera);//原图
                }
            });
            String path1 = Flag.APP_CAMERAL + time2 + ".jpg";
            //String path2 = FileUtil.copyFile(path1, Flag.NATIVESDFILE, time + ".png");
            String path2 = FileUtil.saveBitmap2New(smallBitmap, rotate, time + "", size, false, iswatermark, watermark, isAiCamera);//小图
            if (StringUtil.isBlank(path2) || StringUtil.isBlank(path1)) {
                return null;
            } else {
                //未连接蓝牙,光比值为空
                // if (!BlueToothLoad.getInstance().blueToothIsConnection()) {
                if (!bleConnectModel.isCurrentConnect()) {
                    mCurrentLightRatio.initData();
                    //mCurrentPresetParms = null;
                }
                imageData.setType(Flag.TYPE_PIC);
                imageData.setLocalUrl("file://" + path1);
                imageData.setSmallLocalUrl("file://" + path2);
                imageData.setCurrentPosition(0);
                imageData.setTimeStamp(time);
                imageData.setLightRatioData(mCurrentLightRatio);
                if (cameraParm != null) {
                    imageData.setCameraParm(cameraParm);
                }

               /* if (mCurrentPresetParms == null || (mCurrentPresetParms.getId() != null && mCurrentPresetParms.getId().equals("-1"))) {
                    imageData.setPresetParms(null);
                } else {
                    imageData.setPresetParms(mCurrentPresetParms);
                }*/
                imageDataSqlControl.insertImg(imageData);
                return imageData;
            }
        }

        @Override
        protected void onPostExecute(ImageData result) {
            super.onPostExecute(result);
            if (result != null) {
               /* if(cameraType==RETURN_PHOTO){
                    Intent intent=new Intent();
                    intent.putExtra(Flag.IMAGE_DATA,imageData);
                    mView.finishView(RETURN_PHOTO,intent);
                }
                imageData = result;
                mView.setAblumImg(imageData.getSmallLocalUrl());*/

               /* BizCamExif bizCamExif = new BizCamExif(1, 0, 20, 30, 40, 50, 60, 70, 100);
                Gson gson = new Gson();
                String BizExif = gson.toJson(bizCamExif);
                try {
                    ExifInterface exif = new ExifInterface(imageData.getLocalUrl().substring(7));
                    // 经度
                    exif.setAttribute(ExifInterface.TAG_ISO, "2000");
                    exif.setAttribute(ExifInterface.TAG_EXPOSURE_TIME, "2020");
                    exif.setAttribute(ExifInterface.TAG_FOCAL_LENGTH, "10");
                    exif.setAttribute(ExifInterface.TAG_DATETIME, System.currentTimeMillis() + "");
                    exif.setAttribute(ExifInterface.TAG_MODEL, "vvvv");
                    exif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, BizExif.toString());
                    // exif.setAttribute(ExifInterface.TAG_ISO, "2222");
                    exif.saveAttributes();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                if (activity != null) {
                    AddPicReceiver.notifyModifyUsername(activity, "");
                    if (isAiCamera && imageData.getPresetParms() != null) {
                        imageData.getPresetParms().setCameraParm(imageData.getCameraParm());
//                        UploadScanParmsTask uploadScanParmsTask = new UploadScanParmsTask(activity, false);
//                        uploadScanParmsTask.setAsyncTaskSuccessCallback(new AsyncTaskSuccessCallback<HttpBaseData>() {
//                            @Override
//                            public void successCallback(Result<HttpBaseData> result) {
//
//                            }
//                        });
//                        uploadScanParmsTask.setAsyncTaskFailCallback(new AsyncTaskFailCallback<HttpBaseData>() {
//                            @Override
//                            public void failCallback(Result<HttpBaseData> result) {
//
//                            }
//                        });
//                        uploadScanParmsTask.execute(imageData.getPresetParms(), imageData.getLocalUrl(), scabitmap);
                    }
                    activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(imageData.getSmallLocalUrl())));
                    //mView.setAblumImg(result.getSmallLocalUrl());
                    // if (BlueToothLoad.getInstance().blueToothIsConnection()) {
                   /* if (bleConnectModel.isCurrentConnect()) {
                        long time = System.currentTimeMillis();
                        String url = FileUtil.saveUploadBitmap(bitmap, isRect, rotate, time + "");
                        UploadPicData uploadPicData = new UploadPicData();
                        uploadPicData.setLocalUrl(url);
                        uploadPicData.setImagedata(imageData);
                        uploadPicData.setUploadTime(time);
                        uploadPicSqlControl.insertUploadPic(uploadPicData);
                        //uploadPicSqlControl.startQuery();
                        UploadPicReceiver.notifyModifyAddPic(activity, uploadPicData);
                    }*/
                }
            } else {
                if (mView != null)
                    mView.showToast(null, activity.getResources().getString(R.string.take_photo_fail));
            }
            canClick = true;
            if (mView != null)
                mView.setWaitPicShow(canClick);
            if (smallBitmap != null)
                smallBitmap.recycle();
            if (bitmap != null)
                bitmap.recycle();
            if (watermark != null)
                watermark.recycle();
        }
    }


    /**
     * 存储拍照图片
     */
    private class SaveAiPicTask extends AsyncTask<Void, Void, ImageData> {
        private String smallUrl;
        private String localUrl;
        private String title;

        SaveAiPicTask(String smallUrl, String localUrl, String title) {
            this.smallUrl = smallUrl;
            this.localUrl = localUrl;
            this.title = title;
        }

        protected void onPreExecute() {
            canClick = false;
            if (mView != null)
                mView.setWaitPicShow(canClick);
        }

        @Override
        protected ImageData doInBackground(Void... params) {


            final long time = System.currentTimeMillis();
            CameraStatus cameraStatus = CameraStatus.getCameraStatus();

            String path2 = "file://" + FileUtil.copyFile(smallUrl, Flag.NATIVESDFILE, time + ".png");
            if (StringUtil.isBlank(smallUrl) || StringUtil.isBlank(localUrl)) {
                return null;
            } else {

                imageData.setType(Flag.TYPE_AI360);
                imageData.setLocalUrl(localUrl);
                imageData.setValue5(title);
                imageData.setSmallLocalUrl(path2);
                imageData.setCurrentPosition(0);
                imageData.setTimeStamp(time);
                imageData.setLightRatioData(mCurrentLightRatio);

               /* if (mCurrentPresetParms == null || (mCurrentPresetParms.getId() != null && mCurrentPresetParms.getId().equals("-1"))) {
                    imageData.setPresetParms(null);
                } else {
                    imageData.setPresetParms(mCurrentPresetParms);
                }*/
                imageDataSqlControl.insertImg(imageData);
                return imageData;
            }
        }

        @Override
        protected void onPostExecute(ImageData result) {
            super.onPostExecute(result);
            if (result != null) {
                if (activity != null) {
//                  new Handler().postDelayed(new Runnable() {
//                      @Override
//                      public void run() {
//                          ThreadPoolUtil.execute(new Runnable() {
//                              @Override
//                              public void run() {
//
//                              }
//                          });
//                      }
//                  },300);

                    LoginedUser loginedUser = LoginedUser.getLoginedUser();
                    loginedUser.setUploadStatus(Flag.UPLOAD_UPLOADING);
                    loginedUser.setUploadTime(TimeUtil.getBeiJingTimeGMT() + "");
                    LoginedUser.setLoginedUser(loginedUser);

                    AddPicReceiver.notifyModifyUsername(activity, "");
                    activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(imageData.getSmallLocalUrl())));

                    List<UploadCloudData> murls = new ArrayList<>();
                    murls.add(new UploadCloudData(imageData, ""));

                    AddCloudReceiver.notifyModifyAddPic(activity, murls);

                }
            } else {
                if (mView != null)
                    mView.showToast(null, activity.getResources().getString(R.string.take_photo_fail));
            }
            canClick = true;
            if (mView != null)
                mView.setWaitPicShow(canClick);
        }
    }

    /**
     * @param bitmap
     * @param isrect      是否需要裁剪
     * @param ratio       裁剪比例
     * @param cameraParm  相机参数
     * @param isWaterMark 是否需要加水印
     */
    public void takePhoto(Bitmap bitmap, boolean isrect, int ratio, CameraParm cameraParm, boolean isWaterMark) {
        //canClick = true;
        if (canClick) {
            try {
                if (gpuImage == null) {
                    gpuImage = new GPUImage(activity);
                }
                List<GPUImageFilter> filters = null;
                if (imageData.getPresetParms() != null && imageData.getPresetParms().getParmlists() != null) {
                    filters = BizImageMangage.getInstance().getPresetParms(activity, null, imageData.getPresetParms().getParmlists());
                }
                if (filters != null && filters.size() != 0) {
                    gpuImage.setFilter(new GPUImageFilterGroup(filters));
                    getCameraPhoto(bitmap, gpuImage.getBitmapWithFilterApplied(bitmap), isrect, ratio, cameraParm, isWaterMark, false);
                } else {
                    //gpuImage.setFilter(new GPUImageFilter());
                    getCameraPhoto(bitmap, bitmap, isrect, ratio, cameraParm, isWaterMark, false);
                }
                LogUtil.d("currentPicSize: " + bitmap.getWidth() + " " + bitmap.getHeight());
                //getCameraPhoto(bitmap, gpuImage.getBitmapWithFilterApplied(bitmap), isrect, ratio, cameraParm, isWaterMark, isScanON);
                canClick = false;
            } catch (Throwable t) {
                canClick = true;
                t.printStackTrace();
                if (mView != null)
                    mView.showToast(null, activity.getResources().getString(R.string.take_photo_fail));
            }
        }
    }

    public void initRenderer() {
        myGLRenderer = new MyGLRenderer(activity);
        mView.setXYZRenderer(myGLRenderer);
    }

    public void setRendererXYZ(float x, float y, float z) {

        if (myGLRenderer != null) {
            myGLRenderer.setX(x);
            myGLRenderer.setY(y);
            myGLRenderer.setZ(z);
        }
    }


    private void uploadDeviceConnect(final int usedTime) {
        String phone;

        if (!isFirstIn) {
            LoginedUser loginedUser = LoginedUser.getLoginedUser();
            phone = loginedUser.getUser_name();
        } else {
            phone = "";
        }
        String qrCode = bleConnectModel.getDeviceList().get(bleConnectModel.getSelectPosition()).getName();
        String clientInfo = android.os.Build.MODEL; // 手机型号

        HashMap<String, String> paramMap = new HashMap<String, String>();
        //设备二维码
        paramMap.put("qrCode", qrCode);
        //设备信息
        paramMap.put("clientInfo", clientInfo);
        //使用时间
        paramMap.put("usedTime", String.valueOf(usedTime));
        //激活手机号
        paramMap.put("activatedMobile", phone);
        //激活地
        paramMap.put("location", locations);
        //经度
        paramMap.put("longitude", longitude);
        //纬度
        paramMap.put("latitude", latitude);
        RetrofitFactory.getInstence().API().uploadCoboxInfo(paramMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private void getLocation(Location location) {
        if (location != null) {
            Geocoder gc = new Geocoder(activity, Locale.getDefault());
            List<Address> locationList = null;
            try {
                locationList = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                longitude = String.valueOf(location.getLongitude());
                latitude = String.valueOf(location.getLatitude());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //得到Address实例
            if (locationList != null && locationList.size() != 0) {
                Address address = locationList.get(0);

                if (!StringUtil.isBlank(address.getCountryName())) {
                    //国家
                    locations = address.getCountryName();
                }
                if (!StringUtil.isBlank(address.getAdminArea())) {
                    //省
                    locations += " " + address.getAdminArea();
                }
                if (!StringUtil.isBlank(address.getLocality())) {
                    //市
                    locations += " " + address.getLocality();
                }
                if (!StringUtil.isBlank(address.getSubLocality())) {
                    //区
                    locations += " " + address.getSubLocality();
                }
            }
        }
    }

    public int getSelectPosition() {
        return bleConnectModel.getSelectPosition();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean allowCamera2Support(Activity activity, int cameraId) {
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            //相机支持程度:在CameraMetadata中定义，其中FULL值为1，LEGACY值为2，LIMITED值为0，其支持程度为FULL > LIMITED > LEGACY。
            String cameraIdS = manager.getCameraIdList()[Integer.valueOf(cameraId)];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIdS);
            int support = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            /*if( support == CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY )
                Log.d(TAG, "Camera " + cameraId + " has LEGACY Camera2 support");
            else if( support == CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED )
                Log.d(TAG, "Camera " + cameraId + " has LIMITED Camera2 support");
            else if( support == CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL )
                Log.d(TAG, "Camera " + cameraId + " has FULL Camera2 support");
            else
                Log.d(TAG, "Camera " + cameraId + " has unknown Camera2 support?!");
           */
            //support在limit和full时表示支持camera2
            return support == CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL || support == 3;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取设备摄像头个数
     *
     * @return
     */
    public static int getNumberOfCameras(Activity activity) {
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            return manager.getCameraIdList().length;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (AssertionError e) {
            // had reported java.lang.AssertionError on Google Play, "Expected to get non-empty characteristics" from CameraManager.getOrCreateDeviceIdListLocked(CameraManager.java:465)
            // yes, in theory we shouldn't catch AssertionError as it represents a programming error, however it's a programming error by Google (a condition they thought couldn't happen)
            e.printStackTrace();
        }
        return 0;
    }


    private CameraCharacteristics cameraCharacteristics;

    public long getMinimumExposureTime() {
        Long lower;
        Range<Long> exposure_time_range = getCameraCharacteristics().get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
        lower = exposure_time_range.getLower();
        lower = Math.max(exposure_time_range.getLower(), 100000L);
        // return exposure_time_range.getLower();
        return lower;
    }

    public long getMaximumExposureTime() {
        Range<Long> exposure_time_range = getCameraCharacteristics().get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
        long max = exposure_time_range.getUpper();
        max = Math.min(exposure_time_range.getUpper(), 1000000000L / 2);
        return max;
    }

    public int getMinimumISO() {
        Range<Integer> iso_range = getCameraCharacteristics().get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
        return iso_range.getLower();
    }

    /**
     * Returns maximum ISO value. Only relevant if supportsISORange() returns true.
     */
    public int getMaximumISO() {
        Range<Integer> iso_range = getCameraCharacteristics().get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
        return iso_range.getUpper();
    }

    public float getMinimumFocusDistance() {
        Float minimum_focus_distance = getCameraCharacteristics().get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
        if (minimum_focus_distance == null) {
            minimum_focus_distance = 0.0f;
        }
        return minimum_focus_distance;
    }

    public float getMinNumAe() {
        Range<Integer> range1 = getCameraCharacteristics().get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
        float minmin = range1.getLower();
        return minmin;
    }

    public float getMaxNumAe() {
        Range<Integer> range1 = getCameraCharacteristics().get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
        float maxmax = range1.getUpper();
        return maxmax;
    }

    public CameraCharacteristics getCameraCharacteristics() {
        if (cameraCharacteristics == null) {
            cameraCharacteristics = mView.getCamera().getmCameraHelper().getmCameraCharacteristics();
        }
        return cameraCharacteristics;
    }

    private void initCameraParamType() {
        cameraParamExposureTime = new CameraParamType();
        cameraParamExposureTime.setType(0);
        cameraParamExposureTime.setMax(getMaximumExposureTime());
        cameraParamExposureTime.setMin(getMinimumExposureTime());

        cameraParamISO = new CameraParamType();
        cameraParamISO.setType(1);
        cameraParamISO.setMax((long) getMaximumISO());
        cameraParamISO.setMin((long) getMinimumISO());

        cameraParamWHITEBALANCE = new CameraParamType();
        cameraParamWHITEBALANCE.setType(2);
        cameraParamWHITEBALANCE.setMax((long) 15000);
        cameraParamWHITEBALANCE.setMin((long) 1000);

        cameraParamFOCUS = new CameraParamType();
        cameraParamFOCUS.setType(3);
        cameraParamFOCUS.setMax((long) 15000);
        cameraParamFOCUS.setFocusMin(getMinimumFocusDistance());


    }


    public void getCameraData(long sec, int iso, int wb, float focus) {

        if (!isLockCamera) {
            sec_num = CameraParamUtil.setProgressSeekbarExponential(cameraParamExposureTime.getMin(), cameraParamExposureTime.getMax(), sec);
            int iso_num = CameraParamUtil.setProgressSeekbarExponential(cameraParamISO.getMin(), cameraParamISO.getMax(), iso);
            int wb_num = CameraParamUtil.setProgressSeekbarExponential(cameraParamWHITEBALANCE.getMin(), cameraParamWHITEBALANCE.getMax(), wb);

            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt("sec_num", sec_num);
            bundle.putInt("iso_num", iso_num);
            bundle.putInt("wb_num", wb_num);
            message.setData(bundle);
            if (handler != null)
                handler.sendMessage(message);
        }

//        if (!isLockFocusCamera) {
        int focus_num = CameraParamUtil.getFOCUSInverse(0.0, cameraParamFOCUS.getFocusMin(), focus);
        Message messagef = new Message();
        Bundle bundlef = new Bundle();
        bundlef.putInt("focus_num", focus_num);
        messagef.setData(bundlef);
        if (myFocusHandler != null)
            myFocusHandler.sendMessage(messagef);
//        }


    }


    private void updateCameraData(int sec_num, int iso_num, int wb_num) {
        if (null != cameraLayoutManager) {
            int firstItemPosition = cameraLayoutManager.findFirstVisibleItemPosition();

            for (CameraParamTouchAdapter.Item item : cameraItemList) {
                if (null != activity) {
                    if (item.getName().equals(activity.getResources().getString(R.string.shutter))) {
                        if (sec_num == item.getNumber()) {
                            return;
                        }

                        item.setNumber(sec_num);
                        //得到要更新的item的view
                        if (0 - firstItemPosition >= 0) {
                            View view = cameraParamView.getChildAt(0 - firstItemPosition);
                            if (null != view && null != cameraParamView.getChildViewHolder(view)) {
                                CameraParamTouchAdapter.ViewHolder viewHolder = (CameraParamTouchAdapter.ViewHolder) cameraParamView.getChildViewHolder(view);
                                viewHolder.getLight_number_text().setText(CameraParamUtil.getExposureTime(cameraParamExposureTime, item.getNumber()));
                            }
                        }


                    } else if (item.getName().equals(activity.getResources().getString(R.string.iso))) {
                        if (iso_num == item.getNumber()) {
                            return;
                        }
                        item.setNumber(iso_num);
                        //得到要更新的item的view
                        if (1 - firstItemPosition >= 0) {
                            View view = cameraParamView.getChildAt(1 - firstItemPosition);
                            if (null != view && null != cameraParamView.getChildViewHolder(view)) {
                                CameraParamTouchAdapter.ViewHolder viewHolder = (CameraParamTouchAdapter.ViewHolder) cameraParamView.getChildViewHolder(view);
                                viewHolder.getLight_number_text().setText(CameraParamUtil.getIso(cameraParamISO, item.getNumber()));
                            }
                        }

                    } else if (item.getName().equals(activity.getResources().getString(R.string.white_balance))) {
                        if (wb_num == item.getNumber()) {
                            return;
                        }
                        item.setNumber(wb_num);
                        //得到要更新的item的view
                        if (2 - firstItemPosition >= 0) {
                            View view = cameraParamView.getChildAt(2 - firstItemPosition);
                            if (null != view && null != cameraParamView.getChildViewHolder(view)) {
                                CameraParamTouchAdapter.ViewHolder viewHolder = (CameraParamTouchAdapter.ViewHolder) cameraParamView.getChildViewHolder(view);
                                viewHolder.getLight_number_text().setText(CameraParamUtil.getWHITEBALANCE(cameraParamWHITEBALANCE, item.getNumber()));

                            }
                        }

                    }
                }

            }
        }
    }

    private void updateFocusCameraData(int focus_num) {
        if (null != cameraLayoutManager) {
            int firstItemPosition = cameraLayoutManager.findFirstVisibleItemPosition();
            for (CameraParamTouchAdapter.Item item : cameraItemList) {
                if (null != activity) {
                    if (item.getName().equals(activity.getResources().getString(R.string.focus_len))) {
                        if (focus_num == item.getNumber()) {
                            return;
                        }
                        item.setNumber(focus_num);
                        //得到要更新的item的view
                        if (3 - firstItemPosition >= 0) {
                            View view = cameraParamView.getChildAt(3 - firstItemPosition);
                            if (null != view && null != cameraParamView.getChildViewHolder(view)) {
                                CameraParamTouchAdapter.ViewHolder viewHolder = (CameraParamTouchAdapter.ViewHolder) cameraParamView.getChildViewHolder(view);
                                if (isLockFocusCamera) {
                                    viewHolder.getLight_number_text().setText(CameraParamUtil.getFOCUS(cameraParamFOCUS, item.getNumber()));
                                } else {
                                    viewHolder.getLight_number_text().setText("auto");
                                }
                            }
                        }
                    }
                }

            }
        }
    }


    /**
     * 创建静态内部类
     */

    private static class MyHandler extends Handler {
        //持有弱引用HandlerActivity,GC回收时会被回收掉.
        private final WeakReference<SurfViewCameraActivity> mActivtiy;

        public MyHandler(SurfViewCameraActivity activity) {
            mActivtiy = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SurfViewCameraActivity activity = mActivtiy.get();
            super.handleMessage(msg);
            if (activity != null) {
                //执行业务逻辑
                if (null != msg) {
                    Bundle data = msg.getData();
                    int sec_num = data.getInt("sec_num");
                    int iso_num = data.getInt("iso_num");
                    int focus_num = data.getInt("focus_num");
                    int wb_num = data.getInt("wb_num");
                    activity.presenter.updateCameraData(sec_num, iso_num, wb_num);
                }
            }
        }
    }


    private static class MyFocusHandler extends Handler {
        //持有弱引用HandlerActivity,GC回收时会被回收掉.
        private final WeakReference<SurfViewCameraActivity> mActivtiy;

        public MyFocusHandler(SurfViewCameraActivity activity) {
            mActivtiy = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SurfViewCameraActivity activity = mActivtiy.get();
            super.handleMessage(msg);
            if (activity != null) {
                //执行业务逻辑
                if (null != msg) {

                    Bundle data = msg.getData();
                    int focus_num = data.getInt("focus_num");
                    activity.presenter.updateFocusCameraData(24);
                }
            }
        }
    }

    public void cameraParamNotifyDataSetChanged() {
        if (null != cameraParamTouchAdapter) {
            cameraParamTouchAdapter.notifyDataSetChanged();

        }
    }

}

