package com.bcnetech.hyphoto.ui.activity.personCenter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.RetrofitFactory;
import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.bean.response.PresetDownManageData;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.view.help.OnStartDragListener;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.PresetParmsSqlControl;
import com.bcnetech.hyphoto.help.PresetLoadManageItemTouchHelper;
import com.bcnetech.hyphoto.presenter.PresetLoadManagePresenter;
import com.bcnetech.hyphoto.presenter.iview.IPresetLoadManageView;
import com.bcnetech.hyphoto.receiver.PresetReceiver;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.task.MBaseObserver;
import com.bcnetech.hyphoto.ui.activity.BaseMvpActivity;
import com.bcnetech.hyphoto.ui.adapter.PresetDownDiffAdapter;
import com.bcnetech.hyphoto.ui.adapter.SwapRecyclerAdapter;
import com.bcnetech.hyphoto.ui.view.ImageparmsNewView;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.ui.view.swipemenu.SwapRecyclerView;
import com.bcnetech.hyphoto.ui.view.swipemenu.SwipeMenuBuilder;
import com.bcnetech.hyphoto.ui.view.swipemenu.bean.SwipeMenu;
import com.bcnetech.hyphoto.ui.view.swipemenu.bean.SwipeMenuItem;
import com.bcnetech.hyphoto.ui.view.swipemenu.view.SwipeMenuLayout;
import com.bcnetech.hyphoto.ui.view.swipemenu.view.SwipeMenuView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yhf on 2017/3/27.
 */
public class PresetLoadManageActivity extends BaseMvpActivity<IPresetLoadManageView, PresetLoadManagePresenter> implements IPresetLoadManageView, SwipeMenuBuilder {

    private TitleView title_layout;
    private SwapRecyclerView recyclerView;
    private TextView empty;
    private SwapRecyclerAdapter adapter;
    private PresetDownDiffAdapter diffAdapter;
    private List<PresetParm> list;
    private List<PresetParm> canUsePresets;
    private List<PresetParm> notUsePresets;
    private List<PresetParm> presetParms;
    List<PresetDownManageData> shareParamLogVOLists;
    private PresetParmsSqlControl presetParmsSqlControl;
    private ItemTouchHelper itemTouchHelper;
    private boolean isFirst = true;
//    private RecyclerView diffRecyclerView;
    private PresetReceiver receiver;
    private TextView tv_downloadCount;
    private ImageparmsNewView imageparmsNewView;
    private List<PictureProcessingData> histortList;

    @Override
    public PresetLoadManagePresenter initPresenter() {
        return new PresetLoadManagePresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset_load_manage);
        initView();
        initData();
        onViewClick();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=receiver){
            receiver.unregister(this);
        }
    }

    @Override
    protected void initView() {
        recyclerView = (SwapRecyclerView) findViewById(R.id.default_manage_list);
        empty = (TextView) findViewById(R.id.empty);
        title_layout = (TitleView) findViewById(R.id.title_layout);
        imageparmsNewView= (ImageparmsNewView) findViewById(R.id.image_info);
//        diffRecyclerView = (RecyclerView) findViewById(R.id.default_diff_manage_list);
//        scrollView = (ScrollView) findViewById(R.id.all);
    }

    @Override
    protected void initData() {

        receiver=new PresetReceiver() {
            @Override
            public void onGetData(int count) {
                presetParmsSqlControl.startQueryBySystem(presenter.getDeviceName());
            }
        };
        receiver.register(this);

        list = new ArrayList<>();
        canUsePresets = new ArrayList<>();
        notUsePresets = new ArrayList<>();
        shareParamLogVOLists = new ArrayList<>();
        presetParmsSqlControl = new PresetParmsSqlControl(this);
        presetParmsSqlControl.startQueryBySystem(presenter.getDeviceName());

        adapter = new SwapRecyclerAdapter(this, list, new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }

            @Override
            public void onStartSwip(RecyclerView.ViewHolder viewHolder) {

            }

            @Override
            public void onEndDrag(int fromPosition, int toPosition) {
                Collections.swap(canUsePresets, fromPosition, toPosition);
                PresetParm fromPresetParm = canUsePresets.get(toPosition);
                PresetParm toPresetParm = canUsePresets.get(fromPosition);

                int fromPresetParmIndex = fromPresetParm.getPosition();
                int toPresetParmIndex = toPresetParm.getPosition();
                fromPresetParm.setPosition(toPresetParmIndex);
                toPresetParm.setPosition(fromPresetParmIndex);

                presetParmsSqlControl.startUpdate(fromPresetParm, toPresetParm);
            }
        }, presenter.getDeviceName());
//        diffAdapter = new PresetDownDiffAdapter(this, notUsePresets);
        title_layout.setType(TitleView.PRESET_MANAGE);
    }

    @Override
    protected void onViewClick() {

        presetParmsSqlControl.setListener(new BaseSqlControl.SqlControlListener() {
            @Override
            public void queryListener(Object... parms) {
                notUsePresets.clear();
                canUsePresets.clear();
                list.clear();
                presetParms = (List<PresetParm>) parms[0];

                String mtype = presenter.getDeviceName();
                for (int i = 0; i < presetParms.size(); i++) {
                    if (presetParms.get(i).getShowType().equals("0")) {
                        canUsePresets.add(presetParms.get(i));
                    } else {
                        notUsePresets.add(presetParms.get(i));
                    }
                }
//                presenter.sortList(canUsePresets);
                list.addAll(canUsePresets);

                if (null != notUsePresets && notUsePresets.size() > 0) {
//                    presenter.sortList(notUsePresets);
                    PresetParm parm = new PresetParm();
                    parm.setShowType("hint");
                    list.add(parm);
                    list.addAll(notUsePresets);
                }
                adapter.setData(list);
                adapter.notifyDataSetChanged();

                if ((list == null || list.size() == 0)) {
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);
                }
//                if (isFirst) {
//                    presenter.getPresetLoadManageData();
//                }
            }

            @Override
            public void insertListener(Object... parms) {
//                CostomToastUtil.toast(getResources().getString(R.string.save_ok));
//                presetParmsSqlControl.startQuery();
            }

            @Override
            public void deletListener(Object... parms) {
                ToastUtil.toast(getResources().getString(R.string.delete_success));
                if (canUsePresets == null || canUsePresets.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);
                }
                presetParmsSqlControl.startQueryBySystem(presenter.getDeviceName());
                //关闭图片信息页面
                imageparmsNewView.refresh();
            }

            @Override
            public void upDataListener(Object... parms) {

            }
        });


        title_layout.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new SwapRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {


                final PresetParm presetParm=list.get(position);

                imageparmsNewView.setparmsPreset(presetParm,ImageparmsNewView.TYPE_DELETE);
                imageparmsNewView.searchForPrsetId(presetParm.getPresetId(),PresetLoadManageActivity.this);

                if(!StringUtil.isBlank(presetParm.getImageWidth())&&!"null".equals(presetParm.getImageWidth())&&!StringUtil.isBlank(presetParm.getImageHeight())&&!"null".equals(presetParm.getImageHeight())){
                    imageparmsNewView.setSize(Integer.valueOf(presetParm.getImageWidth()),Integer.valueOf(presetParm.getImageHeight()),presetParm.getSize());
                }
                imageparmsNewView.setImageparmsViewListener(new ImageparmsNewView.ImageparmsViewListener() {
                    @Override
                    public void onImageparmsView() {

                    }

                    @Override
                    public void onPresetParms() {

                    }

                    @Override
                    public void onNoChange(boolean nochange) {

                    }

                    @Override
                    public void onbtnclose() {

                    }

                    @Override
                    public void deletePreset() {

                        RetrofitFactory.getInstence().API().shareParamDelete(presetParm.getPresetId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new MBaseObserver<Object>(PresetLoadManageActivity.this,true) {
                                    @Override
                                    protected void onSuccees(BaseResult<Object> t) throws Exception {
                                        presetParmsSqlControl.delData(presetParm.getId());
                                    }

                                    @Override
                                    protected void onCodeError(BaseResult<Object> t) throws Exception {
                                        ToastUtil.toast(t.getMessage());
                                    }

                                    @Override
                                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                                        ToastUtil.toast(e.getMessage());
                                    }
                                });
                    }

                    @Override
                    public void downLoadPreset() {

                    }

                    @Override
                    public void sharePreset() {

                    }
                });

            }
        });

        itemTouchHelper = new ItemTouchHelper(new PresetLoadManageItemTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    /**
     * 获取下载记录
     * @param datas 下载记录
     */
    @Override
    public void getPresetLoadManageSuccess(List<PresetDownManageData> datas) {
    }

    /**
     * 下载预设参数
     * @param presetParm
     */
    @Override
    public void downLoadPresetSuccess(PresetParm presetParm) {
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onBackPressed() {
        if(null!=imageparmsNewView&&imageparmsNewView.getVisibility()==View.VISIBLE){
            imageparmsNewView.refresh();
        }else {
            super.onBackPressed();
        }

    }

    @Override
    public void finishView(int resultCode, Intent intent) {



    }

    private SwipeMenuView.OnMenuItemClickListener mOnSwipeItemClickListener = new SwipeMenuView.OnMenuItemClickListener() {

        @Override
        public void onMenuItemClick(int pos, SwipeMenu menu, int index, SwipeMenuLayout layout) {

            //点击隐藏
            if (index == 0) {
                PresetParm presetParm = list.get(pos);
                //ShowType 1 隐藏
                presetParm.setShowType("1");
//                list.remove(pos);
//                recyclerView.smoothCloseMenu(pos);
//                list.add(presetParm);

                presetParmsSqlControl.startUpdateShowType(presetParm);
//                adapter.notifyDataSetChanged();
            }
            //点击显示
            if (index == 1) {
                PresetParm presetParm = list.get(pos);
                //ShowType 0 显示
                presetParm.setShowType("0");
//                list.remove(pos);
//                list.add(0,presetParm);
//                recyclerView.smoothCloseMenu(pos);
                presetParmsSqlControl.startUpdateShowType(presetParm);
//                adapter.notifyDataSetChanged();
            }

            adapter.notifyItemChanged(pos);
            layout.smoothCloseMenu();
            presetParmsSqlControl.startQueryBySystem(presenter.getDeviceName());
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (100 == resultCode) {
            isFirst = false;

        }

    }

    @Override
    public SwipeMenuView create() {
        SwipeMenu menu = new SwipeMenu(this);

        SwipeMenuItem item = new SwipeMenuItem(PresetLoadManageActivity.this);


        item.setTitle("")
                .setTitleColor(Color.WHITE)
                .setBackground(new ColorDrawable(getResources().getColor(R.color.grey_item)))
                .setWidth(dp2px(65))
                .setTitleSize(12)
                .setIconWidth(dp2px(22))
                .setIconHeight(dp2px(18))
                .setMargin_right(dp2px(6))
                .setMargin_bottom(dp2px(7))
                .setText_topMargin(dp2px(8))
                .setIcon(R.drawable.hide);

        menu.addMenuItem(item);

        item = new SwipeMenuItem(PresetLoadManageActivity.this);
        item.setTitle("")
                .setTitleColor(Color.WHITE)
                .setBackground(new ColorDrawable(getResources().getColor(R.color.grey_item)))
                .setWidth(dp2px(65))
                .setTitleSize(12)
                .setIconWidth(dp2px(22))
                .setIconHeight(dp2px(18))
                .setMargin_right(dp2px(6))
                .setMargin_bottom(dp2px(7))
                .setText_topMargin(dp2px(8))
                .setIcon(R.drawable.display);
        menu.addMenuItem(item);
//
        SwipeMenuView menuView = new SwipeMenuView(menu);

        menuView.setOnMenuItemClickListener(mOnSwipeItemClickListener);

        return menuView;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
