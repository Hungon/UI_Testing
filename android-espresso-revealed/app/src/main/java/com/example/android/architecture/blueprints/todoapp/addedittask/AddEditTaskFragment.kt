/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.addedittask

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.camera.CameraPreviewActivity
import com.example.android.architecture.blueprints.todoapp.util.ImageUtils
import com.google.common.base.Preconditions.checkNotNull
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
class AddEditTaskFragment : Fragment(), AddEditTaskContract.View {

    private var mPresenter: AddEditTaskContract.Presenter? = null

    private var mTitle: TextView? = null
    private var contentView: View? = null
    private var imageView: ImageView? = null
    private var cameraImageView: ImageView? = null

    private var mDescription: TextView? = null

    override val isActive: Boolean
        get() = isAdded

    override fun onResume() {
        super.onResume()
        mPresenter!!.start()
    }

    override fun setPresenter(presenter: AddEditTaskContract.Presenter) {
        mPresenter = checkNotNull(presenter)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val fab = activity!!.findViewById<View>(R.id.fab_edit_task_done) as FloatingActionButton
        fab.setImageResource(R.drawable.ic_done)
        fab.setOnClickListener {
            val imageViewBytes: ByteArray? = if (imageView != null) {
                bitmapToByte((imageView!!.drawable as BitmapDrawable).bitmap)
            } else {
                null
            }
            mPresenter!!.saveTask(mTitle!!.text.toString(), mDescription!!.text.toString(), imageViewBytes)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.addtask_frag, container, false)
        contentView = activity!!.window.decorView.findViewById(android.R.id.content)
        mTitle = root.findViewById<View>(R.id.add_task_title) as TextView
        mTitle!!.requestFocus()
        //mTitle.getAccessibilityNodeProvider().createAccessibilityNodeInfo()
        mDescription = root.findViewById<View>(R.id.add_task_description) as TextView
        imageView = root.findViewById<View>(R.id.imageView) as ImageView
        val imageButton = root.findViewById<View>(R.id.getImage) as ImageButton
        imageButton.setOnClickListener { onImageButtonClick() }

        cameraImageView = root.findViewById<View>(R.id.makePhoto) as ImageView
        cameraImageView!!.setOnClickListener { showCameraPreview() }

        setHasOptionsMenu(true)
        return root
    }

    fun onImageButtonClick() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, SELECT_PICTURE)
    }

    private fun showCameraPreview() {
        // BEGIN_INCLUDE(startCamera)
        // Check if the Camera permission has been granted
        if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
            Snackbar.make(contentView!!,
                    "Available premission",
                    Snackbar.LENGTH_SHORT).show()
            startCamera()
        } else {
            // Permission is missing and must be requested.
            requestCameraPermission()
        }
        // END_INCLUDE(startCamera)
    }

    /**
     * Requests the [android.Manifest.permission.CAMERA] permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    private fun requestCameraPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                        Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(contentView!!, "Access required",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK") {
                // Request the permission
                requestPermissions(arrayOf(Manifest.permission.CAMERA),
                        PERMISSION_REQUEST_CAMERA)
            }.show()

        } else {
            Snackbar.make(contentView!!, "Camera unavailable", Snackbar.LENGTH_SHORT).show()
            // Request the permission. The result will be received in onRequestPermissionResult().
            requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }
    }

    private fun startCamera() {
        val intent = Intent(activity, CameraPreviewActivity::class.java)
        startActivityForResult(intent, TAKE_PICTURE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    startCamera()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE || requestCode == TAKE_PICTURE) {
                data ?: return
                val selectedImageUri = data.data
                val bitmapDrawable = ImageUtils.scaleAndSetImage(selectedImageUri!!, context!!, 200)

                var ei: ExifInterface? = null
                try {
                    ei = ExifInterface(selectedImageUri.path!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                var orientation = 0
                if (ei != null) {
                    orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED)
                }

                var rotatedBitmap: BitmapDrawable? = null
                when (orientation) {

                    ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(bitmapDrawable, 90f)

                    ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap = rotateImage(bitmapDrawable, 180f)

                    ExifInterface.ORIENTATION_ROTATE_270, ExifInterface.ORIENTATION_UNDEFINED -> rotatedBitmap = rotateImage(bitmapDrawable, 270f)

                    ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmapDrawable
                    else -> rotatedBitmap = bitmapDrawable
                }

                // Apply the scaled bitmap
                imageView!!.setImageDrawable(rotatedBitmap)

                // Now change ImageView's dimensions to match the scaled image
                val params = imageView!!.layoutParams as ConstraintLayout.LayoutParams
                params.width = imageView!!.width
                params.height = imageView!!.height
                imageView!!.layoutParams = params
            }
        }
    }

    override fun showEmptyTaskError() {
        val snackbar = Snackbar.make(contentView!!, getString(R.string.empty_task_message), Snackbar.LENGTH_SHORT)
        snackbar.show()
        val title = activity!!.findViewById<EditText>(R.id.add_task_title)
        title.error = resources.getString(R.string.add_task_empty_title)
        title.setHintTextColor(Color.RED)
    }

    override fun showTasksList() {
        activity!!.setResult(RESULT_OK)
        activity!!.finish()
    }

    override fun setTitle(title: String?) {
        mTitle!!.text = title
    }

    override fun setDescription(description: String?) {
        mDescription!!.text = description
    }

    override fun setImage(bitmap: Bitmap?) {
        imageView!!.setImageBitmap(bitmap)
    }

    // Bitmap to byte[]
    private fun bitmapToByte(bitmap: Bitmap): ByteArray? {
        try {
            val stream = ByteArrayOutputStream()
            //bitmap to byte[] stream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val x = stream.toByteArray()
            //close stream to save memory
            stream.close()
            return x
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    companion object {

        val ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID"
        private val PERMISSION_REQUEST_CAMERA = 0

        fun newInstance(): AddEditTaskFragment {
            return AddEditTaskFragment()
        }

        private val SELECT_PICTURE = 1
        private val TAKE_PICTURE = 2

        fun rotateImage(source: BitmapDrawable, angle: Float): BitmapDrawable {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return BitmapDrawable(Bitmap.createBitmap(source.bitmap, 0, 0, source.bitmap.width, source.bitmap.height,
                    matrix, true))
        }
    }
}// Required empty public constructor
