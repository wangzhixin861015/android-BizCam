package com.bcnetech.hyphoto.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.sharepreference.SharePreferences;
import com.bcnetech.bcnetechlibrary.view.stickygridheaders.StickyGridHeadersGridView;
import com.bcnetech.bcnetechlibrary.view.stickygridheaders.StickyGridHeadersSimpleAdapter;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.bizInterface.ItemClickInterface;
import com.bcnetech.hyphoto.data.GridItem;
import com.bcnetech.hyphoto.task.manage.UploadManager;
import com.bcnetech.hyphoto.utils.DataTimeUtils;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import static android.view.View.VISIBLE;

public class StickyGridNewAdapter extends BaseAdapter implements
        StickyGridHeadersSimpleAdapter {

    public final static int TWO_TYPE = 1;
    public final static int FOUR_TYPE = 2;
    public final static int THREE_TYPE = 3;
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
    private boolean isneedHeader = true;
    private int count = -1;
    private int httpType = UploadManager.NULL;

    //大图
    final int TYPE_1 = 0;
    //小图
    final int TYPE_2 = 1;

    private ItemClickInterface itemClickInterface;

    public StickyGridNewAdapter(Context context, List<GridItem> list, GridView mGridView) {
        this.mGridView = mGridView;
        this.listData = list;
        this.context = context;
        mInflater = LayoutInflater.from(context);
        setType(THREE_TYPE);
    }

    @Override
    public int getItemViewType(int position) {

        GridItem gridItem = listData.get(position);
        if (gridItem.getType().equals("0")) {
            return TYPE_1;
        } else {
            return TYPE_2;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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


        int type = getItemViewType(position);
        ViewHolder mViewHolder = null;
        SmallPicViewHolder smallPicViewHolder = null;
        if (convertView == null) {
            switch (type) {
                case TYPE_1:
                    mViewHolder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.grid_item2, parent, false);
                    mViewHolder.mImageView = (ImageView) convertView.findViewById(R.id.grid_item);
                    mViewHolder.grid_item_video = (ImageView) convertView.findViewById(R.id.grid_video);
                    mViewHolder.grid_item_check = (ImageView) convertView.findViewById(R.id.grid_item_check);
                    mViewHolder.tv_360=convertView.findViewById(R.id.tv_360);
                    mViewHolder.mImageView.setTag(R.id.tag_first, 0);
                    convertView.setTag(mViewHolder);
                    break;
                case TYPE_2:
                    smallPicViewHolder = new SmallPicViewHolder();
                    convertView = mInflater.inflate(R.layout.adapter_images_item, parent, false);
                    smallPicViewHolder.grid_item = (ImageView) convertView.findViewById(R.id.grid_item);
                    smallPicViewHolder.grid_item_check = (ImageView) convertView.findViewById(R.id.grid_item_check);
                    smallPicViewHolder.grid_video = (ImageView) convertView.findViewById(R.id.grid_video);
                    smallPicViewHolder.tv_360=convertView.findViewById(R.id.tv_360);
                    convertView.setTag(smallPicViewHolder);
                    break;
            }

        } else {
            switch (type) {
                case TYPE_1:
                    mViewHolder = (ViewHolder) convertView.getTag();
                    break;
                case TYPE_2:
                    smallPicViewHolder = (SmallPicViewHolder) convertView.getTag();
                    break;
            }

        }
        switch (type) {
            case TYPE_1:
                if ((int) mViewHolder.mImageView.getTag(R.id.tag_first) != layoutType) {
                    mViewHolder.mImageView.getLayoutParams().height = hight;
                    mViewHolder.mImageView.getLayoutParams().width = width;
                    mViewHolder.grid_item_check.getLayoutParams().height = hight;
                    mViewHolder.grid_item_check.getLayoutParams().width = width;
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
                    if (listData.get(position).getImageData().getLocalUrl().contains(".png")) {
                        mViewHolder.mImageView.setBackground(context.getResources().getDrawable(R.drawable.bgbitmap2));
                    }

                    if (mViewHolder.mImageView.getTag(R.id.tag_second) == null || !mViewHolder.mImageView.getTag(R.id.tag_second).equals(path)) {
                        mViewHolder.mImageView.setTag(R.id.tag_second, path);
                        // ImageUtil.EBizImageLoad(mViewHolder.mImageView,path,width,hight);
                        if (listData.get(position).getImageData().getType() == Flag.TYPE_VIDEO) {
                            //ImageUtil.EBizImageLoad(mViewHolder.mImageView, path, width, hight);
                            Picasso.get().load(path).resize(width, hight).centerCrop().into(mViewHolder.mImageView);
                            mViewHolder.grid_item_video.setVisibility(View.VISIBLE);
                            mViewHolder.grid_item_video.setImageDrawable(context.getResources().getDrawable(R.drawable.video));
                            mViewHolder.tv_360.setVisibility(View.GONE);
                        }else if(listData.get(position).getImageData().getType() == Flag.TYPE_AI360){
                            Picasso.get().load(path).resize(width, hight).centerCrop().into(mViewHolder.mImageView);
                            // ImageUtil.EBizImageLoad(mViewHolder.mImageView, path, width, hight);
                            mViewHolder.grid_item_video.setVisibility(View.INVISIBLE);
                            mViewHolder.tv_360.setVisibility(VISIBLE);
                        } else {
                           /* if (FileUtil.getImageType(listData.get(position).getImageData().getLocalUrl().substring(7)).equals("gif")) {
                                ImageUtil.EBizImageLoad(mViewHolder.mImageView, path, width, hight);
                                mViewHolder.grid_item_video.setVisibility(View.VISIBLE);
                                mViewHolder.grid_item_video.setImageDrawable(context.getResources().getDrawable(R.drawable.gif));
                            } else {*/
                            Picasso.get().load(path).resize(width, hight).centerCrop().into(mViewHolder.mImageView);
                            // ImageUtil.EBizImageLoad(mViewHolder.mImageView, path, width, hight);
                            mViewHolder.grid_item_video.setVisibility(View.INVISIBLE);
                            mViewHolder.tv_360.setVisibility(View.GONE);

                            // }
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
                break;
            case TYPE_2:

                if (listData.get(position).ischeck()) {
                    smallPicViewHolder.grid_item_check.setVisibility(View.VISIBLE);
                } else {
                    smallPicViewHolder.grid_item_check.setVisibility(View.INVISIBLE);
                }

                String path1 = listData.get(position).getPath();
                if (StringUtil.isBlank(path1)) {
                    convertView.setEnabled(false);
                    convertView.setAlpha(0f);
                } else {
                    convertView.setAlpha(1f);
                    convertView.setEnabled(true);
                    if (listData.get(position).getImageData().getLocalUrl().contains(".png")) {
                        smallPicViewHolder.grid_item.setBackground(context.getResources().getDrawable(R.drawable.bgbitmap2));
                    }
                    if (smallPicViewHolder.grid_item.getTag(R.id.tag_second) == null || !smallPicViewHolder.grid_item.getTag(R.id.tag_second).equals(path1)) {
                        smallPicViewHolder.grid_item.setTag(R.id.tag_second, path1);
                        if (listData.get(position).getImageData().getLocalUrl().endsWith(".gif")) {
                            ImageUtil.EBizImageLoad(smallPicViewHolder.grid_item, path1, width, hight);
                        } else {
                            Picasso.get().load(path1).resize(width, hight).centerCrop().into(smallPicViewHolder.grid_item);
                        }
                        if (listData.get(position).getImageData().getType() == Flag.TYPE_VIDEO) {
                            smallPicViewHolder.grid_video.setVisibility(View.VISIBLE);
                            smallPicViewHolder.tv_360.setVisibility(View.GONE);

                        } else if(listData.get(position).getImageData().getType() == Flag.TYPE_AI360){
                            smallPicViewHolder.tv_360.setVisibility(VISIBLE);
                            smallPicViewHolder.grid_video.setVisibility(View.INVISIBLE);

                        } else {
                            smallPicViewHolder.grid_video.setVisibility(View.INVISIBLE);
                            smallPicViewHolder.tv_360.setVisibility(View.GONE);

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
                break;
        }

        return convertView;
    }


    @Override
    public View getHeaderView(final int position, View convertView, ViewGroup parent) {
        final HeaderViewHolder mHeaderHolder;
        if (convertView == null) {
            mHeaderHolder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header2_new, parent, false);
            mHeaderHolder.mTextView = (TextView) convertView.findViewById(R.id.header);
            mHeaderHolder.ll_upload = (LinearLayout) convertView.findViewById(R.id.ll_upload);
            mHeaderHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            mHeaderHolder.grid_item = (ImageView) convertView.findViewById(R.id.grid_item);
            mHeaderHolder.tv_hint = (TextView) convertView.findViewById(R.id.tv_hint);
            mHeaderHolder.tv_refresh = (TextView) convertView.findViewById(R.id.tv_refresh);
            convertView.setTag(mHeaderHolder);
        } else {
            mHeaderHolder = (HeaderViewHolder) convertView.getTag();
        }
        //  mHeaderHolder.mTextView.setText(listData.get(position).getTime());
        String newdata = convertDate(listData.get(position).getTime());
        mHeaderHolder.mTextView.setText(newdata);
        final LoginedUser loginedUser = LoginedUser.getLoginedUser();

        if (position == 0 && listData.size() > 1) {
            mHeaderHolder.ll_upload.setVisibility(View.VISIBLE);
            mHeaderHolder.tv_time.setVisibility(View.VISIBLE);

            mHeaderHolder.ll_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickInterface.headClick(v, position, mHeaderHolder);
                }
            });

            mHeaderHolder.tv_refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickInterface.refreshUpload(v, position, mHeaderHolder);
                }
            });

            if (UploadManager.getInstance().getHttpType() == UploadManager.WIFI) {
                if (loginedUser.getUploadStatus() == Flag.UPLOAD_UPLOADING) {

                    mHeaderHolder.tv_hint.setText(context.getString(R.string.in_the_backup));
                    mHeaderHolder.ll_upload.setBackground(context.getDrawable(R.drawable.album_head_bg));
                    mHeaderHolder.grid_item.setImageResource(R.drawable.oval);
                    mHeaderHolder.tv_time.setText(context.getString(R.string.in_the_backup_num) + String.format("%02d", UploadManager.getInstance().getUploadCount()));
                    mHeaderHolder.tv_refresh.setVisibility(View.GONE);
                } else if (loginedUser.getUploadStatus() == Flag.UPLOAD_REUPLOAD) {

                    mHeaderHolder.tv_hint.setText(context.getString(R.string.in_the_backup));
                    mHeaderHolder.ll_upload.setBackground(context.getDrawable(R.drawable.album_head_bg));
                    mHeaderHolder.grid_item.setImageResource(R.drawable.oval);
                    mHeaderHolder.tv_time.setText(context.getString(R.string.in_the_backup_num) + String.format("%02d", listData.size() - 1));
                    mHeaderHolder.tv_refresh.setVisibility(View.GONE);
                } else if (loginedUser.getUploadStatus() == Flag.UPLOAD_NONE) {
                    mHeaderHolder.tv_hint.setText(context.getString(R.string.upload_backup));
                    mHeaderHolder.ll_upload.setBackground(context.getDrawable(R.drawable.album_head_bg));
                    mHeaderHolder.grid_item.setImageResource(R.drawable.new_upload_white);
                    if (!StringUtil.isBlank((loginedUser.getUploadTime()))) {
                        mHeaderHolder.tv_time.setText(context.getString(R.string.to_backup_time) + convertDate2(loginedUser.getUploadTime()));
                    } else {
                        mHeaderHolder.tv_time.setText(context.getString(R.string.automatically));
                    }
                    mHeaderHolder.tv_refresh.setVisibility(View.GONE);
                } else if (loginedUser.getUploadStatus() == Flag.UPLOAD_SUCCESS) {
                    mHeaderHolder.tv_hint.setText(context.getString(R.string.to_backup_success));
                    mHeaderHolder.ll_upload.setBackground(context.getDrawable(R.drawable.album_head_bg));
                    mHeaderHolder.grid_item.setImageResource(R.drawable.uoload_success);
                    mHeaderHolder.tv_time.setText(context.getString(R.string.to_backup_time) + convertDate2(loginedUser.getUploadTime()));
                    mHeaderHolder.tv_refresh.setVisibility(View.GONE);
                } else if (loginedUser.getUploadStatus() == Flag.UPLOAD_FULL) {

                    mHeaderHolder.tv_hint.setText(context.getString(R.string.to_backup_suspended));
                    mHeaderHolder.grid_item.setImageResource(R.drawable.oval);

                    mHeaderHolder.ll_upload.setBackground(context.getDrawable(R.drawable.album_head_red_bg));
                    mHeaderHolder.tv_time.setText(context.getString(R.string.to_backup_space_error));
                    mHeaderHolder.tv_refresh.setVisibility(VISIBLE);
                    mHeaderHolder.tv_refresh.setText(Html.fromHtml("<font color='#0057FF'>" + context.getString(R.string.refresh) + "</font>"));

                } else if (loginedUser.getUploadStatus() == Flag.UPLOAD_FAIL) {

                    mHeaderHolder.tv_hint.setText(context.getString(R.string.to_backup_suspended));
                    mHeaderHolder.grid_item.setImageResource(R.drawable.oval);

                    mHeaderHolder.ll_upload.setBackground(context.getDrawable(R.drawable.album_head_red_bg));
                    mHeaderHolder.tv_time.setText(context.getString(R.string.to_backup_server_error));
                    mHeaderHolder.tv_refresh.setVisibility(VISIBLE);
                    mHeaderHolder.tv_refresh.setText(Html.fromHtml("<font color='#0057FF'>" + context.getString(R.string.refresh) + "</font>"));
                }
            } else {
//                if(loginedUser.getUploadStatus()==Flag.UPLOAD_NETCHANGE){
//                    mHeaderHolder.tv_hint.setText(context.getString(R.string.to_backup_suspended));
//                    mHeaderHolder.grid_item.setImageResource(R.drawable.oval);
//
//                    mHeaderHolder.ll_upload.setBackground(context.getDrawable(R.drawable.album_gray_bg));
//                    mHeaderHolder.tv_time.setText(context.getString(R.string.to_backup_wifi_error) + String.format("%02d",  UploadManager.getInstance().getUploadCount()));
//                    mHeaderHolder.tv_refresh.setVisibility(View.GONE);
//                }else if(loginedUser.getUploadStatus()==Flag.UPLOAD_REUPLOAD){
//                    mHeaderHolder.tv_hint.setText(context.getString(R.string.to_backup_suspended));
//                    mHeaderHolder.grid_item.setImageResource(R.drawable.oval);
//
//                    mHeaderHolder.ll_upload.setBackground(context.getDrawable(R.drawable.album_gray_bg));
//                    mHeaderHolder.tv_time.setText(context.getString(R.string.to_backup_wifi_error) + String.format("%02d",  listData.size()-1));
//                    mHeaderHolder.tv_refresh.setVisibility(View.GONE);
//                }else if(loginedUser.getUploadStatus()==Flag.UPLOAD_FULL){
//                    mHeaderHolder.tv_hint.setText(context.getString(R.string.to_backup_suspended));
//                    mHeaderHolder.grid_item.setImageResource(R.drawable.oval);
//
//                    mHeaderHolder.ll_upload.setBackground(context.getDrawable(R.drawable.album_gray_bg));
//                    mHeaderHolder.tv_time.setText(context.getString(R.string.to_backup_wifi_error) + String.format("%02d",  listData.size()-1));
//                    mHeaderHolder.tv_refresh.setVisibility(View.GONE);
//                } else if(loginedUser.getUploadStatus()==Flag.UPLOAD_FAIL){
//                    mHeaderHolder.tv_hint.setText(context.getString(R.string.to_backup_suspended));
//                    mHeaderHolder.grid_item.setImageResource(R.drawable.oval);
//
//                    mHeaderHolder.ll_upload.setBackground(context.getDrawable(R.drawable.album_gray_bg));
//                    mHeaderHolder.tv_time.setText(context.getString(R.string.to_backup_wifi_error) + String.format("%02d",  listData.size()-1));
//                    mHeaderHolder.tv_refresh.setVisibility(View.GONE);
//                }
//                else
                if (loginedUser.getUploadStatus() == Flag.UPLOAD_NONE) {

                    mHeaderHolder.ll_upload.setBackground(context.getDrawable(R.drawable.album_head_bg));
                    mHeaderHolder.tv_refresh.setVisibility(View.GONE);
                    mHeaderHolder.grid_item.setImageResource(R.drawable.new_upload_white);

                    mHeaderHolder.tv_hint.setText(context.getString(R.string.upload_backup));

                    if (!StringUtil.isBlank((loginedUser.getUploadTime()))) {
                        mHeaderHolder.tv_time.setText(context.getString(R.string.to_backup_time) + convertDate2(loginedUser.getUploadTime()));
                    } else {
                        mHeaderHolder.tv_time.setText(context.getString(R.string.automatically));
                    }
                } else if (loginedUser.getUploadStatus() == Flag.UPLOAD_SUCCESS) {
                    mHeaderHolder.tv_hint.setText(context.getString(R.string.to_backup_success));
                    ViewGroup.LayoutParams layoutParams = mHeaderHolder.ll_upload.getLayoutParams();
                    mHeaderHolder.ll_upload.setLayoutParams(layoutParams);
                    mHeaderHolder.ll_upload.setBackground(context.getDrawable(R.drawable.album_head_bg));
                    mHeaderHolder.grid_item.setImageResource(R.drawable.uoload_success);
                    mHeaderHolder.tv_time.setText(context.getString(R.string.to_backup_time) + convertDate2(loginedUser.getUploadTime()));
                    mHeaderHolder.tv_refresh.setVisibility(View.GONE);
                } else {
                    mHeaderHolder.tv_hint.setText(context.getString(R.string.to_backup_suspended));
                    mHeaderHolder.grid_item.setImageResource(R.drawable.oval);

                    mHeaderHolder.ll_upload.setBackground(context.getDrawable(R.drawable.album_gray_bg));
                    mHeaderHolder.tv_time.setText(context.getString(R.string.to_backup_wifi_error) + String.format("%02d", listData.size() - 1));
                    mHeaderHolder.tv_refresh.setVisibility(View.GONE);
                }
            }

        } else {
            mHeaderHolder.ll_upload.setVisibility(View.GONE);
            mHeaderHolder.tv_time.setVisibility(View.GONE);
            mHeaderHolder.tv_refresh.setVisibility(View.GONE);
        }
        if (!isneedHeader) {
            mHeaderHolder.ll_upload.setVisibility(View.GONE);
            mHeaderHolder.tv_time.setVisibility(View.GONE);
            mHeaderHolder.tv_refresh.setVisibility(View.GONE);
        } else {
            convertView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        return convertView;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getHttpType() {
        return httpType;
    }

    public void setHttpType(int httpType) {
        this.httpType = httpType;
    }

    public void setIsneedHeader(boolean isneedHeader) {
        this.isneedHeader = isneedHeader;
        notifyDataSetChanged();
    }

    public class ViewHolder {
        public ImageView grid_item_check;
        public ImageView mImageView;
        public ImageView grid_item_video;
        public TextView tv_360;
    }

    public class SmallPicViewHolder {
        public ImageView grid_item;
        public ImageView grid_item_check;
        public ImageView grid_video;
        public TextView tv_360;
    }

    public class HeaderViewHolder {
        public TextView mTextView;
        public LinearLayout ll_upload;
        public TextView tv_time;
        public ImageView grid_item;
        public TextView tv_hint;
        public TextView tv_refresh;
    }

    private String convertDate(String date) {
        if (!StringUtil.isBlank(date)) {
            long data = Long.parseLong(DataTimeUtils.getTime(date));
            //日期格式
            String dateFormat = context.getString(R.string.format_date_no_year);
            String f1 = (String) DateFormat.format(dateFormat, data);
            //星期格式
           /* String weekFormat = context.getString(R.string.format_week);
            String f = (String) DateFormat.format(weekFormat, data);*/
            //return f1 + " " + f;
            return f1;
        }
        return "";
    }

    private String convertDate2(String uplaodTime) {
        if (!StringUtil.isBlank(uplaodTime)) {
            Date data = new Date(Long.valueOf(uplaodTime));
            //日期格式
            String dateFormat = context.getString(R.string.format_date_no_year_hh);
            String f1 = (String) DateFormat.format(dateFormat, data);
            //星期格式
           /* String weekFormat = context.getString(R.string.format_week);
            String f = (String) DateFormat.format(weekFormat, data);*/
            //return f1 + " " + f;
            return f1;
        }
        return "";
    }


    @Override
    public long getHeaderId(int position) {
        return listData.get(position).getSection();
    }

    public void setType(int type) {
        SharePreferences sharePreferences = SharePreferences.instance();
        switch (type) {
            case FOUR_TYPE:
              /*  sharePreferences.putBoolean("AblumStatus", false);
                width = hight = (ContentUtil.getScreenWidth(context) - ContentUtil.dip2px(context, 3) * 3) / 4;
                layoutType = FOUR_TYPE;
                mGridView.setNumColumns(4);
                ((StickyGridHeadersGridView) mGridView).upData();
                change = true;
                break;*/
            case TWO_TYPE:
               /* sharePreferences.putBoolean("AblumStatus", true);
                width = hight = (ContentUtil.getScreenWidth(context) - ContentUtil.dip2px(context, 3)) / 2;
                layoutType = TWO_TYPE;
                mGridView.setNumColumns(2);
                ((StickyGridHeadersGridView) mGridView).upData();
                change = false;
                break;*/
            case THREE_TYPE:
                sharePreferences.putBoolean("AblumStatus", true);
                width = hight = (ContentUtil.getScreenWidth(context) - ContentUtil.dip2px(context, 3)) / 3;
                layoutType = TWO_TYPE;
                mGridView.setNumColumns(3);
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
