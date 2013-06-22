package com.amlogic.a3d.anim;

import java.util.ArrayList;

import android.util.Log;
import android.view.animation.Interpolator;

import com.amlogic.a3d.scene.A3DNode;

/**
 * Translate, rotate, scale animation.
 * <br><br>
 * Example 1.  Rotate object o from 0 to 90 degrees in 1 second, 2 times.
 * <pre>{@code 
 * A3DTransformAnimation t = new A3DTransformAnimation(o, new LinearInterpolator(), 1000, 2);
 * t.setRotation(0, -90, 0, 1, 0);
 * t.start();
 * }</pre>
 * If you want to save and start an animation by name, use A3DAnimationManager.
 */
public class A3DTransformAnimation extends A3DAnimation {
    private int mLoops, mLoopsLeft;
    private float[] mTranslateFrom;
    private float[] mTranslateTo;
    private float[] mRotateAxis;
    private float mRotateAngleFrom;
    private float mRotateAngleTo;
    private float[] mScaleFrom;
    private float[] mScaleTo;
    private float[] mAlphaColorFrom;
    private float[] mAlphaColorTo;
    private static final int T_TRANSLATE = (1<<0);
    private static final int T_ROTATE = (1<<1);
    private static final int T_SCALE = (1<<2);
    private static final int T_ALPHACOLOR = (1<<3);
    private static final int T_TEXTURE = (1<<4);

    private int mTransformations = 0;
    private class Texture
    {
    	String ColorTexture;
    	String NormalTexture;
    	public Texture(String colort,String normalt)
    	{
    		ColorTexture=colort;
    		NormalTexture=normalt;
    	}
    }
    private ArrayList<Texture> textureList ;
    private boolean reverseTrans=false;
    private int ManualTextureIndex=-1;

    /**
     * Transformation animation.
     * @param node
     * @param interp    An android.view.animation.Interpolator.  
     *                  Example:  new LinearInterpolator();
     * @param duration  Time in ms for one loop.
     * @param loops     Number of times to play animation.  -1 for infinite.
     */
    public A3DTransformAnimation(A3DNode node,
                                 Interpolator interp,
                                 long duration,
                                 int loops) {
        super(node, interp, duration, 0, 0);
        mLoops = mLoopsLeft = loops;
        mTransformations = 0;
        mTranslateFrom = new float[3];
        mTranslateTo = new float[3];
        mRotateAxis = new float[3];
        mRotateAngleFrom = 0;
        mRotateAngleTo = 0;
        mScaleFrom = new float[3];
        mScaleTo = new float[3];
        mAlphaColorFrom = new float[4];
        mAlphaColorTo = new float[4];
        textureList= new ArrayList<Texture>();
    }

    /**
     * Object will jump to from{X,Y,Z} on start(), then move to to{X,Y,Z}. <br>
     * If you want the object to keep on moving to a different point, take a
     * look at A3DPathAnimation. 
     * @param fromX
     * @param fromY
     * @param fromZ
     * @param toX
     * @param toY
     * @param toZ
     */
    public void setTranslation(float fromX, float fromY, float fromZ,
                               float toX,   float toY,   float toZ) {
        mTranslateFrom[0] = fromX;
        mTranslateFrom[1] = fromY;
        mTranslateFrom[2] = fromZ;
        mTranslateTo[0] = toX;
        mTranslateTo[1] = toY;
        mTranslateTo[2] = toZ;
        mTransformations |= T_TRANSLATE;
    }

    /**
     * Rotate around vector x,y,z at object's center.
     * @param angle Angle in degrees.
     * @param x
     * @param y
     * @param z
     */
    public void setRotation(float fromAngle, float toAngle, 
                            float x, float y, float z) {
        mRotateAngleFrom = fromAngle;
        mRotateAngleTo = toAngle;
        mRotateAxis[0] = x;
        mRotateAxis[1] = y;
        mRotateAxis[2] = z;
        mTransformations |= T_ROTATE;
    }
    
    /**
     * Scale object.
     * 
     * @param x
     * @param y
     * @param z
     */
    public void setScale(float fromX, float fromY, float fromZ,
                         float toX,   float toY,   float toZ) {
        mScaleFrom[0] = fromX;
        mScaleFrom[1] = fromY;
        mScaleFrom[2] = fromZ;
        mScaleTo[0] = toX;
        mScaleTo[1] = toY;
        mScaleTo[2] = toZ;
        mTransformations |= T_SCALE;
    }
    
    public void setAlphaColor(float fromR, float fromG, float fromB, float fromA,
            float toR,   float toG,   float toB, float toA) {
		mAlphaColorFrom[0] = fromR;
		mAlphaColorFrom[1] = fromG;
		mAlphaColorFrom[2] = fromB;
		mAlphaColorFrom[3] = fromA;
		mAlphaColorTo[0] = toR;
		mAlphaColorTo[1] = toG;
		mAlphaColorTo[2] = toB;
		mAlphaColorTo[3] = toA;
		mTransformations |= T_ALPHACOLOR;
	}

    public void setTextureList(String[] text)
    {
    	for(int i=0;i<text.length-1;i+=2)
    	{
    		textureList.add(new Texture(text[i],text[i+1]));
    	}
		mTransformations |= T_TEXTURE;

    }

    
    public boolean setNode(A3DNode node) {
        if (mState == STATE_RUNNING)
            return false;
        mNode = node;
        return true;
    }
    
    public A3DNode getNode() {
        return mNode;
    }

    public void start(long delaytime)
    {
        if (mState == STATE_RUNNING)
            return;
        synchronized (mNode) {
            mStartTime = System.nanoTime() / 1000000L +delaytime;
            mEndTime = mStartTime + mDuration;
            moveToStart();
        }
        mLoopsLeft = mLoops;
        mState = STATE_RUNNING;
        notifyListeners(A3DAnimationListener.EVENT_STARTING);    	
    }
    
    public void start() {
    	start(0);
    }
    
    
    public void stepToTexture(int i)
    {
    	if(i<textureList.size())
    		ManualTextureIndex=i;
    }

    
    public void stop() {
//        if (mState != STATE_RUNNING)
//            return;
        mState = STATE_STOPPED;
        synchronized (mNode) {
            moveToEnd(); //?
        }
        notifyListeners(A3DAnimationListener.EVENT_STOPPED);
    }
    public void  resumeToStart() 
    {
    	moveToStart();
    }
    private void moveToStart() {
    	if(reverseTrans)
    	{
    		moveToEndInternal();
    		return;
    	}
    	moveToStartInternal();
    }
    private void moveToStartInternal() {

    	if ((mTransformations & T_TRANSLATE) != 0) {
            mNode.setPosition(mTranslateFrom[0],
                              mTranslateFrom[1],
                              mTranslateFrom[2]);
        }
        if ((mTransformations & T_ROTATE) != 0) {
            mNode.setRotation(mRotateAngleFrom,
                              mRotateAxis[0],
                              mRotateAxis[1],
                              mRotateAxis[2]);
        }
        if ((mTransformations & T_SCALE) != 0) {
            mNode.setScale(mScaleFrom[0],
                           mScaleFrom[1],
                           mScaleFrom[2]);
        }
        if ((mTransformations & T_ALPHACOLOR) != 0) {
        	mNode.setColor(mAlphaColorFrom[0],
        					mAlphaColorFrom[1],
        					mAlphaColorFrom[2],
        					mAlphaColorFrom[3]);
        }
        if ((mTransformations & T_TEXTURE) != 0) {
        	if(ManualTextureIndex!=-1)
        	{
                mNode.setTexture(textureList.get(ManualTextureIndex).ColorTexture, textureList.get(ManualTextureIndex).NormalTexture);
        	}else
        	{
        		mNode.setTexture(textureList.get(0).ColorTexture, textureList.get(0).NormalTexture);
        	}
        }
    }
    
    private void moveToEnd() {
    	if(reverseTrans)
    	{
    		moveToStartInternal();
    		return ;
    	}
    	moveToEndInternal();
    }
    
    private void moveToEndInternal() {

        if ((mTransformations & T_TRANSLATE) != 0) {
            mNode.setPosition(mTranslateTo[0],
                              mTranslateTo[1],
                              mTranslateTo[2]);
        }
        if ((mTransformations & T_ROTATE) != 0) {
            mNode.setRotation(mRotateAngleTo,
                              mRotateAxis[0],
                              mRotateAxis[1],
                              mRotateAxis[2]);
        }
        if ((mTransformations & T_SCALE) != 0) {
            mNode.setScale(mScaleTo[0],
                           mScaleTo[1],
                           mScaleTo[2]);
        }
        if ((mTransformations & T_ALPHACOLOR) != 0) {
        	mNode.setColor(mAlphaColorTo[0],
        					mAlphaColorTo[1],
        					mAlphaColorTo[2],
        					mAlphaColorTo[3]);
        }
        if ((mTransformations & T_TEXTURE) != 0) {
        	if(ManualTextureIndex!=-1)
        	{
                mNode.setTexture(textureList.get(ManualTextureIndex).ColorTexture, textureList.get(ManualTextureIndex).NormalTexture);
        	}else
        	{
	        	int size =textureList.size();
	        	mNode.setTexture(textureList.get(size-1).ColorTexture, textureList.get(size-1).NormalTexture);
        	}
        }
    }

    
    protected synchronized void update(long time) {
        if (mState != STATE_RUNNING)
            return;
        if(time<mStartTime+preDelayTime)
        {
        	return;
        }
        else if (time >= mEndTime) {
            if (mLoopsLeft > 0)
                --mLoopsLeft;
            if (mLoopsLeft == 0) {
                mState = STATE_FINISHED;
                synchronized (mNode) {
                    moveToEnd();
                }
                notifyListeners(A3DAnimationListener.EVENT_FINISHED);
                return;
            }
            else {
                /* reset and start again */
                mStartTime = time;
                mEndTime = mStartTime + mDuration;
                synchronized (mNode) {
//                    moveToStart();
                }
                return;
            }
        }
        else if(time>mEndTime-endDelayTime)
        {
        	return;
        }
   
        if(mEndTime-mStartTime!=mDuration)
        {
        	Log.v("",""+(mEndTime-mStartTime));
        }
        
        synchronized (mNode) {
//            float normTime = (float)(time - mStartTime) / (float)mDuration;
         	float normTime = (float)(time - mStartTime-preDelayTime) / (float)(mDuration-preDelayTime-endDelayTime);
            float v = mInterp.getInterpolation(normTime);   
            if(reverseTrans)
            	v=1-v;
            if ((mTransformations & T_TRANSLATE) != 0) {
                float x, y, z;
                x = mTranslateFrom[0] + ((mTranslateTo[0] - mTranslateFrom[0]) * v);
                y = mTranslateFrom[1] + ((mTranslateTo[1] - mTranslateFrom[1]) * v);
                z = mTranslateFrom[2] + ((mTranslateTo[2] - mTranslateFrom[2]) * v);
                mNode.setPosition(x, y, z);
            }
            if ((mTransformations & T_ROTATE) != 0) {
                float a;
                a = mRotateAngleFrom + ((mRotateAngleTo - mRotateAngleFrom) * v);
                mNode.setRotation(a, mRotateAxis[0], mRotateAxis[1], mRotateAxis[2]);
            }
            if ((mTransformations & T_SCALE) != 0) {
                float x, y, z;
                x = mScaleFrom[0] + ((mScaleTo[0] - mScaleFrom[0]) * v);
                y = mScaleFrom[1] + ((mScaleTo[1] - mScaleFrom[1]) * v);
                z = mScaleFrom[2] + ((mScaleTo[2] - mScaleFrom[2]) * v);
                mNode.setScale(x, y, z);
            }
            if ((mTransformations & T_ALPHACOLOR) != 0) {
                float r, g, b, a;
                r = mAlphaColorFrom[0] + ((mAlphaColorTo[0] - mAlphaColorFrom[0]) * v);
                g = mAlphaColorFrom[1] + ((mAlphaColorTo[1] - mAlphaColorFrom[1]) * v);
                b = mAlphaColorFrom[2] + ((mAlphaColorTo[2] - mAlphaColorFrom[2]) * v);
                a = mAlphaColorFrom[3] + ((mAlphaColorTo[3] - mAlphaColorFrom[3]) * v);
                mNode.setColor(r, g, b, a);
            }
            if ((mTransformations & T_TEXTURE) != 0) {
            	int size =textureList.size();
                int r = (int) (size * (v-0.001));
                if( ManualTextureIndex !=-1 )
                {
                    mNode.setTexture(textureList.get(ManualTextureIndex).ColorTexture, textureList.get(ManualTextureIndex).NormalTexture);
                }
                else
                {
                	mNode.setTexture(textureList.get(r).ColorTexture, textureList.get(r).NormalTexture);
                }
            }
        }
    }

	@Override
	public void reverse(boolean flag) {
		// TODO Auto-generated method stub
		reverseTrans = flag;

	}
}
