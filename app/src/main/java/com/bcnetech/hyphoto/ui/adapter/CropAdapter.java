package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcnetech.hyphoto.data.CropListdata;
import com.bcnetech.hyphoto.R;

import java.util.List;

public class CropAdapter extends RecyclerView.Adapter<CropAdapter.CropViewHolder> {
    private List<CropListdata> listdata;
    private Activity activity;
    private LayoutInflater mInflater;

    public CropAdapter(List<CropListdata> listdata, Activity activity) {
        this.listdata = listdata;
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public void onBindViewHolder(final CropAdapter.CropViewHolder holder, int position) {
        final CropListdata cropListdata = listdata.get(position);
        holder.setContent(cropListdata.getDrawable_n(), cropListdata.getDrawable_y(),cropListdata.getDrawableReverse(), cropListdata.getCropText(),cropListdata.isReverse());
        holder.setSelected(cropListdata.isSelect(),cropListdata.isCanReverse());
        //holder.showReverse(cropListdata.isShowReverse());
/*
        holder.iv_reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    holder.showReverse(!cropListdata.isReverse());
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return listdata == null ? 0 : listdata.size();
    }

    public void setListdata(List<CropListdata> listdata) {
        this.listdata = listdata;
        notifyDataSetChanged();
    }


    @Override
    public CropViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.crop_select_item, parent, false);
        return new CropViewHolder(view);
    }

    public class CropViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View view;
        private Drawable cropType_n;
        private Drawable cropType_y;
        private Drawable cropReverse;
        private String cropText;
        private ImageView iv_crop;
        private TextView tv_crop;
        private ImageView iv_reverse;
        private boolean isReverse;


        public CropViewHolder(View view) {
            super(view);
            this.view = view;
            iv_crop = (ImageView) view.findViewById(R.id.iv_crop);
            tv_crop = (TextView) view.findViewById(R.id.tv_crop);
            iv_reverse = (ImageView) view.findViewById(R.id.iv_reverse);
            view.setOnClickListener(this);
            iv_reverse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onReverseClick(getPosition());
                }
            });
        }


        public void showReverse(boolean isShowReverse) {
            if (isShowReverse) {
                iv_reverse.setVisibility(View.VISIBLE);
            } else {
                iv_reverse.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, getPosition());
            }
        }

        public void setContent(Drawable cropType_n, Drawable cropType_y,Drawable cropReverse, String cropText,boolean isReverse) {
            this.cropType_n = cropType_n;
            this.cropType_y = cropType_y;
            this.cropText = cropText;
            this.cropReverse = cropReverse;
            this.isReverse = isReverse;
            iv_crop.setImageDrawable(cropType_n);
            tv_crop.setText(cropText);
        }

        public void setSelected(boolean isSelected,boolean isCanReverse) {
            if (isSelected) {
                iv_crop.setImageDrawable(this.cropType_y);
                tv_crop.setTextColor(view.getResources().getColor(R.color.sing_in_color));
                if (isCanReverse) {
                    iv_reverse.setVisibility(View.VISIBLE);
                }else{
                    iv_reverse.setVisibility(View.INVISIBLE);
                }
                if (isReverse){
                    if (cropText == "9 ：16"){
                        this.tv_crop.setText("16 : 9");
                    }else{
                        this.tv_crop.setText(change(cropText));
                    }
                    this.iv_crop.setImageDrawable(cropReverse);
                }else{
                    iv_crop.setImageDrawable(cropType_y);
                }
            } else {
                iv_crop.setImageDrawable(this.cropType_n);
                tv_crop.setTextColor(view.getResources().getColor(R.color.text_color));
                iv_reverse.setVisibility(View.INVISIBLE);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onReverseClick( int position);
    }

    private OnItemClickListener mOnItemClickListener = null;


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    private String change(String s){
        char[] sc=s.toCharArray();
        char temp=sc[0];
        sc[0]=sc[sc.length-1];
        sc[sc.length-1]=temp;
        return String.valueOf(sc);
    }
}
