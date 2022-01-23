package com.bcnetech.hyphoto.ui.activity.camera;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.hyphoto.databinding.ActivityAipreviewBinding;
import com.bcnetech.hyphoto.ui.view.photoview.OnViewDragListener;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.R;
import com.squareup.picasso.Picasso;

public class AiPreviewActivity extends BaseActivity {
    private ActivityAipreviewBinding activityAipreviewBinding;
    private float startPointY, movePointY;
    private static final int MINMOVEDISTANCE = 35;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        onViewClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void initView() {
        activityAipreviewBinding = DataBindingUtil.setContentView(this, R.layout.activity_aipreview);


    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        String bitmapurl = bundle.getString("url");
        activityAipreviewBinding.photoView.setOnViewDragListener(new OnViewDragListener() {
            @Override
            public void onDrag(float dx, float dy) {
                if (activityAipreviewBinding.photoView.getScale() == 1.0) {
                    if (dy > MINMOVEDISTANCE) {
                        finish();
                    }
                }
            }
        });
        BitmapUtils.BitmapSize bitmapSize = BitmapUtils.getBitmapSize(bitmapurl.substring(7));
        int min = Math.min(bitmapSize.width, bitmapSize.height);
        if (min > BitmapUtils.MAXLENGTH) {
            float width = bitmapSize.width;
            float height = bitmapSize.height;
            if (width > height) {
                width = BitmapUtils.MAXLENGTH;
                height = BitmapUtils.MAXLENGTH * bitmapSize.height / bitmapSize.width;
            } else {
                height = BitmapUtils.MAXLENGTH;
                width = BitmapUtils.MAXLENGTH * bitmapSize.width / bitmapSize.height;
            }
            Picasso.get().load(bitmapurl).resize((int) width, (int) height).into(activityAipreviewBinding.photoView);
        } else {
            Picasso.get().load(bitmapurl).into(activityAipreviewBinding.photoView);
        }
       /* Picasso.get().load(bitmapurl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                int min = Math.min(bitmap.getWidth(),bitmap.getHeight());
                if (min> BitmapUtils.MAXLENGTH){
                    Bitmap scalebitmap = BitmapUtils.rescaleBitmap(bitmap);
                    activityAipreviewBinding.photoView.setImageBitmap(scalebitmap);
                }else{
                    activityAipreviewBinding.photoView.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });*/
    }


    @Override
    protected void onViewClick() {
        activityAipreviewBinding.photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityAipreviewBinding.photoView.getScale() == 1.0) {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
