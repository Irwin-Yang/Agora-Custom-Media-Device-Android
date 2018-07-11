package project.android.imageprocessing.input;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.microedition.khronos.opengles.GL10;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.media.MediaPlayer;
import android.media.MediaRecorder.VideoEncoder;
import android.net.Uri;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;

/**
 * A video input extension of GLTextureOutputRenderer.  
 * This class takes advantage of the android MediaPlayer and SurfaceTexture to produce new textures for processing. <p>
 * Note: This class requires an API level of at least 14.
 * @author Chris Batt
 */
@TargetApi(14)
public class VideoPlayerInput extends GLTextureOutputRenderer implements OnFrameAvailableListener{	
	private static final String UNIFORM_CAM_MATRIX = "u_Matrix";
	
	private MediaPlayer player;
	private SurfaceTexture videoTex;
	private Context context;
	private GLSurfaceView view;
	private Uri uri;
	
	private int matrixHandle;
	private float[] matrix = new float[16];
	
	private boolean startWhenReady;
	private boolean ready;
	
	/**
	 * Creates a VideoResourceInput which captures the camera preview with all the default camera parameters and settings.
	 * @param context
	 * The context which contains the given resource id.
	 * @param id
	 * The resource id that points to the video that should be displayed
	 */
	public VideoPlayerInput(GLSurfaceView view, Context context, Uri uri) {
		super();
		this.view = view;
		this.player = MediaPlayer.create(context, uri);
		this.player.setVolume(0, 0);
		this.context = context;
		this.uri = uri;
		startWhenReady = false;
		ready = false;
		setRenderSize(player.getVideoWidth(), player.getVideoHeight());
	}
	
	private void bindTexture() {
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture_in);
	}
	
	@Override
	protected void drawFrame() {
		videoTex.updateTexImage(); 
		super.drawFrame();
	}
	
	@Override
	protected String getFragmentShader() {
		return 
					 "#extension GL_OES_EGL_image_external : require\n"
					+"precision mediump float;\n"                         
					+"uniform samplerExternalOES "+UNIFORM_TEXTURE0+";\n"  
					+"varying vec2 "+VARYING_TEXCOORD+";\n"
				
		 			+ "void main() {\n"
		 			+ "   gl_FragColor = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+");\n"
		 			+ "}\n";
	}
	
	@Override
	protected String getVertexShader() {
		return 
					"uniform mat4 "+UNIFORM_CAM_MATRIX+";\n"
				  + "attribute vec4 "+ATTRIBUTE_POSITION+";\n"
				  + "attribute vec2 "+ATTRIBUTE_TEXCOORD+";\n"	
				  + "varying vec2 "+VARYING_TEXCOORD+";\n"	
				  
				  + "void main() {\n"	
				  + "   vec4 texPos = "+UNIFORM_CAM_MATRIX+" * vec4("+ATTRIBUTE_TEXCOORD+", 1, 1);\n"
				  + "   "+VARYING_TEXCOORD+" = texPos.xy;\n"
				  + "   gl_Position = "+ATTRIBUTE_POSITION+";\n"		                                            			 
				  + "}\n";		
	}

	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
        matrixHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_CAM_MATRIX);    
	}


	@Override
	protected void initWithGLContext() {
		ready = false;
		try { 
			player = MediaPlayer.create(context, uri); 
		} catch (Exception e) {
			Log.e("VideoPlayer", "Failed to load video");
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.e("VideoPlayer", sw.toString());
			player.release();
		}
		setRenderSize(player.getVideoWidth(), player.getVideoHeight());
		super.initWithGLContext();
		
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);        
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		texture_in = textures[0];	
		
		videoTex = new SurfaceTexture(texture_in);
		videoTex.setOnFrameAvailableListener(this);	

		Surface surface = new Surface(videoTex);

        player.setSurface(surface);
        
		ready = true;
		if(startWhenReady) {
			player.start();
		}
	}

	/* (non-Javadoc)
	 * @see android.graphics.SurfaceTexture.OnFrameAvailableListener#onFrameAvailable(android.graphics.SurfaceTexture)
	 */
	@Override
	public void onFrameAvailable(SurfaceTexture arg0) {
		markAsDirty();
		view.requestRender();
	}

	@Override
	protected void passShaderValues() {
		renderVertices.position(0);
		GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 8, renderVertices);  
		GLES20.glEnableVertexAttribArray(positionHandle); 
		textureVertices[curRotation].position(0);
		GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 8, textureVertices[curRotation]);  
		GLES20.glEnableVertexAttribArray(texCoordHandle); 
		
		bindTexture();
	    GLES20.glUniform1i(textureHandle, 0);
	    
	    videoTex.getTransformMatrix(matrix);
		GLES20.glUniformMatrix4fv(matrixHandle, 1, false, matrix, 0);
	}
	
	/**
	 * Sets the video to a new video source. The id must be from the same context as the previous id.
	 * @param id
	 * The id that points to the video resource 
	 */
	public void setVideoSource(Uri uri) {
		this.uri = uri;
	}
	
	/**
	 * Returns whether or not the video is currently playing.
	 * @return playing
	 * Whether or not the video is currently playing.
	 */
	public boolean isPlaying() {
		return player.isPlaying();
	}
	
	/**
	 * Starts the video player if the opengl context has initialized the video already.  Otherwise,
	 * starts the video once the opengl context has been initialized.
	 */
	public void startWhenReady() {
		if(ready) {
			player.start();
		} else {
			startWhenReady = true;
		}
	}
	
	/**
	 * Stops the video
	 */
	public void stop() {
		player.pause();
	}
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.input.GLTextureOutputRenderer#destroy()
	 */
	@Override
	public void destroy() {
		super.destroy();
		if(player.isPlaying()) {
			player.stop();
			player.release();
			player = null;
			ready = false;
		}
		if(texture_in != 0) {
			int[] tex = new int[1];
			tex[0] = texture_in;
			GLES20.glDeleteTextures(1, tex, 0);
			texture_in = 0;
		}
	}
}
