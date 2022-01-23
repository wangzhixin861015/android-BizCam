package com.bcnetech.hyphoto.ui.activity.cloud;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.view.IndexableRecycler.IndexableRecyclerView2;
import com.bcnetech.hyphoto.data.CloudPicture;
import com.bcnetech.hyphoto.ui.adapter.CloudAdapter;
import com.bcnetech.hyphoto.ui.view.ChoiceView;
import com.bcnetech.hyphoto.ui.view.ChoiceView2;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 云相册的文件夹展示
 * Created by yhf on 17/10/17.
 */
public class CloundNewActivity extends BaseActivity {
    public final static int NULL = 0;
    public final static int CLOUD_CHISE = 1;
    public final static int CLOUD = 2;
    public final static int CLOUDWATERMARK = 3;

    private final static String LISTDATA = "LIST_DATA";
    private final static String TYPE_CODE = "RTPE_CODE";

    private List<CloudPicture> listData;
    private IndexableRecyclerView2 indexable_recyclerview;
    private ChoiceView2 cloud_choice;
    private CloudPicture selectcp;
    private TitleView titleView;
    private CloudAdapter indexableRecyclerViewAdapter;
    //    private RelativeLayout empty_null;
    private int typeCode;


    public static void startAction(Activity activity, int typeCode, int resultCode) {
        Intent intent = new Intent(activity, CloundNewActivity.class);
        intent.putExtra(TYPE_CODE, typeCode);
        if (resultCode != Flag.NULLCODE) {
            activity.startActivityForResult(intent, resultCode);
        } else {
            activity.startActivity(intent);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(LISTDATA, (Serializable) listData);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clound_layout_new);
        if (savedInstanceState != null) {
            listData = (List<CloudPicture>) savedInstanceState.getSerializable(LISTDATA);
        }

        typeCode = getIntent().getIntExtra(TYPE_CODE, NULL);
        if (typeCode == NULL) {
            return;
        }
        initView();
        initData();
        onViewClick();
    }

    protected void initView() {
        indexable_recyclerview = (IndexableRecyclerView2) findViewById(R.id.indexable_recyclerview);
        cloud_choice = (ChoiceView2) findViewById(R.id.cloud_choice);
        titleView = (TitleView) findViewById(R.id.title_layout);
//        empty_null = (RelativeLayout) findViewById(R.id.empty_null);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    protected void initData() {
        if (typeCode == CLOUD) {
            cloud_choice.setVisibility(View.GONE);
            titleView.setVisibility(View.VISIBLE);
            titleView.bringToFront();
        } else if (typeCode == CLOUDWATERMARK) {
            cloud_choice.setVisibility(View.GONE);
            titleView.setVisibility(View.VISIBLE);
            titleView.bringToFront();
        } else {
            titleView.setVisibility(View.GONE);
            cloud_choice.setVisibility(View.VISIBLE);
            cloud_choice.setType(ChoiceView.TYPE2);
            cloud_choice.bringToFront();
            cloud_choice.setText(getResources().getString(R.string.select_files));
            cloud_choice.setOkListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectcp == null) {
                        ToastUtil.toast(getResources().getString(R.string.select_files_to_upload));
                    } else {
                        Intent intent;
                        intent = new Intent();
                        intent.putExtra("CloudPicture", selectcp);
                        CloundNewActivity.this.setResult(Flag.CLOUDFILE, intent);
                        CloundNewActivity.this.finish();
                    }

                }
            });
            cloud_choice.setCencelListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        titleView.setType(TitleView.CLOUD_ABLUM_NEW);


        indexableRecyclerViewAdapter = new CloudAdapter(this, listData, new CloudAdapter.ClickInterFace() {
            @Override
            public void refresh() {
                final ArrayList<String> newlist = new ArrayList<>();
                if (listData != null && listData.size() != 0) {
                    for (int i = 0; i < listData.size(); i++) {
                        newlist.add(listData.get(i).getName());
                    }
                }
                indexable_recyclerview.setAdapter(indexableRecyclerViewAdapter, newlist);
            }

            @Override
            public void onClick(CloudPicture cloudPicture) {
                if (typeCode == CLOUD_CHISE) {
                    indexableRecyclerViewAdapter.notifyDataSetChanged();
                    CloundNewActivity.this.selectcp = cloudPicture;
                    cloud_choice.setText(cloudPicture.getName());
                } else if (typeCode == CLOUDWATERMARK) {
                    CloudInfoActivity.actionStart(CloundNewActivity.this, typeCode);
                } else {
                    CloudInfoActivity.actionStart(CloundNewActivity.this, typeCode);
                }
            }
        }, typeCode);
        //   indexable_recyclerview.setAdapter(indexableRecyclerViewAdapter);
        if (listData == null || listData.size() == 0) {
           // getCloundEduAblum();

        }

    }

    protected void onViewClick() {
        titleView.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImageUtil.NONE)
            return;
        if (data == null)
            return;

        if (requestCode == resultCode && resultCode == Flag.CLOUDWATERMARK) {
            setResult(Flag.CLOUDWATERMARK,data);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

