package com.bcnetech.bcnetechlibrary.util.Watch;

/**
 * Created by wb on 2016/3/21.
 */
public interface Watched {
    //添加观察者
    public void add(Watcher watcher);
    //移除观察者
    public void remove(Watcher watcher);
    public void removeAll();
    //通知所有观察者
    public void notifyWatcher(Object... parms);
}
