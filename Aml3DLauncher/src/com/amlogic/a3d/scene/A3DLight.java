package com.amlogic.a3d.scene;

import javax.microedition.khronos.opengles.GL10;

public class A3DLight implements A3DNode{
	public String name;
    public enum Type {
        Directional,
        Point,
        Spot
    };
    
    public Type type = Type.Point;
    // r,g,b,a
    public float ambient[];
    public float diffuse[];
    public float specular[];
	// position x,y,z,w, 
    // type 		w
    // Directional	0
    // Point		1
    // Spot			0..180
    public float position[];
    public float direction[];
	
    public enum SpotAttenuationWay {
        Constant,
        Linear,
        Quadratic
    };
	public float spotCutoff;
	public float spotExponent;
    public float spotAttenuation;
    public int spotAttenuationWay;
    
	public A3DLight(String name, Type type, float ambient[], float diffuse[], float specular[], float position[],float direction[]) {
		this.type = type;
		this.ambient = ambient.clone();
		this.diffuse = diffuse.clone();
		this.specular = specular.clone();
		
		switch (type){
		case Directional:
			this.position = new float[]{position[0], position[1], position[2], 0};
			this.direction = new float[]{position[0], position[1], position[2]};
			break;
		case Point:
			this.position = new float[]{position[0], position[1], position[2], 1};
			this.direction = new float[]{0, 0, 0};
			break;
		case Spot:
			if (position.length>=4){
				this.position = position.clone();
				if (this.position[3]<0)
					this.position[3] = 0;
				if (this.position[3]>180)
					this.position[3] = 180;
			}
			else{
				this.position = new float[]{position[0], position[1], position[2], 45}; // default 90 degree
			}
			this.direction = direction.clone(); // default direction Z
			break;
		default:
		}
	}

	public void setSpotParam(final float cutoff,final float exponent,SpotAttenuationWay spotAttenuationWay,final float spotAttenuation)
	{
		setSpotCutoff(cutoff);
		setSpotAttenuation(spotAttenuationWay,spotAttenuation);
		setSpotExponent(exponent);
	}
	
	public void setSpotAttenuation(final SpotAttenuationWay AttenuationWay,final float Attenuation)
	{
		switch(AttenuationWay)
		{
			case Constant:
				spotAttenuationWay = GL10.GL_CONSTANT_ATTENUATION;
				break;
			case Linear:
				spotAttenuationWay = GL10.GL_LINEAR_ATTENUATION;
				break;
			case Quadratic:
				spotAttenuationWay = GL10.GL_QUADRATIC_ATTENUATION;
				break;
			default:
				break;
		}
		this.spotAttenuation = Attenuation;
	}
	
	public void setSpotExponent(final float exponent)
	{
		this.spotExponent = exponent;
	}
	
	public float getSpotExponent()
	{
		return this.spotExponent;
	}
	
	public void setSpotCutoff(final float cutoff)
	{
		this.spotCutoff = cutoff;
	}
	
	public float getSpotCutoff()
	{
		return this.spotCutoff;
	}
	public void setDirection(float[] direction) {
		switch (type){
		case Directional:
			this.position[0] = direction[0];
			this.position[1] = direction[1];
			this.position[2] = direction[2];
			this.direction[0] = direction[0];
			this.direction[1] = direction[1];
			this.direction[2] = direction[2];
			break;
		case Point:
			break;
		case Spot:
			this.direction[0] = direction[0];
			this.direction[1] = direction[1];
			this.direction[2] = direction[2];
			break;
		default:
		}
	}
	
	//
	public int getCurrentFrame() {
		return 0;
	}

	//
	public int getFrameCount() {
		return 0;
	}

	//
	public float[] getPosition() {
		if (type != Type.Directional)
			return position;
		else
			return null;
	}

	//
	public float[] getRotation() {
		if (type != Type.Point)
			return direction;
		return null;
	}

	//
	public float[] getScale() {
		return new float[]{1,1,1};
	}

	//
	public void setCurrentFrame(int frame) {
		// not support
	}

	//
	public void setPosition(float x, float y, float z) {
		switch (type){
		case Directional:
			break;
		case Point:
		case Spot:
			this.position[0] = x;
			this.position[1] = y;
			this.position[2] = z;
			break;
		default:
		}
	}

	//
	public void setRotation(float angel, float x, float y, float z) {
		switch (type){
		case Directional:
			this.position[0] = x;
			this.position[1] = y;
			this.position[2] = z;
			this.direction[0] = x;
			this.direction[1] = y;
			this.direction[2] = z;
			break;
		case Point:
			break;
		case Spot:
			this.direction[0] = x;
			this.direction[1] = y;
			this.direction[2] = z;
			this.direction[3] = angel;
			break;
		default:
		}
	}

	//
	public void setScale(float width, float height, float depth) {
		// not support
	}
	
	// Fix me -- should use GL_LIGHTn instead of GL_LIGHT0+i
	protected synchronized void render(GL10 gl, int i){
		int lightIndex = GL10.GL_LIGHT0;
		switch(i){
		case 0:
			lightIndex = GL10.GL_LIGHT0;
			break;
		case 1:
			lightIndex = GL10.GL_LIGHT1;
			break;
		case 2:
			lightIndex = GL10.GL_LIGHT2;
			break;
		case 3:
			lightIndex = GL10.GL_LIGHT3;
			break;
		case 4:
			lightIndex = GL10.GL_LIGHT4;
			break;
		case 5:
			lightIndex = GL10.GL_LIGHT5;
			break;
		case 6:
			lightIndex = GL10.GL_LIGHT6;
			break;
		case 7:
			lightIndex = GL10.GL_LIGHT7;
			break;
		}
		//gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(lightIndex);
		gl.glLightfv(lightIndex, GL10.GL_AMBIENT, ambient, 0);
		gl.glLightfv(lightIndex, GL10.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(lightIndex, GL10.GL_SPECULAR, specular, 0);
		switch (type){
		case Directional:
			gl.glLightfv(lightIndex, GL10.GL_POSITION, position, 0);
			break;
		case Point:
			gl.glLightfv(lightIndex, GL10.GL_POSITION, position, 0);
			break;
		case Spot:
			gl.glLightfv(lightIndex, GL10.GL_POSITION, position, 0);
			gl.glLightfv(lightIndex, GL10.GL_SPOT_DIRECTION, direction, 0);
			gl.glLightf(lightIndex, GL10.GL_SPOT_CUTOFF, spotCutoff); 
			gl.glLightf(lightIndex, spotAttenuationWay, spotAttenuation); 
			gl.glLightf(lightIndex, GL10.GL_SPOT_EXPONENT, spotExponent); 
			break;
		default:
			break;
		}
	}

	
	public float[] getColor() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setColor(float r, float g, float b, float a) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setTexture(String color, String normal) {
		// TODO Auto-generated method stub
		
	}

}
