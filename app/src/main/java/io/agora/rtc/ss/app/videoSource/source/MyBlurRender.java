package io.agora.rtc.ss.app.videoSource.source;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

import io.agora.rtc.gl.EglBase;
import io.agora.rtc.gl.RendererCommon;
import io.agora.rtc.mediaio.BaseVideoRenderer;
import io.agora.rtc.mediaio.IVideoSink;
import io.agora.rtc.mediaio.MediaIO;
import io.agora.rtc.mediaio.SurfaceTextureHelper;
import io.agora.rtc.ss.app.R;
import io.agora.rtc.ss.app.videoSource.EGLCreator;
import io.agora.rtc.video.AgoraVideoFrame;
import jp.co.cyberagent.android.gpuimage.GPUImageBoxBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePixelationFilter;
import project.android.imageprocessing.filter.BlurFilter;

/**
 * Created by keke on 2017/12/29.
 */

public class MyBlurRender implements IVideoSink, TextureView.SurfaceTextureListener, SurfaceHolder.Callback {
    private final static String TAG = TextureView.class.getSimpleName();
    private Bitmap mBitmap;

    private BaseVideoRenderer mBaseRender;
    private MyFilterRender mFilterRender;
    private Context mContext;

    private EglBase.Context mEglContext;
    private int[] mConfigAttributes;
    private RendererCommon.GlDrawer mDrawer;

    private TextureView mTextureView;
    private SurfaceView mSurfaceView;
    private GLSurfaceView mGLSurfaceView;
    private SurfaceTextureHelper mSurfaceTextureHelper;
    private EGLCreator mGLCreator;

    private String DEBUG_TAG;
    private boolean mInitGLEnv = false;
    private int mPixelFormat;
    private int mBufferType;
    private final float BLUR_SIZE = 8F;
    private EGL10 mEGL;
    private EGLContext mEGLContext;
    private GL10 mGL;

    public MyBlurRender(Context context, TextureView view) {
        mContext = context;
        mTextureView = view;
        mBaseRender = new BaseVideoRenderer(TAG);
        mBaseRender.setRenderView(mTextureView, this);
        GPUImageGaussianBlurFilter filter = new GPUImageGaussianBlurFilter();
        filter.setBlurSize(BLUR_SIZE);
        mFilterRender = new MyFilterRender(filter);
    }

    public MyBlurRender(Context context, SurfaceView view) {
        mContext = context;
        mSurfaceView = view;
        GPUImageGaussianBlurFilter filter = new GPUImageGaussianBlurFilter();
        filter.setBlurSize(BLUR_SIZE);
        mFilterRender = new MyFilterRender(filter);
    }

    public MyBlurRender(Context context, GLSurfaceView surfaceView) {
        mContext = context;
        mGLSurfaceView = surfaceView;
        mFilterRender = new MyFilterRender(new GPUImageBoxBlurFilter(BLUR_SIZE));
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setRenderer(mFilterRender);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }


    @Override
    public void consumeTextureFrame(int texId, int format, int width, int height, int rotation, long ts, float[] matrix) {
        Log.e(DEBUG_TAG, "consumeTextureFrame");
        if (mSurfaceView != null) {
            if (!mInitGLEnv) {
                mInitGLEnv = true;
                mFilterRender.onSurfaceCreated(null, null);
                mFilterRender.onSurfaceChanged(null, width, height);
            }
            mFilterRender.onTextureFrame(texId, width, height, matrix);
            mFilterRender.onDrawFrame(null);
            mFilterRender.onDrawFrame(null);
        }
        if (mBaseRender != null) {
            mBaseRender.consume(texId, format, width, height, rotation, ts, matrix);
        }
    }

    @Override
    public void consumeByteBufferFrame(ByteBuffer buffer, int format, int width, int height, int rotation, long ts) {
        Log.e(DEBUG_TAG, "consumeByteBufferFrame");
        if (mGLSurfaceView == null) {
            checkCreateGLEnv(width, height);
            mFilterRender.onByteBufferFrame(buffer, width, height, rotation);
            mFilterRender.onDrawFrame(null);
            mFilterRender.onDrawFrame(null);
        } else {
            mFilterRender.onByteBufferFrame(buffer, width, height, rotation);
        }
        if (mBaseRender != null) {
            mBaseRender.consume(buffer, format, width, height, rotation, ts);
        }
    }

    @Override
    public void consumeByteArrayFrame(byte[] data, int format, int width, int height, int rotation, long ts) {
        Log.e(DEBUG_TAG, "consumeByteArrayFrame");
        if (mGLSurfaceView == null) {
            checkCreateGLEnv(width, height);
            mFilterRender.onByteBufferFrame(data, width, height, rotation);
            mFilterRender.onDrawFrame(null);
            mFilterRender.onDrawFrame(null);
        } else {
            mFilterRender.onByteBufferFrame(data, width, height, rotation);
        }
        if (mBaseRender != null) {
            mBaseRender.consume(data, format, width, height, rotation, ts);
        }
    }

    private boolean checkCreateGLEnv(int width, int height) {
        if (mGLCreator == null) {
            mGLCreator = new EGLCreator();
            mGLCreator.createEGLEnvironment();
            Log.i("Irwin", "Before EGL Context: " + mGLCreator.getEglContext() + "  GL: " + mGLCreator.getEglContext().getGL());
            GL10 gl = (GL10) mGLCreator.getGl();
            mGL = gl;
            mFilterRender.onSurfaceCreated(gl, null);
            mFilterRender.onSurfaceChanged(gl, width, height);
            return false;
        }
        return true;
    }

    private boolean checkInitGlEnv(int width, int height) {
        if (mSurfaceTextureHelper == null) {
            Log.i("Irwin", "Before EGL Context: " + mEGLContext + "  GL: " + mGL);
            mSurfaceTextureHelper = SurfaceTextureHelper.create(Thread.currentThread().getName(), null);
            mEGL = (EGL10) EGLContext.getEGL();
            // complicated
            mEGLContext = mEGL.eglGetCurrentContext();
            mGL = (GL10) mEGLContext.getGL();
            Log.i("Irwin", "EGL Context: " + mEGLContext + "  GL: " + mGL);
            mFilterRender.onSurfaceCreated(mGL, null);
            mFilterRender.onSurfaceChanged(mGL, width, height);
            return false;
        }
        return true;
    }


    public MyBlurRender setDebugTag(String tag) {
        DEBUG_TAG = tag;
        return this;
    }

    public void init(EglBase.Context sharedContext) {
        mEglContext = sharedContext;
    }

    public void init(final EglBase.Context sharedContext, final int[] configAttributes, RendererCommon.GlDrawer drawer) {
        mEglContext = sharedContext;
        mConfigAttributes = configAttributes;
        mDrawer = drawer;
    }

    public void setBufferType(MediaIO.BufferType bufferType) {
//        mBaseRender.setBufferType(MediaIO.BufferType.BYTE_ARRAY);
        if (mBaseRender != null) {
            mBaseRender.setBufferType(bufferType);
        }
        mBufferType = bufferType.intValue();
    }

    public void setPixelFormat(MediaIO.PixelFormat pixelFormat) {
//        mBaseRender.setPixelFormat(MediaIO.PixelFormat.TEXTURE_2D);
        if (mBaseRender != null) {
            mBaseRender.setPixelFormat(pixelFormat);
        }
        mPixelFormat = pixelFormat.intValue();
    }

    public void setMirror(final boolean mirror) {
        {
            mBaseRender.getEglRender().setMirror(mirror);
        }
    }

    //from IVideoRenderer begin
    @Override
    public boolean onInitialize() {
        //Log.e(TAG, "onInitialize");
        if (mBaseRender != null) {
            if (mConfigAttributes != null && mDrawer != null) {
                mBaseRender.init(mEglContext, mConfigAttributes, mDrawer);
            } else {
                mBaseRender.init(mEglContext);
            }
        }
        return true;
    }

    @Override
    public boolean onStart() {
        Log.d(TAG, "onStart");
        if (mBaseRender != null) {
            return mBaseRender.start();
        }
        return true;
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        if (mBaseRender != null) {
            mBaseRender.stop();
        }
    }

    @Override
    public void onDispose() {
        Log.d(TAG, "onDispose");
        if (mBaseRender != null) {
            mBaseRender.release();
        }
        mFilterRender.deleteImage();
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    @Override
    public long getEGLContextHandle() {
        if (mBaseRender != null) {
            return mBaseRender.getEGLContextHandle();
        }
        return 0;
    }

    @Override
    public int getPixelFormat() {
        int format;
        if (mBaseRender != null) {
            format = mBaseRender.getPixelFormat();
        } else {
            format = mPixelFormat;
        }
        if (format == AgoraVideoFrame.FORMAT_NONE) {
            throw new IllegalArgumentException("Pixel format is not set");
        }
        return format;
    }

    @Override
    public int getBufferType() {
        int type;
        if (mBaseRender != null) {
            type = mBaseRender.getBufferType();
        } else {
            type = mBufferType;
        }
        if (type == AgoraVideoFrame.BUFFER_TYPE_NONE) {
            throw new IllegalArgumentException("Buffer type is not set");
        }
        return type;
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable");
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureDestroyed");
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        //Log.d(TAG, "onSurfaceTextureUpdated");
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
