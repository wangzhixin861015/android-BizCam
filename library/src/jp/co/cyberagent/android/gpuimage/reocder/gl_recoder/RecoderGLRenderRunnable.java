package jp.co.cyberagent.android.gpuimage.reocder.gl_recoder;

import android.graphics.SurfaceTexture;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.reocder.egl.SohuEGLManager;


/**
 * RenderRunnable
 */
public final class RecoderGLRenderRunnable implements Runnable {

    private static final String TAG = RecoderGLRenderRunnable.class.getSimpleName();

    private final Object mSync = new Object();
    private EGLContext mEGLContext;

    private Object mSurface;
    private int mTexId = -1;
    private int[] texturesId={-1,-1};
    private int width,height;

    // 最终变换矩阵，从glThread中拷贝过来的最终变换矩阵
    private float[] mMatrix = new float[16];

    private boolean mRequestSetEglContext;

    // 是否需要释放资源
    private boolean mRequestRelease;

    // 需要绘制的次数
    private int mRequestDraw;

    private GPUImageFilter gpuImageFilter;
    private FloatBuffer mGLCubeBuffer;
    private FloatBuffer mGLTextureBuffer;

    private int[] textures={0,0};
    /**
     * 创建线程,开启这个Runable
     *
     * @param name
     * @return
     */
    public static final RecoderGLRenderRunnable createHandler(final String name) {

        final RecoderGLRenderRunnable handler = new RecoderGLRenderRunnable();
        synchronized (handler.mSync) {
            new Thread(handler, !TextUtils.isEmpty(name) ? name : TAG).start();
            try {
                handler.mSync.wait();
            } catch (final InterruptedException e) {
            }
        }
        return handler;
    }

    /**
     * 开始录制时，调用该方法,设置一些数据
     *
     * @param eglContext
     * @param texId
     * @param surface
     */
    public final void setEglContext(final EGLContext eglContext, GLSurfaceView glSurfaceView, final int texId, final Object surface) {
        if (!(surface instanceof Surface) && !(surface instanceof SurfaceTexture) && !(surface instanceof SurfaceHolder)) {
            throw new RuntimeException("unsupported window type:" + surface);
        }
        synchronized (mSync) {
            if (mRequestRelease) {
                return;
            }
            mEGLContext = eglContext;
            mTexId = texId;
            mSurface = surface;
            mRequestSetEglContext = true;
            mSync.notifyAll();
            try {
                mSync.wait();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 运行在GLThread
     *
     * @param mvp_matrix
     */
    public final void draw(final float[] mvp_matrix) {
        draw(mTexId, mvp_matrix);
    }



    /**
     * 运行在GLThread
     *
     * @param texId
     * @param mvpMatrix
     */
    public final void draw(final int texId, final float[] mvpMatrix) {
        synchronized (mSync) {
            if (mRequestRelease) {
                return;
            }
            mTexId = texId;
            if (mvpMatrix != null) {
                mMatrix = mvpMatrix.clone();
            } else {
                Matrix.setIdentityM(mMatrix, 0);
            }
            mRequestDraw++;
            mSync.notifyAll();
        }
    }

    public final void draw(boolean start) {
        synchronized (mSync) {
            if (mRequestRelease) {
                return;
            }
            mRequestDraw++;
            mSync.notifyAll();
        }
    }

    /**
     * 释放资源
     */
    public final void release() {
        synchronized (mSync) {
            if (mRequestRelease) {
                return;
            }
            mRequestRelease = true;
            mSync.notifyAll();
            try {
                mSync.wait();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private SohuEGLManager mSohuEgl;

    @Override
    public final void run() {

        synchronized (mSync) {
            mRequestSetEglContext = mRequestRelease = false;
            mRequestDraw = 0;
            mSync.notifyAll();
        }


        boolean localRequestDraw;
        for (; ; ) {
            synchronized (mSync) {
                if (mRequestRelease) {
                    break;
                }
                //
                if (mRequestSetEglContext) {
                    mRequestSetEglContext = false;
                    internalPrepare();
                }
                localRequestDraw = mRequestDraw > 0;
                if (localRequestDraw) {
                    mRequestDraw--;

                }
            }
            if (localRequestDraw) {

                if ((mSohuEgl != null) && mTexId >= 0) {
                    GLES20.glClearColor(0, 0, 0, 0);
                    GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT
                            | GLES20.GL_COLOR_BUFFER_BIT);
                    this.gpuImageFilter.onOutputSizeChanged(width,height);
                    gpuImageFilter.setMatrixM(mMatrix);
                    gpuImageFilter.onDraw(mTexId,texturesId,mGLCubeBuffer,mGLTextureBuffer);
                    mSohuEgl.swapMyEGLBuffers();
                }
            } else {
                synchronized (mSync) {
                    try {
                        mSync.wait();
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }

        }
        synchronized (mSync) {
            mRequestRelease = true;
            releaseEGL();
            mSync.notifyAll();
        }

    }

    private final void internalPrepare() {
        releaseEGL();
        mSohuEgl = new SohuEGLManager(mEGLContext, mSurface);
        mSurface = null;
        mSync.notifyAll();
    }


    public void setFilter(GPUImageFilter gpuImageFilter, FloatBuffer mGLCubeBuffer, FloatBuffer mGLTextureBuffer,int width,int height){
        this.gpuImageFilter=gpuImageFilter;
        this.width = width;
        this.height = height;
        this.gpuImageFilter.init();
        this.gpuImageFilter.onOutputSizeChanged(width,height);
        this.mGLCubeBuffer=mGLCubeBuffer;
        this.mGLTextureBuffer=mGLTextureBuffer;
    }


    private final void releaseEGL() {
        if (mSohuEgl != null) {
            mSohuEgl.release();
            mSohuEgl = null;
        }
    }

}
