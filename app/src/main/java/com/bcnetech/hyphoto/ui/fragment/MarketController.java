package com.bcnetech.hyphoto.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.bcnetech.bcnetchhttp.RetrofitFactory;
import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.bean.request.CheckParamDeleteBody;
import com.bcnetech.bcnetchhttp.bean.request.ParamsListBody;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.bean.response.MarketParamsListData;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.task.MBaseObserver;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.cache.CacheManager;

import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by dasu on 2017/4/22.
 */

public class MarketController {

    private static final String TAG = MarketController.class.getSimpleName();

    private Context mContext;
    private MarketFragment marketFragment;
    private String mReadingType;
    private int mCurPage;
    private List<MarketParamsListData.PresetParmManageItem> datalist;
    private boolean islistend = false;

    public MarketController(Fragment fragment) {
        if (!(fragment instanceof MarketFragment)) {
            throw new UnsupportedOperationException(TAG + "绑定错误的Fragment");
        }
        mContext = fragment.getContext();
        marketFragment = (MarketFragment) fragment;
        mReadingType = marketFragment.getReadingType();
        if (TextUtils.isEmpty(mReadingType)) {
            throw new UnsupportedOperationException("ReadingFragment 必须实现IReadingType接口，指定返回某一type类型");
        }
    }

    //根据分类id获取各页内容
    public void loadBaseData(boolean connect) {
        islistend = false;
        mCurPage = 1;
        List<MarketParamsListData.PresetParmManageItem> cahcedata = (List<MarketParamsListData.PresetParmManageItem>) CacheManager.readObject(mContext, mReadingType);
        if (cahcedata != null && !connect && !CacheManager.isCacheDataFailure(mContext, mReadingType)) {
            checkDataDeleted(cahcedata);
           /* marketFragment.updateData(cahcedata);
            datalist = cahcedata;
            mCurPage = (int) Math.ceil((double) datalist.size() / 10);
            marketFragment.updateCurPage(mCurPage);*/
        } else {
            String SelectCOBOX = LoginedUser.getLoginedUser().getSelect_market_cobox();

            int pageSize=getPageSize(mContext);

            RetrofitFactory.getInstence().API().paramsList(new ParamsListBody(mCurPage + "", pageSize+"",mReadingType))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MBaseObserver<MarketParamsListData>((Activity) mContext, false) {
                        @Override
                        protected void onSuccees(BaseResult<MarketParamsListData> t) throws Exception {
                            marketFragment.updateData(t.getData().getList());
                            datalist = t.getData().getList();
                            marketFragment.updateCurPage(1);
                            CacheManager.saveObject(mContext, t.getData().getList(), mReadingType);
                        }

                        @Override
                        protected void onCodeError(BaseResult<MarketParamsListData> t) throws Exception {
                            marketFragment.onLoadFailed();
                            ToastUtil.toast(t.getMessage());
                        }

                        @Override
                        protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                            marketFragment.onLoadFailed();
                            ToastUtil.toast(e.getMessage());
                        }
                    });


//            MarketParamsTask = new MarketParamsTask((Activity) mContext, false);
//            MarketParamsTask.setAsyncTaskSuccessCallback(new AsyncTaskSuccessCallback<List<PresetParmManageItem>>() {
//                @Override
//                public void successCallback(Result<List<PresetParmManageItem>> result) {
//                    marketFragment.updateData(result.getValue());
//                    datalist = result.getValue();
//                    marketFragment.updateCurPage(1);
//                    CacheManager.saveObject(mContext, result.getValue(), mReadingType);
//                }
//            });
//            MarketParamsTask.setAsyncTaskFailCallback(new AsyncTaskFailCallback<List<PresetParmManageItem>>() {
//                @Override
//                public void failCallback(Result<List<PresetParmManageItem>> result) {
//                    marketFragment.onLoadFailed();
//                    ToastUtil.toast(result.getMessage());
//                }
//            });
//                MarketParamsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mCurPage, mReadingType, SelectCOBOX);
        }
    }

    private static final int STATE_REFRESH_END = 1;
    private static final int STATE_REFRESHING = 2;

    private int mRefreshState = STATE_REFRESH_END;
    private int mCategoryPage = 1;

    public void startPullUpRefresh() {
        if (mRefreshState == STATE_REFRESHING) {
            return;
        }
        mRefreshState = STATE_REFRESHING;

        int pageSize=getPageSize(mContext);

        String SelectCOBOX = LoginedUser.getLoginedUser().getSelect_market_cobox();
        RetrofitFactory.getInstence().API().paramsList(new ParamsListBody(++mCurPage + "",pageSize+"", mReadingType))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<MarketParamsListData>((Activity) mContext, false) {
                    @Override
                    protected void onSuccees(BaseResult<MarketParamsListData> t) throws Exception {
                        mRefreshState = STATE_REFRESH_END;
                        if (t != null && t.getData().getList().size() > 0) {
                            marketFragment.refreshData(t.getData().getList());
                            datalist = t.getData().getList();
                            islistend = false;
                        } else {
                            marketFragment.onLoadFailed();
                            islistend = true;
                        }
                    }

                    @Override
                    protected void onCodeError(BaseResult<MarketParamsListData> t) throws Exception {
                        mRefreshState = STATE_REFRESH_END;
                        marketFragment.onLoadFailed();
                        islistend = true;
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        mRefreshState = STATE_REFRESH_END;
                        marketFragment.onLoadFailed();
                        islistend = true;
                    }
                });




//        MarketParamsTask = new MarketParamsTask((Activity) mContext, false);
//        MarketParamsTask.setAsyncTaskSuccessCallback(new AsyncTaskSuccessCallback<List<PresetParmManageItem>>() {
//            @Override
//            public void successCallback(Result<List<PresetParmManageItem>> result) {
//                mRefreshState = STATE_REFRESH_END;
//                if (result != null && result.getValue().size() > 0) {
//                    marketFragment.refreshData(result.getValue());
//                    datalist = result.getValue();
//                    islistend = false;
//                } else {
//                    marketFragment.onLoadFailed();
//                    islistend = true;
//                }
//            }
//        });
//        MarketParamsTask.setAsyncTaskFailCallback(new AsyncTaskFailCallback<List<PresetParmManageItem>>() {
//            @Override
//            public void failCallback(Result<List<PresetParmManageItem>> result) {
//                mRefreshState = STATE_REFRESH_END;
//                marketFragment.onLoadFailed();
//                islistend = true;
//            }
//        });
//        String SelectCOBOX = LoginedUser.getLoginedUser().getSelect_market_cobox();
//        MarketParamsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ++mCurPage, mReadingType, SelectCOBOX);
    }

    public void checkDataDeleted(final List<MarketParamsListData.PresetParmManageItem> mdatalist) {
        if (mdatalist != null && mdatalist.size() != 0) {
            StringBuilder checkfile = new StringBuilder();
            for (int i = 0; i < mdatalist.size(); i++) {
                checkfile.append(mdatalist.get(i).getCategoryId() + "," + mdatalist.get(i).getId() + ";");
            }
            String checkfilefin = checkfile.substring(0, checkfile.length() - 1);

            RetrofitFactory.getInstence().API().checkParamDelete(new CheckParamDeleteBody(checkfilefin))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MBaseObserver<MarketParamsListData>((Activity) mContext,false) {
                        @Override
                        protected void onSuccees(BaseResult<MarketParamsListData> t) throws Exception {
                            if (t.getData() == null || t.getData().getList().isEmpty()) {
                            } else {
                                for (MarketParamsListData.PresetParmManageItem presetParmManageItem : t.getData().getList()) {
                                    Iterator<MarketParamsListData.PresetParmManageItem> iterator = mdatalist.iterator();
                                    while (iterator.hasNext()) {
                                        MarketParamsListData.PresetParmManageItem p = iterator.next();
                                        if (presetParmManageItem.getFileId() != null && presetParmManageItem.getCatalogId() != null) {
                                            if (p.getId().equals(presetParmManageItem.getFileId()) && p.getCategoryId().equals(presetParmManageItem.getCatalogId())) {
                                                iterator.remove();
                                            }
                                        }
                                    }
                                    CacheManager.saveObject(mContext, mdatalist, mReadingType);
                                }
                            }
                            if (mdatalist != null && !mdatalist.isEmpty()) {
                                marketFragment.updateData(mdatalist);
                                datalist = mdatalist;
                                mCurPage = (int) Math.ceil((double) datalist.size() / 10);
                                marketFragment.updateCurPage(mCurPage);
                            }
                        }

                        @Override
                        protected void onCodeError(BaseResult<MarketParamsListData> t) throws Exception {
                            if (mdatalist != null && !mdatalist.isEmpty()) {
                                marketFragment.updateData(mdatalist);
                                datalist = mdatalist;
                                mCurPage = (int) Math.ceil((double) datalist.size() / 10);
                                marketFragment.updateCurPage(mCurPage);
                            }
                        }

                        @Override
                        protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                            if (mdatalist != null && !mdatalist.isEmpty()) {
                                marketFragment.updateData(mdatalist);
                                datalist = mdatalist;
                                mCurPage = (int) Math.ceil((double) datalist.size() / 10);
                                marketFragment.updateCurPage(mCurPage);
                            }
                        }
                    });



//            CheckParamsTask checkParamsTask = new CheckParamsTask((Activity) mContext, false);
//            checkParamsTask.setAsyncTaskSuccessCallback(new AsyncTaskSuccessCallback<List<PresetParmManageItem>>() {
//                @Override
//                public void successCallback(Result<List<PresetParmManageItem>> result) {
//                    if (result.getValue() == null || result.getValue().isEmpty()) {
//                    } else {
//                        for (MarketParamsListData.PresetParmManageItem presetParmManageItem : result.getValue()) {
//                            Iterator<MarketParamsListData.PresetParmManageItem> iterator = mdatalist.iterator();
//                            while (iterator.hasNext()) {
//                                MarketParamsListData.PresetParmManageItem p = iterator.next();
//                                if (presetParmManageItem.getFileId() != null && presetParmManageItem.getCatalogId() != null) {
//                                    if (p.getId().equals(presetParmManageItem.getFileId()) && p.getCategoryId().equals(presetParmManageItem.getCatalogId())) {
//                                        iterator.remove();
//                                    }
//                                }
//                            }
//                            CacheManager.saveObject(mContext, mdatalist, mReadingType);
//                        }
//                    }
//                    if (mdatalist != null && !mdatalist.isEmpty()) {
//                        marketFragment.updateData(mdatalist);
//                        datalist = mdatalist;
//                        mCurPage = (int) Math.ceil((double) datalist.size() / 10);
//                        marketFragment.updateCurPage(mCurPage);
//                    }
//                }
//            });
//            checkParamsTask.setAsyncTaskFailCallback(new AsyncTaskFailCallback<List<PresetParmManageItem>>() {
//                @Override
//                public void failCallback(Result<List<PresetParmManageItem>> result) {
//                    if (mdatalist != null && !mdatalist.isEmpty()) {
//                        marketFragment.updateData(mdatalist);
//                        datalist = mdatalist;
//                        mCurPage = (int) Math.ceil((double) datalist.size() / 10);
//                        marketFragment.updateCurPage(mCurPage);
//                    }
//                }
//            });
//            checkParamsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, checkfilefin);
        }
    }

    public boolean Islistend() {
        return this.islistend;
    }

    private int getPageSize(Context activity){
        int screenHeight=ContentUtil.getScreenHeight2(activity);
        int h=(ContentUtil.getScreenWidth(activity) - ContentUtil.dip2px(activity, 3)) / 3;
        int pageSize=((screenHeight- ImageUtil.Dp2Px(activity,200))/h+1)*3;
        return pageSize;
    }

}
