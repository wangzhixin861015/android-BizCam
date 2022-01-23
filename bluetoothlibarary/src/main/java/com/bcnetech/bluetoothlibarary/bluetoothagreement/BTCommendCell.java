package com.bcnetech.bluetoothlibarary.bluetoothagreement;

/**
 * Created by wenbin on 2016/12/29.
 */

public interface BTCommendCell {

    /**
     * 版本号
     *
     * @param version
     */
    void getVersion(int version);

    /**
     * 左灯
     *
     * @param number
     */
    void getLeftLight(int number);

    /**
     * 右灯
     *
     * @param number
     */
    void getRightLight(int number);

    /**
     * 地灯
     *
     * @param number
     */
    void getBottomLight(int number);

    /**
     * 背灯
     *
     * @param number
     */
    void getBackgroundLight(int number);

    /**
     * 移动灯1
     *
     * @param number
     */
    void getMove1Light(int number);

    /**
     * 移动灯2
     *
     * @param number
     */
    void getMove2Light(int number);

    /**
     * 获得速度
     *
     * @param number
     */
    void getMotor(int number);

    /**
     * 获得所有灯参数
     *
     * @param number
     */
    void getParmLight(int... number);

    /**
     * 获得个模块在线状态
     *
     * @param number
     */
    void getOnlineStatus(int... number);

    /**
     * 发送所有灯数据
     *
     * @param number
     */
    void CmdSendValue(int... number);

    /**
     * 发送所有灯在线状态
     *
     * @param number
     */
    void CmdSendOnlineStatus(int... number);

    /**
     * 获取转盘状态
     *
     * @param number
     */
    void getMotorStatus(int... number);

    /**
     * 设置转盘开启或关闭
     *
     * @param number
     */
    void setMotor(int... number);


}
