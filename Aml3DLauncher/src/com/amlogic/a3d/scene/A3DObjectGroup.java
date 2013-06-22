package com.amlogic.a3d.scene;

import java.util.ArrayList;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

public class A3DObjectGroup implements A3DNode{
	protected String name;
	protected Vector<A3DObject> objects = null;
	protected Vector<A3DObjectGroup> groups = null;
	protected float[] position;
	protected float[] rotation;
	protected float[] scale;
	protected boolean enabled = true;
	
	/** 
	 * Constructor 
	 * @param name -- name of this group. 
	 */ 
	public A3DObjectGroup(String name) { 
		this.name = name;
		init();
	} 
	
	/** 
	 * Constructor 
	 * @param name -- name of this group. 
	 * @param obj -- the only object for this group. 
	 */ 
	public A3DObjectGroup(String name, A3DObject obj) { 
		this.name = name;
		init();
		addObject(obj); 
	} 

	/** 
	 * Constructor 
	 * @param objects for this group. 
	 */ 
	public A3DObjectGroup(String name, A3DObject[] objs) { 
		this.name = name;
		init();
		for (int i=0;i<objs.length;i++) 
			addObject(objs[i]); 
	} 

	private void init() {
		position = new float[]{0,0,0};
		rotation = new float[]{0,0,0,0};
		scale = new float[]{1,1,1};
		objects = new Vector<A3DObject>();
		groups = new Vector<A3DObjectGroup>();
	}
	
	/** 
	 * set the position of this group in A3DWorld. 
	 * @param x,y,z -- position. 
	 */
	public void setPosition(float x, float y, float z) {
		position[0] = x;
		position[1] = y;
		position[2] = z;
	    for (int i=0;i<objects.size();i++) {
            objects.get(i).SetMirroredParentPosition(position);
        }
	}
	
	/** 
	 * set the rotation of this group in A3DWorld. 
	 * @param x,y,z -- rotation for each axis. 
	 */
	public void setRotation(float angel, float x, float y, float z) {
		rotation[0] = angel;
		rotation[1] = x;
		rotation[2] = y;
		rotation[3] = z;
	}

	/** 
	 * set the scale of this group in A3DWorld. 
	 * @param width,height,depth -- position. 
	 */
	public void setScale(float width, float height, float depth) {
		scale[0] = width;
		scale[1] = height;
		scale[2] = depth;
	}	
	
	/** 
	 * get the position of this group in A3DWorld. 
	 */
	public float[] getPosition() {
		return position;
	}
	
	/** 
	 * get the rotation of this group in A3DWorld. 
	 */
	public float[] getRotation() {
		return rotation;
	}	

	/** 
	 * get the scale of this group. 
	 */
	public float[] getScale() {
		return scale;
	}
	
	/** 
	 * enable this group for rendering 
	 */
	public void enable() {
		enabled = true;
	}

	/** 
	 * disable this group for rendering 
	 */
	public void disable() {
		enabled = false;
	}

	/** 
	 * enable an object in this group for rendering 
	 */
	public void enable(String obj_name) {
		A3DObject obj = getObject(obj_name);
		if (obj!=null)
			obj.enable();
	}

	/** 
	 * disable an object of this group for rendering 
	 */
	public void disable(String obj_name) {
		A3DObject obj = getObject(obj_name);
		if (obj!=null)
			obj.disable();
	}	
	
	/** 
	 * Add an object to this group. 
	 * @param obj -- The object need to add. 
	 */ 
	public void addObject(A3DObject obj) { 
		objects.add(obj);
	}

	/** 
	 * get object count. 
	 */ 
	public int getObjectCount() {
		return objects.size();
	}
	
	/** 
	 * find an object from this group. 
	 * @param name -- name of the object need to return. 
	 */ 
	public A3DObject getObject(String name){
		for (int i=0;i<objects.size();i++)
			if (objects.get(i).name.equals(name))
				return objects.get(i);
		return null;
	}

	/** 
	 * find an object from this group by index. 
	 * @param name -- name of the object need to return. 
	 */ 
	public A3DObject getObject(int index){
		if (index>=0 && index<objects.size())
			return objects.get(index);
		return null;
	}	
	
	/** 
	 * remove an object from this group. 
	 * @param name -- name of the object need to remove. 
	 */ 
	public void removeObject(String name) { 
		for (int i=0;i<objects.size();i++)
			if (objects.get(i).name.equals(name))
				objects.remove(i);
	}
	
	/** 
	 * Add an group to this groups. 
	 * @param grp -- The group need to add. 
	 */ 
	public void addGroup(A3DObjectGroup grp) { 
		groups.add(grp);
	}

	/** 
	 * get group count. 
	 */ 
	public int getGroupCount() {
		return groups.size();
	}
	
	/** 
	 * find an group from this group. 
	 * @param name -- name of the group need to return. 
	 */ 
	public A3DObjectGroup getGroup(String name){
		for (int i=0;i<groups.size();i++)
			if (groups.get(i).name.equals(name))
				return groups.get(i);
		return null;
	}

	/** 
	 * find an group from this group by index. 
	 * @param name -- name of the group need to return. 
	 */ 
	public A3DObjectGroup getGroup(int index){
		if (index>=0 && index<groups.size())
			return groups.get(index);
		return null;
	}	
	
	/** 
	 * remove an group from this group. 
	 * @param name -- name of the group need to remove. 
	 */ 
	public void removeGroup(String name) { 
		for (int i=0;i<groups.size();i++)
			if (groups.get(i).name.equals(name))
				groups.remove(i);
	}
    
	public void generateHardwareBuffers(GL10 gl) {
	    for (int i=0;i<objects.size();i++) {
            objects.get(i).invalidateHardwareBuffers();
            objects.get(i).generateHardwareBuffers(gl);
	    }
	    for (int i=0;i<groups.size();i++) {
//	    	groups.get(i).invalidateHardwareBuffers();
	    	groups.get(i).generateHardwareBuffers(gl);
	    }
	}
	
	public void releaseHardwareBuffers(GL10 gl) {
	    for (int i=0;i<objects.size();i++) {
            objects.get(i).releaseHardwareBuffers(gl);
        }
	    for (int i=0;i<groups.size();i++) {
            groups.get(i).releaseHardwareBuffers(gl);
        }
	}
	
	public void render(GL10 gl,boolean transflag) {
		gl.glPushMatrix();
		
/*change it to get the correct object postion when touching*/		
		if ((position[0]!=0)||(position[1]!=0)||(position[2]!=0))
			gl.glTranslatef(position[0],position[1],position[2]);
		if ((scale[0]!=1)||(scale[1]!=1)||(scale[2]!=1))
			gl.glScalef(scale[0],scale[1],scale[2]);
		if (rotation[0]!=0)
			gl.glRotatef(rotation[0], rotation[1], rotation[2], rotation[3]);
		for (int j=0;j<getObjectCount();j++) {
			A3DObject obj = getObject(j);
			if (obj != null&&obj.getTransFlag()==transflag){
//				obj.setPosition(position[0], position[1], position[2]);
//				obj.setScale(scale[0], scale[1], scale[2]);
//				obj.setRotation(rotation[0], rotation[1], rotation[2], rotation[3]);
				obj.render(gl);
			}
		}	
		
		int count = groups.size();
		int[] indexarray = new int[count];
		for (int k=0;k<count;k++)
		{
			indexarray[k] = k; 
		}
		float postion1[]=new float[]{0,0,0};
		float postion2[]=new float[]{0,0,0};
		int change=0;	
		for (int k=0;k<count;k++)
		{
			postion1=groups.get(indexarray[k]).getPosition();
			for(int p=k+1;p<count;p++)
			{
				postion2=groups.get(indexarray[p]).getPosition();
				if(postion2[2]<postion1[2])
				{
					change= indexarray[k];
					indexarray[k]=indexarray[p];
					indexarray[p]=change;
					postion1=groups.get(indexarray[k]).getPosition();
				}
			}
		}
		
		for (int i = 0; i < groups.size(); i++) {
			A3DObjectGroup group = groups.get(indexarray[i]);
			if (group != null) {
				group.render(gl,transflag);
			}
		}
		gl.glPopMatrix();
	}

    
    public int getCurrentFrame() {
        return 0;
    }

    
    public void setCurrentFrame(int frame) {
        
    }

    /** 
     * get number frame of this object. 
     */ 
    
    public int getFrameCount() { 
        return 1;
    }

	
	public float[] getColor() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setColor(float r, float g, float b, float a) {
		for (int j=0;j<getObjectCount();j++) {
			A3DObject obj = getObject(j);
			if (obj != null){
				obj.setColor(r,g,b,a);
			}
		}	
		for (int i = 0; i < groups.size(); i++) {
			A3DObjectGroup group = groups.get(i);
			if (group != null) {
				group.setColor(r,g,b,a);
			}
		}
	}



	@Override
	public void setTexture(String color, String normal) {
		// TODO Auto-generated method stub
		
	}

}
