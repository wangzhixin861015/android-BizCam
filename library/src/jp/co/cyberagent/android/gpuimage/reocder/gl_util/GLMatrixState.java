package jp.co.cyberagent.android.gpuimage.reocder.gl_util;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Stack;

//存储系统矩阵状态的类
public class GLMatrixState {
    //4x4矩阵 投影用
    private static float[] mProjMatrix = new float[16];
    //摄像机位置朝向9参数矩阵
    private static float[] mVMatrix = new float[16];
    //当前变换矩阵
    private static float[] currMatrix;
    //定位光光源位置
    public static float[] lightLocation = new float[]{0, 0, 0};
    public static FloatBuffer cameraFB;
    public static FloatBuffer lightPositionFB;

    //保护变换矩阵的栈
    public static Stack<float[]> mStack = new Stack<float[]>();

    //获取不变换初始矩阵
    public static void setInitStack()
    {
        currMatrix = new float[16];
        Matrix.setRotateM(currMatrix, 0, 0, 1, 0, 0);
    }

    public static void pushMatrix()//保护变换矩阵
    {
        mStack.push(currMatrix.clone());
    }

    public static void popMatrix()//恢复变换矩阵
    {
        currMatrix = mStack.pop();
    }

    public static void translate(float x, float y, float z)//设置沿xyz轴移动
    {
        Matrix.translateM(currMatrix, 0, x, y, z);
    }

    public static void rotate(float angle, float x, float y, float z)//设置绕xyz轴移动
    {
        Matrix.rotateM(currMatrix, 0, angle, x, y, z);
    }

    public static void scale(float x, float y, float z) {
        Matrix.scaleM(currMatrix, 0, x, y, z);
    }

    public static boolean isNoInit(){
        return currMatrix==null;
    }

    //设置摄像机
    public static void setCamera
    (
            float cx,
            float cy,
            float cz,
            float tx,
            float ty,
            float tz,
            float upx,
            float upy,
            float upz
    ) {
        Matrix.setLookAtM
                (
                        mVMatrix,
                        0,
                        cx,
                        cy,
                        cz,
                        tx,
                        ty,
                        tz,
                        upx,
                        upy,
                        upz
                );

        float[] cameraLocation = new float[3];
        cameraLocation[0] = cx;
        cameraLocation[1] = cy;
        cameraLocation[2] = cz;

        ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
        llbb.order(ByteOrder.nativeOrder());
        cameraFB = llbb.asFloatBuffer();
        cameraFB.put(cameraLocation);
        cameraFB.position(0);
    }

    //设置透视投影参数
    public static void setProjectFrustum
    (
            float left,
            float right,
            float bottom,
            float top,
            float near,
            float far
    ) {
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    //设置正交投影参数
    public static void setProjectOrtho
    (
            float left,
            float right,
            float bottom,
            float top,
            float near,
            float far
    ) {
        Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    //获取具体物体的总变换矩阵
    public static float[] getFinalMatrix() {
        float[] mMVPMatrix = new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }

    //获取具体物体的变换矩阵
    public static float[] getMMatrix() {
        return currMatrix;
    }

    //设置灯光位置的方法
    public static void setLightLocation(float x, float y, float z) {
        lightLocation[0] = x;
        lightLocation[1] = y;
        lightLocation[2] = z;
        ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
        llbb.order(ByteOrder.nativeOrder());
        lightPositionFB = llbb.asFloatBuffer();
        lightPositionFB.put(lightLocation);
        lightPositionFB.position(0);
    }

    //获取摄像机朝向的矩阵
    public static float[] getCaMatrix() {
        return mVMatrix;
    }

    //获取投影矩阵
    public static float[] getProjMatrix() {
        return mProjMatrix;
    }
}
