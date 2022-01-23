package com.bcnetech.hyphoto.task;

/**
 * Created by wenbin on 2016/12/12.
 */

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.FileUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 存储图片到app 相册
 */
public class SavePicFinalTask extends AsyncTask<Void, Void, ArrayList> {
    private Bitmap bitmap;
    private SaveInterface saveInterface;
    private Bitmap srcBitmap;
    private boolean isPng;
    private boolean isMatting;

    public SavePicFinalTask(Bitmap bitmap, Bitmap srcBitmap, SaveInterface saveInterface, boolean isPng,boolean isMatting) {
        this.bitmap = bitmap;
        this.srcBitmap = srcBitmap;
        this.saveInterface = saveInterface;
        this.isPng = isPng;
        this.isMatting=isMatting;
    }

    protected void onPreExecute() {
        //dialogHelper.showProgressDialog("处理中");
    }

    @Override
    protected ArrayList doInBackground(Void... params) {
        long time = System.currentTimeMillis();
        ArrayList<String> urllist = new ArrayList<>();
        String path1 = null;
        String path2 = null;
      /*  try {
            path1 = FileUtil.saveBitmap(bitmap,time-1+"");
            path1="file://" + path1;

            if(StringUtil.isBlank(path1)){
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try {
            path1 = FileUtil.saveBitmaptoNative(bitmap, time - 1 + "", isPng);

            path2 = FileUtil.saveBitmaptoShareNative(isPng? BitmapUtils.compressPNG(srcBitmap):BitmapUtils.compress(srcBitmap), time + 2 + "", isPng);

            if (StringUtil.isBlank(path1)) {
                return null;
            }
            path1 = "file://" + path1;
            path2 = "file://" + path2;
            urllist.add(path1);
            urllist.add(path2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return urllist;
    }

    @Override
    protected void onPostExecute(ArrayList result) {
        super.onPostExecute(result);

        if (saveInterface != null) {
            saveInterface.saveEnd((String) result.get(0),(String) result.get(1));
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        LogUtil.d("onCancelled");
        if (bitmap != null) {
            bitmap.recycle();
        }
    }


    public interface SaveInterface {
        void saveEnd(String url,String srcUrl);
    }


}