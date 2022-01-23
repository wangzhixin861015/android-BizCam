package com.bcnetech.hyphoto.ui.fragment;

/**
 * Created by dasu on 2017/4/21.
 *
 * activity 和 fragment通信接口，即fragment通过该接口通知activity干活
 */

public interface OnSwipeRefreshListener {

    void onRefreshing();

    void onRefreshFinish();

    void onlistTop(int top);

}
