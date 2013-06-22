package com.amlogic.a3d.anim;

import android.view.animation.Interpolator;

import com.amlogic.a3d.scene.A3DNode;

/**
 * Show a model's different frames.
 * <br><br>
 * Example 1.  Play frames 0-20 in 5 seconds of A3DObject o.
 * <pre>{@code
 * A3DFrameAnimation f = new A3DFrameAnimation(o, new LinearInterpolator(), 5000, 0, 20);
 * f.start();
 * }</pre>
 */
public class A3DFrameAnimation extends A3DAnimation {
    private int mFrameFrom;
    private int mFrameTo;
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
    public A3DFrameAnimation(A3DNode node,
                             Interpolator interp,
                             long duration,
                             int frameFrom,
                             int frameTo,
                             int loops) {
        super(node, interp, duration, 0, 0);
        int frameMax = node.getFrameCount() - 1;
        mFrameTo = frameTo > frameMax ? frameMax : frameTo;

        if (frameFrom < 0)
            mFrameFrom = 0;
        else if (frameFrom > mFrameTo)
            mFrameFrom = mFrameTo;
        mLoops = mLoopsLeft = loops;
    }

    public boolean setNode(A3DNode node) {
        if (mState == STATE_RUNNING)
            return false;
        mNode = node;
        return true;
    }
    
    public void start() {
        if (mState == STATE_RUNNING)
            return;
        synchronized (mNode) {
            mStartTime = System.nanoTime() / 1000000L;
            mEndTime = mStartTime + mDuration;
            mNode.setCurrentFrame(mFrameFrom);
        }
        mLoopsLeft = mLoops;
        mState = STATE_RUNNING;
        notifyListeners(A3DAnimationListener.EVENT_STARTING);
    }

    
    public void stop() {
        mState = STATE_STOPPED;
        synchronized (mNode) {
            mNode.setCurrentFrame(mFrameTo);
        }
        notifyListeners(A3DAnimationListener.EVENT_STOPPED);
    }

    
    protected void update(long time) {
        if (mState != STATE_RUNNING)
            return;
        if (time >= mEndTime) {
            if (mLoopsLeft > 0)
                --mLoopsLeft;
            if (mLoopsLeft == 0) {
                mState = STATE_FINISHED;
                synchronized (mNode) {
                    mNode.setCurrentFrame(mFrameTo);
                }
                notifyListeners(A3DAnimationListener.EVENT_FINISHED);
                return;
            }
            else {
                /* reset and start again */
                mStartTime = time;
                mEndTime = mStartTime + mDuration;
                synchronized (mNode) {
                    mNode.setCurrentFrame(mFrameFrom);
                }
                return;
            }
        }
        synchronized (mNode) {
            float normTime = (float)(time - mStartTime) / (float)mDuration;
            float v = mInterp.getInterpolation(normTime);
            float ff = (float)(mFrameFrom + ((mFrameTo - mFrameFrom) * v));
            int f = Math.round(ff);
            mNode.setCurrentFrame(f > mFrameTo ? mFrameTo : f);
        }
    }
    public void  resumeToStart() 
    {
    }

	@Override
	public void start(long delaytime) {
		// TODO Auto-generated method stub
		start();
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
