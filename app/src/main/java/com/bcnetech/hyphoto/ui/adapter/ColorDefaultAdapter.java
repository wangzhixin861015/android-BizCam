package com.bcnetech.hyphoto.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bcnetech.hyphoto.data.ColorChoiceItem;
import com.bcnetech.hyphoto.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by yhf on 2017/3/2.
 */
public class ColorDefaultAdapter extends RecyclerView.Adapter<ColorDefaultAdapter.ViewHolder> {


    private Context context;

    private List<ColorChoiceItem> presets;
    private LayoutInflater inflater;
    private HolderInterFace interFace;
    private int selectItem = 0;


    public Context getContext() {
        return context;
    }

    public ColorDefaultAdapter(Context context, List<ColorChoiceItem> presets) {
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
        return new ColorDefaultAdapter.ViewHolder(inflater.inflate(R.layout.color_default_item, null));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        String type = presets.get(position).getColor();
        if (type.equals("original")) {
            Picasso.get().load(presets.get(position).getPath()).resize(100,100).into(viewHolder.list_item);
        } else if (type.equals("transparent")) {
            viewHolder.list_item.setImageResource(R.drawable.biz_matting_transparent);
        } else if (type.equals("white")) {
            viewHolder.list_item.setImageResource(R.drawable.biz_matting_white);
        } else if (type.equals("black")) {
            viewHolder.list_item.setImageResource(R.drawable.biz_matting_black);
        }else if (type.equals("colorBoard")) {
            if(selectItem==position){
                viewHolder.list_item.setImageResource(R.drawable.biz_matting_colorboard_select);
            }else {
                viewHolder.list_item.setImageResource(R.drawable.biz_matting_colorboard);
            }
        } else if (type.equals("colorLump")) {
            if(selectItem==position){
                viewHolder.list_item.setImageResource(R.drawable.biz_matting_colorlump_select);
            }else {
                viewHolder.list_item.setImageResource(R.drawable.biz_matting_colorlump);
            }
        }
        if (selectItem == position) {

            viewHolder.list_item_check.setVisibility(View.VISIBLE);
            viewHolder.list_item_unCheck.setVisibility(View.GONE);
        } else {
            viewHolder.list_item_check.setVisibility(View.GONE);
            viewHolder.list_item_unCheck.setVisibility(View.VISIBLE);
        }

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interFace.setItemClick(position, viewHolder);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        public ImageView list_item;
        public ImageView list_item_check;
        ImageView list_item_unCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            list_item = (ImageView) view.findViewById(R.id.list_item);
            list_item_check = (ImageView) view.findViewById(R.id.list_item_check);
            list_item_unCheck= (ImageView) view.findViewById(R.id.list_item_unCheck);
        }
    }

    public interface HolderInterFace {
        void setItemClick(int position, ViewHolder viewHolder);
    }
}
