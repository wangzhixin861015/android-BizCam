package com.bcnetech.hyphoto.databinding;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class PartParmsLayoutBindingImpl extends PartParmsLayoutBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.paint_title, 1);
        sViewsWithIds.put(R.id.paint_foot, 2);
        sViewsWithIds.put(R.id.paint_content, 3);
        sViewsWithIds.put(R.id.btnevon, 4);
        sViewsWithIds.put(R.id.eraser, 5);
        sViewsWithIds.put(R.id.btncolouron, 6);
        sViewsWithIds.put(R.id.eye, 7);
        sViewsWithIds.put(R.id.content_layout, 8);
        sViewsWithIds.put(R.id.gpuimageview_texture, 9);
        sViewsWithIds.put(R.id.final_img, 10);
        sViewsWithIds.put(R.id.paintview, 11);
        sViewsWithIds.put(R.id.wait_view, 12);
    }
    // views
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public PartParmsLayoutBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 13, sIncludes, sViewsWithIds));
    }
    private PartParmsLayoutBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.ImageView) bindings[6]
            , (android.widget.ImageView) bindings[4]
            , (android.widget.RelativeLayout) bindings[0]
            , (com.bcnetech.hyphoto.ui.view.ZoomableViewGroup) bindings[8]
            , (android.widget.ImageView) bindings[5]
            , (com.bcnetech.bcnetechlibrary.view.MyImageView) bindings[7]
            , (android.widget.ImageView) bindings[10]
            , (jp.co.cyberagent.android.gpuimage.gputexture.GPUImageViewTexture) bindings[9]
            , (android.widget.RelativeLayout) bindings[3]
            , (android.widget.LinearLayout) bindings[2]
            , (com.bcnetech.hyphoto.ui.view.TitleView) bindings[1]
            , (com.bcnetech.hyphoto.ui.view.PaintView) bindings[11]
            , (com.bcnetech.hyphoto.ui.view.WaitProgressBarView) bindings[12]
            );
        this.content.setTag(null);
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