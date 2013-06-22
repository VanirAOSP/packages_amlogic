package com.amlogic.a3d.anim;

import java.util.Iterator;
import java.util.Vector;

import android.util.Log;

/**
 * Allows storing Animations and starting them by name. <br>
 * <br>
 * Example:
 * <pre>{@code
 * loadFromScript() {
 *   A3DTransformAnimation t = new A3DTransformAnimation(...);
 *   to.setRotation(...);
 *   A3DAnimationManager.getInstance().addAnimation("magic anim", t, false);
 * }
 * 
 * onClick() { 
 *   A3DAnimationManager.getInstance().startAnimation("magic anim");
 * }
 * 
 * onQuit() {
 *   A3DAnimationManager.getInstance().removeAnimation("magic anim");
 * }
 * }</pre>
 */
public class A3DAnimationManager implements A3DAnimationListener {
    private static final String TAG = "A3DAnimationManager";
    private static A3DAnimationManager instance = new A3DAnimationManager();
    private class AnimationM {
        public A3DAnimation mAnimation;
        public String mName;
        public boolean mAutoRemove;
        public AnimationM(String name, A3DAnimation anim, boolean autoremove) {
            mName = name;
            mAnimation = anim;
            mAutoRemove = autoremove;
        }
    }
    private Vector<AnimationM> mAnimationMs;

    private A3DAnimationManager() {
        mAnimationMs = new Vector<AnimationM>();
    }

    static public A3DAnimationManager getInstance() {
        return instance;
    }
    
    /**
     * Add animation.
     * @param animation
     * @param autoremove  Remove animation after it finishes playing all loops.
     * @return true if successful
     */
    public boolean addAnimation(String name, A3DAnimation animation, boolean autoremove) {
        if (getAnimation(name) == null) {
            mAnimationMs.add(new AnimationM(name, animation, autoremove));
            if (autoremove)
                animation.addListener(this);
            return true;
        }
        return false;
    }

    /**
     * Get animation previously added with addAnimation().
     * @param name
     * @return null if not found
     */
    public A3DAnimation getAnimation(String name) {
        Iterator<AnimationM> it = mAnimationMs.iterator();
        while (it.hasNext()) {
            AnimationM a = it.next();
            if (a.mName.equals(name))
                return a.mAnimation;
        }
        //Log.i(TAG, "animation " + name + " not found");
        return null;
    }
    
    /**
     * Get name of an animation.
     * @param animation
     * @return name, null if not found
     */
    public String getName(A3DAnimation animation) {
        Iterator<AnimationM> it = mAnimationMs.iterator();
        while (it.hasNext()) {
            AnimationM a = it.next();
            if (a.mAnimation == animation)
                return a.mName;
        }
        //Log.i(TAG, "animation not found");
        return null;
    }
    
    /**
     * Remove an animation.
     * @param name
     */
    public void removeAnimation(String name) {
        Iterator<AnimationM> it = mAnimationMs.iterator();
        while (it.hasNext()) {
            AnimationM a = it.next();
            if (a.mName.equals(name)) {
                mAnimationMs.remove(a);
                return;
            }
        }
    }

    /**
     * Start an animation by name.
     * @param name
     * @return
     */
    public boolean startAnimation(String name) {
        A3DAnimation a = getAnimation(name);
        if (a != null) {
            a.start();
            return true;
        }
        Log.i(TAG, "animation " + name + " not found");
        return false;
    }
    
    protected void finishedNotification(A3DAnimation animation) {
        //TODO notification interface
        Iterator<AnimationM> it = mAnimationMs.iterator();
        while (it.hasNext()) {
            AnimationM a = it.next();
            if (a.mAnimation == animation) {
                if (a.mAutoRemove == true)
                    mAnimationMs.remove(a);
                return;
            }
        }
    }

    
    public void onAnimationEvent(int eventType, A3DAnimation animation) {
        if (eventType == A3DAnimationListener.EVENT_FINISHED) {
            Iterator<AnimationM> it = mAnimationMs.iterator();
            while (it.hasNext()) {
                AnimationM a = it.next();
                if (a.mAnimation == animation) {
                    mAnimationMs.remove(a);
                    return;
                }
            }
        }
    }
}



