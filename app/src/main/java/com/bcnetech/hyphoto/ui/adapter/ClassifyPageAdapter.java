package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by a1234 on 2017/7/19.
 */

public class ClassifyPageAdapter extends PagerAdapter {
    // 界面列表
    private List<View> views;
    private Activity activity;

    public ClassifyPageAdapter(Activity activity ,List views){
        this.activity=activity;
        this.views=views;
    }

    @Override
    public int getCount() {
        return views==null?0:views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position),0);
        return views.get(position);
    }

}
