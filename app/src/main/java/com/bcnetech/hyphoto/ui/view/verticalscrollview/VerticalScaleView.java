package com.bcnetech.hyphoto.ui.view.verticalscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.ui.view.scaleview.VerticalScaleScrollView;
import com.bcnetech.hyphoto.R;


/**
 * Created by yhf on 2017/10/25.
 */

public class VerticalScaleView extends BaseRelativeLayout {
    private final static int TYPE_INIT = 1; //初始类型
    private final static int ONE_MOVE=2;//单只手指移动
    private int type=TYPE_INIT;
    private VerticalScaleScrollView vScaleScrollView;
    private TextView tv_num;
    private float downY, downX;
    private final static int TYPE_LIMIT = 15;//类型限制临界值
    private ScrollNumListener scrollNumListenr;

    public VerticalScaleView(Context context) {
        super(context);
    }

    public VerticalScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.vertical_scroll_view_layout,this);
        tv_num= (TextView) findViewById(R.id.tv_num);
        vScaleScrollView = (VerticalScaleScrollView) findViewById(R.id.verticalScale);
        vScaleScrollView.setOnScrollListener(new VerticalScaleScrollView.OnScrollListener() {
            @Override
            public void onScaleScroll(int scale) {
                tv_num.setText(""+scale);
                scrollNumListenr.getNumber(scale);
            }
        });
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getAction() & MotionEvent.ACTION_MASK;
//        int nCnt = event.getPointerCount();
//        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
//            if (type==ONE_MOVE){
//               setVisibility(GONE);
//            }
//            type = TYPE_INIT;
//        }//第一个点按下，开启滚动模式，记录开始滚动的点
//        else if (action == MotionEvent.ACTION_DOWN) {
//            if(type == TYPE_INIT){
//                downY = event.getY();
//                downX = event.getX();
////                if (gestureDetector != null) {
////                    gestureDetector.onStartOneMove(downX,downY,event);
//                type=ONE_MOVE;
////                }
//            }
//        }
//        else if (action == MotionEvent.ACTION_MOVE) {
//            if(nCnt == 1 && type == ONE_MOVE){
//                if(getSqrt(downX, downY, event.getX(), event.getY()) > TYPE_LIMIT * TYPE_LIMIT){
//                    if (Math.abs(downX - event.getX()) < Math.abs(downY -  event.getY())) {
//                        setVisibility(VISIBLE);
//                    }
//                }
//            }
//        }
        return vScaleScrollView.onTouchEvent(event);
    }

    private double getSqrt(double startx, double starty, double endx, double endy) {
        return (startx - endx) * (startx - endx) + (starty - endy) * (starty - endy);
    }

    public void setProgresssee(int i){
        setProgress(i);
//        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    //设置进度
    public void setProgress(int progress){
        progress -=100;
        //this.mDefaultThumbOffSet = progress;
        /*if(progress == 0){
            mThumbOffset = formatDouble(100/200*(mDistance))+mThumbWidth/2;
        }else *//*if(progress >= 0){
            mThumbOffset = formatDouble((100 + progress)/200*(mDistance))+mThumbWidth/2;
        }else if(progress < 0){
            mThumbOffset = formatDouble((100 + progress)/200*(mDistance))+mThumbWidth/2;
        }*/
//        mThumbOffset = formatDouble((100 + progress)/200*(mDistance))+mThumbWidth/2;
//        refresh();
        vScaleScrollView.scrollToScale(progress);

    }

    public void setFinaly(int var){
        var -=100;
        vScaleScrollView.setFinalY(var);
    }

    public void setSize(float size){
        vScaleScrollView.resize(size);
    }

    public interface ScrollNumListener{
        void getNumber(int num);
    }

    public ScrollNumListener getScrollNumListenr() {
        return scrollNumListenr;
    }

    public void setScrollNumListenr(ScrollNumListener scrollNumListenr) {
        this.scrollNumListenr = scrollNumListenr;
    }
}
