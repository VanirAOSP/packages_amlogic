package com.amlogic.a3d.scene;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class A3DObjectFrame extends Geometry{
	  
	
	public A3DObjectFrame(){;}
	
	/**
     * Constructor with name, vertex, normal texture or color information.
     * 
     * @param name
     *            the name of this geometry.
     * @param vertex
     *            vertex buffer.
     * @param normal
     *            normals buffer.
     * @param coords
     *            texture coordinates
     * @param color
     *            texture buffer in integer(r,g,b,a)
     * @param color
     *            color buffer in 4 float(r,g,b,a)
     */
    public A3DObjectFrame(ByteBuffer indices, ByteBuffer vertex, ByteBuffer normal, ByteBuffer coords, String colorTexture, String normalTexture, ByteBuffer color) {
    	if (indices!=null)
    		setIndicesBuffer(indices.duplicate().order(ByteOrder.nativeOrder()));
    	if (vertex!=null)
    		setVertexBuffer(vertex.duplicate().order(ByteOrder.nativeOrder()));
    	if (normal!=null)
    		setNormalBuffer(normal.duplicate().order(ByteOrder.nativeOrder()));
    	if (coords!=null)
    		setTextureCoordsBuffer(coords.duplicate().order(ByteOrder.nativeOrder()));
    	if (color!=null)
    		setColorBuffer(color.duplicate().order(ByteOrder.nativeOrder()));
    	setColorTexture(colorTexture);
    	setNormalTexture(normalTexture);
    }

    public A3DObjectFrame(Mesh m) {
    	ByteBuffer bb;
    	
    	
    	bb = m.getVertexBuffer();
    	if (bb != null)
    		setVertexBuffer(bb.duplicate().order(ByteOrder.nativeOrder()));
    	
    	bb = m.getNormalBuffer();
    	if (bb != null)
    		setNormalBuffer(bb.duplicate().order(ByteOrder.nativeOrder()));
    	
    	bb = m.getTextureCoordsBuffer();
    	if (bb != null)
    		setTextureCoordsBuffer(bb.duplicate().order(ByteOrder.nativeOrder()));
    	
    	bb = m.getColorBuffer();
    	if (bb != null)
    		setColorBuffer(bb.duplicate().order(ByteOrder.nativeOrder()));
 
    	bb = m.getIndicesBuffer();
    	if (bb != null)
    		setIndicesBuffer(bb.duplicate().order(ByteOrder.nativeOrder()));
    	
    	setColorTexture(m.getColorTexture());
    	setNormalTexture(m.getNormalTexture());
    	
    	if(m.STRIPFLAG){
    		STRIPFLAG = true;
    	}
    	
    	/*
    	 * Here, I want to interleave three kinds of data into one array;
    	 * Vertex(float): x, y, z
    	 * Normal(float): nx, ny, nz
    	 * Texture(float): s, t
    	 * (3+3+2)*4=32Byte Per Vertex
    	 */
    	ByteBuffer vnt = ByteBuffer.allocateDirect((getFaceCount())*3*32);
    	vnt.order(ByteOrder.nativeOrder());
		FloatBuffer vntfb = vnt.asFloatBuffer();
		vntfb.position();
		FloatBuffer vb = this.vertBuf.asFloatBuffer();
		vb.position(0);
		FloatBuffer nb = this.normBuf.asFloatBuffer();
		nb.position(0);
		FloatBuffer tb = this.texCoordsBuf.asFloatBuffer();
		tb.position(0);
		
		for(int f=0; f < m.getFaceCount(); f++){
			for(int v=0; v<3; v++){
				vntfb.put(vb.get()); //x
				vntfb.put(vb.get()); //y
				vntfb.put(vb.get()); //z
				vntfb.put(nb.get()); //nx
				vntfb.put(nb.get()); //ny
				vntfb.put(nb.get()); //nz
				vntfb.put(tb.get()); //s
				vntfb.put(tb.get()); //t
			}
		}
		vntfb.position(0);
    	this.setVNTBuf(vnt);
    	cubeVetex = m.cube_vetex.clone();
    }
}
