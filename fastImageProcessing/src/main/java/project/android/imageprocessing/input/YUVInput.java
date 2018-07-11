package project.android.imageprocessing.input;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

public class YUVInput extends GLTextureOutputRenderer {
	protected GLSurfaceView view;
	private int imageWidth;
	private int imageHeight;
	private boolean newYuvData;
	private int luminanceTexture, chrominanceUTexture, chrominanceVTexture;
	private int yuvConversionLuminanceTextureUniform, yuvConversionChrominanceUTextureUniform, yuvConversionChrominanceVTextureUniform;;
	
	public YUVInput(GLSurfaceView view) {
		this.view = view;
	}
	
	private int genAndBindTexture() {
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	    return textures[0];
	}
	
	@Override
	protected void initWithGLContext() {
		super.initWithGLContext();
		texture_in = genAndBindTexture();
	}
	
	@Override
	protected void drawFrame() {
		if(newYuvData) {
			newYuvData = false;
			markAsDirty();
		}
		super.drawFrame();
	}
	
	@Override
	protected String getFragmentShader() {
		String frag = 
				"varying highp vec2 " + VARYING_TEXCOORD + ";\n"
				+ "uniform sampler2D luminanceTexture;\n"
				+ "uniform sampler2D chrominanceUTexture;\n"
				+ "uniform sampler2D chrominanceVTexture;\n"
				+ "void main()\n"
				+ "{\n"
				+ "     mediump vec3 yuv;\n"
				+ "     lowp vec3 rgb;\n"
				+ "     yuv.x = texture2D(luminanceTexture, " + VARYING_TEXCOORD + ").r;\n"
				+ "     yuv.y = texture2D(chrominanceUTexture, " + VARYING_TEXCOORD + ").r - 0.5;\n"
				+ "     yuv.z = texture2D(chrominanceVTexture, " + VARYING_TEXCOORD + ").r - 0.5;\n"
				+ "     rgb = mat3(  1,			1,			1,\n"
				+ "                 -0.00093,	-0.3437,	1.77216,\n"
				+ "                  1.401687,	-0.71417,	0.00099) * yuv;\n"
				+ "     gl_FragColor = vec4(rgb, 1);\n"
				+ "}\n";
		return frag;
	}
	
	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
		yuvConversionLuminanceTextureUniform = GLES20.glGetUniformLocation(programHandle, "luminanceTexture");
		yuvConversionChrominanceUTextureUniform = GLES20.glGetUniformLocation(programHandle, "chrominanceUTexture");
		yuvConversionChrominanceVTextureUniform = GLES20.glGetUniformLocation(programHandle, "chrominanceVTexture");
		
		IntBuffer textures = IntBuffer.allocate(3);
		GLES20.glGenTextures(3, textures);
		luminanceTexture = textures.get(0);
		chrominanceUTexture = textures.get(1);
		chrominanceVTexture = textures.get(2);
	}
	
	@Override
	protected void passShaderValues() {
		// TODO Auto-generated method stub
		super.passShaderValues();
	}
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.input.GLTextureOutputRenderer#destroy()
	 */
	@Override
	public void destroy() {
		super.destroy();
		if(texture_in != 0) {
			int[] tex = new int[1];
			tex[0] = texture_in;
			GLES20.glDeleteTextures(1, tex, 0);
		}
		IntBuffer tex = IntBuffer.allocate(3);
		int count = 0;
		if (luminanceTexture > 0)
			tex.put(luminanceTexture);
		if (chrominanceUTexture > 0)
			tex.put(chrominanceUTexture);
		if (chrominanceVTexture > 0)
			tex.put(chrominanceVTexture);
		if (count > 0)
			GLES20.glDeleteTextures(count, tex);
		newYuvData = true;
	}

	public void setYuvData(byte[] yuvData, int width, int height) {
		imageWidth = width;
		imageHeight = height;
		setRenderSize(imageWidth, imageHeight);
		newYuvData = true;
		textureVertices = new FloatBuffer[4];
		
		float[] texData0 = new float[] {
	        0.0f, 1.0f,
	        1.0f, 1.0f,
	        0.0f, 0.0f,
	        1.0f, 0.0f,
		};
		textureVertices[0] = ByteBuffer.allocateDirect(texData0.length * 4).order(ByteOrder. nativeOrder()).asFloatBuffer();
		textureVertices[0].put(texData0).position(0);
		
		float[] texData1 = new float[] {
	        1.0f, 1.0f,
	        1.0f, 0.0f,
	        0.0f, 1.0f,
	        0.0f, 0.0f,
		};
		textureVertices[1] = ByteBuffer.allocateDirect(texData1.length * 4).order(ByteOrder. nativeOrder()).asFloatBuffer();
		textureVertices[1].put(texData1).position(0);
			
		float[] texData2 = new float[] {
	        1.0f, 0.0f,
	        0.0f, 0.0f,
	        1.0f, 1.0f,
	        0.0f, 1.0f,
		};
		textureVertices[2] = ByteBuffer.allocateDirect(texData2.length * 4).order(ByteOrder. nativeOrder()).asFloatBuffer();
		textureVertices[2].put(texData2).position(0);
		
		float[] texData3 = new float[] {
	        0.0f, 0.0f,
	        0.0f, 1.0f,
	        1.0f, 0.0f,
	        1.0f, 1.0f,
		};
		textureVertices[3] = ByteBuffer.allocateDirect(texData3.length * 4).order(ByteOrder. nativeOrder()).asFloatBuffer();
		textureVertices[3].put(texData3).position(0);
		
		ByteBuffer pixels[] = new ByteBuffer[3];
		
		pixels[0] = ByteBuffer.wrap(yuvData, 0, width*height);
		pixels[1] = ByteBuffer.wrap(yuvData, width*height, width*height/4);
		pixels[2] = ByteBuffer.wrap(yuvData, width*height + width*height/4, width*height/4);
		
	    int widths[]  = { width, width / 2, width / 2 };
	    int heights[] = { width, height / 2, height / 2 };

	    // Y-plane
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
	    luminanceTexture = genAndBindTexture();
	    GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, widths[0], heights[0], 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, pixels[0]);
	    GLES20.glUniform1i(yuvConversionLuminanceTextureUniform, 4);
	    
	    // U-plane
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
	    chrominanceUTexture = genAndBindTexture();
	    GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, widths[1], heights[1], 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, pixels[1]);
	    GLES20.glUniform1i(yuvConversionChrominanceUTextureUniform, 5);

	    // V-plane
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE6);
	    chrominanceVTexture = genAndBindTexture();
	    GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, widths[2], heights[2], 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, pixels[2]);
	    GLES20.glUniform1i(yuvConversionChrominanceVTextureUniform, 6);
	    
		view.requestRender();
	}
}
