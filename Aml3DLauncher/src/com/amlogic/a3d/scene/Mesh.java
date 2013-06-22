/**
 * 
 */
package com.amlogic.a3d.scene;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import android.util.Log;

import com.amlogic.a3d.math.MatrixUtils;

/**
 * @author RobinZhu
 *
 */
public class Mesh extends Geometry {

    /* faces */
    Vector<short[]> faces = null;

    /* vertices */
    Vector<float[]> vertices = null; 
    
    /* normals */
    Vector<float[]> normals = null; 

    /* colors */
    Vector<float[]> colors = null; 
    
    /* texture coordinate */
    Vector<float[]> texture_coords = null; 

    /* if normal shared with vertex */
    protected boolean sharedVertexNormals; 

    /* if texture coordinate shared with vertex */
    protected boolean sharedTextureCoords; 

    /* if shrink mesh to match texture coordinates*/
    protected boolean shrinkToTextureCoords = false; 
    
	float[] cube_vetex={10000,-10000,10000,-10000,10000,-10000};    //used to save x_min,x_max,y_min,y_max,z_min,z_max;

    
    Vector<short[]> face_normal_ix; 
    Vector<short[]> face_tx_ix; 
    short[] vertex_tx_ix;
    
    Vector<float[]> face_normals; 
    Vector<float[]> vertex_normals;

    //For Striped Models
    public boolean STRIPFLAG = false;
    Vector<Short> stripeFaces;
    Vector<Short> stripeFaceNormalIndex;
    Vector<Short> stripeFaceTextureIndex;
    
    /**
	 * @param name
	 */
	public Mesh() {
        sharedVertexNormals = false; 
        sharedTextureCoords = false; 
        faces = new Vector<short[]>();
        vertices = new Vector<float[]>();
        normals = new Vector<float[]>(); 
        texture_coords = new Vector<float[]>();  
        face_normals = new Vector<float[]>(); 
        vertex_normals = new Vector<float[]>();
        face_normal_ix = new Vector<short[]>(); 
        face_tx_ix = new Vector<short[]>(); 
        colors =  new Vector<float[]>();
        
        stripeFaces = new Vector<Short>();
        stripeFaceNormalIndex = new Vector<Short>();
        stripeFaceTextureIndex = new Vector<Short>();
    }
	
	public void addStripeFace(short s){
		stripeFaces.add(s);
	}
	public short getStripeFace(int i){
		return stripeFaces.get(i);
	}
	public int getStripeFaceCount(){
		return stripeFaces.size();
	}
	public void addStripeFaceNormalIndex(short s){
		if(s<0){
			s = 0;
		}
		stripeFaceNormalIndex.add(s);
	}
	public short getStripeFaceNormalIndex(int i){
		return stripeFaceNormalIndex.get(i);
	}
	public int getStripeFacesNormalIndexCount(){
		return stripeFaceNormalIndex.size();
	}
	public void addStripeFaceTextureIndex(short s){
		if(s<0){
			s = 0;
		}
		stripeFaceTextureIndex.add(s);
	}
	public short getStripeFaceTextureIndex(int i){
		return stripeFaceTextureIndex.get(i);
	}
	public int getStripeFacesTextureIndexCount(){
		return stripeFaceTextureIndex.size();
	}
    /** 
     * Get the number of faces in the mesh. 
     * @return The number of faces. 
     **/ 
	
    public int getFaceCount() { 
    	return faces.size(); 
    } 

    /** 
     * Get a specific face in the mesh 
     * @param ix The index of the face. 
     * @return An array of three indices that refer to the face's vertices. 
     **/ 
    public short[] getFace(int ix) { 
    	return faces.get(ix); 
    }     
    
    /** 
     * Get a specific vertex. 
     * @param ix The index of the vertex 
     * @return The coordinates of the vertex. 
     **/ 
    public float[] getVertex(int ix) { 
    	if (ix < 0 || ix >= vertices.size()) 
            return null; 
    	return vertices.get(ix); 
    } 

    /** 
     * Get a normal from the normal list 
     * @param ix The index of in the list 
     * @return The normal at that index. 
     **/ 
    public float[] getNormal(int ix) { 
    	if (ix < 0 || ix >= normals.size()) 
            return null; 
        return normals.get(ix); 
    } 

    /** 
     * Get a specific texture coordinate 
     * @param ix The index of the texture coordinate 
     * @return The specified texture coordinate, or null if it doesn't exist. 
     **/ 
    public float[] getTextureCoordinate(int ix) { 
    	if (ix < 0 || ix >= texture_coords.size()) 
            return null; 
        return texture_coords.get(ix); 
    }

    /** 
     * Add a face to the mesh. 
     * @param face The three indices of the face's vertices 
     **/ 
    public void addFace(short[] face) { 
        short[] fce = new short[3]; 
        for (int i=0;i<3;i++) 
            fce[i] = face[i]; 
        faces.add(fce); 
    }     
    
    /** 
     * Add a vertex to the mesh. 
     * @param vertex The coordinates of the vertex. 
     **/ 
    public void addVertex(float[] vertex) { 
        float[] v = new float[3]; 
        for (int i=0;i<3;i++) 
            v[i] = vertex[i]; 
        vertices.add(vertex); 
        
        cube_vetex[0]=(vertex[0]<cube_vetex[0])?vertex[0]:cube_vetex[0];
        cube_vetex[1]=(vertex[0]>cube_vetex[1])?vertex[0]:cube_vetex[1];
        cube_vetex[2]=(vertex[1]<cube_vetex[2])?vertex[1]:cube_vetex[2];
        cube_vetex[3]=(vertex[1]>cube_vetex[3])?vertex[1]:cube_vetex[3];
        cube_vetex[4]=(vertex[2]<cube_vetex[4])?vertex[2]:cube_vetex[4];
        cube_vetex[5]=(vertex[2]>cube_vetex[5])?vertex[2]:cube_vetex[5];

        
    } 

    public void addColor(float[] color) {
    	colors.add(color);
    }
    	
    /** 
     * Clear the vertices storage 
     */ 
    protected void clearVertices() {
    	vertices.clear(); 
    }
     
    /** 
     * Clear the normals storage 
     */ 
    protected void clearNormals() {
    	normals.clear(); 
    }
     
    /** 
     * Clear the texture coordinate storage 
     */ 
    protected void clearTexCoords() {
    	texture_coords.clear(); 
    }

    /** 
     * Clear the face index storage 
     */ 
    protected void clearFaces() { 
        faces.clear(); 
    }     
    
    /** 
     * Add a normal to the normal list. 
     * @param normal The normal to add. 
     **/ 
    public void addNormal(float[] normal) { 
        normals.add(normal); 
    } 
 
    /** 
     * Add a texture coordinate 
     * @param coord The coordinate to add. 
     **/ 
    public void addTextureCoordinate(float[] coord) { 
        float[] crd = new float[coord.length]; 
        for (int i=0;i<crd.length;i++) { 
            crd[i] = coord[i]; 
        } 
        texture_coords.add(crd); 
    }     
	
    public void stripeOrder(Vector<short[]>f, Vector<short[]>n, Vector<short[]>t){
    	faces.clear();
    	faces = f;
    	face_normal_ix.clear();
    	face_normal_ix = n;
    	face_tx_ix.clear();
    	face_tx_ix = t;
    }
    /** 
     * Uses vertex indices and normal indices also.  Saves memory. 
     * @param b If true, share vertex and normal indices, otherwise don't 
     */ 
    public void setSharedVertexNormals(boolean b) { 
            this.sharedVertexNormals = b; 
    } 
	
    /** 
     * Uses texture indices and normal indices also.  Saves memory. 
     * @param b If true, share vertex and normal indices, otherwise don't 
     */ 
    public void setSharedTextureCoords(boolean b) { 
            this.sharedTextureCoords = b; 
    } 
    
    /** 
     * Calculate the normals for a face from the triangle. 
     * @param h The winding rule to use for the triangle 
     **/ 
    public void calculateFaceNormals(boolean right_hand) { 
            face_normals.clear(); 
            Iterator<short[]> it = faces.iterator(); 
            float[] temp1 = new float[3]; 
            float[] temp2 = new float[3]; 

            while (it.hasNext()) { 
                    short[] face = it.next(); 
                    float[] normal = new float[3]; 

                    float[] p0, p1, p2; 
                    if (right_hand) { 
                            p0 = vertices.get(face[0]); 
                            p1 = vertices.get(face[1]); 
                            p2 = vertices.get(face[2]); 
                    } 
                    else { 
                            p0 = vertices.get(face[2]); 
                            p1 = vertices.get(face[1]); 
                            p2 = vertices.get(face[0]); 
                    } 
                    MatrixUtils.minus(p0,p1,temp1); 
                    MatrixUtils.minus(p2,p1,temp2); 
                    MatrixUtils.cross(temp1,temp2, normal); 
                    MatrixUtils.normalize(normal); 
                    face_normals.add(normal); 
            } 
    } 

    /** 
     * Calculate the average normal for a vertex from its constituent 
     * faces. 
     **/ 
    @SuppressWarnings("unchecked") 
    public void calculateVertexNormals() { 
        vertex_normals.clear(); 
            Vector<float[]>[] norms = new Vector[vertices.size()]; 

            for (int i=0;i<vertices.size();i++) 
                    norms[i] = new Vector<float[]>(); 
            for (int i=0;i<faces.size();i++) { 
                short[] face = faces.get(i); 
                    float[] norm = face_normals.get(i); 
                if (norm != null){
                    norms[face[0]].add(norm); 
                    norms[face[1]].add(norm); 
                    norms[face[2]].add(norm); 
            } 
                else{
                	System.out.println("ERROR norm " + i + " not exist");
                }
        } 
        for (int i=0;i<norms.length;i++) { 
                float[] norm = new float[3]; 
                for (int k=0;k<norms[i].size();k++) { 
                        MatrixUtils.plus(norm, (norms[i].get(k)), null); 
                } 
                MatrixUtils.normalize(norm); 
                vertex_normals.add(norm); 
        } 
    } 	
	
    /** 
     * Scale this mesh by the given factor. 
     * @param scale The scaling factor. 
     **/ 
    public void scale(float scale) { 
            for (int i=0;i<vertices.size();i++) { 
                    vertices.get(i)[0] *= scale; 
                    vertices.get(i)[1] *= scale; 
                    vertices.get(i)[2] *= scale; 
            } 
    } 

    
    /** 
     * Get a normal for a face. 
     * @param ix The index of the face. 
     * @return The normal for that face. 
     **/ 
    public float[] getFaceNormal(int ix) { 
            if (ix < 0 || ix >= face_normals.size()) 
                    return null; 
            return face_normals.get(ix); 
    } 


    /** 
     * Get the normals for a face. 
     * @param ix The index of the face. 
     * @return The indices for the normals at each vertex in the face. 
     **/ 
    public short[] getFaceNormals(int ix) { 
            if (sharedVertexNormals) { 
                    return getFace(ix); 
            } 
            else { 
                    if (ix < 0 || ix >= face_normal_ix.size()) { 
                            return null; 
                    } 
                    return face_normal_ix.get(ix); 
            } 
    } 


    // Utility methods.  You shouldn't need anything below this line. 

    /** 
     * Copy the normals from the vertex normals into the face 
     * normals. 
     **/ 
    public void copyNormals() { 
        for (int i=0;i<faces.size();i++) { 
            face_normal_ix.add(faces.get(i)); 
	    } 
	    for (int i=0;i<vertex_normals.size();i++) { 
	        normals.add(vertex_normals.get(i)); 
	    } 
	    vertex_normals.clear();
    } 


    /** 
     * Get the texture coordinate indices for a face. 
     * @param ix The index of the face. 
     * @return The specified texture indices, or null if they don't exist. 
     **/ 
    public short[] getFaceTextures(int ix) { 
        if (sharedTextureCoords) { 
            return getFace(ix); 
        }
    	if (ix < 0 || ix >= face_tx_ix.size()) 
            return null; 
        return face_tx_ix.get(ix); 
    } 

    /** 
     * Add a texture index for a face. 
     * @param ixs The indices to add. 
     **/ 
    public void addTextureIndices(short[] ixs) { 
            short[] ixs2 = new short[ixs.length]; 
            for (int i=0;i<ixs.length;i++) { 
                    ixs2[i] = ixs[i]; 
            } 
            face_tx_ix.add(ixs2); 
    } 

    /** 
     * Add face normals 
     * @param The three indices of the face's normals in the normal list 
     **/ 
    public void addFaceNormals(short[] normals) { 
            face_normal_ix.add(normals); 
    } 

    /** 
     * Reorder the vertices and textures so that the are in face major order. 
     * Makes the mesh take more memory, but aligned better for VertexBuffer operations. 
     */ 
    public void reorder() { 
            short ct = 0; 
            Vector<float[]> verticesL = new Vector<float[]>(); 
            Vector<float[]> normalsL = new Vector<float[]>(); 
            Vector<float[]> texCoordsL = new Vector<float[]>(); 
            Vector<short[]> indices = new Vector<short[]>(); 

            for (int i=0;i<getFaceCount();i++) { 
                    short[] face = getFace(i); 
                    short[] face_n = getFaceNormals(i); 
                    short[] face_tx = getFaceTextures(i); 
                    short[] index = new short[3]; 
                    for (int j=0;j<3;j++) { 
                            float[] n = getNormal(face_n[j]); 
                            float[] v = getVertex(face[j]); 
                            float[] tx = getTextureCoordinate(face_tx[j]); 
                            verticesL.add(v); 
                            normalsL.add(n); 
                            texCoordsL.add(tx); 
                            index[j] = ct++; 
                    } 
                    indices.add(index); 
            } 

            clearVertices(); 
            clearNormals(); 
            clearTexCoords(); 

            for (int i=0;i<verticesL.size();i++) { 
                    addVertex(verticesL.get(i)); 
                    addNormal(normalsL.get(i)); 
                    addTextureCoordinate(texCoordsL.get(i)); 
            } 

            clearFaces(); 
            for (int i=0;i<indices.size();i++) { 
                    addFace(indices.get(i)); 
            } 
            this.face_tx_ix.clear(); 
            sharedVertexNormals = true; 
            sharedTextureCoords = true; 
    } 
    
    public void shrink(){
        short[] vertex_tx_list = new short[faces.size()]; 
        short[] vertex_ix_list = new short[faces.size()]; 
        for (int i=0;i<vertices.size();i++) {
            Arrays.fill(vertex_tx_list, (short) -1);
            int m = 0;
            int flag = 1;;
        	for (int j=0;j<faces.size();j++){
    			short[] face = getFace(j);
            	short[] face_tx = getFaceTextures(j);
            	for (int k=0;k<3;k++){
            		if (face[k] == i){ // same vertex
            			//System.out.println("vertex " + i + " in face " + j + " point " + k + " use texture " + face_tx[k]);
            			flag = 1;
            			for (int l=0;l<m;l++){
            				if (vertex_tx_list[l]==face_tx[k]){
            					if (l>0){
                					//System.out.println(" change vertex to " + vertex_ix_list[l]);
            						face[k] = vertex_ix_list[l];
            					}
            					flag = 0;
            				}
            			}
            			if (flag==1){
            				if (m>0){
                        		float[] v = getVertex(i);
                        		float[] n = getNormal(i);
                        		face[k] = (short) (vertices.size());
                        		addVertex(v);
                        		addNormal(n);
                    			//System.out.println("add vertex to " + getFace(j)[k] + " texture is " + face_tx[k]);
                    			//vertex_tx_ix[face[k]] = face_tx[k];
            				}
            				else{
            					//vertex_tx_ix[i] = face_tx[k];
            				}
            				vertex_tx_list[m] = face_tx[k];
                    		vertex_ix_list[m] = face[k];
            				m++;
            			}
            		}
        		}
        	}
        }
        vertex_tx_ix = new short[vertices.size()];
        for (int i=0;i<vertices.size();i++) {
        	for (int j=0;j<faces.size();j++){
    			short[] face = getFace(j);
        		for (int k=0;k<3;k++){
            		if (face[k] == i){ // same vertex
            			vertex_tx_ix[i] = getFaceTextures(j)[k];
            		}
        		}
        	}
        }
    	shrinkToTextureCoords = true;
    }
    
    
    public ByteBuffer getVertexBuffer() {
    	if (vertBuf==null){
	    	if (shrinkToTextureCoords){
	        	ByteBuffer bb = ByteBuffer.allocateDirect(vertices.size()*3*4);
	    		bb.order(ByteOrder.nativeOrder());
	    		FloatBuffer fb = bb.asFloatBuffer();
	    		for (int i=0;i<vertices.size();i++) {
	   				float[] v = getVertex(i);
	   				fb.put(v);
	    		}
	    		bb.position(0);
	    		vertBuf = bb;
	    	}
	    	else{
	    		if(STRIPFLAG){
		        	ByteBuffer bb = ByteBuffer.allocateDirect(getStripeFaceCount()*3*4);
		    		bb.order(ByteOrder.nativeOrder());
		    		FloatBuffer fb = bb.asFloatBuffer();
		    		for(int f=0; f<getStripeFaceCount();f++){
		    			float[] v = getVertex(getStripeFace(f));
		    			fb.put(v[0]);
		    			fb.put(v[1]);
		    			fb.put(v[2]);
		    		}
		    		bb.position(0);
		    		vertBuf = bb;
	    		}else{
		        	ByteBuffer bb = ByteBuffer.allocateDirect(getFaceCount()*3*3*4);
		    		bb.order(ByteOrder.nativeOrder());
		    		FloatBuffer fb = bb.asFloatBuffer();
		    		for (int f=0;f<getFaceCount();f++) {
						short[] face = getFace(f);
		    			for (int j=0;j<3;j++) {
		    				float[] v = getVertex(face[j]);
							fb.put((v[0]));
							fb.put((v[1]));
							fb.put((v[2]));
						}
					} 
		    		bb.position(0);
		    		vertBuf = bb;
	    		}
	        } 
    	}
	    return vertBuf;
    } 

    
    public ByteBuffer getNormalBuffer() {
    	if (normBuf==null){
	    	if (normals.size()>0){
		    	if (shrinkToTextureCoords){
				 	ByteBuffer bb = ByteBuffer.allocateDirect(normals.size()*3*4);
				 	bb.order(ByteOrder.nativeOrder());
				 	FloatBuffer fb = bb.asFloatBuffer();
				 	for (int i=0;i<normals.size();i++) {
				 		float[] n = getNormal(i);
		 				fb.put(n);
				 	}
					bb.position(0);
					normBuf = bb;
		    	}
		    	else{
		    		if(STRIPFLAG){
			        	ByteBuffer bb = ByteBuffer.allocateDirect(getStripeFaceCount()*3*4);
			    		bb.order(ByteOrder.nativeOrder());
			    		FloatBuffer fb = bb.asFloatBuffer();
			    		for(int f=0; f<getStripeFaceCount();f++){
			    			float[] v = getNormal(getStripeFaceNormalIndex(f));
			    			fb.put(v[0]);
			    			fb.put(v[1]);
			    			fb.put(v[2]);
			    			Log.v("normalBuffer",""+f);
			    			if(f>=2108){
				    			Log.v("normalBuffer",""+f);
			    			}
			    		}
			    		bb.position(0);
			    		normBuf = bb;
		    		}
		    		else{
		    			ByteBuffer bb = ByteBuffer.allocateDirect(getFaceCount()*3*3*4);		    		
			    	 	bb.order(ByteOrder.nativeOrder());
			    	 	FloatBuffer fb = bb.asFloatBuffer();
			    	 	for (int i=0;i<getFaceCount();i++) {
			    		 		short[] face_n = getFaceNormals(i);
			    	 		for (int j=0;j<3;j++) {
			    	 			float[] n = getNormal(face_n[j]);
					 			fb.put(n);
			    	 		}
			    	 	}
			    		bb.position(0);
			    		normBuf = bb;
		    		}
		        }
    		}
    	}
    	return normBuf;
    } 

    
    public ByteBuffer getTextureCoordsBuffer() { 
    	if (texCoordsBuf==null){
	    	if (texture_coords.size()>0){
		    	if (shrinkToTextureCoords){
			    	ByteBuffer bb = ByteBuffer.allocateDirect(vertices.size()*2*4);
				 	bb.order(ByteOrder.nativeOrder());
				 	FloatBuffer fb = bb.asFloatBuffer();
					for (int i=0;i<vertices.size();i++) {
						float[] tx = getTextureCoordinate(vertex_tx_ix[i]);
						fb.put(tx);
					}
					bb.position(0);
					texCoordsBuf = bb;
		    	}
		    	else{
		    		if(STRIPFLAG){
			        	ByteBuffer bb = ByteBuffer.allocateDirect(getStripeFaceCount()*2*4);
			    		bb.order(ByteOrder.nativeOrder());
			    		FloatBuffer fb = bb.asFloatBuffer();
			    		for(int f=0; f<getStripeFaceCount();f++){
			    			float[] v = getTextureCoordinate(getStripeFaceTextureIndex(f));
			    			fb.put(v[0]);
			    			fb.put(v[1]);
			    		}
						bb.position(0);
						texCoordsBuf = bb;
		    		}
		    		else{
			    		if (face_tx_ix.size()>0){
				    		ByteBuffer bb = ByteBuffer.allocateDirect(getFaceCount()*3*2*4);
				    		bb.order(ByteOrder.nativeOrder());
							FloatBuffer fb = bb.asFloatBuffer();
							for (int i=0;i<getFaceCount();i++) {
								short[] face_tx = getFaceTextures(i);
								if (face_tx==null){
									System.out.println("total face " + faces.size());
									System.out.println("total face texture " + face_tx_ix.size());
									System.out.println("face " + i + " can not find texture.");
								}
								else{
									for (int j=0;j<3;j++) {
										float[] tx = getTextureCoordinate(face_tx[j]);
										if (tx!=null){
											fb.put(tx);
										}
										else{
											System.out.println("total texture coords " + texture_coords.size());
											System.out.println("face " + i + "vertex" + face_tx[j] + " can not find coordinate.");
										}
									}
								}
							}
							bb.position(0);
							texCoordsBuf = bb;
			    		}
		    			
		    		}
			    }
	    	}
    	}
	    return texCoordsBuf;
	}

    
    public ByteBuffer getIndicesBuffer() { 
    	if (indicesBuf==null){
	    	if (shrinkToTextureCoords){
		   		ByteBuffer bb = ByteBuffer.allocateDirect(getFaceCount()*3*2);
			 	bb.order(ByteOrder.nativeOrder());
			 	ShortBuffer sb = bb.asShortBuffer();
				for (int i=0;i<getFaceCount();i++) {
					sb.put(getFace(i));
				}
				bb.position(0);
				indicesBuf = bb;
	    	}
	    	else{
	    		if(STRIPFLAG){
			   		ByteBuffer bb = ByteBuffer.allocateDirect(getStripeFaceCount()*2);
				 	bb.order(ByteOrder.nativeOrder());
				 	ShortBuffer sb = bb.asShortBuffer();
					for (int i=0;i<getStripeFaceCount();i++) {
						if(i<32768)
							sb.put((short)i);
						else 
							sb.put((short)(i-65536));
					}
					bb.position(0);
					indicesBuf = bb;
	    		}else{
			   		ByteBuffer bb = ByteBuffer.allocateDirect(getFaceCount()*3*2);
				 	bb.order(ByteOrder.nativeOrder());
				 	ShortBuffer sb = bb.asShortBuffer();
					for (int i=0;i<getFaceCount()*3;i++) {
						if(i<32768)
							sb.put((short)i);
						else 
							sb.put((short)(i-65536));
					}
					bb.position(0);
					indicesBuf = bb;
	    		}
	    	}
    	}
    	return indicesBuf;
    }

    
    public ByteBuffer getColorBuffer() {
    	if (colorBuf == null){
	    	if (colors.size()>0){
		    	if (shrinkToTextureCoords){
		        	ByteBuffer bb = ByteBuffer.allocateDirect(vertices.size()*4*4);
		    		bb.order(ByteOrder.nativeOrder());
		    		FloatBuffer fb = bb.asFloatBuffer();
		    		for (int i=0;i<vertices.size();i++) {
		   				float[] v = colors.get(i);
		   				fb.put(v);
		    		}
		    		bb.position(0);
		    		colorBuf = bb;
		    	}
		    	else{
		    		if (faces.size()>0){
			    		ByteBuffer bb = ByteBuffer.allocateDirect(getFaceCount()*3*4*4);
			    		bb.order(ByteOrder.nativeOrder());
			    		FloatBuffer fb = bb.asFloatBuffer();
			    		for (int f=0;f<getFaceCount();f++) {
							short[] face = getFace(f);
			    			for (int j=0;j<3;j++) {
			    				float[] v = colors.get(face[j]);
								fb.put(v);
							}
						} 
			    		bb.position(0);
			    		colorBuf = bb;
		    		}
		    	} 
	    	}
	    }
    	return colorBuf;
    }
}