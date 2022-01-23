package com.bcnetech.bcnetchhttp.uuid;

import java.net.InetAddress;

/**
 * Created by wenbin on 16/6/6.
 */
public class UUIDGenerator {
    private static final int IP;
    private static short counter;
    private static final int JVM;

    public UUIDGenerator() {
    }

    public static String generateHex() {
        return (new StringBuffer(36)).append(format(getIP())).append(format(getJVM())).append(format(getHighTime())).append(format(getLowTime())).append(format(getCount())).toString();
    }

    public static byte[] generateBytes() {
        byte[] var0 = new byte[16];
        System.arraycopy(getBytes(getIP()), 0, var0, 0, 4);
        System.arraycopy(getBytes(getJVM()), 0, var0, 4, 4);
        System.arraycopy(getBytes(getHighTime()), 0, var0, 8, 2);
        System.arraycopy(getBytes(getLowTime()), 0, var0, 10, 4);
        System.arraycopy(getBytes(getCount()), 0, var0, 14, 2);
        return var0;
    }

    private static String format(int var0) {
        String var1 = Integer.toHexString(var0);
        StringBuffer var2 = new StringBuffer("00000000");
        var2.replace(8 - var1.length(), 8, var1);
        return var2.toString();
    }

    private static String format(short var0) {
        String var1 = Integer.toHexString(var0);
        StringBuffer var2 = new StringBuffer("0000");
        var2.replace(4 - var1.length(), 4, var1);
        return var2.toString();
    }

    private static int getJVM() {
        return JVM;
    }

    private static short getCount() {
        Class var0 = UUIDGenerator.class;
        synchronized(UUIDGenerator.class) {
            if(counter < 0) {
                counter = 0;
            }

            return counter++;
        }
    }

    private static int getIP() {
        return IP;
    }

    private static short getHighTime() {
        return (short)((int)(System.currentTimeMillis() >>> 32));
    }

    private static int getLowTime() {
        return (int)System.currentTimeMillis();
    }

    private static int toInt(byte[] var0) {
        int var1 = 0;

        for(int var2 = 0; var2 < 4; ++var2) {
            var1 = (var1 << 8) - -128 + var0[var2];
        }

        return var1;
    }

    private static byte[] getBytes(int var0) {
        return new byte[]{(byte)(var0 >> 24), (byte)(var0 >> 16), (byte)(var0 >> 8), (byte)var0};
    }

    private static byte[] getBytes(short var0) {
        return new byte[]{(byte)(var0 >> 8), (byte)var0};
    }

    public static String toHexString(byte[] var0) {
        StringBuffer var1 = new StringBuffer();

        for(int var2 = 0; var2 < var0.length; ++var2) {
            var1.append(enoughZero(Integer.toHexString(var0[var2] & 255), 2));
        }

        return var1.toString();
    }

    public static String enoughZero(String var0, int var1) {
        while(var0.length() < var1) {
            var0 = "0" + var0;
        }

        return var0;
    }

    public static void main(String[] var0) {
        System.out.println(generateHex());
    }

    static {
        int var0;
        try {
            var0 = toInt(InetAddress.getLocalHost().getAddress());
        } catch (Exception var2) {
            var0 = 0;
        }

        IP = var0;
        counter = 0;
        JVM = (int)(System.currentTimeMillis() >>> 8);
    }
}
