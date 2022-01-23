package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;


/**
 * Created by wb on 2016/5/4.
 */
public class TitleView extends BaseRelativeLayout {
    public final static int ALBUM = 1;
    public final static int MOVE = 4;
    public final static int CHOICE = 5;
    public final static int EDIT = 6;
    public final static int CLOUND = 7;
    public final static int PERSON = 8;
    public final static int BLUE_TOOTH = 9;
    public final static int CLOUD_ABLUM = 10;
    public final static int CLOUD_INFO = 11;
    public final static int CLASS_INFO = 12;
    public final static int CLASS_INFO_2 = 13;
    public final static int TWO_TYPE = 2;
    public final static int FOUR_TYPE = 3;
    public final static int LIGHT_RATIO = 14;//光比调节
    public final static int CLASS_STUDENT = 15;
    public final static int DESIGN = 16;
    public final static int SETTING = 17;
    public final static int LEFT_CONTENT = 18;
    public final static int TYPE_OPEN = 19;
    public final static int TYPE_CLOSE = 20;
    public final static int PRESET = 21;//预设参数
    public final static int USER_CENTER = 22;
    public final static int MARKET = 23;
    public final static int TEMPLATE = 24;
    public final static int PRESET_MANAGE = 25;//预设参数管理
    public final static int TYPE_LOGIN = 26;//注册登录
    public final static int CHANGE_PSW = 27;//修改密码
    public final static int PIC_PARMS = 28;//图片后期
    public final static int UPLOAD_MANAGE = 29; //上传管理
    public final static int SETTING_PRESET_DETAIL = 30;//预设参数详情
    public final static int PRESET_MARKER = 31; //光影市场
    public final static int ALBUM_NEW = 32; //新相册页面
    public final static int INTO = 33; //导入界面
    public final static int CLOUD_ABLUM_NEW = 34; //新云图库页面
    public final static int PIC_PARMS_NEW = 35; //新参数调整

    public final static int IMAGE_UTILS_NEW = 36; //新图片详情页面
    public final static int IMAGE_BG_REPAIR = 37; //新背景修复
    public final static int IMAGE_PARMS = 38; //新画笔
    public final static int MAIN_LOGIN = 39; //新登录
    public final static int MAIN_LOGIN_PWD = 40; //新登录密码
    public final static int MAIN_LOGIN_VERIFY = 41; //验证验证码

    public final static int MAIN_LOGIN_US = 42; //新登录
    public final static int MAIN_LOGIN_PWD_US = 43; //新登录密码
    public final static int MAIN_LOGIN_VERIFY_US = 44; //验证验证码
    public final static int MAIN_LOGIN_REPWD_US = 45;//重新设置密码
    public final static int MAIN_LOGIN_REPWD = 46; //验证验证码
    public final static int PRESET_MARKER_INNER = 47;//光影市场耳机页面
    public final static int IMAGE_PARAMS_NEW_VIEW=48; //参数详情
    public final static int WATERMARK_RECORD = 49;//视频录制跳转后的选择水印界面
    public final static int BIZCAM_U_TO = 50;//商拍优图
    public final static int BIZCAM_U_TO_WV=51; //商拍优图Web
    public final static int BIZCAM_HELP=52; //商拍帮助
    public final static int BIZCAM_HELP_DETAIL=53; //商拍详情


    private int hight;
    private ObjectAnimator designAnim;


    private ImageView left_img;
    private TextView title_text;
    private ImageView right_img;
    private TextView right_text;
    private TextView left_text;
    private String setting_title;

    private ValueAnimator inAnim, outAnim;
    private AnimInterFace animInterFace;

    public TitleView(Context context) {
        super(context);
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void initView() {
        inflate(getContext(), R.layout.album_activity, this);
        left_img = (ImageView) findViewById(R.id.left_img);
        title_text = (TextView) findViewById(R.id.title_text);
        right_img = (ImageView) findViewById(R.id.right_img);
        right_text = (TextView) findViewById(R.id.right_text);
        left_text = (TextView) findViewById(R.id.left_text);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick() {

    }


    public void setType(int type) {
        switch (type) {
            case CHOICE:
                left_img.setVisibility(GONE);
                title_text.setVisibility(VISIBLE);
                title_text.setText(getResources().getString(R.string.ablum));
                right_img.setVisibility(VISIBLE);
                right_img.setImageDrawable(getResources().getDrawable(R.drawable.set1));
                right_img.setImageLevel(1);
                break;
            case ALBUM:
                left_img.setVisibility(VISIBLE);
                //left_img.setImageDrawable(getResources().getDrawable(R.drawable.set1));
                left_img.setImageDrawable(getResources().getDrawable(R.drawable.head));
                title_text.setVisibility(VISIBLE);
                title_text.setText(getResources().getString(R.string.ablum));
                right_img.setVisibility(VISIBLE);
                //right_img.setImageDrawable(getResources().getDrawable(R.drawable.btndesign));
                right_img.setImageDrawable(getResources().getDrawable(R.drawable.set1));
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.maintitlebg));
                right_img.setImageLevel(1);
                break;
            case ALBUM_NEW:
                left_img.setVisibility(VISIBLE);
                //left_img.setImageDrawable(getResources().getDrawable(R.drawable.set1));
                left_img.setImageDrawable(getResources().getDrawable(R.drawable.guide_into));
//                title_text.setVisibility(VISIBLE);
//                title_text.setText(getResources().getString(R.string.ablum));
//                right_img.setVisibility(VISIBLE);
                right_text.setText(R.string.select);
                right_text.setVisibility(VISIBLE);
                right_text.setTextColor(getResources().getColor(R.color.sing_in_color));
                //right_img.setImageDrawable(getResources().getDrawable(R.drawable.btndesign));
//                right_img.setImageDrawable(getResources().getDrawable(R.drawable.set1));
                //      findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.drawable.title_bg));
                findViewById(R.id.title_layout_base).setBackgroundColor(getResources().getColor(R.color.translucent));
                right_img.setImageLevel(1);
                break;
            case MOVE:
                left_img.setVisibility(GONE);
                title_text.setVisibility(VISIBLE);
                setTitleText(getResources().getString(R.string.move_pics));
                setLeftImg(R.drawable.btnback);
                break;
            case EDIT:
                left_img.setVisibility(VISIBLE);
                setLeftImg(R.drawable.btnback);
                title_text.setVisibility(VISIBLE);
                title_text.setText(getResources().getString(R.string.upload_new_catelog));
                right_text.setVisibility(VISIBLE);
                right_text.setText(getResources().getString(R.string.finish));
                break;
            case CLOUND:
                left_img.setVisibility(VISIBLE);
                title_text.setVisibility(VISIBLE);
                // setTitleText("云图库");
                setLeftImg(R.drawable.btnback);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.maintitlebg));
                break;
            case CLOUD_ABLUM:
                left_img.setVisibility(VISIBLE);
                setLeftImg(R.drawable.new_close);
                title_text.setVisibility(VISIBLE);
                title_text.setText(getResources().getString(R.string.clound_ablum));
                title_text.setTextSize(14);
                title_text.setTextColor(getResources().getColor(R.color.text_item_child));
                right_text.setVisibility(VISIBLE);
                right_text.setText(getResources().getString(R.string.new_file));
                right_text.setTextColor(getResources().getColor(R.color.sing_in_color));
                findViewById(R.id.title_layout_base).setBackgroundColor(getResources().getColor(R.color.translucent));
                break;
            case PERSON:
                left_img.setVisibility(VISIBLE);
                right_img.setVisibility(VISIBLE);
                title_text.setVisibility(VISIBLE);
                setTitleText(getResources().getString(R.string.person_info));
                setLeftImg(R.drawable.btnback);
                setRightImg(R.drawable.edit);
                break;
            case BLUE_TOOTH:
                left_img.setVisibility(VISIBLE);
                right_img.setVisibility(VISIBLE);
                title_text.setTextColor(Color.WHITE);
                title_text.setVisibility(VISIBLE);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.translucent));
                setLeftImg(R.drawable.close1);
                setRightImg(R.drawable.bluetooth);
                break;
            case CLOUD_INFO:
                title_text.setVisibility(VISIBLE);
                title_text.setTextColor(getResources().getColor(R.color.text_item_child));
                title_text.setTextSize(14);
                title_text.setText(getResources().getString(R.string.bceasy_cloud));
                left_img.setVisibility(VISIBLE);
                left_img.setImageDrawable(getResources().getDrawable(R.drawable.new_close));
                right_text.setText(R.string.select);
                right_text.setVisibility(VISIBLE);
                right_text.setTextColor(getResources().getColor(R.color.sing_in_color));
                findViewById(R.id.title_layout_base).setBackgroundColor(getResources().getColor(R.color.translucent));
                break;
            case CLASS_INFO:
                setLeftImg(R.drawable.btnback);
                left_img.setVisibility(VISIBLE);
                title_text.setVisibility(VISIBLE);
                title_text.setText("");
                break;
            case CLASS_INFO_2:
                setLeftImg(R.drawable.btnback);
                left_img.setVisibility(VISIBLE);
                title_text.setVisibility(VISIBLE);
                title_text.setText("");
                right_img.setVisibility(VISIBLE);
                break;
            case LIGHT_RATIO:
                setLeftImg(R.drawable.btnback);
                left_img.setVisibility(VISIBLE);
                title_text.setVisibility(VISIBLE);
                title_text.setText(getResources().getString(R.string.my_light_ratio));
                right_img.setVisibility(VISIBLE);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.backgroud));
                break;
            case CLASS_STUDENT:
                setLeftImg(R.drawable.btnback);
                left_img.setVisibility(VISIBLE);
                title_text.setVisibility(VISIBLE);
                title_text.setText("已提交的同学");
                break;
            case PRESET:
                setLeftImg(R.drawable.btnback);
                left_img.setVisibility(VISIBLE);
                title_text.setVisibility(VISIBLE);
                title_text.setText(getResources().getString(R.string.preparms));
                right_img.setVisibility(VISIBLE);
                break;
            case DESIGN:
                setLeftImg(R.drawable.close1);
                left_img.setVisibility(VISIBLE);
                setRightImg(R.drawable.btndesign);
                right_img.setVisibility(VISIBLE);
                title_text.setText("");
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.maintitlebg));
                break;
            case SETTING:
                setLeftImg(R.drawable.arrow_back);
                left_img.setVisibility(VISIBLE);
                setLeftImg(R.drawable.new_close);
                right_img.setVisibility(GONE);
                title_text.setTextSize(14);
                title_text.setVisibility(VISIBLE);
                title_text.setText(setting_title);
                title_text.setTextColor(getResources().getColor(R.color.text_item_child));
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.translucent));
                break;
            case LEFT_CONTENT:
                title_text.setVisibility(VISIBLE);
                setLeftImg(R.drawable.btnback);
                left_img.setVisibility(VISIBLE);
                setRightImg(R.drawable.download_white);
                right_img.setVisibility(VISIBLE);
                break;
            case MARKET:
                left_img.setVisibility(VISIBLE);
                title_text.setVisibility(VISIBLE);
                right_img.setVisibility(GONE);
                setLeftImg(R.drawable.btnback);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.backgroud));
                break;
            case TEMPLATE:
                left_img.setVisibility(VISIBLE);
                title_text.setVisibility(VISIBLE);
                right_img.setVisibility(GONE);
                setLeftImg(R.drawable.close1);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.backgroud));
                break;
            case PRESET_MANAGE:
                setLeftImg(R.drawable.arrow_back);
                left_img.setVisibility(VISIBLE);
                title_text.setVisibility(VISIBLE);
                title_text.setTextSize(14);
                title_text.setTextColor(getResources().getColor(R.color.text_item_child));
                title_text.setText(getResources().getString(R.string.preparms));
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.translucent));
                break;
            case UPLOAD_MANAGE:
                setLeftImg(R.drawable.arrow_back);
                left_img.setVisibility(VISIBLE);
                title_text.setVisibility(VISIBLE);
                title_text.setText(getResources().getString(R.string.upload_manage));
                right_text.setVisibility(VISIBLE);
                title_text.setTextSize(14);
                title_text.setTextColor(getResources().getColor(R.color.text_item_child));
                right_text.setText(getResources().getString(R.string.upload_all));
                right_text.setTextColor(getResources().getColor(R.color.red_us_border));
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.translucent));
                break;
            case TYPE_LOGIN:
                setLeftImg(R.drawable.close1);
                title_text.setVisibility(VISIBLE);
                right_img.setVisibility(GONE);
                right_text.setVisibility(VISIBLE);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.backgroud));
                right_text.setTextColor(getResources().getColor(R.color.white));
                break;
            case CHANGE_PSW:
                setLeftImg(R.drawable.arrow_back);
                title_text.setVisibility(VISIBLE);
                right_img.setVisibility(GONE);
                right_text.setVisibility(GONE);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.translucent));
                right_text.setTextColor(getResources().getColor(R.color.red_us_border));
                break;
            case PIC_PARMS:
            case USER_CENTER:
                setLeftImg(R.drawable.close1);
                title_text.setVisibility(VISIBLE);
                right_img.setVisibility(VISIBLE);
                setRightImg(R.drawable.yes);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.translucent));
                break;
            case SETTING_PRESET_DETAIL:
                title_text.setVisibility(VISIBLE);
                setLeftImg(R.drawable.btnback);
                left_img.setVisibility(VISIBLE);
                break;
            case PRESET_MARKER:
                setLeftImg(R.drawable.new_close);
                //setRightText(getResources().getString(R.string.filter));
                title_text.setVisibility(VISIBLE);
                title_text.setTextColor(getResources().getColor(R.color.text_item_child));
                title_text.setTextSize(14);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.translucent));
                break;
            case PRESET_MARKER_INNER:
                setLeftImg(R.drawable.arrow_back);
                title_text.setVisibility(VISIBLE);
                title_text.setTextColor(getResources().getColor(R.color.text_item_child));
                title_text.setTextSize(14);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.translucent));
                break;

            case INTO:
                setLeftImg(R.drawable.new_close);
                title_text.setVisibility(VISIBLE);
                title_text.setTextSize(14);
                title_text.setTextColor(Color.GRAY);
                right_img.setVisibility(VISIBLE);
                setRightImg(R.drawable.new_yes);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.translucent));
                break;
            case CLOUD_ABLUM_NEW:
                left_img.setVisibility(VISIBLE);
                setLeftImg(R.drawable.new_close);
                title_text.setVisibility(VISIBLE);
                title_text.setTextSize(14);
                title_text.setText(getResources().getString(R.string.clound_ablum));
                title_text.setTextColor(getResources().getColor(R.color.text_item_child));
                right_text.setVisibility(GONE);
                setRightImg(R.drawable.new_yes);
                findViewById(R.id.title_layout_base).setBackgroundColor(getResources().getColor(R.color.translucent));
                break;
            case PIC_PARMS_NEW:
                setLeftImg(R.drawable.new_close);
                title_text.setVisibility(VISIBLE);
                title_text.setTextSize(14);
                title_text.setTextColor(getResources().getColor(R.color.text_item_child));
                right_img.setVisibility(VISIBLE);
                setRightImg(R.drawable.new_yes);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.drawable.title_bg));
                break;
            case IMAGE_UTILS_NEW:
                left_img.setVisibility(VISIBLE);
                //left_img.setImageDrawable(getResources().getDrawable(R.drawable.set1));
                left_img.setImageDrawable(getResources().getDrawable(R.drawable.guide_into));
//                title_text.setVisibility(VISIBLE);
//                title_text.setText(getResources().getString(R.string.ablum));
//                right_img.setVisibility(VISIBLE);
                right_text.setText(R.string.select);
                right_text.setVisibility(VISIBLE);
                right_text.setTextColor(getResources().getColor(R.color.little_blue));
                //right_img.setImageDrawable(getResources().getDrawable(R.drawable.btndesign));
//                right_img.setImageDrawable(getResources().getDrawable(R.drawable.set1));
                //      findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.drawable.title_bg));
                findViewById(R.id.title_layout_base).setBackgroundColor(getResources().getColor(R.color.translucent));
                right_img.setImageLevel(1);
                break;
            case IMAGE_PARMS:
            case IMAGE_BG_REPAIR:
                setLeftImg(R.drawable.new_close);
                title_text.setVisibility(VISIBLE);
                title_text.setTextSize(14);
                title_text.setTextColor(getResources().getColor(R.color.text_item_child));
                right_img.setVisibility(VISIBLE);
                setRightImg(R.drawable.new_yes);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.drawable.title_bg));
                break;
            case MAIN_LOGIN:
                setLeftImg(R.drawable.new_close);
                right_img.setVisibility(VISIBLE);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.white));
                break;
            case MAIN_LOGIN_PWD:
                setLeftImg(R.drawable.arrow_back);
                right_img.setImageDrawable(getResources().getDrawable(R.drawable.arrow_next));
                right_text.setText(Html.fromHtml("<u>" + "<font color='#000000'>忘记密码?</font>" + "</u>"));
                right_text.setVisibility(VISIBLE);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.white));
                break;
            case MAIN_LOGIN_VERIFY:
                setLeftImg(R.drawable.arrow_back);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.white));
                break;
            case MAIN_LOGIN_US:
                setLeftImg(R.drawable.new_close);
                right_img.setVisibility(VISIBLE);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.white));
                break;
            case MAIN_LOGIN_REPWD_US:
                setLeftImg(R.drawable.arrow_back);
                right_img.setVisibility(VISIBLE);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.white));
                break;
            case MAIN_LOGIN_REPWD:
                left_img.setVisibility(VISIBLE);
                setLeftImg(R.drawable.new_close);
                right_img.setVisibility(VISIBLE);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.white));
                break;
            case MAIN_LOGIN_PWD_US:
                setLeftImg(R.drawable.arrow_back);
                right_img.setImageDrawable(getResources().getDrawable(R.drawable.arrow_next));
                right_text.setText(Html.fromHtml("<font color='#000000'>Retrieve</font>"));
                right_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                right_text.setVisibility(VISIBLE);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.white));
                break;
            case MAIN_LOGIN_VERIFY_US:
                setLeftImg(R.drawable.arrow_back);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.white));
                break;
            case IMAGE_PARAMS_NEW_VIEW:
                setLeftImg(R.drawable.new_close);
                title_text.setVisibility(VISIBLE);
                title_text.setTextColor(getResources().getColor(R.color.text_item_child));
                right_text.setTextColor(getResources().getColor(R.color.sing_in_color));
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.translucent));
                break;
            case WATERMARK_RECORD:
                left_img.setVisibility(GONE);
                title_text.setVisibility(GONE);
                right_text.setVisibility(VISIBLE);
                right_text.setTextSize(14);
                right_text.setTextColor(Color.BLACK);
                right_text.setText(getResources().getString(R.string.open_wm));
                left_text.setVisibility(VISIBLE);
                left_text.setTextSize(14);
                left_text.setTextColor(Color.BLACK);
                left_text.setText(getResources().getString(R.string.close_wm));
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.color.white));
                break;
            case BIZCAM_U_TO:
                setLeftImg(R.drawable.arrow_back);
                title_text.setTextColor(getResources().getColor(R.color.bizxam_u_to));
                title_text.setVisibility(VISIBLE);
                title_text.setTextSize(14);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.drawable.title_bg));
                break;
            case BIZCAM_U_TO_WV:
                setLeftImg(R.drawable.new_close);
                title_text.setTextColor(getResources().getColor(R.color.bizxam_u_to));
                title_text.setVisibility(VISIBLE);
                title_text.setTextSize(14);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.drawable.title_bg));
                break;
            case BIZCAM_HELP:
                setLeftImg(R.drawable.new_close);
                title_text.setTextColor(getResources().getColor(R.color.black));
                title_text.setVisibility(VISIBLE);
                title_text.setTextSize(14);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.drawable.title_bg));
                break;
            case BIZCAM_HELP_DETAIL:
                setLeftImg(R.drawable.arrow_back);
                title_text.setTextColor(getResources().getColor(R.color.black));
                title_text.setVisibility(VISIBLE);
                title_text.setTextSize(14);
                findViewById(R.id.title_layout_base).setBackground(getResources().getDrawable(R.drawable.title_bg));
                break;
        }
    }

    public void setSettingTitle(String title) {
        this.setting_title = title;
    }

    public TitleView setLeftImg(int img_id) {
        left_img.setImageDrawable(getResources().getDrawable(img_id));
        return this;
    }

    public TitleView setTitleText(String text) {
        title_text.setText(text);
        return this;
    }

    public TitleView setRightImg(int img_id) {
        right_img.setImageDrawable(getResources().getDrawable(img_id));
        return this;
    }

    public TitleView setRightImgIsShow(boolean isShow) {
        if (isShow == true) {
            right_img.setVisibility(VISIBLE);
        } else {
            right_img.setVisibility(GONE);
        }
        return this;
    }

    public ImageView getRight_img() {
        return right_img;
    }

    public ImageView getLeft_img() {
        return left_img;
    }

    public void setLeft_img(ImageView left_img) {
        this.left_img = left_img;
    }

    public TitleView setLeftTextListener(View.OnClickListener onClickListener) {
        left_text.setOnClickListener(onClickListener);
        return this;
    }

    public TitleView setLeftListener(View.OnClickListener onClickListener) {
        left_img.setOnClickListener(onClickListener);
        return this;
    }

    public TitleView setRightListener(View.OnClickListener onClickListener) {
        right_img.setOnClickListener(onClickListener);
//       AddPicReceiver.notifyModifyUsername(getContext());
        return this;
    }

    public TitleView setRightText(String string) {
        right_text.setVisibility(VISIBLE);
        right_text.setText(string);
        return this;
    }

    public TitleView setRightTextCanClick(String string, boolean canClick) {
        right_text.setVisibility(VISIBLE);
        right_text.setClickable(canClick);
        right_text.setText(Html.fromHtml(string));
        return this;
    }

    public TitleView setRightTextIsShow(boolean isShow) {
        if (isShow == true) {
            right_text.setVisibility(VISIBLE);
        } else {
            right_text.setVisibility(GONE);
        }

        return this;
    }

    public String getRightText() {

        return right_text.getText().toString();
    }

    public void setablum() {

    }

    public TitleView setRightText(String string, boolean b) {
        if (b) {
            right_text.setVisibility(VISIBLE);
        } else {
            right_text.setVisibility(GONE);
        }
        right_text.setText(string);
        return this;
    }

    public TitleView setRightTextColor(int color) {
        right_text.setTextColor(color);
        return this;
    }

    public TitleView setRightTextListener(View.OnClickListener onClickListener) {
        right_text.setOnClickListener(onClickListener);
        return this;
    }

    public void setLeftType(int type) {
        switch (type) {
            case TWO_TYPE:
                right_img.setImageLevel(0);
                break;
            case FOUR_TYPE:
                right_img.setImageLevel(1);
                break;
        }
    }

    public void startrotateAnmi(int type) {
        switch (type) {
            case TYPE_OPEN:
                designAnim = ObjectAnimator.ofFloat(right_img, "rotation", 0, 90);
                designAnim.start();
                break;
            case TYPE_CLOSE:
                designAnim = ObjectAnimator.ofFloat(right_img, "rotation", 90, 0);
                designAnim.start();
                break;
        }
    }

    /*public void startAnim(View view){
        if(getY()==0||getY()==-getHeight()){
            return;
        }
        if(inAnim==null||outAnim==null){
            initAnim(view);
        }
        int h=getHeight();
        if(outAnim.isRunning()||inAnim.isRunning()){
            outAnim.end();
            inAnim.end();
        }
        if(h+getY()<h/2){
            outAnim.start();
        }
        else{
            inAnim.start();
        }
    }*/

    /**
     * 开始动画
     *
     * @param h
     */
    public void startAnim(int h) {
        hight = ContentUtil.dip2px(getContext(), h);
        initAnim();
    }

    private void initAnim() {
        outAnim = AnimFactory.tranYTopOutAnim(this, hight);
        inAnim = AnimFactory.tranYTopInAnim(this, hight);
        outAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                inAnim.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void setAnimInterFace(AnimInterFace animInterFace) {
        this.animInterFace = animInterFace;
    }

    private void initAnim(View view) {
        inAnim = AnimFactory.TitleInAnim(this, view);
        outAnim = AnimFactory.TitleOutAnim(this, view);
        inAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animInterFace.inAnimEnd();
                TitleView.this.setVisibility(VISIBLE);
                TitleView.this.invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        outAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animInterFace.outAnimEnd();
                TitleView.this.setVisibility(GONE);
                TitleView.this.invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public interface AnimInterFace {
        void inAnimEnd(Object... parms);

        void outAnimEnd(Object... parms);
    }

    public void startOut() {
        initAnim();
        TitleView.this.outAnim.start();
    }

    public void startIn() {
        initAnim();
        TitleView.this.inAnim.start();
    }

}




