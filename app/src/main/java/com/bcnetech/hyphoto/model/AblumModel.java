package com.bcnetech.hyphoto.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.util.sharepreference.SharePreferences;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.GridItem;
import com.bcnetech.hyphoto.model.imodel.IAblumModel;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.utils.FileUtil;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by wenbin on 2017/4/14.
 */

public class AblumModel extends BaseModel<IAblumModel> {


    public List<GridItem> getGridData(List<GridItem> mGirdList, Object... parms) {
        List<ImageData> list = (List<ImageData>) parms[0];
        if (mGirdList != null) {
            mGirdList.clear();
        }
        for (int i = 0; i < list.size(); i++) {
            try {
                GridItem gridItem = new GridItem(list.get(i).getSmallLocalUrl(), StringUtil.paserTimeToYM(list.get(i).getTimeStamp()));
                gridItem.setImageData(list.get(i));
                gridItem.setType("0");
                mGirdList.add(gridItem);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        mGirdList = sortTime(mGirdList);
        /**
         * 加空行
         */
        if (mGirdList != null && mGirdList.size() != 0) {
            GridItem mGridItem = new GridItem("", "");
            mGridItem.setType("0");
            mGirdList.add(mGridItem);
        }
        return mGirdList;
    }

    public boolean isFirstIn() {
        SharePreferences preferences = SharePreferences.instance();
        boolean isFirstIn = preferences.getBoolean("isFirstIn", true);
        return isFirstIn;

    }

    private List<GridItem> sortTime(List<GridItem> mGirdList) {
        Map<String, Integer> sectionMap = new HashMap<String, Integer>();
        int section = 1;

        Collections.sort(mGirdList, new Comparator<GridItem>() {
            @Override
            public int compare(GridItem o1, GridItem o2) {
                return -(o1.getTime().compareTo(o2.getTime()));
//                return -(o1.getPath().substring(o1.getPath().lastIndexOf("/") + 1, o1.getPath().lastIndexOf("."))).compareTo(o2.getPath().substring(o2.getPath().lastIndexOf("/") + 1, o2.getPath().lastIndexOf(".")));
            }
        });

        for (ListIterator<GridItem> it = mGirdList.listIterator(); it.hasNext(); ) {
            GridItem mGridItem = it.next();
            String ym = mGridItem.getTime();
            if (!sectionMap.containsKey(ym)) {
                mGridItem.setSection(section);
                sectionMap.put(ym, section);
                section++;
            } else {
                mGridItem.setSection(sectionMap.get(ym));
            }
        }
        return mGirdList;
    }


    /**
     * 删除图片
     *
     * @param gridItem
     */
    public String[] startDel(GridItem gridItem) {
        String[] strs = new String[1];
        strs[0] = gridItem.getImageData().getLocalUrl();

        if (gridItem.getImageData().getImageParts() != null) {
            for (int i = 0; i < gridItem.getImageData().getImageParts().size(); i++) {
                DeleteImage(gridItem.getImageData().getImageParts().get(i).getImageUrl());
                DeleteImage(gridItem.getImageData().getImageParts().get(i).getSmallUrl());
            }
        }
        if (!StringUtil.isBlank(gridItem.getImageData().getValue2()) && !"null".equals(DeleteImage(gridItem.getImageData().getValue2()))) {
            DeleteImage(gridItem.getImageData().getValue2());
        }

        DeleteImage(gridItem.getImageData().getLocalUrl());
        DeleteImage(gridItem.getImageData().getSmallLocalUrl());

        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(gridItem.getImageData().getSmallLocalUrl())));

        return strs;

    }


    //删除数据库以及文件中的图片
    public boolean DeleteImage(String imgPath) {
        ContentResolver resolver = activity.getContentResolver();
        Cursor cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=?",
                new String[]{imgPath}, null);
        boolean result = false;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri = ContentUris.withAppendedId(contentUri, id);
            int count = activity.getContentResolver().delete(uri, null, null);
            result = count == 1;
        } else {
            if (imgPath.startsWith("file://")) {
                imgPath = imgPath.substring(7);
            }
            File file = new File(imgPath);
            if(file.isDirectory()){
                FileUtil.deleteDir(imgPath);
            }else {
                if (file.exists()) {//显示文件不存在,该文件夹内存在两张一样的图片
                    result = file.delete();
                }
            }

        }
        return result;
    }

    /**
     * 判断是否可以分享参数
     * 预设参数为空
     * 存在
     */
    public boolean isCanShare(ImageData imageparms) {
        boolean havelightretion = false;
        PresetParm presetParm = imageparms.getPresetParms();//预设参数
        List<Integer> list = getLightRation(imageparms);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != -1) {
                havelightretion = true;
            }
        }

        //没有预设参数:有光比
        if (presetParm == null || TextUtils.isEmpty(presetParm.getTextSrc())) {//判断条件:预设参数为空或预设参数中名称为空
            if (havelightretion) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * @param imageData
     * @return
     */
    private List<Integer> getLightRation(ImageData imageData) {
        LightRatioData lightRatioData = imageData.getLightRatioData();
        if (lightRatioData != null) {
            int leftlight = lightRatioData.getLeftLight();
            int rightlight = lightRatioData.getRightLight();
            int backlight = lightRatioData.getBackgroudLight();
            int bottomlight = lightRatioData.getBottomLight();
            int movelight = lightRatioData.getMoveLight();
            int movelight2 = lightRatioData.getTopLight();
            int rpm = lightRatioData.getLight1();
            List<Integer> lightrations = new ArrayList<Integer>();
            lightrations.add(leftlight);
            lightrations.add(rightlight);
            lightrations.add(backlight);
            lightrations.add(bottomlight);
            lightrations.add(movelight);
            lightrations.add(movelight2);
            lightrations.add(rpm);
            return lightrations;
        }
        return null;
    }

    /**
     * 将图片导出到系统相册
     */
    public void saveImageToGallery(Context context, String path) {

      /*  try {
            String finalurl = FileUtil.copyFile(path);
            if (finalurl.equals("exist")) {
                CostomToastUtil.toast("文件已存在");
                return;
            }
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + finalurl.substring(7))));
            CostomToastUtil.toast(activity.getResources().getString(R.string.output_success));
        } catch (Exception e) {
        }*/
        IntoNativeTask intoNativeTask = new IntoNativeTask(context, path);
        intoNativeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class IntoNativeTask extends AsyncTask<Void, Void, String> {
        private String oldpath;
        private Context context;

        IntoNativeTask(Context context, String oldpath) {
            this.oldpath = oldpath;
            this.context = context;
        }

        protected void onPreExecute() {
            //dialogHelper.showProgressDialog("处理中");
        }

        @Override
        protected String doInBackground(Void... params) {
            String path = null;
            try {
                path = FileUtil.copyFile(oldpath, null, null);
                if (StringUtil.isBlank(path)) {
                    return null;
                }
                if (path.equals("exist")) {
                    return "exist";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return path;
        }

        @Override
        protected void onPostExecute(String finalurl) {
            super.onPostExecute(finalurl);

            if (finalurl != null && !TextUtils.isEmpty(finalurl)) {
                if (finalurl.equals("exist")) {
                    ToastUtil.toast(context.getString(R.string.file_exist));
                    return;
                }
                if (finalurl.equals("error")) {
                    ToastUtil.toast(context.getString(R.string.file_error));
                    return;
                }
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + finalurl)));
                ToastUtil.toast(activity.getResources().getString(R.string.output_success));
            }
        }


    }
}