package com.bcnetech.hyphoto.ui.activity.photoedit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnLayoutInflatedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.bcnetechlibrary.dialog.ChoiceDialog;
import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog3;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.ColorChoiceItem;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.ImageDataSqlControl;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.receiver.AddPicReceiver;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.ui.adapter.ColorClickAdapter;
import com.bcnetech.hyphoto.ui.adapter.ColorDefaultAdapter;
import com.bcnetech.hyphoto.ui.view.ImageNewUtilsView;
import com.bcnetech.hyphoto.ui.view.MattingPicView;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.ui.view.colorpickerview.ColorPickerNewView;
import com.bcnetech.hyphoto.ui.view.colorpickerview.ColorPickerView;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yhf on 17/11/1.
 */

public class BizMattingNewActivity extends BaseActivity {
    private TitleView matting_title;
    private ImageView mark_main;
    private ImageView mark_bg;
    private ImageView undo;
    private ImageView eye;
    private MattingPicView matting_pic;
    private ImageData imageparms;
    //    private RelativeLayout wait_view;
    private boolean eyeon = false;
    private RelativeLayout rl_main;
    //    private LinearLayout ll_colours;
//    private RelativeLayout  rl_guide_view;
//    private WaitProgressBarView wait_progress;
    private PopupWindow mCurPopupWindow;
    private ColorPickerView colorPickerView;
    private int mcolor = -1;
    private int bottomSelect = 1;

    private RecyclerView recycle_colorDefault;
    private boolean isSelect = false;
    private Bitmap bitmap;
    private List<ColorChoiceItem> colorDefaultItems;
    private ColorDefaultAdapter colorDefaultAdapter;
    private ImageView iv_image;
    private ColorPickerNewView colorBoardView;
    private ImageView iv_border;
    private RecyclerView recycle_colorClick;
    private ColorClickAdapter colorClickAdapter;
    private Bitmap mattingBitmap;

    private List<ColorChoiceItem> colorLumpItems;
    private DGProgressDialog3 dgProgressDialog;
    private boolean isMatting = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biz_matting_new_layout);
        initImageDefData();
        initView();
        initData();
        onViewClick();
    }

    @Override
    protected void initView() {
        matting_title = (TitleView) findViewById(R.id.matting_title);
        mark_main = (ImageView) findViewById(R.id.mark_main);
        mark_bg = (ImageView) findViewById(R.id.mark_bg);
        undo = (ImageView) findViewById(R.id.undo);
        eye = (ImageView) findViewById(R.id.eye);
        matting_pic = (MattingPicView) findViewById(R.id.matting_pic);
        rl_main = (RelativeLayout) findViewById(R.id.rl_main);



    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Flag.PART_PARMS, imageparms);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageparms = (ImageData) savedInstanceState.getSerializable(Flag.PART_PARMS);
    }

    @Override
    protected void initData() {

        matting_title.setType(TitleView.PIC_PARMS_NEW);
        matting_title.setTitleText(getResources().getString(R.string.sign_bg));
        matting_pic.initImageDefData(imageparms.getSmallLocalUrl());
        matting_title.getRight_img().setVisibility(View.INVISIBLE);


        if (imageparms.getSmallLocalUrl().contains(".png")) {
            rl_main.setBackground(getResources().getDrawable(R.drawable.bgbitmap));
        }

        dgProgressDialog = new DGProgressDialog3(this, false, getResources().getString(R.string.matting));
        mCurPopupWindow=initPopupWindow(eye);
        dissmissDialog();

    }

    /**
     * 初始化图片配置文件
     */
    private void initImageDefData() {
        imageparms= ImageNewUtilsView.imageparms;
//        imageparms = (ImageData) getIntent().getSerializableExtra(Flag.PART_PARMS);
        if (imageparms == null) {
            finish();
        } else {
            //当配置文件图片参数处理  和  图片部分处理为空   且  预设参数不为空  深拷贝数据到配置文件
            if (imageparms.getImageTools() == null) {
                imageparms.setImageTools(new ArrayList<PictureProcessingData>());
            }
            if (imageparms.getImageParts() == null) {
                imageparms.setImageParts(new ArrayList<PictureProcessingData>());
            }
            if (imageparms.getImageTools().size() == 0 && imageparms.getImageParts().size() == 0) {
                if (imageparms.getPresetParms() != null && imageparms.getPresetParms().getParmlists() != null &&
                        imageparms.getPresetParms().getParmlists().size() != 0) {
                    for (int i = 0; i < imageparms.getPresetParms().getParmlists().size(); i++) {
                        if (imageparms.getPresetParms().getParmlists().get(i).getType() / 1000 * 1000 == BizImageMangage.PARMS) {
                            imageparms.getImageTools().add(new PictureProcessingData(imageparms.getPresetParms().getParmlists().get(i)));
                        } else {
                            imageparms.getImageParts().add(new PictureProcessingData(imageparms.getPresetParms().getParmlists().get(i)));
                        }
                    }
                }
            }
        }
    }


    @Override
    protected void onViewClick() {
        matting_pic.setListener(new MattingPicView.MattingWaitListener() {
            @Override
            public void showMattingDialog() {
                showDialog();
            }

            @Override
            public void dismissMattingDialog(int size,Bitmap finishBitmap,boolean isAlpha) {
                dissmissDialog();
                mattingBitmap = finishBitmap;

                if (isAlpha) {
                    if (size == 1) {
                        matting_title.getRight_img().setVisibility(View.INVISIBLE);
                    } else {
                        matting_title.getRight_img().setVisibility(View.VISIBLE);
                    }
                } else {
                    matting_title.getRight_img().setVisibility(View.INVISIBLE);
                }
                if (size == 0) {
                    isMatting = false;
                } else {
                    isMatting = true;
                }
            }
        });
        matting_title.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        matting_title.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = matting_pic.getFinishbitmap();

                if (isSelect == true) {
                    final ChoiceDialog cardDialog = ChoiceDialog.createInstance(BizMattingNewActivity.this);
                    cardDialog.setOk(getResources().getString(R.string.confirm));
                    cardDialog.setCancel(getResources().getString(R.string.cancel));
                    cardDialog.setAblumTitle(getResources().getString(R.string.alert));
                    cardDialog.setAblumMessage(getResources().getString(R.string.coalescing));
                    cardDialog.isNeedBlur(false);
                    cardDialog.setChoiceCallback(new ChoiceDialog.CardDialogCallback() {
                        @Override
                        public void onOKClick() {
                            bitmap = drawBg4Bitmap(mcolor, bitmap);
                            SaveMateTask saveMateTask = new SaveMateTask(bitmap, isSelect);
                            showWait();
                            saveMateTask.execute();
                            cardDialog.dismiss();
                        }

                        @Override
                        public void onCancelClick() {
                            //是否抠图
                            if (isMatting) {
                                SaveMateTask saveMateTask = new SaveMateTask(bitmap, isSelect);
                                showWait();
                                saveMateTask.execute();
                            } else {
                                finish();
                            }
                            cardDialog.dismiss();
                        }

                        @Override
                        public void onDismiss() {
                        }
                    });
                    cardDialog.show();
                } else {
                    SaveMateTask saveMateTask = new SaveMateTask(bitmap, isSelect);
                    showWait();
                    saveMateTask.execute();
                }


                // savePic();
            }
        });
        mark_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSelect == 1) {
                    return;
                }
                if (bottomSelect == 2) {
                    mark_main.setImageDrawable(getResources().getDrawable(R.drawable.mark_new_main));

                }
                if (bottomSelect == 4) {
                    eye.setImageResource(R.drawable.change_background);
                    if (null != mCurPopupWindow && mCurPopupWindow.isShowing()) {
                        mCurPopupWindow.dismiss();
                    }
                    if (imageparms.getSmallLocalUrl().contains(".png")) {
                        rl_main.setBackground(getResources().getDrawable(R.drawable.bgbitmap));
                    } else {
                        rl_main.setBackgroundColor(Color.parseColor("#F2F4F7"));
                    }
                    matting_pic.setBackground(MattingPicView.BACKGROUD_TYPE_SEEDRAW);
                } else {
                    matting_pic.setBackground(MattingPicView.BACKGROUD_TYPE_DRAW);
                }
                matting_title.setTitleText(getResources().getString(R.string.sign_bg));
                bottomSelect = 1;
                mark_bg.setImageDrawable(getResources().getDrawable(R.drawable.mark_new_bg_select));
                //设置画笔状态
                matting_pic.setPaintType(MattingPicView.TYPE_BACKGROUND);


            }
        });
        mark_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSelect == 2) {
                    return;
                }
                if (bottomSelect == 1) {
                    mark_bg.setImageDrawable(getResources().getDrawable(R.drawable.mark_new_bg));
                }
                if (bottomSelect == 4) {
                    eye.setImageResource(R.drawable.change_background);
                    if (null != mCurPopupWindow && mCurPopupWindow.isShowing()) {
                        mCurPopupWindow.dismiss();
                    }
                    if (imageparms.getSmallLocalUrl().contains(".png")) {
                        rl_main.setBackground(getResources().getDrawable(R.drawable.bgbitmap));
                    } else {
                        rl_main.setBackgroundColor(Color.parseColor("#F2F4F7"));
                    }
                    matting_pic.setBackground(MattingPicView.BACKGROUD_TYPE_SEEDRAW);

                } else {
                    matting_pic.setBackground(MattingPicView.BACKGROUD_TYPE_DRAW);
                }
                matting_title.setTitleText(getResources().getString(R.string.sign_main));
                bottomSelect = 2;
                mark_main.setImageDrawable(getResources().getDrawable(R.drawable.mark_new_main_select));
                //设置画笔状态
                matting_pic.setPaintType(MattingPicView.TYPE_MAIN);

            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSelect == 3) {
                    matting_pic.revoke();
                    return;
                }
                if (bottomSelect == 1) {
                    mark_bg.setImageDrawable(getResources().getDrawable(R.drawable.mark_new_bg));
                } else if (bottomSelect == 2) {
                    mark_main.setImageDrawable(getResources().getDrawable(R.drawable.mark_new_main));

                } else if (bottomSelect == 4) {
                    eye.setImageResource(R.drawable.change_background);
                    if (null != mCurPopupWindow && mCurPopupWindow.isShowing()) {
                        mCurPopupWindow.dismiss();
                    }
                    if (imageparms.getSmallLocalUrl().contains(".png")) {
                        rl_main.setBackground(getResources().getDrawable(R.drawable.bgbitmap));
                    } else {
                        rl_main.setBackgroundColor(Color.parseColor("#F2F4F7"));
                    }
                    matting_pic.setBackground(MattingPicView.BACKGROUD_TYPE_SEEDRAW);
                }
                //设置画笔状态
                matting_pic.setPaintType(MattingPicView.TYPE_UNDO);
                bottomSelect = 3;
                matting_pic.revoke();

            }
        });

//        wait_view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });

        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                if(bottomSelect==4){
//                    return;
//                }
                if (bottomSelect == 1) {
                    mark_bg.setImageDrawable(getResources().getDrawable(R.drawable.mark_new_bg));
                } else if (bottomSelect == 2) {
                    mark_main.setImageDrawable(getResources().getDrawable(R.drawable.mark_new_main));

                }
                bottomSelect = 4;
                matting_title.setTitleText(getResources().getString(R.string.change_bg));
                eye.setImageResource(R.drawable.change_background_select);
                if (null == mCurPopupWindow) {
                    mCurPopupWindow = initPopupWindow(eye);
                    mCurPopupWindow.showAsDropDown(eye);
                } else {
                    if(colorDefaultAdapter.getSelectItem()!=0&&null!=mattingBitmap){
                        iv_image.setImageBitmap(mattingBitmap);
                    }
                    mCurPopupWindow.showAsDropDown(eye);

                    if (colorDefaultAdapter.getSelectItem() == 0) {
                        matting_pic.setBackground(MattingPicView.BACKGROUD_TYPE_INIT);
                    } else if (colorDefaultAdapter.getSelectItem() == 1) {
                        matting_pic.setBackground(MattingPicView.BACKGROUD_TYPE_MATTING);
                        rl_main.setBackgroundResource(R.drawable.bgbitmap);
                    } else {
                        matting_pic.setBackground(MattingPicView.BACKGROUD_TYPE_MATTING);
                        rl_main.setBackgroundColor(mcolor);
                    }
                }
            }
        });


    }


    public void showDialog() {
//        wait_progress.setVisibility(View.GONE);
//        wait_view.setVisibility(View.VISIBLE);
        matting_title.getRight_img().setEnabled(false);
        matting_title.getLeft_img().setEnabled(false);
        mark_bg.setEnabled(false);
        mark_main.setEnabled(false);
        undo.setEnabled(false);
        eye.setEnabled(false);
        if (null != dgProgressDialog && !dgProgressDialog.isShowing()) {
            dgProgressDialog.showDialog();
        }
    }

    private void showWait() {
        dgProgressDialog.showDialog();
        dgProgressDialog.setShowHintText(getResources().getString(R.string.waiting));
//        wait_progress.setVisibility(View.VISIBLE);
    }

    public void dissmissDialog() {
        matting_title.getRight_img().setEnabled(true);
        matting_title.getLeft_img().setEnabled(true);
        mark_bg.setEnabled(true);
        mark_main.setEnabled(true);
        undo.setEnabled(true);
        eye.setEnabled(true);
//        wait_view.setVisibility(View.INVISIBLE);
        if (null != dgProgressDialog && dgProgressDialog.isShowing()) {
            dgProgressDialog.dismiss();
        }
//        gifView.pause();
    }

    private class SaveMateTask extends AsyncTask<Void, Void, ImageData> {
        private Bitmap bitmap;
        private boolean isCheck;

        SaveMateTask(Bitmap bitmap, boolean isCheck) {
            this.bitmap = bitmap;
            this.isCheck = isCheck;
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

                path1 = FileUtil.saveBitmap(bitmap, time - 1 + "", true);
                path1 = "file://" + path1;

                path2 = "file://" + FileUtil.copyFile(path1, Flag.NATIVESDFILE, time + ".png");


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
                ImageDataSqlControl imageDataSqlControl = new ImageDataSqlControl(BizMattingNewActivity.this);
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
                BizMattingNewActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(result.getSmallLocalUrl())));
                AddPicReceiver.notifyModifyUsername(BizMattingNewActivity.this, "add");
            } else {
                ToastUtil.toast(getResources().getString(R.string.add_error));
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BizImageMangage.getInstance().setSrcPix(null);
    }



    public PopupWindow initPopupWindow(final View anchorView) {
        final View contentView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.pop_change_background_new_layout, null);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 创建PopupWindow时候指定高宽时showAsDropDown能够自适应
        // 如果设置为wrap_content,showAsDropDown会认为下面空间一直很充足（我以认为这个Google的bug）
        // 备注如果PopupWindow里面有ListView,ScrollView时，一定要动态设置PopupWindow的大小
        final PopupWindow popupWindow = new PopupWindow(contentView,
                contentView.getMeasuredWidth(), contentView.getMeasuredHeight(), false);
        recycle_colorDefault = (RecyclerView) contentView.findViewById(R.id.recycle_colorDefault);
        colorBoardView = (ColorPickerNewView) contentView.findViewById(R.id.color_pickerNewView);
        iv_image = (ImageView) contentView.findViewById(R.id.iv_image);
        iv_border = (ImageView) contentView.findViewById(R.id.iv_border);
        recycle_colorClick = (RecyclerView) contentView.findViewById(R.id.recycle_colorClick);


        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycle_colorDefault.setLayoutManager(linearLayoutManager);
        initChoiceAdapter();

        colorDefaultAdapter = new ColorDefaultAdapter(this, colorDefaultItems);
        recycle_colorDefault.setAdapter(colorDefaultAdapter);
        colorDefaultAdapter.setInterFace(new ColorDefaultAdapter.HolderInterFace() {
            @Override
            public void setItemClick(int position, ColorDefaultAdapter.ViewHolder viewHolder) {

                if (colorDefaultAdapter.getSelectItem() == position) {
                    return;
                }
                colorDefaultAdapter.setSelectItem(position);
                if (colorDefaultItems.get(position).getColor().equals("original")) {
                    Picasso.get().load(colorDefaultItems.get(position).getPath()).resize(500,500).centerInside().into(iv_image);
                    matting_pic.setBackground(MattingPicView.BACKGROUD_TYPE_INIT);
                    rl_main.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    iv_image.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    iv_image.setVisibility(View.VISIBLE);
                    iv_border.setVisibility(View.VISIBLE);
                    colorBoardView.setVisibility(View.GONE);
                    recycle_colorClick.setVisibility(View.GONE);
                    mcolor = -1;
                    isSelect = false;
                    if (!isMatting) {
                        if (matting_title.getRight_img().getVisibility() == View.VISIBLE) {
                            matting_title.getRight_img().setVisibility(View.INVISIBLE);
                        }
                    }
                } else if (colorDefaultItems.get(position).getColor().equals("transparent")) {
                    if(null!=mattingBitmap){
                        iv_image.setImageBitmap(mattingBitmap);
                    }
                    rl_main.setBackgroundResource(R.drawable.bgbitmap);
                    iv_image.setVisibility(View.VISIBLE);
                    iv_image.setBackgroundResource(R.drawable.bgbitmap_new);
                    iv_border.setVisibility(View.VISIBLE);
                    colorBoardView.setVisibility(View.GONE);
                    recycle_colorClick.setVisibility(View.GONE);
                    matting_pic.setBackground(MattingPicView.BACKGROUD_TYPE_MATTING);
                    mcolor = -1;
                    isSelect = false;
                    if (!isMatting) {
                        if (matting_title.getRight_img().getVisibility() == View.VISIBLE) {
                            matting_title.getRight_img().setVisibility(View.INVISIBLE);
                        }
                    }
                } else if (colorDefaultItems.get(position).getColor().equals("white")) {
                    if(null!=mattingBitmap){
                        iv_image.setImageBitmap(mattingBitmap);
                    }                    rl_main.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    iv_image.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    iv_image.setVisibility(View.VISIBLE);
                    iv_border.setVisibility(View.VISIBLE);
                    colorBoardView.setVisibility(View.GONE);
                    recycle_colorClick.setVisibility(View.GONE);
                    matting_pic.setBackground(MattingPicView.BACKGROUD_TYPE_MATTING);
                    mcolor = Color.parseColor("#FFFFFF");
                    isSelect = true;
                    if (!isMatting) {
                        if (matting_title.getRight_img().getVisibility() != View.VISIBLE) {
                            matting_title.getRight_img().setVisibility(View.VISIBLE);
                        }
                    }
                } else if (colorDefaultItems.get(position).getColor().equals("black")) {
                    if(null!=mattingBitmap){
                        iv_image.setImageBitmap(mattingBitmap);
                    }                    rl_main.setBackgroundColor(Color.parseColor("#000000"));
                    iv_image.setBackgroundColor(Color.parseColor("#000000"));
                    iv_border.setVisibility(View.VISIBLE);
                    iv_image.setVisibility(View.VISIBLE);
                    colorBoardView.setVisibility(View.GONE);
                    matting_pic.setBackground(MattingPicView.BACKGROUD_TYPE_MATTING);
                    mcolor = Color.parseColor("#000000");
                    recycle_colorClick.setVisibility(View.GONE);
                    isSelect = true;
                    if (!isMatting) {
                        if (matting_title.getRight_img().getVisibility() != View.VISIBLE) {
                            matting_title.getRight_img().setVisibility(View.VISIBLE);
                        }
                    }
                } else if (colorDefaultItems.get(position).getColor().equals("colorBoard")) {
                    colorBoardView.setVisibility(View.VISIBLE);
                    colorBoardView.setColor(mcolor);
                    iv_image.setVisibility(View.GONE);
                    iv_border.setVisibility(View.GONE);
                    recycle_colorClick.setVisibility(View.GONE);
                    matting_pic.setBackground(MattingPicView.BACKGROUD_TYPE_MATTING);
                    isSelect = true;
                    if (!isMatting) {
                        if (matting_title.getRight_img().getVisibility() != View.VISIBLE) {
                            matting_title.getRight_img().setVisibility(View.VISIBLE);
                        }
                    }
                } else if (colorDefaultItems.get(position).getColor().equals("colorLump")) {
                    recycle_colorClick.setVisibility(View.VISIBLE);
                    iv_image.setVisibility(View.GONE);
                    iv_border.setVisibility(View.GONE);
                    colorBoardView.setVisibility(View.GONE);
                    matting_pic.setBackground(MattingPicView.BACKGROUD_TYPE_MATTING);
                    if (colorClickAdapter.getSelectItem() != -1) {
                        mcolor = Color.parseColor(colorLumpItems.get(colorClickAdapter.getSelectItem()).getColor());
                        rl_main.setBackgroundColor(mcolor);
                        iv_image.setBackgroundColor(mcolor);
                        isSelect = true;
                        if (!isMatting) {
                            if (matting_title.getRight_img().getVisibility() == View.VISIBLE) {
                                matting_title.getRight_img().setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                }
                colorDefaultAdapter.notifyDataSetChanged();
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 8);
        recycle_colorClick.setLayoutManager(gridLayoutManager);
        initClickAdapterData();
        colorClickAdapter = new ColorClickAdapter(this, colorLumpItems);
        colorClickAdapter.setInterFace(new ColorClickAdapter.HolderInterFace() {
            @Override
            public void setItemClick(int position, ColorClickAdapter.ViewHolder viewHolder) {
                if (colorClickAdapter.getSelectItem() == position) {
                    return;
                }
                isSelect = true;
                rl_main.setBackgroundColor(Color.parseColor(colorLumpItems.get(position).getColor()));
                colorClickAdapter.setSelectItem(position);
                colorClickAdapter.notifyDataSetChanged();
                mcolor = Color.parseColor(colorLumpItems.get(position).getColor());
                if (!isMatting) {
                    if (matting_title.getRight_img().getVisibility() != View.VISIBLE) {
                        matting_title.getRight_img().setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        recycle_colorClick.setAdapter(colorClickAdapter);

        colorBoardView.setOnColorChangedListener(new ColorPickerNewView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                rl_main.setBackgroundColor(color);
                mcolor = color;
            }
        });

        Picasso.get().load(imageparms.getSmallLocalUrl()).resize(500 ,500).centerInside().into(iv_image);

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 自动调整箭头的位置
                autoAdjustArrowPos(popupWindow, contentView, anchorView);
                contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
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
//        popupWindow.showAsDropDown(anchorView);
        return popupWindow;
    }

    private void autoAdjustArrowPos(PopupWindow popupWindow, View contentView, View anchorView) {
//        View upArrow = contentView.findViewById(R.id.up_arrow);
        View downArrow = contentView.findViewById(R.id.down_arrow);

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
        LinearLayout.LayoutParams downArrowParams = (LinearLayout.LayoutParams) downArrow.getLayoutParams();
        downArrowParams.leftMargin = arrowLeftMargin;
        downArrow.setLayoutParams(downArrowParams);
    }

    public static Bitmap drawBg4Bitmap(int color, Bitmap orginBitmap) {
        Paint paint = new Paint();
        paint.setColor(color);
        Bitmap bitmap = Bitmap.createBitmap(orginBitmap.getWidth(),
                orginBitmap.getHeight(), orginBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, orginBitmap.getWidth(), orginBitmap.getHeight(), paint);
        canvas.drawBitmap(orginBitmap, 0, 0, paint);
        return bitmap;
    }

    private void initClickAdapterData() {
        if (colorLumpItems == null) {
            colorLumpItems = new ArrayList<>();
        }
        colorLumpItems.add(new ColorChoiceItem("#FF2A05", false));
        colorLumpItems.add(new ColorChoiceItem("#FEFD32", false));
        colorLumpItems.add(new ColorChoiceItem("#09FB30", false));
        colorLumpItems.add(new ColorChoiceItem("#05FDFF", false));
        colorLumpItems.add(new ColorChoiceItem("#1922FC", false));
        colorLumpItems.add(new ColorChoiceItem("#FF36FC", false));
        colorLumpItems.add(new ColorChoiceItem("#C0272E", false));
        colorLumpItems.add(new ColorChoiceItem("#ED2624", false));

        colorLumpItems.add(new ColorChoiceItem("#F15A24", false));
        colorLumpItems.add(new ColorChoiceItem("#F7931E", false));
        colorLumpItems.add(new ColorChoiceItem("#FAB03B", false));
        colorLumpItems.add(new ColorChoiceItem("#FCEE2E", false));
        colorLumpItems.add(new ColorChoiceItem("#D9E02B", false));
        colorLumpItems.add(new ColorChoiceItem("#8CC53E", false));
        colorLumpItems.add(new ColorChoiceItem("#39B549", false));
        colorLumpItems.add(new ColorChoiceItem("#009245", false));

        colorLumpItems.add(new ColorChoiceItem("#006837", false));
        colorLumpItems.add(new ColorChoiceItem("#23B573", false));
        colorLumpItems.add(new ColorChoiceItem("#00A99D", false));
        colorLumpItems.add(new ColorChoiceItem("#29ACE2", false));
        colorLumpItems.add(new ColorChoiceItem("#0171BC", false));
        colorLumpItems.add(new ColorChoiceItem("#2E3192", false));
        colorLumpItems.add(new ColorChoiceItem("#1A1464", false));
        colorLumpItems.add(new ColorChoiceItem("#662E91", false));

        colorLumpItems.add(new ColorChoiceItem("#92288F", false));
        colorLumpItems.add(new ColorChoiceItem("#9E195D", false));
        colorLumpItems.add(new ColorChoiceItem("#D4225A", false));
        colorLumpItems.add(new ColorChoiceItem("#ED2979", false));
        colorLumpItems.add(new ColorChoiceItem("#C7B299", false));
        colorLumpItems.add(new ColorChoiceItem("#998675", false));
        colorLumpItems.add(new ColorChoiceItem("#736357", false));
        colorLumpItems.add(new ColorChoiceItem("#534741", false));

        colorLumpItems.add(new ColorChoiceItem("#C69C6D", false));
        colorLumpItems.add(new ColorChoiceItem("#A67C52", false));
        colorLumpItems.add(new ColorChoiceItem("#8C6239", false));
        colorLumpItems.add(new ColorChoiceItem("#754D24", false));
        colorLumpItems.add(new ColorChoiceItem("#603813", false));
        colorLumpItems.add(new ColorChoiceItem("#ADB4C1", false));
        colorLumpItems.add(new ColorChoiceItem("#BDCCD4", false));
        colorLumpItems.add(new ColorChoiceItem("#F1F3F7", false));

        colorLumpItems.add(new ColorChoiceItem("#1A1A1A", false));
        colorLumpItems.add(new ColorChoiceItem("#323232", false));
        colorLumpItems.add(new ColorChoiceItem("#4D4D4D", false));
        colorLumpItems.add(new ColorChoiceItem("#666666", false));
        colorLumpItems.add(new ColorChoiceItem("#999999", false));
        colorLumpItems.add(new ColorChoiceItem("#B3B3B3", false));
        colorLumpItems.add(new ColorChoiceItem("#CCCCCC", false));
        colorLumpItems.add(new ColorChoiceItem("#E6E6E6", false));

    }

    private void initChoiceAdapter() {
        if (colorDefaultItems == null) {
            colorDefaultItems = new ArrayList<>();
        }
        colorDefaultItems.add(new ColorChoiceItem("original", true, imageparms.getSmallLocalUrl()));
        colorDefaultItems.add(new ColorChoiceItem("transparent", false, ""));
        colorDefaultItems.add(new ColorChoiceItem("white", false, ""));
        colorDefaultItems.add(new ColorChoiceItem("black", false, ""));
        colorDefaultItems.add(new ColorChoiceItem("colorBoard", false, ""));
        colorDefaultItems.add(new ColorChoiceItem("colorLump", false, ""));


    }


    @Override
    protected void initGuide() {
        super.initGuide();
        View ll_matting = findViewById(R.id.ll_matting);
        if(!Flag.isEnglish){
            NewbieGuide.with(this)
                    .setLabel("pagePhotoEditMatting")//设置引导层标示区分不同引导层，必传！否则报错
                    .alwaysShow(false)//是否每次都显示引导层，默认false，只显示一次
                    .addGuidePage(//添加一页引导页
                            GuidePage.newInstance()//创建一个实例
                                    .addHighLight(ll_matting, HighLight.Shape.ROUND_RECTANGLE, 100, 0, null)
                                    .setLayoutRes(R.layout.guide_photoedit_select)//设置引导页布局
                                    .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {

                                        @Override
                                        public void onLayoutInflated(View view, Controller controller) {
                                            RelativeLayout rl_photoedit_matting = (RelativeLayout) view.findViewById(R.id.rl_photoedit_matting);
                                            rl_photoedit_matting.setVisibility(View.VISIBLE);
                                            //引导页布局填充后回调，用于初始化
                                       /* TextView tv = view.findViewById(R.id.textView2);
                                        tv.setText("我是动态设置的文本");*/
                                        }
                                    })
                                    .setEnterAnimation(enterAnimation)//进入动画
                                    .setExitAnimation(exitAnimation)//退出动画
                    )
                    .show();//显示引导层(至少需要一页引导页才能显示)
        }else {
            NewbieGuide.with(this)
                    .setLabel("pagePhotoEditMatting")//设置引导层标示区分不同引导层，必传！否则报错
                    .alwaysShow(false)//是否每次都显示引导层，默认false，只显示一次
                    .addGuidePage(//添加一页引导页
                            GuidePage.newInstance()//创建一个实例
                                    .addHighLight(ll_matting, HighLight.Shape.ROUND_RECTANGLE, 100, 0, null)
                                    .setLayoutRes(R.layout.guide_photoedit_select)//设置引导页布局
                                    .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {

                                        @Override
                                        public void onLayoutInflated(View view, Controller controller) {
                                            RelativeLayout rl_photoedit_matting = (RelativeLayout) view.findViewById(R.id.rl_photoedit_us_matting);
                                            rl_photoedit_matting.setVisibility(View.VISIBLE);

                                            ImageView iv_us_hint_one=view.findViewById(R.id.iv_us_hint_one);
                                            iv_us_hint_one.setVisibility(View.VISIBLE);

                                            TextView tv_us_one=view.findViewById(R.id.tv_us_one);
                                            tv_us_one.setVisibility(View.VISIBLE);

                                            TextView tv_us_two=view.findViewById(R.id.tv_us_two);
                                            tv_us_two.setVisibility(View.VISIBLE);

                                            LinearLayout ll_us_one=view.findViewById(R.id.ll_us_one);
                                            ll_us_one.setVisibility(View.VISIBLE);


                                            //引导页布局填充后回调，用于初始化
                                       /* TextView tv = view.findViewById(R.id.textView2);
                                        tv.setText("我是动态设置的文本");*/
                                        }
                                    })
                                    .setEnterAnimation(enterAnimation)//进入动画
                                    .setExitAnimation(exitAnimation)//退出动画
                    ).addGuidePage(//添加一页引导页
                    GuidePage.newInstance()//创建一个实例
                            .setLayoutRes(R.layout.guide_photoedit_select)//设置引导页布局
                            .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {

                                @Override
                                public void onLayoutInflated(View view, Controller controller) {
                                    RelativeLayout rl_photoedit_matting = (RelativeLayout) view.findViewById(R.id.rl_photoedit_us_matting);
                                    rl_photoedit_matting.setVisibility(View.VISIBLE);

                                    ImageView iv_us_hint_two=view.findViewById(R.id.iv_us_hint_two);
                                    iv_us_hint_two.setVisibility(View.VISIBLE);

                                    TextView tv_us_four=view.findViewById(R.id.tv_us_four);
                                    tv_us_four.setVisibility(View.VISIBLE);

                                    TextView tv_us_three=view.findViewById(R.id.tv_us_three);
                                    tv_us_three.setVisibility(View.VISIBLE);

                                    ImageView iv_us_three=view.findViewById(R.id.iv_us_three);
                                    iv_us_three.setVisibility(View.VISIBLE);

                                    ImageView iv_us_four=view.findViewById(R.id.iv_us_four);
                                    iv_us_four.setVisibility(View.VISIBLE);

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


}
