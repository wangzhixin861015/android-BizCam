package com.bcnetech.hyphoto.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcnetech.hyphoto.ui.view.GifView;
import com.bcnetech.hyphoto.R;


/**
 * Created by yhf on 18/1/24.
 */

public class LoginDialog extends Dialog{
    private TextView title;
//    private ProgressBar pb_login;
    private GifView gifView;
    private ImageView iv_hint;

    private ChoiceInterface choiceInterface;
    public LoginDialog(Context context) {
        super(context);
    }

    public LoginDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected LoginDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog_layout);
        getWindow().getDecorView().setBackground(null);
        getWindow().getDecorView().setPadding(40, 0, 40, 0);
        this.setCancelable(false);
        initView();
        initData();
        onViewClick();
    }

    private void initView(){
        title = (TextView) findViewById(R.id.dialog_title);
//        pb_login= (ProgressBar) findViewById(R.id.pb_login);
        gifView= (GifView) findViewById(R.id.gif_view);
        iv_hint= (ImageView) findViewById(R.id.iv_hint);

    }

    @Override
    public void show() {
        super.show();
        gifView.setmMovieStart(0);
        if(-1==gifView.getGifResource()){
            gifView.setGifResource(R.mipmap.loading);
        }
    }

    private void initData(){
    }

    private void onViewClick(){


    }

    public void success(String message){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
        gifView.setVisibility(View.GONE);
        iv_hint.setVisibility(View.VISIBLE);
        iv_hint.setImageResource(R.drawable.image_hint_success);
        title.setVisibility(View.VISIBLE);
        title.setText(message);
        title.setTextSize(22);
        title.setTextColor(getContext().getResources().getColor(R.color.sing_in_color));
    }

    public void error(String message){
        gifView.setVisibility(View.GONE);
        iv_hint.setVisibility(View.VISIBLE);
        iv_hint.setImageResource(R.drawable.image_hint_error);
        title.setVisibility(View.VISIBLE);
        title.setText(message);
        title.setTextSize(14);
        title.setTextColor(getContext().getResources().getColor(R.color.red_us_border));
    }



    public void setChoiceInterface(ChoiceInterface choiceInterface){
        this.choiceInterface=choiceInterface;
    }

    /**
     * 创建一个实例
     *
     * @param context
     * @return
     */
    public static LoginDialog createInstance(Context context) {
        return new LoginDialog(context);
    }

    public interface ChoiceInterface{
        void choiceOk();
        void choiceCencel();
    }
}
