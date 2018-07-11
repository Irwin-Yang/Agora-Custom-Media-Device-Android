package io.agora.rtc.ss.app.videoSource.source;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import io.agora.rtc.mediaio.AgoraTextureCamera;
import jp.co.cyberagent.android.gpuimage.GPUImagePixelationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageRenderer;
import jp.co.cyberagent.android.gpuimage.PixelBuffer;
import project.android.imageprocessing.filter.MosaicsFilter;

public class MyCameraTextureSource extends AgoraTextureCamera {
    private static final String TAG = MyCameraTextureSource.class.getSimpleName();
    private GPUImageRenderer mRender;
    private PixelBuffer mPixelBuffer;
    private int mWidth;
    private int mHeight;


    EGL10 mEGL;
    EGLDisplay mEGLDisplay;
    EGLConfig[] mEGLConfigs;
    EGLConfig mEGLConfig;
    EGLContext mEGLContext;
    EGLSurface mEGLSurface;
    GL10 mGL;

    private boolean mInitGLEnv = false;


    public MyCameraTextureSource(Context context, int width, int height) {
        super(context, width, height);
        GPUImagePixelationFilter filter = new GPUImagePixelationFilter();
        filter.setPixel(100F);
        mWidth = width;
        mHeight = height;
        mRender = new GPUImageRenderer(filter);
    }

    @Override
    public int getBufferType() {
        return super.getBufferType();
    }

    @Override
    public void onTextureFrameAvailable(int oesTextureId, float[] transformMatrix, long timestampNs) {
        if (!mInitGLEnv) {
            mInitGLEnv = true;
            mEGL = (EGL10) EGLContext.getEGL();
            // complicated
            mEGLContext = mEGL.eglGetCurrentContext();
            mGL = (GL10) mEGLContext.getGL();
            mRender.onSurfaceCreated(mGL, null);
            mRender.onSurfaceChanged(mGL, mWidth, mHeight);
        }
        Log.i(TAG, "Texture available: " + oesTextureId);
        mRender.setTexture(oesTextureId, mWidth, mHeight, transformMatrix);
        mRender.onDrawFrame(mGL);
        mRender.onDrawFrame(mGL);
        super.onTextureFrameAvailable(oesTextureId, transformMatrix, timestampNs);
    }

}
