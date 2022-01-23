package com.bcnetech.bcnetechlibrary.util.Watch;


/**
 * Created by wb on 2016/3/21.
 */
public interface Watcher {
    //当被观察者改变时观察者执行的操作
    public void updateNotify(Object... parms);
}