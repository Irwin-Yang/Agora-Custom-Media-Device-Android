package io.agora.rtc.ss.app.videoSource.source;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.TextureView;

import java.nio.ByteBuffer;

import io.agora.rtc.gl.EglBase;
import io.agora.rtc.gl.RendererCommon;
import io.agora.rtc.mediaio.BaseVideoRenderer;
import io.agora.rtc.mediaio.IVideoSink;
import io.agora.rtc.mediaio.MediaIO;
import io.agora.rtc.video.AgoraVideoFrame;
import jp.co.cyberagent.android.gpuimage.GPUImagePixelationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageRenderer;

/**
 * Created by keke on 2017/12/29.
 */

public class PrivateTextureHelper implements IVideoSink, TextureView.SurfaceTextureListener {
    private final static String TAG = TextureView.class.getSimpleName();

    private BaseVideoRenderer mRender;

    private EglBase.Context mEglContext;
    private int[] mConfigAttributes;
    private RendererCommon.GlDrawer mDrawer;

    private TextureView mTextureView;

    private String DEBUG_TAG;

    public PrivateTextureHelper(Context context, TextureView view) {
        mTextureView = view;
        mRender = new BaseVideoRenderer(TAG);
        mRender.setRenderView(mTextureView, this);
    }


    public PrivateTextureHelper setDebugTag(String tag) {
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
//        mRender.setBufferType(MediaIO.BufferType.BYTE_ARRAY);
        mRender.setBufferType(bufferType);
    }

    public void setPixelFormat(MediaIO.PixelFormat pixelFormat) {
//        mRender.setPixelFormat(MediaIO.PixelFormat.TEXTURE_2D);
        mRender.setPixelFormat(pixelFormat);
    }

    public void setMirror(final boolean mirror) {
        mRender.getEglRender().setMirror(mirror);
    }

    //from IVideoRenderer begin
    @Override
    public boolean onInitialize() {
        //Log.e(TAG, "onInitialize");
        if (mConfigAttributes != null && mDrawer != null) {
            mRender.init(mEglContext, mConfigAttributes, mDrawer);
        } else {
            mRender.init(mEglContext);
        }
        return true;
    }

    @Override
    public boolean onStart() {
        Log.d(TAG, "onStart");
        return mRender.start();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        mRender.stop();
    }

    @Override
    public void onDispose() {
        Log.d(TAG, "onDispose");
        mRender.release();
    }

    @Override
    public long getEGLContextHandle() {
        return mRender.getEGLContextHandle();
    }

    @Override
    public int getPixelFormat() {
        int format = mRender.getPixelFormat();
        if (format == AgoraVideoFrame.FORMAT_NONE) {
            throw new IllegalArgumentException("Pixel format is not set");
        }
        return format;
    }

    @Override
    public int getBufferType() {
        int type = mRender.getBufferType();
        if (type == AgoraVideoFrame.BUFFER_TYPE_NONE) {
            throw new IllegalArgumentException("Buffer type is not set");
        }
        return type;
    }

    @Override
    public void consumeTextureFrame(int texId, int format, int width, int height, int rotation, long ts, float[] matrix) {
        Log.e(DEBUG_TAG, "consumeTextureFrame");

        mRender.consume(texId, format, width, height, rotation, ts, matrix);
    }

    @Override
    public void consumeByteBufferFrame(ByteBuffer buffer, int format, int width, int height, int rotation, long ts) {
        Log.e(DEBUG_TAG, "consumeByteBufferFrame");
        mRender.consume(buffer, format, width, height, rotation, ts);
    }

    @Override
    public void consumeByteArrayFrame(byte[] data, int format, int width, int height, int rotation, long ts) {
        Log.e(DEBUG_TAG, "consumeByteArrayFrame");
        mRender.consume(data, format, width, height, rotation, ts);
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


}
