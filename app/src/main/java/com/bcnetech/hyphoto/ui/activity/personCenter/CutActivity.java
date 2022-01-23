package com.bcnetech.hyphoto.ui.activity.personCenter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.hyphoto.databinding.ActivityCutBinding;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.ui.view.WrhImageView;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.IOException;

public class CutActivity extends BaseActivity {
    ActivityCutBinding activityCutBinding;
    private boolean havecuted = false;
    private boolean canClick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        String url = "file://" + this.getIntent().getStringExtra("murl");
        final int degree = readPictureDegree(this.getIntent().getStringExtra("murl"));
        if (url != null) {
            ImageUtil.EBizImageLoad(url, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (bitmap != null) {
                        if (degree == 90) {
                            bitmap = rotaingImageView(90, bitmap);
                        }
                        activityCutBinding.progress.setVisibility(View.VISIBLE);
                        activityCutBinding.progress.bringToFront();
                        canClick = false;
                        activityCutBinding.myCutView.setImageBitmap(bitmap, new WrhImageView.WrhLoadingInter() {
                            @Override
                            public void onLoading() {
                                canClick = true;
                                activityCutBinding.progress.setVisibility(View.GONE);
                            }
                        });

                    }

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }

    }

    /**
     * 强制旋转图片
     */

    public Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Log.d("angel:", angle + "");
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 获取图片信息
     *
     * @param path
     * @return
     */
    public int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;

    }

    private void init() {
        activityCutBinding = DataBindingUtil.setContentView(this, R.layout.activity_cut);
        activityCutBinding.cutTitle.setType(TitleView.PIC_PARMS_NEW);
        activityCutBinding.cutTitle.setBackgroundColor(getResources().getColor(R.color.trans_backgroud));
        activityCutBinding.cutTitle.bringToFront();
        activityCutBinding.cutTitle.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        activityCutBinding.cutTitle.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canClick) {
                    Bitmap bitmap = activityCutBinding.myCutView.clipBitmap();
                    SaveCropTask task = new SaveCropTask(bitmap);
                    task.execute();
                }
            }
        });
    }

    private class SaveCropTask extends AsyncTask<Void, Void, String> {
        private Bitmap bitmap;

        SaveCropTask(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        protected void onPreExecute() {
            //dialogHelper.showProgressDialog("处理中");
        }

        @Override
        protected String doInBackground(Void... params) {
            long time = System.currentTimeMillis();
            String path1 = null;
            try {
                path1 = FileUtil.saveBitmap2(bitmap, time - 1 + "");
                path1 = "file://" + path1;

                if (StringUtil.isBlank(path1)) {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return path1;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                Intent intent = new Intent();
                intent.putExtra("mimage", result);
                setResult(RESULT_OK, intent);
                finish();
            }

        }
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

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick() {

    }
}
