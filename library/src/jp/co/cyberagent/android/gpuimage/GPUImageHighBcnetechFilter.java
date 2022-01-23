package jp.co.cyberagent.android.gpuimage;

import android.opengl.GLES20;

/**
 * Created by wenbin on 2017/4/13.
 */

public class GPUImageHighBcnetechFilter extends GPUImageFilter {
    public static final String HIGHLIGHT_SHADOW_FRAGMENT_SHADER = "" +
            "varying lowp vec2 textureCoordinate;\n" +

            "uniform sampler2D inputImageTexture;\n" +

            "uniform lowp float shadows;\n" +
            "uniform lowp float highlights;\n" +
            "const mediump vec3 luminanceWeighting = vec3(0.3, 0.3, 0.3);\n" +

            "void main(){\n" +

            "lowp vec4 base = texture2D(inputImageTexture, textureCoordinate);\n" +
            "lowp vec4 dest;\n" +
            "mediump float num13 = 1.0;\n" +
            "mediump float num14 = 1.0;\n" +
            "mediump float num15 = 1.0;\n" +
            "mediump float num16 = 5.0;\n" +
            "mediump float num17 = highlights;\n" +
            "mediump float num18 = 40.0 * 1.1;\n" +
            "mediump float num19 = 80.0 * 1.0;\n" +
            "mediump float num20 = shadows;\n" +
            "mediump float num21 = 0.0;\n" +
            "mediump float num22 = 0.9;\n" +
            "mediump float num23 = 0.75;\n" +

            "mediump float r = base.r * 255.0;\n" +
            "mediump float g = base.g * 255.0;\n" +
            "mediump float b = base.b * 255.0;\n" +
            "mediump float num36 = dot(base.rgb, luminanceWeighting) * 255.0;\n" +
            "mediump float num33 = base.r * 255.0;\n" +
            "mediump float num34 = base.g * 255.0;\n" +
            "mediump float num35 = base.b * 255.0;\n" +
            "mediump float a = base.w;\n" +

            "mediump float num37 = dot(base.rgb, luminanceWeighting) * 255.0;\n" +
            "mediump float num82 = num36 - num37;\n" +
            "mediump float num83 = num82 * num23;\n" +
            "mediump float num84 = num37 + num83;\n" +
            "mediump float num38 = num36;\n" +

            "mediump float num44 = (r * num13) / num38;\n" +
            "mediump float num45 = (g * num14) / num38;\n" +
            "mediump float num46 = (b * num15) / num38;\n" +
            "mediump float num47 = ((num44 + num45) + num46) / 3.0;\n" +
            "mediump float num57 = 1.0;\n" +
            "mediump float num86 = 0.0;\n" +


            "if(shadows < 0.0){\n" +
            "num86 = 0.99;//其大小 控制 图片shadows 变暗的程度，shadows 负值调整区间使用\n" +
            "}\n" +
            "if (shadows >=0.0) {\n" +
            "num86 = 1.0;\n" +
            "}\n" +

            "mediump float num74 = 1.0 + (((100.0 - num18) * 2.0) / 100.0);\n" +
            "mediump float num75 = (50.0 / (num84 + 10.0)) - (0.3 * num74);\n" +

            "if (num75 < 0.0) {\n" +
            //num75 = -num75;
            "num75 = 0.0;\n" +
            "}\n" +

            "mediump float num76 = min((num16 + 1.0) / 3.0, num75);\n" +
            "mediump float num77 = 1.0 + ((num76 * num47) * num20);\n" +
            "mediump float num58 = (num86 * num77) + ((num57 + 1.0) * (1.0 - num86));\n" +
            "mediump float num59 = num33 + (num37 * (num58 - 1.0));\n" +
            "mediump float num60 = (num33 * num58) - num59;\n" +
            "mediump float num61 = num59 + (num60 * num22);\n" +
            "mediump float num62 = num33 + (((((num33 - num37) * num22) * num76) * num47) * (num58 - 1.0));\n" +
            "mediump float num63 = ((num61 * num86) + (num62 * (1.0 - num86))) / num33;\n" +
            "mediump float num64 = num34 + (num37 * (num58 - 1.0));\n" +
            "mediump float num65 = (num34 * num58) - num64;\n" +
            "mediump float num66 = num64 + (num65 * num22);\n" +
            "mediump float num67 = num34 + (((((num34 - num37) * num22) * num76) * num47) * (num58 - 1.0));\n" +
            "mediump float num68 = ((num66 * num86) + (num67 * (1.0 - num86))) / num34;\n" +
            "mediump float num69 = num35 + (num37 * (num58 - 1.0));\n" +
            "mediump float num70 = (num35 * num58) - num69;\n" +
            "mediump float num71 = num69 + (num70 * num22);\n" +
            "mediump float num72 = num35 + (((((num35 - num37) * num22) * num76) * num47) * (num58 - 1.0));\n" +
            "mediump float num73 = ((num71 * num86) + (num72 * (1.0 - num86))) / num35;\n" +
            "mediump float num43 = 255.0 - num37;\n" +
            "mediump float num79 = 1.0 + (((100.0 - num19) * 2.0) / 100.0);\n" +
            "mediump float num78 = (50.0 / ((255.0 - num84) + 10.0)) - (0.3 * num79);\n" +

            "if (num78 < 0.0) {\n" +

            "if(highlights < 0.0) {\n" +
            "num78 = -num78 / 10.0;//分母 大小控制 highlights 变亮的程度，highlights 负值调整区间使用\n" +
            "}\n" +
            "if(highlights >= 0.0){\n" +

            "num78 = 0.0;\n" +
            "}\n" +
            "}\n" +

            "mediump float num80 = min((num16 + 1.0) / 3.0, num78);\n" +
            "mediump float num81 = 1.0 + (((num80 * num57) * num47) * num17);\n" +
            "mediump float num42 = (num43 * num81) - num43;\n" +
            "mediump float num48 = num37 - ((((num37 - (num33 * num63)) * num21) * num80) * num47);\n" +
            "mediump float num49 = (num42 + num37) - num48;\n" +
            "mediump float num50 = 1.0 - (num49 / num48);\n" +
            "mediump float num51 = num37 - ((((num37 - (num34 * num68)) * num21) * num80) * num47);\n" +
            "mediump float num52 = (num42 + num37) - num51;\n" +
            "mediump float num53 = 1.0 - (num52 / num51);\n" +
            "mediump float num54 = num37 - ((((num37 - (num35 * num73)) * num21) * num80) * num47);\n" +
            "mediump float num55 = (num42 + num37) - num54;\n" +
            "mediump float num56 = 1.0 - (num55 / num54);\n" +
            "mediump float num39 = (num33 * num63) * num50;\n" +
            "mediump float num41 = (num34 * num68) * num53;\n" +
            "mediump float num40 = (num35 * num73) * num56;\n" +

            "lowp float num27 = clamp(num39/255.0, 0.0, 1.0);\n" +
            "lowp float num29 = clamp(num41/255.0, 0.0, 1.0);\n" +
            "lowp float num30 = clamp(num40/255.0, 0.0, 1.0);\n" +

            "dest = vec4(num27, num29, num30, a);\n" +

            "if(shadows == 0.0 && highlights == 0.0){\n" +
            "dest = vec4(base.rgb, a);\n" +
            "}\n" +
            "gl_FragColor = dest;\n" +
            "}\n";

    private int mShadowsLocation;
    private float mShadows;
    private int mHighlightsLocation;
    private float mHighlights;

    public GPUImageHighBcnetechFilter() {
        this(0.0f, 0.0f);
    }

    public GPUImageHighBcnetechFilter(final float shadows, final float highlights) {
        super(NO_FILTER_VERTEX_SHADER, HIGHLIGHT_SHADOW_FRAGMENT_SHADER);
        mHighlights = highlights;
        mShadows = shadows;
    }

    @Override
    public void onInit() {
        super.onInit();
        mHighlightsLocation = GLES20.glGetUniformLocation(getProgram(), "highlights");
        mShadowsLocation = GLES20.glGetUniformLocation(getProgram(), "shadows");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setHighlights(mHighlights);
        setShadows(mShadows);
    }

    public void setHighlights(final float highlights) {
        mHighlights = highlights;
        setFloat(mHighlightsLocation, mHighlights*-1);
    }

    public void setShadows(final float shadows) {
        mShadows = shadows;
        setFloat(mShadowsLocation, mShadows);
    }
}