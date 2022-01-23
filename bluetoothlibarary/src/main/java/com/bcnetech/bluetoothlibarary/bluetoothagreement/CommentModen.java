package com.bcnetech.bluetoothlibarary.bluetoothagreement;

import com.bcnetech.bluetoothlibarary.data.CommendItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by wenbin on 2016/12/29.
 */

public abstract class CommentModen {

    /**
     * 获得版本信息
     *
     * @return
     */
    public abstract byte[] getCurrentVersion();


    /***
     * 读取左灯数据
     * @return
     */
    public abstract byte[] getReadLeftLand();

    /***
     * 读取右灯数据
     * @return
     */
    public abstract byte[] getReadRightLand();

    /***
     * 读取地灯灯数据
     * @return
     */
    public abstract byte[] getReadBottomLand();

    /***
     * 读取背灯数据
     * @return
     */
    public abstract byte[] getReadBackGroundLand();

    /***
     * 读取补光灯数据
     * @return
     */
    public abstract byte[] getReadContentLand();

    /**
     * 读取所有灯光数据
     *
     * @return
     */
    public abstract byte[] getParamsLand();

    /***
     * 读取移动灯数据
     * @return
     */
    public abstract byte[] getReadMoveLand();

    /**
     * 读取移动灯2数据
     *
     * @return
     */
    public abstract byte[] getReadMove2Land();

    /**
     * 获取转盘数据
     *
     * @return
     */
    public abstract byte[] getReadMotorLand();

    /**
     * @param text
     * @return
     */
    public abstract byte[] getWriteBlueTouchNameLand(String text);


    /***
     * 输入左灯数据
     * @return
     */
    public abstract byte[] getWriteLeftLand(int number);

    /***
     * 输入右灯数据
     * @return
     */
    public abstract byte[] getWriteRightLand(int number);

    /***
     * 输入底灯数据
     * @return
     */
    public abstract byte[] getWriteBottomLand(int number);

    /***
     * 输入背灯数据
     * @return
     */
    public abstract byte[] getWriteBackGroundLand(int number);

    /***
     * 输入移动灯数据
     * @return
     */
    public abstract byte[] getWriteMoveLand(int number);


    /***
     * 输入移动灯数据
     * @return
     */
    public abstract byte[] getWriteMove2Land(int number);

    /**
     * 设置所有灯光数据
     */
    public abstract byte[] getWriteParam(int... parms);

    /**
     * 获取转盘状态
     */
    public abstract byte[] getMotorStatus(int... number);

    /**
     * 设置转盘开启或关闭
     */
    public abstract byte[] setMotor(int... number);


    public abstract List<CommendItem> processingResultData(byte[] cmds);


    /**
     * 协议1。1解析
     *
     * @param is
     * @return
     * @throws IOException
     */
    public abstract CommendItem getStringByProtocol(InputStream is) throws Exception;

}
