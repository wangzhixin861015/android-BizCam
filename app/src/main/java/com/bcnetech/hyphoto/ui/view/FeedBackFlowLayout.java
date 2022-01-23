package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.bcnetech.hyphoto.data.WaterMarkData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a1234 on 2017/11/30.
 */
public class FeedBackFlowLayout extends ViewGroup {
    private FlowLayoutListener listener;
    //记录每个View的位置
    private List<ChildPos> mChildPos = new ArrayList<ChildPos>();
    //private List<SystemTag> selectList = new ArrayList<SystemTag>();

    private class ChildPos {
        int left, top, right, bottom;

        public ChildPos(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }


    public interface FlowLayoutListener {
        void onClick(WaterMarkData drawable);
    }

    public void setOnFlowLayoutListener(FlowLayoutListener listener) {
        this.listener = listener;
    }

    /**
     * 点击相应的tag
     */
    public void getChild() {
        //得到内部元素的个数
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final FeedBackBottomView child = (FeedBackBottomView) getChildAt(count - i - 1);
//            if (child.getCurrentWMD() != null) {
//                if (!StringUtil.isBlank(child.getCurrentWMD().getWatermarkurl())) {
//                    if (CameraStatus.getCameraStatus().getWaterMark().getWaterMarkStatus() == CameraStatus.Size.WATERMARK_BIZCAM) {
//                        child.setSelect(true);
//                    }
//                }else {
//                    if (child.getCurrentWMD().getWatermarkurl().equals(CameraStatus.getCameraStatus().getWaterMark().getWatermarkUrl())) {
//                        child.setSelect(true);
//                    }
//                }
//            }
            child.setWaterMarkBottomListener(new FeedBackBottomView.WaterMarkBottomListener() {
                @Override
                public void onClick(WaterMarkData waterMarkData) {
                    listener.onClick(waterMarkData);
                }
            });
        }
    }

    public void resetSelect(WaterMarkData drawable) {
        if (drawable!=null) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final FeedBackBottomView child = (FeedBackBottomView) getChildAt(count - i - 1);
                child.setSelect(false);
                if (drawable.equals(child.getCurrentWMD())) {
                    child.setSelect(true);
                }
            }
        }
    }


    public class SystemTag {
        Drawable drawable;
        boolean select;

        public Drawable getDrawable() {
            return drawable;
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }

        public boolean isSelect() {
            return select;
        }

        public void setSelect(boolean select) {
            this.select = select;
        }

        @Override
        public String toString() {
            return "SystemTag{" +
                    "drawable=" + drawable +
                    ", select=" + select +
                    '}';
        }
    }

    public FeedBackFlowLayout(Context context) {
        this(context, null);
    }

    public FeedBackFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 最终调用这个构造方法
     *
     * @param context  上下文
     * @param attrs    xml属性集合
     * @param defStyle Theme中定义的style
     */
    public FeedBackFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 测量宽度和高度
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取流式布局的宽度和模式
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //获取流式布局的高度和模式
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //使用wrap_content的流式布局的最终宽度和高度
        int width = 0, height = 0;
        //记录每一行的宽度和高度
        int lineWidth = 0, lineHeight = 0;
        //得到内部元素的个数
        int count = getChildCount();
        mChildPos.clear();
        for (int i = 0; i < count; i++) {
            //获取对应索引的view
            View child = getChildAt(count - i - 1);
            //测量子view的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            //子view占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            //子view占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            //换行
            if (lineWidth + childWidth > widthSize - getPaddingLeft() - getPaddingRight()) {
                //取最大的行宽为流式布局宽度
                width = Math.max(width, lineWidth);
                //叠加行高得到流式布局高度
                height += lineHeight;
                //重置行宽度为第一个View的宽度
                lineWidth = childWidth;
                //重置行高度为第一个View的高度
                lineHeight = childHeight;
                //记录位置
                mChildPos.add(new ChildPos(
                        getPaddingLeft() + lp.leftMargin,
                        getPaddingTop() + height + lp.topMargin,
                        getPaddingLeft() + childWidth - lp.rightMargin,
                        getPaddingTop() + height + childHeight - lp.bottomMargin));
            } else {  //不换行
                //记录位置
                mChildPos.add(new ChildPos(
                        getPaddingLeft() + lineWidth + lp.leftMargin,
                        getPaddingTop() + height + lp.topMargin,
                        getPaddingLeft() + lineWidth + childWidth - lp.rightMargin,
                        getPaddingTop() + height + childHeight - lp.bottomMargin));
                //叠加子View宽度得到新行宽度
                lineWidth += childWidth;
                //取当前行子View最大高度作为行高度
                lineHeight = Math.max(lineHeight, childHeight);
            }
            //最后一个控件
            if (i == count - 1) {
                width = Math.max(lineWidth, width);
                height += lineHeight;
            }
        }

        setMeasuredDimension(
                widthMode == MeasureSpec.EXACTLY ? widthSize : width + getPaddingLeft() + getPaddingRight(),
                heightMode == MeasureSpec.EXACTLY ? heightSize : height + getPaddingTop() + getPaddingBottom());
    }

    /**
     * 让ViewGroup能够支持margin属性
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 设置每个View的位置
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            //第i 个位置
            View child = getChildAt(i);
            //第i个view
            ChildPos pos = mChildPos.get(count - i - 1);
            //设置View的左边、上边、右边边位置
            child.layout(pos.left, pos.top, pos.right, pos.bottom);
        }

    }

    public boolean isHaveView() {
        if (mChildPos.size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 清除所有view
     */
    public void deleteAllView() {
        FeedBackFlowLayout.this.removeAllViews();
    }
}