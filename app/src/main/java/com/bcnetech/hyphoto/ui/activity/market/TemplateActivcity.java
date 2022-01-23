package com.bcnetech.hyphoto.ui.activity.market;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.bcnetech.bcnetchhttp.RetrofitFactory;
import com.bcnetech.bcnetchhttp.RetrofitUploadFactory;
import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.base.PresetInfoObserver;
import com.bcnetech.bcnetchhttp.bean.request.ParamsDownloadBody;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.bean.response.MarketParamsIndexListData;
import com.bcnetech.bcnetchhttp.bean.response.MarketParamsListData;
import com.bcnetech.bcnetchhttp.bean.response.Preinstail;
import com.bcnetech.bcnetchhttp.bean.response.ShareOwnerParmsData;
import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.util.sharepreference.SharePreferences;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.PresetParmsSqlControl;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.model.MarketModel;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.task.MBaseObserver;
import com.bcnetech.hyphoto.ui.adapter.MarketParamsAdapter;
import com.bcnetech.hyphoto.ui.fragment.MarketFragment;
import com.bcnetech.hyphoto.ui.view.ImageparmsNewView;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.bcnetech.hyphoto.utils.cache.CacheManager;
import com.bcnetech.hyphoto.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.VISIBLE;

/**
 * 模版拼图和预设参数市场
 * Created by a1234 on 17/2/14.
 */

public class TemplateActivcity extends BaseActivity {
    private boolean isLoading;
    private Handler handler = new Handler();
    private RecyclerView params_recycle;
    private MarketParamsAdapter marketParamsAdapter;
    private MarketModel marketModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private StaggeredGridLayoutManager staggeredGridLayoutManager_params;
    private List<MarketParamsListData.PresetParmManageItem> paramsList;
    private TitleView titleview;
    private int page;
    boolean listend = false;
    private int position;
    private PresetParm presetParm;
    //    private String fileId;
//    private String categoryId;
    private PresetParmsSqlControl presetParmsSqlControl;
    private String imageHeight;
    private String imageWidth;
    private String catalogId;
    private String catalogTitle;
    private boolean clickSave = false;
    private ImageparmsNewView imageInfo;
    private List<PictureProcessingData> histortList;

    public static void actionStart(Context context, int position, String catalogId, String catalogTitle, List<MarketParamsListData.PresetParmManageItem> paramsList) {
        Intent intent = new Intent(context, TemplateActivcity.class);
        intent.setAction("preparm");
        intent.putExtra("position", position);
        intent.putExtra("catalogTitle", catalogTitle);
        intent.putExtra("catalogId", catalogId);
        intent.putExtra("Parmlist", (Serializable) paramsList);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);
        initView();
        initData();
        onViewClick();
    }

    @Override
    protected void initView() {
        titleview = (TitleView) findViewById(R.id.market_title);
        titleview.setType(TitleView.PRESET_MARKER_INNER);
//        titleview.setLeftImg(R.drawable.btnback);
        titleview.setTitleText(getResources().getString(R.string.preparm_market));
        params_recycle = (RecyclerView) findViewById(R.id.params_recycle);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        imageInfo = (ImageparmsNewView) findViewById(R.id.image_info);
    }

    @Override
    protected void initData() {
        presetParmsSqlControl = new PresetParmsSqlControl(TemplateActivcity.this);
        position = this.getIntent().getIntExtra("position", -1);
        catalogId = this.getIntent().getStringExtra("catalogId");
        catalogTitle = this.getIntent().getStringExtra("catalogTitle");
//        paramsList = (List<MarketParamsListData.PresetParmManageItem>) getIntent().getSerializableExtra("Parmlist");

        paramsList= (List<MarketParamsListData.PresetParmManageItem>) CacheManager.readObject(this,"Parmlist");

        if (catalogTitle != null) {
            titleview.setTitleText(catalogTitle);
        }
//        SpacesItemDecoration decoration = new SpacesItemDecoration(30, SpacesItemDecoration.TYPE_MARKET);
//        params_recycle.addItemDecoration(decoration);
        staggeredGridLayoutManager_params = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        params_recycle.setLayoutManager(staggeredGridLayoutManager_params);
        params_recycle.setHasFixedSize(true);
        marketModel = new MarketModel();
       /* handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               *//* if(position<10) {
                    setParamsData(1);
                }else{
                   int pages = ((double)position+1/(double)10)>(position+1/10)?position+1/10+1:position+1/10;
                    for (int i=1;i<=pages;i++){
                        setParamsData(i);
                    }
                }*//*
                //listend = false;
            }
        }, 200);*/

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //下啦刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                if (paramsList != null) {
                    paramsList.clear();
                }
                setParamsData(1);
            }
        });
        this.page = ((double) (paramsList.size()) / (double) 10) > ((paramsList.size()) / 10) ? (paramsList.size()) / 10 + 1 : (paramsList.size()) / 10;
        marketParamsAdapter = new MarketParamsAdapter(TemplateActivcity.this, paramsList);
        params_recycle.setAdapter(marketParamsAdapter);
        MoveToPosition(staggeredGridLayoutManager_params, position);
    }


    @Override
    protected void onViewClick() {
        //点击下载
        marketParamsAdapter.setOnItemClickListener(new MarketParamsAdapter.ItemClickListener() {
            @Override
            public void onDown(View v, int position) {
                MarketParamsListData.PresetParmManageItem presetParmManageItem = paramsList.get(position);
                final String fileId = presetParmManageItem.getId();
                final String categoryId = presetParmManageItem.getCategoryId();
                imageWidth = presetParmManageItem.getImageWidth();
                imageHeight = presetParmManageItem.getImageHeight();
                if (fileId != null) {
                    getMarketPresetPams(presetParmManageItem.getFileId(), position, categoryId, presetParmManageItem.getCoverId());
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            List<MarketParamsListData.PresetParmManageItem> cahcedata = (List<MarketParamsListData.PresetParmManageItem>) CacheManager.readObject(TemplateActivcity.this, categoryId);
                            if (cahcedata != null && !CacheManager.isCacheDataFailure(TemplateActivcity.this, categoryId)) {
                                for (int i = 0; i < cahcedata.size(); i++) {
                                    if (cahcedata.get(i).getId().equals(fileId)) {
                                        cahcedata.get(i).setIsDownload("1");
                                        break;
                                    }
                                }
                                CacheManager.saveObject(TemplateActivcity.this, cahcedata, catalogId);
                                clickSave = true;
                            }
                        }
                    });

                    //downloadParams();
                }
                // PresetDownloadActivity.actionStart(TemplateActivcity.this, presetParmManageItem.getId(), presetParmManageItem.getDownloadCount(), presetParmManageItem.getImageWidth(), presetParmManageItem.getImageHeight(), presetParmManageItem.getCoboxName(),presetParmManageItem.getCategoryId());
            }

            @Override
            public void onPresetInfo(View v, final int position) {
                final MarketParamsListData.PresetParmManageItem presetParmManageItem = paramsList.get(position);
                //参数Id
                final String fileId = presetParmManageItem.getFileId();

                final String categoryId = presetParmManageItem.getCategoryId();

                imageWidth = presetParmManageItem.getImageWidth();
                imageHeight = presetParmManageItem.getImageHeight();

                RetrofitUploadFactory.getUPloadInstence().API().getLightData(presetParmManageItem.getFileId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new PresetInfoObserver<Preinstail>(TemplateActivcity.this,true) {
                            @Override
                            protected void onSuccees(Preinstail t) throws Exception {
                                if (t != null) {
                                    Preinstail preinstail = t;
                                    preinstail.setFileId(presetParmManageItem.getFileId());

                                    preinstail.setCoverId(presetParmManageItem.getCoverId());
                                    presetParm = marketModel.transferImageData(preinstail);
                                    if (histortList == null) {
                                        histortList = new ArrayList<>();
                                    }
                                    //历史记录
                                    histortList.clear();

                                    //原图
                                    PictureProcessingData srcPicData = new PictureProcessingData();
                                    PictureProcessingData paramPicData = new PictureProcessingData();

                                    if (null != TemplateActivcity.this.presetParm.getPartParmlists()) {

                                        for (int i = 0; i < presetParm.getPartParmlists().size(); i++) {
                                            PictureProcessingData pictureProcessingData = presetParm.getPartParmlists().get(i);
                                            if (pictureProcessingData.getType() == BizImageMangage.SRC) {
                                                srcPicData = presetParm.getPartParmlists().get(i);
                                            } else if (pictureProcessingData.getType() == BizImageMangage.PARMS) {
                                                paramPicData = presetParm.getPartParmlists().get(i);
                                            } else {
                                                histortList.add(presetParm.getPartParmlists().get(i));
                                            }
                                        }
                                        Collections.reverse(histortList);

//                                histortList.addAll(presetParm.getPartParmlists());
                                    }

//                            if(null!=presetParm.getParmlists()){
//
//                                if (presetParm.getParmlists() != null && presetParm.getParmlists().size() != 0) {
//                                    for (int i = 0;presetParm.getParmlists() != null && i < presetParm.getParmlists().size(); i++) {
//                                        if (presetParm.getParmlists().get(i).getType() == BizImageMangage.WHITE_BALANCE) {
//                                            histortList.add(new PictureProcessingData(BizImageMangage.WHITE_BALANCE,srcPicData.getImageUrl(),srcPicData.getImageData()));
//                                        }
//                                    }
////                                    histortList.add(new PictureProcessingData(BizImageMangage.PARMS,srcPicData.getImageUrl(), srcPicData.getImageData()));
//                                }
//                            }

                                    histortList.add(new PictureProcessingData(BizImageMangage.PARMS, paramPicData.getImageUrl(), paramPicData.getImageData()));

                                    histortList.add(new PictureProcessingData(BizImageMangage.SRC, srcPicData.getImageUrl(), srcPicData.getImageData()));
                                    imageInfo.setparmsPreset(presetParm, ImageparmsNewView.TYPE_DOWN);
                                    imageInfo.setDataPreset(histortList, histortList.size() - 1);
                                    if (!StringUtil.isBlank(imageWidth) && !StringUtil.isBlank(imageHeight)) {
                                        imageInfo.setSize(Integer.valueOf(imageWidth), Integer.valueOf(imageHeight), presetParm.getSize());
                                    }
                                    imageInfo.setVisibility(View.VISIBLE);
                                    imageInfo.inAnimationAtart();
                                    imageInfo.isDownload(presetParmManageItem.getIsDownload());
                                    imageInfo.setImageparmsViewListener(new ImageparmsNewView.ImageparmsViewListener() {
                                        @Override
                                        public void onImageparmsView() {

                                        }

                                        @Override
                                        public void onPresetParms() {

                                        }

                                        @Override
                                        public void onNoChange(boolean nochange) {

                                        }

                                        @Override
                                        public void onbtnclose() {

                                        }

                                        @Override
                                        public void deletePreset() {

                                        }

                                        @Override
                                        public void downLoadPreset() {
                                            getMarketPresetPams(presetParmManageItem.getFileId(), position, categoryId, presetParmManageItem.getCoverId());
                                            getHandler().post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    List<MarketParamsListData.PresetParmManageItem> cahcedata = (List<MarketParamsListData.PresetParmManageItem>) CacheManager.readObject(TemplateActivcity.this, categoryId);
                                                    if (cahcedata != null && !CacheManager.isCacheDataFailure(TemplateActivcity.this, categoryId)) {
                                                        for (int i = 0; i < cahcedata.size(); i++) {
                                                            if (cahcedata.get(i).getId().equals(fileId)) {
                                                                cahcedata.get(i).setIsDownload("1");
                                                                break;
                                                            }
                                                        }
                                                        CacheManager.saveObject(TemplateActivcity.this, cahcedata, catalogId);
                                                        clickSave = true;
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void sharePreset() {

                                        }
                                    });
                                    return;
                                } else {
                                    ToastUtil.toast(getResources().getString(R.string.empty));
                                    return;
                                }
                            }

                            @Override
                            protected void onCodeError(Preinstail t) throws Exception {
                                ToastUtil.toast(getResources().getString(R.string.download_error));
                            }

                            @Override
                            protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                                ToastUtil.toast(e.getMessage());
                            }
                        });
            }
        });
        titleview.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent();
                intent.putExtra("clicksave", clickSave);
                TemplateActivcity.this.setResult(MarketFragment.RESQUST_CODE, intent);
                finish();
            }
        });
        //上啦加载
        params_recycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (paramsList != null) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int[] lastVisiblePosition = staggeredGridLayoutManager_params.findLastVisibleItemPositions(null);
                        int lastitem = lastVisiblePosition[0];
                        if (lastitem >= staggeredGridLayoutManager_params.getItemCount() - 1) {
                            boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                            if (isRefreshing) {
                                marketParamsAdapter.notifyItemRemoved(marketParamsAdapter.getItemCount());
                                return;
                            }
                            if (!isLoading) {
                                isLoading = true;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        isLoading = false;

                                        if (!listend) {

                                            setParamsData(page + 1);
                                        } else {
                                            ToastUtil.toast(getResources().getString(R.string.none_more_data));
                                        }

                                    }
                                }, 200);
                            }
                        }
                    }
                }
            }
        });


        presetParmsSqlControl.setListener(new BaseSqlControl.SqlControlListener() {
            @Override
            public void deletListener(Object... parms) {

            }

            @Override
            public void upDataListener(Object... parms) {

            }

            @Override
            public void queryListener(Object... parms) {
                // -1 查询最后一个 0 不存在 1存在
                int exits = (int) parms[2];

                if (exits == 1) {
                    dissmissDialog();
                    ToastUtil.toast(getResources().getString(R.string.parm_exist));
                    return;
                }

                if (exits == 0) {
                    presetParmsSqlControl.getLastOne();
                }

                if (exits == -1) {
                    List<PresetParm> lastParm = (List<PresetParm>) parms[0];
                    presetParm.setShowType("0");
                    presetParm.setPosition(lastParm.get(0).getPosition() + 1);
                    presetParm.setImageHeight(imageHeight);
                    presetParm.setImageWidth(imageWidth);
                    presetParmsSqlControl.startInsert(TemplateActivcity.this.presetParm);

                    return;
                }
            }

            @Override
            public void insertListener(Object... parms) {
                dissmissDialog();
                ToastUtil.toast(getResources().getString(R.string.save_ok));
                if (imageInfo.getVisibility() == VISIBLE) {
                    imageInfo.refresh();
                }
            }
        });
    }

    public void MoveToPosition(StaggeredGridLayoutManager manager, int n) {
        manager.scrollToPositionWithOffset(n, 0);
    }

    private void setParamsData(final int page) {
        TemplateActivcity.this.page = page;
        marketModel.setParamsData(TemplateActivcity.this, page, catalogId);
        marketModel.setMarketModelInter(new MarketModel.MarketModelInter() {
                                            @Override
                                            public void onData(List<MarketParamsListData.PresetParmManageItem> mlist) {
                                                if (mlist != null) {
                                                    if (mlist.size() == 0) {
                                                        listend = true;
                                                        ToastUtil.toast(getResources().getString(R.string.none_more_data));
                                                    }
                                                    // TemplateActivcity.this.paramsList = mlist;
                                                  /*  if (mlist.size() < 10) {
                                                        listend = true;
                                                    } else {
                                                        listend = false;
                                                    }*/
                                                    if (paramsList == null) {
                                                        paramsList = mlist;
                                                    } else {
                                                        paramsList.addAll(mlist);
                                                    }
                                                    //marketParamsAdapter.notifyItemRemoved(marketParamsAdapter.getItemCount());
                                                   /* if (position!=-1&&first&&position<paramsList.size()) {
                                                        swipeRefreshLayout.setRefreshing(false);
                                                        marketParamsAdapter.setData(paramsList);

                                                        MoveToPosition(staggeredGridLayoutManager_params, position);
                                                        marketParamsAdapter.notifyDataSetChanged();
                                                        first = false;
                                                    }*/
                                                    Iterator<MarketParamsListData.PresetParmManageItem> itemIterator = paramsList.iterator();
                                                    String SelectCOBOX = LoginedUser.getLoginedUser().getSelect_market_cobox();
                                                    if (SelectCOBOX == null)
                                                        SelectCOBOX = "";
                                                    if (!SelectCOBOX.equals("")) {
                                                        while (itemIterator.hasNext()) {
                                                            MarketParamsListData.PresetParmManageItem presetParmManageItem = itemIterator.next();
                                                            if (!StringUtil.isBlank(presetParmManageItem.getCoboxName()) && !presetParmManageItem.getCoboxName().equals(SelectCOBOX))
                                                                itemIterator.remove();
                                                        }
                                                    }
                                                    swipeRefreshLayout.setRefreshing(false);
                                                    marketParamsAdapter.setData(paramsList);
                                                    marketParamsAdapter.notifyDataSetChanged();
                                                }
                                            }

                                            @Override
                                            public void onDataIndex(List<MarketParamsIndexListData.PresetParmIndexManageItem> list) {

                                            }
                                        }

        );
    }

    private void downloadParams(final int position, String fileId, String categoryId) {

        RetrofitFactory.getInstence().API().paramsDownload(new ParamsDownloadBody(fileId,categoryId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<ShareOwnerParmsData>(this,false) {
                    @Override
                    protected void onSuccees(BaseResult<ShareOwnerParmsData> t) throws Exception {
                        ShareOwnerParmsData shareOwnerParmsData = t.getData();
                        presetParm.setPresetId(shareOwnerParmsData.getFileId());
                        presetParm.setCategoryId(shareOwnerParmsData.getCatalogId());
                        presetParm.setCoverId(shareOwnerParmsData.getCoverId());
                        presetParmsSqlControl.startByFileId(shareOwnerParmsData.getFileId());
                        marketParamsAdapter.updateDownload(position);


                    }

                    @Override
                    protected void onCodeError(BaseResult<ShareOwnerParmsData> t) throws Exception {
                        if (t.getMessage().contains(TemplateActivcity.this.getResources().getString(R.string.token_fail))) {
                            ToastUtil.toast(t.getMessage());
                            return;
                        }
                        ToastUtil.toast(t.getMessage());
                        dissmissDialog();
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        ToastUtil.toast(e.getMessage());
                        dissmissDialog();
                    }
                });
    }


    private void getMarketPresetPams(final String fileId, final int position, final String categoryId, final String coverId) {
        showTransformDialog();
        SharePreferences preferences = SharePreferences.instance();
        boolean isFirstIn = preferences.getBoolean("isFirstIn", true);
        if (!isFirstIn) {

            RetrofitUploadFactory.getUPloadInstence().API().getLightData(fileId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new PresetInfoObserver<Preinstail>(TemplateActivcity.this,false) {
                        @Override
                        protected void onSuccees(Preinstail t) throws Exception {
                            if (t != null) {
                                Preinstail preinstail = t;
                                preinstail.setFileId(fileId);
                                preinstail.setCoverId(coverId);
                                presetParm = marketModel.transferImageData(preinstail);
                                downloadParams(position, fileId, categoryId);
                            } else {
                                ToastUtil.toast(getResources().getString(R.string.empty));
                                return;
                            }
                        }

                        @Override
                        protected void onCodeError(Preinstail t) throws Exception {
                            ToastUtil.toast(getResources().getString(R.string.download_error));
                            dissmissDialog();
                        }

                        @Override
                        protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                            ToastUtil.toast(e.getMessage());
                            dissmissDialog();
                        }
                    });
        }
    }
}
