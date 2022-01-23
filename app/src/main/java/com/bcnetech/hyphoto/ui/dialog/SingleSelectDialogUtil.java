package com.bcnetech.hyphoto.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.hyphoto.ui.adapter.WBBaseAdapter;
import com.bcnetech.hyphoto.R;


/**
 * 多个item单选对话框
 *
 * @author xuan
 */
public class SingleSelectDialogUtil extends Dialog {

    private final Activity mActivity;
    private View mContentView;
    private ListView mListView;

    public SingleSelectDialogUtil(Context context) {
        super(context);
        mActivity = (Activity) context;
        loadView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// 背景透明
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);// 不需要标题
        setContentView(mContentView);
    }

    // 获取View
    private void loadView() {
        mContentView = LayoutInflater.from(mActivity).inflate(
                R.layout.cw_dialog_singleselect, null);
        mListView = (ListView) mContentView.findViewById(R.id.listView);
    }

    public static class Builder {
        private final Activity activity;
        private String[] itemTexts;
        private View.OnClickListener[] itemOnClickListeners;

        public Builder(Context context) {
            activity = (Activity) context;
        }

        public Builder setItemTextAndOnClickListener(String[] itemTexts,
                                                     View.OnClickListener[] itemOnClickListeners) {
            this.itemTexts = itemTexts;
            this.itemOnClickListeners = itemOnClickListeners;
            return this;
        }

        public SingleSelectDialogUtil createInstance() {
            final SingleSelectDialogUtil dialog = new SingleSelectDialogUtil(
                    activity);
            if (!StringUtil.isBlanks(itemTexts)) {
                dialog.mListView.setAdapter(new WBBaseAdapter() {
                    @Override
                    public View getView(int postion, View view, ViewGroup arg2) {
                        if (null == view) {
                            view = LayoutInflater.from(activity).inflate(
                                    R.layout.cw_dialog_singleselect_listitem,
                                    null);
                        }

                        String itemText = itemTexts[postion];
                        final View.OnClickListener itemOnClickListener = itemOnClickListeners[postion];

                        View seperator = view.findViewById(R.id.seperator);
                        final TextView itemTv = (TextView) view
                                .findViewById(R.id.itemTv);

                        if (0 == postion) {
                            // 第一item不要分割线
                            seperator.setVisibility(View.GONE);
                        }

                        itemTv.setText(itemText);
                        if (null != itemOnClickListener) {
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    itemOnClickListener.onClick(view);
                                    dialog.dismiss();
                                }
                            });

                        }

                        return view;
                    }

                    @Override
                    public int getCount() {
                        return itemTexts.length;
                    }
                });

            }

            return dialog;
        }
    }

    @Override
    public void show() {
        super.show();
        WindowManager windowManager = mActivity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = (display.getWidth() - dp2px(40)); // 设置宽度
        this.getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);
    }

    // dp转ps
    public int dp2px(int dp) {
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return (int) (metrics.density * dp);
    }

}
