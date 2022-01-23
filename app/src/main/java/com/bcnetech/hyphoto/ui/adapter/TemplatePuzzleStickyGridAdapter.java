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
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.view.stickygridheaders.StickyGridHeadersSimpleAdapter;
import com.bcnetech.hyphoto.bizInterface.ItemClickInterface;
import com.bcnetech.hyphoto.data.GridItem;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

public class TemplatePuzzleStickyGridAdapter extends BaseAdapter implements
        StickyGridHeadersSimpleAdapter {

    public final static int TWO_TYPE = 1;
    private GridView mGridView = null;
    private LayoutInflater mInflater;
    //private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
    private int layoutType = TWO_TYPE;
    private int width;
    private int hight;
    private List<GridItem> listData;
    private Context context;

    private ItemClickInterface itemClickInterface;

    public TemplatePuzzleStickyGridAdapter(Context context, List<GridItem> list, GridView mGridView) {
        this.mGridView = mGridView;
        this.listData = list;
        this.context = context;
        mInflater = LayoutInflater.from(context);
        width = hight = (ContentUtil.getScreenWidth(context) - ContentUtil.dip2px(context, 3)) / 2;
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
            mViewHolder.mImageView = (ImageView) convertView.findViewById(R.id.grid_item);

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
            convertView.setLayoutParams(new AbsListView.LayoutParams(width,hight));
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
            if (listData.get(position).getImageData().isMatting()) {
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
        mHeaderHolder.mTextView.setText(listData.get(position).getTime());

        return convertView;
    }

    public class ViewHolder {
        public ImageView grid_item_check;
        public ImageView mImageView;
    }

    public class HeaderViewHolder {
        public TextView mTextView;
    }


    @Override
    public long getHeaderId(int position) {
        return listData.get(position).getSection();
    }



    public void setData(List listData) {
        this.listData = listData;
    }


    public void setItemClickInterface(ItemClickInterface itemClickInterface) {
        this.itemClickInterface = itemClickInterface;
    }
}
