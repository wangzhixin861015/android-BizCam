package com.bcnetech.hyphoto.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.CameraParamType;
import com.bcnetech.hyphoto.utils.CameraParamUtil;

import java.util.List;

/**
 * Created by wenbin on 16/11/1.
 */

public class CameraParamTouchAdapter extends RecyclerView.Adapter<CameraParamTouchAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Item> list;
    private ClickListener clickListener;
    private boolean isLockFocusCamera=false;

    public boolean isLockFocusCamera() {
        return isLockFocusCamera;
    }

    public void setLockFocusCamera(boolean lockFocusCamera) {
        isLockFocusCamera = lockFocusCamera;
    }

    public CameraParamTouchAdapter(Context context, List<Item> list, ClickListener clickListener){
        this.context=context;
        this.inflater=LayoutInflater.from(context);
        this.clickListener=clickListener;
        this.list=list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.surf_camera_parms_toouch_item,null));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Item item=list.get(position);
        holder.light_name.setText(item.getName()+"");
        if(item.getNumber()<0||item.getNumber()>100){
            if(item.getName().equals(context.getResources().getString(R.string.shutter))){
                holder.light_number_text.setText(CameraParamUtil.getExposureTime(item.getCameraParamType(),0));
            }else if(item.getName().equals(context.getResources().getString(R.string.iso))){
                holder.light_number_text.setText(CameraParamUtil.getIso(item.getCameraParamType(),0));
            }else if(item.getName().equals(context.getResources().getString(R.string.white_balance))){
                holder.light_number_text.setText(CameraParamUtil.getWHITEBALANCE(item.getCameraParamType(),0));
            }else if(item.getName().equals(context.getResources().getString(R.string.focus_len))){
                holder.light_number_text.setText("auto");
            }
        }else{
            if(item.getName().equals(context.getResources().getString(R.string.shutter))){
                holder.light_number_text.setText(CameraParamUtil.getExposureTime(item.getCameraParamType(),item.getNumber()));
            }else if(item.getName().equals(context.getResources().getString(R.string.iso))){
                holder.light_number_text.setText(CameraParamUtil.getIso(item.getCameraParamType(),item.getNumber()));
            }else if(item.getName().equals(context.getResources().getString(R.string.white_balance))){
                holder.light_number_text.setText(CameraParamUtil.getWHITEBALANCE(item.getCameraParamType(),item.getNumber()));
            }else if(item.getName().equals(context.getResources().getString(R.string.focus_len))){
                if(isLockFocusCamera){
                    holder.light_number_text.setText(CameraParamUtil.getFOCUS(item.getCameraParamType(),item.getNumber()));
                }else {
                    holder.light_number_text.setText("auto");
                }
            }
        }

//        if(item.getNumber()==-1||item.getNumber()== BlueTouchContentView.NUL_DATA){
            //            holder.light_number_text.setBackgroundResource(R.drawable.shape_grey);
//        }
//        else{
//            if(!BlueToothLoad.getInstance().blueToothIsConnection()){
                //holder.light_name.setTextColor(context.getResources().getColor((R.color.white)));
                //holder.light_number_text.setTextColor(context.getResources().getColor((R.color.white)));
//            }
//
//        }
        holder.light_number_text.setTag(position);
        if(item.isClick()){
            holder.bottom_tool_in_layout.setBackgroundResource(R.drawable.cobox_choose_bg);
            holder.light_name.setTextColor(context.getResources().getColor((R.color.white)));
            holder.light_number_text.setTextColor(context.getResources().getColor((R.color.white)));
//
        }
        else{
            holder.bottom_tool_in_layout.setBackgroundColor(context.getResources().getColor(R.color.translucent));
            holder.light_name.setTextColor(context.getResources().getColor((R.color.white)));
            holder.light_number_text.setTextColor(context.getResources().getColor((R.color.white)));
        }
        holder.viwe.setTag(position);
        holder.viwe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos=(int)v.getTag();
                if(clickListener!=null){
                    clickListener.click(pos);
                }
            }
        });

    }

    public void setData(List<Item> list){
        this.list=list;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView light_number_text;
        private TextView light_name;
        private View viwe;
        private RelativeLayout bottom_tool_in_layout;
        public ViewHolder(View itemView) {
            super(itemView);
            viwe=itemView;
            light_number_text=(TextView) viwe.findViewById(R.id.light_number_text);
            light_name=(TextView) viwe.findViewById(R.id.light_name);
            bottom_tool_in_layout= (RelativeLayout) viwe.findViewById(R.id.bottom_tool_in_layout);
        }

        public TextView getLight_number_text() {
            return light_number_text;
        }

        public void setLight_number_text(TextView light_number_text) {
            this.light_number_text = light_number_text;
        }

        public TextView getLight_name() {
            return light_name;
        }

        public void setLight_name(TextView light_name) {
            this.light_name = light_name;
        }
    }

    public interface  ClickListener{
        void click(int position);
    }


    public static class Item {
        private String name;
        private int number;
        private boolean isClick;
        private CameraParamType cameraParamType;

        public Item(String name, int number,CameraParamType cameraParamType) {
            this.name = name;
            this.number = number;
            this.cameraParamType=cameraParamType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public boolean isClick() {
            return isClick;
        }

        public void setClick(boolean click) {
            isClick = click;
        }

        public CameraParamType getCameraParamType() {
            return cameraParamType;
        }

        public void setCameraParamType(CameraParamType cameraParamType) {
            this.cameraParamType = cameraParamType;
        }
    }
}
