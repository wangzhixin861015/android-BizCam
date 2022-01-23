package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.ui.popwindow.PartPaintPop;

import java.util.List;


/**
 * Created by wenbin on 16/10/28.
 */

public class PartPaintPopAdapter extends RecyclerView.Adapter<PartPaintPopAdapter.ViewHolder> {
    private List list;
    private Activity activity;
    private LayoutInflater inflater;
    private ClickInterFace clickInterFace;
    public PartPaintPopAdapter(Activity activity, List list) {
        this.activity = activity;
        this.list = list;
        this.inflater = LayoutInflater.from(activity);


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.part_paint_pop_item, null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        PartPaintPop.ItemData itemData=(PartPaintPop.ItemData) list.get(position);
        switch (itemData.getType()){
            case BizImageMangage.PART_PAINT_EXPOSURE:
                holder.part_paint_pop_item_img.setImageResource(R.drawable.btnevon);
                holder.part_paint_pop_item_name.setText(activity.getResources().getString(R.string.exposire));
                break;
            case BizImageMangage.PART_PAINT_BRIGHTNESS:
                holder.part_paint_pop_item_img.setImageResource(R.drawable.btnlighton);
                holder.part_paint_pop_item_name.setText(activity.getResources().getString(R.string.brightness));
                break;
            case BizImageMangage.PART_PAINT_SATURATION:
                holder.part_paint_pop_item_img.setImageResource(R.drawable.btncolouron);
                holder.part_paint_pop_item_name.setText(activity.getResources().getString(R.string.saturation));
                break;
        }

        if(itemData.isClick()){
            holder.part_paint_pop_item_name.setTextColor(activity.getResources().getColor(R.color.color_blue_bottom));
        }
        else{
            holder.part_paint_pop_item_name.setTextColor(Color.WHITE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(clickInterFace!=null){
                    clickInterFace.onClickView(position);
                }
               /* PartPaintPop.ItemData itemData;
                for(int i=0;i<list.size();i++){
                    itemData=(PartPaintPop.ItemData)list.get(i);
                    itemData.setClick(false);
                }
                itemData=(PartPaintPop.ItemData)list.get(position);
                itemData.setClick(true);
                notifyDataSetChanged();*/
            }
        });
    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    public void setData(List list){
        this.list=list;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private ImageView part_paint_pop_item_img;
        private TextView part_paint_pop_item_name;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            part_paint_pop_item_img = (ImageView) itemView.findViewById(R.id.part_paint_pop_item_img);
            part_paint_pop_item_name = (TextView) itemView.findViewById(R.id.part_paint_pop_item_name);
        }

    }

    public void setClickInterFace(ClickInterFace clickInterFace){
        this.clickInterFace=clickInterFace;
    }
    public interface ClickInterFace{
        void onClickView(int poistion);
    }
}
