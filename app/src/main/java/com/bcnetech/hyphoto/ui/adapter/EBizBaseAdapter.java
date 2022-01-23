package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by wenbin on 16/5/5.
 */
public class EBizBaseAdapter extends BaseAdapter{
    protected List listData;
    protected Activity activity;
    protected LayoutInflater mInflater;
    protected EBizBaseAdapter(Activity activity, List listData){
        this.activity=activity;
        this.listData=listData;
        this.mInflater = LayoutInflater.from(activity);

    }

    protected EBizBaseAdapter(Activity activity){
        this.activity=activity;
        this.mInflater = LayoutInflater.from(activity);

    }
    @Override
    public int getCount() {
        return listData==null?0:listData.size();
    }


    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public void setData(List listData){
        this.listData=listData;
    }
    public List getListData(){
        return listData;
    };


}
