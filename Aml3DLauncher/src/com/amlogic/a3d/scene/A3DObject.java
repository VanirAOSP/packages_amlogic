package com.amlogic.a3d.scene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import com.amlogic.a3d.math.MatrixUtils;
import com.amlogic.a3d.util.Model;

import android.opengl.Matrix;
import android.os.Environment;
import android.util.Log;

public class A3DObject implements A3DNode {
	protected String name;	
	protected Vector<A3DObjectFrame> frames = null;
	protected float[] position;
	protected float[] rotation;
	protected float[] scale;
	protected float[] color;
	protected FloatBuffer colorFB;
	protected FloatBuffer colorFBDefaultValue;
	protected boolean enabled = true;
	protected Vector <String> textures;
	protected int current_frame = 0;
	protected boolean mirroredFlag = false;
	protected float[] mirroredParentPosition = null;
	protected boolean transparentFlag = false;
	

    // only one frame and texture right now
    private Texture mTex = null;
    private int mTexIDNorm = 0;
    private int mTexIDColor = 0;
    private int mFaceCount = -1;
    private A3DObjectFrame mFrame = null;

    public boolean mUseHardwareBuffers;
    //public int mVertBufferIndex;
    public int mIndexBufferIndex;
    //public int mTextureCoordBufferIndex;
    //public int mNormalBufferIndex;
    //public int mColorBufferIndex;
    public int mVNTBufferIndex;
    
	/** 
	 * Constructor 
	 * @param name of this object
	 * @param mesh of the only one frame for this object. 
	 */ 
	public A3DObject(String name, Mesh mesh, String colorTexture, String normalTexture,boolean mFlag) {
		this.name = name;
		init();
		addFrame(new A3DObjectFrame(mesh.getIndicesBuffer(),
									mesh.getVertexBuffer(), 
									mesh.getNormalBuffer(), 
									mesh.getTextureCoordsBuffer(), 
									colorTexture,
									normalTexture,
									mesh.getColorBuffer())); 
		mirroredFlag = mFlag;
	} 

	/** 
	 * Constructor 
	 * @param name of this object
	 * @param the only one frame for this object. 
	 */ 
	public A3DObject(String name, A3DObjectFrame frame, boolean mFlag) { 
		this.name = name;
		init();
		addFrame(frame); 
		mirroredFlag = mFlag;
	} 

	/** 
	 * Constructor 
	 * @param name of this object
	 * @param frames The frames for this object. 
	 */ 
	public A3DObject(String name, A3DObjectFrame[] frames, boolean mFlag) { 
		this.name = name;
		init();
		for (int i=0;i<frames.length;i++) 
			addFrame(frames[i]); 
		mirroredFlag = mFlag;
	} 

	/** 
	 * Constructor
	 * @param name of this object 
	 * @param Model contains frames for this object. 
	 */ 
	public A3DObject(String name, Model model, boolean mFlag) { 
		this.name = name;
		init();
		for (int i=0;i<model.getFrameCount();i++) 
			addFrame(new A3DObjectFrame(model.getFrame(i).getMesh())); 
		mirroredFlag = mFlag;
	} 	

	/** 
	 * Constructor
	 * @param name of this object 
	 * @param Model contains frames for this object. 
	 */ 
	public A3DObject(String name, String path, String colorTexture, String normalTexture, boolean mFlag)  throws java.io.IOException { 
		this.name = name;
		init();
		read(path, colorTexture, normalTexture);
		mirroredFlag = mFlag;
	} 	

	private void init() {
		position = new float[]{0,0,0};
		rotation = new float[]{0,0,0,0};
		scale = new float[]{1,1,1};
		color = new float[]{1,1,1,1};
		colorFB = null;
		colorFBDefaultValue = FloatBuffer.allocate(4*4);
		colorFBDefaultValue.put(new float[]{1,1,1,1});
		colorFBDefaultValue.position(0);
		frames = new Vector<A3DObjectFrame>();
		textures = new Vector<String>();
	}
	
	/** 
	 * set the position of this object in A3DWorld. 
	 * @param x,y,z -- position. 
	 */
	public void setPosition(float x, float y, float z) {
		position[0] = x;
		position[1] = y;
		position[2] = z;
	}
	
	public void SetMirroredParentPosition(float[] pos){
		mirroredParentPosition = pos;
	}
	
	/** 
	 * set the rotation of this object. 
	 * @param angel,x,y,z -- rotation on vector x,y,z. 
	 */
	public void setRotation(float angel, float x, float y, float z) {
		rotation[0] = angel;
		rotation[1] = x;
		rotation[2] = y;
		rotation[3] = z;
	}

	/** 
	 * set the scale of this object in A3DWorld. 
	 * @param width,height,depth -- position. 
	 */
	public void setScale(float width, float height, float depth) {
		scale[0] = width;
		scale[1] = height;
		scale[2] = depth;
	}
		

	/** 
	 * get the position of this object. 
	 */
	public float[] getPosition() {
		return position;
	}
	
	/** 
	 * get the rotation of this object. 
	 */
	public float[] getRotation() {
		return rotation;
	}

	/** 
	 * get the scale of this object. 
	 */
	public float[] getScale() {
		return scale;
	}
	
	/** 
	 * enable this object for rendering 
	 */ 
	public void enable() { 
		enabled = true;
	}

	/** 
	 * enable this object for rendering 
	 */ 
	public void disable() { 
		enabled = false;
	}
	
	/** 
	 * Add a Frame to this object. 
	 * @param f The Frame to add. 
	 */ 
	public void addFrame(A3DObjectFrame f) { 
		int i;
		frames.add(f);
		for (i=0;i<textures.size();i++)
			if (textures.get(i)==f.getColorTexture())
				break;
		if(i>= textures.size())
			textures.add(f.getColorTexture());		

		for (i=0;i<textures.size();i++)
			if (textures.get(i)==f.getNormalTexture())
				break;
		if(i>= textures.size())
			textures.add(f.getNormalTexture());		
	}

	public A3DObjectFrame getCurrentObjectFrame() { 
        return frames.get(current_frame);
    }
	
	/** 
	 * get number frame of this object. 
	 */ 
	public int getFrameCount() { 
		return frames.size();
	}

        public void setCurrentFrame(int frame) {
        current_frame = frame;
        
    }   

	/** 
	 * get current frame of this object. 
	 */
    	public int getCurrentFrame() { 
		return current_frame;
	}
	
	/** 
	 * get number texture used by this object. 
	 */ 
	public int getTextureCount() {
		return textures.size();
	}
	
	/** 
	 * get texture index by name. 
	 */ 
	public int getTextureIndex(String name) {
		int i;
		for (i=0;i<getTextureCount();i++) {
			if (name == textures.get(i))
				break;
		}
		return i;
	}

	/** 
	 * save this object. 
	 * @param path  -- path in for saving the object.
	 * format: header frame_header {frame_data} {texture} {color}
	 * 	header: 
	 * 			number_frames(1 integer)
	 * 			extra (1 integer)
	 * 			extra0(1 integer)
	 * 			extra1(1 integer)
	 *	frame_header:
	 * 			{
	 *				offset(1 integer) 
	 * 				vertex_count(1 integer)
	 * 				face_count(1 integer)
	 * 				flag has_normal(bit2),has_coord(bit1),has_color(bit0)
	 *   		} number_frames
	 * 	frame_data:
	 * 			{
	 *  			vertex
	 *  			face
	 *  			normals
	 *  			coordinate
	 *  			color
	 *  		} number_frames
	 *  		
	 */ 
	
	public void save(String path) { 
//		File root = Environment.getExternalStorageDirectory();
		File file = new File(path);
		try {
			int frame_count = getFrameCount();
			int header_length = (6/*+frame_count*4*/)*4;
			int offset;

			ByteBuffer bb = ByteBuffer.allocate(header_length);
			bb.order(ByteOrder.nativeOrder());
			IntBuffer ibuf = bb.asIntBuffer();
			
			ibuf.put(frame_count); // number frame
			
			
			if (frames.get(0).getColorTexture()!=null){
				Texture tex = TextureManager.global.getTexture(frames.get(0).getColorTexture());
				ibuf.put((tex.width<<16)|tex.height); // with texture
				ibuf.put(tex.getBuffer().limit());
			}
			else{
				ibuf.put(0); // extra
				ibuf.put(0); // extra
			}
			if (frames.get(0).getNormalTexture()!=null){
				Texture tex = TextureManager.global.getTexture(frames.get(0).getNormalTexture());
				ibuf.put((tex.width<<16)|tex.height); // with texture
				ibuf.put(tex.getBuffer().limit());
			}
			else{
				ibuf.put(0); // extra
				ibuf.put(0); // extra
			}
			ibuf.put(0); // extra


			FileChannel wChannel = new FileOutputStream(file, false).getChannel();
			bb.position(0);
			wChannel.write(bb);
			
			ByteBuffer bbi = ByteBuffer.allocate(4);
			bbi.order(ByteOrder.nativeOrder());
			IntBuffer bbibuf = bbi.asIntBuffer();
			
			for (int i=0;i<frame_count;i++) {
				A3DObjectFrame frame = frames.get(i);
				ByteBuffer bf = frame.getVNTBuffer().duplicate();
				bf.order(ByteOrder.nativeOrder());
				bbibuf.position(0);
				bbibuf.put(bf.capacity());
				bbi.position(0);
				wChannel.write(bbi);
				wChannel.write(bf);	
				

				bbibuf.position(0);
				bbibuf.put(frames.get(i).getIndicesBuffer().capacity());		
				bbi.position(0);
				wChannel.write(bbi);
				
				if (frames.get(i).getFaceCount()>0) {
					ByteBuffer bi = frames.get(i).getIndicesBuffer().duplicate();
					bf.order(ByteOrder.nativeOrder());
					wChannel.write(bi);
					//System.out.println("indices size: "+bb.limit());
				}

				//write the cube model vertex data;
				ByteBuffer tmp = ByteBuffer.allocate(6*4);
				tmp.order(ByteOrder.nativeOrder());
				FloatBuffer tmpbuf = tmp.asFloatBuffer();
				tmpbuf.put(frames.get(i).getCubeVetex());
				wChannel.write(tmp);
				
			}
			if (frames.get(0).getColorTexture()!=null){
				bb = TextureManager.global.getTextureBuffer(frames.get(0).getColorTexture()).duplicate();
				wChannel.write(bb);
			}
			if (frames.get(0).getNormalTexture()!=null){
				bb = TextureManager.global.getTextureBuffer(frames.get(0).getNormalTexture()).duplicate();
				wChannel.write(bb);
			}
			wChannel.close();
		} 
		catch (IOException e){
			e.printStackTrace();
		}
	}

	public void read(String path, String colorTexture, String normalTexture) throws java.io.IOException { 
//		File root = Environment.getExternalStorageDirectory();
		File file = new File(path);
		if (file.exists())
		try {
			int extension_index = file.getName().lastIndexOf('.');
			ByteBuffer bb = ByteBuffer.allocate(6*4);
			bb.order(ByteOrder.nativeOrder());
			FileChannel rChannel = new FileInputStream(file).getChannel();
			rChannel.read(bb);
			bb.position(0);
			IntBuffer header = bb.asIntBuffer();
			int frame_count = header.get();
			int with_colorTexture = header.get();
			int colorTexture_len = header.get();
			int with_normalTexture = header.get();
			int normalTexture_len = header.get();
			ByteBuffer data;
			@SuppressWarnings("unused")
			int length;
			
			ByteBuffer bbi = ByteBuffer.allocate(4);
			bbi.order(ByteOrder.nativeOrder());
			IntBuffer bbibuf = bbi.asIntBuffer();
			
			//System.out.println("read object " + path);
			//System.out.println("frame count = " + frame_count);
			if (frame_count>0){
				for (int i=0;i<frame_count;i++){
					A3DObjectFrame frame = new A3DObjectFrame();
					bbi.position(0);
					rChannel.read(bbi);
					bbibuf.position(0);
					int vnt_size = bbibuf.get();
					ByteBuffer vnt = ByteBuffer.allocate(vnt_size);
					vnt.order(ByteOrder.nativeOrder());
					rChannel.read(vnt);
					vnt.position(0);
					frame.setVNTBuf(vnt);
					
					bbi.position(0);
					rChannel.read(bbi);					
					bbibuf.position(0);
					int ibcount = bbibuf.get();
					ByteBuffer idsb = ByteBuffer.allocate(ibcount);
					idsb.order(ByteOrder.nativeOrder());
					rChannel.read(idsb);
					idsb.position(0);
					frame.setIndicesBuffer(idsb);
					
					ByteBuffer cubebuffer= ByteBuffer.allocate(6*4);
					cubebuffer.order(ByteOrder.nativeOrder());
					FloatBuffer cubedata = cubebuffer.asFloatBuffer();
					rChannel.read(cubebuffer);
					cubedata.position(0);
					float x1=cubedata.get(0);
					float x2=cubedata.get(1);
					float y1=cubedata.get(2);
					float y2=cubedata.get(3);
					float z1=cubedata.get(4);
					float z2=cubedata.get(5);
					float[] tmp={x1,x2,y1,y2,z1,z2};
					frame.setCubeVetex(tmp);
					addFrame(frame);
				}
			}
			if ((colorTexture==null)&&(with_colorTexture!=0)){
				String ctexture_name = null;
				if ((extension_index>0)&&(extension_index<=file.getName().length()-2)){
					ctexture_name = file.getName().substring(0, extension_index);
					if (TextureManager.global.getTexture(ctexture_name)==null){
						data = ByteBuffer.allocateDirect(colorTexture_len);
						data.order(ByteOrder.nativeOrder());
						length = rChannel.read(data);
						data.position(0);
				       	TextureManager.global.addTexture(new Texture(ctexture_name, data, with_colorTexture>>16, with_colorTexture&0xffff));
				       	for (int i=0;i<frame_count;i++)
				       		frames.get(i).setColorTexture(ctexture_name);
					}
				}
			}
			if ((normalTexture==null)&&(with_normalTexture!=0)){
				String ntexture_name = null;
				if ((extension_index>0)&&(extension_index<=file.getName().length()-2)){
					ntexture_name = file.getName().substring(0, extension_index);
					ntexture_name += "_NORMAL";
					if (TextureManager.global.getTexture(ntexture_name)==null){
						data = ByteBuffer.allocateDirect(normalTexture_len);
						data.order(ByteOrder.nativeOrder());
						length = rChannel.read(data);
						data.position(0);
				       	TextureManager.global.addTexture(new Texture(ntexture_name, data, with_normalTexture>>16, with_normalTexture&0xffff));
				       	for (int i=0;i<frame_count;i++)
				       		frames.get(i).setNormalTexture(ntexture_name);
					}
				}
			}
			rChannel.close();
		} 
		catch (IOException e){
			e.printStackTrace();
		}
	}

    /** 
     * When the OpenGL ES device is lost, GL handles become invalidated.
     * In that case, we just want to "forget" the old handles (without
     * explicitly deleting them) and make new ones.
     */
    public void invalidateHardwareBuffers() {
    	mTexIDColor = 0;
        //mVertBufferIndex = 0;
        mIndexBufferIndex = 0;
        //mTextureCoordBufferIndex = 0;
        //mNormalBufferIndex = 0;
        //mColorBufferIndex = 0;
        mVNTBufferIndex = 0;
        mUseHardwareBuffers = false;
    }
    
    /**
     * Deletes the hardware buffers allocated by this object (if any).
     */
    public void releaseHardwareBuffers(GL10 gl) {
        if (mUseHardwareBuffers) {
            if (gl instanceof GL11) {
                GL11 gl11 = (GL11)gl;
                int[] buffer = new int[1];
                //buffer[0] = mVertBufferIndex;
                //gl11.glDeleteBuffers(1, buffer, 0);
                
                //buffer[0] = mTextureCoordBufferIndex;
                //gl11.glDeleteBuffers(1, buffer, 0);
                
                //buffer[0] = mNormalBufferIndex;
                //gl11.glDeleteBuffers(1, buffer, 0);
                
                //buffer[0] = mColorBufferIndex;
                //gl11.glDeleteBuffers(1, buffer, 0);
                
                buffer[0] = mIndexBufferIndex;
                gl11.glDeleteBuffers(1, buffer, 0);

                buffer[0] = mVNTBufferIndex;
                gl11.glDeleteBuffers(1, buffer, 0);
            }
            
            invalidateHardwareBuffers();
        }
    }

    /** 
     * Allocates hardware buffers on the graphics card and fills them with
     * data if a buffer has not already been previously allocated.  Note that
     * this function uses the GL_OES_vertex_buffer_object extension, which is
     * not guaranteed to be supported on every device.
     * @param gl  A pointer to the OpenGL ES context.
     */
	public void generateHardwareBuffers(GL10 gl) {
	    if (mFrame == null) 
            mFrame = getCurrentObjectFrame();
        if (!mUseHardwareBuffers) {     //TODO !mUseHardwareBuffers? make this easier to understand
            if (gl instanceof GL11) {
                int error;
                GL11 gl11 = (GL11)gl;
                int[] buffer = new int[1];
                
                Log.d("A3DObject", "generateHardwareBuffers");
                /*
                // Allocate and fill the vertex buffer.
                gl11.glGenBuffers(1, buffer, 0);
                mVertBufferIndex = buffer[0];
                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertBufferIndex);      //TODO put all in one buffer?
                final int vertexSize = mFrame.getVertexBuffer().capacity(); 
                gl11.glBufferData(GL11.GL_ARRAY_BUFFER, vertexSize, 
                                  mFrame.getVertexBuffer(), GL11.GL_STATIC_DRAW);
                error = gl11.glGetError();
                if (error != GL10.GL_NO_ERROR)
                    Log.e("A3DObject", "generateHardwareBuffers 1 GLError: " + error);
                
                // Allocate and fill the texture coordinate buffer.
                if (mFrame.getTextureCoordsBuffer() != null) {
                    gl11.glGenBuffers(1, buffer, 0);
                    mTextureCoordBufferIndex = buffer[0];
                    gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mTextureCoordBufferIndex);
                    final int texCoordSize = mFrame.getTextureCoordsBuffer().capacity();
                    gl11.glBufferData(GL11.GL_ARRAY_BUFFER, texCoordSize, 
                                      mFrame.getTextureCoordsBuffer(), GL11.GL_STATIC_DRAW);   
                    error = gl11.glGetError();
                    if (error != GL10.GL_NO_ERROR)
                        Log.e("A3DObject", "2 GLError: " + error);
                }
                
                // Allocate and fill the normal buffer.
                if (mFrame.getNormalBuffer() != null) {
                    gl11.glGenBuffers(1, buffer, 0);
                    mNormalBufferIndex = buffer[0];
                    gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mNormalBufferIndex);
                    final int normalSize = mFrame.getNormalBuffer().capacity();
                    gl11.glBufferData(GL11.GL_ARRAY_BUFFER, normalSize, 
                                      mFrame.getNormalBuffer(), GL11.GL_STATIC_DRAW); 
                }
                error = gl11.glGetError();
                if (error != GL10.GL_NO_ERROR)
                    Log.e("A3DObject", "3 GLError: " + error);

                // Allocate and fill the color buffer.
                if (mFrame.getColorBuffer() != null) {
                    gl11.glGenBuffers(1, buffer, 0);
                    mColorBufferIndex = buffer[0];
                    gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mColorBufferIndex);
                    final int colorSize = mFrame.getColorBuffer().capacity();
                    gl11.glBufferData(GL11.GL_ARRAY_BUFFER, colorSize, 
                                      mFrame.getColorBuffer(), GL11.GL_STATIC_DRAW);   
                    error = gl11.glGetError();
                    if (error != GL10.GL_NO_ERROR)
                        Log.e("A3DObject", "4 GLError: " + error);
                }
                // Unbind the array buffer.
                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
                */
                // Allocate and fill the VNT buffer.
                if (mFrame.getVNTBuffer() != null) {
                    gl11.glGenBuffers(1, buffer, 0);
                    mVNTBufferIndex = buffer[0];
                    gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVNTBufferIndex);
                    final int colorSize = mFrame.getVNTBuffer().capacity();
                    mFrame.getVNTBuffer().order(ByteOrder.nativeOrder());
                    gl11.glBufferData(GL11.GL_ARRAY_BUFFER, colorSize, 
                                      mFrame.getVNTBuffer(), GL11.GL_STATIC_DRAW);   
                    error = gl11.glGetError();
                    if (error != GL10.GL_NO_ERROR)
                        Log.e("A3DObject", "4 GLError: " + error);
                }
                // Unbind the array buffer.
                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
                
                // Allocate and fill the index buffer.
                gl11.glGenBuffers(1, buffer, 0);
                mIndexBufferIndex = buffer[0];
                gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferIndex);
                final int indexSize = mFrame.getIndicesBuffer().capacity();
                mFrame.getIndicesBuffer().order(ByteOrder.nativeOrder());
                gl11.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, indexSize, 
                                  mFrame.getIndicesBuffer(), GL11.GL_STATIC_DRAW);
                error = gl11.glGetError();
                if (error != GL10.GL_NO_ERROR)
                    Log.e("A3DObject", "Generate index buffer GLError: " + error);
                
                // Unbind the element array buffer.
                gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
                

                mUseHardwareBuffers = true;
                
                //assert mVertBufferIndex != 0;
                //assert mTextureCoordBufferIndex != 0;
                assert mIndexBufferIndex != 0;
                assert mVNTBufferIndex != 0;
                assert gl11.glGetError() == 0;
            }
            else Log.d("A3DObject", "generateHardwareBuffers err1");
        }
        else Log.d("A3DObject", "generateHardwareBuffers err2");
    }
	
	public synchronized void render(GL10 gl) {
		if(enabled == false)
			return;
	    GL11 gl11 = (GL11) gl;
		int texIndex = GL10.GL_TEXTURE0;
		if (mFrame == null) 
		    mFrame = getCurrentObjectFrame();
		if ((mFrame.getIndicesBuffer()!=null)&&(mFrame.getVertexBuffer()!=null || mFrame.getVNTBuffer()!=null)) {
			gl.glPushMatrix();
			if(transparentFlag){
//				gl.glDisable(GL10.GL_CULL_FACE);
				gl.glDepthMask(false);
				//gl.glEnable(GL10.GL_FRONT_AND_BACK);
				gl.glEnable(GL10.GL_CULL_FACE);
				//gl.glCullFace(GL10.GL_BACK);


			}
			if ((position[0]!=0)||(position[1]!=0)||(position[2]!=0))
				gl.glTranslatef(position[0],position[1],position[2]);
			if ((scale[0]!=1)||(scale[1]!=1)||(scale[2]!=1))
				gl.glScalef(scale[0],scale[1],scale[2]);
			if (rotation[0]!=0)
				gl.glRotatef(rotation[0], rotation[1], rotation[2], rotation[3]);

			if (mUseHardwareBuffers) {
    			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVNTBufferIndex);
    
    			gl11.glVertexPointer(3, GL10.GL_FLOAT, 32, 0);
    			gl11.glNormalPointer(GL10.GL_FLOAT, 32, 12);
			}
			else {
			    if (mFrame.getVertexBuffer()!=null) 
			        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFrame.getVertexBuffer());
			    if (mFrame.getNormalBuffer()!=null)
			        gl.glNormalPointer(GL10.GL_FLOAT, 0, mFrame.getNormalBuffer());
			}
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnable(GL10.GL_NORMALIZE);
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

			if (mFrame.getColorTexture()!=null || mFrame.getVNTBuffer()!=null){
				gl.glEnable(GL10.GL_LIGHTING);
				gl.glEnable(GL10.GL_TEXTURE_2D);
				//if (mTex == null)
				    mTex = TextureManager.global.getTexture(mFrame.getColorTexture());
					
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, mTex.getAmbient(), 0);
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mTex.getDiffuse(), 0);
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, mTex.getSpecular(), 0);
		        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, mTex.getEmission(), 0);
		        gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, mTex.getShininess());
//		        if (mTexIDNorm == 0)
                    mTexIDNorm = TextureManager.global.getTextureID(mFrame.getNormalTexture());
//                if (mTexIDColor == 0)
                    mTexIDColor = TextureManager.global.getTextureID(mFrame.getColorTexture());
				if (mFrame.getTextureCoordsBuffer()!=null || mFrame.getVNTBuffer()!=null){
				    if (false && mTexIDNorm > 0) {
						gl.glClientActiveTexture(texIndex);
						gl.glActiveTexture( texIndex );
						gl.glEnable( GL10.GL_TEXTURE_2D );
						gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			            gl.glBindTexture( GL10.GL_TEXTURE_2D, mTexIDNorm);//NormalTexture;
			            if (mUseHardwareBuffers)
			                gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 32, 24);
			            else if (mFrame.getTextureCoordsBuffer() != null)
                            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mFrame.getTextureCoordsBuffer());

			            gl.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL11.GL_COMBINE );
			            gl.glTexEnvf( GL10.GL_TEXTURE_ENV, GL11.GL_COMBINE_RGB,  GL11.GL_DOT3_RGB );
			            gl.glTexEnvf( GL10.GL_TEXTURE_ENV, GL11.GL_SRC0_RGB,  GL10.GL_TEXTURE );
			            gl.glTexEnvf( GL10.GL_TEXTURE_ENV, GL11.GL_OPERAND0_RGB, GL10.GL_SRC_COLOR );
			            gl.glTexEnvf( GL10.GL_TEXTURE_ENV, GL11.GL_SRC1_RGB,  GL11.GL_PRIMARY_COLOR );
			            gl.glTexEnvf( GL10.GL_TEXTURE_ENV, GL11.GL_OPERAND1_RGB, GL10.GL_SRC_COLOR );  

			            gl.glTexEnvf(  GL11.GL_TEXTURE_ENV, GL11.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
						gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_SRC0_ALPHA, GL11.GL_CONSTANT);
						gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
						gl.glTexEnvfv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, colorFBDefaultValue);
						gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_SRC1_ALPHA, GL11.GL_TEXTURE);
						gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_OPERAND1_ALPHA, GL11.GL_SRC_ALPHA);

			            gl.glClientActiveTexture(++texIndex);
			            gl.glActiveTexture( texIndex );
			            gl.glEnable( GL10.GL_TEXTURE_2D );   
						gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
						gl.glBindTexture( GL10.GL_TEXTURE_2D, mTexIDColor);//colorTexture;
						if (mUseHardwareBuffers)
						    gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 32, 24);
						else if (mFrame.getTextureCoordsBuffer() != null)
						    gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mFrame.getTextureCoordsBuffer());

						gl.glTexEnvf( GL11.GL_TEXTURE_ENV,  GL11.GL_TEXTURE_ENV_MODE,  GL11.GL_COMBINE );
						gl.glTexEnvf(  GL11.GL_TEXTURE_ENV,  GL11.GL_COMBINE_RGB,   GL11.GL_MODULATE );
						
						gl.glTexEnvf(  GL11.GL_TEXTURE_ENV,  GL11.GL_SRC0_RGB,    GL11.GL_TEXTURE);
						gl.glTexEnvf(  GL11.GL_TEXTURE_ENV,  GL11.GL_OPERAND0_RGB,  GL11.GL_SRC_COLOR );
						gl.glTexEnvf(  GL11.GL_TEXTURE_ENV,   GL11.GL_SRC1_RGB,   GL11.GL_PREVIOUS);
						gl.glTexEnvf(  GL11.GL_TEXTURE_ENV,   GL11.GL_OPERAND1_RGB,  GL11.GL_SRC_COLOR );		               		             				

						gl.glTexEnvf(  GL11.GL_TEXTURE_ENV, GL11.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
						gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_SRC0_ALPHA, GL11.GL_CONSTANT);
						gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
						gl.glTexEnvfv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, (colorFB == null)?colorFBDefaultValue : colorFB);
						gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_SRC1_ALPHA, GL11.GL_TEXTURE);
						gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_OPERAND1_ALPHA, GL11.GL_SRC_ALPHA);
						
					}
					else{
						gl.glClientActiveTexture(texIndex);
			            gl.glActiveTexture( texIndex );
			            gl.glEnable( GL10.GL_TEXTURE_2D );   
						gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
						gl.glBindTexture( GL10.GL_TEXTURE_2D, mTexIDColor);//colorTexture;
						if (mUseHardwareBuffers)
						    gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 32, 24);
						else if (mFrame.getTextureCoordsBuffer() != null)
						    gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mFrame.getTextureCoordsBuffer());

						gl.glTexEnvf( GL11.GL_TEXTURE_ENV,  GL11.GL_TEXTURE_ENV_MODE,  GL11.GL_COMBINE );
						
						gl.glTexEnvf(  GL11.GL_TEXTURE_ENV,  GL11.GL_COMBINE_RGB,   GL11.GL_MODULATE );						
						gl.glTexEnvf(  GL11.GL_TEXTURE_ENV,  GL11.GL_SRC0_RGB,    GL11.GL_TEXTURE);
						gl.glTexEnvf(  GL11.GL_TEXTURE_ENV,  GL11.GL_OPERAND0_RGB,  GL11.GL_SRC_COLOR );
						gl.glTexEnvf(  GL11.GL_TEXTURE_ENV,   GL11.GL_SRC1_RGB,   GL11.GL_PREVIOUS);
						gl.glTexEnvf(  GL11.GL_TEXTURE_ENV,   GL11.GL_OPERAND1_RGB,  GL11.GL_SRC_COLOR );		               		             				
						
						if(colorFB != null && (colorFB.get(0) < 1 || colorFB.get(1) < 1 || colorFB.get(2) < 1)){
	 						//This disturbs normal calculation, so ignore these lines.
							gl.glTexEnvf(  GL11.GL_TEXTURE_ENV, GL11.GL_COMBINE_RGB, GL11.GL_MODULATE);
							gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_SRC0_RGB, GL11.GL_CONSTANT);
							gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
							gl.glTexEnvfv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, (colorFB == null)?colorFBDefaultValue : colorFB);
							gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_SRC1_RGB, GL11.GL_TEXTURE);
							gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
						}
						
						gl.glTexEnvf(  GL11.GL_TEXTURE_ENV, GL11.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
						gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_SRC0_ALPHA, GL11.GL_CONSTANT);
						gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
						gl.glTexEnvfv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, (colorFB == null)?colorFBDefaultValue : colorFB);
						gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_SRC1_ALPHA, GL11.GL_TEXTURE);
						gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_OPERAND1_ALPHA, GL11.GL_SRC_ALPHA);
					}
				}
			}
			else if (mFrame.getColorBuffer() != null)
			{
			    Log.d("obj", "else getColorBuffer != null 0!!!!!");
				gl.glDisable(GL10.GL_LIGHTING);
				gl.glColorPointer(4, GL10.GL_FLOAT, 0, mFrame.getColorBuffer());
				gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			}

			if (mFaceCount == -1)
			    mFaceCount = mFrame.getFaceCount();
			    
			int elementtype = mFrame.STRIPFLAG ? GL11.GL_TRIANGLE_STRIP : GL11.GL_TRIANGLES;
			int elementcount = mFrame.STRIPFLAG ? mFaceCount : mFaceCount*3;
		    if (mUseHardwareBuffers) {
		        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferIndex);
		        gl11.glDrawElements(elementtype, elementcount, GL11.GL_UNSIGNED_SHORT, 0);
		    }
		    else if (mFrame.getIndicesBuffer() != null)
		        gl11.glDrawElements(elementtype, elementcount, GL11.GL_UNSIGNED_SHORT, mFrame.getIndicesBuffer());

			if(mirroredFlag && (mirroredParentPosition == null ||
					(mirroredParentPosition[1]>-0.1f && position[1]+mirroredParentPosition[1]>-0.1f))){
				gl.glDisable(GL10.GL_LIGHTING);
				gl.glEnable(GL10.GL_STENCIL_TEST);
				gl.glStencilFunc(GL10.GL_EQUAL, 1, 0xffffffff);
				gl.glStencilOp(GL10.GL_KEEP, GL10.GL_KEEP, GL10.GL_KEEP);

				gl.glCullFace(GL10.GL_FRONT);
				gl.glEnable(GL10.GL_DEPTH_TEST);
				gl.glDepthMask(true);
					
				gl.glPushMatrix();
				gl.glEnable(GL10.GL_BLEND);

				gl.glRotatef(-1.0f*rotation[0], rotation[1], rotation[2], rotation[3]);
				gl.glScalef(1, -1, 1);
				if(mirroredParentPosition != null)
					gl.glTranslatef(0, 2*(position[1]+mirroredParentPosition[1]), 0);
				else
					gl.glTranslatef(0, 2*position[1], 0);
				gl.glRotatef(rotation[0], rotation[1], rotation[2], rotation[3]);
			
//				gl.glBlendFunc(GL10.GL_ONE_MINUS_DST_ALPHA, GL10.GL_DST_ALPHA);
				if (mUseHardwareBuffers) {
				    gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferIndex);
				    gl11.glDrawElements(GL11.GL_TRIANGLES, mFaceCount*3, GL11.GL_UNSIGNED_SHORT, 0);
				}
				else if (mFrame.getIndicesBuffer() != null)
				    gl11.glDrawElements(GL11.GL_TRIANGLES, mFaceCount*3, GL11.GL_UNSIGNED_SHORT, mFrame.getIndicesBuffer());
//				gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				gl.glPopMatrix();

				gl.glCullFace(GL10.GL_BACK);
				gl.glDisable(GL10.GL_STENCIL_TEST);
				gl.glEnable(GL10.GL_LIGHTING);
			}

			if (mUseHardwareBuffers) {
			    gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
			    gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
			}
			if (mFrame.getNormalBuffer()!=null)
				gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
			if (mFrame.getVertexBuffer()!=null)
				gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			if (mFrame.getColorTexture()!=null){
				gl.glDisable(GL10.GL_TEXTURE_2D);
				if (mFrame.getTextureCoordsBuffer()!=null || mFrame.getVNTBuffer()!=null){
					//gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
					gl.glClientActiveTexture(GL10.GL_TEXTURE0);
					gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
					gl.glClientActiveTexture(GL10.GL_TEXTURE1);
					gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

					gl.glActiveTexture(GL10.GL_TEXTURE1);
					gl.glDisable(GL10.GL_TEXTURE_2D);
					gl.glActiveTexture(GL10.GL_TEXTURE0);
					gl.glEnable(GL10.GL_TEXTURE_2D);				
				}
			}
			else{
				gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
				gl.glEnable(GL10.GL_LIGHTING);
			}			
			if(transparentFlag){
//				gl.glEnable(GL10.GL_CULL_FACE);
				gl.glDepthMask(true);
				//gl.glDisable(GL10.GL_FRONT_AND_BACK);
				gl.glDisable(GL10.GL_CULL_FACE);
				//gl.glCullFace(GL10.GL_BACK);
			}

			gl.glPopMatrix();
		}

	}
		
	public void enableMirror(boolean mFlag){
		mirroredFlag = mFlag;
	}

	public void enableTransparent(boolean transFlag){
		transparentFlag = transFlag;
	}

	
	public float[] getColor() {
		// TODO Auto-generated method stub
		return color;
	}

	
	public void setColor(float r, float g, float b, float a) {
		// TODO Auto-generated method stub
		color[0] = r;
		color[1] = g;
		color[2] = b;
		color[3] = a;
		
		if(r == 1 && g == 1 && b == 1 && a == 1){
			colorFB = null;
		}
		else{
			if(colorFB == null) colorFB = FloatBuffer.allocate(4*4);
			colorFB.put(color);
			colorFB.position(0);
		}
	}
	

	public boolean getTransFlag()
	{
		return transparentFlag;
	}
	public void setTexture(String colortex,String normaltex)
	{
		if(frames.size()==1)
		{
			frames.get(0).colorTexture=colortex;
			frames.get(0).normalTexture=normaltex;
		}
	}
	public String[] getTexture()
	{
		return null;
	}
	
	
	//return 4 points,4 points deside a cube;
	/*
	 *          3---------- 
	 *          /|       / |
	 *        1--|------ 2 | 
	 *         | |      |  |
	 *         | |      |  |
	 *         | /      | /
	 *        0 --------
	 * */
	public float[] getCubeGeometry()
	{
		float[] tmp=frames.get(0).getCubeVetex().clone();
//		Log.v("",name+":"+tmp[0]+" "+tmp[1]+" "+tmp[2]+" "+tmp[3]+" "+tmp[4]+" "+tmp[5] );
		tmp[0]=tmp[0]*scale[0];
		tmp[1]=tmp[1]*scale[0];
		tmp[2]=tmp[2]*scale[1];
		tmp[3]=tmp[3]*scale[1];
		tmp[4]=tmp[4]*scale[2];
		tmp[5]=tmp[5]*scale[2];
        float[] cubepoint = {tmp[0],tmp[2],tmp[5],0,tmp[0],tmp[3],tmp[5],0,
        					 tmp[1],tmp[3],tmp[5],0,tmp[0],tmp[3],tmp[4],0};

        if(rotation[0]!=0)
        {
	        float[] mTemp = new float[16];

			Matrix.setRotateM(mTemp, 0, rotation[0], rotation[1], rotation[2], rotation[3]);
			Log.v("",""+rotation[0]+" "+rotation[1]+" "+rotation[2]+" "+rotation[3]);

			Log.v("rotate",name+":"+mTemp[0]+" "+mTemp[1]+" "+mTemp[2]+" "+mTemp[3]+" "
					+mTemp[4]+" "+mTemp[5]+" "  +mTemp[6]+" "+mTemp[7]+" "
					+mTemp[8]+" "+mTemp[9]+" "+mTemp[10]+" "+mTemp[11]+" "
					+mTemp[12]+" "+mTemp[13]+" "+mTemp[14]+" "+mTemp[15]);
			
			Matrix.multiplyMV(cubepoint,0,mTemp,0,cubepoint,0);
			Matrix.multiplyMV(cubepoint,4,mTemp,0,cubepoint,4);
			Matrix.multiplyMV(cubepoint,8,mTemp,0,cubepoint,8);
			Matrix.multiplyMV(cubepoint,12,mTemp,0,cubepoint,12);
        }
		float[] mresult = {cubepoint[0]+position[0],cubepoint[1]+position[1],cubepoint[2]+position[2],0,
				   cubepoint[4]+position[0],cubepoint[5]+position[1],cubepoint[6]+position[2],0,
				   cubepoint[8]+position[0],cubepoint[9]+position[1],cubepoint[10]+position[2],0,
				   cubepoint[12]+position[0],cubepoint[13]+position[1],cubepoint[14]+position[2],0};

		return mresult;
	}
	
}
