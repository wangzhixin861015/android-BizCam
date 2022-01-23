package com.bcnetech.hyphoto.ui.view.verticalscrollview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.CameraParamType;
import com.bcnetech.hyphoto.utils.CameraParamUtil;


/**
 * Created by yhf on 2018/7/13.
 */

public class VerticalScaleNewView extends BaseRelativeLayout {

    private VerticalSeekBar vScaleScrollView;
    private TextView tv_num;
    private ScrollNumListener scrollNumListenr;
    private CameraParamType cameraParamType;

    public VerticalScaleNewView(Context context) {
        super(context);
    }

    public VerticalScaleNewView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalScaleNewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.vertical_scroll_new_view_layout, this);
        tv_num = (TextView) findViewById(R.id.tv_num);
        vScaleScrollView = (VerticalSeekBar) findViewById(R.id.seekBar);
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();

    }

    //设置进度
    public void setProgress(int progress) {
        vScaleScrollView.setProgress(progress);

        if (null != cameraParamType) {
            if (cameraParamType.getType() == 0) {
                tv_num.setText(CameraParamUtil.getExposureTime(cameraParamType, (progress / 2)));
            } else if (cameraParamType.getType() == 1) {
                tv_num.setText(CameraParamUtil.getIso(cameraParamType, (progress / 2)));
            } else if (cameraParamType.getType() == 2) {
                tv_num.setText(CameraParamUtil.getWHITEBALANCE(cameraParamType, (progress / 2)));
            } else if (cameraParamType.getType() == 3) {
                tv_num.setText(CameraParamUtil.getFOCUS(cameraParamType, (progress / 2)));
            }else if (cameraParamType.getType() == 4) {
                tv_num.setText(((progress -100)>0)?"+"+((progress -100)/30)+" EV":""+((progress -100)/30)+" EV");

            }
        } else {
            tv_num.setText("" + (progress / 2));
        }
    }




    //设置中心点
    public void setProgressAndDefaultPoint(float defaultPoint, double progress, CameraParamType cameraParamType) {
        this.cameraParamType = cameraParamType;

        vScaleScrollView.setProgressAndDefaultPoint(200 - (defaultPoint * 2), (int)progress);
        if (null != cameraParamType) {
            if (cameraParamType.getType() == 0) {
                tv_num.setText(CameraParamUtil.getExposureTime(cameraParamType, (int) (progress / 2)));
            } else if (cameraParamType.getType() == 1) {
                tv_num.setText(CameraParamUtil.getIso(cameraParamType, (int) (progress / 2)));
            } else if (cameraParamType.getType() == 2) {
                tv_num.setText(CameraParamUtil.getWHITEBALANCE(cameraParamType, (int) (progress / 2)));
            } else if (cameraParamType.getType() == 3) {
                tv_num.setText(CameraParamUtil.getFOCUS(cameraParamType, (int) (progress / 2)));
            }
        } else {
            tv_num.setText(""+(progress / 2));
        }
    }


    /**
     * 参数调整
     * 设置中心点
     */
    public void setParamProgressAndDefaultPoint(float defaultPoint, double progress) {
        vScaleScrollView.setProgressAndDefaultPoint(200 - (defaultPoint ), (int) (progress ));
        if(tv_num.getText().length()!=(""+(int)(progress -100)).length()){
            if((""+(int)(progress -100)).length()>5){
                tv_num.setTextSize(12);
            }else {
                tv_num.setTextSize(14);
            }
        }
        tv_num.setText(((int)(progress -100)>0)?"+"+((int)(progress -100)):""+((int)(progress -100)));

    }


    /**
     * 参数调整
     * 设置中心点
     */
    public void setParamProgressAndDefaultPoint2(float defaultPoint, double progress) {
        this.cameraParamType=new CameraParamType();
        cameraParamType.setType(4);
        vScaleScrollView.setProgressAndDefaultPoint(200 - (defaultPoint ), (int) (progress ));
        tv_num.setText(((progress -100)>0)?"+"+((progress -100)/30)+" EV":""+((progress -100)/30)+" EV");
    }


    public interface ScrollNumListener {
        void getNumber(int num);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(-90);
        super.onDraw(canvas);
    }

    public ScrollNumListener getScrollNumListenr() {
        return scrollNumListenr;
    }

    public void setScrollNumListenr(ScrollNumListener scrollNumListenr) {
        this.scrollNumListenr = scrollNumListenr;
    }
}
