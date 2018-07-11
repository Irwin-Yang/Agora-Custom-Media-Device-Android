package project.android.imageprocessing.filter;

import java.util.HashMap;
import java.util.Map;

import android.graphics.PointF;
import android.opengl.GLES20;

/**
 * An extension of BasicFilter.  This class provides a generic filter that can be used to create any single
 * pass filter.  The fragment and vertex shaders must be set or it will default to the basicfilter shaders. Any other parameter
 * required for the shaders can be added to the generic filter using the add methods provided.
 * @author Chris Batt
 */
public class GenericFilter extends BasicFilter {
	private String fragmentShader;
	private String vertexShader;
	private Map<String, Integer> uniformHandleList;
	private Map<String, Integer> uniformIntList;
	private Map<String, Float> uniformFloatList;
	private Map<String, PointF> uniformVec2List;
	private Map<String, float[]> uniformVec3List;
	private Map<String, float[]> uniformVec4List;
	private Map<String, float[]> uniformMat3List;
	private Map<String, float[]> uniformMat4List;
	private Map<String, float[]> uniformFloatArrayList;
	
	/**
	 * Creates a GenericFilter with the BasicFilter shaders.
	 */
	public GenericFilter() {
		uniformHandleList = new HashMap<String, Integer>();
		uniformIntList = new HashMap<String, Integer>();
		uniformFloatList = new HashMap<String, Float>();
		uniformVec2List = new HashMap<String, PointF>();
		uniformVec3List = new HashMap<String, float[]>();
		uniformVec4List = new HashMap<String, float[]>();
		uniformMat3List = new HashMap<String, float[]>();
		uniformMat4List = new HashMap<String, float[]>();
		uniformFloatArrayList = new HashMap<String, float[]>();
		setFragmentShader(super.getFragmentShader());
		setVertexShader(super.getVertexShader());
	}
	
	/**
	 * Adds an integer value parameter to the shader.
	 * @param name
	 * The uniform name that is used in the shader to reference this value.
	 * @param value
	 * The value that should be used for this name.
	 */
	public void addUniformInteger(String name, int value) {
		if(!uniformIntList.containsKey(name)) {
			reInitialize();
		}
		uniformIntList.put(name, value);
	}
	
	/**
	 * Adds an float value parameter to the shader.
	 * @param name
	 * The uniform name that is used in the shader to reference this value.
	 * @param value
	 * The value that should be used for this name.
	 */
	public void addUniformFloat(String name, float value) {
		if(!uniformFloatList.containsKey(name)) {
			reInitialize();
		}
		uniformFloatList.put(name, value);
	}

	/**
	 * Adds an vec2 value parameter to the shader.
	 * @param name
	 * The uniform name that is used in the shader to reference this value.
	 * @param value
	 * The value that should be used for this name.
	 */
	public void addUniformVec2(String name, PointF value) {
		if(!uniformVec2List.containsKey(name)) {
			reInitialize();
		}
		uniformVec2List.put(name, value);
	}
	
	/**
	 * Adds an vec3 value parameter to the shader.
	 * @param name
	 * The uniform name that is used in the shader to reference this value.
	 * @param value
	 * The value that should be used for this name.
	 */
	public void addUniformVec3(String name, float[] value) {
		if(!uniformVec3List.containsKey(name)) {
			reInitialize();
		}
		uniformVec3List.put(name, value);
	}

	/**
	 * Adds an vec4 value parameter to the shader.
	 * @param name
	 * The uniform name that is used in the shader to reference this value.
	 * @param value
	 * The value that should be used for this name.
	 */
	public void addUniformVec4(String name, float[] value) {
		if(!uniformVec4List.containsKey(name)) {
			reInitialize();
		}
		uniformVec4List.put(name, value);
	}

	/**
	 * Adds an mat3 value parameter to the shader.
	 * @param name
	 * The uniform name that is used in the shader to reference this value.
	 * @param value
	 * The value that should be used for this name.
	 */
	public void addUniformMat3(String name, float[] value) {
		if(!uniformMat3List.containsKey(name)) {
			reInitialize();
		}
		uniformMat3List.put(name, value);
	}
	
	/**
	 * Adds an mat4 value parameter to the shader.
	 * @param name
	 * The uniform name that is used in the shader to reference this value.
	 * @param value
	 * The value that should be used for this name.
	 */
	public void addUniformMat4(String name, float[] value) {
		if(!uniformMat4List.containsKey(name)) {
			reInitialize();
		}
		uniformMat4List.put(name, value);
	}
	
	/**
	 * Adds an float array value parameter to the shader.
	 * @param name
	 * The uniform name that is used in the shader to reference this value.
	 * @param value
	 * The value that should be used for this name.
	 */
	public void addUniformFloatArray(String name, float[] value) {
		if(!uniformFloatArrayList.containsKey(name)) {
			reInitialize();
		}
		uniformFloatArrayList.put(name, value);
	}
	
	/**
	 * Sets the fragment shader to be used for this program. If this is changed during filter processing, it will
	 * be updated on the next drawing pass.
	 * @param fragmentShader
	 * The fragment shader to use.
	 */
	public void setFragmentShader(String fragmentShader) {
		this.fragmentShader = fragmentShader;
		reInitialize();
	}
	
	/**
	 * Sets the vertex shader to be used for this program. If this is changed during filter processing, it will
	 * be updated on the next drawing pass.
	 * @param vertexShader
	 * The vertex shader to use.
	 */
	public void setVertexShader(String vertexShader) {
		this.vertexShader = vertexShader;
		reInitialize();
	}
	
	@Override
	protected String getFragmentShader() {
		return fragmentShader;
	}
	
	@Override
	protected String getVertexShader() {
		return vertexShader;
	}
	
	@Override
	protected void passShaderValues() {
		super.passShaderValues();
		for(String key : uniformIntList.keySet()) {
			GLES20.glUniform1f(uniformHandleList.get(key), uniformIntList.get(key));
		}
		for(String key : uniformFloatList.keySet()) {
			GLES20.glUniform1f(uniformHandleList.get(key), uniformFloatList.get(key));
		}
		for(String key : uniformVec2List.keySet()) {
			GLES20.glUniform2f(uniformHandleList.get(key), uniformVec2List.get(key).x, uniformVec2List.get(key).y);
		}
		for(String key : uniformVec3List.keySet()) {
			GLES20.glUniform3f(uniformHandleList.get(key), uniformVec3List.get(key)[0], uniformVec3List.get(key)[1], uniformVec3List.get(key)[2]);
		}
		for(String key : uniformVec4List.keySet()) {
			GLES20.glUniform4f(uniformHandleList.get(key), uniformVec4List.get(key)[0], uniformVec4List.get(key)[1], uniformVec4List.get(key)[2], uniformVec4List.get(key)[3]);
		}
		for(String key : uniformMat3List.keySet()) {
			GLES20.glUniformMatrix3fv(uniformHandleList.get(key), 1, false, uniformMat3List.get(key), 0);
		}
		for(String key : uniformMat4List.keySet()) {
			GLES20.glUniformMatrix4fv(uniformHandleList.get(key), 1, false, uniformMat4List.get(key), 0);
		}
		for(String key : uniformFloatArrayList.keySet()) {
			GLES20.glUniform1fv(uniformHandleList.get(key), uniformFloatArrayList.get(key).length, uniformFloatArrayList.get(key), 0);
		}
	}
	
	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
		for(String key : uniformIntList.keySet()) {
			uniformHandleList.put(key, GLES20.glGetUniformLocation(programHandle, key));
		}
		for(String key : uniformFloatList.keySet()) {
			uniformHandleList.put(key, GLES20.glGetUniformLocation(programHandle, key));
		}
	}
}
