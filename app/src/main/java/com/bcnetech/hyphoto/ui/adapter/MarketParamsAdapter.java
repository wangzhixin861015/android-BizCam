package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.bean.response.MarketParamsListData;
import com.bcnetech.bcnetechlibrary.drawable.CircleDefultDrawable;
import com.bcnetech.bcnetechlibrary.drawable.CircleImageDrawable;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.hyphoto.data.MarketTemplateData;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.FontImageUtil;
import com.bcnetech.hyphoto.utils.PinyUtil;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.bcnetech.hyphoto.utils.TestBase64Url;
import com.bcnetech.hyphoto.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * 光影市场二级的adapter
 * Market的preparams的Adapter
 * Created by a1234 on 17/2/14.
 */

public class MarketParamsAdapter extends RecyclerView.Adapter<MarketParamsAdapter.ViewHolder> {
    private int type;
    private List<MarketParamsListData.PresetParmManageItem> listdata;
    private Activity activity;
    private LayoutInflater mInflater;
    private int w;
    private ItemClickListener mItemClickListener;
    private Bitmap newbit;
    private final float sizeRation = 0.5f;
    private int updatepisition = -1;
    private int width;
    private int hight;


    public MarketParamsAdapter(Activity activity, List<MarketParamsListData.PresetParmManageItem> listdata) {
        this.activity = activity;
        this.listdata = listdata;
        mInflater = LayoutInflater.from(activity);
        this.w = ContentUtil.getScreenWidth(activity) / 2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_market, parent, false);
        //  view.setLayoutParams(new RelativeLayout.LayoutParams(w, ViewGroup.LayoutParams.WRAP_CONTENT));
        width = hight = (ContentUtil.getScreenWidth(activity));
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.itemView.setTag(position);
        holder.iv_preparm.setLayoutParams(new RelativeLayout.LayoutParams(width,hight));
        final String author = listdata.get(position).getAutherName();
        if (author != null && !TextUtils.isEmpty(author)) {
            holder.tv_author.setText(author);
        } else {
            holder.tv_author.setText(activity.getResources().getString(R.string.noname));
        }
        holder.tv_deviceType.setText(listdata.get(position).getDeviceType());
        String isdownload = listdata.get(position).getIsDownload();
//        if (isdownload.equals("0")) {
            //未下载过
            holder.tv_download.setBackground(activity.getResources().getDrawable(R.drawable.blue_item_bg));
            holder.tv_download.setClickable(true);
            holder.tv_download.setTextColor(activity.getResources().getColor(R.color.white));

            ImageSpan imgspan = new ImageSpan(activity, R.drawable.download_white);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("  " + activity.getResources().getString(R.string.download));
            spannableStringBuilder.setSpan(imgspan, 0, 1, ImageSpan.ALIGN_BASELINE);
            holder.tv_download.setText(spannableStringBuilder);
//        } else {
//
//            holder.tv_download.setBackground(activity.getResources().getDrawable(R.drawable.grey_item_bg));
//            holder.tv_download.setClickable(false);
//            holder.tv_download.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//
//            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(activity.getResources().getString(R.string.downloaded));
//            spannableStringBuilder.setSpan(null, 0, 1, ImageSpan.ALIGN_BASELINE);
//            holder.tv_download.setText(spannableStringBuilder);
//        }
//        if (updatepisition != -1 && updatepisition == position) {
//            listdata.get(position).setIsDownload("1");
//            holder.tv_download.setBackground(activity.getResources().getDrawable(R.drawable.grey_item_bg));
//            holder.tv_download.setClickable(false);
//            holder.tv_download.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//
//            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(activity.getResources().getString(R.string.downloaded));
//            spannableStringBuilder.setSpan(null, 0, 1, ImageSpan.ALIGN_BASELINE);
//
//            holder.tv_download.setText(spannableStringBuilder);
//        }
        // holder.tv_downloadCount.setText(listdata.get(position).getDownloadCount()+"");

//        holder.tv_detail.setText(listdata.get(position).getName());
        String url = StringUtil.getSizeUrl(BitmapUtils.getBitmapUrl6(listdata.get(position).getCoverId()),
                width,
                0);
        LogUtil.d("eBiz" + url);
        /*com.example.bizcan.utils.ImageUtil.EBizImageLoad(holder.iv_preparm, url, new ImageLoadingListener() {
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
        });*/
        Picasso.get().load(url).into(holder.iv_preparm);

        holder.tv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onDown(v,(Integer)holder.itemView.getTag());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onPresetInfo(v,(Integer)holder.itemView.getTag());
            }
        });

        if (!StringUtil.isBlank(listdata.get(position).getAutherUrl())) {
            String headurl = BitmapUtils.getBitmapUrl3(listdata.get(position).getAutherUrl());
            ImageUtil.EBizImageLoad(holder.iv_author, headurl, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                    setImgHead(author, holder.iv_author);
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (bitmap != null) {
                        holder.iv_author.setImageDrawable(new CircleImageDrawable(bitmap));
                    } else if (author != null && !TextUtils.isEmpty(author)) {
                        String f = PinyUtil.getSpells(author);
                        holder.iv_author.setImageDrawable(new CircleDefultDrawable(FontImageUtil.setDefaultColor(activity, f), author.charAt(0) + ""));
                    } else {
                        holder.iv_author.setImageDrawable(new CircleDefultDrawable(FontImageUtil.setDefaultColor(activity, "匿"), "匿"));
                    }
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });


        } else {
            setImgHead(author, holder.iv_author);
        }
    }

    public void setImgHead(String author, ImageView view) {
        if (author != null && !TextUtils.isEmpty(author)) {
            String f = PinyUtil.getSpells(author);
            view.setImageDrawable(new CircleDefultDrawable(FontImageUtil.setDefaultColor(activity, f), author.charAt(0) + ""));
        } else {
            view.setImageDrawable(new CircleDefultDrawable(FontImageUtil.setDefaultColor(activity, "匿"), "匿"));
        }
    }

    @Override
    public int getItemCount() {
        return listdata == null ? 0 : listdata.size();
    }

    public void setData(List list) {
        this.listdata = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_preparm;
        public ImageView iv_template;
        public ImageView iv_author;
        public TextView tv_author;
        //        public TextView tv_detail;
        public TextView tv_deviceType;
        public TextView tv_downloadCount;
        public TextView tv_download;

        public ViewHolder(View view) {
            super(view);

            iv_template = (ImageView) view.findViewById(R.id.iv_template);
            iv_preparm = (ImageView) view.findViewById(R.id.iv_preparm);
            iv_author = (ImageView) view.findViewById(R.id.iv_author);
            tv_author = (TextView) view.findViewById(R.id.tv_author);
            tv_deviceType = (TextView) view.findViewById(R.id.tv_deviceType);
          //  tv_downloadCount = (TextView) view.findViewById(R.id.tv_downloadCount);
            tv_download = (TextView) view.findViewById(R.id.tv_download);
//            tv_detail = (TextView) view.findViewById(R.id.tv_detail);
            iv_template.setVisibility(View.GONE);
            iv_preparm.setVisibility(View.VISIBLE);
            // iv_preparm.setType(RoundRectImageView.MARKET_MODEL);





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
        void onDown(View v, int position);
        void onPresetInfo(View v, int position);
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

    public void updateDownload(int position) {
        this.updatepisition = position;
        this.notifyDataSetChanged();
    }

}

