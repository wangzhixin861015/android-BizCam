package com.bcnetech.hyphoto.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.hyphoto.R;

import java.util.List;

/**
 * Created by a1234 on 17/3/27.
 */

public class IsoAdapter extends RecyclerView.Adapter<IsoAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<String> list;
    private OnItemClickListener mOnItemClickListener;
    private int selectedPos = 0;


    public IsoAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView textView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.textView = (TextView) view.findViewById(R.id.foot_text_item);

        }
    }

    public void setData(List list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.inflater.inflate(R.layout.layout_iso_item, null);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.itemView.setSelected(selectedPos == position);
        String data = list.get(position);
        holder.textView.setText(data + "");
        TextView textView = (TextView) holder.itemView.findViewById(R.id.foot_text_item);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(10, 0, 0, 0);
        textView.setLayoutParams(layoutParams);
        if (selectedPos == position) {
            textView.setTextColor(context.getResources().getColor(R.color.little_blue));

        } else {
            textView.setTextColor(context.getResources().getColor(R.color.white));
        }
        if (mOnItemClickListener != null) {
            //为ItemView设置监听器
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IsoAdapter.this.notifyDataSetChanged();
                    notifyItemChanged(selectedPos);
                    selectedPos = position;
                    //notifyItemChanged(selectedPos);
                    holder.textView.setTextColor(context.getResources().getColor(R.color.little_blue));
                    int position = holder.getLayoutPosition(); // 1
                    mOnItemClickListener.onItemClick(holder.itemView, position); // 2
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setAutoType() {
        selectedPos = 0;
        notifyItemChanged(selectedPos);
        notifyDataSetChanged();
    }
}





