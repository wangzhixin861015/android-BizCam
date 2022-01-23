package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.utils.FontImageUtil;
import com.bcnetech.hyphoto.utils.KeyBoardUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by a1234 on 16/9/26.
 */

public class ImageShareParmsEditView extends BaseRelativeLayout {
    private EditText et_addtag;
    private EditText et_nameparms;
    private EditText et_textarea;
    private ImageView share_img;
    private TextView tv_saveShare;
    private TextView tv_save;
    private XCFlowLayout mFlowLayout;
    private RelativeLayout rl_addtag;
    private String newTag;
    private String Tags;
    private String ParmNames;
    //    private ImageView iv_replaceAddtag;
    private List<String> TagList;
    private List<String> SystemTag;//系统提供的tag
    private List<XCFlowLayout.SystemTag> getSysTag;//点击系统tag
    private ImgShaParEdiVListener imgShaParEdiVListener;
    private ShareParmListener shareParmListener;
    private ScrollView sv;
    private int iy;
    private boolean haveName = false;
    public static final int EDIT_NAME = 1;
    public static final int EDIT_TAG = 2;
    public static final int EDIT_TEXTAREA = 3;
    private boolean isshowbg = false;
    private Handler handler;
    private String editingTag;
    public static final int SYSTEM_TAG_COUNT = 4;
    private TitleView titleView;
    private SwitchButton switchButton;

    private int type = 2;

    public ImageShareParmsEditView(Context context) {
        super(context);
    }

    public ImageShareParmsEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageShareParmsEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        inflate(getContext(), R.layout.image_share_parms_edit, this);
        mFlowLayout = (XCFlowLayout) findViewById(R.id.flowlayout);
        et_addtag = (EditText) findViewById(R.id.et_addtag);
        et_textarea = (EditText) findViewById(R.id.et_text);
        et_nameparms = (EditText) findViewById(R.id.et_name);
        rl_addtag = (RelativeLayout) findViewById(R.id.rl_add);
        share_img = (ImageView) findViewById(R.id.share_img);
        sv = (ScrollView) findViewById(R.id.sv);
        mFlowLayout = (XCFlowLayout) findViewById(R.id.flowlayout);
        tv_saveShare = (TextView) findViewById(R.id.tv_saveShare);
        tv_save = (TextView) findViewById(R.id.tv_save);
        titleView = (TitleView) findViewById(R.id.parms_title);
        switchButton = (SwitchButton) findViewById(R.id.switchButton);
//        iv_replaceAddtag = (ImageView) findViewById(R.id.iv_replaceAddtag);
    }

    public void setImag(String url) {
        if (isshowbg) {
            share_img.setBackground(getResources().getDrawable(R.drawable.bgbitmap));
        }
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, bitmap.getHeight());
//       // params.addRule(CENTER_IN_PARENT);
//        params.addRule(CENTER_HORIZONTAL);
//        params.addRule(CENTER_VERTICAL);
//        share_img.setLayoutParams(params);
        //share_img.setImageBitmap(BitmapUtils.getImageCompress(url));
        Picasso.get().load(url).into(share_img);
    }

    public void showbackground(boolean isshow) {
        if (isshow) {
            isshowbg = true;
        }
    }

    public void mgetHeight(int height) {
        this.iy = height;
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();
        titleView.setType(TitleView.IMAGE_BG_REPAIR);
        titleView.setLeftListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh(0);
            }
        });

        titleView.setRightListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isHaveName()) {
                    ToastUtil.toast(getResources().getString(R.string.edit_parm_name));
                    return;
                }
                if (!isAddTaghave()) {
                    ToastUtil.toast(getResources().getString(R.string.add_tag_please));
                    return;
                }
                if (null != switchButton && switchButton.isChecked()) {
                    imageShareInterface.upload(2);
                } else {
                    imageShareInterface.upload(1);
                }

            }
        });

    }

    @Override
    protected void initData() {
        handler = new Handler();
        RandomSystemTag();
        initSystemTag();
        mFlowLayout.getChild(SystemTag);
        ClickSystemTags();
        et_addtag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = et_addtag.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > et_addtag.getWidth()
                        - et_addtag.getPaddingRight()
                        - drawable.getIntrinsicWidth()) {
                    if (et_addtag.getText() != null) {
                        newTag = et_addtag.getText().toString();
                        if (FontImageUtil.ishaveCharacter(newTag)) {
                            ToastUtil.toast(getResources().getString(R.string.only_CE));
                        } else {
                            //中文字数不超过50个，英文或数字不超过100个字母或数字
                            boolean isChineseChar = FontImageUtil.isChineseChar(newTag);
                            boolean charSizeOk = true;
                            if (isChineseChar) {
                                if (newTag.length() > 50) {
                                    charSizeOk = false;
                                }
                            } else {
                                if (newTag.length() > 100) {
                                    charSizeOk = false;
                                }
                            }
                            if (!charSizeOk) {
                                ToastUtil.toast(getResources().getString(R.string.letter_limit));
                            } else {
                                boolean isCanAdd = true;
                                if (TagList == null) {
                                    TagList = new ArrayList<String>();
                                }
                                for (int i = 0; i < TagList.size(); i++) {
                                    if (TagList.get(i).toString().equals(newTag)) {
                                        ToastUtil.toast(getResources().getString(R.string.same_tag));
                                        isCanAdd = false;
                                    }
                                }
                                if (SystemTag != null) {
                                    for (int i = 0; i < SystemTag.size(); i++) {
                                        if (newTag.equals(SystemTag.get(i))) {
                                            ToastUtil.toast(getResources().getString(R.string.same_tag));
                                            isCanAdd = false;
                                        }
                                    }
                                }
                                if (!TextUtils.isEmpty(newTag) && isCanAdd) {
                                    TagList.add(newTag);
                                    initChildViews();
                                    mFlowLayout.refreshDrawableState();
                                    et_addtag.setText("");
                                }
                            }
                        }
                    } else {
                        ToastUtil.toast(getResources().getString(R.string.empty_tag));
                    }

                }
                //输入tag后
                if (imgShaParEdiVListener != null) {
                    imgShaParEdiVListener.onParmsTag();
                }
                return false;
            }
        });

//        et_addtag.setOnFocusChangeListener(new OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if(hasFocus){
//
//                }else {
//                    if(!StringUtil.isBlank(et_addtag.getText().toString())){
//                        addTagFin();
//                    }
//                }
//            }
//        });
        et_addtag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Tags = et_addtag.getText().toString();
                editingTag = s.toString();
                if (s.toString().contains(" ")) {
                    String[] str = s.toString().split(" ");
                    String str1 = "";
                    for (int i = 0; i < str.length; i++) {
                        str1 += str[i];
                        editingTag = str1;
                        et_addtag.setText(str1);
                        et_addtag.setSelection(start);
                    }
                }
                //输入tag时
                if (imgShaParEdiVListener != null) {
                    imgShaParEdiVListener.onParmsTag();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_nameparms.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_nameparms != null && s != null) {
                    if (FontImageUtil.ishaveCharacter(et_nameparms.getText().toString())) {
                        ToastUtil.toast(getResources().getString(R.string.only_CE));
                        return;
                    }
                    if (et_nameparms.getText().toString().length() > 6) {
                        ToastUtil.toast(getResources().getString(R.string.long_name));
                        return;
                    }
                    ParmNames = et_nameparms.getText().toString();

                    if (imgShaParEdiVListener != null) {
                        imgShaParEdiVListener.onParmsName(ParmNames);
                    }
                    //输入清除完毕后,返回初始状态
                }
                if (!TextUtils.isEmpty(et_nameparms.getText().toString())) {
                    haveName = true;
                } else {
                    haveName = false;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tv_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 1;
                if (!isHaveName() || !isAddTaghave()) {
                    tv_saveShare.setBackgroundResource(R.drawable.gray_background_share_right);
                    tv_save.setBackgroundResource(R.drawable.blue_background_share_left);
                    return;
                }
                tv_saveShare.setBackgroundResource(R.drawable.gray_background_share_right);
                tv_save.setBackgroundResource(R.drawable.blue_background_share_left);
                shareParmListener.share(type);

            }
        });

        tv_saveShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 2;
                if (!isHaveName() || !isAddTaghave()) {
                    tv_saveShare.setBackgroundResource(R.drawable.blue_background_share_right);
                    tv_save.setBackgroundResource(R.drawable.gray_background_share_left);
                    return;
                }
                tv_saveShare.setBackgroundResource(R.drawable.blue_background_share_right);
                tv_save.setBackgroundResource(R.drawable.gray_background_share_left);
                shareParmListener.share(type);

            }
        });

        share_img.setLayoutParams(new LinearLayout.LayoutParams(ContentUtil.getScreenWidth(getContext()), ContentUtil.getScreenWidth(getContext())));
    }

    private void ClickSystemTags() {
        mFlowLayout.setOnFlowLayoutListener(new XCFlowLayout.FlowLayoutListener() {
            @Override
            public void onClick(List<XCFlowLayout.SystemTag> selectList) {
                if (getSysTag == null) {
                    LinkedHashSet<String> set = new LinkedHashSet<String>();
                    getSysTag = new ArrayList<XCFlowLayout.SystemTag>();
                }
                getSysTag = selectList;
                //点击系统tag
                if (imgShaParEdiVListener != null) {
                    imgShaParEdiVListener.onParmsTag();
                }
            }
        });
    }

    public boolean isHaveName() {
        return this.haveName;
    }

    /**
     * 添加标签结束
     */
    public void addTagFin() {
        List a = getSysTag;
        List b = SystemTag;
//        mFlowLayout.deleteAllView();

        List<String> mlist = getTags();
        et_addtag.setText("");
        editingTag = "";
        for (int i = 0; i < mlist.size(); i++) {
            String systemtag = mlist.get(i);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = 10;
            lp.rightMargin = 10;
            lp.topMargin = 14;
            lp.bottomMargin = 14;
            TextView view = new TextView(getContext());
            view.setText(systemtag);
            view.setTextColor(Color.WHITE);
            view.setTextSize(12);
            int x = ContentUtil.dip2px(getContext(), 14);
            int y = ContentUtil.dip2px(getContext(), 6);
            view.setPadding(x, y, x, y);
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.recttangle_blue));
            mFlowLayout.addView(view, lp);
//            invisibleRightImage();
            if (getSysTag != null) {
                for (int j = 0; j < getSysTag.size(); j++) {
                    getSysTag.get(j).setSelect(false);
                }
            }
            TagList = mlist;
        }

    }

    /**
     * 隐藏编辑tag
     */
    private void invisibleRightImage() {
        et_addtag.setVisibility(GONE);
//        iv_replaceAddtag.setVisibility(VISIBLE);

    }

    /**
     * 显示编辑tag
     */
    private void ShowRightImage() {
        et_addtag.setVisibility(VISIBLE);
//        iv_replaceAddtag.setVisibility(GONE);

    }


    /*
     * 初始化系统tag
     */
    public void initSystemTag() {
        for (int i = 0; i < SYSTEM_TAG_COUNT; i++) {
            String systemtag = SystemTag.get(i);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = 10;
            lp.rightMargin = 10;
            lp.topMargin = 14;
            lp.bottomMargin = 14;
            final TextView view = new TextView(getContext());
            view.setText(systemtag);
            view.setTextColor(Color.WHITE);
            view.setTextSize(12);
            int x = ContentUtil.dip2px(getContext(), 14);
            int y = ContentUtil.dip2px(getContext(), 6);
            view.setPadding(x, y, x, y);
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.recttangle_black_lv2));
            mFlowLayout.addView(view, lp);
        }
    }

    public List<String> getNewList(List<String> li) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < li.size(); i++) {
            String str = li.get(i);  //获取传入集合对象的每一个元素
            if (!list.contains(str)) {   //查看新集合中是否有指定的元素，如果没有则加入
                list.add(str);
            }
        }
        return list;  //返回集合
    }

    private boolean isEditedTag = false;

    /**
     * 添加自定义tag
     */
    private void initChildViews() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 10;
        lp.rightMargin = 10;
        lp.topMargin = 14;
        lp.bottomMargin = 14;
        TextView view = new TextView(getContext());
        if (TagList != null) {
            TagList = getNewList(TagList);
            String tagname = TagList.get(TagList.size() - 1);
            view.setText(tagname);
            view.setTextColor(Color.WHITE);
            view.setTextSize(12);
        }
        int x = ContentUtil.dip2px(getContext(), 14);
        int y = ContentUtil.dip2px(getContext(), 6);
        view.setPadding(x, y, x, y);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.recttangle_blue));
        mFlowLayout.addView(view, lp);
        if (view != null) {
            isEditedTag = true;
        }
    }

    private void clickSystemTag() {

    }

    public interface ShareParmListener {
        void share(int type);
    }

    //回调
    public void setIShareParmListener(ShareParmListener shareParmListener) {
        this.shareParmListener = shareParmListener;
    }


    public interface ImgShaParEdiVListener {
        void onParmsName(String name);

        void onParmsTag();

    }

    //回调
    public void setImgShaParEdiVListener(ImgShaParEdiVListener imgShaParEdiVListener) {
        this.imgShaParEdiVListener = imgShaParEdiVListener;
    }


    public void ShowAddTag() {
        rl_addtag.setVisibility(VISIBLE);
        handler.post(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    /**
     * 重新展示tag
     */
    public void ReShowAddTag() {
        ShowRightImage();
        mFlowLayout.deleteAllView();
        initSystemTag();
        mFlowLayout.getChild(SystemTag);
        clearTag();
        isEditedTag = false;
//        rl_addtag.setVisibility(VISIBLE);
        et_addtag.setHint(getResources().getString(R.string.add_tag_please));
        if (getSysTag != null) {
            for (int i = 0; i < getSysTag.size(); i++) {
                getSysTag.get(i).setSelect(false);
            }
        }
    }

    public void InvisibleAddTag() {
        et_addtag.setText("");
        mFlowLayout.deleteAllView();
        initSystemTag();
        mFlowLayout.getChild(SystemTag);
//        rl_addtag.setVisibility(GONE);
        ImageShareParmsEditView.this.invalidate();
    }

    public boolean isShowAddTag() {
        if (et_addtag.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    public void getkeyboard(int type) {
        InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (type) {
            case EDIT_NAME:
                setFocus(et_addtag, false);
                setFocus(et_nameparms, true);
                setFocus(et_textarea, false);
                imm.showSoftInput(et_nameparms, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                break;
            case EDIT_TAG:
                setFocus(et_addtag, true);
                setFocus(et_nameparms, false);
                setFocus(et_textarea, false);
                imm.showSoftInput(et_addtag, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                break;
            case EDIT_TEXTAREA:
                setFocus(et_addtag, false);
                setFocus(et_nameparms, false);
                setFocus(et_textarea, true);
                imm.showSoftInput(et_textarea, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                break;
        }
    }

    private void setFocus(EditText editText, boolean b) {
//        editText.setFocusableInTouchMode(b);
//        editText.setFocusableInTouchMode(b);
        if (b) {
            editText.requestFocus();
        } else {
            editText.clearFocus();
        }
    }

    /**
     * 判断是否显示添加标签界面
     *
     * @return
     */
    public boolean isAddTagShow() {
        int status = rl_addtag.getVisibility();
        if (status == VISIBLE) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否有Tag
     *
     * @return
     */
    public boolean isAddTaghave() {
        //edittext中有标签;已经添加过自定义标签;已经选择过系统标签
        boolean haveeditTag = false;
        boolean havecustomTag = false;
        boolean choiceSysTag = false;
        if (!TextUtils.isEmpty(editingTag)) {
            haveeditTag = true;
        }
        if (TagList != null) {
            if (TagList.size() != 0) {
                havecustomTag = true;
            }
        }
        if (getSysTag != null) {
            for (int i = 0; i < getSysTag.size(); i++) {
                if (getSysTag.get(i).isSelect()) {
                    choiceSysTag = true;
                }
            }
        }
        if (isEditedTag) {
            et_addtag.setHint(getResources().getString(R.string.go_on_add_tag));
        } else {
            if (choiceSysTag) {
                et_addtag.setHint(getResources().getString(R.string.go_on_add_tag));
            } else {
                et_addtag.setHint(getResources().getString(R.string.add_tag_please));
            }
        }

        if (havecustomTag || choiceSysTag || haveeditTag) {
            return true;
        }
        return false;
    }

    public void clearTag() {
        if (TagList != null) {
            TagList.clear();
        }
        if (getSysTag != null) {
            for (int i = 0; i < getSysTag.size(); i++) {
                getSysTag.get(i).setSelect(false);
            }
        }
        Tags = "";
    }

    public int getType() {
        return type;
    }

    /**
     * textarea
     */
    public void ShowTextArea() {
        et_textarea.setVisibility(VISIBLE);

    }


    public void InvisibleIntro() {
        et_textarea.setText("");
        et_textarea.setVisibility(GONE);
        ImageShareParmsEditView.this.invalidate();
    }

    /**
     * 判断输入与是否显示
     */
    public boolean isTextAreaShow() {
        int status = et_textarea.getVisibility();
        Log.d("text", status + "  " + et_textarea.isShown());
        if (status == VISIBLE) {
            return true;
        }
        return false;
    }

    /**
     * 获取名称
     *
     * @return
     */
    public String getName() {
        if (et_nameparms.getText().toString() != null) {
            return et_nameparms.getText().toString();
        }
        return null;
    }


    /**
     * 获取tag
     *
     * @return
     */
    public List<String> getTags() {
        List<String> newList = new ArrayList<String>();
        if (TagList != null) {
            newList.addAll(TagList);
        }
        if (getSysTag != null) {
            for (int i = 0; i < getSysTag.size(); i++) {
                if (getSysTag.get(i).isSelect()) {
                    newList.add(getSysTag.get(i).getText());
                }
            }

        }
        if (editingTag != null) {
            if (!TextUtils.isEmpty(editingTag)) {
                newList.add(editingTag);
            }
        }
        newList = trimArray(newList);
        return newList;
    }

    public ArrayList<String> trimArray(List list) {
        Iterator it = list.iterator();
        ArrayList<String> newColl = new ArrayList();
        while (it.hasNext()) {
            Object obj = it.next();
            if (!newColl.contains(obj)) {
                newColl.add((String) obj);
            }
        }
        return newColl;
    }

    public void RandomSystemTag() {
        SystemTag = new ArrayList<>();
        Random rd = new Random();
        HashSet<Integer> hs = new HashSet();
        String[] TagPool = {getResources().getString(R.string.tag1), getResources().getString(R.string.tag2), getResources().getString(R.string.tag3), getResources().getString(R.string.tag4),
                getResources().getString(R.string.tag5), getResources().getString(R.string.tag6), getResources().getString(R.string.tag7), getResources().getString(R.string.tag8),
                getResources().getString(R.string.tag9), getResources().getString(R.string.tag10), getResources().getString(R.string.tag11), getResources().getString(R.string.tag12),
                getResources().getString(R.string.tag13), getResources().getString(R.string.tag14), getResources().getString(R.string.tag1), getResources().getString(R.string.tag15),
                getResources().getString(R.string.tag16), getResources().getString(R.string.tag17), getResources().getString(R.string.tag18), getResources().getString(R.string.tag19),
                getResources().getString(R.string.tag20), getResources().getString(R.string.tag21), getResources().getString(R.string.tag22), getResources().getString(R.string.tag23),
                getResources().getString(R.string.tag24), getResources().getString(R.string.tag25), getResources().getString(R.string.tag26)};
        while (hs.size() < 4) {
            int i = rd.nextInt(TagPool.length);
            hs.add(i);
        }
        for (Integer it : hs) {
            SystemTag.add(TagPool[it]);
        }
    }

    /**
     * 刷新页面
     */
    public void refresh(int type) {
        setVisibility(GONE);
        KeyBoardUtil.closeKeybord(et_addtag, getContext());
        KeyBoardUtil.closeKeybord(et_nameparms, getContext());
        imageShareInterface.close(type);
        sv.fullScroll(ScrollView.FOCUS_UP);
        ReShowAddTag();
        switchButton.setChecked(false);
        et_nameparms.setText("");
        et_addtag.setText("");
    }

    /**
     * 获取描述
     *
     * @return
     */
    public String getTextArea() {
        if (et_textarea.getText().toString() != null) {
            return et_textarea.getText().toString();
        }
        return null;
    }

    private ImageShareInterface imageShareInterface;

    public ImageShareInterface getImageShareInterface() {
        return imageShareInterface;
    }

    public void setImageShareInterface(ImageShareInterface imageShareInterface) {
        this.imageShareInterface = imageShareInterface;
    }

    public interface ImageShareInterface {
        void close(int type);

        void upload(int type);
    }
}
