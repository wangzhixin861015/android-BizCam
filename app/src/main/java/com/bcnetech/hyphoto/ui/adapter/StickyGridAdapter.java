package com.bcnetech.hyphoto.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.sharepreference.SharePreferences;
import com.bcnetech.bcnetechlibrary.view.stickygridheaders.StickyGridHeadersGridView;
import com.bcnetech.bcnetechlibrary.view.stickygridheaders.StickyGridHeadersSimpleAdapter;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.bizInterface.ItemClickInterface;
import com.bcnetech.hyphoto.data.GridItem;
import com.bcnetech.hyphoto.ui.view.RoundRectImageView;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StickyGridAdapter extends BaseAdapter implements
        StickyGridHeadersSimpleAdapter {

    public final static int TWO_TYPE = 1;
    public final static int FOUR_TYPE = 2;
    private GridView mGridView = null;
    private LayoutInflater mInflater;
    //private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
    private int layoutType = FOUR_TYPE;
    private int width;
    private int hight;
    private List<GridItem> listData;
    private Context context;
    private boolean change = false;
    private int checkposition;

    private ItemClickInterface itemClickInterface;

    public StickyGridAdapter(Context context, List<GridItem> list, GridView mGridView) {
        this.mGridView = mGridView;
        this.listData = list;
        this.context = context;
        mInflater = LayoutInflater.from(context);
        setType(FOUR_TYPE);
    }

    @Override
    public int getCount() {
        return listData == null ? 0 : listData.size();
    }


    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.grid_item, parent, false);
            mViewHolder.mImageView = (RoundRectImageView) convertView.findViewById(R.id.grid_item);
            mViewHolder.grid_item_video = (ImageView) convertView.findViewById(R.id.grid_video);
            mViewHolder.grid_item_check = (ImageView) convertView.findViewById(R.id.grid_item_check);
            mViewHolder.mImageView.setTag(R.id.tag_first, 0);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        if ((int) mViewHolder.mImageView.getTag(R.id.tag_first) != layoutType) {
            mViewHolder.mImageView.getLayoutParams().height = hight;
            mViewHolder.mImageView.getLayoutParams().width = width;
            mViewHolder.grid_item_check.getLayoutParams().height = hight;
            mViewHolder.grid_item_check.getLayoutParams().width = width;
            if (change) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ContentUtil.dip2px(context, 27), ContentUtil.dip2px(context, 27));
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.setMargins(ContentUtil.dip2px(context, 6), 0, 0, ContentUtil.dip2px(context, 6));
                mViewHolder.grid_item_video.setLayoutParams(layoutParams);
            } else {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ContentUtil.dip2px(context, 56), ContentUtil.dip2px(context, 56));
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.setMargins(ContentUtil.dip2px(context, 13), 0, 0, ContentUtil.dip2px(context, 13));
                mViewHolder.grid_item_video.setLayoutParams(layoutParams);
            }
            convertView.setLayoutParams(new AbsListView.LayoutParams(width, hight));
        }


        if (listData.get(position).ischeck()) {
            mViewHolder.grid_item_check.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.grid_item_check.setVisibility(View.INVISIBLE);
        }

        String path = listData.get(position).getPath();
        if (StringUtil.isBlank(path)) {
            convertView.setEnabled(false);
            convertView.setAlpha(0f);
        } else {
            convertView.setAlpha(1f);
            convertView.setEnabled(true);
            if (listData.get(position).getImageData().isMatting()&&listData.get(position).getImageData().getType()==Flag.TYPE_PIC) {
                mViewHolder.mImageView.setBackground(context.getResources().getDrawable(R.drawable.bgbitmap2));
            }

            if (mViewHolder.mImageView.getTag(R.id.tag_second) == null || !mViewHolder.mImageView.getTag(R.id.tag_second).equals(path)) {
                mViewHolder.mImageView.setTag(R.id.tag_second, path);
                ImageUtil.EBizImageLoad(mViewHolder.mImageView, path, width, hight, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                       /* if (!view.getTag(R.id.tag_second).equals(s)) {
                            ((ImageView) view).setImageBitmap(bitmap);
                            AnimFactory.ImgAlphaAnim(view).start();
                        }*/
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
                if (listData.get(position).getImageData().getType() == Flag.TYPE_VIDEO) {
                    mViewHolder.mImageView.setType(RoundRectImageView.VIDEO_MODEL);
                    mViewHolder.grid_item_video.setVisibility(View.VISIBLE);
                } else {
                    mViewHolder.mImageView.setType(RoundRectImageView.NORMAL_MODEL);
                    mViewHolder.grid_item_video.setVisibility(View.GONE);
                }
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickInterface != null) {
                        itemClickInterface.itemClick(v, position);
                    }
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickInterface != null) {
                        return itemClickInterface.itemLongClick(v, position);
                    }
                    return false;
                }
            });
        }
        return convertView;
    }


    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder mHeaderHolder;
        if (convertView == null) {
            mHeaderHolder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header2, parent, false);
            mHeaderHolder.mTextView = (TextView) convertView.findViewById(R.id.header);
            convertView.setTag(mHeaderHolder);
        } else {
            mHeaderHolder = (HeaderViewHolder) convertView.getTag();
        }
        //  mHeaderHolder.mTextView.setText(listData.get(position).getTime());
        String newdata = convertDate(listData.get(position).getTime());
        mHeaderHolder.mTextView.setText(newdata);
        return convertView;
    }

    public class ViewHolder {
        public ImageView grid_item_check;
        public RoundRectImageView mImageView;
        public ImageView grid_item_video;
    }

    public class HeaderViewHolder {
        public TextView mTextView;
    }

    private String convertDate(String data){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String formatdata = "";
        try {
            Date d = sdf.parse(data);
            sdf = new SimpleDateFormat("yyyy-M-d");
            formatdata =  sdf.format(d);


            Calendar cal = Calendar.getInstance();
            cal.setTime(d);

            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                formatdata += "  "+context.getResources().getString(R.string.monday);
            }
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
                formatdata += "  "+context.getResources().getString(R.string.tuesday);
            }
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
                formatdata += "  "+context.getResources().getString(R.string.wednesday);
            }
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
                formatdata += "  "+context.getResources().getString(R.string.thursday);
            }
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                formatdata += "  "+context.getResources().getString(R.string.friday);
            }
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                formatdata += "  "+context.getResources().getString(R.string.saturday);
            }
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                formatdata += "  "+context.getResources().getString(R.string.sunday);
            }
        } catch (ParseException e){

        }
        /////
        return formatdata;
    }


    @Override
    public long getHeaderId(int position) {
        return listData.get(position).getSection();
    }

    public void setType(int type) {
        SharePreferences sharePreferences = SharePreferences.instance();
        switch (type) {
            case FOUR_TYPE:
                sharePreferences.putBoolean("AblumStatus", false);
                width = hight = (ContentUtil.getScreenWidth(context) - ContentUtil.dip2px(context, 3) * 3) / 4;
                layoutType = FOUR_TYPE;
                mGridView.setNumColumns(4);
                ((StickyGridHeadersGridView) mGridView).upData();
                change = true;
                break;
            case TWO_TYPE:
                sharePreferences.putBoolean("AblumStatus", true);
                width = hight = (ContentUtil.getScreenWidth(context) - ContentUtil.dip2px(context, 3)) / 2;
                layoutType = TWO_TYPE;
                mGridView.setNumColumns(2);
                ((StickyGridHeadersGridView) mGridView).upData();
                change = false;
                break;
        }
        //notifyDataSetChanged();
    }

    public void setData(List listData) {
        this.listData = listData;
    }


    public void getCheckPosition() {

    }

    public void setItemClickInterface(ItemClickInterface itemClickInterface) {
        this.itemClickInterface = itemClickInterface;
    }
}
