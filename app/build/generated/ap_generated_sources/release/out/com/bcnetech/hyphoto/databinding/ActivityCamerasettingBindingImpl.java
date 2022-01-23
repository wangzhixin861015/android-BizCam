package com.bcnetech.hyphoto.databinding;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityCamerasettingBindingImpl extends ActivityCamerasettingBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.camera_setting_title, 1);
        sViewsWithIds.put(R.id.ll_camera_options, 2);
        sViewsWithIds.put(R.id.camera_options, 3);
        sViewsWithIds.put(R.id.pic_size, 4);
        sViewsWithIds.put(R.id.pic_ratio, 5);
        sViewsWithIds.put(R.id.video_duration, 6);
        sViewsWithIds.put(R.id.subline, 7);
        sViewsWithIds.put(R.id.watermark, 8);
        sViewsWithIds.put(R.id.ll_export, 9);
        sViewsWithIds.put(R.id.export, 10);
        sViewsWithIds.put(R.id.black_white, 11);
        sViewsWithIds.put(R.id.compress, 12);
        sViewsWithIds.put(R.id.ll_newsetting, 13);
        sViewsWithIds.put(R.id.netsetting, 14);
        sViewsWithIds.put(R.id.cache_clear, 15);
        sViewsWithIds.put(R.id.only_wifi, 16);
        sViewsWithIds.put(R.id.ll_hardware, 17);
        sViewsWithIds.put(R.id.hardware, 18);
        sViewsWithIds.put(R.id.cobox_update, 19);
        sViewsWithIds.put(R.id.tv_version, 20);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityCamerasettingBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 21, sIncludes, sViewsWithIds));
    }
    private ActivityCamerasettingBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[11]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[15]
            , (android.widget.TextView) bindings[3]
            , (com.bcnetech.hyphoto.ui.view.TitleView) bindings[1]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[19]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[12]
            , (android.widget.TextView) bindings[10]
            , (android.widget.TextView) bindings[18]
            , (android.widget.LinearLayout) bindings[2]
            , (android.widget.LinearLayout) bindings[9]
            , (android.widget.LinearLayout) bindings[17]
            , (android.widget.LinearLayout) bindings[13]
            , (android.widget.TextView) bindings[14]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[16]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[5]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[4]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[7]
            , (android.widget.TextView) bindings[20]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[6]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[8]
            );
        this.mboundView0 = (android.widget.RelativeLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}