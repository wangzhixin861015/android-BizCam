package com.bcnetech.bcnetechlibrary.popwindow;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.bcnetech.bcnetechlibrary.R;
import com.bcnetech.bcnetechlibrary.popwindow.interfaces.PopWindowInterface;
import com.bcnetech.bcnetechlibrary.view.ListviewWaitView;

/**
 * Created by wenbin on 16/6/16.
 */
public class TopListPop extends  BasePopWindow{
    private Activity activity;
    private ListView list_view;
    private ListviewWaitView empty;
    private BaseAdapter adapter;
    private PopWindowInterface popWindowInterface;
    private View view;

    public TopListPop(Activity activity, BaseAdapter adapter, PopWindowInterface popWindowInterface) {
        super(activity);
        this.activity=activity;
        this.adapter=adapter;
        this.popWindowInterface=popWindowInterface;
        initView();
        initData();
        onViewClick();
    }
    private void initView(){
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.top_list_pop,null);
        list_view=(ListView) view.findViewById(R.id.list_view);
        empty=(ListviewWaitView) view.findViewById(R.id.empty);
        setContentView(view);
    }

    private void initData(){
        list_view.setAdapter(adapter);
        list_view.setEmptyView(empty);

    }

    private void onViewClick(){
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popWindowInterface.OnWBClickListener(position);
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                popWindowInterface.OnWBDismissListener();
            }
        });
    }



    @Override
    public void showPop(View view) {
        showAsDropDown(view);
    }

    @Override
    public void dismissPop() {

    }


}
