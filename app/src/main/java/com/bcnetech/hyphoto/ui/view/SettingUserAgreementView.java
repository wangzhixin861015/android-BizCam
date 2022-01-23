package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bcnetech.bcnetchhttp.config.UrlConstants;
import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;

/**
 * Created by a1234 on 16/9/11.
 */

public class SettingUserAgreementView extends BaseRelativeLayout {
    private int hight;
    private ValueAnimator outAnim, inAnim;
    private WebView wv_advises;
    private DGProgressDialog dialog;
    private Context context;

    public SettingUserAgreementView(Context context) {
        super(context);
        this.context = context;

    }

    public SettingUserAgreementView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public SettingUserAgreementView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void initView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.setting_agreement_view, null);
        wv_advises =(WebView) view.findViewById(R.id.wv_advises);
        SettingUserAgreementView.this.addView(view);

}
    public void loadPage(){
        wv_advises.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress==100){
                    dissmissMyDialog();
                }
                else{
                    showMyDialog();
                }
            }
        });
        wv_advises.setBackgroundColor(getResources().getColor(R.color.backgroud_new));
        wv_advises.setWebViewClient(new WebViewClient());
        wv_advises.getSettings().setJavaScriptEnabled(true);
        String url = "http://www.shhuiyingapp.com/yszc/yszc.html";
       // String url = UrlConstants.BASE+UrlConstants.USER_PROTOCOL;
        wv_advises.loadUrl(url);
    }
    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();
    }

    public void startAnim(int h) {
        hight = ContentUtil.dip2px(getContext(), h);
        initAnim();
    }

    private void showMyDialog(){
        if(dialog==null){
            dialog=new DGProgressDialog(context, getResources().getString(R.string.waiting));
        }
        dialog.show();
    }
    private void dissmissMyDialog(){
        if(dialog!=null){
            dialog.dismiss();
        }

    }


    private void initAnim() {
        outAnim = AnimFactory.tranYBottomOutAnim(this, hight);
        outAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                SettingUserAgreementView.this.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                SettingUserAgreementView.this.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        inAnim = AnimFactory.tranYBottomInAnim(this, hight);
        inAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                SettingUserAgreementView.this.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                SettingUserAgreementView.this.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}

