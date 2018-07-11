package io.agora.rtc.ss.app.renderer;

import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;

import io.agora.rtc.mediaio.IVideoSink;
import io.agora.rtc.mediaio.MediaIO;
import io.agora.rtc.ss.app.renderer.filter.ImageFilter;

public class VideoFilterRenderer implements IVideoSink {

    private GLSurfaceView mSurfaceView;
    private int mBufferType;
    private int mPixelFormat;
    private ImageFilterRenderer mFilterRender;

    public VideoFilterRenderer(GLSurfaceView surfaceView) {
        this(surfaceView, null);
    }

    public VideoFilterRenderer(GLSurfaceView surfaceView, ImageFilter filter) {
        init(surfaceView, filter);
    }

    private void init(GLSurfaceView surfaceView, ImageFilter filter) {
        mSurfaceView = surfaceView;
        surfaceView.setEGLContextClientVersion(2);
        mFilterRender = new ImageFilterRenderer(filter);
        surfaceView.setRenderer(mFilterRender);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public void setFilter(ImageFilter filter) {
        mFilterRender.setFilter(filter);
    }

    public ImageFilter getFilter() {
        return mFilterRender.getFilter();
    }


    public void setScaleType(ImageFilterRenderer.ScaleType scaleType) {
        mFilterRender.setScaleType(scaleType);
    }

    public void setBufferType(MediaIO.BufferType bufferType) {
        this.mBufferType = bufferType.intValue();
    }


    public void setPixelFormat(MediaIO.PixelFormat pixelFormat) {
        this.mPixelFormat = pixelFormat.intValue();
    }

    @Override
    public int getBufferType() {
        return mBufferType;
    }

    @Override
    public int getPixelFormat() {
        return mPixelFormat;
    }

    @Override
    public void consumeByteBufferFrame(ByteBuffer buffer, int format, int width, int height, int rotation, long ts) {
        mFilterRender.onByteBufferFrame(buffer, width, height, rotation);
    }

    @Override
    public void consumeByteArrayFrame(byte[] data, int format, int width, int height, int rotation, long ts) {
        mFilterRender.onByteArrayFrame(data, width, height, rotation);
    }

    @Override
    public void consumeTextureFrame(int texId, int format, int width, int height, int rotation, long ts, float[] matrix) {

    }


    @Override
    public boolean onInitialize() {
        return true;
    }

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDispose() {
        mFilterRender.destroy();
    }

    @Override
    public long getEGLContextHandle() {
        return 0;
    }

}
