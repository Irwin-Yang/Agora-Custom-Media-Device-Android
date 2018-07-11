package project.android.imageprocessing.input;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import project.android.imageprocessing.helper.ImageHelper;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

/**
 * A image input renderer extension of the BasicFilter. 
 * This class takes an image as input and processes it so that it can be sent to other filters.
 * The image can be changed at any time without creating a new GLImageToTextureRenderer by using the setImage(int resourceId) method.
 * @author Chris Batt
 */
public class ImageResourceInput extends GLTextureOutputRenderer {
	private Context context;
	private GLSurfaceView view;
	private Bitmap bitmap;
	private int imageWidth;
	private int imageHeight;
	private boolean newBitmap;
	
	/**
	 * Creates a GLImageToTextureRenderer using the given bitmap as the image input. 
	 * @param bitmap
	 * The bitmap which contains the image.
	 */
	public ImageResourceInput(GLSurfaceView view, Bitmap bitmap) {
		this.view = view;
		setImage(bitmap);
	}
	
	/**
	 * Creates a GLImageToTextureRenderer using the given resourceId as the image input. 
	 * All future images must also come from the same context.
	 * @param context
	 * The context in which the resourceId exists.
	 * @param resourceId
	 * The resource id of the image which should be processed.
	 */
	public ImageResourceInput(GLSurfaceView view, Context context, int resourceId) {
		this.context = context;
		this.view = view;
		setImage(resourceId);
	}
	
	/**
	 * Creates a GLImageToTextureRenderer using the given file path to the image input. 
	 * @param pathName
	 * The file path to the image to load.
	 */
	public ImageResourceInput(GLSurfaceView view, String pathName) {
		this.view = view;
		setImage(pathName);
	}
	
	@Override
	protected void drawFrame() {
		if(newBitmap) {
			loadTexture();
		}
		super.drawFrame();
	}
	
	/**
	 * Returns the height of the current image being output.
	 * @return image height
	 */
	public int getImageHeight() {
		return imageHeight;
	}
	
	/**
	 * Returns the width of the current image being output.
	 * @return image width
	 */
	public int getImageWidth() {
		return imageWidth;
	}
	
	private void loadImage(Bitmap bitmap) {
		this.bitmap = bitmap;
        imageWidth = bitmap.getWidth();
        imageHeight = bitmap.getHeight();
		setRenderSize(imageWidth, imageHeight);
		newBitmap = true;
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
		view.requestRender();
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
		newBitmap = true;
	}
	
	private void loadTexture() 	{	
		if(texture_in != 0) {
			int[] tex = new int[1];
			tex[0] = texture_in;
			GLES20.glDeleteTextures(1, tex, 0);
		}
		texture_in = ImageHelper.bitmapToTexture(bitmap);
		newBitmap = false;
		markAsDirty();
	}
	
	/**
	 * Sets the image being output by this renderer to the given bitmap.
	 * @param bitmap
	 * The bitmap which contains the image.
	 */
	public void setImage(Bitmap bitmap) {
		loadImage(bitmap);
	}

	/**
	 * Sets the image being output by this renderer to the image loaded from the given id.
	 * @param resourceId
	 * The resource id of the new image to be output by this renderer.
	 */
	public void setImage(int resourceId) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
		loadImage(BitmapFactory.decodeResource(context.getResources(), resourceId, options));
	}
	
	/**
	 * Sets the image being output by this renderer to the image loaded from the given file path.
	 * @param filePath
	 * The file path to the image to load.
	 */
	public void setImage(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		loadImage(BitmapFactory.decodeFile(filePath, options));
	}
}
