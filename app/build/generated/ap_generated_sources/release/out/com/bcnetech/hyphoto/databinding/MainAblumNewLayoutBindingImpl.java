package com.bcnetech.hyphoto.databinding;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class MainAblumNewLayoutBindingImpl extends MainAblumNewLayoutBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = new android.databinding.ViewDataBinding.IncludedLayouts(23);
        sIncludes.setIncludes(1, 
            new String[] {"ablum_new_layout"},
            new int[] {2},
            new int[] {com.bcnetech.hyphoto.R.layout.ablum_new_layout});
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.bg_blur_line, 3);
        sViewsWithIds.put(R.id.bg_blur, 4);
        sViewsWithIds.put(R.id.big_imgview, 5);
        sViewsWithIds.put(R.id.rl_bigImage, 6);
        sViewsWithIds.put(R.id.photoView, 7);
        sViewsWithIds.put(R.id.view_pager_video_view, 8);
        sViewsWithIds.put(R.id.view_pager_fake3dview, 9);
        sViewsWithIds.put(R.id.image_utils_view, 10);
        sViewsWithIds.put(R.id.blur_imageview, 11);
        sViewsWithIds.put(R.id.blueToothListView, 12);
        sViewsWithIds.put(R.id.drawerview, 13);
        sViewsWithIds.put(R.id.panelHandle, 14);
        sViewsWithIds.put(R.id.new_shop, 15);
        sViewsWithIds.put(R.id.up, 16);
        sViewsWithIds.put(R.id.down, 17);
        sViewsWithIds.put(R.id.name, 18);
        sViewsWithIds.put(R.id.new_cam, 19);
        sViewsWithIds.put(R.id.panelContent, 20);
        sViewsWithIds.put(R.id.sviewPager, 21);
        sViewsWithIds.put(R.id.indicatorView, 22);
    }
    // views
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public MainAblumNewLayoutBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 23, sIncludes, sViewsWithIds));
    }
    private MainAblumNewLayoutBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (android.widget.ImageView) bindings[4]
            , (android.widget.LinearLayout) bindings[3]
            , (com.bcnetech.hyphoto.ui.view.AlbumViewPager) bindings[5]
            , (com.bcnetech.hyphoto.ui.view.BlueToothListNewView) bindings[12]
            , (android.widget.ImageView) bindings[11]
            , (android.widget.RelativeLayout) bindings[1]
            , (android.widget.ImageView) bindings[17]
            , (com.bcnetech.hyphoto.ui.view.DrawerView) bindings[13]
            , (com.bcnetech.hyphoto.ui.view.ImageNewUtilsView) bindings[10]
            , (com.bcnetech.hyphoto.databinding.AblumNewLayoutBinding) bindings[2]
            , (com.bcnetech.hyphoto.ui.view.viewPagerIndicator.view.indicator.FixedIndicatorView) bindings[22]
            , (android.widget.RelativeLayout) bindings[0]
            , (android.widget.TextView) bindings[18]
            , (android.widget.LinearLayout) bindings[19]
            , (android.widget.LinearLayout) bindings[15]
            , (android.widget.RelativeLayout) bindings[20]
            , (android.widget.LinearLayout) bindings[14]
            , (com.bcnetech.hyphoto.ui.view.photoview.PhotoView) bindings[7]
            , (android.widget.RelativeLayout) bindings[6]
            , (android.widget.RelativeLayout) bindings[21]
            , (android.widget.ImageView) bindings[16]
            , (com.bcnetech.hyphoto.ui.view.ShowFake3DView) bindings[9]
            , (com.bcnetech.hyphoto.ui.view.VideoView) bindings[8]
            );
        this.contentRel.setTag(null);
        this.mablum.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x2L;
        }
        include.invalidateAll();
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        if (include.hasPendingBindings()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    public void setLifecycleOwner(@Nullable android.arch.lifecycle.LifecycleOwner lifecycleOwner) {
        super.setLifecycleOwner(lifecycleOwner);
        include.setLifecycleOwner(lifecycleOwner);
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeInclude((com.bcnetech.hyphoto.databinding.AblumNewLayoutBinding) object, fieldId);
        }
        return false;
    }
    private boolean onChangeInclude(com.bcnetech.hyphoto.databinding.AblumNewLayoutBinding Include, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
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
        executeBindingsOn(include);
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): include
        flag 1 (0x2L): null
    flag mapping end*/
    //end
}