package com.bcnetech.bcnetechlibrary.popwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.R;
import com.bcnetech.bcnetechlibrary.popwindow.interfaces.BlueToothListInterface;
import com.bcnetech.bcnetechlibrary.view.ListviewWaitView;

/**
 * Created by yhf on 2017/3/3.
 */
public class BlueToothListPop extends BasePopWindow {

    private Activity activity;
    private ListView list_view;
    private ListviewWaitView empty;
    private BaseAdapter adapter;
    private BlueToothListInterface blueToothListInterface;
    private RelativeLayout rl_content;
    private View view;
    private RelativeLayout qrcode_content;
    private ImageView iv_content;
    private int type =0;
    private TextView tv_qr_returun;

    public BlueToothListPop(Activity activity, BaseAdapter adapter, BlueToothListInterface blueToothListInterface) {
        super(activity);
        this.activity = activity;
        this.adapter = adapter;
        this.blueToothListInterface = blueToothListInterface;
        initView();
        initData();
        onViewClick();
    }


    private void initView() {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.blue_tooth_list_top, null);
        list_view = (ListView) view.findViewById(R.id.list_view);
        empty = (ListviewWaitView) view.findViewById(R.id.empty);
        rl_content = (RelativeLayout) view.findViewById(R.id.rl_content);
        qrcode_content = (RelativeLayout) view.findViewById(R.id.qrcode_content);
        iv_content = (ImageView) view.findViewById(R.id.iv_content);
        tv_qr_returun = (TextView)view.findViewById(R.id.tv_qr_returun);
        setContentView(view);
    }

    private void initData() {
        list_view.setAdapter(adapter);
        list_view.setEmptyView(empty);
        this.setOutsideTouchable(true);
    }

    private void onViewClick() {
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                blueToothListInterface.onBlueToothClick(position);
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                blueToothListInterface.onBlueToothDissmiss();
            }
        });

///
        qrcode_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0){
                    type =1;
                    rl_content.setVisibility(View.INVISIBLE);
                    tv_qr_returun.setText("切换手动选择");
                }else if(type ==1){
                    type =0;
                    rl_content.setVisibility(View.VISIBLE);
                    tv_qr_returun.setText("切换扫码选择");
                }
                blueToothListInterface.onBlueToothbottomClick(type);
            }
        });
        iv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    @Override
    public void dismissPop() {
    }

    @Override
    public void showPop(View view) {
        Rect visibleFrame = new Rect();
        view.getGlobalVisibleRect(visibleFrame);
        int height = view.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
        setHeight(height);
        showAsDropDown(view);
    }
}
