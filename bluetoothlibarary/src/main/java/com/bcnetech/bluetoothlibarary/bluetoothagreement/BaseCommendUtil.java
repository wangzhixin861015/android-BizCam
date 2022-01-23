package com.bcnetech.bluetoothlibarary.bluetoothagreement;

import com.bcnetech.bluetoothlibarary.data.CommendItem;

import java.io.InputStream;
import java.util.List;

/**
 * Created by wenbin on 2016/12/29.
 */

public  class BaseCommendUtil extends CommentModen{

    protected final static byte VERSION=(byte) 0x01;

    /**
     * 命令回调函数
     */
    protected BTCommendCell btCommendCell;


    @Override
    /**
     * 获得版本号
     * @return
     */
    public byte[] getCurrentVersion(){
        return  new byte[0];
    }

    @Override
    public byte[] getReadLeftLand() {
        return new byte[0];
    }

    @Override
    public byte[] getReadRightLand() {
        return new byte[0];
    }

    @Override
    public byte[] getReadBottomLand() {
        return new byte[0];
    }

    @Override
    public byte[] getReadBackGroundLand() {
        return new byte[0];
    }

    @Override
    public byte[] getReadContentLand() {
        return new byte[0];
    }

    @Override
    public byte[] getParamsLand() {
        return new byte[0];
    }

    @Override
    public byte[] getReadMoveLand() {
        return new byte[0];
    }

    @Override
    public byte[] getReadMove2Land() {
        return new byte[0];
    }

    @Override
    public byte[] getReadMotorLand() {
        return new byte[0];
    }

    @Override
    public byte[] getWriteLeftLand(int number) {
        return new byte[0];
    }

    @Override
    public byte[] getWriteRightLand(int number) {
        return new byte[0];
    }

    @Override
    public byte[] getWriteBottomLand(int number) {
        return new byte[0];
    }

    @Override
    public byte[] getWriteBackGroundLand(int number) {
        return new byte[0];
    }

    @Override
    public byte[] getWriteMoveLand(int number) {
        return new byte[0];
    }

    @Override
    public byte[] getWriteMove2Land(int number) {
        return new byte[0];
    }

    @Override
    public byte[] getWriteBlueTouchNameLand(String text) {
        return new byte[0];
    }

    @Override
    public byte[] getWriteParam(int... parms) {
        return new byte[0];
    }

    @Override
    public List<CommendItem> processingResultData(byte[] cmds) {
        return null;
    }

    @Override
    public CommendItem getStringByProtocol(InputStream is) throws Exception {
        return null;
    }

    @Override
    public byte[] getMotorStatus(int... number) {
        return new byte[0];
    }

    @Override
    public byte[] setMotor(int... number) {
        return new byte[0];
    }

    public void setBtCommendCell(BTCommendCell btCommendCell) {
        this.btCommendCell = btCommendCell;
    }
}
