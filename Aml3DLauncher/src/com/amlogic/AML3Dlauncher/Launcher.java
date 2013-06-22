package com.amlogic.AML3Dlauncher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.amlogic.a3d.anim.A3DAnimation;
import com.amlogic.a3d.anim.A3DAnimationListener;
import com.amlogic.a3d.anim.A3DAnimator;
import com.amlogic.a3d.anim.A3DTransformAnimation;
import com.amlogic.a3d.scene.A3DLight;
import com.amlogic.a3d.scene.A3DObject;
import com.amlogic.a3d.scene.A3DViewRenderer;
import com.amlogic.a3d.scene.A3DWorld;
import com.amlogic.a3d.scene.Texture;
import com.amlogic.a3d.scene.TextureManager;
import com.amlogic.a3d.util.Line3D;
import com.amlogic.a3d.util.Model;
import com.amlogic.a3d.util.ObjLoader;




public class Launcher extends Activity
{
	AmlGLSurfaceView view ;
    A3DViewRenderer render;
    A3DWorld world;
    MigrantWorker worker;
    private AbsoluteLayout alayout;
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
        BitmapFactory.setDefaultConfig(Bitmap.Config.ARGB_8888);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
//    	view = new AmlGLSurfaceView(this,null);
//    	view.getHolder().setFormat(PixelFormat.RGBA_8888);
    	
//        alayout=(AbsoluteLayout)getLayoutInflater().inflate(R.layout.main, null);	    
//		setContentView(alayout);
//		
//        GLSurfaceView glSurfaceView =
//            (GLSurfaceView) findViewById(R.id.glsurfaceview);
//
//        glSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
    	
    	
    	world = new A3DWorld();
    	

        alayout=(AbsoluteLayout)getLayoutInflater().inflate(R.layout.main, null);	    
		setContentView(alayout);
    	//setContentView(view);
		view =   (AmlGLSurfaceView) findViewById(R.id.glsurfaceview);
	
		view.getHolder().setFormat(PixelFormat.RGBA_8888);
    	
          
        render = new A3DViewRenderer(view, world);
        view.setRenderer(render);

        worker=new MigrantWorker(this,world);
        
    	//set background 
        InputStream is=ResourceMgr.instance.getInputStream(this,worker.getBackground());  
        if(is!=null)
        {
	        DisplayMetrics dm = new DisplayMetrics();  
	        getWindowManager().getDefaultDisplay().getMetrics(dm);  
	        render.setBackground(BitmapFactory.decodeStream(is), 0, 0, 100, dm.widthPixels, dm.heightPixels);
        }
    }
	public void onConfigurationChanged(Configuration newConfig) {	
		super.onConfigurationChanged(newConfig);
		if (this.getResources().getConfiguration().orientation 
				== Configuration.ORIENTATION_LANDSCAPE) {
		}
		else if (this.getResources().getConfiguration().orientation 
				== Configuration.ORIENTATION_PORTRAIT) {
		}
	}

	public boolean onGenericMotionEvent(MotionEvent event){
	  int step = (int)event.getAxisValue(MotionEvent.AXIS_VSCROLL);
      
      if (step > 2) {
      	worker.doNextWork();	
          return true;
      } else {
      	if(step < -2){	
		   worker.doPrevWork();
            }
            return true;
        }
     }
	
    
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	Log.v("","fps="+render.getFPS());
    	switch(keyCode)
    	{
    	case KeyEvent.KEYCODE_DPAD_LEFT:
    	case KeyEvent.KEYCODE_MENU:
    		//worker.doNextWork();
     		break;
    	case KeyEvent.KEYCODE_DPAD_RIGHT:
		case KeyEvent.KEYCODE_BACK :   	
			worker.doPrevWork();
    		break;
    /*	case KeyEvent.KEYCODE_DPAD_UP:
    	case KeyEvent.KEYCODE_VOLUME_DOWN :
    		worker.prevSubItem();
    		return true;
    	case KeyEvent.KEYCODE_DPAD_DOWN:
    	case 84 :
    		worker.nextSubItem();
    		break;*/
   // 	case KeyEvent.KEYCODE_VOLUME_UP :
		case KeyEvent.KEYCODE_DPAD_CENTER:
    	case KeyEvent.KEYCODE_ENTER:
    		worker.select();
    		return true;
//    	case KeyEvent.KEYCODE_VOLUME_DOWN :
//    		//    		System.exit(0); 
//    		break;
    	}
    	return false;
    }
    
	private float mPreviousX;
	private float mPreviousY;
    public boolean onTouchEvent(final MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
	           /* Line3D line= render.GetSelectionRay(x,y);
	            if(worker.isAnyObjectTouched(line)!=null)
	            {
	            	Log.v("touch"," ok ");
	            }else
	            {
	            	Log.v("touch"," no ");
	            }*/

			   	worker.select();
	            break;
			case MotionEvent.ACTION_MOVE:
				float dx = x - mPreviousX;
				float dy = y - mPreviousY;
				if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > 2.5) {


				} else if (Math.abs(dx) < Math.abs(dy) && Math.abs(dy) > 2.5) {

				}
			}
		mPreviousX = x;
		mPreviousY = y;
		return true;
    }
}
