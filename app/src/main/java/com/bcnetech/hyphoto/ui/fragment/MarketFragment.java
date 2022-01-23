package com.bcnetech.hyphoto.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bcnetech.bcnetchhttp.bean.response.MarketParamsListData;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.ui.activity.market.TemplateActivcity;
import com.bcnetech.hyphoto.ui.adapter.MarketParamsAdapter2;
import com.bcnetech.hyphoto.ui.view.recyclerview.LoadMoreRecyclerView;
import com.bcnetech.hyphoto.ui.view.recyclerview.OnPullUpRefreshListener;
import com.bcnetech.hyphoto.utils.SpacesItemDecoration;
import com.bcnetech.hyphoto.utils.cache.CacheManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by a1234 on 2017/7/19.
 */

public class MarketFragment extends BaseMarketFragment implements IMarketController {
    //刷新的状态
    private static final int STATE_REFRESHING = 1;
    private static final int STATE_REFRESH_FINISH = 2;
    public static final int RESQUST_CODE = 5;
    private int mRefreshState = STATE_REFRESH_FINISH;
    private OnSwipeRefreshListener mRefreshListener;
    private MarketController marketController;
    private Context mContext;
    private String catalogTitle;
    private String catalogId;

    private LoadMoreRecyclerView recycler_view;
    private boolean listend;
    private MarketParamsAdapter2 marketParamsAdapter;
    // private List<PresetParmManageItem> paramsList;
    private StaggeredGridLayoutManager staggeredGridLayoutManager_params;
    private int topRowVerticalPosition;
    private boolean isSeleted;//判断是否通过筛选后刷新过数据

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnSwipeRefreshListener) {
            mRefreshListener = (OnSwipeRefreshListener) context;
        }
    }


    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager_listview, container, false);
        initView(view);
        return view;
    }

    private List<MarketParamsListData.PresetParmManageItem> mBlogList = new ArrayList<>();
    private int mCurPage = 1;


    protected void initView(View view) {
        recycler_view = (LoadMoreRecyclerView) view.findViewById(R.id.recycler_view);
        SpacesItemDecoration decoration = new SpacesItemDecoration(6, SpacesItemDecoration.TYPE_MARKET);
        recycler_view.addItemDecoration(decoration);
        staggeredGridLayoutManager_params = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recycler_view.setLayoutManager(staggeredGridLayoutManager_params);
        recycler_view.setHasFixedSize(true);
        marketParamsAdapter = new MarketParamsAdapter2(getActivity(), mBlogList);
        recycler_view.setAdapter(marketParamsAdapter);

        recycler_view.setOnPullUpRefreshListener(onPullUpRefresh());
        marketParamsAdapter.setOnItemClickListener(new MarketParamsAdapter2.ItemClickListener() {
            @Override
            public void OnItemClick(View v, final int position) {


                Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {

                        boolean isSuccess=CacheManager.saveObject(getContext(),mBlogList,"Parmlist");


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
                                    Intent intent = new Intent(getActivity(), TemplateActivcity.class);
                                    intent.setAction("preparm");
                                    intent.putExtra("position", position);
                                    intent.putExtra("catalogTitle", catalogTitle);
                                    intent.putExtra("catalogId", catalogId);
                                    getActivity().startActivityForResult(intent, RESQUST_CODE);
                                }else {
                                    ToastUtil.toast(getActivity().getResources().getString(R.string.error));
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







                //TemplateActivcity.actionStart(getActivity(), position, catalogId, catalogTitle, mBlogList);
                //PresetParmManageItem presetParmManageItem = paramsList.get(position);
                //PresetDownloadActivity.actionStart(MarketPresetActicity.this, presetParmManageItem.getId(), presetParmManageItem.getDownloadCount(), presetParmManageItem.getImageWidth(), presetParmManageItem.getImageHeight(), presetParmManageItem.getCoboxName(),presetParmManageItem.getCategoryId());
            }
        });
        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mRefreshListener.onlistTop(topRowVerticalPosition);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    //分类id
    public MarketFragment setArguments(String catalogId, String title) {
        Bundle bundle = new Bundle();
        bundle.putString("_type", catalogId);
        setArguments(bundle);
        this.catalogId = catalogId;
        this.catalogTitle = title;
        return this;
    }

    @Override
    public String getReadingType() {
        Bundle bundle = getArguments();
        return bundle.getString("_type", "");
    }

    private void notifyDataSetChanged() {
        if (marketParamsAdapter != null) {
            marketParamsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateData(List<MarketParamsListData.PresetParmManageItem> data) {
        Iterator<MarketParamsListData.PresetParmManageItem> itemIterator = data.iterator();
        mRefreshState = STATE_REFRESH_FINISH;
        mRefreshListener.onRefreshFinish();
       /* if (data == null || data.size() == 0) {
            return;
        }*/
        if (mBlogList == null) {
            mBlogList = new ArrayList<>();
        }
        mBlogList.clear();
        mBlogList.addAll(data);

        if (isFragmentVisible()) {
            recycler_view.smoothScrollToPosition(0);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onLoadFailed() {
        mRefreshState = STATE_REFRESH_FINISH;
        mRefreshListener.onRefreshFinish();
        ToastUtil.toast(getResources().getString(R.string.none_more_data));
    }


    @Override
    public void updateCurPage(int page) {
        mCurPage = page;
        if (marketParamsAdapter != null) {
            marketParamsAdapter.updateCurPage(page);
        }
    }

    public void retryLoadData() {
        isSeleted = true;
        mRefreshState = STATE_REFRESHING;
        if (marketController!=null) {
            marketController.loadBaseData(true);
        }
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible) {
            //recycler_view.smoothScrollToPosition(0);
            // marketController.checkDataDeleted(mBlogList);
            if (topRowVerticalPosition != 0) {
                mRefreshListener.onlistTop(topRowVerticalPosition);
            }
            notifyDataSetChanged();
            if (mRefreshState == STATE_REFRESHING) {
                mRefreshListener.onRefreshing();
            }
        } else {
            mRefreshState = STATE_REFRESH_FINISH;
            mRefreshListener.onRefreshFinish();
            // mRefreshListener.onlistTop(-1);
        }
    }

    @Override
    protected void onFragmentFirstVisible() {
        marketController = new MarketController(this);
        mRefreshState = STATE_REFRESHING;
        marketController.loadBaseData(false);
    }

    private OnPullUpRefreshListener onPullUpRefresh() {
        return new OnPullUpRefreshListener() {
            @Override
            public void onPullUpRefresh() {
                //正在刷新的话，就不加载下拉刷新了
                if (mRefreshState == STATE_REFRESHING) {
                    return;
                }
                mRefreshState = STATE_REFRESHING;
                mRefreshListener.onRefreshing();
                if (!marketController.Islistend()) {
                    marketController.startPullUpRefresh();
                } else {
                    mRefreshListener.onRefreshFinish();
                    ToastUtil.toast(getResources().getString(R.string.none_more_data));
                }

            }
        };
    }

    public void refreshData(List<MarketParamsListData.PresetParmManageItem> data) {
        Iterator<MarketParamsListData.PresetParmManageItem> itemIterator = data.iterator();

        mRefreshState = STATE_REFRESH_FINISH;
        mRefreshListener.onRefreshFinish();
        if (mBlogList == null) {
            mBlogList = new ArrayList<>();
        }
        int oldSize = mBlogList.size();
        mBlogList.addAll(data);
       // CacheManager.saveObject(mContext, mBlogList, this.getReadingType());
        marketParamsAdapter.notifyItemRangeInserted(oldSize, data.size());
    }

    public void updateCurrentFragment() {
        mBlogList = (List<MarketParamsListData.PresetParmManageItem>) CacheManager.readObject(mContext, this.getReadingType());
        marketParamsAdapter.setData(mBlogList);
        marketParamsAdapter.notifyDataSetChanged();
    }

    public boolean isSeleted() {
        return isSeleted;
    }

    public void setSeleted(boolean seleted) {
        isSeleted = seleted;
    }

}
