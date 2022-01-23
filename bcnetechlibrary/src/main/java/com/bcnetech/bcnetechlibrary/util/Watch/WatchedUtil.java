package com.bcnetech.bcnetechlibrary.util.Watch;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by wb on 2016/3/21.
 */
public class WatchedUtil {
    private static WatchedUtil watchedUtil=null;
    private WatchedUtil(){

    }
    public static WatchedUtil getInstance(){
        if(watchedUtil==null){
            watchedUtil=new WatchedUtil();
        }
        return watchedUtil;
    }

    /***
     * 创建订单详情被观察者
     */
    private Watched orderInfoWatched;
    // 观察者list
    private List<Watcher> list = new ArrayList<Watcher>();
    public Watched getOrderInfoWatched(){
        if(orderInfoWatched==null) {
            orderInfoWatched = new Watched() {
                @Override
                public void add(Watcher watcher) {
                    list.add(watcher);
                }

                @Override
                public void remove(Watcher watcher) {
                    list.remove(watcher);
                }

                @Override
                public void removeAll() {
                    list.clear();
                }

                @Override
                public void notifyWatcher(Object... parms) {
                    for (Watcher watcher : list) {
                        watcher.updateNotify(parms);
                    }
                }
            };
        }
        return  orderInfoWatched;
    }
    public void clearOrderInfoWatched(){
        if(orderInfoWatched!=null){
            orderInfoWatched.removeAll();
            orderInfoWatched=null;
        }
    }

}
