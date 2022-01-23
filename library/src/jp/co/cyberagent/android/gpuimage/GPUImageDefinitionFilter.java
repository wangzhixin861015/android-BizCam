package jp.co.cyberagent.android.gpuimage;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by wenbin on 2016/12/2.
 */

public class GPUImageDefinitionFilter extends GPUImageFilter {
    public static final String VERTEX_SHADER =
            "attribute vec4 position;\n" +
                    "attribute vec4 inputTextureCoordinate;\n" +
                    "\n" +
                    "const int GAUSSIAN_SAMPLES = 9;\n" +
                    "\n" +
                    "uniform float texelWidthOffset;\n" +
                    "uniform float texelHeightOffset;\n" +
                    "\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "varying vec2 blurCoordinates[GAUSSIAN_SAMPLES];\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "	gl_Position = position;\n" +
                    "	textureCoordinate = inputTextureCoordinate.xy;\n" +
                    "	\n" +
                    "	// Calculate the positions for the blur\n" +
                    "	int multiplier = 0;\n" +
                    "	vec2 blurStep;\n" +
                    "   vec2 singleStepOffset = vec2(texelHeightOffset, texelWidthOffset);\n" +
                    "    \n" +
                    "	for (int i = 0; i < GAUSSIAN_SAMPLES; i++)\n" +
                    "   {\n" +
                    "		multiplier = (i - ((GAUSSIAN_SAMPLES - 1) / 2));\n" +
                    "       // Blur in x (horizontal)\n" +
                    "       blurStep = float(multiplier) * singleStepOffset;\n" +
                    "		blurCoordinates[i] = inputTextureCoordinate.xy + blurStep;\n" +
                    "	}\n" +
                    "}\n";

    public static final String FRAGMENT_SHADER =
            "uniform sampler2D inputImageTexture;\n" +
                    "\n" +
                    "const lowp int GAUSSIAN_SAMPLES = 9;\n" +
                    "\n" +
                    " uniform highp float exposure;\n" +
                    "varying highp vec2 textureCoordinate;\n" +
                    "varying highp vec2 blurCoordinates[GAUSSIAN_SAMPLES];\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "	highp vec3 sum = vec3(0.0);\n" +
                    "   highp vec4 fragColor=texture2D(inputImageTexture,textureCoordinate);\n" +

                    "	\n" +
                    "    sum += texture2D(inputImageTexture, blurCoordinates[0]).rgb * 0.05;\n" +
                    "    sum += texture2D(inputImageTexture, blurCoordinates[1]).rgb * 0.09;\n" +
                    "    sum += texture2D(inputImageTexture, blurCoordinates[2]).rgb * 0.12;\n" +
                    "    sum += texture2D(inputImageTexture, blurCoordinates[3]).rgb * 0.15;\n" +
                    "    sum += texture2D(inputImageTexture, blurCoordinates[4]).rgb * 0.18;\n" +
                    "    sum += texture2D(inputImageTexture, blurCoordinates[5]).rgb * 0.15;\n" +
                    "    sum += texture2D(inputImageTexture, blurCoordinates[6]).rgb * 0.12;\n" +
                    "    sum += texture2D(inputImageTexture, blurCoordinates[7]).rgb * 0.09;\n" +
                    "    sum += texture2D(inputImageTexture, blurCoordinates[8]).rgb * 0.05;\n" +
                    "   if(abs(fragColor.x-sum.x)>(1.0/255.0)){\n" +
                    "       sum.x= fragColor.x*(1.0+exposure)+sum.x*(-exposure); \n" +
                    "   }else{\n" +
                    "       sum.x=fragColor.x;\n" +
                    "   }\n" +
                    "   if(abs(fragColor.y-sum.y)>(1.0/255.0)){\n" +
                    "       sum.y=  fragColor.y*(1.0+exposure)+sum.y*(-exposure); \n" +
                    "   }else{\n" +
                    "       sum.y=fragColor.y;}\n" +
                    "   if(abs(fragColor.z-sum.z)>(1.0/255.0)){\n" +
                    "       sum.z=  fragColor.z*(1.0+exposure)+sum.z*(-exposure); \n" +
                    "   }else{\n" +
                    "       sum.z=fragColor.z;\n" +
                    "   }\n" +
                    "   \n" +
                    "	gl_FragColor = vec4(sum,fragColor.a);\n" +

                    "}";

    protected float mBlurSize = 1.6f;

    private int mExposureLocation;
    private float mExposure;
    private int texelWidthOffsetLocation;
    private float mTexelWidthOffsetLocation;
    private int texelHeightOffsetLocation;
    private float mTexelHeightOffsetLocation;


    public GPUImageDefinitionFilter() {
        this(1.0f);
    }

    public GPUImageDefinitionFilter(final float exposure) {
        super(VERTEX_SHADER, FRAGMENT_SHADER);
        mExposure = exposure;
        mTexelWidthOffsetLocation=getHorizontalTexelOffsetRatio() / mOutputWidth;
        mTexelHeightOffsetLocation= getVerticalTexelOffsetRatio() / mOutputHeight;
    }

    @Override
    public void onInit() {
        super.onInit();
        mExposureLocation = GLES20.glGetUniformLocation(getProgram(), "exposure");
        texelWidthOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "texelWidthOffset");
        texelHeightOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "texelHeightOffset");

    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setExposure(mExposure);
    }

    @Override
    public void onOutputSizeChanged(int width, int height) {
        super.onOutputSizeChanged(width, height);
        setExposure(mExposure);
    }

    public void setExposure(final float exposure) {
        mExposure = exposure;
        setFloat(mExposureLocation, mExposure);
        setmTexelWidthOffsetLocation();
        setmTexelHeightOffsetLocation();
    }

    public void setmBlurSize(final float mBlurSize) {
        this.mBlurSize = mBlurSize;
        setmTexelWidthOffsetLocation();
        setmTexelHeightOffsetLocation();
    }


    public void setmTexelWidthOffsetLocation() {
        Log.d("eBiz","setmTexelWidthOffsetLocation"+mOutputWidth);
        mTexelWidthOffsetLocation=getHorizontalTexelOffsetRatio() / mOutputWidth;
        setFloat(texelWidthOffsetLocation, mTexelWidthOffsetLocation);
    }


    public void setmTexelHeightOffsetLocation() {
        mTexelHeightOffsetLocation= getVerticalTexelOffsetRatio() / mOutputHeight;
        setFloat(texelHeightOffsetLocation, mTexelHeightOffsetLocation);
    }

    public float getVerticalTexelOffsetRatio() {
        return mBlurSize;
    }

    public float getHorizontalTexelOffsetRatio() {
        return mBlurSize;
    }



/*    public GPUImageDefinitionFilter() {
        this(1f);
    }

    public GPUImageDefinitionFilter(float blurSize) {
        super(VERTEX_SHADER, FRAGMENT_SHADER, VERTEX_SHADER, FRAGMENT_SHADER);
        mBlurSize = blurSize;
    }

    @Override
    public float getVerticalTexelOffsetRatio() {
        return mBlurSize;
    }

    @Override
    public float getHorizontalTexelOffsetRatio() {
        return mBlurSize;
    }

    *//**
     * A multiplier for the blur size, ranging from 0.0 on up, with a default of 1.0
     *
     * @param blurSize from 0.0 on up, default 1.0
     *//*
    */
}
