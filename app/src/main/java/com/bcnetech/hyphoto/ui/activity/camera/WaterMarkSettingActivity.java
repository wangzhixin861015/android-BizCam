package com.bcnetech.hyphoto.ui.activity.camera;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnLayoutInflatedListener;
import com.app.hubert.guide.model.GuidePage;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.dialog.ChoiceDialog;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.hyphoto.data.WaterMarkData;
import com.bcnetech.hyphoto.databinding.ActivityWatermarksettingBinding;
import com.bcnetech.hyphoto.presenter.WaterMarkPresenter;
import com.bcnetech.hyphoto.presenter.iview.IWaterMarkView;
import com.bcnetech.hyphoto.ui.activity.BaseMvpActivity;
import com.bcnetech.hyphoto.ui.activity.cloud.CloudInfoActivity;
import com.bcnetech.hyphoto.ui.activity.cloud.CloundNewActivity;
import com.bcnetech.hyphoto.ui.popwindow.IntoPop;
import com.bcnetech.hyphoto.ui.view.CameraSettingItemView;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.ui.view.WMFlowLayout;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.Image.BaseUploadHeadModel;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.bcnetech.hyphoto.utils.WaterMarkUtil;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;
import com.bcnetech.hyphoto.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by a1234 on 2017/11/30.
 */

public class WaterMarkSettingActivity extends BaseMvpActivity<IWaterMarkView, WaterMarkPresenter> implements IWaterMarkView {
   private ActivityWatermarksettingBinding activityWatermarksettingBinding;
    private CameraStatus cameraStatus;
    private ChoiceDialog mchoiceDialog;
    private IntoPop intoPop;
    private Bitmap srcBitmap, wm_bitcam;


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
        activityWatermarksettingBinding = DataBindingUtil.setContentView(this,R.layout.activity_watermarksetting);
    }

    @Override
    protected void initData() {

        presenter.initWMList();
        cameraStatus = CameraStatus.getCameraStatus();
        activityWatermarksettingBinding.watermarkSettingTitle.setType(TitleView.PIC_PARMS_NEW);
        activityWatermarksettingBinding.watermarkSettingTitle.setRightImgIsShow(false);
        activityWatermarksettingBinding.watermarkSettingTitle.setTitleText(getResources().getString(R.string.watermark));
        activityWatermarksettingBinding.watermark.setSetting_name(getResources().getString(R.string.add_watermark));
        activityWatermarksettingBinding.watermark.isShowButton(true);
        srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_sample);
        wm_bitcam = BitmapFactory.decodeResource(getResources(), R.drawable.wm_bizcam);
        if (presenter.isFromRecord()) {
            activityWatermarksettingBinding.watermarkSettingTitle.setType(TitleView.WATERMARK_RECORD);
            activityWatermarksettingBinding.watermarkSettingTitle.setRightImgIsShow(false);
            activityWatermarksettingBinding.watermark.setVisibility(View.GONE);
        } else {
            if (!cameraStatus.getWaterMark().isWaterMarkOn()) {
                activityWatermarksettingBinding.watermark.setSwitchButton(false);
                activityWatermarksettingBinding.rlWm.setVisibility(View.GONE);
            } else {
                activityWatermarksettingBinding.watermark.setSwitchButton(true);
                activityWatermarksettingBinding.rlWm.setVisibility(View.VISIBLE);
            }
        }
        if (cameraStatus.getWaterMark().getWatermarkUrl() == null) {
            //sample_watermark.setImageDrawable(wm_bizcam.getDrawable());
            activityWatermarksettingBinding.sampleImage.setImageBitmap(WaterMarkUtil.createWaterMaskRightBottom(srcBitmap, wm_bitcam));
            activityWatermarksettingBinding.rlSelectWm.resetSelect(presenter.getWm_bizcam());
        } else {
            for (int i = 0; i < presenter.getWmlist().size(); i++) {
                if (null == presenter.getWmlist().get(i)) {
                    continue;
                }
                if (null == presenter.getWmlist().get(i).getWatermarkurl()) {
                    continue;
                }
                if (cameraStatus.getWaterMark().getWatermarkUrl().equals(presenter.getWmlist().get(i).getWatermarkurl())) {
                    //sample_watermark.setImageDrawable(wmlist.get(i).getDrawable());
                    activityWatermarksettingBinding.sampleImage.setImageBitmap(WaterMarkUtil.createWaterMaskRightBottom(srcBitmap, BitmapUtils.drawableToBitmap(presenter.getWmlist().get(i).getDrawable())));
                    activityWatermarksettingBinding.rlSelectWm.resetSelect(presenter.getWmlist().get(i));
                }
            }
        }
    }

    @Override
    protected void onViewClick() {
        activityWatermarksettingBinding.watermarkSettingTitle.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        activityWatermarksettingBinding.watermarkSettingTitle.setLeftTextListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraStatus cameraStatus = CameraStatus.getCameraStatus();
                cameraStatus.getWaterMark().setWaterMarkOn(false);
                CameraStatus.saveToFile(cameraStatus);
                finish();
            }
        });
        activityWatermarksettingBinding.watermarkSettingTitle.setRightTextListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraStatus cameraStatus = CameraStatus.getCameraStatus();
                cameraStatus.getWaterMark().setWaterMarkOn(true);
                CameraStatus.saveToFile(cameraStatus);
                finish();
            }
        });
        activityWatermarksettingBinding.watermark.setCurrentCheckedListener(new CameraSettingItemView.CurrentChecked() {
            @Override
            public void onCurrentCheck(boolean currentcheck) {
                if (currentcheck) {
                    activityWatermarksettingBinding.rlWm.setVisibility(View.VISIBLE);
                    cameraStatus.getWaterMark().setWaterMarkOn(true);
                } else {
                    activityWatermarksettingBinding.rlWm.setVisibility(View.GONE);
                    cameraStatus.getWaterMark().setWaterMarkOn(false);
                }

            }
        });

        activityWatermarksettingBinding.rlSelectWm.setOnFlowLayoutListener(new WMFlowLayout.FlowLayoutListener() {
            @Override
            public void onLongClick(WaterMarkData drawable) {
                for (int i = 0; i < presenter.getWmlist().size(); i++) {
                    if (drawable.equals(presenter.getWmlist().get(i)) && drawable != presenter.getWm_bizcam()) {
                        deleImageDialog(i);
                    }
                }
            }

            @Override
            public void onselect(WaterMarkData drawable) {
                if (null == drawable) {
                    into();
                    // addWaterMark();
                    return;
                }
                for (int i = 0; i < presenter.getWmlist().size(); i++) {
                    if (drawable.equals(presenter.getWmlist().get(i))) {
                        if (drawable == presenter.getWm_bizcam()) {
                            cameraStatus.getWaterMark().setWaterMarkStatus(CameraStatus.Size.WATERMARK_BIZCAM);
                            cameraStatus.getWaterMark().setWatermarkUrl(null);
                            //sample_watermark.setImageDrawable(wm_bizcam.getDrawable());
                            activityWatermarksettingBinding.sampleImage.setImageBitmap(WaterMarkUtil.createWaterMaskRightBottom(srcBitmap, wm_bitcam));
                        }
                        if (drawable.equals(presenter.getWmlist().get(i)) && drawable != presenter.getWm_bizcam()) {
                            cameraStatus.getWaterMark().setWaterMarkStatus(CameraStatus.Size.WATERMARK_CUSTOM);
                            cameraStatus.getWaterMark().setWatermarkUrl(drawable.getWatermarkurl());
                            // sample_watermark.setImageDrawable(wmlist.get(i).getDrawable());
                            activityWatermarksettingBinding.sampleImage.setImageBitmap(WaterMarkUtil.createWaterMaskRightBottom(srcBitmap, BitmapUtils.drawableToBitmap(presenter.getWmlist().get(i).getDrawable())));
                        }
                        CameraStatus.saveToFile(cameraStatus);
                    }
                }

            }
        });
    }

    private void addWaterMark() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        startActivityForResult(intent, BaseUploadHeadModel.REQUEST_CODE_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        showLoading();
        if (resultCode == ImageUtil.NONE) {
            dissmissDialog();
            return;
        }
        if (data == null) {
            dissmissDialog();
            return;
        }

        // 从相册获取照片
        if (BaseUploadHeadModel.REQUEST_CODE_ALBUM == requestCode) {
            if (null == data.getData()) {
                dissmissDialog();
                return;
            }
            String url = presenter.getRealFilePath(data.getData());
            presenter.loadAndCut("file://" + url);
            dissmissDialog();
            return;
        }

        if (requestCode == resultCode && resultCode == Flag.CLOUDWATERMARK) {
            String finalurl = data.getStringExtra("watermark");
            if (finalurl != null && !StringUtil.isBlank(finalurl)) {
                presenter.loadAndCut(finalurl);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void showMyWaterMark(final String path) {
        ImageUtil.EBizImageLoad("file://" + path, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                dissmissDialog();
                Drawable drawable = new BitmapDrawable(bitmap);
                WaterMarkData waterMarkData = new WaterMarkData(drawable, path);
                presenter.getWmlist().add(1, waterMarkData);
                presenter.initSystemTag();
                activityWatermarksettingBinding.rlSelectWm.resetSelect(waterMarkData);
                //sample_watermark.setImageDrawable(waterMarkData.getDrawable());
                activityWatermarksettingBinding.sampleImage.setImageBitmap(WaterMarkUtil.createWaterMaskRightBottom(srcBitmap, bitmap));
                cameraStatus.getWaterMark().setWaterMarkStatus(CameraStatus.Size.WATERMARK_CUSTOM);
                cameraStatus.getWaterMark().setWatermarkUrl(waterMarkData.getWatermarkurl());
                CameraStatus.saveToFile(cameraStatus);
                dissmissDialog();
                initGuide();
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    public void deleImageDialog(final int position) {
        if (mchoiceDialog == null) {
            mchoiceDialog = ChoiceDialog.createInstance(WaterMarkSettingActivity.this);
        }
        mchoiceDialog.show();
        mchoiceDialog.setAblumTitle(WaterMarkSettingActivity.this.getResources().getString(R.string.delete));
        String message = WaterMarkSettingActivity.this.getResources().getString(R.string.delete_watermark);
        mchoiceDialog.setAblumMessageGray(message);
        mchoiceDialog.setChoiceCallback(new ChoiceDialog.CardDialogCallback() {
            @Override
            public void onOKClick() {
                String url = presenter.getWmlist().get(position).getWatermarkurl();
                presenter.deleteWM(presenter.getWmlist().get(position));
                presenter.getWmlist().remove(presenter.getWmlist().get(position));
                if (!presenter.getWmlist().contains(presenter.getWm_plus())) {
                    presenter.getWmlist().add(0, presenter.getWm_plus());
                }
                presenter.initSystemTag();
                if (CameraStatus.getCameraStatus().getWaterMark() != null && CameraStatus.getCameraStatus().getWaterMark().getWatermarkUrl() != null) {
                    if (CameraStatus.getCameraStatus().getWaterMark().getWatermarkUrl().equals(url)) {
                        activityWatermarksettingBinding.rlSelectWm.resetSelect(presenter.getWm_bizcam());
                        //sample_watermark.setImageDrawable(wm_bizcam.getDrawable());
                        activityWatermarksettingBinding.sampleImage.setImageBitmap(WaterMarkUtil.createWaterMaskRightBottom(srcBitmap, wm_bitcam));
                        cameraStatus.getWaterMark().setWaterMarkStatus(CameraStatus.Size.WATERMARK_BIZCAM);
                        cameraStatus.getWaterMark().setWatermarkUrl(presenter.getWm_bizcam().getWatermarkurl());
                        CameraStatus.saveToFile(cameraStatus);
                    }
                }
                dismiss();
            }

            @Override
            public void onCancelClick() {
                dismiss();
            }

            @Override
            public void onDismiss() {

            }
        });
       /* mchoiceDialog.setChoiceInterface(new ChoiceDialog5.ChoiceInterface() {
            @Override
            public void choiceOk() {
                String url = wmlist.get(position).getWatermarkurl();
                deleteWM(wmlist.get(position));
                wmlist.remove(wmlist.get(position));
                if (!wmlist.contains(wm_plus)) {
                    wmlist.add(0, wm_plus);
                }
                initSystemTag();
                if (CameraStatus.getCameraStatus().getWaterMark() != null && CameraStatus.getCameraStatus().getWaterMark().getWatermarkUrl() != null) {
                    if (CameraStatus.getCameraStatus().getWaterMark().getWatermarkUrl().equals(url)) {
                        rl_select_wm.resetSelect(wm_bizcam);
                        //sample_watermark.setImageDrawable(wm_bizcam.getDrawable());
                        sample_image.setImageBitmap(WaterMarkUtil.createWaterMaskRightBottom(srcBitmap,wm_bitcam));
                        cameraStatus.getWaterMark().setWaterMarkStatus(CameraStatus.Size.WATERMARK_BIZCAM);
                        cameraStatus.getWaterMark().setWatermarkUrl(wm_bizcam.getWatermarkurl());
                        CameraStatus.saveToFile(cameraStatus);
                    }
                }
                dismiss();
            }

            @Override
            public void choiceCencel() {
                dismiss();
            }
        });*/
    }

    private void dismiss() {
        if (mchoiceDialog != null) {
            mchoiceDialog.dismiss();
        }
    }

    private void into() {
        if (intoPop == null) {
            intoPop = new IntoPop(WaterMarkSettingActivity.this);
        }
        intoPop.showPop(this.getWindow().getDecorView());
        intoPop.setIntoClickListener(this, new IntoPop.IntoClickListener() {
            @Override
            public void nativeFile() {
                intoPop.dismissPop();
                addWaterMark();
            }

            @Override
            public void cloud() {
                intoPop.dismissPop();

                CloudInfoActivity.actionStart(WaterMarkSettingActivity.this, CloundNewActivity.CLOUDWATERMARK);
            }
        });
    }

    @Override
    public WMFlowLayout getWMFlowLayout() {
        return this.activityWatermarksettingBinding.rlSelectWm;
    }

    @Override
    public void showLoading() {
        showTransformDialog();
    }

    @Override
    public void hideLoading() {
        dissmissDialog();
    }

    @Override
    public void finishView(int resultCode, Intent intent) {

    }

    @Override
    public WaterMarkPresenter initPresenter() {
        return new WaterMarkPresenter();
    }

    @Override
    protected void initGuide() {
        super.initGuide();
        if (presenter.getWmlist() != null && presenter.getWmlist().size() > 2) {
            NewbieGuide.with(this)
                    .setLabel("pageWatermark")//设置引导层标示区分不同引导层，必传！否则报错
                    .alwaysShow(false)//是否每次都显示引导层，默认false，只显示一次
                    .addGuidePage(//添加一页引导页
                            GuidePage.newInstance()//创建一个实例
                                    .addHighLight(activityWatermarksettingBinding.rlSelectWm)
                                    .setLayoutRes(R.layout.guide_photoedit_select)//设置引导页布局
                                    .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {

                                        @Override
                                        public void onLayoutInflated(View view, Controller controller) {
                                            ImageView photoedit_autorecovery = (ImageView) view.findViewById(R.id.photoedit_autorecovery);
                                            photoedit_autorecovery.setImageDrawable(getResources().getDrawable(R.drawable.guide_wm));
                                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                            int[] location = new int[2];
                                            //rl_select_wm.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
                                            activityWatermarksettingBinding.rlSelectWm.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
                                            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                            params.setMargins(0, 0, 0, ContentUtil.getScreenHeight2(WaterMarkSettingActivity.this) - location[1]);
                                            photoedit_autorecovery.setLayoutParams(params);
                                            photoedit_autorecovery.setVisibility(View.VISIBLE);
                                        }
                                    })
                                    .setEnterAnimation(enterAnimation)//进入动画
                                    .setExitAnimation(exitAnimation)//退出动画
                    )
                    .show();//显示引导层(至少需要一页引导页才能显示)
        }
    }
}
