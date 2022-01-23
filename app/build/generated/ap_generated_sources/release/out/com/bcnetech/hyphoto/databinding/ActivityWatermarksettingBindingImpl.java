package com.bcnetech.hyphoto.databinding;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityWatermarksettingBindingImpl extends ActivityWatermarksettingBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.watermark_setting_title, 1);
        sViewsWithIds.put(R.id.watermark, 2);
        sViewsWithIds.put(R.id.rl_wm, 3);
        sViewsWithIds.put(R.id.tv_sample, 4);
        sViewsWithIds.put(R.id.rl_sample, 5);
        sViewsWithIds.put(R.id.sample_image, 6);
        sViewsWithIds.put(R.id.sample_watermark, 7);
        sViewsWithIds.put(R.id.rl_select_wm, 8);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityWatermarksettingBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 9, sIncludes, sViewsWithIds));
    }
    private ActivityWatermarksettingBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.RelativeLayout) bindings[5]
            , (com.bcnetech.hyphoto.ui.view.WMFlowLayout) bindings[8]
            , (android.widget.RelativeLayout) bindings[3]
            , (com.bcnetech.hyphoto.ui.view.ResizableImageView) bindings[6]
            , (android.widget.ImageView) bindings[7]
            , (android.widget.TextView) bindings[4]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingItemView) bindings[2]
            , (com.bcnetech.hyphoto.ui.view.TitleView) bindings[1]
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