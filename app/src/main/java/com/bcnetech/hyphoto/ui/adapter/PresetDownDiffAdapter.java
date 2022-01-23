package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.utils.ImageUtil;

import java.util.List;

/**
 * Created by yhf on 2017/3/30.
 */
public class PresetDownDiffAdapter extends RecyclerView.Adapter<PresetDownDiffAdapter.ViewHolder>{

    private Activity activity;
    private List<PresetParm> list;
    private LayoutInflater inflater;

    public PresetDownDiffAdapter(Activity activity, List<PresetParm> list) {
        this.activity = activity;
        this.list = list;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.preset_down_diff_item, null);
        return new PresetDownDiffAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PresetParm presetParm = list.get(position);

        ImageUtil.EBizImageLoad(holder.preset_item_img,presetParm.getTextSrc()+"");
        holder.preset_content_name.setText(presetParm.getName()+"");
        holder.preset_auther_name.setText(presetParm.getAuther()+"");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mOnItemClickListener != null) {
                    int position = holder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, holder, list.get(position), position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setData(List<PresetParm> list) {
        this.list = list;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private ImageView preset_item_img;
        private TextView preset_content_name;
        private TextView preset_auther_name;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            preset_item_img = (ImageView) itemView.findViewById(R.id.preset_item_img);
            preset_content_name = (TextView) itemView.findViewById(R.id.preset_content_name);
            preset_auther_name = (TextView) itemView.findViewById(R.id.preset_auther_name);

        }

    }

    public PresetDownDiffAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener<T> {
        void onItemClick(View view, RecyclerView.ViewHolder holder, T o, int position);
    }

    public void setOnItemClickListener(PresetDownDiffAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

}
