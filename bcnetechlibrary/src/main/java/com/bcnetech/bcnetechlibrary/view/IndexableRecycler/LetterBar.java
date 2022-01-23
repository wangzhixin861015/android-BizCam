package com.bcnetech.bcnetechlibrary.view.IndexableRecycler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bcnetech.bcnetechlibrary.R;

import java.util.ArrayList;
import java.util.List;

public class LetterBar extends View {
    public static final int SECTION_SIZE = 27;

    private int mTextColor = Color.RED;
    private float mTextSize = 20.0f;

    private float mItemHeight;
    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingRight;
    private int mPaddingBottom;
    private int mContentWidth;
    private int mContentHeight;
    private float mCenter;
    private String text;
    private List<String> mlist;

    private final static List<String> mSections = new ArrayList<>(SECTION_SIZE);
    private static int mSectionSize;

    private OnLetterSelectListener mOnLetterSelectListener;

    private TextPaint mTextPaint;
    private TextPaint textPaint;

    static {
        char character = 'A';
        mSections.add("#");
        for (int i = 0; i < SECTION_SIZE - 1; i++) {
            mSections.add(String.valueOf((char) (character + i)));
        }
        mSectionSize = mSections.size();
    }

    public LetterBar(Context context) {
        super(context);
        init(null, 0);
    }

    public LetterBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public LetterBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_UP:
                onSelect(y, true);
                break;
            default:
                onSelect(y, false);
                break;
        }
        return true;
    }

    public void onText(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mSections.contains(text.toUpperCase())) {
                this.text = text.toUpperCase();
            } else {
                this.text = "#";
            }
        }
        invalidate();
    }

    public void textlist(List<String> mlist) {
        this.mlist = mlist;
        this.invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < SECTION_SIZE; ++i) {
            canvas.drawText(mSections.get(i), mCenter, mPaddingTop + mItemHeight * (i + 1), mTextPaint);
            if (this.mlist != null && mlist.size() != 0) {
                if (this.mlist.contains(mSections.get(i))) {
                    canvas.drawText(mSections.get(i), mCenter, mPaddingTop + mItemHeight * (i + 1), textPaint);
                }
               /* if (this.text.equals(mSections.get(i))){
                    canvas.drawText(mSections.get(i), mCenter, mPaddingTop + mItemHeight * (i + 1), textPaint);
                }else{
                   // canvas.drawText(mSections.get(mSectionSize-1), mCenter, mPaddingTop + mItemHeight * (i + 1), textPaint);
                }*/
            }
        }
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.LetterBar, defStyle, 0);

        mTextColor = a.getColor(
                R.styleable.LetterBar_textColor,
                mTextColor);
        mTextSize = a.getDimension(R.styleable.LetterBar_textSize, mTextSize);

        a.recycle();

        initTextPaint();

        post(new Runnable() {
            @Override
            public void run() {
                mPaddingLeft = getPaddingLeft();
                mPaddingTop = getPaddingTop();
                mPaddingRight = getPaddingRight();
                mPaddingBottom = getPaddingBottom();

                mContentWidth = getWidth() - mPaddingLeft - mPaddingRight;
                mContentHeight = getHeight() - mPaddingTop - mPaddingBottom;
                mCenter = mContentWidth / 2.0f;
                mItemHeight = mContentHeight / mSectionSize;
                invalidate();
            }
        });
    }

    private void initTextPaint() {
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);

        textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(mTextSize);
        textPaint.setColor(getResources().getColor(R.color.sing_in_color));
    }

    public void setOnLetterSelectListener(OnLetterSelectListener onLetterSelectListener) {
        mOnLetterSelectListener = onLetterSelectListener;
    }

    private void onSelect(float y, boolean stop) {
        int index = (int) ((y - mPaddingTop) / mContentHeight * mSectionSize);
        if (index < 0) {
            index = 0;
        } else if (index >= mSectionSize) {
            index = mSectionSize - 1;
        }
        if (mOnLetterSelectListener != null) {
            mOnLetterSelectListener.onLetterSelect(index, mSections.get(index), stop);
        }
    }

    public interface OnLetterSelectListener {
        void onLetterSelect(int position, String letter, boolean confirmed);
    }
}
