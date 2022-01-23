package jp.co.cyberagent.android.gpuimage.reocder.media_encoder;


import android.media.MediaCodec;
import android.media.MediaFormat;

import java.io.IOException;
import java.nio.ByteBuffer;

import jp.co.cyberagent.android.gpuimage.reocder.media_muxer.SohuMediaMuxerManager;

/**
 * 视频与音频录制的基类
 */
public abstract class BaseMediaEncoderRunable implements Runnable {


    protected static final int TIMEOUT_USEC = 10000;

    public interface MediaEncoderListener {
        void onPrepared(BaseMediaEncoderRunable encoder);

        void onStopped(BaseMediaEncoderRunable encoder);
    }

    // 同步锁
    protected final Object mSync = new Object();
    protected volatile boolean mIsCapturing;
    // 可用数据帧数量
    private int mRequestDrainEncoderCount;
    // 结束录制的标识
    protected volatile boolean mRequestStop;
    protected volatile boolean mRequestPause;
    // 结束录制标识
    protected boolean mIsEndOfStream;
    // muxer结束标识
    protected boolean mMuxerStarted;

    protected int mTrackIndex;

    protected MediaCodec mMediaCodec;
    private MediaCodec.BufferInfo mBufferInfo;

    protected SohuMediaMuxerManager mSohuMediaMuxerManager;
    protected final MediaEncoderListener mMediaEncoderListener;

    private long oncePauseTime;

    private boolean pausing = false;

    public int rotation = 0;

    /**
     * 构造方法
     *
     * @param mediaMuxerManager
     * @param mediaEncoderListener
     */
    public BaseMediaEncoderRunable(final SohuMediaMuxerManager mediaMuxerManager, final MediaEncoderListener mediaEncoderListener) {
        if (mediaEncoderListener == null) {
            throw new NullPointerException("MediaEncoderListener is null");
        }
        if (mediaMuxerManager == null) {
            throw new NullPointerException("MediaMuxerWrapper is null");
        }
        this.mSohuMediaMuxerManager = mediaMuxerManager;
        this.mMediaEncoderListener = mediaEncoderListener;
        this.mSohuMediaMuxerManager.addEncoder(BaseMediaEncoderRunable.this);

        synchronized (mSync) {
            mBufferInfo = new MediaCodec.BufferInfo();
            new Thread(this, getClass().getSimpleName()).start();
            try {
                mSync.wait();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * the method to indicate frame data is soon available or already available
     *
     * @return return true if encoder is ready to encod.
     */
    public boolean frameAvailableSoon() {
        synchronized (mSync) {
            if (!mIsCapturing || mRequestStop) {
                return false;
            }
            mRequestDrainEncoderCount++;
            mSync.notifyAll();
        }
        return true;
    }

    /**
     * encoding loop on private thread
     */
    @Override
    public void run() {
        // 线程开启
        synchronized (mSync) {
            mRequestStop = false;
            mRequestDrainEncoderCount = 0;
            mSync.notify();
        }
        // 线程开启
        final boolean isRunning = true;
        boolean localRequestStop;
        boolean localRequestDrainEncoderFlag;
        boolean localRequestPause;
        while (isRunning) {
            synchronized (mSync) {
                localRequestStop = mRequestStop;
                localRequestDrainEncoderFlag = (mRequestDrainEncoderCount > 0);
                if (localRequestDrainEncoderFlag) {
                    mRequestDrainEncoderCount--;
                }
            }
            if (localRequestStop) {
                drainEncoder();
                signalEndOfInputStream();
                drainEncoder();
                release();
                break;
            }


            if (localRequestDrainEncoderFlag) {
                drainEncoder();
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
        //跳出循环，录像结束
        synchronized (mSync) {
            mRequestStop = true;
            mIsCapturing = false;
        }
    }


    /**
     * 目前在主线程被调用
     *
     * @throws IOException
     */
    public abstract void prepare() throws IOException;

    /**
     * 目前主线程调用
     */
    public void startRecording() {
        synchronized (mSync) {
            mIsCapturing = true;
            mRequestStop = false;
            mSync.notifyAll();
        }
    }


    /**
     * 停止录制(目前在主线程调用)
     */
    public void stopRecording() {
        synchronized (mSync) {
            if (!mIsCapturing || mRequestStop) {
                return;
            }
            mRequestStop = true;
            mSync.notifyAll();
        }
    }

    /**
     * 暂停录制
     */
    public void pauseRecording() {
        synchronized (mSync) {
            if (!mIsCapturing || mRequestStop) {
                return;
            }
            mRequestPause = true;
            mSync.notifyAll();
        }
    }

    /**
     * 恢复录制
     */
    public void resumeRecording() {
        synchronized (mSync) {
            mIsCapturing = true;
            mRequestPause = false;
            mSync.notifyAll();
        }
    }

    /**
     * Release all releated objects
     */
    public void release() {
        mIsCapturing = false;
        if (mMediaCodec != null) {
            try {
                mMediaCodec.stop();
                mMediaCodec.release();
                mMediaCodec = null;
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        if (mMuxerStarted) {
            if (mSohuMediaMuxerManager != null) {
                try {
                    mSohuMediaMuxerManager.stop();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
        mBufferInfo = null;
        try {
            mMediaEncoderListener.onStopped(BaseMediaEncoderRunable.this);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录制
     */
    public void signalEndOfInputStream() {
        encode(null, 0, getPTSUs());
    }

    /**
     * Method to set byte array to the MediaCodec encoder
     *
     * @param buffer
     * @param length             　length of byte array, zero means EOS.
     * @param presentationTimeUs
     */
    protected void encode(final ByteBuffer buffer, final int length, final long presentationTimeUs) {
        if (!mIsCapturing) {
            return;
        }
        final ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
        while (mIsCapturing) {
            final int inputBufferIndex = mMediaCodec.dequeueInputBuffer(TIMEOUT_USEC);
            if (inputBufferIndex >= 0) {
                final ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                if (buffer != null) {
                    inputBuffer.put(buffer);
                }
                if (length <= 0) {
                    mIsEndOfStream = true;
                    mMediaCodec.queueInputBuffer(
                            inputBufferIndex, 0, 0,
                            presentationTimeUs,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    break;
                } else {
                    mMediaCodec.queueInputBuffer(
                            //
                            inputBufferIndex, 0, length,
                            presentationTimeUs, 0);
                }
                break;
            } else if (inputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
            }
        }
    }


    /**
     * mEncoder从缓冲区取数据，然后交给mMuxer编码
     */
    protected void drainEncoder() {
        if (mMediaCodec == null) {
            return;
        }

        //
        int count = 0;

        if (mSohuMediaMuxerManager == null) {
            return;
        }

        ByteBuffer[] encoderOutputBuffers = mMediaCodec.getOutputBuffers();
        LOOP:
        while (mIsCapturing) {
            if (mRequestPause) {
                continue;
            }
            int encoderStatus = mMediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!mIsEndOfStream) {
                    if (++count > 5) {
                        break LOOP;
                    }
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                encoderOutputBuffers = mMediaCodec.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (mMuxerStarted) {
                    throw new RuntimeException("format changed twice");
                }
                final MediaFormat format = mMediaCodec.getOutputFormat();
                mTrackIndex = mSohuMediaMuxerManager.addTrack(format);
                mMuxerStarted = true;
                if (!mSohuMediaMuxerManager.start()) {
                    synchronized (mSohuMediaMuxerManager) {
                        while (!mSohuMediaMuxerManager.isStarted())
                            try {
                                mSohuMediaMuxerManager.wait(100);
                            } catch (final InterruptedException e) {
                                break LOOP;
                            }
                    }
                }
            } else if (encoderStatus < 0) {

            } else {
                final ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                }
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    mBufferInfo.size = 0;
                }
                if (mBufferInfo.size != 0) {
                    count = 0;
                    if (!mMuxerStarted) {
                        throw new RuntimeException("drain:muxer hasn't started");
                    }
                    mBufferInfo.presentationTimeUs = getPTSUs();
                    mSohuMediaMuxerManager.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                    prevOutputPTSUs = mBufferInfo.presentationTimeUs;
                }
                mMediaCodec.releaseOutputBuffer(encoderStatus, false);
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    mIsCapturing = false;
                    break;
                }
            }
        }
    }

    /**
     * previous presentationTimeUs for writing
     */
    private long prevOutputPTSUs = 0;

    /**
     * get next encoding presentationTimeUs
     * 获取当前时间，以纳秒为单位
     *
     * @return
     */
    protected long getPTSUs() {
        long result = System.nanoTime() / 1000L;
        if (result < prevOutputPTSUs)
            result = (prevOutputPTSUs - result) + result;
        return result;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
}
