package com.example.android.architecture.blueprints.todoapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri

import com.example.android.architecture.blueprints.todoapp.R

import java.io.FileNotFoundException
import java.io.InputStream
import java.util.NoSuchElementException

object ImageUtils {

    @Throws(NoSuchElementException::class)
    fun scaleAndSetImage(uri: Uri, context: Context, dimension: Int): BitmapDrawable {
        val bitmap: Bitmap
        var drawable: Drawable

        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            drawable = Drawable.createFromStream(inputStream, uri.toString())
        } catch (e: FileNotFoundException) {
            drawable = context.resources.getDrawable(R.drawable.logo)
        }

        try {
            bitmap = (drawable as BitmapDrawable).bitmap
        } catch (e: NullPointerException) {
            throw NoSuchElementException("No drawable on given view")
        }

        // Get current dimensions AND the desired bounding box
        var width = 0

        try {
            width = bitmap.width
        } catch (e: NullPointerException) {
            throw NoSuchElementException("Can't find bitmap on given view/drawable")
        }

        var height = bitmap.height
        val bounding = dpToPx(dimension, context)

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        val xScale = bounding.toFloat() / width
        val yScale = bounding.toFloat() / height
        val scale = if (xScale <= yScale) xScale else yScale

        // Create a matrix for the scaling and add the scaling data
        val matrix = Matrix()
        matrix.postScale(scale, scale)

        // Create a new bitmap and convert it to a format understood by the ImageView
        val scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        width = scaledBitmap.width // re-use
        height = scaledBitmap.height // re-use

        return BitmapDrawable(scaledBitmap)
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }
}
