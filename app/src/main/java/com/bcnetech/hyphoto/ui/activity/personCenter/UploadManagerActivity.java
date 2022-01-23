package com.bcnetech.hyphoto.ui.activity.personCenter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.bean.data.DownloadInfoData;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.HttpUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.DownloadInfoSqlControl;
import com.bcnetech.hyphoto.data.UploadBean;
import com.bcnetech.hyphoto.presenter.UploadManagerPresenter;
import com.bcnetech.hyphoto.presenter.iview.IUploadManagerView;
import com.bcnetech.hyphoto.receiver.HttpChangReceiver;
import com.bcnetech.hyphoto.receiver.UploadManagerReceiver;
import com.bcnetech.hyphoto.task.manage.UploadManager;
import com.bcnetech.hyphoto.ui.activity.BaseMvpActivity;
import com.bcnetech.hyphoto.ui.adapter.UploadAdapter;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.ui.view.swipemenu.SwapRecyclerView;
import com.bcnetech.hyphoto.ui.view.swipemenu.SwipeMenuBuilder;
import com.bcnetech.hyphoto.ui.view.swipemenu.bean.SwipeMenu;
import com.bcnetech.hyphoto.ui.view.swipemenu.bean.SwipeMenuItem;
import com.bcnetech.hyphoto.ui.view.swipemenu.view.SwipeMenuLayout;
import com.bcnetech.hyphoto.ui.view.swipemenu.view.SwipeMenuView;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.bcnetech.hyphoto.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yhf on 2017/4/14.
 */
public class UploadManagerActivity extends BaseMvpActivity<IUploadManagerView,UploadManagerPresenter> implements SwipeMenuBuilder  {

    private TitleView title_layout;
    private SwapRecyclerView recyclerView;
    private TextView empty;
    private DownloadInfoSqlControl downloadInfoSqlControl;
    private boolean showDalog=false;
    private UploadManager uploadManager;
    private UploadAdapter adapter;
    private List<DownloadInfoData> list;
    private HttpChangReceiver httpChangReceiver;
    private UploadManagerReceiver uploadManagerReceiver;
    private ProgressBar progressBar;
    private int increment;
    private TextView tv_hint;
    private boolean isFirst=true;
    private int count;
    private boolean canIncrement=true;


    @Override
    public UploadManagerPresenter initPresenter() {
        return new UploadManagerPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_load_manage);
        initView();
        initData();
        onViewClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        downloadInfoSqlControl.QueryInfoAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (httpChangReceiver != null) {
            httpChangReceiver.unregister(this);
        }

        if(uploadManagerReceiver!=null){
            uploadManagerReceiver.unregister(this);
        }
    }

    @Override
    protected void initView() {
        recyclerView = (SwapRecyclerView) findViewById(R.id.default_manage_list);
        empty = (TextView) findViewById(R.id.empty);
        title_layout = (TitleView) findViewById(R.id.title_layout);
        progressBar= (ProgressBar) findViewById(R.id.pb_progressbar);
        tv_hint= (TextView) findViewById(R.id.tv_hint);
    }

    @Override
    protected void initData() {
        downloadInfoSqlControl=new DownloadInfoSqlControl(this);


        uploadManagerReceiver=new UploadManagerReceiver() {
            @Override
            public void onGetData(String type) {
                downloadInfoSqlControl.QueryInfoAll();
                if(!StringUtil.isBlank(type)&&type.equals("upload")&&canIncrement){
                    progressBar.incrementProgressBy(1);
                }
            }

            @Override
            public void onDeleteData(int count) {

            }
        };

        uploadManagerReceiver.register(this);


        title_layout.setType(TitleView.UPLOAD_MANAGE);
        uploadManager=UploadManager.getInstance();


        list=new ArrayList<>();
        adapter=new UploadAdapter(this,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onViewClick() {

        downloadInfoSqlControl.setListener(new BaseSqlControl.SqlControlListener() {
            @Override
            public void queryListener(Object... parms) {
                List<DownloadInfoData> downloadInfoDatas=(List<DownloadInfoData>) parms[0];
                if(null==list){
                    list=new ArrayList<>();
                }
                if(isFirst){
                    isFirst=false;
                    count=downloadInfoDatas.size();
                    progressBar.setMax(count);
                }
                //无网络连接
                if(UploadManager.NULL==uploadManager.getHttpType()){
                    tv_hint.setText(getResources().getString(R.string.none_upload));
                    //移动网络
                }else if(UploadManager.NET==uploadManager.getHttpType()){
                    if(downloadInfoDatas.size()>0){
                        tv_hint.setText(getResources().getString(R.string.move_upload)+(count-downloadInfoDatas.size())+"/"+count);
                    }else {
                        tv_hint.setText(getResources().getString(R.string.none_upload));
                    }
                    //wifi连接
                }else if(UploadManager.WIFI==uploadManager.getHttpType()) {
                    if(downloadInfoDatas.size()>0){
                        tv_hint.setText(getResources().getString(R.string.wifi_upload)+(count-downloadInfoDatas.size())+"/"+count);

                    }else {
                        tv_hint.setText(getResources().getString(R.string.none_upload));
                    }
                }




//                uploadManager.runOnDraw(downloadInfoDatas);
                list.clear();
                list.addAll(downloadInfoDatas);
                adapter.setData(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void insertListener(Object... parms) {

            }

            @Override
            public void deletListener(Object... parms) {

            }

            @Override
            public void upDataListener(Object... parms) {

            }
        });

//        adapter.setReUoloadClickListener(new UploadAdapter.OnReUploadClickListener() {
//            @Override
//            public void onClick(RecyclerView.ViewHolder holder, int position) {
//                if (!HttpUtil.isNetworkAvailable(UploadManagerActivity.this, LoginedUser.getLoginedUser().isonlywifi(),true)) {
//                    CostomToastUtil.toast(getResources().getString(R.string.network_disconn));
//                    return;
//                }
//                DownloadInfoData downloadInfoData=list.get(position);
//                downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_REUPLOAD);
//                downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);
//                adapter.notifyDataSetChanged();
//                uploadManager.runTaskOne(downloadInfoData);
//            }
//        });

        title_layout.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title_layout.setRightTextListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.size()!=0){
                    canIncrement=false;
                    uploadManager.dellAllRunTask();
                    downloadInfoSqlControl.QueryInfoAll();

                    EventBus.getDefault().post(new UploadBean(0, Flag.UPLOAD_SUCCESS, uploadManager.getHttpType()));

                }

//                if (!HttpUtil.isNetworkAvailable(UploadManagerActivity.this, LoginedUser.getLoginedUser().isonlywifi())) {
//                    CostomToastUtil.toast(getResources().getString(R.string.network_disconn));
//                    return;
//                }
//
//                if (uploadManager.getmRunOnDraw() != null) {
//                    while (!uploadManager.getmRunOnDraw().isEmpty()) {
//                        uploadManager.getmRunOnDraw().poll();
//                    }
//                }
//
//                for(int i=0;i<list.size();i++){
//                    DownloadInfoData downloadInfoData=list.get(i);
//                    if(downloadInfoData.getType()==DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL){
//                        uploadManager.runOnDraw(downloadInfoData);
//                        downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_REUPLOAD);
//                        downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//                uploadManager.runUploarunTaskAlldTaskOne(downloadInfoData,UploadManagerActivity.this);

//                uploadManager.runTaskById(uploadManager.getmRunOnDraw());
            }
        });

    }

    private SwipeMenuView.OnMenuItemClickListener mOnSwipeItemClickListener = new SwipeMenuView.OnMenuItemClickListener() {

        @Override
        public void onMenuItemClick(int pos, SwipeMenu menu, int index, SwipeMenuLayout layout) {
            if (!HttpUtil.isNetworkAvailable(UploadManagerActivity.this, LoginedUser.getLoginedUser().isonlywifi(),true)) {
                ToastUtil.toast(getResources().getString(R.string.network_disconn));
                layout.smoothCloseMenu();
                return;
            }
            DownloadInfoData downloadInfoData=list.get(pos);
            if(downloadInfoData.getType()==DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL){
                downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_UPLOAD);
                downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);
                adapter.notifyDataSetChanged();
                uploadManager.runOnDraw(downloadInfoData);
            }
            layout.smoothCloseMenu();

        }
    };


    @Override
    public SwipeMenuView create() {
        SwipeMenu menu = new SwipeMenu(this);
        SwipeMenuItem item = new SwipeMenuItem(UploadManagerActivity.this);

        item.setTitle("")
                .setTitleColor(Color.WHITE)
                .setBackground(new ColorDrawable(getResources().getColor(R.color.grey_item)))
                .setWidth(dp2px(65))
                .setTitleSize(12)
                .setIconWidth(dp2px(22))
                .setIconHeight(dp2px(18))
                .setMargin_right(dp2px(6))
                .setMargin_bottom(dp2px(7))
                .setText_topMargin(dp2px(8))
                .setIcon(R.drawable.reup);

        menu.addMenuItem(item);
        SwipeMenuView menuView = new SwipeMenuView(menu);

        menuView.setOnMenuItemClickListener(mOnSwipeItemClickListener);

        return menuView;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
