package com.bcnetech.hyphoto.ui.activity.personCenter;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.BizCamHelpData;
import com.bcnetech.hyphoto.presenter.BizCamHelpDetailPresenter;
import com.bcnetech.hyphoto.presenter.iview.IBizCamHelpDetailView;
import com.bcnetech.hyphoto.ui.activity.BaseMvpActivity;
import com.bcnetech.hyphoto.ui.adapter.BizCamHelpDetailAdapter;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.ui.view.videoplayer.view.MVideoPlayer;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yhf on 2018/12/11.
 */

public class BizCamHelpDetailActivity extends BaseMvpActivity<IBizCamHelpDetailView, BizCamHelpDetailPresenter> {

    private TitleView titleView;
    private List<BizCamHelpData> bizCamHelpDatas;
    private ViewPager viewPager;
    private BizCamHelpDetailAdapter bizCamHelpDetailAdapter;
    private TextView tv_page;
    private ImageView iv_left;
    private ImageView iv_right;
    private int mPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bizcam_help_detail);
        initView();
        initData();
        onViewClick();
    }

    @Override
    protected void initView() {
        titleView = findViewById(R.id.titleView);
        viewPager = findViewById(R.id.viewpager_video);
        tv_page = findViewById(R.id.tv_page);
        iv_left = findViewById(R.id.iv_left);
        iv_right = findViewById(R.id.iv_right);
    }

    @Override
    protected void initData() {
        bizCamHelpDatas = (ArrayList<BizCamHelpData>) getIntent().getSerializableExtra("bizCamHelpDatas");
        mPosition = getIntent().getIntExtra("position", -1);

        titleView.setType(TitleView.BIZCAM_HELP_DETAIL);

        show(mPosition);

        bizCamHelpDetailAdapter = new BizCamHelpDetailAdapter(this, bizCamHelpDatas);
        bizCamHelpDetailAdapter.setFirst(true);
        viewPager.setAdapter(bizCamHelpDetailAdapter);
        viewPager.setCurrentItem(mPosition);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                show(position);
                int childCount = viewPager.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = viewPager.getChildAt(i);
                    try {
                        if (childAt != null && childAt instanceof RelativeLayout) {
                            int c = ((RelativeLayout) childAt).getChildCount();
                            for (int j = 0; j < c; j++) {
                                View childAt1 = ((RelativeLayout) childAt).getChildAt(j);
                                if (childAt1 != null && childAt1 instanceof RelativeLayout) {
                                    int d = ((RelativeLayout) childAt1).getChildCount();
                                    for (int k = 0; k < d; k++) {
                                        View videoView = ((RelativeLayout) childAt1).getChildAt(k);
                                        if (videoView != null && videoView instanceof MVideoPlayer) {
                                            if ((int)videoView.getTag()==position){
                                                LogUtil.d("a");
                                                ((MVideoPlayer) videoView).playVideo();
                                            }else {

                                                ((MVideoPlayer) videoView).stopVideo();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            //此方法是在状态改变的时候调用，其中arg0这个参数有三种状态（0，1，2）。arg0 ==1的时辰默示正在滑动，arg0==2的时辰默示滑动完毕了，arg0==0的时辰默示什么都没做。
            @Override
            public void onPageScrollStateChanged(int state) {
                LogUtil.d("state" + state);
            }
        });
    }

    @Override
    protected void onViewClick() {
        titleView.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition--;
                if(mPosition<=0){
                    mPosition=0;
                }
                viewPager.setCurrentItem(mPosition);
            }
        });

        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition++;
                if(mPosition>=bizCamHelpDatas.size()-1){
                    mPosition=bizCamHelpDatas.size()-1;
                }
                viewPager.setCurrentItem(mPosition);
            }
        });
    }

    @Override
    public BizCamHelpDetailPresenter initPresenter() {
        return new BizCamHelpDetailPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void show(int position) {
        if (position == 0) {
            iv_left.setImageResource(R.drawable.bizcam_help_unleft);
            iv_right.setImageResource(R.drawable.bizcam_help_right);
        } else if (position == bizCamHelpDatas.size() - 1) {
            iv_left.setImageResource(R.drawable.bizcam_help_left);
            iv_right.setImageResource(R.drawable.bizcam_help_unright);
        } else {
            iv_left.setImageResource(R.drawable.bizcam_help_left);
            iv_right.setImageResource(R.drawable.bizcam_help_right);
        }

        if (bizCamHelpDatas.get(position).getType() == Flag.BIZCAM_HELP_AI_COBOX) {
            titleView.setTitleText("智拍");
        } else if (bizCamHelpDatas.get(position).getType() == Flag.BIZCAM_HELP_AI_COLINK) {
            titleView.setTitleText("智拍");
        } else if (bizCamHelpDatas.get(position).getType() == Flag.BIZCAM_HELP_PAINT) {
            titleView.setTitleText("画笔");
        } else if (bizCamHelpDatas.get(position).getType() == Flag.BIZCAM_HELP_MATTING) {
            titleView.setTitleText("抠图");
        } else if (bizCamHelpDatas.get(position).getType() == Flag.BIZCAM_HELP_REPAIR) {
            titleView.setTitleText("修复");
        }
        tv_page.setText((position + 1) + "");
    }
}
