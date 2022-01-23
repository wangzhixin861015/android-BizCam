package com.bcnetech.hyphoto.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;

import java.util.List;

/**
 * Created by yhf on 2017/5/2.
 */

public class LightAdapter extends RecyclerView.Adapter<LightAdapter.ViewHolder>{

    private List<LightRatioData> list;
    private LayoutInflater inflater;
    private Context context;
    private LightInterface lightInterface;

    public LightAdapter(Context context,List list){
        this.context=context;
        this.list=list;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LightAdapter.ViewHolder(inflater.inflate(R.layout.light_item,null));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final LightRatioData lightRatioData=list.get(position);
        holder.name.setText(lightRatioData.getName());
        if(position==list.size()-1){
            holder.line.setVisibility(View.GONE);
        }else {
            holder.line.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lightInterface.itemClick(position,holder);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private View viwe;
        private View line;
        public ViewHolder(View itemView) {
            super(itemView);
            viwe=itemView;
            name=(TextView) viwe.findViewById(R.id.name);
            line=viwe.findViewById(R.id.line);
        }

    }

    public interface LightInterface{
        void itemClick(int position,ViewHolder viewHolder);
    }

    public LightInterface getLightInterface() {
        return lightInterface;
    }

    public void setLightInterface(LightInterface lightInterface) {
        this.lightInterface = lightInterface;
    }
}
