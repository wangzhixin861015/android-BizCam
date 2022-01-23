package com.bcnetech.hyphoto.ui.activity.photoedit;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.view.PickerViewV;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.ImageDataSqlControl;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.receiver.AddPicReceiver;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.ui.view.CircleView;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.ui.view.WaitProgressBarView;
import com.bcnetech.hyphoto.ui.view.verticalscrollview.VerticalSeekBar;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageAtmosphereFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.gpumanage.GPUImageFilterTools;

/**
 * 氛围
 * Created y a1234 on 16/10/25.
 */

public class AtmosphereActivity extends BaseActivity {
    private final static int INIT_DATA = 100;
    private final static int LIMIT_MAX_DATA = 200;//参数MAX 限制
    private final static int LIMIT_MIN_DATA = 0;//参数MIN 限制
    private final static float CLICK_ALPHA = 0.4f;
    private final static float UNCLICK_ALPHA = 1f;

    private final static String CURRENT_IMAGE_DATA = "currentPicDatas";

    private GPUImageView gpuimage;
    private GPUImage tempgpuimage;
    //  private TextView text_fix;
    // private ImageView image_util;
    private PickerViewV pickerv;
    private RelativeLayout frame_layout;
    private VerticalSeekBar left_land_seebar;
    private RelativeLayout relsee;
    //  private ImageView back;
    private CircleView custom_mask;//覆盖在imageview上的mask图,框住的区域亮度增强
    //   private ImageView paint_ok;
    //   private ImageView paint_cencel;
    private WaitProgressBarView wait_view;
    private TitleView atmosphere_title;
    private ImageView final_bit;

    private ImageData imageData;
    private List<PictureProcessingData> currentPicDatas;
    private GPUImageFilter srcFilter, groupFilter;
    private GPUImageAtmosphereFilter currentFilter;
    private PictureProcessingData pictureProcessingData;
    private SaveAtmosTask task;
    private Bitmap mCurrentBitmap, smallbit;

    private int currentPosition;
    private int currentPartParmsPosition;//当前部分修改操作节点；
    private boolean canClick;//判断是否能点击
    private float currentNum;
    private boolean mFirstWindowFouse, activityFouse;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Flag.CURRENT_PART_POSITION, currentPartParmsPosition);
        outState.putSerializable(CURRENT_IMAGE_DATA, (Serializable) currentPicDatas);
        outState.putSerializable(Flag.PART_PARMS, imageData);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_atmosphere_layout);

        if (savedInstanceState == null) {
            currentPartParmsPosition = getIntent().getIntExtra(Flag.CURRENT_PART_POSITION, -1);
            imageData = (ImageData) getIntent().getSerializableExtra(Flag.PART_PARMS);
        } else {
            imageData = (ImageData) savedInstanceState.getSerializable(Flag.PART_PARMS);
            currentPartParmsPosition = savedInstanceState.getInt(Flag.CURRENT_PART_POSITION);
            currentPicDatas = (List<PictureProcessingData>) savedInstanceState.getSerializable(CURRENT_IMAGE_DATA);
        }

        if (imageData == null) {
            ToastUtil.toast(getResources().getString(R.string.data_error));
            finish();
            return;
        }
        String url = BizImageMangage.getInstance().getUrlFromCurrentPostion(currentPartParmsPosition, imageData, true, true);
        if (StringUtil.isBlank(url)) {
            ToastUtil.toast(getResources().getString(R.string.data_error));
            finish();
            return;
        }
        pictureProcessingData = new PictureProcessingData(BizImageMangage.ATMOSPHERE, url);

        initView();
        initData();
        onViewClick();
        initCurrentData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentBitmap == null) {
            ImageUtil.EBizImageLoad(pictureProcessingData.getImageUrl(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    wait_view.setVisibility(View.VISIBLE);
                    canClick = false;
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    canClick = true;
                    wait_view.setVisibility(View.GONE);
                    ToastUtil.toast(getResources().getString(R.string.loading_fail));
                    finish();
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    gpuimage.setImage(loadedImage);
                    mCurrentBitmap = loadedImage;
                    custom_mask.getpicSize(loadedImage.getWidth(), loadedImage.getHeight());
                    left_land_seebar.setProgresssee(currentPicDatas.get(currentPosition).getNum());
                    if (activityFouse) {
                        autoGpuImageParmsLayout();
                        mFirstWindowFouse = false;
                    }
                    currentFilter.setmCricleWithe(loadedImage.getWidth());
                    currentFilter.setmCricleHeight(loadedImage.getHeight());
                    currentFilter.setmCricleBlurR(loadedImage.getWidth() / 5);
                    currentFilter.setExposure(filterLimitChang(currentPicDatas.get(0).getNum()));
                    currentFilter.setExposure2(filterLimitChang(currentPicDatas.get(1).getNum()));
                    gpuimage.requestRender();
                    canClick = true;
                    wait_view.setVisibility(View.GONE);
                    Rect rect = new Rect();
                    gpuimage.getHitRect(rect);
                    custom_mask.getGpuImageWH(rect);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    canClick = true;
                    wait_view.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        activityFouse = hasFocus;
        if (mFirstWindowFouse && hasFocus) {
            if (mCurrentBitmap != null) {
                autoGpuImageParmsLayout();
                mFirstWindowFouse = false;
            }
        }

    }

    private void autoGpuImageParmsLayout() {
        double withe = frame_layout.getMeasuredWidth();
        double hight = frame_layout.getMeasuredHeight();
        double w = withe;
        double h = hight;

        double strx = 0;
        double stry = 0;
        if (w / h > mCurrentBitmap.getWidth() * 1.0 / mCurrentBitmap.getHeight()) {
            w = h * mCurrentBitmap.getWidth() * 1.0 / mCurrentBitmap.getHeight();
            strx = (withe - w) / 2;
        } else {
            h = w * mCurrentBitmap.getHeight() * 1.0 / mCurrentBitmap.getWidth();
            stry = (hight - h) / 2;
        }

        gpuimage.setLayoutParams(new RelativeLayout.LayoutParams((int) w, (int) h));
        gpuimage.setTranslationX((int) strx);
        gpuimage.setTranslationY((int) stry);
        custom_mask.getpicSize(mCurrentBitmap.getWidth(), mCurrentBitmap.getHeight());
    }


    @Override
    protected void initView() {
        atmosphere_title = (TitleView) findViewById(R.id.atmosphere_title);
        custom_mask = (CircleView) findViewById(R.id.custom_mask);
        // back = (ImageView) findViewById(R.id.back);
        frame_layout = (RelativeLayout) findViewById(R.id.frame_layout);
        gpuimage = (GPUImageView) findViewById(R.id.gpuimage);
        //text_fix = (TextView) findViewById(text_fix);
        //image_util = (ImageView) findViewById(R.id.image_util);
        pickerv = (PickerViewV) findViewById(R.id.pickerv);
        left_land_seebar = (VerticalSeekBar) findViewById(R.id.left_land_seebar);
        relsee = (RelativeLayout) findViewById(R.id.relsee);
        // paint_ok=(ImageView) findViewById(paint_ok);
        // paint_cencel=(ImageView) findViewById(R.id.paint_cencel);
        wait_view = (WaitProgressBarView) findViewById(R.id.wait_view);
        final_bit = (ImageView) findViewById(R.id.final_bit);
    }

    @Override
    protected void initData() {
        custom_mask.bringToFront();
        atmosphere_title.setType(TitleView.PIC_PARMS);
        activityFouse = false;
        mFirstWindowFouse = true;
        final_bit.setVisibility(View.GONE);
        currentNum = INIT_DATA;
        relsee.setVisibility(View.INVISIBLE);
        // back.setVisibility(View.GONE);
        pickerv.setCanTouch(true);
        pickerv.setData(initPickerData());
        pickervChanceType((pickerv.getCurrentItem().getName()));
       /* if (imageData.isMatting()){
            rl_bg.setBackground(AtmosphereActivity.this.getResources().getDrawable(R.drawable.bgbitmap));
        }else{
            rl_bg.setBackgroundColor(Color.BLACK);
        }*/
    }

    private List<PickerViewV.Item> initPickerData() {
        List list = new ArrayList<>();
        list.add(new PickerViewV.Item(getResources().getString(R.string.inner_bright)));
        list.add(new PickerViewV.Item(getResources().getString(R.string.outer_bright)));

        currentPicDatas = new ArrayList<>();
        currentPicDatas.add(new PictureProcessingData(BizImageMangage.ATMOSPHERE_IN, INIT_DATA));
        currentPicDatas.add(new PictureProcessingData(BizImageMangage.ATMOSPHERE_OUT, INIT_DATA));
        return list;
    }


    @Override
    protected void onViewClick() {
        atmosphere_title.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick) {
                    return;
                }
                if (task != null) {
                    task.cancel(true);
                }
                finish();
            }
        });
        atmosphere_title.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick) {
                    return;
                }
                tempgpuimage = new GPUImage(AtmosphereActivity.this);
                tempgpuimage.setImage(mCurrentBitmap);
                tempgpuimage.setFilter(groupFilter);
                smallbit = tempgpuimage.getBitmapWithFilterApplied();
                final_bit.setImageBitmap(smallbit);
                final_bit.setVisibility(View.VISIBLE);
                gpuimage.setFilter(currentFilter);
                wait_view.setVisibility(View.VISIBLE);
           /* WaitPop waitPop = new WaitPop(AtmosphereActivity.this);
            waitPop.showPop(AtmosphereActivity.this.getWindow().getDecorView());*/
                canClick = false;

                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap finalbit = gpuimage.getGPUImage().getBitmapWithFilterApplied();
                        task = new SaveAtmosTask(finalbit, smallbit);
                    /*task= new SavePicTask(finalbit,
                            new SavePicTask.SaveInterface() {
                                @Override
                                public void saveEnd(String url) {
                                    if (url != null) {
                                        if(imageData.getImageParts()==null){
                                            imageData.setImageParts(new ArrayList<PictureProcessingData>());
                                        }
                                        imageData.getImageParts().add(new PictureProcessingData(BizImageMangage.ATMOSPHERE,url));
                                    }
                                    else{
                                        CostomToastUtil.toast(getResources().getString(R.string.change_fail));
                                    }
                                    wait_view.setVisibility(View.GONE);
                                    canClick=true;
                                    Intent intent=new Intent();
                                    intent.putExtra(Flag.PART_PARMS,imageData);
                                    setResult(Flag.PART_PARMS_ACTIVITY,intent);
                                    finish();
                                }
                            });*/
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    }
                });
          /*  Bitmap finalbit = gpuimage.getGPUImage().getBitmapWithFilterApplied();
            task= new SavePicTask(finalbit,
                    new SavePicTask.SaveInterface() {
                        @Override
                        public void saveEnd(String url) {
                            if (url != null) {
                                if(imageData.getImageParts()==null){
                                    imageData.setImageParts(new ArrayList<PictureProcessingData>());
                                }
                                imageData.getImageParts().add(new PictureProcessingData(BizImageMangage.ATMOSPHERE,url));
                            }
                            else{
                                CostomToastUtil.toast(getResources().getString(R.string.change_fail));
                            }
                            wait_view.setVisibility(View.GONE);
                            canClick=true;
                            Intent intent=new Intent();
                            intent.putExtra(Flag.PART_PARMS,imageData);
                            setResult(Flag.PART_PARMS_ACTIVITY,intent);
                            finish();
                        }
                    });
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
*/
            }
        });
        /**
         * 保存修改
         */
      /*  paint_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!canClick){
                    return;
                }
                gpuimage.setFilter(currentFilter);
                wait_view.setVisibility(View.VISIBLE);
                canClick=false;
                task= new SavePicTask(gpuimage.getGPUImage().getBitmapWithFilterApplied(),
                        new SavePicTask.SaveInterface() {
                            @Override
                            public void saveEnd(String url) {
                                if (url != null) {
                                    if(imageData.getImageParts()==null){
                                        imageData.setImageParts(new ArrayList<PictureProcessingData>());
                                    }
                                    imageData.getImageParts().add(new PictureProcessingData(BizImageMangage.ATMOSPHERE,url));
                                }
                                else{
                                    CostomToastUtil.toast(getResources().getString(R.string.change_fail));
                                }
                                wait_view.setVisibility(View.GONE);
                                canClick=true;
                                Intent intent=new Intent();
                                intent.putExtra(Flag.PART_PARMS,imageData);
                                setResult(Flag.PART_PARMS_ACTIVITY,intent);
                                finish();
                            }
                        });
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });*/

        /**
         * 取消修改
         */
       /* paint_cencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(task!=null){
                    task.cancel(true);
                }
                finish();
            }
        });*/

        /**
         * 图片操作回调函数
         */
        custom_mask.setChangListener(new CircleView.ChangListener() {
            /**
             * 返回每次调整后的坐标
             * @param x
             * @param y
             * @param r
             */
            @Override
            public void returnXYR(float x, float y, float r) {

                currentFilter.setmCricleX(x);
                currentFilter.setmCricleY(y);
                currentFilter.setmCricleR(r);
                gpuimage.requestRender();
            }

            @Override
            public void startUpDown() {
                relsee.setVisibility(View.VISIBLE);
            }

            @Override
            public void upDown(int number) {

                currentNum = currentPicDatas.get(currentPosition).getNum() + number;
                if (currentNum > LIMIT_MAX_DATA) {
                    currentNum = LIMIT_MAX_DATA;
                } else if (currentNum < LIMIT_MIN_DATA) {
                    currentNum = LIMIT_MIN_DATA;
                }
                if (currentPosition == 0) {
                    currentFilter.setExposure(filterLimitChang((int) currentNum));
                } else {
                    currentFilter.setExposure2(filterLimitChang((int) currentNum));
                }
                left_land_seebar.setProgresssee((int) currentNum);
                titleTextShow((int) currentNum);
                gpuimage.requestRender();
            }

            @Override
            public void endMove() {
                relsee.setVisibility(View.INVISIBLE);
                currentPicDatas.get(currentPosition).setNum((int) currentNum);
            }
        });


/*
        image_util.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!canClick){
                    return false;
                }
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        text_fix.setAlpha(CLICK_ALPHA);
                        gpuimage.setFilter(srcFilter);
                        gpuimage.requestRender();
                        break;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        text_fix.setAlpha(UNCLICK_ALPHA);
                        gpuimage.setFilter(currentFilter);
                        gpuimage.requestRender();
                        break;
                }

                return true;
            }
        });

        text_fix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!canClick){
                    return;
                }
            }
        });*/

        pickerv.setOnSelectListener(new PickerViewV.onSelectListener() {
            @Override
            public void onSelect(PickerViewV.Item text) {
                pickervChanceType(text.getName());
            }
        });


    }

    private void pickervChanceType(String name) {
        if (name.equals(getResources().getString(R.string.inner_bright))) {
            currentPosition = 0;

        } else if (name.equals(getResources().getString(R.string.outer_bright))) {
            currentPosition = 1;
        }

        left_land_seebar.setProgresssee(currentPicDatas.get(currentPosition).getNum());
        titleTextShow(currentPicDatas.get(currentPosition).getNum());
    }

    //
    //保存最终图片和未加滤镜的修改图片
    private class SaveAtmosTask extends AsyncTask<Void, Void, ArrayList> {
        private Bitmap bitmap;//未加滤镜图片
        private Bitmap finalbitmap;//展示用的最终图片

        SaveAtmosTask(Bitmap bitmap, Bitmap finalbitmap) {
            this.bitmap = bitmap;
            this.finalbitmap = finalbitmap;
        }

        protected void onPreExecute() {
            //dialogHelper.showProgressDialog("处理中");
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            long time = System.currentTimeMillis();
            ArrayList<String> urllist = new ArrayList<>();
            String path1 = null;//未加滤镜图片
            String path2 = null;//最终图片
            try {
                urllist = new ArrayList<>();
                path1 = FileUtil.saveBitmap(bitmap, time - 1 + "", imageData.isMatting());
                path2 = FileUtil.saveBitmaptoNative(finalbitmap, time + 1 + "", imageData.isMatting());
                if (StringUtil.isBlank(path1) || StringUtil.isBlank(path2)) {
                    return null;
                }
                path1 = "file://" + path1;
                path2 = "file://" + path2;
                urllist.add(path1);
                urllist.add(path2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return urllist;
        }

        @Override
        protected void onPostExecute(ArrayList result) {
            super.onPostExecute(result);
            if (result != null || result.size() == 0) {
                String processurl = (String) result.get(0);
                String finalurl = (String) result.get(1);
                if (processurl != null && finalurl != null) {
                    List<PictureProcessingData> processlist;
                    if (imageData.getImageParts() == null) {
                        processlist = new ArrayList<PictureProcessingData>();
                    } else {
                        processlist = imageData.getImageParts();
                    }
                    processlist.add(new PictureProcessingData(BizImageMangage.ATMOSPHERE, processurl));
                    //
                    if (imageData.getCurrentPosition() == 0) {

                        imageData.setImageTools(new ArrayList<PictureProcessingData>());
                        for (int i = 0; i < imageData.getImageParts().size(); i++) {
                            // DeleteImage(imageDatatext.getImageParts().get(i).getImageUrl().substring("file://".length()));//删除之前小图
                        }
                        imageData.setImageParts(processlist);//设置部分修改
                        imageData.setCurrentPosition(//设置当前位置
                                imageData.getCurrentPosition() +
                                        (processlist.size()));
                    } else {//当前位置不为0
                    /*if (imageDatatext.getImageTools() == null || imageDatatext.getImageTools().size() == 0) {
                        //如果没有做过部分修改
                        for (int i = imageDatatext.getCurrentPosition(); i < imageDatatext.getImageParts().size(); i++) {
                           // DeleteImage(imageDatatext.getImageParts().get(i).getImageUrl().substring("file://".length()));
                        }
                    } else {
                        for (int i = imageDatatext.getCurrentPosition() - 1; i < imageDatatext.getImageParts().size(); i++) {
                           // DeleteImage(imageDatatext.getImageParts().get(i).getImageUrl().substring("file://".length()));

                        }
                    }*/

                        if (imageData.getImageTools() != null && imageData.getImageTools().size() != 0) {
                            if (imageData.getCurrentPosition() - 1 > (processlist.size())) {
                            } else {
                                imageData.setCurrentPosition(imageData.getCurrentPosition() + 1);
                            }
                        } else {
                            if (imageData.getCurrentPosition() > (processlist.size())) {
                            } else {
                                imageData.setCurrentPosition(imageData.getCurrentPosition() + 1);

                            }
                            imageData.setImageParts(processlist);
                        }
                        imageData.setImageParts(processlist);

                    }
                    DeleteImage(imageData.getSmallLocalUrl().substring("file://".length()));
                    imageData.setSmallLocalUrl(finalurl);
                    final ImageDataSqlControl imageDataSqlControl = new ImageDataSqlControl(AtmosphereActivity.this);
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            imageDataSqlControl.setListener(new BaseSqlControl.SqlControlListener() {
                                @Override
                                public void queryListener(Object... parms) {

                                }

                                @Override
                                public void insertListener(Object... parms) {

                                }

                                @Override
                                public void deletListener(Object... parms) {

                                }

                                @Override
                                public void upDataListener(Object... parms) {

                                }
                            });
                            imageDataSqlControl.startUpdata(new String[]{imageData.getLocalUrl()}, imageData);
                            AddPicReceiver.notifyModifyUsername(AtmosphereActivity.this,"refresh");
                        }
                    });
                }
            } else {
                ToastUtil.toast(getResources().getString(R.string.change_fail));
            }
            dissmissDialog();
          /*  Intent intent = new Intent();
            intent.putExtra(Flag.PART_PARMS, imageDatatext);
            setResult(Flag.PART_PARMS_ACTIVITY, intent);*/
            finish();
        }
    }

    //删除数据库以及文件中的图片
    private boolean DeleteImage(String imgPath) {
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=?",
                new String[]{imgPath}, null);
        boolean result = false;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri = ContentUris.withAppendedId(contentUri, id);
            int count = this.getContentResolver().delete(uri, null, null);
            result = count == 1;
        } else {
            File file = new File(imgPath);
            if (file.exists()) {//显示文件不存在,该文件夹内存在两张一样的图片
                result = file.delete();
            }
        }
        return result;
    }
    //
    List<PictureProcessingData> imagetools;
    /**
     * 进入参数调整初始化数据
     */
    private void initCurrentData() {

        List<GPUImageFilter> gpuImageFilters = new ArrayList<>();
        List<GPUImageFilter> srcgpuImageFilters = new ArrayList<>();
        currentFilter = (GPUImageAtmosphereFilter) BizImageMangage.getInstance().getGPUFilterforType(this, BizImageMangage.ATMOSPHERE);

        gpuImageFilters.add(currentFilter);
        if (imageData.getImageTools() != null && imageData.getImageTools().size() != 0) {
            imagetools = imageData.getImageTools();

        } else if (imageData.getPresetParms() != null && imageData.getPresetParms().getParmlists() != null && imageData.getPresetParms().getParmlists().size() != 0) {
            imagetools = imageData.getPresetParms().getParmlists();
        }
        for (int i = 0; imagetools!= null && i < imagetools.size(); i++) {
            GPUImageFilter mFilter;
            GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
            mFilter = BizImageMangage.getInstance().getGPUFilterforType(this, imagetools.get(i).getType());
            gpuImageFilters.add(mFilter);
            srcgpuImageFilters.add(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
            mFilterAdjuster.adjust(imagetools.get(i).getNum());
        }

        if (srcgpuImageFilters == null || srcgpuImageFilters.size() == 0) {
            srcFilter = new GPUImageFilter();
        } else {
            srcFilter = new GPUImageFilterGroup(srcgpuImageFilters);
        }
        groupFilter = new GPUImageFilterGroup(gpuImageFilters);
        gpuimage.setFilter(groupFilter);
        gpuimage.requestRender();
    }

    /**
     * 转换设置filter
     *
     * @param number
     * @return
     */
    private float filterLimitChang(int number) {
        return (float) (number * 1.0 / INIT_DATA - 1);
    }

    private void titleTextShow(int number) {
        // text_fix.setText((number-100) + "");
        atmosphere_title.setTitleText((number - 100) + "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCurrentBitmap != null) {
            mCurrentBitmap.recycle();
        }
        srcFilter = null;
        groupFilter = null;
        currentFilter = null;
        if (task != null) {
            task.cancel(true);
        }
        task = null;
        tempgpuimage = null;
    }
}
