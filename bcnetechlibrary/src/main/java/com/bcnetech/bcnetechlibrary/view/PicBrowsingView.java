package com.bcnetech.bcnetechlibrary.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetechlibrary.R;

/**
 * Created by wenbin on 16/8/17.
 */

public class PicBrowsingView extends RelativeLayout{
    private ViewPager viewPager;
    public PicBrowsingView(Context context) {
        super(context);
        initView();
        initData();
        OnClickView();
    }

    public PicBrowsingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        OnClickView();
    }

    public PicBrowsingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initData();
        OnClickView();
    }

    private void initView(){
        inflate(getContext(), R.layout.pic_browsing_view,this);
        viewPager=(ViewPager) findViewById(R.id.viewpager);
    }


    private void initData(){


    }


    private void OnClickView(){

    }

    public void setViewPagerAdapter(PagerAdapter pagerAdapter){
        viewPager.setAdapter(pagerAdapter);
    }


}
