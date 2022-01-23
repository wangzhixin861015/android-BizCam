package jp.co.cyberagent.android.gpuimage;

import android.opengl.GLES20;

/**
 * Created by wenbin on 16/10/26.
 */

public class GPUImagePartExposureFilter extends  GPUImageFilter{


    public static final String PART_EXPOSURE_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform sampler2D inputImageTexture2;\n" +
            " uniform highp float exposure;\n" +

            "void main(){\n" +
            "     highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     highp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate);\n" +
            "     if(exposure<-0.5){  \n"+
            "       if(textureColor2.x>0.52) {                   \n" +
            "           gl_FragColor = vec4(textureColor.rgb *pow(2.0,(textureColor2.x-0.52)*2.5) ,textureColor.w);  }     \n" +
            "       if(textureColor2.x<=0.52&&textureColor2.x>=0.48) {                                                                        \n" +
            "           gl_FragColor = vec4(textureColor.x,textureColor.y,textureColor.z ,textureColor.w);   }    \n" +
            "       if(textureColor2.x<0.48) {                                                                            \n"+
            "           gl_FragColor = vec4(textureColor.rgb * pow(2.0, (textureColor2.x-0.48)*2.5),textureColor.w);   } \n"+
            "     }\n"+
            "     if(exposure>0.5){  \n"+
            "       if(textureColor2.x>0.52) {                   \n" +
            "           gl_FragColor = vec4(textureColor.x*(2.0-2.0*textureColor2.x)+254.0/255.0*(textureColor2.x-0.5)*2.0,textureColor.y*(2.0-2.0*textureColor2.x)+177.0/255.0*(textureColor2.x-0.5)*2.0,textureColor.z*(2.0-2.0*textureColor2.x)+133.0/255.0*(textureColor2.x-0.5)*2.0,textureColor.w);   }     \n" +
            "       if(textureColor2.x<=0.52&&textureColor2.x>=0.48) {                                                                        \n" +
            "           gl_FragColor = vec4(textureColor.x,textureColor.y,textureColor.z ,textureColor.w);   }    \n" +
            "       if(textureColor2.x<0.48) {                                                                            \n"+
            "           gl_FragColor = vec4(textureColor.x*(2.0*textureColor2.x)+254.0/255.0*(0.5-textureColor2.x)*2.0,textureColor.y*(2.0*textureColor2.x)+177.0/255.0*(0.5-textureColor2.x)*2.0,textureColor.z*(2.0*textureColor2.x)+133.0/255.0*(0.5-textureColor2.x)*2.0,textureColor.w);   } \n"+
            "     } \n"+
            "     if(exposure>-0.5&&exposure<0.5){  \n"+
            "        gl_FragColor = vec4(textureColor.x,textureColor.y,textureColor.z ,textureColor.w);\n"+
            "     }\n"+

            //   "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate)-texture2D(inputImageTexture2, textureCoordinate);\n" +
            "}";

    private int mGLUniformTexture2;
    private float mPixel;
    private int mPixelLocation;
    private int mExposureLocation;
    private float mExposure;
    public GPUImagePartExposureFilter(final float exposure) {
        super(NO_FILTER_VERTEX_SHADER, PART_EXPOSURE_FRAGMENT_SHADER);
        mExposure = exposure;
    }

    @Override
    public void onInit() {
        super.onInit();
        mGLUniformTexture2 = GLES20.glGetUniformLocation(mGLProgId, "inputImageTexture2");
        mExposureLocation = GLES20.glGetUniformLocation(getProgram(), "exposure");
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
    }

    public void setExposure(final float exposure) {
        mExposure = exposure;
        setFloat(mExposureLocation, mExposure);
    }
}

