package com.bcnetech.hyphoto.ui.view.interfaceListener;

/**
 * Created by wenbin on 2017/3/6.
 */

public interface RecoderBtnViewInterface {

    void cencelAnim();

    void startArcAnim(long timeType);
    void setRecoderInterface(RecoderInterface recoderInterface);

    interface RecoderInterface{
        void startRecoder();
        void endRecoder();

    }

}
