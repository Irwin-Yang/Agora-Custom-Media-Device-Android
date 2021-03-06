package project.android.imageprocessing.filter;

import java.util.ArrayList;
import java.util.List;

import project.android.imageprocessing.input.GLTextureOutputRenderer;
import android.opengl.GLES20;
import android.util.Log;

/**
 * A multiple filter input extension of the BasicFilter. 
 * This class allows for multiple textures as inputs to the filter. 
 * This class can be used as the base for a filter which requires multiple filter inputs.
 * By itself, this class is not useful because it's fragment shader only uses one texture. 
 * To take advantage of the multiple texture inputs, the getFragmentShader() method should be
 * override to return a more useful fragment shader.  This class supports a maximum of 10
 * input textures. However, only one set of texture coordinates is used so any rotation done in 
 * this filter will be applied to all input images.
 * 
 * The position of the input textures should be registered before the filter attempts to draw.
 * For example, if this class was extended with a fragment shader that required a grey scale filter as the first input
 * texture and a blur filter as the second input, it would be set up as follows: 
 * <code>
 * 	GreyScaleFilter grey = new GreyScaleFilter();
 *  FastBlurFilter blur = new FastBlurFilter();
 *  SomeMultiInputFilter multiInput = new SomeMultiInputFilter();
 *  grey.addTarget(multiInput);
 *  blur.addTarget(multiInput);
 *  multiInput.registerFilterLocation(grey, 0);
 *  multiInput.registerFilterLocation(blur, 1);
 * </code>
 * @author Chris Batt
 */
public abstract class MultiInputFilter extends BasicFilter {
	private int numOfInputs;
	private int[] textureHandle;
	protected int[] texture;
	protected List<GLTextureOutputRenderer> texturesReceived;
	protected List<GLTextureOutputRenderer> filterLocations;
	
	/**
	 * Creates a MultiInputFilter with any number of initial filters or filter graphs that produce a
	 * set number of textures which can be used by this filter.
	 * @param numOfInputs
	 * The number of inputs of this filter.  For example, if the fragment shader of this filter 
	 * requires three input textures, then this would be set to three.
	 */
	public MultiInputFilter(int numOfInputs) {
		super();
		this.numOfInputs = numOfInputs;
		textureHandle = new int[numOfInputs-1];
		texture = new int[numOfInputs-1];
		texturesReceived = new ArrayList<GLTextureOutputRenderer>(numOfInputs);
		filterLocations = new ArrayList<GLTextureOutputRenderer>(numOfInputs);
	}
	
	/**
	 * Removes all currently registered filters from filter location list.  
	 */
	public void clearRegisteredFilterLocations() {
		filterLocations.clear();
	}
	
	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
		for(int i = 0; i < numOfInputs-1; i++) {
			textureHandle[i] = GLES20.glGetUniformLocation(programHandle, UNIFORM_TEXTUREBASE+(i+1));
		}
	}
	
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.filter.BasicFilter#newTextureReady(int, project.android.imageprocessing.input.GLTextureOutputRenderer)
	 */
	@Override
	public synchronized void newTextureReady(int texture, GLTextureOutputRenderer source, boolean newData) {
		if(!texturesReceived.contains(source)) {
			texturesReceived.add(source);
			if(newData) {
				markAsDirty();
			}
		}
		int pos = filterLocations.lastIndexOf(source);
		if(pos == 0) {
			texture_in = texture;
		} else {
			if (pos < 0)
				return ;
			this.texture[pos-1] = texture;
		}
		if(texturesReceived.size() == numOfInputs) {
			setWidth(source.getWidth());
			setHeight(source.getHeight());
			onDrawFrame();
			texturesReceived.clear();
		}		
	}
	
	@Override
	protected void passShaderValues() {
		super.passShaderValues();
		int tex = 0;
		for(int i = 0; i < numOfInputs-1; i++) {
			switch(i) {
				case 0: tex = GLES20.GL_TEXTURE1; break;
				case 1: tex = GLES20.GL_TEXTURE2; break;
				case 2: tex = GLES20.GL_TEXTURE3; break;
				case 3: tex = GLES20.GL_TEXTURE4; break;
				case 4: tex = GLES20.GL_TEXTURE5; break;
				case 5: tex = GLES20.GL_TEXTURE6; break;
				case 6: tex = GLES20.GL_TEXTURE7; break;
				case 7: tex = GLES20.GL_TEXTURE8; break;
				case 8: tex = GLES20.GL_TEXTURE9; break;
			}
			GLES20.glActiveTexture(tex);
		    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[i]);
			GLES20.glUniform1i(textureHandle[i], i+1);
		}
	}
	
	/**
	 * Registers the given filter in the next available texture location.
	 * @param filter
	 * An output filter which passes its output to this filter.
	 */
	public void registerFilterLocation(GLTextureOutputRenderer filter) {
		if(!filterLocations.contains(filter)) {
			filterLocations.add(filter);
		}
	}
	
	/**
	 * Registers the given filter in the given texture location.
	 * @param filter
	 * An output filter which passes its output to this filter.
	 * @param location
	 * The texture location that this filter should pass its output to. This location must be in [0,numOfInputs).
	 */
	public void registerFilterLocation(GLTextureOutputRenderer filter, int location) {
		if(filterLocations.contains(filter)) {
			filterLocations.remove(filter);
		}
		filterLocations.add(location, filter);
	}
}
