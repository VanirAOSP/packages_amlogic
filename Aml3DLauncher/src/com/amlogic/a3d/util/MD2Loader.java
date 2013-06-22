package com.amlogic.a3d.util; 
  
 import java.io.*; 

import android.util.Log;

import com.amlogic.a3d.scene.Mesh;
  
 /** 
  * A loader for MD2 (Quake II and others) files 
  */ 
 public class MD2Loader extends AbstractModelLoader { 
	 	 private static final String TAG = "MD2Loader"; 
	 	 public int max_frame = 1;
	 	 
         /** 
          * {@inheritDoc} 
          */ 
         public boolean canLoad(File f) { 
                 return (f.getName().endsWith(".md2")); 
         } 
          
         /** 
          * Load a mode from a named file.  Searches for "skin.jpg" or "skin.png" in the same directory. 
          * @param file The name of the file to load 
          * @param scale A re-scaling value for the resulting Meshes 
          * @return The loaded model. 
          */ 
         public Model load(String file, float scale)  
         throws IOException 
         { 
                 File f = new File(file); 
                 File skin = new File(f.getParentFile(), "skin.jpg"); 
                 if (skin.exists()) { 
                         return load(new FileInputStream(f), scale, skin.getAbsolutePath(),skin.getAbsolutePath()); 
                 } 
                 else { 
                         return load(new FileInputStream(f), scale, null, null); 
                 } 
         } 
  
         /** 
          * {@inheritDoc} 
          */ 
         public Model load(InputStream in)  
                 throws IOException 
         { 
                 return load(in, null, null); 
         } 
          
         /** 
          * Load from an input stream 
          * @param in The stream to read from. 
          * @param default_texture The file name of the default texture to use 
          * @return The loaded model. 
          * @throws IOException 
          */ 
         public Model load(InputStream in, String colorTexture, String normalTexture)  
         throws IOException 
         { 
                 return load(in, 1.0f, colorTexture, normalTexture); 
         } 
         
         /** 
          * Load from an input stream 
          * @param in The stream to read from. 
          * @param default_texture The file name of the default texture to use 
          * @param scale The re-scaling factor for the loaded models 
          * @return The loaded model. 
          * @throws IOException 
          */ 
         @SuppressWarnings("unused") 
         public Model load(InputStream in, float scale,  
                         String colorTexture, String normalTexture)  
         throws IOException 
         { 
                 LittleEndianInputStream is = new LittleEndianInputStream(in); 
  
                 int magic = is.readInt(); 
                 int version = is.readInt(); 
                 int skinWidth = is.readInt(); 
                 int skinHeight = is.readInt(); 
                 int frameSize = is.readInt(); 
                 int numSkins = is.readInt(); 
                 int numVertices = is.readInt(); 
                 int numTexCoords = is.readInt(); 
                 int numTriangles = is.readInt(); 
                 int numGlCommands = is.readInt(); 
                 int numFrames = is.readInt(); 
                 int offSkins = is.readInt(); 
                 int offTexCoords = is.readInt(); 
                 int offTriangles = is.readInt(); 
                 int offFrames = is.readInt(); 
                 int offGlCommands = is.readInt(); 
                 int offEnd = is.readInt(); 
                 String ctexture = colorTexture; 
                 String ntexture = normalTexture; 
  
                 if (numFrames > max_frame)
                	 numFrames = max_frame; 

                 Log.v(TAG, Integer.toString(magic)); 
                 Log.v(TAG, Integer.toString(version)); 
                 Log.v(TAG, Integer.toString(skinWidth)+"x"+skinHeight); 
                 Log.v(TAG, "Skins: "+numSkins); 
                 Log.v(TAG, "Verts: "+numVertices); 
                 Log.v(TAG, "Texs: "+numTexCoords); 
                 Log.v(TAG, "Tris: "+numTriangles); 
                 Log.v(TAG, "GLs: "+numGlCommands); 
                 Log.v(TAG, "Frames: "+numFrames); 
                 Log.v(TAG, "size: "+offEnd);
  
                 byte[] bytes = new byte[offEnd-68]; 
                 is.read(bytes); 
  
                 ByteArrayInputStream bs = new ByteArrayInputStream(bytes, offSkins-68, bytes.length-offSkins); 
                 is = new LittleEndianInputStream(bs); 
  
                 for (int i=0;i<numSkins;i++) { 
                         String path = is.readString(64); 
                         File f = new File(path); 
                         if (f.exists()) 
                             ctexture = f.getAbsolutePath(); 
                 } 
  
                 ModelFrame frames[] = new ModelFrame[numFrames]; 
  
  
                 bs = new ByteArrayInputStream(bytes, offFrames-68, bytes.length-offFrames); 
                 is = new LittleEndianInputStream(bs); 
  
                 for (int i=0;i<numFrames;i++) { 
                         Mesh m = new Mesh(); 
                         float[] scle = new float[3]; 
                         scle[0] = is.readFloat(); 
                         scle[1] = is.readFloat(); 
                         scle[2] = is.readFloat(); 
                         float[] translate = new float[3]; 
                         translate[0] = is.readFloat(); 
                         translate[1] = is.readFloat(); 
                         translate[2] = is.readFloat(); 
                         String name = is.readString(16); 
                         for (int j=0;j<numVertices;j++) { 
                                 float[] vert = new float[3]; 
                                 vert[0] = scle[0]*is.readUnsignedChar()+translate[0]; 
                                 vert[2] = scle[1]*is.readUnsignedChar()+translate[1]; 
                                 vert[1] = scle[2]*is.readUnsignedChar()+translate[2]; 
                                 int nIx = is.readUnsignedChar(); 
                                 m.addVertex(vert); 
                                 m.addNormal(anorms[nIx]); 
                         } 
                         m.setColorTexture(ctexture);
                         frames[i] = new ModelFrame(m); 
                 } 
  
  
                 bs =  
                         new ByteArrayInputStream(bytes, offTriangles-68, bytes.length-offTriangles); 
                 is = new LittleEndianInputStream(bs); 
  
  
  
                 for (int i=0;i<numTriangles;i++) { 
                         short[] face = new short[3]; 
                         short[] face_t = new short[3]; 
                         face[0] = is.readUnsignedShort(); 
                         face[1] = is.readUnsignedShort(); 
                         face[2] = is.readUnsignedShort(); 
                         face_t[0] = is.readUnsignedShort(); 
                         face_t[1] = is.readUnsignedShort(); 
                         face_t[2] = is.readUnsignedShort(); 
                         Mesh m = frames[0].getMesh(); 
                         for (int j=0;j<numFrames;j++) { 
                                 m = frames[j].getMesh(); 
                                 m.addFace(face); 
                                 m.setSharedVertexNormals(true); 
                                 m.addTextureIndices(face_t); 
                         } 
                 } 
  
                 bs = new ByteArrayInputStream(bytes, offTexCoords-68, bytes.length-offTexCoords); 
                 is = new LittleEndianInputStream(bs); 
  
                 for (int i=0;i<numTexCoords;i++) { 
                         short s = is.readShort(); 
                         short t = is.readShort(); 
                         float[] coord = new float[2]; 
                         coord[0] = ((float)s)/skinWidth; 
                         coord[1] = ((float)t)/skinHeight; 
                         for (int j=0;j<numFrames;j++)  
                                 frames[j].getMesh().addTextureCoordinate(coord); 
                 } 
  
                 if (scale!=1){
                	 Log.v(TAG, "scale the model" + scale);
                	 for (int i=0;i<frames.length;i++) { 
                		 frames[i].getMesh().scale(scale); 
                	 }
                 }
                 for (int i=0;i<frames.length;i++) { 
                	 Log.v(TAG, "shrink the model");
                	 frames[i].getMesh().shrink();
                 }
                 return new Model(frames); 
         } 
  
         private static float[][] anorms = new float[][] 
                {{ -0.525731f,  0.000000f,  0.850651f },  
                 { -0.442863f,  0.238856f,  0.864188f },  
                 { -0.295242f,  0.000000f,  0.955423f },  
                 { -0.309017f,  0.500000f,  0.809017f },  
                 { -0.162460f,  0.262866f,  0.951056f },  
                 {  0.000000f,  0.000000f,  1.000000f },  
                 {  0.000000f,  0.850651f,  0.525731f },  
                 { -0.147621f,  0.716567f,  0.681718f },  
                 {  0.147621f,  0.716567f,  0.681718f },  
                 {  0.000000f,  0.525731f,  0.850651f },  
                 {  0.309017f,  0.500000f,  0.809017f },  
                 {  0.525731f,  0.000000f,  0.850651f },  
                 {  0.295242f,  0.000000f,  0.955423f },  
                 {  0.442863f,  0.238856f,  0.864188f },  
                 {  0.162460f,  0.262866f,  0.951056f },  
                 { -0.681718f,  0.147621f,  0.716567f },  
                 { -0.809017f,  0.309017f,  0.500000f },  
                 { -0.587785f,  0.425325f,  0.688191f },  
                 { -0.850651f,  0.525731f,  0.000000f },  
                 { -0.864188f,  0.442863f,  0.238856f },  
                 { -0.716567f,  0.681718f,  0.147621f },  
                 { -0.688191f,  0.587785f,  0.425325f },  
                 { -0.500000f,  0.809017f,  0.309017f },  
                 { -0.238856f,  0.864188f,  0.442863f },  
                 { -0.425325f,  0.688191f,  0.587785f },  
                 { -0.716567f,  0.681718f, -0.147621f },  
                 { -0.500000f,  0.809017f, -0.309017f },  
                 { -0.525731f,  0.850651f,  0.000000f },  
                 {  0.000000f,  0.850651f, -0.525731f },  
                 { -0.238856f,  0.864188f, -0.442863f },  
                 {  0.000000f,  0.955423f, -0.295242f },  
                 { -0.262866f,  0.951056f, -0.162460f },  
                 {  0.000000f,  1.000000f,  0.000000f },  
                 {  0.000000f,  0.955423f,  0.295242f },  
                 { -0.262866f,  0.951056f,  0.162460f },  
                 {  0.238856f,  0.864188f,  0.442863f },  
                 {  0.262866f,  0.951056f,  0.162460f },  
                 {  0.500000f,  0.809017f,  0.309017f },  
                 {  0.238856f,  0.864188f, -0.442863f },  
                 {  0.262866f,  0.951056f, -0.162460f },  
                 {  0.500000f,  0.809017f, -0.309017f },  
                 {  0.850651f,  0.525731f,  0.000000f },  
                 {  0.716567f,  0.681718f,  0.147621f },  
                 {  0.716567f,  0.681718f, -0.147621f },  
                 {  0.525731f,  0.850651f,  0.000000f },  
                 {  0.425325f,  0.688191f,  0.587785f },  
                 {  0.864188f,  0.442863f,  0.238856f },  
                 {  0.688191f,  0.587785f,  0.425325f },  
                 {  0.809017f,  0.309017f,  0.500000f },  
                 {  0.681718f,  0.147621f,  0.716567f },  
                 {  0.587785f,  0.425325f,  0.688191f },  
                 {  0.955423f,  0.295242f,  0.000000f },  
                 {  1.000000f,  0.000000f,  0.000000f },  
                 {  0.951056f,  0.162460f,  0.262866f },  
                 {  0.850651f, -0.525731f,  0.000000f },  
                 {  0.955423f, -0.295242f,  0.000000f },  
                 {  0.864188f, -0.442863f,  0.238856f },  
                 {  0.951056f, -0.162460f,  0.262866f },  
                 {  0.809017f, -0.309017f,  0.500000f },  
                 {  0.681718f, -0.147621f,  0.716567f },  
                 {  0.850651f,  0.000000f,  0.525731f },  
                 {  0.864188f,  0.442863f, -0.238856f },  
                 {  0.809017f,  0.309017f, -0.500000f },  
                 {  0.951056f,  0.162460f, -0.262866f },  
                 {  0.525731f,  0.000000f, -0.850651f },  
                 {  0.681718f,  0.147621f, -0.716567f },  
                 {  0.681718f, -0.147621f, -0.716567f },  
                 {  0.850651f,  0.000000f, -0.525731f },  
                 {  0.809017f, -0.309017f, -0.500000f },  
                 {  0.864188f, -0.442863f, -0.238856f },  
                 {  0.951056f, -0.162460f, -0.262866f },  
                 {  0.147621f,  0.716567f, -0.681718f },  
                 {  0.309017f,  0.500000f, -0.809017f },  
                 {  0.425325f,  0.688191f, -0.587785f },  
                 {  0.442863f,  0.238856f, -0.864188f },  
                 {  0.587785f,  0.425325f, -0.688191f },  
                 {  0.688191f,  0.587785f, -0.425325f },  
                 { -0.147621f,  0.716567f, -0.681718f },  
                 { -0.309017f,  0.500000f, -0.809017f },  
                 {  0.000000f,  0.525731f, -0.850651f },  
                 { -0.525731f,  0.000000f, -0.850651f },  
                 { -0.442863f,  0.238856f, -0.864188f },  
                 { -0.295242f,  0.000000f, -0.955423f },  
                 { -0.162460f,  0.262866f, -0.951056f },  
                 {  0.000000f,  0.000000f, -1.000000f },  
                 {  0.295242f,  0.000000f, -0.955423f },  
                 {  0.162460f,  0.262866f, -0.951056f },  
                 { -0.442863f, -0.238856f, -0.864188f },  
                 { -0.309017f, -0.500000f, -0.809017f },  
                 { -0.162460f, -0.262866f, -0.951056f },  
                 {  0.000000f, -0.850651f, -0.525731f },  
                 { -0.147621f, -0.716567f, -0.681718f },  
                 {  0.147621f, -0.716567f, -0.681718f },  
                 {  0.000000f, -0.525731f, -0.850651f },  
                 {  0.309017f, -0.500000f, -0.809017f },  
                 {  0.442863f, -0.238856f, -0.864188f },  
                 {  0.162460f, -0.262866f, -0.951056f },  
                 {  0.238856f, -0.864188f, -0.442863f },  
                 {  0.500000f, -0.809017f, -0.309017f },  
                 {  0.425325f, -0.688191f, -0.587785f },  
                 {  0.716567f, -0.681718f, -0.147621f },  
                 {  0.688191f, -0.587785f, -0.425325f },  
                 {  0.587785f, -0.425325f, -0.688191f },  
                 {  0.000000f, -0.955423f, -0.295242f },  
                 {  0.000000f, -1.000000f,  0.000000f },  
                 {  0.262866f, -0.951056f, -0.162460f },  
                 {  0.000000f, -0.850651f,  0.525731f },  
                 {  0.000000f, -0.955423f,  0.295242f },  
                 {  0.238856f, -0.864188f,  0.442863f },  
                 {  0.262866f, -0.951056f,  0.162460f },  
                 {  0.500000f, -0.809017f,  0.309017f },  
                 {  0.716567f, -0.681718f,  0.147621f },  
                 {  0.525731f, -0.850651f,  0.000000f },  
                 { -0.238856f, -0.864188f, -0.442863f },  
                 { -0.500000f, -0.809017f, -0.309017f },  
                 { -0.262866f, -0.951056f, -0.162460f },  
                 { -0.850651f, -0.525731f,  0.000000f },  
                 { -0.716567f, -0.681718f, -0.147621f },  
                 { -0.716567f, -0.681718f,  0.147621f },  
                 { -0.525731f, -0.850651f,  0.000000f },  
                 { -0.500000f, -0.809017f,  0.309017f },  
                 { -0.238856f, -0.864188f,  0.442863f },  
                 { -0.262866f, -0.951056f,  0.162460f },  
                 { -0.864188f, -0.442863f,  0.238856f },  
                 { -0.809017f, -0.309017f,  0.500000f },  
                 { -0.688191f, -0.587785f,  0.425325f },  
                 { -0.681718f, -0.147621f,  0.716567f },  
                 { -0.442863f, -0.238856f,  0.864188f },  
                 { -0.587785f, -0.425325f,  0.688191f },  
                 { -0.309017f, -0.500000f,  0.809017f },  
                 { -0.147621f, -0.716567f,  0.681718f },  
                 { -0.425325f, -0.688191f,  0.587785f },  
                 { -0.162460f, -0.262866f,  0.951056f },  
                 {  0.442863f, -0.238856f,  0.864188f },  
                 {  0.162460f, -0.262866f,  0.951056f },  
                 {  0.309017f, -0.500000f,  0.809017f },  
                 {  0.147621f, -0.716567f,  0.681718f },  
                 {  0.000000f, -0.525731f,  0.850651f },  
                 {  0.425325f, -0.688191f,  0.587785f },  
                 {  0.587785f, -0.425325f,  0.688191f },  
                 {  0.688191f, -0.587785f,  0.425325f },  
                 { -0.955423f,  0.295242f,  0.000000f },  
                 { -0.951056f,  0.162460f,  0.262866f },  
                 { -1.000000f,  0.000000f,  0.000000f },  
                 { -0.850651f,  0.000000f,  0.525731f },  
                 { -0.955423f, -0.295242f,  0.000000f },  
                 { -0.951056f, -0.162460f,  0.262866f },  
                 { -0.864188f,  0.442863f, -0.238856f },  
                 { -0.951056f,  0.162460f, -0.262866f },  
                 { -0.809017f,  0.309017f, -0.500000f },  
                 { -0.864188f, -0.442863f, -0.238856f },  
                 { -0.951056f, -0.162460f, -0.262866f },  
                 { -0.809017f, -0.309017f, -0.500000f },  
                 { -0.681718f,  0.147621f, -0.716567f },  
                 { -0.681718f, -0.147621f, -0.716567f },  
                 { -0.850651f,  0.000000f, -0.525731f },  
                 { -0.688191f,  0.587785f, -0.425325f },  
                 { -0.587785f,  0.425325f, -0.688191f },  
                 { -0.425325f,  0.688191f, -0.587785f },  
                 { -0.425325f, -0.688191f, -0.587785f },  
                 { -0.587785f, -0.425325f, -0.688191f },  
                 { -0.688191f, -0.587785f, -0.425325f }}; 
 } 