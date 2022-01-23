package com.bcnetech.bcnetchhttp.bean.data;

/**
     * imagePath = "0-2017-1-10_102307-784.jpg";
     * type = 1000;
     */
   public class SharePartParms {
        String imagePath;
        int type;
        String imageData;

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    @Override
    public String toString() {
        return "SharePartParms{" +
                "imagePath='" + imagePath + '\'' +
                ", type=" + type +
                ", imageData=" + imageData +
                '}';
    }
}