package com.amlogic.a3d.scene.shape;

import com.amlogic.a3d.scene.Mesh;

public class Rectangle extends Mesh {
	protected float width;
	protected float height;
	
	public Rectangle(float width, float height){
		super();
		setSharedTextureCoords(true);
		setSharedVertexNormals(true);
		addVertex(new float[]{-width/2, -height/2, 0});
		addVertex(new float[]{ width/2, -height/2, 0});
		addVertex(new float[]{ width/2,  height/2, 0});
		addVertex(new float[]{-width/2,  height/2, 0});
		addNormal(new float[]{0,0,1});
		addNormal(new float[]{0,0,1});
		addNormal(new float[]{0,0,1});
		addNormal(new float[]{0,0,1});
		addFace(new short[]{0,1,2});
		addFace(new short[]{0,2,3});
		addFaceNormals(new short[]{0,1,2});
		addFaceNormals(new short[]{0,2,3});
		addTextureCoordinate(new float[]{0.0f, 1.0f});
		addTextureCoordinate(new float[]{1.0f, 1.0f});
		addTextureCoordinate(new float[]{1.0f, 0.0f});
		addTextureCoordinate(new float[]{0.0f, 0.0f});
		addTextureIndices(new short[]{0,1,2});
		addTextureIndices(new short[]{0,2,3});
	}
}
