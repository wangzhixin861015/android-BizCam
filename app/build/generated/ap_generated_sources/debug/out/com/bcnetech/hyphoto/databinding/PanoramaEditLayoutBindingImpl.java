package com.bcnetech.hyphoto.databinding;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class PanoramaEditLayoutBindingImpl extends PanoramaEditLayoutBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.tv_title, 1);
        sViewsWithIds.put(R.id.iv_back, 2);
        sViewsWithIds.put(R.id.gpuimage, 3);
        sViewsWithIds.put(R.id.show3dview, 4);
        sViewsWithIds.put(R.id.sb_adjust, 5);
        sViewsWithIds.put(R.id.filter_cover, 6);
        sViewsWithIds.put(R.id.filter_name, 7);
        sViewsWithIds.put(R.id.et_title, 8);
        sViewsWithIds.put(R.id.rl_button, 9);
        sViewsWithIds.put(R.id.tv_button, 10);
        sViewsWithIds.put(R.id.filterwait, 11);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public PanoramaEditLayoutBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 12, sIncludes, sViewsWithIds));
    }
    private PanoramaEditLayoutBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.EditText) bindings[8]
            , (com.bcnetech.hyphoto.ui.view.FilterCoverView) bindings[6]
            , (android.widget.LinearLayout) bindings[7]
            , (com.bcnetech.hyphoto.ui.view.FilterWaitView) bindings[11]
            , (jp.co.cyberagent.android.gpuimage.gputexture.GPUImageViewTexture) bindings[3]
            , (android.widget.ImageView) bindings[2]
            , (android.widget.RelativeLayout) bindings[9]
            , (com.bcnetech.hyphoto.ui.view.FilterProcessView) bindings[5]
            , (com.bcnetech.hyphoto.ui.view.ShowFake3DView) bindings[4]
            , (android.widget.TextView) bindings[10]
            , (android.widget.RelativeLayout) bindings[1]
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