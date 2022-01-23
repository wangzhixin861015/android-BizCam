package com.bcnetech.bcnetchhttp.base;

import android.accounts.NetworkErrorException;
import android.app.Activity;

import com.bcnetech.bcnetchhttp.R;
import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog3;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by yhf on 2018/9/12.
 */

public abstract class PresetInfoObserver<T> implements Observer<T> {

    protected Activity activity;
    protected boolean showDialog;
    private DGProgressDialog3 dialog3 = null;


    public PresetInfoObserver(Activity activity, boolean showDialog) {
        this.activity = activity;
        this.showDialog = showDialog;
        if (showDialog) {
            dialog3 = (new DGProgressDialog3(activity, false, progressTip()));
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        onRequestStart();
    }

    @Override
    public void onNext(T tBaseEntity) {
        onRequestEnd();
        if (activity.isFinishing()) {
            activity = null;
            return;
        }
//        if (tBaseEntity.isSuccess()) {
        try {
            onSuccees(tBaseEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        } else {
//            try {
//                onCodeError(tBaseEntity);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void onError(Throwable e) {
//        Log.w(TAG, "onError: ", );这里可以打印错误信息
        onRequestEnd();
        try {
            if (e instanceof ConnectException
                    || e instanceof TimeoutException
                    || e instanceof NetworkErrorException
                    || e instanceof UnknownHostException) {
                onFailure(e, true);
            } else {
                onFailure(e, false);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onComplete() {
    }

    /**
     * 返回成功
     *
     * @param t
     * @throws Exception
     */
    protected abstract void onSuccees(T t) throws Exception;

    /**
     * 返回成功了,但是code错误
     *
     * @param t
     * @throws Exception
     */
    protected abstract void onCodeError(T t) throws Exception;

    /**
     * 返回失败
     *
     * @param e
     * @param isNetWorkError 是否是网络错误
     * @throws Exception
     */
    protected abstract void onFailure(Throwable e, boolean isNetWorkError) throws Exception;

    protected void onRequestStart() {

        showProgressDialog();
    }

    protected void onRequestEnd() {
        closeProgressDialog();
    }

    public void showProgressDialog() {
        if(showDialog&&dialog3!=null){
            dialog3.show();
        }
    }

    public void closeProgressDialog() {
        if(dialog3!=null&&dialog3.isShowing()){
            dialog3.dismiss();
            dialog3=null;
        }
    }

    /**
     * 加载中的提示语
     *
     * @return
     */
    protected String progressTip() {
        return activity.getResources().getString(R.string.waiting_please);
    }

}
