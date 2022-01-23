package com.bcnetech.hyphoto.ui.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.listener.OnLayoutInflatedListener;
import com.app.hubert.guide.listener.OnPageChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetchhttp.config.UrlConstants;
import com.bcnetech.bcnetechlibrary.blurkit.BlurCallable;
import com.bcnetech.bcnetechlibrary.blurkit.BlurUtil;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.EventStatisticsUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.bcnetechlibrary.util.TimeUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.util.sharepreference.SharePreferences;
import com.bcnetech.hyphoto.EventCommon;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.GridItem;
import com.bcnetech.hyphoto.data.SDCardMedia;
import com.bcnetech.hyphoto.data.UploadBean;
import com.bcnetech.hyphoto.databinding.MainAblumNewLayoutBinding;
import com.bcnetech.hyphoto.model.BleConnectModel;
import com.bcnetech.hyphoto.model.ImageImportModel;
import com.bcnetech.hyphoto.presenter.AblumNewPresenter;
import com.bcnetech.hyphoto.presenter.iview.IAblumNewView;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.task.manage.UploadManager;
import com.bcnetech.hyphoto.ui.activity.camera.AI360CameraActivity;
import com.bcnetech.hyphoto.ui.activity.camera.AICameraActivity;
import com.bcnetech.hyphoto.ui.activity.camera.CameraSettingActivity;
import com.bcnetech.hyphoto.ui.activity.cloud.CloudInfoActivity;
import com.bcnetech.hyphoto.ui.activity.market.MarketFragmentActivity;
import com.bcnetech.hyphoto.ui.activity.personCenter.SettingDetailActivity;
import com.bcnetech.hyphoto.ui.activity.personCenter.UserCenterActivity;
import com.bcnetech.hyphoto.ui.adapter.PicBrowsingAdapter;
import com.bcnetech.hyphoto.ui.adapter.StickyGridNewAdapter;
import com.bcnetech.hyphoto.ui.view.BlueToothListNewView;
import com.bcnetech.hyphoto.ui.view.BottomToolView;
import com.bcnetech.hyphoto.ui.view.CoBoxChooseTopView;
import com.bcnetech.hyphoto.ui.view.DrawerView;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.ui.view.photoview.PhotoView;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.PermissionUtil;
import com.bcnetech.hyphoto.utils.SPUtils;
import com.bcnetech.hyphoto.utils.cache.CacheManager;
import com.bcnetech.bluetoothlibarary.bluetoothagreement.CommendManage;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static android.view.View.VISIBLE;
import static android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM;
import static android.widget.RelativeLayout.ALIGN_PARENT_LEFT;
import static android.widget.RelativeLayout.ALIGN_PARENT_RIGHT;
import static android.widget.RelativeLayout.ALIGN_PARENT_TOP;
import static android.widget.RelativeLayout.CENTER_HORIZONTAL;
import static com.bcnetech.hyphoto.ui.view.BottomToolView.ABLUM;
import static com.bcnetech.hyphoto.ui.view.BottomToolView.PHOTO_EDIT;

/**
 * Created by wenbin on 2017/4/14.
 */

public class AlbumNewActivity extends BaseMvpActivity<IAblumNewView, AblumNewPresenter> implements IAblumNewView, DrawerView.DrawerLister/*, Panel.OnPanelListener*/ {
    MainAblumNewLayoutBinding mainAblumNewLayoutBinding;
    private ValueAnimator animShow, animClose;
    private int mBackKeyPressedTimes = 0;
    public int position;
    private boolean canClick = true;
    private LayoutInflater inflate;
    public final static int WHITE = 0;
    public final static int BLACK = 1;
    private int type = WHITE;
    private String path;
    private boolean isPanelClick = true;
    private Queue SDCardMediaQueue;
    private ImageImportModel imageSelectorModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        onViewClick();
        if (getcFolder() == null) {
            redDeviceList();
        }
        PermissionUtil.checkAllPermissionWelcome(AlbumNewActivity.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainAblumNewLayoutBinding.viewPagerVideoView.getVisibility() == VISIBLE) {
            mainAblumNewLayoutBinding.viewPagerVideoView.showVideoView();
            //mainAblumNewLayoutBinding.viewPagerVideoView.onResume();
        }
        isPanelClick = true;
        mainAblumNewLayoutBinding.drawerview.closeDrawer(false);
        mainAblumNewLayoutBinding.up.setVisibility(VISIBLE);
        mainAblumNewLayoutBinding.down.setVisibility(View.INVISIBLE);
        if (!BleConnectModel.getBleConnectModelInstance().isCurrentConnect()) {
            mainAblumNewLayoutBinding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CONNECTION_ERROR);
        }
        if (mainAblumNewLayoutBinding.blueToothListView.getVisibility() == VISIBLE) {
            mainAblumNewLayoutBinding.blueToothListView.close();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mainAblumNewLayoutBinding.viewPagerVideoView.getVisibility() == VISIBLE) {
            mainAblumNewLayoutBinding.viewPagerVideoView.onPause();
            mainAblumNewLayoutBinding.viewPagerVideoView.onViewPagerVideoViewGone();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

        presenter.onActivityResult(requestCode, resultCode, data);
        if (requestCode == resultCode && resultCode == Flag.MATTING_PARMS_ACTIVITY) {
            mainAblumNewLayoutBinding.bigImgview.setCurrentItem(0, false);
        }
        if (requestCode == resultCode && resultCode == Flag.IMAGESELECTOR_ACTIVITY) {

            List<SDCardMedia> selectSDCardMedias = (List<SDCardMedia>) CacheManager.readObject(this,"ImageSelect");
                if (selectSDCardMedias != null) {
                    SDCardMediaQueue = new LinkedList<>();
                    for (SDCardMedia SDCardMedia : selectSDCardMedias) {
                        SDCardMediaQueue.add(SDCardMedia);
                    }
                    imageSelectorModel = ImageImportModel.getImageSelectInstance();
                    imageSelectorModel.setContext(AlbumNewActivity.this);
                    imageSelectorModel.setImageSelectCallBack(new ImageImportModel.ImageSelectCallBack() {
                        @Override
                        public void onImageImporting(int restCount) {
                        }

                        @Override
                        public void onImageImportOver() {
                            imageSelectorModel = null;
                            initGuide();
                        }
                    });
                    imageSelectorModel.setImageDataSqlControl(presenter.getImageDataSqlControl());
                    imageSelectorModel.startImport(SDCardMediaQueue);
                }
            }
    }

    @Override
    protected void initView() {
        mainAblumNewLayoutBinding = DataBindingUtil.setContentView(this, R.layout.main_ablum_new_layout);

    }





    @Override
    protected void initData() {
        if (UrlConstants.isTestType) {
            mainAblumNewLayoutBinding.name.setText("Test");
        }

        mainAblumNewLayoutBinding.bigImgview.setOffscreenPageLimit(2);//viewpager设置缓存页数
        mainAblumNewLayoutBinding.include.titleLayout.setType(TitleView.ALBUM_NEW);
        mainAblumNewLayoutBinding.include.titleLayout.bringToFront();
        mainAblumNewLayoutBinding.include.gridView.setAreHeadersSticky(false);
        StickyGridNewAdapter stickyGridAdapter = presenter.initAdapter(mainAblumNewLayoutBinding.include.gridView);
        mainAblumNewLayoutBinding.include.gridView.setAdapter(stickyGridAdapter);
        mainAblumNewLayoutBinding.include.gridView.setSelection(presenter.getmFirstVisible());
        mainAblumNewLayoutBinding.include.titleLayout.setLeftType(presenter.getCurrentlayoutType());
        mainAblumNewLayoutBinding.include.bottomToolView.setType(ABLUM);
        mainAblumNewLayoutBinding.include.bottomToolView.setVisibility(View.GONE);
        mainAblumNewLayoutBinding.drawerview.setHandlerHeight(ContentUtil.dip2px(AlbumNewActivity.this, 72));

        presenter.initBigImageAdapter(new PicBrowsingAdapter.CloseInter() {
            @Override
            public void close() {
                if (animClose != null && canClick) {
                    animClose.start();
                }
            }

            @Override
            public void startDrag() {
                mainAblumNewLayoutBinding.bigImgview.startDrag();
            }

            @Override
            public void stopDrag() {
                mainAblumNewLayoutBinding.bigImgview.stopDrag();
            }

            @Override
            public void switchType(int type) {
                if (type == PicBrowsingAdapter.WHITE) {
                    //上下划入
                    mainAblumNewLayoutBinding.imageUtilsView.in();
                } else {
                    //上下滑出
                    mainAblumNewLayoutBinding.imageUtilsView.out();
                    // setVirtureBar(false);
                }

                int childCount = mainAblumNewLayoutBinding.bigImgview.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = mainAblumNewLayoutBinding.bigImgview.getChildAt(i);
                    try {
                        if (childAt != null && childAt instanceof RelativeLayout) {
                            RelativeLayout rl_main = (RelativeLayout) childAt;
                            if (type == PicBrowsingAdapter.WHITE) {
                                rl_main.setBackgroundColor(getResources().getColor(R.color.white));
                            } else if (type == PicBrowsingAdapter.BLACK) {
                                rl_main.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                            int c = ((RelativeLayout) childAt).getChildCount();
                            for (int j = 0; j < c; j++) {
                                View mview = ((RelativeLayout) childAt).getChildAt(j);
                                if (mview != null && mview instanceof PhotoView) {
                                    PhotoView matrixImageView = (PhotoView) mview;// 得到viewPager里面的页面
                                    if (type == PicBrowsingAdapter.WHITE) {
                                        String url = (String) matrixImageView.getTag();
                                        if (url.contains(".png")) {
                                            matrixImageView.setBackground(getResources().getDrawable(R.drawable.bgbitmap));
                                        } else {
                                            matrixImageView.setBackgroundColor(getResources().getColor(R.color.white));
                                        }
//                                        matrixImageView.setBackgroundColor(getResources().getColor(R.color.white));
                                    } else if (type == PicBrowsingAdapter.BLACK) {
                                        matrixImageView.setBackgroundColor(getResources().getColor(R.color.black));
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        /*if (isToken == true) {
            LoginPresenter.startAction(this, Flag.TYPE_LOGIN);
            CostomToastUtil.toast(this.getResources().getString(R.string.token_fail));
            presenter.deleteAllUpload();
        }*/

        // mainAblumNewLayoutBinding.panel.setOnPanelListener(this);
        mainAblumNewLayoutBinding.drawerview.setDrawerLister(this);

        if (Flag.isEnglish) {
            mainAblumNewLayoutBinding.newShop.setVisibility(View.INVISIBLE);
        } else {
            mainAblumNewLayoutBinding.newShop.setVisibility(VISIBLE);
        }

        mainAblumNewLayoutBinding.newCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPanelClick)
                    if(PermissionUtil.checkAllPermissionWelcome(AlbumNewActivity.this)) {
                        openCamer();
                    }
            }
        });
        mainAblumNewLayoutBinding.newShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlbumNewActivity.this, GoodsMarketActivity.class);
                startActivity(intent);
            }
        });

        mainAblumNewLayoutBinding.panelHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainAblumNewLayoutBinding.drawerview.changeStatus();
            }
        });
        // sViewPager.setCanScroll(true);
       /* indicatorViewPager = new IndicatorViewPager(indicator, sViewPager);
        indicatorViewPager.setPageOffscreenLimit(0);*/
        inflate = LayoutInflater.from(this);
        inflate.inflate(R.layout.main_guide2, mainAblumNewLayoutBinding.sviewPager, true);

        LinearLayout me = (LinearLayout) mainAblumNewLayoutBinding.sviewPager.findViewById(R.id.me);

        LinearLayout camera = (LinearLayout) mainAblumNewLayoutBinding.sviewPager.findViewById(R.id.camera);

        // LinearLayout settings = (LinearLayout) mainAblumNewLayoutBinding.sviewPager.findViewById(R.id.settings);

        LinearLayout newupload = (LinearLayout) mainAblumNewLayoutBinding.sviewPager.findViewById(R.id.newupload);

        LinearLayout ls_market = (LinearLayout) mainAblumNewLayoutBinding.sviewPager.findViewById(R.id.ls_market);

        LinearLayout aicamera = (LinearLayout) mainAblumNewLayoutBinding.sviewPager.findViewById(R.id.aicamera);

        LinearLayout aicamera360 = (LinearLayout) mainAblumNewLayoutBinding.sviewPager.findViewById(R.id.aicamera360);
        aicamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PermissionUtil.checkAllPermissionWelcome(AlbumNewActivity.this)) {
                    showBlueToothView(false);
                }
            }
        });

        aicamera360.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PermissionUtil.checkAllPermissionWelcome(AlbumNewActivity.this)) {
                    showBlueToothView(true);
                }
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPanelClick)
                    if(PermissionUtil.checkAllPermissionWelcome(AlbumNewActivity.this)) {
                        openCamer();
                    }

            }
        });

        newupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opennewupload();
            }
        });

        ls_market.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLsMarket();
            }
        });

      /*  settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });*/
        me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PermissionUtil.checkAllPermissionWelcome(AlbumNewActivity.this)) {
                    openUserSettings();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBlueToothStatus(final UploadBean bean) {

    }

    /**
     * 提示蓝牙连接view
     *
     * @param isAI360
     */
    private void showBlueToothView(final boolean isAI360) {
        if (!presenter.checkGPSIsOpen()) {
            ToastUtil.toast(getResources().getString(R.string.request_gps));
            return;
        }
        if (!presenter.getBleConnectModel().isBlueEnable()) {
            mainAblumNewLayoutBinding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CLOSE);
        } else {
            mainAblumNewLayoutBinding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_OPEN);
            if (presenter.getBleConnectModel() != null && !presenter.getBleConnectModel().isCurrentConnect()) {
                mainAblumNewLayoutBinding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CONNECTION_ERROR);
            }
        }

        presenter.initBleConnect(isAI360);
        mainAblumNewLayoutBinding.blueToothListView.initParm(presenter.getSurfBlueToothPopAdapter(), new BlueToothListNewView.BlueToothListInterface() {
            @Override
            public void onBlueToothDissmiss(Object... params) {
                presenter.getBleConnectModel().stopSearch();
                if (presenter.getBleConnectModel().isCurrentConnect()) {
                    initGuide();
                }
            }

            @Override
            public void onBlueToothClick(Object... params) {
                presenter.getBleConnectModel().choiceDeivce((int) params[0]);
            }

            @Override
            public void onListConnection(Object... params) {
                int i = 0;
            }

            @Override
            public void onScanConnection(Object... params) {
                String result = (String) params[0];
                ToastUtil.toast(result);
                if (result.contains(CommendManage.COBOX_NAME) || result.contains(CommendManage.CBEDU_NAME) || result.contains(CommendManage.COLINK_NAME)) {
                    boolean scanResult=presenter.getBleConnectModel().scanDevice(result);

                    if(!scanResult){
                        ToastUtil.toast(getString(R.string.scan_is_connection));
                    }
                }
            }
        });

        if (presenter.getBleConnectModel().isCurrentConnect()) {
            if (isAI360) {
                openAI360Camera();
            } else {
                openAICamera();
            }
        } else {
            mainAblumNewLayoutBinding.drawerview.closeDrawer(false);
            mainAblumNewLayoutBinding.blueToothListView.setVisibility(VISIBLE);
            mainAblumNewLayoutBinding.blueToothListView.setApplyBlur();
            mainAblumNewLayoutBinding.blueToothListView.bringToFront();
            if (PermissionUtil.getFindLocationPermmission(AlbumNewActivity.this)) {
                mainAblumNewLayoutBinding.blueToothListView.setVisibility(VISIBLE);
            } else {
                ToastUtil.toast(AlbumNewActivity.this.getResources().getString(R.string.please_location_permissions));
            }

            mainAblumNewLayoutBinding.blueToothListView.setCloseConnecrtion(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainAblumNewLayoutBinding.blueToothListView.setBlueTouchType(BlueToothListNewView.BLUE_TOUCH_CONNECTION_ERROR);
                    presenter.getBleConnectModel().disConnectCurrent();

//                    presenter.getBleConnectModel().choiceDeivce(presenter.getBleConnectModel().getSelectPosition());
                }
            });
        }
    }


    public void opennewupload() {
        if (isPanelClick) {
            SharePreferences preferences = SharePreferences.instance();
            boolean isFirstIn = preferences.getBoolean("isFirstIn", true);
            if (isFirstIn) {
                EventStatisticsUtil.event(AlbumNewActivity.this, EventCommon.ABLUM_LEFT_CLOUD_UNLOGIN);

                // LoginPresenter.startAction(AlbumNewActivity.this, Flag.TYPE_LOGIN);
            } else {
                EventStatisticsUtil.event(AlbumNewActivity.this, EventCommon.ABLUM_LEFT_CLOUD_LOGIN);


                CloudInfoActivity.actionStart(AlbumNewActivity.this, CloudInfoActivity.TYPE_CLOUD);
//            CloundActivity.startAction(AlbumNewActivity.this, CloundActivity.CLOUD, Flag.NULLCODE);
            }
        }
        // mablum.closeDrawer(ablum_left_content);
    }

    public void openLsMarket() {
        if (isPanelClick) {
            EventStatisticsUtil.event(AlbumNewActivity.this, EventCommon.ABLUM_LEFT_PRESET_MARKET);
            startActivity(new Intent(AlbumNewActivity.this, MarketFragmentActivity.class));
        }
        // MarketPresetActicity.actionStart(AlbumActivity.this);

        //mablum.closeDrawer(ablum_left_content);
    }

    public void openSettings() {
        if (isPanelClick)
            startActivity(new Intent(AlbumNewActivity.this, CameraSettingActivity.class));
    }

    public void openUserSettings() {
        if (isPanelClick)
            startActivity(new Intent(AlbumNewActivity.this, UserCenterActivity.class));
    }

    public void openAICamera() {
        if (isPanelClick) {
            Intent intent = new Intent(AlbumNewActivity.this, AICameraActivity.class);
            startActivity(intent);
        }
    }

    private void openAI360Camera() {
        if (isPanelClick) {
            Intent intent = new Intent(AlbumNewActivity.this, AI360CameraActivity.class);
            startActivity(intent);
        }
    }

    public void showBackground(List<GridItem> mGirdList) {
        if (mGirdList != null) {
            if (mGirdList.size() > 0 && mGirdList.size() != 1) {
                mainAblumNewLayoutBinding.bgBlurLine.setVisibility(View.GONE);
            } else {
                LoginedUser loginedUser = LoginedUser.getLoginedUser();
                loginedUser.setUploadStatus(Flag.UPLOAD_NONE);
                LoginedUser.setLoginedUser(loginedUser);
                mainAblumNewLayoutBinding.bgBlurLine.setVisibility(VISIBLE);
            }
        }
    }


    @Override
    protected void onViewClick() {
        presenter.onViewClick(/*mainAblumNewLayoutBinding.panel*/);

        mainAblumNewLayoutBinding.include.titleLayout.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PermissionUtil.checkAllPermissionWelcome(AlbumNewActivity.this)) {
                    EventStatisticsUtil.event(AlbumNewActivity.this, EventCommon.PERSONCENTER_SETTING);
                    Intent intent = new Intent(AlbumNewActivity.this, ImageSelectorActivity.class);
                    intent.putExtra(Flag.MAX_SELECT_COUNT, 0);
                    intent.putExtra(Flag.IS_SINGLE, false);
                    // startActivity(intent);
                    startActivityForResult(intent, Flag.IMAGESELECTOR_ACTIVITY);
                }

             /*   Intent intent = new Intent(AlbumNewActivity.this, SettingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                AlbumNewActivity.this.startActivity(intent);*/
                //ImageSelectorActivity.openActivity(AlbumNewActivity.this, false, 0);
            }
        });


        /**
         * 进入多选
         */
        mainAblumNewLayoutBinding.include.titleLayout.setRightTextListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inTopSelect();
                mainAblumNewLayoutBinding.drawerview.setVisibility(View.INVISIBLE);
            }
        });

        /**
         * 退出多选
         */
        mainAblumNewLayoutBinding.include.albumNewTopSelectView.close(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outTopSelect();
            }
        });

        /**
         * 分享
         */
        mainAblumNewLayoutBinding.include.albumNewTopSelectView.shareMore(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.showSharePopupWindow(v);
            }
        });

        /**
         * 更多
         */
        mainAblumNewLayoutBinding.include.albumNewTopSelectView.otherClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.showOtherPopupWindow(v);
            }
        });


        mainAblumNewLayoutBinding.photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == WHITE) {
                    type = BLACK;
                    if (!path.contains(".png")) {
                        mainAblumNewLayoutBinding.photoView.setBackgroundColor(getResources().getColor(R.color.black));
                    }
                } else if (type == BLACK) {
                    type = WHITE;
                    if (!path.contains(".png")) {
                        mainAblumNewLayoutBinding.photoView.setBackgroundColor(getResources().getColor(R.color.backgroud_new));
                    }
//                        matrixImageView.setBackgroundColor(context.getResources().getColor(R.color.white));
                }
                if (type == PicBrowsingAdapter.WHITE) {
                    //上下划入
                    mainAblumNewLayoutBinding.imageUtilsView.in();
                } else {
                    //上下滑出
                    mainAblumNewLayoutBinding.imageUtilsView.out();
                    // setVirtureBar(false);
                }
            }
        });
        mainAblumNewLayoutBinding.blurImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainAblumNewLayoutBinding.drawerview.isDrawerOpen())
                    mainAblumNewLayoutBinding.drawerview.closeDrawer(true);
            }
        });

//        photoView.setOneClickListener(new ScaleImageView.OneClickListener() {
//            @Override
//            public void clickListener() {
//
//            }
//        });
    }


    /*   public void setVisAblumLeft() {
           if (!mablum.isDrawerOpen(ablum_left_content)) {
               mablum.openDrawer(ablum_left_content);
           }
       }*/
    public void openCamer() {
        EventStatisticsUtil.event(AlbumNewActivity.this, EventCommon.ABLUM_RIGHT);

        if (PermissionUtil.isMIUI() && !PermissionUtil.checkAppops(AlbumNewActivity.this, AppOpsManager.OPSTR_FINE_LOCATION)) {
                    /*CostomToastUtil.toast("请手动开启定位权限");
                    PermissionUtil.startAppSettings(AlbumActivity.this);*/
            PermissionUtil.forMIUI(AlbumNewActivity.this);
            return;
        }
        presenter.getCameraPer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public AblumNewPresenter initPresenter() {
        return new AblumNewPresenter();
    }


    @Override
    public void showLoading() {
        showDialog();
    }

    @Override
    public void hideLoading() {
        dissmissDialog();
    }

    @Override
    public void finishView(int resultCode, Intent intent) {

    }


    @Override
    public void setGridViewFirstVisItem(int firstVisItem) {
        mainAblumNewLayoutBinding.include.gridView.setSelection(firstVisItem);
        mainAblumNewLayoutBinding.include.gridView.setNotifyDataSetChanged();
    }

    @Override
    public void initAnim(float x, float y) {
        if (animShow == null) {
            animShow = AnimFactory.BigImgShow(mainAblumNewLayoutBinding.rlBigImage, x, y);
            animShow.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    // mablum.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); //关闭手势滑动
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mainAblumNewLayoutBinding.drawerview.setVisibility(View.INVISIBLE);
                    // mainAblumNewLayoutBinding.panel.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animClose = AnimFactory.BigImgClose(mainAblumNewLayoutBinding.rlBigImage, x, y);
            animClose.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mainAblumNewLayoutBinding.viewPagerVideoView.onViewPagerVideoViewGone();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mainAblumNewLayoutBinding.drawerview.setVisibility(View.VISIBLE);
                    //  mainAblumNewLayoutBinding.panel.setVisibility(View.VISIBLE);
                    if (mainAblumNewLayoutBinding.viewPagerFake3dview.getVisibility() == VISIBLE) {
                        mainAblumNewLayoutBinding.viewPagerFake3dview.setVisibility(View.GONE);
                    }
                    mainAblumNewLayoutBinding.rlBigImage.setTranslationX(0);
                    mainAblumNewLayoutBinding.rlBigImage.setTranslationY(0);
                    mainAblumNewLayoutBinding.rlBigImage.setLayoutParams(new RelativeLayout.LayoutParams(ContentUtil.getScreenWidth(AlbumNewActivity.this), ContentUtil.getScreenHeight(AlbumNewActivity.this)));
                    mainAblumNewLayoutBinding.rlBigImage.setVisibility(View.GONE);

                    mainAblumNewLayoutBinding.imageUtilsView.setVisibility(View.GONE);
                    mainAblumNewLayoutBinding.imageUtilsView.onViewClosed();

                    // mablum.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED); //打开手势滑动
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    @Override
    public void inFooterSetting(int type) {
        switch (type) {
            case PHOTO_EDIT:
                mainAblumNewLayoutBinding.include.bottomToolView.setType(PHOTO_EDIT);
                mainAblumNewLayoutBinding.include.bottomToolView.invalidate();
                break;
            case BottomToolView.ABLUM:
                mainAblumNewLayoutBinding.include.bottomToolView.setType(ABLUM);
                mainAblumNewLayoutBinding.include.bottomToolView.invalidate();
                break;

        }
     /*   bottomToolViewIn.start();
        ablumLeftImgOut.start();
        ablumRightImgOut.start();*/

    }

    @Override
    public void outFooterSetting() {
       /* bottomToolViewOut.start();

        ablumLeftImgIn.start();
        ablumRightImgIn.start();*/
    }


    @Override
    public void showBigImage(int position, GridItem gridItem) {

        mainAblumNewLayoutBinding.rlBigImage.setVisibility(VISIBLE);

        refreshBigImage(gridItem, position, "new");

        if (animShow != null) {
            animShow.start();
        }


        mainAblumNewLayoutBinding.imageUtilsView.setVisibility(View.VISIBLE);
        mainAblumNewLayoutBinding.imageUtilsView.bringToFront();

        if (gridItem.getImageData().getType() == Flag.TYPE_VIDEO) {

            NewbieGuide.with(this)
                    .setLabel("pagePhotoEditVideo")//设置引导层标示区分不同引导层，必传！否则报错
                    .alwaysShow(false)//是否每次都显示引导层，默认false，只显示一次
                    .addGuidePage(GuidePage.newInstance()
                            .setLayoutRes(R.layout.guide_photoedit_select)
                            .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                @Override
                                public void onLayoutInflated(View view, final Controller controller) {
                                    ImageView photoedit_info = (ImageView) view.findViewById(R.id.photoedit_info);
                                    photoedit_info.setVisibility(View.VISIBLE);
                                }
                            })
                    )
                    .show();//显示引导层(至少需要一页引导页才能显示)
        } else if (gridItem.getImageData().getType() == Flag.TYPE_PIC) {
            View panelContent_image = findViewById(R.id.panelContent_image);

            NewbieGuide.with(this)
                    .setLabel("pagePhotoEditImage")//设置引导层标示区分不同引导层，必传！否则报错
                    .alwaysShow(false)//是否每次都显示引导层，默认false，只显示一次
                    .addGuidePage(//添加一页引导页
                            GuidePage.newInstance()//创建一个实例
                                    .setLayoutRes(R.layout.guide_photoedit_select)//设置引导页布局
                                    .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {

                                        @Override
                                        public void onLayoutInflated(View view, Controller controller) {
                                            ImageView photoedit_zoom = (ImageView) view.findViewById(R.id.photoedit_zoom);
                                            photoedit_zoom.setVisibility(View.VISIBLE);
                                            //引导页布局填充后回调，用于初始化
                                        }
                                    })
                                    .setEnterAnimation(enterAnimation)//进入动画
                                    .setExitAnimation(exitAnimation)//退出动画
                    )
                    .addGuidePage(GuidePage.newInstance()
                            .addHighLight(panelContent_image)
                            .setLayoutRes(R.layout.guide_photoedit_select)//引导页布局，点击跳转下一页或者消失引导层的控件id
                            .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                @Override
                                public void onLayoutInflated(View view, final Controller controller) {
                                    ImageView photoedit_choice = (ImageView) view.findViewById(R.id.photoedit_choice);
                                    photoedit_choice.setVisibility(View.VISIBLE);
                                }
                            })

                            .setBackgroundColor(getResources().getColor(R.color.translucent))//设置背景色，建议使用有透明度的颜色
                            .setEnterAnimation(enterAnimation)//进入动画
                            .setExitAnimation(exitAnimation)//退出动画

                    )
                    .addGuidePage(GuidePage.newInstance()
                            .setLayoutRes(R.layout.guide_photoedit_select)
                            .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                @Override
                                public void onLayoutInflated(View view, final Controller controller) {
                                    ImageView photoedit_info = (ImageView) view.findViewById(R.id.photoedit_info);
                                    photoedit_info.setVisibility(View.VISIBLE);
                                }
                            })
                    )
                    .show();//显示引导层(至少需要一页引导页才能显示)
        }
    }

    @Override
    public void refreshBigImage(final GridItem gridItem, int position, String type) {

        if (gridItem.getImageData().getType() == Flag.TYPE_VIDEO) {

            mainAblumNewLayoutBinding.viewPagerVideoView.setVisibility(View.VISIBLE);
            mainAblumNewLayoutBinding.viewPagerVideoView.showVideoView();
            mainAblumNewLayoutBinding.viewPagerFake3dview.setVisibility(View.GONE);
            mainAblumNewLayoutBinding.photoView.setVisibility(View.GONE);
            mainAblumNewLayoutBinding.viewPagerVideoView.setImageData(gridItem.getImageData());
        } else if (gridItem.getImageData().getType() == Flag.TYPE_AI360) {
            mainAblumNewLayoutBinding.viewPagerFake3dview.setVisibility(View.VISIBLE);
            mainAblumNewLayoutBinding.viewPagerVideoView.setVisibility(View.GONE);
            mainAblumNewLayoutBinding.photoView.setVisibility(View.GONE);

            mainAblumNewLayoutBinding.viewPagerFake3dview.setFolderUrl(gridItem.getImageData().getLocalUrl());
        } else {
            mainAblumNewLayoutBinding.photoView.setBackgroundColor(Color.WHITE);
            path = gridItem.getPath();
            if (gridItem.getPath().contains(".png")) {
                mainAblumNewLayoutBinding.photoView.setBackground(getResources().getDrawable(R.drawable.bgbitmap));
            } else {
                mainAblumNewLayoutBinding.photoView.setBackgroundColor(getResources().getColor(R.color.backgroud_new));
            }
            mainAblumNewLayoutBinding.photoView.setWillNotDraw(false);
            mainAblumNewLayoutBinding.viewPagerFake3dview.setVisibility(View.GONE);
            mainAblumNewLayoutBinding.viewPagerVideoView.setVisibility(View.GONE);
            mainAblumNewLayoutBinding.photoView.setVisibility(View.VISIBLE);
            BitmapUtils.BitmapSize bitmapSize = BitmapUtils.getBitmapSize(gridItem.getPath().substring(7));
            int min = Math.min(bitmapSize.width, bitmapSize.height);
            int orientation=BitmapUtils.getOrientation(gridItem.getPath().substring(7));

            if (min > BitmapUtils.MAXLENGTH) {

                float width ;
                float height;
                if(orientation==90||orientation==270){
                    width = bitmapSize.height;
                    height = bitmapSize.width;

                    if (width > height) {
                        width = BitmapUtils.MAXLENGTH;
                        height = BitmapUtils.MAXLENGTH * bitmapSize.width / bitmapSize.height;
                    } else {
                        height = BitmapUtils.MAXLENGTH;
                        width = BitmapUtils.MAXLENGTH * bitmapSize.height / bitmapSize.width;
                    }

                }else {
                    width = bitmapSize.width;
                    height = bitmapSize.height;

                    if (width > height) {
                        width = BitmapUtils.MAXLENGTH;
                        height = BitmapUtils.MAXLENGTH * bitmapSize.height / bitmapSize.width;
                    } else {
                        height = BitmapUtils.MAXLENGTH;
                        width = BitmapUtils.MAXLENGTH * bitmapSize.width / bitmapSize.height;
                    }
                }
                Picasso.get().load(gridItem.getPath()).resize((int) width, (int) height).into(mainAblumNewLayoutBinding.photoView);
            } else {
                Picasso.get().load(gridItem.getPath()).into(mainAblumNewLayoutBinding.photoView);
            }



            /*Picasso.get().load(gridItem.getPath()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                   int min = Math.min(bitmap.getWidth(),bitmap.getHeight());
                    if (min>BitmapUtils.MAXLENGTH){
                        Bitmap scalebitmap = BitmapUtils.rescaleBitmap(bitmap);
                       mainAblumNewLayoutBinding.photoView.setImageBitmap(scalebitmap);
                    }else{
                        mainAblumNewLayoutBinding.photoView.setImageBitmap(bitmap);
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    Picasso.get().load(gridItem.getPath()).into(mainAblumNewLayoutBinding.photoView);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });*/
            //Picasso.get().load(gridItem.getPath()).into(mainAblumNewLayoutBinding.photoView);
        }

        mainAblumNewLayoutBinding.imageUtilsView.setImageData(gridItem.getImageData(),type);


        AlbumNewActivity.this.position = position;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setFooterType(int type) {
        mainAblumNewLayoutBinding.include.bottomToolView.setDoubleSelect(type);
    }

    public ImageData getResultData() {
        return mainAblumNewLayoutBinding.imageUtilsView.getResultImageData();
    }

    @Override
    public void onBackPressed() {
        //点击放大图片时按返回键返回到相册界面
        if (null != mainAblumNewLayoutBinding.bigImgview && mainAblumNewLayoutBinding.bigImgview.getVisibility() == View.VISIBLE) {
            if (mainAblumNewLayoutBinding.imageUtilsView.isNowtypeStart()) {
                if (animClose != null && canClick) {
                    animClose.start();
                }
            }
        } else if (mainAblumNewLayoutBinding.drawerview.isDrawerOpen()) {
            mainAblumNewLayoutBinding.drawerview.closeDrawer(true);
        } else if (mainAblumNewLayoutBinding.imageUtilsView.isInfoShow()) {
            mainAblumNewLayoutBinding.imageUtilsView.closeInfoView();
        } else {
            if (mBackKeyPressedTimes == 0) {
                Toast.makeText(this, getResources().getString(R.string.back_press), Toast.LENGTH_SHORT).show();
                mBackKeyPressedTimes = 1;
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            mBackKeyPressedTimes = 0;
                        }
                    }
                }.start();
                return;
            } else {
                finish();
            }

            super.onBackPressed();
        }
    }


    public void closeImageUtil() {
        if (mainAblumNewLayoutBinding.imageUtilsView.isNowtypeStart()) {
            if (animClose != null && canClick) {
                animClose.start();
            }
        }
    }

    public void updataImageUtils(List<GridItem> list) {
        if (mainAblumNewLayoutBinding.imageUtilsView.getVisibility() == VISIBLE) {
            mainAblumNewLayoutBinding.imageUtilsView.updataImageData(list.get(position).getImageData());
            // image_utils_view.setHistoryImg(presenter.getCurrentBit());
        }
    }


    @Override
    public void inTopSelect() {
        presenter.setCanClick(true);
        mainAblumNewLayoutBinding.include.albumNewTopSelectView.setVisibility(VISIBLE);


        presenter.startFloatAnim(new AnimFactory.FloatListener() {
            @Override
            public void floatValueChang(float f) {
//                presetBottomView.setTranslationY(bottom_layout.getMeasuredHeight() * (1 - f));
//                presetBottomView.setAlpha(f);
//
//                camera_bottom_layout.setTranslationY(bottom_layout.getMeasuredHeight() * f);
//                camera_bottom_layout.setAlpha(1 - f);

                mainAblumNewLayoutBinding.include.titleLayout.setTranslationY(mainAblumNewLayoutBinding.include.topLayout.getMeasuredHeight() * -f);
                mainAblumNewLayoutBinding.include.titleLayout.setAlpha(1 - f);

                mainAblumNewLayoutBinding.include.albumNewTopSelectView.setTranslationY(-mainAblumNewLayoutBinding.include.topLayout.getMeasuredHeight() * (1 - f));
                mainAblumNewLayoutBinding.include.albumNewTopSelectView.setAlpha(f);

            }
        });
    }

    @Override
    public void outTopSelect() {
        if (mainAblumNewLayoutBinding.bigImgview.getVisibility() == VISIBLE) {
            mainAblumNewLayoutBinding.drawerview.setVisibility(View.INVISIBLE);
            // mainAblumNewLayoutBinding.panel.setVisibility(View.INVISIBLE);
        } else {
            mainAblumNewLayoutBinding.drawerview.setVisibility(View.VISIBLE);
            //  mainAblumNewLayoutBinding.panel.setVisibility(VISIBLE);
        }
        presenter.clearPic();
        presenter.setCanClick(false);
        presenter.startFloatAnim(new AnimFactory.FloatListener() {
            @Override
            public void floatValueChang(float f) {
//                presetBottomView.setTranslationY(bottom_layout.getMeasuredHeight() * f);
//                presetBottomView.setAlpha(1 - f);
//
//                camera_bottom_layout.setTranslationY(bottom_layout.getMeasuredHeight() * (1 - f));
//                camera_bottom_layout.setAlpha(f);

                mainAblumNewLayoutBinding.include.titleLayout.setTranslationY(-mainAblumNewLayoutBinding.include.topLayout.getMeasuredHeight() * (1 - f));
                mainAblumNewLayoutBinding.include.titleLayout.setAlpha(f);

                mainAblumNewLayoutBinding.include.albumNewTopSelectView.setTranslationY(mainAblumNewLayoutBinding.include.topLayout.getMeasuredHeight() * -f);
                mainAblumNewLayoutBinding.include.albumNewTopSelectView.setAlpha(1 - f);

            }
        });
    }

    /**
     * 能否点击
     *
     * @param canClick
     */
    @Override
    public void setTopSelectCanClick(boolean canClick) {
        mainAblumNewLayoutBinding.include.albumNewTopSelectView.setTopSelectCanClick(canClick);
    }

    /**
     * 分享按钮点击
     *
     * @param canClick
     */
    @Override
    public void setShareCanClick(boolean canClick) {
        mainAblumNewLayoutBinding.include.albumNewTopSelectView.setShareCanClick(canClick);
    }

    @Override
    public void onDrawStatus(boolean isOpen) {
        setApplyBlur(isOpen);
        if (isOpen) {
            mainAblumNewLayoutBinding.up.setVisibility(View.INVISIBLE);
            mainAblumNewLayoutBinding.down.setVisibility(View.VISIBLE);
        } else {
            mainAblumNewLayoutBinding.up.setVisibility(VISIBLE);
            mainAblumNewLayoutBinding.down.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setApplyBlur(boolean isBlur) {
        if (isBlur) {
            mainAblumNewLayoutBinding.blurImageview.setVisibility(VISIBLE);
            BlurCallable blurCallable = new BlurCallable(BlurUtil.getViewCache(this));
            FutureTask<Drawable> futureTask = new FutureTask(blurCallable);
            ThreadPoolUtil.execute(futureTask);
            // blurCallable.setRadius(15);
            try {
                Drawable drawable = futureTask.get(3000, TimeUnit.MILLISECONDS);
                if (futureTask.isDone())
                    futureTask.cancel(false);
                mainAblumNewLayoutBinding.blurImageview.setBackground(drawable);
                mainAblumNewLayoutBinding.blurImageview.setVisibility(VISIBLE);
            } catch (Exception e) {
                mainAblumNewLayoutBinding.blurImageview.setVisibility(VISIBLE);
            }
        } else {
            mainAblumNewLayoutBinding.blurImageview.setVisibility(View.GONE);
            mainAblumNewLayoutBinding.blurImageview.setBackgroundColor(getResources().getColor(R.color.translucent));
        }
    }



   /* @Override
    public void onPanelClosed(Panel panel) {
        mainAblumNewLayoutBinding.up.setBackground(getResources().getDrawable(R.drawable.new_up));
        ViewGroup.LayoutParams lp = mainAblumNewLayoutBinding.up.getLayoutParams();
        lp.width = (int) getResources().getDimension(R.dimen.dp_34);
        lp.height = (int) getResources().getDimension(R.dimen.dp_8);
        mainAblumNewLayoutBinding.up.setLayoutParams(lp);
    }

    @Override
    public void onPanelOpened(Panel panel) {
        mainAblumNewLayoutBinding.up.setBackground(getResources().getDrawable(R.drawable.new_down));
        ViewGroup.LayoutParams lp = mainAblumNewLayoutBinding.up.getLayoutParams();
        lp.width = (int) getResources().getDimension(R.dimen.dp_34);
        lp.height = (int) getResources().getDimension(R.dimen.dp_4);
        mainAblumNewLayoutBinding.up.setLayoutParams(lp);
        // newUp.setMaxHeight((int) getResources().getDimension(R.dimen.dp_4));
    }*/

    @Override
    public void refreshGridView(UploadBean uploadBean) {
        LoginedUser loginedUser = LoginedUser.getLoginedUser();

//        if(loginedUser.getUploadStatus()==Flag.UPLOAD_UPLOADING){
        View view = mainAblumNewLayoutBinding.include.gridView.getHeaderAt(0);
        if (null == view) {
            if (uploadBean.getHttpType() == UploadManager.WIFI) {
                if (uploadBean.getUploadStatus() == Flag.UPLOAD_SUCCESS) {
                    if (uploadBean.getCount() > 0) {
                        loginedUser.setUploadStatus(Flag.UPLOAD_FAIL);
                        LoginedUser.setLoginedUser(loginedUser);
                    } else {
                        loginedUser.setUploadStatus(Flag.UPLOAD_SUCCESS);
                        LoginedUser.setLoginedUser(loginedUser);
                    }
                } else if (uploadBean.getUploadStatus() == Flag.UPLOAD_FULL) {
                    loginedUser.setUploadStatus(Flag.UPLOAD_FULL);
                    LoginedUser.setLoginedUser(loginedUser);
                } else if (uploadBean.getUploadStatus() == Flag.UPLOAD_NETCHANGE) {
                    if (uploadBean.getCount() > 0) {
                        loginedUser.setUploadStatus(Flag.UPLOAD_UPLOADING);
                        LoginedUser.setLoginedUser(loginedUser);
                    } else {

                        if (!StringUtil.isBlank((loginedUser.getUploadTime())) && TimeUtil.isSameDay(Long.valueOf(loginedUser.getUploadTime()), TimeUtil.getBeiJingTimeGMT())) {
                            loginedUser.setUploadStatus(Flag.UPLOAD_SUCCESS);
                        } else {
                            loginedUser.setUploadStatus(Flag.UPLOAD_NONE);
                        }
                        LoginedUser.setLoginedUser(loginedUser);
                    }
                }
            } else {
//                loginedUser.setUploadStatus(Flag.UPLOAD_NETCHANGE);
//                LoginedUser.setLoginedUser(loginedUser);
            }
            return;
        }

        TextView tv_hint = (TextView) view.findViewById(R.id.tv_hint);
        LinearLayout ll_upload = (LinearLayout) view.findViewById(R.id.ll_upload);
        ImageView grid_item = (ImageView) view.findViewById(R.id.grid_item);
        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
        TextView tv_refresh = (TextView) view.findViewById(R.id.tv_refresh);

        if (uploadBean.getHttpType() == UploadManager.WIFI) {
            //上传完成
            if (uploadBean.getUploadStatus() == Flag.UPLOAD_SUCCESS) {

                loginedUser.setUploadStatus(Flag.UPLOAD_SUCCESS);
                LoginedUser.setLoginedUser(loginedUser);
                tv_hint.setText(getResources().getString(R.string.to_backup_success));
                grid_item.setImageResource(R.drawable.upload_success);
                tv_time.setText(getResources().getString(R.string.to_backup_time) + convertDate2(loginedUser.getUploadTime()));
                ll_upload.setBackground(getDrawable(R.drawable.album_head_bg));

                tv_refresh.setVisibility(View.GONE);
                //上传空间已满
            } else if (uploadBean.getUploadStatus() == Flag.UPLOAD_FULL) {
                tv_hint.setText(getResources().getString(R.string.to_backup_suspended));
                ll_upload.setBackground(getDrawable(R.drawable.album_head_red_bg));
                tv_time.setText(getResources().getString(R.string.to_backup_space_error));
                tv_refresh.setVisibility(VISIBLE);
                tv_refresh.setText(Html.fromHtml("<font color='#0057FF'>" + getResources().getString(R.string.refresh) + "</font>"));
                loginedUser.setUploadStatus(Flag.UPLOAD_FULL);
                LoginedUser.setLoginedUser(loginedUser);

                //上传中
            } else if (uploadBean.getUploadStatus() == Flag.UPLOAD_UPLOADING) {

                tv_hint.setText(getResources().getString(R.string.in_the_backup));
                ll_upload.setBackground(getDrawable(R.drawable.album_head_bg));
                grid_item.setImageResource(R.drawable.oval);
                tv_time.setText(getResources().getString(R.string.in_the_backup_num) + String.format("%02d", uploadBean.getCount()));

                tv_refresh.setVisibility(View.GONE);
            } else if (uploadBean.getUploadStatus() == Flag.UPLOAD_NETCHANGE) {
                //准备上传(非WIFI状态下点击上传按钮 下次进入自动上传)
                if (loginedUser.getUploadStatus() == Flag.UPLOAD_REUPLOAD) {

                    tv_hint.setText(getResources().getString(R.string.in_the_backup));
                    ll_upload.setBackground(getDrawable(R.drawable.album_head_bg));
                    grid_item.setImageResource(R.drawable.oval);
                    tv_time.setText(getResources().getString(R.string.in_the_backup_num) + String.format("%02d", presenter.getmGirdListSize()));
                    presenter.uploadData();
                } else {
                    if (uploadBean.getCount() == 0) {
                        ll_upload.setBackground(getDrawable(R.drawable.album_head_bg));

                        if (!StringUtil.isBlank((loginedUser.getUploadTime())) && TimeUtil.isSameDay(Long.valueOf(loginedUser.getUploadTime()), TimeUtil.getBeiJingTimeGMT())) {
                            loginedUser.setUploadStatus(Flag.UPLOAD_SUCCESS);
                            LoginedUser.setLoginedUser(loginedUser);
                            tv_hint.setText(getResources().getString(R.string.to_backup_success));
                            grid_item.setImageResource(R.drawable.upload_success);
                            tv_time.setText(getResources().getString(R.string.to_backup_time) + convertDate2(loginedUser.getUploadTime()));
                        } else {
                            loginedUser.setUploadStatus(Flag.UPLOAD_NONE);
                            LoginedUser.setLoginedUser(loginedUser);
                            tv_hint.setText(getString(R.string.upload_backup));
                            grid_item.setImageResource(R.drawable.new_upload_white);
                            if (!StringUtil.isBlank((loginedUser.getUploadTime()))) {
                                tv_time.setText(getResources().getString(R.string.to_backup_time) + convertDate2(loginedUser.getUploadTime()));
                            } else {
                                tv_time.setText(getString(R.string.automatically));
                            }
                        }
                    } else {
                        tv_hint.setText(getResources().getString(R.string.in_the_backup));
                        ll_upload.setBackground(getDrawable(R.drawable.album_head_bg));
                        grid_item.setImageResource(R.drawable.oval);
                        tv_time.setText(getResources().getString(R.string.in_the_backup_num) + String.format("%02d", uploadBean.getCount()));

                        loginedUser.setUploadStatus(Flag.UPLOAD_UPLOADING);
                        LoginedUser.setLoginedUser(loginedUser);
                    }
                }

                tv_refresh.setVisibility(View.GONE);
            } else if (uploadBean.getUploadStatus() == Flag.UPLOAD_FAIL) {

                tv_hint.setText(getResources().getString(R.string.to_backup_suspended));
                ll_upload.setBackground(getDrawable(R.drawable.album_head_red_bg));
                tv_time.setText(getResources().getString(R.string.to_backup_server_error));
                tv_refresh.setVisibility(VISIBLE);
                tv_refresh.setText(Html.fromHtml("<font color='#0057FF'>" + getResources().getString(R.string.refresh) + "</font>"));

                loginedUser.setUploadStatus(Flag.UPLOAD_FAIL);
                LoginedUser.setLoginedUser(loginedUser);
            }
        } else {
            if (uploadBean.getUploadStatus() == Flag.UPLOAD_REUPLOAD) {

                grid_item.setImageResource(R.drawable.oval);
                tv_hint.setText(getResources().getString(R.string.to_backup_suspended));
                tv_time.setText(getResources().getString(R.string.to_backup_wifi_error) + String.format("%02d", uploadBean.getCount()));
                ll_upload.setBackground(getDrawable(R.drawable.album_gray_bg));

                loginedUser.setUploadStatus(Flag.UPLOAD_REUPLOAD);
                LoginedUser.setLoginedUser(loginedUser);
            } else {
                if (uploadBean.getCount() > 0) {
                    grid_item.setImageResource(R.drawable.oval);
                    tv_hint.setText(getResources().getString(R.string.to_backup_suspended));
                    tv_time.setText(getResources().getString(R.string.to_backup_wifi_error) + String.format("%02d", uploadBean.getCount()));
                    ll_upload.setBackground(getDrawable(R.drawable.album_gray_bg));

                    loginedUser.setUploadStatus(Flag.UPLOAD_NETCHANGE);
                    LoginedUser.setLoginedUser(loginedUser);
                }
            }

            tv_refresh.setVisibility(View.GONE);
        }

        mainAblumNewLayoutBinding.include.gridView.invalidate(0, view.getTop(), mainAblumNewLayoutBinding.include.gridView.getWidth(),
                view.getHeight());
//        }
    }

    private String convertDate2(String uplaodTime) {
        if (!StringUtil.isBlank(uplaodTime)) {
            Date data = new Date(Long.valueOf(uplaodTime));
            //日期格式
            String dateFormat = this.getString(R.string.format_date_no_year_hh);
            String f1 = (String) DateFormat.format(dateFormat, data);
            //星期格式
           /* String weekFormat = context.getString(R.string.format_week);
            String f = (String) DateFormat.format(weekFormat, data);*/
            //return f1 + " " + f;
            return f1;
        }
        return "";
    }

    @Override
    public void setPanelClick(boolean isPanelClick) {
        this.isPanelClick = isPanelClick;
    }

    @Override
    public void initGuide() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (presenter.mGirdList == null || presenter.mGirdList.size() == 0) {
                    //没有图片的提示
                    NewbieGuide.with(AlbumNewActivity.this)
                            .setLabel("pageAblum")//设置引导层标示区分不同引导层，必传！否则报错
                            .setShowCounts(1)
                            .setOnGuideChangedListener(new OnGuideChangedListener() {
                                @Override
                                public void onShowed(Controller controller) {
                                    //引导层显示
                                }

                                @Override
                                public void onRemoved(Controller controller) {
                                    //引导层消失（多页切换不会触发）
                                }
                            })
                            .setOnPageChangedListener(new OnPageChangedListener() {

                                @Override
                                public void onPageChanged(int page) {
                                }
                            })
                            .addGuidePage(//添加一页引导页
                                    GuidePage.newInstance()//创建一个实例
                                            .setBackgroundColor(getResources().getColor(R.color.trans_backgroud))
                                            .setLayoutRes(R.layout.guide_ablum)//设置引导页布局
                                            .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                                @Override
                                                public void onLayoutInflated(View view, Controller controller) {
                                                    ImageView select = (ImageView) view.findViewById(R.id.select);
                                                    ImageView backup = (ImageView) view.findViewById(R.id.backup);
                                                    ImageView picture = (ImageView) view.findViewById(R.id.picture);
                                                    if (presenter.mGirdList == null || presenter.mGirdList.size() == 0) {
                                                        select.setImageDrawable(getResources().getDrawable(R.drawable.guide_ablum_add));
                                                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                        layoutParams.addRule(ALIGN_PARENT_LEFT);
                                                        layoutParams.addRule(ALIGN_PARENT_TOP);
                                                        select.setLayoutParams(layoutParams);
                                                    }
                                                    select.setVisibility(View.VISIBLE);
                                                    backup.setVisibility(View.GONE);
                                                    picture.setVisibility(View.GONE);
                                                    //引导页布局填充后回调，用于初始化
                                       /* TextView tv = view.findViewById(R.id.textView2);
                                        tv.setText("我是动态设置的文本");*/
                                                }
                                            })
                                            .setEnterAnimation(enterAnimation)//进入动画
                                            .setExitAnimation(exitAnimation)//退出动画
                            )
                            .addGuidePage(GuidePage.newInstance()
                                    // .addHighLight(tvBottom, HighLight.Shape.RECTANGLE, 20)
                                    .setBackgroundColor(getResources().getColor(R.color.trans_backgroud))
                                    .setLayoutRes(R.layout.guide_ablum)//引导页布局，点击跳转下一页或者消失引导层的控件id
                                    .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                        @Override
                                        public void onLayoutInflated(View view, final Controller controller) {
                                            ImageView select = (ImageView) view.findViewById(R.id.select);
                                            ImageView backup = (ImageView) view.findViewById(R.id.backup);
                                            ImageView picture = (ImageView) view.findViewById(R.id.picture);
                                            if (presenter.mGirdList == null || presenter.mGirdList.size() == 0) {
                                                backup.setImageDrawable(getResources().getDrawable(R.drawable.guide_ablum_bottom));
                                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                layoutParams.addRule(ALIGN_PARENT_BOTTOM);
                                                layoutParams.addRule(CENTER_HORIZONTAL);
                                                backup.setLayoutParams(layoutParams);
                                            }
                                            select.setVisibility(View.GONE);
                                            backup.setVisibility(View.VISIBLE);
                                            picture.setVisibility(View.GONE);
                                        }
                                    })
                                    .setBackgroundColor(getResources().getColor(R.color.translucent))//设置背景色，建议使用有透明度的颜色
                                    .setEnterAnimation(enterAnimation)//进入动画
                                    .setExitAnimation(exitAnimation)//退出动画
                            )
                            .addGuidePage(GuidePage.newInstance()
                                    //.addHighLight(tvBottom)
                                    .setLayoutRes(R.layout.guide_ablum)
                                    .setBackgroundColor(getResources().getColor(R.color.trans_backgroud))
                                    .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                        @Override
                                        public void onLayoutInflated(View view, final Controller controller) {
                                            ImageView select = (ImageView) view.findViewById(R.id.select);
                                            ImageView backup = (ImageView) view.findViewById(R.id.backup);
                                            ImageView picture = (ImageView) view.findViewById(R.id.picture);
                                            if (presenter.mGirdList == null || presenter.mGirdList.size() == 0) {
                                                picture.setImageDrawable(getResources().getDrawable(R.drawable.guide_ablum_camera));
                                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                layoutParams.addRule(ALIGN_PARENT_BOTTOM);
                                                layoutParams.addRule(ALIGN_PARENT_RIGHT);
                                                picture.setLayoutParams(layoutParams);
                                            }
                                            select.setVisibility(View.GONE);
                                            backup.setVisibility(View.GONE);
                                            picture.setVisibility(View.VISIBLE);
                                        }
                                    })
                            )
                            .show();//显示引导层(至少需要一页引导页才能显示)
                } else {
                    //有图片的提示
                    NewbieGuide.with(AlbumNewActivity.this)
                            .setLabel("pageAblumPhoto")//设置引导层标示区分不同引导层，必传！否则报错
                            .setShowCounts(1)
                            .setOnGuideChangedListener(new OnGuideChangedListener() {
                                @Override
                                public void onShowed(Controller controller) {
                                    //引导层显示
                                }

                                @Override
                                public void onRemoved(Controller controller) {
                                    //引导层消失（多页切换不会触发）
                                }
                            })
                            .setOnPageChangedListener(new OnPageChangedListener() {

                                @Override
                                public void onPageChanged(int page) {
                                }
                            })
                            .addGuidePage(//添加一页引导页
                                    GuidePage.newInstance()//创建一个实例
                                            .setBackgroundColor(getResources().getColor(R.color.trans_backgroud))
                                            .setLayoutRes(R.layout.guide_ablum)//设置引导页布局
                                            .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                                @Override
                                                public void onLayoutInflated(View view, Controller controller) {
                                                    ImageView select = (ImageView) view.findViewById(R.id.select);
                                                    ImageView backup = (ImageView) view.findViewById(R.id.backup);
                                                    ImageView picture = (ImageView) view.findViewById(R.id.picture);

                                                    select.setVisibility(View.VISIBLE);
                                                    backup.setVisibility(View.GONE);
                                                    picture.setVisibility(View.GONE);
                                                    //引导页布局填充后回调，用于初始化
                                       /* TextView tv = view.findViewById(R.id.textView2);
                                        tv.setText("我是动态设置的文本");*/
                                                }
                                            })
                                            .setEnterAnimation(enterAnimation)//进入动画
                                            .setExitAnimation(exitAnimation)//退出动画
                            )
                            .addGuidePage(GuidePage.newInstance()
                                    // .addHighLight(tvBottom, HighLight.Shape.RECTANGLE, 20)
                                    .setBackgroundColor(getResources().getColor(R.color.trans_backgroud))
                                    .setLayoutRes(R.layout.guide_ablum)//引导页布局，点击跳转下一页或者消失引导层的控件id
                                    .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                        @Override
                                        public void onLayoutInflated(View view, final Controller controller) {
                                            ImageView select = (ImageView) view.findViewById(R.id.select);
                                            ImageView backup = (ImageView) view.findViewById(R.id.backup);
                                            ImageView picture = (ImageView) view.findViewById(R.id.picture);

                                            select.setVisibility(View.GONE);
                                            backup.setVisibility(View.VISIBLE);
                                            picture.setVisibility(View.GONE);
                                        }
                                    })
                                    .setBackgroundColor(getResources().getColor(R.color.translucent))//设置背景色，建议使用有透明度的颜色
                                    .setEnterAnimation(enterAnimation)//进入动画
                                    .setExitAnimation(exitAnimation)//退出动画
                            )
                            .addGuidePage(GuidePage.newInstance()
                                    //.addHighLight(tvBottom)
                                    .setLayoutRes(R.layout.guide_ablum)
                                    .setBackgroundColor(getResources().getColor(R.color.trans_backgroud))
                                    .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                        @Override
                                        public void onLayoutInflated(View view, final Controller controller) {
                                            ImageView select = (ImageView) view.findViewById(R.id.select);
                                            ImageView backup = (ImageView) view.findViewById(R.id.backup);
                                            ImageView picture = (ImageView) view.findViewById(R.id.picture);

                                            select.setVisibility(View.GONE);
                                            backup.setVisibility(View.GONE);
                                            picture.setVisibility(View.VISIBLE);
                                        }
                                    })
                            )
                            .show();//显示引导层(至少需要一页引导页才能显示)
                }
            }
        }, 500);

    }

    @Override
    public void onConnectBlueTooth(boolean isConnected, String deviceName, boolean isAI360) {
        if (isConnected) {
            mainAblumNewLayoutBinding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CONNECTION);
            mainAblumNewLayoutBinding.blueToothListView.setConnectBluetoothName(deviceName);
            if (isAI360) {
                openAI360Camera();
            } else {
                openAICamera();
            }
            mainAblumNewLayoutBinding.blueToothListView.setVisibility(View.GONE);
        } else {
            mainAblumNewLayoutBinding.blueToothListView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CONNECTION_ERROR);
        }

    }

    @Override
    public void setBlueTouchType(int blueTouchType) {
        mainAblumNewLayoutBinding.blueToothListView.setBlueTouchType(blueTouchType);
    }
}
