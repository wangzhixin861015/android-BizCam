package com.bcnetech.bluetoothlibarary.bluetoothagreement;

import com.bcnetech.bluetoothlibarary.bluetoothUtil.BlueToochCommendUtil;
import com.bcnetech.bluetoothlibarary.data.CommendItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenbin on 2016/12/5.
 */

public class CommendManage {
    public static final int CONNECTED = 999;
    public static final int VERSION = 1;
    public static final int LEFT_LAND = 2;
    public static final int RIGHT_LAND = 3;
    public static final int BACK_LAND = 4;
    public static final int BOTTOM_LAND = 5;
    public static final int MOVE_LAND = 6;
    public static final int MOVE2_LAND = 7;
    public static final int MOTOR = 8;
    public static final int MOTOR_RUM = 9;
    public static final int MOTOR_STOP = 10;
    public static final int ONLINE_STATUS = 11;
    public static final int USETIME_VALUE = 12;
    public static final int VERSION1_1 = 4096;//COBOX S1
    public static final int VERSION2_1 = 8192;//COBOX S2
    public static final int VERSION_BOX = 12288;//商拍魔盒
    private static int version;//蓝牙版本号
    public static final String COBOX_NAME = "COBOX";
    public static final String CBEDU_NAME = "CBEDU";
    public static final String COLINK_NAME = "COLINK";
    public static final int MOTORON = 0;//转盘未运行
    public static final int MOTOROFF = 1;//转盘运行
    public static final int MOTOROFFLINE = -1;//转盘在线
    public static final int MOTORONLINE = 0;//转盘离线


    /**
     * 商拍魔盒
     */
    public static final int L1 = LEFT_LAND;
    public static final int L2 = RIGHT_LAND;
    public static final int L3 = BACK_LAND;
    public static final int L4 = BOTTOM_LAND;

    private static CommendManage instance;

    private CommendManage() {
    }

    public static CommendManage getInstance() {
        if (instance == null) {
            instance = new CommendManage();
        }
        return instance;
    }

    private CommendUtil1 commendUtil;


    /**
     * 获得版本信息
     *
     * @return
     */
    public byte[] getCurrentVersion() {
        commendUtilIsNull();
        return commendUtil.getCurrentVersion();

    }

    /***
     * 读取左灯数据
     * @return
     */
    public byte[] getReadLeftLand() {
        commendUtilIsNull();

        return commendUtil.getReadLeftLand();
    }

    /***
     * 读取右灯数据
     * @return
     */
    public byte[] getReadRightLand() {
        commendUtilIsNull();
        return commendUtil.getReadRightLand();
    }

    /***
     * 读取地灯灯数据
     * @return
     */
    public byte[] getReadBottomLand() {
        commendUtilIsNull();
        return commendUtil.getReadBottomLand();
    }

    /***
     * 读取背灯数据
     * @return
     */
    public byte[] getReadBackGroundLand() {
        commendUtilIsNull();
        return commendUtil.getReadBackGroundLand();
    }

    /***
     * 读取补光灯数据
     * @return
     */
    public byte[] getReadContentLand() {
        commendUtilIsNull();
        return commendUtil.getReadMotorLand();
    }


    public byte[] getParamsLand() {
        commendUtilIsNull();

        return commendUtil.getParamsLand();
    }


    /***
     * 输入左灯数据
     * @return
     */
    public byte[] getWriteLeftLand(int number) {
        commendUtilIsNull();
        return commendUtil.getWriteLeftLand(number);
    }


    /***
     * 输入右灯数据
     * @return
     */
    public byte[] getWriteRightLand(int number) {
        commendUtilIsNull();
        return commendUtil.getWriteRightLand(number);
    }

    /***
     * 输入底灯数据
     * @return
     */
    public byte[] getWriteBottomLand(int number) {
        commendUtilIsNull();
        return commendUtil.getWriteBottomLand(number);
    }

    /***
     * 输入背灯数据
     * @return
     */
    public byte[] getWriteBackGroundLand(int number) {
        commendUtilIsNull();
        return commendUtil.getWriteBackGroundLand(number);
    }

    /***
     * 输入移动灯数据
     * @return
     */
    public byte[] getWriteMoveLand(int number) {
        commendUtilIsNull();
        return commendUtil.getWriteMoveLand(number);
    }

    /***
     * 输入移动灯2数据
     * @return
     */
    public byte[] getWriteMove2Land(int number) {
        commendUtilIsNull();
        return commendUtil.getWriteMove2Land(number);
    }

    /***
     * 输入移动灯2数据
     * @return
     */
    public byte[] getWriteBlueTouchNameLand(String text) {
        commendUtilIsNull();
        return commendUtil.getWriteBlueTouchNameLand(text);
    }

    /***
     * 系统升级
     * @return
     */
    public byte[] systemUpdata() {
        commendUtilIsNull();
        return commendUtil.systemUpdata();
    }

    /***
     * 得当前系统运行时间
     * @return
     */
    public byte[] getUsetimeValue() {
        commendUtilIsNull();
        return commendUtil.getUsetimeValue();
    }


    /***
     * 设置转盘状态
     * @return
     */
    public byte[] setMotorStatus(boolean isStart) {
        commendUtilIsNull();
        return commendUtil.setMotor(isStart ? 1 : 0);
    }

    /***
     * 获取转盘状态
     * @return
     */
    /*public byte[] getMotorStatus() {
        commendUtilIsNull();
        return commendUtil.getMotor();
    }
*/

    /**
     * 输入所有灯的数据
     *
     * @param left
     * @param right
     * @param background
     * @param bottom
     * @param move1
     * @param move2
     * @param motor
     * @param dataparm
     * @return
     */
    public byte[] getWriteParm(int left, int right, int background, int bottom, int move1, int move2, int motor, int dataparm) {
        commendUtilIsNull();
        return commendUtil.getWriteParam(dataparm, motor, move2, move1, bottom, background, right, left);
    }


    private void commendUtilIsNull() {
        if (commendUtil == null) {
            getCommendUtil();
        }
    }

    /**
     * 判断命令util实例是否存在
     */
    private void getCommendUtil() {

        switch (version) {
            case VERSION1_1:
            case VERSION_BOX:
            default:
                if (commendUtil == null || !(commendUtil instanceof CommendUtil1)) {
                    commendUtil = new CommendUtil1();
                }
                break;
        }
    }


    /**
     * @param commendItem
     * @return
     */
    public List<CommendItem> processingResultData(CommendItem commendItem) {
        List<CommendItem> list = null;
        if (commendItem == null || commendItem.getRespons().length == 0) {
            return null;
        }
        list = new ArrayList<>();

        processingResultData(list, commendItem.getRespons());
        return list;
    }

    /**
     * ble
     *
     * @return
     */
    public List<CommendItem> processingResultData(byte[] respond) {
        List<CommendItem> list = null;
        if (respond == null || respond.length == 0) {
            return null;
        }
        list = new ArrayList<>();

        processingResultData(list, respond);
        return list;
    }

    private List<CommendItem> processingResultData(List<CommendItem> list, byte[] cmds) {
        if (cmds[0] == (byte) 0xEA
                && cmds[1] == (byte) 0xA5
                && cmds[3] == (byte) 0x01) {
            int size = BlueToochCommendUtil.byteToIntSmall(new byte[]{cmds[4], cmds[5]});
            byte[] data = new byte[size];
            for (int i = 0; i < size; i++) {
                data[i] = cmds[6 + i];
            }
            version = BlueToochCommendUtil.byteToIntSmall(data);
            getCommendUtil();
            CommendItem item = new CommendItem();
            item.setType(VERSION);
            item.setNum(version);
            list.add(item);
        } else if (cmds[0] == (byte) 0xEA
                && cmds[1] == (byte) 0xA5
                && cmds[3] == (byte) 0x63) {
            getCommendUtil();
            switch (version) {
                case VERSION1_1:
                case VERSION_BOX:
                default:
                    int size = BlueToochCommendUtil.byteToIntSmall(new byte[]{cmds[4], cmds[5]});
                    byte[] data = new byte[size];
                    for (int i = 0; i < size; i++) {
                        data[i] = cmds[6 + i];
                    }
                    int time = BlueToochCommendUtil.byteToIntSmall(data);
                    CommendItem item = new CommendItem();
                    item.setType(USETIME_VALUE);
                    item.setNum(time);
                    list.add(item);
                    break;
            }

        } else {
            getCommendUtil();
            switch (version) {
                case VERSION1_1:
                case VERSION_BOX:
                default:
                    list.addAll(commendUtil.processingResultData(cmds));
                    break;
            }
        }
        return list;
    }

    /**
     * 协议1。1解析
     *
     * @param is
     * @return
     * @throws IOException
     */
    public CommendItem getStringByProtocol(InputStream is) throws Exception {
        commendUtilIsNull();
        return commendUtil.getStringByProtocol(is);
    }

    public void setBtCommendCell(BTCommendCell btCommendCell) {
        commendUtilIsNull();
        commendUtil.setBtCommendCell(btCommendCell);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
