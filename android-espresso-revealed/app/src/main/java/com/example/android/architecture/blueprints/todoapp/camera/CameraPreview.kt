/*
* Copyright 2015 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.architecture.blueprints.todoapp.camera

import android.content.Context
import android.hardware.Camera
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView

import java.io.IOException

/**
 * Camera preview that displays a [Camera].
 *
 *
 * Handles basic lifecycle methods to display and stop the preview.
 *
 *
 * Implementation is based directly on the documentation at
 * http://developer.android.com/guide/topics/media/camera.html
 */
class CameraPreview @JvmOverloads constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int,
                                              private val mCamera: Camera? = null, private val mCameraInfo: Camera.CameraInfo? = null, private val mDisplayOrientation: Int = 0) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    private val mHolder: SurfaceHolder

    init {

        // Do not initialise if no camera has been set
        if (mCamera == null || mCameraInfo == null) {
        }

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = holder
        mHolder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera?.setPreviewDisplay(holder)
            mCamera?.startPreview()
            Log.d(TAG, "Camera preview started.")
        } catch (e: IOException) {
            Log.d(TAG, "Error setting camera preview: " + e.message)
        }

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.surface == null) {
            // preview surface does not exist
            Log.d(TAG, "Preview surface does not exist")
            return
        }

        // stop preview before making changes
        try {
            mCamera?.stopPreview()
            Log.d(TAG, "Preview stopped.")
        } catch (e: Exception) {
            // ignore: tried to stop a non-existent preview
            Log.d(TAG, "Error starting camera preview: " + e.message)
        }

        val orientation = calculatePreviewOrientation(mCameraInfo, mDisplayOrientation)
        mCamera?.setDisplayOrientation(orientation)

        try {
            mCamera?.setPreviewDisplay(mHolder)
            mCamera?.startPreview()
            Log.d(TAG, "Camera preview started.")
        } catch (e: Exception) {
            Log.d(TAG, "Error starting camera preview: " + e.message)
        }

    }

    companion object {

        private val TAG = "CameraPreview"

        /**
         * Calculate the correct orientation for a [Camera] preview that is displayed on screen.
         *
         *
         * Implementation is based on the sample code provided in
         * [Camera.setDisplayOrientation].
         */
        fun calculatePreviewOrientation(info: Camera.CameraInfo?, rotation: Int): Int {
            var degrees = 0

            when (rotation) {
                Surface.ROTATION_0 -> degrees = 0
                Surface.ROTATION_90 -> degrees = 90
                Surface.ROTATION_180 -> degrees = 180
                Surface.ROTATION_270 -> degrees = 270
            }

            return if (info != null) {
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    val result = (info.orientation + degrees) % 360
                    ((360 - result) % 360) ?: 0  // compensate the mirror
                } else {  // back-facing
                    ((info.orientation - degrees + 360) % 360) ?: 0
                }
            } else {
                0
            }
        }
    }
}
