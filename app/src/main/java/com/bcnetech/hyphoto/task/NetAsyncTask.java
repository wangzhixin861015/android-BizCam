package com.bcnetech.hyphoto.task;

import android.app.Activity;
import android.os.AsyncTask;

import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog;
import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog3;


/**
 * Created by wenbin on 16/5/4.
 */
public abstract class NetAsyncTask<T> extends AsyncTask<Object, Object, Result<T>> {
    //后面尖括号内分别是参数（线程休息时间），进度(publishProgress用到)，返回值 类型

    private Activity activity = null;
    private AsyncTaskSuccessCallback<T> asyncTaskSuccessCallback;
    private AsyncTaskFailCallback<T> asyncTaskFailCallback;
    private DGProgressDialog dialog = null;
    private DGProgressDialog3 dialog3 = null;
    private boolean showDialog = true;

    public NetAsyncTask(Activity activity) {
        this.activity = activity;
    }


    /*
     * 第一个执行的方法
     * 执行时机：在执行实际的后台操作前，被UI 线程调用
     * 作用：可以在该方法中做一些准备工作，如在界面上显示一个进度条，或者一些控件的实例化，这个方法可以不用实现。
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        if (activity.isFinishing()) {
            return;
        }
        if (showDialog && dialog != null) {
            dialog.show();
            //dialog.setType(DGProgressDialog2.TYPE_LOADING);
        }
        if (showDialog && dialog3 != null) {
            dialog3.show();
            //dialog.setType(DGProgressDialog2.TYPE_LOADING);
        }
    }

    protected void setProgressDialog(DGProgressDialog dialog) {
        this.dialog = dialog;
    }

    protected void setProgressDialog3(DGProgressDialog3 dialog) {
        this.dialog3 = dialog;
    }

    /*
     * 执行时机：在onPreExecute 方法执行后马上执行，该方法运行在后台线程中
     * 作用：主要负责执行那些很耗时的后台处理工作。可以调用 publishProgress方法来更新实时的任务进度。该方法是抽象方法，子类必须实现。
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Result<T> doInBackground(Object... params) {
        return onHttpRequestBase(params);
    }
    /*
     * 执行时机：这个函数在doInBackground调用publishProgress时被调用后，UI 线程将调用这个方法.虽然此方法只有一个参数,但此参数是一个数组，可以用values[i]来调用
     * 作用：在界面上展示任务的进展情况，例如通过一个进度条进行展示。此实例中，该方法会被执行100次
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
    }


    /*
     * 执行时机：在doInBackground 执行完成后，将被UI 线程调用
     * 作用：后台的计算结果将通过该方法传递到UI 线程，并且在界面上展示给用户
     * result:上面doInBackground执行后的返回值，所以这里是"执行完毕"
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */

    @Override
    protected void onPostExecute(Result<T> o) {
        super.onPostExecute(o);
        if (activity.isFinishing()) {
            return;
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        if (o.isSuccess()) {
           /* if(showDialog&&dialog!=null) {
                dialog.setType(DGProgressDialog2.TYPE_SUCCESS);
            }*/

            if (dialog3 != null && dialog3.isShowing()) {
                dialog3.dismiss();
                dialog3 = null;
            }

            asyncTaskSuccessCallback.successCallback(o);
        } else {
            if (null != dialog3) {
                dialog3.setNetAsyncTask(new DGProgressDialog3.NetAsyncTask() {
                    @Override
                    public void animationEnd() {
                        dialog3.dismiss();
                        dialog3 = null;
                    }
                }, o.getCode());
            }
            asyncTaskFailCallback.failCallback(o);
        }
    }

    public void setShowProgressDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    public void setAsyncTaskSuccessCallback(AsyncTaskSuccessCallback<T> asyncTaskSuccessCallback) {
        this.asyncTaskSuccessCallback = asyncTaskSuccessCallback;
    }

    public void setAsyncTaskFailCallback(AsyncTaskFailCallback<T> asyncTaskFailCallback) {
        this.asyncTaskFailCallback = asyncTaskFailCallback;

    }

    protected abstract Result<T> onHttpRequestBase(Object... objects);


}
