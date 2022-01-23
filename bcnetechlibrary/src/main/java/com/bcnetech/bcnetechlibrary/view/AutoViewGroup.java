package com.bcnetech.bcnetechlibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.bcnetech.bcnetechlibrary.R;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenbin on 16/10/8.
 */

public class AutoViewGroup extends ViewGroup {

    /**
     * 左间距
     */
    private int paddingLeft = 0;
    /**
     * 右间距
     */
    private int paddingRight = 0;
    /**
     *
     */
    private int paddingTop = 0;
    /**
     *
     */
    private int paddingBottom = 0;

    private int childSuggestH= ContentUtil.dip2px(getContext(),28);
    /**
     * 水平方向间距
     */
    private int horizontalSpace = ContentUtil.dip2px(getContext(),10);
    /**
     * 行间距
     */
    private int verticalSpace = ContentUtil.dip2px(getContext(),25);


    private List<Integer> listX;
    private List<Integer> listY;

    public AutoViewGroup(Context context) {
        super(context);

    }

    public AutoViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AutoViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int width = getWidth();


        int startOffsetX = paddingLeft;// 横坐标开始
        int startOffsety = 0;//纵坐标开始
        int rowCount = 1;

        int preEndOffsetX = startOffsetX;

        for (int i = 0; i < count; i++) {
            final View childView = getChildAt(i);

            int w = childView.getMeasuredWidth();
            int h = childView.getMeasuredHeight();

            int x = listX.get(i);
            int y = listY.get(i);

            // 布局子控件
            childView.layout(x, y, x + w, y + h);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int count = getChildCount();
        int width = measureWidth(widthMeasureSpec);


        int startOffsetX = paddingLeft;// 横坐标开始
        int startOffsety = 0 + paddingTop;//纵坐标开始
        int rowCount = 1;

        int preEndOffsetX = startOffsetX;

        listX.clear();
        listY.clear();
        for (int i = 0; i < count; i++) {
            final View childView = getChildAt(i);
            // 设置子空间Child的宽高
            childView.measure(0, 0);
                         /* 获取子控件Child的宽高 */
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childSuggestH;
            preEndOffsetX = startOffsetX + childWidth /*+ CHILD_MARGIN*/;
            //TODO [yaojian]margin属性？
            if (preEndOffsetX > width - paddingRight) {
                if (startOffsetX > paddingLeft) {
                                        /* 换行  */
                    startOffsetX = paddingLeft;
                    startOffsety += childHeight + verticalSpace;
                    rowCount++;
                }
            }
            listX.add(startOffsetX);
            listY.add(startOffsety);

            //            childView.layout(startOffsetX, startOffsety, preEndOffsetX, startOffsety+childHeight);
            startOffsetX = startOffsetX + childWidth + horizontalSpace;
        }
        int lastLineHeight = 0;
        View lastChild = getChildAt(count - 1);
        if (null != lastChild) {
            lastLineHeight = childSuggestH;
        }
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), startOffsety + lastLineHeight + paddingBottom);
        //        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 注意setMeasuredDimension和resolveSize的用法
        //        setMeasuredDimension(resolveSize(measuredWidth, widthMeasureSpec),
        //                resolveSize(top, heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);


        // Default size if no limits are specified.
        int result = 400;

        if (specMode == MeasureSpec.AT_MOST) {
            // Calculate the ideal size of your control
            // within this maximum size.
            // If your control fills the available space
            // return the outer bound.
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            // If your control can fit within these bounds return that value.
            result = specSize;
        }
        return result;
    }

    private void init(AttributeSet attrs) {
        TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.AutoLineFeedLayout);
        int attrCount = attrArray.getIndexCount();
        for (int i = 0; i < attrCount; i++) {
            int attrId = attrArray.getIndex(i);
            if (attrId == R.styleable.AutoLineFeedLayout_horizontalSpacing) {
                float dimen = attrArray.getDimension(attrId, 0);
                horizontalSpace = (int) dimen;

            } else if (attrId == R.styleable.AutoLineFeedLayout_verticalSpacing) {
                float dimen = attrArray.getDimension(attrId, 0);
                verticalSpace = (int) dimen;

            } else if (attrId == R.styleable.AutoLineFeedLayout_paddingBottom) {
                float dimen = attrArray.getDimension(attrId, 0);
                paddingBottom = (int) dimen;

            } else if (attrId == R.styleable.AutoLineFeedLayout_paddingLeft) {
                float dimen = attrArray.getDimension(attrId, 0);
                paddingLeft = (int) dimen;

            } else if (attrId == R.styleable.AutoLineFeedLayout_paddingRight) {
                float dimen = attrArray.getDimension(attrId, 0);
                paddingRight = (int) dimen;

            } else if (attrId == R.styleable.AutoLineFeedLayout_paddingTop) {
                float dimen = attrArray.getDimension(attrId, 0);
                paddingTop = (int) dimen;

            } else if (attrId == R.styleable.AutoLineFeedLayout_debug) {

            } else {
            }

        }

        listX = new ArrayList<Integer>();
        listY = new ArrayList<Integer>();
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public void setChildSuggestH(int childSuggestH) {
        this.childSuggestH = childSuggestH;
    }

    public void setHorizontalSpace(int horizontalSpace) {
        this.horizontalSpace = horizontalSpace;
    }

    public void setVerticalSpace(int verticalSpace) {
        this.verticalSpace = verticalSpace;
    }

    public void setListX(List<Integer> listX) {
        this.listX = listX;
    }

    public void setListY(List<Integer> listY) {
        this.listY = listY;
    }
}