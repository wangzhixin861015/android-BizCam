package com.bcnetech.bluetoothlibarary.bluetoothagreement;

/**
 * Created by wenbin on 16/7/19.
 */
public class CommendUtill0 extends BaseCommendUtil {
    private byte []len;
    public CommendUtill0 (){
        len=new byte[]{ (byte) 0xA5,(byte) 0x5A,  0x00, 0x00, 0x00,  0x00, (byte)0xEE };
    }


    /***
     * 读取左灯数据
     * @return
     */
    public byte [] getReadLeftLand(){
        if(len==null){
            len=new byte[]{ (byte) 0xA5,(byte) 0x5A,  0x00, 0x00, 0x00,  0x00, (byte)0xEE };
        }
        len[2]= 0x00;
        len[3]= 0x00;
        len[4]= 0x00;
        len[5]=(byte) (len[2]+len[3]+len[4]);
        return len;
    }

    /***
     * 读取右灯数据
     * @return
     */
    public byte [] getReadRightLand(){
        if(len==null){
            len=new byte[]{ (byte) 0xA5,(byte) 0x5A,  0x00, 0x00, 0x00,  0x00, (byte)0xEE };
        }
        len[2]= 0x00;
        len[3]= 0x01;
        len[4]= 0x00;
        len[5]=(byte) (len[2]+len[3]+len[4]);
        return len;
    }

    /***
     * 读取地灯灯数据
     * @return
     */
    public byte [] getReadBottomLand(){
        if(len==null){
            len=new byte[]{ (byte) 0xA5,(byte) 0x5A,  0x00, 0x00, 0x00,  0x00, (byte)0xEE };
        }
        len[2]= 0x00;
        len[3]= 0x02;
        len[4]= 0x00;
        len[5]=(byte) (len[2]+len[3]+len[4]);
        return len;
    }

    /***
     * 读取背灯数据
     * @return
     */
    public byte [] getReadBackGroundLand(){
        if(len==null){
            len=new byte[]{ (byte) 0xA5,(byte) 0x5A,  0x00, 0x00, 0x00,  0x00, (byte)0xEE };
        }
        len[2]= 0x00;
        len[3]= 0x03;
        len[4]= 0x00;
        len[5]=(byte) (len[2]+len[3]+len[4]);
        return len;
    }

    /***
     * 读取补光灯数据
     * @return
     */
    public byte [] getReadContentLand(){
        if(len==null){
            len=new byte[]{ (byte) 0xA5,(byte) 0x5A,  0x00, 0x00, 0x00,  0x00, (byte)0xEE };
        }
        len[2]= 0x00;
        len[3]= 0x04;
        len[4]= 0x00;
        len[5]=(byte) (len[2]+len[3]+len[4]);
        return len;
    }



    /***
     * 输入左灯数据
     * @return
     */
    public byte [] getWriteLeftLand(int number){
        if(len==null){
            len=new byte[]{ (byte) 0xA5,(byte) 0x5A,  0x00, 0x00, 0x00,  0x00, (byte)0xEE };
        }
        len[2]= 0x01;
        len[3]= 0x00;
        len[4]=(byte)number;
        len[5]=(byte) (len[2]+len[3]+len[4]);
        return len;
    }



    /***
     * 输入右灯数据
     * @return
     */
    public byte [] getWriteRightLand(int number){
        if(len==null){
            len=new byte[]{ (byte) 0xA5,(byte) 0x5A,  0x00, 0x00, 0x00,  0x00, (byte)0xEE };
        }
        len[2]= 0x01;
        len[3]= 0x01;
        len[4]=(byte)number;
        len[5]=(byte) (len[2]+len[3]+len[4]);
        return len;
    }

    /***
     * 输入底灯数据
     * @return
     */
    public byte [] getWriteBottomLand(int number){
        if(len==null){
            len=new byte[]{ (byte) 0xA5,(byte) 0x5A,  0x00, 0x00, 0x00,  0x00, (byte)0xEE };
        }
        len[2]= 0x01;
        len[3]= 0x02;
        len[4]=(byte)number;
        len[5]=(byte) (len[2]+len[3]+len[4]);
        return len;
    }
    /***
     * 输入背灯数据
     * @return
     */
    public byte [] getWriteBackGroundLand(int number){
        if(len==null){
            len=new byte[]{ (byte) 0xA5,(byte) 0x5A,  0x00, 0x00, 0x00,  0x00, (byte)0xEE };
        }
        len[2]= 0x01;
        len[3]= 0x03;
        len[4]=(byte)number;
        len[5]=(byte) (len[2]+len[3]+len[4]);
        return len;
    }

    /***
     * 输入移动灯数据
     * @return
     */
    public byte [] getWriteMoveLand(int number){
        if(len==null){
            len=new byte[]{ (byte) 0xA5,(byte) 0x5A,  0x00, 0x00, 0x00,  0x00, (byte)0xEE };
        }
        len[2]= 0x01;
        len[3]= 0x04;
        len[4]=(byte)number;
        len[5]=(byte) (len[2]+len[3]+len[4]);
        return len;
    }






}
