package com.bcnetech.hyphoto.data;

import android.graphics.Paint;
import android.graphics.Path;

import java.util.List;

/**
 * Created by yhf on 2017/4/25.
 */

public class PathData {

    private boolean isDo=false;

    private int countBackground=0;
    private int countMain=0;
    private Path path;

    private Paint paint;

    private List<PaintCircle> paintCircle;

    private Paint paint2;

    public class PaintCircle{



        private float currentPaintX;

        private float currentPaintY;

        public float getCurrentPaintX() {
            return currentPaintX;
        }

        public void setCurrentPaintX(float currentPaintX) {
            this.currentPaintX = currentPaintX;
        }

        public float getCurrentPaintY() {
            return currentPaintY;
        }

        public void setCurrentPaintY(float currentPaintY) {
            this.currentPaintY = currentPaintY;
        }


    }

    private float paintSize;

    private int color;

    public Paint getPaint2() {
        return paint2;
    }

    public void setPaint2(Paint paint2) {
        this.paint2 = paint2;
    }

    public PathData() {

    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }



    public float getPaintSize() {
        return paintSize;
    }

    public void setPaintSize(float paintSize) {
        this.paintSize = paintSize;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<PaintCircle> getPaintCircle() {
        return paintCircle;
    }

    public void setPaintCircle(List<PaintCircle> paintCircle) {
        this.paintCircle = paintCircle;
    }

    public boolean isDo() {
        return isDo;
    }

    public void setDo(boolean aDo) {
        isDo = aDo;
    }

    public int getCountBackground() {
        return countBackground;
    }

    public void setCountBackground(int countBackground) {
        this.countBackground = countBackground;
    }

    public int getCountMain() {
        return countMain;
    }

    public void setCountMain(int countMain) {
        this.countMain = countMain;
    }
}
