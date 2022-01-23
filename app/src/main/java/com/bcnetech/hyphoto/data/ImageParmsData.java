package com.bcnetech.hyphoto.data;

import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * Created by yhf on 2017/11/10.
 */
public class ImageParmsData {


    public List<ImageParm> imageParms;

    public List<ImageParm> getImageParms() {
        return imageParms;
    }

    public void setImageParms(List<ImageParm> imageParms) {
        this.imageParms = imageParms;
    }

    public class ImageParm{

        String imageParmsText;

        Drawable image_down;

        boolean isSelect;

        public ImageParm(Drawable image_down, String imageParmsText,boolean isSelect) {
            this.image_down = image_down;
            this.imageParmsText = imageParmsText;
            this.isSelect=isSelect;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        public Drawable getImage_down() {
            return image_down;
        }

        public void setImage_down(Drawable image_down) {
            this.image_down = image_down;
        }

        public String getImageParmsText() {
            return imageParmsText;
        }

        public void setImageParmsText(String imageParmsText) {
            this.imageParmsText = imageParmsText;
        }
    }




}
