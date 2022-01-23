package jp.co.cyberagent.android.gpuimage;

import android.opengl.GLES20;

/**
 * Created by wenbin on 16/10/27.
 */

public class GPUImagePartSaturationFilter extends GPUImageFilter{

    public static final String SATURATION_FRAGMENT_SHADER = "" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " uniform highp float exposure;\n" +
            " \n" +
            " // Values from \"Graphics Shaders: Theory and Practice\" by Bailey and Cunningham\n" +
            " const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "    lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate);\n" +
            "    lowp float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
            "    lowp vec3 greyScaleColor = vec3(luminance);\n" +
            "    \n" +
            "    if(exposure<-0.5){  \n"+
            "          gl_FragColor = vec4(mix(greyScaleColor, textureColor.rgb, 2.0*textureColor2.x), textureColor.w);\n" +
            "    }\n" +
            "    if(exposure>0.5){   \n"+
            "       if(textureColor2.x>0.52) {                   \n" +
            "           gl_FragColor = vec4(textureColor.x*(2.0-2.0*textureColor2.x)+159.0/255.0*(textureColor2.x-0.5)*2.0,textureColor.y*(2.0-2.0*textureColor2.x)+200.0/255.0*(textureColor2.x-0.5)*2.0,textureColor.z*(2.0-2.0*textureColor2.x)+248.0/255.0*(textureColor2.x-0.5)*2.0,textureColor.w);   }     \n" +
            "       if(textureColor2.x<=0.52&&textureColor2.x>=0.48) {                                                                        \n" +
            "           gl_FragColor = vec4(textureColor.x,textureColor.y,textureColor.z ,textureColor.w);   }    \n" +
            "       if(textureColor2.x<0.48) {                                                                            \n"+
            "           gl_FragColor = vec4(textureColor.x*(2.0*textureColor2.x)+159.0/255.0*(0.5-textureColor2.x)*2.0,textureColor.y*(2.0*textureColor2.x)+200.0/255.0*(0.5-textureColor2.x)*2.0,textureColor.z*(2.0*textureColor2.x)+248.0/255.0*(0.5-textureColor2.x)*2.0,textureColor.w);   } \n"+
            "    } \n"+

            "    if(exposure>-0.5&&exposure<0.5){\n"+
            "        gl_FragColor = vec4(textureColor.x,textureColor.y,textureColor.z ,textureColor.w);\n"+
            "    }\n"+

            " }";

    private int mGLUniformTexture2;
    private int mExposureLocation;
    private float mExposure;

    public GPUImagePartSaturationFilter(final float exposure) {
        super(NO_FILTER_VERTEX_SHADER, SATURATION_FRAGMENT_SHADER);
        mExposure = exposure;
    }

    @Override
    public void onInit() {
        super.onInit();
        mGLUniformTexture2 = GLES20.glGetUniformLocation(mGLProgId, "inputImageTexture2");
        mExposureLocation = GLES20.glGetUniformLocation(getProgram(), "exposure");
    }

    @Override
    protected void setTexture3(int textureId3) {
        super.setTexture3(textureId3);
        if(textureId3!= OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId3);
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
