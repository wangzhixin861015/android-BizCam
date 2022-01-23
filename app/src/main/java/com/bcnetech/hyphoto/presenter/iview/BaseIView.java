package com.bcnetech.hyphoto.presenter.iview;

import android.content.Intent;

/**
 * Created by wenbin on 2017/2/17.
 */

public interface BaseIView {
    void showLoading();
    void hideLoading();
    void finishView(int resultCode,Intent intent);
}
