package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnLayoutInflatedListener;
import com.app.hubert.guide.model.GuidePage;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.bean.CameraParm;
import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog3;
import com.bcnetech.bcnetechlibrary.dialog.EditDialog2;
import com.bcnetech.bcnetechlibrary.drawable.CircleDefultDrawable;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.EventStatisticsUtil;
import com.bcnetech.bcnetechlibrary.util.TimeUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.EventCommon;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.PresetParmsSqlControl;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.task.manage.UploadManager;
import com.bcnetech.hyphoto.ui.activity.AlbumNewActivity;
import com.bcnetech.hyphoto.ui.adapter.AutoLocationAdapter;
import com.bcnetech.hyphoto.ui.adapter.GridViewRationAdapter;
import com.bcnetech.hyphoto.ui.adapter.SceneRationAdapter;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.FontImageUtil;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.PinyUtil;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.bcnetech.bizcamerlibrary.camera.utils.CameraUtils;
import com.bcnetech.bluetoothlibarary.bluetoothagreement.CommendManage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.gpumanage.GPUImageFilterTools;

/**
 * Created by a1234 on 16/9/22.
 */

public class ImageparmsNewView extends RelativeLayout {
    private final static int PAD = 15;
    private final static int PADTB = 5;
    private final static int TEXT_SIZE = 14;
    public final static int SHOW = 1;
    public final static int CLOSE = 2;
    private int type = CLOSE;
    private int hight;

    private TextView pic_info;

    private TextView tv_day;
    private TextView tv_size;
    private TextView tv_phone;
    private TextView tv_cobox;
    private TextView tv_preset;

    private TextView tv_day2;
    private TextView tv_size2;
    private TextView tv_phone2;
    private TextView tv_cobox2;
    private TextView tv_preset2;

    private TextView tv_presetParam;

    private AutoLocationAdapter autoLocationAdapter;
    private AutoLocateHorizontalView autoLocateHorizontalView;
    private RelativeLayout rl_main;

    private boolean haveLightRation = true;
    private boolean isnotShowparms = true;
    // private TextView tv_preparms;
    private List<PictureProcessingData> processtools;

    private TextView tv_coboxType2;
    private TextView tv_yingjian2;

    private String COBOX_SMART = "4096";

    private ImageData imagedata;
    private Activity mactivity;

    private ImageparmsViewListener imageparmsViewListener;
    private ExecutorService cachedThreadPool;
    private Handler handler;
    private GPUImage gpuImage;
    private Bitmap changBitmap;
    private ImageView iv_iamge;
    private int startPosition;
    private RelativeLayout rl_photo;
    private ImageView iv_bg;
    private LinearLayout ll_main;

    private PresetParm presetParm;
    private LinearLayout ll_bg;
    private ScrollView scrollView;
    private TextView tv_presetDelete;
    private TextView tv_presetDown;
    private ImageView iv_down;
    private TextView tv_type;
    private ImageView iv_video;
    private ShowFake3DView imageparams_showFake3d;

    public static final int TYPE_DOWN = 0;
    public static final int TYPE_DELETE = 1;

    private PresetParmsSqlControl presetParmsSqlControl;

    private List<PictureProcessingData> histortList;
    private TitleView parms_title;
    private ImageView iv_imageTwo;
    private EditDialog2 dialog2;
    private RelativeLayout rl_preset;
    private RelativeLayout rl_origin;
    private RelativeLayout rl_bg;
    private TextView tv_imgType;
    private UploadManager uploadManager;


    public ImageparmsNewView(Context context) {
        super(context);
        init();
    }

    public ImageparmsNewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageparmsNewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {

        /* ImageparmsView.this.setVerticalScrollBarEnabled(false);*/
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.image_edit_parms_new_pop, null);
        this.addView(view);


        tv_day = (TextView) view.findViewById(R.id.tv_day);
        tv_size = (TextView) view.findViewById(R.id.tv_size);
        tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        tv_cobox = (TextView) view.findViewById(R.id.tv_cobox);
        tv_preset = (TextView) view.findViewById(R.id.tv_preset);

        tv_day2 = (TextView) view.findViewById(R.id.tv_day2);
        tv_size2 = (TextView) view.findViewById(R.id.tv_size2);
        tv_phone2 = (TextView) view.findViewById(R.id.tv_phone2);
        tv_cobox2 = (TextView) view.findViewById(R.id.tv_cobox2);
        tv_preset2 = (TextView) view.findViewById(R.id.tv_preset2);
        iv_iamge = (ImageView) findViewById(R.id.iv_image);
        iv_imageTwo = (ImageView) findViewById(R.id.iv_imageTwo);
        rl_main = (RelativeLayout) findViewById(R.id.rl_main);
        tv_presetParam = (TextView) findViewById(R.id.tv_presetParam);

        autoLocateHorizontalView = (AutoLocateHorizontalView) findViewById(R.id.auto_location);
        rl_photo = (RelativeLayout) findViewById(R.id.rl_photo);
        iv_bg = (ImageView) findViewById(R.id.iv_bg);
        ll_main = (LinearLayout) findViewById(R.id.ll_main);
        ll_bg = (LinearLayout) findViewById(R.id.ll_bg);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        pic_info = (TextView) findViewById(R.id.pic_info);
        tv_presetDelete = (TextView) findViewById(R.id.tv_presetDelete);
        tv_presetDown = (TextView) findViewById(R.id.tv_presetDown);
        iv_down = (ImageView) findViewById(R.id.iv_down);
        tv_type = (TextView) findViewById(R.id.tv_type);
        parms_title = (TitleView) findViewById(R.id.parms_title);
        rl_preset = (RelativeLayout) view.findViewById(R.id.rl_preset);
        iv_video = (ImageView) view.findViewById(R.id.iv_video);
        rl_origin = (RelativeLayout) view.findViewById(R.id.rl_origin);
        rl_bg = (RelativeLayout) view.findViewById(R.id.rl_bg);
        tv_imgType = (TextView) view.findViewById(R.id.tv_imgType);
        imageparams_showFake3d = view.findViewById(R.id.imageparms_showfake3d);

        initDatas();

    }

    /**
     * 是否展示预设参数
     *
     * @param isshow
     */
    public void showpresetParms(boolean isshow) {
        if (isshow) {
            tv_preset.setVisibility(VISIBLE);
            tv_preset2.setVisibility(VISIBLE);


        } else {
            tv_preset.setVisibility(GONE);
            tv_preset2.setVisibility(GONE);
        }
    }

    public void setImgHead(String author, ImageView view) {
        if (author != null && !TextUtils.isEmpty(author)) {
            String f = PinyUtil.getSpells(author);
            view.setImageDrawable(new CircleDefultDrawable(FontImageUtil.setDefaultColor(getContext(), f), author.charAt(0) + ""));
        } else {
            view.setImageDrawable(new CircleDefultDrawable(FontImageUtil.setDefaultColor(getContext(), "匿"), "匿"));
        }
    }

    private void initDatas() {
        cachedThreadPool = Executors.newCachedThreadPool();
        parms_title.setType(TitleView.IMAGE_PARAMS_NEW_VIEW);
        initBottomAnim(ImageparmsNewView.this, ContentUtil.getScreenHeight(getContext()));

        onViewClicks();
        handler = new Handler();

    }

    private void onViewClicks() {

        parms_title.setLeftListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageparmsViewListener.onbtnclose();
                refresh();
            }
        }).setRightText(getContext().getString(R.string.send_contribute)).setRightTextListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == dialog2) {
                    dialog2 = new EditDialog2(getContext());
                }
                dialog2.setClickInterFace(new EditDialog2.ClickInterFace() {
                    @Override
                    public void ok(String text) {
                        uploadClick(text, 2);
                        dialog2.diaglogDismiss();
                    }

                    @Override
                    public void cencel() {
                        dialog2.diaglogDismiss();
                    }
                });
                dialog2.show();
                dialog2.setOkText(getContext().getString(R.string.send_contribute));
            }
        }).setRightTextIsShow(false);

        /**
         * 滑到底部
         */
        iv_down.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });


        /**
         * 关闭页面
         */
        iv_bg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageparmsViewListener.onbtnclose();
                refresh();
            }
        });


        /**
         * 分享
         */
        tv_presetParam.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == dialog2) {
                    dialog2 = new EditDialog2(getContext());
                }
                dialog2.setClickInterFace(new EditDialog2.ClickInterFace() {
                    @Override
                    public void ok(String text) {
                        uploadClick(text, 1);
                        dialog2.diaglogDismiss();
                    }

                    @Override
                    public void cencel() {
                        dialog2.dismiss();
                    }
                });

                dialog2.show();
                dialog2.setOkText(getResources().getString(R.string.save));
//                imageparmsViewListener.sharePreset();
            }
        });

        /**
         * 下载预设 参数
         */
        tv_presetDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageparmsViewListener.downLoadPreset();
            }
        });

        /**
         * 删除预设参数
         */
        tv_presetDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageparmsViewListener.deletePreset();
            }
        });
    }


    public void setData(List<PictureProcessingData> list, int position, int startPosition, int type) {

        if (type == Flag.TYPE_VIDEO) {
            autoLocateHorizontalView.setVisibility(GONE);
            ll_bg.setVisibility(GONE);
            iv_video.setVisibility(VISIBLE);
            iv_imageTwo.setAlpha(0f);
            imageparams_showFake3d.setVisibility(GONE);
            Picasso.get().load(imagedata.getSmallLocalUrl()).resize(ImageUtil.Dp2Px(getContext(), 278), ImageUtil.Dp2Px(getContext(), 278)).centerInside().into(iv_iamge);
            rl_origin.setVisibility(GONE);
        } else if (type == Flag.TYPE_AI360) {
            autoLocateHorizontalView.setVisibility(GONE);
            ll_bg.setVisibility(GONE);
            iv_video.setVisibility(GONE);
            iv_imageTwo.setAlpha(0f);
            imageparams_showFake3d.setVisibility(VISIBLE);
            imageparams_showFake3d.setFolderUrl(imagedata.getLocalUrl());
            rl_origin.setVisibility(GONE);
        } else {
            this.processtools = list;
            this.startPosition = startPosition;
            if (gpuImage == null) {
                gpuImage = new GPUImage(getContext());
            }
            iv_video.setVisibility(GONE);
            imageparams_showFake3d.setVisibility(GONE);
            if (list.size() == 1) {
                autoLocateHorizontalView.setVisibility(GONE);
                ll_bg.setVisibility(GONE);
                if (processtools.get(0).getType() != BizImageMangage.SRC_PRESET_PARMS) {
                    Picasso.get().load(processtools.get(list.size() - 1).getImageUrl()).resize(ImageUtil.Dp2Px(getContext(), 278), ImageUtil.Dp2Px(getContext(), 278)).centerInside().into(iv_iamge);
                }
                iv_imageTwo.setAlpha(0f);
                rl_origin.setVisibility(GONE);
            } else {
                autoLocateHorizontalView.setVisibility(VISIBLE);
                ll_bg.setVisibility(VISIBLE);
                rl_origin.setVisibility(VISIBLE);

                autoLocationAdapter = new AutoLocationAdapter(getContext(), list, AutoLocationAdapter.LOCAK_PARAM);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                autoLocateHorizontalView.setLayoutManager(linearLayoutManager);
                autoLocateHorizontalView.setOnSelectedPositionChangedListener(new AutoLocateHorizontalView.OnSelectedPositionChangedListener() {
                    @Override
                    public void selectedPositionChanged(final int pos) {
                        imagedata.setCurrentPosition(pos);
                    }

                    @Override
                    public void scrolledPositionChanged(final int mainPos, final int targetPos, float mainPercent, float targetPercent, int selectPos, float selectPercent) {

                        if (selectPos == processtools.size() - 1) {
                            if (rl_origin.getAlpha() != 1f) {
                                rl_origin.setAlpha(1f);
                            }
                        } else {
                            if (selectPercent - (int) selectPercent > 0.9f || selectPercent - (int) selectPercent == 0f) {
                                if (rl_origin.getAlpha() != 1f) {
                                    rl_origin.setAlpha(1f);
                                }
                            } else {
                                if (rl_origin.getAlpha() != 0f) {
                                    rl_origin.setAlpha(0f);
                                }
                            }
                        }
                        showHint(selectPos);

                        showPic(mainPos, targetPos);
                        iv_iamge.setAlpha(mainPercent);
                        iv_imageTwo.setAlpha(targetPercent);


                    }
                });
                autoLocateHorizontalView.setInitPos(position);
                autoLocateHorizontalView.setAdapter(autoLocationAdapter);
            }


        }
    }

    public void closeView() {
        imageparmsViewListener.onbtnclose();
        refresh();
        gpuImage = null;
        //  imagedata = null;
        presetParm = null;
        if (changBitmap != null) {
            changBitmap.recycle();
        }
        if (processtools != null) {
            processtools.clear();
        }
        if (histortList != null) {
            histortList.clear();
        }
    }

    private int selectPositon;

    public void setDataPreset(List<PictureProcessingData> list, int position) {

        this.processtools = list;
        if (gpuImage == null) {
            gpuImage = new GPUImage(getContext());
        }


        //判断之前历史记录是否存在
        if (StringUtil.isBlank(list.get(list.size() - 1).getImageData())) {
            autoLocateHorizontalView.setVisibility(GONE);
            ll_bg.setVisibility(GONE);

            if (!StringUtil.isBlank(presetParm.getCoverId()) && presetParm.getCoverId().equals("default")) {
                Picasso.get().load(R.drawable.preset_default).into(iv_iamge);
            } else if (!StringUtil.isBlank(presetParm.getCoverId()) && presetParm.getCoverId().equals("default2")) {
                Picasso.get().load(R.drawable.preset_default_two).into(iv_iamge);
            } else if (!StringUtil.isBlank(presetParm.getCoverId()) && presetParm.getCoverId().equals("default3")) {
                Picasso.get().load(R.drawable.preset_default_three).into(iv_iamge);
            } else {
                Picasso.get().load(presetParm.getTextSrc()).into(iv_iamge);
            }
            rl_origin.setVisibility(GONE);
            autoLocateHorizontalView.setOnSelectedPositionChangedListener(null);
        } else {
            if (list.size() == 1) {
                autoLocateHorizontalView.setVisibility(GONE);
                ll_bg.setVisibility(GONE);
                rl_origin.setVisibility(GONE);
            } else {
                autoLocateHorizontalView.setVisibility(VISIBLE);
                ll_bg.setVisibility(VISIBLE);
            }
            autoLocationAdapter = new AutoLocationAdapter(getContext(), list, AutoLocationAdapter.PRESET_PARAM);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            autoLocateHorizontalView.setLayoutManager(linearLayoutManager);
            autoLocateHorizontalView.setOnSelectedPositionChangedListener(new AutoLocateHorizontalView.OnSelectedPositionChangedListener() {
                @Override
                public void selectedPositionChanged(final int pos) {

                }

                @Override
                public void scrolledPositionChanged(final int mainPos, final int targetPos, final float mainPercent, final float targetPercent, int selectPos, float selectPercent) {

                    if (selectPos == processtools.size() - 1) {
                        if (rl_origin.getAlpha() != 1f) {
                            rl_origin.setAlpha(1f);
                        }
                    } else {
                        if (selectPercent - (int) selectPercent > 0.9f || selectPercent - (int) selectPercent == 0f) {
                            if (rl_origin.getAlpha() != 1f) {
                                rl_origin.setAlpha(1f);
                            }
                        } else {
                            if (rl_origin.getAlpha() != 0f) {
                                rl_origin.setAlpha(0f);
                            }
                        }
                    }
                    showHint(selectPos);

                    if (mainPos != selectPositon) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final Bitmap bitmap = BitmapUtils.convertStringToIcon(processtools.get(mainPos).getImageData());
                                final Bitmap bitmapTwo = BitmapUtils.convertStringToIcon(processtools.get(targetPos).getImageData());

//                            changBitmap = getCurrentImgPreset(bitmap, pos);

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
//                                        dissmissDialog();
                                        if (processtools.size() % 2 == 0) {
                                            if (mainPos % 2 == 0) {
                                                iv_iamge.setImageBitmap(bitmapTwo);
                                                iv_imageTwo.setImageBitmap(bitmap);
                                            } else {
                                                iv_iamge.setImageBitmap(bitmap);
                                                iv_imageTwo.setImageBitmap(bitmapTwo);
                                            }
                                        } else {
                                            if (mainPos % 2 == 0) {
                                                iv_iamge.setImageBitmap(bitmap);
                                                iv_imageTwo.setImageBitmap(bitmapTwo);
                                            } else {
                                                iv_iamge.setImageBitmap(bitmapTwo);
                                                iv_imageTwo.setImageBitmap(bitmap);
                                            }
                                        }

                                    }
                                });

//                            changBitmap = getCurrentImgPreset(bitmap, pos);

//                                        dissmissDialog();

                            }
                        }).start();
                    }
                    selectPositon = mainPos;
                    if (processtools.size() % 2 == 0) {
                        if (mainPos % 2 == 0) {
                            iv_iamge.setAlpha(targetPercent);
                            iv_imageTwo.setAlpha(mainPercent);
                        } else {
                            iv_iamge.setAlpha(mainPercent);
                            iv_imageTwo.setAlpha(targetPercent);
                        }
                    } else {
                        if (mainPos % 2 == 0) {
                            iv_iamge.setAlpha(mainPercent);
                            iv_imageTwo.setAlpha(targetPercent);
                        } else {
                            iv_iamge.setAlpha(targetPercent);
                            iv_imageTwo.setAlpha(mainPercent);
                        }
                    }


                }
            });
            autoLocateHorizontalView.setInitPos(position);
            autoLocateHorizontalView.setAdapter(autoLocationAdapter);

        }

    }


    /**
     * 传入参数
     */
    public void setparmsPreset(PresetParm presetParm, int type) {
        if (presetParm != null) {
            this.presetParm = presetParm;

//            parm = imagedata.getPresetParms();
//            String url = imagedata.getLocalUrl();
            try {
                String day = new SimpleDateFormat(getResources().getString(R.string.format_date_no_year)).format(presetParm.getTimeStamp());
                String day2 = new SimpleDateFormat("a hh:mm").format(presetParm.getTimeStamp());
                String week = TimeUtil.getWeek(new SimpleDateFormat("yyyy-MM-dd").format(presetParm.getTimeStamp()));
                tv_day.setText(day);
                tv_day2.setText(week + "  " + day2);
            } catch (Exception e) {
                tv_day.setText("");
                tv_day2.setText("");
            }
            String temp[] = presetParm.getTextSrc().replaceAll("\\\\", "/").split("/");
            String fileName = "";

            if (temp.length > 1) {
                String temp2[] = temp[temp.length - 1].split("\\?");
                if (temp2.length > 1) {
                    fileName = temp2[0];
                } else {
                    fileName = temp[temp.length - 1];
                }
            }
            tv_size.setText(fileName);
            tv_phone.setText(presetParm.getEquipment());
            if (null != presetParm.getCameraParm()) {
                setCameraData(presetParm.getCameraParm());
            } else {
                tv_phone2.setVisibility(GONE);
            }


            if (type == TYPE_DELETE) {
                if ("1".equals(presetParm.getId())) {
                    tv_presetDelete.setVisibility(GONE);
                } else if ("2".equals(presetParm.getId())) {
                    tv_presetDelete.setVisibility(GONE);
                } else if ("3".equals(presetParm.getId())) {
                    tv_presetDelete.setVisibility(GONE);
                } else {
                    tv_presetDelete.setVisibility(VISIBLE);
                }


            } else if (type == TYPE_DOWN) {
                tv_presetDown.setVisibility(VISIBLE);
            }


            tv_presetParam.setVisibility(GONE);
            if (null != presetParm.getLightRatioData()) {
                parms_title.setTitleText(presetParm.getName());
                tv_preset2.setText("@" + presetParm.getAuther());
                getLightRatioData(presetParm.getLightRatioData());
            }
//            isCanShare(imagedata);
//            isHavePresetParms(imagedata);
        }

    }

    /**
     * 传入参数
     */
    public void setparms(ImageData imagedata) {
        if (imagedata != null) {
            this.imagedata = imagedata;
            this.startPosition = imagedata.getCurrentPosition();
            if (imagedata.getType() == Flag.TYPE_VIDEO) {
                pic_info.setText(getResources().getString(R.string.videoinfo));
                tv_type.setText(getResources().getString(R.string.video));

            } else {
//                rl_duration.setVisibility(GONE);
                pic_info.setText(getResources().getString(R.string.picinfo));
                tv_type.setText(getResources().getString(R.string.image_parms));
//                params_textView.setText(getResources().getString(R.string.pic_params_info));
            }

            try {
                String day = new SimpleDateFormat(getResources().getString(R.string.format_date_no_year)).format(imagedata.getTimeStamp());
                String day2 = new SimpleDateFormat("a hh:mm").format(imagedata.getTimeStamp());
                String week = TimeUtil.getWeek(new SimpleDateFormat("yyyy-MM-dd").format(imagedata.getTimeStamp()));
                tv_day.setText(day);
                tv_day2.setText(week + "  " + day2);
            } catch (Exception e) {
                tv_day.setText("");
                tv_day2.setText("");
            }
            String temp[] = imagedata.getLocalUrl().replaceAll("\\\\", "/").split("/");
            String fileName = "";
            if (temp.length > 1) {
                fileName = temp[temp.length - 1];
            }
            tv_size.setText(fileName);
            tv_phone.setText(getDeviceName());
            if (null != imagedata.getCameraParm()) {
                setCameraData(imagedata.getCameraParm());
            } else {
                tv_phone2.setVisibility(GONE);
            }

            if (null != imagedata.getLightRatioData()) {
                tv_preset2.setText("@" + LoginedUser.getLoginedUser().getNickname());
                getLightRatioData(imagedata.getLightRatioData());
            }

            if (imagedata.getLocalUrl().contains(".png")) {
                rl_bg.setBackground(getContext().getResources().getDrawable(R.drawable.bgbitmap2));
            } else {
                rl_bg.setBackground(getContext().getResources().getDrawable(R.color.black));

            }

            isCanShare(imagedata);

        }
    }

    private void setCameraData(CameraParm cameraData) {
        if (null == cameraData)
            return;
        StringBuilder stb = new StringBuilder();
        if (null != cameraData.getIso()) {
            if (!stb.toString().isEmpty()) {
                stb.append(" ");
            }
            stb.append("ISO" + cameraData.getIso());
        }

        if (null != cameraData.getSec()) {
            if (!stb.toString().isEmpty()) {
                stb.append(" ");
            }
            //Camera2返回数据为long长度类型，Camera1返回数据为Double长度类型
            try {
                stb.append(CameraUtils.getExposureTimeString(Long.parseLong(cameraData.getSec())));
            } catch (NumberFormatException e) {
                //#0.00
                stb.append(FileUtil.formatSize((Double.parseDouble(cameraData.getSec())), FileUtil.SIZETYPE_00));
            }
        }

        if (null != cameraData.getFocalLength()) {
            if (!stb.toString().isEmpty()) {
                stb.append(" ");
            }
            //#0.00
            stb.append(FileUtil.formatSize((Double.parseDouble(cameraData.getFocalLength())), FileUtil.SIZETYPE_00) + "mm");
        }
        tv_phone2.setVisibility(VISIBLE);
        tv_phone2.setText(stb.toString());
    }

    public List<Integer> getNewList(List<Integer> li) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < li.size(); i++) {
            int str = li.get(i);  //获取传入集合对象的每一个元素
            if (!list.contains(str)) {   //查看新集合中是否有指定的元素，如果没有则加入
                list.add(str);
            }
        }
        return list;  //返回集合
    }

    private void addCameraParm(ArrayList<SceneRationAdapter.Name> list, String name, String ration) {
        if (null != ration) {
            SceneRationAdapter.Name rationName = new SceneRationAdapter.Name();
            rationName.setName(name);
            rationName.setRation(ration);

            list.add(rationName);
        }
    }

    /**
     * 获取系统名称
     *
     * @return
     */
    public String getDeviceName() {
        //TelephonyManager tm = (TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String mtype = android.os.Build.MODEL; // 手机型号
//

        return mtype;
    }

    private TextView initTextView(String name, int backgroud) {
        TextView textView = new TextView(getContext());
        textView.setText(name);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(ContentUtil.dip2px(getContext(), PAD), ContentUtil.dip2px(getContext(), PADTB), ContentUtil.dip2px(getContext(), PAD), ContentUtil.dip2px(getContext(), PADTB));
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTextSize(TEXT_SIZE);
        textView.setBackgroundResource(backgroud);
        return textView;
    }


    public void setType(int type) {
        this.type = type;
    }


    public interface ImageparmsViewListener {
        void onImageparmsView();

        void onPresetParms();

        void onNoChange(boolean nochange);

        void onbtnclose();

        void downLoadPreset();

        void deletePreset();

        void sharePreset();
    }

    public void setImageparmsViewListener(ImageparmsViewListener imageparmsViewListener) {
        this.imageparmsViewListener = imageparmsViewListener;

    }

    /**
     * 获取图片尺寸
     *
     * @param width
     * @param height
     */
    public void getSize(int width, int height, String fileUrl) {
        fileUrl = fileUrl.contains("file:/") ? fileUrl.substring(7) : fileUrl;
        String size = FileUtil.getFileOrFilesSize(fileUrl, FileUtil.SIZETYPE_MB);
        tv_size2.setText(width + "x" + height + " " + size + "MB");

    }


    /**
     * 获取图片尺寸
     *
     * @param width
     * @param height
     */
    public void setSize(int width, int height, String size) {
//        double size = FileUtil.getFileOrFilesSize(fileUrl.substring(7), 3);
        tv_size2.setText(width + "x" + height + " " + size);

    }

    private void setLIghtRation(ArrayList<GridViewRationAdapter.Name> list, LightRatioData data, String name, String num) {
        if (!num.equals("-1") && !num.equals("")) {
            GridViewRationAdapter.Name n1 = new GridViewRationAdapter.Name();
            n1.setName(name);
            list.add(n1);
            n1.setNum(num);
        }
    }

    public void getLightRatioData(LightRatioData data) {
        if (data != null) {
            if (!StringUtil.isBlank(BizImageMangage.getCoboxName(String.valueOf(data.getVersion()))) && !data.getVersion().equals("0")) {
                rl_preset.setVisibility(VISIBLE);
                String cobox = BizImageMangage.getCoboxName(String.valueOf(data.getVersion()));
                if (cobox.equals("COLINK")) {
                    tv_cobox.setText("COLINK");
                } else {
                    tv_cobox.setText("COBOX");
                }
                tv_cobox2.setText(getResources().getString(R.string.shooting_with1) + cobox + getResources().getString(R.string.shooting_with2));
            }
        }
    }

    /**
     * 判断是否可以分享参数
     * 预设参数为空
     * 存在光比
     */
    public boolean isCanShare(ImageData imageparms) {
        boolean supportC2 = LoginedUser.getLoginedUser().isSupportCamera2();
        tv_presetParam.setVisibility(GONE);
        parms_title.setRightTextIsShow(false);
        rl_preset.setVisibility(GONE);
        if (null == imageparms)
            return false;
        if (!supportC2) {
            //Camera1：连接设备且做过全局调整
            if (null != imageparms.getImageTools() && imageparms.getImageTools().size() > 0 && isHaveLightRation(imageparms) && imageparms.getType() == Flag.TYPE_PIC) {
                tv_presetParam.setVisibility(VISIBLE);
                parms_title.setRightTextIsShow(true);
                rl_preset.setVisibility(VISIBLE);
                return true;
            }
            /*else {
                if (null != imageparms && null != imageparms.getImageTools()) {
                    if (imageparms.getImageTools().size() > 0) {
                        tv_presetParam.setVisibility(VISIBLE);
                    }
                }
            }*/
        } else {
            //Camera2：连接设备
            if (isHaveLightRation(imageparms) && imageparms.getType() == Flag.TYPE_PIC) {
                tv_presetParam.setVisibility(VISIBLE);
                parms_title.setRightTextIsShow(true);
                rl_preset.setVisibility(VISIBLE);
                return true;

            }
        }
        return false;
    }

    private boolean isHaveLightRation(ImageData imageData) {
        if (imageData.getPresetParms() == null) {
            if (imageData.getLightRatioData() != null) {
                if (imageData.getLightRatioData().getVersion() != null && !imageData.getLightRatioData().getVersion().equals(CommendManage.VERSION_BOX + "")) {
                    if (imageData.getLightRatioData().getLeftLight() != -1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断是否有预设参数
     *
     * @return
     */
    private void isHavePresetParms(ImageData imagedata) {
        PresetParm presetParm = imagedata.getPresetParms();//预设参数
        if (presetParm != null) {
            if (presetParm.getTimeStamp() != 0) {
                showpresetParms(true);
            } else {
                showpresetParms(false);
            }
        } else {
            showpresetParms(false);
        }
    }

//    public void setVideotime(String time) {
//        if (time != null) {
//            rl_duration.setVisibility(VISIBLE);
//            tv_duration.setText(time);
//        }
//    }


    /**
     * @param imageData
     * @return
     */
    private List<Integer> getLightRation(ImageData imageData) {
        LightRatioData lightRatioData = imageData.getLightRatioData();
        if (lightRatioData != null) {
            int leftlight = lightRatioData.getLeftLight();
            int rightlight = lightRatioData.getRightLight();
            int backlight = lightRatioData.getBackgroudLight();
            int bottomlight = lightRatioData.getBottomLight();
            int movelight = lightRatioData.getMoveLight();
            int movelight2 = lightRatioData.getTopLight();
            int rpm = lightRatioData.getLight1();
            List<Integer> lightrations = new ArrayList<Integer>();
            lightrations.add(leftlight);
            lightrations.add(rightlight);
            lightrations.add(backlight);
            lightrations.add(bottomlight);
            lightrations.add(movelight);
            lightrations.add(movelight2);
            lightrations.add(rpm);
            return lightrations;
        }
        return null;
    }

    private void setVideoType(ImageData imagedata) {
        getRecodeTime(imagedata);
    }

    private void getRecodeTime(ImageData imageData) {
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(imageData.getLocalUrl());
        String duration = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 视频时间

        String showtime = ShowTime(Double.valueOf(duration));
//        setVideotime(showtime + "s");
    }

    //时间显示函数,我们获得音乐信息的是以毫秒为单位的，把把转换成我们熟悉的00:00格式
    public String ShowTime(double time) {
        time /= 1000;
        time = Math.ceil(time);
        double minute = time / 60;
        double hour = minute / 60;
        double second = time % 60;
        minute %= 60;
        int min = (int) minute;
        int sec = (int) second;
        return String.format("%02d:%02d", min, sec);
    }

    /**
     * 设置当前添加参数调整后的图片
     */
    public Bitmap getCurrentImg(Bitmap bitmap, int pos) {
        List<PictureProcessingData> imageToolLists = null;
        if (imagedata.getCurrentPosition() == processtools.size() - 1) {
            if (imagedata.getPresetParms() != null) {
                imageToolLists = imagedata.getPresetParms().getParmlists();
            }

        } else {
            imageToolLists = imagedata.getImageTools();
        }
        return filterToBitmap(imageToolLists, bitmap, pos);
    }

    /**
     * 设置当前添加参数调整后的图片
     */
    private Bitmap getCurrentImgPreset(Bitmap bitmap, int pos) {
        List<PictureProcessingData> imageToolLists = null;
        if (pos == processtools.size() - 1) {
            imageToolLists = null;

        } else {
            imageToolLists = presetParm.getParmlists();
        }
        return filterToBitmapPreset(imageToolLists, bitmap, pos);
    }


    /**
     * 更具传入的filter  获取bitamp
     *
     * @param imageToolLists
     * @param bitmap
     * @return
     */
    private Bitmap filterToBitmap(List<PictureProcessingData> imageToolLists, Bitmap bitmap, int pos) {
        List<GPUImageFilter> gpuImageFilters = new ArrayList<>();

        if (processtools.get(pos).getType() == BizImageMangage.PARMS) {
            for (int i = 0; imageToolLists != null && i < imageToolLists.size(); i++) {
                GPUImageFilter mFilter;
                GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
                mFilter = BizImageMangage.getInstance().getGPUFilterforType(getContext(), imageToolLists.get(i).getType());
                gpuImageFilters.add(mFilter);
                mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
                if (imageToolLists.get(i).getType() != BizImageMangage.WHITE_BALANCE) {
                    mFilterAdjuster.adjust(imageToolLists.get(i).getNum());
                }
            }
        } else {
            for (int i = 0; imageToolLists != null && i < imageToolLists.size(); i++) {
                GPUImageFilter mFilter;
                GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
                mFilter = BizImageMangage.getInstance().getGPUFilterforType(getContext(), imageToolLists.get(i).getType());
                gpuImageFilters.add(mFilter);
                mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
                if (imageToolLists.get(i).getType() == BizImageMangage.WHITE_BALANCE) {
                    mFilterAdjuster.adjust(imageToolLists.get(i).getNum(), imageToolLists.get(i).getTintNum());
                } else {
                    mFilterAdjuster.adjust(imageToolLists.get(i).getNum());
                }
            }
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


    /**
     * 更具传入的filter  获取bitamp
     *
     * @param imageToolLists
     * @param bitmap
     * @return
     */
    private Bitmap filterToBitmapPreset(List<PictureProcessingData> imageToolLists, Bitmap bitmap, int pos) {
        List<GPUImageFilter> gpuImageFilters = new ArrayList<>();
        if (processtools.get(pos).getType() == BizImageMangage.PARMS) {
            for (int i = 0; imageToolLists != null && i < imageToolLists.size(); i++) {
                GPUImageFilter mFilter;
                GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
                mFilter = BizImageMangage.getInstance().getGPUFilterforType(getContext(), imageToolLists.get(i).getType());
                gpuImageFilters.add(mFilter);
                mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
                if (imageToolLists.get(i).getType() != BizImageMangage.WHITE_BALANCE) {
                    mFilterAdjuster.adjust(imageToolLists.get(i).getNum());
                }
//            mFilterAdjuster.adjust(imageToolLists.get(i).getNum());
            }
        } else {
            for (int i = 0; imageToolLists != null && i < imageToolLists.size(); i++) {
                GPUImageFilter mFilter;
                GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
                mFilter = BizImageMangage.getInstance().getGPUFilterforType(getContext(), imageToolLists.get(i).getType());
                gpuImageFilters.add(mFilter);
                mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
                if (imageToolLists.get(i).getType() == BizImageMangage.WHITE_BALANCE) {
                    mFilterAdjuster.adjust(imageToolLists.get(i).getNum(), imageToolLists.get(i).getTintNum());
                } else {
                    mFilterAdjuster.adjust(imageToolLists.get(i).getNum());
                }
//            mFilterAdjuster.adjust(imageToolLists.get(i).getNum());
            }
        }

        GPUImageFilter currentFilter;
        if (gpuImageFilters == null || gpuImageFilters.size() == 0) {
            currentFilter = new GPUImageFilter();
        } else {
            currentFilter = new GPUImageFilterGroup(gpuImageFilters);
        }
        if (null != gpuImage) {

        }
        gpuImage.setFilter(currentFilter);
        return gpuImage.getBitmapWithFilterApplied(bitmap);
    }


    public void refresh() {

        outAnimationStart();
        if (null != gpuImage) {
            gpuImage = null;
        }
        if (null != processtools) {
            processtools = null;
        }
        if (null != imagedata) {
            imagedata.setCurrentPosition(startPosition);
        }
        if (null != histortList) {
            histortList = null;
        }
        if (null != changBitmap) {
            changBitmap = null;
        }
        selectPositon = 0;
        iv_iamge.setAlpha(1f);
        iv_imageTwo.setAlpha(0f);
        iv_iamge.setImageDrawable(null);
        iv_imageTwo.setImageDrawable(null);

        scrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    public void searchForPrsetId(String presetId, Activity activity) {
        presetParmsSqlControl = new PresetParmsSqlControl(activity);
        presetParmsSqlControl.startByFileId(presetId);
        presetParmsSqlControl.setListener(new BaseSqlControl.SqlControlListener() {
            @Override
            public void queryListener(Object... parms) {
                PresetParm presetParm = (PresetParm) parms[0];

                if (histortList == null) {
                    histortList = new ArrayList<>();
                }
                histortList.clear();
                //原图
                PictureProcessingData srcPicData = new PictureProcessingData();
                PictureProcessingData paramPicData = null;
                if (null != presetParm.getPartParmlists()) {


                    for (int i = 0; i < presetParm.getPartParmlists().size(); i++) {
                        PictureProcessingData pictureProcessingData = presetParm.getPartParmlists().get(i);
                        if (pictureProcessingData.getType() == BizImageMangage.SRC) {
                            srcPicData = presetParm.getPartParmlists().get(i);
                        } else if (pictureProcessingData.getType() == BizImageMangage.PARMS) {
                            paramPicData = presetParm.getPartParmlists().get(i);
                        } else {
                            histortList.add(presetParm.getPartParmlists().get(i));
                        }
                    }
                    Collections.reverse(histortList);
                }
                if (null != paramPicData) {
                    histortList.add(new PictureProcessingData(BizImageMangage.PARMS, paramPicData.getImageUrl(), paramPicData.getImageData()));
                }
                histortList.add(new PictureProcessingData(BizImageMangage.SRC, srcPicData.getImageUrl(), srcPicData.getImageData()));
                setDataPreset(histortList, histortList.size() - 1);
                setVisibility(View.VISIBLE);
                inAnimationAtart();


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
    }

    public void isDownload(String isDownload) {
//        if (isDownload.equals("0")) {
        tv_presetDown.setText(getResources().getString(R.string.download));
        tv_presetDown.setClickable(true);
//        } else {
//            tv_presetDown.setText(getResources().getString(R.string.downloaded));
//            tv_presetDown.setClickable(false);
//        }
    }


    private PresetParm parms;//预设参数

    public void uploadClick(String presetName, int uploadType) {
        parms = new PresetParm();
        String url = imagedata.getSmallLocalUrl();
        parms.setTextSrc(url);
        String devicename = getDeviceName();
        parms.setEquipment(devicename);
        parms.setTimeStamp(imagedata.getTimeStamp());
//                }
        LightRatioData data = imagedata.getLightRatioData();
        LoginedUser loginedUser = LoginedUser.getLoginedUser();
        String name = loginedUser.getNickname();
        parms.setAuther(name);
        parms.setLightRatioData(data);
        parms.setName(presetName);
        parms.setLabels(new ArrayList<String>());
        parms.setDescribe("");
        parms.setParmlists(imagedata.getImageTools());
        parms.setPartParmlists(imagedata.getImageParts());
        int[] imageWH = ImageUtil.decodeUriAsBitmap(imagedata.getSmallLocalUrl());
        int width = imageWH[0];
        int height = imageWH[1];
        parms.setImageHeight(height + "");
        parms.setImageWidth(width + "");
        parms.setCameraParm(imagedata.getCameraParm());
        savePreParmsToCloud(parms, uploadType, imagedata.isMatting());
    }

    public void setMactivity(Activity mactivity) {
        this.mactivity = mactivity;
    }

    private void savePreParmsToCloud(final PresetParm presetParm, final int type, final boolean isMatting) {
        uploadManager = UploadManager.getInstance(mactivity);
        //仅保存
        if (type == 1) {
            EventStatisticsUtil.event(getContext(), EventCommon.PHOTO_EDIT_IMG_SHARE_SAVE_ONLY);
            newUpload(1);
        } else if (type == 2) {
            EventStatisticsUtil.event(getContext(), EventCommon.PHOTO_EDIT_IMG_SHARE_SAVE_AND_SHARE);
            newUpload(2);
        }

    }

    private DGProgressDialog3 dgProgressDialog3;

    public void newUpload(final int type) {
        if (null == dgProgressDialog3) {
            dgProgressDialog3 = new DGProgressDialog3(getContext(), true, getResources().getString(R.string.waiting_please));
        }
        dgProgressDialog3.show();
        String code_pic = "";
        String code_param = "";
        if (type == 1) {

            code_pic = "cover";
            code_param = "light_data_person";
        } else if (type == 2) {

            code_pic = "cover";
            code_param = "light_data_market";
        }
        uploadManager.shareParams(type, "", imagedata.getLocalUrl(), parms, imagedata.isMatting(), imagedata.getValue2(), parms.getName());
        uploadManager.setUploadListener(new UploadManager.UploadListener() {
            @Override
            public void onUploadSuccess() {
                if (type == 1) {
                    presetParmsSqlControl = new PresetParmsSqlControl((AlbumNewActivity) getContext());
                    presetParmsSqlControl.startByFileId(parms.getPresetId());
                    presetParmsSqlControl.setListener(new BaseSqlControl.SqlControlListener() {
                        @Override
                        public void deletListener(Object... parms) {

                        }

                        @Override
                        public void upDataListener(Object... parms) {

                        }

                        @Override
                        public void queryListener(Object... data) {

                            // -1 查询全部 0 不存在 1存在
                            int exits = (int) data[2];

                            if (exits == 1) {
                                ToastUtil.toast(getResources().getString(R.string.parm_exist));
                                return;
                            }

                            if (exits == 0) {
                                presetParmsSqlControl.getLastOne();

                            }

                            if (exits == -1) {
                                List<PresetParm> lastParm = (List<PresetParm>) data[0];
//                                                        List<PictureProcessingData> mparms = parms.getParmlists();
//                                                        for (int i = 0; mparms != null && i < mparms.size(); i++) {
//                                                            if (mparms.get(i).getType() == BizImageMangage.WHITE_BALANCE) {
//                                                                mparms.get(i).setNum(mparms.get(i).getNum() + 100);
//                                                                mparms.get(i).setTintNum(mparms.get(i).getTintNum() + 100);
//                                                            } else {
//                                                                mparms.get(i).setNum(mparms.get(i).getNum() + 100);
//                                                            }
//                                                        }
                                parms.setShowType("0");
                                parms.setPosition(lastParm.get(0).getPosition() + 1);
                                parms.setSystem("android");
                                presetParmsSqlControl.startInsert(parms);
                                return;
                            }
                        }

                        @Override
                        public void insertListener(Object... parms) {
//                                                    share_img_edit.refresh(1);
                            dgProgressDialog3.dismiss();
                            imageparmsViewListener.onbtnclose();
                            refresh();
                            ToastUtil.toast(getResources().getString(R.string.save_ok));
                        }
                    });
                } else {
//                                            //上传个人光影
//                                            newUpload(1);
                    dgProgressDialog3.dismiss();
                    imageparmsViewListener.onbtnclose();
                    refresh();
                    ToastUtil.toast(getResources().getString(R.string.send_success));

                }
            }

            @Override
            public void onUploadFaile() {
                ToastUtil.toast(getResources().getString(R.string.save_error));
                dgProgressDialog3.dismiss();
            }
        });
    }


    public ValueAnimator inAnimation, outAnimation;

    private void initBottomAnim(View view, int y) {
        inAnimation = AnimFactory.BottomInAnim(view, y);
        outAnimation = AnimFactory.BottomOutAnim(view, y);
    }

    public void outAnimationStart() {
        if (null == outAnimation) {
            initBottomAnim(ImageparmsNewView.this, ContentUtil.getScreenHeight(getContext()));
        }
        outAnimation.start();
        outAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ImageparmsNewView.this.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void inAnimationAtart() {
        if (null == inAnimation) {
            initBottomAnim(ImageparmsNewView.this, ContentUtil.getScreenHeight(getContext()));
        }
        inAnimation.start();
    }

    public void setChangBitmap(Bitmap changBitmap) {
        this.changBitmap = changBitmap;
        if (processtools.size() != 1) {
            autoLocationAdapter.setChangeBitmap(changBitmap);
        } else {
            iv_iamge.setImageBitmap(changBitmap);
        }
    }

    private void showHint(int selectPos) {
        tv_imgType.setText(BizImageMangage.getParamsName(processtools.get(selectPos).getType(), getContext()));
    }

    private void showPic(final int mainPos, final int targetPos) {
        if (processtools.get(mainPos).getType() == BizImageMangage.SRC) {
            Picasso.get().load(processtools.get(mainPos).getImageUrl()).resize(ImageUtil.Dp2Px(getContext(), 278), ImageUtil.Dp2Px(getContext(), 278)).centerInside().into(iv_iamge);
        } else if (processtools.get(mainPos).getType() == BizImageMangage.SRC_PRESET_PARMS) {
//            if(null!=changBitmap){
//
//            }
//            Picasso.get(getContext()).load(processtools.get(mainPos).getImageUrl()).resize(ImageUtil.Dp2Px(getContext(), 278), ImageUtil.Dp2Px(getContext(), 278)).centerInside().into(new Target() {
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                    changBitmap = getCurrentImg(bitmap, mainPos);
//                    iv_iamge.setImageBitmap(changBitmap);
//                }
//
//                @Override
//                public void onBitmapFailed(Drawable errorDrawable) {
//                }
//
//                @Override
//                public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                }
//            });

            iv_iamge.setImageBitmap(changBitmap);

        } else if (processtools.get(mainPos).getType() == BizImageMangage.PARMS) {
            if (!StringUtil.isBlank(processtools.get(mainPos).getImageUrl()) && !"null".equals(processtools.get(mainPos).getImageUrl())) {

                          /*  Glide.with(getContext()).load(processtools.get(pos).getImageUrl()).asBitmap().override(ImageUtil.Dp2Px(getContext(), 278), ImageUtil.Dp2Px(getContext(), 278)).dontAnimate().into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    iv_iamge.setImageBitmap(resource);
                                }
                            });*/
                Picasso.get().load(processtools.get(mainPos).getImageUrl()).resize(ImageUtil.Dp2Px(getContext(), 278), ImageUtil.Dp2Px(getContext(), 278)).centerInside().into(iv_iamge);
            } else {
                Picasso.get().load(processtools.get(mainPos).getSmallUrl()).resize(ImageUtil.Dp2Px(getContext(), 278), ImageUtil.Dp2Px(getContext(), 278)).centerInside().into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Bitmap mBitmap = getCurrentImg(bitmap, mainPos);
                        iv_iamge.setImageBitmap(mBitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
                           /* Glide.with(getContext()).load(processtools.get(pos).getSmallUrl()).asBitmap().override(ImageUtil.Dp2Px(getContext(), 278), ImageUtil.Dp2Px(getContext(), 278)).dontAnimate().into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    changBitmap = getCurrentImg(resource, pos);
                                    iv_iamge.setImageBitmap(changBitmap);
                                }
                            });*/
            }
        } else {
            if (!StringUtil.isBlank(processtools.get(mainPos).getSmallUrl()) && !"null".equals(processtools.get(mainPos).getSmallUrl())) {
                          /*  Glide.with(getContext()).load(processtools.get(pos).getImageUrl()).asBitmap().override(ImageUtil.Dp2Px(getContext(), 278), ImageUtil.Dp2Px(getContext(), 278)).dontAnimate().into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    iv_iamge.setImageBitmap(resource);
                                }
                            });*/
                Picasso.get().load(processtools.get(mainPos).getSmallUrl()).resize(ImageUtil.Dp2Px(getContext(), 278), ImageUtil.Dp2Px(getContext(), 278)).centerInside().into(iv_iamge);
            } else {
                Picasso.get().load(processtools.get(mainPos).getImageUrl()).resize(ImageUtil.Dp2Px(getContext(), 278), ImageUtil.Dp2Px(getContext(), 278)).centerInside().into(iv_iamge);

            }
        }
        if (processtools.get(targetPos).getType() == BizImageMangage.SRC) {
            Picasso.get().load(processtools.get(targetPos).getImageUrl()).resize(ImageUtil.Dp2Px(getContext(), 278), ImageUtil.Dp2Px(getContext(), 278)).centerInside().into(iv_imageTwo);
        } else if (processtools.get(targetPos).getType() == BizImageMangage.SRC_PRESET_PARMS) {
            Picasso.get().load(processtools.get(targetPos).getImageUrl()).resize(ImageUtil.Dp2Px(getContext(), 278), ImageUtil.Dp2Px(getContext(), 278)).centerInside().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    changBitmap = getCurrentImg(bitmap, targetPos);
                    iv_imageTwo.setImageBitmap(changBitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } else if (processtools.get(targetPos).getType() == BizImageMangage.PARMS) {
            if (!StringUtil.isBlank(processtools.get(targetPos).getImageUrl()) && !"null".equals(processtools.get(targetPos).getImageUrl())) {
                Picasso.get().load(processtools.get(targetPos).getImageUrl()).resize(ImageUtil.Dp2Px(getContext(), 278), ImageUtil.Dp2Px(getContext(), 278)).centerInside().into(iv_imageTwo);
            } else {
                Picasso.get().load(processtools.get(targetPos).getSmallUrl()).resize(ImageUtil.Dp2Px(getContext(), 278), ImageUtil.Dp2Px(getContext(), 278)).centerInside().into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        changBitmap = getCurrentImg(bitmap, targetPos);
                        iv_imageTwo.setImageBitmap(changBitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        } else {
            if (!StringUtil.isBlank(processtools.get(targetPos).getSmallUrl()) && !"null".equals(processtools.get(targetPos).getSmallUrl())) {
                Picasso.get().load(processtools.get(targetPos).getSmallUrl()).resize(ImageUtil.Dp2Px(getContext(), 278), ImageUtil.Dp2Px(getContext(), 278)).centerInside().into(iv_imageTwo);
            } else {
                Picasso.get().load(processtools.get(targetPos).getImageUrl()).resize(ImageUtil.Dp2Px(getContext(), 278), ImageUtil.Dp2Px(getContext(), 278)).centerInside().into(iv_imageTwo);
            }
        }
    }


    public void showGuide() {

        Animation enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(300);
        enterAnimation.setFillAfter(true);

        Animation exitAnimation = new AlphaAnimation(1f, 0f);
        exitAnimation.setDuration(300);
        exitAnimation.setFillAfter(true);

        NewbieGuide.with((AlbumNewActivity) getContext())
                .setLabel("pagePhotoInfo")//设置引导层标示区分不同引导层，必传！否则报错
                .alwaysShow(false)//是否每次都显示引导层，默认false，只显示一次
                .addGuidePage(//添加一页引导页
                        GuidePage.newInstance()//创建一个实例
                                .setLayoutRes(R.layout.guide_photoedit_select)//设置引导页布局
                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {

                                    @Override
                                    public void onLayoutInflated(View view, Controller controller) {
                                        RelativeLayout rl_photo_info = (RelativeLayout) view.findViewById(R.id.rl_photo_info);
                                        rl_photo_info.setVisibility(View.VISIBLE);
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


