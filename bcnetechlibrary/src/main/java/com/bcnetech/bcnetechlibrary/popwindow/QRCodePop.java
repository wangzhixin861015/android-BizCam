package com.bcnetech.bcnetechlibrary.popwindow;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetechlibrary.R;
import com.bcnetech.bcnetechlibrary.popwindow.interfaces.PopWindowInterface;

/**
 * Created by wenbin on 16/8/4.
 */

public class QRCodePop extends BasePopWindow {
    private Activity activity;
    private RelativeLayout qrcode_content;
    private ImageView cameral_layout_rl;
    private boolean isShowQr = false;

    private PopWindowInterface popWindowInterface;

    public QRCodePop(Activity activity, PopWindowInterface popWindowInterface) {
        super(activity);
        this.activity = activity;
        this.popWindowInterface = popWindowInterface;
        initView();
        onViewClick();
        initBottomAnim(qrcode_content);
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.qr_pop, null);
        cameral_layout_rl=(ImageView) view.findViewById(R.id.cameral_layout_rl);
        qrcode_content = (RelativeLayout) view.findViewById(R.id.qrcode_content);
        setContentView(view);

    }


    public void getOut(){
        qrcode_content.bringToFront();
    }

    private void onViewClick() {
        qrcode_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowQr){
                    isShowQr = false;
                }else{
                    isShowQr = true;
                }
                if (popWindowInterface != null) {
                    popWindowInterface.OnWBClickListener(isShowQr);
                }
            }
        });
        cameral_layout_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (popWindowInterface != null) {
                    popWindowInterface.OnWBDismissListener();
                }
            }
        });

    }


    @Override
    public void showPop(final View view) {
        inAnimation.start();
        showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void dismissPop() {
        outAnimation.start();
    }
}
