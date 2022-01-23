package com.bcnetech.hyphoto.ui.view;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.blurkit.BlurCallable;
import com.bcnetech.bcnetechlibrary.blurkit.BlurUtil;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.databinding.BlueToothListNewPopBinding;
import com.bcnetech.hyphoto.model.BleConnectModel;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCamerLoaderBase;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCameraLoader;
import com.bcnetech.bizcamerlibrary.camera.utils.CameraHelper;
import com.bcnetech.hyphoto.R;

import java.util.HashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by yhf on 2017/11/28.
 */
public class BlueToothListNewView extends BaseRelativeLayout {
    private BlueToothListNewPopBinding blueToothListNewPopBinding;
    private Activity activity;
    private BaseAdapter adapter;
    private BlueToothListInterface blueToothListInterface;
    private int type = TYPE_BT;
    private TranslateAnimation animation;
    private GPUImageCameraLoader mCamera;
    private boolean isQR = true;
    public static final int TYPE_BT = 11;
    public static final int TYPE_QR = 12;
    private String coboxName = "";

    //蓝牙关闭
    public static final int BLUE_TOUCH_CLOSE = 1;
    //蓝牙开启
    public static final int BLUE_TOUCH_OPEN = 2;
    //蓝牙连接
    public static final int BLUE_TOUCH_CONNECTION = 3;
    //蓝牙断开
    public static final int BLUE_TOUCH_CONNECTION_ERROR = 4;

    private int connectionType;

    private HashMap<String, String> hashMap;
    private final static double CAMERA_SIZE = 4.0 / 3.0;//长：宽
    private int w;
    private int h;


    public BlueToothListNewView(Context context) {
        super(context);
    }

    public BlueToothListNewView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlueToothListNewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initData() {
        super.initData();
        activity = (Activity) getContext();

        blueToothListNewPopBinding.listView.setAdapter(adapter);
        blueToothListNewPopBinding.listView.setEmptyView(blueToothListNewPopBinding.empty);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation
                .RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.9f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);

        if (hashMap == null) {
            hashMap = new HashMap<String, String>();
        }
        w = ContentUtil.dip2px(activity, 230);
        h = (int) (w * CAMERA_SIZE);

        LayoutParams layoutParams = new LayoutParams(w, h);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        blueToothListNewPopBinding.surfaceview.setLayoutParams(layoutParams);
    }

    @Override
    protected void initView() {
        blueToothListNewPopBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.blue_tooth_list_new_pop, this, true);
        ImageSpan imgspan = new ImageSpan(getContext(), R.drawable.btq);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("  " + getContext().getResources().getString(R.string.bt_help));
        spannableStringBuilder.setSpan(imgspan, 0, 1, ImageSpan.ALIGN_BASELINE);
        blueToothListNewPopBinding.tvQrcodeHelp.setText(spannableStringBuilder);
        blueToothListNewPopBinding.tvQrcodeHelp.setVisibility(GONE);

        super.initView();
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();

        blueToothListNewPopBinding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               /* if (BlueToothLoad.getInstance().blueToothIsClose()) {
                    ToastUtil.toast(getResources().getString(R.string.open_bt));
                    return;
                }*/
                if (!BleConnectModel.getBleConnectModelInstance().isBlueEnable()) {
                    ToastUtil.toast(getResources().getString(R.string.open_bt));
                    return;
                }
                blueToothListInterface.onBlueToothClick(position);
            }
        });

        blueToothListNewPopBinding.llMain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        blueToothListNewPopBinding.ivContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        blueToothListNewPopBinding.llListChoice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setType(TYPE_BT);
            }
        });

        blueToothListNewPopBinding.llScanChoice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setType(TYPE_QR);
                if (null == mCamera) {
                    mCamera = new GPUImageCameraLoader(getContext(), blueToothListNewPopBinding.surfaceview);
                    mCamera.onResume();
                    hashMap.put(Flag.PREVIEW, CameraHelper.PREVIEW_11 + "");
                    hashMap.put(Flag.PICSIZE + "", CameraStatus.getCameraStatus().getPictureSize().getIndex() + "");
                    hashMap.put(Flag.CAMERATYPE, GPUImageCameraLoader.CAMER_TYPE + "");
                    mCamera.setCameraParams(hashMap);

                    initCrop();
                } else {
                    if (isQR == false) {
                        mCamera.onResume();
                        initCrop();
                        isQR = true;
                    }
                }
                class GetQRResultListener implements GPUImageCamerLoaderBase.GetQRResultListener {
                    @Override
                    public void onGetQRResult(String result) {
                        // if (BlueToothLoad.getInstance().blueToothIsClose()) {
                        if (!BleConnectModel.getBleConnectModelInstance().isBlueEnable()) {
                            ToastUtil.toast(getResources().getString(R.string.open_bt));
                            return;
                        }
                        if (null != result) {
                            if(null==blueToothListInterface){
                                ToastUtil.toast(getContext().getString(R.string.scan_error));
                                return;
                            }
                            blueToothListInterface.onScanConnection(result);
                        }
                        mCamera.onPause();
                        setType(TYPE_BT);
                        isQR = false;
                        setVisibility(View.GONE);
                    }
                }
                mCamera.setQRResultListener(new GetQRResultListener());


            }
        });

        blueToothListNewPopBinding.tvQrcodeHelp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                blueToothListNewPopBinding.llQrhelp.setVisibility(VISIBLE);
            }
        });

        blueToothListNewPopBinding.rlConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                blueToothListNewPopBinding.llQrhelp.setVisibility(GONE);
            }
        });

    }

    public void setApplyBlur() {
        //applyBlur(blueToothListNewPopBinding.ivContent);
        BlurCallable blurCallable = new BlurCallable(BlurUtil.getViewCache(activity));
        FutureTask<Drawable> futureTask = new FutureTask(blurCallable);
        ThreadPoolUtil.execute(futureTask);
        try {
            blueToothListNewPopBinding.ivContent.setBackground(futureTask.get(3000, TimeUnit.MILLISECONDS));
            if (futureTask.isDone()) {
                futureTask.cancel(false);
            }
        } catch (Exception e) {
        }

    }

    public interface BlueToothListInterface {
        void onBlueToothDissmiss(Object... params);

        void onBlueToothClick(Object... params);

        void onListConnection(Object... params);

        void onScanConnection(Object... params);
    }

    public void initParm(BaseAdapter adapter, BlueToothListInterface blueToothListInterface) {
        this.adapter = adapter;
        this.blueToothListInterface = blueToothListInterface;
        this.blueToothListNewPopBinding.listView.setAdapter(adapter);
    }

    public BlueToothListInterface getBlueToothListInterface() {
        return blueToothListInterface;
    }

    public void setBlueToothListInterface(BlueToothListInterface blueToothListInterface) {
        this.blueToothListInterface = blueToothListInterface;
    }

    public void destroy() {
        if (null != mCamera) {
            mCamera.onDestroy();
            mCamera = null;
        }
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int dp230 = ContentUtil.dip2px(getContext(), 230);
        int cameraWidth = mCamera.getPreviewSize().height;
        int cameraHeight = mCamera.getPreviewSize().width;

        /** 获取布局容器的宽高 */
        int containerWidth = dp230;
        int containerHeight = dp230;

        /** 获取布局中扫描框的位置信息 */
        int cropLeft = (containerWidth - dp230) / 2;
        int cropTop = (containerHeight - dp230) / 2;

        int cropWidth = dp230;
        int cropHeight = dp230;

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
    }

    private void setType(int type) {
        if (type == TYPE_BT) {
            if (this.type == TYPE_BT) {
                return;
            }
            blueToothListNewPopBinding.llTop.setVisibility(View.VISIBLE);
            if (connectionType == BLUE_TOUCH_CLOSE) {
                blueToothListNewPopBinding.rlContent.setVisibility(GONE);
                blueToothListNewPopBinding.llBlueTooth.setVisibility(GONE);
                blueToothListNewPopBinding.tvBlueTooth.setText("");
                blueToothListNewPopBinding.rlBtClose.setVisibility(VISIBLE);
            } else {
                if (connectionType == BLUE_TOUCH_CONNECTION) {
                    blueToothListNewPopBinding.rlContent.setVisibility(GONE);
                    blueToothListNewPopBinding.llBlueTooth.setVisibility(VISIBLE);
                } else {
                    blueToothListNewPopBinding.rlContent.setVisibility(VISIBLE);
                    blueToothListNewPopBinding.llBlueTooth.setVisibility(GONE);
                }
            }

            blueToothListNewPopBinding.qrcodeContent.setVisibility(View.GONE);
            blueToothListNewPopBinding.surfaceview.setVisibility(GONE);
            blueToothListNewPopBinding.ivTop.setVisibility(GONE);
//            iv_left.setVisibility(GONE);
//            iv_right.setVisibility(GONE);
            blueToothListNewPopBinding.rlBottom.setVisibility(GONE);

            blueToothListNewPopBinding.ivListChoice.setImageResource(R.drawable.bluetooth_list_select);
            blueToothListNewPopBinding.tvListChoice.setTextColor(activity.getResources().getColor(R.color.sing_in_color));

            blueToothListNewPopBinding.ivScanChoice.setImageResource(R.drawable.bluetooth_scan_unselect);
            blueToothListNewPopBinding.tvScanChoice.setTextColor(activity.getResources().getColor(R.color.text_item_child));
            blueToothListNewPopBinding.llQrhelp.setVisibility(GONE);
            blueToothListNewPopBinding.tvQrcodeHelp.setVisibility(GONE);
//            blueToothListInterface.onListConnection();


        } else {
            if (this.type == TYPE_QR) {
                return;
            }
            blueToothListNewPopBinding.llTop.setVisibility(View.GONE);
            blueToothListNewPopBinding.llBlueTooth.setVisibility(GONE);
            blueToothListNewPopBinding.rlContent.setVisibility(GONE);
            blueToothListNewPopBinding.rlBtClose.setVisibility(GONE);

            blueToothListNewPopBinding.qrcodeContent.setVisibility(VISIBLE);
            blueToothListNewPopBinding.captureScanLine.startAnimation(animation);
            blueToothListNewPopBinding.surfaceview.setVisibility(VISIBLE);
            blueToothListNewPopBinding.ivTop.setVisibility(VISIBLE);
            blueToothListNewPopBinding.rlBottom.setVisibility(VISIBLE);

            blueToothListNewPopBinding.ivListChoice.setImageResource(R.drawable.bluetooth_list_unselect);
            blueToothListNewPopBinding.tvListChoice.setTextColor(activity.getResources().getColor(R.color.text_item_child));

            blueToothListNewPopBinding.ivScanChoice.setImageResource(R.drawable.bluetooth_scan_select);
            blueToothListNewPopBinding.tvScanChoice.setTextColor(activity.getResources().getColor(R.color.sing_in_color));
            blueToothListNewPopBinding.llQrhelp.setVisibility(GONE);
            blueToothListNewPopBinding.tvQrcodeHelp.setVisibility(VISIBLE);
        }
        this.type = type;
    }

    public void setConnectBluetoothName(String name) {
        if (this.type == TYPE_BT) {
            blueToothListNewPopBinding.rlContent.setVisibility(GONE);
            blueToothListNewPopBinding.llBlueTooth.setVisibility(VISIBLE);
        }
        coboxName = name;
        blueToothListNewPopBinding.tvBlueTooth.setText(name);
    }

    public void setBlueTouchType(int blueTouchType) {
        this.connectionType = blueTouchType;
        switch (blueTouchType) {
            case BLUE_TOUCH_CLOSE:
                blueToothListNewPopBinding.rlContent.setVisibility(GONE);
                blueToothListNewPopBinding.llBlueTooth.setVisibility(GONE);
                blueToothListNewPopBinding.rlBtClose.setVisibility(VISIBLE);
                break;
            case BLUE_TOUCH_OPEN:
                blueToothListNewPopBinding.rlContent.setVisibility(VISIBLE);
                blueToothListNewPopBinding.llBlueTooth.setVisibility(GONE);
                blueToothListNewPopBinding.rlBtClose.setVisibility(GONE);
                break;
            case BLUE_TOUCH_CONNECTION:
                blueToothListNewPopBinding.tvBlueTooth.setText(coboxName);
                blueToothListNewPopBinding.rlContent.setVisibility(GONE);
                blueToothListNewPopBinding.llBlueTooth.setVisibility(VISIBLE);
                break;
            case BLUE_TOUCH_CONNECTION_ERROR:
                blueToothListNewPopBinding.rlContent.setVisibility(VISIBLE);
                blueToothListNewPopBinding.llBlueTooth.setVisibility(GONE);
                blueToothListNewPopBinding.tvBlueTooth.setText("");
                blueToothListNewPopBinding.rlBtClose.setVisibility(GONE);
                break;

        }
    }

    public void setCloseConnecrtion(OnClickListener listener) {
        blueToothListNewPopBinding.tvDisConnection.setOnClickListener(listener);
    }

    public boolean isShow() {
        return this.getVisibility() == VISIBLE;
    }

    public void close() {
        setVisibility(GONE);
        if (null != mCamera) {
            mCamera.onPause();
            //mCamera.onDestroy();
        }
        setType(TYPE_BT);
        isQR = false;
        setVisibility(View.GONE);
        if (null != blueToothListInterface)
            blueToothListInterface.onBlueToothDissmiss();
    }
}
