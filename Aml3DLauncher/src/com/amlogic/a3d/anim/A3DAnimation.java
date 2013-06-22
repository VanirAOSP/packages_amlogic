package com.amlogic.a3d.anim;

import java.util.Iterator;
import java.util.Vector;

import android.view.animation.Interpolator;

import com.amlogic.a3d.scene.A3DNode;

/**
 * Base class for animations.
 * @see A3DTransformAnimation
 * @see A3DFrameAnimation
 * @see A3DPathAnimation
 */
public abstract class A3DAnimation {
    protected A3DNode mNode;
    protected Interpolator mInterp;
    protected long mDuration;
    protected long mStartTime;
    protected long mEndTime;
    protected long preDelayTime=0;
    protected long endDelayTime=0;
    protected int mState;
    private Vector<A3DAnimationListener> mListeners;

    public final int STATE_WAITSTART = 0;
    public final int STATE_RUNNING   = 1;
    public final int STATE_STOPPED   = 2;
    public final int STATE_FINISHED  = 3;    

    /**
     * Animation.
     * @param node      The object to animate.
     * @param interp    An android.view.animation.Interpolator.<br>
     *                  Example:  new LinearInterpolator();
     * @param duration  Time in ms for one loop.
     * @param start     Start time.
     * @param end       End time.
     */
    public A3DAnimation(A3DNode node,
                        Interpolator interp,
                        long duration, 
                        long start, 
                        long end) {
        mNode = node;
        mInterp = interp;
        mDuration = duration;
        mStartTime = start;
        mEndTime = end;
        mState = STATE_WAITSTART;
        mListeners = new Vector<A3DAnimationListener>();
        mListeners.add(A3DAnimator.getInstance());
    }
    
    /**
     * Add a listener to be notified of an animation's start and stop.    
     * @param listener
     * @return
     */
    public boolean addListener(A3DAnimationListener listener) {
        if (mListeners.contains(listener) == false) {
            mListeners.add(listener);
            return true;
        }
        return false;
    }
    
    /**
     * Used by an animation to notify all listeners.
     * @param event_type
     */
    protected void notifyListeners(int event_type) {
        Iterator<A3DAnimationListener> it = mListeners.iterator();
        while (it.hasNext()) {
            A3DAnimationListener listener = it.next();
            listener.onAnimationEvent(event_type, this);
        }
    }
    
    /**
     * Start the animation.
     */
    public abstract void start();
    public abstract void start(long delaytime);

    /**
     * Stop the animation.
     */
    public abstract void stop();

    /**
     * Check if animation is running.
     * 
     * @return
     */
    public boolean isRunning() {
        return mState == STATE_RUNNING;
    }

    /**
     * Stop the animation.
     */
    public void setDuration(long duration){
        mDuration = duration;
    }
    
    public void setDelayTime(long time1,long time2)
    {
    	preDelayTime = time1;
    	endDelayTime = time2;
    }
    
    public abstract boolean setNode(A3DNode node) ;
    public abstract A3DNode getNode();

    /**
     * Get state.
     * @return STATE_WAITSTART, STATE_RUNNING, STATE_FINISHED
     */
    public int getState() {
        return mState;
    }
    public abstract void  resumeToStart() ;
    /** 
     * Update.  Called by Animator.
     */
    protected abstract void update(long time);
    
    public abstract void reverse(boolean flag);

}
