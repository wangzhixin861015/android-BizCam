package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.hanyupinyin.HanziToPinyin;
import com.bcnetech.bcnetechlibrary.view.IndexableRecycler.SectionedRecyclerAdapter;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.CloudPicture;
import com.bcnetech.hyphoto.ui.activity.cloud.CloundActivity;
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
 * Created by wenbin on 16/7/1.
 */
public class CloudAdapter extends RecyclerView.Adapter implements SectionedRecyclerAdapter.SectionedRecyclerDelegate {
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

    public CloudAdapter(Activity activity, List<CloudPicture> listData, ClickInterFace clickInterFace,int typeCode) {
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
        mSections.clear();
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
            mSections.add(section);
            pos += mSectionedHashMap.get(title).size();
        }

        mLineNumber = pos;
    }

    @Override
    public List<SectionedRecyclerAdapter.Section> getSections() {
        return mSections;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BannerViewHolder(mLayoutInflater.inflate(R.layout.recycler_item, parent, false));
    }
    private List<String> stringTolist(String s) {
        String[] arr = StringUtil.isBlank(s)?new String[]{}:s.split(",");
        List<String> list = java.util.Arrays.asList(arr);
        return list;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CloudPicture cloudPicture = listData.get(position);
//        final ThreeBitmapDrawable drawable = new ThreeBitmapDrawable(BitmapFactory.decodeResource(activity.getResources(), R.drawable.background),
//                null,
//                null,
//                null);
       String filId = cloudPicture.getFileId();
        List<String> fileId =stringTolist(filId);
        final BannerViewHolder bannerViewHolder=(BannerViewHolder) holder;

        if (fileId!=null){
            if (fileId.size()>0&&fileId.get(0) !=null) {
                String url = BitmapUtils.getBitmapUrl6(fileId.get(0));
                ImageUtil.EBizImageLoad(bannerViewHolder.item_image1, StringUtil.getSizeUrl(url,w,w), w, w, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }else {
                bannerViewHolder.item_image1.setImageResource(0);
            }
            if (fileId.size()>1&&fileId.get(1)!=null) {
                String url = BitmapUtils.getBitmapUrl6(fileId.get(1));
                ImageUtil.EBizImageLoad(bannerViewHolder.item_image2, StringUtil.getSizeUrl(url,w,w), w, w, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }else {
                bannerViewHolder.item_image2.setImageResource(0);
            }
            if (fileId.size()>2&&fileId.get(2) !=null) {
                String url = BitmapUtils.getBitmapUrl6(fileId.get(2));
                ImageUtil.EBizImageLoad( bannerViewHolder.item_image3, StringUtil.getSizeUrl(url,w,w), w, w, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }else {
                bannerViewHolder.item_image3.setImageResource(0);
            }
        }

        /*GetAblumPicTask task = new GetAblumPicTask(activity);
        task.setShowProgressDialog(false);
        final BannerViewHolder bannerViewHolder=(BannerViewHolder) holder;
        task.setAsyncTaskSuccessCallback(new AsyncTaskSuccessCallback<List<CloudPicData>>() {
            @Override
            public void successCallback(Result<List<CloudPicData>> result) {
                final List<CloudPicData> mresult;
                mresult = result.getValue();
                if (mresult != null) {
                    if (mresult.size() >= 1 && mresult.get(0) != null) {
                        String url = UrlConstants.DEFAUL_UPLOAD_PIC_SITE + "/" + mresult.get(0).getFileId() + ".png";
                        ImageUtil.EBizImageLoad(url, w, w, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                drawable.setmBitmap3(loadedImage);
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        });
                    }

                    if (mresult.size() >= 2 && mresult.get(1) != null) {
                        String url = UrlConstants.DEFAUL_UPLOAD_PIC_SITE + "/" + mresult.get(1).getFileId() + ".png";
                        ImageUtil.EBizImageLoad(url, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                drawable.setmBitmap2(loadedImage);
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        });
                    }
                    if (mresult.size() >= 3 && mresult.get(2) != null) {
                        String url = UrlConstants.DEFAUL_UPLOAD_PIC_SITE + "/" + mresult.get(2).getFileId() + ".png";
                        ImageUtil.EBizImageLoad(url, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                drawable.setmBitmap1(loadedImage);
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        });
                    }
                }
            }
        });
        task.setAsyncTaskFailCallback(new AsyncTaskFailCallback<List<CloudPicData>>() {
            @Override
            public void failCallback(Result<List<CloudPicData>> result) {
                CostomToastUtil.toast(result.getMessage());
            }
        });
        task.execute(cloudPicture.getId());*/
//        if(canDelet){
//            bannerViewHolder.delete.setVisibility(View.VISIBLE);
//            bannerViewHolder.delete.setTag(cloudPicture.getId()+"");
//            bannerViewHolder.delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String id=String.valueOf(v.getTag());
//                    //deleteGroup(id);
//                }
//            });
//        }
//        else{
//            bannerViewHolder.delete.setVisibility(View.GONE);
//        }

//        if(cloudPicture.getCode()!=null&&cloudPicture.getCode().equals("edu_share")){
//            bannerViewHolder.share.setVisibility(View.VISIBLE);
//        }else {
//            bannerViewHolder.share.setVisibility(View.INVISIBLE);
//        }

      /*  if (fileId!=null) {
            bannerViewHolder.mImageView.setBackground(drawable);
        }else{
            bannerViewHolder.mImageView.setBackground(activity.getResources().getDrawable(R.drawable.bgnone));
        }*/
        if(typeCode== CloundNewActivity.CLOUD_CHISE) {
            if (cloudPicture.isselected()) {
                bannerViewHolder.cloud_item_check.setBackground(activity.getResources().getDrawable(R.drawable.recttangle_blue));
            }else{
                bannerViewHolder.cloud_item_check.setBackgroundColor(activity.getResources().getColor(R.color.translucent));
            }
        }
        bannerViewHolder.name.setText(cloudPicture.getName());
        bannerViewHolder.count.setText(cloudPicture.getCount()+activity.getResources().getString(R.string.countitem));
       // bannerViewHolder.name.setBackgroundColor(activity.getResources().getColor(R.color.translucent));
        bannerViewHolder.item_layout.setTag(cloudPicture);
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
                        if(typeCode== CloundActivity.CLOUD_CHISE) {
                            bannerViewHolder.cloud_item_check.setBackground(activity.getResources().getDrawable(R.drawable.recttangle_blue));
                            //bannerViewHolder.item_layout.setBackground(activity.getResources().getDrawable(R.drawable.recttangle_blue));
                        }else{
                            bannerViewHolder.cloud_item_check.setBackgroundColor(activity.getResources().getColor(R.color.translucent));
                           // bannerViewHolder.item_layout.setBackgroundColor(activity.getResources().getColor(R.color.translucent));
                        }
                        clickInterFace.onClick(cloudPicture);
                       //CloudAdapter.this.notifyDataSetChanged();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                }

                return true;
            }
        });

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
        ImageView item_image1;
        ImageView item_image2;
        ImageView item_image3;
        TextView name;
        TextView count;
        LinearLayout item_layout;
        ImageView share;
        ImageView cloud_item_check;

        public BannerViewHolder(View itemView) {
            super(itemView);
            item_image1 = (ImageView) itemView.findViewById(R.id.item_image1);
            item_image2 = (ImageView) itemView.findViewById(R.id.item_image2);
            item_image3 = (ImageView) itemView.findViewById(R.id.item_image3);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            count = (TextView)itemView.findViewById(R.id.tv_cloudcount);
            item_layout= (LinearLayout) itemView.findViewById(R.id.item_layout);
            share=(ImageView) itemView.findViewById(R.id.share);
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


    public interface ClickInterFace{
        void refresh();
        void onClick(CloudPicture cloudPicture);
    }

  /*  private List<CloudPicData> getCloudInfoList(String group_id) {
        GetAblumPicTask task = new GetAblumPicTask(activity,true);
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
                ToastUtil.toast(result.getMessage());
            }
        });
        task.execute(group_id);
        return  mresult;
    }
*/

}