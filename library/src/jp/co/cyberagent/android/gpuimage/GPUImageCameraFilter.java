package jp.co.cyberagent.android.gpuimage;

import android.graphics.PointF;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;

import jp.co.cyberagent.android.gpuimage.reocder.gl_util.GLMatrixState;


/**
 * Created by wenbin on 2017/4/20.
 */

public class GPUImageCameraFilter extends GPUImageFilter {

    public static final String NO_FILTER_VERTEX_SHADER = "" +
            "uniform highp mat4 uMVPMatrix;\n" +
            "attribute highp vec4 position;\n" +
            "attribute highp vec4 inputTextureCoordinate;\n" +
            " \n" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = uMVPMatrix * vec4(position.xy,0,1);\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";
    public static final String NO_FILTER_FRAGMENT_SHADER = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "uniform samplerExternalOES inputImageTexture;\n" +
            "void main()\n" +
            "{\n" +
            "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "}";
    private final LinkedList<Runnable> mRunOnDraw;
    private final String mVertexShader;
    private final String mFragmentShader;
    protected int mGLProgId;
    protected int mGLAttribPosition;
    protected int mGLUniformTexture;
    protected int mGLAttribTextureCoordinate;
    protected int mOutputWidth;
    protected int mOutputHeight;
    private boolean mIsInitialized;
    int muMVPMatrixHandle;
    public GPUImageCameraFilter() {
        this(NO_FILTER_VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);
    }

    public GPUImageCameraFilter(final String vertexShader, final String fragmentShader) {
        mRunOnDraw = new LinkedList<Runnable>();
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
    }

    public void onInit() {
        mGLProgId = OpenGlUtils.loadProgram(mVertexShader, mFragmentShader);
        mGLAttribPosition = GLES20.glGetAttribLocation(mGLProgId, "position");
        mGLUniformTexture = GLES20.glGetUniformLocation(mGLProgId, "inputImageTexture");
        mGLAttribTextureCoordinate = GLES20.glGetAttribLocation(mGLProgId, "inputTextureCoordinate");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mGLProgId, "uMVPMatrix");
        mIsInitialized = true;
    }

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTexCoorBuffer;

    private float[] mVpMatrix;
    public void onInitialized() {
    }


    public void onDestroy() {
        mRunOnDraw.clear();
    }

    public void onOutputSizeChanged(final int width, final int height) {
        mOutputWidth = width;
        mOutputHeight = height;


        if(mVertexBuffer!=null){
            mVertexBuffer.clear();
        }
        float vertices[] = {
                        -2.0f, 2.0f,
                        -2.0f, -2.0f,
                        2.0f, 2.0f,
                        -2.0f, -2.0f,
                        2.0f, -2.0f,
                        2.0f, 2.0f
                };
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);



        if(mTexCoorBuffer!=null){
            mTexCoorBuffer.clear();
        }
        float tempX1 = 0;
        float tempX2 = 1;
        float tempY1 = 0f;
        float tempY2 = 0.975f;
        float texCoor[] = new float[]{
                        tempY1,tempX1,
                        tempY1,tempX2,
                        tempY2,tempX1,
                        tempY1,tempX2,
                        tempY2,tempX2,
                        tempY2,tempX1
                };

        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = cbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoor);
        mTexCoorBuffer.position(0);

        initMatrixM(width,height);
    }

    public void initMatrixM(int width,int height){
        GLMatrixState.setInitStack();
        GLMatrixState.setCamera(
                0,
                0,
                1,
                0,
                0,
                0,
                0,
                1,
                0);
        float ratio=1;
        GLMatrixState.setProjectFrustum(-1, 1, -ratio, ratio, 1, 20);
    }



    @Override
    public void setMatrixM(float[] matrixM) {
        super.setMatrixM(matrixM);
        this.mVpMatrix=matrixM;
    }

    public void onDraw(final int textureId, final int[] textureIds, final FloatBuffer cubeBuffer,
                       final FloatBuffer textureBuffer) {

        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!mIsInitialized) {
            return;
        }
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mVpMatrix, 0);
        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);

        mTexCoorBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 2 * 4, mTexCoorBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);

        if (textureId != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }


        onDrawArraysPre();
        setTexture2(textureIds[0]);
        setTexture3(textureIds[1]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);

    }

    protected void onDrawArraysPre() {
    }

    protected void setTexture2(int textureId2) {
    }

    protected void setTexture3(int textureId3) {
    }

    protected void runPendingOnDrawTasks() {
        while (!mRunOnDraw.isEmpty()) {
            mRunOnDraw.removeFirst().run();
        }
    }

    public boolean isInitialized() {
        return mIsInitialized;
    }

    public int getOutputWidth() {
        return mOutputWidth;
    }

    public int getOutputHeight() {
        return mOutputHeight;
    }

    public int getProgram() {
        return mGLProgId;
    }

    public int getAttribPosition() {
        return mGLAttribPosition;
    }

    public int getAttribTextureCoordinate() {
        return mGLAttribTextureCoordinate;
    }

    public int getUniformTexture() {
        return mGLUniformTexture;
    }

    protected void setInteger(final int location, final int intValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1i(location, intValue);
            }
        });
    }

    protected void setFloat(final int location, final float floatValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1f(location, floatValue);
            }
        });
    }

    protected void setFloatVec2(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec3(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec4(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatArray(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1fv(location, arrayValue.length, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setPoint(final int location, final PointF point) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                float[] vec2 = new float[2];
                vec2[0] = point.x;
                vec2[1] = point.y;
                GLES20.glUniform2fv(location, 1, vec2, 0);
            }
        });
    }

    protected void setUniformMatrix3f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glUniformMatrix3fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void setUniformMatrix4f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glUniformMatrix4fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.addLast(runnable);
        }
    }
}
