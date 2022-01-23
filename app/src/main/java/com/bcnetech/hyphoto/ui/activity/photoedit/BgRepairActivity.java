package com.bcnetech.hyphoto.ui.activity.photoedit;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnLayoutInflatedListener;
import com.app.hubert.guide.model.GuidePage;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog3;
import com.bcnetech.bcnetechlibrary.util.PreferenceUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.view.BifacialView;
import com.bcnetech.bcnetechlibrary.view.MyImageView;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.ImageDataSqlControl;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.receiver.AddPicReceiver;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.ui.view.BGPaintView;
import com.bcnetech.hyphoto.ui.view.ImageNewUtilsView;
import com.bcnetech.hyphoto.ui.view.ScaleImageView;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.gpumanage.GPUImageFilterTools;

/**
 * 背景修复
 * Created by a1234 on 16/10/15.
 */

public class BgRepairActivity extends BaseActivity {
    private GPUImage gpuImage;
    private ScaleImageView bgrimage;
    private BGPaintView bgrpaiant;

    private ImageView mark_main;//撤销上一步
    private MyImageView eye;//点击查看原图
    private ImageView mark_bg;//智能修复


//    private RelativeLayout wait_view;

    private ImageData imageparms;
    private Bitmap mCurrentBitmap;//原始图片（加上滤镜）
    private Bitmap orginBitmap;//原始图片（无滤镜）
    private Bitmap changBitmap;
    private Bitmap tempBit;

    private RelativeLayout rl;
    private Handler handler;
    private BizImageMangage mangage = BizImageMangage.getInstance();
    private ArrayList<Bitmap> fixbitmap;//cun
    private String pathList;
    private PictureProcessingData pictureProcessingData;
    private int currentPosition;//当前部分修改操作节点
    private GPUImageFilter srcFilterGroup;
    private final static String CURRENT_IMAGE_DATA = "currentPicDatas";
    private TitleView titleView;
    private SaveBgRepairTask task;
//    private WaitProgressBarView wait_progress;

    private PreferenceUtil preferenceUtil;
    private int bgRepairCount = 0;
    private String url;


    private BifacialView bifacialView;
    private List<PictureProcessingData> imagePart;
    private int orientation;

    private DGProgressDialog3 dgProgressDialog;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Flag.PART_PARMS, imageparms);
        outState.putSerializable(CURRENT_IMAGE_DATA, pictureProcessingData);
        outState.putParcelableArrayList("fixbitmap", fixbitmap);
        outState.putInt(Flag.CURRENT_PART_POSITION, currentPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentPosition = savedInstanceState.getInt(Flag.CURRENT_PART_POSITION);
        imageparms = (ImageData) savedInstanceState.getSerializable(Flag.PART_PARMS);
        pictureProcessingData = (PictureProcessingData) savedInstanceState.getSerializable(CURRENT_IMAGE_DATA);
        fixbitmap = (ArrayList) savedInstanceState.getSerializable("fixbitmap");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bgrepair);
        currentPosition = getIntent().getIntExtra(Flag.CURRENT_PART_POSITION, -1);
//        imageparms = (ImageData) getIntent().getSerializableExtra(Flag.PART_PARMS);
        imageparms= ImageNewUtilsView.imageparms;

        if (imageparms == null) {
            finish();
            return;
        }

        url = BizImageMangage.getInstance().getUrlFromCurrentPostion(currentPosition, imageparms, true, true);
        if (StringUtil.isBlank(url)) {
            ToastUtil.toast(getResources().getString(R.string.data_error));
            finish();
            return;
        }
        pictureProcessingData = new PictureProcessingData(BizImageMangage.BACKGROUND_REPAIR, url);

        initView();
        initData();
        onViewClick();
    }

    @Override
    protected void initView() {
        gpuImage = new GPUImage(this);
        bgrimage = (ScaleImageView) findViewById(R.id.bgr_imageview);
        bgrpaiant = (BGPaintView) findViewById(R.id.bgr_paint);

        mark_main = (ImageView) findViewById(R.id.mark_main);
        mark_bg = (ImageView) findViewById(R.id.mark_bg);

        rl = (RelativeLayout) findViewById(R.id.rl_bgr);

        eye = (MyImageView) findViewById(R.id.eye);
        bifacialView = (BifacialView) findViewById(R.id.bifacialView);


        titleView = (TitleView) findViewById(R.id.bgrepair_title);

    }

    @Override
    protected void initData() {
        dgProgressDialog = new DGProgressDialog3(this, false, getResources().getString(R.string.repairing));

        dissmissDialog();
        titleView.setType(TitleView.IMAGE_BG_REPAIR);
        setGpuImage();
        pathList = pictureProcessingData.getImageUrl();
        ImageUtil.EBizImageLoad(pathList, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
//                showWait();
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                orientation = com.bcnetech.bcnetechlibrary.util.ImageUtil.readPictureDegree(pictureProcessingData.getImageUrl().substring(7));

                if (orientation > 0) {
                    Matrix matrix = new Matrix();
                    matrix.setRotate(orientation);
                    // 重新绘制Bitmap
                    Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    bitmap = newBitmap;
                }

                if (bitmap.getWidth() > BitmapUtils.MAXLENGTH || bitmap.getHeight() > BitmapUtils.MAXLENGTH) {
                    bitmap = ImageUtil.newDecodeSampledBitmapFromFile(pictureProcessingData.getImageUrl().substring(7), bitmap, BitmapUtils.MAXLENGTH, BitmapUtils.MAXLENGTH);
                }

                orginBitmap = bitmap;
                gpuImage.setImage(bitmap);
                Bitmap mbitmap = gpuImage.getBitmapWithFilterApplied();
                mCurrentBitmap = mbitmap;
                changBitmap = Bitmap.createBitmap(bitmap);
                fixbitmap = new ArrayList<Bitmap>();
                bgrimage.setBitmap(mCurrentBitmap, null);

//                wait_view.setVisibility(View.GONE);
                bgrpaiant.setVisibility(View.VISIBLE);
                bgrpaiant.setPaintColor(Color.RED);
                bgrimage.setListener(new ScaleImageView.ImgUpData() {
                    @Override
                    public void imgUpdata(Rect canveRect) {
                        bgrpaiant.setImgRect(canveRect);
                    }
                });
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                bgrpaiant.setLayoutParams(params);
                handler = new Handler();
                bgrpaiant.setCanveLayoutParms(new BGPaintView.OnDrawListener() {
                    @Override
                    public void touchEven(MotionEvent event) {
                        bgrimage.onTouchEvent(event);
                    }

                    @Override
                    public void beforDrow(final Bitmap bit) {
                        BgRepairActivity.this.showDialog();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //原图和mask{
                                changBitmap = mangage.ProcessingPic(changBitmap, bit, BizImageMangage.BACKGROUND_REPAIR, null);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (changBitmap != null) {
                                            fixbitmap.add(changBitmap);
                                            savetoFile();
                                        }
                                    }

                                });
                            }
                        }).start();
                    }

                }, bitmap.getWidth(), bitmap.getHeight());
                bgrimage.invalidate();
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

        if (pathList.contains(".png")) {
            rl.setBackground(getResources().getDrawable(R.drawable.bgbitmap));
        }
    }

    private void savetoFile() {
        BgRepairActivity.this.dissmissDialog();
        bgrpaiant.reStartDraw();
        gpuImage.setImage(changBitmap);
        bgrimage.resetBitmap(gpuImage.getBitmapWithFilterApplied());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // cleanallcache();
        if (tempBit != null) {
            tempBit.recycle();
        }
        if (mCurrentBitmap != null) {
            mCurrentBitmap.recycle();
        }
        if (changBitmap != null) {
            changBitmap.recycle();
        }
        if (orginBitmap != null) {
            orginBitmap.recycle();
        }
        srcFilterGroup = null;
        if (task != null) {
            task.cancel(true);
        }
        task = null;
        if (gpuImage != null) {
            gpuImage.deleteImage();
        }
    }

    private void cleanallcache() {
        if (fixbitmap != null || fixbitmap.size() != 0) {
            fixbitmap.clear();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        }
        return true;
    }

    @Override
    protected void onViewClick() {

        bgrimage.setListener(new ScaleImageView.ImgUpData() {
            @Override
            public void imgUpdata(Rect canveRect) {
                bgrpaiant.setImgRect(canveRect);
            }
        });
        titleView.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (wait_view.getVisibility() != View.VISIBLE) {
                cleanallcache();
                finish();
//                }
            }
        });
        //点击智能的修复
        mark_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //原图和mask{
                        // mark_bg_Clicked = true;
                        //   tempBit = changBitmap;
                        changBitmap = mangage.ProcessingPic(changBitmap, null, BizImageMangage.AUTOCOMPLATE, null);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (changBitmap != null) {
                                    fixbitmap.add(changBitmap);
                                    savetoFile();
                                }
                            }

                        });
                    }
                }).start();
            }
        });
        //点击撤销
        mark_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fixbitmap != null && fixbitmap.size() > 1) {
                    showDialog();
                    Bitmap b = fixbitmap.get(fixbitmap.size() - 1);
                    b.recycle();
                    fixbitmap.remove(fixbitmap.size() - 1);
                    changBitmap = fixbitmap.get(fixbitmap.size() - 1);
                    onArrorClick(changBitmap);
                    BgRepairActivity.this.dissmissDialog();
                } else if (fixbitmap.size() == 1) {
                    fixbitmap.clear();
                    changBitmap = orginBitmap;
                    onArrorClick(changBitmap);
                    BgRepairActivity.this.dissmissDialog();
                }

                System.gc();
            }

        });

        titleView.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showWait();
                gpuImage.setImage(changBitmap);
                try {
                    tempBit = new BitmapWithFilterCallable().call();
                } catch (Exception e) {
                    tempBit = gpuImage.getBitmapWithFilterApplied();
                }
                task = new SaveBgRepairTask(changBitmap, tempBit);
                task.execute();

            }
        });

        eye.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bifacialView.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        titleView.setTitleText(getResources().getString(R.string.view_diff));
                        bifacialView.setBitmapRect(bgrimage.getBitmapRect());
                        bifacialView.setCanveRect(bgrimage.getCanveRect());
                        bgrimage.setVisibility(View.GONE);
                        rl.setVisibility(View.GONE);
                        bifacialView.setVisibility(View.VISIBLE);

                        bifacialView.setDrawableRight(new BitmapDrawable(bgrimage.getBitmap()));

                        bifacialView.setDrawableLeft(new BitmapDrawable(mCurrentBitmap), mCurrentBitmap);


                        eye.setDrawableLeft(getResources().getDrawable(R.drawable.before));
                        eye.setDrawableRight(getResources().getDrawable(R.drawable.after));
                        eye.setShow(true);

                        mark_main.setVisibility(View.GONE);
                        mark_bg.setVisibility(View.GONE);
                        break;
                    case MotionEvent.ACTION_UP:
                        titleView.setTitleText("");
                        eye.setShow(false);
                        eye.remove();
                        bifacialView.setVisibility(View.GONE);
                        bgrimage.setVisibility(View.VISIBLE);
                        rl.setVisibility(View.VISIBLE);
                        mark_main.setVisibility(View.VISIBLE);
                        mark_bg.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });


    }

    private void onArrorClick(Bitmap bitmap) {
        if (bitmap != null) {
            GPUImage gs = new GPUImage(BgRepairActivity.this);
            gs.setImage(bitmap);
            gs.setFilter(srcFilterGroup);
            tempBit = gs.getBitmapWithFilterApplied();
            bgrimage.resetBitmap(tempBit);
            if (gs != null) {
                gs.deleteImage();
            }
            gs = null;
        }
    }

    // dp转ps
    private int dp2px(int dp) {
        DisplayMetrics metrics = new DisplayMetrics();
        BgRepairActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return (int) (metrics.density * dp);
    }

    //保存最终图片和未加滤镜的修改图片
    private class SaveBgRepairTask extends AsyncTask<Void, Void, ArrayList> {
        private Bitmap bitmap;//未加滤镜图片
        private Bitmap finalbitmap;//展示用的最终图片

        SaveBgRepairTask(Bitmap bitmap, Bitmap finalbitmap) {
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
            String path3 = null; //分享小图
            try {
                urllist = new ArrayList<>();
                path1 = FileUtil.saveBitmap(bitmap, time - 1 + "", url.contains(".png"));

                path2 = FileUtil.saveBitmaptoNative(finalbitmap, time + 1 + "", url.contains(".png"));

                path3 = FileUtil.saveBitmaptoShareNative(url.contains(".png") ? BitmapUtils.compressPNG(finalbitmap) : BitmapUtils.compress(finalbitmap), time + 2 + "", url.contains(".png"));


                cleanallcache();
                if (StringUtil.isBlank(path1) || StringUtil.isBlank(path2)) {
                    return null;
                }
                path2 = "file://" + path2;
                path1 = "file://" + path1;
                path3 = "file://" + path3;
                urllist.add(path1);
                urllist.add(path2);
                urllist.add(path3);
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
                String smallUrl = (String) result.get(2);
                if (processurl != null && finalurl != null) {
                    List<PictureProcessingData> processlist;
                    if (imageparms.getImageParts() == null) {
                        processlist = new ArrayList<PictureProcessingData>();
                    } else {
                        processlist = imageparms.getImageParts();
                    }
                    processlist.add(new PictureProcessingData(BizImageMangage.BACKGROUND_REPAIR, processurl, smallUrl, ""));

                    if (imageparms.getCurrentPosition() == 0) {

                        imageparms.setImageTools(new ArrayList<PictureProcessingData>());
                        for (int i = 0; i < imageparms.getImageParts().size(); i++) {
                            // DeleteImage(imageDatatext.getImageParts().get(i).getImageUrl().substring("file://".length()));//删除之前小图
                        }
                        imageparms.setImageParts(processlist);//设置部分修改
                        imageparms.setCurrentPosition(//设置当前位置
                                imageparms.getCurrentPosition() +
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

                        if (imageparms.getImageTools() != null && imageparms.getImageTools().size() != 0) {
                            if (imageparms.getCurrentPosition() - 1 > (processlist.size())) {
                            } else {
                                imageparms.setCurrentPosition(imageparms.getCurrentPosition() + 1);
                            }
                        } else {
                            if (imageparms.getCurrentPosition() > (processlist.size())) {
                            } else {
                                imageparms.setCurrentPosition(imageparms.getCurrentPosition() + 1);

                            }
                            imageparms.setImageParts(processlist);
                        }
                        imageparms.setImageParts(processlist);

                    }
                    DeleteImage(imageparms.getSmallLocalUrl().substring("file://".length()));
                    imageparms.setSmallLocalUrl(finalurl);


                    final ImageDataSqlControl imageDataSqlControl = new ImageDataSqlControl(BgRepairActivity.this);
                    handler.post(new Runnable() {
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
                            imageDataSqlControl.startUpdata(new String[]{imageparms.getLocalUrl()}, imageparms);
                            BgRepairActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(imageparms.getSmallLocalUrl())));
                            AddPicReceiver.notifyModifyUsername(BgRepairActivity.this, "refresh");
                        }
                    });
                }
            } else {
                ToastUtil.toast(getResources().getString(R.string.change_fail));
            }
            dissmissDialog();
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

    List<PictureProcessingData> imagetools;

    private void setGpuImage() {
        List list2 = new ArrayList();
        if (imageparms.getImageTools() != null && imageparms.getImageTools().size() != 0) {
            imagetools = imageparms.getImageTools();

        } else if (imageparms.getPresetParms() != null && imageparms.getPresetParms().getParmlists() != null && imageparms.getPresetParms().getParmlists().size() != 0) {
            imagetools = imageparms.getPresetParms().getParmlists();
        }
        for (int i = 0; imagetools != null && i < imagetools.size(); i++) {
            GPUImageFilter mFilter;
            GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
            mFilter = BizImageMangage.getInstance().getGPUFilterforType(this, imagetools.get(i).getType());
            list2.add(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);

            if (imagetools.get(i).getType() == BizImageMangage.WHITE_BALANCE) {
                mFilterAdjuster.adjust(imagetools.get(i).getNum(), imagetools.get(i).getTintNum());
            } else {
                mFilterAdjuster.adjust(imagetools.get(i).getNum());
            }
        }
        if (list2.size() != 0) {
            srcFilterGroup = new GPUImageFilterGroup(list2);
        } else {
            srcFilterGroup = new GPUImageFilter();
        }
        gpuImage.setFilter(srcFilterGroup);
    }


    public void showDialog() {
        bgrpaiant.setVisibility(View.GONE);
        mark_main.setEnabled(false);
        eye.setEnabled(false);
        titleView.setEnabled(false);

        if (null != dgProgressDialog && !dgProgressDialog.isShowing()) {
            dgProgressDialog.showDialog();
        }
    }

    public void showWait() {
//        wait_view.setVisibility(View.VISIBLE);
//        wait_progress.setVisibility(View.VISIBLE);
//        gifView.setVisibility(View.GONE);

        dgProgressDialog.showDialog();
        dgProgressDialog.setShowHintText(getResources().getString(R.string.waiting));
    }

    public void dissmissDialog() {
        mark_main.setEnabled(true);
        eye.setEnabled(true);
        titleView.setEnabled(true);
        bgrpaiant.setVisibility(View.VISIBLE);
        if (null != dgProgressDialog && dgProgressDialog.isShowing()) {
            dgProgressDialog.dismiss();
        }
    }


    /*    public void ProcessBitmap(final Bitmap bit, final boolean isundo){
     *//* try {
            final String times = System.currentTimeMillis() + "";
            FileUtil.saveBitmap2(bit, times);
        }catch (IOException e){

        }*//*

        if (imageparms.getImageParts() != null) {
            imagePart = imageparms.getImageParts();
        } else {
            imagePart = new ArrayList<PictureProcessingData>();
        }
        //BgRepairActivity.this.showDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //原图和mask{
                if (isundo) {
                    changBitmap = mangage.ProcessingPic(orginBitmap, bit, BizImageMangage.BACKGROUND_REPAIR, null);
                }else{
                    changBitmap = mangage.ProcessingPic(changBitmap, bit, BizImageMangage.BACKGROUND_REPAIR, null);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (changBitmap != null) {
                            savetoFile();
                        }
                    }

                });
            }
        }).start();
    }*/


    private class BitmapWithFilterCallable implements Callable<Bitmap> {

        public BitmapWithFilterCallable() {
        }

        @Override
        public Bitmap call() throws Exception {
            return gpuImage.getBitmapWithFilterApplied();
        }
    }


    @Override
    protected void initGuide() {
        super.initGuide();
        View ll_rotate = findViewById(R.id.ll_rotate);
        NewbieGuide.with(this)

                .setLabel("pagePhotoEditRepair")//设置引导层标示区分不同引导层，必传！否则报错
                .alwaysShow(false)//是否每次都显示引导层，默认false，只显示一次
                .addGuidePage(//添加一页引导页
                        GuidePage.newInstance()//创建一个实例
                                .addHighLight(ll_rotate)
                                .setLayoutRes(R.layout.guide_photoedit_select)//设置引导页布局
                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {

                                    @Override
                                    public void onLayoutInflated(View view, Controller controller) {
                                        ImageView photoedit_repair = (ImageView) view.findViewById(R.id.photoedit_repair);
                                        photoedit_repair.setVisibility(View.VISIBLE);
                                        //引导页布局填充后回调，用于初始化
                                       /* TextView tv = view.findViewById(R.id.textView2);
                                        tv.setText("我是动态设置的文本");*/
                                    }
                                })
                                .setEnterAnimation(enterAnimation)//进入动画
                                .setExitAnimation(exitAnimation)//退出动画
                )
                .show();//显示引导层(至少需要一页引导页才能显示)
    }
}