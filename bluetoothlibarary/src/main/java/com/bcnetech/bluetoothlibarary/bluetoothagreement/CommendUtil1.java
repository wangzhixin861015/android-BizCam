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

public class CommendUtil1 extends BaseCommendUtil {

    private final static byte HEAD1 = (byte) 0xEA;
    private final static byte HEAD2 = (byte) 0xA5;
    private final static byte END = (byte) 0xFC;

    private final static byte GET_TIME = (byte) 0x02;
    private final static byte SET_TIME = (byte) 0x03;
    private final static byte SET_LEFT_LAND = (byte) 0x10;
    private final static byte SET_RIGHT_LAND = (byte) 0x11;
    private final static byte SET_BACK_LAND = (byte) 0x12;
    private final static byte SET_BOTTOM_LAND = (byte) 0x13;
    private final static byte SET_MOVE_LAND = (byte) 0x14;
    private final static byte SET_MOVE2_LAND = (byte) 0x15;
    private final static byte SET_PARAM = (byte) 0x18;
    private final static byte GET_LEFT_LAND = (byte) 0x30;
    private final static byte GET_RIGHT_LAND = (byte) 0x31;
    private final static byte GET_BACK_LAND = (byte) 0x32;
    private final static byte GET_BOTTOM_LAND = (byte) 0x33;
    private final static byte GET_MOVE_LAND = (byte) 0x34;
    private final static byte GET_MOVE2_LAND = (byte) 0x35;
    private final static byte GET_MOTOR = (byte) 0x36;
    private final static byte GET_PARAM = (byte) 0x38;
    private final static byte GET_ONLINE_STATUS = (byte) 0x39;
    private final static byte GET_CMD_SEND = (byte) 0x51;
    private final static byte MOTOR_RUM = (byte) 0x60;
    private final static byte MOTOR_STOP = (byte) 0x61;
    private final static byte GET_USETIME_VALUE = (byte) 0x63;
    private final static byte BLUETOUCH_NAME = (byte) 0xE0;
    private final static byte SET_KEY_PARAM = (byte) 0xA0;
    private final static byte GET_KEY_PARAM = (byte) 0xA1;
    private final static byte SYSTEM_UPDATA = (byte) 0XE1;
    private final static byte SYSTEM_REBOOT = (byte) 0xE2;
    private final static byte SYSTEM_SHUTDOWN = (byte) 0xE3;
    private final static byte SYSTEM_RESTORE_FACTORY = (byte) 0xE4;
    private final static byte SET_MOTORRUN = (byte) 0x60;
    private final static byte SET_MOTORSTOP = (byte) 0x61;
    //以下为商拍魔盒蓝牙命令
    private final static byte SET_L1_LAMP = (byte) 0x10;
    private final static byte SET_L2_LAMP = (byte) 0x11;
    private final static byte SET_L3_LAMP = (byte) 0x12;
    private final static byte SET_L4_LAMP = (byte) 0x13;
    private final static byte SET_PARAM_LAMP = (byte) 0x18;
    private final static byte GET_L1_LAMP = (byte) 0x30;
    private final static byte GET_L2_LAMP = (byte) 0x31;
    private final static byte GET_L3_LAMP = (byte) 0x32;
    private final static byte GET_L4_LAMP = (byte) 0x33;
    private final static byte GET_PARAM_LAMP = (byte) 0x38;


    private byte[] len;


    private byte[] versionCmd;

    public CommendUtil1() {
        len = new byte[6];
    }


    /**
     * 获得版本号
     *
     * @return
     */
    public byte[] getCurrentVersion() {
        return processingData((byte) 0x00, VERSION, new byte[]{});
    }


    /**
     * 获得当前时间
     *
     * @return
     */
    public byte[] getCurrentTime() {
        return processingData((byte) 0x00, GET_TIME, new byte[]{});
    }

    /***
     * 读取左灯数据
     * @return
     */
    public byte[] getReadLeftLand() {
        return processingData((byte) 0x00, GET_LEFT_LAND, new byte[]{});
    }

    /***
     * 读取右灯数据
     * @return
     */
    public byte[] getReadRightLand() {
        return processingData((byte) 0x00, GET_RIGHT_LAND, new byte[]{});
    }

    /***
     * 读取地灯灯数据
     * @return
     */
    public byte[] getReadBottomLand() {
        return processingData((byte) 0x00, GET_BOTTOM_LAND, new byte[]{});
    }

    /***
     * 读取背灯数据
     * @return
     */
    public byte[] getReadBackGroundLand() {
        return processingData((byte) 0x00, GET_BACK_LAND, new byte[]{});
    }

    /***
     * 读取移动灯数据
     * @return
     */
    public byte[] getReadMoveLand() {
        return processingData((byte) 0x00, GET_MOVE_LAND, new byte[]{});
    }

    /**
     * 读取移动灯2数据
     *
     * @return
     */
    public byte[] getReadMove2Land() {
        return processingData((byte) 0x00, GET_MOVE2_LAND, new byte[]{});
    }


    /**
     * 获取转盘数据
     *
     * @return
     */
    public byte[] getReadMotorLand() {
        return processingData((byte) 0x00, GET_MOTOR, new byte[]{});
    }


    /**
     * 获取组参数
     * 38
     * 07
     * 00
     * Data0
     * Data1
     * Data2
     * Data3
     * Data4
     * Data5
     * Data6
     * Data7
     *
     * @return 左灯亮度=data0;
     * 右灯亮度=data1;
     * 背灯亮度=data2;
     * 底灯亮度=data3;
     * 移动灯 1 亮度=data4;
     * 移动灯 2 亮度=data5;
     * 转盘转速=data6;
     * 预留=data7 其中底灯，移动灯 1，2， 转盘有时才为真实值，否 则为 255
     */
    public byte[] getParamsLand() {
        return processingData((byte) 0x00, GET_PARAM, new byte[]{});
    }


    /**
     * 获得所有模块在线状态
     * 39
     * 07
     * 00
     * Data0
     * Data1
     * Data2
     * Data3
     * Data4
     * Data5
     * Data6
     *
     * @return 左灯=data0;
     * 右灯=data1;
     * 背灯=data2;
     * 底灯=data3;
     * 移动灯 1=data4;
     * 移动灯 2=data5;
     * 转盘=data6;为 1 表示设备在线，为 0 表示设备不在线，
     * 其中左 灯，右灯，背灯目前一直 在线(后期版本可以修改 硬件实现实时检测)，
     * data0,data1,data2 会固定 发1
     */
    public byte[] getAllOnlineLand() {
        return processingData((byte) 0x00, GET_ONLINE_STATUS, new byte[]{});
    }


    /**
     * @param text
     * @return
     */
    @Override
    public byte[] getWriteBlueTouchNameLand(String text) {

        return processingData((byte) 0x00, BLUETOUCH_NAME, text.getBytes());
    }

    /***
     * 输入左灯数据
     * @return
     */
    public byte[] getWriteLeftLand(int number) {
        return processingData((byte) 0x00, SET_LEFT_LAND, new byte[]{(byte) number});
    }


    /***
     * 输入右灯数据
     * @return
     */
    public byte[] getWriteRightLand(int number) {
        return processingData((byte) 0x00, SET_RIGHT_LAND, new byte[]{(byte) number});
    }

    /***
     * 输入底灯数据
     * @return
     */
    public byte[] getWriteBottomLand(int number) {
        return processingData((byte) 0x00, SET_BOTTOM_LAND, new byte[]{(byte) number});
    }

    /***
     * 输入背灯数据
     * @return
     */
    public byte[] getWriteBackGroundLand(int number) {
        return processingData((byte) 0x00, SET_BACK_LAND, new byte[]{(byte) number});
    }

    /***
     * 输入移动灯数据
     * @return
     */
    public byte[] getWriteMoveLand(int number) {
        return processingData((byte) 0x00, SET_MOVE_LAND, new byte[]{(byte) number});
    }

    /**
     * 输入移动灯2数据
     *
     * @param number
     * @return
     */
    public byte[] getWriteMove2Land(int number) {
        return processingData((byte) 0x00, SET_MOVE2_LAND, new byte[]{(byte) number});
    }


    /**
     * @return
     */
    public byte[] getKeyParam() {
        return processingData((byte) 0x00, GET_KEY_PARAM, new byte[]{});
    }

    /**
     * @param data0
     * @param data1
     * @param data2
     * @param data3
     * @return
     */
    public byte[] setKeyParam(int data0, int data1, int data2, int data3) {
        return processingData((byte) 0x00, SET_KEY_PARAM, new byte[]{(byte) data0, (byte) data1, (byte) data2, (byte) data3});
    }


    /**
     * 系统升级
     *
     * @return
     */
    public byte[] systemUpdata() {
        return processingData((byte) 0x00, SYSTEM_UPDATA, new byte[]{});
    }

    /**
     * 系统重启
     *
     * @return
     */
    public byte[] systemRrboot() {

        return processingData((byte) 0x00, SYSTEM_REBOOT, new byte[]{});
    }

    /**
     * 系统关机
     *
     * @return
     */
    public byte[] systemShutdown() {
        return processingData((byte) 0x00, SYSTEM_SHUTDOWN, new byte[]{});
    }

    /**
     * 恢复出厂设置
     *
     * @return
     */
    public byte[] systemRestoreFactory() {
        return processingData((byte) 0x00, SYSTEM_RESTORE_FACTORY, new byte[]{});
    }

    /**
     * 得当前系统运 行时间
     *
     * @return
     */
    public byte[] getUsetimeValue() {
        return processingData((byte) 0x00, GET_USETIME_VALUE, new byte[]{});
    }


    /**
     * left
     * right
     * background
     * bottom
     * move1
     * move2
     * motor
     * dataparm
     *
     * @return
     */
    public byte[] getWriteParam(int... parms) {
        byte[] data = new byte[]{(byte) parms[0], (byte) parms[1], (byte) parms[2], (byte) parms[3], (byte) parms[4],
                (byte) parms[5], (byte) parms[6], (byte) parms[7]};
        return processingData((byte) 0x00, SET_PARAM, data);
    }


    /**
     * 电机启动
     *
     * @return
     */
    public byte[] motorRun() {
        return processingData((byte) 0x00, MOTOR_RUM, new byte[]{});
    }


    /**
     * 电机停止
     *
     * @return
     */
    public byte[] motorStop() {
        return processingData((byte) 0x00, MOTOR_STOP, new byte[]{});
    }

    /////////////以下为商拍魔盒蓝牙命令/////////////

    /***
     * 输入L1数据
     * @return
     */
    public byte[] setL1Lamp(int number) {
        return processingData((byte) 0x00, SET_L1_LAMP, new byte[]{(byte) number});
    }


    /***
     * 输入L2数据
     * @return
     */
    public byte[] setL2Lamp(int number) {
        return processingData((byte) 0x00, SET_L2_LAMP, new byte[]{(byte) number});
    }

    /***
     * 输入L3数据
     * @return
     */
    public byte[] setL3Lamp(int number) {
        return processingData((byte) 0x00, SET_L3_LAMP, new byte[]{(byte) number});
    }

    /***
     * 输入L4数据
     * @return
     */
    public byte[] setL4Lamp(int number) {
        return processingData((byte) 0x00, SET_L4_LAMP, new byte[]{(byte) number});
    }

    /**
     * 设置全部灯数据
     *
     * @param parms
     * @return
     */
    public byte[] setLAMPParam(int... parms) {
        byte[] data = new byte[]{(byte) parms[0], (byte) parms[1], (byte) parms[2], (byte) parms[3]};
        return processingData((byte) 0x00, SET_PARAM, data);
    }

    /***
     * 读取L1数据
     * @return
     */
    public byte[] getL1Lamp() {
        return processingData((byte) 0x00, GET_L1_LAMP, new byte[]{});
    }

    /***
     * 读取L2数据
     * @return
     */
    public byte[] getL2Lamp() {
        return processingData((byte) 0x00, GET_L2_LAMP, new byte[]{});
    }

    /***
     * 读取L3数据
     * @return
     */
    public byte[] getL3Lamp() {
        return processingData((byte) 0x00, GET_L3_LAMP, new byte[]{});
    }

    /***
     * 读取L4数据
     * @return
     */
    public byte[] getL4Lamp() {
        return processingData((byte) 0x00, GET_L4_LAMP, new byte[]{});
    }

    /**
     * 读取全部灯数据
     *
     * @return
     */
    public byte[] getParamLamp() {
        return processingData((byte) 0x00, GET_PARAM_LAMP, new byte[]{});
    }

    /**
     * 设置转盘状态
     * 0:停止转动，1：开始转动
     *
     * @return
     */
    public byte[] setMotor(int status) {
        return processingData((byte) 0x00, status == 0 ? SET_MOTORSTOP : SET_MOTORRUN, new byte[]{});
    }

    /**
     * 获取转盘状态
     * 0:停止转动，1：转动，255：转盘离线
     *
     * @return
     */
   /* public byte[] getMotor() {
        return processingData((byte) 0x00, SET_MOTOR, new byte[]{});
    }
*/
    public List<CommendItem> processingResultData(byte[] cmds) {
        List list = new ArrayList();
        if (cmds.length < 9) {
            return list;
        }
        int cmd = cmds[3];
        int size = BlueToochCommendUtil.byteToIntSmall(new byte[]{cmds[4], cmds[5]});

        switch (cmd) {
            case GET_LEFT_LAND:
                list.add(new CommendItem(CommendManage.LEFT_LAND, cmds[6]));
                break;
            case GET_RIGHT_LAND:
                list.add(new CommendItem(CommendManage.RIGHT_LAND, cmds[6]));
                break;
            case GET_BACK_LAND:
                list.add(new CommendItem(CommendManage.BACK_LAND, cmds[6]));
                break;
            case GET_BOTTOM_LAND:
                list.add(new CommendItem(CommendManage.BOTTOM_LAND, cmds[6]));
                break;
            case GET_MOVE_LAND:
                list.add(new CommendItem(CommendManage.MOVE_LAND, cmds[6]));
                break;
            case GET_MOVE2_LAND:
                list.add(new CommendItem(CommendManage.MOVE2_LAND, cmds[6]));
                break;
            case GET_MOTOR:
                list.add(new CommendItem(CommendManage.MOTOR, cmds[6]));
                break;
            case GET_ONLINE_STATUS:
                if (size >= 6) {
                    list.add(new CommendItem(CommendManage.ONLINE_STATUS, CommendManage.LEFT_LAND, cmds[6]));
                    list.add(new CommendItem(CommendManage.ONLINE_STATUS, CommendManage.RIGHT_LAND, cmds[7]));
                    list.add(new CommendItem(CommendManage.ONLINE_STATUS, CommendManage.BACK_LAND, cmds[8]));
                    list.add(new CommendItem(CommendManage.ONLINE_STATUS, CommendManage.BOTTOM_LAND, cmds[9]));
                    list.add(new CommendItem(CommendManage.ONLINE_STATUS, CommendManage.MOVE_LAND, cmds[10]));
                    list.add(new CommendItem(CommendManage.ONLINE_STATUS, CommendManage.MOVE2_LAND, cmds[11]));
                    list.add(new CommendItem(CommendManage.ONLINE_STATUS, CommendManage.MOTOR, cmds[12]));
                }
                break;
            case GET_CMD_SEND:
                if (size >= 7) {
                    list.add(new CommendItem(CommendManage.LEFT_LAND, cmds[6]));
                    list.add(new CommendItem(CommendManage.RIGHT_LAND, cmds[7]));
                    list.add(new CommendItem(CommendManage.BACK_LAND, cmds[8]));
                    list.add(new CommendItem(CommendManage.BOTTOM_LAND, cmds[9]));
                    list.add(new CommendItem(CommendManage.MOVE_LAND, cmds[10]));
                    list.add(new CommendItem(CommendManage.MOVE2_LAND, cmds[11]));
                    list.add(new CommendItem(CommendManage.MOTOR, cmds[12]));
                }
                break;
            case SET_PARAM:
            case GET_PARAM:
                if (size >= 7) {
                    list.add(new CommendItem(CommendManage.LEFT_LAND, cmds[6]));
                    list.add(new CommendItem(CommendManage.RIGHT_LAND, cmds[7]));
                    list.add(new CommendItem(CommendManage.BACK_LAND, cmds[8]));
                    list.add(new CommendItem(CommendManage.BOTTOM_LAND, cmds[9]));
                    list.add(new CommendItem(CommendManage.MOVE_LAND, cmds[10]));
                    list.add(new CommendItem(CommendManage.MOVE2_LAND, cmds[11]));
                    list.add(new CommendItem(CommendManage.MOTOR, cmds[12]));
                } else if (size >= 4) {
                    //商拍魔盒
                    list.add(new CommendItem(CommendManage.LEFT_LAND, cmds[6]));
                    list.add(new CommendItem(CommendManage.RIGHT_LAND, cmds[7]));
                    list.add(new CommendItem(CommendManage.BACK_LAND, cmds[8]));
                    list.add(new CommendItem(CommendManage.BOTTOM_LAND, cmds[9]));
                }

                break;

            case MOTOR_RUM:
                list.add(new CommendItem(CommendManage.MOTOR_RUM, cmds[6]));
                break;

            case MOTOR_STOP:
                list.add(new CommendItem(CommendManage.MOTOR_STOP, cmds[6]));
                break;


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
    public CommendItem getStringByProtocol(InputStream is) throws IOException {
        CommendItem commendItem = null;
        int bytes, bytes2;
        if ((bytes = is.read(len)) == 6) {
            if (len[0] == HEAD1 && len[1] == HEAD2) {
                int data_size = 0;
                data_size += (((int) len[4]));
                data_size += (((int) len[5]) << 8);

                byte[] buf_data = new byte[bytes + data_size + 3];

                for (int i = 0; i < bytes; i++) {
                    buf_data[i] = len[i];
                }
                byte[] data = new byte[data_size + 3];
                if ((bytes2 = is.read(data)) == (data_size + 3)) {
                    if (data[data_size] == END) {
                        for (int i = 0; i < bytes2; i++) {
                            buf_data[i + bytes] = data[i];
                        }

                    } else {
                        return commendItem;
                    }

                } else {
                    return commendItem;
                }

                commendItem = new CommendItem();
                commendItem.setRespons(buf_data);
            }
        }

        return commendItem;
    }

    /**
     * BizCam专属 生成命令
     *
     * @param type
     * @param cmd
     * @param data
     * @return
     */
    private byte[] processingData(byte type, byte cmd, byte[] data) {
        int size = data == null ? 0 : data.length;
        byte[] respone = new byte[6 + size + 3];
        respone[0] = HEAD1;
        respone[1] = HEAD2;
        respone[2] = type;
        respone[3] = cmd;
        respone[4] = (byte) size;
        respone[5] = (byte) (size >> 8);
        int j = 6;
        for (int i = size - 1; i >= 0; i--) {
            respone[j] = data[i];
            j++;
        }
        respone[j++] = END;
        int crc = BlueToochCommendUtil.calcCrc16(respone, 0, 6 + size + 1);
        respone[j++] = (byte) (crc);
        respone[j] = (byte) (crc >> 8);

        return respone;
    }


}
