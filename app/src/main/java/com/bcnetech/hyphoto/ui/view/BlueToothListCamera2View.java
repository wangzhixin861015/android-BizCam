package com.bcnetech.hyphoto.ui.view;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetechlibrary.blurkit.BlurCallable;
import com.bcnetech.bcnetechlibrary.blurkit.BlurUtil;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.databinding.BlueToothListC2ViewBinding;
import com.bcnetech.hyphoto.model.BleConnectModel;
import com.bcnetech.hyphoto.ui.activity.camera.SurfViewCameraActivity;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCamerLoaderBase;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCameraLoader;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by yhf on 2017/11/28.
 */

public class BlueToothListCamera2View extends BaseRelativeLayout {

    private Activity activity;
    private BaseAdapter adapter;
    private BlueToothListInterface blueToothListInterface;
    private int type = 0;
    private TranslateAnimation animation;
    private GPUImageCameraLoader mCamera;

    private BlueToothListC2ViewBinding blueToothListC2ViewBinding;

    private boolean isQR = true;

    private static final int TYPE_LIST = 10;
    private static final int TYPE_SCAN = 11;

    //蓝牙关闭
    public static final int BLUE_TOUCH_CLOSE = 1;
    //蓝牙开启
    public static final int BLUE_TOUCH_OPEN = 2;
    //蓝牙连接
    public static final int BLUE_TOUCH_CONNECTION = 3;
    //蓝牙断开
    public static final int BLUE_TOUCH_CONNECTION_ERROR = 4;

    private final static double CAMERA_SIZE = 4.0 / 3.0;//长：宽


    public BlueToothListCamera2View(Context context) {
        super(context);
    }

    public BlueToothListCamera2View(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlueToothListCamera2View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initData() {
        super.initData();
        activity = (SurfViewCameraActivity) getContext();
        blueToothListC2ViewBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.blue_tooth_list_c2_view, this, true);
        blueToothListC2ViewBinding.listView.setAdapter(adapter);
        blueToothListC2ViewBinding.listView.setEmptyView(blueToothListC2ViewBinding.empty);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation
                .RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.9f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);

        int w = ContentUtil.dip2px(activity, 230);
        int h = (int) (w * CAMERA_SIZE);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(w, h);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        blueToothListC2ViewBinding.cameraTextureView.setLayoutParams(layoutParams);
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.blue_tooth_list_new_pop, this);
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();

        blueToothListC2ViewBinding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                blueToothListInterface.onBlueToothClick(position);
            }
        });
//        setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//
//            }
//        });


//        iv_content.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCamera.onPause();
//                blueToothListInterface.onBlueToothDissmiss();
//            }
//        });

        blueToothListC2ViewBinding.llListChoice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setType(TYPE_LIST);
            }
        });

        blueToothListC2ViewBinding.llScanChoice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initCrop();
                setType(TYPE_SCAN);
               /* if (null == mCamera) {
                    mCamera = new GPUImageCameraLoader(getContext(),  blueToothListC2ViewBinding.cameraTextureView);
                    mCamera.onResume();
                    initCrop();
                } else {
                    if (isQR == false) {
                        mCamera.onResume();
                        initCrop();
                        isQR = true;
                    }
                }*/
                class GetQRResultListener implements GPUImageCamerLoaderBase.GetQRResultListener {
                    @Override
                    public void onGetQRResult(String result) {
                        blueToothListInterface.onScanConnection(result);
                    }
                }
                mCamera.setQRResultListener(new GetQRResultListener());


            }
        });

        blueToothListC2ViewBinding.ivContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        blueToothListC2ViewBinding.cameraTextureView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setApplyBlur() {
        //applyBlur(blueToothListNewPopBinding.ivContent);
        BlurCallable blurCallable = new BlurCallable(BlurUtil.getViewCache(activity));
        FutureTask<Drawable> futureTask = new FutureTask(blurCallable);
        ThreadPoolUtil.execute(futureTask);
        try {
            blueToothListC2ViewBinding.ivContent.setBackground(futureTask.get(3000, TimeUnit.MILLISECONDS));
            if (futureTask.isDone()) {
                futureTask.cancel(false);
            }
        } catch (Exception e) {
        }

    }

    public void setCamera(GPUImageCameraLoader mCamera) {
        this.mCamera = mCamera;
        mCamera.setTextureView(blueToothListC2ViewBinding.cameraTextureView);
    }

    public void close() {
        if (null != mCamera) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mCamera.onPause();
                }
            }).start();
        }
        setType(TYPE_LIST);
        isQR = false;
        setVisibility(View.GONE);
        if (blueToothListInterface != null)
            blueToothListInterface.onBlueToothDissmiss();
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
        this.blueToothListC2ViewBinding.listView.setAdapter(adapter);
    }

    public BlueToothListInterface getBlueToothListInterface() {
        return blueToothListInterface;
    }

    public void setBlueToothListInterface(BlueToothListInterface blueToothListInterface) {
        this.blueToothListInterface = blueToothListInterface;
    }

    public void destroy() {
        if (null != mCamera) {
            setType(TYPE_LIST);
            isQR = false;
            setVisibility(View.GONE);
            mCamera.onDestroy();
            mCamera = null;
        }
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        /*int dp230 = ContentUtil.dip2px(getContext(), 230);
        int cameraWidth = mCamera.getCurrentPreviewSize().getHeight();
        int cameraHeight = mCamera.getCurrentPreviewSize().getWidth();

        *//** 获取布局容器的宽高 *//*
        int containerWidth = dp230;
        int containerHeight = dp230;

        *//** 获取布局中扫描框的位置信息 *//*
        int cropLeft = (containerWidth - dp230) / 2;
        int cropTop = (containerHeight - dp230) / 2;

        int cropWidth = dp230;
        int cropHeight = dp230;

        *//** 计算最终截取的矩形的左上角顶点x坐标 *//*
        int x = cropLeft * cameraWidth / containerWidth;
        *//** 计算最终截取的矩形的左上角顶点y坐标 *//*
        int y = cropTop * cameraHeight / containerHeight;

        *//** 计算最终截取的矩形的宽度 *//*
        int width = cropWidth * cameraWidth / containerWidth;
        *//** 计算最终截取的矩形的高度 *//*
        int height = cropHeight * cameraHeight / containerHeight;

        *//** 生成最终的截取的矩形 *//*
        Rect mCropRect = new Rect(x, y, width + x, height + y);
        mCamera.setCropRect(mCropRect);
        mCamera.setQRCode(true);*/
    }

    public void setType(int type) {
        if (type == TYPE_LIST) {
            if (this.type == TYPE_LIST) {
                return;
            }
            blueToothListC2ViewBinding.llTop.setVisibility(View.VISIBLE);
            blueToothListC2ViewBinding.cameraTextureView.setVisibility(GONE);
            //if (BlueToothLoad.getInstance().blueToothIsConnection()) {
            if (BleConnectModel.getBleConnectModelInstance().isCurrentConnect()) {
                blueToothListC2ViewBinding.rlContent.setVisibility(GONE);
                blueToothListC2ViewBinding.llBlueTooth.setVisibility(VISIBLE);
            } else {
                blueToothListC2ViewBinding.rlContent.setVisibility(VISIBLE);
                blueToothListC2ViewBinding.llBlueTooth.setVisibility(GONE);
            }
            blueToothListC2ViewBinding.ivTop.setVisibility(GONE);
            blueToothListC2ViewBinding.ivBottom.setVisibility(GONE);
            blueToothListC2ViewBinding.qrcodeContent.setVisibility(View.GONE);
            blueToothListC2ViewBinding.ivListChoice.setImageResource(R.drawable.bluetooth_list_select);
            blueToothListC2ViewBinding.tvListChoice.setTextColor(activity.getResources().getColor(R.color.sing_in_color));

            blueToothListC2ViewBinding.ivScanChoice.setImageResource(R.drawable.bluetooth_scan_unselect);
            blueToothListC2ViewBinding.tvScanChoice.setTextColor(activity.getResources().getColor(R.color.text_item_child));
            if (blueToothListInterface != null) {
                blueToothListInterface.onListConnection();
            }
            this.type = type;

        } else {
            if (this.type == TYPE_SCAN) {
                return;
            }
            blueToothListC2ViewBinding.llTop.setVisibility(View.GONE);
            blueToothListC2ViewBinding.llBlueTooth.setVisibility(GONE);
            blueToothListC2ViewBinding.rlContent.setVisibility(GONE);

            blueToothListC2ViewBinding.qrcodeContent.setVisibility(VISIBLE);
            blueToothListC2ViewBinding.captureScanLine.startAnimation(animation);
            blueToothListC2ViewBinding.cameraTextureView.setVisibility(VISIBLE);
            blueToothListC2ViewBinding.ivTop.setVisibility(VISIBLE);
            blueToothListC2ViewBinding.ivBottom.setVisibility(VISIBLE);
//                qrcode_content.setVisibility(View.VISIBLE);
//                capture_scan_line.startAnimation(animation);
            blueToothListC2ViewBinding.ivListChoice.setImageResource(R.drawable.bluetooth_list_unselect);
            blueToothListC2ViewBinding.tvListChoice.setTextColor(activity.getResources().getColor(R.color.text_item_child));

            blueToothListC2ViewBinding.ivScanChoice.setImageResource(R.drawable.bluetooth_scan_select);
            blueToothListC2ViewBinding.tvScanChoice.setTextColor(activity.getResources().getColor(R.color.sing_in_color));
            this.type = type;
        }
    }

    public void setConnectBluetoothName(String name) {
        blueToothListC2ViewBinding.rlContent.setVisibility(GONE);
        blueToothListC2ViewBinding.llBlueTooth.setVisibility(VISIBLE);
        blueToothListC2ViewBinding.tvBlueTooth.setText(name);
    }

    public void setBlueTouchType(int blueTouchType) {
        switch (blueTouchType) {
            case BLUE_TOUCH_CLOSE:
                blueToothListC2ViewBinding.rlContent.setVisibility(VISIBLE);
                blueToothListC2ViewBinding.llBlueTooth.setVisibility(GONE);
                blueToothListC2ViewBinding.tvBlueTooth.setText("");
                break;
            case BLUE_TOUCH_OPEN:
                blueToothListC2ViewBinding.rlContent.setVisibility(VISIBLE);
                blueToothListC2ViewBinding.llBlueTooth.setVisibility(GONE);
                break;
            case BLUE_TOUCH_CONNECTION:
                break;
            case BLUE_TOUCH_CONNECTION_ERROR:
                blueToothListC2ViewBinding.rlContent.setVisibility(VISIBLE);
                blueToothListC2ViewBinding.llBlueTooth.setVisibility(GONE);
                blueToothListC2ViewBinding.tvBlueTooth.setText("");
                break;
        }
    }

    public void setCloseConnecrtion(OnClickListener listener) {
        blueToothListC2ViewBinding.tvDisConnection.setOnClickListener(listener);
    }

}
