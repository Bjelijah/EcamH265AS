package com.howell.playerrender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;


//import com.example.yuvtest.GraphicsUtil;
//import com.howell.webcam.protocol.playerrender.R;
import com.howell.activity.PlayerActivity;
import com.howell.jni.JniUtil;

/**
 * @class YV12Renderer
 * 		This is a GLSurfaceView's renderer, which render the YV12 image to the view.
 * 		Use native code to render Y,U,V, rest thing is done by the class.
 */

public class YV12Renderer implements Renderer {

	//private ActivityFilterGL activity;
	public static long time;

	// Pass-through vertex shader.
	private static final String VERTEX_SHADER_STRING =
	      "varying vec2 interp_tc;\n" +
	      "\n" +
	      "attribute vec4 in_pos;\n" +
	      "attribute vec2 in_tc;\n" +
	      "\n" +
	      "void main() {\n" +
	      "  gl_Position = in_pos;\n" +
	      "  interp_tc = in_tc;\n" +
	      "}\n";

	// YUV to RGB pixel shader. Loads a pixel from each plane and pass through the
	// matrix.
	private static final String FRAGMENT_SHADER_STRING =
	      "precision mediump float;\n" +
	      "varying vec2 interp_tc;\n" +
	      "\n" +
	      "uniform sampler2D y_tex;\n" +
	      "uniform sampler2D u_tex;\n" +
	      "uniform sampler2D v_tex;\n" +
	      "\n" +
	      "void main() {\n" +
	      "  float y = texture2D(y_tex, interp_tc).r;\n" +
	      "  float u = texture2D(u_tex, interp_tc).r - .5;\n" +
	      "  float v = texture2D(v_tex, interp_tc).r - .5;\n" +
	      // CSC according to http://www.fourcc.org/fccyvrgb.php
	      "  gl_FragColor = vec4(y + 1.403 * v, " +
	      "                      y - 0.344 * u - 0.714 * v, " +
	      "                      y + 1.77 * u, 1);\n" +
	      "}\n";
	
	// Remote image should span the full screen.
	private static final FloatBuffer remoteVertices = directNativeFloatBuffer(
	      new float[] { -1, 1, -1, -1, 1, 1, 1, -1 });

	  // Local image should be thumbnailish.
//	  private static final FloatBuffer localVertices = directNativeFloatBuffer(
//	      new float[] { 0.6f, 0.9f, 0.6f, 0.6f, 0.9f, 0.9f, 0.9f, 0.6f });

	  // Texture Coordinates mapping the entire texture.
	private static final FloatBuffer textureCoords = directNativeFloatBuffer(
	      new float[] { 0, 0, 0, 1, 1, 0, 1, 1 });

	private final static String TAG = "VideoStreamsView";
	  // 
	private int[] yuvTextures = {-1, -1, -1};
	private int posLocation = -1;
	private Context context_;
	private GLSurfaceView gl_surface_view_;
	
//	private static float frames;
	

	private native void nativeInit();
	public static native void nativeDeinit();
	private native void nativeRenderY();
	private native void nativeRenderU();
	private native void nativeRenderV();
	private native void nativeOnSurfaceCreated();
	//public static native void setCatchPictureFlag(String path,int length);
		
	public void setTime(long time){
		if(PlayerActivity.stopSendMessage){
			YV12Renderer.time = 0;
			return;
		}
		YV12Renderer.time = time;
	}
	
	  
	public YV12Renderer(Context context, GLSurfaceView view) {
		context_ = context;
		gl_surface_view_ = view;
		
//		frames = 0.000f;
		nativeInit();
		
	
		
		
	}
	
	// Wrap a float[] in a direct FloatBuffer using native byte order.
	  
	private static FloatBuffer directNativeFloatBuffer(float[] array) {
	    FloatBuffer buffer = ByteBuffer.allocateDirect(array.length * 4).order(
	        ByteOrder.nativeOrder()).asFloatBuffer();
	    buffer.put(array);
	    buffer.flip();
	    return buffer;
	}
	
	private void render(int i) {
		  if (i==0)
			  nativeRenderY();
		  else if (i==1) {
			  nativeRenderU();
		  }
		  else 
			  nativeRenderV();
	}
	  
	@Override
	public void onDrawFrame(GL10 arg0) {
//		frames += 0.001f;
		PlayerActivity.addFrames();
		//System.out.println("onDrawFrame");
		 GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		 
		 for (int i = 0; i < 3; ++i) {
		  
		      GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
		      GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yuvTextures[i]);
//		      int w = i == 0 ? frame.width : frame.width / 2;
//		      int h = i == 0 ? frame.height : frame.height / 2;
//		      abortUnless(w == frame.yuvStrides[i], frame.yuvStrides[i] + "!=" + w);
		      
//		      GLES20.glTexImage2D(
//		          GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, w, h, 0,
//		          GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, plane);
		      render(i);
		      
		      GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
		              GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		          GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
		              GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		          GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
		              GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		          GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
		              GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		 }
		 
		 checkNoGLES2Error();
	
		//Log.d("render","on draw frame");
		// TODO Auto-generated method stub
		
		    drawRectangle(yuvTextures, remoteVertices);
		    //drawRectangle(yuvTextures[0], localVertices);
		    checkNoGLES2Error();
//		  }
		    //System.out.println("onDrawFrame over");
	}
	
//	public static float getFrames(){
//		return frames;
//	}

//Draw |textures| using |vertices| (X,Y coordinates).
	private void drawRectangle(int[] textures, FloatBuffer vertices) {
		for (int i = 0; i < 3; ++i) {
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i]);
		}
		
		GLES20.glVertexAttribPointer(posLocation, 2, GLES20.GL_FLOAT, false, 0, vertices);
		GLES20.glEnableVertexAttribArray(posLocation);
		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		checkNoGLES2Error();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d("render","on surface changed "+width+" "+height);
		//GLES20.glActiveTexture(GLES20.GL_ACTIVE_TEXTURE);
		if (height>width) {
		int new_h=width*9/16;
			GLES20.glViewport(0, (height-new_h)/2, width, new_h);
		}
		else {
			GLES20.glViewport(0, 0, width, height);
		}
		// TODO Auto-generated method stub
	}

	// Compile & attach a |type| shader specified by |source| to |program|.
	private static void addShaderTo(
	    int type, String source, int program) {
	    int[] result = new int[] { GLES20.GL_FALSE };
	    int shader = GLES20.glCreateShader(type);
	    GLES20.glShaderSource(shader, source);
	    GLES20.glCompileShader(shader);
	    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, result, 0);
	    abortUnless(result[0] == GLES20.GL_TRUE,
	        GLES20.glGetShaderInfoLog(shader) + ", source: " + source);
	    GLES20.glAttachShader(program, shader);
	    GLES20.glDeleteShader(shader);
	    checkNoGLES2Error();
	}
	  
	  // Poor-man's assert(): die with |msg| unless |condition| is true.
	private static void abortUnless(boolean condition, String msg) {
	    if (!condition) {
	      throw new RuntimeException(msg);
	    }
	}
	  
	// Assert that no OpenGL ES 2.0 error has been raised.
	private static void checkNoGLES2Error() {
	    int error = GLES20.glGetError();
	    abortUnless(error == GLES20.GL_NO_ERROR, "GLES20 error: " + error);
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	// TODO Auto-generated method stub
	// Define a simple shader program for our point.
		Log.d("render","on surface create");
		
		int program = GLES20.glCreateProgram();
	    addShaderTo(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_STRING, program);
	    addShaderTo(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_STRING, program);

	    GLES20.glLinkProgram(program);
	    int[] result = new int[] { GLES20.GL_FALSE };
	    result[0] = GLES20.GL_FALSE;
	    GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, result, 0);
	    abortUnless(result[0] == GLES20.GL_TRUE,
	        GLES20.glGetProgramInfoLog(program));
	    GLES20.glUseProgram(program);

	    GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "y_tex"), 0);
	    GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "u_tex"), 1);
	    GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "v_tex"), 2);

	    // Actually set in drawRectangle(), but queried only once here.
	    posLocation = GLES20.glGetAttribLocation(program, "in_pos");

	    int tcLocation = GLES20.glGetAttribLocation(program, "in_tc");
	    GLES20.glEnableVertexAttribArray(tcLocation);
	    GLES20.glVertexAttribPointer(
	        tcLocation, 2, GLES20.GL_FLOAT, false, 0, textureCoords);

	    GLES20.glGenTextures(3, yuvTextures, 0);
	    
	    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	    checkNoGLES2Error();
	    
		Log.d("render","surface created");
		
		nativeOnSurfaceCreated();
	}
	
	//TODO 闁告瑯鍨禍鎺楁儎鐎涙ê澶嶅ù锝堟硶閺併倗锟藉Δ鍐惧剨濞戞捁顕滅槐婵囩▔瀹ュ浠橀悷鏇氭缁垘aw濞戞搩鍙�浼村矗閿燂拷
	public static String readTextFileFromRawResource(final Context context, final int resourceId) {
		final InputStream inputStream = context.getResources().openRawResource(resourceId);
		final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String nextLine;
		final StringBuilder body = new StringBuilder();

		try {
			while ((nextLine = bufferedReader.readLine()) != null) {
				body.append(nextLine);
				body.append('\n');
			}
		} catch (IOException e) {
			return null;
		}

		return body.toString();
	}

	public static int loadShader(int type, String shaderSrc) {
		int shader;
		int[] compiled = new int[1];

		// Create the shader object
		shader = GLES20.glCreateShader(type);
		if (shader == 0) {
			return 0;
		}
		// Load the shader source
		GLES20.glShaderSource(shader, shaderSrc);
		// Compile the shader
		GLES20.glCompileShader(shader);
		// Check the compile status
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);

		if (compiled[0] == 0) {
			Log.e("ESShader", GLES20.glGetShaderInfoLog(shader));
			GLES20.glDeleteShader(shader);
			return 0;
		}
		return shader;
	}

	public static int loadProgram(String vertShaderSrc, String fragShaderSrc) {
		int vertexShader;
		int fragmentShader;
		int programObject;
		int[] linked = new int[1];

		// Load the vertex/fragment shaders
		vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertShaderSrc);
		if (vertexShader == 0) {
			return 0;
		}

		fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragShaderSrc);
		if (fragmentShader == 0) {
			GLES20.glDeleteShader(vertexShader);
			return 0;
		}

		// Create the program object
		programObject = GLES20.glCreateProgram();

		if (programObject == 0) {
			return 0;
		}

		GLES20.glAttachShader(programObject, vertexShader);
		GLES20.glAttachShader(programObject, fragmentShader);

		// Link the program
		GLES20.glLinkProgram(programObject);

		// Check the link status
		GLES20.glGetProgramiv(programObject, GLES20.GL_LINK_STATUS, linked, 0);

		if (linked[0] == 0) {
			Log.e("ESShader", "Error linking program:");
			Log.e("ESShader", GLES20.glGetProgramInfoLog(programObject));
			GLES20.glDeleteProgram(programObject);
			return 0;
		}

		// Free up no longer needed shader resources
		GLES20.glDeleteShader(vertexShader);
		GLES20.glDeleteShader(fragmentShader);

		return programObject;
	}
	
	//invoked by outside
	public void requestRender() {
		//Log.d("render","native invoke render ");
		gl_surface_view_.requestRender();
	}
	
	public final void finalize() {
		nativeDeinit();
	}
}
