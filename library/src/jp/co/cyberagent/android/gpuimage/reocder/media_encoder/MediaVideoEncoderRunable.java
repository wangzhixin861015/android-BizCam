package jp.co.cyberagent.android.gpuimage.reocder.media_encoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.opengl.EGLContext;
import android.opengl.GLSurfaceView;
import android.view.Surface;

import java.io.IOException;
import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.reocder.gl_recoder.RecoderGLRenderRunnable;
import jp.co.cyberagent.android.gpuimage.reocder.media_muxer.SohuMediaMuxerManager;

public class MediaVideoEncoderRunable extends BaseMediaEncoderRunable {

    private static final String TAG = MediaVideoEncoderRunable.class.getSimpleName();

    private static final String MIME_TYPE = "video/avc";

    // FPS 帧率
    private static final int FRAME_RATE = 30;
    private static final float BPP = 0.25f;

    private final int mWidth;
    private final int mHeight;
    private int videoWidth,videoHeight;
    private RecoderGLRenderRunnable mRenderRunnable;

    private Surface mSurface;
    private GPUImageFilter gpuImageFilter;
    /**
     * 构造方法,父类中，开启了该线程
     *
     * @param mediaMuxerManager
     * @param mediaEncoderListener
     * @param width
     * @param height
     */
    public MediaVideoEncoderRunable(final SohuMediaMuxerManager mediaMuxerManager, final MediaEncoderListener mediaEncoderListener, final int width, final int height,GPUImageFilter gpuImageFilter) {
        super(mediaMuxerManager, mediaEncoderListener);
        mWidth = width;
        mHeight = height;
        this.gpuImageFilter=gpuImageFilter;
        mRenderRunnable = RecoderGLRenderRunnable.createHandler(TAG);
    }

    /**
     * 运行在GLThread
     *
     * @param mvp_matrix
     * @return
     */
    public boolean frameAvailableSoon(final float[] mvp_matrix) {
        boolean result;
        if (result = super.frameAvailableSoon()) {
            mRenderRunnable.draw(mvp_matrix);
        }
        return result;
    }


    /**
     * 运行在GLThread
     *
     * @return
     */
    public boolean frameAvailableSoon(boolean start) {
        boolean result;
        if (result = super.frameAvailableSoon()) {
            mRenderRunnable.draw(start);
        }
        return result;
    }

    /**
     * 开始录制前的准备(目前由SohuMediaMuxerManager在主线程调用)
     *
     * @throws IOException
     */
    @Override
    public void prepare() throws IOException {
        mTrackIndex = -1;
        mMuxerStarted = mIsEndOfStream = false;
        if (videoWidth==0||videoHeight==0){
            videoWidth = mWidth;
            videoHeight = mHeight;
        }
        final MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, videoWidth, videoHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, calcBitRate());
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1000/FRAME_RATE);
        mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
        mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSurface = mMediaCodec.createInputSurface();
        mMediaCodec.start();
        if (mMediaEncoderListener != null) {
            try {
                mMediaEncoderListener.onPrepared(this);
            } catch (final Exception e) {
            }
        }
    }

    //设置预览分辨率和拍照分辨率
    public void setMPreViewSize(int width,int height) {
        this.videoWidth = width;
        this.videoHeight = height;
    }

    /**
     * 运行在GLThread中
     *
     * @param eglContext 这个eglContext来自GLThread的eglContext
     * @param texId      纹理Id
     */
    public void setEglContext(final EGLContext eglContext, GLSurfaceView glSurfaceView, final int texId) {
        mRenderRunnable.setEglContext(eglContext, glSurfaceView, texId, mSurface);
    }

    public void setFilter(FloatBuffer  mGLCubeBuffer,FloatBuffer mGLTextureBuffer){
        //mRenderRunnable.setFilter(gpuImageFilter,mGLCubeBuffer,mGLTextureBuffer,mWidth,mHeight);
        mRenderRunnable.setFilter(gpuImageFilter,mGLCubeBuffer,mGLTextureBuffer,videoWidth,videoHeight);
    }

    @Override
    public void release() {
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        if (mRenderRunnable != null) {
            mRenderRunnable.release();
            mRenderRunnable = null;
        }
        super.release();
    }

    /**
     * 码率
     *
     * @return
     */
    private int calcBitRate() {
        //final int bitrate = (int) (BPP * FRAME_RATE * mWidth * mHeight);
        final int bitrate = (int)(BPP*FRAME_RATE*videoWidth*videoHeight);
        return 30000000;
    }

    @Override
    public void signalEndOfInputStream() {
        mMediaCodec.signalEndOfInputStream();
        mIsEndOfStream = true;
    }

}
