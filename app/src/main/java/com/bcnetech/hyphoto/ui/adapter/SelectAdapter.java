package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcnetech.hyphoto.data.PopSelectData;
import com.bcnetech.hyphoto.R;

import java.util.List;

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.SelectHolder> {
    private List<PopSelectData> listdata;
    private Activity activity;
    private LayoutInflater mInflater;
    private int selectType = -1;
    private ClickInterFace clickInterFace;


    public SelectAdapter(List<PopSelectData> listdata, Activity activity, ClickInterFace clickInterFace) {
        this.listdata = listdata;
        this.clickInterFace = clickInterFace;
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);
    }

    public void setList(List<PopSelectData> listdata) {
        this.listdata = listdata;
        notifyDataSetChanged();
    }


    @Override
    public SelectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.camerasetting_pop_item_layout, parent, false);
        return new SelectAdapter.SelectHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectHolder holder, final int position) {
        PopSelectData data = listdata.get(position);
        holder.popcontent.setText(data.getSeletContent());

        if (data.isSelect()) {
            selectType = position;
            holder.pop_select.setVisibility(View.VISIBLE);
            holder.popcontent.setTextColor(activity.getResources().getColor(R.color.blue_flag_bg));
        } else {
            holder.pop_select.setVisibility(View.GONE);
            holder.popcontent.setTextColor(activity.getResources().getColor(R.color.text_color));
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickInterFace.onClick(position);
                for (int i = 0; i < listdata.size(); i++) {
                    listdata.get(i).setSelect(false);
                    if (i == position) {
                        listdata.get(i).setSelect(true);
                    }
                }
                notifyDataSetChanged();
            }
        });
    }

    public int getSelect() {
        return selectType;
    }

    public void setNoSelect() {
        for (int i = 0; i < listdata.size(); i++) {
            listdata.get(i).setSelect(false);
        }
        notifyDataSetChanged();
        selectType = -1;
    }


    @Override
    public int getItemCount() {
        return listdata == null ? 0 : listdata.size();
    }

    public class SelectHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView popcontent;
        private ImageView pop_select;

        private SelectHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            pop_select = (ImageView) view.findViewById(R.id.pop_select);
            popcontent = (TextView) view.findViewById(R.id.popcontent);
        }
    }

    public interface ClickInterFace {
        void onClick(int position);
    }
}