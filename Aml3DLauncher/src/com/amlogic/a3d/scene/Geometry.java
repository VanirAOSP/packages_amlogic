package com.amlogic.a3d.scene;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.amlogic.a3d.scene.TextureManager;

/**
 * Geometry defines a basic object in a 3D scene.
 * It contains the geometric data and all rendering informations.
 * 
 * @author RobinZhu
 */
public abstract class Geometry {
    /* number of vertexes */
    protected int vertNum = 0;

    /* vertex buffer */
    protected ByteBuffer vertBuf = null;

    /* indices buffer */
    protected ByteBuffer indicesBuf = null;
    
    /* normal buffer */
    protected ByteBuffer normBuf = null;

    /* color buffer with 4 float r,g,b,a in group*/
    protected ByteBuffer colorBuf = null;

    /* texture coordinate*/
    protected ByteBuffer texCoordsBuf = null;
    
    /* default color */
    protected float[] defaultColor = {1, 1, 1, 1};
    
    /* texture name */
    String colorTexture;
    String normalTexture;
    
    public boolean STRIPFLAG = false;
    
    /* Vertext+Normal+Texture*/
    protected ByteBuffer vntBuf = null;
    float[] cubeVetex;
    
    /**
     * Constructor of Geometry
     */
    public Geometry() {
    }

    /**
     * returns the number of vertex contained in this geometry.
     */
    public int getVertexCount() {
    	if (vertBuf == null)
    		return 0;
    	return vertBuf.asFloatBuffer().limit()/3;
    }
    
    /**
     * return the byte buffer that contains this geometry's vertex
     *         information.
     */
    public ByteBuffer getVertexBuffer() {
    	return vertBuf;
    }

    /**
     * returns the number of triangles(faces) contained in this geometry.
     */
    public int getFaceCount() {
    	if (indicesBuf == null)
    		return 0;
    	if(STRIPFLAG){
    		return indicesBuf.asShortBuffer().limit();
    	}else{
    		return indicesBuf.asShortBuffer().limit()/3;
    	}
    }    
    
    /**
     * return the byte buffer that contains this geometry's vertex
     *         information.
     */
    public ByteBuffer getIndicesBuffer() {
    	return indicesBuf;
    }    
    
    /**
     * returns the number of normals contained in this geometry.
     */
    public int getNormalCount() {
    	if (normBuf == null)
    		return 0;
    	return normBuf.asFloatBuffer().limit()/3;
    }
    
    /**
     * return the float buffer containing the geometry information.
     */
    public ByteBuffer getNormalBuffer() {
        return normBuf;
    }

    /**
     * returns the number of normals contained in this geometry.
     */
    public int getTextureCoordsCount() {
    	if (texCoordsBuf == null)
    		return 0;
    	return texCoordsBuf.asFloatBuffer().limit()/2;
    }

    /**
     * return the float buffers that contain this geometry's texture information.
     */
    public ByteBuffer getTextureCoordsBuffer() {
        return texCoordsBuf;
    }

    /**
     * returns the number of normals contained in this geometry.
     */
    public int getColorCount() {
    	if (colorBuf == null)
    		return 0;
    	return colorBuf.asFloatBuffer().limit()/4;
    }
    
    /**
     * return the buffer that contains this geometry's color information.
     */
    public ByteBuffer getColorBuffer() {
        if (colorBuf == null){
        	colorBuf = ByteBuffer.allocateDirect(getIndicesBuffer().limit()*4*4);
        	colorBuf.order(ByteOrder.nativeOrder());
        	FloatBuffer fb = colorBuf.asFloatBuffer();
        	for (int i=0; i<getIndicesBuffer().limit(); i++){
        		fb.put(defaultColor);
        	}
			colorBuf.position(0);
        }
    	return colorBuf;
    }       

    /**
     * return the default color used when no color buffer or texture.
     */
    public float[] getDefaultColor() {
    	return defaultColor;
    }
    
    /**
     * <code>setVertexBuffer</code> sets this geometry's vertices via a float
     * buffer consisting of groups of three floats: x,y and z.
     * 
     * @param vertBuf
     *            the new vertex buffer.
     */
    public void setVertexBuffer(ByteBuffer vertBuf) {
        this.vertBuf = vertBuf;
        if (vertBuf != null)
            vertNum = vertBuf.asFloatBuffer().limit() / 3;
        else
            vertNum = 0;
    }    
    
    /**
     * <code>setNormalBuffer</code> sets this geometry's normals via a float
     * buffer consisting of groups of three floats: x,y and z.
     * 
     * @param normBuf
     *            the new normal buffer.
     */
    public void setNormalBuffer(ByteBuffer normBuf) {
        this.normBuf = normBuf;
    }

    /**
     * <code>setIndicesBuffer</code> sets this geometry's normals via a float
     * buffer consisting of groups of three floats: x,y and z.
     * 
     * @param indicesBuf
     *            the new indices buffer.
     */
    public void setIndicesBuffer(ByteBuffer indicesBuf) {
        this.indicesBuf = indicesBuf;
    }    
    
    /**
     * <code>setTextureBuffer</code> sets this geometry's textures (position
     * 0) via a float buffer. This convenience method assumes we are setting
     * coordinates for texture unit 0 and that there are 2 coordinate values per
     * vertex.
     * 
     * @param coords
     *            the new coordinates for unit 0.
     */
    public void setTextureCoordsBuffer(ByteBuffer coords) {
        this.texCoordsBuf = coords;
    }
     
    /**
     * <code>setColorBuffer</code> sets this geometry's colors via a float
     * buffer consisting of groups of four floats: r,g,b and a.
     * 
     * @param colorBuf
     *            the new color buffer.
     */
    public void setColorBuffer(ByteBuffer colorBuf) {
        this.colorBuf = colorBuf;
    }

    /**
     * return the default color used when no color buffer or texture.
     */
    public void setDefaultColor(float[] rgba) {
    	if (rgba.length==4){
    		defaultColor = rgba.clone();
    		colorBuf = null;
    	}
    }
    
    /**
     * <code>setTexture</code> sets this geometry's texture by name
     * 
     * @param name
     *            the texture name in Texture Manager.
     */
    public void setColorTexture(String name) {
        this.colorTexture = name;
    }

    /**
     * <code>getTexture</code> gets this geometry's texture by name
     */
    public String getColorTexture() {
        return colorTexture;
    }
    
    public void setNormalTexture(String name) {
        this.normalTexture = name;
    }

    /**
     * <code>getTexture</code> gets this geometry's texture by name
     */
    public String getNormalTexture() {
        return normalTexture;
    }
    /**
     * <code>getTextureBuffer</code> gets this geometry's texture buffer by name
     */
	public ByteBuffer getTextureBuffer(String name) {
		return TextureManager.global.getTextureBuffer(name);
	}

	public void setVNTBuf(ByteBuffer vnt){
		this.vntBuf = vnt;
	}
	
	public ByteBuffer getVNTBuffer(){
		return this.vntBuf;
	}
	
	public float[] getCubeVetex()
	{
		return this.cubeVetex;
	}
	
	public void  setCubeVetex(float[] vetexdata )
	{
		this.cubeVetex=vetexdata.clone();
	}
}

