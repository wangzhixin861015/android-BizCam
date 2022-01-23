package jp.co.cyberagent.android.gpuimage.gpumanage;

import android.app.Activity;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by wenbin on 16/5/17.
 */
public class GPUImageFilterAdjuster {

    private GPUImageFilter mFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
    private int progress;

    public GPUImageFilterAdjuster(Activity activity, GPUImageFilterTools.FilterType type, int progress){
        mFilter=GPUImageFilterTools.createFilterForType(activity, type);
        mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        mFilterAdjuster.adjust(progress);
        this.progress=progress;
    }


    public GPUImageFilter getmFilter() {
        return mFilter;
    }

    public void setmFilter(GPUImageFilter mFilter) {
        this.mFilter = mFilter;
    }

    public GPUImageFilterTools.FilterAdjuster getmFilterAdjuster() {
        return mFilterAdjuster;
    }

    public void setmFilterAdjuster(GPUImageFilterTools.FilterAdjuster mFilterAdjuster) {
        this.mFilterAdjuster = mFilterAdjuster;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        mFilterAdjuster.adjust(progress);
        this.progress = progress;
    }
}
