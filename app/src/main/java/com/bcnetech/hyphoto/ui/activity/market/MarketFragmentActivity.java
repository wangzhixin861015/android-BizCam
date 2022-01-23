package com.bcnetech.hyphoto.ui.activity.market;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.bean.response.MarketParamsIndexListData;
import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.bcnetechlibrary.util.EventStatisticsUtil;
import com.bcnetech.hyphoto.data.PopSelectData;
import com.bcnetech.hyphoto.ui.adapter.MarketPagerAdapter;
import com.bcnetech.hyphoto.ui.adapter.MarketParamsTitleAdapter;
import com.bcnetech.hyphoto.ui.fragment.MarketActivityController;
import com.bcnetech.hyphoto.ui.fragment.MarketFragment;
import com.bcnetech.hyphoto.ui.fragment.OnSwipeRefreshListener;
import com.bcnetech.hyphoto.ui.popwindow.MarketFilterPop;
import com.bcnetech.hyphoto.ui.view.SwipeRefreshPagerLayout;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.utils.cache.CacheManager;
import com.bcnetech.hyphoto.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a1234 on 2017/8/7.
 * <p>
 * 该类主要管理着多个Fragment，页面具体Ui由Fragment实现
 */

public class MarketFragmentActivity extends BaseActivity implements OnSwipeRefreshListener {
    private MarketFilterPop marketFilterPop;
    private String selectCOBOX = "";
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_fragment);
        initView();
        initData();
        initVariable();
        // showDialog();
        mReadingController.getReadingTypes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private MarketActivityController mReadingController;
    private MarketPagerAdapter mPagerAdapter;

    private void initVariable() {
        mReadingController = new MarketActivityController(this);
    }

    //private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SwipeRefreshLayout mRefreshLayout;
    private ViewGroup mContentLayout;
    private RecyclerView rec_title;//分类总列表view
    private MarketParamsTitleAdapter titleadapter;
    private List<MarketParamsIndexListData.PresetParmIndexManageItem> indexparamsList;
    private TitleView titleview;

    private SwipeRefreshLayout.OnRefreshListener onPullDownRefresh() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPagerAdapter){
                    getCurRefreshFragment().retryLoadData();
                }
            }
        };
    }


    private MarketFragment getCurRefreshFragment() {
        return (MarketFragment) mPagerAdapter.getCurrentFragment();
    }


    public void updateTypes(List<MarketParamsIndexListData.PresetParmIndexManageItem> data) {
        //dissmissDialog();
        if (data == null || data.size() == 0) {
            return;
        }
        this.indexparamsList = data;
        if (mPagerAdapter == null) {
            mPagerAdapter = new MarketPagerAdapter(getSupportFragmentManager(), data);
            mViewPager.setAdapter(mPagerAdapter);
            titleadapter = new MarketParamsTitleAdapter(MarketFragmentActivity.this, data);
            rec_title.setAdapter(titleadapter);
            ((SimpleItemAnimator) rec_title.getItemAnimator()).setSupportsChangeAnimations(false);

            ScrollSpeedLinearLayoutManger linearLayoutManager = new ScrollSpeedLinearLayoutManger(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rec_title.setLayoutManager(linearLayoutManager);

            mPagerAdapter.notifyDataSetChanged();
            onViewClick();
        }
    }

    @Override
    public void onRefreshing() {
        if (mRefreshLayout != null && !mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void onRefreshFinish() {
        if (mRefreshLayout != null && mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
    }

    protected void onViewClick() {
        if (titleadapter != null) {
            titleadapter.setOnItemClickListener(new MarketParamsTitleAdapter.ItemClickListener() {
                @Override
                public void OnItemClick(View v, int position) {
                    MarketParamsIndexListData.PresetParmIndexManageItem item = indexparamsList.get(position);
                    EventStatisticsUtil.event(MarketFragmentActivity.this, item.getId());

                  //  setTitleClickMove(position);
             /*       //刷新下方recycleview
                    try {
                       // Field field = mViewPager.getClass().getField("mCurItem");
                       // field.setAccessible(true);
                       // field.setInt(mViewPager, position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
// 通过数据修改
                    mPagerAdapter.notifyDataSetChanged();
// 切换到指定页面
                    mViewPager.setCurrentItem(position);
                }
            });
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MarketParamsIndexListData.PresetParmIndexManageItem item = indexparamsList.get(position);
                EventStatisticsUtil.event(MarketFragmentActivity.this, item.getId());
                titleadapter.setClickPosition(position);
                setTitleClickMove(position);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!getCurRefreshFragment().isSeleted()) {
                            mRefreshLayout.setRefreshing(true);
                            getCurRefreshFragment().retryLoadData();
                        }
                    }
                }, 400);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });

        titleview.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleview.setRightTextListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilter();
            }
        });
    }

    protected void initData() {
        final ArrayList<PopSelectData> list = new ArrayList<>();
        String SelectCOBOX = LoginedUser.getLoginedUser().getSelect_market_cobox();
        if (SelectCOBOX == null)
            SelectCOBOX = "";
        list.add(new PopSelectData(getResources().getString(R.string.cobox_all), SelectCOBOX.equals("")));
        list.add(new PopSelectData(getResources().getString(R.string.cobox_s1), SelectCOBOX.equals("Cobox S1")));
        list.add(new PopSelectData(getResources().getString(R.string.cobox_s2), SelectCOBOX.equals("Cobox S2")));
        marketFilterPop = new MarketFilterPop(this, list);
        marketFilterPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                int position = marketFilterPop.getSelectType();
                selectList(list.get(position).getSeletContent());
            }
        });
    }

    protected void initView() {
        //mTabLayout = (TabLayout) findViewById(R.id.layout_reading_title);
        mViewPager = (ViewPager) findViewById(R.id.vp_reading_content);
        mContentLayout = (ViewGroup) findViewById(R.id.layout_content);
        mRefreshLayout = (SwipeRefreshPagerLayout) findViewById(R.id.layout_reading_content);
        mRefreshLayout.setOnRefreshListener(onPullDownRefresh());
        rec_title = (RecyclerView) findViewById(R.id.rec_title);
        titleview = (TitleView) findViewById(R.id.title_layout);
        titleview.setType(TitleView.PRESET_MARKER);
        titleview.setRightTextColor(Color.BLUE);
        titleview.setTitleText(getResources().getString(R.string.preparm_market));
    }

    @Override
    public void onlistTop(int top) {
        mRefreshLayout.setEnabled(top >= 0);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == resultCode && resultCode == MarketFragment.RESQUST_CODE) {
            boolean clicksave = data.getBooleanExtra("clicksave", false);
            if (clicksave) {
                mPagerAdapter.updateCurrentFragment();
            }
        }
    }

    public void setTitleClickMove(int position) {
        RecyclerView.LayoutManager layoutManager = rec_title.getLayoutManager();
        RecyclerView.State state = new RecyclerView.State();
        if (layoutManager instanceof ScrollSpeedLinearLayoutManger) {
            ScrollSpeedLinearLayoutManger linearManager = (ScrollSpeedLinearLayoutManger) layoutManager;
            if (position < indexparamsList.size()) {
                //获取最后一个可见view的位置
                int lastItemPositionComplete = linearManager.findLastCompletelyVisibleItemPosition();
                int lastItemPosition = linearManager.findLastVisibleItemPosition();
                //获取第一个可见view的位置
                int firstItemPositionComplete = linearManager.findFirstCompletelyVisibleItemPosition();
                int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                if (position != 0) {
                    if (position == firstItemPosition || position == firstItemPositionComplete) {
                        layoutManager.smoothScrollToPosition(rec_title, state, position - 1);
                    } else if (position == lastItemPosition || position == lastItemPositionComplete) {
                        layoutManager.smoothScrollToPosition(rec_title, state, position + 1);
                    }
                } else {
                    //初始值位置
                    layoutManager.smoothScrollToPosition(rec_title, state, position);
                }
                // layoutManager.smoothScrollToPosition(rec_title, state, position);
            }
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_anim_in, R.anim.cache_anim);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.activity_anim_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.activity_anim_in, R.anim.cache_anim);
    }

    private void showFilter() {
        marketFilterPop.showPop(this.getWindow().getDecorView());
    }

    private void selectList(String content) {
        if (content.equals(getResources().getString(R.string.cobox_all))) {
            selectCOBOX = "";
        } else if (content.equals(getResources().getString(R.string.cobox_s1))) {
            selectCOBOX = "Cobox S1";

        } else if (content.equals(getResources().getString(R.string.cobox_s2))) {
            selectCOBOX = "Cobox S2";
        }
        LoginedUser loginedUser = LoginedUser.getLoginedUser();
        CacheManager.DeleteAllCache(MarketFragmentActivity.this);
        loginedUser.setSelect_market_cobox(selectCOBOX);
        LoginedUser.setLoginedUser(loginedUser);
        LoginedUser.saveToFile();
        mPagerAdapter.selectAllFragment();
        mRefreshLayout.setRefreshing(true);
        getCurRefreshFragment().retryLoadData();
    }

    public String getSelectCOBOX() {
        return selectCOBOX;
    }
}
