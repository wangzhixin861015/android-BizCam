package com.bcnetech.hyphoto.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.ui.view.AutoLocateHorizontalView;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.bcnetech.hyphoto.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by yhf on 17/12/4.
 */

public class AutoLocationAdapter extends RecyclerView.Adapter<AutoLocationAdapter.AutoLocationViewHolder> implements AutoLocateHorizontalView.IAutoLocateHorizontalView {
    private Context context;
    private View view;
    private List<PictureProcessingData> list;
    private int type;
    public final static int LOCAK_PARAM=0;
    public final static int PRESET_PARAM=1;
    private Handler handler;
    private Bitmap changeBitmap;
    public AutoLocationAdapter(Context context, List<PictureProcessingData> list,int type){
        this.context = context;
        this.list = list;
        this.type=type;
    }

    public Bitmap getChangeBitmap() {
        return changeBitmap;
    }

    public void setChangeBitmap(Bitmap changeBitmap) {
        this.changeBitmap = changeBitmap;
    }

    @Override
    public AutoLocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.auto_location_item,parent,false);
        return new AutoLocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AutoLocationViewHolder holder, final int position) {
        PictureProcessingData imageTool=list.get(position);


        if(type==LOCAK_PARAM){
            if (imageTool.getType() == BizImageMangage.SRC) {
                Picasso.get().load(imageTool.getImageUrl()).resize(ImageUtil.Dp2Px(context, 52), ImageUtil.Dp2Px(context, 52)).placeholder(R.color.backgroud).centerCrop().into(holder.ivType);
            } else if (imageTool.getType() == BizImageMangage.SRC_PRESET_PARMS) {
                holder.ivType.setImageBitmap(changeBitmap);
            } else if (imageTool.getType() == BizImageMangage.PARMS) {
                if (!StringUtil.isBlank(imageTool.getImageUrl()) && !"null".equals(imageTool.getImageUrl())) {
                    Picasso.get().load(imageTool.getImageUrl()).resize(ImageUtil.Dp2Px(context, 52), ImageUtil.Dp2Px(context, 52)).centerCrop().into(holder.ivType);
                } else {
//                    Picasso.get(context).load(imageTool.getSmallUrl()).resize(ImageUtil.Dp2Px(context, 72), ImageUtil.Dp2Px(context, 52)).centerInside().into(new Target() {
//                        @Override
//                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                            changBitmap = getCurrentImg(bitmap, pos);
//                            iv_iamge.setImageBitmap(changBitmap);
//                        }
//
//                        @Override
//                        public void onBitmapFailed(Drawable errorDrawable) {
//
//                        }
//
//                        @Override
//                        public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                        }
//                    });
                           /* Glide.with(context).load(processtools.get(pos).getSmallUrl()).asBitmap().override(ImageUtil.Dp2Px(context, 278), ImageUtil.Dp2Px(context, 278)).dontAnimate().into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    changBitmap = getCurrentImg(resource, pos);
                                    iv_iamge.setImageBitmap(changBitmap);
                                }
                            });*/
                }
            } else {
                if (!StringUtil.isBlank(imageTool.getSmallUrl()) && !"null".equals(imageTool.getSmallUrl())) {
                    Picasso.get().load(imageTool.getSmallUrl()).resize(ImageUtil.Dp2Px(context, 52), ImageUtil.Dp2Px(context, 52)).centerCrop().into(holder.ivType);
                } else {
                    Picasso.get().load(imageTool.getImageUrl()).resize(ImageUtil.Dp2Px(context, 52), ImageUtil.Dp2Px(context, 52)).centerCrop().into(holder.ivType);
                }
            }
        }else {
            handler = new Handler();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    if(null!=list.get(position).getImageData()){
                        final Bitmap changBitmap = BitmapUtils.compress(BitmapUtils.convertStringToIcon(list.get(position).getImageData()));
//                            changBitmap = getCurrentImgPreset(bitmap, pos);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
//                                        dissmissDialog();
                                holder.ivType.setImageBitmap(changBitmap);
                            }
                        });
                    }

                }
            }).start();
        }

//        switch (imageTool.getType()){
//
//            case BizImageMangage.PAINT_BRUSH:
//                    holder.ivType.setImageResource(R.drawable.new_penwhite);
//
//                break;
//
//            case BizImageMangage.BACKGROUND_REPAIR:
//                    holder.ivType.setImageResource(R.drawable.new_backgroundchangwhite);
//
//                break;
//
//            case BizImageMangage.ROTATE:
//                    holder.ivType.setImageResource(R.drawable.new_revolvewhite);
//
//                break;
//            case BizImageMangage.PARMS:
//                    holder.ivType.setImageResource(R.drawable.new_lightwhite);
//
//                break;
//            case BizImageMangage.WHITE_BALANCE:
//                    holder.ivType.setImageResource(R.drawable.new_balancewhite);
//
//                break;
//            case BizImageMangage.SRC:
//                    holder.ivType.setImageResource(R.drawable.src_white);
//                break;
//            case BizImageMangage.SRC_PRESET_PARMS:
//                holder.ivType.setImageResource(R.drawable.src_white);
//                break;
//
//
//        }
    }

    @Override
    public int getItemCount() {
        return  list.size();
    }

    @Override
    public View getItemView() {
        return view;
    }

    @Override
    public void onViewSelected(boolean isSelected, int pos, RecyclerView.ViewHolder holder, int itemWidth) {

    }



    public class AutoLocationViewHolder extends RecyclerView.ViewHolder{
        ImageView ivType;
        TextView tv_age;
        AutoLocationViewHolder(View itemView) {
            super(itemView);
            ivType = (ImageView) itemView.findViewById(R.id.iv_type);
        }
    }


}
