package com.bcnetech.bcnetechlibrary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.R;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.view.LimitEditText;

/**
 * Created by wenbin on 2016/12/26.
 */

public class EditDialog2 extends Dialog{
    private Context context;
    private TextView title;
    private LimitEditText edit_text;
    private TextView cancel,ok;
    private ClickInterFace clickInterFace;

    public EditDialog2(Context context) {
        super(context);
        this.context=context;
    }

    public EditDialog2(Context context, int themeResId) {
        super(context, themeResId);
        this.context=context;
    }

    protected EditDialog2(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context=context;
    }

    public void setTitle(String text){
        title.setText(text);
    }

    public void setHint(String text){
        edit_text.setHint(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_pop2);getWindow().getDecorView().setBackground(null);
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.dimAmount=0.0f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().getDecorView().setPadding(48, 0, 48, 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        initView();
        onViewClick();

    }



    private void initView(){
        title=(TextView) findViewById(R.id.title);
        edit_text=(LimitEditText) findViewById(R.id.edit_text);
        ok=(TextView) findViewById(R.id.ok);
        cancel=(TextView) findViewById(R.id.cancel);
    }


    private void onViewClick(){
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickInterFace!=null){
                    clickInterFace.cencel();
                }
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edit=edit_text.getText().toString();
                if(StringUtil.isBlank(edit)){
                    ToastUtil.toast(context.getResources().getString(R.string.input_message));
                    return;
                }
                if(clickInterFace!=null){
                    clickInterFace.ok(edit);
                }

            }
        });

    }

    public void setOkText(String hint){
        ok.setText(hint);
    }

    public void diaglogDismiss(){
        edit_text.setText("");
        dismiss();
    }

    public void setClickInterFace(ClickInterFace clickInterFace){
        this.clickInterFace=clickInterFace;
    }

    public interface ClickInterFace{
        void ok(String text);
        void cencel();

    }


}
