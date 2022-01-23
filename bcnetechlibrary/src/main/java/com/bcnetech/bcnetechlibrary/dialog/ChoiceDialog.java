package com.bcnetech.bcnetechlibrary.dialog;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.R;
import com.bcnetech.bcnetechlibrary.util.StringUtil;

/**
 * Created by a1234 on 2018/8/23.
 */
public class ChoiceDialog extends BaseBlurDialog {
    public ChoiceDialog(Context context, CardDialogCallback cardDialogCallback) {
        super(context, cardDialogCallback);
    }

    public ChoiceDialog(Context context) {
        super(context);
    }

    protected void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        //获取浮动窗口视图所在布局
        dialog_viewgroup = (RelativeLayout) inflater.inflate(R.layout.card_dialog_layout, null);
        background = (RelativeLayout) dialog_viewgroup.findViewById(R.id.rl_background);
        cardView = (CardView) dialog_viewgroup.findViewById(R.id.card_view);
        ok = (TextView) dialog_viewgroup.findViewById(R.id.dialog_ok);
        cancel = (TextView) dialog_viewgroup.findViewById(R.id.dialog_cancel);
        dialog_title = (TextView) dialog_viewgroup.findViewById(R.id.dialog_title);
        dialog_message = (TextView) dialog_viewgroup.findViewById(R.id.dialog_message);
    }


    public void setLayout(int custom_layout) {
        LayoutInflater inflater = LayoutInflater.from(context);
        //获取浮动窗口视图所在布局
        dialog_viewgroup = (RelativeLayout) inflater.inflate(custom_layout, null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_ok) {
            cardDialogCallback.onOKClick();
//            dismiss();
        } else if (v.getId() == R.id.dialog_cancel) {
            cardDialogCallback.onCancelClick();
//            dismiss();
        }
    }

    /**
     * 用于分享图片dialog的设置
     *
     * @param hint
     */
    public void setHint(String hint) {
        this.dialog_message.setText(hint);
        this.dialog_message.setTextSize(14);
        this.dialog_message.setTextColor(Color.parseColor("#666666"));
        dialog_viewgroup.invalidate();
    }


    /**
     * 红色title
     *
     * @param title 标题
     */
    public void setAblumTitle(String title) {
        this.dialog_title.setText(title);
        this.dialog_title.setTextColor(Color.parseColor("#FF1800"));
        dialog_viewgroup.invalidate();
    }

    /**
     * 黑色title
     *
     * @param title 标题
     */
    public void setAblumTitleBlack(String title) {
        this.dialog_title.setText(title);
        this.dialog_title.setTextColor(Color.parseColor("#000000"));
        dialog_viewgroup.invalidate();
    }

    /**
     * 相册页面
     *
     * @param message 标题
     */
    public void setAblumMessage(String message) {
        this.dialog_message.setText(Html.fromHtml(message));
        this.dialog_message.setTextSize(14);
        this.dialog_message.setTextColor(Color.parseColor("#666666"));
        dialog_viewgroup.invalidate();
    }

    public void setMessageSizeColor(int size, int color) {
        if (size != 0) {
            this.dialog_message.setTextSize(size);
        }
        if (color != 0) {
            this.dialog_message.setTextColor(color);
        }
    }

    public void setTitleSizeColor(int size, int color) {
        if (size != 0) {
            this.dialog_title.setTextSize(size);
        }
        if (color != 0) {
            this.dialog_title.setTextColor(color);
        }
    }


    /**
     * 相册页面
     *
     * @param message 标题
     */
    public void setAblumMessageGray(String message) {
        this.dialog_message.setText(Html.fromHtml(message));
        this.dialog_message.setTextSize(14);
        this.dialog_message.setTextColor(Color.parseColor("#BBBBBB"));
        dialog_viewgroup.invalidate();
    }

    public void setOk(String text) {
        ok.setText(text);
    }

    public void setCancel(String text) {
        if (!StringUtil.isBlank(text)) {
            this.cancel.setVisibility(View.VISIBLE);
            this.cancel.setText(text);
        } else {
            this.cancel.setVisibility(View.GONE);
        }
    }

    public void setTouchOutside(boolean b){
        this.setCanceledOnTouchOutside(b);
    }


    public static ChoiceDialog createInstance(Context context, CardDialogCallback cardDialogCallback) {
        return new ChoiceDialog(context, cardDialogCallback);
    }

    public static ChoiceDialog createInstance(Context context) {
        return new ChoiceDialog(context);
    }
}
