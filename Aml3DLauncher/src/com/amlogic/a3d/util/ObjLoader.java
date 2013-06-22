package com.amlogic.a3d.util; 
  
import java.io.File; 
import java.io.FileInputStream; 
import java.io.IOException; 
import java.io.InputStream; 
import java.io.InputStreamReader; 
import java.io.LineNumberReader; 
import java.util.StringTokenizer; 
import java.util.Vector;

import android.util.Log;

import com.amlogic.a3d.scene.Mesh;
  
 /** 
  * A loader for Wavefront OBJ files. 
  */ 
 public class ObjLoader extends AbstractModelLoader { 
         protected boolean right_hand; 
  
         protected static final String[] suffs = new String[] {".jpg", ".png", ".gif"}; 
          
         // Constructor 
         public ObjLoader() { 
                 this(true); 
         } 
  
         /** 
          * Constructor 
          * @param h The order in which triangle vertices are specified. 
          */ 
         public ObjLoader(boolean right_hand) { 
                 this.right_hand = right_hand; 
         } 
  
         /** 
          * {@inheritDoc} 
          */ 
         public boolean canLoad(File f) { 
                 return f.getName().endsWith(".obj"); 
         } 
  
         /** 
          * {@inheritDoc} 
          */ 
         public Model load(String file)  
         throws IOException 
         { 
                 Model m = load(new FileInputStream(file)); 
                 return m; 
         } 
  
         /** 
          * {@inheritDoc} 
          */ 
         public Model load(String file, String colorTexture, String normalTexture)  
         throws IOException 
         { 
                 Model m = load(new FileInputStream(file), colorTexture, normalTexture); 
                 return m; 
         } 


 		
 		public Model load(InputStream is) throws IOException {
            Model m = load(is, null, null); 
            return m;
 		}          
         
         /** 
          * {@inheritDoc} 
          */ 
         public Model load(InputStream in, String colorTexture, String normalTexture)  
         throws IOException 
         { 
                 boolean file_normal = false; 
                 Mesh m = new Mesh(); 
                 int nCount = 0; 
                 float[] coord = new float[2]; 
  
                 LineNumberReader input = new LineNumberReader(new InputStreamReader(in), (32 * 1024));
                 String line = null; 
                 try { 
                         for (line = input.readLine();  
                         line != null;  
                         line = input.readLine()) 
                         { 
                                 if (line.length() > 0) { 
                                         if (line.startsWith("v ")) { 
                                                 float[] vertex = new float[3]; 
                                                 StringTokenizer tok = new StringTokenizer(line); 
                                                 tok.nextToken(); 
                                                 vertex[0] = Float.parseFloat(tok.nextToken()); 
                                                 vertex[1] = Float.parseFloat(tok.nextToken()); 
                                                 vertex[2] = Float.parseFloat(tok.nextToken()); 
                                                 m.addVertex(vertex); 
                                         } 
                                         else if (line.startsWith("vt ")) { 
                                                 StringTokenizer tok = new StringTokenizer(line); 
                                                 tok.nextToken(); 
                                                 coord[0] = Float.parseFloat(tok.nextToken()); 
                                                 coord[1] = Float.parseFloat(tok.nextToken()); 
                                                 m.addTextureCoordinate(coord); 
                                         } 
                                         else if (line.startsWith("f ")) { 
                                                 short[] face = new short[3]; 
                                                 short[] face_n_ix = new short[3]; 
                                                 short[] face_tx_ix = new short[3]; 
                                                 short[] val; 
  
                                                 StringTokenizer tok = new StringTokenizer(line); 
                                                 tok.nextToken(); 
                                                 val = parseShortTriple(tok.nextToken()); 
                                                 face[0] = val[0]; 
                                                 if (val.length > 1 && val[1] > -1) 
                                                         face_tx_ix[0] = val[1]; 
                                                 if (val.length > 2 && val[2] > -1) 
                                                         face_n_ix[0] = val[2]; 
  
                                                 val = parseShortTriple(tok.nextToken()); 
                                                 face[1] = val[0]; 
                                                 if (val.length > 1 && val[1] > -1) 
                                                         face_tx_ix[1] = val[1]; 
                                                 if (val.length > 2 && val[2] > -1) 
                                                         face_n_ix[1] = val[2]; 
  
                                                 val = parseShortTriple(tok.nextToken()); 
                                                 face[2] = val[0]; 
                                                 if (val.length > 1 && val[1] > -1) { 
                                                         face_tx_ix[2] = val[1]; 
                                                         m.addTextureIndices(face_tx_ix); 
                                                 } 
                                                 if (val.length > 2 && val[2] > -1) { 
                                                         face_n_ix[2] = val[2]; 
                                                         m.addFaceNormals(face_n_ix); 
                                                 } 
                                                 m.addFace(face); 
                                                 if (tok.hasMoreTokens()) { 
                                                         val = parseShortTriple(tok.nextToken()); 
                                                         face[1] = face[2]; 
                                                         face[2] = val[0]; 
                                                         if (val.length > 1 && val[1] > -1) { 
                                                                 face_tx_ix[1] = face_tx_ix[2]; 
                                                                 face_tx_ix[2] = val[1]; 
                                                                 m.addTextureIndices(face_tx_ix); 
                                                         } 
                                                         if (val.length > 2 && val[2] > -1) { 
                                                                 face_n_ix[1] = face_n_ix[2]; 
                                                                 face_n_ix[2] = val[2]; 
                                                                 m.addFaceNormals(face_n_ix); 
                                                         } 
                                                         m.addFace(face); 
                                                 } 
  
                                         } 
                                         else if (line.startsWith("vn ")) { 
                                                 nCount++; 
                                                 float[] norm = new float[3]; 
                                                 StringTokenizer tok = new StringTokenizer(line); 
                                                 tok.nextToken(); 
                                                 norm[0] = Float.parseFloat(tok.nextToken()); 
                                                 norm[1] = Float.parseFloat(tok.nextToken()); 
                                                 norm[2] = Float.parseFloat(tok.nextToken()); 
                                                 m.addNormal(norm); 
                                                 file_normal = true; 
                                         } 
                                         else if(line.startsWith("t ")){
                                        	 short[] val; 
                                        	 if(m.STRIPFLAG){
                                        		 //Another striped data
                                        		 m.addStripeFace(m.getStripeFace(m.getStripeFaceCount()-1));
                                        		 m.addStripeFace(m.getStripeFace(m.getStripeFaceCount()-1));
                                        		 m.addStripeFaceTextureIndex(m.getStripeFaceTextureIndex(m.getStripeFacesTextureIndexCount()-1));
                                        		 m.addStripeFaceTextureIndex(m.getStripeFaceTextureIndex(m.getStripeFacesTextureIndexCount()-1));
                                        		 m.addStripeFaceNormalIndex(m.getStripeFaceNormalIndex(m.getStripeFacesNormalIndexCount()-1));
                                        		 m.addStripeFaceNormalIndex(m.getStripeFaceNormalIndex(m.getStripeFacesNormalIndexCount()-1));
                                                 StringTokenizer tok = new StringTokenizer(line); 

                                                 tok.nextToken(); 

                                                 val = parseShortTriple(tok.nextToken());
                                            	 m.addStripeFace(val[0]);
                                            	 m.addStripeFaceTextureIndex(val[1]);
                                            	 m.addStripeFaceNormalIndex(val[2]);
                                            	 m.addStripeFace(val[0]);
                                            	 m.addStripeFaceTextureIndex(val[1]);
                                            	 m.addStripeFaceNormalIndex(val[2]);

                                            	 while (tok.hasMoreTokens()) { 
                                                	 val = parseShortTriple(tok.nextToken());
                                                	 m.addStripeFace(val[0]);
                                                	 m.addStripeFaceTextureIndex(val[1]);
                                                	 m.addStripeFaceNormalIndex(val[2]);
                                                 }
                                        	 }else{
                                        		 //First time found striped data
                                        		 m.STRIPFLAG = true;
                                                 StringTokenizer tok = new StringTokenizer(line); 
                                                 tok.nextToken(); 
                                                 while (tok.hasMoreTokens()) { 
                                                	 val = parseShortTriple(tok.nextToken());
                                                	 m.addStripeFace(val[0]);
                                                	 m.addStripeFaceTextureIndex(val[1]);
                                                	 m.addStripeFaceNormalIndex(val[2]);
                                                 }
                                        	 }
                                         }
                                         else if(line.startsWith("q ")){
                                             short[] val; 
                                        	 m.STRIPFLAG = true;
                                             StringTokenizer tok = new StringTokenizer(line); 
                                             tok.nextToken(); 
                                             while (tok.hasMoreTokens()) { 
                                            	 val = parseShortTriple(tok.nextToken());
                                            	 m.addStripeFace(val[0]);
                                            	 m.addStripeFaceTextureIndex(val[1]);
                                            	 m.addStripeFaceNormalIndex(val[2]);
                                             }
                                         }
                                 } 
                         } 
                 } 
                 catch (Exception ex) { 
                         System.err.println("Error parsing file:"); 
                         System.err.println(input.getLineNumber()+" : "+line); 
                 } 
                 if (!file_normal) { 
                         m.calculateFaceNormals(right_hand); 
                         m.calculateVertexNormals(); 
                         m.copyNormals(); 
                 } 
                 m.setColorTexture(colorTexture);
                 m.setNormalTexture(normalTexture);
                 //for (int i=0;i<vertex_normals.size();i++) { 
                 //m.setVertexNormal(i, vertex_normals.get(i)); 
                 //}
                 
                 return new Model(m); 
         } 
          
         protected static short parseShort(String val) { 
                 if (val.length() == 0) { 
                         return -1; 
                 } 
                 return Short.parseShort(val); 
         } 
  
         protected static short[] parseShortTriple(String face) { 
                 int ix = face.indexOf("/"); 
                 if (ix == -1) 
                         return new short[] {(short) (Short.parseShort(face)-1)}; 
                 else { 
                         int ix2 = face.indexOf("/", ix+1); 
                         if (ix2 == -1) { 
                                 return new short[]  
                                                {(short) (Short.parseShort(face.substring(0,ix))-1), 
                                		 		 (short) (Short.parseShort(face.substring(ix+1))-1)}; 
                         } 
                         else { 
                                 return new short[]  
                                                {(short) (parseShort(face.substring(0,ix))-1), 
                                                 (short) (parseShort(face.substring(ix+1,ix2))-1), 
                                                 (short) (parseShort(face.substring(ix2+1))-1) 
                                                }; 
                         } 
                 } 
         }

  
 } 