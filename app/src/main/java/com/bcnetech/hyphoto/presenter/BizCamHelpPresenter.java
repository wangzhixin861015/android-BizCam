package com.bcnetech.hyphoto.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bcnetech.hyphoto.presenter.iview.IBizCamHelpView;
import com.bcnetech.hyphoto.ui.activity.personCenter.BizCamHelpActivity;

/**
 * Created by yhf on 2018/10/17.
 */

public class BizCamHelpPresenter extends BasePresenter<IBizCamHelpView> {

    /***
     * @param activity
     */
    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, BizCamHelpActivity.class);
        activity.startActivity(intent);

    }
    @Override
    public void onCreate(Bundle bundle) {

    }

    @Override
    public void onDestroy() {

    }
}