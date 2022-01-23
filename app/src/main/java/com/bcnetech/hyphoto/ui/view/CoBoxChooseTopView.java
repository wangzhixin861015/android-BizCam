package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.utils.StringUtil;


/**
 * Created by yhf on 2017/3/2.
 */
public class CoBoxChooseTopView extends BaseRelativeLayout {

    private RelativeLayout rl_choose;
    private ImageView iv_down;
    private ImageView iv_market;
    private TextView tv_choose;
    private int topViewType = TYPE_NORMAL;
    private LinearLayout ll_left;
    private ImageView cobox_btn;

    //蓝牙关闭
    public static final int BLUE_TOUCH_CLOSE = 1;
    //蓝牙开启
    public static final int BLUE_TOUCH_OPEN = 2;
    //蓝牙连接
    public static final int BLUE_TOUCH_CONNECTION = 3;
    //蓝牙断开
    public static final int BLUE_TOUCH_CONNECTION_ERROR = 4;


    //预设参数
    public static final int TYPE_PRESET = 1;
    //录像
    public static final int TYPE_CAMERA = 2;
    //主页面
    public static final int TYPE_NORMAL = 3;
    //专业模式
    public static final int TYPE_PRO = 4;

    public static final int TYPE_PRESET_CAMERA = 5;

    public CoBoxChooseTopView(Context context) {
        super(context);
    }

    public CoBoxChooseTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CoBoxChooseTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.cobox_choose_top_view, this);
        rl_choose = (RelativeLayout) findViewById(R.id.rl_choose);
        iv_down = (ImageView) findViewById(R.id.iv_down);
        iv_market = (ImageView) findViewById(R.id.iv_market);
        tv_choose = (TextView) findViewById(R.id.tv_choose);
        ll_left = (LinearLayout) findViewById(R.id.ll_left);
        cobox_btn = (ImageView) findViewById(R.id.cobox_btn);
    }

    public void setDownLeftBtnClick(View.OnClickListener listener) {
        iv_down.setOnClickListener(listener);
    }

    public void setMarketLeftBtnClick(View.OnClickListener listener) {
        iv_market.setOnClickListener(listener);
    }


    public void setConnectBluetoothName(String name) {
        if (!StringUtil.isBlank(name)) {
            tv_choose.setTextColor(getResources().getColor(R.color.white));
        }
        tv_choose.setText(name);
    }

    public void setCoboxConnectClickEnabled(boolean canClick) {
        if (canClick) {
            cobox_btn.setAlpha(1f);

        } else {
            cobox_btn.setAlpha(0.5f);
        }
        ll_left.setEnabled(canClick);
    }

    public void setCenterClick(View.OnClickListener listener) {
        rl_choose.setOnClickListener(listener);
    }


    public int getTopViewType() {
        return topViewType;
    }

    /**
     * 设置当前蓝牙状态
     *
     * @param blueTouchType 蓝牙状态
     */
    public void setBlueTouchType(int blueTouchType) {
        switch (blueTouchType) {
            case BLUE_TOUCH_CLOSE:
                cobox_btn.setImageResource(R.drawable.coboxoff);
                tv_choose.setTextColor(getResources().getColor(R.color.text_color1));
                break;
            case BLUE_TOUCH_OPEN:
                break;
            case BLUE_TOUCH_CONNECTION:
                cobox_btn.setImageResource(R.drawable.coboxon);
                break;
            case BLUE_TOUCH_CONNECTION_ERROR:
                cobox_btn.setImageResource(R.drawable.coboxoff);
                tv_choose.setTextColor(getResources().getColor(R.color.text_color1));
                break;
        }
    }
}
