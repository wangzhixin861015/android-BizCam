package com.bcnetech.hyphoto.databinding;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class SurfviewCameraLayoutBindingImpl extends SurfviewCameraLayoutBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.camera_layout, 1);
        sViewsWithIds.put(R.id.camera_texture, 2);
        sViewsWithIds.put(R.id.focusview, 3);
        sViewsWithIds.put(R.id.fake_view, 4);
        sViewsWithIds.put(R.id.qrcode_content, 5);
        sViewsWithIds.put(R.id.capture_crop_view, 6);
        sViewsWithIds.put(R.id.capture_scan_line, 7);
        sViewsWithIds.put(R.id.dotted_line, 8);
        sViewsWithIds.put(R.id.lineImageView, 9);
        sViewsWithIds.put(R.id.iv_subline, 10);
        sViewsWithIds.put(R.id.top_layout, 11);
        sViewsWithIds.put(R.id.title_layout, 12);
        sViewsWithIds.put(R.id.camera_close_btn, 13);
        sViewsWithIds.put(R.id.coBox_choose_top_view, 14);
        sViewsWithIds.put(R.id.camera_setting_btn, 15);
        sViewsWithIds.put(R.id.rl_seekBar, 16);
        sViewsWithIds.put(R.id.right_seekBar, 17);
        sViewsWithIds.put(R.id.scaleView, 18);
        sViewsWithIds.put(R.id.bottom_control, 19);
        sViewsWithIds.put(R.id.lightratioList, 20);
        sViewsWithIds.put(R.id.camera_bottom_view, 21);
        sViewsWithIds.put(R.id.preset_bottom_view, 22);
        sViewsWithIds.put(R.id.camera_adjust_layout, 23);
        sViewsWithIds.put(R.id.camera_param_adjust_view, 24);
        sViewsWithIds.put(R.id.camera_setting_view, 25);
        sViewsWithIds.put(R.id.blueToothListView, 26);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public SurfviewCameraLayoutBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 27, sIncludes, sViewsWithIds));
    }
    private SurfviewCameraLayoutBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (com.bcnetech.hyphoto.ui.view.BlueToothListCamera2View) bindings[26]
            , (android.widget.RelativeLayout) bindings[19]
            , (android.widget.RelativeLayout) bindings[23]
            , (com.bcnetech.hyphoto.ui.view.CameraBottomView) bindings[21]
            , (android.widget.ImageView) bindings[13]
            , (android.widget.RelativeLayout) bindings[1]
            , (com.bcnetech.hyphoto.ui.view.CameraParamAdjustView) bindings[24]
            , (android.widget.ImageView) bindings[15]
            , (com.bcnetech.hyphoto.ui.view.CameraSettingNewView) bindings[25]
            , (com.bcnetech.bizcamerlibrary.camera.CameraTextureView) bindings[2]
            , (android.widget.RelativeLayout) bindings[6]
            , (android.widget.ImageView) bindings[7]
            , (com.bcnetech.hyphoto.ui.view.CoBoxChooseTopView) bindings[14]
            , (com.bcnetech.hyphoto.ui.view.DottedLineView) bindings[8]
            , (android.widget.ImageView) bindings[4]
            , (com.bcnetech.hyphoto.ui.view.FocusView) bindings[3]
            , (com.bcnetech.bcnetechlibrary.view.SublineImageView) bindings[10]
            , (android.support.v7.widget.RecyclerView) bindings[20]
            , (com.bcnetech.bcnetechlibrary.view.LineImageView2) bindings[9]
            , (com.bcnetech.hyphoto.ui.view.PresetBottomView) bindings[22]
            , (android.widget.RelativeLayout) bindings[5]
            , (com.bcnetech.bcnetechlibrary.view.VerticalSeekBar) bindings[17]
            , (android.widget.RelativeLayout) bindings[16]
            , (com.bcnetech.hyphoto.ui.view.verticalscrollview.VerticalScaleNewView) bindings[18]
            , (android.widget.RelativeLayout) bindings[12]
            , (android.widget.RelativeLayout) bindings[11]
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