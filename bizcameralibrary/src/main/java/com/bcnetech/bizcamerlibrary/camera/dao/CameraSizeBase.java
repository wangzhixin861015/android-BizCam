package com.bcnetech.bizcamerlibrary.camera.dao;

import android.util.Size;

/**
 * Created by a1234 on 2018/7/30.
 */

public abstract class CameraSizeBase {
     Size sizeLarge;
     Size sizeMiddle;
     Size sizeSmall;

     public Size getSizeLarge() {
          return sizeLarge;
     }

     public void setSizeLarge(Size sizeLarge) {
          this.sizeLarge = sizeLarge;
     }

     public Size getSizeMiddle() {
          return sizeMiddle;
     }

     public void setSizeMiddle(Size sizeMiddle) {
          this.sizeMiddle = sizeMiddle;
     }

     public Size getSizeSmall() {
          return sizeSmall;
     }

     public void setSizeSmall(Size sizeSmall) {
          this.sizeSmall = sizeSmall;
     }
}
