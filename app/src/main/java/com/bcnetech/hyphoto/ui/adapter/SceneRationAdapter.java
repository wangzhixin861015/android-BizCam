package com.bcnetech.hyphoto.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bcnetech.hyphoto.R;

import java.util.ArrayList;

/**
 * Created by yhf on 2017/4/1.
 */
public class SceneRationAdapter extends BaseAdapter {


    private LayoutInflater mInflater;
    private ArrayList<Name> name;
    private Context mContext;

    public SceneRationAdapter( ArrayList<Name> name, Context mContext) {
        this.mInflater = LayoutInflater.from(mContext);
        this.name = name;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public Object getItem(int position) {
        return name.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(null==convertView){
            viewHolder=new ViewHolder();
            convertView=mInflater.inflate(R.layout.la_layout,null);
            viewHolder.name= (TextView) convertView.findViewById(R.id.ration_type);
            viewHolder.ration= (TextView) convertView.findViewById(R.id.ration_num);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        // set name
        viewHolder.name.setText(name.get(position).getName() + "");
        // set icon
        viewHolder.ration.setText(name.get(position).getRation());

        return convertView;
    }


    public class ViewHolder {
        TextView name;
        TextView ration;
    }

    public static class Name {
        String name;
        String ration;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRation() {
            return ration;
        }

        public void setRation(String ration) {
            this.ration = ration;
        }
    }
}
