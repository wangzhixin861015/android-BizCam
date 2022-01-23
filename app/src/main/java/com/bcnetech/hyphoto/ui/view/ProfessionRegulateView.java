package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.ui.adapter.IsoAdapter;
import com.bcnetech.hyphoto.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by a1234 on 17/3/2.
 * * 白平衡
 * [auto, incandescent, fluorescent, warm-fluorescent, daylight, cloudy-daylight, twilight, shade, manual]
 * 自动 白炽灯 荧光灯 xxx 日光 阴天 xxx xxx 手动
 * 对焦
 * [auto, macro, continuous-video, continuous-picture]
 * [auto, infinity, fixed, macro, continuous-video, continuous-picture, manual]
 * ISO
 * auto,ISO_HJR,ISO100,ISO200,ISO400,ISO800,ISO1600,ISO3200
 */

public class ProfessionRegulateView extends BaseRelativeLayout {
    /* public static final int TYPE_CUSTOM =1;
     public static final int TYPE_BULB =2;
     public static final int TYPE_LAMP =3;
     public static final int TYPE_SUNSHINE =4;
     public static final int TYPE_CLOUD =5;*/
    public static final int TYPE_AUTO = 6;
    public static final int TYPE_SEC = 7;
    public static final int TYPE_ISO = 8;
    public static final int TYPE_FOCUS = 9;
    private int type;
    private RelativeLayout rl_auto;
    private LinearLayout typesec;
    private LinearLayout typewb;
    private LinearLayout typefoucs;
    private LinearLayout typeiso;
    private SeekBar seekBar;
    private TextView m_focus;
    private ImageView iv_autofocus;
    private boolean haveChangeParams = false;
    private boolean isAutoIsoOn = true;
    private boolean isAutoSecOn = true;
    private boolean isAutoFocusOn = true;
    private boolean isC2Support = false;
    //  private BottomToolItemView item;
    private BottomToolItemView item2;
    private BottomToolItemView item3;
    private BottomToolItemView item4;
    private BottomToolItemView item5;
    private BottomToolItemView item6;
    private TextView focus_auto;
    private TextView focus_macro;
    private TextView focus_infinity;
    private ProReListener listener;
    private int sbSec, sbIso, sbFocus = -1;
    private TextView tv_seekbar_start;
    private TextView tv_seekbar_end;
    private TextView tv_seekbar_middle;
    private RecyclerView recyclerView;
    private IsoAdapter isoAdapter;

    public ProfessionRegulateView(Context context) {
        super(context);
    }

    public ProfessionRegulateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProfessionRegulateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.layout_prefessor_regulate, this);
        typesec = (LinearLayout) findViewById(R.id.type_sec);
        typewb = (LinearLayout) findViewById(R.id.type_wb);
        typefoucs = (LinearLayout) findViewById(R.id.type_focus);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        iv_autofocus = (ImageView) findViewById(R.id.iv_autofocus);
        rl_auto = (RelativeLayout) findViewById(R.id.rl_autofocus);
        m_focus = (TextView) findViewById(R.id.m_focus);
        // item = (BottomToolItemView) findViewById(R.id.item);
        item2 = (BottomToolItemView) findViewById(R.id.item2);
        item3 = (BottomToolItemView) findViewById(R.id.item3);
        item4 = (BottomToolItemView) findViewById(R.id.item4);
        item5 = (BottomToolItemView) findViewById(R.id.item5);
        item6 = (BottomToolItemView) findViewById(R.id.item6);
        focus_auto = (TextView) findViewById(R.id.focus_auto);
        focus_macro = (TextView) findViewById(R.id.focus_macro);
        focus_infinity = (TextView) findViewById(R.id.focus_infinity);
        tv_seekbar_start = (TextView) findViewById(R.id.tv_seekbar_start);
        tv_seekbar_end = (TextView) findViewById(R.id.tv_seekbar_end);
        tv_seekbar_middle = (TextView) findViewById(R.id.tv_seekbar_middle);
        recyclerView = (RecyclerView) findViewById(R.id.iso_recycle);
        typeiso = (LinearLayout) findViewById(R.id.type_iso);

    }

    @Override
    protected void initData() {
        super.initData();
        ResetwbData();
        ResetFocusData();
        setSeekbar(seekBar, sbFocus);
        setSeekbar(seekBar, sbIso);
        setSeekbar(seekBar, sbSec);
        setwbNum(Flag.TYPE_AUTO);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//横向滚动的RecycleView
        recyclerView.setLayoutManager(linearLayoutManager);
        isoAdapter = new IsoAdapter(getContext(), setIsolist());
        recyclerView.setAdapter(isoAdapter);
    }

    private ArrayList<String> setIsolist() {
        ArrayList<String> isoarray = new ArrayList<>();
        isoarray.add(getResources().getString(R.string.auto));
        isoarray.add("100");
        isoarray.add("200");
        isoarray.add("400");
        isoarray.add("800");
        isoarray.add("1600");
        isoarray.add("3200");
        return isoarray;
    }

    private void setSeekbar(SeekBar seekbar, int num) {
        if (num != -1) {
            seekbar.setProgress(num);
        }
    }

    public void setIsC2Support(boolean setIsC2Support) {
        this.isC2Support = setIsC2Support;
    }

    //设置全自动
    public void setAutoType(boolean b) {
        if (b) {
            isAutoIsoOn = true;
            isAutoSecOn = true;
            isAutoFocusOn = true;
            haveChangeParams = false;
            ResetFocusData();
            ResetwbData();
            sbSec = sbIso = sbFocus = 0;
            setSeekbar(seekBar, sbFocus);
            iv_autofocus.setImageDrawable(haveChangeParams ? getResources().getDrawable(R.drawable.auto) : getResources().getDrawable(R.drawable.autoon));
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Flag.FOCUS, Flag.FOCUS_CONTINUOUS);
            hashMap.put(Flag.ISO, Flag.TYPE_AUTO);
            hashMap.put(Flag.WHITEBALANCE, Flag.TYPE_AUTO);
            setCurrentParams(hashMap);
            listener.onIsParmsChanged(false);
            listener.onFocus(Flag.FOCUS_CONTINUOUS);
            listener.onIso(Flag.TYPE_AUTO);
            listener.onWb(Flag.TYPE_AUTO);
            isoAdapter.setAutoType();
        }
    }

    public void setAutoFocus(boolean isAutoFocusOn) {
        this.isAutoFocusOn = isAutoFocusOn;
        if (isAutoFocusOn) {
            ResetFocusData();
            setFocusNum(Flag.FOCUS_CONTINUOUS);
        }
    }


    public void setType(int type) {
        switch (type) {
            case ProfessionCameraView.TYPESEC:
                typeiso.setVisibility(GONE);
                typefoucs.setVisibility(GONE);
                seekBar.setThumb(getResources().getDrawable(R.drawable.oval2));
                typewb.setVisibility(GONE);
                typesec.setVisibility(VISIBLE);
                this.type = TYPE_SEC;
                setSeekbar(seekBar, sbSec);
                tv_seekbar_start.setText("1/2");
                tv_seekbar_end.setText("1/10000");
                tv_seekbar_middle.setVisibility(GONE);
                iv_autofocus.setImageDrawable(isAutoSecOn ? getResources().getDrawable(R.drawable.autoon) : getResources().getDrawable(R.drawable.auto));
                m_focus.setTextColor(isAutoSecOn ? getResources().getColor(R.color.little_blue) : getResources().getColor(R.color.white));
                break;
            case ProfessionCameraView.TYPEISO:
                typeiso.setVisibility(VISIBLE);
                typefoucs.setVisibility(GONE);
                typewb.setVisibility(GONE);
                typesec.setVisibility(GONE);
                recyclerView.setVisibility(VISIBLE);
                this.type = TYPE_ISO;
                /*seekBar.setThumb(getResources().getDrawable(R.drawable.oval3));
                setSeekbar(seekBar, sbIso);
                tv_seekbar_start.setText("100");
                tv_seekbar_end.setText("3200");
                tv_seekbar_middle.setVisibility(GONE);
                iv_autofocus.setImageDrawable(isAutoIsoOn ? getResources().getDrawable(R.drawable.autoon) : getResources().getDrawable(R.drawable.auto));
                m_focus.setTextColor(isAutoIsoOn ? getResources().getColor(R.color.little_blue) : getResources().getColor(R.color.white));*/
                break;
            case ProfessionCameraView.TYPEFOCUS:
                typeiso.setVisibility(GONE);
                if (!isC2Support) {
                    typewb.setVisibility(GONE);
                    typefoucs.setVisibility(VISIBLE);
                    typesec.setVisibility(GONE);
                } else {
                    typefoucs.setVisibility(GONE);
                    seekBar.setThumb(getResources().getDrawable(R.drawable.oval2));
                    typewb.setVisibility(GONE);
                    typesec.setVisibility(VISIBLE);
                    this.type = TYPE_FOCUS;
                    setSeekbar(seekBar, sbFocus);
                    tv_seekbar_start.setText("-100");
                    tv_seekbar_end.setText("100");
                    tv_seekbar_middle.setVisibility(VISIBLE);
                    iv_autofocus.setImageDrawable(isAutoFocusOn ? getResources().getDrawable(R.drawable.autoon) : getResources().getDrawable(R.drawable.auto));
                    m_focus.setTextColor(isAutoFocusOn ? getResources().getColor(R.color.little_blue) : getResources().getColor(R.color.white));
                }
                break;
            case ProfessionCameraView.TYPEWB:
                typeiso.setVisibility(GONE);
                typefoucs.setVisibility(GONE);
                typewb.setVisibility(VISIBLE);
                typesec.setVisibility(GONE);
                tv_seekbar_middle.setVisibility(GONE);
                break;
        }
    }


    private void onAutoClick() {
        switch (type) {
            case TYPE_ISO:
                if (isAutoIsoOn) {
                    isAutoIsoOn = false;
                    iv_autofocus.setImageDrawable(getResources().getDrawable(R.drawable.auto));
                    m_focus.setTextColor(getResources().getColor(R.color.white));
                } else {
                    isAutoIsoOn = true;
                    iv_autofocus.setImageDrawable(getResources().getDrawable(R.drawable.autoon));
                    m_focus.setTextColor(getResources().getColor(R.color.little_blue));
                }
                if (isAutoIsoOn) {
                    String iso = Flag.TYPE_AUTO;
                    listener.onIso(iso);
                } else {
                    if (sbIso != -1) {
                        listener.onIso(String.valueOf(sbIso * 31 + 100));
                    }
                }
                break;
            case TYPE_FOCUS:
                if (isAutoFocusOn) {
                    isAutoFocusOn = false;
                    iv_autofocus.setImageDrawable(getResources().getDrawable(R.drawable.auto));
                    m_focus.setTextColor(getResources().getColor(R.color.white));
                } else {
                    isAutoFocusOn = true;
                    iv_autofocus.setImageDrawable(getResources().getDrawable(R.drawable.autoon));
                    m_focus.setTextColor(getResources().getColor(R.color.little_blue));
                }
                if (isAutoFocusOn) {
                    String focus = Flag.TYPE_AUTO;
                    listener.onFocus(focus);
                } else {
                    if (sbFocus != -1) {
                        listener.onFocus(String.valueOf(sbFocus * 0.14));
                    }
                }
                break;
            case TYPE_SEC:
                if (isAutoSecOn) {
                    isAutoSecOn = false;
                    iv_autofocus.setImageDrawable(getResources().getDrawable(R.drawable.auto));
                    m_focus.setTextColor(getResources().getColor(R.color.white));
                } else {
                    isAutoSecOn = true;
                    iv_autofocus.setImageDrawable(getResources().getDrawable(R.drawable.autoon));
                    m_focus.setTextColor(getResources().getColor(R.color.little_blue));
                }
                if (isAutoSecOn) {
                    String sec = Flag.TYPE_AUTO;
                    listener.onSec(sec);
                } else {
                    if (sbSec != -1) {
                        listener.onSec(String.valueOf(sbSec * 866975 + 13231));
                    }
                }
                break;
        }

    }

    private void ResetwbData() {
     /*   item.setImgText(R.drawable.custom_wb, getResources().getString(R.string.custom));
        item.setTextColor(getResources().getColor(R.color.white));*/
        item2.setImgText(R.drawable.bulb, getResources().getString(R.string.bulb));
        item2.setTextColor(getResources().getColor(R.color.white));
        item3.setImgText(R.drawable.lamp, getResources().getString(R.string.lamp));
        item3.setTextColor(getResources().getColor(R.color.white));
        item4.setImgText(R.drawable.sun, getResources().getString(R.string.sunshine));
        item4.setTextColor(getResources().getColor(R.color.white));
        item5.setImgText(R.drawable.cloud_wb, getResources().getString(R.string.cloud));
        item5.setTextColor(getResources().getColor(R.color.white));
        item6.setImgText(R.drawable.auto, getResources().getString(R.string.auto));
        item6.setTextColor(getResources().getColor(R.color.white));
    }

    private void ResetFocusData() {
        focus_infinity.setText(getResources().getString(R.string.infinity));
        focus_infinity.setTextColor(getResources().getColor(R.color.white));
        focus_macro.setText(getResources().getString(R.string.macro));
        focus_macro.setTextColor(getResources().getColor(R.color.white));
        focus_auto.setText(getResources().getString(R.string.auto));
        focus_auto.setTextColor(getResources().getColor(R.color.white));
    }

    public int getType() {
        return this.type;
    }

    @Override
    protected void onViewClick() {
        isoAdapter.setOnItemClickListener(new IsoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    isAutoIsoOn = true;
                    listener.onIso(Flag.TYPE_AUTO);
                } else {
                    String miso = nb2String(setIsolist().get(position));
                    listener.onIso(miso);
                    isAutoIsoOn = false;
                    haveChangeParams = true;
                    listener.onIsParmsChanged(haveChangeParams);
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                haveChangeParams = true;
                listener.onIsParmsChanged(haveChangeParams);
                switch (type) {
                    case TYPE_ISO:
                        if (sbIso != progress) {
                            //range:100--3200
                            String iso;
                            sbIso = progress;
                            if (!isC2Support) {
                                calculateSeekbar(progress);
                                // iso = nb2String(progress);
                                iso = null;
                            } else {
                                progress = progress * 31 + 100;
                                iso = String.valueOf(progress);
                            }
                            listener.onIso(iso);
                            isAutoIsoOn = false;
                            iv_autofocus.setImageDrawable(getResources().getDrawable(R.drawable.auto));
                            m_focus.setTextColor(getResources().getColor(R.color.white));
                        }
                        break;
                    case TYPE_SEC:
                        if (sbSec != progress) {
                            //range: 13231--866975130
                            sbSec = progress;
                            progress = progress * 866975 + 13231;
                            listener.onSec(String.valueOf(progress));
                            isAutoSecOn = false;
                            iv_autofocus.setImageDrawable(getResources().getDrawable(R.drawable.auto));
                            m_focus.setTextColor(getResources().getColor(R.color.white));
                        }
                        break;
                    case TYPE_FOCUS:
                        if (sbFocus != progress) {
                            //range:14
                            double focus;
                            sbFocus = progress;
                            focus = progress * 0.14;
                            listener.onFocus(String.valueOf(focus));
                            isAutoFocusOn = false;
                            iv_autofocus.setImageDrawable(getResources().getDrawable(R.drawable.auto));
                            m_focus.setTextColor(getResources().getColor(R.color.white));
                        }
                        break;
                }
                //onAutoClick();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rl_auto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onAutoClick();
            }
        });

      /*  item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetwbData();
                item.setImgText(R.drawable.customon, getResources().getString(R.string.custom));
                item.setTextColor(getResources().getColor(R.color.little_blue));
                listener.onWb(Flag.TYPE_MANUAL);
            }
        });*/
        item2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetwbData();
                haveChangeParams = true;
                listener.onIsParmsChanged(haveChangeParams);
                item2.setImgText(R.drawable.bulbon, getResources().getString(R.string.bulb));
                item2.setTextColor(getResources().getColor(R.color.little_blue));
                listener.onWb(Flag.TYPE_INCANDESCENT);
            }
        });
        item3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetwbData();
                haveChangeParams = true;
                listener.onIsParmsChanged(haveChangeParams);
                item3.setImgText(R.drawable.lampon, getResources().getString(R.string.lamp));
                item3.setTextColor(getResources().getColor(R.color.little_blue));
                listener.onWb(Flag.TYPE_FLUORESCENT);
            }
        });
        item4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetwbData();
                haveChangeParams = true;
                listener.onIsParmsChanged(haveChangeParams);
                item4.setImgText(R.drawable.sunon, getResources().getString(R.string.sunshine));
                item4.setTextColor(getResources().getColor(R.color.little_blue));
                listener.onWb(Flag.TYPE_DAYLIGHT);
            }
        });
        item5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetwbData();
                haveChangeParams = true;
                listener.onIsParmsChanged(haveChangeParams);
                item5.setImgText(R.drawable.cloudon, getResources().getString(R.string.cloud));
                item5.setTextColor(getResources().getColor(R.color.little_blue));
                listener.onWb(Flag.TYPE_CLOUD);
            }
        });
        item6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetwbData();
                item6.setImgText(R.drawable.autoon, getResources().getString(R.string.auto));
                item6.setTextColor(getResources().getColor(R.color.little_blue));
                listener.onWb(Flag.TYPE_AUTO);
            }
        });

        focus_auto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetFocusData();
                setFocusNum(Flag.FOCUS_CONTINUOUS);
                listener.onFocus(Flag.FOCUS_CONTINUOUS);
            }
        });
        focus_infinity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetFocusData();
                haveChangeParams = true;
                listener.onIsParmsChanged(haveChangeParams);
                setFocusNum(Flag.FOCUS_INFINITY);
                listener.onFocus(Flag.FOCUS_INFINITY);
            }
        });
        focus_macro.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetFocusData();
                haveChangeParams = true;
                listener.onIsParmsChanged(haveChangeParams);
                setFocusNum(Flag.FOCUS_MACRO);
                // listener.onFocus(Flag.FOCUS_CONTINUOUS);
                listener.onFocus(Flag.FOCUS_MACRO);
            }
        });
    }

    private void calculateSeekbar(int process) {
        if (process > 0 && process <= 20) {
            seekBar.setProgress(20);
        } else if (process > 20 && process <= 40) {
            seekBar.setProgress(40);
        } else if (process > 40 && process <= 60) {
            seekBar.setProgress(60);
        } else if (process > 60 && process <= 80) {
            seekBar.setProgress(80);
        } else if (process > 80 && process <= 100) {
            seekBar.setProgress(100);
        }
    }

    private String nb2String(String num) {
        switch (num) {
            case "100":
                num = Flag.ISO100;
                break;
            case "200":
                num = Flag.ISO200;
                break;
            case "400":
                num = Flag.ISO400;
                break;
            case "800":
                num = Flag.ISO800;
                break;
            case "1600":
                num = Flag.ISO1600;
                break;
            case "3200":
                num = Flag.ISO3200;
                break;
        }
        return num;
    }

    private int String2nb(String num) {
        int iso = 0;
        switch (num) {
            case Flag.TYPE_AUTO:
                isAutoIsoOn = true;
                break;
            case Flag.ISO100:
                iso = 0;
                break;
            case Flag.ISO200:
                iso = 20;
                break;
            case Flag.ISO400:
                iso = 40;
                break;
            case Flag.ISO800:
                iso = 60;
                break;
            case Flag.ISO1600:
                iso = 80;
                break;
            case Flag.ISO3200:
                iso = 100;
                break;
        }
        return iso;
    }


    public void setCurrentParams(HashMap<String, String> paramsMap) {
        if (paramsMap != null) {
            String iso = paramsMap.get(Flag.ISO);
            if (iso != null && !TextUtils.isEmpty(iso)) {
                setIsonum(iso);
                Log.d("params_iso", iso);
            }
            String wb = paramsMap.get(Flag.WHITEBALANCE);
            if (wb != null && !TextUtils.isEmpty(wb)) {
                ResetwbData();
                setwbNum(wb);
                Log.d("params_wb", wb);
            }
            String focus = paramsMap.get(Flag.FOCUS);
            if (focus != null && !TextUtils.isEmpty(focus)) {
                ResetFocusData();
                setFocusNum(focus);
                Log.d("params_focus", focus);
            }
        }
    }

    private void setwbNum(String wb) {
        switch (wb) {
            case Flag.TYPE_AUTO:
                ResetwbData();
                item6.setImgText(R.drawable.autoon, getResources().getString(R.string.auto));
                item6.setTextColor(getResources().getColor(R.color.little_blue));
                break;
            case Flag.TYPE_CLOUD:
                ResetwbData();
                item5.setImgText(R.drawable.cloudon, getResources().getString(R.string.cloud));
                item5.setTextColor(getResources().getColor(R.color.little_blue));
                break;
            case Flag.TYPE_FLUORESCENT:
                ResetwbData();
                item3.setImgText(R.drawable.lampon, getResources().getString(R.string.lamp));
                item3.setTextColor(getResources().getColor(R.color.little_blue));
                break;
            case Flag.TYPE_INCANDESCENT:
                ResetwbData();
                item2.setImgText(R.drawable.bulbon, getResources().getString(R.string.bulb));
                item2.setTextColor(getResources().getColor(R.color.little_blue));
                break;
            case Flag.TYPE_DAYLIGHT:
                ResetwbData();
                item4.setImgText(R.drawable.sunon, getResources().getString(R.string.sunshine));
                item4.setTextColor(getResources().getColor(R.color.little_blue));
                break;
           /* case Flag.TYPE_MANUAL:
                ResetwbData();
                item.setImgText(R.drawable.customon, getResources().getString(R.string.custom));
                item.setTextColor(getResources().getColor(R.color.little_blue));
                break;*/
        }
    }


    private void setIsonum(String iso) {
        int isonum = String2nb(iso);
        sbIso = isonum;
    }

    private void setFocusNum(String focus) {
        switch (focus) {
            case Flag.FOCUS_CONTINUOUS:
                focus_auto.setTextColor(getResources().getColor(R.color.little_blue));
                break;
            case Flag.FOCUS_INFINITY:
                focus_infinity.setTextColor(getResources().getColor(R.color.little_blue));
                break;
            case Flag.FOCUS_MACRO:
                focus_macro.setTextColor(getResources().getColor(R.color.little_blue));
                break;
        }
    }

    public interface ProReListener {
        void onIso(String type);

        void onWb(String type);

        void onFocus(String type);

        void onSec(String sec);

        void onIsParmsChanged(boolean isAutoFocus);

    }

    public void setProReListener(ProReListener proReListener) {
        this.listener = proReListener;
    }
}
