package com.bcnetech.hyphoto.ui.fragment;



import com.bcnetech.bcnetchhttp.bean.response.MarketParamsListData;

import java.util.List;



interface IMarketController {

    String getReadingType();

    void updateData(List<MarketParamsListData.PresetParmManageItem> data);

    void onLoadFailed();

    void updateCurPage(int page);

}
