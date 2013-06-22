package com.amlogic.a3d.anim;

import java.util.Iterator;
import java.util.Vector;

import android.util.Log;

/**
 * Updates objects for their animation by calling every object's update().
 */
public class A3DAnimator implements A3DAnimationListener {
    private Vector<A3DAnimation> mActiveAnimations;
    private Vector<A3DAnimation> mStartingAnimations;
    private Vector<A3DAnimation> mFinishedAnimations;
    private static A3DAnimator instance = new A3DAnimator();
    private long mLastTime;
    private static final long FRAME_TIME_DELTA = 17;    //time between each update in milliseconds
    private static final int STATE_DEAD = 0;
    private static final int STATE_ALIVE = 1;
    private static final int STATE_SLEEPING = 2;
    private int mState = STATE_ALIVE;
    
    private A3DAnimator() {
        super();
        mActiveAnimations = new Vector<A3DAnimation>();
        mStartingAnimations = new Vector<A3DAnimation>();
        mFinishedAnimations = new Vector<A3DAnimation>();
        mLastTime = 0;
    }

    static public A3DAnimator getInstance() {
    	return instance;
    }
    public void clearAnimation()
    {
    	mActiveAnimations.clear();
    	mStartingAnimations.clear();
    	mFinishedAnimations.clear();
    }

    /**
     * Update all animations
     * @return false if nothing updated, true otherwise
     */
    public boolean updateAnimations() {
        synchronized (instance) {
            mActiveAnimations.removeAll(mFinishedAnimations);
            mStartingAnimations.removeAll(mFinishedAnimations);
            mFinishedAnimations.clear();
            mActiveAnimations.addAll(mStartingAnimations);
            mStartingAnimations.clear();
            if (mState != STATE_ALIVE || 
                mActiveAnimations.isEmpty()) {
                return false;
            }
            long time = System.nanoTime() / 1000000L;
            long timedelta = time - mLastTime;
            if (timedelta < FRAME_TIME_DELTA) {
                return false;
            }
            mLastTime = time;
            
            Iterator<A3DAnimation> it = mActiveAnimations.iterator();
            while (it.hasNext()) {
                A3DAnimation a = it.next();
                a.update(time);
            }
            return true;
        }
    }

    public void reqResume() {
        synchronized (instance) {
            mState = STATE_ALIVE;
        }
    }
    
    public void reqPause() {
        synchronized (instance) {
            mState = STATE_SLEEPING;
        }
    }
    
    public void reqDie() {
        synchronized (instance) {
            mActiveAnimations.clear();
            mState = STATE_DEAD;
        }
    }

    /**
     * All animations must notify the animator when they are starting,
     * otherwise the animations will not be updated.
     * <br><br>
     * Animation.start() will notify the Animator to add it to the active list, 
     * and then the renderer will update all animations by calling
     * Animator.update(). 
     */
    
    public void onAnimationEvent(final int eventType, final A3DAnimation animation) {
        /* mStartingAnimations and mFinishedAnimations are used instead of
         * modifying mActiveAnimations directly because this method can be 
         * called from the loop using Iterator<A3DAnimation>.
         */
        synchronized (instance) {
            if (eventType == A3DAnimationListener.EVENT_STARTING) {
                if (mStartingAnimations.contains(animation) == false && 
                    mActiveAnimations.contains(animation) == false) {
                    mStartingAnimations.add(animation);
                }
            }
            else if (eventType == A3DAnimationListener.EVENT_FINISHED ||
                     eventType == A3DAnimationListener.EVENT_STOPPED) {
                mFinishedAnimations.add(animation);
            }
        }
    }

    /**
     * Start all animations in a list at the same time.
     * @param animlist
     */
    public void startAnimations(Vector<A3DAnimation> animlist) {
        synchronized (instance) {
            Iterator<A3DAnimation> it = animlist.iterator();
            while (it.hasNext()) {
                A3DAnimation a = it.next();
                a.start();
            }
        }
    }
}
