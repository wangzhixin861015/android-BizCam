package com.bcnetech.hyphoto.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.R;

import java.util.List;

/**
 * Created by wenbin on 16/7/21.
 */
public class BlueTouchFooterAdapter extends RecyclerView.Adapter<BlueTouchFooterAdapter.ViewHolder> {
    private List<LightRatioData> list;
    private LayoutInflater inflater;
    private Context context;
    private LightRatioListener lightRatioListener;
    private int isClick;
    public BlueTouchFooterAdapter(Context context,List list,LightRatioListener lightRatioListener){
        this.context=context;
        this.list=list;
        this.inflater=LayoutInflater.from(context);
        this.lightRatioListener=lightRatioListener;
        isClick=-1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.blue_tooth_footer_item,null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final LightRatioData lightRatioData=list.get(position);
        holder.name.setText(lightRatioData.getName());
        if(isClick==position){
            holder.name.setTextSize(18);
            holder.name.setTextColor(context.getResources().getColor(R.color.color_white2));
        }
        else{
            holder.name.setTextSize(14);
            holder.name.setTextColor(context.getResources().getColor(R.color.color_white));
        }
        holder.name.setTag(position);
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClick=(int)v.getTag();
                if(lightRatioListener!=null){
                    lightRatioListener.clickTypeChang(true);
                    lightRatioListener.setLight(list.get(isClick));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }



    public void setUnClick(){
        if(isClick==-1){
            return;
        }
        isClick=-1;
        if(lightRatioListener!=null){
            lightRatioListener.clickTypeChang(false);
        }
    }

    public void setClick(int position){
        if(list==null||position>=list.size()){
            return;
        }

        isClick=position;
        if(lightRatioListener!=null){
            lightRatioListener.clickTypeChang(true);
            lightRatioListener.setLight(list.get(isClick));
        }
    }

    public void setData(List<LightRatioData> list){
        this.list=list;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private View viwe;
        public ViewHolder(View itemView) {
            super(itemView);
            viwe=itemView;
            name=(TextView) viwe.findViewById(R.id.name);
        }
    }



    public interface  LightRatioListener{
        void setLight(LightRatioData light);
        void clickTypeChang(boolean isClick);
    }
}
