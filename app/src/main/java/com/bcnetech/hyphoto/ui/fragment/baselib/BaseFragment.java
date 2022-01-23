package com.bcnetech.hyphoto.ui.fragment.baselib;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * tab的一个界面，当界面第一次启动的时候，生命周期如下<br>
 *
 * ----------------------Fragment1:onAttach <br>
 * ----------------------Fragment1:onCreate <br>
 * ----------------------Fragment1:onCreateView <br>
 * ----------------------Fragment1:onActivityCreated <br>
 * ----------------------Fragment1:onStart <br>
 * ----------------------Fragment1:onResume<br>
 *
 * 当他的宿主Activity被销毁时发生的生命周期如下<br>
 *
 * ----------------------Fragment1:onPause<br>
 * ----------------------Fragment1:onStop<br>
 * ----------------------Fragment1:onDestroyView<br>
 * ----------------------Fragment1:onDestroy<br>
 * ----------------------Fragment1:onDetach<br>
 * Created by wenbin on 16/5/9.
 */
public class BaseFragment extends CallFragment {
    @Override
    public void callByActivity(int command, Object... data) {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = getFragmentView();
        initView();
        initData();
        onViewClick();
        return contentView;
    }

    protected void initView(){

    }
    protected void initData(){

    }
    protected void onViewClick(){

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * 子类实现，设置Fragment的View
     */
    protected View getFragmentView() {
        TextView tv = new TextView(getActivity());
        tv.setText("This is default fragment.");
        return tv;
    }


}
