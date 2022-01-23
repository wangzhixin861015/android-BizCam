package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.SqlControl.CameraSettingData;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;

import java.util.List;

public class CameraSettingAdapter extends RecyclerView.Adapter<CameraSettingAdapter.CameraSettingHolder> {
    private List<CameraSettingData> listdata;
    private Activity activity;
    private LayoutInflater mInflater;
    private int selectType = -1;
    private ClickInterFace clickInterFace;


    public CameraSettingAdapter(List<CameraSettingData> listdata, Activity activity, ClickInterFace clickInterFace) {
        this.listdata = listdata;
        this.clickInterFace = clickInterFace;
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);
    }

    public void setList(List<CameraSettingData> listdata) {
        this.listdata = listdata;
        notifyDataSetChanged();
    }


    @Override
    public CameraSettingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.camerasetting_pop_item_layout, parent, false);
        return new CameraSettingAdapter.CameraSettingHolder(view);
    }

    @Override
    public void onBindViewHolder(CameraSettingHolder holder, final int position) {
        CameraSettingData cameraSettingData = listdata.get(position);

        if (cameraSettingData.getType() == Flag.PICSIZE || cameraSettingData.getType() == Flag.VIDEOSIZE) {
            if (cameraSettingData.getSize().getName().equals(CameraStatus.Size.LARGE.getName())) {
                holder.popcontent.setText(cameraSettingData.getSize().getName() /*+ "(" + cameraSettingData.getDetailSize().getWidth() + "×" + cameraSettingData.getDetailSize().getHeight() + ")"*/);
            } else if (cameraSettingData.getSize().getName().equals(CameraStatus.Size.MIDDLE.getName())) {
                holder.popcontent.setText(cameraSettingData.getSize().getName() /*+ "(" +cameraSettingData.getDetailSize().getWidth() + "×" + cameraSettingData.getDetailSize().getHeight() + ")"*/);
            } else if (cameraSettingData.getSize().getName().equals(CameraStatus.Size.SMALL.getName())) {
                holder.popcontent.setText(cameraSettingData.getSize().getName() /*+ "(" + cameraSettingData.getDetailSize().getWidth() + "×" + cameraSettingData.getDetailSize().getHeight() + ")"*/);
            }

        } else {
            holder.popcontent.setText(cameraSettingData.getSize().getName());
        }

        if (cameraSettingData.isSelect()) {
            selectType = position;
            holder.pop_select.setVisibility(View.VISIBLE);
            holder.popcontent.setTextColor(activity.getResources().getColor(R.color.sing_in_color));
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

    public class CameraSettingHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView popcontent;
        private ImageView pop_select;

        private CameraSettingHolder(View itemView) {
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