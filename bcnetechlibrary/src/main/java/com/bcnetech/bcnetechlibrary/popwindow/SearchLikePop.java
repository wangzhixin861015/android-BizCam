package com.bcnetech.bcnetechlibrary.popwindow;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.bcnetech.bcnetechlibrary.R;

/**
 * Created by wenbin on 16/6/6.
 */
public class SearchLikePop extends BasePopWindow {
    private Activity activity;
    private RecyclerView search_list;
    private RecyclerView.Adapter adapter;

    public SearchLikePop(Activity activity) {
        super(activity);
        this.activity=activity;
        initView();
        initData();
        onViewClick();
    }


    private void initView(){
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.search_like_pop,null);
        search_list=(RecyclerView) view.findViewById(R.id.search_list); 
        setContentView(view);
    }

    private void initData(){
        search_list.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        search_list.setHasFixedSize(true);
    }

    private void onViewClick(){

    }

    public void setAdapter (RecyclerView.Adapter adapter){
        this.adapter=adapter;
        search_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    

    
    @Override
    public void showPop(View view) {

    }

    @Override
    public void dismissPop() {

    }
}
