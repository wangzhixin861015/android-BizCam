package com.bcnetech.hyphoto.ui.fragment;

import android.app.Activity;
import android.content.Context;

import com.bcnetech.bcnetchhttp.RetrofitFactory;
import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.bean.request.CheckParamDeleteBody;
import com.bcnetech.bcnetchhttp.bean.response.MarketParamsIndexListData;
import com.bcnetech.bcnetchhttp.bean.response.MarketParamsListData;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.task.MBaseObserver;
import com.bcnetech.hyphoto.ui.activity.market.MarketFragmentActivity;
import com.bcnetech.hyphoto.utils.cache.CacheManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by dasu on 2017/4/22.
 */

public class MarketActivityController {

    private Context mContext;
    private MarketFragmentActivity marketFragmentActivity;

    public MarketActivityController(Context context) {
        if (!(context instanceof MarketFragmentActivity)) {
            throw new UnsupportedOperationException("绑定错误的Activity");
        }
        mContext = context;
        marketFragmentActivity = (MarketFragmentActivity) context;
    }

    /**
     * 获取分类列表
     */
    public void getReadingTypes() {
        final List<MarketParamsIndexListData.PresetParmIndexManageItem> data = (List<MarketParamsIndexListData.PresetParmIndexManageItem>) CacheManager.readObject(mContext, Flag.CACHE_TITLE);
        if (data != null && data.size() > 0) {
            if (data != null && !CacheManager.isCacheDataFailure(mContext, Flag.CACHE_TITLE)) {
                if (data.get(data.size() - 1).getId().equals("2")) {
                    data.remove(data.size() - 1);
                }
                String files = "";

                for (int k = 0; k < data.size(); k++) {
                    String mReadingType = data.get(k).getId();
                    List<MarketParamsListData.PresetParmManageItem> cahcedata = (List<MarketParamsListData.PresetParmManageItem>) CacheManager.readObject(mContext, mReadingType);
                    if (cahcedata != null && cahcedata.size() > 0) {
                        for (int m = 0; m < cahcedata.size(); m++) {
                            if (k == 0 && m == 0) {
                                files = cahcedata.get(m).getCategoryId() + "," + cahcedata.get(m).getId() + ";";
                            } else {
                                files = files + cahcedata.get(m).getCategoryId() + "," + cahcedata.get(m).getId() + ";";
                            }
                        }
                    }

                }

                RetrofitFactory.getInstence().API().checkParamDelete(new CheckParamDeleteBody(files))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MBaseObserver<MarketParamsListData>((Activity) mContext, false) {
                            @Override
                            protected void onSuccees(BaseResult<MarketParamsListData> marketParamsListDataBaseResult) throws Exception {
                                List<MarketParamsListData.PresetParmManageItem> fileInfomationList = marketParamsListDataBaseResult.getData().getList();
                                if (fileInfomationList.size() > 0) {

                                    for (int k = 0; k < data.size(); k++) {
                                        String mReadingType = data.get(k).getId();
                                        List<MarketParamsListData.PresetParmManageItem> cahcedata = (List<MarketParamsListData.PresetParmManageItem>) CacheManager.readObject(mContext, mReadingType);
                                        if (cahcedata != null && cahcedata.size() > 0) {
                                            for (int m = 0; m < cahcedata.size(); m++) {
                                                for (int t = 0; t < fileInfomationList.size(); t++) {
                                                    if (cahcedata.get(m).getId().equals(fileInfomationList.get(t).getFileId())) {
                                                        cahcedata.remove(m);
                                                    }
                                                }
                                            }
                                            CacheManager.saveObject(mContext, cahcedata, mReadingType);
                                        }
                                    }
                                }

                            }

                            @Override
                            protected void onCodeError(BaseResult<MarketParamsListData> t) throws Exception {
                                ToastUtil.toast(t.getMessage());
                            }

                            @Override
                            protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                                ToastUtil.toast(e.getMessage());
                            }
                        });


                marketFragmentActivity.updateTypes(data);
            } else {
                RetrofitFactory.getInstence().API().marketParamCatalog()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MBaseObserver<MarketParamsIndexListData>(marketFragmentActivity, false) {
                            @Override
                            protected void onSuccees(BaseResult<MarketParamsIndexListData> t) throws Exception {
                                if (t.getData().getList().get(0).getId().equals("2")) {
                                    t.getData().getList().remove(0);
                                } else if (t.getData().getList().get(t.getData().getList().size() - 1).getId().equals("2")) {
                                    t.getData().getList().remove(t.getData().getList().size() - 1);
                                }
                                CacheManager.saveObject(mContext, t.getData().getList(), Flag.CACHE_TITLE);
                                marketFragmentActivity.updateTypes(t.getData().getList());
                            }

                            @Override
                            protected void onCodeError(BaseResult<MarketParamsIndexListData> t) throws Exception {
                                ToastUtil.toast(t.getMessage());
                            }

                            @Override
                            protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                                ToastUtil.toast(e.getMessage());
                            }
                        });
            }

        }else {
            RetrofitFactory.getInstence().API().marketParamCatalog()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MBaseObserver<MarketParamsIndexListData>(marketFragmentActivity, false) {
                        @Override
                        protected void onSuccees(BaseResult<MarketParamsIndexListData> t) throws Exception {
                            if (t.getData().getList().get(0).getId().equals("2")) {
                                t.getData().getList().remove(0);
                            } else if (t.getData().getList().get(t.getData().getList().size() - 1).getId().equals("2")) {
                                t.getData().getList().remove(t.getData().getList().size() - 1);
                            }
                            CacheManager.saveObject(mContext, t.getData().getList(), Flag.CACHE_TITLE);
                            marketFragmentActivity.updateTypes(t.getData().getList());
                        }

                        @Override
                        protected void onCodeError(BaseResult<MarketParamsIndexListData> t) throws Exception {
                            ToastUtil.toast(t.getMessage());
                        }

                        @Override
                        protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                            ToastUtil.toast(e.getMessage());
                        }
                    });
        }
    }
}
