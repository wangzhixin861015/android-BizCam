package com.bcnetech.hyphoto.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.Folder;
import com.bcnetech.hyphoto.data.SDCardMedia;
import com.bcnetech.hyphoto.databinding.ActivityImageSelectBinding;
import com.bcnetech.hyphoto.model.ImageModel;
import com.bcnetech.hyphoto.ui.adapter.ImageAdapter;
import com.bcnetech.hyphoto.ui.adapter.RVFolderAdapter;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.utils.SpacesItemDecoration;
import com.bcnetech.hyphoto.utils.cache.CacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 选择本地图片
 */
public class ImageSelectorActivity extends BaseActivity {
    ActivityImageSelectBinding activityImageSelectBinding;
    private ImageAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;

    private ArrayList<Folder> mFolders;
    private Folder mFolder;
    private boolean isToSettings = false;
    private boolean canClick = true;
    private Queue<SDCardMedia> SDCardMediaQueue;

    private RVFolderAdapter rvFolderAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        onViewClick();
    }


    @Override
    protected void initData() {
        activityImageSelectBinding.selectorTitle.setType(TitleView.INTO);
        activityImageSelectBinding.selectorTitle.setTitleText(getResources().getString(R.string.lead_in));
        initImageList();
        checkPermissionAndLoadImages();

    }

    @Override
    protected void onViewClick() {

        if (rvFolderAdapter != null) {
            rvFolderAdapter.setOnItemClickListener(new RVFolderAdapter.ItemClickListener() {
                @Override
                public void OnItemClick(View v, int position) {
                    setTitleClickMove(position);
                    setFolder(mFolders.get(position));
                }
            });
        }
        activityImageSelectBinding.selectorTitle.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canClick) {
                    finish();
                }
            }
        });
        activityImageSelectBinding.selectorTitle.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canClick) {
                    confirm();
                }
            }
        });
    }

    @Override
    protected void initView() {
        activityImageSelectBinding = DataBindingUtil.setContentView(this,R.layout.activity_image_select);
    }
    public void setTitleClickMove(int position) {
        RecyclerView.LayoutManager layoutManager = activityImageSelectBinding.recTitle.getLayoutManager();
        RecyclerView.State state = new RecyclerView.State();
        if (layoutManager instanceof ScrollSpeedLinearLayoutManger) {
            ScrollSpeedLinearLayoutManger linearManager = (ScrollSpeedLinearLayoutManger) layoutManager;
            if (position < mFolders.size()) {
                //获取最后一个可见view的位置
                int lastItemPositionComplete = linearManager.findLastCompletelyVisibleItemPosition();
                int lastItemPosition = linearManager.findLastVisibleItemPosition();
                //获取第一个可见view的位置
                int firstItemPositionComplete = linearManager.findFirstCompletelyVisibleItemPosition();
                int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                if (position != 0) {
                    if (position == firstItemPosition || position == firstItemPositionComplete) {
                        layoutManager.smoothScrollToPosition(activityImageSelectBinding.recTitle, state, position - 1);
                    } else if (position == lastItemPosition || position == lastItemPositionComplete) {
                        layoutManager.smoothScrollToPosition(activityImageSelectBinding.recTitle, state, position + 1);
                    }
                } else {
                    //初始值位置
                    layoutManager.smoothScrollToPosition(activityImageSelectBinding.recTitle, state, position);
                }
                // layoutManager.smoothScrollToPosition(rec_title, state, position);
            }
        }
    }

    /**
     * 初始化图片列表
     */
    private void initImageList() {
        SpacesItemDecoration decoration = new SpacesItemDecoration(4, SpacesItemDecoration.TYPE_INTO);
        activityImageSelectBinding.rvImage.addItemDecoration(decoration);

        mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        activityImageSelectBinding.rvImage.setLayoutManager(mLayoutManager);
        activityImageSelectBinding.rvImage.setHasFixedSize(true);
        mAdapter = new ImageAdapter(this, 0, false);
        activityImageSelectBinding.rvImage.setAdapter(mAdapter);
        mAdapter.setOnImageSelectListener(new ImageAdapter.OnImageSelectListener() {
            @Override
            public void OnImageSelect(SDCardMedia SDCardMedia, boolean isSelect, int selectCount) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(getResources().getString(R.string.lead_in));
                if (selectCount!=0){
                    stringBuffer.append(" ");
                    stringBuffer.append("(");
                    stringBuffer.append(ImageSelectorActivity.this.getResources().getString(R.string.selected));
                    stringBuffer.append(selectCount);
                    stringBuffer.append(")");
                }
                activityImageSelectBinding.selectorTitle.setTitleText(stringBuffer.toString());
            }
        });
        ((SimpleItemAnimator) activityImageSelectBinding.rvImage.getItemAnimator()).setSupportsChangeAnimations(false);


        ScrollSpeedLinearLayoutManger linearLayoutManager = new ScrollSpeedLinearLayoutManger(ImageSelectorActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        activityImageSelectBinding.recTitle.setLayoutManager(linearLayoutManager);
        activityImageSelectBinding.recTitle.setHasFixedSize(true);
        rvFolderAdapter = new RVFolderAdapter(this);
        activityImageSelectBinding.recTitle.setAdapter(rvFolderAdapter);
        ((SimpleItemAnimator) activityImageSelectBinding.recTitle.getItemAnimator()).setSupportsChangeAnimations(false);




        if (mFolders != null && !mFolders.isEmpty()) {
            setFolders(mFolders);
            setFolder(mFolders.get(0));

        }

        mAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(SDCardMedia SDCardMedia, int position) {
                // toPreviewActivity(mAdapter.getData(), position);
            }
        });
    }

    /**
     * 控制滑动速度的LinearLayoutManager
     */
    public class ScrollSpeedLinearLayoutManger extends LinearLayoutManager {
        private float MILLISECONDS_PER_INCH = 0.8f;
        private Context contxt;

        public ScrollSpeedLinearLayoutManger(Context context) {
            super(context);
            this.contxt = context;
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
            LinearSmoothScroller linearSmoothScroller =
                    new LinearSmoothScroller(recyclerView.getContext()) {
                        @Override
                        public PointF computeScrollVectorForPosition(int targetPosition) {
                            return ScrollSpeedLinearLayoutManger.this
                                    .computeScrollVectorForPosition(targetPosition);
                        }

                        //This returns the milliseconds it takes to
                        //scroll one pixel.
                        @Override
                        protected float calculateSpeedPerPixel
                        (DisplayMetrics displayMetrics) {
                            return MILLISECONDS_PER_INCH / displayMetrics.density;
                            //返回滑动一个pixel需要多少毫秒
                        }

                    };
            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }


        public void setSpeedSlow() {
            //自己在这里用density去乘，希望不同分辨率设备上滑动速度相同
            //0.3f是自己估摸的一个值，可以根据不同需求自己修改
            MILLISECONDS_PER_INCH = contxt.getResources().getDisplayMetrics().density * 0.3f;
        }

        public void setSpeedFast() {
            MILLISECONDS_PER_INCH = contxt.getResources().getDisplayMetrics().density * 0.03f;
        }
    }

    /**
     * 设置选中的文件夹，同时刷新图片列表
     *
     * @param folder
     */
    private void setFolder(Folder folder) {
        if (folder != null && mAdapter != null && !folder.equals(mFolder)) {
            mFolder = folder;
            activityImageSelectBinding.rvImage.scrollToPosition(0);
            mAdapter.refresh(folder.getSDCardMedias());
        }
    }

    private void setFolders(ArrayList<Folder> folders) {
        if (folders != null ) {
            mFolders = folders;
            activityImageSelectBinding.recTitle.scrollToPosition(0);
            rvFolderAdapter.refresh(mFolders);
        }
    }

    private void confirm() {
        if (mAdapter == null) {
            return;
        }
        if (mAdapter.getSelectImages().size() == 0) {
            ToastUtil.toast(getResources().getString(R.string.pic_select_none));
            return;
        }
        //因为图片的实体类是Image，而我们返回的是String数组，所以要进行转换。
        final List<SDCardMedia> selectSDCardMedias = mAdapter.getSelectImages();


        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {

                boolean isSuccess= CacheManager.saveObject(ImageSelectorActivity.this,selectSDCardMedias,"ImageSelect");

                emitter.onNext(isSuccess);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    private Disposable mDisposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Boolean isSuccess) {

                        if(isSuccess){
                            Intent intent = new Intent();
//        Bundle bundle = new Bundle();
//        bundle.putParcelableArrayList(Flag.IMAGESELECTOR,selectSDCardMedias);
//        intent.putExtras(bundle);
                            ImageSelectorActivity.this.setResult(Flag.IMAGESELECTOR_ACTIVITY,intent);
                            finish();
                        }else {
                            ToastUtil.toast("失败");
                        }

                        mDisposable.dispose();


                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });





    }



    @Override
    protected void onStart() {
        super.onStart();
        if (isToSettings) {
            isToSettings = false;
            checkPermissionAndLoadImages();
        }
    }


    /**
     * 检查权限并加载SD卡里的图片。
     */
    private void checkPermissionAndLoadImages() {
        loadImageForSDCard();
    }



    /**
     * 从SDCard加载图片。
     */
    private void loadImageForSDCard() {
        ImageModel.loadImageForSDCard(this, new ImageModel.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Folder> folders) {
                mFolders = folders;


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mFolders != null && !mFolders.isEmpty()) {
                            setFolders(mFolders);
                            setFolder(mFolders.get(0));

                        }
                    }
                });
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (canClick) {
            super.onBackPressed();
        }
    }
}
