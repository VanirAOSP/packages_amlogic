package com.amlogic.a3d.scene;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.graphics.Bitmap;
import android.util.Log;

public class Texture {
    private static final String TAG = "Texture";
	String name;
	protected ByteBuffer buffer = null;
	protected int width;
	protected int height;
	protected int id;

	// r,g,b,a
	protected float matAmbient[];
	protected float matDiffuse[];
	protected float matSpecular[];
	protected float matEmission[];
	
	// shininess s
    protected float shininess[];
	
	public Texture(String name, ByteBuffer buffer, int width, int height) {
		this.name = name;
		this.buffer = buffer;
		this.id = -1;
		this.width = width;
		this.height = height;
		init();
	}
	
	public Texture(String name, Bitmap bmp) {
		this.name = name;
		this.id = -1;
		width = bmp.getWidth();
		height = bmp.getHeight();
		buffer = ByteBuffer.allocateDirect(width * height * 4);
		buffer.order(ByteOrder.nativeOrder());
		
		Log.d(TAG, getName());
		Log.d(TAG, "width:"+getWidth()+" height:"+getHeight());
		Log.d(TAG, "id:"+getID()+" buffer len:"+getBuffer().limit());

        int [] pixels = new int[width*height]; 
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int c1, c2;
        for (int i=0;i<pixels.length;i++){
        	c1 = pixels[i]&0x00ff00ff;
        	c2 = pixels[i]&0xff00ff00;
        	c2 |= c1>>16;
        	c2 |= c1<<16;
        	pixels[i] = c2;
        }

        buffer.asIntBuffer().put(pixels);
		buffer.position(0);	
		init();
	}

	void init() {
		matAmbient = new float[]{0.5f,0.5f,0.5f,0.5f};
		matDiffuse = new float[]{1,1,1,1};
		matSpecular = new float[]{1,1,1,1};
		matEmission = new float[]{0,0,0,0};
		shininess = new float[]{10.0f};
	}

	public void setAmbient(float[] matAmbient){
		this.matAmbient = matAmbient.clone();
	}
	public void setDiffuse(float[] matDiffuse){
		this.matDiffuse = matDiffuse.clone();
	}
	public void setSpecular(float[] matSpecular){
		this.matSpecular = matSpecular.clone();
	}
	public void setEmission(float[] matEmission){
		this.matEmission = matEmission.clone();
	}
	public void setShininess(float shininess) {
		this.shininess = new float[1];
		this.shininess[0] = shininess;
	}
	
	float[] getAmbient(){
		return this.matAmbient;
	}
	float[] getDiffuse(){
		return this.matDiffuse;
	}
	float[] getEmission(){
		return this.matEmission;
	}
	float[] getSpecular(){
		return this.matSpecular;
	}
	float getShininess() {
		return this.shininess[0];
	}
	
	public String getName() {
		return name;
	}
	
	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
