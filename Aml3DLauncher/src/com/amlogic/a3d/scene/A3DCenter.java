package com.amlogic.a3d.scene;

public class A3DCenter implements A3DNode {
	private float[] eyeCenter;
	
	public A3DCenter(float[] center){
		eyeCenter = center.clone();
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
		return eyeCenter;
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
		eyeCenter[0] = x;
		eyeCenter[1] = y;
		eyeCenter[2] = z;		
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
