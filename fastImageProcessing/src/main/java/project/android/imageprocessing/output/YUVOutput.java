
package project.android.imageprocessing.output;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;


import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;
import project.android.imageprocessing.GLRenderer;
import project.android.imageprocessing.input.GLTextureOutputRenderer;
import project.android.imageprocessing.output.GLTextureInputRenderer;

public class YUVOutput extends GLRenderer implements GLTextureInputRenderer {
    private YUVOutputCallback callback;

    protected int[] frameBuffer;

    protected int[] texture_out;

    protected int[] depthRenderBuffer;

    protected byte[] yuvPlanarData = null;

    public static final int YUVFormat_NV12 = 0;

    public static final int YUVFormat_420P = 1;

    public static final int YUVFormat_NV21 = 2;

    private int yuvFormat = YUVFormat_NV12;

    /**
     *
     */
    public YUVOutput(YUVOutputCallback callback) {
        this.callback = callback;
        textureVertices = new FloatBuffer[4];

        float[] texData0 = new float[] {
                0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
        };
        textureVertices[0] = ByteBuffer.allocateDirect(texData0.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureVertices[0].put(texData0).position(0);

        float[] texData1 = new float[] {
                1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
        };
        textureVertices[1] = ByteBuffer.allocateDirect(texData1.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureVertices[1].put(texData1).position(0);

        float[] texData2 = new float[] {
                1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
        };
        textureVertices[2] = ByteBuffer.allocateDirect(texData2.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureVertices[2].put(texData2).position(0);

        float[] texData3 = new float[] {
                0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
        };
        textureVertices[3] = ByteBuffer.allocateDirect(texData3.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureVertices[3].put(texData3).position(0);
    }

    /*
     * (non-Javadoc)
     * @see project.android.imageprocessing.GLRenderer#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();
        if (frameBuffer != null) {
            GLES20.glDeleteFramebuffers(1, frameBuffer, 0);
            frameBuffer = null;
        }
        if (texture_out != null) {
            GLES20.glDeleteTextures(1, texture_out, 0);
            texture_out = null;
        }
        if (depthRenderBuffer != null) {
            GLES20.glDeleteRenderbuffers(1, depthRenderBuffer, 0);
            depthRenderBuffer = null;
        }
        callback = null;
    }

    protected void yuv444PackedToNV21(byte[] src, byte[] dst, int yuvformat, int width, int height) {
        int size = width * height;
        byte[] yuvIn = src;
        byte[] yDst = dst;
        ShortBuffer uvDst = ByteBuffer.wrap(dst, size, size / 2).asShortBuffer();
        IntBuffer y32Dst = ByteBuffer.wrap(dst).asIntBuffer();

        final int PB = 4;

        // Y plane
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width / 4; j++) {
                int y = (yuvIn[PB * ((width * i) + (4 * j + 0)) + 0] << 0)
                        | (yuvIn[PB * ((width * i) + (4 * j + 1)) + 0] << 8)
                        | (yuvIn[PB * ((width * i) + (4 * j + 2)) + 0] << 16)
                        | (yuvIn[PB * ((width * i) + (4 * j + 3)) + 0] << 24);
                y32Dst.put(width * i / 4 + j, y);
            }
            for (int j = width / 4 * 4; j < width; j++) {
                yDst[width * i + j] = yuvIn[PB * ((width * i) + j) + 0];
            }
        }

        height = height / 2;
        width = width / 2;
        // UV plane
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int u = (int)yuvIn[PB * ((2 * width * (2 * i + 0)) + (2 * j + 0)) + 1]
                        + (int)yuvIn[PB * ((2 * width * (2 * i + 0)) + (2 * j + 1)) + 1]
                        + (int)yuvIn[PB * ((2 * width * (2 * i + 1)) + (2 * j + 0)) + 1]
                        + (int)yuvIn[PB * ((2 * width * (2 * i + 1)) + (2 * j + 1)) + 1];

                int v = (int)yuvIn[PB * ((2 * width * (2 * i + 0)) + (2 * j + 0)) + 2]
                        + (int)yuvIn[PB * ((2 * width * (2 * i + 0)) + (2 * j + 1)) + 2]
                        + (int)yuvIn[PB * ((2 * width * (2 * i + 1)) + (2 * j + 0)) + 2]
                        + (int)yuvIn[PB * ((2 * width * (2 * i + 1)) + (2 * j + 1)) + 2];

                // uvDst.put((width*i) + j, (short) (((v/4) << 8) | (u/4)));
                uvDst.put((width * i) + j, (short)0x8080);
            }
        }
    }

    @Override
    protected String getFragmentShader() {
        // TODO Auto-generated method stub

//        return "precision mediump float;\n" + "uniform sampler2D " + UNIFORM_TEXTURE0 + ";\n"
//                + "varying vec2 " + VARYING_TEXCOORD + ";\n"
//
//                + "void main(){\n" + "     mediump vec3 yuv;\n" + "     lowp vec3 rgb;\n"
//                + "     yuv  = texture2D(" + UNIFORM_TEXTURE0 + "," + VARYING_TEXCOORD
//                + ").rgb * \n" + "     mat3(0.299,    0.587,       0.114,\n"
//                + "         -0.1687,  -0.3313,      0.5,\n"
//                + "          0.5,     -0.4187,     -0.0813) \n "
//                + "         + vec3(0,         0.5,        0.5);\n"
//                + "     gl_FragColor = vec4(yuv, 1);\n" + "}\n";
        return "precision mediump float;\n" + "uniform sampler2D " + UNIFORM_TEXTURE0 + ";\n"
                + "varying vec2 " + VARYING_TEXCOORD + ";\n"

                + "void main(){\n" + "     mediump vec3 yuv;\n" + "     lowp vec3 rgb;\n"
                + "     yuv  = texture2D(" + UNIFORM_TEXTURE0 + "," + VARYING_TEXCOORD
                + ").rgb * \n" + "     mat3(0.257,    0.504,       0.098,\n"
                + "         -0.148,  -0.291,       0.439,\n"
                + "          0.439,   -0.368,      -0.071) \n "
                + "         + vec3(0.0625,     0.5,         0.5);\n"
                + "     gl_FragColor = vec4(yuv, 1);\n" + "}\n";

        // yuv = texture2D(inputImageTexture, textureCoordinate).rgb *
        // mat3(0.257, 0.504, 0.098,
        // -0.148, -0.291, 0.439,
        // 0.439, -0.368, -0.071) +
        // vec3(0.0625, 0.5, 0.5);
    }

    byte[] mPixels = null;

    @Override
    public void drawFrame() {
        if (frameBuffer == null) {
            if (getWidth() != 0 && getHeight() != 0) {
                initFBO();
            } else {
                return;
            }
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);

        super.drawFrame();

        int size = getWidth() *getHeight() * 4;
        if (mPixels == null || mPixels.length != size) {
        	mPixels = null;

        	mPixels = new byte[size];
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(mPixels);
        byteBuffer.position(0);
        GLES20.glReadPixels(0, 0, getWidth(), getHeight(), GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
                byteBuffer);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        // for(int i = 0; i < pixels.length; i++) {
        // pixels[i] = (pixels[i] & (0xFF00FF00)) | ((pixels[i] >> 16) &
        // 0x000000FF) | ((pixels[i] << 16) & 0x00FF0000); //swap red and blue
        // to translate back to bitmap rgb style
        // }

        // // Bitmap image = Bitmap.createBitmap(pixels, getWidth(),
        // getHeight(), Bitmap.Config.ARGB_8888);
//         if (yuvPlanarData != null) {
//            yuv444PackedToNV21(pixels, yuvPlanarData, yuvFormat, getWidth(),
//            getHeight());
//            callback.yuvDataCreated(yuvPlanarData);
//         }

        callback.yuvDataCreated(mPixels,getWidth(),getHeight());
    }

    @Override
    protected void handleSizeChange() {
        int size = getWidth() * getHeight();
        if (size > 0) {
            yuvPlanarData = new byte[size * 3 / 2];
        }
        initFBO();
    }

    @Override
    public void setRenderSize(int width, int height) {
        boolean dirty = false;
        if (getWidth() != width || getHeight() != height)
            dirty = true;

        if (dirty) {
            super.setRenderSize(width, height);
        }
    }

    private void initFBO() {
        if (frameBuffer != null) {
            GLES20.glDeleteFramebuffers(1, frameBuffer, 0);
            frameBuffer = null;
        }
        if (texture_out != null) {
            GLES20.glDeleteTextures(1, texture_out, 0);
            texture_out = null;
        }
        if (depthRenderBuffer != null) {
            GLES20.glDeleteRenderbuffers(1, depthRenderBuffer, 0);
            depthRenderBuffer = null;
        }

        frameBuffer = new int[1];
        texture_out = new int[1];
        depthRenderBuffer = new int[1];
        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        GLES20.glGenRenderbuffers(1, depthRenderBuffer, 0);
        GLES20.glGenTextures(1, texture_out, 0);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture_out[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, getWidth(), getHeight(), 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, texture_out[0], 0);

        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRenderBuffer[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
                getWidth(), getHeight());
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, depthRenderBuffer[0]);

        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException(this
                    + ": Failed to set up render buffer YUVOutputCallbackwith status " + status
                    + " and error " + GLES20.glGetError());
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * project.android.imageprocessing.output.GLTextureInputRenderer#newTextureReady
     * (int, project.android.imageprocessing.input.GLTextureOutputRenderer)
     */
    @Override
    public void newTextureReady(int texture, GLTextureOutputRenderer source, boolean newData) {
        long begin = System.currentTimeMillis();
    	texture_in = texture;
        if (getWidth() <= 0 || getHeight() <= 0) {
            setRenderSize(source.getWidth(), source.getHeight());
        }
        onDrawFrame();
        long end = System.currentTimeMillis();
        Log.d("YUVOutput", "cast: " + (end - begin));
    }

    protected int getYuvFormat() {
        return yuvFormat;
    }

    protected void setYuvFormat(int yuvFormat) {
        this.yuvFormat = yuvFormat;
    }

    public static interface YUVOutputCallback {
        public void yuvDataCreated(byte[] yuvdata,int w,int h);
    }

}
