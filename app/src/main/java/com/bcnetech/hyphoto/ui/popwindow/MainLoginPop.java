package com.bcnetech.hyphoto.ui.popwindow;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetechlibrary.popwindow.BasePopWindow;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.hyphoto.ui.view.LockableScrollView;
import com.bcnetech.hyphoto.ui.view.MLoginNewView;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by yhf on 2017/11/20.
 */

public class MainLoginPop extends BasePopWindow {

    private Activity activity;
    //    private View view;
    private LockableScrollView lockableScrollView;
    private ImageView cameral_layout_rl;
    private RelativeLayout content;

    private MLoginNewView mLoginNewView;
    private boolean isClicked = false;


    public MainLoginPop(Activity activity) {
        super(activity);
        this.activity = activity;
        initView();
        initData();
        onViewClick();
        initAlpAnim(content);
        initBottomAnim(content);
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.main_login_pop, null);
        content = (RelativeLayout) view.findViewById(R.id.rl_content);
        lockableScrollView = (LockableScrollView) view.findViewById(R.id.lockableScrollView);
        cameral_layout_rl = (ImageView) view.findViewById(R.id.cameral_layout_rl);
        mLoginNewView = (MLoginNewView) view.findViewById(R.id.mLoginNewView);
        setContentView(view);
    }


    private void initData() {
        try {
            (new Handler()).post(new Runnable() {
                @Override
                public void run() {
                    applyBlur(cameral_layout_rl);
                }
            });
        } catch (Exception e) {

        }

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cameral_layout_rl.getLayoutParams();
        lp.height = ContentUtil.getScreenHeight(activity);
        lp.width = ContentUtil.getScreenWidth(activity);
        cameral_layout_rl.setLayoutParams(lp);
        lockableScrollView.setScrollingEnabled(false);


//        mLoginNewView.setMLoginInter(new MLoginNewView.MLoginInter() {
//            @Override
//            public void finishView() {
//                dismissPop();
//            }
//
//            @Override
//            public void onLogin() {
//
//            }
//        });


//        newDatas();
//        initBottomAnim(view, ContentUtil.dip2px(activity,56));
//        initAlpAnim(view);
//        listview.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
//        listview.setHasFixedSize(true);
//        adapter=new PartPaintPopAdapter(activity,datas);
//        listview.setAdapter(adapter);
    }

    /**
     * @param type
     */
    public void setType(int type) {
        mLoginNewView.setType(type);
    }

    public void setMLoginInter(MLoginNewView.MLoginInter mLoginInter) {
        mLoginNewView.setMLoginInter(mLoginInter);
        mLoginNewView.setClickInter(new MLoginNewView.ClickInter() {
            @Override
            public void onClick() {
                isClicked = true;
            }
        });
    }

    private void onViewClick() {
        setInputHeight(mLoginNewView);
    }

    @Override
    public void dismissPop() {
        outAnimation.start();
        alpOutAnim.start();
    }

    public void closeKeyBoard() {
        mLoginNewView.closeKeyBoard();
    }

    public void openKeyBoard() {
        mLoginNewView.openKeyBoard();
    }


    @Override
    public void showPop(final View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(cameral_layout_rl, "alpha", 1f, 0f, 1f);
        animator.setDuration(100);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                inAnimation.start();
                alpInAnim.start();
                showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.start();
    }

    @Override
    protected void initBottomAnim(View view) {
        super.initBottomAnim(view);
    }

    @Override
    protected void initAlpAnim(View view) {
        super.initAlpAnim(view);
    }

    public void openNumKeyBoard() {
        mLoginNewView.openNumKeyBoard();
    }


    /***
     * 高斯模糊背景
     * @param bg
     */
    private void applyBlur(View bg) {
//        View view = activity.getWindow().getDecorView();
//        view.setDrawingCacheEnabled(true);
//        view.buildDrawingCache(true);
//        view.destroyDrawingCache();


        RelativeLayout rl_main = (RelativeLayout) activity.findViewById(R.id.rl_main);
        rl_main.setDrawingCacheEnabled(true);
        rl_main.buildDrawingCache(true);
        rl_main.destroyDrawingCache();
        /**
         * 获取当前窗口快照，相当于截屏
         */
        Bitmap bmp1 = rl_main.getDrawingCache();
        int height = getOtherHeight();
        int virtualbar = getVirtualBarHeigh();

        if(height>=0&&virtualbar>=0){
            /**
             * 除去状态栏和标题栏
             */
            Bitmap bmp2 = Bitmap.createBitmap(bmp1, 0, height, bmp1.getWidth(), bmp1.getHeight() - height - virtualbar);
            blur(bmp2, bg);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void blur(Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 4;//图片缩放比例；
        float radius = 10;//模糊程度
        Bitmap overlay = ImageUtil.resizeImage(bkg,
                (int) (ContentUtil.getScreenWidth(activity) / scaleFactor),
                (int) ((ContentUtil.getScreenHeight(activity) - getOtherHeight()) / scaleFactor),0);

        overlay = ImageUtil.doBlur(overlay, (int) radius, false);
        Drawable drawable = new BitmapDrawable(activity.getResources(), overlay);

        view.setBackground(drawable);
        /**
         * 打印高斯模糊处理时间，如果时间大约16ms，用户就能感到到卡顿，时间越长卡顿越明显，如果对模糊完图片要求不高，可是将scaleFactor设置大一些。
         */
        Log.i("jerome", "blur time:" + (System.currentTimeMillis() - startMs));
    }

    /**
     * 获取系统状态栏和软件标题栏，部分软件没有标题栏，看自己软件的配置；
     *
     * @return
     */
    private int getOtherHeight() {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int contentTop = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentTop - statusBarHeight;
        return statusBarHeight + titleBarHeight;
    }

    /**
     * 获取虚拟功能键高度
     */
    public int getVirtualBarHeigh() {
        int vh = 0;
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }

    private int navigationBarHeight, statusBarHeight, currentkeyboardHeight, keyboardAnimHeight;

    public void setInputHeight(final View view) {
        final View myLayout = activity.getWindow().getDecorView();
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect(); // r will be populated with the coordinates of your view that area still visible.
                view.getWindowVisibleDisplayFrame(r);
                int screenHeight = myLayout.getRootView().getHeight();
                int heightDiff = screenHeight - (r.bottom - r.top);
                if (heightDiff > 100) // if more than 100 pixels, its probably a keyboard // get status bar height
                    statusBarHeight = 0;
                navigationBarHeight = ContentUtil.getNavigationBarHeight(activity);
                try {
                    Class<?> c = Class.forName("com.android.internal.R$dimen");
                    Object obj = c.newInstance();
                    Field field = c.getField("status_bar_height");
                    int x = Integer.parseInt(field.get(obj).toString());
                    statusBarHeight = activity.getResources().getDimensionPixelSize(x);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int realKeyboardHeight = (heightDiff - statusBarHeight - navigationBarHeight) > 300 ? heightDiff - statusBarHeight - navigationBarHeight : 0;
                if (currentkeyboardHeight != 0) {
                    keyboardAnimHeight = currentkeyboardHeight - realKeyboardHeight;
                }
                if (currentkeyboardHeight != realKeyboardHeight)
                    currentkeyboardHeight = realKeyboardHeight;
                if (isClicked && keyboardAnimHeight != 0 && Math.abs(keyboardAnimHeight) < 300) {
                    showKeyboardAnim(view);
                }
            }
        });
    }

    private void showKeyboardAnim(View view) {
        ObjectAnimator translationy = ObjectAnimator.ofFloat(view, "translationY", -keyboardAnimHeight, 0);
        translationy.start();
        keyboardAnimHeight = 0;
        isClicked = false;
    }


}
