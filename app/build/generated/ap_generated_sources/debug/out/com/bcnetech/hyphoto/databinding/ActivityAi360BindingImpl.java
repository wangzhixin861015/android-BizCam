package com.bcnetech.hyphoto.databinding;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityAi360BindingImpl extends ActivityAi360Binding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.ai_hint_view, 1);
        sViewsWithIds.put(R.id.photoViewlayout, 2);
        sViewsWithIds.put(R.id.surfaceview, 3);
        sViewsWithIds.put(R.id.focus_view, 4);
        sViewsWithIds.put(R.id.draw_rect, 5);
        sViewsWithIds.put(R.id.cl_top, 6);
        sViewsWithIds.put(R.id.iv_close, 7);
        sViewsWithIds.put(R.id.iv_cobox, 8);
        sViewsWithIds.put(R.id.iv_360hint, 9);
        sViewsWithIds.put(R.id.tv_360hint, 10);
        sViewsWithIds.put(R.id.ai360preview, 11);
        sViewsWithIds.put(R.id.bottom_shade, 12);
        sViewsWithIds.put(R.id.ll_bottom, 13);
        sViewsWithIds.put(R.id.rl_rl_scale, 14);
        sViewsWithIds.put(R.id.rl_Scale, 15);
        sViewsWithIds.put(R.id.horizontalscalescrollview, 16);
        sViewsWithIds.put(R.id.camera_point_view, 17);
        sViewsWithIds.put(R.id.tv_reduce, 18);
        sViewsWithIds.put(R.id.tv_add, 19);
        sViewsWithIds.put(R.id.preset_bottom_view, 20);
        sViewsWithIds.put(R.id.rl_btn, 21);
        sViewsWithIds.put(R.id.camera_main_btn, 22);
        sViewsWithIds.put(R.id.video_button_btn, 23);
        sViewsWithIds.put(R.id.panorama_edit, 24);
        sViewsWithIds.put(R.id.blueToothListView, 25);
    }
    // views
    @NonNull
    private final android.support.constraint.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityAi360BindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 26, sIncludes, sViewsWithIds));
    }
    private ActivityAi360BindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (com.bcnetech.hyphoto.ui.view.AI360PreView) bindings[11]
            , (com.bcnetech.hyphoto.ui.view.AIHintView) bindings[1]
            , (com.bcnetech.hyphoto.ui.view.BlueToothListNewView) bindings[25]
            , (android.view.View) bindings[12]
            , (com.bcnetech.hyphoto.ui.view.clickanimview.BamImageView) bindings[22]
            , (com.bcnetech.hyphoto.ui.view.CameraPointView) bindings[17]
            , (android.support.constraint.ConstraintLayout) bindings[6]
            , (com.bcnetech.hyphoto.ui.view.DrawRectView) bindings[5]
            , (com.bcnetech.hyphoto.ui.view.FocusView) bindings[4]
            , (com.bcnetech.hyphoto.ui.view.scaleview.HorizontalScaleScrollview) bindings[16]
            , (android.widget.ImageView) bindings[9]
            , (android.widget.ImageView) bindings[7]
            , (android.widget.ImageView) bindings[8]
            , (android.widget.LinearLayout) bindings[13]
            , (com.bcnetech.hyphoto.ui.view.PanoramaEditView) bindings[24]
            , (android.widget.RelativeLayout) bindings[2]
            , (com.bcnetech.hyphoto.ui.view.PresetBottomView) bindings[20]
            , (android.widget.RelativeLayout) bindings[21]
            , (android.widget.RelativeLayout) bindings[14]
            , (android.widget.RelativeLayout) bindings[15]
            , (android.opengl.GLSurfaceView) bindings[3]
            , (android.widget.TextView) bindings[10]
            , (android.widget.TextView) bindings[19]
            , (android.widget.TextView) bindings[18]
            , (com.bcnetech.hyphoto.ui.view.VideoButtonView) bindings[23]
            );
        this.mboundView0 = (android.support.constraint.ConstraintLayout) bindings[0];
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