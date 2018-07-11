package io.agora.rtc.ss.app.videoSource.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import io.agora.rtc.ss.app.R;
import io.agora.rtc.ss.app.videoSource.source.MyFilterRender;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageRenderer;

public class GLFilterActivity extends FragmentActivity {

    private GLSurfaceView mGLSurfaceView;
    private MyFilterRender mRender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        setContentView(mGLSurfaceView);
        mGLSurfaceView.setEGLContextClientVersion(2);

        GPUImageGaussianBlurFilter filter = new GPUImageGaussianBlurFilter();
        filter.setBlurSize(20f);
        mRender = new MyFilterRender(filter);
        mGLSurfaceView.setRenderer(mRender);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mRender.setImageBitmap(getImage());
        mGLSurfaceView.requestRender();
    }

    private Bitmap getImage() {
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.timg);
        return drawable.getBitmap();
    }
}
