package com.amlogic.AML3Dlauncher;

import com.amlogic.a3d.scene.A3DViewRenderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;
import android.util.Log;

public class AmlGLSurfaceView extends GLSurfaceView{
	A3DViewRenderer render;
	
	public AmlGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	//@Override
	public void setRenderer(Renderer renderer) {
		render = (A3DViewRenderer)renderer;
		// TODO Auto-generated method stub
		super.setRenderer(renderer);
	}
	 
	//@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		render.releaseBuffers();
		
		Log.i("skyworth3d", "onPause()");
	}

	//@Override
	public void onResume() {
		Log.i("skyworth3d", "onResume()");
		
		render.reloadBuffers();
		
		// TODO Auto-generated method stub
		super.onResume();
	}

}

