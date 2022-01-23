package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by yhf on 2017/4/20.
 */

public class MattingPicView extends BaseRelativeLayout {

    private MattingPaintView paintView;
    private RelativeLayout rl_main;
    private ScaleImageView matting_img;
    private MaskView mask_view;
    private BizImageMangage mangage;

    private int realwidth;
    private int realheight;

    private MattingWaitListener listener;
    //原图
    private Bitmap mCurrentBitmap;


    //修改后的图片
    private Bitmap changBitmap;

    //标记背景
    public static final int TYPE_BACKGROUND = 1;
    //标记主体
    public static final int TYPE_MAIN = 2;
    //返回
    public static final int TYPE_UNDO = 3;
    //查看
    public static final int TYPE_EYE = 4;


    private DGProgressDialog dialog;
    public int type;

    private Handler handler;

    //原图片
    public static final int BACKGROUD_TYPE_INIT = 1;
    //抠图
    public static final int BACKGROUD_TYPE_MATTING = 2;
    //画图
    public static final int BACKGROUD_TYPE_DRAW = 3;


    //
    public static final int BACKGROUD_TYPE_SEEDRAW = 5;

    private int width, height;
    private int orientation;

    public MattingPicView(Context context) {
        super(context);
    }

    public MattingPicView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MattingPicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initData() {
        super.initData();
        mangage = BizImageMangage.getInstance();
        handler = new Handler();
        matting_img.setCanClick(false);
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.matting_pic_layout, this);
        paintView = (MattingPaintView) findViewById(R.id.matting_paint);
        rl_main = (RelativeLayout) findViewById(R.id.rl_main);
        matting_img = (ScaleImageView) findViewById(R.id.matting_imageview);
        mask_view = (MaskView) findViewById(R.id.mask_view);
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();

        paintView.setListener(new MattingPaintView.OnDrawListener() {
            @Override
            public void beforDrow(final Bitmap bit, final int size) {

                if (handler != null) {
                    showDialog();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            int[] srcPix = new int[width * height];
                            mCurrentBitmap.getPixels(srcPix, 0, width, 0, 0, width, height);

                            final Bitmap bitmap = mangage.lazySnapping(srcPix, bit, width, height);

                            final Bitmap finishBitmap=BitmapUtils.compressPNGTwo(getFinishbitmap());

                            boolean isAlpha = false;
                            int[] pixels = new int[finishBitmap.getWidth() * finishBitmap.getHeight()];
                            finishBitmap.getPixels(pixels, 0, finishBitmap.getWidth(), 0, 0, finishBitmap.getWidth(), finishBitmap.getHeight());

                            for (int i = 0; i < pixels.length; i++) {
                                int color = pixels[i];
                                int a = Color.alpha(color);
                                if (a == 0) {
                                    isAlpha = true;
                                    break;
                                }
                            }
                            final boolean finalIsAlpha = isAlpha;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dissmissDialog(size,finishBitmap, finalIsAlpha);
                                    mask_view.setMaskBit(bitmap);

                                }
                            });
                        }
                    }).start();

                }


            }

            @Override
            public void touchEven(MotionEvent event) {
                matting_img.onTouchEvent(event);
            }
        });
    }

    public void setMattingPaintListener(MattingPaintView.OnDrawListener l) {
        paintView.setListener(l);

    }

    /**
     * 初始化图片
     */
    public void initImageDefData(String smallLocalUrl) {
        ImageUtil.EBizImageLoad(smallLocalUrl, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                orientation = com.bcnetech.bcnetechlibrary.util.ImageUtil.readPictureDegree(s.substring(7));
                if (orientation > 0) {
                    Matrix matrix = new Matrix();
                    matrix.setRotate(orientation);
                    // 重新绘制Bitmap
                    Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    bitmap = newBitmap;
                }

                if (bitmap.getWidth() > BitmapUtils.MAXLENGTH  || bitmap.getHeight() > BitmapUtils.MAXLENGTH) {
                    bitmap = ImageUtil.newDecodeSampledBitmapFromFile(s.substring(7), bitmap, BitmapUtils.MAXLENGTH, BitmapUtils.MAXLENGTH);
                }
                mCurrentBitmap = bitmap;
                width = mCurrentBitmap.getWidth();
                height = mCurrentBitmap.getHeight();

                changBitmap = Bitmap.createBitmap(bitmap);

                matting_img.setBitmap(mCurrentBitmap, null);
                // matting_img.setBitmap(changBitmap, null);
                matting_img.setListener(new ScaleImageView.ImgUpData() {
                    @Override
                    public void imgUpdata(Rect canveRect) {
                        realwidth = canveRect.width();
                        realheight = canveRect.height();
                        paintView.setImgRect(canveRect);
                        mask_view.setCanveRect(canveRect);
                        setPaintType(TYPE_BACKGROUND);

                        paintView.setCanveLayoutParms(width, height);

                    }
                });

                matting_img.invalidate();
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    /**
     * 设置画笔状态
     *
     * @param type
     */
    public void setPaintType(int type) {
        this.type = type;
        switch (type) {
            case TYPE_BACKGROUND:
                setPaintMatting();
                paintView.setCanDraw(true);
                paintView.setVisibility(VISIBLE);
                paintView.setPaintType(TYPE_BACKGROUND);
                break;
            case TYPE_MAIN:
                setPaintMatting();
                paintView.setCanDraw(true);
                paintView.setVisibility(VISIBLE);
                paintView.setPaintType(TYPE_MAIN);
                break;
            case TYPE_UNDO:
//                setPaintMatting();
                paintView.setCanDraw(false);
                paintView.setVisibility(VISIBLE);
//                paintView.setPaintType(TYPE_MAIN);
                break;
            case TYPE_EYE:
//                setPaintMatting();
                paintView.setCanDraw(false);
                paintView.setVisibility(INVISIBLE);
//                paintView.setPaintType(TYPE_MAIN);
                break;

        }
    }


    /**
     * 设置背景
     *
     * @param type
     */
    public void setBackground(int type) {
        switch (type) {
            case BACKGROUD_TYPE_INIT:

                matting_img.setBitmap(mCurrentBitmap);
                paintView.setVisibility(VISIBLE);
                mask_view.setVisibility(VISIBLE);
                invalidate();
                break;
            case BACKGROUD_TYPE_MATTING:
                matting_img.setBitmap(getShowFinishbitmap());
                paintView.setVisibility(INVISIBLE);
                mask_view.setVisibility(INVISIBLE);
                invalidate();
                break;
            case BACKGROUD_TYPE_DRAW:
                paintView.setVisibility(VISIBLE);
                mask_view.setVisibility(VISIBLE);
                invalidate();
                break;
            case BACKGROUD_TYPE_SEEDRAW:
                matting_img.setBitmap(mCurrentBitmap);
                paintView.setVisibility(VISIBLE);
                mask_view.setVisibility(VISIBLE);
                invalidate();
                break;
        }
    }

    public Bitmap getFinishbitmap() {
        if (null == mangage.getSrcPixBit()) {
            return mCurrentBitmap;
        } else {
            Bitmap newMask = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            newMask.setPixels(mangage.getSrcPixBit(), 0, width, 0, 0, width, height);


            return newMask;

        }
    }

    public Bitmap getShowFinishbitmap() {
        if (null == mangage.getSrcPixBit()) {
            return mCurrentBitmap;
        } else {
            Bitmap newMask = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            newMask.setPixels(mangage.getSrcPixBit(), 0, width, 0, 0, width, height);

            boolean isAlpha = true;
            int[] pixels = new int[newMask.getWidth() * newMask.getHeight()];
            newMask.getPixels(pixels, 0, newMask.getWidth(), 0, 0, newMask.getWidth(), newMask.getHeight());

            for (int i = 0; i < pixels.length; i++) {
                int color = pixels[i];
                int a = Color.alpha(color);
                if (a > 0) {
                    isAlpha = false;
                    break;
                }
            }
            if (!isAlpha) {
                return newMask;
            }
            return mCurrentBitmap;
        }
    }

    /**
     * 画笔修复
     */
    private void setPaintMatting() {
        //paintView.bringToFront();
        matting_img.setListener(new ScaleImageView.ImgUpData() {
            @Override
            public void imgUpdata(Rect canveRect) {
                mask_view.setCanveRect(canveRect);
                paintView.setImgRect(canveRect);
            }
        });

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        matting_img.setLayoutParams(params);
        paintView.setLayoutParams(params);
        // realheight=params.height;
        //realwidth=params.width;


    }

    /**
     * 获取抠图后的图片
     *
     * @return
     */
    public Bitmap getChangBitmap() {
        return changBitmap;
    }

    /**
     * 获取原图
     *
     * @return
     */
    public Bitmap getmCurrentBitmap() {
        return mCurrentBitmap;
    }

    /**
     * 撤销
     */
    public void revoke() {
        paintView.undo();
    }


    private void showDialog() {
        if (listener != null) {
            listener.showMattingDialog();
        }
    }

    private void dissmissDialog(int size,Bitmap finishBitmap,boolean isAlpha) {
        if (listener != null) {
            listener.dismissMattingDialog(size,finishBitmap,isAlpha);
        }
    }


    public MattingWaitListener getListener() {
        return listener;
    }

    public void setListener(MattingWaitListener listener) {
        this.listener = listener;
    }

    public interface MattingWaitListener {
        void showMattingDialog();

        void dismissMattingDialog(int size,Bitmap finishBitmap,boolean isAlpha);
    }
}
