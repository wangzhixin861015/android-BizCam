package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.bean.CameraParm;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.R;

import java.util.HashMap;

/**
 * Created by a1234 on 16/9/11.
 */

public class ProfessionCameraView extends BaseRelativeLayout {
    public static final int TYPESEC = 1;
    public static final int TYPEWB = 2;
    public static final int TYPEISO = 3;
    public static final int TYPEFOCUS = 4;


    private TextView tv_sec;
    private TextView tv_wb;
    private TextView tv_iso;
    private TextView tv_focus;
    private MRelativeLayout rl_focus;
    private ImageView iv_focus;
    private ProfessionRegulateView professionRegulateView;
    private PrefessorCameraInter prefessorCameraInter;
    private ValueAnimator regulateAnimIn, regulateAnimOut;
    private boolean boolean_sec, boolean_wb, boolean_iso, boolean_f = false;
    private CameraParm cameraParm;//生成的相机参数
    private PresetParm presetParm;//传来的预设参数
    private boolean isC2Support = false;

    public ProfessionCameraView(Context context) {
        super(context);
    }

    public ProfessionCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProfessionCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        inflate(getContext(), R.layout.layout_prefessor_cam, this);
        tv_sec = (TextView) findViewById(R.id.tv_sec);
        tv_wb = (TextView) findViewById(R.id.tv_wb);
        tv_iso = (TextView) findViewById(R.id.tv_iso);
        tv_focus = (TextView) findViewById(R.id.tv_focus);
        rl_focus = (MRelativeLayout) findViewById(R.id.rl_focus);
        iv_focus = (ImageView) findViewById(R.id.iv_focus);
        professionRegulateView = (ProfessionRegulateView) findViewById(R.id.pre_regulate);
    }

    @Override
    protected void initData() {
        super.initData();
        setIso(getResources().getString(R.string.auto));
        setFocus(getResources().getString(R.string.auto));
        setWb(getResources().getString(R.string.auto));
        tv_sec.setVisibility(GONE);
        initAnim();
    }

    public void setIsC2Support(boolean setIsC2Support){
        this.isC2Support = setIsC2Support;
        professionRegulateView.setIsC2Support(setIsC2Support);
        if (!isC2Support){
            tv_sec.setVisibility(GONE);
        }else{
            tv_sec.setVisibility(VISIBLE);
            setSec(getResources().getString(R.string.auto));
        }
    }

    private void showRegulate() {
        if (professionRegulateView.getVisibility() == VISIBLE) {
            return;
        } else {
            regulateAnimIn.start();
        }
    }

    @Override
    protected void onViewClick() {
        tv_sec.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllColor();
                tv_sec.setTextColor(getResources().getColor(R.color.little_blue));
                showRegulate();
                if (boolean_sec) {
                    regulateAnimOut.start();
                    setAllColor();
                    boolean_sec = false;
                } else {
                    boolean_sec = true;
                    boolean_f = false;
                    boolean_iso = false;
                    boolean_wb = false;
                }
                professionRegulateView.setType(ProfessionCameraView.TYPESEC);
            }
        });
        tv_wb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllColor();
                tv_wb.setTextColor(getResources().getColor(R.color.little_blue));
                showRegulate();
                if (boolean_wb) {
                    regulateAnimOut.start();
                    setAllColor();
                    boolean_wb = false;
                } else {
                    boolean_sec = false;
                    boolean_f = false;
                    boolean_iso = false;
                    boolean_wb = true;
                }
                professionRegulateView.setType(ProfessionCameraView.TYPEWB);
            }
        });
        tv_iso.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllColor();
                tv_iso.setTextColor(getResources().getColor(R.color.little_blue));
                showRegulate();
                if (boolean_iso) {
                    regulateAnimOut.start();
                    setAllColor();
                    boolean_iso = false;
                } else {
                    boolean_sec = false;
                    boolean_f = false;
                    boolean_iso = true;
                    boolean_wb = false;
                }
                professionRegulateView.setType(ProfessionCameraView.TYPEISO);
            }
        });
        rl_focus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllColor();
                iv_focus.setImageDrawable(getResources().getDrawable(R.drawable.focusbtnon));
                tv_focus.setTextColor(getResources().getColor(R.color.little_blue));
                showRegulate();
                if (boolean_f) {
                    regulateAnimOut.start();
                    setAllColor();
                    boolean_f = false;
                } else {
                    boolean_sec = false;
                    boolean_f = true;
                    boolean_iso = false;
                    boolean_wb = false;
                }
                professionRegulateView.setType(ProfessionCameraView.TYPEFOCUS);
            }
        });

        professionRegulateView.setProReListener(new ProfessionRegulateView.ProReListener() {
          /*  @Override
            public void onSeekbar(int type, int num) {
               switch (type){
                   case ProfessionRegulateView.TYPE_SEC:
                       if (prefessorCameraInter!=null){
                           prefessorCameraInter.setSec(num);
                       }
                       break;
                   case ProfessionRegulateView.TYPE_FOCUS:
                       if (prefessorCameraInter!=null){
                           prefessorCameraInter.setFocus(num);
                       }
                       break;
               }
            }*/

            @Override
            public void onIsParmsChanged(boolean isAutoFocus) {
                prefessorCameraInter.setParmsChanged(isAutoFocus);
            }

            @Override
            public void onIso(String num) {
                if (num != null) {
                    if (prefessorCameraInter != null) {
                        prefessorCameraInter.setIso(num);
                    }
                    setIsonum(num);
                   /* if (ProfessionCameraView.this.cameraParm == null) {
                        cameraParm = new CameraParm();
                    }
                    cameraParm.setIso(num);*/
                }
            }

            @Override
            public void onWb(String type) {
                if (type != null) {
                    if (prefessorCameraInter != null) {
                        prefessorCameraInter.setWb(type);
                    }
                    setwbNum(type);
                  /*  if (ProfessionCameraView.this.cameraParm == null) {
                        cameraParm = new CameraParm();
                    }
                    cameraParm.setWhiteBalance(type);*/
                }
            }

            @Override
            public void onFocus(String type) {
                if (type != null) {
                    if (prefessorCameraInter != null) {
                        prefessorCameraInter.setFocus(type);
                    }
                    switch (type) {
                        case Flag.FOCUS_CONTINUOUS:
                            setFocus(getResources().getString(R.string.auto));
                            break;
                        case Flag.FOCUS_MACRO:
                            setFocus(getResources().getString(R.string.macro));
                            break;
                        case Flag.FOCUS_INFINITY:
                            setFocus(getResources().getString(R.string.infinity));
                            break;
                    }
                   /* if (ProfessionCameraView.this.cameraParm == null) {
                        cameraParm = new CameraParm();
                    }
                    cameraParm.setFocalLength(type);*/
                }
            }

            @Override
            public void onSec(String sec) {
                if (!sec.equals("-1")) {
                    if (prefessorCameraInter != null) {
                        prefessorCameraInter.setSec(sec);
                    }
                 /*   if (ProfessionCameraView.this.cameraParm == null) {
                        cameraParm = new CameraParm();
                    }
                    cameraParm.setSec(sec);*/
                }
            }
        });
    }

    private void setAllColor() {
        tv_wb.setTextColor(getResources().getColor(R.color.white));
        tv_sec.setTextColor(getResources().getColor(R.color.white));
        tv_iso.setTextColor(getResources().getColor(R.color.white));
        tv_focus.setTextColor(getResources().getColor(R.color.white));
        iv_focus.setImageDrawable(getResources().getDrawable(R.drawable.focusbtn));
    }

    private void setAllStatus(){
        boolean_f  = boolean_iso = boolean_sec = boolean_wb= false;
    }

    public void setAutoType(boolean b) {
        professionRegulateView.setAutoType(b);
    }

    public void setAutoFocus(boolean b){
        professionRegulateView.setAutoFocus(b);
        if (b){
            setFocusNum(Flag.FOCUS_CONTINUOUS);
        }
    }


    private void setSec(String sec) {
         /*
      * Spannable.SPAN_EXCLUSIVE_EXCLUSIVE：前后都不包括，即在指定范围的前面和后面插入新字符都不会应用新样式
      * Spannable.SPAN_EXCLUSIVE_INCLUSIVE ：前面不包括，后面包括。即仅在范围字符的后面插入新字符时会应用新样式
      * Spannable.SPAN_INCLUSIVE_EXCLUSIVE ：前面包括，后面不包括。
      * Spannable.SPAN_INCLUSIVE_INCLUSIVE ：前后都包括。
      */
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        String title = "Sec ";
        spannableStringBuilder.append(title + sec);
        AbsoluteSizeSpan span = new AbsoluteSizeSpan(16, true);
        spannableStringBuilder.setSpan(span, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_sec.setText(spannableStringBuilder);
    }

    private void setIso(String iso) {
        String title = "ISO ";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(title + iso);
        AbsoluteSizeSpan span = new AbsoluteSizeSpan(16, true);
        spannableStringBuilder.setSpan(span, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_iso.setText(spannableStringBuilder);
    }

    private void setFocus(String f) {
        tv_focus.setText(f);
    }

    private void setWb(String wb) {
        String title = "WB ";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(title + wb);
        AbsoluteSizeSpan span = new AbsoluteSizeSpan(16, true);
        spannableStringBuilder.setSpan(span, 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_wb.setText(spannableStringBuilder);
    }

    private void initAnim() {
        regulateAnimIn = AnimFactory.BottomInAnim(professionRegulateView);
        regulateAnimOut = AnimFactory.BottomOutAnim(professionRegulateView);
        regulateAnimIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (regulateAnimOut != null) {
                    regulateAnimOut.cancel();
                }
                professionRegulateView.setVisibility(View.VISIBLE);
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
        regulateAnimOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (regulateAnimIn != null) {
                    regulateAnimIn.cancel();
                }
                // mGridView.setLayoutParams(new RelativeLayout.LayoutParams((int) mgridviewW, (int) (mGridView.getHeight() + (mgridviewH))));

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                professionRegulateView.setVisibility(View.GONE);
                /*callActivity(MainActivity.BOTTOMIMG_ANIM_OUT);
                bottom_tool_view.setVisibility(View.GONE);*/

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //canClick = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void SetExit(){
        professionRegulateView.setVisibility(View.GONE);
        setAllColor();
        setAllStatus();
    }

    /**
     * 通过当前状态设置数据
     *
     * @param paramsMap
     */
    public void setCurrentParams(HashMap<String, String> paramsMap) {
        //有预设参数,使用预设参数中的相机参数;没有预设参数,使用相机参数
        if (presetParm != null && presetParm.getCameraParm() != null) {
            paramsMap.clear();
            if (presetParm.getCameraParm().getWhiteBalance() != null) {
                paramsMap.put(Flag.WHITEBALANCE, presetParm.getCameraParm().getWhiteBalance());
            }
            if (presetParm.getCameraParm().getFocalLength() != null) {
                paramsMap.put(Flag.FOCUS, presetParm.getCameraParm().getFocalLength());
            }
            if (presetParm.getCameraParm().getIso() != null) {
                paramsMap.put(Flag.ISO, presetParm.getCameraParm().getIso());
            }
        }
        if (paramsMap != null) {
            String iso = paramsMap.get(Flag.ISO);
            if (iso != null && !TextUtils.isEmpty(iso)) {
                setIsonum(iso);
            } else {
                tv_iso.setVisibility(GONE);
            }
            String wb = paramsMap.get(Flag.WHITEBALANCE);
            if (wb != null && !TextUtils.isEmpty(wb)) {
                setwbNum(wb);
            } else {
                tv_wb.setVisibility(GONE);
            }
            String focus = paramsMap.get(Flag.FOCUS);
            if (focus != null && !TextUtils.isEmpty(focus)) {
                setFocusNum(focus);
            } else {
                rl_focus.setVisibility(GONE);
            }
        }
        professionRegulateView.setCurrentParams(paramsMap);
    }

    private void setwbNum(String wb) {
        switch (wb) {
            case Flag.TYPE_AUTO:
                setWb(getResources().getString(R.string.auto));
                break;
            case Flag.TYPE_CLOUD:
                setWb(getResources().getString(R.string.cloud));
                break;
            case Flag.TYPE_FLUORESCENT:
                setWb(getResources().getString(R.string.lamp));
                break;
            case Flag.TYPE_INCANDESCENT:
                setWb(getResources().getString(R.string.bulb));
                break;
            case Flag.TYPE_DAYLIGHT:
                setWb(getResources().getString(R.string.sunshine));
                break;
            case Flag.TYPE_MANUAL:
                setWb(getResources().getString(R.string.manual));
                break;
        }
    }

    private void setFocusNum(String focus) {
        switch (focus) {
            case Flag.FOCUS_CONTINUOUS:
                setFocus(getResources().getString(R.string.auto));
                break;
            case Flag.FOCUS_INFINITY:
                setFocus(getResources().getString(R.string.infinity));
                break;
            case Flag.FOCUS_MACRO:
                setFocus(getResources().getString(R.string.macro));
                break;

        }
    }

    private void setIsonum(String iso) {
        switch (iso) {
            case Flag.TYPE_AUTO:
                setIso(getResources().getString(R.string.auto));
                break;
            case Flag.ISO100:
                setIso("100");
                break;
            case Flag.ISO200:
                setIso("200");
                break;
            case Flag.ISO400:
                setIso("400");
                break;
            case Flag.ISO800:
                setIso("800");
                break;
            case Flag.ISO1600:
                setIso("1600");
                break;
            case Flag.ISO3200:
                setIso("3200");
                break;
        }
    }

    /**
     * 生成相机参数
     */
    public CameraParm setCameraParms() {
        return this.cameraParm;
    }

    /**
     * 获取预设参数
     */
    public void getPresetParms(PresetParm presetParm) {
        this.presetParm = presetParm;
    }

    public interface PrefessorCameraInter {
        void setWb(String type);

        void setIso(String num);

        void setFocus(String num);

        void setSec(String num);

        void setParmsChanged(boolean b);
    }

    public void setPrefessorCameraLister(PrefessorCameraInter prefessorCameraInter) {
        this.prefessorCameraInter = prefessorCameraInter;
    }
}

