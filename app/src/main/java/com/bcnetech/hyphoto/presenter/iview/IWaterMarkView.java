package com.bcnetech.hyphoto.presenter.iview;

import com.bcnetech.hyphoto.ui.view.WMFlowLayout;

/**
 * author: wsBai
 * date: 2019/1/21
 */
public interface IWaterMarkView extends BaseIView {
    WMFlowLayout getWMFlowLayout();

    void showMyWaterMark(final String path);
}
