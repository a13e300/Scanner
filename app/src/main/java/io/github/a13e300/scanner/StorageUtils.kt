package io.github.a13e300.scanner

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
            saveImagePreQ(context, fileName, bitmap)
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

    private fun saveImagePreQ(context: Context, fileName: String, bitmap: Bitmap) {
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, fileName)
        val fos = FileOutputStream(image)
        fos.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        // 确保保存的图片能被系统相册扫描
        context.sendBroadcast(Intent(
            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
            Uri.fromFile(image)
        ))
    }

    fun verifyStoragePermissions(activity: Activity) {
        try {
            //授权列表
            val permissionList: MutableList<String> = ArrayList()
            //检查是否获取该权限 WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (!permissionList.isEmpty()) { //权限列表不是空
                val permissions = permissionList.toTypedArray()
                //动态申请权限的请求
                ActivityCompat.requestPermissions(activity, permissions, 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}