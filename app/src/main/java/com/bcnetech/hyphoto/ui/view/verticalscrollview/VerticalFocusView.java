package com.bcnetech.hyphoto.ui.view.verticalscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.ui.view.scaleview.VerticalScaleScrollFocusView;


/**
 * Created by yhf on 2017/10/25.
 */

public class VerticalFocusView extends BaseRelativeLayout {
    private final static int TYPE_INIT = 1; //初始类型
    private final static int ONE_MOVE=2;//单只手指移动
    private int type=TYPE_INIT;
    private VerticalScaleScrollFocusView vScaleScrollView;
    private TextView tv_num;
    private float downY, downX;
    private final static int TYPE_LIMIT = 15;//类型限制临界值
    private ScrollNumListener scrollNumListenr;

    public VerticalFocusView(Context context) {
        super(context);
    }

    public VerticalFocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalFocusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.vertical_scroll_focus_layout,this);
        tv_num= (TextView) findViewById(R.id.tv_num);
        vScaleScrollView = (VerticalScaleScrollFocusView) findViewById(R.id.verticalScale);

    }

    @Override
    protected void onViewClick() {
        super.onViewClick();
        vScaleScrollView.setOnScrollListener(new VerticalScaleScrollFocusView.OnScrollListener() {
            @Override
            public void onScaleScroll(int scale) {
                tv_num.setText(""+scale);
                scrollNumListenr.getNumber(scale);
            }
        });

        vScaleScrollView.setScrollFocusListener(new VerticalScaleScrollFocusView.ScrollFocusListener() {
            @Override
            public void onStartUpDown(MotionEvent event) {
                scrollNumListenr.onStartUpDown(event);
            }

            @Override
            public void onUpDown(MotionEvent event) {
                scrollNumListenr.onUpDown(event);
            }

            @Override
            public void onEndUpDown(MotionEvent event) {
                scrollNumListenr.onEndUpDown(event);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return vScaleScrollView.onTouchEvent(event);
    }


    private double getSqrt(double startx, double starty, double endx, double endy) {
        return (startx - endx) * (startx - endx) + (starty - endy) * (starty - endy);
    }


    public void setAeNum(int max,int min,int num){
        vScaleScrollView.setAeNum(max,min,null,num);
    }



     public void setRangeSize(int rangeSize){
         vScaleScrollView.setRangeSize(rangeSize);
     }
    //设置进度
    public void setProgress(int progress){
        vScaleScrollView.scrollToScale(progress);
    }

    public void setFinaly(int var){
        vScaleScrollView.setFinalY(var);
    }

    public void setSize(float size){
        vScaleScrollView.resize(size);
    }

    public void invate(){
        vScaleScrollView.invalidate();
    }



    public interface ScrollNumListener{
        void getNumber(int num);

        void onStartUpDown(MotionEvent event);

        void onUpDown(MotionEvent event);

        void onEndUpDown(MotionEvent event);
    }

    public ScrollNumListener getScrollNumListenr() {
        return scrollNumListenr;
    }

    public void setScrollNumListenr(ScrollNumListener scrollNumListenr) {
        this.scrollNumListenr = scrollNumListenr;
    }


}
