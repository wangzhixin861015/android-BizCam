package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bcnetech.bcnetchhttp.RetrofitInternationalFactory;
import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.bean.request.ResetNameBody;
import com.bcnetech.bcnetchhttp.bean.response.LoginReceiveData;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetechlibrary.drawable.CircleDefultDrawable;
import com.bcnetech.bcnetechlibrary.drawable.CircleImageDrawable;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.bcnetechlibrary.view.LimitEditText;
import com.bcnetech.hyphoto.task.MBaseObserver;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.FontImageUtil;
import com.bcnetech.hyphoto.utils.Image.UploadHeadModel;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.KeyBoardUtil;
import com.bcnetech.hyphoto.utils.PinyUtil;
import com.bcnetech.hyphoto.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.UnsupportedEncodingException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.bcnetech.bcnetchhttp.bean.response.LoginedUser.getLoginedUser;


/**
 * Created by a1234 on 2017/12/6.
 */

public class EditUserView extends BaseRelativeLayout {
    public final static int PERSON_ACTIVITY = 1;
    private TitleView edit_title;
    private DrawTextImageView drawTextImageView;
    private ImageView iv_head;
    private LimitEditText nick_name;
    private LoginedUser loginedUser;
    private UploadHeadModel uploadHeadModel;
    private Handler handler;
    String loginNickName;
    String nicknane;
    private EditUserListener listener;
    private ValueAnimator inAnim,outAnim;
    private int height;
    private Bitmap currentHeadimg;


    public EditUserView(Context context) {
        super(context);
    }

    public EditUserView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditUserView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.edit_user_view, this);
        edit_title = (TitleView) findViewById(R.id.edit_title);
        drawTextImageView = (DrawTextImageView) findViewById(R.id.iv_headimg);
        nick_name = (LimitEditText) findViewById(R.id.nick_name);
    }

    @Override
    protected void initData() {
        super.initData();
        edit_title.setType(TitleView.IMAGE_BG_REPAIR);
        edit_title.bringToFront();
        initAnim();
    }

    public void setUserInfo(){
        loginedUser = getLoginedUser();
        nicknane = loginedUser.getNickname();
        loginNickName = loginedUser.getNickname();
        if (nicknane != null) {
            nick_name.setText(nicknane);
        }
        /*if (loginedUser.getLoginData().getAvatar() != null && !loginedUser.getLoginData().getAvatar().equals("null")) {//有头像使用头像url
            String avatar = BitmapUtils.getBitmapUrl3(loginedUser.getLoginData().getAvatar());
            ShowsetHead(avatar);
        } else {
            setNativeHeader();
        }*/
        if (currentHeadimg==null){
            setNativeHeader();
        }else{
            CircleImageDrawable circleImageDrawable = new CircleImageDrawable(currentHeadimg);
            circleImageDrawable.mutate().setColorFilter(getResources().getColor(R.color.bg_translucent), PorterDuff.Mode.MULTIPLY);
            drawTextImageView.setImageDrawable(circleImageDrawable);
        }
        handler = new Handler();
        uploadHeadModel = new UploadHeadModel();

        uploadHeadModel.initForActivity(drawTextImageView, (Activity) getContext(), handler == null ? new Handler() : handler);
        uploadHeadModel.setUploadListener(new UploadHeadModel.UploadListener() {
            @Override
            public void upDataImg(String data) {
                String avatar = BitmapUtils.getBitmapUrl3(data);
                ShowsetHead(avatar);
            }
        });
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();
        nick_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nick_name.setCursorVisible(true);
            }
        });

        //点击保存修改
        edit_title.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nickname = nick_name.getText().toString().trim();
                boolean isitok = FontImageUtil.ishaveCharacter(nickname);
                boolean iscontainEmoj = FontImageUtil.containsEmoji(nickname);
                if (isitok || iscontainEmoj) {
                    ToastUtil.toast(getResources().getString(R.string.nicknameinlegal));
                    return;
                }
                try {
                    byte[] bytes = nickname.getBytes("GBK");
                    if(bytes.length>16){
                        ToastUtil.toast(getResources().getString(R.string.long_nickname));
                        return;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (nickname != null && !TextUtils.isEmpty(nickname) && !nickname.equals(loginNickName)) {

                    RetrofitInternationalFactory.getInstence().API().resetUserInfoName(new ResetNameBody(nickname))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new MBaseObserver<Object>((Activity) getContext(),true) {
                                @Override
                                protected void onSuccees(BaseResult<Object> t) throws Exception {
                                    loginedUser.setNickname(nickname);
                                    LoginReceiveData loginReceiveData = loginedUser.getLoginData();
                                    loginReceiveData.setNickname(nickname);
                                    loginedUser.setLoginData(loginReceiveData);
                                    LoginedUser.saveToFile();
                                    disMiss();
                                }

                                @Override
                                protected void onCodeError(BaseResult<Object> t) throws Exception {

                                    ToastUtil.toast(t.getMessage());
                                }

                                @Override
                                protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                                    ToastUtil.toast(e.getMessage());
                                }
                            });

                }
            }
        });
        edit_title.setLeftListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                disMiss();
            }
        });
    }

    private void ShowsetHead(String avatar) {
        ImageUtil.EBizImageLoad(avatar, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                setNativeHeader();

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                setNativeHeader();
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (bitmap != null) {
                    CircleImageDrawable circleImageDrawable = new CircleImageDrawable(bitmap);
                    circleImageDrawable.mutate().setColorFilter(getResources().getColor(R.color.bg_translucent), PorterDuff.Mode.MULTIPLY);
                    drawTextImageView.setImageDrawable(circleImageDrawable);
                } else {
                    setNativeHeader();
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                setNativeHeader();
            }
        });
    }

    private void setNativeHeader() {
        String nickname = loginedUser.getNickname();
        String f;
        if (nickname != null && !TextUtils.isEmpty(nickname)) {//没有头像,有昵称使用昵称的首字母
            f = PinyUtil.getSpells(nickname);
            CircleDefultDrawable defultDrawable = new CircleDefultDrawable(FontImageUtil.setDefaultColor(getContext(), f), nickname.charAt(0) + "");
            defultDrawable.mutate().setColorFilter(getResources().getColor(R.color.bg_translucent), PorterDuff.Mode.MULTIPLY);
            drawTextImageView.setImageDrawable(defultDrawable);
        } else {//没有昵称使用注册手机号码的后四位
            String username = loginedUser.getUser_name();
            String cutusername = username.substring(username.length() - 4, username.length());
            f = PinyUtil.getSpells(cutusername);
            CircleDefultDrawable defultDrawable = new CircleDefultDrawable(FontImageUtil.setDefaultColor(getContext(), f), cutusername.charAt(0) + "");
            defultDrawable.mutate().setColorFilter(getResources().getColor(R.color.bg_translucent), PorterDuff.Mode.MULTIPLY);
            drawTextImageView.setImageDrawable(defultDrawable);
        }
    }

    public interface EditUserListener{
        void onClose();
    }

    public void setEditListener(EditUserListener listener){
        this.listener = listener;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        uploadHeadModel.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERSON_ACTIVITY) {
            initData();
        }
    }

    private void initAnim() {
        height = ContentUtil.getScreenHeight(getContext());
        outAnim = AnimFactory.tranYBottomOutAnim(this, height);
        outAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                EditUserView.this.setVisibility(GONE);
                KeyBoardUtil.closeKeybord(nick_name,getContext());
                listener.onClose();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                EditUserView.this.setVisibility(GONE);
                KeyBoardUtil.closeKeybord(nick_name,getContext());
                listener.onClose();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        inAnim = AnimFactory.tranYBottomInAnim(this, height);
        inAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                EditUserView.this.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                EditUserView.this.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    public void showME(){
        inAnim.start();
    }

    public void disMiss(){
        outAnim.start();
    }

    public void setCurrentHeadimg(Bitmap bitmap){
        this.currentHeadimg = bitmap;
        setUserInfo();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
