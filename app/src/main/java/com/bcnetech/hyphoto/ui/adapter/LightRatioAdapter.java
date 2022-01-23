package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.view.help.ItemTouchHelperAdapter;
import com.bcnetech.bcnetechlibrary.view.help.OnStartDragListener;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.ui.view.swipemenu.SwapWrapperUtils;
import com.bcnetech.hyphoto.ui.view.swipemenu.SwipeMenuBuilder;
import com.bcnetech.hyphoto.ui.view.swipemenu.view.SwipeMenuLayout;
import com.bcnetech.hyphoto.ui.view.swipemenu.view.SwipeMenuView;

import java.util.Collections;
import java.util.List;

/**
 * Created by wenbin on 16/8/16.
 */

public class LightRatioAdapter extends RecyclerView.Adapter<LightRatioAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {
    private Activity activity;
    private List<LightRatioData> list;
    private LayoutInflater inflater;
    private OnStartDragListener dragStartListenert;
    private float stary;
    private SwipeMenuBuilder swipeMenuBuilder;

    public LightRatioAdapter(Activity activity, List list, OnStartDragListener dragStartListenert) {
        this.activity = activity;
        this.list = list;
        this.inflater = LayoutInflater.from(activity);
        swipeMenuBuilder = (SwipeMenuBuilder) this.activity;
        this.dragStartListenert = dragStartListenert;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //根据数据创建右边的操作view
        SwipeMenuView menuView = swipeMenuBuilder.create();
        //包装用户的item布局
        SwipeMenuLayout swipeMenuLayout = SwapWrapperUtils.wrap(parent, R.layout.light_ratio_item, menuView, new LinearInterpolator(), new LinearInterpolator());
        LightRatioAdapter.ViewHolder holder=new LightRatioAdapter.ViewHolder(swipeMenuLayout);
        setListener(parent,holder);
        return holder;
    }

    protected void setListener(final ViewGroup parent, final  LightRatioAdapter.ViewHolder viewHolder) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dragStartListenert.onStartDrag(viewHolder);
                return false;
            }
        });
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final LightRatioData lightRatioData = list.get(position);

        holder.name.setText(lightRatioData.getName() + "");
        holder.l_num_v.setText(lightRatioData.getLeftLight() + "");
        holder.r_num_v.setText(lightRatioData.getRightLight() + "");
        holder.t_num_v.setText(lightRatioData.getMoveLight() + "");
        holder.t_bottom_v.setText(lightRatioData.getBottomLight()+"");
        holder.t_background_v.setText(lightRatioData.getBackgroudLight()+"");
        holder.move2_num_v.setText(lightRatioData.getTopLight()+"");

        holder.l_num.setVisibility(View.VISIBLE);
        holder.l_num_v.setVisibility(View.VISIBLE);
        holder.r_num.setVisibility(View.VISIBLE);
        holder.r_num_v.setVisibility(View.VISIBLE);
        holder.t_num.setVisibility(View.VISIBLE);
        holder.t_num_v.setVisibility(View.VISIBLE);
        holder.t_bottom.setVisibility(View.VISIBLE);
        holder.t_bottom_v.setVisibility(View.VISIBLE);
        holder.t_background.setVisibility(View.VISIBLE);
        holder.t_background_v.setVisibility(View.VISIBLE);
        holder.move2_num.setVisibility(View.VISIBLE);
        holder.move2_num_v.setVisibility(View.VISIBLE);




    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(list, fromPosition, toPosition);
        dragStartListenert.onEndDrag(fromPosition,toPosition);
        notifyItemMoved(fromPosition, toPosition);

        return true;
    }

    @Override
    public void onItemDismiss(int position) {
    }


    public void setData(List<LightRatioData> list) {
        this.list = list;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private RelativeLayout rel_item;
        private TextView name;
        private TextView r_num;
        private TextView r_num_v;
        private TextView l_num;
        private TextView l_num_v;
        private TextView t_num;
        private TextView t_num_v;
        private TextView t_bottom;
        private TextView t_bottom_v;
        private TextView t_background;
        private TextView t_background_v;
        private TextView move2_num_v;
        private TextView move2_num;
        public ViewHolder(View view) {
            super(view);
            itemView = view;
            rel_item = (RelativeLayout) itemView.findViewById(R.id.rel_item);
            name = (TextView) itemView.findViewById(R.id.name);
            r_num = (TextView) itemView.findViewById(R.id.r_num);
            r_num_v = (TextView) itemView.findViewById(R.id.r_num_v);
            l_num = (TextView) itemView.findViewById(R.id.l_num);
            l_num_v = (TextView) itemView.findViewById(R.id.l_num_v);
            t_num = (TextView) itemView.findViewById(R.id.t_num);
            t_num_v = (TextView) itemView.findViewById(R.id.t_num_v);
            t_bottom=(TextView) itemView.findViewById(R.id.t_bottom);
            t_bottom_v=(TextView) itemView.findViewById(R.id.t_bottom_v);
            t_background=(TextView) itemView.findViewById(R.id.t_background);
            t_background_v=(TextView) itemView.findViewById(R.id.t_background_v);
            move2_num=(TextView) itemView.findViewById(R.id.move2_num);
            move2_num_v=(TextView) itemView.findViewById(R.id.move2_num_v);

        }

    }
}
