package com.example.imageproc.jni;

/**
 * Created by wangzhixin on 2018/8/23.
 */

public class ProcessInt {
    public int api_method;
    public int img_width;
    public int img_height;
    public int[] srcbuf;//原图
    public int[] dstbuf;
    public String version;
    public int retval;
    public int[] outmuf;
    public byte[] inmask;//mask
    public double clarityVal;
    public int[] wbval;
    public int[] iparams;
    //识别返回，0为判断状态，1为偏移角度
    public double[] dparams;


}
