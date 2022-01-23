package com.example.imageproc.jni;

import java.util.Arrays;

/**
 * Created by wangzhixin on 2018/9/17.
 */

public class ProcessByte {

    public int api_method;
    public int img_width;
    public int img_height;
    public int retval;
    public int [] target;
    public byte [] srcbuf;
    public int [] dstbuf;
    public byte [] outmuf;
    public byte [] inmask;
    public int  wbval;
    public double clarityVal;
    public String version;
    public int[] iparams;
    //识别返回，0为判断状态，1为偏移角度
    public double[] dparams;

    @Override
    public String toString() {
        return "ProcessByte{" +
                ", iparams=" + Arrays.toString(iparams) +
                '}';
    }
}
