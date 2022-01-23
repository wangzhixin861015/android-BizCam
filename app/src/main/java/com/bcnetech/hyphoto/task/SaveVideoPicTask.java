package com.bcnetech.hyphoto.task;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;

import java.util.HashMap;

public class SaveVideoPicTask extends AsyncTask<Void, Void, Bitmap> {
    private String url;
    private int width;
    private int height;
    private Bitmap bitmap;
    private SaveInterface saveInterface;

    public SaveVideoPicTask(String url, int width, int height,SaveInterface saveInterface) {
        this.url = url;
        this.width = width;
        this.height = height;
        this.saveInterface = saveInterface;
    }

    protected void onPreExecute() {
        //dialogHelper.showProgressDialog("处理中");
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        retriever.release();
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if(saveInterface!=null){
            saveInterface.saveEnd(result);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface SaveInterface {
        void saveEnd(Bitmap bitmap);
    }

}