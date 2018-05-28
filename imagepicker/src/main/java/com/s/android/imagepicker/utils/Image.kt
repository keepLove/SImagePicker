package com.s.android.imagepicker.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.app.Fragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 *
 * @author android
 * @date   2018/5/25
 */

/**
 * 跳转到系统图库
 */
fun Fragment.sJumpToPicture(requestCode: Int) {
    val intent = Intent()
    intent.type = "image/*"
    intent.action = Intent.ACTION_GET_CONTENT
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    startActivityForResult(intent, requestCode)
}

/**
 * 跳转到系统摄像机
 */
fun Fragment.sJumpToCamera(uri: Uri, requestCode: Int) {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
    startActivityForResult(intent, requestCode)
}

/**
 * 跳转到系统裁剪
 */
fun Fragment.sJumpToCrop(uri: Uri, outUri: Uri, requestCode: Int) {
    val intent = Intent("com.android.camera.action.CROP")
    intent.setDataAndType(uri, "image/*")
    intent.putExtra("crop", "true")
    intent.putExtra("aspectX", 1000)
    intent.putExtra("aspectY", 999)
    intent.putExtra("scale", true)
    intent.putExtra("return-date", false)
    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    intent.putExtra("output", outUri)
    startActivityForResult(intent, requestCode)
}

/**
 * 压缩
 * @param maxWidth 最大宽度
 * @param maxHeight 最大高度
 */
fun Bitmap.compressBitmap(context: Context, maxWidth: Float = 500f, maxHeight: Float = 500f): Bitmap {
    // 缩放图片的尺寸
    val w = this.width
    val h = this.height
    // 最长宽度或高度maxWH
    var be = 1.0f
    if (w > h && w > maxWidth) {
        be = w / maxWidth
    } else if (w < h && h > maxHeight) {
        be = h / maxHeight
    }
    if (be <= 0) {
        be = 1.0f
    }
    val desWidth = (w / be).toInt()
    val desHeight = (h / be).toInt()
    val bitmap = Bitmap.createScaledBitmap(this, desWidth, desHeight, true)
    val desPath = File(context.getCacheFile(), "${System.currentTimeMillis()}_compress.jpeg")
    val fos = FileOutputStream(desPath)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    return bitmap
}

/**
 * 有些手机拍照图片有问题
 */
fun File.checkPhoto() {
    var degree: Int
    try {
        val exifInterface = ExifInterface(this.path)
        degree = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        if (degree != 0) {
            when (degree) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
                else -> {
                }
            }
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(this.path, options)
            if (options.outWidth > 1080 || options.outHeight > 1920) {
                options.inSampleSize = 2
            }
            options.inJustDecodeBounds = false
            val bitmap = BitmapFactory.decodeFile(this.path, options)
            val matrix = Matrix()
            matrix.reset()
            matrix.postRotate(degree.toFloat())
            var bitmap1: Bitmap? = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
            if (bitmap1 == null) {
                bitmap1 = bitmap
            }
            if (bitmap1 != bitmap) {
                bitmap.recycle()
            }
            val outputStream = FileOutputStream(this)
            if (bitmap1!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                bitmap1.recycle()
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}