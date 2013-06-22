package com.amlogic.a3d.util; 
  
import java.util.Vector; 

import android.view.animation.Animation;
import java.nio.ByteBuffer;

import com.amlogic.a3d.scene.Mesh;
import com.amlogic.a3d.util.ModelFrame;
  
 /** 
 * A Model is a collection of one or more animation frames. 
 */ 
 public class Model { 
 Vector<ModelFrame> frames; 
 Vector<Animation> animations;
	float x;
	float y;
	float z;
	float dst_x;
	float dst_y;
	float dst_z;
	float delta_x;
	float delta_y;
	float delta_z;
	int step;
	int tex_id;
    ByteBuffer tex;
    int tex_width;
    int tex_height;
	int verts;
	int frame_ix;
	

	/** 
	 * Constructor 
	 * @param mesh of the only one frame for this Model. 
	 */ 
	public Model(Mesh mesh) { 
		frames = new Vector<ModelFrame>();
		addFrame(new ModelFrame(mesh)); 
	} 

	/** 
	 * Constructor 
	 * @param the only one frame for this Model. 
	 */ 
	public Model(ModelFrame frame) { 
		frames = new Vector<ModelFrame>();
		addFrame(frame); 
	} 

	/** 
	 * Constructor 
	 * @param frames The frames for this Model. 
	 */ 
	public Model(ModelFrame[] frames) { 
		this.frames = new Vector<ModelFrame>();
		for (int i=0;i<frames.length;i++) 
			addFrame(frames[i]); 
	} 
	  
	/** 
	 * Add a Frame to this model. 
	 * @param f The Frame to add. 
	 */ 
	public void addFrame(ModelFrame f) { 
		frames.add(f); 
} 
 
 /** 
 * Get a Frame from this model. 
 * @param ix The index of the Frame to get. 
 * @return The specified Frame 
 */ 
 public ModelFrame getFrame(int ix) { 
	 return frames.get(ix); 
 } 
  
 /** 
 * Get the number of frames in this model 
 * @return The number of frames. 
 */ 
 public int getFrameCount() { 
	 return frames.size(); 
 } 
 
 /** 
  * Get the number of animation in this model 
  * @return The number of animation. 
  */ 
 public int getAnimationCount() {
	 return 0;
 }
 
 /** 
  * Get the number of frames in this model 
  * @return The number of frames. 
  */ 
 public Animation getAnimation(int index) {
	 return animations.elementAt(index);
 }	 
} 
