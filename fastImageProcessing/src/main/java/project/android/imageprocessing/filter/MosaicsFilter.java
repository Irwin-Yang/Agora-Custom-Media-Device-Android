package project.android.imageprocessing.filter;

import android.opengl.GLES20;

/**
 * Created by Administrator on 2018/4/12.
 */

public class MosaicsFilter extends MultiPixelRenderer {

    @Override
    protected String getFragmentShader() {
        return
                "precision mediump float;\n"
                        +"uniform sampler2D "+UNIFORM_TEXTURE0+";\n"
                        +"varying vec2 "+VARYING_TEXCOORD+";\n"
                        +"const vec2 TexSize = vec2(400.0, 400.0);\n"
                        +"const vec2 mosaicSize = vec2(8.0, 8.0);\n"

                        +"void main(){\n"
                        +"   vec2 intXY = vec2("+VARYING_TEXCOORD+".x*TexSize.x, "+VARYING_TEXCOORD+".y*TexSize.y);\n"
                        +"   vec2 XYMosaic = vec2(floor(intXY.x/mosaicSize.x)*mosaicSize.x, floor(intXY.y/mosaicSize.y)*mosaicSize.y);\n"
                        +"   vec2 UVMosaic = vec2(XYMosaic.x/TexSize.x, XYMosaic.y/TexSize.y);\n"
                        +"   vec4 color = texture2D("+UNIFORM_TEXTURE0+", UVMosaic);\n"
                        +"   gl_FragColor = color;\n"
                        +"}\n";
    }

    @Override
    protected void handleScale() {
        super.handleScale();
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false,
                mScaleMatrix, 0);
    }
}
