package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.bizcamerlibrary.camera.gpuimagecamera.GPUImageCameraLoader;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.gpumanage.GPUImageFilterTools;

/**
 * Created by wenbin on 2017/2/23.
 */

public class PreviewSurfView extends BaseRelativeLayout {

    private GLSurfaceView glSurfaceView;
    private GPUImageFilter gpuImageFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
    private GPUImageCameraLoader mCamera;


    public PreviewSurfView(Context context) {
        super(context);
    }

    public PreviewSurfView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewSurfView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        glSurfaceView=new GLSurfaceView(getContext());
        glSurfaceView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
        addView(glSurfaceView);
    }

    @Override
    protected void initData() {
        mCamera = new GPUImageCameraLoader(getContext(), glSurfaceView);
    /*    gpuImageFilter= BizImageMangage.getInstance().getGPUFilterforType(getContext(), BizImageMangage.GAUSSIAN_BLUR);
        mFilterAdjuster=  new GPUImageFilterTools.FilterAdjuster(gpuImageFilter);

        mFilterAdjuster.adjust(100);
        mCamera.switchFilterTo(gpuImageFilter);*/

    }

    @Override
    protected void onViewClick() {

    }


    public void onResume(){
        mCamera.onResume();
    }

    public void onPause(){
        mCamera.onPause();
    }

    public void onDestroy(){
        mCamera.onDestroy();
        mCamera=null;
    }

}
