package com.bcnetech.hyphoto.data;

import android.graphics.drawable.Drawable;

public class CropListdata {
        Drawable drawable_n;
        Drawable drawable_y;
        Drawable drawableReverse;
        String cropText;
        boolean isSelect;
        boolean isReverse;
        boolean isShowReverse;
    boolean isCanReverse;


        public CropListdata(Drawable drawable_n, Drawable drawable_y,Drawable drawableReverse, String cropText, boolean isSelect, boolean isReverse, boolean isShowReverse,boolean isCanReverse) {
            this.drawable_n = drawable_n;
            this.drawable_y = drawable_y;
            this.drawableReverse = drawableReverse;
            this.cropText = cropText;
            this.isSelect = isSelect;
            this.isReverse = isReverse;
            this.isShowReverse = isShowReverse;
            this.isCanReverse = isCanReverse;
        }

        public Drawable getDrawable_n() {
            return drawable_n;
        }

        public void setDrawable_n(Drawable drawable_n) {
            this.drawable_n = drawable_n;
        }

        public Drawable getDrawable_y() {
            return drawable_y;
        }

        public void setDrawable_y(Drawable drawable_y) {
            this.drawable_y = drawable_y;
        }

        public String getCropText() {
            return cropText;
        }

        public void setCropText(String cropText) {
            this.cropText = cropText;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        public boolean isReverse() {
            return isReverse;
        }

        public void setReverse(boolean reverse) {
            isReverse = reverse;
        }

        public boolean isShowReverse() {
            return isShowReverse;
        }

        public void setShowReverse(boolean showReverse) {
            isShowReverse = showReverse;
        }

        public Drawable getDrawableReverse() {
            return drawableReverse;
        }

        public void setDrawableReverse(Drawable drawableReverse) {
            this.drawableReverse = drawableReverse;
        }

    public boolean isCanReverse() {
        return isCanReverse;
    }

    public void setCanReverse(boolean canReverse) {
        isCanReverse = canReverse;
    }

    @Override
    public String toString() {
        return "CropListdata{" +
                "drawable_n=" + drawable_n +
                ", drawable_y=" + drawable_y +
                ", drawableReverse=" + drawableReverse +
                ", cropText='" + cropText + '\'' +
                ", isSelect=" + isSelect +
                ", isReverse=" + isReverse +
                ", isShowReverse=" + isShowReverse +
                ", isCanReverse=" + isCanReverse +
                '}';
    }
}