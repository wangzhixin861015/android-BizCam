package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;

/**
 * Created by yhf on 2017/10/16.
 */

public class AlbumNewTopSelectView extends BaseRelativeLayout {
    private ImageView iv_close;
    private TextView tv_share;
    private TextView iv_upload;
    private ImageView iv_delete;
    private ImageView iv_share;
    private ImageView iv_other;

    public AlbumNewTopSelectView(Context context) {
        super(context);
    }

    public AlbumNewTopSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlbumNewTopSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initData() {
        super.initData();
//        tv_share.setEnabled(true);
//        iv_upload.setEnabled(false);
//        iv_delete.setEnabled(false);
        iv_share.setVisibility(VISIBLE);
        iv_other.setVisibility(VISIBLE);
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.album_new_top_select_view,this);
        iv_close= (ImageView) findViewById(R.id.iv_close);
        tv_share= (TextView) findViewById(R.id.tv_share);
        iv_upload= (TextView) findViewById(R.id.iv_upload);
        iv_delete= (ImageView) findViewById(R.id.iv_delete);
        iv_share= (ImageView) findViewById(R.id.iv_share);
        iv_other= (ImageView) findViewById(R.id.iv_other);
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();
    }

    /**
     * 关闭
     * @param listener
     */
    public void close(View.OnClickListener listener){
        iv_close.setOnClickListener(listener);
    }
//
//    /**
//     * 删除
//     * @param listener
//     */
//    public void delete(View.OnClickListener listener){
//        iv_delete.setOnClickListener(listener);
//    }
//
//    /**
//     * 上传
//     * @param listener
//     */
//    public void upload(View.OnClickListener listener){
//        iv_upload.setOnClickListener(listener);
//    }
//
//    /**
//     * 分享
//     * @param listener
//     */
//    public void share(View.OnClickListener listener){
//        tv_share.setOnClickListener(listener);
//    }

    /**
     * 分享
     * @param listener
     */
    public void shareMore(View.OnClickListener listener){
        iv_share.setOnClickListener(listener);
    }

    public void otherClick(View.OnClickListener listener){
        iv_other.setOnClickListener(listener);
    }

    /**
     * 其他
     * @param listener
     */
    public void oter(View.OnClickListener listener){
        iv_other.setOnClickListener(listener);
    }

    /**
     * 能否点击
     * @param canClick
     */
    public void setTopSelectCanClick(boolean canClick){
//        if(canClick){
////            tv_share.setTextColor(getResources().getColor(R.color.text_item_child));
//            iv_upload.setImageResource(R.drawable.upload);
//            iv_delete.setImageResource(R.drawable.new_delete);
//        }else {
//            tv_share.setTextColor(getResources().getColor(R.color.text_item_child26));
//            iv_upload.setImageResource(R.drawable.unupload);
//            iv_delete.setImageResource(R.drawable.new_undelete);
//        }
////        tv_share.setEnabled(canClick);
//        iv_upload.setEnabled(canClick);
//        iv_delete.setEnabled(canClick);
    }

    /**
     * 超过九张图片能否分享
     * @param canClick
     */
    public void setShareCanClick(boolean canClick){
//        if(canClick){
//            tv_share.setTextColor(getResources().getColor(R.color.text_item_child));
//
//        }else {
//            tv_share.setTextColor(getResources().getColor(R.color.text_item_child26));
//        }
//        tv_share.setEnabled(canClick);
    }
}
