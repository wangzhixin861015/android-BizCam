/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.cyberagent.android.gpuimage;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.co.cyberagent.android.gpuimage.reocder.gl_util.GLMatrixState;
import jp.co.cyberagent.android.gpuimage.reocder.gl_util.GLTextureUtil;
import jp.co.cyberagent.android.gpuimage.reocder.media_encoder.MediaVideoEncoderRunable;
import jp.co.cyberagent.android.gpuimage.util.TextureRotationUtil;

import static jp.co.cyberagent.android.gpuimage.util.TextureRotationUtil.TEXTURE_NO_ROTATION;

@TargetApi(11)
public class GPUImageRenderer implements Renderer, PreviewCallback {
    public static final int NO_IMAGE = -1;
    // FPS 帧率
    private static final int FRAME_RATE = 24;
    private static final float BPP = 0.25f;
    static final float CUBE[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f
    };

    private float cubeMode[], cube[];

    private GPUImageFilter mFilter;
    private GPUImageCameraFilter gpuImageCameraFilter;
    public final Object mSurfaceChangedWaiter = new Object();
    private int[] mGLTextureIds;
    private int mGLTextureId = NO_IMAGE;
    private int mGLTextureIdText = NO_IMAGE;
    private SurfaceTexture mSurfaceTexture = null;
    private final FloatBuffer mGLCubeBuffer;
    private final FloatBuffer mGLTextureBuffer;
    private IntBuffer mGLRgbBuffer;

    private int mOutputWidth;
    private int mOutputHeight;
    private int mImageWidth;
    private int mImageHeight;
    private int mAddedPadding;

    private final Queue<Runnable> mRunOnDraw;
    private final Queue<Runnable> mRunOnDrawEnd;
    private Rotation mRotation;
    private boolean mFlipHorizontal;
    private boolean mFlipVertical;
    private GPUImage.ScaleType mScaleType = GPUImage.ScaleType.CENTER_CROP;

    private float mBackgroundRed = 1;
    private float mBackgroundGreen = 1;
    private float mBackgroundBlue = 1;
    private boolean isNoCamera;
    private MediaVideoEncoderRunable mMediaVideoEncoderRunable;
    private Rect rect;
    private boolean isGetBitmap;
    private RecoderInterFace recoderInterFace;
    private boolean isCan;

    public GPUImageRenderer(final GPUImageFilter filter) {
        mFilter = filter;
        mRunOnDraw = new LinkedList<Runnable>();
        mRunOnDrawEnd = new LinkedList<Runnable>();
        cubeMode = new float[]{
                CUBE[0], CUBE[1],
                CUBE[2], CUBE[3],
                CUBE[4], CUBE[5],
                CUBE[6], CUBE[7]
        };
        mGLCubeBuffer = ByteBuffer.allocateDirect(cubeMode.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        cube = cubeMode;
        mGLCubeBuffer.put(cube).position(0);
        mGLTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        setRotation(Rotation.NORMAL, false, false);
    }

    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {
        mGLTextureIds = new int[]{NO_IMAGE, NO_IMAGE};
        GLES20.glClearColor(mBackgroundRed, mBackgroundGreen, mBackgroundBlue, 1);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        mFilter.init();
    }

    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        mOutputWidth = width;
        mOutputHeight = height;
        //X，Y————以像素为单位，指定了视口的左上角（在第一象限内，以（0，0）为原点的）位置。
        //width，height————表示这个视口矩形的宽度和高度，根据窗口的实时变化重绘窗口。
        GLES20.glViewport(0, 0, width, height);
        GLES20.glUseProgram(mFilter.getProgram());
        mFilter.onOutputSizeChanged(width, height);
        adjustImageScaling();
        synchronized (mSurfaceChangedWaiter) {
            mSurfaceChangedWaiter.notifyAll();
        }
    }

    @Override
    public void onDrawFrame(final GL10 gl) {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);//清除缓存
        runAll(mRunOnDraw);
        float[] mVpMatrix = new float[0];
        if (!GLMatrixState.isNoInit()) {
            startMatrixM();
            mVpMatrix = GLMatrixState.getFinalMatrix();
            mFilter.setMatrixM(mVpMatrix);
        }
        synchronized (GPUImageRenderer.this) {
            if (mMediaVideoEncoderRunable != null) {
                mMediaVideoEncoderRunable.frameAvailableSoon(mVpMatrix);
            }
        }
        mFilter.onDraw(mGLTextureId, mGLTextureIds, mGLCubeBuffer, mGLTextureBuffer);
        if (isGetBitmap) {
            if (recoderInterFace != null) {
                recoderInterFace.getCurrentGPUBitmap(gl);
            }
            isGetBitmap = false;
        }


        if (!GLMatrixState.isNoInit()) {
            releasMareixM();
        }

        runAll(mRunOnDrawEnd);
    }

    /**
     * 设置图像位置
     *
     * @param rect
     */
    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public float[] getCube() {
        return cube;
    }

    public void setCube(float[] cube) {
        this.cube = cube;
        if (mGLCubeBuffer != null && this.cube != null) {
            mGLCubeBuffer.put(cube).position(0);
        }
    }


    public void startMatrixM() {
        GLMatrixState.pushMatrix();
        GLMatrixState.translate(0, 0, -1);
        // GLMatrixState.translate(0, -0.75f, 0);
        GLMatrixState.rotate(-90, 0, 0, 1);
    }

    public void releasMareixM() {
        GLMatrixState.popMatrix();
    }

    /**
     * Sets the background color
     *
     * @param red   red color value
     * @param green green color value
     * @param blue  red color value
     */
    public void setBackgroundColor(float red, float green, float blue) {
        mBackgroundRed = red;
        mBackgroundGreen = green;
        mBackgroundBlue = blue;
    }

    private void runAll(Queue<Runnable> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                queue.poll().run();
            }
        }
    }

    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera) {
      /*  if (recoderInterFace != null) {
            recoderInterFace.onPreviewFrame(data);
        }

        final Size previewSize = camera.getParameters().getPreviewSize();
        if (mGLRgbBuffer == null) {
            mGLRgbBuffer = IntBuffer.allocate(previewSize.width * previewSize.height);
        }
        if (mRunOnDraw.isEmpty()) {
            runOnDraw(new Runnable() {
                @Override
                public void run() {
                    GPUImageNativeLibrary.YUVtoRBGA(data, previewSize.width, previewSize.height,
                            mGLRgbBuffer.array());
                    mGLTextureId = OpenGlUtils.loadTexture(mGLRgbBuffer, previewSize, mGLTextureId);
                    camera.addCallbackBuffer(data);
                    if (mImageWidth != previewSize.width) {
                        mImageWidth = previewSize.width;
                        mImageHeight = previewSize.height;
                        adjustImageScaling();
                    }
                }
            });
        }*/
    }


    private boolean powerof2(int n) {
        return ((n & (n - 1)) == 0);
    }

    public void setUpSurfaceTexture(final Camera camera, final SurfaceTexture.OnFrameAvailableListener listener) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                isNoCamera = true;
                initCamera(camera, listener);
                setFilter(mFilter);
            }
        });
    }


    public void initCamera(Camera camera, SurfaceTexture.OnFrameAvailableListener listener) {
        try {
            mGLTextureId = GLTextureUtil.createOESTextureID();
            mSurfaceTexture = new SurfaceTexture(mGLTextureId);
            mSurfaceTexture.setOnFrameAvailableListener(listener);
            //允许相机将获取的图片在一个隐藏的surfacetexture上显示
            camera.setPreviewTexture(mSurfaceTexture);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (RuntimeException e){
            e.printStackTrace();
            //Camera is being used after Camera.release() was called
            camera = Camera.open(0);
            try {
                camera.setPreviewTexture(mSurfaceTexture);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            camera.startPreview();
        }
    }

    public void releaseCamera() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        deleteImage();
        setFilter(mFilter);
        isNoCamera = false;
    }

    public void setNoCameraGPU(boolean isNoCamera) {
        this.isNoCamera = isNoCamera;
        if (mFilter != null) {
            mFilter.destroy();
        }
        upDataCameraFilter();
        mFilter.init();
        GLES20.glUseProgram(mFilter.getProgram());
        mFilter.onOutputSizeChanged(mOutputWidth, mOutputHeight);
    }

    public void setFilter(final GPUImageFilter filter) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                final GPUImageFilter oldFilter = mFilter;
                mFilter = filter;
                if (oldFilter != null) {
                    oldFilter.destroy();
                }
                upDataCameraFilter();
                mFilter.init();
                GLES20.glUseProgram(mFilter.getProgram());
                mFilter.onOutputSizeChanged(mOutputWidth, mOutputHeight);
            }
        });
    }


    public void upDataCameraFilter() {
        if (mSurfaceTexture != null && isNoCamera) {
            if (gpuImageCameraFilter == null) {
                gpuImageCameraFilter = new GPUImageCameraFilter();
            }
            if (mFilter instanceof GPUImageFilterGroup) {
                List<GPUImageFilter> list = ((GPUImageFilterGroup) mFilter).getFilters();
                if (!(list.get(0) instanceof GPUImageCameraFilter)) {
                    GPUImageFilterGroup gpuImageFilterGroup = new GPUImageFilterGroup();
                    gpuImageFilterGroup.addFilter(gpuImageCameraFilter);
                    for (int i = 0; i < list.size(); i++) {
                        if (!(list.get(i) instanceof GPUImageCameraFilter)) {
                            gpuImageFilterGroup.addFilter(list.get(i));
                        }
                    }
                    mFilter = gpuImageFilterGroup;
                }
            } else {
                if (!(mFilter instanceof GPUImageCameraFilter)) {
                    GPUImageFilterGroup gpuImageFilterGroup = new GPUImageFilterGroup();
                    gpuImageFilterGroup.addFilter(gpuImageCameraFilter);
                    gpuImageFilterGroup.addFilter(mFilter);
                    mFilter = gpuImageFilterGroup;
                }
            }
        } else {
            if (mFilter instanceof GPUImageFilterGroup) {
                List<GPUImageFilter> list = ((GPUImageFilterGroup) mFilter).getFilters();
                if (list.get(0) instanceof GPUImageCameraFilter) {
                    GPUImageFilterGroup gpuImageFilterGroup = new GPUImageFilterGroup();
                    for (int i = 0; i < list.size(); i++) {
                        if (!(list.get(i) instanceof GPUImageCameraFilter)) {
                            gpuImageFilterGroup.addFilter(list.get(i));
                        }
                    }
                    mFilter = gpuImageFilterGroup;
                }
            } else {
                if (mFilter instanceof GPUImageCameraFilter) {
                    mFilter = new GPUImageFilter();
                }
            }
        }

    }


    public void deleteImage() {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glDeleteTextures(4, new int[]{
                        mGLTextureId, mGLTextureIds[0], mGLTextureIds[1], mGLTextureIdText
                }, 0);
                mGLTextureId = NO_IMAGE;
                mGLTextureIdText = NO_IMAGE;
                mGLTextureIds[0] = NO_IMAGE;
                mGLTextureIds[1] = NO_IMAGE;
            }
        });
    }

    public void setImageBitmap(final Bitmap bitmap) {
        setImageBitmap(bitmap, true);
    }

    public void setImageBitmap(final Bitmap bitmap, final boolean recycle) {
        if (bitmap == null) {
            return;
        }

        runOnDraw(new Runnable() {

            @Override
            public void run() {
                Bitmap resizedBitmap = null;
                if (bitmap.getWidth() % 2 == 1) {
                    resizedBitmap = Bitmap.createBitmap(bitmap.getWidth() + 1, bitmap.getHeight(),
                            Bitmap.Config.ARGB_8888);
                    Canvas can = new Canvas(resizedBitmap);
                    can.drawARGB(0x00, 0x00, 0x00, 0x00);
                    can.drawBitmap(bitmap, 0, 0, null);
                    mAddedPadding = 1;
                } else {
                    mAddedPadding = 0;
                }

                mGLTextureId = OpenGlUtils.loadTexture(
                        resizedBitmap != null ? resizedBitmap : bitmap, mGLTextureId, recycle);
                if (resizedBitmap != null) {
                    resizedBitmap.recycle();
                }
                mImageWidth = bitmap.getWidth();
                mImageHeight = bitmap.getHeight();
                adjustImageScaling();
            }
        });
    }

    public void setImageBitmapPartPaint(final Bitmap bitmap, final boolean recycle) {
        if (bitmap == null) {
            return;
        }
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                mGLTextureIds[0] = OpenGlUtils.loadTexture(bitmap, mGLTextureIds[0], recycle);
            }
        });
    }

    public void setImageBitmapPartSat(final Bitmap bitmap, final boolean recycle) {
        if (bitmap == null) {
            return;
        }
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                mGLTextureIds[1] = OpenGlUtils.loadTexture(bitmap, mGLTextureIds[1], recycle);
            }
        });
    }

    public void setScaleType(GPUImage.ScaleType scaleType) {
        mScaleType = scaleType;
    }

    public int getFrameWidth() {
        return mOutputWidth;
    }

    public int getFrameHeight() {
        return mOutputHeight;
    }

    private void adjustImageScaling() {
        if (mOutputWidth == 0 || mOutputHeight == 0) {
            return;
        }
        float outputWidth = mOutputWidth;
        float outputHeight = mOutputHeight;
        if (mRotation == Rotation.ROTATION_270 || mRotation == Rotation.ROTATION_90) {
            outputWidth = mOutputHeight;
            outputHeight = mOutputWidth;
        }

        float ratio1 = outputWidth / mImageWidth;
        float ratio2 = outputHeight / mImageHeight;
        float ratioMax = Math.max(ratio1, ratio2);
        int imageWidthNew = Math.round(mImageWidth * ratioMax);
        int imageHeightNew = Math.round(mImageHeight * ratioMax);

        float ratioWidth = imageWidthNew / outputWidth;
        float ratioHeight = imageHeightNew / outputHeight;

        cubeMode = CUBE;
        float[] textureCords = TextureRotationUtil.getRotation(mRotation, mFlipHorizontal, mFlipVertical);
        if (mScaleType == GPUImage.ScaleType.CENTER_CROP) {
            float distHorizontal = (1 - 1 / ratioWidth) / 2;
            float distVertical = (1 - 1 / ratioHeight) / 2;
            textureCords = new float[]{
                    addDistance(textureCords[0], distHorizontal), addDistance(textureCords[1], distVertical),
                    addDistance(textureCords[2], distHorizontal), addDistance(textureCords[3], distVertical),
                    addDistance(textureCords[4], distHorizontal), addDistance(textureCords[5], distVertical),
                    addDistance(textureCords[6], distHorizontal), addDistance(textureCords[7], distVertical),
            };
        } else {
            cubeMode = new float[]{
                    CUBE[0] / ratioHeight, CUBE[1] / ratioWidth,
                    CUBE[2] / ratioHeight, CUBE[3] / ratioWidth,
                    CUBE[4] / ratioHeight, CUBE[5] / ratioWidth,
                    CUBE[6] / ratioHeight, CUBE[7] / ratioWidth,
            };
        }
        cube = cubeMode;
        mGLCubeBuffer.clear();
        mGLCubeBuffer.put(cube).position(0);
        mGLTextureBuffer.clear();
        mGLTextureBuffer.put(textureCords).position(0);
    }

    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }

    public void setRotationCamera(final Rotation rotation, final boolean flipHorizontal,
                                  final boolean flipVertical) {
        setRotation(rotation, flipVertical, flipHorizontal);
    }

    public void setRotation(final Rotation rotation) {
        mRotation = rotation;
        adjustImageScaling();
    }

    public void setRotation(final Rotation rotation,
                            final boolean flipHorizontal, final boolean flipVertical) {
        mFlipHorizontal = flipHorizontal;
        mFlipVertical = flipVertical;
        setRotation(rotation);
    }

    public Rotation getRotation() {
        return mRotation;
    }

    public boolean isFlippedHorizontally() {
        return mFlipHorizontal;
    }

    public boolean isFlippedVertically() {
        return mFlipVertical;
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.add(runnable);
        }
    }

    protected void runOnDrawEnd(final Runnable runnable) {
        synchronized (mRunOnDrawEnd) {
            mRunOnDrawEnd.add(runnable);
        }
    }

    public void setRecoderInterFace(RecoderInterFace recoderInterFace) {
        this.recoderInterFace = recoderInterFace;
    }

    public interface RecoderInterFace {

        void onDrawFrame();

        void getCurrentGPUBitmap(GL10 gl);

        void onPreviewFrame(final byte[] data);
    }

    /**
     * 开始录制视频时，由主线程||异步线程回调回来的
     *
     * @param mediaVideoEncoderRunable
     */
    public void setVideoEncoder(final GLSurfaceView glSurfaceView, final MediaVideoEncoderRunable mediaVideoEncoderRunable) {
        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    if (mediaVideoEncoderRunable != null) {
                        mediaVideoEncoderRunable.setEglContext(EGL14.eglGetCurrentContext(), glSurfaceView, mGLTextureId);
                        mediaVideoEncoderRunable.setFilter(mGLCubeBuffer, mGLTextureBuffer);
                    }
                    mMediaVideoEncoderRunable = mediaVideoEncoderRunable;


                }
            }
        });
    }

    public void getGPUBitmap() {
        isGetBitmap = true;
    }
}
