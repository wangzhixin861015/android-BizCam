package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.view.help.ItemTouchHelperAdapter;
import com.bcnetech.bcnetechlibrary.view.help.OnStartDragListener;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.ui.view.swipemenu.SwapWrapperUtils;
import com.bcnetech.hyphoto.ui.view.swipemenu.SwipeMenuBuilder;
import com.bcnetech.hyphoto.ui.view.swipemenu.view.SwipeMenuLayout;
import com.bcnetech.hyphoto.ui.view.swipemenu.view.SwipeMenuView;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.bcnetech.hyphoto.R;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yhf on 2017/3/27.
 */
public class SwapRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private Activity activity;
    private SwipeMenuBuilder swipeMenuBuilder;
    private List<PresetParm> list;
    private OnStartDragListener dragStartListenert;
    private String mtype;
    private boolean isScrolling=false;
    private ExecutorService cachedThreadPool;

    @Override
    public int getItemViewType(int position) {

        PresetParm presetParm=list.get(position);
        if(presetParm.getShowType().equals("0")){
            return 0;
        }else if(presetParm.getShowType().equals("hint")){
            return 1;
        }else {
            return 2;
        }
    }

    public boolean isScrolling() {
        return isScrolling;
    }

    public void setScrolling(boolean scrolling) {
        isScrolling = scrolling;
    }

    public SwapRecyclerAdapter(Activity activity, List<PresetParm> list, OnStartDragListener dragStartListenert, String mtype) {
        this.activity = activity;
        this.list = list;
        this.mtype=mtype;
        swipeMenuBuilder = (SwipeMenuBuilder) this.activity;
        cachedThreadPool = Executors.newCachedThreadPool();
        this.dragStartListenert=dragStartListenert;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==0){
            //根据数据创建右边的操作view
            SwipeMenuView menuView = swipeMenuBuilder.create();
            //包装用户的item布局
            SwipeMenuLayout swipeMenuLayout = SwapWrapperUtils.wrap(parent, R.layout.swap_recycler_item, menuView, new LinearInterpolator(), new LinearInterpolator());
            SwapRecyclerAdapter.CanUseViewHolder holder=new SwapRecyclerAdapter.CanUseViewHolder(swipeMenuLayout);
            setListener(parent, holder, viewType);
            return holder;
        }else if(viewType==1){
            //根据数据创建右边的操作view
            SwipeMenuView menuView = swipeMenuBuilder.create();
            SwipeMenuLayout swipeMenuLayout = SwapWrapperUtils.wrap(parent, R.layout.swap_hint_item,menuView, new LinearInterpolator(), new LinearInterpolator());

//            View view = LayoutInflater.from(activity).inflate(R.layout.swap_hint_item, parent, false);
            SwapRecyclerAdapter.ViewHolder viewHolder = new SwapRecyclerAdapter.ViewHolder(swipeMenuLayout);

            return viewHolder;
        }else{
            //根据数据创建右边的操作view
            SwipeMenuView menuView = swipeMenuBuilder.create();
            SwipeMenuLayout swipeMenuLayout = SwapWrapperUtils.wrap(parent, R.layout.preset_down_diff_item,menuView, new LinearInterpolator(), new LinearInterpolator());
//            View notUseView = LayoutInflater.from(activity).inflate(R.layout.preset_down_diff_item, parent, false);
            SwapRecyclerAdapter.NotUseViewHolder notUseViewHolder = new SwapRecyclerAdapter.NotUseViewHolder(swipeMenuLayout);
            setListener(parent, notUseViewHolder, viewType);
            return notUseViewHolder;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final PresetParm presetParm=list.get(position);
        switch (getItemViewType(position)){
            case 0:
                // 0 显示 1 隐藏
                final CanUseViewHolder canUseViewHolder= (CanUseViewHolder) holder;
//                if(presetParm.getShowType().equals("1")){

//                    canUseViewHolder.display.setVisibility(View.VISIBLE);
//                    canUseViewHolder.hide.setVisibility(View.GONE);
//                    canUseViewHolder.preset_item_layout.setBackgroundResource(R.color.backgroud);
//                }else {
                canUseViewHolder.display.setVisibility(View.GONE);
                canUseViewHolder.hide.setVisibility(View.VISIBLE);
//                }

                if(!StringUtil.isBlank(presetParm.getCoverId())){
                    if(FileUtil.fileIsExists(presetParm.getCoverId())){
                        ImageUtil.EBizImageLoad(canUseViewHolder.preset_item_img,"file://" + Flag.PRESET_IMAGE_PATH + presetParm.getCoverId() + ".jpg");
                    }else if(presetParm.getCoverId().equals("default")){
                        Picasso.get().load(R.drawable.preset_default).into(canUseViewHolder.preset_item_img);
                    }else if(presetParm.getCoverId().equals("default2")){
                        Picasso.get().load(R.drawable.preset_default_two).into(canUseViewHolder.preset_item_img);
                    }else if(presetParm.getCoverId().equals("default3")){
                        Picasso.get().load(R.drawable.preset_default_three).into(canUseViewHolder.preset_item_img);
                    }else {
                        if(presetParm.getTextSrc().contains("png")){
                            Picasso.get().load(presetParm.getTextSrc()).into(canUseViewHolder.preset_item_img);
                        }else {
                            String url = StringUtil.getSizeUrl(BitmapUtils.getBitmapUrl6(presetParm.getCoverId()),
                                    ImageUtil.Dp2Px(activity,120),
                                    ImageUtil.Dp2Px(activity,120));

                            Picasso.get().load(url).into(canUseViewHolder.preset_item_img);

                        }
                    }
                }else {
                    if(FileUtil.fileIsExists(presetParm.getPresetId())){
                        ImageUtil.EBizImageLoad(canUseViewHolder.preset_item_img,"file://" + Flag.PRESET_IMAGE_PATH + presetParm.getPresetId() + ".jpg");
                    }else if(presetParm.getPresetId().equals("1")){
                        Picasso.get().load(R.drawable.preset_default).into(canUseViewHolder.preset_item_img);
                    }else {
                        if(presetParm.getTextSrc().contains("png")){
                            Picasso.get().load(presetParm.getTextSrc()).into(canUseViewHolder.preset_item_img);
                        }else {
                            String url = StringUtil.getSizeUrl(BitmapUtils.getBitmapUrl6(presetParm.getPresetId()),
                                    ImageUtil.Dp2Px(activity,120),
                                    ImageUtil.Dp2Px(activity,120));

                            Picasso.get().load(url).into(canUseViewHolder.preset_item_img);

                        }


                    }
                }

                canUseViewHolder.preset_content_name.setText(presetParm.getName()+"");
                canUseViewHolder.preset_auther_name.setText("© "+presetParm.getAuther()+"");
                break;
            case 2:
                final NotUseViewHolder notUseViewHolder= (NotUseViewHolder) holder;



                if(!StringUtil.isBlank(presetParm.getCoverId())){
                    if(FileUtil.fileIsExists(presetParm.getCoverId())){
                        ImageUtil.EBizImageLoad(notUseViewHolder.preset_item_img,"file://"+Flag.PRESET_IMAGE_PATH+presetParm.getCoverId()+".jpg");
                    }else if(presetParm.getCoverId().equals("default")){
                        Picasso.get().load(R.drawable.preset_default).into(notUseViewHolder.preset_item_img);
                    }else if(presetParm.getCoverId().equals("default2")){
                        Picasso.get().load(R.drawable.preset_default_two).into(notUseViewHolder.preset_item_img);
                    }else if(presetParm.getCoverId().equals("default3")){
                        Picasso.get().load(R.drawable.preset_default_three).into(notUseViewHolder.preset_item_img);
                    }else {
                        String url = StringUtil.getSizeUrl(BitmapUtils.getBitmapUrl6(presetParm.getCoverId()),
                                ImageUtil.Dp2Px(activity,120),
                                ImageUtil.Dp2Px(activity,120));

                        Picasso.get().load(url).into(notUseViewHolder.preset_item_img);

                    }
                }else {
                    if(FileUtil.fileIsExists(presetParm.getPresetId())){
                        ImageUtil.EBizImageLoad(notUseViewHolder.preset_item_img,"file://"+Flag.PRESET_IMAGE_PATH+presetParm.getPresetId()+".jpg");
                    }else if(presetParm.getId().equals("1")){
                        Picasso.get().load(R.drawable.preset_default).into(notUseViewHolder.preset_item_img);
                    }else {

                        String url = StringUtil.getSizeUrl(BitmapUtils.getBitmapUrl6(presetParm.getPresetId()),
                                ImageUtil.Dp2Px(activity,120),
                                ImageUtil.Dp2Px(activity,120));

                        Picasso.get().load(url).into(notUseViewHolder.preset_item_img);

                    }
                }

                notUseViewHolder.preset_content_name.setText(presetParm.getName()+"");
                notUseViewHolder.preset_auther_name.setText("© "+presetParm.getAuther()+"");
                notUseViewHolder.hide.setVisibility(View.GONE);
                notUseViewHolder.display.setVisibility(View.VISIBLE);
//                notUseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(activity, PresetContentActivity.class);
//                        intent.putExtra(Flag.PRESET_PARMS, list.get(position));
//                        intent.putExtra("isdownload",true);
//                        intent.putExtra("position",position);
//                        activity.startActivityForResult(intent,100);
//                    }
//                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    protected void setListener(final ViewGroup parent, final  RecyclerView.ViewHolder viewHolder, int viewType) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, viewHolder, list.get(position), position);
                }
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    dragStartListenert.onStartDrag(viewHolder);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if(list.get(fromPosition).getPresetId().contains("default")||list.get(toPosition).getPresetId().contains("default")){
            return true;
        }
        Collections.swap(list, fromPosition, toPosition);
        dragStartListenert.onEndDrag(fromPosition,toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    public static class CanUseViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private RelativeLayout preset_item_layout;
        private ImageView preset_item_img;
        private TextView preset_content_name;
        private TextView preset_auther_name;
        private SwipeMenuLayout swipeMenuLayout;
        private LinearLayout hide;
        private LinearLayout display;

        public CanUseViewHolder(View view) {
            super(view);
            itemView = view;
            preset_item_layout = (RelativeLayout) itemView.findViewById(R.id.preset_item_layout);
            preset_item_img = (ImageView) itemView.findViewById(R.id.preset_item_img);
            preset_content_name = (TextView) itemView.findViewById(R.id.preset_content_name);
            preset_auther_name = (TextView) itemView.findViewById(R.id.preset_auther_name);
            swipeMenuLayout= (SwipeMenuLayout) itemView;
            hide= (LinearLayout) swipeMenuLayout.getmMenuView().getChildAt(0);
            display=(LinearLayout) swipeMenuLayout.getmMenuView().getChildAt(1);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View itemView;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
        }
    }

    public static class NotUseViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private ImageView preset_item_img;
        private TextView preset_content_name;
        private TextView preset_auther_name;
        private SwipeMenuLayout swipeMenuLayout;
        private LinearLayout hide;
        private LinearLayout display;

        public NotUseViewHolder(View view) {
            super(view);
            itemView = view;
            preset_item_img = (ImageView) itemView.findViewById(R.id.preset_item_img);
            preset_content_name = (TextView) itemView.findViewById(R.id.preset_content_name);
            preset_auther_name = (TextView) itemView.findViewById(R.id.preset_auther_name);
            swipeMenuLayout= (SwipeMenuLayout) itemView;
            hide= (LinearLayout) swipeMenuLayout.getmMenuView().getChildAt(0);
            display=(LinearLayout) swipeMenuLayout.getmMenuView().getChildAt(1);
        }

    }

    public void setData(List<PresetParm> list) {
        this.list = list;
    }


    public OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener<T> {
        void onItemClick(View view, RecyclerView.ViewHolder holder, T o, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


}