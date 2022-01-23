package jp.co.cyberagent.android.gpuimage.util;

import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by wenbin on 2017/2/13.
 */

public class GlSurfaceViwMoveZoomListener {
    /*static final float CUBE[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };*/
    private final static double DEVEL = 0.06;//限制大小
    private final static double MOVE_SPEED = 0.2;//动画移动速度

    private final static float CUBE_LINE = 2;//
    private final static float CUBE_LEFT = -1;//
    private final static float CUBE_RIGHT = 1;//
    private final static float CUBE_BOTTOM = -1;//
    private final static float CUBE_TOP = 1;//

    private float startPaintX, startPaintY, startPaint2X, startPaint2Y;
    private float cube[], currentcube[], endCube[];
    private float startLen;
    private float weith, heigh;
    private GPUImageView gpuimage;
    private MyTimerTask mTask;
    private Timer timer;

    public GlSurfaceViwMoveZoomListener() {
        timer = new Timer();
    }

    public void onStartZoomMove(GPUImageView gpuimage, float pointOneX, float pointOneY, float pointTwoX, float pointTwoY) {
        if (gpuimage == null) return;
        this.gpuimage = gpuimage;
        this.cube = gpuimage.getCube();
        this.weith = gpuimage.getMeasuredWidth();
        this.heigh = gpuimage.getMeasuredHeight();
        if (cube == null) {
            return;
        }
        currentcube = new float[]{//顶点数组
                cube[0], cube[1],//左下
                cube[2], cube[3],//右下
                cube[4], cube[5],//左上
                cube[6], cube[7]//右上
        };
        startPaintX = pointOneX;
        startPaintY = pointOneY;
        startPaint2X = pointTwoX;
        startPaint2Y = pointTwoY;
        startLen = (float) Math.sqrt((startPaint2X - startPaintX) * (startPaint2X - startPaintX) + (startPaint2Y - startPaintY) * (startPaint2Y - startPaintY));
    }

    public void onZoomMove(float pointOneX, float pointOneY, float pointTwoX, float pointTwoY) {
        if (gpuimage == null || cube == null) return;
        float xline = (pointOneX + pointTwoX) / 2 - (startPaint2X + startPaintX) / 2;
        float yline = (pointOneY + pointTwoY) / 2 - (startPaint2Y + startPaintY) / 2;
        float endLen = (float) Math.sqrt((pointTwoX - pointOneX) * (pointTwoX - pointOneX) + (pointTwoY - pointOneY) * (pointTwoY - pointOneY));
        double proportion = endLen / startLen;

        currentcube[0] = cube[0] + (xline / weith) * 2;
        currentcube[2] = cube[2] + (xline / weith) * 2;
        currentcube[4] = cube[4] + (xline / weith) * 2;
        currentcube[6] = cube[6] + (xline / weith) * 2;

        float contentX = (currentcube[2] + currentcube[0]) / 2;
        float widthLine = (float) ((currentcube[2] - currentcube[0]) * proportion) / 2;
        if (widthLine < CUBE_LINE / 4) {
            widthLine = CUBE_LINE / 4;
        }
        if (widthLine > CUBE_LINE) {
            widthLine = CUBE_LINE;
        }


        currentcube[0] = contentX - widthLine;
        currentcube[2] = contentX + widthLine;
        currentcube[4] = contentX - widthLine;
        currentcube[6] = contentX + widthLine;

        currentcube[1] = cube[1] - (yline / heigh) * 2;
        currentcube[3] = cube[3] - (yline / heigh) * 2;
        currentcube[5] = cube[5] - (yline / heigh) * 2;
        currentcube[7] = cube[7] - (yline / heigh) * 2;

        float contentY = (currentcube[5] + currentcube[1]) / 2;
        float heightLine = (float) ((currentcube[5] - currentcube[1]) * proportion) / 2;
        if (heightLine < CUBE_LINE / 4) {
            heightLine = CUBE_LINE / 4;
        }
        if (heightLine > CUBE_LINE) {
            heightLine = CUBE_LINE;
        }
        currentcube[1] = contentY - heightLine;
        currentcube[3] = contentY - heightLine;
        currentcube[5] = contentY + heightLine;
        currentcube[7] = contentY + heightLine;

        gpuimage.setCube(currentcube);
        gpuimage.requestRender();
    }

    public void onEndZoomMove() {
        if (gpuimage == null || currentcube == null || cube == null) return;
        endCube = new float[]{
                currentcube[0], currentcube[1],
                currentcube[2], currentcube[3],
                currentcube[4], currentcube[5],
                currentcube[6], currentcube[7]
        };
        verificationXY();
        startAnim();
    }

    public void onDestroy() {
        gpuimage = null;
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
    }

    private float moveX;
    private float moveY;

    private void verificationXY() {
        if (currentcube[2] - currentcube[0] < CUBE_LINE) {
            endCube[0] = -1;
            endCube[1] = -1;
            endCube[2] = 1;
            endCube[3] = -1;
            endCube[4] = -1;
            endCube[5] = 1;
            endCube[6] = 1;
            endCube[7] = 1;
        } else {
            moveX = 0;
            if (currentcube[0] > CUBE_LEFT) {
                moveX = CUBE_LEFT - currentcube[0];
            }
            if (currentcube[2] < CUBE_RIGHT) {
                moveX = CUBE_RIGHT - currentcube[2];
            }
            endCube[0] = currentcube[0] + moveX;
            endCube[2] = currentcube[2] + moveX;
            endCube[4] = currentcube[4] + moveX;
            endCube[6] = currentcube[6] + moveX;

            if (currentcube[1] > CUBE_BOTTOM) {
                moveY = CUBE_BOTTOM - currentcube[1];
            }
            if (currentcube[5] < CUBE_TOP) {
                moveY = CUBE_TOP - currentcube[5];
            }
            endCube[1] = currentcube[1] + moveY;
            endCube[3] = currentcube[3] + moveY;
            endCube[5] = currentcube[5] + moveY;
            endCube[7] = currentcube[7] + moveY;
        }

    }


    /**
     * 开始动画(还原或放大)
     */
    private void startAnim() {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
            //  performSelect();//每次选择完成后,对接口方法里要传递的数据进行设置
        }
        mTask = new MyTimerTask(updateHandlerUp);
        timer.schedule(mTask, 0, 10);
        gpuimage.setCube(currentcube);
        gpuimage.requestRender();
    }

    Handler updateHandlerUp = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (Math.abs(currentcube[0] - endCube[0]) < DEVEL && Math.abs(currentcube[1] - endCube[1]) < DEVEL) {
                currentcube[0] = endCube[0];
                currentcube[1] = endCube[1];
                currentcube[2] = endCube[2];
                currentcube[3] = endCube[3];
                currentcube[4] = endCube[4];
                currentcube[5] = endCube[5];
                currentcube[6] = endCube[6];
                currentcube[7] = endCube[7];
                if (mTask != null) {
                    mTask.cancel();
                    mTask = null;
                    //  performSelect();//每次选择完成后,对接口方法里要传递的数据进行设置
                }
            } else {
                currentcube[0] = (float) ((endCube[0] - currentcube[0]) * MOVE_SPEED + currentcube[0]);
                currentcube[1] = (float) ((endCube[1] - currentcube[1]) * MOVE_SPEED + currentcube[1]);
                currentcube[2] = (float) ((endCube[2] - currentcube[2]) * MOVE_SPEED + currentcube[2]);
                currentcube[3] = (float) ((endCube[3] - currentcube[3]) * MOVE_SPEED + currentcube[3]);
                currentcube[4] = (float) ((endCube[4] - currentcube[4]) * MOVE_SPEED + currentcube[4]);
                currentcube[5] = (float) ((endCube[5] - currentcube[5]) * MOVE_SPEED + currentcube[5]);
                currentcube[6] = (float) ((endCube[6] - currentcube[6]) * MOVE_SPEED + currentcube[6]);
                currentcube[7] = (float) ((endCube[7] - currentcube[7]) * MOVE_SPEED + currentcube[7]);
            }
            gpuimage.setCube(currentcube);
            gpuimage.requestRender();
        }
    };

    class MyTimerTask extends TimerTask {
        Handler handler;

        public MyTimerTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }

    }

}
