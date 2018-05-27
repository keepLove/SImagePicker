package com.s.android.imagepicker

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

/**
 *
 * @author android
 * @date   2018/5/27
 */
/**
 * 获取缓存文件夹路径
 */
fun Context.getCacheFile(): File {
    var file = externalCacheDir
    if (file == null) {
        file = filesDir
    }
    if (file == null) {
        file = File(Environment.getExternalStorageDirectory(), "ImagePicker")
    }
    if (!file.exists()) {
        val mkdirs = file.mkdirs()
        if (!mkdirs) {
            loge("Failed to create file")
        }
    }
    return file
}

/**
 * uri to file
 * content://media/external/images/media/13550
 */
fun Uri.toFile(context: Context): File? {
    val column = MediaStore.Images.Media.DATA
    val projection = arrayOf(column)
    var cursor: Cursor? = null
    try {
        cursor = context.contentResolver.query(this, projection, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return File(cursor.getString(index))
        }
    } finally {
        cursor?.close()
    }
    return null
}

/**
 * uri to bitmap
 * content://media/external/images/media/13550
 */
fun Uri.toBitmap(context: Context): Bitmap {
    val outFile = File(context.getCacheFile(), "${System.currentTimeMillis()}_compress.jpeg")
    return MediaStore.Images.Media.getBitmap(context.contentResolver, this).compress(500f, 500f, outFile.absolutePath)
}

fun Bitmap.compress(maxWidth: Float, maxHeight: Float, desPath: String): Bitmap {
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
    val fos = FileOutputStream(desPath)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    return bitmap
}