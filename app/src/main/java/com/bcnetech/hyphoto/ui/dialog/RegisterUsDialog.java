package com.bcnetech.hyphoto.ui.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.R;


/**
 * Created by yhf on 18/1/24.
 */

public class RegisterUsDialog extends ProgressDialog{
    private TextView title;
    private TextView tvAccount;
    private ImageView ivClose;
    private TextView tv_three;
    private TextView tv_four;
    private ImageView iv_registerLogo;
    private ImageView iv_bceasy;
    private RelativeLayout rl_main;
    private TextView tv_title;
    private TextView tv_login;
    private TextView tv_one;
    private TextView tv_two;
    private TextView tv_twoUpload;
    private TextView tv_message;

    public static final int  FORGEST_PWD=1;
    public static final int  RREGISTER=2;
    public static final int  UPLOAD=3;

    private ChoiceInterface choiceInterface;
    public RegisterUsDialog(Context context) {
        super(context);
    }

    public RegisterUsDialog(Context context, int themeResId) {
        super(context, themeResId);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_dialog_layout);
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.dimAmount=0.0f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().getDecorView().setBackground(null);
//        this.setCancelable(false);


//        Window window = getWindow();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//        window.setAttributes(lp);
        initView();
        initData();
        onViewClick();
    }

    private void initView(){
        title = (TextView) findViewById(R.id.dialog_title);
        tvAccount= (TextView) findViewById(R.id.tv_account);
        ivClose= (ImageView) findViewById(R.id.iv_close);
        tv_three= (TextView) findViewById(R.id.tv_three);
        tv_four= (TextView) findViewById(R.id.tv_four);
        iv_registerLogo= (ImageView) findViewById(R.id.iv_registerLogo);
        iv_bceasy= (ImageView) findViewById(R.id.iv_bceasy);
        rl_main= (RelativeLayout) findViewById(R.id.rl_main);
        tv_title= (TextView) findViewById(R.id.tv_title);
        tv_login= (TextView) findViewById(R.id.tv_login);
        tv_one= (TextView) findViewById(R.id.tv_one);
        tv_two= (TextView) findViewById(R.id.tv_two);
        tv_twoUpload= (TextView) findViewById(R.id.tv_twoUpload);
        tv_message= (TextView) findViewById(R.id.tv_message);
    }

    private void initData(){
    }

    private void onViewClick(){
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceInterface.goLogin();
            }
        });
//        tvAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceInterface.close();
            }
        });

    }

    public void setType(int type){
        if(type==FORGEST_PWD){
            tv_three.setText("To confirm your email address to retrieve");
            tv_four.setText("your password.");
            tv_title.setText("Retrieve");
            tv_login.setVisibility(View.GONE);
            iv_bceasy.setVisibility(View.VISIBLE);
            iv_registerLogo.setVisibility(View.VISIBLE);
            rl_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    choiceInterface.close();
                }
            });
        }else if(type==RREGISTER){
            tv_title.setText("Final Step...");
            tv_three.setText("To confirm your email address to retrieve");
            tv_four.setText("your password.");
            tv_login.setVisibility(View.VISIBLE);
            iv_bceasy.setVisibility(View.GONE);
            iv_registerLogo.setVisibility(View.GONE);
        }else {
            tv_title.setText(getContext().getString(R.string.to_backup_dialog_title));
            tv_one.setText(getContext().getString(R.string.to_backup_dialog_message_one));
            tv_two.setText(getContext().getString(R.string.to_backup_dialog_message_two));
            tv_twoUpload.setText(getContext().getString(R.string.to_backup_dialog_message_three));

            tv_three.setVisibility(View.GONE);
            tv_four.setVisibility(View.GONE);
            iv_bceasy.setVisibility(View.GONE);
            iv_registerLogo.setVisibility(View.GONE);
            tv_twoUpload.setVisibility(View.VISIBLE);
            tv_message.setVisibility(View.VISIBLE);
            tv_login.setVisibility(View.GONE);
            ivClose.setVisibility(View.GONE);
            rl_main.setBackground(getContext().getDrawable(R.drawable.syn_uploadbg));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ImageUtil.Dp2Px(getContext(),412));
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            rl_main.setLayoutParams(new RelativeLayout.LayoutParams(params));
            this.setCancelable(true);
        }

    }

    public void setAccount(String account){
        tvAccount.setText(Html.fromHtml(account));
    }

    public void setAccountListener(View.OnClickListener listener){
        tvAccount.setOnClickListener(listener);
    }

    public void success(String message){

        title.setVisibility(View.VISIBLE);
        title.setText(message);
    }



    public void setClickLitner(ChoiceInterface choiceInterface){
        this.choiceInterface=choiceInterface;
    }

    /**
     * 创建一个实例
     *
     * @param context
     * @return
     */
    public static RegisterUsDialog createInstance(Context context) {
        return new RegisterUsDialog(context);
    }

    public interface ChoiceInterface{
        void close();
        void goLogin();
    }
}
