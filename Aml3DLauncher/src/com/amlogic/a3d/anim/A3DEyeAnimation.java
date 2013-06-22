package com.amlogic.a3d.anim;

import android.view.animation.Interpolator;

import com.amlogic.a3d.math.MatrixUtils;
import com.amlogic.a3d.scene.A3DEye;
import com.amlogic.a3d.scene.A3DNode;
import com.amlogic.a3d.scene.A3DWorld;

/**
 * Show a model's different frames.
 * <br><br>
 * Example 1.  Play frames 0-20 in 5 seconds of A3DObject o.
 * <pre>{@code
 * A3DFrameAnimation f = new A3DFrameAnimation(o, new LinearInterpolator(), 5000, 0, 20);
 * f.start();
 * }</pre>
 */
public class A3DEyeAnimation extends A3DAnimation {
    private A3DEye eyeFrom;
    private A3DEye eyeTo;
    private int mLoops, mLoopsLeft;
    /**
     * Animates by changing the model's current frame.
     * @param node
     * @param interp    An android.view.animation.Interpolator.  
     *                  Example:  new LinearInterpolator();
     * @param duration  Time in ms for one loop.
     * @param frameFrom Start frame.
     * @param frameTo   End frame.
     * @param loops     Number of times to play animation.  -1 for infinite.
     */
    public A3DEyeAnimation(A3DNode node,
                             Interpolator interp,
                             long duration,
                             A3DEye eye0,
                             A3DEye eye1,
                             int loops) {
        super(node, interp, duration, 0, 0);
        eyeTo = eye1;
        eyeFrom = eye0;
        mLoops = mLoopsLeft = loops;
    }

    
    public boolean setNode(A3DNode node) {
        if (mState == STATE_RUNNING)
            return false;
        mNode = node;
        eyeFrom = new A3DEye(((A3DEye)mNode).getPosition(),((A3DEye)mNode).getCenter(),
				 ((A3DEye)mNode).getUp(),((A3DEye)mNode).getFrov());
        return true; 
    }
    
    public void start() {
    	start(0);
    }
    
	public void start(long delaytime) {
		// TODO Auto-generated method stub
        if (mState == STATE_RUNNING)
            return;
        synchronized (mNode) {
            mStartTime = System.nanoTime() / 1000000L+delaytime;
            mEndTime = mStartTime + mDuration;
            moveToStart();
        }
        mLoopsLeft = mLoops;
        mState = STATE_RUNNING;
        
        notifyListeners(A3DAnimationListener.EVENT_STARTING);
	}

    public void stop() {
        mState = STATE_STOPPED;
        synchronized (mNode) {
            moveToEnd(); //? 
        }
        notifyListeners(A3DAnimationListener.EVENT_STOPPED);
    }

    
    protected void update(long time) {
        if (mState != STATE_RUNNING||time<mStartTime)
            return;
        if (time >= mEndTime) {
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
        synchronized (mNode) {
            float normTime = (float)(time - mStartTime) / (float)mDuration;
            float v = mInterp.getInterpolation(normTime);
        	float[] eyePosition = new float[3] ;
        	MatrixUtils.minus(eyeTo.getPosition(), eyeFrom.getPosition(), eyePosition);
        	MatrixUtils.scalarMultiply(eyePosition, v);
        	MatrixUtils.plus(eyePosition.clone(),eyeFrom.getPosition() , eyePosition);

        	float[] upDirection= new float[3] ;;
        	MatrixUtils.minus(eyeTo.getUp(), eyeFrom.getUp(), upDirection);
        	MatrixUtils.scalarMultiply(upDirection, v);
        	MatrixUtils.plus(upDirection.clone(),eyeFrom.getUp() , upDirection);

        	float[] viewcenter= new float[3] ; ;
        	MatrixUtils.minus(eyeTo.getCenter(), eyeFrom.getCenter(), viewcenter);
        	MatrixUtils.scalarMultiply(viewcenter, v);
        	MatrixUtils.plus(viewcenter.clone(),eyeFrom.getCenter() , viewcenter);
        	
        	float eyeFrov= eyeFrom.getFrov()+(eyeTo.getFrov()-eyeFrom.getFrov())*v;
        	
        	((A3DEye)mNode).setPosition(eyePosition[0], eyePosition[1], eyePosition[2]);
        	((A3DEye)mNode).setUp(upDirection[0], upDirection[1], upDirection[2]);
        	((A3DEye)mNode).setCenter(viewcenter[0], viewcenter[1], viewcenter[2]);
        	((A3DEye)mNode).setFrov(eyeFrov);
        }
    }
    private void moveToStart() {
    	float[] pos =eyeFrom.getPosition();
    	((A3DEye)mNode).setPosition(pos[0],pos[1],pos[2]);
    	pos =eyeFrom.getUp();
    	((A3DEye)mNode).setUp(pos[0],pos[1],pos[2]);
    	pos =eyeFrom.getCenter();
    	((A3DEye)mNode).setCenter(pos[0],pos[1],pos[2]);
    }
    
    private void moveToEnd() {
    	float[] pos =eyeTo.getPosition();
    	((A3DEye)mNode).setPosition(pos[0],pos[1],pos[2]);
    	pos =eyeTo.getUp();
    	((A3DEye)mNode).setUp(pos[0],pos[1],pos[2]);
    	pos =eyeTo.getCenter();
    	((A3DEye)mNode).setCenter(pos[0],pos[1],pos[2]);
    }
    
    public void  resumeToStart() 
    {
    	moveToStart();
    }


	@Override
	public void reverse(boolean flag) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public A3DNode getNode() {
		// TODO Auto-generated method stub
		return null;
	}




}
