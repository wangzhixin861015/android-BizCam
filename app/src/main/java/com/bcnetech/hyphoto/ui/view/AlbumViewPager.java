package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;

public class AlbumViewPager extends NoScrollViewPager implements MatrixImageView.OnMovingListener {

    /**  当前子控件是否处理拖动状态  */
    private boolean mChildIsBeingDragged=false;

    public AlbumViewPager(Context context) {
        super(context);
    }

    public AlbumViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public PagerAdapter getAdapter() {
        return super.getAdapter();
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public int getCurrentItem() {
        return super.getCurrentItem();
    }

    @Override
    public void startDrag() {
        mChildIsBeingDragged=true;
    }

    @Override
    public void stopDrag() {
        mChildIsBeingDragged=false;
    }




}