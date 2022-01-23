package com.bcnetech.hyphoto.ui.activity.photoedit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.ImageDataSqlControl;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.receiver.AddPicReceiver;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.ui.view.ImageNewUtilsView;
import com.bcnetech.hyphoto.ui.view.ScaleImageView;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.example.imageproc.Process;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.gpumanage.GPUImageFilterTools;

/**
 * 背景修复
 * Created by a1234 on 16/10/15.
 */

public class AutoRecoveryActivity extends BaseActivity {

    private ScaleImageView bgrimage;

    private ImageData imageparms;
    private Bitmap mCurrentBitmap;//原始图片（加上滤镜）
    private Bitmap changBitmap;


    private RelativeLayout rl;
    private Handler handler;
    private BizImageMangage mangage = BizImageMangage.getInstance();

    private String pathList;
    private PictureProcessingData pictureProcessingData;
    private int currentPosition;//当前部分修改操作节点
    private GPUImageFilter srcFilterGroup;
    private final static String CURRENT_IMAGE_DATA = "currentPicDatas";
    private TitleView titleView;
    private SaveMateTask task;
//    private WaitProgressBarView wait_progress;

    private PreferenceUtil preferenceUtil;
    private int bgRepairCount = 0;
    private String url;


    private BifacialView bifacialView;
    private List<PictureProcessingData> imagePart;
    private int orientation;

    private DGProgressDialog3 dgProgressDialog;

    private ImageView recovery0;
    private Button recoveryButton0;
    private RelativeLayout recoveryRel0;
    private Bitmap recoveryBitmap0=null;


    private ImageView recovery1;
    private Button recoveryButton1;
    private RelativeLayout recoveryRel1;
    private Bitmap recoveryBitmap1=null;
    private Bitmap recoveryBitmapOriginal1=null;


    private ImageView recovery2;
    private Button recoveryButton2;
    private RelativeLayout recoveryRel2;
    private Bitmap recoveryBitmap2=null;
    private Bitmap recoveryBitmapOriginal2=null;

    private ImageView recovery3;
    private Button recoveryButton3;
    private RelativeLayout recoveryRel3;
    private Bitmap recoveryBitmap3=null;
    private Bitmap recoveryBitmapOriginal3=null;

    private ImageView recovery4;
    private Button recoveryButton4;
    private RelativeLayout recoveryRel4;
    private Bitmap recoveryBitmap4=null;
    private Bitmap recoveryBitmapOriginal4=null;

    private ImageView recovery5;
    private Button recoveryButton5;
    private RelativeLayout recoveryRel5;
    private Bitmap recoveryBitmap5=null;
    private Bitmap recoveryBitmapOriginal5=null;



      private boolean isOrgin=true;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Flag.PART_PARMS, imageparms);
        outState.putSerializable(CURRENT_IMAGE_DATA, pictureProcessingData);
        outState.putInt(Flag.CURRENT_PART_POSITION, currentPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentPosition = savedInstanceState.getInt(Flag.CURRENT_PART_POSITION);
        imageparms = (ImageData) savedInstanceState.getSerializable(Flag.PART_PARMS);
        pictureProcessingData = (PictureProcessingData) savedInstanceState.getSerializable(CURRENT_IMAGE_DATA);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_auto_recovery);
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
        bgrimage = (ScaleImageView) findViewById(R.id.bgr_imageview);
        rl = (RelativeLayout) findViewById(R.id.rl_bgr);
        bifacialView = (BifacialView) findViewById(R.id.bifacialView);
        titleView = (TitleView) findViewById(R.id.bgrepair_title);

        recovery0=(ImageView)findViewById(R.id.recovery0);
        recoveryButton0=(Button)findViewById(R.id.recoveryButton0);
        recoveryRel0=(RelativeLayout)findViewById(R.id.recoveryRel0);
        recoveryButton0.getBackground().setAlpha(200);

        recovery1=(ImageView)findViewById(R.id.recovery1);
        recoveryButton1=(Button)findViewById(R.id.recoveryButton1);
        recoveryRel1=(RelativeLayout)findViewById(R.id.recoveryRel1);
        recoveryButton1.getBackground().setAlpha(200);

        recovery2=(ImageView)findViewById(R.id.recovery2);
        recoveryButton2=(Button)findViewById(R.id.recoveryButton2);
        recoveryRel2=(RelativeLayout)findViewById(R.id.recoveryRel2);
        recoveryButton2.getBackground().setAlpha(200);

        recovery3=(ImageView)findViewById(R.id.recovery3);
        recoveryButton3=(Button)findViewById(R.id.recoveryButton3);
        recoveryRel3=(RelativeLayout)findViewById(R.id.recoveryRel3);
        recoveryButton3.getBackground().setAlpha(200);


        recovery4=(ImageView)findViewById(R.id.recovery4);
        recoveryButton4=(Button)findViewById(R.id.recoveryButton4);
        recoveryRel4=(RelativeLayout)findViewById(R.id.recoveryRel4);
        recoveryButton4.getBackground().setAlpha(200);

        recovery5=(ImageView)findViewById(R.id.recovery5);
        recoveryButton5=(Button)findViewById(R.id.recoveryButton5);
        recoveryRel5=(RelativeLayout)findViewById(R.id.recoveryRel5);
        recoveryButton5.getBackground().setAlpha(200);

    }



    public void setBackgrond(int type){
        if(type==0){
            recoveryButton0.setVisibility(View.VISIBLE);
            recoveryRel0.setBackground(getResources().getDrawable(R.drawable.shape_button_press));

            recoveryButton1.setVisibility(View.GONE);
            recoveryRel1.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton2.setVisibility(View.GONE);
            recoveryRel2.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton3.setVisibility(View.GONE);
            recoveryRel3.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton4.setVisibility(View.GONE);
            recoveryRel4.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton5.setVisibility(View.GONE);
            recoveryRel5.setBackground(getResources().getDrawable(R.drawable.shape_button));
        }else if(type==1){
            recoveryButton0.setVisibility(View.GONE);
            recoveryRel0.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton1.setVisibility(View.VISIBLE);
            recoveryRel1.setBackground(getResources().getDrawable(R.drawable.shape_button_press));

            recoveryButton2.setVisibility(View.GONE);
            recoveryRel2.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton3.setVisibility(View.GONE);
            recoveryRel3.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton4.setVisibility(View.GONE);
            recoveryRel4.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton5.setVisibility(View.GONE);
            recoveryRel5.setBackground(getResources().getDrawable(R.drawable.shape_button));
        }else if(type==2){
            recoveryButton0.setVisibility(View.GONE);
            recoveryRel0.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton1.setVisibility(View.GONE);
            recoveryRel1.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton2.setVisibility(View.VISIBLE);
            recoveryRel2.setBackground(getResources().getDrawable(R.drawable.shape_button_press));

            recoveryButton3.setVisibility(View.GONE);
            recoveryRel3.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton4.setVisibility(View.GONE);
            recoveryRel4.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton5.setVisibility(View.GONE);
            recoveryRel5.setBackground(getResources().getDrawable(R.drawable.shape_button));
        }else if(type==3){
            recoveryButton0.setVisibility(View.GONE);
            recoveryRel0.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton1.setVisibility(View.GONE);
            recoveryRel1.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton2.setVisibility(View.GONE);
            recoveryRel2.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton3.setVisibility(View.VISIBLE);
            recoveryRel3.setBackground(getResources().getDrawable(R.drawable.shape_button_press));

            recoveryButton4.setVisibility(View.GONE);
            recoveryRel4.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton5.setVisibility(View.GONE);
            recoveryRel5.setBackground(getResources().getDrawable(R.drawable.shape_button));
        }else if(type==4){
            recoveryButton0.setVisibility(View.GONE);
            recoveryRel0.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton1.setVisibility(View.GONE);
            recoveryRel1.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton2.setVisibility(View.GONE);
            recoveryRel2.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton3.setVisibility(View.GONE);
            recoveryRel3.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton4.setVisibility(View.VISIBLE);
            recoveryRel4.setBackground(getResources().getDrawable(R.drawable.shape_button_press));

            recoveryButton5.setVisibility(View.GONE);
            recoveryRel5.setBackground(getResources().getDrawable(R.drawable.shape_button));
        }else if(type==5){
            recoveryButton0.setVisibility(View.GONE);
            recoveryRel0.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton1.setVisibility(View.GONE);
            recoveryRel1.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton2.setVisibility(View.GONE);
            recoveryRel2.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton3.setVisibility(View.GONE);
            recoveryRel3.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton4.setVisibility(View.GONE);
            recoveryRel4.setBackground(getResources().getDrawable(R.drawable.shape_button));

            recoveryButton5.setVisibility(View.VISIBLE);
            recoveryRel5.setBackground(getResources().getDrawable(R.drawable.shape_button_press));
        }

    }




    @Override
    protected void initData() {
        dgProgressDialog = new DGProgressDialog3(this, false, getResources().getString(R.string.repairing));

        dissmissDialog();
        titleView.setType(TitleView.IMAGE_BG_REPAIR);
        titleView.setTitleText("原图");
        setGpuImage();
        pathList = pictureProcessingData.getImageUrl();
        ImageUtil.EBizImageLoad(imageparms.getSmallLocalUrl(), new ImageLoadingListener() {
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

           /*     if (bitmap.getWidth() > 2560 || bitmap.getHeight() > 2560) {
                    bitmap = ImageUtil.newDecodeSampledBitmapFromFile(pictureProcessingData.getImageUrl().substring(7), bitmap, 2560, 2560);
                }*/

                mCurrentBitmap = bitmap;
                changBitmap = Bitmap.createBitmap(bitmap);
                bgrimage.setBitmap(mCurrentBitmap, null);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);

                handler = new Handler();
                bgrimage.invalidate();

                autoRecovery(Process.JNIAPI_METHOD_DECOLOR_PREV);

            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

        if (pathList.contains(".png")) {
            rl.setBackground(getResources().getDrawable(R.drawable.bgbitmap));
        }
    }

    private void savetoFile(Bitmap bitmap) {
        bgrimage.resetBitmap(bitmap);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // cleanallcache();

        if (mCurrentBitmap != null) {
            mCurrentBitmap.recycle();
        }
        if (changBitmap != null) {
            changBitmap.recycle();
        }

        srcFilterGroup = null;
        if (task != null) {
            task.cancel(true);
        }
        task = null;

    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        }
        return true;
    }

    @Override
    protected void onViewClick() {

        titleView.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleView.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(isOrgin){
                   finish();
               }else{
                   showWait();
                   task = new SaveMateTask(changBitmap);
                   task.execute();
               }
            }
        });

        recoveryRel0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgrond(0);
                isOrgin=true;
                titleView.setTitleText("原图");
                changBitmap=mCurrentBitmap;
                savetoFile(changBitmap);
            }
        });
        recoveryRel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgrond(1);
                isOrgin=false;
                titleView.setTitleText("M1");
                if( recoveryBitmapOriginal1!=null){
                    changBitmap=recoveryBitmapOriginal1;
                    savetoFile(recoveryBitmapOriginal1);
                }else{
                    autoRecovery(Process.JNIAPI_METHOD_DECOLOR_1);
                }


            }
        });
        recoveryRel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgrond(2);
                isOrgin=false;
                titleView.setTitleText("M2");
                if( recoveryBitmapOriginal2!=null){
                    changBitmap=recoveryBitmapOriginal2;
                    savetoFile(recoveryBitmapOriginal2);
                }else{
                    autoRecovery(Process.JNIAPI_METHOD_DECOLOR_2);
                }
            }
        });
        recoveryRel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgrond(3);
                isOrgin=false;
                titleView.setTitleText("M3");
                if( recoveryBitmapOriginal3!=null){
                    changBitmap=recoveryBitmapOriginal3;
                    savetoFile(recoveryBitmapOriginal3);
                }else{
                    autoRecovery(Process.JNIAPI_METHOD_DECOLOR_3);
                }
            }
        });
        recoveryRel4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgrond(4);
                isOrgin=false;
                titleView.setTitleText("M4");
                if( recoveryBitmapOriginal4!=null){
                    changBitmap=recoveryBitmapOriginal4;
                    savetoFile(recoveryBitmapOriginal4);
                }else{
                    autoRecovery(Process.JNIAPI_METHOD_DECOLOR_4);
                }

            }
        });
        recoveryRel5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgrond(5);
                isOrgin=false;
                titleView.setTitleText("M5");
                if( recoveryBitmapOriginal5!=null){
                    changBitmap=recoveryBitmapOriginal5;
                    savetoFile(recoveryBitmapOriginal5);
                }else{
                    autoRecovery(Process.JNIAPI_METHOD_DECOLOR_5);
                }
            }
        });

    }


    private class SaveMateTask extends AsyncTask<Void, Void, ImageData> {
        private Bitmap bitmap;


        SaveMateTask(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        protected void onPreExecute() {
            //dialogHelper.showProgressDialog("处理中");
        }

        @Override
        protected ImageData doInBackground(Void... params) {
            long time = System.currentTimeMillis();
            String path1 = null;
            String path2 = null;
            try {

                path1 = FileUtil.saveBitmap(bitmap, time - 1 + "", false);
                path1 = "file://" + path1;

                path2 =  "file://" +FileUtil.copyFile(path1, Flag.NATIVESDFILE, time + ".jpg");


                //path2 = FileUtil.saveBitmaptoNative(bitmap, time + "");
                //path2 = "file://" + path2;
                if (StringUtil.isBlank(path1)) {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            ImageData imageData = new ImageData();
            imageData.setType(Flag.TYPE_PIC);
            imageData.setLocalUrl(path1);
            imageData.setSmallLocalUrl(path2);
            imageData.setTimeStamp(time);
            imageData.setMatting(true);

            LightRatioData lightRatioData = new LightRatioData();
            lightRatioData.initData();
            imageData.setLightRatioData(lightRatioData);

            return imageData;
        }

        @Override
        protected void onPostExecute(ImageData result) {
            super.onPostExecute(result);

            if (result != null) {
                ImageDataSqlControl imageDataSqlControl = new ImageDataSqlControl(AutoRecoveryActivity.this);
                imageDataSqlControl.setListener(new BaseSqlControl.SqlControlListener() {
                    @Override
                    public void queryListener(Object... parms) {

                    }

                    @Override
                    public void insertListener(Object... parms) {
                        setResult(Flag.MATTING_PARMS_ACTIVITY);
                        dissmissDialog();
                        finish();
                    }

                    @Override
                    public void deletListener(Object... parms) {

                    }

                    @Override
                    public void upDataListener(Object... parms) {

                    }
                });
                imageDataSqlControl.insertImg(result);
                AutoRecoveryActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(result.getSmallLocalUrl())));
                AddPicReceiver.notifyModifyUsername(AutoRecoveryActivity.this, "add");
            } else {
                ToastUtil.toast(getResources().getString(R.string.add_error));
            }


        }
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

    }


    public void showDialog() {
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
        titleView.setEnabled(true);
        if (null != dgProgressDialog && dgProgressDialog.isShowing()) {
            dgProgressDialog.dismiss();
        }
    }
    public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    public void autoRecovery(final int type) {
        showDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //原图和mask{
                // mark_bg_Clicked = true;
                //   tempBit = changBitmap;
                List<Bitmap>  bitmaps=null;
                if(type==Process.JNIAPI_METHOD_DECOLOR_PREV){
                    Bitmap sxBitmap=zoomImg(mCurrentBitmap,500,500);
                    bitmaps = mangage.ProcessingAutoRepair(sxBitmap,type);
                }else{

                    bitmaps = mangage.ProcessingAutoRepair(mCurrentBitmap,type);
                }

               // List<Bitmap>  bitmaps = mangage.ProcessingAutoRepair(mCurrentBitmap,type);
                final List<Bitmap>  changBitmaps=bitmaps;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (changBitmaps != null&&changBitmaps.size()==6) {
                            recoveryBitmap0=changBitmaps.get(0);
                            recovery0.setImageBitmap(recoveryBitmap0);

                            recoveryBitmap1=changBitmaps.get(1);
                            recovery1.setImageBitmap(recoveryBitmap1);

                            recoveryBitmap2=changBitmaps.get(2);
                            recovery2.setImageBitmap(recoveryBitmap2);

                            recoveryBitmap3=changBitmaps.get(3);
                            recovery3.setImageBitmap(recoveryBitmap3);

                            recoveryBitmap4=changBitmaps.get(4);
                            recovery4.setImageBitmap(recoveryBitmap4);

                            recoveryBitmap5=changBitmaps.get(5);
                            recovery5.setImageBitmap(recoveryBitmap5);
                            AutoRecoveryActivity.this.dissmissDialog();
                            savetoFile(changBitmap);
                            showGuide();
                        }

                        if(changBitmaps != null&&changBitmaps.size()==1){
                            if(type==Process.JNIAPI_METHOD_DECOLOR_1){
                               recoveryBitmapOriginal1=changBitmaps.get(0);
                                changBitmap=recoveryBitmapOriginal1;
                            }else if(type==Process.JNIAPI_METHOD_DECOLOR_2){
                                recoveryBitmapOriginal2=changBitmaps.get(0);
                                changBitmap=recoveryBitmapOriginal2;
                            }else if(type==Process.JNIAPI_METHOD_DECOLOR_3){
                                recoveryBitmapOriginal3=changBitmaps.get(0);
                                changBitmap=recoveryBitmapOriginal3;
                            }else if(type==Process.JNIAPI_METHOD_DECOLOR_4){
                                recoveryBitmapOriginal4=changBitmaps.get(0);
                                changBitmap=recoveryBitmapOriginal4;
                            }else if(type==Process.JNIAPI_METHOD_DECOLOR_5){
                                recoveryBitmapOriginal5=changBitmaps.get(0);
                                changBitmap=recoveryBitmapOriginal5;
                            }


                            AutoRecoveryActivity.this.dissmissDialog();
                            savetoFile(changBitmaps.get(0));

                        }

                    }

                });
            }
        }).start();
    }
    @Override
    protected void initGuide() {
        super.initGuide();
    }
    private void showGuide(){

        View ll_rotate=findViewById(R.id.ll_rotate);
        NewbieGuide.with(this)

                .setLabel("pagePhotoEditAuto")//设置引导层标示区分不同引导层，必传！否则报错
                .alwaysShow(false)//是否每次都显示引导层，默认false，只显示一次
                .addGuidePage(//添加一页引导页
                        GuidePage.newInstance()//创建一个实例
                                .addHighLight(ll_rotate)
                                .setLayoutRes(R.layout.guide_photoedit_select)//设置引导页布局
                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {

                                    @Override
                                    public void onLayoutInflated(View view, Controller controller) {
                                        ImageView photoedit_autorecovery = (ImageView)view.findViewById(R.id.photoedit_autorecovery);
                                        photoedit_autorecovery.setVisibility(View.VISIBLE);
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