package com.bcnetech.hyphoto.databinding;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class BlueToothListNewPopBindingImpl extends BlueToothListNewPopBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.iv_content, 1);
        sViewsWithIds.put(R.id.ll_main, 2);
        sViewsWithIds.put(R.id.surfaceview, 3);
        sViewsWithIds.put(R.id.iv_top, 4);
        sViewsWithIds.put(R.id.iv_left, 5);
        sViewsWithIds.put(R.id.iv_right, 6);
        sViewsWithIds.put(R.id.rl_bottom, 7);
        sViewsWithIds.put(R.id.tv_qrcode_help, 8);
        sViewsWithIds.put(R.id.ll_top, 9);
        sViewsWithIds.put(R.id.ll_choice, 10);
        sViewsWithIds.put(R.id.ll_listChoice, 11);
        sViewsWithIds.put(R.id.iv_listChoice, 12);
        sViewsWithIds.put(R.id.tv_listChoice, 13);
        sViewsWithIds.put(R.id.ll_scanChoice, 14);
        sViewsWithIds.put(R.id.iv_scanChoice, 15);
        sViewsWithIds.put(R.id.tv_scanChoice, 16);
        sViewsWithIds.put(R.id.rl_bt_close, 17);
        sViewsWithIds.put(R.id.tv_bt_close, 18);
        sViewsWithIds.put(R.id.rl_content, 19);
        sViewsWithIds.put(R.id.list_view, 20);
        sViewsWithIds.put(R.id.empty, 21);
        sViewsWithIds.put(R.id.qrcode_content, 22);
        sViewsWithIds.put(R.id.capture_crop_view, 23);
        sViewsWithIds.put(R.id.capture_scan_line, 24);
        sViewsWithIds.put(R.id.ll_blueTooth, 25);
        sViewsWithIds.put(R.id.tv_blueTooth, 26);
        sViewsWithIds.put(R.id.tv_disConnection, 27);
        sViewsWithIds.put(R.id.ll_qrhelp, 28);
        sViewsWithIds.put(R.id.iv_cobox_qr, 29);
        sViewsWithIds.put(R.id.tv_cobox_help, 30);
        sViewsWithIds.put(R.id.rl_confirm, 31);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public BlueToothListNewPopBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 32, sIncludes, sViewsWithIds));
    }
    private BlueToothListNewPopBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.RelativeLayout) bindings[23]
            , (android.widget.ImageView) bindings[24]
            , (com.bcnetech.bcnetechlibrary.view.ListviewWaitView) bindings[21]
            , (android.widget.ImageView) bindings[29]
            , (android.widget.ImageView) bindings[1]
            , (android.widget.ImageView) bindings[5]
            , (android.widget.ImageView) bindings[12]
            , (android.widget.ImageView) bindings[6]
            , (android.widget.ImageView) bindings[15]
            , (android.widget.ImageView) bindings[4]
            , (android.widget.ListView) bindings[20]
            , (android.widget.LinearLayout) bindings[25]
            , (android.widget.LinearLayout) bindings[10]
            , (android.widget.LinearLayout) bindings[11]
            , (android.widget.RelativeLayout) bindings[2]
            , (android.widget.RelativeLayout) bindings[28]
            , (android.widget.LinearLayout) bindings[14]
            , (android.widget.LinearLayout) bindings[9]
            , (android.widget.RelativeLayout) bindings[22]
            , (android.widget.RelativeLayout) bindings[7]
            , (android.widget.RelativeLayout) bindings[17]
            , (android.widget.RelativeLayout) bindings[31]
            , (android.widget.RelativeLayout) bindings[19]
            , (android.opengl.GLSurfaceView) bindings[3]
            , (android.widget.TextView) bindings[26]
            , (android.widget.TextView) bindings[18]
            , (android.widget.TextView) bindings[30]
            , (android.widget.TextView) bindings[27]
            , (android.widget.TextView) bindings[13]
            , (android.widget.TextView) bindings[8]
            , (android.widget.TextView) bindings[16]
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