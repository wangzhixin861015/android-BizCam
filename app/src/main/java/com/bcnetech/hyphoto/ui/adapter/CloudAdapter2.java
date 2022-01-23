package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.drawable.CloudAblumDrawable;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.hanyupinyin.HanziToPinyin;
import com.bcnetech.bcnetechlibrary.view.IndexableRecycler.SectionedRecyclerAdapter;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.CloudPicture;
import com.bcnetech.hyphoto.ui.activity.cloud.CloundNewActivity;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * activity的云图库adapter
 * Created by yhf on 17/10/17.
 */
public class CloudAdapter2 extends RecyclerView.Adapter implements SectionedRecyclerAdapter.SectionedRecyclerDelegate{
    public static final int TYPE_BANNER = 0;
    private final LayoutInflater mLayoutInflater;
    private List<CloudPicture> listData;
    private int mLineNumber = 0;
    private int w;
    private boolean canDelet;
    private Activity activity;
    private int typeCode;

    private ClickInterFace clickInterFace;
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    LinkedHashMap<String, List<CloudPicture>> mSectionedHashMap;

    public CloudAdapter2(Activity activity, List<CloudPicture> listData, ClickInterFace clickInterFace, int typeCode) {
        this.activity=activity;
        this.listData = listData;
        this.clickInterFace=clickInterFace;
        mLayoutInflater = LayoutInflater.from(activity);
        w= ContentUtil.dip2px(activity,80);
        this.typeCode=typeCode;
        init();
    }

    private void init() {
        mSectionedHashMap = new LinkedHashMap<>();
        for (int i = 0;listData!=null&& i < listData.size(); i++) {
            String ch = HanziToPinyin.getFirstPinYinChar(listData.get(i).getName());
            if (ch == null || ch.isEmpty() || !Character.isUpperCase(ch.codePointAt(0)))
                ch = "#";
            List<CloudPicture> itemModels = mSectionedHashMap.get(ch);
            if (itemModels == null) {
                itemModels = new ArrayList<>();
            }
            itemModels.add(listData.get(i));
            mSectionedHashMap.put(ch, itemModels);
        }
        calculateSectionPosition();
    }

    private void calculateSectionPosition() {
        Set<String> keySet = mSectionedHashMap.keySet();
        String strings[] = new String[keySet.size()];
        keySet.toArray(strings);
        Arrays.sort(strings);
        int pos = 0;
        for (String title : strings) {
            SectionedRecyclerAdapter.Section section = new SectionedRecyclerAdapter.Section(pos, title);
            pos += mSectionedHashMap.get(title).size();
        }

        mLineNumber = pos;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BannerViewHolder(mLayoutInflater.inflate(R.layout.recycler_item_new, parent, false));
    }
    private List<String> stringTolist(String s) {
        String[] arr = StringUtil.isBlank(s)?new String[]{}:s.split(",");
        List<String> list = Arrays.asList(arr);
        return list;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CloudPicture cloudPicture=listData.get(position);
       final BannerViewHolder bannerViewHolder=(BannerViewHolder) holder;
       /* if(canDelet){
            bannerViewHolder.delete.setVisibility(View.VISIBLE);
            bannerViewHolder.delete.setTag(cloudPicture.getId()+"");
            bannerViewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id=String.valueOf(v.getTag());
                    deleteGroup(id);
                }
            });
        }
        else{
            bannerViewHolder.delete.setVisibility(View.GONE);
        }*/
        String filId = cloudPicture.getFileId();
        List<String> fileId =stringTolist(filId);
        if(typeCode== CloundNewActivity.CLOUD_CHISE) {
            if (cloudPicture.isselected()) {
                bannerViewHolder.item_layout.setBackground(activity.getResources().getDrawable(R.drawable.blue_background));
            }else{
                bannerViewHolder.item_layout.setBackgroundColor(Color.WHITE);
            }
        }
        bannerViewHolder.name.setText(cloudPicture.getName());
        bannerViewHolder.item_layout.setTag(cloudPicture);
        bannerViewHolder.item_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CloudPicture cloudPicture=(CloudPicture)v.getTag();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        cloudPicture.setIsselected(true);
                        for (int i=0;i<listData.size();i++){
                            if ((listData.get(i).getId())!=cloudPicture.getId()){
                                listData.get(i).setIsselected(false);
                            }
                        }
                        if(typeCode== CloundNewActivity.CLOUD_CHISE) {
                            bannerViewHolder.item_layout.setBackground(activity.getResources().getDrawable(R.drawable.blue_background));
                        }else{
                            bannerViewHolder.item_layout.setBackgroundColor(Color.WHITE);
                        }
                        clickInterFace.onClick(cloudPicture);
                        //  CloudAdapter.this.notifyDataSetChanged();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                }

                return true;
            }
        });
        if (fileId!=null) {
            if (fileId.size() > 0 && fileId.get(0) != null) {
                String url = BitmapUtils.getBitmapUrl2(fileId.get(0));
                ImageUtil.EBizImageLoad(StringUtil.getSizeUrl(url,w,w), w, w, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        Bitmap bmp= BitmapFactory.decodeResource(activity.getResources(), R.drawable.loading);
                        CloudAblumDrawable cloudAblumDrawable = new CloudAblumDrawable(bmp);
                        // bannerViewHolder.mImageView.setImageBitmap(loadedImage);
                        bannerViewHolder.mImageView.setImageDrawable(cloudAblumDrawable);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        CloudAblumDrawable cloudAblumDrawable = new CloudAblumDrawable(loadedImage);
                        // bannerViewHolder.mImageView.setImageBitmap(loadedImage);
                        bannerViewHolder.mImageView.setImageDrawable(cloudAblumDrawable);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }
        }
//
//        if(listData.get(position).getPhotos()!=null&&listData.get(position).getPhotos().size()>=1) {
//
//        }


    }
    public void setDelete(){
        canDelet=!canDelet;
    }
    @Override
    public int getItemCount() {
        return mLineNumber;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_BANNER;
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView name;
        TextView count;
        RelativeLayout item_layout;
        ImageView cloud_item_check;
        public BannerViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.item_image);
            name = (TextView) itemView.findViewById(R.id.name);
            item_layout=(RelativeLayout) itemView.findViewById(R.id.item_layout);
            count=(TextView) itemView.findViewById(R.id.count);
            cloud_item_check = (ImageView)itemView.findViewById(R.id.cloud_item_check);
        }
    }


    public void setListData(List<CloudPicture> listData){
        this.listData=listData;
        init();
        if(clickInterFace!=null){
            clickInterFace.refresh();
        }
    }

    @Override
    public List<SectionedRecyclerAdapter.Section> getSections() {
        return mSections;
    }

    public interface ClickInterFace{
        void refresh();
        void onClick(CloudPicture cloudPicture);
    }

   /* private List<CloudPicData> getCloudInfoList(String group_id) {
        GetAblumPicTask task = new GetAblumPicTask(activity);
        task.setShowProgressDialog(false);
       final List<CloudPicData> mresult = new ArrayList<CloudPicData>();
        task.setAsyncTaskSuccessCallback(new AsyncTaskSuccessCallback<List<CloudPicData>>() {
            @Override
            public void successCallback(Result<List<CloudPicData>> result) {
            }
        });
        task.setAsyncTaskFailCallback(new AsyncTaskFailCallback<List<CloudPicData>>() {
            @Override
            public void failCallback(Result<List<CloudPicData>> result) {
                CostomToastUtil.toast(result.getMessage());
            }
        });
        task.execute(group_id);
        return  mresult;
    }
*/

}