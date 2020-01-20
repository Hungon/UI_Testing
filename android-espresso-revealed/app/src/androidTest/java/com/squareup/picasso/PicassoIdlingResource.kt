package com.squareup.picasso

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.support.test.espresso.IdlingResource
import android.support.test.runner.lifecycle.ActivityLifecycleCallback
import android.support.test.runner.lifecycle.Stage

import java.lang.ref.WeakReference

/**
 * Idling resource for Picasso image loading lib.
 * After it is registered in tests Espresso will wait until images are loaded and idling resource is idle.
 */
class PicassoIdlingResource : IdlingResource, ActivityLifecycleCallback {
    private var mCallback: IdlingResource.ResourceCallback? = null
    private var mPicassoWeakReference: WeakReference<Picasso>? = null
    private val mHandler = Handler(Looper.getMainLooper())

    private val isIdle: Boolean
        get() = (mPicassoWeakReference == null
                || mPicassoWeakReference!!.get() == null
                || mPicassoWeakReference!!.get()?.targetToAction?.isEmpty() == true)

    override fun getName(): String {
        return "PicassoIdlingResource"
    }

    override fun isIdleNow(): Boolean {
        if (isIdle) {
            notifyDone()
            return true
        } else {
            /* Force a re-check of the idle state in a little while.
       * If isIdleNow() returns false, Espresso only polls it every few seconds which can slow down our tests.
       */
            mHandler.postDelayed({ isIdleNow }, IDLE_POLL_DELAY_MILLIS.toLong())
            return false
        }
    }

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        mCallback = resourceCallback
    }

    private fun notifyDone() {
        if (mCallback != null) {
            mCallback!!.onTransitionToIdle()
        }
    }

    override fun onActivityLifecycleChanged(activity: Activity, stage: Stage) {
        when (stage) {
            Stage.RESUMED -> mPicassoWeakReference = WeakReference(Picasso.with(activity))
            Stage.PAUSED ->
                // Clean up reference
                mPicassoWeakReference = null
        }// NOP
    }

    companion object {

        private val IDLE_POLL_DELAY_MILLIS = 100
    }
}
