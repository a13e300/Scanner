package io.github.a13e300.scanner

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

// https://developer.android.com/training/data-storage/shared/media
// https://stackoverflow.com/a/66817176
object StorageUtils {
    fun saveImages(context: Context, bitmap: Bitmap) {
        val fileName = "qrcode_${System.currentTimeMillis()}.png"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageQ(context, fileName, bitmap)
        } else {
            saveImagePreQ(fileName, bitmap)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageQ(context: Context, filename: String, bitmap: Bitmap) {
        var fos: OutputStream?
        val imageUri: Uri
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        val contentResolver = context.contentResolver

        contentResolver.also { resolver ->
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
            fos = imageUri.let { resolver.openOutputStream(it) }
            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }

            contentValues.clear()
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
            resolver.update(imageUri, contentValues, null, null)
        }
    }

    private fun saveImagePreQ(fileName: String, bitmap: Bitmap) {
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, fileName)
        val fos = FileOutputStream(image)
        fos.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}