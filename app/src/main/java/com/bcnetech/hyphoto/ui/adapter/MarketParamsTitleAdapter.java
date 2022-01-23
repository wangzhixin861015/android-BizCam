package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.bean.response.MarketParamsIndexListData;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.hyphoto.data.MarketTemplateData;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.bcnetech.hyphoto.utils.TestBase64Url;
import com.bcnetech.hyphoto.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;


/**
 * 光影市场分类的adapter
 * Market的preparams的Adapter
 * Created by a1234 on 17/2/14.
 */

public class MarketParamsTitleAdapter extends RecyclerView.Adapter<MarketParamsTitleAdapter.ViewHolder> {
    private int type;
    private List<MarketParamsIndexListData.PresetParmIndexManageItem> listdata;
    private Activity activity;
    private LayoutInflater mInflater;
    private int w;
    private ItemClickListener mItemClickListener;
    private Bitmap newbit;
    private final float sizeRation = 0.5f;
    private int clickPosition = -1;
    private int brforeposition = -1;


    public MarketParamsTitleAdapter(Activity activity, List<MarketParamsIndexListData.PresetParmIndexManageItem> listdata) {
        this.activity = activity;
        this.listdata = listdata;
        mInflater = LayoutInflater.from(activity);
        this.w = ContentUtil.getScreenWidth(activity) / 2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.title_item_market, parent, false);
        //  view.setLayoutParams(new RelativeLayout.LayoutParams(w, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
      //  holder.setIsRecyclable(false);
        holder.itemView.setTag(position);
        holder.tv_title.setText(listdata.get(position).getName());
        String bitmapId = listdata.get(position).getFileId();
        if (bitmapId != null) {
            if (bitmapId.contains(",")) {
                bitmapId = bitmapId.substring(0, listdata.get(position).getFileId().indexOf(","));
            }

            String url = StringUtil.getSizeUrl(BitmapUtils.getBitmapUrl6(bitmapId),
                    ContentUtil.getScreenWidth(activity),
                    0);
            LogUtil.d("eBiz:" + url);

            if (listdata.get(position).getFileId().equals(holder.iv_preparm.getTag())) {
            } else {
                holder.iv_preparm.setTag(listdata.get(position).getFileId());
                ImageUtil.EBizImageLoad(holder.iv_preparm, url, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        holder.iv_preparm.setImageBitmap(bitmap);
                        if (bitmap.getWidth() > bitmap.getHeight()) {
                            float resultWidth = bitmap.getHeight() / sizeRation;
                            if (resultWidth < bitmap.getWidth()) {
                                newbit = Bitmap.createBitmap(bitmap, 0, 0, (int) resultWidth, bitmap.getHeight());
                                holder.iv_preparm.setImageBitmap(newbit);
                            } else {
                                holder.iv_preparm.setImageBitmap(bitmap);
                            }
                        } else if (bitmap.getHeight() > bitmap.getWidth()) {
                            float resultHeight = bitmap.getWidth() / sizeRation;
                            if (resultHeight < bitmap.getHeight()) {
                                newbit = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), (int) resultHeight);
                                holder.iv_preparm.setImageBitmap(newbit);
                            } else {
                                holder.iv_preparm.setImageBitmap(bitmap);
                            }
                        }

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }
        } else {
            holder.iv_preparm.setImageDrawable(activity.getResources().getDrawable(R.drawable.loading));
        }


        if (clickPosition != -1) {
            if (position == clickPosition) {
                brforeposition = position;
                holder.tv_title.setBackgroundColor(Color.parseColor("#0057FF"));
            } else {
                holder.tv_title.setBackgroundColor(Color.parseColor("#ADB5C2"));
            }
        } else {
            //初始值将第一个设置为点击状态
            if (position == 0) {
                brforeposition = position;
                holder.tv_title.setBackgroundColor(Color.parseColor("#0057FF"));
            } else {
                holder.tv_title.setBackgroundColor(Color.parseColor("#ADB5C2"));
            }
        }
    }


    @Override
    public int getItemCount() {
        return listdata == null ? 0 : listdata.size();
    }

    public void setData(List list) {
        this.listdata = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView iv_preparm;
        public TextView tv_title;

        public ViewHolder(View view) {
            super(view);

            iv_preparm = (ImageView) view.findViewById(R.id.iv_preparm);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            iv_preparm.setVisibility(View.VISIBLE);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickPosition = (Integer) itemView.getTag();
            if (mItemClickListener != null) {
                mItemClickListener.OnItemClick(v, (Integer) itemView.getTag());
            }
           // notifyDataSetChanged();
            notifyItemChanged(clickPosition);
            notifyItemChanged(brforeposition);
        }
    }


    private int calculateHeight(MarketTemplateData marketTemplateData) {
        int itemheight = marketTemplateData.getCover_height();
        int itemwidth = marketTemplateData.getCover_width();
        double scale = itemwidth * 1.0 / this.w;
        int resultH = (int) (itemheight * 1.0 / scale);
        LogUtil.d(w + "  " + resultH);

        return resultH;
    }

    public interface ItemClickListener {
        void OnItemClick(View v, int position);
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }


    private String getBase64Code() {
        String code = "";
        String url = "[{\"scale\":{\"w\":150}}]";
        code = TestBase64Url.base64UrlEncode(url.getBytes());
        return code;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setClickPosition(int position){
        this.clickPosition = position;
        notifyItemChanged(clickPosition);
        notifyItemChanged(brforeposition);
    }
}

