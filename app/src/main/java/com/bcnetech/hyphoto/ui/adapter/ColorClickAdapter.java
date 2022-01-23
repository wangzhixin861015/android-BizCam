package com.bcnetech.hyphoto.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.ColorChoiceItem;

import java.util.List;

/**
 * Created by yhf on 2017/3/2.
 */
public class ColorClickAdapter extends RecyclerView.Adapter<ColorClickAdapter.ViewHolder> {


    private Context context;

    private List<ColorChoiceItem> presets;
    private LayoutInflater inflater;
    private HolderInterFace interFace;
    private int selectItem = -1;


    public Context getContext() {
        return context;
    }

    public ColorClickAdapter(Context context, List<ColorChoiceItem> presets) {
        this.context = context;
        this.presets = presets;
        this.inflater = LayoutInflater.from(context);
    }

    public int getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    public HolderInterFace getInterFace() {
        return interFace;
    }

    public void setInterFace(HolderInterFace interFace) {
        this.interFace = interFace;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<ColorChoiceItem> getPresets() {
        return presets;
    }

    public void setPresets(List<ColorChoiceItem> presets) {
        this.presets = presets;
    }

    @Override
    public int getItemCount() {
        return presets == null ? 0 : presets.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ColorClickAdapter.ViewHolder(inflater.inflate(R.layout.color_click_item, null));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        String color = presets.get(position).getColor();
        viewHolder.list_item.setBackgroundColor(Color.parseColor(color));
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interFace.setItemClick(position, viewHolder);
            }
        });

        if(selectItem==position){
            viewHolder.list_item_check.setVisibility(View.VISIBLE);
        }else {
            viewHolder.list_item_check.setVisibility(View.GONE);

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        public ImageView list_item;
        public ImageView list_item_check;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            list_item = (ImageView) view.findViewById(R.id.list_item);
            list_item_check = (ImageView) view.findViewById(R.id.list_item_check);
        }
    }

    public interface HolderInterFace {
        void setItemClick(int position, ViewHolder viewHolder);
    }
}
