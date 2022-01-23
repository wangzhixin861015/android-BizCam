package com.bcnetech.hyphoto.task;



/**
 * Created by wenbin on 16/5/4.
 */
public interface AsyncTaskSuccessCallback<T> {
    public void successCallback(Result<T> result);
}
