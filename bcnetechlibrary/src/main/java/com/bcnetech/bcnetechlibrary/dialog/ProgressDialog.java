package com.bcnetech.bcnetechlibrary.dialog;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.R;
import com.bcnetech.bcnetechlibrary.util.ImageUtil;
import com.bcnetech.bcnetechlibrary.view.CustomStatusView;

/**
 * Created by a1234 on 2018/8/27.
 */

public class ProgressDialog extends BaseBlurDialog {
    private CustomStatusView custom_status;
    private TextView tv_content;
    private ImageView gif_loading;
    public boolean isGif = false;
    public static final int TYPE_LOADING =112;
    public static final int TYPE_SUCCESS =113;
    public static final int TYPE_FAIL =114;
    public static final int TYPE_REGISTER_SUCCESS =115;
    public static final int TYPE_ABLUM_SELECT=116;
    public static final int TYPE_IMAGE_SELECT=117;
    public static final int TYPE_UPLOAD=118;
    public static final int TYPE_UT_LOGIN_SUCCESS=119;
    public static final int TYPE_UT_LOGIN_FAIL=120;

    private DGProgressListener dgProgressListener;
    public ProgressDialog(Context context, CardDialogCallback cardDialogCallback) {
        super(context, cardDialogCallback);
    }

    public ProgressDialog(Context context) {
        super(context);
    }

    public ProgressDialog(Context context, DGProgressListener dgProgressListener, boolean isGif) {
        super(context);
        this.isGif = isGif;
        this.dgProgressListener = dgProgressListener;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void init(Context context) {
        super.init(context);
    }

    @Override
   protected void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        dialog_viewgroup = (RelativeLayout) inflater.inflate(R.layout.progress_dialog_layout, null);
        background = (RelativeLayout) dialog_viewgroup.findViewById(R.id.rl_background);
        cardView = (CardView) dialog_viewgroup.findViewById(R.id.card_view);
        custom_status= dialog_viewgroup.findViewById(R.id.custom_status);
        tv_content = (TextView)dialog_viewgroup.findViewById(R.id.tv_content);
        gif_loading = (ImageView)dialog_viewgroup.findViewById(R.id.gif_loading);
        setIsGif(isGif);
        custom_status.setCustomLister(new CustomStatusView.CustomLister() {
            @Override
            public void onAnimStart() {
                dgProgressListener.onAnim();
            }

            @Override
            public void onSuccessAnimEnd() {
                dgProgressListener.onSuccessAnimed();
            }

            @Override
            public void onFailAnimEnd() {
                dgProgressListener.onFailAnimed();
            }
        });
    }

    private void setIsGif(boolean isGif){
        if (isGif){
            gif_loading.setVisibility(View.VISIBLE);
            custom_status.setVisibility(View.GONE);
        }else{
            gif_loading.setVisibility(View.GONE);
            custom_status.setVisibility(View.VISIBLE);
        }
    }

    public void setText(boolean isSuccess,String text){
        if (isSuccess) {
            tv_content.setTextColor(Color.GREEN);
        }else{
            tv_content.setTextColor(Color.RED);
        }
        tv_content.setText(text);
    }

    public void setText(String text,int type){
        switch (type){
            case TYPE_ABLUM_SELECT:
                tv_content.setText(text);
                tv_content.setTextColor(Color.BLACK);
                tv_content.setTextSize(14);
                break;
            case TYPE_UT_LOGIN_SUCCESS:
                tv_content.setText(text);
                tv_content.setTextColor(Color.BLACK);
                break;
            case TYPE_UT_LOGIN_FAIL:
                tv_content.setText(text);
                tv_content.setTextColor(Color.BLACK);
                break;
        }

    }

    public void setType(int type){
        switch (type){
            case TYPE_LOADING:
                custom_status.setVisibility(View.VISIBLE);
                gif_loading.setVisibility(View.GONE);
                custom_status.loadLoading();
                this.setCanceledOnTouchOutside(false);
                break;
            case TYPE_SUCCESS:
                custom_status.setVisibility(View.VISIBLE);
                gif_loading.setVisibility(View.GONE);
                custom_status.loadSuccess();
                this.setCanceledOnTouchOutside(false);
                break;
            case TYPE_REGISTER_SUCCESS:
                custom_status.setVisibility(View.VISIBLE);
                gif_loading.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                custom_status.loadSuccess();
                this.setCanceledOnTouchOutside(false);
                break;
            case TYPE_FAIL:
                custom_status.setVisibility(View.VISIBLE);
                gif_loading.setVisibility(View.GONE);
                custom_status.loadFailure();
                this.setCanceledOnTouchOutside(false);
                break;
            case TYPE_ABLUM_SELECT:
                custom_status.setVisibility(View.GONE);
                gif_loading.setImageResource(R.drawable.select_hint);
                gif_loading.setVisibility(View.VISIBLE);
                this.setCanceledOnTouchOutside(true);
                break;
            case TYPE_IMAGE_SELECT:
                custom_status.setVisibility(View.GONE);
                gif_loading.setImageResource(R.drawable.image_select);
                gif_loading.setVisibility(View.VISIBLE);
                this.setCanceledOnTouchOutside(false);
                break;
            case TYPE_UPLOAD:
                custom_status.setVisibility(View.GONE);
                gif_loading.setImageResource(R.drawable.syn_upload);
                gif_loading.setVisibility(View.VISIBLE);
                this.setCanceledOnTouchOutside(true);
                break;
            case TYPE_UT_LOGIN_SUCCESS:
                gif_loading.setVisibility(View.VISIBLE);
                custom_status.setVisibility(View.GONE);
                gif_loading.setImageResource(R.drawable. bizcam_ut_loging_success);
                RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) gif_loading.getLayoutParams();
                params.height= ImageUtil.Dp2Px(context,40);
                params.width=ImageUtil.Dp2Px(context,40);
                params.topMargin=ImageUtil.Dp2Px(context,40);
                gif_loading.setLayoutParams(params);
                this.setCanceledOnTouchOutside(true);
                break;
            case TYPE_UT_LOGIN_FAIL:
                gif_loading.setVisibility(View.VISIBLE);
                custom_status.setVisibility(View.GONE);
                gif_loading.setImageResource(R.drawable.bizcam_ut_loging_fail);
                this.setCanceledOnTouchOutside(true);

                RelativeLayout.LayoutParams param= (RelativeLayout.LayoutParams) gif_loading.getLayoutParams();
                param.height= ImageUtil.Dp2Px(context,40);
                param.width=ImageUtil.Dp2Px(context,40);
                param.topMargin=ImageUtil.Dp2Px(context,40);
                gif_loading.setLayoutParams(param);
                break;
        }
    }

    public interface DGProgressListener{
        void onSuccessAnimed();
        void onFailAnimed();
        void onAnim();
    }

    /**
     * 创建一个实例
     *
     * @param context
     * @return
     */
    public static DGProgressDialog2 createInstance(Context context) {
        return new DGProgressDialog2(context);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void setCardRadius(int radius) {
        super.setCardRadius(radius);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void isNeedBlur(boolean needBlur) {
        super.isNeedBlur(needBlur);
    }

    @Override
    protected void setOk(String text) {

    }

    @Override
    protected void setCancel(String text) {

    }
}
