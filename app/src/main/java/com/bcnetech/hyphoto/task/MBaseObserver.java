package com.bcnetech.hyphoto.task;

import android.app.Activity;
import android.content.Intent;

import com.bcnetech.bcnetchhttp.base.BaseObserver;
import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.util.sharepreference.SharePreferences;
import com.bcnetech.hyphoto.task.manage.UploadManager;
import com.bcnetech.hyphoto.ui.activity.MainActivity;
import com.bcnetech.hyphoto.ui.activity.MainUsActivity;
import com.bcnetech.hyphoto.R;

import static com.bcnetech.bcnetchhttp.bean.response.LoginedUser.getLoginedUser;

/**
 * Created by yhf on 2018/9/18.
 */

public class MBaseObserver<T> extends BaseObserver<T> {

    public MBaseObserver(Activity activity, boolean showDialog) {
        super(activity, showDialog);
    }

    @Override
    protected void onSuccees(BaseResult<T> t) throws Exception {

    }

    @Override
    protected void onCodeError(BaseResult<T> t) throws Exception {


    }

    @Override
    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {

    }

    @Override
    protected void onTokenError(BaseResult<T> t) throws Exception {
        if (t.getCode() == 10010) {
            SharePreferences sp = SharePreferences.instance();
            sp.putBoolean("isFirstIn", true);
            //      CostomToastUtil.toast(activity.getResources().getString(R.string.token_fail));
//                        LoginPresenter.startAction(activity, Flag.TYPE_LOGIN);
            getLoginedUser().setLogined(false);
            getLoginedUser().quitLogin();
            UploadManager.getInstance().dellAllRunTask();
            Intent intent;
            if (Flag.isEnglish) {
                intent = new Intent(activity, MainUsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("isToken", true);
            } else {
                intent = new Intent(activity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("isToken", true);
            }
            ToastUtil.toast(activity.getResources().getString(R.string.token_fail));
            activity.startActivity(intent);
        }
    }
}
