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

import android.app.Activity
import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout

import com.example.android.architecture.blueprints.todoapp.R

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * Displays a [CameraPreview] of the first [Camera].
 * An error message is displayed if the Camera is not available.
 *
 *
 * This Activity is only used to illustrate that access to the Camera API has been granted (or
 * denied) as part of the runtime permissions model. It is not relevant for the use of the
 * permissions API.
 *
 *
 * Implementation is based directly on the documentation at
 * http://developer.android.com/guide/topics/media/camera.html
 */
class CameraPreviewActivity : Activity() {

    private var mCamera: Camera? = null

    private val mPicture = Camera.PictureCallback { data, camera ->
        val storageDir = File(application.filesDir.path + "/Pictures/")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        val pictureFile = File(storageDir, "image_" + System.currentTimeMillis() + ".png")
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions")
            return@PictureCallback
        }

        try {
            val fos = FileOutputStream(pictureFile)
            fos.write(data)
            fos.close()

            releaseCamera()

            val intentData = Intent()
            intentData.data = Uri.fromFile(pictureFile)
            setResult(Activity.RESULT_OK, intentData)
            finish()

        } catch (e: FileNotFoundException) {
            Log.d(TAG, "File not found: " + e.message)
        } catch (e: IOException) {
            Log.d(TAG, "Error accessing file: " + e.message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Open an instance of the first camera and retrieve its info.
        mCamera = getCameraInstance(CAMERA_ID)
        val cameraInfo = Camera.CameraInfo()
        Camera.getCameraInfo(CAMERA_ID, cameraInfo)

        if (mCamera == null) {
            // Camera is not available, display error message
            setContentView(R.layout.activity_camera_unavailable)
        } else {

            setContentView(R.layout.activity_camera)

            // Get the rotation of the screen to adjust the preview image accordingly.
            val displayRotation = windowManager.defaultDisplay.rotation

            // Create the Preview view and set it as the content of this Activity.
            val cameraPreview = CameraPreview(this, null,
                    0, mCamera, cameraInfo, displayRotation)
            val preview = findViewById<RelativeLayout>(R.id.camera_preview)
            preview.addView(cameraPreview)
        }

        val takePictureButton = findViewById<Button>(R.id.picture)
        takePictureButton.setOnClickListener { mCamera!!.takePicture(null, null, mPicture) }
    }

    public override fun onPause() {
        super.onPause()
        // Stop camera access
        releaseCamera()
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    private fun getCameraInstance(cameraId: Int): Camera? {
        var c: Camera? = null
        try {
            c = Camera.open(cameraId) // attempt to get a Camera instance
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "Camera " + cameraId + " is not available: " + e.message)
        }

        return c // returns null if camera is unavailable
    }

    /**
     * Release the camera for other applications.
     */
    private fun releaseCamera() {
        if (mCamera != null) {
            mCamera!!.release()
            mCamera = null
        }
    }

    companion object {
        private val TAG = "CameraPreviewActivity"
        /**
         * Id of the camera to access. 0 is the first camera.
         */
        private val CAMERA_ID = 0
    }
}
