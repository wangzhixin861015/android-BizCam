package com.bcnetech.hyphoto.ui.activity.photoedit;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnLayoutInflatedListener;
import com.app.hubert.guide.model.GuidePage;
import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog3;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.ImageParmsData;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.listener.BizGestureListener;
import com.bcnetech.hyphoto.presenter.ImageParmsNewPresenter;
import com.bcnetech.hyphoto.presenter.iview.IImageParmsNewView;
import com.bcnetech.hyphoto.ui.activity.BaseMvpActivity;
import com.bcnetech.hyphoto.ui.view.ImageParmsCircleView;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.ui.view.WaitProgressBarView;
import com.bcnetech.hyphoto.ui.view.ZoomableViewGroup;
import com.bcnetech.hyphoto.ui.view.verticalscrollview.VerticalScaleNewView;
import com.bcnetech.hyphoto.ui.view.viewPagerIndicator.view.indicator.FixedIndicatorView;
import com.bcnetech.hyphoto.ui.view.viewPagerIndicator.view.indicator.IndicatorViewPager;
import com.bcnetech.hyphoto.ui.view.viewPagerIndicator.view.viewpager.SViewPager;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.gputexture.GPUImageTexture;
import jp.co.cyberagent.android.gpuimage.gputexture.GPUImageViewTexture;

import static android.support.v4.view.PagerAdapter.POSITION_NONE;
import static android.view.View.VISIBLE;


/**
 * 图片参数调整
 * Created by yhf on 17/10/25.
 */
public class ImageParmsNewActivity extends BaseMvpActivity<IImageParmsNewView, ImageParmsNewPresenter> implements IImageParmsNewView {

    private final static float CLICK_ALPHA = 0.4f;
    private final static float UNCLICK_ALPHA = 1f;

    private GPUImageViewTexture gpuimage;
    private GPUImage tempgpuimage;

    private WaitProgressBarView wait_view;
    private TitleView parms_title;
    private ImageView final_img;
    private Bitmap savebit;

    private boolean canClick;//判断是否能点击
    private BizGestureListener bizGestureListener;

    private SViewPager sViewPager;
    private FixedIndicatorView indicator;
    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;
    private int select_now = 0;
    private RelativeLayout ll_light;

    private LinearLayout ll_balance;

    private LinearLayout ll_temperature;
    private TextView tv_temperature;
    private ImageView iv_temperature;

    private LinearLayout ll_tint;
    private TextView tv_tint;
    private ImageView iv_tint;

    private LinearLayout ll_auto;
    private ImageView iv_auto;

    private LinearLayout ll_straw;
    private ImageView iv_straw;
    private ImageParmsCircleView imageParmsCircleView;
    private ImageView gray_level;

    private int select_type = 0;
    private Rect dstRect;

    private ZoomableViewGroup zoomableViewGroup;
    private List<ImageParmsData> imageParmsDatas;
    private BizImageMangage bizImageMangage;
    private Bitmap changeBitmap;
    private float temperature;
    private float tint;
    private ClassShowCut showCut;
    private boolean canTouch = true;
    private VerticalScaleNewView scaleView;

    private ImageView iv_underline;
    private int one;
    private int translationY;
    private int screenW;
    private DGProgressDialog3 dgProgressDialog3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_parms_new_layout);
//        presenter.onCreate(getIntent().getExtras());
        presenter.initCurrentData();
        initView();
        initData();
        onViewClick();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        gpuimage = null;
        setContentView(new View(this));
        if (tempgpuimage != null) {
            tempgpuimage.deleteImage();
        }
    }

    @Override
    public ImageParmsNewPresenter initPresenter() {
        return new ImageParmsNewPresenter();
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
    protected void initView() {
        //  back = (ImageView) findViewById(R.id.back);
        gpuimage = (GPUImageViewTexture) findViewById(R.id.gpuimage);
        //  text_fix = (TextView) findViewById(text_fix);
        //  image_util = (ImageView) findViewById(image_util);
//        pickerv = (PickerViewV) findViewById(R.id.pickerv);
//        left_land_seebar = (VerticalSeekBar) findViewById(R.id.left_land_seebar);
//        relsee = (RelativeLayout) findViewById(R.id.relsee);
        wait_view = (WaitProgressBarView) findViewById(R.id.wait_view);
        parms_title = (TitleView) findViewById(R.id.parms_title);
        final_img = (ImageView) findViewById(R.id.final_img);
        sViewPager = (SViewPager) findViewById(R.id.sviewPager);
        indicator = (FixedIndicatorView) findViewById(R.id.indicatorView);
        ll_light = (RelativeLayout) findViewById(R.id.ll_light);
        ll_balance = (LinearLayout) findViewById(R.id.ll_balance);

        ll_temperature = (LinearLayout) findViewById(R.id.ll_temperature);
        tv_temperature = (TextView) findViewById(R.id.tv_temperature);
        iv_temperature = (ImageView) findViewById(R.id.iv_temperature);

        ll_tint = (LinearLayout) findViewById(R.id.ll_tint);
        tv_tint = (TextView) findViewById(R.id.tv_tint);
        iv_tint = (ImageView) findViewById(R.id.iv_tint);

        ll_auto = (LinearLayout) findViewById(R.id.ll_auto);
        iv_auto = (ImageView) findViewById(R.id.iv_auto);

        ll_straw = (LinearLayout) findViewById(R.id.ll_straw);
        iv_straw = (ImageView) findViewById(R.id.iv_straw);

        imageParmsCircleView = (ImageParmsCircleView) findViewById(R.id.imageParmsCircleView);
        zoomableViewGroup = (ZoomableViewGroup) findViewById(R.id.zoomableViewGroup);
        gray_level = (ImageView) findViewById(R.id.gray_level);

        scaleView = (VerticalScaleNewView) findViewById(R.id.scaleView);

        iv_underline = (ImageView) findViewById(R.id.iv_underline);
    }

    @Override
    protected void initData() {
        parms_title.setType(TitleView.PIC_PARMS_NEW);
        gpuimage.setScaleType(GPUImageTexture.ScaleType.CENTER_INSIDE);
//        startEditTime();
        final_img.setVisibility(View.GONE);
//        pickerv.setCanTouch(true);
//        pickerv.setData(presenter.initPickerData());
//        presenter.pickervChanceType(App.getInstance().getResources().getString(R.string.exposire));

        if (presenter.getImgType() == presenter.LIGHT) {
            presenter.pickervChanceType(getResources().getString(R.string.exposire));
            ll_light.setVisibility(View.VISIBLE);
            ll_balance.setVisibility(View.GONE);
            TAB_COUNT = 3;
        } else if (presenter.getImgType() == presenter.COLOR) {
            presenter.pickervChanceType(getResources().getString(R.string.color_temperature));
            ll_light.setVisibility(View.GONE);
            ll_balance.setVisibility(View.VISIBLE);
            TAB_COUNT = 4;
            iv_underline.setVisibility(VISIBLE);
        }

        if (imageParmsDatas == null) {
            imageParmsDatas = setListData();
        }
        bizImageMangage = BizImageMangage.getInstance();
        if (presenter.getUrl().contains(".png")) {
            zoomableViewGroup.setBackground(getResources().getDrawable(R.drawable.bgbitmap));
        }

        if (CameraStatus.getCameraStatus().isBlackAndWhite()) {
            gray_level.setVisibility(View.VISIBLE);
        } else {
            gray_level.setVisibility(View.GONE);
        }


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenW = dm.widthPixels; // 获取分辨率宽度
        lineWidth = ImageUtil.Dp2Px(this, 20);
        offset = (screenW / TAB_COUNT - lineWidth) / 2;  // 计算偏移值
        one = offset * 2 + lineWidth;
        translationY = ImageUtil.Dp2Px(this, 38);
//        // 设置下划线初始位置
        iv_underline.setTranslationX(offset);
        iv_underline.setTranslationY(translationY);

    }

    /**
     * 当前选项卡的位置
     */
    private int current_index = 0;

    /**
     * 选项卡总数
     */
    private int TAB_COUNT = 3;


    /**
     * 初始偏移量（手机屏幕宽度 / 选项卡总数 - 下划线图片宽度） / 2
     */
    private int offset = 0;

    /**
     * 下划线图片宽度
     */
    private int lineWidth;


    @Override
    protected void onViewClick() {
        showGuide(presenter.getImgType());
        parms_title.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick) {
                    return;
                }

                showSaveLoading();

                Bitmap smallbit = null;
                try {
                    smallbit = new BitmapWithFilterCallable(0).call();
                } catch (Exception e) {
                    try {
                        smallbit=gpuimage.capture();//最终小图
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                final_img.setImageBitmap(smallbit);
                final_img.setVisibility(View.VISIBLE);
                //白平衡
               /* Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //白平衡
                        if(presenter.getImgType()==presenter.COLOR){
                            presenter.saveWhiterbalance();
                        }else if(presenter.getImgType()==presenter.LIGHT){
                            presenter.back();
                        }
                    }
                });
                thread.start();*/
                //白平衡
                if (presenter.getImgType() == presenter.COLOR) {
                    presenter.saveWhiterbalance();
                } else if (presenter.getImgType() == presenter.LIGHT) {
                    presenter.back();
                }
            }
        });
        parms_title.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick) {
                    return;
                }
                finish();
            }
        });

        zoomableViewGroup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (canTouch) {
                    return bizGestureListener.onTouchEvent(event);
                }
                return false;
            }
        });


        bizGestureListener = new BizGestureListener(this);


        bizGestureListener.setGestureOnTouchDetector(new BizGestureListener.GestureOnTouchDetector() {
            @Override
            public void touch(MotionEvent event) {
                zoomableViewGroup.touchEvent(event);

            }
        });

        bizGestureListener.setGestureUpDownDetector(new BizGestureListener.GestureUpDownDetector() {
            @Override
            public void onStartUpDown() {
                scaleView.setVisibility(VISIBLE);
            }

            @Override
            public void onUpDown(float addNum) {
                if (indicatorViewPager.getCurrentItem() != select_now) {
                    indicatorViewPager.setCurrentItem(select_now, true);
                }
                presenter.setFilterNumber(addNum * resize);
            }

            @Override
            public void onEndUpDown() {
                scaleView.setVisibility(View.GONE);
                presenter.setFilterNumberEnd();
            }

            @Override
            public void onUpDown(MotionEvent motionEvent) {

            }

            @Override
            public void onEndUpDown(MotionEvent motionEvent) {


            }

            @Override
            public void onStartUpDown(MotionEvent motionEvent) {


            }

            @Override
            public void onDown(MotionEvent motionEvent) {

            }
        });

        ll_temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_type == 0) {
                    return;
                }


                if (select_type != 1) {
//                    float fromX = one * 1;
//                    // 下划线移动完毕后的位置
//
//                    Animation animation = new TranslateAnimation(fromX, toX, 0, 0);
//                    animation.setFillAfter(true);
//                    animation.setDuration(250);
//
//                    iv_underline.setAnimation(animation);

//                    ValueAnimator valueAnimator= AnimFactory.rotationAnim(new AnimFactory.FloatListener() {
//                        @Override
//                        public void floatValueChang(float f) {
//                            iv_underline.setTranslationX((offset+one)-(f*one));
//                        }
//                    });
//                    valueAnimator.setDuration(250);
//                    valueAnimator.start();
                    iv_underline.setVisibility(VISIBLE);
                }
                if (iv_underline.getTranslationX() != offset) {
                    ValueAnimator valueAnimator = AnimFactory.rotationAnim(new AnimFactory.FloatListener() {
                        @Override
                        public void floatValueChang(float f) {
                            iv_underline.setTranslationX((offset + one) - (f * one));
                        }
                    });
                    valueAnimator.setDuration(250);
                    valueAnimator.start();
                }
//                if(null!=showCut){
//                    showCut.exit=true;
//                }
//                verticalScaleView.setVisibility(View.VISIBLE);
//                startEditTime();
                canTouch = true;
//                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tv_temperature.getLayoutParams();
//                lp.topMargin = ImageUtil.Dp2Px(ImageParmsNewActivity.this, 8);
                tv_temperature.setTextSize(16);
                tv_temperature.setTextColor(Color.parseColor("#0057FF"));
                iv_temperature.setVisibility(View.GONE);


                changeSelect(select_type);
                if (select_type == 2) {
                    presenter.refreshCTandTint();
                }
                select_type = 0;
                presenter.pickervChanceType(tv_temperature.getText().toString());
            }
        });

        ll_tint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_type == 1) {
                    return;
                }
                if (select_type != 0) {
                    iv_underline.setVisibility(VISIBLE);
                }
                if (iv_underline.getTranslationX() != offset + one) {
                    ValueAnimator valueAnimator = AnimFactory.rotationAnim(new AnimFactory.FloatListener() {
                        @Override
                        public void floatValueChang(float f) {
                            iv_underline.setTranslationX((offset) + (f * one));
                        }
                    });
                    valueAnimator.setDuration(250);
                    valueAnimator.start();
                }
//                if(null!=showCut){
//                    showCut.exit=true;
//                }
//                verticalScaleView.setVisibility(View.VISIBLE);
//                startEditTime();
                canTouch = true;
                tv_tint.setTextSize(16);
                tv_tint.setTextColor(Color.parseColor("#0057FF"));
                iv_tint.setVisibility(View.GONE);

                changeSelect(select_type);
                if (select_type == 2) {
                    presenter.refreshCTandTint();
                }
                select_type = 1;
                presenter.pickervChanceType(tv_tint.getText().toString());
            }
        });


        ll_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_type == 2) {
                    return;
                }
                iv_underline.setVisibility(View.INVISIBLE);
                iv_auto.setImageResource(R.drawable.auto_new_select);
                changeSelect(select_type);
                select_type = 2;
                setTitleText(getResources().getString(R.string.auto_white_balance));
                canTouch = false;
                mHandler = new Handler();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        changeBitmap = bizImageMangage.ProcessingPic(presenter.getmCurrentBitmap(), null, BizImageMangage.AUTO_WHITE_BALANCE, null);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < presenter.getCurrentPicDatas().size(); i++) {
                                    if (presenter.getCurrentPicDatas().get(i).getType() == BizImageMangage.WHITE_BALANCE) {
                                        presenter.setAutoFilterNumber(bizImageMangage.getTemperatureTint()[0] , bizImageMangage.getTemperatureTint()[1]);
                                        break;
                                    }
                                }
                            }
                        });
                    }
                }).start();

            }
        });

        ll_straw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_type == 3) {
                    return;
                }
                iv_underline.setVisibility(View.INVISIBLE);
                canTouch = false;
                imageParmsCircleView.setVisibility(View.VISIBLE);
                iv_straw.setImageResource(R.drawable.straw_select);
                changeSelect(select_type);
                select_type = 3;
                presenter.pickervChanceType(getResources().getString(R.string.color_custom));
                presenter.setFilterNumber(temperature, tint);
                imageParmsCircleView.setZoomableViewGroup(zoomableViewGroup);


            }
        });
        imageParmsCircleView.setChangListener(new ImageParmsCircleView.ChangListener() {
            @Override
            public void returnXYR(float x, float y, float r) {

            }

            @Override
            public void startUpDown() {

            }

            @Override
            public void upDown(int number) {

            }

            @Override
            public void endMove() {

            }

            @Override
            public void returnRGB(int color) {
                presenter.setColor(color);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                float fragColor_r = b - g;
                float fragColor_g = b - r;
                float fragColor_b = 0.0f;
                if (((r <= b) && (r >= g)) || ((r >= b) && (r <= g))) {
                    fragColor_g = 0.0f;
                    fragColor_b = r - b;
                    fragColor_r = r - g;
                } else if (((g <= r) && (g >= b)) || ((g >= r) && (g <= b))) {
                    fragColor_r = 0.0f;
                    fragColor_g = g - r;
                    fragColor_b = g - b;
                }

                if ((fragColor_r - fragColor_b) / 2 > 100) {
                    temperature = 100;
                } else if ((fragColor_r - fragColor_b) / 2 < -100) {
                    temperature = -100;
                } else {
                    temperature = (fragColor_r - fragColor_b) / 2;
                }
                if (fragColor_g > 100) {
                    tint = 100;
                } else if (fragColor_g < -100) {
                    tint = -100;
                } else {
                    tint = fragColor_g;
                }

                if (imageParmsCircleView.getVisibility() == View.VISIBLE) {

                    presenter.setFilterNumber(temperature, tint);

                }

            }
        });


        sViewPager.setCanScroll(true);
        indicatorViewPager = new IndicatorViewPager(indicator, sViewPager);
        indicatorViewPager.setPageOffscreenLimit(2);
        inflate = LayoutInflater.from(this);

        final IndicatorViewPager.IndicatorPagerAdapter adapter = new IndicatorViewPager.IndicatorViewPagerAdapter() {
            //            private int[] images = {R.color.white, R.color.white, R.color.white};
//            //当前选中
            float fromX;
            float toX;
            Animation animation;

            @Override
            public View getViewForTab(int position, View convertView, ViewGroup container) {
                if (convertView == null) {
                    convertView = inflate.inflate(R.layout.tab_iamge_parms, container, false);
                }
                return convertView;
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }


            @Override
            public View getViewForPage(final int position, View convertView, ViewGroup container) {

                final ViewHolder holder;
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = inflate.inflate(R.layout.image_parms_page_layout, container, false);
                    holder.ll_exposure = (LinearLayout) convertView.findViewById(R.id.ll_exposure);
                    holder.tv_exposure = (TextView) convertView.findViewById(R.id.tv_exposure);
                    holder.iv_exposure = (ImageView) convertView.findViewById(R.id.iv_exposure);
                    holder.ll_contrast = (LinearLayout) convertView.findViewById(R.id.ll_contrast);
                    holder.tv_contrast = (TextView) convertView.findViewById(R.id.tv_contrast);
                    holder.iv_contrast = (ImageView) convertView.findViewById(R.id.iv_contrast);


                    holder.ll_highlight = (LinearLayout) convertView.findViewById(R.id.ll_highlight);
                    holder.tv_highlight = (TextView) convertView.findViewById(R.id.tv_highlight);
                    holder.iv_highlight = (ImageView) convertView.findViewById(R.id.iv_highlight);
                    holder.iv_page_underline = (ImageView) convertView.findViewById(R.id.iv_page_underline);


                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.iv_page_underline.setTranslationX(offset);
                holder.iv_page_underline.setTranslationY(translationY);

                //曝光 高光 冷暖色调
                holder.ll_exposure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //选中页
                        if (select_now != position) {
                            //
                            if (select_now > position) {
                                // 下划线开始移动前的位置
                                fromX = screenW * 2;
                                // 下划线移动完毕后的位置
                                toX = one * 0;
                                animation = new TranslateAnimation(screenW, toX, 0, 0);
                                animation.setFillAfter(true);
                                animation.setDuration(250);
                            } else {
                                // 下划线开始移动前的位置
                                fromX = -screenW;
                                // 下划线移动完毕后的位置
                                toX = one * 0;
                                animation = new TranslateAnimation(fromX, toX, 0, 0);
                                animation.setFillAfter(true);
                                animation.setDuration(250);
                            }
                        } else {
                            // 下划线开始移动前的位置
                            fromX = one * current_index;
                            // 下划线移动完毕后的位置
                            toX = one * 0;
                            animation = new TranslateAnimation(fromX, toX, 0, 0);
                            animation.setFillAfter(true);
                            animation.setDuration(250);
                        }

                        for (int i = 0; i < imageParmsDatas.size(); i++) {
                            List<ImageParmsData.ImageParm> imageParms = imageParmsDatas.get(i).getImageParms();
                            for (int j = 0; j < imageParms.size(); j++) {
                                if (i == position && j == 0) {
                                    current_index = 0;
                                    imageParms.get(j).setSelect(true);
                                } else {
                                    imageParms.get(j).setSelect(false);
                                }
                            }
                        }


                        // 给图片添加动画

//                        if(null!=showCut){
//                            showCut.exit=true;
//                        }
//                        verticalScaleView.setVisibility(View.VISIBLE);
//                        startEditTime();
                        select_now = position;
                        notifyDataSetChanged();

                        presenter.pickervChanceType(holder.tv_exposure.getText().toString());

                    }
                });


                //对比度 阴影 清晰度
                holder.ll_contrast.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //选中页
                        if (select_now != position) {
                            //
                            if (select_now > position) {
                                // 下划线开始移动前的位置
                                fromX = screenW * 2;
                                // 下划线移动完毕后的位置
                                toX = one * 1;
                                animation = new TranslateAnimation(screenW, toX, 0, 0);
                                animation.setFillAfter(true);
                                animation.setDuration(250);
                            } else {
                                // 下划线开始移动前的位置
                                fromX = -screenW;
                                // 下划线移动完毕后的位置
                                toX = one * 1;
                                animation = new TranslateAnimation(fromX, toX, 0, 0);
                                animation.setFillAfter(true);
                                animation.setDuration(250);
                            }
                        } else {
                            // 下划线开始移动前的位置
                            fromX = one * current_index;
                            // 下划线移动完毕后的位置
                            toX = one * 1;
                            animation = new TranslateAnimation(fromX, toX, 0, 0);
                            animation.setFillAfter(true);
                            animation.setDuration(250);
                        }
                        for (int i = 0; i < imageParmsDatas.size(); i++) {
                            List<ImageParmsData.ImageParm> imageParms = imageParmsDatas.get(i).getImageParms();
                            for (int j = 0; j < imageParms.size(); j++) {
                                if (i == position && j == 1) {
                                    current_index = 1;
                                    imageParms.get(j).setSelect(true);
                                } else {
                                    imageParms.get(j).setSelect(false);
                                }
                            }
                        }


//                        if(null!=showCut){
//                            showCut.exit=true;
//                        }
//                        verticalScaleView.setVisibility(View.VISIBLE);
//                        startEditTime();
                        select_now = position;
                        notifyDataSetChanged();
                        presenter.pickervChanceType(holder.tv_contrast.getText().toString());

                    }
                });

                //亮度 饱和度 锐化
                holder.ll_highlight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//选中页
                        if (select_now != position) {
                            //
                            if (select_now > position) {
                                // 下划线开始移动前的位置
                                fromX = screenW * 2;
                                // 下划线移动完毕后的位置
                                toX = one * 2;
                                animation = new TranslateAnimation(screenW, toX, 0, 0);
                                animation.setFillAfter(true);
                                animation.setDuration(250);
                            } else {
                                // 下划线开始移动前的位置
                                fromX = -screenW;
                                // 下划线移动完毕后的位置
                                toX = one * 2;
                                animation = new TranslateAnimation(fromX, toX, 0, 0);
                                animation.setFillAfter(true);
                                animation.setDuration(250);
                            }
                        } else {
                            // 下划线开始移动前的位置
                            fromX = one * current_index;
                            // 下划线移动完毕后的位置
                            toX = one * 2;
                            animation = new TranslateAnimation(fromX, toX, 0, 0);
                            animation.setFillAfter(true);
                            animation.setDuration(250);
                        }
                        for (int i = 0; i < imageParmsDatas.size(); i++) {
                            List<ImageParmsData.ImageParm> imageParms = imageParmsDatas.get(i).getImageParms();
                            for (int j = 0; j < imageParms.size(); j++) {
                                if (i == position && j == 2) {
                                    current_index = 2;
                                    imageParms.get(j).setSelect(true);
                                } else {
                                    imageParms.get(j).setSelect(false);
                                }
                            }
                        }

//                        if(null!=showCut){
//                            showCut.exit=true;
//                        }
//                        verticalScaleView.setVisibility(View.VISIBLE);
//                        startEditTime();
                        select_now = position;
                        notifyDataSetChanged();
                        presenter.pickervChanceType(holder.tv_highlight.getText().toString());


                    }
                });

                if (select_now == position) {
                    holder.iv_page_underline.setVisibility(VISIBLE);
                    if (null != animation) {
                        holder.iv_page_underline.setAnimation(animation);
                    }
                } else {
                    holder.iv_page_underline.setVisibility(View.GONE);
                }


                if (position == 0) {
                    List<ImageParmsData.ImageParm> imageParms = imageParmsDatas.get(position).getImageParms();
                    holder.tv_exposure.setText(imageParms.get(0).getImageParmsText());
                    holder.tv_contrast.setText(imageParms.get(1).getImageParmsText());
                    holder.tv_highlight.setText(imageParms.get(2).getImageParmsText());

                    if (imageParms.get(0).isSelect()) {
                        holder.iv_exposure.setVisibility(View.GONE);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.tv_exposure.getLayoutParams();
                        lp.topMargin = ImageUtil.Dp2Px(ImageParmsNewActivity.this, 8);
                        holder.tv_exposure.setTextColor(Color.parseColor("#0057FF"));
                        holder.tv_exposure.setTextSize(16);

                    } else if (imageParms.get(1).isSelect()) {
                        holder.iv_contrast.setVisibility(View.GONE);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.tv_contrast.getLayoutParams();
                        lp.topMargin = ImageUtil.Dp2Px(ImageParmsNewActivity.this, 8);
                        holder.tv_contrast.setTextColor(Color.parseColor("#0057FF"));
                        holder.tv_contrast.setTextSize(16);
                    } else if (imageParms.get(2).isSelect()) {
                        holder.iv_highlight.setVisibility(View.GONE);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.tv_highlight.getLayoutParams();
                        lp.topMargin = ImageUtil.Dp2Px(ImageParmsNewActivity.this, 8);
                        holder.tv_highlight.setTextColor(Color.parseColor("#0057FF"));
                        holder.tv_highlight.setTextSize(16);
                    }

                } else if (position == 1) {
                    List<ImageParmsData.ImageParm> imageParms = imageParmsDatas.get(position).getImageParms();
                    holder.tv_exposure.setText(imageParms.get(0).getImageParmsText());
                    holder.tv_contrast.setText(imageParms.get(1).getImageParmsText());
                    holder.tv_highlight.setText(imageParms.get(2).getImageParmsText());
                    if (imageParms.get(0).isSelect()) {
                        holder.iv_exposure.setVisibility(View.GONE);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.tv_exposure.getLayoutParams();
                        lp.topMargin = ImageUtil.Dp2Px(ImageParmsNewActivity.this, 8);
                        holder.tv_exposure.setTextColor(Color.parseColor("#0057FF"));
                        holder.tv_exposure.setTextSize(16);

                    } else if (imageParms.get(1).isSelect()) {
                        holder.iv_contrast.setVisibility(View.GONE);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.tv_contrast.getLayoutParams();
                        lp.topMargin = ImageUtil.Dp2Px(ImageParmsNewActivity.this, 8);
                        holder.tv_contrast.setTextColor(Color.parseColor("#0057FF"));
                        holder.tv_contrast.setTextSize(16);
                    } else if (imageParms.get(2).isSelect()) {
                        holder.iv_highlight.setVisibility(View.GONE);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.tv_highlight.getLayoutParams();
                        lp.topMargin = ImageUtil.Dp2Px(ImageParmsNewActivity.this, 8);
                        holder.tv_highlight.setTextColor(Color.parseColor("#0057FF"));
                        holder.tv_highlight.setTextSize(16);
                    }
                } else if (position == 2) {
                    List<ImageParmsData.ImageParm> imageParms = imageParmsDatas.get(position).getImageParms();
                    holder.tv_exposure.setText(imageParms.get(0).getImageParmsText());
                    holder.tv_contrast.setText(imageParms.get(1).getImageParmsText());
                    holder.tv_highlight.setText(imageParms.get(2).getImageParmsText());

                    if (imageParms.get(0).isSelect()) {
                        holder.iv_exposure.setVisibility(View.GONE);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.tv_exposure.getLayoutParams();
                        lp.topMargin = ImageUtil.Dp2Px(ImageParmsNewActivity.this, 8);
                        holder.tv_exposure.setTextColor(Color.parseColor("#0057FF"));
                        holder.tv_exposure.setTextSize(16);

                    } else if (imageParms.get(1).isSelect()) {
                        holder.iv_contrast.setVisibility(View.GONE);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.tv_contrast.getLayoutParams();
                        lp.topMargin = ImageUtil.Dp2Px(ImageParmsNewActivity.this, 8);
                        holder.tv_contrast.setTextColor(Color.parseColor("#0057FF"));
                        holder.tv_contrast.setTextSize(16);
                    } else if (imageParms.get(2).isSelect()) {
                        holder.iv_highlight.setVisibility(View.GONE);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.tv_highlight.getLayoutParams();
                        lp.topMargin = ImageUtil.Dp2Px(ImageParmsNewActivity.this, 8);
                        holder.tv_highlight.setTextColor(Color.parseColor("#0057FF"));
                        holder.tv_highlight.setTextSize(16);
                    }
                }
                return convertView;
            }

            class ViewHolder {

                //曝光 高光 冷暖色调
                public LinearLayout ll_exposure;
                public TextView tv_exposure;
                public ImageView iv_exposure;


                //对比度 阴影 清晰度
                public LinearLayout ll_contrast;
                public TextView tv_contrast;
                public ImageView iv_contrast;


                //亮度 饱和度 锐化
                public LinearLayout ll_highlight;
                public TextView tv_highlight;
                public ImageView iv_highlight;

                private ImageView iv_page_underline;


            }

            @Override
            public int getCount() {
                return imageParmsDatas.size();
            }

        };


        indicatorViewPager.setAdapter(adapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        gpuimage.onResume();
        //   gpuimage.requestRender();
    }

    @Override
    public void showLoading() {
        canClick = false;
        wait_view.setVisibility(View.VISIBLE);
    }


    @Override
    public void hideLoading() {
        canClick = true;
        wait_view.setVisibility(View.GONE);
    }

    @Override
    public void showSaveLoading() {
        canClick = false;
        if (null == dgProgressDialog3) {
            dgProgressDialog3 = new DGProgressDialog3(this, true, getResources().getString(R.string.waiting));
        }
        dgProgressDialog3.show();
    }

    @Override
    public void dissMissSaveLoading() {
        canClick = true;
        if (dgProgressDialog3 != null && dgProgressDialog3.isShowing()) {
            dgProgressDialog3.dismiss();
        }
    }

    @Override
    public void finishView(int resultCode, Intent intent) {
        if (intent == null) {
            finish();
            return;
        }
        setResult(resultCode, intent);
        finish();
    }


    @Override
    public void setGPUFilter(GPUImageFilter gpuFilter) {
        gpuimage.setFilter(gpuFilter);
        gpuimage.requestRender();
    }

    @Override
    public void setTitleText(String text) {
        //  text_fix.setText(text);
        parms_title.setTitleText(text);
    }

    /**
     * @param progresssee 参数 0-200
     */
    @Override
    public void setProgresssee(int progresssee) {
//        if(isFirst){
//            verticalScaleView.setFinaly(progresssee);
//        }else {
//        verticalScaleView.setProgress(progresssee);
//        }

        scaleView.setParamProgressAndDefaultPoint(100, progresssee);

        gpuimage.requestRender();
    }

    @Override
    public void refresh() {
        gpuimage.requestRender();
    }

    @Override
    public void setGpuImageSize(int strX, int strY, int w, int h, Bitmap bitmap) {
        gpuimage.setLayoutParams(new RelativeLayout.LayoutParams(w, h));
        gpuimage.setTranslationX(strX);
        gpuimage.setTranslationY(strY);
        dstRect = new Rect(strX, strY, (w + strX), (h + strY));
        imageParmsCircleView.getGpuImageWH(dstRect);

        zoomableViewGroup.setListener(new ZoomableViewGroup.ImgUpData() {
            @Override
            public void imgUpdata(Matrix matrix) {
                RectF rectF = new RectF(dstRect.left, dstRect.top, dstRect.right, dstRect.bottom);
                matrix.mapRect(rectF);
//                paintview.setImgRect(new Rect((int)rectF.left,(int)rectF.top,(int)rectF.right,(int)rectF.bottom));
                zoomableViewGroup.setRect(new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
                float[] values = new float[9];
                matrix.getValues(values);

                resize = values[Matrix.MSCALE_X];
                imageParmsCircleView.resize(values[Matrix.MSCALE_X]);

            }
        });

        zoomableViewGroup.setRect(dstRect);

        imageParmsCircleView.setBitmap(bitmap);
    }

    private float resize;

    @Override
    public void setGpuImageBitmap(Bitmap bitmap) {
        gpuimage.setImage(bitmap);
        //imageParmsCircleView.getpicSize(bitmap.getWidth(), bitmap.getHeight());

        //zoomableViewGroup.setBitWidth(bitmap.getWidth());
        //zoomableViewGroup.setBitHight(bitmap.getHeight());


    }

    @Override
    public double getShowRelWidth() {
        return zoomableViewGroup.getMeasuredWidth();
    }

    @Override
    public double getShowRelHeight() {
        return zoomableViewGroup.getMeasuredHeight();
    }


    @Override
    public Bitmap getFinalBit() {
        tempgpuimage = new GPUImage(ImageParmsNewActivity.this);
        GPUImageFilter temp = presenter.getCurrentFilter();
        tempgpuimage.setImage(presenter.getmCurrentBitmap());
        tempgpuimage.setFilter(temp);
        Bitmap mbitmap = null;
        try {
            mbitmap = new BitmapWithFilterCallable(1).call();
        } catch (Exception e) {
            mbitmap = tempgpuimage.getBitmapWithFilterApplied();
        }
       /* try {
            savebit = gpuimage.capture();
            return savebit;
        } catch (Exception e) {

        }*/
        return mbitmap;
    }

    @Override
    public Bitmap getFinalSrcBit() {
        if (null == tempgpuimage) {
            tempgpuimage = new GPUImage(ImageParmsNewActivity.this);
        }
        GPUImageFilter temp = presenter.getCurrentFilter();
        tempgpuimage.setImage(presenter.getSrcBitmap());
        tempgpuimage.setFilter(temp);
        Bitmap mbitmap = null;
        try {
            mbitmap = new BitmapWithFilterCallable(1).call();
        } catch (Exception e) {
            mbitmap = tempgpuimage.getBitmapWithFilterApplied();
        }
        return mbitmap;
    }

    @Override
    public Bitmap getWhiteBalanceBit() {
        gpuimage.setFilter(presenter.getGpuImageWhiteBalanceNewFilter());
        return gpuimage.getBitmapWithFilterApplied();
    }

    private void changeSelect(int type) {
        if (type == 0) {
//            LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) tv_temperature.getLayoutParams();
//            lp1.topMargin = ImageUtil.Dp2Px(ImageParmsNewActivity.this, 12);
//            tv_temperature.setLayoutParams(lp1);
            tv_temperature.setTextSize(14);
            tv_temperature.setTextColor(Color.parseColor("#ADB5C2"));
            iv_temperature.setVisibility(View.GONE);

        } else if (type == 1) {
//            LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) tv_tint.getLayoutParams();
//            lp1.topMargin = ImageUtil.Dp2Px(ImageParmsNewActivity.this, 12);
//            tv_tint.setLayoutParams(lp1);
            tv_tint.setTextSize(14);
            tv_tint.setTextColor(Color.parseColor("#ADB5C2"));
            iv_tint.setVisibility(View.GONE);
        } else if (type == 2) {
            iv_auto.setImageResource(R.drawable.auto_new_unselect);
        } else if (type == 3) {
            imageParmsCircleView.setVisibility(View.INVISIBLE);
            iv_straw.setImageResource(R.drawable.straw);
        }
    }

    private List<ImageParmsData> setListData() {
        List<ImageParmsData> listdata = new ArrayList<>();

        //第一页
        ImageParmsData first = new ImageParmsData();

        ImageParmsData.ImageParm imageParm1 = first.new ImageParm(getResources().getDrawable(R.drawable.parms_page_select_bg), getResources().getString(R.string.exposire), true);
        ImageParmsData.ImageParm imageParm2 = first.new ImageParm(getResources().getDrawable(R.drawable.parms_page_select_bg), getResources().getString(R.string.contrast), false);
        ImageParmsData.ImageParm imageParm3 = first.new ImageParm(getResources().getDrawable(R.drawable.parms_page_select_bg), getResources().getString(R.string.brightness), false);

        List<ImageParmsData.ImageParm> imageParms1 = new ArrayList<>();

        imageParms1.add(imageParm1);
        imageParms1.add(imageParm2);
        imageParms1.add(imageParm3);

        first.setImageParms(imageParms1);


        //第二页
        ImageParmsData two = new ImageParmsData();
        ImageParmsData.ImageParm imageParm4 = first.new ImageParm(getResources().getDrawable(R.drawable.parms_page_select_bg), getResources().getString(R.string.high_light), false);
        ImageParmsData.ImageParm imageParm5 = first.new ImageParm(getResources().getDrawable(R.drawable.parms_page_select_bg), getResources().getString(R.string.shadow), false);
        ImageParmsData.ImageParm imageParm6 = first.new ImageParm(getResources().getDrawable(R.drawable.parms_page_select_bg), getResources().getString(R.string.saturation), false);


        List<ImageParmsData.ImageParm> imageParms2 = new ArrayList<>();
        imageParms2.add(imageParm4);
        imageParms2.add(imageParm5);
        imageParms2.add(imageParm6);

        two.setImageParms(imageParms2);


        //第三页
        ImageParmsData three = new ImageParmsData();
        ImageParmsData.ImageParm imageParm7 = first.new ImageParm(getResources().getDrawable(R.drawable.parms_page_select_bg), getResources().getString(R.string.clod_hot), false);
        ImageParmsData.ImageParm imageParm8 = first.new ImageParm(getResources().getDrawable(R.drawable.parms_page_select_bg), getResources().getString(R.string.defintion), false);
        ImageParmsData.ImageParm imageParm9 = first.new ImageParm(getResources().getDrawable(R.drawable.parms_page_select_bg), getResources().getString(R.string.sharpen), false);

        List<ImageParmsData.ImageParm> imageParms3 = new ArrayList<>();
        imageParms3.add(imageParm7);
        imageParms3.add(imageParm8);
        imageParms3.add(imageParm9);
        three.setImageParms(imageParms3);

        listdata.add(first);
        listdata.add(two);
        listdata.add(three);

        return listdata;
    }

    private int showTime = 1;//显示倒计时
    private Handler mHandler;//全局handler

    /**
     * 开启倒计时
     */
    public void startEditTime() {
        showTime = 1;
        showCut = new ClassShowCut();
        showCut.start();
    }

    class ClassShowCut extends Thread implements Runnable {//倒计时逻辑子线程

        private volatile boolean exit = false;

        @Override
        public void run() {
            while (showTime > 0) {//整个倒计时执行的循环
                if (exit) {
                    break;
                }
                showTime--;
                LogUtil.d(showTime + "");

                try {
                    Thread.sleep(5000);//线程休眠1秒     这个就是倒计时的间隔时间
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            //下面是倒计时结束逻辑
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (exit == false) {
//                        verticalScaleView.setVisibility(View.INVISIBLE);
                    }
                }
            });
            showTime = 1;//修改倒计时剩余时间变量为1秒
        }
    }

    private class BitmapWithFilterCallable implements Callable<Bitmap> {

        private int mType;

        public BitmapWithFilterCallable(int type) {
            this.mType = type;
        }

        @Override
        public Bitmap call() throws Exception {
            if (mType == 0) {
                return gpuimage.capture();
            } else {
                return tempgpuimage.getBitmapWithFilterApplied();

            }
        }
    }

    private void showGuide(int type){
        if(type==presenter.LIGHT){
            View panelContent_image=findViewById(R.id.panelContent);

            NewbieGuide.with(this)
                    .setLabel("pagePhotoEditParam")//设置引导层标示区分不同引导层，必传！否则报错
                    .alwaysShow(false)//是否每次都显示引导层，默认false，只显示一次
                    .addGuidePage(//添加一页引导页
                            GuidePage.newInstance()//创建一个实例
                                    .addHighLight(panelContent_image)
                                    .setLayoutRes(R.layout.guide_photoedit_select)//设置引导页布局
                                    .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {

                                        @Override
                                        public void onLayoutInflated(View view, Controller controller) {
                                            ImageView photoedit_choice = (ImageView)view.findViewById(R.id.photoedit_param_choice);
                                            photoedit_choice.setVisibility(View.VISIBLE);
                                            //引导页布局填充后回调，用于初始化
                                       /* TextView tv = view.findViewById(R.id.textView2);
                                        tv.setText("我是动态设置的文本");*/
                                        }
                                    })
                                    .setEnterAnimation(enterAnimation)//进入动画
                                    .setExitAnimation(exitAnimation)//退出动画
                    )

                    .addGuidePage(GuidePage.newInstance()
                            .setLayoutRes(R.layout.guide_photoedit_select)
                            .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                @Override
                                public void onLayoutInflated(View view, final Controller controller) {
                                    ImageView photoedit_param_adjust = (ImageView) view.findViewById(R.id.photoedit_param_adjust);
                                    photoedit_param_adjust.setVisibility(View.VISIBLE);
                                }
                            })
                    )
                    .show();//显示引导层(至少需要一页引导页才能显示)
        }

    }
}