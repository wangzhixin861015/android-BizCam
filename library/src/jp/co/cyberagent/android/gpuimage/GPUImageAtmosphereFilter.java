package jp.co.cyberagent.android.gpuimage;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by wenbin on 2016/11/21.
 */

public class GPUImageAtmosphereFilter extends GPUImageFilter{

    public static final String EXPOSURE_FRAGMENT_SHADER = "precision highp float;\n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform highp float exposure;\n" +
            " uniform highp float exposure2;\n" +
            " uniform highp float cricleX;\n" +
            " uniform highp float cricleY;\n" +
            " uniform highp float cricleR;\n" +
            " uniform highp float cricleWithe;\n" +
            " uniform highp float cricleHeight;\n" +
            " uniform highp float cricleBlurR;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     float inR= sqrt((textureCoordinate.x-cricleX)*(textureCoordinate.x-cricleX)*cricleWithe*cricleWithe+(textureCoordinate.y-cricleY)*(textureCoordinate.y-cricleY)*cricleHeight*cricleHeight); \n"+
            "     float otherR=inR-cricleR;\n"+
            "     float otherR2=otherR-cricleBlurR;\n"+
            "     float color0=exposure;\n" +
            "     float color5=exposure2;\n"+
            "     float color1=color0-(color0-color5)*0.2;\n" +
            "     float color1_5=color0-(color0-color1)*(inR/cricleR);\n" +
            "     float color3=color5+(color0-color5)*0.1;\n"+
            "     float color2=color1-(color1-color3)*(otherR/cricleBlurR);\n"+
            "     float color4=color3-(color3-color5)*(otherR2/cricleBlurR);\n"+
            "     if(inR<cricleR){\n"+
            "       if(textureColor.w<0.1){" +
            "           gl_FragColor = vec4(0.0,0.0,0.0,0.0);" +
            "       }else{\n"+
            "           gl_FragColor = vec4(textureColor.x +color1_5,textureColor.y +color1_5,textureColor.z +color1_5,textureColor.w);" +
            "       }\n" +
            "     }else{\n"+
            "       if(otherR<cricleBlurR){\n" +
            "           if(textureColor.w<0.1){" +
            "               gl_FragColor = vec4(0.0,0.0,0.0,0.0);" +
            "           }else{\n"+
            "             gl_FragColor = vec4(textureColor.x+color2,textureColor.y+color2,textureColor.z+color2,textureColor.w);" +
            "           }\n" +
            "       }else{\n" +
            "           if(otherR2<cricleBlurR){\n" +
            "               if(textureColor.w<0.1){" +
            "                   gl_FragColor = vec4(0.0,0.0,0.0,0.0);" +
            "               }else{\n"+
            "                   gl_FragColor = vec4(textureColor.x+color4,textureColor.y+color4,textureColor.z+color4,textureColor.w);" +
            "               }\n" +
            "           }else{\n"+
            "               if(textureColor.w<0.1){" +
            "                   gl_FragColor = vec4(0.0,0.0,0.0,0.0);" +
            "               }else{\n"+
            "                   gl_FragColor = vec4(textureColor.x +color5,textureColor.y +color5,textureColor.z +color5,textureColor.w);" +
            "               }\n" +
            "           }\n" +
            "       }\n" +
            "     }\n"+
            " } ";
    private int mExposureLocation;
    private float mExposure;
    private int mExposureLocation2;
    private float mExposure2;
    private int mCricleXLocation;
    private float mCricleX;
    private int mCricleYLocation;
    private float mCricleY;
    private int mCricleRLocation;
    private float mCricleR;
    private int mCricleWitheLocation;
    private float mCricleWithe;
    private int mCricleHeightLocation;
    private float mCricleHeight;
    private int mCricleBlurRLocation;
    private float mCricleBlurR;

    public GPUImageAtmosphereFilter() {

        this(0.5f,0.5f,150.0f,150.0f,50.0f,50.0f,50.0f,50.f);
    }

    public GPUImageAtmosphereFilter(final float exposure
            ,final float exposure2
            ,final float cricleX
            ,final float cricleY
            ,final float cricleR
            ,final float cricleWithe
            ,final float cricleHeight
            ,final float cricleBlurR) {
        super(NO_FILTER_VERTEX_SHADER, EXPOSURE_FRAGMENT_SHADER);
        mExposure = exposure;
        mExposure2= exposure2;
        mCricleX = cricleX;
        mCricleY = cricleY;
        mCricleR = cricleR;
        mCricleWithe = cricleWithe;
        mCricleHeight = cricleHeight;
        mCricleBlurR =cricleBlurR;
    }

    @Override
    public void onInit() {
        super.onInit();
        mExposureLocation = GLES20.glGetUniformLocation(getProgram(), "exposure");
        mExposureLocation2 = GLES20.glGetUniformLocation(getProgram(), "exposure2");
        mCricleXLocation = GLES20.glGetUniformLocation(getProgram(), "cricleX");
        mCricleYLocation = GLES20.glGetUniformLocation(getProgram(), "cricleY");
        mCricleRLocation = GLES20.glGetUniformLocation(getProgram(), "cricleR");
        mCricleWitheLocation = GLES20.glGetUniformLocation(getProgram(), "cricleWithe");
        mCricleHeightLocation = GLES20.glGetUniformLocation(getProgram(), "cricleHeight");
        mCricleBlurRLocation = GLES20.glGetUniformLocation(getProgram(), "cricleBlurR");
    }



    @Override
    public void onInitialized() {
        super.onInitialized();
        setExposure(mExposure);
        setExposure2(mExposure2);
        setmCricleR(mCricleR);
        setmCricleWithe(mCricleWithe);
        setmCricleHeight(mCricleHeight);
        setmCricleBlurR(mCricleBlurR);
        setmCricleX(mCricleX);
        setmCricleY(mCricleY);
    }



    public void setExposure(final float exposure) {

        mExposure = exposure;
        setFloat(mExposureLocation, mExposure);
    }

    public void setExposure2(float exposure){
        mExposure2=exposure;
        setFloat(mExposureLocation2, mExposure2);
    }

    public void setmCricleX(float mCricleX) {
        this.mCricleX = mCricleX;
        setFloat(mCricleXLocation, mCricleX/mCricleWithe);
    }


    public void setmCricleY(float mCricleY) {
        this.mCricleY = mCricleY;
        setFloat(mCricleYLocation, mCricleY/mCricleHeight);
    }

    public void setmCricleR(float mCricleR) {
        this.mCricleR = mCricleR;
        setFloat(mCricleRLocation, mCricleR);
    }

    public void setmCricleWithe(float mCricleWithe) {
        this.mCricleWithe = mCricleWithe;
        setFloat(mCricleWitheLocation, mCricleWithe);
    }

    public void setmCricleHeight(float mCricleHeight) {
        this.mCricleHeight = mCricleHeight;
        setFloat(mCricleHeightLocation, mCricleHeight);
    }


    public void setmCricleBlurR(float mCricleBlurR) {
        this.mCricleBlurR = mCricleBlurR;
        setFloat(mCricleBlurRLocation, mCricleBlurR);
    }

    public void show(){
        Log.d("eBiz",mExposure+"  "+mExposure2+" "+mCricleX+"  "+mCricleY+"  "+mCricleR+"  "+mCricleWithe+
                "  "+mCricleHeight+ "  "+mCricleBlurR);
    }
}
