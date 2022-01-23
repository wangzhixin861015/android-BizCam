package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetchhttp.bean.response.MarketParamsListData;
import com.bcnetech.bcnetechlibrary.drawable.CircleDefultDrawable;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.MarketTemplateData;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.FontImageUtil;
import com.bcnetech.hyphoto.utils.PinyUtil;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.bcnetech.hyphoto.utils.TestBase64Url;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * 光影市场一级的adapter
 * Created by a1234 on 17/2/14.
 */

public class MarketParamsAdapter2 extends RecyclerView.Adapter<MarketParamsAdapter2.ViewHolder> {
    private int type;
    private List<MarketParamsListData.PresetParmManageItem> listdata;
    private Activity activity;
    private LayoutInflater mInflater;
    private int w;
    private ItemClickListener mItemClickListener;
    private Bitmap newbit;
    private final float sizeRation = 0.5f;
    private int width;
    private int hight;

    public MarketParamsAdapter2(Activity activity, List<MarketParamsListData.PresetParmManageItem> listdata) {
        this.activity = activity;
        this.listdata = listdata;
        mInflater = LayoutInflater.from(activity);
        width = hight = (ContentUtil.getScreenWidth(activity) - ContentUtil.dip2px(activity, 3)) / 3;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_market2, parent, false);
        //  view.setLayoutParams(new RelativeLayout.LayoutParams(w, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.itemView.setTag(position);
        holder.iv_preparm.setLayoutParams(new RelativeLayout.LayoutParams(width,hight));

        // final String author = listdata.get(position).getAutherName();
//        holder.tv_detail.setText(listdata.get(position).getName());
        String url=StringUtil.getSizeUrl(BitmapUtils.getBitmapUrl6(listdata.get(position).getUrl()),
                ContentUtil.getScreenWidth(activity)/2,
                0);
        LogUtil.d("eBiz"+url);
        Picasso.get().load(url).placeholder(R.color.shape_grey_color).into(holder.iv_preparm);
        //com.example.bizcan.utils.ImageUtil.EBizImageLoad(holder.iv_preparm, url);
                /*if (bitmap.getWidth()>bitmap.getHeight()){
                    float resultWidth = bitmap.getHeight()/sizeRation;
                    if (resultWidth<bitmap.getWidth()){
                        newbit = Bitmap.createBitmap(bitmap,0,0,(int)resultWidth,bitmap.getHeight());
                        holder.iv_preparm.setImageBitmap(newbit);
                    }else{
                        holder.iv_preparm.setImageBitmap(bitmap);
                    }
                }else if(bitmap.getHeight()>bitmap.getWidth()){
                    float resultHeight = bitmap.getWidth()/sizeRation;
                    if (resultHeight<bitmap.getHeight()){
                        newbit = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),(int)resultHeight);
                        holder.iv_preparm.setImageBitmap(newbit);
                    }else{
                        holder.iv_preparm.setImageBitmap(bitmap);
                    }
                }*/




    }

    public void setImgHead(String author,ImageView view){
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView iv_preparm;

        public ViewHolder(View view) {
            super(view);

            iv_preparm = (ImageView) view.findViewById(R.id.iv_preparm);
            iv_preparm.setVisibility(View.VISIBLE);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.OnItemClick(v, (Integer) itemView.getTag());
            }

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

    public void updatePages(List<String> pages) {
        notifyItemChanged(listdata.size());
    }

    public void updateCurPage(int page) {
        notifyItemChanged(listdata.size());
    }

}

