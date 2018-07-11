package project.android.imageprocessing.filter;

import java.util.ArrayList;
import java.util.List;

import project.android.imageprocessing.input.GLTextureOutputRenderer;
import project.android.imageprocessing.output.GLTextureInputRenderer;

/**
 * A multiple filter renderer extension of the BasicFilter. 
 * This class allows for a filter that contains multiple filters to create the output. 
 * This class can be used as the base for a filter which is made up of multiple filters. 
 * Similar to the CompositeFilter, the GroupFilter consists of multiple filters.  
 * The difference is that the CompositeFilter has its own shaders whereas the GroupFilter does not.
 * This class is simply a wrapper for a small pipeline of filters. Like the CompositeFilter, all filters
 * must be registered. The filters that begin the internal pipeline of this filter should be registered as
 * initial filters. The filters that end the pipeline and produce output for the next filter in the external chain
 * should be registered as terminal filters.  All other filters that are internal to this class should be
 * registered using registerFilter(BasicFilter filter).  In most cases there should only be one terminalFilter because
 * there is no way to separate the outputs into different streams. 
 * @author Chris Batt
 */
public abstract class GroupFilter extends BasicFilter {
	
	private List<BasicFilter> initialFilters;
	private List<BasicFilter> filters;
	private List<BasicFilter> terminalFilters;
	
	/**
	 * Creates a GroupFilter with any number of initial filters or filter graphs.
	 */
	public GroupFilter() {
		initialFilters = new ArrayList<BasicFilter>();
		terminalFilters = new ArrayList<BasicFilter>();
		filters = new ArrayList<BasicFilter>();
	}
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.input.GLTextureOutputRenderer#destroy()
	 */
	@Override
	public void destroy() {
		super.destroy();
		for(BasicFilter filter : filters) {
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
	@Override
	public void newTextureReady(int texture, GLTextureOutputRenderer source, boolean newData) {
		if(terminalFilters.contains(source)) {
			setWidth(source.getWidth());
			setHeight(source.getHeight());
			synchronized(getLockObject()) {
				for(GLTextureInputRenderer target : getTargets()) {
					target.newTextureReady(texture, this, newData);
				}
			}
		} else {
			for(BasicFilter initialFilter : initialFilters) {
				initialFilter.newTextureReady(texture, source, newData);
			}
		}
	}
	
	protected void registerFilter(BasicFilter filter) {
		if(!filters.contains(filter)) {
			filters.add(filter);
		}
	}

	protected void registerInitialFilter(BasicFilter filter) {
		initialFilters.add(filter);
		registerFilter(filter);
	}
	
	protected void registerTerminalFilter(BasicFilter filter) {
		terminalFilters.add(filter);
		registerFilter(filter);
	}
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.GLRenderer#setRenderSize(int, int)
	 */
	@Override
	public void setRenderSize(int width, int height) {
		for(BasicFilter filter : filters) {
			filter.setRenderSize(width, height);
		}
	}
}
