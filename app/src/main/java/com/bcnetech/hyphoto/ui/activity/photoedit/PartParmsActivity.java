package com.bcnetech.hyphoto.ui.activity.photoedit;

import android.animation.ValueAnimator;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnLayoutInflatedListener;
import com.app.hubert.guide.model.GuidePage;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.ImageDataSqlControl;
import com.bcnetech.hyphoto.databinding.PartParmsLayoutBinding;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.receiver.AddPicReceiver;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.ui.view.DoubleVerticalScaleScrollView;
import com.bcnetech.hyphoto.ui.view.ImageNewUtilsView;
import com.bcnetech.hyphoto.ui.view.PaintView;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.ui.view.ZoomableViewGroup;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.PopWindowUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.gpumanage.GPUImageFilterTools;


/**
 * 画笔
 * Created by wenbin on 16/10/24.
 */

public class PartParmsActivity extends BaseActivity {
    private final static String CURRENT_IMAGE_DATA = "currentPicDatas";
    private PartParmsLayoutBinding partParmsLayoutBinding;
    private DoubleVerticalScaleScrollView double_vertical_scroll;

    private ImageData imageData;
    private Rect dstRect;
    //private Rect bitmapRect = new Rect();;
    private GPUImageFilterGroup filterGroup, initFilterGroup;
    private GPUImageFilter filterLight, filterSat, srcFilterGroup;
    private GPUImageFilterTools.FilterAdjuster adjustLight, adjustSat;
    private PictureProcessingData pictureProcessingData;
    private SavePaintTask task;
    private Bitmap mCurrentBitmap, smallbit;
    private int currentPosition;//当前部分修改操作节点
    private boolean canClick;//判断是否能点击
    private int type;
    private boolean mFirstWindowFouse, activityFouse;
    private ValueAnimator transAnim;
    private PopupWindow mCurPopupWindow;
    private TextView tv_num;
    private int MoveDistance;//橡皮擦移动距离


    private PaintBottomData paintBottomData;

    private Bitmap orginBitmap;//原始图片（无滤镜）
    private String url;
    private int orientation;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Flag.PART_PARMS, imageData);
        outState.putSerializable(CURRENT_IMAGE_DATA, pictureProcessingData);
        outState.putInt(Flag.CURRENT_PART_POSITION, currentPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentPosition = savedInstanceState.getInt(Flag.CURRENT_PART_POSITION);
        imageData = (ImageData) savedInstanceState.getSerializable(Flag.PART_PARMS);
        pictureProcessingData = (PictureProcessingData) savedInstanceState.getSerializable(CURRENT_IMAGE_DATA);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        partParmsLayoutBinding = DataBindingUtil.setContentView(this, R.layout.part_parms_layout);
        // setContentView(R.layout.part_parms_layout);
        currentPosition = getIntent().getIntExtra(Flag.CURRENT_PART_POSITION, -1);
//        imageData = (ImageData) getIntent().getSerializableExtra(Flag.PART_PARMS);
        imageData= ImageNewUtilsView.imageparms;
        if (imageData == null) {
            finish();
            return;
        }
        url = BizImageMangage.getInstance().getUrlFromCurrentPostion(currentPosition, imageData, true, true);

        if (StringUtil.isBlank(url)) {
            ToastUtil.toast(getResources().getString(R.string.data_error));
            finish();
            return;
        }
        pictureProcessingData = new PictureProcessingData(BizImageMangage.PAINT_BRUSH, url);

        initView();
        onViewClick();
        initData();

    }

    @Override
    protected void onStart() {
        super.onStart();
        orientation = com.bcnetech.bcnetechlibrary.util.ImageUtil.readPictureDegree(pictureProcessingData.getImageUrl().substring(7));
        ImageUtil.EBizImageLoad(imageData.getSmallLocalUrl(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                orginBitmap = bitmap;

            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });


        if (mCurrentBitmap == null) {
            ImageUtil.EBizImageLoad(pictureProcessingData.getImageUrl(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    partParmsLayoutBinding.waitView.setVisibility(View.VISIBLE);
                    partParmsLayoutBinding.finalImg.setBackgroundColor(Color.BLACK);
                    partParmsLayoutBinding.finalImg.setVisibility(View.VISIBLE);
                    canClick = false;
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ToastUtil.toast(getResources().getString(R.string.loading_fail));
                    canClick = true;
                    partParmsLayoutBinding.waitView.setVisibility(View.GONE);
                    finish();

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    mCurrentBitmap = loadedImage;
                    if (orientation > 0) {
                        Matrix matrix = new Matrix();
                        matrix.setRotate(orientation);
                        // 重新绘制Bitmap
                        Bitmap bitmap = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), matrix, true);
                        mCurrentBitmap = bitmap;
                    }
                    if (activityFouse) {
                        autoGpuImageParmsLayout();
                        mFirstWindowFouse = false;
                    }

                    partParmsLayoutBinding.gpuimageviewTexture.setImage(mCurrentBitmap);
                    partParmsLayoutBinding.contentLayout.setListener(new ZoomableViewGroup.ImgUpData() {
                        @Override
                        public void imgUpdata(Matrix matrix) {
                            if (dstRect != null) {
                                RectF rectF = new RectF(dstRect.left, dstRect.top, dstRect.right, dstRect.bottom);
                                matrix.mapRect(rectF);


                                partParmsLayoutBinding.contentLayout.setRect(new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
                                partParmsLayoutBinding.paintview.setImgRect(new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
                            }

                            //  bitmapRect=content_layout.getRect();
                        }
                    });
                    if (dstRect != null) {
                        partParmsLayoutBinding.contentLayout.setRect(dstRect);
                    }

                    partParmsLayoutBinding.contentLayout.setBitWidth(loadedImage.getWidth());
                    partParmsLayoutBinding.contentLayout.setBitHight(loadedImage.getHeight());


                    partParmsLayoutBinding.paintview.setCanveLayoutParms(new PaintView.OnDrawListener() {
                        @Override
                        public void beforDrow(Bitmap bitmap) {
                        }

                        @Override
                        public void touchEven(MotionEvent event) {
                            if (mCurPopupWindow != null && mCurPopupWindow.isShowing()) {
                                mCurPopupWindow.dismiss();
                                mCurPopupWindow = null;
                            }

                            partParmsLayoutBinding.contentLayout.touchEvent(event);

                        }

                        @Override
                        public void moveDrow(Bitmap bit) {

                            if (type == BizImageMangage.PART_PAINT_SATURATION) {
                                partParmsLayoutBinding.gpuimageviewTexture.setPartImage2(bit);
                            } else {
                                partParmsLayoutBinding.gpuimageviewTexture.setPartImage(bit);
                            }

                        }

                        @Override
                        public void onStartZoomMove(float pointOneX, float pointOneY, float pointTwoX, float pointTwoY) {
                        }

                        @Override
                        public void onZoomMove(float pointOneX, float pointOneY, float pointTwoX, float pointTwoY) {
                        }

                        @Override
                        public void onEndZoomMove() {
                        }
                    }, loadedImage.getWidth(), loadedImage.getHeight());

                    partParmsLayoutBinding.gpuimageviewTexture.setPartImage(partParmsLayoutBinding.paintview.getBaseBitmap());
                    partParmsLayoutBinding.gpuimageviewTexture.setPartImage2(partParmsLayoutBinding.paintview.getBaseBitmap2());
                    partParmsLayoutBinding.gpuimageviewTexture.setFilter(filterGroup);
                    //orginBitmap=gpuImage.getBitmapWithFilterApplied();
                    canClick = true;
                    partParmsLayoutBinding.finalImg.setBackgroundColor(Color.TRANSPARENT);
                    partParmsLayoutBinding.finalImg.setVisibility(View.GONE);
                    partParmsLayoutBinding.waitView.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    ToastUtil.toast(getResources().getString(R.string.loading_fail));
                    canClick = true;
                    partParmsLayoutBinding.waitView.setVisibility(View.GONE);
                    finish();
                }
            });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        partParmsLayoutBinding.gpuimageviewTexture.onResume();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        activityFouse = hasFocus;
        if (activityFouse && mFirstWindowFouse) {
            if (mCurrentBitmap != null) {
                autoGpuImageParmsLayout();
                mFirstWindowFouse = false;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PopWindowUtil.disPartPaintPop();
        if (partParmsLayoutBinding.gpuimageviewTexture != null) {
            partParmsLayoutBinding.gpuimageviewTexture.getGPUImage().deleteImage();
        }
        if (mCurrentBitmap != null) {
            mCurrentBitmap.recycle();
        }
        filterGroup = null;
        initFilterGroup = null;

        filterLight = null;
        filterSat = null;
        srcFilterGroup = null;
        adjustLight = null;
        adjustSat = null;
        if (task != null) {
            task.cancel(true);
        }
        task = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // KeyEvent.KEYCODE_BACK代表返回操作.
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 处理返回操作.
        }
        return true;
    }

    private void autoGpuImageParmsLayout() {
        double withe = partParmsLayoutBinding.contentLayout.getMeasuredWidth();
        double hight = partParmsLayoutBinding.contentLayout.getMeasuredHeight();
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

        partParmsLayoutBinding.gpuimageviewTexture.setLayoutParams(new RelativeLayout.LayoutParams((int) w, (int) h));
        partParmsLayoutBinding.contentLayout.getPicWH((int) w, (int) h);
        partParmsLayoutBinding.gpuimageviewTexture.setTranslationX((int) strx);
        partParmsLayoutBinding.gpuimageviewTexture.setTranslationY((int) stry);

        dstRect = new Rect((int) strx, (int) stry, (int) (w + strx), (int) (h + stry));
        partParmsLayoutBinding.paintview.setImgRect(dstRect);
    }

    @Override
    protected void initView() {
        partParmsLayoutBinding.paintview.setType(PaintView.LIGHT);

        partParmsLayoutBinding.paintview.setPaintType(false);
        type = BizImageMangage.PART_PAINT_EXPOSURE;
        partParmsLayoutBinding.paintview.setPaintColor(6.0f);

        partParmsLayoutBinding.contentLayout.setShow(true);

        partParmsLayoutBinding.paintTitle.setTitleText(getResources().getString(R.string.white_black_pen) + " 6.0");
    }

    List<PictureProcessingData> imagetools;

    private void initImgData() {
        partParmsLayoutBinding.paintTitle.setType(TitleView.IMAGE_PARMS);
        List list = new ArrayList();
        List list2 = new ArrayList();
        List list3 = new ArrayList();
        filterSat = BizImageMangage.getInstance().getGPUFilterforType(this, BizImageMangage.PART_PAINT_SATURATION);
        adjustSat = new GPUImageFilterTools.FilterAdjuster(filterSat);
        adjustSat.adjust(GPUImageFilterTools.UN_SHOW);
        list.add(filterSat);
        list3.add(filterSat);
        filterLight = BizImageMangage.getInstance().getGPUFilterforType(this, BizImageMangage.PART_PAINT_EXPOSURE);
        adjustLight = new GPUImageFilterTools.FilterAdjuster(filterLight);
        adjustLight.adjust(GPUImageFilterTools.UN_SHOW);
        list.add(filterLight);
        list3.add(filterLight);
        initFilterGroup = new GPUImageFilterGroup(list3);
        if (imageData.getImageTools() != null && imageData.getImageTools().size() != 0) {
            imagetools = imageData.getImageTools();

        } else if (imageData.getPresetParms() != null && imageData.getPresetParms().getParmlists() != null && imageData.getPresetParms().getParmlists().size() != 0) {
            imagetools = imageData.getPresetParms().getParmlists();
        }
        for (int i = 0; imagetools != null && i < imagetools.size(); i++) {
            GPUImageFilter mFilter;
            GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
            mFilter = BizImageMangage.getInstance().getGPUFilterforType(this, imagetools.get(i).getType());
            list2.add(mFilter);
            list.add(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
            if (imagetools.get(i).getType() == BizImageMangage.WHITE_BALANCE) {
                mFilterAdjuster.adjust(imagetools.get(i).getNum(), imagetools.get(i).getTintNum());
            } else {
                mFilterAdjuster.adjust(imagetools.get(i).getNum());
            }
            // mFilterAdjuster.adjust(imageData.getImageTools().get(i).getNum());
        }
        if (list2.size() != 0) {
            srcFilterGroup = new GPUImageFilterGroup(list2);
        } else {
            srcFilterGroup = new GPUImageFilter();
        }
        for (int i = list.size() % 2; i > 0; i--) {
            list.add(BizImageMangage.getInstance().getGPUFilterforType(this, BizImageMangage.PART_PAINT_SATURATION));
        }

        filterSat = BizImageMangage.getInstance().getGPUFilterforType(this, BizImageMangage.PART_PAINT_SATURATION);
        adjustSat = new GPUImageFilterTools.FilterAdjuster(filterSat);
        adjustSat.adjust(GPUImageFilterTools.SRC);
        list.add(filterSat);
        filterLight = BizImageMangage.getInstance().getGPUFilterforType(this, BizImageMangage.PART_PAINT_EXPOSURE);
        adjustLight = new GPUImageFilterTools.FilterAdjuster(filterLight);
        adjustLight.adjust(GPUImageFilterTools.SRC);
        list.add(filterLight);
        filterGroup = new GPUImageFilterGroup(list);
    }

    @Override
    protected void initData() {
        activityFouse = false;
        mFirstWindowFouse = true;
        partParmsLayoutBinding.finalImg.setVisibility(View.GONE);
        paintBottomData = new PaintBottomData();
        initImgData();
        if (url.contains(".png")) {
            partParmsLayoutBinding.contentLayout.setBackground(getResources().getDrawable(R.drawable.bgbitmap));
        }


    }


    @Override
    protected void onViewClick() {
        partParmsLayoutBinding.eye.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (null != mCurPopupWindow) {
                    mCurPopupWindow.dismiss();
                    mCurPopupWindow = null;
                }
                // bifacialView.onTouchEvent(event);

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        //  partParmsLayoutBinding.paintTitle.setTitleText(getResources().getString(R.string.view_diff));
                        // text_fix.setAlpha(CLICK_ALPHA);
                        partParmsLayoutBinding.gpuimageviewTexture.setFilter(srcFilterGroup);
                        partParmsLayoutBinding.gpuimageviewTexture.requestRender();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // partParmsLayoutBinding.paintTitle.setTitleText("");
                        // text_fix.setAlpha(UNCLICK_ALPHA);
                        partParmsLayoutBinding.gpuimageviewTexture.setFilter(filterGroup);
                        partParmsLayoutBinding.gpuimageviewTexture.requestRender();
                        break;
                }
                return false;
            }
        });

        partParmsLayoutBinding.eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paintBottomData.isEraserMode = !paintBottomData.isEraserMode;
                partParmsLayoutBinding.eraser.setImageDrawable(paintBottomData.isEraserMode ? getResources().getDrawable(R.drawable.eraser_down) : getResources().getDrawable(R.drawable.eraser_up));
                startAnim(true);
                if (paintBottomData.isEraserMode) {
                    // double_vertical_scroll.scrollToScale(0);
                    if (tv_num != null)
                        tv_num.setText(getResources().getString(R.string.eraser));
                    if (paintBottomData.CurrentTYPE == PaintBottomData.TYPE_COLOR) {
                        partParmsLayoutBinding.paintTitle.setTitleText(getResources().getString(R.string.colors_eraser));
                    }
                    if (paintBottomData.CurrentTYPE == PaintBottomData.TYPE_EVON) {
                        partParmsLayoutBinding.paintTitle.setTitleText(getResources().getString(R.string.wb_eraser));
                    }
                    partParmsLayoutBinding.paintview.setPaintColor(0.0f);
                } else {
                    //再次点击取消橡皮擦模式
                    if (paintBottomData.CurrentTYPE == PaintBottomData.TYPE_COLOR) {
                        partParmsLayoutBinding.paintTitle.setTitleText(getResources().getString(R.string.colors_pen) + "  " + -(float) paintBottomData.getCurrentScale() / 10f);
                    } else {
                        partParmsLayoutBinding.paintTitle.setTitleText(getResources().getString(R.string.white_black_pen) + "  " + -(float) paintBottomData.getCurrentScale() / 10f);
                    }
                    partParmsLayoutBinding.paintview.setPaintColor(-(float) paintBottomData.getCurrentScale() / 100f);
                    if (tv_num != null)
                        tv_num.setText("" + -(float) paintBottomData.getCurrentScale() / 10f);
                }
            }
        });


        partParmsLayoutBinding.btncolouron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partParmsLayoutBinding.eraser.setImageDrawable(getResources().getDrawable(R.drawable.eraser_up));
                //再次点击取消橡皮擦模式
                if (paintBottomData.CurrentTYPE == PaintBottomData.TYPE_COLOR) {
                    partParmsLayoutBinding.paintTitle.setTitleText(getResources().getString(R.string.colors_pen) + "  " + -(float) paintBottomData.getCurrentScale() / 10f);
                } else {
                    partParmsLayoutBinding.paintTitle.setTitleText(getResources().getString(R.string.white_black_pen) + "  " + -(float) paintBottomData.getCurrentScale() / 10f);
                }
                partParmsLayoutBinding.btnevon.setImageDrawable(getResources().getDrawable(R.drawable.new_btnevon));
                partParmsLayoutBinding.btncolouron.setImageDrawable(getResources().getDrawable(R.drawable.new_btncolouron_down));
                startAnim(false);
                paintBottomData.isEraserMode = false;
                if (null != mCurPopupWindow) {
                    mCurPopupWindow.dismiss();
                    mCurPopupWindow = null;
                    if (paintBottomData.CurrentTYPE != PaintBottomData.TYPE_COLOR) {
                        paintBottomData.CurrentTYPE = PaintBottomData.TYPE_COLOR;
                        mCurPopupWindow = showTipPopupWindow(partParmsLayoutBinding.btncolouron);
                    }
                } else {
                    paintBottomData.CurrentTYPE = PaintBottomData.TYPE_COLOR;
                    mCurPopupWindow = showTipPopupWindow(partParmsLayoutBinding.btncolouron);
                }
                partParmsLayoutBinding.paintview.setType(PaintView.SAT);
                partParmsLayoutBinding.paintview.setPaintType(false);
                type = BizImageMangage.PART_PAINT_SATURATION;


              /*  if (null == mCurPopupWindow) {
                    mCurPopupWindow = showTipPopupWindow(partParmsLayoutBinding.btncolouron);
                }*/
            }
        });
        partParmsLayoutBinding.btnevon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partParmsLayoutBinding.eraser.setImageDrawable(getResources().getDrawable(R.drawable.eraser_up));
                //再次点击取消橡皮擦模式
                if (paintBottomData.CurrentTYPE == PaintBottomData.TYPE_COLOR) {
                    partParmsLayoutBinding.paintTitle.setTitleText(getResources().getString(R.string.colors_pen) + "  " + -(float) paintBottomData.getCurrentScale() / 10f);
                } else {
                    partParmsLayoutBinding.paintTitle.setTitleText(getResources().getString(R.string.white_black_pen) + "  " + -(float) paintBottomData.getCurrentScale() / 10f);
                }
                partParmsLayoutBinding.btnevon.setImageDrawable(getResources().getDrawable(R.drawable.new_btnevon_down));
                partParmsLayoutBinding.btncolouron.setImageDrawable(getResources().getDrawable(R.drawable.new_btncolouron));
                startAnim(false);
                paintBottomData.isEraserMode = false;
                if (null != mCurPopupWindow) {
                    mCurPopupWindow.dismiss();
                    mCurPopupWindow = null;
                    if (paintBottomData.CurrentTYPE != PaintBottomData.TYPE_EVON) {
                        paintBottomData.CurrentTYPE = PaintBottomData.TYPE_EVON;
                        mCurPopupWindow = showTipPopupWindow(partParmsLayoutBinding.btnevon);
                    }
                } else {
                    paintBottomData.CurrentTYPE = PaintBottomData.TYPE_EVON;
                    mCurPopupWindow = showTipPopupWindow(partParmsLayoutBinding.btnevon);
                }

                partParmsLayoutBinding.paintview.setType(PaintView.LIGHT);
                partParmsLayoutBinding.paintview.setPaintType(false);
                type = BizImageMangage.PART_PAINT_EXPOSURE;

               /* if (null == mCurPopupWindow) {
                    mCurPopupWindow = showTipPopupWindow(partParmsLayoutBinding.btnevon);
                }*/
            }
        });

        partParmsLayoutBinding.paintTitle.setLeftListener(new View.OnClickListener() {
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

        partParmsLayoutBinding.paintTitle.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick) {
                    return;
                }
                showTransformDialog();
                try {
                    smallbit = partParmsLayoutBinding.gpuimageviewTexture.capture();//最终小图
                    partParmsLayoutBinding.finalImg.setVisibility(View.VISIBLE);
                    partParmsLayoutBinding.finalImg.setImageBitmap(smallbit);
                } catch (Exception e) {
                    Bitmap finalbt = partParmsLayoutBinding.gpuimageviewTexture.getGPUImage().getBitmapWithFilterApplied(partParmsLayoutBinding.paintview.getBaseBitmap(), partParmsLayoutBinding.paintview.getBaseBitmap2());
                    partParmsLayoutBinding.gpuimageviewTexture.setImage(finalbt);
                    partParmsLayoutBinding.gpuimageviewTexture.setFilter(filterGroup);
                    Bitmap mbitmap = partParmsLayoutBinding.gpuimageviewTexture.getBitmapWithFilterApplied();
                    partParmsLayoutBinding.finalImg.setVisibility(View.VISIBLE);
                    partParmsLayoutBinding.finalImg.setImageBitmap(mbitmap);
                }


                adjustLight.adjust(GPUImageFilterTools.SRC);
                adjustSat.adjust(GPUImageFilterTools.SRC);
                partParmsLayoutBinding.gpuimageviewTexture.setFilter(initFilterGroup);
                canClick = false;
                //修改图片
                Bitmap finalbit = null;
                try {
                    finalbit = new BitmapWithFilterCallable(0).call();
                } catch (Exception e) {
                    finalbit = partParmsLayoutBinding.gpuimageviewTexture.getGPUImage().getBitmapWithFilterApplied(partParmsLayoutBinding.paintview.getBaseBitmap(), partParmsLayoutBinding.paintview.getBaseBitmap2());
                }
                partParmsLayoutBinding.gpuimageviewTexture.setFilter(srcFilterGroup);
                partParmsLayoutBinding.gpuimageviewTexture.setImage(finalbit);
                Bitmap finalbitwithFilterApplied = null;
                try {
                    finalbitwithFilterApplied = new BitmapWithFilterCallable(1).call();
                } catch (Exception e) {
                    finalbitwithFilterApplied = partParmsLayoutBinding.gpuimageviewTexture.getBitmapWithFilterApplied();
                }
                task = new SavePaintTask(finalbit, finalbitwithFilterApplied);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }


    private void startAnim(boolean clickEraser) {
        MoveDistance = (partParmsLayoutBinding.paintContent.getWidth() - ContentUtil.dip2px(PartParmsActivity.this, 52)) / 5;
        MoveDistance = paintBottomData.CurrentTYPE == PaintBottomData.TYPE_EVON ? -Math.abs(MoveDistance) : Math.abs(MoveDistance);

        //点击橡皮擦
        if (clickEraser) {
            //点击橡皮擦向该方向移动,再次点击橡皮擦向反方向移动
            transAnim = initAnim(partParmsLayoutBinding.eraser, MoveDistance, paintBottomData.isEraserMode);
            transAnim.start();
        } else {
            //点击画笔
            if (paintBottomData.isEraserMode) {
                transAnim = initAnim(partParmsLayoutBinding.eraser, MoveDistance, false);
                transAnim.start();
            } else {
                //未点击橡皮擦，则不移动
                MoveDistance = 0;
            }
        }

    }

    private ValueAnimator initAnim(final View view, final float x, boolean b) {
        ValueAnimator inAnimation;
        if (b) {
            inAnimation = ValueAnimator.ofFloat(0, 1);
        } else {
            inAnimation = ValueAnimator.ofFloat(1, 0);
        }
        inAnimation.setDuration(150);
        inAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setTranslationX(x * f);
            }
        });
        return inAnimation;
    }


    //保存最终图片和未加滤镜的修改图片
    private class SavePaintTask extends AsyncTask<Void, Void, ArrayList> {
        private Bitmap bitmap;//未加滤镜图片
        private Bitmap finalbitmap;//展示用的最终图片

        SavePaintTask(Bitmap bitmap, Bitmap finalbitmap) {
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
//                if (imageData.getSmallLocalUrl().endsWith(".png")){
//                    imageData.setMatting(true);
//                }else{
//                    imageData.setMatting(false);
//                }
                path1 = FileUtil.saveBitmap(bitmap, time - 1 + "", imageData.getSmallLocalUrl().contains(".png"));
                path2 = FileUtil.saveBitmaptoNative(finalbitmap, time + 1 + "", imageData.getSmallLocalUrl().contains(".png"));

                path3 = FileUtil.saveBitmaptoShareNative(imageData.getSmallLocalUrl().contains(".png") ? BitmapUtils.compressPNG(finalbitmap) : BitmapUtils.compress(finalbitmap), time + 2 + "", imageData.getSmallLocalUrl().contains(".png"));


                if (StringUtil.isBlank(path1) || StringUtil.isBlank(path2)) {
                    return null;
                }
                path1 = "file://" + path1;
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
                    if (imageData.getImageParts() == null) {
                        processlist = new ArrayList<PictureProcessingData>();
                    } else {
                        processlist = imageData.getImageParts();
                    }
                    processlist.add(new PictureProcessingData(BizImageMangage.PAINT_BRUSH, processurl, smallUrl, ""));

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

                    final ImageDataSqlControl imageDataSqlControl = new ImageDataSqlControl(PartParmsActivity.this);
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
                            PartParmsActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(imageData.getSmallLocalUrl())));
                            AddPicReceiver.notifyModifyUsername(PartParmsActivity.this, "refresh");
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

    public PopupWindow showTipPopupWindow(final View anchorView) {

        final View contentView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.double_vertical_scroll_view_layout, null);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                contentView.getMeasuredWidth(), contentView.getMeasuredHeight(), false);

        tv_num = (TextView) contentView.findViewById(R.id.tv_num);
        double_vertical_scroll = (DoubleVerticalScaleScrollView) contentView.findViewById(R.id.double_vertical_scroll);
        double_vertical_scroll.setOnScrollListener(new DoubleVerticalScaleScrollView.OnScrollListener() {
            @Override
            public void onScaleScroll(int scale) {
                if (scale == 0) {
                    //tv_num.setText(getResources().getString(R.string.eraser));
                    tv_num.setText("" + -(float) scale / 10f);
                } else {
                    if (paintBottomData.isEraserMode) {
                        startAnim(false);
                        paintBottomData.isEraserMode = false;
                        partParmsLayoutBinding.eraser.setImageDrawable(getResources().getDrawable(R.drawable.eraser_up));
                    }
                    tv_num.setText("" + -(float) scale / 10f);
                }
                paintBottomData.setCurrentScale(scale);
                partParmsLayoutBinding.paintview.setPaintColor(-(float) paintBottomData.getCurrentScale() / 100f);
                if (paintBottomData.CurrentTYPE == PaintBottomData.TYPE_COLOR) {
                    partParmsLayoutBinding.paintTitle.setTitleText(getResources().getString(R.string.colors_pen) + "  " + tv_num.getText());
                } else {
                    partParmsLayoutBinding.paintTitle.setTitleText(getResources().getString(R.string.white_black_pen) + "  " + tv_num.getText());

                }

            }
        });
        double_vertical_scroll.setDefault_inde(paintBottomData.getCurrentScale());
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!paintBottomData.isEraserMode)
                    partParmsLayoutBinding.paintview.setPaintColor(-(float) paintBottomData.getCurrentScale() / 100f);
            }
        });
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });


        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 自动调整箭头的位置
                //autoAdjustArrowPos(popupWindow, contentView, anchorView);
                contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        // setOutsideTouchable设置生效的前提是setTouchable(true)和setFocusable(false)
        popupWindow.setOutsideTouchable(false);

        // 设置为true之后，PopupWindow内容区域 才可以响应点击事件
        popupWindow.setTouchable(true);

        // true时，点击返回键先消失 PopupWindow
        // 但是设置为true时setOutsideTouchable，setTouchable方法就失效了（点击外部不消失，内容区域也不响应事件）
        // false时PopupWindow不处理返回键

        popupWindow.setFocusable(false);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;   // 这里面拦截不到返回键
            }
        });
        // 如果希望showAsDropDown方法能够在下面空间不足时自动在anchorView的上面弹出
        // 必须在创建PopupWindow的时候指定高度，不能用wrap_content
        int XOff = contentView.getMeasuredWidth() / 2 - ContentUtil.dip2px(PartParmsActivity.this, 12);
        popupWindow.showAsDropDown(anchorView, -XOff, 0);
        return popupWindow;
    }


    private static class PaintBottomData {
        public static int TYPE_COLOR = 1001;//右
        public static int TYPE_EVON = 1002;//左
        private int btncolouronScale = -60;
        private int btnevonScale = -60;
        boolean isEraserMode = false;//现在是否为橡皮擦模式
        private int CurrentTYPE = TYPE_EVON;

        private int getCurrentScale() {
            return CurrentTYPE == TYPE_COLOR ? btncolouronScale : btnevonScale;
        }

        private void setCurrentScale(int scale) {
            if (CurrentTYPE == TYPE_COLOR) {
                btncolouronScale = scale;
            } else {
                btnevonScale = scale;
            }
        }
    }

    private class BitmapWithFilterCallable implements Callable<Bitmap> {
        //0--画笔 1--参数
        private int mType;

        public BitmapWithFilterCallable(int type) {
            mType = type;
        }

        @Override
        public Bitmap call() throws Exception {
            if (mType == 0) {
                return partParmsLayoutBinding.gpuimageviewTexture.getGPUImage().getBitmapWithFilterApplied(partParmsLayoutBinding.paintview.getBaseBitmap(), partParmsLayoutBinding.paintview.getBaseBitmap2());

            } else {
                return partParmsLayoutBinding.gpuimageviewTexture.getBitmapWithFilterApplied();

            }
        }
    }


    @Override
    protected void initGuide() {
        super.initGuide();
        NewbieGuide.with(this)
                .setLabel("pagePhotoEditPart")//设置引导层标示区分不同引导层，必传！否则报错
                .alwaysShow(false)//是否每次都显示引导层，默认false，只显示一次
                .addGuidePage(//添加一页引导页
                        GuidePage.newInstance()//创建一个实例
//                                .addHighLight(panelContent_image, HighLight.Shape.ROUND_RECTANGLE,100,0,null)
                                .setLayoutRes(R.layout.guide_photoedit_select)//设置引导页布局
                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {

                                    @Override
                                    public void onLayoutInflated(View view, Controller controller) {
                                        ImageView photoedit_part = (ImageView) view.findViewById(R.id.photoedit_part);
                                        photoedit_part.setVisibility(View.VISIBLE);
                                        //引导页布局填充后回调，用于初始化
                                       /* TextView tv = view.findViewById(R.id.textView2);
                                        tv.setText("我是动态设置的文本");*/
                                    }
                                })
                                .setEnterAnimation(enterAnimation)//进入动画
                                .setExitAnimation(exitAnimation)//退出动画
                ).show();//显示引导层(至少需要一页引导页才能显示)
    }


}
