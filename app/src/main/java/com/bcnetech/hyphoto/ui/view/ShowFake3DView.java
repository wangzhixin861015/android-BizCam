package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.hyphoto.R;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * author: wsBai
 * date: 2018/12/4
 */
public class ShowFake3DView extends RelativeLayout {
    private Context context;
    private String folderUrl;
    private ArrayList<String> list;
    private int startX, currentX;
    private ZoomableViewGroup zoomableViewGroup;
    private ImageView imageView;
    // 当前图片的编号,初始为1
    private Bitmap nextBitmap;
    private int scrNum = 1;
    private Rect dstRect;
    private double width;
    private double hight;

    public ShowFake3DView(Context context) {
        super(context);
        initView(context);
    }

    public ShowFake3DView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ShowFake3DView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setBackgroundColor(int color){
        zoomableViewGroup.setBackgroundColor(color);
    }

    private void initView(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_3d, this);
        imageView = this.findViewById(R.id.iv);
        zoomableViewGroup = this.findViewById(R.id.zoomableViewGroup);
        zoomableViewGroup.setListener(new ZoomableViewGroup.ImgUpData() {
            @Override
            public void imgUpdata(Matrix matrix) {
                if (dstRect != null) {
                    RectF rectF = new RectF(dstRect.left, dstRect.top, dstRect.right, dstRect.bottom);
                    matrix.mapRect(rectF);
                    zoomableViewGroup.setRect(new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
                }

            }
        });
        zoomableViewGroup.setRect(dstRect);
    }

    private void autoGpuImageParmsLayout() {
        ViewTreeObserver viewTreeObserver = this.getViewTreeObserver();

        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                width = zoomableViewGroup.getMeasuredWidth();
                hight = zoomableViewGroup.getMeasuredHeight();
                if (nextBitmap != null && width != 0 && hight != 0) {
                    double w = width;
                    double h = hight;

                    double strx = 0;
                    double stry = 0;
                    if (w / h > nextBitmap.getWidth() * 1.0 / nextBitmap.getHeight()) {
                        w = h * nextBitmap.getWidth() * 1.0 / nextBitmap.getHeight();
                        strx = (width - w) / 2;
                    } else {
                        h = w * nextBitmap.getHeight() * 1.0 / nextBitmap.getWidth();
                        stry = (hight - h) / 2;
                    }

                    imageView.setLayoutParams(new RelativeLayout.LayoutParams((int) w, (int) h));
                    zoomableViewGroup.getPicWH((int) w, (int) h);
                    imageView.setTranslationX((int) strx);
                    imageView.setTranslationY((int) stry);

                    dstRect = new Rect((int) strx, (int) stry, (int) (w + strx), (int) (h + stry));
                }
            }
        });


    }


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == GONE || visibility == INVISIBLE) {
            width = 0;
            hight = 0;
            dstRect = null;
            startX = 0;
            currentX = 0;
            nextBitmap = null;
            zoomableViewGroup.resetMatrix();
        }
    }

    public void setFolderUrl(String url) {
        folderUrl = url;
        this.list = new ArrayList<>();
        File file = new File(url);
        if (!file.exists())
            return;
        final File[] subFile = file.listFiles();

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) { // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                if (subFile[iFileLength].getName().endsWith("jpg"))
                    list.add(folderUrl + subFile[iFileLength].getName());
                LogUtil.d("process::" + iFileLength + "");
            }
        }
        ArrayList(list);
        LogUtil.d(list.toString());
        setPicList(list);
    }

    /**
     * 按文件名排序
     *
     * @param list
     */
    public void ArrayList(List list) {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                try {
                    if (Long.parseLong(o1.substring(o1.lastIndexOf("/"), o1.lastIndexOf(".")).substring(5)) > Long.parseLong(o2.substring(o2.lastIndexOf("/"), o2.lastIndexOf(".")).substring(5))) {
                        return 1;
                    }
                    return -1;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return 1;
                }
            }
        });
    }

    public void setPicList(ArrayList<String> list) {
        this.list = list;
        try {
            nextBitmap = BitmapFactory.decodeFile(list.get(0));
            autoGpuImageParmsLayout();
        } catch (Exception e) {
            e.printStackTrace();
            nextBitmap = null;
        }
        if (nextBitmap != null) {
            imageView.setImageBitmap(nextBitmap);
        } else {
            Glide.with(context).load(list.get(0)).placeholder(imageView.getDrawable()).into(imageView);
        }

        double w = zoomableViewGroup.getWidth();
        double h = zoomableViewGroup.getHeight();

        double strx = 0;
        double stry = 0;
        if (w / h > nextBitmap.getWidth() * 1.0 / nextBitmap.getHeight()) {
            w = h * nextBitmap.getWidth() * 1.0 / nextBitmap.getHeight();
            strx = (zoomableViewGroup.getWidth() - w) / 2;
        } else {
            h = w * nextBitmap.getHeight() * 1.0 / nextBitmap.getWidth();
            stry = (zoomableViewGroup.getHeight() - h) / 2;
        }
        dstRect = new Rect((int) strx, (int) stry, (int) (w + strx), (int) (h + stry));
        imageView.setLayoutParams(new RelativeLayout.LayoutParams((int) w, (int) h));
        zoomableViewGroup.setRect(dstRect);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        zoomableViewGroup.touchEvent(event);
        if (zoomableViewGroup.getScale() == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = (int) event.getRawX();
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (event.getPointerCount() == 1) {
                        currentX = (int) event.getRawX();
                        // 判断手势滑动方向，并切换图片
                        if (currentX - startX > 80) {
                            //modifySrcL();
                            for (int i = 0; i < (currentX - startX) / 80; i++) {
                                modifySrcL();
                            }
                        } else if (currentX - startX < -80) {
                            // modifySrcR();
                            for (int i = 0; i < (currentX - startX) / -80; i++) {
                                modifySrcR();
                            }
                        }
                    }
                    // 重置起始位置
//                startX = (int) event.getX();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_UP:
                    break;
            }
        }
        return true;
    }

    // 向右滑动修改资源
    private void modifySrcR() {
        if (list == null) {
            return;
        }
        if (scrNum > list.size()) {
            scrNum = 1;
        }
        if (scrNum > 0) {
            try {
                nextBitmap = BitmapFactory.decodeFile(list.get(scrNum - 1));
            } catch (Exception e) {
                e.printStackTrace();
                nextBitmap = null;
            }
            if (nextBitmap != null) {
                imageView.setImageBitmap(nextBitmap);
            } else {
                Glide.with(context).load(list.get(scrNum - 1)).placeholder(imageView.getDrawable()).into(imageView);
            }
            scrNum++;
        }
        startX = currentX;

    }

    // 向左滑动修改资源
    private void modifySrcL() {
        if (list == null) {
            return;
        }
        if (scrNum <= 0) {
            scrNum = list.size();
        }
        if (scrNum <= list.size()) {
            try {
                nextBitmap = BitmapFactory.decodeFile(list.get(scrNum - 1));
            } catch (Exception e) {
                nextBitmap = null;
                e.printStackTrace();
            }
            if (nextBitmap != null) {
                imageView.setImageBitmap(nextBitmap);
            } else {
                Glide.with(context).load(list.get(scrNum - 1)).placeholder(imageView.getDrawable()).into(imageView);
            }
            scrNum--;
        }
        startX = currentX;
    }
}
