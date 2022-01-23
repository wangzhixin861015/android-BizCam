package com.bcnetech.hyphoto.databinding;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class CameraSettingNewLayoutBindingImpl extends CameraSettingNewLayoutBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.rl_title, 1);
        sViewsWithIds.put(R.id.camera_close_btn, 2);
        sViewsWithIds.put(R.id.netsetting, 3);
        sViewsWithIds.put(R.id.subline, 4);
        sViewsWithIds.put(R.id.flash, 5);
        sViewsWithIds.put(R.id.watermark, 6);
        sViewsWithIds.put(R.id.pic_setting, 7);
        sViewsWithIds.put(R.id.pic_ratio, 8);
        sViewsWithIds.put(R.id.pic_size, 9);
        sViewsWithIds.put(R.id.video_setting, 10);
        sViewsWithIds.put(R.id.video_ratio, 11);
        sViewsWithIds.put(R.id.video_size, 12);
        sViewsWithIds.put(R.id.video_duration, 13);
        sViewsWithIds.put(R.id.black_white, 14);
        sViewsWithIds.put(R.id.compress, 15);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public CameraSettingNewLayoutBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 16, sIncludes, sViewsWithIds));
    }
    private CameraSettingNewLayoutBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[14]
            , (android.widget.ImageView) bindings[2]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[15]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[5]
            , (android.widget.TextView) bindings[3]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[8]
            , (android.widget.TextView) bindings[7]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[9]
            , (android.widget.RelativeLayout) bindings[1]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[4]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[13]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[11]
            , (android.widget.TextView) bindings[10]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[12]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[6]
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