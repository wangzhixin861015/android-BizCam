package com.bcnetech.hyphoto.databinding;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class SurfviewCameraNewLayoutBindingImpl extends SurfviewCameraNewLayoutBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.top_layout, 1);
        sViewsWithIds.put(R.id.title_layout, 2);
        sViewsWithIds.put(R.id.camera_close_btn, 3);
        sViewsWithIds.put(R.id.coBox_choose_top_view, 4);
        sViewsWithIds.put(R.id.camera_setting_btn, 5);
        sViewsWithIds.put(R.id.camera_setting_view, 6);
        sViewsWithIds.put(R.id.blueToothListView, 7);
        sViewsWithIds.put(R.id.cl_mcontent, 8);
        sViewsWithIds.put(R.id.camera_layout, 9);
        sViewsWithIds.put(R.id.surfaceview, 10);
        sViewsWithIds.put(R.id.camera_welcome_view, 11);
        sViewsWithIds.put(R.id.scaleView, 12);
        sViewsWithIds.put(R.id.focusview, 13);
        sViewsWithIds.put(R.id.rect_view, 14);
        sViewsWithIds.put(R.id.fake_view_top, 15);
        sViewsWithIds.put(R.id.fake_view_bottom, 16);
        sViewsWithIds.put(R.id.dotted_line, 17);
        sViewsWithIds.put(R.id.lineImageView, 18);
        sViewsWithIds.put(R.id.iv_subline, 19);
        sViewsWithIds.put(R.id.bottom_control, 20);
        sViewsWithIds.put(R.id.cl_adjust, 21);
        sViewsWithIds.put(R.id.camera_param_adjust_view, 22);
        sViewsWithIds.put(R.id.lightratioList, 23);
        sViewsWithIds.put(R.id.preset_bottom_view, 24);
        sViewsWithIds.put(R.id.camera_bottom_view, 25);
    }
    // views
    @NonNull
    private final android.support.constraint.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public SurfviewCameraNewLayoutBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 26, sIncludes, sViewsWithIds));
    }
    private SurfviewCameraNewLayoutBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (com.bcnetech.hyphoto.ui.view.BlueToothListNewView) bindings[7]
            , (android.support.constraint.ConstraintLayout) bindings[20]
            , (com.bcnetech.hyphoto.ui.view.CameraBottomView) bindings[25]
            , (android.widget.ImageView) bindings[3]
            , (android.support.constraint.ConstraintLayout) bindings[9]
            , (com.bcnetech.hyphoto.ui.view.CameraParamAdjustView) bindings[22]
            , (android.widget.ImageView) bindings[5]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingView) bindings[6]
            , (com.bcnetech.hyphoto.ui.view.CameraWelcomeView) bindings[11]
            , (android.support.constraint.ConstraintLayout) bindings[21]
            , (android.support.constraint.ConstraintLayout) bindings[8]
            , (com.bcnetech.hyphoto.ui.view.CoBoxChooseTopView) bindings[4]
            , (com.bcnetech.hyphoto.ui.view.DottedLineView) bindings[17]
            , (android.widget.ImageView) bindings[16]
            , (android.widget.ImageView) bindings[15]
            , (com.bcnetech.hyphoto.ui.view.FocusView) bindings[13]
            , (com.bcnetech.bcnetechlibrary.view.SublineImageView) bindings[19]
            , (android.support.v7.widget.RecyclerView) bindings[23]
            , (com.bcnetech.bcnetechlibrary.view.LineImageView2) bindings[18]
            , (com.bcnetech.hyphoto.ui.view.PresetBottomView) bindings[24]
            , (android.opengl.GLSurfaceView) bindings[14]
            , (com.bcnetech.hyphoto.ui.view.verticalscrollview.VerticalScaleNewView) bindings[12]
            , (android.opengl.GLSurfaceView) bindings[10]
            , (android.support.constraint.ConstraintLayout) bindings[2]
            , (android.support.constraint.ConstraintLayout) bindings[1]
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