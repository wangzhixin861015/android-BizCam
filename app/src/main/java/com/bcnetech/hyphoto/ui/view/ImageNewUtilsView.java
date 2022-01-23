package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.dialog.ChoiceDialog;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.EventStatisticsUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.EventCommon;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.ShareData;
import com.bcnetech.hyphoto.data.SqlControl.ImageDataSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.PresetParmsSqlControl;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.presenter.ImageParmsNewPresenter;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.ui.activity.AlbumNewActivity;
import com.bcnetech.hyphoto.ui.activity.photoedit.AutoRecoveryActivity;
import com.bcnetech.hyphoto.ui.activity.photoedit.BgRepairActivity;
import com.bcnetech.hyphoto.ui.activity.photoedit.BizMattingNewActivity;
import com.bcnetech.hyphoto.ui.activity.photoedit.CropRotateActivity;
import com.bcnetech.hyphoto.ui.activity.photoedit.PartParmsActivity;
import com.bcnetech.hyphoto.ui.view.viewPagerIndicator.view.indicator.FixedIndicatorView;
import com.bcnetech.hyphoto.ui.view.viewPagerIndicator.view.indicator.IndicatorViewPager;
import com.bcnetech.hyphoto.ui.view.viewPagerIndicator.view.viewpager.SViewPager;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.ShareImagesUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.gpumanage.GPUImageFilterTools;

/**
 * Created by a1234 on 17/6/15.
 */

public class ImageNewUtilsView extends BaseRelativeLayout {
    private Activity activity;
    public static ImageData imageparms;//图片的参数
    private ImageparmsNewView imageparmsNewView;
    private ImageShareParmsEditView share_img_edit;

    public ImageNewUtilsView(Context context) {
        super(context);
    }

    private int width, height;
    // private Bitmap changBitmap;

    private ImageDataSqlControl imageDataSqlControl;
    private GPUImage gpuImage;

    private boolean nowtypeStart = true;//判断当前状态,点击返回键时用

    private ImageNewUtilsTopView imageNewUtilsTopView;

    private SViewPager sViewPager;
    private FixedIndicatorView indicator;
    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;
    private Intent intent;
    private RelativeLayout panelContent_image;
    private LinearLayout ll_title;
    private RelativeLayout image_info;
    private int startPosition;
    private PresetParmsSqlControl presetParmsSqlControl;

    private PresetParm parms;//预设参数
    private List<PictureProcessingData> histortList;
    private ChoiceDialog mchoiceDialog;

    private RelativeLayout rl_ai360_share;
    private TextView tv_ai360_share;


    public ImageNewUtilsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageNewUtilsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.image_new_utils_layout, this);
        imageNewUtilsTopView = (ImageNewUtilsTopView) findViewById(R.id.image_new_utils_title);
        imageparmsNewView = (ImageparmsNewView) findViewById(R.id.image_info);


        sViewPager = (SViewPager) findViewById(R.id.sviewPager_image);
        indicator = (FixedIndicatorView) findViewById(R.id.indicatorView_image);
        panelContent_image = (RelativeLayout) findViewById(R.id.panelContent_image);
        ll_title = (LinearLayout) findViewById(R.id.ll_title);
        share_img_edit = (ImageShareParmsEditView) findViewById(R.id.share_img_edit);
        rl_ai360_share = findViewById(R.id.rl_ai360_share);
        tv_ai360_share = findViewById(R.id.tv_ai360_share);
    }

    @Override
    protected void initData() {
        super.initData();

        sViewPager.setCanScroll(true);
        indicatorViewPager = new IndicatorViewPager(indicator, sViewPager);
        indicatorViewPager.setPageOffscreenLimit(2);
        inflate = LayoutInflater.from(getContext());



        share_img_edit.setImageShareInterface(new ImageShareParmsEditView.ImageShareInterface() {
            @Override
            public void close(int type) {
                if (type != 0) {
                    imageparmsNewView.closeView();
                }

            }

            @Override
            public void upload(int type) {
//                if (parms == null) {
                parms = new PresetParm();
                String url = imageparms.getSmallLocalUrl();
                parms.setTextSrc(url);
                String devicename = imageparmsNewView.getDeviceName();
                parms.setEquipment(devicename);
                parms.setTimeStamp(imageparms.getTimeStamp());
//                }
                LightRatioData data = imageparms.getLightRatioData();
                LoginedUser loginedUser = LoginedUser.getLoginedUser();
                String name = loginedUser.getNickname();
                parms.setAuther(name);
                parms.setLightRatioData(data);
                parms.setName(share_img_edit.getName());
                parms.setLabels(share_img_edit.getTags());
                parms.setDescribe("");
                parms.setParmlists(imageparms.getImageTools());
                parms.setPartParmlists(imageparms.getImageParts());
                parms.setImageHeight(height + "");
                parms.setImageWidth(width + "");
                parms.setCameraParm(imageparms.getCameraParm());
                savePreParmsToCloud(parms, type, imageparms.isMatting());
            }
        });

        imageparmsNewView.setImageparmsViewListener(new ImageparmsNewView.ImageparmsViewListener() {
            @Override
            public void onImageparmsView() {

            }

            @Override
            public void onPresetParms() {

            }

            @Override
            public void onNoChange(boolean nochange) {

            }

            @Override
            public void onbtnclose() {
                nowtypeStart = true;
                imageNewUtilsTopView.setClickEnabled(true);
            }

            @Override
            public void deletePreset() {

            }

            @Override
            public void downLoadPreset() {

            }

            @Override
            public void sharePreset() {
                share_img_edit.bringToFront();
                share_img_edit.setImag(imageparms.getSmallLocalUrl());
                share_img_edit.RandomSystemTag();
                share_img_edit.setVisibility(VISIBLE);
            }
        });

        setAdapter();

    }

    public boolean isShowDialog() {
        String url = BizImageMangage.getInstance().getUrlFromCurrentPostion(imageparms.getCurrentPosition(), imageparms, true, true);
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(url.substring(7), newOpts);//此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        if (w > BitmapUtils.MAXLENGTH || h > BitmapUtils.MAXLENGTH) {
            return true;
        } else {
            return false;
        }
    }


    private void dissmissChoiceDialog() {
        if (mchoiceDialog != null) {
            mchoiceDialog.dismiss();
        }
    }

    public void imageDialog(final String type) {
        if (mchoiceDialog == null) {
            mchoiceDialog = ChoiceDialog.createInstance(getContext());
        }
        mchoiceDialog.show();
        mchoiceDialog.setAblumTitle(getContext().getString(R.string.alert));
        String message = getResources().getString(R.string.is_scan);
        mchoiceDialog.setAblumMessage(message);
        mchoiceDialog.setChoiceCallback(new ChoiceDialog.CardDialogCallback() {
            @Override
            public void onOKClick() {
                dissmissChoiceDialog();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (type.equals("BgRepairActivity")) {
                            intent = new Intent(getContext(), BgRepairActivity.class);
                            intent.putExtra(Flag.CURRENT_PART_POSITION, imageparms.getCurrentPosition());
                            getContext().startActivity(intent);
                        } else if (type.equals("BizMattingNewActivity")) {
                            Intent intent = new Intent(getContext(), BizMattingNewActivity.class);
                            ((Activity) getContext()).startActivityForResult(intent, Flag.MATTING_PARMS_ACTIVITY);
                        } else if (type.equals("CropRotateActivity")) {
                            intent = new Intent(getContext(), CropRotateActivity.class);
                            intent.putExtra(Flag.CURRENT_PART_POSITION, imageparms.getCurrentPosition());
                            getContext().startActivity(intent);
                        }
                    }
                });
            }

            @Override
            public void onCancelClick() {
                dissmissChoiceDialog();
            }

            @Override
            public void onDismiss() {

            }
        });

    }


    @Override
    protected void onViewClick() {
        super.onViewClick();


        /**
         * 删除
         */
        imageNewUtilsTopView.delete(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AlbumNewActivity) getContext()).presenter.deleImageDialog(((AlbumNewActivity) getContext()).position);
            }
        });
        imageNewUtilsTopView.close(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AlbumNewActivity) getContext()).closeImageUtil();
            }
        });

        /**
         * 详情
         */
        imageNewUtilsTopView.info(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowtypeStart = false;
                startPosition = imageparms.getCurrentPosition();
//
                if (histortList == null) {
                    histortList = new ArrayList<>();
                }
                histortList.clear();
                if (imageparms.getImageParts() != null) {
                    histortList.addAll(imageparms.getImageParts());
                    Collections.reverse(histortList);
                }

                if (imageparms.getImageTools() != null && imageparms.getImageTools().size() != 0) {

                    /**
                     * 图加滤镜
                     */
                    if (!StringUtil.isBlank(imageparms.getValue2()) && !"null".equals(imageparms.getValue2())) {
                        histortList.add(new PictureProcessingData(BizImageMangage.PARMS, imageparms.getValue2()));
                    } else {
                        histortList.add(new PictureProcessingData(BizImageMangage.PARMS, "", imageparms.getLocalUrl(), ""));
                    }
                }
                if (imageparms.getPresetParms() != null) {
                    histortList.add(new PictureProcessingData(BizImageMangage.SRC_PRESET_PARMS, imageparms.getLocalUrl()));

                } else {
                    histortList.add(new PictureProcessingData(BizImageMangage.SRC, imageparms.getLocalUrl()));
                }
                imageparmsNewView.setData(histortList, histortList.size() - 1, startPosition, imageparms.getType());
                if (imageparms.getPresetParms() != null) {
                    if (BitmapFactory.decodeFile(imageparms.getLocalUrl().substring(7)) != null) {
                        Bitmap bitmap = imageparmsNewView.getCurrentImg(BitmapUtils.compress(BitmapFactory.decodeFile(imageparms.getLocalUrl().substring(7))), histortList.size() - 1);
                        imageparmsNewView.setChangBitmap(bitmap);
                    }
                }
                imageparmsNewView.bringToFront();
                imageparmsNewView.setVisibility(VISIBLE);
                if (imageparmsNewView.isCanShare(imageparms)) {
                    imageparmsNewView.showGuide();
                }
                imageparmsNewView.inAnimationAtart();
                imageNewUtilsTopView.setClickEnabled(false);
            }
        });

        tv_ai360_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == sharePopupWindow) {
                    sharePopupWindow = showTipPopupWindow(rl_ai360_share);
                } else {

                    sharePopupWindow.showAsDropDown(rl_ai360_share);

                }
            }
        });
    }


    private PopupWindow sharePopupWindow;


    public boolean isNowtypeStart() {
        return this.nowtypeStart;
    }


    public void setAdapter() {

        final boolean isConnectionBox = false;
        final boolean isConnectionBoxAndMH = false;
        final int size;
        if (isConnectionBox) {
            size = 3;
        } else {
            size = 2;
        }


        IndicatorViewPager.IndicatorPagerAdapter adapter = new IndicatorViewPager.IndicatorViewPagerAdapter() {


            @Override
            public View getViewForTab(int position, View convertView, ViewGroup container) {
                if (convertView == null) {
                    convertView = inflate.inflate(R.layout.tab_matting_guide, container, false);
                }
                return convertView;
            }

            @Override
            public View getViewForPage(int position, View convertView, ViewGroup container) {

                ViewHolder holder;
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = inflate.inflate(R.layout.image_utils_guide, container, false);

                    holder.auto_recovery = (LinearLayout) convertView.findViewById(R.id.auto_recovery);

                    holder.light = (LinearLayout) convertView.findViewById(R.id.light);

                    holder.pen = (LinearLayout) convertView.findViewById(R.id.pen);
                    holder.pen_img = (ImageView) convertView.findViewById(R.id.pen_img);
                    holder.pen_text = (TextView) convertView.findViewById(R.id.pen_text);

                    holder.white_balance = (LinearLayout) convertView.findViewById(R.id.white_balance);
                    holder.white_balance_img = (ImageView) convertView.findViewById(R.id.white_balance_img);
                    holder.white_balance_text = (TextView) convertView.findViewById(R.id.white_balance_text);

                    holder.backgroundchang = (LinearLayout) convertView.findViewById(R.id.backgroundchang);
                    holder.backgroundchang_img = (ImageView) convertView.findViewById(R.id.backgroundchang_img);
                    holder.backgroundchang_text = (TextView) convertView.findViewById(R.id.backgroundchang_text);

                    holder.revolve = (LinearLayout) convertView.findViewById(R.id.revolve);
                    holder.revolve_img = (ImageView) convertView.findViewById(R.id.revolve_img);
                    holder.revolve_text = (TextView) convertView.findViewById(R.id.revolve_text);

                    holder.matting = (LinearLayout) convertView.findViewById(R.id.matting);
                    holder.matting_img = (ImageView) convertView.findViewById(R.id.matting_img);
                    holder.matting_text = (TextView) convertView.findViewById(R.id.matting_text);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.light.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageParmsNewPresenter.startAction((Activity) getContext(),
                                ImageParmsNewPresenter.LIGHT,
                                imageparms.getCurrentPosition(),
                                Flag.IMAGE_PARMS_ACTIVITY);
                    }
                });

                holder.pen.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(getContext(), PartParmsActivity.class);
                        intent.putExtra(Flag.CURRENT_PART_POSITION, imageparms.getCurrentPosition());
                        getContext().startActivity(intent);
                    }
                });

                holder.backgroundchang.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isShowDialog()) {
                            imageDialog("BgRepairActivity");
                        } else {
                            intent = new Intent(getContext(), BgRepairActivity.class);
                            intent.putExtra(Flag.CURRENT_PART_POSITION, imageparms.getCurrentPosition());
                            getContext().startActivity(intent);
                        }


                    }
                });


                holder.auto_recovery.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(getContext(), AutoRecoveryActivity.class);
                        intent.putExtra(Flag.CURRENT_PART_POSITION, imageparms.getCurrentPosition());
                        getContext().startActivity(intent);


                    }
                });

                holder.revolve.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isShowDialog()) {
                            imageDialog("CropRotateActivity");
                        } else {
                            intent = new Intent(getContext(), CropRotateActivity.class);
                            intent.putExtra(Flag.CURRENT_PART_POSITION, imageparms.getCurrentPosition());
                            getContext().startActivity(intent);
                        }
                      /*  Intent intent = new Intent(getContext(), CropRotateActivity.class);
                        intent.putExtra(Flag.PART_PARMS, imageparms);
                        intent.putExtra(Flag.CURRENT_PART_POSITION, imageparms.getCurrentPosition());
                        getContext().startActivity(intent);*/
                    }
                });

                holder.white_balance.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageParmsNewPresenter.startAction((Activity) getContext(),
                                ImageParmsNewPresenter.COLOR,
                                imageparms.getCurrentPosition(),
                                Flag.IMAGE_PARMS_ACTIVITY);
                    }
                });

                holder.matting.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isShowDialog()) {
                            imageDialog("BizMattingNewActivity");
                        } else {
                            Intent intent = new Intent(getContext(), BizMattingNewActivity.class);
                            ((Activity) getContext()).startActivityForResult(intent, Flag.MATTING_PARMS_ACTIVITY);
                        }


                    }
                });
                if (isConnectionBoxAndMH) {
                    if (position == 0) {
                        holder.auto_recovery.setVisibility(View.VISIBLE);
                        holder.pen.setVisibility(View.VISIBLE);
                        holder.matting.setVisibility(View.VISIBLE);

                        holder.light.setVisibility(View.GONE);
                        holder.backgroundchang.setVisibility(View.GONE);
                        holder.revolve.setVisibility(View.GONE);

                        holder.white_balance.setVisibility(View.GONE);


                    } else if (position == 1) {
                        holder.auto_recovery.setVisibility(View.GONE);
                        holder.pen.setVisibility(View.GONE);
                        holder.matting.setVisibility(View.GONE);

                        holder.light.setVisibility(View.VISIBLE);
                        holder.backgroundchang.setVisibility(View.VISIBLE);
                        holder.revolve.setVisibility(View.VISIBLE);

                        holder.white_balance.setVisibility(View.GONE);

                    } else if (position == 2) {
                        holder.auto_recovery.setVisibility(View.GONE);
                        holder.pen.setVisibility(View.GONE);
                        holder.matting.setVisibility(View.GONE);

                        holder.light.setVisibility(View.GONE);
                        holder.backgroundchang.setVisibility(View.GONE);
                        holder.revolve.setVisibility(View.GONE);

                        holder.white_balance.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (position == 0) {

                        holder.light.setVisibility(View.VISIBLE);
                        holder.pen.setVisibility(View.VISIBLE);
                        holder.matting.setVisibility(View.VISIBLE);

                        holder.white_balance.setVisibility(View.GONE);
                        holder.backgroundchang.setVisibility(View.GONE);
                        holder.revolve.setVisibility(View.GONE);

                        holder.auto_recovery.setVisibility(View.GONE);

                    } else if (position == 1) {
                        holder.light.setVisibility(View.GONE);
                        holder.pen.setVisibility(View.GONE);
                        holder.matting.setVisibility(View.GONE);

                        holder.white_balance.setVisibility(View.VISIBLE);
                        holder.backgroundchang.setVisibility(View.VISIBLE);
                        holder.revolve.setVisibility(View.VISIBLE);

                        holder.auto_recovery.setVisibility(View.GONE);

                    }
                }
                return convertView;
            }

            @Override
            public int getCount() {
                return size;
            }

            class ViewHolder {

                private LinearLayout auto_recovery;
                private LinearLayout light;
                private LinearLayout pen;
                private ImageView pen_img;
                private TextView pen_text;

                private LinearLayout white_balance;
                private ImageView white_balance_img;
                private TextView white_balance_text;

                private LinearLayout backgroundchang;
                private ImageView backgroundchang_img;
                private TextView backgroundchang_text;

                private LinearLayout revolve;
                private ImageView revolve_img;
                private TextView revolve_text;

                private LinearLayout matting;
                private ImageView matting_img;
                private TextView matting_text;


            }

        };
        indicator.setCurrentItem(0, false);
        indicatorViewPager.setAdapter(adapter);

    }


    public void setImageData(ImageData imageData,String type) {
        this.imageparms = imageData;
        if(type.equals("add")||type.equals("new")){
            setAdapter();
        }
        imageparmsNewView.setparms(imageparms);
        imageparmsNewView.invalidate();
        int[] imageWH = ImageUtil.decodeUriAsBitmap(imageData.getSmallLocalUrl());
        ImageNewUtilsView.this.width = imageWH[0];
        ImageNewUtilsView.this.height = imageWH[1];
        String url = imageparms.getLocalUrl();
        if (imageData.getType() == Flag.TYPE_VIDEO) {
            url = "file://" + imageparms.getLocalUrl();
        }
        imageparmsNewView.getSize(width, height, url);


        if (imageparms.getType() == Flag.TYPE_PIC) {
            if (imageparms.getLocalUrl().endsWith(".png") || imageparms.getLocalUrl().endsWith(".jpg") || imageparms.getLocalUrl().endsWith(".jpeg")) {
                panelContent_image.setVisibility(VISIBLE);
            } else {
                panelContent_image.setVisibility(GONE);
            }
            rl_ai360_share.setVisibility(GONE);
        } else if (imageparms.getType() == Flag.TYPE_AI360) {
            rl_ai360_share.setVisibility(VISIBLE);
            panelContent_image.setVisibility(GONE);

            if (!StringUtil.isBlank(imageparms.getValue3())) {
                tv_ai360_share.setText(getResources().getString(R.string.share));
                tv_ai360_share.setClickable(true);
            } else {
                tv_ai360_share.setText(getResources().getString(R.string.generate_share_link));
                tv_ai360_share.setClickable(false);
            }
        } else {
            panelContent_image.setVisibility(GONE);
            rl_ai360_share.setVisibility(GONE);
        }

    }


    public void updataImageData(ImageData imageData) {
        this.imageparms = imageData;
        imageparmsNewView.setparms(imageData);
        int[] imageWH = ImageUtil.decodeUriAsBitmap(imageData.getSmallLocalUrl());
        ImageNewUtilsView.this.width = imageWH[0];
        ImageNewUtilsView.this.height = imageWH[1];
        String url = imageparms.getLocalUrl();
        if (imageData.getType() == Flag.TYPE_VIDEO) {
            url = "file://" + imageparms.getLocalUrl();
        }
        imageparmsNewView.getSize(width, height, url);

    }


    /**
     * 更具传入的filter  获取bitamp
     *
     * @param imageToolLists
     * @param bitmap
     * @return
     */
    private Bitmap filterToBitmap(List<PictureProcessingData> imageToolLists, Bitmap bitmap) {
        List<GPUImageFilter> gpuImageFilters = new ArrayList<>();
        for (int i = 0; imageToolLists != null && i < imageToolLists.size(); i++) {
            GPUImageFilter mFilter;
            GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
            mFilter = BizImageMangage.getInstance().getGPUFilterforType(getContext(), imageToolLists.get(i).getType());
            gpuImageFilters.add(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
            mFilterAdjuster.adjust(imageToolLists.get(i).getNum());
        }

        GPUImageFilter currentFilter;
        if (gpuImageFilters == null || gpuImageFilters.size() == 0) {
            currentFilter = new GPUImageFilter();
        } else {
            currentFilter = new GPUImageFilterGroup(gpuImageFilters);
        }
        gpuImage.setFilter(currentFilter);
        return gpuImage.getBitmapWithFilterApplied(bitmap);
    }


    public ImageData getResultImageData() {
        return this.imageparms;
    }

    public void onViewClosed() {
        imageparmsNewView.closeView();
        gpuImage = null;
        // imageparms=null;
        parms = null;
        imageparms=null;
        if (histortList != null) {
            histortList.clear();
        }
        System.gc();
    }

    private ValueAnimator valueAnimator;

    public void out() {

        startFloatAnim(new AnimFactory.FloatListener() {
            @Override
            public void floatValueChang(float f) {


                ll_title.setTranslationY(imageNewUtilsTopView.getMeasuredHeight() * -f);
                ll_title.setAlpha(1 - f);

                panelContent_image.setTranslationY(panelContent_image.getMeasuredHeight() * f);
                panelContent_image.setAlpha(1 - f);
            }
        });
    }

    public void in() {
        startFloatAnim(new AnimFactory.FloatListener() {
            @Override
            public void floatValueChang(float f) {
                ll_title.setTranslationY(-imageNewUtilsTopView.getMeasuredHeight() * (1 - f));
                ll_title.setAlpha(f);

                panelContent_image.setTranslationY(panelContent_image.getMeasuredHeight() * (1 - f));
                panelContent_image.setAlpha(f);
            }
        });
    }


    public void startFloatAnim(AnimFactory.FloatListener floatListener) {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator = AnimFactory.rotationAnim(floatListener);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }


    private void savePreParmsToCloud(final PresetParm presetParm, final int type, final boolean isMatting) {
        //仅保存
        if (type == 1) {
            EventStatisticsUtil.event(getContext(), EventCommon.PHOTO_EDIT_IMG_SHARE_SAVE_ONLY);
//            newUpload(1);
        } else if (type == 2) {
            EventStatisticsUtil.event(getContext(), EventCommon.PHOTO_EDIT_IMG_SHARE_SAVE_AND_SHARE);
//            newUpload(2);
        }

    }


    private PopupWindow showTipPopupWindow(final View anchorView) {

        LinearLayout ll_qq;
        LinearLayout ll_wechat;
        LinearLayout ll_wechat_firend;
        LinearLayout ll_sina;
        LinearLayout ll_qq_zone;
        LinearLayout ll_other;

        LinearLayout ll_twitter;
        LinearLayout ll_facebook;

        LinearLayout ll_cnOne;
        LinearLayout ll_cnTwo;
        LinearLayout ll_us;

        LinearLayout ll_us_other;

        final View contentView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.share_view_layout, null);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 创建PopupWindow时候指定高宽时showAsDropDown能够自适应
        // 如果设置为wrap_content,showAsDropDown会认为下面空间一直很充足（我以认为这个Google的bug）
        // 备注如果PopupWindow里面有ListView,ScrollView时，一定要动态设置PopupWindow的大小
        final PopupWindow popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, contentView.getMeasuredHeight(), false);

        ll_other = (LinearLayout) contentView.findViewById(R.id.ll_other);
        ll_qq_zone = (LinearLayout) contentView.findViewById(R.id.ll_qq_zone);
        ll_qq = (LinearLayout) contentView.findViewById(R.id.ll_qq);
        ll_sina = (LinearLayout) contentView.findViewById(R.id.ll_sina);
        ll_wechat = (LinearLayout) contentView.findViewById(R.id.ll_wechat);
        ll_wechat_firend = (LinearLayout) contentView.findViewById(R.id.ll_wechat_firend);

        ll_cnOne = (LinearLayout) contentView.findViewById(R.id.ll_cnOne);
        ll_cnTwo = (LinearLayout) contentView.findViewById(R.id.ll_cnTwo);
        ll_us = (LinearLayout) contentView.findViewById(R.id.ll_us);
        ll_us_other = (LinearLayout) contentView.findViewById(R.id.ll_us_other);
        ll_twitter = (LinearLayout) contentView.findViewById(R.id.ll_twitter);
        ll_facebook = (LinearLayout) contentView.findViewById(R.id.ll_facebook);

        if (Flag.isEnglish) {
            ll_us.setVisibility(View.VISIBLE);
        } else {
            ll_cnOne.setVisibility(View.VISIBLE);
            ll_cnTwo.setVisibility(View.VISIBLE);
        }

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
//
            }
        });


        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 自动调整箭头的位置
                autoAdjustArrowPos(popupWindow, contentView, anchorView);
                contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        ll_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ShareData> picPaths = new ArrayList<>();
                ShareData shareData = new ShareData();

                shareData.setUrl(imageparms.getValue3());
                shareData.setCover(imageparms.getSmallLocalUrl().substring(7));
                shareData.setTitle(imageparms.getValue5());

                picPaths.add(shareData);

                popupWindow.dismiss();
                ShareImagesUtil.onShareQQ((AlbumNewActivity) getContext(), picPaths, false, imageparms.getValue3());
            }
        });
        ll_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ShareData> picPaths = new ArrayList<>();
                ShareData shareData = new ShareData();

                shareData.setUrl(imageparms.getValue3());
                shareData.setCover(imageparms.getSmallLocalUrl().substring(7));
                shareData.setTitle(imageparms.getValue5());

                picPaths.add(shareData);

                popupWindow.dismiss();
                ShareImagesUtil.onShareWeChat((AlbumNewActivity) getContext(), picPaths, false, imageparms.getValue3());
            }
        });
        ll_wechat_firend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ShareData> picPaths = new ArrayList<>();
                ShareData shareData = new ShareData();

                shareData.setUrl(imageparms.getValue3());
                shareData.setCover(imageparms.getSmallLocalUrl().substring(7));
                shareData.setTitle(imageparms.getValue5());

                picPaths.add(shareData);


                popupWindow.dismiss();
                ShareImagesUtil.onShareWeChatFriend((AlbumNewActivity) getContext(), picPaths, false, imageparms.getValue3());
            }
        });
        ll_sina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ShareData> picPaths = new ArrayList<>();
                ShareData shareData = new ShareData();

                shareData.setUrl(imageparms.getValue3());
                shareData.setCover(imageparms.getSmallLocalUrl().substring(7));
                shareData.setTitle(imageparms.getValue5());

                picPaths.add(shareData);

                popupWindow.dismiss();
                ShareImagesUtil.onShareSina((AlbumNewActivity) getContext(), picPaths, false, imageparms.getValue3());
            }
        });
        ll_qq_zone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ShareData> picPaths = new ArrayList<>();
                ShareData shareData = new ShareData();

                shareData.setUrl(imageparms.getValue3());
                shareData.setCover(imageparms.getSmallLocalUrl().substring(7));
                shareData.setTitle(imageparms.getValue5());

                picPaths.add(shareData);

                popupWindow.dismiss();
                ShareImagesUtil.onShareQQZone((AlbumNewActivity) getContext(), picPaths, false, imageparms.getValue3());
            }
        });
        ll_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ShareImagesUtil.onShareOther((AlbumNewActivity) getContext(), null, false, imageparms.getValue3());
            }
        });

        ll_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ShareImagesUtil.onShareFacebook((AlbumNewActivity) getContext(), null, false, imageparms.getValue3());
            }
        });

        ll_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupWindow.dismiss();
                ShareImagesUtil.onShareTwitter((AlbumNewActivity) getContext(), null, false, imageparms.getValue3());
            }
        });

        ll_us_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ShareImagesUtil.onShareOther((AlbumNewActivity) getContext(), null, false, imageparms.getValue3());
            }
        });


        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        // setOutsideTouchable设置生效的前提是setTouchable(true)和setFocusable(false)
        popupWindow.setOutsideTouchable(true);

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
        popupWindow.showAsDropDown(anchorView);
        return popupWindow;
    }

    private void autoAdjustArrowPos(PopupWindow popupWindow, View contentView, View anchorView) {
        ImageView downArrow = contentView.findViewById(R.id.ai_arrow);
        downArrow.setVisibility(VISIBLE);

        ImageView upArrow = contentView.findViewById(R.id.down_arrow);
        upArrow.setVisibility(GONE);

        downArrow.setImageResource(R.drawable.popup_window_arrow_down);

        int pos[] = new int[2];
        contentView.getLocationOnScreen(pos);
        int popLeftPos = pos[0];
        anchorView.getLocationOnScreen(pos);
        int anchorLeftPos = pos[0];
        int arrowLeftMargin = anchorLeftPos - popLeftPos + anchorView.getWidth() / 2 - downArrow.getWidth() / 2;
//        upArrow.setVisibility(popupWindow.isAboveAnchor() ? View.INVISIBLE : View.VISIBLE);
//        downArrow.setVisibility(popupWindow.isAboveAnchor() ? View.VISIBLE : View.INVISIBLE);

//        RelativeLayout.LayoutParams upArrowParams = (RelativeLayout.LayoutParams) upArrow.getLayoutParams();
//        upArrowParams.leftMargin = arrowLeftMargin;
        RelativeLayout.LayoutParams downArrowParams = (RelativeLayout.LayoutParams) downArrow.getLayoutParams();
        downArrowParams.leftMargin = arrowLeftMargin;
        downArrow.setLayoutParams(downArrowParams);
    }


    public boolean isInfoShow() {
        return imageparmsNewView.getVisibility() == VISIBLE;
    }

    public void closeInfoView() {
        imageparmsNewView.outAnimationStart();
        imageparmsNewView.closeView();
    }


}