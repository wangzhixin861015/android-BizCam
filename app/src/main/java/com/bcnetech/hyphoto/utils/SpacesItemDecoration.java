package com.bcnetech.hyphoto.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    public static final int TYPE_CLOUD = 1;
    public static final int TYPE_MARKET = 2;
    public static final int TYPE_INTO = 3;
    private int type;
    private int space;

    public SpacesItemDecoration(int space,int type) {
        this.space = space;
        this.type = type;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        //if (parent.getChildPosition(view) == 0)
        switch (type){
            case TYPE_MARKET:
                if (parent.getChildPosition(view)==0||parent.getChildPosition(view)==1||parent.getChildPosition(view)==2){
                    outRect.top = 0;
                }else{
                    outRect.top = space;
                }
                break;
            case TYPE_CLOUD:
                if (parent.getChildPosition(view)==0||parent.getChildPosition(view)==1||parent.getChildPosition(view)==2){
                    outRect.top = 0;

                }else{
                    outRect.top = space;
                }
                break;
            case TYPE_INTO:
                if (parent.getChildPosition(view)==0||parent.getChildPosition(view)==1||parent.getChildPosition(view)==2){
                    outRect.top = ContentUtil.dip2px(view.getContext(),2)+space;

                }else{
                    outRect.top = space;
                }
                break;
        }
    }
}