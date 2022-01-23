package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;

/**
 * Created by wenbin on 16/5/16.
 */
public class ChoiceView extends BaseRelativeLayout {
    public final static int TYPE1=1;
    public final static int TYPE2=2;
    private int type=TYPE1;
    private TextView text;
    private ValueAnimator animIn,animOut;
    private RelativeLayout type1;
    private LinearLayout type2;
    private AnimEndListener animEndListener;
    private ChoiceLevelTop choiceLevelTop;
    private ChoicelLevelTwo choicelLevelTwo;
    private TextView cencel_type2;
    private TextView ok_type2;
    public ChoiceView(Context context) {
        super(context);
    }

    public ChoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        inflate(getContext(), R.layout.choice_layout,this);
        type1=(RelativeLayout) findViewById(R.id.type1);
        type2=(LinearLayout) findViewById(R.id.type2);
        text=(TextView) findViewById(R.id.text);
        cencel_type2=(TextView) findViewById(R.id.cencel_type2);
        ok_type2=(TextView) findViewById(R.id.ok_type2);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick() {
        cencel_type2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choiceLevelTop!=null){
                    choiceLevelTop.cencelBut();
                }
            }
        });
        ok_type2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choiceLevelTop!=null){
                    choiceLevelTop.okBut();
                }
            }
        });


        findViewById(R.id.cencel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choicelLevelTwo!=null){
                    choicelLevelTwo.cencelBut();
                }
            }
        });
        findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choicelLevelTwo!=null){
                    choicelLevelTwo.okBut();
                }
            }
        });
    }

    public void setChoiceLevelTop(ChoiceLevelTop choiceLevelTopListenr){
        this.choiceLevelTop=choiceLevelTopListenr;
        cencel_type2.setText(getResources().getString(R.string.cancel));
        ok_type2.setText(getResources().getString(R.string.confirm));
    }
    public void setChoiceLevelTop(ChoiceLevelTop choiceLevelTopListenr,String ok,String cencel){
        setChoiceLevelTop(choiceLevelTopListenr);
        if(!StringUtil.isBlank(cencel)) {
            cencel_type2.setText(cencel);
        }
        if(!StringUtil.isBlank(ok)){
            ok_type2.setText(ok);
        }
    }
    public void setChoicelLevelTwo(ChoicelLevelTwo choicelLevelTwoListenr){
        this.choicelLevelTwo=choicelLevelTwoListenr;
    }

    public void setType(int type){
        switch (type){
            case TYPE1:
                type=TYPE1;
                type1.setVisibility(GONE);
                type2.setVisibility(VISIBLE);
                break;
            case TYPE2:
                type=TYPE2;
                type1.setVisibility(VISIBLE);
                type2.setVisibility(GONE);
                break;
        }

    }

    public int getType(){
        return type;
    }


    public ChoiceView setCencelListener(OnClickListener listenr){
        findViewById(R.id.cencel).setOnClickListener(listenr);
        return this;
    }

    public ChoiceView setOkListener(OnClickListener listener){
        findViewById(R.id.ok).setOnClickListener(listener);
        return this;
    }

    public ChoiceView setCencelType2Listenr(OnClickListener listenr){
        findViewById(R.id.cencel_type2).setOnClickListener(listenr);
        return this;
    }

    public ChoiceView setOkType2Listenr(OnClickListener listenr){
        findViewById(R.id.ok_type2).setOnClickListener(listenr);
        return this;
    }
    public ChoiceView setOkType2Text(String text){
        ((TextView)findViewById(R.id.ok_type2)).setText(text);
        return this;
    }
    public ChoiceView setCencelType2Text(String text){
        ((TextView)findViewById(R.id.cencel_type2)).setText(text);
        return this;
    }

    public ChoiceView setText(String str){
        if(str!=null)
            text.setText(str);
        return this;
    }
    public void initAnim(Animator.AnimatorListener animInListener, Animator.AnimatorListener animOutListener){
        animIn= AnimFactory.BottomInAnim(this);
        animOut=AnimFactory.BottomOutAnim(this);

        if(animInListener!=null){
            animIn.addListener(animInListener);
        }
        else{

        }

        if(animOutListener!=null){
            animOut.addListener(animOutListener);
        }
        else{
            animOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(GONE);
                    if(animEndListener!=null){
                        animEndListener.AnimEnd();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }
    public void startIn(){
        this.setVisibility(VISIBLE);
        if(animIn!=null)
            animIn.start();
    }
    public void startOut(AnimEndListener listener){
        if(animOut!=null)
            animOut.start();

        if(listener!=null)
            animEndListener=listener;


    }




    public interface AnimEndListener{
        void AnimEnd();
    }



    /**
     * 确定取消接口
     */
    public interface ChoiceLevelTop{
        void okBut();
        void cencelBut();
    }

    /**
     * 对错接口
     */
    public interface ChoicelLevelTwo{
        void okBut();
        void cencelBut();
    }
}
