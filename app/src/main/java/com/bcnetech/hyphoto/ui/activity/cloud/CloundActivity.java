package com.bcnetech.hyphoto.ui.activity.cloud;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.bcnetechlibrary.view.IndexableRecycler.IndexableRecyclerView;
import com.bcnetech.hyphoto.data.CloudPicture;
import com.bcnetech.hyphoto.receiver.DeleteCloudReceiver;
import com.bcnetech.hyphoto.ui.adapter.CloudAdapter;
import com.bcnetech.hyphoto.ui.view.ChoiceView;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 云相册的文件夹展示
 * Created by wenbin on 16/5/31.
 */
public class CloundActivity extends BaseActivity {
    public final static int NULL = 0;
    public final static int CLOUD_CHISE = 1;
    public final static int CLOUD = 2;

    private final static String LISTDATA = "LIST_DATA";
    private final static String TYPE_CODE = "RTPE_CODE";

    private List<CloudPicture> listData;
    private IndexableRecyclerView indexable_recyclerview;
    private ChoiceView cloud_choice;
    private CloudPicture selectcp;
    private TitleView titleView;
    private CloudAdapter indexableRecyclerViewAdapter;
    private RelativeLayout empty_null;
    private int typeCode;
    private DeleteCloudReceiver deleteCloudReceiver;


    public static void startAction(Activity activity, int typeCode, int resultCode) {
        Intent intent = new Intent(activity, CloundActivity.class);
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
        setContentView(R.layout.clound_layout);
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
        indexable_recyclerview = (IndexableRecyclerView) findViewById(R.id.indexable_recyclerview);
        cloud_choice = (ChoiceView) findViewById(R.id.cloud_choice);
        titleView = (TitleView) findViewById(R.id.title_layout);
        empty_null = (RelativeLayout) findViewById(R.id.empty_null);
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
        titleView.setType(TitleView.CLOUD_ABLUM);

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
                    CloundActivity.this.selectcp = cloudPicture;
                    cloud_choice.setText(cloudPicture.getName());
                } else {
                    CloudInfoActivity.actionStart(CloundActivity.this,CloudInfoActivity.TYPE_CLOUD);
                }
            }
        }, typeCode);
        //   indexable_recyclerview.setAdapter(indexableRecyclerViewAdapter);
        if (listData == null || listData.size() == 0) {
            getCloundAblum();

        }

        deleteCloudReceiver=new DeleteCloudReceiver() {
            @Override
            public void onGetData() {
                getCloundAblum();
            }
        };

        deleteCloudReceiver.register(this);

    }

    protected void onViewClick() {

        titleView.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }).setRightTextListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    private void getCloundAblum() {
//            GetCloundAblumTask task = new GetCloundAblumTask(this, true);
//            task.setShowProgressDialog(true);
//            task.setAsyncTaskSuccessCallback(new AsyncTaskSuccessCallback<CloudAblum>() {
//                @Override
//                public void successCallback(Result<CloudAblum> result) {
//
////                    if(listData.size()>0){
////                        List<CloudPicture> value=result.getValue();
////                        listData.addAll(value);
////                    }else{
//                        listData = result.getValue().getBimageCatalogList();
////                    }
//                    dissmissDialog();
//                    indexableRecyclerViewAdapter.setListData(listData);
//                    indexableRecyclerViewAdapter.notifyDataSetChanged();
//                    if (listData != null && listData.size() != 0) {
//                        empty_null.setVisibility(View.GONE);
//                    }
//                }
//            });
//            task.setAsyncTaskFailCallback(new AsyncTaskFailCallback<CloudAblum>() {
//                @Override
//                public void failCallback(Result<CloudAblum> result) {
//                    dissmissDialog();
//                    dissmissChoiceDialog();
//                    ToastUtil.toast(result.getMessage());
//                    if (listData == null || listData.size() == 0) {
//                        empty_null.setVisibility(View.VISIBLE);
//                    }
//                }
//            });
//            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=deleteCloudReceiver){
            deleteCloudReceiver.unregister(this);
        }
    }
}

