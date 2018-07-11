package project.android.imageprocessing.filter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import project.android.imageprocessing.helper.ImageHelper;
import project.android.imageprocessing.helper.Rotation;
import project.android.imageprocessing.helper.TextureRotationUtil;
import project.android.imageprocessing.input.GLTextureOutputRenderer;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.test.MoreAsserts;
import android.util.Log;

public class MultiImageFilter extends BasicFilter {
    
	private static final String TAG = "MultiImageFilter";
	
    private int[] mMoreFilterInputTextureUniforms;
    private int[] mMoreFilterSourceTextures;
    private Bitmap[] mBitmap;
    
    private int[] mFilterTextureCoordinateAttributes;
	private ByteBuffer[] mTexture2CoordinatesBuffers;
	
	private String mMoreInputTextureCoordinate = "";
	private String mMoreTextureCoordinate = "";
	private String mTexture2Coordinates = "";
    
    private boolean mMoreVertex = false;
	
	public MultiImageFilter(boolean vertex) {
		this.mMoreVertex = vertex;
	}
	
	@Override
	public void destroy() {
		super.destroy();
		if (mMoreFilterSourceTextures != null) {
		for (int i = 0; i < mMoreFilterSourceTextures.length; i++) {
			if (mMoreFilterSourceTextures[i] != ImageHelper.NO_TEXTURE) {
				int[] arrayOfInt = new int[1];
	            arrayOfInt[0] = mMoreFilterSourceTextures[i];
	            GLES20.glDeleteTextures(1, arrayOfInt, 0);
	            mMoreFilterSourceTextures[i] = ImageHelper.NO_TEXTURE;
			}
		}
		}
	}
	
	@Override
	protected String getVertexShader() {
		return "attribute vec4 position;\n" +
	            "attribute vec4 inputTextureCoordinate;\n" +
	            mMoreInputTextureCoordinate +
	            " \n" +
	            "varying vec2 textureCoordinate;\n" +
	            mMoreTextureCoordinate +
	            " \n" +
	            "void main()\n" +
	            "{\n" +
	            "    gl_Position = position;\n" +
	            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
	            mTexture2Coordinates + 
	            "}";
	}
	
	@Override
	public synchronized void newTextureReady(int texture, GLTextureOutputRenderer source,
			boolean newData) {
		// TODO Auto-generated method stub
//		Log.d(TAG, this.getClass().getSimpleName() + ".newTextureReady " + "; pause: " + isPause);
		if (isPause)
			return ;
		super.newTextureReady(texture, source, newData);
	}
	
	
	boolean isPause = false;
	public void pause() {
		isPause = true;
	}
	
	public void start() {
		isPause = false;
	}

	@Override
	protected void passShaderValues() {
		super.passShaderValues();
		if (mBitmap == null)
			return ;
		
		for (int i = 0; i < mBitmap.length; i++) {
			if (mMoreFilterSourceTextures[i] == ImageHelper.NO_TEXTURE)
				mMoreFilterSourceTextures[i] = ImageHelper.loadTexture(mBitmap[i], ImageHelper.NO_TEXTURE, true);
			if (mMoreVertex) {
				GLES20.glEnableVertexAttribArray(mFilterTextureCoordinateAttributes[i]);
			}
			if (mMoreVertex) {
            	mTexture2CoordinatesBuffers[i].position(0);
            	GLES20.glVertexAttribPointer(mFilterTextureCoordinateAttributes[i], 2, 5126, false, 0, mTexture2CoordinatesBuffers[i]);
            }
			GLES20.glActiveTexture(GLES20.GL_TEXTURE3 + i);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mMoreFilterSourceTextures[i]);
            GLES20.glUniform1i(mMoreFilterInputTextureUniforms[i], i + 3);
            
            
		}
	}

	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
		if (mMoreFilterInputTextureUniforms == null) 
			return;
		for (int i = 0; i < mBitmap.length; i++) {
			mMoreFilterSourceTextures[i] = ImageHelper.loadTexture(mBitmap[i], ImageHelper.NO_TEXTURE, true);
		}
		for (int i = 0; i < mMoreFilterInputTextureUniforms.length; i++) {
			mMoreFilterInputTextureUniforms[i] = GLES20
	                .glGetUniformLocation(getProgram(), UNIFORM_TEXTUREBASE + (i+2));
			if (mMoreVertex)
				mFilterTextureCoordinateAttributes[i] = GLES20.glGetAttribLocation(getProgram(), "inputTextureCoordinate" + (i + 2));
		}
	}
	
	private int getProgram() {
		return programHandle;
	}

	public void setBitmap(Bitmap[] paramArrayOfBitmap) {
		if (paramArrayOfBitmap == null || paramArrayOfBitmap.length == 0)
			return ;
		mBitmap = paramArrayOfBitmap;
		mMoreFilterInputTextureUniforms = new int[mBitmap.length];
		mMoreFilterSourceTextures = new int[mBitmap.length];
		if (mMoreVertex)
		mFilterTextureCoordinateAttributes = new int[mBitmap.length];
		mTexture2CoordinatesBuffers = new ByteBuffer[mBitmap.length];
		for (int i = 0; i < mBitmap.length; i++) {
			mMoreFilterSourceTextures[i] = ImageHelper.NO_TEXTURE;
			if (mMoreVertex) {
				int pos = i + 2;
				mMoreInputTextureCoordinate += ("attribute vec4 inputTextureCoordinate" + pos + ";\n");
				mMoreTextureCoordinate += ("varying vec2 textureCoordinate" + pos + ";\n");
				mTexture2Coordinates += ("textureCoordinate" + pos +" = inputTextureCoordinate" + pos + ".xy;\n");
				mTexture2CoordinatesBuffers[i] = setRotation(Rotation.NORMAL, false, false);
			}
		}
		
	}
	
	public ByteBuffer setRotation(Rotation paramRotation, boolean paramBoolean1, boolean paramBoolean2)
    {
        float[] arrayOfFloat = TextureRotationUtil.getRotation(paramRotation, paramBoolean1, paramBoolean2);
        ByteBuffer localByteBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());
        FloatBuffer localFloatBuffer = localByteBuffer.asFloatBuffer();
        localFloatBuffer.put(arrayOfFloat);
        localFloatBuffer.flip();
        return localByteBuffer;
    }
}
