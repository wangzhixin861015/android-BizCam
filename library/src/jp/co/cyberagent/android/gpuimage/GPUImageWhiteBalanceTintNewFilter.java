/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.cyberagent.android.gpuimage;

import android.opengl.GLES20;

/**
 * Adjusts the white balance of incoming image. <br>
 * <br>
 * temperature: 
 * tint:
 */
public class GPUImageWhiteBalanceTintNewFilter extends GPUImageFilter {
    public static final String WHITE_BALANCE_FRAGMENT_SHADER = "" +
            "uniform sampler2D inputImageTexture;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform lowp float temperature;\n" +
            "uniform lowp float tint;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "	lowp vec4 source = texture2D(inputImageTexture, textureCoordinate);\n" +
            "	\n" +
            "	lowp vec3 rgb = source.rgb * 255.0;\n" +
            "	lowp vec3 adjust = vec3(temperature+tint, -tint, -temperature+tint);\n" +
            "	rgb = adjust + rgb;\n" +
            "   rgb = rgb / 255.0;\n" +
            "\n" +
            "	lowp vec3 processed = vec3(\n" +
            "		 clamp(rgb.r, 0.0, 1.0), //adjusting temperature\n" +
            "		 clamp(rgb.g, 0.0, 1.0), \n" +
            "		 clamp(rgb.b, 0.0, 1.0));\n" +
            "\n" +
            "	gl_FragColor = vec4(processed, source.a);\n" +
            "}";

    private int mTemperatureLocation;
    private float mTemperature;
    private int mTintLocation;
    private float mTint;

    public GPUImageWhiteBalanceTintNewFilter() {
        this(5000.0f, 0.0f);
    }

    public GPUImageWhiteBalanceTintNewFilter(final float temperature, final float tint) {
        super(NO_FILTER_VERTEX_SHADER, WHITE_BALANCE_FRAGMENT_SHADER);
        mTemperature = temperature;
        mTint = tint;
    }

    @Override
    public void onInit() {
        super.onInit();
        mTemperatureLocation = GLES20.glGetUniformLocation(getProgram(), "temperature");
        mTintLocation = GLES20.glGetUniformLocation(getProgram(), "tint");

        if(mTint!=0.0f){
            setFloat(mTintLocation, mTint);
            return;
        }
        setTemperature(mTemperature);
        setTint(mTint);
    }


    public void setTemperature(final float temperature) {
        mTemperature = temperature/3;
//        Log.d("色调 mTemp",mTint+"");
        setFloat(mTemperatureLocation,mTemperature);
    }
    
    public void setTint(final float tint) {
        mTint = tint/3;
//        Log.d("色调 mTint",mTint+"");
        setFloat(mTintLocation, mTint);
    }
}
