package com.bcnetech.hyphoto.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bcnetech.hyphoto.presenter.iview.IBizCamUToView;
import com.bcnetech.hyphoto.ui.activity.personCenter.BizCamUToActivity;

/**
 * Created by yhf on 2018/10/17.
 */

public class BizCamUToPresenter extends BasePresenter<IBizCamUToView> {

    /***
     * @param activity
     */
    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, BizCamUToActivity.class);
        activity.startActivity(intent);

    }
    @Override
    public void onCreate(Bundle bundle) {

    }

    @Override
    public void onDestroy() {

    }
}
