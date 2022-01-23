package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.databinding.CamerasettingItemLayoutBinding;
import com.bcnetech.hyphoto.R;

/**
 * Created by a1234 on 16/8/29.
 */

public class CameraSettingItemView extends BaseRelativeLayout {
    CamerasettingItemLayoutBinding camerasettingItemLayoutBinding;
    private CurrentChecked currentCheckedListener;


    public CameraSettingItemView(Context context) {
        super(context);
        initView();
        onViewClick();
    }

    public CameraSettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        onViewClick();
    }

    public CameraSettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        onViewClick();
    }


    protected void initView() {
        camerasettingItemLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.camerasetting_item_layout, this, true);
    }

    protected void onViewClick() {
        camerasettingItemLayoutBinding.switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                currentCheckedListener.onCurrentCheck(isChecked);
            }
        });
    }

    public void setCurrentCheckedListener(CurrentChecked currentCheckedListener) {
        this.currentCheckedListener = currentCheckedListener;
    }

    public void setSwitchButton(boolean b) {
        camerasettingItemLayoutBinding.switchButton.setChecked(b);
    }


    public void setSetting_name(String name) {
        camerasettingItemLayoutBinding.settingName.setText(name);
    }

    public String getSetting_name() {
        return camerasettingItemLayoutBinding.settingName.getText().toString();
    }

    public void setSettingInstructions(String settingInstructions) {
        camerasettingItemLayoutBinding.settingInstructions.setText(settingInstructions);
        camerasettingItemLayoutBinding.settingInstructions.setVisibility(VISIBLE);
    }

    public void setSetting_parm(String parm) {
        camerasettingItemLayoutBinding.settingParm.setText(parm);
    }

    public void isShowButton(boolean isShow) {
        if (isShow) {
            camerasettingItemLayoutBinding.switchButton.setVisibility(VISIBLE);
            camerasettingItemLayoutBinding.settingRight.setVisibility(GONE);
            camerasettingItemLayoutBinding.settingParm.setVisibility(GONE);
        } else {
            camerasettingItemLayoutBinding.switchButton.setVisibility(GONE);
            camerasettingItemLayoutBinding.settingRight.setVisibility(VISIBLE);
            camerasettingItemLayoutBinding.settingParm.setVisibility(VISIBLE);
        }
    }

    public interface CurrentChecked {
        void onCurrentCheck(boolean currentcheck);
    }
}