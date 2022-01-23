package com.bcnetech.bizcamerlibrary.camera;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class OrientationSensorListener implements SensorEventListener {
	 private static final int _DATA_X = 0;
     private static final int _DATA_Y = 1;
     private static final int _DATA_Z = 2;
     
     public static final int ORIENTATION_UNKNOWN = -1;
     
     private Handler rotateHandler;
     
     
     
	public OrientationSensorListener(Handler handler) {
		rotateHandler = handler;
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ORIENTATION:

                //方位角（z轴） 该值表示方位，也就是手机绕着Z轴旋转的角度。
                // 0表示北（North）；90表示东（East）；180表示南（South）；270表示西（West）。
                float zAngle=values[0];

                //仰俯角（x轴）该值表示倾斜度，或手机翘起的程度。
                // 当手机绕着X轴倾斜时该值发生变化。values[1]的取值范围是-180≤values[1]≤180。
                float xAngle=values[1];

                //翻转角（y轴）表示手机沿着Y轴的滚动角度。取值范围是-90≤values[2]≤90
                float yAngle=-values[2];

                if (rotateHandler!=null) {
                    Message message=new Message();
                    Bundle bundle=new Bundle();
                    message.setData(bundle);
                    bundle.putFloat("xAngle",xAngle);
                    bundle.putFloat("yAngle",yAngle);
                    bundle.putFloat("zAngle",zAngle);
                    message.what=999;
                    rotateHandler.sendMessage(message);
                }
                break;
            case Sensor.TYPE_ACCELEROMETER:
                int orientation = ORIENTATION_UNKNOWN;
                float X = -values[_DATA_X];
                float Y = -values[_DATA_Y];
                float Z = -values[_DATA_Z];
                Message message=new Message();
                Bundle bundle=new Bundle();
                message.setData(bundle);
                bundle.putFloat("xAngle",X);
                bundle.putFloat("yAngle",Y);
                bundle.putFloat("zAngle",Z);
                message.what=777;
                rotateHandler.sendMessage(message);
                float magnitude = X * X + Y * Y;
                // Don't trust the angle if the magnitude is small compared to the y value
                if (magnitude * 4 >= Z * Z) {
                    float OneEightyOverPi = 57.29577957855f;
                    float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                    orientation = 90 - (int) Math.round(angle);
                    // normalize to 0 - 359 range
                    while (orientation >= 360) {
                        orientation -= 360;
                    }
                    while (orientation < 0) {
                        orientation += 360;
                    }
                }

                if (rotateHandler!=null) {
                    rotateHandler.obtainMessage(888, orientation, 0).sendToTarget();
                }
                break;
        }



	}

}
