package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.data.WaterMarkData;
import com.bcnetech.hyphoto.ui.activity.personCenter.SettingDetailActivity;
import com.bcnetech.hyphoto.ui.popwindow.IntoPop;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.Image.BaseUploadHeadModel;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.bcnetech.hyphoto.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by a1234 on 16/9/11.
 */

public class SettingAdviseView extends BaseRelativeLayout {
    private int hight;
    private ValueAnimator outAnim, inAnim;
    private Feedbackinter feedbackinter;
    private TextView sendadvise;
    private EditText edit_advises;
    private List<WaterMarkData> wmlist;
    private FeedBackFlowLayout rl_select_fb;
    public static final int MAX_WM_COUNT = 6;
    private IntoPop intoPop;
    private Uri cameraSaveUri;
    private File file;
    private EditText edit_phone;
    private EditText edit_name;

    private ImageView name_clear;
    private ImageView phone_clear;

    /**
     * 去相册
     */
    public static final int REQUEST_CODE_ALBUM = 1;
    /**
     * 去拍照
     */
    public static final int REQUEST_CODE_CAMERA = 2;


    public SettingAdviseView(Context context) {
        super(context);

    }

    public SettingAdviseView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public SettingAdviseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void initView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.setting_advise_view, null);
        SettingAdviseView.this.addView(view);
        sendadvise = (TextView) view.findViewById(R.id.sendadvise);
        edit_advises = (EditText) view.findViewById(R.id.edit_advises);
        rl_select_fb = view.findViewById(R.id.rl_select_fb);
        edit_phone = view.findViewById(R.id.edit_phone);
        edit_name = view.findViewById(R.id.edit_name);
        name_clear = view.findViewById(R.id.name_clear);
        phone_clear = view.findViewById(R.id.phone_clear);
    }

    @Override
    protected void initData() {
        super.initData();
        initWMList();

        edit_phone.setText(LoginedUser.getLoginedUser().getUser_name());
        edit_name.setText(LoginedUser.getLoginedUser().getNickname());
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();
        sendadvise.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String advises = edit_advises.getText().toString();
                if (StringUtil.isBlank(advises)) {
                    ToastUtil.toast(getResources().getString(R.string.feedback_hint1));
                    return;
                }
                String name = edit_name.getText().toString();
                if (StringUtil.isBlank(name)) {
                    ToastUtil.toast(getResources().getString(R.string.feedback_hint2));
                    return;
                }
                String phone = edit_phone.getText().toString();
                if (StringUtil.isBlank(phone)) {
                    ToastUtil.toast(getResources().getString(R.string.feedback_hint3));
                    return;
                }
                feedbackinter.onClick(advises, name, phone, wmlist);
            }
        });

        rl_select_fb.setOnFlowLayoutListener(new FeedBackFlowLayout.FlowLayoutListener() {
            @Override
            public void onClick(WaterMarkData drawable) {
                if (null == drawable) {
                    into();
                    // addWaterMark();
                    return;
                }
            }
        });

        name_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_name.setText("");
            }
        });

        edit_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (0 != edit_name.getText().toString().length()) {
                    name_clear.setVisibility(View.VISIBLE);
                } else {
                    name_clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edit_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus == true) {
                    if (0 != edit_name.getText().toString().length()) {
                        name_clear.setVisibility(View.VISIBLE);
                    }
                }
                if (hasFocus == false) {
                    name_clear.setVisibility(View.GONE);
                }
            }
        });



        phone_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_phone.setText("");
            }
        });

        edit_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (0 != edit_phone.getText().toString().length()) {
                    phone_clear.setVisibility(View.VISIBLE);
                } else {
                    phone_clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edit_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus == true) {
                    if (0 != edit_phone.getText().toString().length()) {
                        phone_clear.setVisibility(View.VISIBLE);
                    }
                }
                if (hasFocus == false) {
                    phone_clear.setVisibility(View.GONE);
                }
            }
        });

    }

    public void startAnim(int h) {
        hight = ContentUtil.dip2px(getContext(), h);
        initAnim();
    }

    public interface Feedbackinter {
        void onClick(String content, String name, String phone, List<WaterMarkData> wmlist);
    }

    public void onClickFeedback(Feedbackinter feedbackinter) {
        this.feedbackinter = feedbackinter;
    }

    private void initAnim() {
        outAnim = AnimFactory.tranYBottomOutAnim(this, hight);
        outAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                SettingAdviseView.this.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                SettingAdviseView.this.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        inAnim = AnimFactory.tranYBottomInAnim(this, hight);
        inAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                SettingAdviseView.this.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                SettingAdviseView.this.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void initWMList() {
        // 图片列表
        wmlist = new ArrayList<>();
        wmlist.add(null);
        initSystemTag();
    }

    /*
     * 初始化系统tag
     */
    public void initSystemTag() {
        rl_select_fb.deleteAllView();
        if (wmlist.size() > MAX_WM_COUNT) {
            wmlist.remove(0);
        }
        for (int i = 0; i < wmlist.size(); i++) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            WaterMarkData waterMarkData = wmlist.get(i);
            FeedBackBottomView feedBackBottomView = new FeedBackBottomView(getContext());
            feedBackBottomView.setWaterMarkData(waterMarkData);
           /* if (i % 4 != 0 && i != wmlist.size() - 1) {
                lp.leftMargin = 12;
                int x = ContentUtil.dip2px(WaterMarkSettingActivity.this, 3);
                //waterMarkBottomView.setPadding(x, 0, x, 0);
            }*/
            lp.leftMargin = ImageUtil.Dp2Px(getContext(), 16);
            lp.topMargin = ImageUtil.Dp2Px(getContext(), 17);
            lp.bottomMargin = ImageUtil.Dp2Px(getContext(), 17);
            rl_select_fb.addView(feedBackBottomView, lp);
        }
       /* preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                sample_image.getLocationOnScreen(location);
                if (location[0] != 0 || location[1] != 0) {
                    if (preDrawListener != null) {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_select_wm.getLayoutParams();
                        params.setMargins(location[0], 0, location[0], 0);
                        rl_select_wm.setLayoutParams(params);
                        rl_select_wm.invalidate();
                        tv_sample.setPadding(location[0], 0, 0, 0);
                        sample_image.getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
                        return true;
                    }
                }
                return false;
            }

        };*/
        rl_select_fb.getChild();

    }

    private void into() {
        if (intoPop == null) {
            intoPop = new IntoPop((SettingDetailActivity) getContext());
        }
        intoPop.showPop(((SettingDetailActivity) getContext()).getWindow().getDecorView());
        intoPop.setText();
        intoPop.setIntoClickListener((SettingDetailActivity) getContext(), new IntoPop.IntoClickListener() {
            @Override
            public void nativeFile() {
                intoPop.dismissPop();
                gotoCamera(getContext());

            }

            @Override
            public void cloud() {
                intoPop.dismissPop();
                gotoAlbum();
            }
        });
    }

    /**
     * 去相册
     */
    public void gotoAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        ((SettingDetailActivity) getContext()).startActivityForResult(intent, REQUEST_CODE_ALBUM);
    }

    /**
     * 去相机拍照返回图片
     */
    public void gotoCamera(Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        long time = System.currentTimeMillis();
        file = new File(Flag.FEEDBACK_IMAGE + time + ".jpg");
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            cameraSaveUri = FileProvider.getUriForFile(context, "com.bcnetech.hyphoto.FileProvider", file);
        } else {
            cameraSaveUri = (Uri.fromFile(file));
        }
//        cameraSaveUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraSaveUri);
        ((SettingDetailActivity) getContext()).startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (BaseUploadHeadModel.REQUEST_CODE_ALBUM == requestCode) {
            if(null!=data){
               String url = getRealFilePath(data.getData(), requestCode);
               loadAndCut("file://" + url);
            }else {
                ToastUtil.toast(getContext().getString(R.string.add_error));
            }
            return;
        }
        if (BaseUploadHeadModel.REQUEST_CODE_CAMERA == requestCode) {
            String url = getRealFilePath(cameraSaveUri, requestCode);
            loadAndCut("file://" + url);
        }
    }

    private void loadAndCut(String uri) {
        showMyWaterMark(uri);
    }

    private void showMyWaterMark(final String path) {
        int bitmaporientation = 0;
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(path.substring(7));
            String orientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
            if (!TextUtils.isEmpty(orientation)) {
                switch (Integer.parseInt(orientation)) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        bitmaporientation = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        bitmaporientation = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        bitmaporientation = 270;
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapUtils.compress(BitmapFactory.decodeFile(path.substring(7)));

        bitmap = ImageUtil.resizeImage(bitmap, bitmaporientation);

        Drawable drawable = new BitmapDrawable(bitmap);
        WaterMarkData waterMarkData = new WaterMarkData(drawable, path);
        wmlist.add(1, waterMarkData);
        initSystemTag();

    }

    /**
     * 获取相册和拍照uri的实际路径
     *
     * @param uri
     * @return
     */
    public String getRealFilePath(final Uri uri, int type) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {

            if (Build.VERSION.SDK_INT >= 24) {
                if (type == REQUEST_CODE_ALBUM) {
                    Cursor cursor = getContext().getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                    if (null != cursor) {
                        if (cursor.moveToFirst()) {
                            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                            if (index > -1) {
                                data = cursor.getString(index);
                            }
                        }
                        cursor.close();
                    }
                } else if (type == REQUEST_CODE_CAMERA) {
                    data = file.getAbsolutePath();
                }
            } else {
                Cursor cursor = getContext().getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        if (index > -1) {
                            data = cursor.getString(index);
                        }
                    }
                    cursor.close();
                }
            }


        }
        return data;
    }

}

