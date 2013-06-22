package com.amlogic.a3d.scene;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGL;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import com.amlogic.a3d.anim.A3DAnimator;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.util.Log;

import com.amlogic.a3d.util.Line3D;
import com.amlogic.a3d.util.MatrixTrackingGL;
import com.amlogic.a3d.util.Point3D;

public class A3DViewRenderer implements GLSurfaceView.Renderer {
	private A3DWorld world;
	private GLSurfaceView view;
	private int width;
	private int height;
	//private float frov = 30.0f;
	public boolean useHardwareBuffers = true;
	private boolean firstFrame = true;
	private int FPS = 0;
	private int fpsCounter = 0;
	private long fpsStartTime = 0;
//	private GL10 gl = null;
	private MatrixTrackingGL gl = null;
	private float[] modelMatrix = new float[16];
	private float[] projMatrix = new float[16];

    
	public A3DViewRenderer(GLSurfaceView view, A3DWorld world) {
		super();
		this.world = world;
		this.view = view;

		/*
		 * Set 4x Full-Scene Anti-Aliasing (FSAA).
		 * 
		 * From Mali Anti-aliasing example: "It should be noted that 4x
		 * anti-aliasing is nearly free on Mali hardware (around 2% performance
		 * drop). (...) For many applications 4x will be sufficient, and as it's
		 * nearly free we would recommend this over no anti-aliasing."
		 */

		view.setEGLConfigChooser(new EGLConfigChooser() {
			
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				int[] attributes = new int[] { EGL10.EGL_SAMPLES, 4,
						EGL10.EGL_RED_SIZE, 8, EGL10.EGL_GREEN_SIZE, 8,
						EGL10.EGL_BLUE_SIZE, 8, EGL10.EGL_ALPHA_SIZE, 8,
						EGL10.EGL_DEPTH_SIZE, 8, EGL10.EGL_STENCIL_SIZE, 8,
						EGL10.EGL_NONE };
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				if (egl
						.eglChooseConfig(display, attributes, configs, 1,
								result) == false
						|| result[0] <= 0) {
					// fallback for emulator
					attributes = new int[] { EGL10.EGL_NONE };
					egl.eglChooseConfig(display, attributes, configs, 1,
									result);
				}
				return configs[0];
			}
		});
	}
	
	int bgTexID = -1;

	float lastEyeZ = 0;
	
	public void onDrawFrame(GL10 gltmp) {
		if (!firstFrame &&
			A3DAnimator.getInstance().updateAnimations() == false)
			;
//			return;
		if (firstFrame) firstFrame = false;

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT
				| GL10.GL_STENCIL_BUFFER_BIT);

		int[] range = world.getActiveRange();
		if(range != null){
			gl.glEnable(GL10.GL_SCISSOR_TEST);  
		    gl.glScissor(range[0],range[1],range[2],range[3]);  
		}
		else{
			gl.glDisable(GL10.GL_SCISSOR_TEST);  
		}

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		GLU.gluLookAt(gl, world.getEye()[0], world.getEye()[1],
				world.getEye()[2], world.getCenter()[0], world.getCenter()[1],
				world.getCenter()[2], world.getUp()[0], world.getUp()[1], world.getUp()[2]);
//		GLU.gluLookAt(gl, 0,0,240, 0,0,0,0,1,0);
        gl.getMatrix(modelMatrix, 0);
        
        
		if (world.mirror != null) {
			// Draw mirror to stencil buff
			gl.glDisable(GL10.GL_DEPTH_TEST);
			gl.glColorMask(false, false, false, false);
			gl.glDepthMask(false);
			gl.glDisable(GL10.GL_LIGHTING);
			gl.glEnable(GL10.GL_STENCIL_TEST);
			gl.glStencilOp(GL10.GL_REPLACE, GL10.GL_REPLACE, GL10.GL_REPLACE);
			gl.glStencilFunc(GL10.GL_ALWAYS, 1, 0xffffffff);
			world.mirror.render(gl);

			gl.glDepthMask(true);
			gl.glColorMask(true, true, true, true);
			gl.glEnable(GL10.GL_DEPTH_TEST);
			gl.glEnable(GL10.GL_LIGHTING);
			//gl.glStencilFunc(GL10.GL_EQUAL, 1, 0xffffffff);
			//gl.glStencilOp(GL10.GL_KEEP, GL10.GL_KEEP, GL10.GL_KEEP);

			//gl.glDisable(GL10.GL_STENCIL_TEST);
		}

		// Draw original objects
		gl.glEnable(GL10.GL_LIGHTING);
		for (int i = 0; i < world.getLightCount(); i++) {
			A3DLight light = world.getLight(i);
			if (light != null) {
				light.render(gl, i);
			}
		}
		
		for (int i = 0; i < world.getObjectCount(null); i++) {
			A3DObject obj = world.getObject(-1, i);
			if (obj != null)
				obj.render(gl);
		}

		for (int i = 0; i < world.getGroupCount(); i++) {
			A3DObjectGroup group = world.getGroup(i);
			if (group != null) {
				group.render(gl,false);
			}
		}
		
		bgTexID = (bgTexID<0)?TextureManager.loadEOSTexture(gl):bgTexID;
		if(lastEyeZ!=world.getEye()[2]){
			lastEyeZ = world.getEye()[2];
		}
		else{
			//Draw background;
			if(bgTexID>0){
				drawBackground(gl);
			}
		}
		
		
		if(world.mirror != null){
			world.mirror.render(gl);
		}
		
		for (int i = 0; i < world.getTransObjectCount(); i++) {
			A3DObject obj = world.getTransObject(i);
			if (obj != null)
				obj.render(gl);
		}
		
		for (int i = 0; i < world.getGroupCount(); i++) {
			A3DObjectGroup group = world.getGroup(i);
			if (group != null) {
				group.render(gl,true);
			}
		}
		calFPS();
	}

	
	public void onSurfaceChanged(GL10 gltmp, int width, int height) {
		System.out.println("gl surface changeed.");
		if ((this.width != width) || (this.height != height)) {
			if (height == 0) { // Prevent A Divide By Zero By
				height = 1; // Making Height Equal One
			}
			this.width = width;
			this.height = height;

			gl.glViewport(0, 0, width, height); // Reset The Current Viewport
			gl.glMatrixMode(GL10.GL_PROJECTION); // Select The Projection Matrix
			gl.glLoadIdentity(); // Reset The Projection Matrix

			// Calculate The Aspect Ratio Of The Window
			GLU.gluPerspective(gl, world.getFrov(), (float) 1280 / (float) 720, 1f, 50f);
			gl.getMatrix(projMatrix, 0);

		}


	}

	
	public void onSurfaceCreated(GL10 gltmp, EGLConfig config) {
		width = view.getWidth();
		height = view.getHeight();
        gl = new MatrixTrackingGL(gltmp);
 //       gl = gltmp;

		System.out.println("gl surface created.");
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0, 0, 0, 0);

		//gl.glDisable(GL10.GL_DITHER);
		
		// gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
		gl.glEnable(GL10.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL10.GL_GREATER, 0.0f);
		gl.glDepthRangef(0, 1);
		
		// Pretty perspective
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		gl.glEnable(GL10.GL_CULL_FACE);		
		//gl.glEnable(GL10.GL_FRONT_AND_BACK);

		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		GLU.gluPerspective(gl, world.getFrov(), ((float) 1280) / 720, 1f, 50f);
//		gl.glFrustumf(-100, 100, -100, 100, 5f, 50f);
//		GLU.gluPerspective(gl, 90, ((float) width) / height, 10f, 1000f);
        gl.getMatrix(projMatrix, 0);

        
		bindTextures(gl);
		
		if (useHardwareBuffers){
		    world.generateHardwareBuffers(gl);  //TODO save glcontext and when finished, call releaseHardwareBuffers() !!
		}
	}
	
	public void releaseBuffers(){
		if (useHardwareBuffers){
		    world.releaseHardwareBuffers(gl);
		}
		releaseTextures(gl);
	}
	
	public void reloadBuffers(){
		bindTextures(gl);
		if (useHardwareBuffers){
		    world.generateHardwareBuffers(gl);
		}
	}
	
	private void bindTextures(GL10 gltmp) {
		for (int i = 0; i < TextureManager.global.getTextureCount(); i++) {
			Texture texture = TextureManager.global.getTexture(i);
			System.out.println(texture.getName());
			System.out.println("width:" + texture.getWidth() + " height:"
					+ texture.getHeight());
			System.out.println("id:" + texture.getID() + " buffer len:"
					+ texture.getBuffer().limit());
			if (texture.getID() == -1) {
				int[] tmp_tex = new int[1];
				gl.glEnable(GL10.GL_TEXTURE_2D);
				gl.glGenTextures(1, tmp_tex, 0);
				int tx = tmp_tex[0];
				gl.glBindTexture(GL10.GL_TEXTURE_2D, tmp_tex[0]);
				gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, texture
						.getWidth(), texture.getHeight(), 0, GL10.GL_RGBA,
						GL10.GL_UNSIGNED_BYTE, texture.getBuffer());
				gl.glTexParameterf(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
				texture.setID(tx);
				gl.glDisable(GL10.GL_TEXTURE_2D);
			}
			/*
			if (texture.getID() == -1) {
				int[] tmp_tex = new int[1];
				Bitmap bitmap = Bitmap.createBitmap(texture.getWidth(), texture.getHeight(), Config.ARGB_8888);
				bitmap.copyPixelsFromBuffer(texture.getBuffer());
				gl.glEnable(GL10.GL_TEXTURE_2D);
				gl.glGenTextures(1, tmp_tex, 0);
				int tx = tmp_tex[0];
				gl.glBindTexture(GL10.GL_TEXTURE_2D, tmp_tex[0]);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
				if(gl instanceof GL11) {
					gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
					GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
				}
				texture.setID(tx);
				gl.glDisable(GL10.GL_TEXTURE_2D);
			}
			*/
		}
	}
	
	private void releaseTextures(GL10 gl) {
		for (int i = 0; i < TextureManager.global.getTextureCount(); i++) {
			Texture texture = TextureManager.global.getTexture(i);
			if (texture.getID() != -1) {
				int[] tmp_tex = new int[1];
//				gl.glEnable(GL10.GL_TEXTURE_2D);
				tmp_tex[0] = texture.getID();
				texture.setID(-1);
				gl.glDeleteTextures(1, tmp_tex, 0);
//				gl.glDisable(GL10.GL_TEXTURE_2D);
			}
		}
	}
	
	private void calFPS(){
		//Here we simply calculate the FPS
		long curTime = System.currentTimeMillis();
		if(fpsStartTime==0){
			fpsStartTime = curTime;
			fpsCounter = 10;
		}
		else if(curTime > fpsStartTime + 1000){
			fpsStartTime = curTime;
			FPS += fpsCounter;
			FPS /= 2;
			fpsCounter = 0;
		}
		else{
			fpsCounter += 10;
		}
	}
	public int getFPS(){
		return FPS;
	}
	
	private int[] bgImageRect = new int[4];
	private int[] bgImagePos = new int[3];
	public void setBackground(Bitmap bmp, int x, int y, int z, int width, int height){
		TextureManager.backgroundBmp = bmp;
		bgImageRect[0] = 0;
		bgImageRect[1] = height;
		bgImageRect[2] = width;
		bgImageRect[3] = -height;
		bgImagePos[0] = x;
		bgImagePos[1] = y;
		bgImagePos[2] = z;
	}
	private void drawBackground(GL10 gl){
		if( bgTexID> 0){
	        gl.glBindTexture(GL10.GL_TEXTURE_2D, bgTexID);
	        //((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, 
	        //        GL11Ext.GL_TEXTURE_CROP_RECT_OES, bgImageRect, 0);
            ((GL11Ext) gl).glDrawTexfOES(bgImagePos[0], bgImagePos[1], bgImagePos[2], bgImageRect[2], bgImageRect[1]);
		}
	}
	
	public Line3D GetSelectionRay(float mouse_x, float mouse_y) {
//        Log.v("","modelview matrix:"+modelMatrix[0]+" "+modelMatrix[1]+" "+modelMatrix[2]+" "+modelMatrix[3]+" "
//        							+modelMatrix[4]+" "+modelMatrix[5]+" "+modelMatrix[6]+" "+modelMatrix[7]+" "
//        							+modelMatrix[8]+" "+modelMatrix[9]+" "+modelMatrix[10]+" "+modelMatrix[11]+" "
//        							+modelMatrix[12]+" "+modelMatrix[13]+" "+modelMatrix[14]+" "+modelMatrix[15]);
//        Log.v("","projection matrix:"+projMatrix[0]+" "+projMatrix[1]+" "+projMatrix[2]+" "+projMatrix[3]+" "
//				+projMatrix[4]+" "+projMatrix[5]+" "+projMatrix[6]+" "+projMatrix[7]+" "
//				+projMatrix[8]+" "+projMatrix[9]+" "+projMatrix[10]+" "+projMatrix[11]+" "
//				+projMatrix[12]+" "+projMatrix[13]+" "+projMatrix[14]+" "+projMatrix[15]);
//        
        float[] ret = new float[4];
        float[] ret1 = new float[4];
	
		GLU.gluUnProject((float)mouse_x, (float)(height-mouse_y-1.0f), (float) 0.0, modelMatrix, 0, projMatrix, 0, new int[]{0, 0, width, height}, 0, ret, 0);
		
		Log.v("near"," x:"+ret[0]/ret[3]+" y:"+ret[1]/ret[3]+" z:"+ret[2]/ret[3]);
		GLU.gluUnProject((float)mouse_x, (float)(height-mouse_y-1.0f), (float) 1.0, modelMatrix, 0, projMatrix, 0, new int[]{0, 0, width, height}, 0, ret1, 0);
		
		Log.v("far"," x:"+ret1[0]/ret1[3]+" y:"+ret1[1]/ret1[3]+" z:"+ret1[2]/ret1[3]);	
		return new Line3D(new Point3D(ret[0]/ret[3],ret[1]/ret[3],ret[2]/ret[3]),new Point3D(ret1[0]/ret1[3],ret1[1]/ret1[3],ret1[2]/ret1[3]));

	}
	
    private void getMatrix(GL10 gltmp, int mode, float[] mat) {
        gl.glMatrixMode(mode);
        gl.getMatrix(mat, 0);
    }
}
