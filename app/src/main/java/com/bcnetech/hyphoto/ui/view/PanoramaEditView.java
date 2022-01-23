package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.JsonUtil;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.data.AIUploadData;
import com.bcnetech.hyphoto.model.ImageParmsModel;
import com.bcnetech.hyphoto.ui.view.scaleview.BaseScaleView;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.FontImageUtil;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.databinding.PanoramaEditLayoutBinding;
import com.example.imageproc.Process;
import com.example.imageproc.jni.ProcessByte;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDefinitionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.gpumanage.GPUImageFilterTools;
import jp.co.cyberagent.android.gpuimage.gputexture.GPUImageTexture;

/**
 * @author wsbai
 * @date 2018/12/2
 */
public class PanoramaEditView extends BaseRelativeLayout {
    public static final int MINLENGTH = 800;
    public static final int TYPE_PREVIEW = 0;
    public static final int TYPE_3D = 1;
    private static final int FILTERSCALE = 20;
    private int currentType = TYPE_PREVIEW;
    private Activity activity;
    private String panoramaUrl;//包含全景图片集合的文件夹
    private PanoramaEditLayoutBinding panoramaEditLayoutBinding;
    private int[] cutRect;//裁剪图片需要的坐标
    private ArrayList<String> panoramasList;
    private ArrayList<String> panoramasRenderList;
    private long duration;//视频时长
    private Size videoSize;//视频宽高
    private ImageParmsModel imageParmsModel;
    private HashMap<Integer, GPUImageFilterTools.FilterAdjuster> adjustMap;
    private PanoramaListener panoramaListener;
    private Bitmap backgroundBitmap;
    private GPUImageFilter SaturationFilter, ExposureFilter, ContrastFilter, DefiinitionFilter;
    private GPUImageFilter currentFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjusterSaturation, mFilterAdjusterExposure, mFilterAdjusterContrast, mFilterAdjusterDefinition;
    private ArrayList<GPUImageFilter> gpuImageFilters;
    private Context context;
    private int currentRender = 100;
    private Bitmap sampleBitmap;
    private ValueAnimator inAnim, outAnim;
    private boolean isSaveFile = false;
    private Size finalSize;
    private Size cutSize;

    public PanoramaEditView(Context context) {
        super(context);
        this.context = context;
    }

    public PanoramaEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public PanoramaEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void initView() {
        super.initView();
        panoramaEditLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.panorama_edit_layout, this, true);
        panoramaEditLayoutBinding.ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentType == TYPE_3D) {
                    setType(TYPE_PREVIEW);
                } else {
                    panoramaListener.onPanoramaEditViewGone();
                    PanoramaEditView.this.show(false);
                }
            }
        });
        panoramaEditLayoutBinding.rlButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentType == TYPE_PREVIEW) {
                    panoramaEditLayoutBinding.filterwait.show(true);
                } else {
                    String title = panoramaEditLayoutBinding.etTitle.getText().toString();
                    if (title == null || StringUtil.isBlank(title)) {
                        panoramaEditLayoutBinding.etTitle.setHint(R.string.addhint);
                    } else if (FontImageUtil.containsEmoji(title) || FontImageUtil.ishaveCharacter(title)) {
                        ToastUtil.toast(getContext().getString(R.string.photo_360_hint_share));
                    } else {
                        saveAndExit(title);
                        panoramaListener.saveAndExit("file://" + panoramasRenderList.get(0), panoramaUrl, title);
                        panoramaListener.onPanoramaEditViewGone();
                        PanoramaEditView.this.show(false);
                        setType(TYPE_PREVIEW);

                        panoramaEditLayoutBinding.etTitle.setText("");
                    }
                }
            }
        });

    }

    public void onBackPress() {
        if (currentType == TYPE_3D) {
            setType(TYPE_PREVIEW);
        } else {
            panoramaListener.onPanoramaEditViewGone();
            PanoramaEditView.this.show(false);
        }
    }


    @Override
    protected void initData() {
        super.initData();
        panoramaEditLayoutBinding.filterwait.setFilterWaitViewListener(new FilterWaitView.FilterWaitViewListener() {
            @Override
            public void onClose() {
                setType(TYPE_3D);
            }

            @Override
            public void onShow() {
                FilterBitmap();
            }
        });
        panoramaEditLayoutBinding.gpuimage.setScaleType(GPUImageTexture.ScaleType.CENTER_INSIDE);
        panoramaEditLayoutBinding.sbAdjust.setOnScrollListener(new BaseScaleView.OnScrollListener() {
            @Override
            public void onScaleScroll(int scale) {
                LogUtil.d(scale + "");
                currentRender = scale * 2;
                scaleFilters(scale);
                panoramaEditLayoutBinding.gpuimage.requestRender();
            }
        });
    }

    private void scaleFilters(int scale) {
        currentRender = scale * 2;
        int definitionScale = Math.abs((int) ((currentRender - 100) * 0.4)) + 100;
        if(null!=mFilterAdjusterDefinition){
            mFilterAdjusterDefinition.adjust(definitionScale);

        }else {
            if(null==DefiinitionFilter){
                DefiinitionFilter = new GPUImageDefinitionFilter();
            }
            mFilterAdjusterDefinition = new GPUImageFilterTools.FilterAdjuster(DefiinitionFilter);
            mFilterAdjusterDefinition.adjust(definitionScale);

        }

        int saturationcale = Math.abs((int) ((currentRender - 100) / 4)) + 100;
        if(null!=mFilterAdjusterSaturation){
            mFilterAdjusterSaturation.adjust(saturationcale);

        }else {
            if(null==SaturationFilter){
                SaturationFilter = new GPUImageSaturationFilter(1.0f);

            }
            mFilterAdjusterSaturation = new GPUImageFilterTools.FilterAdjuster(SaturationFilter);
            mFilterAdjusterSaturation.adjust(saturationcale);
        }

        int contrastscale = Math.abs((int) (currentRender - 100) / 4) + 100;
        if(null!=mFilterAdjusterContrast){
            mFilterAdjusterContrast.adjust(contrastscale);

        }else {
            if(null==ContrastFilter){
                ContrastFilter = new GPUImageContrastFilter(2.0f);

            }
            mFilterAdjusterContrast = new GPUImageFilterTools.FilterAdjuster(ContrastFilter);
            mFilterAdjusterContrast.adjust(contrastscale);

        }

        int exposurescale = (int) ((((int) (currentRender - 100) / 4) * 4) / 2.0 + 10) + 100;
        if(null!=mFilterAdjusterExposure){
            mFilterAdjusterExposure.adjust(exposurescale);

        }else {
            if(null==ExposureFilter){
                ExposureFilter = new GPUImageExposureFilter(0.0f);
            }
            mFilterAdjusterExposure = new GPUImageFilterTools.FilterAdjuster(ExposureFilter);
            mFilterAdjusterExposure.adjust(exposurescale);

        }
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    }

    public void setType(int type) {
        this.currentType = type;
        switch (this.currentType) {
            case TYPE_PREVIEW:
                panoramaEditLayoutBinding.gpuimage.setVisibility(VISIBLE);
                panoramaEditLayoutBinding.gpuimage.onResume();
                setCoverFilter();
                initFilter();
                panoramaEditLayoutBinding.tvButton.setText(getResources().getString(R.string.built_360));
                panoramaEditLayoutBinding.filterCover.setVisibility(VISIBLE);
                panoramaEditLayoutBinding.show3dview.setVisibility(GONE);
                panoramaEditLayoutBinding.sbAdjust.setVisibility(VISIBLE);
                panoramaEditLayoutBinding.filterName.setVisibility(GONE);
                break;
            case TYPE_3D:
                panoramaEditLayoutBinding.show3dview.setBackgroundColor(Color.BLACK);
                panoramaEditLayoutBinding.filterName.setVisibility(VISIBLE);
                panoramaEditLayoutBinding.sbAdjust.setVisibility(GONE);
                panoramaEditLayoutBinding.filterCover.setVisibility(GONE);
                panoramaEditLayoutBinding.gpuimage.onPause();
                panoramaEditLayoutBinding.gpuimage.setVisibility(GONE);
                panoramaEditLayoutBinding.show3dview.setVisibility(VISIBLE);
                panoramaEditLayoutBinding.show3dview.setPicList(panoramasRenderList);
                panoramaEditLayoutBinding.tvButton.setText(getResources().getString(R.string.save));
                break;
        }
    }

    /**
     * 遍历文件夹下所有文件
     * 获取生成的帧图片列表
     *
     * @param fileAbsolutePath
     * @return
     */
    public void getFileName(String fileAbsolutePath, final Size videoSize, int[] cutRect) {
        panoramaListener.onProcessCrop();
        panoramasList = new ArrayList<>();
        this.cutRect = cutRect;
        this.panoramaUrl = fileAbsolutePath;
        this.videoSize = videoSize;
        File file = new File(fileAbsolutePath);
        if (!file.exists())
            return;
        final File[] subFile = file.listFiles();
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) { // 判断是否为文件夹
                    if (!subFile[iFileLength].isDirectory()) {
                        if (sampleBitmap == null) {
                            try {
                                //获取原图，用于滤镜展示
                                byte[] bytes = InputStream2ByteArray(panoramaUrl + subFile[0].getName());
                                int parm[] = new int[5];
                                parm[0] = (0);
                                parm[1] = (0);
                                parm[2] = (videoSize.getWidth());
                                parm[3] = (videoSize.getHeight());
                                parm[4] = 1;
                                final ProcessByte jin = new ProcessByte();
                                jin.iparams = parm;
                                jin.img_width = videoSize.getHeight();
                                jin.img_height = videoSize.getWidth();
                                jin.srcbuf = bytes;
                                jin.api_method = Process.JNIAPI_METHOD_FAKE3D2;
                                final ProcessByte res = (ProcessByte) Process.byteJniApiMethod(jin);
                                int[] pixels = res.dstbuf;
                                sampleBitmap = Bitmap.createBitmap(pixels, 0, videoSize.getWidth(), videoSize.getWidth(), videoSize.getHeight(), Bitmap.Config.ARGB_8888);
                            } catch (IOException e) {

                            }
                        }
                        //裁剪图片
                        panoramasList.add("file://" + Yuv2Bitmap(panoramaUrl + subFile[iFileLength].getName()));
                        LogUtil.d("process::" + iFileLength + "");
                    }
                }
                panoramaListener.onProcessOver();
            }
        });
    }

    /**
     * 设置进度条图片
     */
    public void setAdapter() {
        initFilter();
        ArrayList<Bitmap> FilterBitmaps = new ArrayList<>();
        GPUImageTexture gpuImageTexture = new GPUImageTexture(getContext());
        gpuImageTexture.setImage(scaleBitmap(sampleBitmap));
        gpuImageTexture.setFilter(currentFilter);
        for (int i = 0; i < FilterProcessView.FILTERPROCESSCOUNT; i++) {
            scaleFilters(FILTERSCALE * i);
            gpuImageTexture.requestRender();
            Bitmap bitmap = gpuImageTexture.getBitmapWithFilterApplied();
            FilterBitmaps.add(bitmap);
            //FilterBitmaps.add(scaleBitmap(sampleBitmap));
        }
        gpuImageTexture = null;
        destroyFilter();
        panoramaEditLayoutBinding.sbAdjust.setBitmapList(FilterBitmaps);
    }

    /**
     * 缩小进度条图片
     *
     * @param bitmap
     * @return
     */
    private Bitmap scaleBitmap(Bitmap bitmap) {
        float width = ContentUtil.dip2px(context, 52);
        float scale = width / (float) bitmap.getWidth();
        float height = bitmap.getHeight() * scale;
        return Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
    }

    public void setCoverFilter() {
        // clearTempRender();
        panoramaEditLayoutBinding.gpuimage.getGPUImage().requestRender();
        //Bitmap bitmap = BitmapFactory.decodeFile(panoramasList.get(0).substring(7));
        panoramaEditLayoutBinding.gpuimage.setImage(sampleBitmap);
    }

    private void clearOriginFiles() {
        if (panoramasList != null) {
            for (int i = 0; i < panoramasList.size(); i++) {
                if (panoramasList.get(i).contains("origin")) {
                    File file = new File(panoramasList.get(i).substring(7));
                    if (file.exists())
                        file.delete();
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void initFilter() {
        SaturationFilter = new GPUImageSaturationFilter(1.0f);
        ContrastFilter = new GPUImageContrastFilter(2.0f);
        ExposureFilter = new GPUImageExposureFilter(0.0f);
        DefiinitionFilter = new GPUImageDefinitionFilter();
        gpuImageFilters = new ArrayList<>();
        gpuImageFilters.add(SaturationFilter);
        gpuImageFilters.add(ContrastFilter);
        gpuImageFilters.add(ExposureFilter);
        gpuImageFilters.add(DefiinitionFilter);
        currentFilter = new GPUImageFilterGroup(gpuImageFilters);
        panoramaEditLayoutBinding.gpuimage.setFilter(currentFilter);
        mFilterAdjusterSaturation = new GPUImageFilterTools.FilterAdjuster(SaturationFilter);
        mFilterAdjusterContrast = new GPUImageFilterTools.FilterAdjuster(ContrastFilter);
        mFilterAdjusterExposure = new GPUImageFilterTools.FilterAdjuster(ExposureFilter);
        mFilterAdjusterDefinition = new GPUImageFilterTools.FilterAdjuster(DefiinitionFilter);
        scaleFilters(currentRender);
        panoramaEditLayoutBinding.gpuimage.requestRender();
    }

    public void destroyFilter() {
        if (SaturationFilter != null)
            SaturationFilter.destroy();
        SaturationFilter = null;
        if (ContrastFilter != null)
            ContrastFilter.destroy();
        ContrastFilter = null;
        if (ExposureFilter != null)
            ExposureFilter.destroy();
        if (DefiinitionFilter != null)
            DefiinitionFilter.destroy();
        ExposureFilter = null;
        mFilterAdjusterContrast = null;
        mFilterAdjusterSaturation = null;
        mFilterAdjusterExposure = null;
        mFilterAdjusterDefinition = null;
        panoramaEditLayoutBinding.gpuimage.getGPUImage().deleteImage();
        panoramaEditLayoutBinding.sbAdjust.reset();
    }

    private Queue<String> mRunOnDraw;

    private void FilterBitmap() {
        if (mRunOnDraw == null)
            mRunOnDraw = new LinkedList<>();
        if (panoramasRenderList == null) {
            panoramasRenderList = new ArrayList<>();
        }
        panoramasRenderList.clear();
        mRunOnDraw.clear();
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < panoramasList.size(); i++) {
                    mRunOnDraw.add(panoramasList.get(i));

                }
                getImage(mRunOnDraw.poll());

            }
        });
       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 100);*/
    }

    private synchronized void getImage(String path) {
        if (mRunOnDraw != null && !mRunOnDraw.isEmpty()) {
            GPUImageTexture gpuImageTexture = new GPUImageTexture(getContext());
            Bitmap origin = BitmapFactory.decodeFile(path.substring(7));
            gpuImageTexture.setImage(origin);
            GPUImageFilterGroup group = new GPUImageFilterGroup();
            group.addFilter(SaturationFilter.clone());
            group.addFilter(ExposureFilter.clone());
            group.addFilter(ContrastFilter.clone());
            group.addFilter(DefiinitionFilter.clone());
            gpuImageTexture.setFilter(group);
            Bitmap bitmap = gpuImageTexture.getBitmapWithFilterApplied();
            try {
                String bitmapUrl = FileUtil.savecustomBitmap(bitmap, panoramaUrl, path.substring(path.lastIndexOf('/') + 1).substring(7));
                panoramasRenderList.add(bitmapUrl);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                gpuImageTexture.deleteImage();
                gpuImageTexture = null;
                origin.recycle();
                bitmap.recycle();
            }
            getImage(mRunOnDraw.poll());
        } else {
            destroyFilter();
            panoramaListener.FilterBitmapFin();
        }
    }

    public void onDestroy() {
        finalSize = null;
        cutSize = null;
        SaturationFilter = null;
        ExposureFilter = null;
        ContrastFilter = null;
        DefiinitionFilter = null;
        gpuImageFilters = null;
        mFilterAdjusterDefinition = null;
        mFilterAdjusterSaturation = null;
        mFilterAdjusterExposure = null;
        mFilterAdjusterContrast = null;
        if (sampleBitmap != null) {
            sampleBitmap.recycle();
            sampleBitmap = null;
        }
        if (backgroundBitmap != null) {
            backgroundBitmap.recycle();
            backgroundBitmap = null;
        }
        panoramaEditLayoutBinding.etTitle.setText("");
        panoramaEditLayoutBinding.gpuimage.onPause();
        panoramaEditLayoutBinding.gpuimage.getGPUImage().deleteImage();
    }

    private byte[] InputStream2ByteArray(String filePath) throws IOException {

        InputStream in = new FileInputStream(filePath);
        byte[] data = toByteArray(in);
        in.close();

        return data;
    }

    private byte[] toByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    private String Yuv2Bitmap(String url) {
        if (cutRect != null) {

            if (cutRect[0] < 0)
                cutRect[0] = 0;

            if (cutRect[1] < 0)
                cutRect[1] = 0;
            if (cutRect[0] + cutRect[2] > videoSize.getWidth()) {
                cutRect[2] = videoSize.getWidth() - cutRect[0];
            }
            if (cutRect[1] + cutRect[3] > videoSize.getHeight()) {
                cutRect[3] = videoSize.getHeight() - cutRect[1];
            }

            try {
                byte[] bytes = InputStream2ByteArray(url);
                int parm[] = new int[5];
                parm[0] = (cutRect[0]);
                parm[1] = (cutRect[1]);
                parm[2] = (cutRect[2]);
                parm[3] = (cutRect[3]);
                parm[4] = 1;
                final ProcessByte jin = new ProcessByte();
                jin.iparams = parm;
                jin.img_width = videoSize.getHeight();
                jin.img_height = videoSize.getWidth();
                jin.srcbuf = bytes;
                jin.api_method = Process.JNIAPI_METHOD_FAKE3D2;
                final ProcessByte res = (ProcessByte) Process.byteJniApiMethod(jin);
                int[] pixels = res.dstbuf;
                Bitmap bitmap = Bitmap.createBitmap(pixels, 0, cutRect[2], cutRect[2], cutRect[3], Bitmap.Config.ARGB_8888);
            /*if (sampleBitmap == null)
                sampleBitmap = Bitmap.createBitmap(pixels, 0, cutRect[2], cutRect[2], cutRect[3], Bitmap.Config.ARGB_8888);*/
                String name = url.substring(url.lastIndexOf("/"), url.lastIndexOf(".")).substring(1);
                String bitmapUrl = FileUtil.savecustomBitmap(mergeBitmap(bitmap), panoramaUrl, "origin-" + name + ".jpg");
                File file = new File(url);
                if (file.exists()) {
                    file.delete();
                }
                if (cutSize == null) {
                    cutSize = new Size(parm[2], parm[3]);
                }
                return bitmapUrl;
            } catch (Exception e) {

                e.printStackTrace();
            }
        }


        return null;
    }

    private float calculateSquareRatio(Size big, Size small) {
        long bigSquare = big.getWidth() * big.getHeight();
        long smallSquare = small.getHeight() * small.getWidth();
        return (float) smallSquare / (float) bigSquare;
    }

    /**
     * 生成图片的宽高为裁剪过后图片高的1.25倍
     *
     * @param bitmap
     */
    private Bitmap mergeBitmap(Bitmap bitmap) {
        int maxLength = 0;
        if (Math.max(bitmap.getWidth(), bitmap.getHeight()) < MINLENGTH) {
            maxLength = MINLENGTH;
        } else {
            maxLength = (int) (Math.max(bitmap.getWidth(), bitmap.getHeight()) * 1.25);
        }
        Size smallSize = new Size(bitmap.getWidth(), bitmap.getHeight());
        Rect mSrcRect = new Rect(0, 0, smallSize.getWidth(), smallSize.getHeight());
        // 计算左边位置
        int left = maxLength / 2 - smallSize.getWidth() / 2;
        // 计算上边位置
        int top = maxLength / 2 - smallSize.getHeight() / 2;
        Rect mDestRect = new Rect(left, top, left + smallSize.getWidth(), top + smallSize.getHeight());
        if (backgroundBitmap == null) {
            //生成背景图
            backgroundBitmap = Bitmap.createBitmap(maxLength, maxLength, Bitmap.Config.ARGB_8888);
            int[] pix = new int[maxLength * maxLength];
            for (int y = 0; y < maxLength; y++)
                for (int x = 0; x < maxLength; x++) {
                    int index = y * maxLength + x;
                    int r = ((pix[index] >> 16) & 0xff) | 0xff;
                    int g = ((pix[index] >> 8) & 0xff) | 0xff;
                    int b = (pix[index] & 0xff) | 0xff;
                    pix[index] = 0xff000000 | (r << 16) | (g << 8) | b;
                    //  pix[index] = 0xff000000;

                }
            //　合并图片
            backgroundBitmap.setPixels(pix, 0, maxLength, 0, 0, maxLength, maxLength);
        }
        Canvas canvas = new Canvas(backgroundBitmap);
        canvas.drawBitmap(bitmap, mSrcRect, mDestRect, new Paint());
        if (finalSize == null) {
            finalSize = new Size(backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
        }
        bitmap.recycle();
        return backgroundBitmap;
    }

    public boolean isSaveFile() {
        return isSaveFile;
    }

    private void saveAndExit(String title) {
        clearOriginFiles();
        final float ratio;
        if (finalSize != null && cutSize != null) {
            ratio = calculateSquareRatio(finalSize, cutSize);
        } else {
            ratio = 0.5f;
        }
        panoramaEditLayoutBinding.show3dview.ArrayList(panoramasRenderList);
        //保存json
        isSaveFile = true;
        AIUploadData aiUploadData = new AIUploadData();
        AIUploadData.Content content = new AIUploadData.Content();
        aiUploadData.setRation(ratio);
        aiUploadData.setName(title);
        aiUploadData.setId(title);
        List<AIUploadData.Content.A> listA = new ArrayList<>();
        for (int i = 0; i < panoramasRenderList.size(); i++) {
            AIUploadData.Content.A a = new AIUploadData.Content.A();
            String name = panoramasRenderList.get(i).substring(panoramasRenderList.get(i).lastIndexOf('/') + 1);
            a.setId(name);
            listA.add(a);
        }
        content.setA(listA);
        aiUploadData.setContent(content);
        String aiUploadDataJson = JsonUtil.Object2Json(aiUploadData);
        try {
            FileUtil.saveImageDataJson(panoramaUrl, System.currentTimeMillis() + "", aiUploadDataJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disMissFilterWait() {
        panoramaEditLayoutBinding.filterwait.show(false);
    }

    public interface PanoramaListener {
        void onProcessCrop();

        void onProcessOver();

        void saveAndExit(String smallUrl, String localUrl, String title);

        void onPanoramaEditViewGone();

        void FilterBitmapFin();

        void onClose();
    }

    public void setPanoramaListener(PanoramaListener panoramaListener) {
        this.panoramaListener = panoramaListener;
    }

    private void initAnim() {
        outAnim = AnimFactory.ImgAlphaAnimOut(this);
        outAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                PanoramaEditView.this.setVisibility(GONE);
                if (panoramaListener != null)
                    panoramaListener.onClose();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                PanoramaEditView.this.setVisibility(GONE);
                if (panoramaListener != null)
                    panoramaListener.onClose();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        inAnim = AnimFactory.ImgAlphaAnim(this);
        inAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                PanoramaEditView.this.bringToFront();
                PanoramaEditView.this.setVisibility(VISIBLE);
                panoramaEditLayoutBinding.gpuimage.onResume();
                isSaveFile = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                PanoramaEditView.this.setVisibility(VISIBLE);
                panoramaEditLayoutBinding.gpuimage.onResume();
                isSaveFile = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void setFilterWait() {
        panoramaEditLayoutBinding.filterwait.init();
    }

    public void show(boolean isShow) {
        panoramaEditLayoutBinding.etTitle.setHint("");
        initAnim();
        if (isShow) {
            inAnim.start();
        } else {
            outAnim.start();
        }
    }

}
