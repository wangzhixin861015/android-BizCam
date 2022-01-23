package com.bcnetech.hyphoto.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bcnetech.hyphoto.presenter.iview.IBizCamHelpDetailView;
import com.bcnetech.hyphoto.ui.activity.personCenter.BizCamHelpDetailActivity;

/**
 * Created by yhf on 2018/10/17.
 */

public class BizCamHelpDetailPresenter extends BasePresenter<IBizCamHelpDetailView> {

    /***
     * @param activity
     */
    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, BizCamHelpDetailActivity.class);
        activity.startActivity(intent);

    }
    @Override
    public void onCreate(Bundle bundle) {

    }

    @Override
    public void onDestroy() {

    }
}
