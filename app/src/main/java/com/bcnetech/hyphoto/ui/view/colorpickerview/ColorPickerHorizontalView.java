
/*
 * Copyright (C) 2017 skydoves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bcnetech.hyphoto.ui.view.colorpickerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bcnetech.hyphoto.R;

public class ColorPickerHorizontalView extends FrameLayout {

    private int selectedColor;
    private Point selectedPoint;

    private ImageView palette;
    private ImageView selector;

    private Drawable paletteDrawable;
    private Drawable selectorDrawable;
    private ImageView imageView;

    protected ColorListener mColorListener;
    private int xmPoint,yPoint;

    public ColorPickerHorizontalView(Context context) {
        super(context);
    }

    public ColorPickerHorizontalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        getAttrs(attrs);
        onCreate();
    }

    public ColorPickerHorizontalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        getAttrs(attrs);
        onCreate();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColorPickerHorizontalView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        getAttrs(attrs);
        onCreate();
    }

    private void init() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                onFirstLayout();
            }
        });
    }

    private void onFirstLayout() {
        selectCenter();
        loadListeners();
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPickerView);
        try {
            if (a.hasValue(R.styleable.ColorPickerView_palette))
                paletteDrawable = a.getDrawable(R.styleable.ColorPickerView_palette);
            if (a.hasValue(R.styleable.ColorPickerView_selector))
                selectorDrawable = a.getDrawable(R.styleable.ColorPickerView_selector);
        } finally {
            a.recycle();
        }
    }

    private void onCreate() {
        setPadding(0, 0, 0, 0);
        palette = new ImageView(getContext());
        if (paletteDrawable != null)
            palette.setImageDrawable(paletteDrawable);

        FrameLayout.LayoutParams wheelParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Dp2Px(getContext(),12));
        wheelParams.gravity = Gravity.CENTER;
        addView(palette, wheelParams);

        imageView=new ImageView(getContext());

        imageView.setImageDrawable(getResources().getDrawable(R.drawable.color_picker_blue_background));
        FrameLayout.LayoutParams  back= new LayoutParams(paletteDrawable.getIntrinsicWidth(), Dp2Px(getContext(),12));
        back.gravity = Gravity.CENTER;
        addView(imageView, back);

        selector = new ImageView(getContext());
        if (selectorDrawable != null) {
            selector.setImageDrawable(selectorDrawable);

            FrameLayout.LayoutParams thumbParams = new LayoutParams(Dp2Px(getContext(),16), Dp2Px(getContext(),20));
            thumbParams.gravity = Gravity.LEFT;
            addView(selector, thumbParams);
        }
    }

    private void loadListeners() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        selector.setPressed(true);
                        return onTouchReceived(event);
                    case MotionEvent.ACTION_MOVE:
                        selector.setPressed(true);
                        return onTouchReceived(event);
                    default:
                        selector.setPressed(false);
                        return false;
                }
            }
        });
    }

    private boolean onTouchReceived(MotionEvent event) {
        Point snapPoint = new Point((int)event.getX(), (int)event.getY());
        selectedColor = getColorFromBitmap(snapPoint.x, yPoint);

        // check validation
        if(getColor() != Color.TRANSPARENT) {
//            Log.d("biz_matting_pop_bg","aaaaa");
            selector.setX(snapPoint.x - (selector.getMeasuredWidth() / 2));
//            selector.setY(snapPoint.y - (selector.getMeasuredHeight() / 2));
            selectedPoint = new Point(snapPoint.x, yPoint);
            fireColorListener(getColor());
            return true;
        }
        else
//            Log.d("bbbb","bbbb");
            return true;
    }

    private int getColorFromBitmap(float x, float y) {
        if (paletteDrawable == null) return 0;

        Matrix invertMatrix = new Matrix();
        palette.getImageMatrix().invert(invertMatrix);

        float[] mappedPoints = new float[]{x, y};
        invertMatrix.mapPoints(mappedPoints);

        if (palette.getDrawable() != null && palette.getDrawable() instanceof BitmapDrawable &&
                mappedPoints[0] > 0 && mappedPoints[1] > 0 &&
                mappedPoints[0] < palette.getDrawable().getIntrinsicWidth() && mappedPoints[1] < palette.getDrawable().getIntrinsicHeight()) {

            invalidate();
            return ((BitmapDrawable) palette.getDrawable()).getBitmap().getPixel((int) mappedPoints[0], (int) mappedPoints[1]);
        }
        return 0;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    private void fireColorListener(int color) {
        if (mColorListener != null) {
            mColorListener.onColorSelected(color);
        }
    }

    public void setColorListener(ColorListener colorListener) {
        mColorListener = colorListener;
    }

    public int getColor() {
        return selectedColor;
    }

    public String getColorHtml(){
        return String.format("%06X", (0xFFFFFF & selectedColor));
    }

    public int[] getColorRGB() {
        int[] rgb = new int[3];
        int color = (int) Long.parseLong(String.format("%06X", (0xFFFFFF & selectedColor)), 16);
        rgb[0] = (color >> 16) & 0xFF; // hex to int : R
        rgb[1] = (color >> 8) & 0xFF; // hex to int : G
        rgb[2] = (color >> 0) & 0xFF; // hex to int : B
        return rgb;
    }

    public Point getSelectedPoint() {
        return selectedPoint;
    }

    public void setSelectorPoint(int x, int y) {
        xmPoint=x;
        yPoint=y;
        selector.setX(x);
        selector.setY(y);
        selectedPoint = new Point(x, y);
        selectedColor = getColorFromBitmap(x, y);
//        fireColorListener(getColor());
    }

    public void setPaletteDrawable(Drawable drawable) {
        removeView(palette);
        palette = new ImageView(getContext());
        paletteDrawable = drawable;
        palette.setImageDrawable(paletteDrawable);
        addView(palette);

        removeView(selector);
        addView(selector);

        selector.setX(getMeasuredWidth()/2 - selector.getWidth()/2);
        selector.setY(getMeasuredHeight()/2- selector.getHeight()/2);
    }

    public void setSelectorDrawable(Drawable drawable) {
        selector.setImageDrawable(drawable);
    }

    public void selectCenter() {
        setSelectorPoint((getMeasuredWidth() - palette.getMeasuredWidth())/2, getMeasuredHeight()/2-selector.getHeight()/3);
    }

    // dp转ps
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
