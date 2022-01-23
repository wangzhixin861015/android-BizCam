package com.bcnetech.hyphoto.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bcnetech.hyphoto.presenter.iview.IUploadManagerView;
import com.bcnetech.hyphoto.ui.activity.personCenter.UploadManagerActivity;

/**
 * Created by yhf on 2017/4/14.
 */

public class UploadManagerPresenter extends BasePresenter<IUploadManagerView>  {

    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, UploadManagerActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle bundle) {

    }

    @Override
    public void onDestroy() {

    }
}
