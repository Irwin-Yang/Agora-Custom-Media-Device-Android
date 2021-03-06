package project.android.imageprocessing.filter;

import java.util.ArrayList;
import java.util.List;

import project.android.imageprocessing.input.GLTextureOutputRenderer;

/**
 * An extension of MultiInputPixelFilter.  This class is similar to CompositeFilter except
 * it allows for multi-pixel calculations in the shaders.  For information about the extension of 
 * this filter, see  {@link CompositeFilter}.
 * @author Chris Batt
 */
public abstract class CompositeMultiPixelFilter extends MultiInputPixelFilter {
	private List<BasicFilter> initialFilters;
	private List<GLTextureOutputRenderer> terminalFilters;
	private List<GLTextureOutputRenderer> inputOutputFilters;
	private List<GLTextureOutputRenderer> filters;

	/**
	 * Creates a CompositeMultiPixelFilter with the default {@link BasicFilter} shaders that takes in a given number of inputs
	 * @param numOfInputs
	 * The number of inputs that this filter expects
	 */
	public CompositeMultiPixelFilter(int numOfInputs) {
		super(numOfInputs);
		initialFilters = new ArrayList<BasicFilter>();
		terminalFilters = new ArrayList<GLTextureOutputRenderer>();
		inputOutputFilters = new ArrayList<GLTextureOutputRenderer>();
		filters = new ArrayList<GLTextureOutputRenderer>();
	}
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.input.GLTextureOutputRenderer#destroy()
	 */
	@Override
	public void destroy() {
		super.destroy();
		for(GLTextureOutputRenderer filter : filters) {
			filter.destroy();
		}
	}
	
	/*
	 * If the source is one of the end points of the input filters then it is the result 
	 * of one of the internal filters. When all internal filters have finished we can
	 * draw the multi-input filter. If the source is not in the list of renderers then it 
	 * must be an external input which should be passed to each of the initial renderers
	 * of this multi-input filter.
	 */
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.filter.BasicFilter#newTextureReady(int, project.android.imageprocessing.input.GLTextureOutputRenderer)
	 */
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.filter.MultiInputFilter#newTextureReady(int, project.android.imageprocessing.input.GLTextureOutputRenderer)
	 */
	@Override
	public void newTextureReady(int texture, GLTextureOutputRenderer source, boolean newData) {
		if(inputOutputFilters.contains(source)) {
			if(!texturesReceived.contains(source)) {
				super.newTextureReady(texture, source, newData);
				for(BasicFilter initialFilter : initialFilters) {
					initialFilter.newTextureReady(texture, source, newData);
				}
			}
		} else if(terminalFilters.contains(source)) {
			super.newTextureReady(texture, source, newData);
		} else {
			for(BasicFilter initialFilter : initialFilters) {
				initialFilter.newTextureReady(texture, source, newData);
			}
		}
	}
	
	protected void registerFilter(GLTextureOutputRenderer filter) {
		if(!filters.contains(filter)) {
			filters.add(filter);
		}
	}
	
	protected void registerInitialFilter(BasicFilter filter) {
		initialFilters.add(filter);
		registerFilter(filter);
	}

	protected void registerInputOutputFilter(GLTextureOutputRenderer filter) {
		inputOutputFilters.add(filter);
		registerFilter(filter);
	}
	
	protected void registerTerminalFilter(GLTextureOutputRenderer filter) {
		terminalFilters.add(filter);
		registerFilter(filter);
	}
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.GLRenderer#setRenderSize(int, int)
	 */
	@Override
	public void setRenderSize(int width, int height) {
		for(GLTextureOutputRenderer filter : filters) {
			filter.setRenderSize(width, height);
		}
		super.setRenderSize(width, height);
	}

}
