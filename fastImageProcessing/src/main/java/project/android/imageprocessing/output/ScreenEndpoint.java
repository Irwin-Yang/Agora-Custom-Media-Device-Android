package project.android.imageprocessing.output;

import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import project.android.imageprocessing.FastImageProcessingPipeline;
import project.android.imageprocessing.GLRenderer;
import project.android.imageprocessing.input.GLTextureOutputRenderer;

/**
 * A screen renderer extension of GLRenderer.
 * This class accepts a texture as input and renders it to the screen.
 *
 * @author Chris Batt
 */
public class ScreenEndpoint extends GLRenderer implements GLTextureInputRenderer {
    private FastImageProcessingPipeline rendererContext;

    /**
     * Creates a GLTextureToScreenRenderer.
     * If it is not set to full screen mode, the reference to the render context is allowed to be null.
     *
     * @param rendererContext   A reference to the GLSurfaceView.Renderer that contains the OpenGL context.
     * @param fullScreenTexture Whether or not to use the input filter size as the render size or to render full screen.
     */
    public ScreenEndpoint(FastImageProcessingPipeline rendererContext) {
        super();
        this.rendererContext = rendererContext;
    }

    @Override
    protected void initWithGLContext() {
        if (rendererContext == null) return;
        setRenderSize(rendererContext.getWidth(), rendererContext.getHeight());
        super.initWithGLContext();
    }

    @Override
    protected void handleScale() {
        super.handleScale();

        Matrix.setIdentityM(mScaleMatrix, 0);

        float scaleX = 1.0f, scaleY = 1.0f;

        float ratio = (float)
                480 / 640;
        float vratio = (float) getWidth() / getHeight();

        if (ratio < vratio) {
            scaleY = vratio / ratio;
        } else {
            scaleX = ratio / vratio;
        }

        Matrix.scaleM(mScaleMatrix, 0,
                scaleX * (1.0f),
                scaleY, 1);
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false,
                mScaleMatrix, 0);
    }

    /* (non-Javadoc)
         * @see project.android.imageprocessing.output.GLTextureInputRenderer#newTextureReady(int, project.android.imageprocessing.input.GLTextureOutputRenderer)
         */
    @Override
    public void newTextureReady(int texture, GLTextureOutputRenderer source, boolean newData) {
        texture_in = texture;
        setWidth(source.getWidth());
        setHeight(source.getHeight());
        onDrawFrame();
    }

    public void destroy() {
        super.destroy();
        this.rendererContext = null;
    }
}
