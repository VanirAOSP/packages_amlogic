package com.amlogic.a3d.scene;

import java.nio.ByteBuffer;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

public class TextureManager {
	TextureManager(){
		textures = new Vector<Texture>();
	}
	
	public static TextureManager global = new TextureManager();
	
    /* array contains textures */
    protected Vector<Texture> textures = null;
    
	public void addTexture(Texture tex) {
		textures.add(tex);
	}	

	public void removeTexture(String name) {
		for (int i=0;i<textures.size();i++) {
			if (textures.get(i).name.equals(name)) {
				textures.remove(i);
			}
		}
	}	
	
	public int getTextureCount(){
		return textures.size();
	}

	public Texture getTexture(String name) {
		if (name != null){
			for (int i=0;i<textures.size();i++) {
				if (textures.get(i).name.equals(name)) {
					return textures.get(i);
				}
			}
		}
		return null;
	}

	public Texture getTexture(int index) {
		if (index<textures.size() && index>=0) {
			return textures.get(index);
		}
		return null;
	}	
	
	public ByteBuffer getTextureBuffer(String name) {
		for (int i=0;i<textures.size();i++) {
			if (textures.get(i).name.equals(name)) {
				return textures.get(i).buffer;
			}
		}
		return null;
	}
	
	public int getTextureID(String name) {
		Texture texture = getTexture(name);
		if(texture == null)
			return -1;
		else
			return texture.getID();
	}
	
	public void removeAll(){
		while(textures.size()>0) {
			textures.remove(0);
		}
	}
	public static Bitmap backgroundBmp=null;
	public static int loadEOSTexture(GL10 gl){		
		if(backgroundBmp==null) return -1;
		int[] tmp_tex = new int[1];
		int[] bmpRect = new int[4];
		bmpRect[0] = 0;
		bmpRect[1] = backgroundBmp.getHeight();
		bmpRect[2] = backgroundBmp.getWidth();;
		bmpRect[3] = -backgroundBmp.getHeight();
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glGenTextures(1, tmp_tex, 0);
		int texID = tmp_tex[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, tmp_tex[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
				GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
				GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, backgroundBmp, 0);
        ((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, 
                GL11Ext.GL_TEXTURE_CROP_RECT_OES, bmpRect, 0);
		gl.glDisable(GL10.GL_TEXTURE_2D);

		return texID;
	}

	
}