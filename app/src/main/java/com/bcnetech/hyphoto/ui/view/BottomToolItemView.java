package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;


/**
 * Created by wenbin on 16/5/5.
 */
public class BottomToolItemView extends BaseRelativeLayout {
    public final static int CLICK=1;//按钮
    public final static int CLICK_BTN=2;//选择


    public final static String IS_CLICK="1";//
    public final static String UN_CLICK="2";//
    private ImageView bottom_tool_item_img;
    private TextView bottom_tool_item_text;
    private RelativeLayout bottom_tool_rl;
    public BottomToolItemView(Context context) {
        super(context);
    }

    public BottomToolItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomToolItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.bottom_tool_item,this);
        bottom_tool_item_img=(ImageView) findViewById(R.id.bottom_tool_item_img);
        bottom_tool_item_text=(TextView) findViewById(R.id.bottom_tool_item_text);
        bottom_tool_rl=(RelativeLayout)findViewById(R.id.bottom_tool_rl);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();
    }

    public void setTouchType(int type){
        switch (type){
            case CLICK:
                setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                bottom_tool_item_text.setTextColor(getContext().getResources().getColor(R.color.color_blue_bottom));
                                break;
                            case MotionEvent.ACTION_UP:
                                bottom_tool_item_text.setTextColor(getContext().getResources().getColor(R.color.color_white));
                                break;
                            case MotionEvent.ACTION_CANCEL:
                                bottom_tool_item_text.setTextColor(getContext().getResources().getColor(R.color.color_white));
                                break;
                        }
                        return false;
                    }
                });
                break;
            case CLICK_BTN:
                setTag(UN_CLICK);
                setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                bottom_tool_item_text.setTextColor(getContext().getResources().getColor(R.color.color_blue_bottom));
                                break;
                            case MotionEvent.ACTION_UP:
                                if(((String)getTag()).equals(IS_CLICK)){
                                    bottom_tool_item_text.setTextColor(getContext().getResources().getColor(R.color.color_white));
                                    setTag(UN_CLICK);
                                }
                                else{
                                    bottom_tool_item_text.setTextColor(getContext().getResources().getColor(R.color.color_blue_bottom));
                                    setTag(IS_CLICK);
                                }

                                break;
                        }
                        return false;
                    }
                });
                break;
        }
    }


    public BottomToolItemView setImgText(int img, String text){
        bottom_tool_item_img.setImageDrawable(getResources().getDrawable(img));
        bottom_tool_item_text.setText(text);
        return  this;
    }
    public BottomToolItemView setImgWH(int x){
        int w= ContentUtil.dip2px(getContext(),x);
        bottom_tool_item_img.getLayoutParams().width= w;
        bottom_tool_item_img.getLayoutParams().height= w;
        return  this;

    }
    public BottomToolItemView setTextSize(int style){
        bottom_tool_item_text.setTextAppearance(getContext(),style);
        return  this;
    }
    public BottomToolItemView setTextColor(int color){
        bottom_tool_item_text.setTextColor(color);
        return  this;
    }
    public void setWH(int w,int h){
        bottom_tool_rl.setLayoutParams(new LayoutParams(w,h));
    }
}
