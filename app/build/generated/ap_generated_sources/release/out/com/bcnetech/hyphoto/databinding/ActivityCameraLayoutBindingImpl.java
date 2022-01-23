package com.bcnetech.hyphoto.databinding;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityCameraLayoutBindingImpl extends ActivityCameraLayoutBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.temp_cover, 1);
        sViewsWithIds.put(R.id.ai_hint_view, 2);
        sViewsWithIds.put(R.id.surfaceview, 3);
        sViewsWithIds.put(R.id.focus_center, 4);
        sViewsWithIds.put(R.id.focus_alpha, 5);
        sViewsWithIds.put(R.id.blueToothListView, 6);
        sViewsWithIds.put(R.id.title, 7);
        sViewsWithIds.put(R.id.iv_hander, 8);
        sViewsWithIds.put(R.id.ll_bottom, 9);
        sViewsWithIds.put(R.id.rl_scale_content, 10);
        sViewsWithIds.put(R.id.rl_Scale, 11);
        sViewsWithIds.put(R.id.horizontalscalescrollview, 12);
        sViewsWithIds.put(R.id.camera_point_view, 13);
        sViewsWithIds.put(R.id.tv_reduce, 14);
        sViewsWithIds.put(R.id.tv_add, 15);
        sViewsWithIds.put(R.id.preset_bottom_view, 16);
        sViewsWithIds.put(R.id.cl_top, 17);
        sViewsWithIds.put(R.id.iv_close, 18);
        sViewsWithIds.put(R.id.iv_cobox, 19);
    }
    // views
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityCameraLayoutBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 20, sIncludes, sViewsWithIds));
    }
    private ActivityCameraLayoutBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (com.bcnetech.hyphoto.ui.view.AIHintView) bindings[2]
            , (com.bcnetech.hyphoto.ui.view.BlueToothListNewView) bindings[6]
            , (com.bcnetech.hyphoto.ui.view.CameraPointView) bindings[13]
            , (android.support.constraint.ConstraintLayout) bindings[0]
            , (android.support.constraint.ConstraintLayout) bindings[17]
            , (com.bcnetech.hyphoto.ui.view.FocusAlphaview) bindings[5]
            , (com.bcnetech.hyphoto.ui.view.FocusCenterView) bindings[4]
            , (com.bcnetech.hyphoto.ui.view.scaleview.HorizontalScaleScrollview) bindings[12]
            , (android.widget.ImageView) bindings[18]
            , (android.widget.ImageView) bindings[19]
            , (android.widget.ImageView) bindings[8]
            , (android.widget.LinearLayout) bindings[9]
            , (com.bcnetech.hyphoto.ui.view.PresetBottomView) bindings[16]
            , (android.widget.RelativeLayout) bindings[11]
            , (android.widget.RelativeLayout) bindings[10]
            , (android.opengl.GLSurfaceView) bindings[3]
            , (android.widget.RelativeLayout) bindings[1]
            , (android.widget.TextView) bindings[7]
            , (android.widget.TextView) bindings[15]
            , (android.widget.TextView) bindings[14]
            );
        this.clLayout.setTag(null);
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