package com.bcnetech.bcnetechlibrary.popwindow;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.R;
import com.bcnetech.bcnetechlibrary.popwindow.interfaces.PopWindowInterface;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;

/**
 * Created by wenbin on 16/8/4.
 */

public class EditPop extends BasePopWindow {
    private Activity activity;
    private TextView title;
    private EditText limit_edit_text;
    private TextView cancel,ok;

    private PopWindowInterface popWindowInterface;

    public EditPop(Activity activity,  PopWindowInterface popWindowInterface) {
        super(activity);
        this.activity=activity;
        this.popWindowInterface=popWindowInterface;
        initView();
        initData();
        onViewClick();
    }
    private void initView(){
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.edit_pop,null);
        title=(TextView) view.findViewById(R.id.title);
        limit_edit_text=(EditText) view.findViewById(R.id.limit_edit_text);
        ok=(TextView) view.findViewById(R.id.ok);
        cancel=(TextView) view.findViewById(R.id.cancel);
        setContentView(view);
    }

    private void initData(){
        title.setText("存储为我的光比设定");
    }

    private void onViewClick(){
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                popWindowInterface.OnWBDismissListener();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edit=limit_edit_text.getText().toString();
                if(StringUtil.isBlank(edit)){
                    ToastUtil.toast("请输入内容");
                    return;
                }
                popWindowInterface.OnWBClickListener(edit);
            }
        });

    }



    @Override
    public void showPop(View view) {
        showAsDropDown(view);
    }

    @Override
    public void dismissPop() {

    }
}
