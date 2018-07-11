package project.android.imageprocessing.filter;

import android.opengl.GLES20;

/**
 * Created by Administrator on 2018/4/12.
 */

public class BlurFilter extends MultiPixelRenderer {

    private int mMaskSize=11;

    @Override
    protected String getFragmentShader() {

        float hStep = 1.0f / getWidth();
        float vStep = 1.0f / getHeight();

        return  "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                //"in" attributes from our vertex shader
                "varying vec2 "+VARYING_TEXCOORD+";\n" +

                //declare uniforms
                "uniform samplerExternalOES "+UNIFORM_TEXTURE0+";\n" +

                "float normpdf(in float x, in float sigma) {\n" +
                "    return 0.39894 * exp(-0.5 * x * x / (sigma * sigma)) / sigma;\n" +
                "}\n" +


                "void main() {\n" +
                "    vec3 c = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+").rgb;\n" +

                //declare stuff
                "    const int mSize = " + mMaskSize + ";\n" +
                "    const int kSize = (mSize - 1) / 2;\n" +
                "    float kernel[ mSize];\n" +
                "    vec3 final_colour = vec3(0.0);\n" +

                //create the 1-D kernel
                "    float sigma = 7.0;\n" +
                "    float Z = 0.0;\n" +
                "    for (int j = 0; j <= kSize; ++j) {\n" +
                "        kernel[kSize + j] = kernel[kSize - j] = normpdf(float(j), sigma);\n" +
                "    }\n" +

                //get the normalization factor (as the gaussian has been clamped)
                "    for (int j = 0; j < mSize; ++j) {\n" +
                "        Z += kernel[j];\n" +
                "    }\n" +

                //read out the texels
                "    for (int i = -kSize; i <= kSize; ++i) {\n" +
                "        for (int j = -kSize; j <= kSize; ++j) {\n" +
                "            final_colour += kernel[kSize + j] * kernel[kSize + i] * texture2D("+UNIFORM_TEXTURE0+", ("+VARYING_TEXCOORD+".xy + vec2(float(i)*" + floatToString(hStep) + ", float(j)*" + floatToString(vStep) + "))).rgb;\n" +
                "        }\n" +
                "    }\n" +

                "    gl_FragColor = vec4(final_colour / (Z * Z), 1.0);\n" +
                "}";
    }

    @Override
    protected void handleScale() {
        super.handleScale();
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false,
                mScaleMatrix, 0);
    }

    protected static String floatToString(float value) {
        String result = String.valueOf(value);
        result = result.replace(",", ".");
        return result;
    }
}
