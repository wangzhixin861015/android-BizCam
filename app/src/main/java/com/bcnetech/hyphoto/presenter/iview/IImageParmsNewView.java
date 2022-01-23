package com.bcnetech.hyphoto.presenter.iview;

import android.graphics.Bitmap;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by yhf on 2017/10/25.
 */

public interface IImageParmsNewView extends BaseIView{

    void setGPUFilter(GPUImageFilter gpuFilter);

    void setTitleText(String text);

    void setProgresssee(int progresssee);

    void setGpuImageSize(int strX, int strY, int w, int h,Bitmap bitmap);

    void setGpuImageBitmap(Bitmap bitmap);

    double getShowRelWidth();

    double getShowRelHeight();

    void refresh();

    Bitmap getFinalBit();

    Bitmap getFinalSrcBit();

    Bitmap getWhiteBalanceBit();

    void showSaveLoading();

    void dissMissSaveLoading();

}
