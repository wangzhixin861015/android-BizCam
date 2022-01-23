package com.bcnetech.hyphoto.task;

/**
 * Created by wenbin on 16/5/4.
 */
public interface AsyncTaskFailCallback<T> {
    public void failCallback(Result<T> result);
}
