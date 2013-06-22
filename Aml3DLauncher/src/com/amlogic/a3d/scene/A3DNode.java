package com.amlogic.a3d.scene;

public interface A3DNode {
	/** 
	 * set the position of this object. 
	 * @param x,y,z -- position. 
	 */
	public void setPosition(float x, float y, float z);
	
	/** 
	 * set the rotation of this object. 
	 * @param angel,x,y,z -- rotation on vector x,y,z. 
	 */
	public void setRotation(float angel, float x, float y, float z);

	/** 
	 * set the scale of this object in A3DWorld. 
	 * @param width,height,depth -- position. 
	 */
	public void setScale(float width, float height, float depth);

	/** 
	 * get the position of this object. 
	 */
	public float[] getPosition();
	
	/** 
	 * get the rotation of this object. 
	 */
	public float[] getRotation();

	/** 
	 * get the scale of this object. 
	 */
	public float[] getScale();
	
	public int getCurrentFrame();
	
	public void setCurrentFrame(int frame);

	public int getFrameCount();
	
	public void setColor(float r, float g, float b, float a);
	public float[] getColor();
	
	public void setTexture(String color,String normal);

}
