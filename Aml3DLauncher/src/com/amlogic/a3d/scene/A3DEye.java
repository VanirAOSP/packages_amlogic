package com.amlogic.a3d.scene;

import com.amlogic.a3d.scene.A3DLight.Type;

public class A3DEye implements A3DNode {
	private float[] eyePosition;
	private float[] upDirection;
	private float[] viewcenter={0,0,0};
	protected float eyeFrov;
	
	public A3DEye(float[] position, float[] up, float frov) {
		eyePosition = position.clone();
		upDirection = up.clone();
		eyeFrov = frov;
	}
	
	public A3DEye(float[] position, float[] center, float[] up, float frov) {
		eyePosition = position.clone();
		upDirection = up.clone();
		viewcenter = center.clone();
		eyeFrov = frov;
	}
	
	public A3DEye(float[] position) {
		eyePosition = position.clone();
		upDirection = new float[]{0,1,0};
		eyeFrov = 30;
	}
	
	public float[] getUp(){
		return upDirection;
	}
	
	public void setUp(float x, float y, float z){
		upDirection[0] = x;
		upDirection[1] = y;
		upDirection[2] = z;
	}
	
	public float getFrov() {
		return eyeFrov;
	}
	
	public void  setCenter(float x, float y, float z) {
		viewcenter[0] = x;
		viewcenter[1] = y;
		viewcenter[2] = z;
	}
	
	public float[] getCenter() {
		return viewcenter;
	}
	
	public void setFrov(float f) {
		eyeFrov = f;
	}
	
	
	
	public float[] getColor() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public int getCurrentFrame() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getFrameCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public float[] getPosition() {
		return eyePosition.clone();
	}

	
	public float[] getRotation() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public float[] getScale() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setColor(float r, float g, float b, float a) {
		// TODO Auto-generated method stub
		
	}

	
	public void setCurrentFrame(int frame) {
		// TODO Auto-generated method stub
		
	}

	
	public void setPosition(float x, float y, float z) {
		eyePosition[0] = x;
		eyePosition[1] = y;
		eyePosition[2] = z;
	}

	
	public void setRotation(float angel, float x, float y, float z) {
		// TODO Auto-generated method stub
		
	}

	
	public void setScale(float width, float height, float depth) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void setTexture(String color, String normal) {
		// TODO Auto-generated method stub
		
	}

}
