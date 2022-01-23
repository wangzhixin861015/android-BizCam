package com.bcnetech.hyphoto.presenter.iview;

import android.view.TextureView;

/**
 * Created by wenbin on 2017/3/20.
 */

public interface IRecoderView extends BaseIView {
    TextureView getTextureView();

    void setSeeBar(int number);

    void setSeeBarMax(int number);

    void setVideoStatus(boolean videoStatus);

    void showToast(String toast);

    int getRotateCount();

}
