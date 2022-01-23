package com.bcnetech.hyphoto.ui.activity.cloud;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetchhttp.RetrofitFactory;
import com.bcnetech.bcnetchhttp.base.BaseObserver;
import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.bean.request.ModifyScope;
import com.bcnetech.bcnetchhttp.bean.response.CloudPicData;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.bcnetechlibrary.dialog.ChoiceDialog;
import com.bcnetech.bcnetechlibrary.dialog.ProgressDialog;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.view.stickygridheaders.StickyGridHeadersGridView;
import com.bcnetech.hyphoto.data.GridItem;
import com.bcnetech.hyphoto.data.SqlControl.ImageDataSqlControl;
import com.bcnetech.hyphoto.data.UploadCloudData;
import com.bcnetech.hyphoto.receiver.AddPicReceiver;
import com.bcnetech.hyphoto.receiver.DeleteCloudReceiver;
import com.bcnetech.hyphoto.receiver.DownloadPicReceiver;
import com.bcnetech.hyphoto.receiver.HttpChangReceiver;
import com.bcnetech.hyphoto.task.MBaseObserver;
import com.bcnetech.hyphoto.ui.adapter.CloudInfoAdapter;
import com.bcnetech.hyphoto.ui.adapter.CloudInfoPicBrowsingAdapter;
import com.bcnetech.hyphoto.ui.adapter.StickyGridAdapter;
import com.bcnetech.hyphoto.ui.view.AlbumViewPager;
import com.bcnetech.hyphoto.ui.view.BottomToolView;
import com.bcnetech.hyphoto.ui.view.ChoiceView;
import com.bcnetech.hyphoto.ui.view.CloudTopSelectView;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.ui.view.ViewPagerVideoView;
import com.bcnetech.hyphoto.ui.view.photoview.PhotoView;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.SpacesItemDecoration;
import com.bcnetech.hyphoto.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.VISIBLE;

/**
 * 云相册的具体图片展示
 * Created by wenbin on 16/6/30.
 */
public class CloudInfoActivity extends BaseActivity {
    public static final int TYPE_CLOUD = 1;
    public static final int TYPE_NATIVE = 2;
    private TitleView titleView;
    private RecyclerView recyclerView;
    private CloudInfoAdapter adapter;
    private BottomToolView bottom_tool_view;
    private ChoiceView choiceview_upload;
    private AlbumViewPager big_imgview;
    private RelativeLayout rl_cloud;

    private List<CloudPicData> listdata;
    private ValueAnimator bottomToolViewIn, bottomToolViewOut;
    private int mgridviewH, mgridviewW;
    private int clicknum = 0;
    private Vibrator vibrator;
    private ValueAnimator animShow, animClose;
    private boolean isShowImg;
    private ImageDataSqlControl imageDataSqlControl;
    private StickyGridHeadersGridView mGridView;
    private StickyGridAdapter madapter;
    private CloudInfoPicBrowsingAdapter picBrowsingAdapter;
    private List<GridItem> mGirdList;
    private List<UploadCloudData> muploadList;
    private int itemcheckcount;
    private CloudTopSelectView cloudInfoTopSelectView;
    private RelativeLayout top_layout;
    private int itemClickCount = 0;//当前选中item数量
    private ChoiceDialog mchoiceDialog;
    private int typeCode;
    private HttpChangReceiver httpChangReceiver;
    private boolean isWifiMode = true;
    private AlertDialog alertDialog;

    private boolean isChangePage = false;
    public int position;
    private int type = SMALL_IMAGE;
    private boolean isCanClick = false;
    private static final int SMALL_IMAGE = 1;
    private static final int BIG_IMAGE = 2;
    private PopupWindow morePopupWindow;
    private ProgressDialog dgProgressDialog2;
    private RelativeLayout empty_null;
    private CloudInfoAdapter.ItemClickInterface itemClickInterface;


    public static void actionStart(Activity context, int typeCode) {
        Intent intent = new Intent(context, CloudInfoActivity.class);
        intent.putExtra("WaterMarkSelect", typeCode);
        if (typeCode == CloundNewActivity.CLOUDWATERMARK) {
            context.startActivityForResult(intent, Flag.CLOUDWATERMARK);
        } else {
            context.startActivity(intent);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cloud_info_layout);
        typeCode = (int) this.getIntent().getSerializableExtra("WaterMarkSelect");
        initView();
        initData();
        onViewClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (httpChangReceiver != null) {
            httpChangReceiver.unregister(this);
        }

    }

    protected void initView() {
        choiceview_upload = (ChoiceView) findViewById(R.id.choiceview_upload);
        titleView = (TitleView) findViewById(R.id.title_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        big_imgview = (AlbumViewPager) findViewById(R.id.big_imgview);
        bottom_tool_view = (BottomToolView) findViewById(R.id.bottom_tool_view);
        mGridView = (StickyGridHeadersGridView) findViewById(R.id.ablum_grid);
        rl_cloud = (RelativeLayout) findViewById(R.id.rl_cloud);
        cloudInfoTopSelectView = findViewById(R.id.cloudInfoTopSelectView);
        top_layout = (RelativeLayout) findViewById(R.id.top_layout);
        empty_null = (RelativeLayout) findViewById(R.id.empty_null);
    }

    protected void initData() {
        picBrowsingAdapter = new CloudInfoPicBrowsingAdapter(this, null, new CloudInfoPicBrowsingAdapter.CloseInter() {
            @Override
            public void close() {
                if (animClose != null) {
                    animClose.start();
                }
            }

            @Override
            public void switchType(int type) {

            }
        });
        httpChangReceiver = new HttpChangReceiver() {

            @Override
            public void httpWifiConnection() {
                isWifiMode = true;
            }

            @Override
            public void httpModNetConnection() {
                isWifiMode = false;
            }

            @Override
            public void httpDisConnection() {
            }
        };
        httpChangReceiver.register(this);

        adapter = new CloudInfoAdapter(CloudInfoActivity.this, listdata);
        if (listdata == null || listdata.size() == 0) {
            getCloudInfoList();
        }
        bottom_tool_view.setType(BottomToolView.CLOUD_INFO);
        mgridviewW = ContentUtil.getScreenWidth(this);
        mgridviewH = ContentUtil.dip2px(this, 56);
        titleView.setType(TitleView.CLOUD_INFO);
        SpacesItemDecoration decoration = new SpacesItemDecoration(4, SpacesItemDecoration.TYPE_INTO);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (typeCode == CloundNewActivity.CLOUDWATERMARK) {
            titleView.setRightTextIsShow(false);

        } else {
            titleView.setRightTextIsShow(true);
        }

        initAnim();
    }


    protected void onViewClick() {
        titleView.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleView.setRightTextListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inTopSelect(/*SMALL_IMAGE*/);
            }
        });
        recyclerView.setOnTouchListener(new View.OnTouchListener()

                                        {
                                            @Override
                                            public boolean onTouch(View v, MotionEvent event) {
                                                switch (event.getAction()) {

                                                    case MotionEvent.ACTION_UP:
                        /*if(isShowImg) {
                            animClose.start();
                            isShowImg=false;
                        }*/

                                                        break;
                                                }
                                                return false;
                                            }
                                        }

        );

        cloudInfoTopSelectView.close(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outTopSelect();
            }
        });

        cloudInfoTopSelectView.more(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOtherPopupWindow(v);
            }
        });

        itemClickInterface = new CloudInfoAdapter.ItemClickInterface() {
            @Override
            public void itemClick(View view, int position) {
                if (typeCode == CloundNewActivity.CLOUDWATERMARK) {
                    if (listdata.get(position).getFormat().contains("video")) {
                        ToastUtil.toast(getResources().getString(R.string.watermark_no_video));
                    } else if(listdata.get(position).getFormat().contains("25d/25d")){
                        ToastUtil.toast(getResources().getString(R.string.watermark_no_360));
                    }else{
                        Intent intent = new Intent();
                        String urlorign = BitmapUtils.getBitmapUrl2(listdata.get(position).getFileId());
                        String url = com.bcnetech.hyphoto.utils.StringUtil.getSizeUrl(urlorign, 0, 0);
                        intent.putExtra("watermark", url);
                        setResult(Flag.CLOUDWATERMARK, intent);
                        finish();

                    }
                } else {
                    if (isCanClick) {
                        if (position >= 0 && !StringUtil.isBlank(listdata.get(position).getFileId())) {
                            if (listdata.get(position).isClick()) {
                                listdata.get(position).setClick(false);
                                itemClickCount--;
                                view.findViewById(R.id.grid_item_check).setVisibility(View.INVISIBLE);
                            } else {
                                listdata.get(position).setClick(true);
                                itemClickCount++;
                                view.findViewById(R.id.grid_item_check).setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        };
        adapter.setItemClickInterface(itemClickInterface);
        setCanClick(false);
    }


    private void getCloudInfoList() {
        // listdata = (List<CloudPicData>) CacheManager.readObject(CloudInfoActivity.this, group_id);
        if (true/*listdata == null || listdata.size() == 0 || CacheManager.isCacheDataFailure(CloudInfoActivity.this, group_id)*/) {
            RetrofitFactory.getInstence()
                    .API()
                    .getCloud()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MBaseObserver<List<CloudPicData>>(this, true) {
                        @Override
                        protected void onSuccees(BaseResult<List<CloudPicData>> t) throws Exception {
                            listdata = t.getData();
                            if (null == listdata || listdata.size() == 0) {
                                empty_null.setVisibility(VISIBLE);
                            } else {
                                empty_null.setVisibility(View.GONE);
                                /*if (typeCode == CloundNewActivity.CLOUDWATERMARK) {
                                    setCanClick(false);
                                } else {
                                    setCanClick(true);
                                }*/

                                adapter.setData(listdata);
                                adapter.notifyDataSetChanged();
                            }

//                    CacheManager.saveObject(CloudInfoActivity.this, listdata, group_id);

                        }

                        @Override
                        protected void onCodeError(BaseResult<List<CloudPicData>> t) throws Exception {
                            ToastUtil.toast(t.getMessage());
                        }

                        @Override
                        protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {

                        }
                    });
        } else {
            adapter.setData(listdata);
            adapter.notifyDataSetChanged();
        }
    }

    private void initAnim() {
        bottomToolViewIn = AnimFactory.BottomInAnim(bottom_tool_view);
        bottomToolViewOut = AnimFactory.BottomOutAnim(bottom_tool_view);
        bottomToolViewIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // bottom_tool_view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                recyclerView.setLayoutParams(new RelativeLayout.LayoutParams(mgridviewW, (recyclerView.getHeight() - (mgridviewH))));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        bottomToolViewOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (bottomToolViewIn != null) {
                    bottomToolViewIn.cancel();
                }
                recyclerView.setLayoutParams(new RelativeLayout.LayoutParams(mgridviewW, (recyclerView.getHeight() + (mgridviewH))));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bottom_tool_view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }


    public void showDialog() {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(this).setTitle(this.getResources().getString(R.string.download_info))//设置对话框标题
                    .setMessage(this.getResources().getString(R.string.download_message))//设置显示的内容
                    .setPositiveButton(this.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {//添加确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                            if (type == SMALL_IMAGE) {
                                if (itemClickCount == 0) {
                                    return;
                                }
                                downloadPics();
                            } else {
                                downloadPic(position);
                            }
                        }
                    }).setNegativeButton(this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {//添加返回按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//响应事件
                            alertDialog.dismiss();
                        }
                    }).create();//在按键响应事件中显示此对话框
            //alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        alertDialog.show();
    }

    class AddPic implements Runnable {
        public static final int CAMERA = 1;
        public static final int APPLOCAL = 2;
        Activity activity;
        List<String> list;
        int type;

        public AddPic(Activity activity, List<String> list, int type) {
            this.activity = activity;
            this.list = list;
            this.type = type;
        }

        @Override
        public void run() {
            for (int i = 0; i < list.size(); i++) {
                ImageUtil.EBizImageLoad(list.get(i), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (type == CAMERA) {
                            FileUtil.saveImageInCamera(activity, loadedImage);
                        } else if (type == APPLOCAL) {
                            try {
                                FileUtil.saveMyBitmap(loadedImage, System.currentTimeMillis() + "");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        AddPicReceiver.notifyModifyUsername(activity, "");
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }
        }
    }

    public void initAnim(float x, float y) {


        animShow = AnimFactory.BigImgShow(big_imgview, x, y);
        animShow.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animClose = AnimFactory.BigImgClose(big_imgview, x, y);
        animClose.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                big_imgview.setTranslationX(0);
                big_imgview.setTranslationY(0);
                big_imgview.setLayoutParams(new RelativeLayout.LayoutParams(ContentUtil.getScreenWidth(CloudInfoActivity.this), ContentUtil.getScreenHeight(CloudInfoActivity.this)));
                big_imgview.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setCanClick(boolean canClick) {
        this.isCanClick = canClick;
    }

    private ValueAnimator valueAnimator;

    public void inTopSelect(/*int type*/) {
       /* if (type == SMALL_IMAGE) {
            setCanClick(true);
        }*/
        setCanClick(true);
        this.type = type;
        cloudInfoTopSelectView.setVisibility(VISIBLE);
        startFloatAnim(new AnimFactory.FloatListener() {
            @Override
            public void floatValueChang(float f) {

                titleView.setTranslationY(top_layout.getMeasuredHeight() * -f);
                titleView.setAlpha(1 - f);

                cloudInfoTopSelectView.setTranslationY(-top_layout.getMeasuredHeight() * (1 - f));
                cloudInfoTopSelectView.setAlpha(f);

            }
        });
    }

    public void outTopSelect(/*int type*/) {
      /*  if (type == BIG_IMAGE) {
            big_imgview.setVisibility(View.GONE);
        } else {
            setCanClick(false);
            clearPic();
        }*/
        setCanClick(false);
        clearPic();
        this.type = type;
        cloudInfoTopSelectView.setVisibility(View.GONE);
        startFloatAnim(new AnimFactory.FloatListener() {
            @Override
            public void floatValueChang(float f) {

                titleView.setTranslationY(-top_layout.getMeasuredHeight() * (1 - f));
                titleView.setAlpha(f);

                cloudInfoTopSelectView.setTranslationY(top_layout.getMeasuredHeight() * -f);
                cloudInfoTopSelectView.setAlpha(1 - f);

            }
        });

    }

    public void clearPic() {
        if (listdata == null) {
            return;
        }
        for (CloudPicData item : listdata) {
            item.setClick(false);
        }
        itemClickCount = 0;
        adapter.notifyDataSetChanged();
    }


    public void startFloatAnim(AnimFactory.FloatListener floatListener) {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator = AnimFactory.rotationAnim(floatListener);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }


    public void downloadPics() {
        if (mchoiceDialog == null) {
            mchoiceDialog = ChoiceDialog.createInstance(this);
        }
        mchoiceDialog.show();
        mchoiceDialog.setAblumTitleBlack(CloudInfoActivity.this.getResources().getString(R.string.download));
        String message = getResources().getString(R.string.backstage) + " <font color=\"#D0021B\">" + itemClickCount + "</font> ";

        if (itemClickCount == 1) {
            message += getResources().getString(R.string.your_cloud);
        } else {
            message += getResources().getString(R.string.your_clouds);
        }
        mchoiceDialog.setAblumMessageGray(message);
        mchoiceDialog.setCancel(this.getResources().getString(R.string.cancel));
        mchoiceDialog.setOk(CloudInfoActivity.this.getResources().getString(R.string.download));
        mchoiceDialog.setChoiceCallback(new ChoiceDialog.CardDialogCallback() {
            @Override
            public void onOKClick() {
                dissmissChoiceDialog();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        List<CloudPicData> list = new ArrayList();
                        int count = 0;
                        for (int i = 0; i < listdata.size(); i++) {
                            if (listdata.get(i).isClick()) {
                                list.add(listdata.get(i));
                                count++;
                            }
                        }
                        ToastUtil.toast(getResources().getString(R.string.begin_download));
                        adapter.notifyDataSetChanged();
                        itemClickCount -= count;
//                        outTopSelect(SMALL_IMAGE);
                        clearPic();
                        DownloadPicReceiver.notifyModifyPreset(CloudInfoActivity.this, list);
                    }
                });
            }

            @Override
            public void onCancelClick() {
                dissmissChoiceDialog();
            }

            @Override
            public void onDismiss() {

            }
        });
    }

    public void downloadPic(final int position) {
        if (mchoiceDialog == null) {
            mchoiceDialog = ChoiceDialog.createInstance(this);
        }
        mchoiceDialog.show();
        mchoiceDialog.setAblumTitleBlack(CloudInfoActivity.this.getResources().getString(R.string.download));
        String message = getResources().getString(R.string.backstage_download);
        mchoiceDialog.setAblumMessageGray(message);
        mchoiceDialog.setOk(CloudInfoActivity.this.getResources().getString(R.string.download));
        mchoiceDialog.setChoiceCallback(new ChoiceDialog.CardDialogCallback() {
            @Override
            public void onOKClick() {
                dissmissChoiceDialog();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        List<CloudPicData> list = new ArrayList();
                        list.add(listdata.get(position));
                        ToastUtil.toast(getResources().getString(R.string.begin_download));
                        adapter.notifyDataSetChanged();
                        DownloadPicReceiver.notifyModifyPreset(CloudInfoActivity.this, list);
                    }
                });
            }

            @Override
            public void onCancelClick() {
                dissmissChoiceDialog();
            }

            @Override
            public void onDismiss() {

            }
        });

    }

    public void dissmissChoiceDialog() {
        if (mchoiceDialog != null) {
            mchoiceDialog.dismiss();
        }
    }

    public void showBigPic(int position) {
//        clearPic();
        // vibrator.vibrate(30);
        picBrowsingAdapter.setData(listdata);
        showBigImage(picBrowsingAdapter, position);
    }

    public void showBigImage(final CloudInfoPicBrowsingAdapter adapter, int position) {
        big_imgview.setVisibility(View.VISIBLE);
        big_imgview.setAdapter(adapter);
        big_imgview.setCurrentItem(position);
        big_imgview.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (CloudInfoActivity.this.position != position) {
                    isChangePage = true;
                }
                CloudInfoActivity.this.position = position;
            }

            //此方法是在状态改变的时候调用，其中arg0这个参数有三种状态（0，1，2）。arg0 ==1的时辰默示正在滑动，arg0==2的时辰默示滑动完毕了，arg0==0的时辰默示什么都没做。
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (isChangePage == true) {
                        int childCount = big_imgview.getChildCount();
                        for (int i = 0; i < childCount; i++) {
                            View childAt = big_imgview.getChildAt(i);
                            try {
                                if (childAt != null && childAt instanceof RelativeLayout) {
                                    int c = ((RelativeLayout) childAt).getChildCount();
                                    for (int j = 0; j < c; j++) {
                                        View mview = ((RelativeLayout) childAt).getChildAt(j);
                                        if (mview != null && mview instanceof PhotoView) {
                                            PhotoView matrixImageView = (PhotoView) mview;// 得到viewPager里面的页面
                                            matrixImageView.setDisplayMatrix(new Matrix());
                                        } else if (mview != null && mview instanceof ViewPagerVideoView) {
                                            ViewPagerVideoView viewPagerVideoView = (ViewPagerVideoView) mview;
                                            viewPagerVideoView.onViewPagerVideoViewGone();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        isChangePage = false;
                    }
                }
            }
        });
        if (animShow != null) {
            animShow.start();
        }
        CloudInfoActivity.this.position = position;

    }


    private PopupWindow showMorePopupWindow(final View anchorView) {

        LinearLayout ll_delete;
        LinearLayout ll_download;
        LinearLayout ll_youtu;

        final View contentView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.cloud_more_view_layout, null);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 创建PopupWindow时候指定高宽时showAsDropDown能够自适应
        // 如果设置为wrap_content,showAsDropDown会认为下面空间一直很充足（我以认为这个Google的bug）
        // 备注如果PopupWindow里面有ListView,ScrollView时，一定要动态设置PopupWindow的大小
        final PopupWindow popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, contentView.getMeasuredHeight(), false);

        ll_delete = (LinearLayout) contentView.findViewById(R.id.ll_delete);
        ll_download = contentView.findViewById(R.id.ll_download);
        ll_youtu = contentView.findViewById(R.id.ll_youtu);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 自动调整箭头的位置
                autoAdjustArrowPos(popupWindow, contentView, anchorView);
                contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mchoiceDialog == null) {
                    mchoiceDialog = ChoiceDialog.createInstance(CloudInfoActivity.this);
                }

                mchoiceDialog.setAblumTitle(CloudInfoActivity.this.getResources().getString(R.string.delete));
                String message = CloudInfoActivity.this.getResources().getString(R.string.delete_images_from) + " <font color=\"#D0021B\">" + itemClickCount + "</font>  ";
                if (itemClickCount == 1) {
                    message += CloudInfoActivity.this.getResources().getString(R.string.your_cloud);
                } else {
                    message += CloudInfoActivity.this.getResources().getString(R.string.your_clouds);
                }
                mchoiceDialog.setAblumMessage(message);
                mchoiceDialog.setOk(CloudInfoActivity.this.getResources().getString(R.string.delete));
                mchoiceDialog.setCancel(CloudInfoActivity.this.getResources().getString(R.string.cancel));
                mchoiceDialog.setChoiceCallback(new ChoiceDialog.CardDialogCallback() {
                    @Override
                    public void onOKClick() {
                        dissmissChoiceDialog();
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {

                                String files = "";

                                final List<CloudPicData> checkList = new ArrayList<>();

                                for (int i = 0; null != listdata && i < listdata.size(); i++) {
                                    if (listdata.get(i).isClick()) {
                                        checkList.add(listdata.get(i));
                                    }
                                }


                                for (int i = 0; i < checkList.size(); i++) {
                                    files += checkList.get(i).getCatalogId() + "," + checkList.get(i).getFileId() + ";";
                                }
                                if (!StringUtil.isBlank(files)) {
                                    RetrofitFactory.getInstence()
                                            .API()
                                            .deleteCloud(files)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<BaseResult>() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                }

                                                @Override
                                                public void onNext(final BaseResult baseResult) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ToastUtil.toast(baseResult.getMessage());
                                                        }
                                                    });


                                                    int count = 0;
                                                    Iterator<CloudPicData> iter = listdata.iterator();
                                                    while (iter.hasNext()) {
                                                        CloudPicData item = iter.next();
                                                        for (int i = 0; i < checkList.size(); i++) {
                                                            if (item.getFileId().equals(checkList.get(i).getFileId())) {
                                                                try {
                                                                    iter.remove();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                                count++;
                                                            }
                                                        }
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                    itemClickCount -= count;
                                                    DeleteCloudReceiver.notifyModifyUsername(CloudInfoActivity.this);
                                                    clearPic();
                                                }

                                                @Override
                                                public void onError(final Throwable e) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ToastUtil.toast(e.getMessage());
                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onComplete() {

                                                }
                                            });
                                }
                            }
                        });
                      /*  if (type == SMALL_IMAGE) {
                            if (itemClickCount == 0) {
                                return;
                            }
                            showDeleteImagesDialog();
                        } else {
                            showDeleteImageDialog(position);
                        }*/
                    }

                    @Override
                    public void onCancelClick() {
                        dissmissChoiceDialog();
                    }

                    @Override
                    public void onDismiss() {

                    }
                });
                popupWindow.dismiss();
                mchoiceDialog.show();
            }
        });

        ll_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (isWifiMode) {
                    if (type == SMALL_IMAGE) {
                        if (itemClickCount == 0) {
                            return;
                        }
                        downloadPics();
                    } else {
                        downloadPic(position);
                    }
                } else {
                    showDialog();
                }
            }
        });

        ll_youtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list = new ArrayList<>();

                for (int i = 0; null != listdata && i < listdata.size(); i++) {
                    if (listdata.get(i).isClick()) {
                        list.add(listdata.get(i).getFileId());
                    }
                }
                String[] array = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    array[i] = (String) list.get(i);
                }
                ModifyScope modifyScope = new ModifyScope();
                modifyScope.setIds(array);
                modifyScope.setScope("2");
                RetrofitFactory.getInstence().API().bizCamUToUpload(modifyScope)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseObserver<Object>(CloudInfoActivity.this, true) {
                            @Override
                            protected void onSuccees(BaseResult<Object> t) throws Exception {
                                clearPic();
                            }

                            @Override
                            protected void onCodeError(final BaseResult<Object> t) throws Exception {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.toast(t.getMessage());
                                    }
                                });

                            }
                                @Override
                                protected void onTokenError (BaseResult < Object > t) throws
                                Exception {

                                }

                                @Override
                                protected void onFailure (Throwable e,boolean isNetWorkError) throws
                                Exception {

                                }
                            });
                        }
            });


        popupWindow.setBackgroundDrawable(new

            ColorDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(false);
        popupWindow.setTouchInterceptor(new View.OnTouchListener()

            {
                @Override
                public boolean onTouch (View v, MotionEvent event){
                return false;   // 这里面拦截不到返回键
            }
            });
        popupWindow.showAsDropDown(anchorView);
        return popupWindow;
        }

        private void autoAdjustArrowPos (PopupWindow popupWindow, View contentView, View anchorView)
        {
//        View upArrow = contentView.findViewById(R.id.up_arrow);
            View downArrow = contentView.findViewById(R.id.down_arrow);

            int pos[] = new int[2];
            contentView.getLocationOnScreen(pos);
            int popLeftPos = pos[0];
            anchorView.getLocationOnScreen(pos);
            int anchorLeftPos = pos[0];
            int arrowLeftMargin = anchorLeftPos - popLeftPos + anchorView.getWidth() / 2 - downArrow.getWidth() / 2;
            RelativeLayout.LayoutParams downArrowParams = (RelativeLayout.LayoutParams) downArrow.getLayoutParams();
            downArrowParams.leftMargin = arrowLeftMargin;
            downArrow.setLayoutParams(downArrowParams);
        }

        public void showOtherPopupWindow (View view){
            if (itemClickCount == 0) {
                if (null == dgProgressDialog2) {
                    dgProgressDialog2 = new ProgressDialog(this, new ProgressDialog.DGProgressListener() {
                        @Override
                        public void onSuccessAnimed() {

                        }

                        @Override
                        public void onFailAnimed() {

                        }

                        @Override
                        public void onAnim() {

                        }
                    }, true);
                    dgProgressDialog2.setType(ProgressDialog.TYPE_ABLUM_SELECT);
                    dgProgressDialog2.setText(this.getResources().getString(R.string.album_hint),
                            ProgressDialog.TYPE_ABLUM_SELECT);
                    dgProgressDialog2.show();
                } else {
                    dgProgressDialog2.show();
                }
                return;

            }

            if (null == morePopupWindow) {
                morePopupWindow = showMorePopupWindow(view);
            } else {
                morePopupWindow.showAsDropDown(view);
            }
        }


    }
