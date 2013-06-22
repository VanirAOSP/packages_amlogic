package com.amlogic.a3d.scene;

import java.util.Vector;
import javax.microedition.khronos.opengles.GL10;

public class A3DWorld {
	protected Vector<A3DObject> objects = null;
	protected Vector<A3DObjectGroup> groups = null;
	protected Vector<A3DLight> lights = null;
	protected A3DEye eye = new A3DEye(new float[]{0,0,10});
//	protected A3DCenter center = new A3DCenter(new float[]{0, 0, 11});
	
	protected A3DObject mirror = null;
	protected Vector<A3DObject> mirroredobjects = null;
	protected Vector<A3DObject> transobjects = null;

	/** 
	 * Constructor 
	 * @param name -- name of this group. 
	 * @param obj -- one object for this world. 
	 */ 
	public A3DWorld() { 
		init();
	} 
	
	/** 
	 * Constructor 
	 * @param name -- name of this group. 
	 * @param obj -- one object for this world. 
	 */ 
	public A3DWorld(A3DObject obj) { 
		init();
		objects.add(obj); 
	} 

	/** 
	 * Constructor 
	 * @param objects for this world. 
	 */ 
	public A3DWorld(A3DObject[] objs) { 
		init();
		for (int i=0;i<objs.length;i++) 
			objects.add(objs[i]); 
	} 

	/** 
	 * Constructor 
	 * @param obj -- one object group for this world. 
	 */ 
	public A3DWorld(A3DObjectGroup grp) { 
		init();
		groups.add(grp); 
	} 

	/** 
	 * Constructor 
	 * @param object groups for this world. 
	 */ 
	public A3DWorld(A3DObjectGroup[] grps) { 
		init();
		for (int i=0;i<grps.length;i++) 
			groups.add(grps[i]); 
	} 

	/** 
	 * Constructor 
	 * @param objects and groups for this world. 
	 */ 
	public A3DWorld(A3DObjectGroup[] grps, A3DObject[] objs) { 
		init();
		for (int i=0;i<grps.length;i++) 
			groups.add(grps[i]); 
		for (int i=0;i<objs.length;i++) 
			objects.add(objs[i]); 
	} 

	private void init() {
		objects = new Vector<A3DObject>(); 
        groups = new Vector<A3DObjectGroup>();
        lights = new Vector<A3DLight>();
    	mirroredobjects = new Vector<A3DObject>();
    	transobjects = new Vector<A3DObject>();
//        eye = new float[]{0,0,10}; // eye at 0,0,10
//        center = new float[]{0,0,0}; // center at 0,0,0
//        up = new float[]{0,1,0}; // default y up	
//        frov = 45;
	}
	
	/** 
	 * add an object group in world
	 * @param group 
	 */
	public void addMirror(A3DObject m) {
		mirror = m;
	}
	/** 
	 * add an object group in world
	 * @param group 
	 */
	public void addGroup(A3DObjectGroup group) {
		groups.add(group);
	}
	
	/** 
	 * add an object in world
	 * @param obj 
	 */
	public void addObject(A3DObject obj) {
		objects.add(obj);
	}

	public void addTransObject(A3DObject obj) {
		obj.transparentFlag = true;
		transobjects.add(obj);
	}
	public void removeTransObject(int i) {
		if(i<transobjects.size() && i>=0){
			transobjects.get(i).enableTransparent(false); 
			transobjects.remove(i);
		}
	}
	public void removeTransObject(String object_name) {
		for (int i=0;i<transobjects.size();i++)
			if (transobjects.get(i).name.equals(object_name))
				transobjects.remove(i);
	}
	public A3DObject getTransObject(int i) {
		if(i<transobjects.size() && i>=0){
			return transobjects.get(i); 
		}
		return null;
	}
	public int getTransObjectCount() {
		return transobjects.size();
	}

	/** 
	 * remove an object group in world
	 * @param group 
	 */
	public void removeGroup(String group_name) {
		for (int i=0;i<groups.size();i++) 
			if (groups.get(i).name.equals(group_name))
				groups.remove(i);
	}
	
	/** 
	 * remove an object in world
	 * @param obj 
	 */
	public void removeObject(String group_name, String object_name) {
		if (group_name!=null){
			for (int i=0;i<groups.size();i++) 
				if (groups.get(i).name.equals(group_name))
					groups.remove(i);
		}
		else {
			for (int i=0;i<objects.size();i++)
				if (objects.get(i).name.equals(object_name))
					objects.remove(i);
		}
	}	
	
	/** 
	 * get count of object group in world
     */
	public int getGroupCount() {
		return groups.size();
	}

	/** 
	 * get count of object in world(set group_name as null) or in group
	 * @param group_name group name or null for object in world 
     */
	public int getObjectCount(String group_name) {
		if (group_name == null){
			return objects.size();
		}
		for (int i=0;i<groups.size();i++) 
			if (groups.get(i).name.equals(group_name))
				return groups.get(i).getObjectCount();
		return 0;
	}
	
	/** 
	 * find an object group in world
	 * @param group_name name of the group 
	 */
	public A3DObjectGroup getGroup(String group_name) {
		for (int i=0;i<groups.size();i++) 
		{
			if (groups.get(i).name.equals(group_name))
			{
				return groups.get(i);
			}else
			{
				A3DObjectGroup grp=getGroupinGroup(group_name,groups.get(i));
				if(grp!=null)
				{
					return grp;
				}
			}
		}
		return null;
	}

	/** 
	 * find an object group in world 
	 * @param group_name name of the group
	 * @param object_name name of the group
	 */
	public A3DObject getObject(String group_name, String object_name) {
		if (group_name!=null){
			for (int i=0;i<groups.size();i++) 
			{
				if (groups.get(i).name.equals(group_name))
					return groups.get(i).getObject(object_name);
			}
		}
		else {
			for (int i=0;i<objects.size();i++)
			    if (objects.get(i).name.equals(object_name))
					return objects.get(i);
			for (int i=0;i<mirroredobjects.size();i++)
			    if (mirroredobjects.get(i).name.equals(object_name))
					return mirroredobjects.get(i);
			for (int i=0;i<transobjects.size();i++)
			    if (transobjects.get(i).name.equals(object_name))
					return transobjects.get(i);
		}
		return null;
	}
	
	public A3DObject getObject( String object_name) {
		A3DObject obj=getObject(null,object_name);
		if(obj!=null)
			return obj;
		
		for (int i=0;i<groups.size();i++) 
		{
			obj=getObjectinGroup(object_name,groups.get(i));
			if(obj!=null)
			{
				return obj;
			}
		}
		return null;	
	}
 
	public A3DObjectGroup getGroupinGroup(String name,A3DObjectGroup  parentgroup)
	{
		A3DObjectGroup tmp=null;
		for (int i=0;i<parentgroup.getGroupCount();i++) 
		{
			if (parentgroup.getGroup(i).name.equals(name))
				return parentgroup.getGroup(i);
			else
			{
				tmp=getGroupinGroup(name,parentgroup.getGroup(i));
				if(tmp!=null)
					return tmp;
			}
		}
		return null;
	}
	
	public A3DObject getObjectinGroup(String object_name,A3DObjectGroup  parentgroup)
	{
		A3DObject tmp=null;
		for (int i=0;i<parentgroup.getObjectCount();i++) 
		{
			if (parentgroup.getObject(i).name.equals(object_name))
				return parentgroup.getObject(i);
		}
		for (int i=0;i<parentgroup.getGroupCount();i++) 
		{
			tmp=getObjectinGroup(object_name,parentgroup.getGroup(i));
			if(tmp!=null)
				return tmp;
		}	
		return null;
	}
	
	public void generateHardwareBuffers(GL10 gl) {
	    if (mirror != null) {
	        mirror.invalidateHardwareBuffers();
	        mirror.generateHardwareBuffers(gl);
        }
	    for (int i=0;i<transobjects.size();i++) {
	        transobjects.get(i).invalidateHardwareBuffers();
	        transobjects.get(i).generateHardwareBuffers(gl);
        }
	    for (int i=0;i<objects.size();i++) {
            objects.get(i).invalidateHardwareBuffers();
            objects.get(i).generateHardwareBuffers(gl);
	    }
	    for (int i=0;i<groups.size();i++) {
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

	/** 
	 * find an object group in world by index
	 * @param group_name name of the group 
	 */
	public A3DObjectGroup getGroup(int index) {
		if (index<groups.size() && index>=0) 
			return groups.get(index);
		return null;
	}

	/** 
	 * find an object group in world by index 
	 * @param group_index, set to <0 or set to > group size will get object in world 
	 * @param object_index
	 */
	public A3DObject getObject(int group_index, int object_index) {
		if (group_index>=0 && group_index<groups.size()){
			return groups.get(group_index).getObject(object_index);
		}
		else {
			if (object_index>=0 && object_index<objects.size())
				return objects.get(object_index);
		}
		return null;
	}

	/** 
	 * enable an object group for rendering 
	 * @param group_name name of the group 
	 */
	public void enableGroup(String group_name) {
		for (int i=0;i<groups.size();i++) 
			if (groups.get(i).name.equals(group_name))
				groups.get(i).enable();	
	}

	/** 
	 * disable an object group for rendering 
	 * @param group_name name of the group 
	 */
	public void disableGroup(String group_name) {
		for (int i=0;i<groups.size();i++) 
			if (groups.get(i).name.equals(group_name))
				groups.get(i).disable();	
	}

	/** 
	 * enable an object group for rendering 
	 * @param group_name name of the group
	 * @param object_name name of the group
	 */
	public void enableObject(String group_name, String object_name) {
		if (group_name!=null){
			for (int i=0;i<groups.size();i++) 
				if (groups.get(i).name.equals(group_name))
					groups.get(i).enable(object_name);
		}
		else {
			for (int i=0;i<objects.size();i++)
				if (objects.get(i).name.equals(object_name))
					objects.get(i).enable();
		}
	}

	/** 
	 * enable an object group for rendering 
	 * @param group_name name of the group
	 * @param object_name name of the group
	 */
	public void disableObject(String group_name, String object_name) {
		if (group_name!=null){
			for (int i=0;i<groups.size();i++) 
				if (groups.get(i).name.equals(group_name))
					groups.get(i).disable(object_name);
		}
		else {
			for (int i=0;i<objects.size();i++)
				if (objects.get(i).name.equals(object_name))
					objects.get(i).disable();
		}
	}
	
	/** 
	 * add an light in world
	 * @param group 
	 */
	public void addLight(A3DLight light) {
		lights.add(light);
	}

	/** 
	 * add an light in world
	 * @param group 
	 */
	public void removeLight(String name) {
		for (int i=0;i<lights.size();i++)
			if (lights.get(i).name.equals(name))
				lights.remove(i);
	}	

	/** 
	 * get count of light in world
     */
	public int getLightCount() {
		return lights.size();
	}	
	
	/** 
	 * get an light in world
	 * @param index 
	 */
	public A3DLight getLight(int index) {
		if (index<lights.size() && index>=0)
			return lights.get(index);
		return null;
	}
	
	public void setEye(A3DEye eyex)
	{
		eye=eyex;
	}
	
	/** 
	 * set where your eye is
	 * @param x, y, z
	 */
//	public void setEye(float x, float y, float z) {
//		eye.setPosition(x, y, z);
//	}

	/** 
	 * set where world center is
	 * @param x, y, z
	 */
//	public void setCenter(float x, float y, float z) {
//		center.setPosition(x, y, z);
//	}

	/** 
	 * set the direction up of the world
	 * @param x, y, z
	 */
//	public void setUp(float x, float y, float z) {
//		eye.setUp(x, y, z);
//	}
	
	/** 
	 * get the eye object
	 */
	public A3DEye getEyeObj() {
		return eye;
	}
	
	/** 
	 * get the center object
	 */
//	public A3DCenter getCenterObj() {
//		return center;
//	}
	
	/** 
	 * get where your eye is
	 */
	public float[] getEye() {
		return eye.getPosition();
	}

	/** 
	 * get where world center is
	 */
	public float[] getCenter() {
//		return center.getPosition();
		return eye.getCenter();
	}

	/** 
	 * get the direction up of the world
	 */
	public float[] getUp() {
		return eye.getUp();
	}
	
	public float getFrov() {
		return eye.getFrov();
	}
	
//	public void setFrov(float f) {
//		eye.setFrov(f);
//	}
	
	protected int[] activeRange = null;
	public void setActiveRange(int x, int y, int w, int h){
		if(x==0 && y==0 && w==0 && h==0){
			activeRange = null;
		}
		else{
			activeRange = new int[]{x,y,w,h};
		}
	}
	public int[] getActiveRange(){
		return activeRange;
	}
}
