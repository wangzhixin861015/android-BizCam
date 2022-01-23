package com.bcnetech.hyphoto.ui.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bcnetech.bcnetchhttp.config.UrlConstants;
import com.bcnetech.bcnetechlibrary.activity.BaseActivity;

import com.bcnetech.hyphoto.databinding.ActivityGoodsMarketBinding;
import com.bcnetech.hyphoto.R;

/**
 * Created by wangzhixin on 2018/12/28.
 */

public class GoodsMarketActivity extends BaseActivity {

    private ActivityGoodsMarketBinding activityGoodsMarketBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
        onViewClick();
    }

    @SuppressLint("JavascriptInterface")
    @Override
    protected void initView() {
        activityGoodsMarketBinding = DataBindingUtil.setContentView(this, R.layout.activity_goods_market);
        activityGoodsMarketBinding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        activityGoodsMarketBinding.webview.loadUrl(UrlConstants.MALL);
        activityGoodsMarketBinding.webview.addJavascriptInterface(this, "Mobile");

        WebSettings settings=activityGoodsMarketBinding.webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
//        适应webview
        settings.setLoadWithOverviewMode(true);
//        设置可以支持缩放
        settings.setSupportZoom(true);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onViewClick() {

    }
    @JavascriptInterface
    public void goTaobao(String goodsId){
        String newGoodsId=goodsId;
        String taobaoAppStr_goods = "taobao://item.taobao.com/item.htm?id=";
        String taobaoWebStr_goods = "https://item.taobao.com/item.htm?id=";
        taobaoAppStr_goods=taobaoAppStr_goods+newGoodsId;
        taobaoWebStr_goods=taobaoWebStr_goods+newGoodsId;
        if (isAppInstalled(this, "com.taobao.taobao")) {
            Intent intent2 = getPackageManager().getLaunchIntentForPackage("com.taobao.taobao"); //这行代码比较重要
            intent2.setAction("android.intent.action.VIEW");
            intent2.setClassName("com.taobao.taobao", "com.taobao.tao.detail.activity.DetailActivity");
            Uri uri = Uri.parse(taobaoAppStr_goods);
            intent2.setData(uri);
            startActivity(intent2);
        }else{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(taobaoWebStr_goods));
            startActivity(intent);
        }
    }

    @JavascriptInterface
    public void webClose(){
        finish();
    }
    private boolean isAppInstalled(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }
    /**
     * 重写onKeyDown，当浏览网页，WebView可以后退时执行后退操作。
     * false 执行安卓返回方法即webview返回上一页 true 表示h5处理返回事件，android端不再处理
     * @param keyCode
     * @param event
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            activityGoodsMarketBinding.webview.evaluateJavascript("javascript:_Native_backListener()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    if ("false".equals(value) || "null".equals(value)) {
                        goBack();
                    }else if ("true".equals(value)){
                        // h5处理，客户端不做任何操作
                    }
                }
            });
        }
        return true;
    }



    private void goBack(){
        if (activityGoodsMarketBinding.webview.canGoBack()){
            activityGoodsMarketBinding.webview.goBack();
        }else{
            finish();
        }
    }
}
