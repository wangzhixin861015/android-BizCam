package com.bcnetech.hyphoto.databinding;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class BlueToothListC2ViewBindingImpl extends BlueToothListC2ViewBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.iv_content, 1);
        sViewsWithIds.put(R.id.ll_main, 2);
        sViewsWithIds.put(R.id.camera_texture_view, 3);
        sViewsWithIds.put(R.id.iv_top, 4);
        sViewsWithIds.put(R.id.iv_left, 5);
        sViewsWithIds.put(R.id.iv_right, 6);
        sViewsWithIds.put(R.id.iv_bottom, 7);
        sViewsWithIds.put(R.id.ll_top, 8);
        sViewsWithIds.put(R.id.ll_choice, 9);
        sViewsWithIds.put(R.id.ll_listChoice, 10);
        sViewsWithIds.put(R.id.iv_listChoice, 11);
        sViewsWithIds.put(R.id.tv_listChoice, 12);
        sViewsWithIds.put(R.id.ll_scanChoice, 13);
        sViewsWithIds.put(R.id.iv_scanChoice, 14);
        sViewsWithIds.put(R.id.tv_scanChoice, 15);
        sViewsWithIds.put(R.id.rl_content, 16);
        sViewsWithIds.put(R.id.list_view, 17);
        sViewsWithIds.put(R.id.empty, 18);
        sViewsWithIds.put(R.id.qrcode_content, 19);
        sViewsWithIds.put(R.id.capture_crop_view, 20);
        sViewsWithIds.put(R.id.capture_scan_line, 21);
        sViewsWithIds.put(R.id.ll_blueTooth, 22);
        sViewsWithIds.put(R.id.tv_blueTooth, 23);
        sViewsWithIds.put(R.id.tv_disConnection, 24);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public BlueToothListC2ViewBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 25, sIncludes, sViewsWithIds));
    }
    private BlueToothListC2ViewBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (com.bcnetech.bizcamerlibrary.camera.CameraTextureView) bindings[3]
            , (android.widget.RelativeLayout) bindings[20]
            , (android.widget.ImageView) bindings[21]
            , (com.bcnetech.bcnetechlibrary.view.ListviewWaitView) bindings[18]
            , (android.widget.ImageView) bindings[7]
            , (android.widget.ImageView) bindings[1]
            , (android.widget.ImageView) bindings[5]
            , (android.widget.ImageView) bindings[11]
            , (android.widget.ImageView) bindings[6]
            , (android.widget.ImageView) bindings[14]
            , (android.widget.ImageView) bindings[4]
            , (android.widget.ListView) bindings[17]
            , (android.widget.LinearLayout) bindings[22]
            , (android.widget.LinearLayout) bindings[9]
            , (android.widget.LinearLayout) bindings[10]
            , (android.widget.RelativeLayout) bindings[2]
            , (android.widget.LinearLayout) bindings[13]
            , (android.widget.LinearLayout) bindings[8]
            , (android.widget.RelativeLayout) bindings[19]
            , (android.widget.RelativeLayout) bindings[16]
            , (android.widget.TextView) bindings[23]
            , (android.widget.TextView) bindings[24]
            , (android.widget.TextView) bindings[12]
            , (android.widget.TextView) bindings[15]
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