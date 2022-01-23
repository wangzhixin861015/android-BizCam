package com.bcnetech.hyphoto.ui.activity.photoedit;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.CropListdata;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.ImageDataSqlControl;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.receiver.AddPicReceiver;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.ui.adapter.CropAdapter;
import com.bcnetech.hyphoto.ui.view.ImageNewUtilsView;
import com.bcnetech.hyphoto.ui.view.RotateRularView;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.ui.view.croprotate.CropImageView;
import com.bcnetech.hyphoto.ui.view.croprotate.GestureCropImageView;
import com.bcnetech.hyphoto.ui.view.croprotate.OverlayView;
import com.bcnetech.hyphoto.ui.view.croprotate.TransformImageView;
import com.bcnetech.hyphoto.ui.view.croprotate.UCropView;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yalantis.ucrop.callback.BitmapCropCallback;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.gpumanage.GPUImageFilterTools;

/**
 * 旋转裁剪
 * Created by a1234 on 17/10/31.
 */

public class CropRotateActivity extends BaseActivity {
    private GPUImage gpuImage;
    private GPUImageFilter srcFilterGroup;
    private TitleView titleView;
    private RotateRularView crop_rotates2;
    private UCropView ucrop;
    private GestureCropImageView mGestureCropImageView;
    private OverlayView mOverlayView;
    private RelativeLayout mirror_l, mirror_t, rotate_l, rotate_r;
    private ImageView crop_select;
    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    public static final Bitmap.CompressFormat SECOND_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private ImageData imageparms;
    private float currentAngel = 0;

    private PictureProcessingData pictureProcessingData;
    private int currentPosition;//当前部分修改操作节点String
    private String originPath;
    private String pathList;
    private final static String CURRENT_IMAGE_DATA = "currentPicDatas";
    private RelativeLayout wait_view;
    private PopupWindow mCurPopupWindow;
    private CropAdapter cropAdapter;
    private List<CropListdata> cropListdatas;
    private float currentRatio = 0;
    private boolean rotatechange = false;//判断当前图片呈横向还是纵向
    private boolean isPng = false;
    private Uri outputUri;
    private boolean isCroping = false;
    private int[] originLength;
    private String tempPath = "";
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                pathList = "file://" + Flag.LOCAL_IMAGE_PATH + "croped.jpg";
                tempPath = Flag.LOCAL_IMAGE_PATH + "croped.jpg";
            } else if (msg.what == 2) {
                tempPath = "";
            }
            File file = new File( Flag.LOCAL_IMAGE_PATH + "croped.jpg");
            if (!file.exists()){
               pathList= originPath;
               tempPath = "";
            }
            Uri inputUri = Uri.parse(pathList);
            String output = "";
            if (pathList.contains(".png")) {
                output = Flag.APP_CAMERAL + System.currentTimeMillis() + ".png";
                isPng = true;
            } else {
                output = Flag.APP_CAMERAL + System.currentTimeMillis() + ".jpg";
                isPng = false;
            }
            outputUri = Uri.parse(output);
            originLength = ImageUtil.decodeUriAsBitmap(output);
            mGestureCropImageView.setImageUri(inputUri, outputUri, isPng, imagetools);
            mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO, true);
            if (pathList.contains(".png")) {
                mGestureCropImageView.setBackground(getResources().getDrawable(R.drawable.bgbitmap));
            }
            dissmissDialog();
            return false;
        }
    });


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
        setContentView(R.layout.new_layout_rotate);
        currentPosition = getIntent().getIntExtra(Flag.CURRENT_PART_POSITION, -1);
//        imageparms = (ImageData) getIntent().getSerializableExtra(Flag.PART_PARMS);
        imageparms= ImageNewUtilsView.imageparms;
        if (imageparms == null) {
            finish();
            return;
        }
        // String url = BizImageMangage.getInstance().getUrlFromCurrentPostion(currentPosition, imageparms, false, false);
        String url = BizImageMangage.getInstance().getUrlFromCurrentPostion(currentPosition, imageparms, true, true);
        if (StringUtil.isBlank(url)) {
            ToastUtil.toast(getResources().getString(R.string.data_error));
            finish();
            return;
        }
        pictureProcessingData = new PictureProcessingData(BizImageMangage.ROTATE, url);
        initView();
        initData();
        onViewClick();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGestureCropImageView != null) {
            mGestureCropImageView.cancelAllAnimations();
        }
    }

    @Override
    protected void initView() {
        gpuImage = new GPUImage(this);
        ucrop = (UCropView) findViewById(R.id.ucrop);
        mGestureCropImageView = ucrop.getCropImageView();
        mOverlayView = ucrop.getOverlayView();
        crop_rotates2 = ucrop.getRotateRularView();
        mGestureCropImageView.setTransformImageListener(mImageListener);
        titleView = (TitleView) findViewById(R.id.rotate_title);
        wait_view = (RelativeLayout) findViewById(R.id.wait_view);
        crop_select = (ImageView) findViewById(R.id.crop_select);
        mirror_l = (RelativeLayout) findViewById(R.id.mirror_l);
        mirror_t = (RelativeLayout) findViewById(R.id.mirror_t);
        rotate_l = (RelativeLayout) findViewById(R.id.rotate_l);
        rotate_r = (RelativeLayout) findViewById(R.id.rotate_r);
    }

    private TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {
        }

        @Override
        public void onScale(float currentScale) {
        }

        @Override
        public void onLoadComplete() {
        }

        @Override
        public void onLoadFailure(@NonNull Exception e) {
            finish();
        }

    };

    @Override
    protected void initData() {
        setGpuImage();
        titleView.setType(TitleView.PIC_PARMS_NEW);
        //  titleView.setBackgroundColor(getResources().getColor(R.color.trans_backgroud));
        titleView.bringToFront();
        setText("00.00°");
        originPath = pictureProcessingData.getImageUrl();
        pathList = pictureProcessingData.getImageUrl();
        showTransformDialog();
        Glide.with(CropRotateActivity.this).load(pathList).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(final Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                int min = Math.min(bitmap.getWidth(), bitmap.getHeight());
                if (min > BitmapUtils.MAXLENGTH) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap scalebitmap = BitmapUtils.rescaleBitmap(bitmap);
                            BitmapUtils.saveBitmap(scalebitmap, Flag.LOCAL_IMAGE_PATH + "croped.jpg");
                            handler.sendEmptyMessage(1);
                        }
                    }).start();
                } else {
                    handler.sendEmptyMessage(2);
                }
            }
        });
       /* Picasso.get().load(pathList).into(new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                showTransformDialog();
                int min = Math.min(bitmap.getWidth(), bitmap.getHeight());
                if (min > BitmapUtils.MAXLENGTH) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap scalebitmap = BitmapUtils.rescaleBitmap(bitmap);
                            BitmapUtils.saveBitmap(scalebitmap, Flag.LOCAL_IMAGE_PATH + "croped.jpg");
                            handler.sendEmptyMessage(1);
                        }
                    }).start();
                } else {
                    handler.sendEmptyMessage(2);
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });*/
        // Crop image view options
        mGestureCropImageView.setMaxBitmapSize(CropImageView.DEFAULT_MAX_BITMAP_SIZE);
        mGestureCropImageView.setMaxScaleMultiplier(CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER);
        mGestureCropImageView.setImageToWrapCropBoundsAnimDuration(CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION);

        // Overlay view  options
        mOverlayView.setFreestyleCropEnabled(true);

        mOverlayView.setDimmedColor(getResources().getColor(R.color.ucrop_color_default_dimmed));
        mOverlayView.setCircleDimmedLayer(OverlayView.DEFAULT_CIRCLE_DIMMED_LAYER);

        mOverlayView.setShowCropFrame(OverlayView.DEFAULT_SHOW_CROP_FRAME);
        mOverlayView.setShowTriangel(OverlayView.DEFAULT_SHOW_TRIANGEL);
        mOverlayView.setCropFrameColor(getResources().getColor(R.color.ucrop_color_default_crop_frame));
        mOverlayView.setCropFrameStrokeWidth(getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_frame_stoke_width));

        mOverlayView.setShowCropGrid(OverlayView.DEFAULT_SHOW_CROP_GRID);
        mOverlayView.setCropGridRowCount(OverlayView.DEFAULT_CROP_GRID_ROW_COUNT);
        mOverlayView.setCropGridColumnCount(OverlayView.DEFAULT_CROP_GRID_COLUMN_COUNT);
        mOverlayView.setCropGridColor(getResources().getColor(R.color.ucrop_color_default_crop_grid));
        mOverlayView.setCropGridStrokeWidth(getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_grid_stoke_width));
    }


    /**
     * 裁剪框比例
     */
    private void setCropByGive() {
        if (cropListdatas == null || cropListdatas.size() == 0) {
            return;
        }
        for (CropListdata cropListdata : cropListdatas) {
            if (cropListdata.isSelect()) {
                if (cropListdata.getCropText().equals(getResources().getString(R.string.free))) {
                    currentRatio = 0.0f;
                    mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE);
                } else if (cropListdata.getCropText().equals(getResources().getString(R.string.original))) {
                    currentRatio = 0.0f;
                    mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_DISABLE);
                } else if (cropListdata.getCropText().equals("1 ：1")) {
                    currentRatio = 1.0f;
                    mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH);
                } else if (cropListdata.getCropText().equals("4 ：5")) {
                    if (cropListdata.isReverse()) {
                        currentRatio = (float) 5 / (float) 4;
                    } else {
                        currentRatio = (float) 4 / (float) 5;
                    }
                    mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH);
                } else if (cropListdata.getCropText().equals("5 ：7")) {
                    if (cropListdata.isReverse()) {
                        currentRatio = (float) 7 / (float) 5;
                    } else {
                        currentRatio = (float) 5 / (float) 7;

                    }
                    mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH);
                } else if (cropListdata.getCropText().equals("2 ：3")) {
                    if (cropListdata.isReverse()) {
                        currentRatio = (float) 3 / (float) 2;
                    } else {
                        currentRatio = (float) 2 / (float) 3;

                    }
                    mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH);
                } else if (cropListdata.getCropText().equals("9 ：16")) {
                    if (cropListdata.isReverse()) {
                        currentRatio = (float) 16 / (float) 9;
                    } else {
                        currentRatio = (float) 9 / (float) 16;
                    }
                    mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH);
                } else if (cropListdata.getCropText().equals("3 ：4")) {
                    if (cropListdata.isReverse()) {
                        currentRatio = (float) 4 / (float) 3;

                    } else {
                        currentRatio = (float) 3 / (float) 4;
                    }
                    mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH);
                }
              /*  switch (cropListdata.getCropText()) {
                    case "自由裁剪":
                        currentRatio = 0.0f;
                        mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE);
                        break;
                    case "原始比例":
                        currentRatio = 0.0f;
                        mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_DISABLE);
                        break;
                    case "1 ：1":
                        currentRatio = 1.0f;
                        mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH);
                        break;
                    case "4 ：5":
                        if (cropListdata.isReverse()) {
                            currentRatio = (float) 5 / (float) 4;
                        } else {
                            currentRatio = (float) 4 / (float) 5;
                        }
                        mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH);
                        break;
                    case "5 ：7":
                        if (cropListdata.isReverse()) {
                            currentRatio = (float) 7 / (float) 5;
                        } else {
                            currentRatio = (float) 5 / (float) 7;

                        }
                        mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH);
                        break;
                    case "2 ：3":
                        if (cropListdata.isReverse()) {
                            currentRatio = (float) 3 / (float) 2;
                        } else {
                            currentRatio = (float) 2 / (float) 3;

                        }
                        mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH);
                        break;
                    case "3 ：4":
                        if (cropListdata.isReverse()) {
                            currentRatio = (float) 4 / (float) 3;

                        } else {
                            currentRatio = (float) 3 / (float) 4;
                        }
                        mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH);
                        break;
                    case "9 ：16":
                        if (cropListdata.isReverse()) {
                            currentRatio = (float) 16 / (float) 9;
                        } else {
                            currentRatio = (float) 9 / (float) 16;
                        }
                        mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH);
                        break;
                }*/
            }
        }
        setCropRatio(currentRatio);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // KeyEvent.KEYCODE_BACK代表返回操作.
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 处理返回操作.
        }
        return true;
    }


    @Override
    protected void onViewClick() {
        mGestureCropImageView.setGestureCropInter(new GestureCropImageView.GestureCropInter() {
            @Override
            public void rotateAngel(float rotateAngel) {
                currentAngel = rotateAngel;
                crop_rotates2.setRotate(rotateAngel);
                mGestureCropImageView.rotateScale(rotateAngel);
                String a = new DecimalFormat("###,###,00.00").format(-rotateAngel);
                setText(a + "°");
            }
        });

        titleView.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wait_view.getVisibility() != View.VISIBLE) {
                    finish();
                }

            }
        });
        titleView.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCroping) {
                    cropAndSaveImage();
                }
               /* if (wait_view.getVisibility() != View.VISIBLE) {

                }*/
            }

        });

        crop_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCroping) {
                    if (mCurPopupWindow != null && mCurPopupWindow.isShowing()) {
                        mCurPopupWindow.dismiss();
                        crop_select.setImageDrawable(getResources().getDrawable(R.drawable.crop_n));
                    } else {
                        crop_select.setImageDrawable(getResources().getDrawable(R.drawable.crop_y));
                        mCurPopupWindow = showTipPopupWindow(crop_select, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                    }
                }
            }
        });
        rotate_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurPopupWindow != null && mCurPopupWindow.isShowing()) {
                    mCurPopupWindow.dismiss();
                    crop_select.setImageDrawable(getResources().getDrawable(R.drawable.crop_n));
                }
                if (!isCroping) {
                    if (rotatechange) {
                        rotatechange = false;
                    } else {
                        rotatechange = true;
                    }
                    rotateByAngle(-90);
                    //setCropRatio(1 / mGestureCropImageView.getCurrentRatio());
                }
            }
        });

        rotate_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurPopupWindow != null && mCurPopupWindow.isShowing()) {
                    mCurPopupWindow.dismiss();
                    crop_select.setImageDrawable(getResources().getDrawable(R.drawable.crop_n));
                }
                if (!isCroping) {
                    if (rotatechange) {
                        rotatechange = false;
                    } else {
                        rotatechange = true;
                    }
                    rotateByAngle(90);
                    //setCropRatio(1 / mGestureCropImageView.getCurrentRatio());
                }
            }
        });

        mirror_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurPopupWindow != null && mCurPopupWindow.isShowing()) {
                    mCurPopupWindow.dismiss();
                    crop_select.setImageDrawable(getResources().getDrawable(R.drawable.crop_n));
                }
                if (!isCroping) {
                    if (rotatechange) {
                        setMirror_t();
                    } else {
                        setMirror_l();

                    }
                    currentAngel = -currentAngel;
                    reverseRotation();
                    mGestureCropImageView.setCurrentNum(currentAngel);
                }
            }
        });

        mirror_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurPopupWindow != null && mCurPopupWindow.isShowing()) {
                    mCurPopupWindow.dismiss();
                    crop_select.setImageDrawable(getResources().getDrawable(R.drawable.crop_n));
                }
                if (!isCroping) {
                    if (rotatechange) {
                        setMirror_l();
                    } else {
                        setMirror_t();

                    }
                    currentAngel = -currentAngel;
                    reverseRotation();
                    mGestureCropImageView.setCurrentNum(currentAngel);
                }
            }
        });
    }


    private void setCropRatio(float currentRatio) {
        mGestureCropImageView.setTargetAspectRatio(currentRatio, false);
        this.currentRatio = currentRatio;
        mGestureCropImageView.setScaleMin();
        mGestureCropImageView.setImageToWrapCropBounds();
    }


    /**
     * 设置text
     */
    private void setText(String text) {
        titleView.setTitleText(text);
    }


    //保存最终图片和未加滤镜的修改图片
    private class SaveRotateTask extends AsyncTask<Void, Void, ArrayList> {
        private Uri uri;//原图（裁剪过后）
        private Bitmap finalbitmap;//加过滤镜（裁剪过后）

        SaveRotateTask(Uri uri, Bitmap finalbitmap) {
            this.uri = uri;
            this.finalbitmap = finalbitmap;
        }

        protected void onPreExecute() {
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
                path1 = uri.toString();
//                if (imageparms.getSmallLocalUrl().endsWith(".png")) {
//                    imageparms.setMatting(true);
//                } else {
//                    imageparms.setMatting(false);
//                }
                path2 = FileUtil.saveBitmaptoNative(finalbitmap, time + 1 + "", imageparms.getSmallLocalUrl().contains(".png"));

                path3 = FileUtil.saveBitmaptoShareNative(imageparms.getSmallLocalUrl().contains(".png") ? BitmapUtils.compressPNG(finalbitmap) : BitmapUtils.compress(finalbitmap), time + 2 + "", imageparms.getSmallLocalUrl().contains(".png"));


                if (StringUtil.isBlank(path1) || StringUtil.isBlank(path2)) {
                    return null;
                }
                path2 = "file://" + path2;
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

                    processlist.add(new PictureProcessingData(BizImageMangage.ROTATE, processurl, smallUrl, ""));
                    //
                    if (imageparms.getCurrentPosition() == 0) {

                        imageparms.setImageTools(new ArrayList<PictureProcessingData>());
                        for (int i = 0; i < imageparms.getImageParts().size(); i++) {
                        }
                        imageparms.setImageParts(processlist);//设置部分修改
                        imageparms.setCurrentPosition(//设置当前位置
                                imageparms.getCurrentPosition() +
                                        (processlist.size()));
                    } else {//当前位置不为0

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
                    final ImageDataSqlControl imageDataSqlControl = new ImageDataSqlControl(CropRotateActivity.this);
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
                            imageDataSqlControl.startUpdata(new String[]{imageparms.getLocalUrl()}, imageparms);
                            CropRotateActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(imageparms.getSmallLocalUrl())));
                            AddPicReceiver.notifyModifyUsername(CropRotateActivity.this, "refresh");
                        }
                    });

                }
            } else {
                ToastUtil.toast(getResources().getString(R.string.change_fail));
            }
            isCroping = false;
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

    private void setFilteredBitmap(Uri uri) {
        setGpuImage();
        Bitmap filebitmap = BitmapFactory.decodeFile(uri.toString().substring(7));
        filebitmap = gpuImage.getBitmapWithFilterApplied(filebitmap);
        if (filebitmap.getWidth() != originLength[0] && filebitmap.getWidth() > 4) {
            filebitmap = Bitmap.createBitmap(filebitmap, 0, 0, (int) (filebitmap.getWidth() - 3), filebitmap.getHeight());
        }
        SaveRotateTask saveRotateTask = new SaveRotateTask(uri, filebitmap);
        saveRotateTask.execute();
    }

    //重置旋转
    private void resetRotation() {
        mGestureCropImageView.postRotate(-mGestureCropImageView.getCurrentAngle());
        mGestureCropImageView.setImageToWrapCropBounds();
    }

    //逆向旋转
    private void reverseRotation() {
        mGestureCropImageView.resetMatrix();
        // mGestureCropImageView.postRotate(-mGestureCropImageView.getCurrentAngle());
        mGestureCropImageView.setImageToWrapCropBounds();
        mGestureCropImageView.rotateScale(currentAngel);
    }

    //纯旋转，不放大
    private void rotateByAngle(float angle) {
        mGestureCropImageView.postRotate(angle);
        //mGestureCropImageView.setScaleMin();
        mGestureCropImageView.setImageToWrapCropBounds();
    }

    private void setMirror_l() {
        mGestureCropImageView.setMirror_L();
    }

    private void setMirror_t() {
        mGestureCropImageView.setMirror_T();
    }

    //裁剪保存图片
    protected void cropAndSaveImage() {
        isCroping = true;
        showTransformDialog();
       /* ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                String inputUri = setMirrorBitmap();
                mGestureCropImageView.setmImageInputPath(inputUri);
                mGestureCropImageView.setmCurrentImageMatrix();
                Bitmap.CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
                if (isPng) {
                    compressFormat = SECOND_COMPRESS_FORMAT;
                }
                mGestureCropImageView.cropAndSaveImage(compressFormat, 100, new BitmapCropCallback() {

                    @Override
                    public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
                        setFilteredBitmap(resultUri);
                    }

                    @Override
                    public void onCropFailure(@NonNull Throwable t) {
                        dissmissDialog();
                        isCroping = false;
                        ToastUtil.toast(getResources().getString(R.string.save_error));
                        //  finish();
                    }
                });
            }
        });*/
        String inputUri = setMirrorBitmap();
        mGestureCropImageView.setmImageInputPath(inputUri);
        mGestureCropImageView.setmCurrentImageMatrix();
        Bitmap.CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
        if (isPng) {
            compressFormat = SECOND_COMPRESS_FORMAT;
        }
        mGestureCropImageView.cropAndSaveImage(compressFormat, 100, new BitmapCropCallback() {

            @Override
            public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
                setFilteredBitmap(resultUri);
            }

            @Override
            public void onCropFailure(@NonNull Throwable t) {
                dissmissDialog();
                isCroping = false;
                ToastUtil.toast(getResources().getString(R.string.save_error));
                //  finish();
            }
        });
    }

    private String setMirrorBitmap() {
        String inputUri = "";
        Bitmap currentBitmap = mGestureCropImageView.getCurrentBitmap();
        String name = System.currentTimeMillis() + "";
        try {
            inputUri = FileUtil.cacheSaveBitmap(currentBitmap, name, isPng);
        } catch (IOException e) {

        }
        return inputUri;
    }

    private String getBitmapwithFilted(Bitmap bitmap) {
        String inputUri = "";
        return inputUri;
    }


    //
    public PopupWindow showTipPopupWindow(final View anchorView, final View.OnClickListener onClickListener) {
        final View contentView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.popuw_content_top_arrow_layout, null);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 创建PopupWindow时候指定高宽时showAsDropDown能够自适应
        // 如果设置为wrap_content,showAsDropDown会认为下面空间一直很充足（我以认为这个Google的bug）
        // 备注如果PopupWindow里面有ListView,ScrollView时，一定要动态设置PopupWindow的大小
      /*  mCurPopupWindow = new PopupWindow(contentView,
                mGestureCropImageView.getWidth() ,contentView.getMeasuredHeight(), false);*/
        mCurPopupWindow = new PopupWindow(contentView,
                contentView.getMeasuredWidth(), contentView.getMeasuredHeight(), false);

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurPopupWindow.dismiss();
                onClickListener.onClick(v);
            }
        });
        RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.tip_text);
       /* ImageView iv_bg = (ImageView)contentView.findViewById(R.id.iv_bg);
        iv_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurPopupWindow.dismiss();
            }
        });*/
        recyclerView.setLayoutManager(new LinearLayoutManager(CropRotateActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if (cropListdatas == null) {
            cropListdatas = setListData();
        }
        cropAdapter = new CropAdapter(cropListdatas, CropRotateActivity.this);
        recyclerView.setAdapter(cropAdapter);

        cropAdapter.setOnItemClickListener(new CropAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                for (int i = 0; i < cropListdatas.size(); i++) {
                    if (i != position) {
                        cropListdatas.get(i).setSelect(false);
                    } else {
                        cropListdatas.get(i).setSelect(true);
                    }
                }
                cropAdapter.setListdata(cropListdatas);
                setCropByGive();
            }

            @Override
            public void onReverseClick(int position) {
                for (int i = 0; i < cropListdatas.size(); i++) {
                    if (i == position) {
                        cropListdatas.get(i).isReverse();
                        cropListdatas.get(i).setReverse(!cropListdatas.get(i).isReverse());
                    }
                }
                cropAdapter.setListdata(cropListdatas);
                setCropByGive();
            }
        });
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 自动调整箭头的位置
                autoAdjustArrowPos(mCurPopupWindow, contentView, anchorView);
                contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        mCurPopupWindow.setBackgroundDrawable(new ColorDrawable());

        // setOutsideTouchable设置生效的前提是setTouchable(true)和setFocusable(false)
        mCurPopupWindow.setOutsideTouchable(true);

        // 设置为true之后，PopupWindow内容区域 才可以响应点击事件
        mCurPopupWindow.setTouchable(true);

        // true时，点击返回键先消失 PopupWindow
        // 但是设置为true时setOutsideTouchable，setTouchable方法就失效了（点击外部不消失，内容区域也不响应事件）
        // false时PopupWindow不处理返回键
        mCurPopupWindow.setFocusable(true);
        mCurPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;   // 这里面拦截不到返回键
            }
        });
        // 如果希望showAsDropDown方法能够在下面空间不足时自动在anchorView的上面弹出
        // 必须在创建PopupWindow的时候指定高度，不能用wrap_content
        mCurPopupWindow.showAsDropDown(anchorView);
        mCurPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                crop_select.setImageDrawable(getResources().getDrawable(R.drawable.crop_n));
            }
        });
        return mCurPopupWindow;
    }

    private void autoAdjustArrowPos(PopupWindow popupWindow, View contentView, View anchorView) {
        View upArrow = contentView.findViewById(R.id.up_arrow);
        View downArrow = contentView.findViewById(R.id.down_arrow);
        int pos[] = new int[2];
        contentView.getLocationOnScreen(pos);
        int popLeftPos = pos[0];
        anchorView.getLocationOnScreen(pos);
        int anchorLeftPos = pos[0];
        int arrowLeftMargin = anchorLeftPos - popLeftPos + anchorView.getWidth() / 2 - upArrow.getWidth() / 2 - ContentUtil.dip2px(this, 5);
        upArrow.setVisibility(popupWindow.isAboveAnchor() ? View.INVISIBLE : View.VISIBLE);
        downArrow.setVisibility(popupWindow.isAboveAnchor() ? View.VISIBLE : View.INVISIBLE);

        RelativeLayout.LayoutParams upArrowParams = (RelativeLayout.LayoutParams) upArrow.getLayoutParams();
        upArrowParams.leftMargin = arrowLeftMargin;
        RelativeLayout.LayoutParams downArrowParams = (RelativeLayout.LayoutParams) downArrow.getLayoutParams();
        downArrowParams.leftMargin = arrowLeftMargin;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imagetools = null;
       /* if (!StringUtil.isBlank(tempPath)) {
            try {
                File dir = new File(tempPath);
                dir.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
      /*  File dirFiles = new File(Environment.getExternalStorageDirectory() + Flag.BaseCache);
        FileUtil.DeleteFile(dirFiles);*/
    }

    private List<CropListdata> setListData() {
        List<CropListdata> listdata = new ArrayList<>();
        listdata.add(new CropListdata(getResources().getDrawable(R.drawable.crop_auto_n), getResources().getDrawable(R.drawable.crop_auto_y), null, getResources().getString(R.string.free), true, false, false, false));
        listdata.add(new CropListdata(getResources().getDrawable(R.drawable.crop_origin_n), getResources().getDrawable(R.drawable.crop_origin_y), null, getResources().getString(R.string.original), false, false, false, false));
        listdata.add(new CropListdata(getResources().getDrawable(R.drawable.crop_11_n), getResources().getDrawable(R.drawable.crop_11_y), null, "1 ：1", false, false, false, false));
        listdata.add(new CropListdata(getResources().getDrawable(R.drawable.crop_45_n), getResources().getDrawable(R.drawable.crop_45_y), getResources().getDrawable(R.drawable.crop_54_y), "4 ：5", false, false, false, true));
        listdata.add(new CropListdata(getResources().getDrawable(R.drawable.crop_57_n), getResources().getDrawable(R.drawable.crop_57_y), getResources().getDrawable(R.drawable.crop_75_y), "5 ：7", false, false, false, true));
        listdata.add(new CropListdata(getResources().getDrawable(R.drawable.crop_23_n), getResources().getDrawable(R.drawable.crop_23_y), getResources().getDrawable(R.drawable.crop_32_y), "2 ：3", false, false, false, true));
        listdata.add(new CropListdata(getResources().getDrawable(R.drawable.crop_34_n), getResources().getDrawable(R.drawable.crop_34_y), getResources().getDrawable(R.drawable.crop_43_y), "3 ：4", false, false, false, true));
        listdata.add(new CropListdata(getResources().getDrawable(R.drawable.crop_916_n), getResources().getDrawable(R.drawable.crop_916_y), getResources().getDrawable(R.drawable.crop_169_y), "9 ：16", false, false, false, true));
        return listdata;
    }
}

