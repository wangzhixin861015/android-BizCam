package com.bcnetech.hyphoto.ui.activity.personCenter;

import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.presenter.BizCamUToPresenter;
import com.bcnetech.hyphoto.presenter.iview.IBizCamUToView;
import com.bcnetech.hyphoto.ui.activity.BaseMvpActivity;
import com.bcnetech.hyphoto.ui.view.BizCamUToView;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCamerLoaderBase;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCameraLoader;
import com.bcnetech.bizcamerlibrary.camera.utils.CameraHelper;

import java.util.HashMap;

/**
 * Created by yhf on 2018/10/17.
 */

public class BizCamUToActivity extends BaseMvpActivity<IBizCamUToView,BizCamUToPresenter>{

    private ImageView iv_left;
    private ImageView iv_right;
    private ImageView iv_top;
    private ImageView iv_bottom;
    private TranslateAnimation animation;
    private ImageView capture_scan_line;

    private GPUImageCameraLoader mCamera;
    private GLSurfaceView glSurfaceView;
    private HashMap<String, String> hashMap;
    private TitleView titleView;
    private BizCamUToView bizcam_uto_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bizcam_u_to);
        initView();
        initData();
        onViewClick();
    }

    @Override
    protected void initView() {
        iv_left=findViewById(R.id.iv_left);
        iv_right=findViewById(R.id.iv_right);
        iv_top=findViewById(R.id.iv_top);
        iv_bottom=findViewById(R.id.iv_bottom);
        capture_scan_line=findViewById(R.id.capture_scan_line);
        glSurfaceView=findViewById(R.id.surfaceview);
        titleView=findViewById(R.id.titleView);
        bizcam_uto_view=findViewById(R.id.bizcam_uto_view);
    }

    @Override
    protected void initData() {
        initViewWH();

        titleView.setType(TitleView.BIZCAM_U_TO);
        titleView.setTitleText(getResources().getString(R.string.bizcam_ut_hint1));

        if (hashMap == null) {
            hashMap = new HashMap<String, String>();
        }

        animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation
                .RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.9f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);

        capture_scan_line.startAnimation(animation);
        if (null == mCamera) {
            mCamera = new GPUImageCameraLoader(this, glSurfaceView);
            mCamera.onResume();
            hashMap.put(Flag.PREVIEW, CameraHelper.PREVIEW_169 + "");
            hashMap.put(Flag.PICSIZE + "", CameraStatus.getCameraStatus().getPictureSize().getIndex() + "");
            hashMap.put(Flag.CAMERATYPE, GPUImageCameraLoader.CAMER_TYPE + "");
            mCamera.setCameraParams(hashMap);
            initCrop();
        }
        class GetQRResultListener implements GPUImageCamerLoaderBase.GetQRResultListener {
            @Override
            public void onGetQRResult(String result) {
                bizcam_uto_view.show(result);
                mCamera.onPause();
            }
        }
        mCamera.setQRResultListener(new GetQRResultListener());

        bizcam_uto_view.okClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bizcam_uto_view.setBizCamUtoListener(new BizCamUToView.BizCamUtoListener() {
                    @Override
                    public void loginSuccess() {
                        finish();
                    }
                });
                mCamera.onResume();
                mCamera.setQRResultListener(new GetQRResultListener());
            }
        });

        bizcam_uto_view.cancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onViewClick() {
        titleView.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public BizCamUToPresenter initPresenter() {
        return new BizCamUToPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mCamera) {
            mCamera.onDestroy();
            mCamera = null;
        }
    }

    private void initViewWH(){
        int lrW= ContentUtil.getScreenWidth(this);
        int leftRightW= (lrW-ImageUtil.Dp2Px(this,260))/2;

        int tbH=ContentUtil.getScreenHeight2(this);
        int topBottomH=(tbH-ImageUtil.Dp2Px(this,260))/2;

        RelativeLayout.LayoutParams leftParams= (RelativeLayout.LayoutParams) iv_left.getLayoutParams();
        leftParams.width=leftRightW;
        iv_left.setLayoutParams(leftParams);

        RelativeLayout.LayoutParams rightParams= (RelativeLayout.LayoutParams) iv_right.getLayoutParams();
        rightParams.width=leftRightW;
        iv_right.setLayoutParams(rightParams);


        RelativeLayout.LayoutParams topParams= (RelativeLayout.LayoutParams) iv_top.getLayoutParams();
        topParams.height=topBottomH;
        iv_top.setLayoutParams(topParams);

        RelativeLayout.LayoutParams bottomParams= (RelativeLayout.LayoutParams) iv_bottom.getLayoutParams();
        bottomParams.height=topBottomH;
        iv_bottom.setLayoutParams(bottomParams);
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int dp230 = ContentUtil.dip2px(this, 260);
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
}
