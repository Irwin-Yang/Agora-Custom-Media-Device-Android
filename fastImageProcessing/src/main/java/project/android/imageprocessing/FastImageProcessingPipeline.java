package project.android.imageprocessing;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;


/**
 * Filter pipeline renderer implementation of the GLSurfaceView.Renderer.  In addition to the GLSurfaceView.Renderer methods, 
 * this class provides methods for processing the given graph of filters.  This graph of filters can be set by creating one 
 * or more filter graphs and then passing the root of the graphs to this class using addRootRenderer(GLRenderer rootRenderer).
 * This class will not start processing the filters until startRendering() has been called.  Once it has started rendering, 
 * the filter graph should not be changed without first calling pauseRendering().  Although this is theoretically not required,
 * it is recommended.  If a filter is removed from the processing pipeline, addFilterToDestroy(GLRenderer filter) should be called
 * to clean up opengl memory.
 * @author Chris Batt
 */
public class FastImageProcessingPipeline implements Renderer {
	private boolean rendering;
	private List<GLRenderer> rootRenderers;
	private int width;
	private int height;

	public interface SizeChanged {
		public void onSizeChanged(int width, int height);
	}

	private SizeChanged mSizeChanged = null;
	
	private List<GLRenderer> filtersToDestroy;

	public void setSizeChanged(SizeChanged mSizeChanged) {
		this.mSizeChanged = mSizeChanged;
	}

	/**
	 * Creates a FastImageProcessingPipeline with the initial state as paused and having no rootRenderer.
	 */
	public FastImageProcessingPipeline() {
		rendering = false;
		filtersToDestroy = new ArrayList<GLRenderer>();
		rootRenderers = new ArrayList<GLRenderer>();
	}
	
	/**
	 * Adds a given filter to the list of filters to have its resources removed next time this pipeline 
	 * receives an opengl context.  The filter will still be usable and will recreate all of the destroyed
	 * opengl objects next time it is used in the pipeline.
	 * @param renderer
	 */
	public void addFilterToDestroy(GLRenderer renderer) {
		synchronized(filtersToDestroy) {
			filtersToDestroy.add(renderer);
		}
	}
	
	/**
	 * Adds a root node of graph of filters that the pipeline will process and draw to the given endpoints of the graph.
	 * @param rootRenderer 
	 * A root node (input node) of the graph of filters and endpoints.
	 */
	public synchronized void addRootRenderer(GLRenderer rootRenderer) {
		rootRenderers.add(rootRenderer);
	}

	/**
	 * Returns the height of GLSurfaceView on the screen.
	 * @return height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Returns the width of GLSurfaceView on the screen.
	 * @return width
	 */
	public int getWidth() {
		return width;
	}
	
	private synchronized boolean isRendering() {
		return rendering;
	}
	
	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
	 */
	@Override
	public void onDrawFrame(GL10 unused) {
		if(isRendering()) {
			for(int i = 0; i < rootRenderers.size(); i++) {
				GLRenderer rootRenderer;
				synchronized(this) {
					rootRenderer = rootRenderers.get(i);
				}
				rootRenderer.onDrawFrame();
				
			}
		}
		synchronized(filtersToDestroy) {
			for(GLRenderer renderer : filtersToDestroy) {
				renderer.destroy();
			}
			filtersToDestroy.clear();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
	 */
	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		this.width = width;
		this.height = height;

		if (mSizeChanged != null)
			mSizeChanged.onSizeChanged(width, height);
	}

	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 */
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	}

	/**
	 * Pauses the rendering of the graph. This method should be called before the alteration of the filter graph; however,
	 * altering the filter graph without pauses should still work.
	 */
	public synchronized void pauseRendering() {
		rendering = false;
	}

	/**
	 * Removes a root node of graph of filters that the pipeline will process and draw to the given endpoints of the graph.
	 * @param rootRenderer 
	 * A root node (input node) of the graph of filters and endpoints.
	 */
	public synchronized void removeRootRenderer(GLRenderer rootRenderer) {
		rootRenderers.remove(rootRenderer);
	}
	
	/**
	 * Starts the rendering of the graph. If this is called before a root node renderer has been
	 * added, it will do nothing.
	 */
	public synchronized void startRendering() {
		if(rootRenderers.size() != 0) {
			rendering = true;
		}
	}
}