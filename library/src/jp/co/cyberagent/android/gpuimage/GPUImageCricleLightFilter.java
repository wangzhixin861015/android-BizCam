package jp.co.cyberagent.android.gpuimage;

import android.opengl.GLES20;

/**
 * Created by wenbin on 2016/11/15.
 */

public class GPUImageCricleLightFilter extends GPUImageFilter {

    public static final String EXPOSURE_FRAGMENT_SHADER = "" +
            " varying highp vec2 textureCoordinate;\n" +

            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " uniform highp float exposure;\n" +
            " uniform highp float exposure2;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     highp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate);\n" +
            "     if(textureCoordinate.x<0.1&&textureCoordinate.y<0.1){\n"+
            "       gl_FragColor = vec4(1.0,0.0,0.0,textureColor.w);\n" +
            "     }\n"+
            "     \n"+
            "     \n"+
            "     \n"+
            "     \n"+
            "     else{\n"+
            "       if(textureColor2.x<0.01) {  \n" +
            "           gl_FragColor = vec4(textureColor.x +(exposure2/2.0),textureColor.y +(exposure2/2.0),textureColor.z +(exposure2/2.0),textureColor.w);\n" +
            "       }else{\n" +
            "           gl_FragColor = vec4(textureColor.x+((exposure-exposure2)/2.0*(textureColor2.x-0.01)+exposure2/2.0),textureColor.y+((exposure-exposure2)/2.0*(textureColor2.x-0.01)+exposure2/2.0),textureColor.z+((exposure-exposure2)/2.0*(textureColor2.x-0.01)+exposure2/2.0) ,textureColor.w);\n" +
            "       }\n" +
            "     }\n" +
            " } ";
    private int mGLUniformTexture2;
    private int mExposureLocation;
    private float mExposure;
    private int mExposureLocation2;
    private float mExposure2;

    public GPUImageCricleLightFilter() {

        this(1.0f,1.0f);
    }

    public GPUImageCricleLightFilter(final float exposure,final float exposure2) {
        super(NO_FILTER_VERTEX_SHADER, EXPOSURE_FRAGMENT_SHADER);
        mExposure = exposure;
        mExposure2= exposure2;

    }

    @Override
    public void onInit() {
        super.onInit();
        mGLUniformTexture2 = GLES20.glGetUniformLocation(mGLProgId, "inputImageTexture2");
        mExposureLocation = GLES20.glGetUniformLocation(getProgram(), "exposure");
        mExposureLocation2 = GLES20.glGetUniformLocation(getProgram(), "exposure2");
    }

    @Override
    protected void setTexture2(int textureId2) {
        super.setTexture2(textureId2);
        if(textureId2!= OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId2);
            GLES20.glUniform1i(mGLUniformTexture2, 1);
        }
    }



    @Override
    public void onInitialized() {
        super.onInitialized();
        setExposure(mExposure);
        setExposure2(mExposure2);
    }

    /**
     * 设置内部亮度
     * @param exposure
     */
    public void setExposure(final float exposure) {
        mExposure = exposure;
        setFloat(mExposureLocation, mExposure);
    }

    /**
     * 设置外部亮度
     * @param exposure
     */
    public void setExposure2(float exposure){
        mExposure2=exposure;
        setFloat(mExposureLocation2, mExposure2);
    }
}
