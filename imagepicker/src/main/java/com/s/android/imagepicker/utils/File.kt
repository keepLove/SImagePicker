@file:JvmName("FileUtil")

package com.s.android.imagepicker.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File

/**
 *
 * @author android
 * @date   2018/5/27
 */
/**
 * 获取缓存文件夹路径
 */
internal fun Context.getCacheFile(): File {
    var file = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    if (file == null) {
        file = filesDir
    }
    if (file == null) {
        file = externalCacheDir
    }
    file = File(file, "ImagePicker")
    if (!file.exists()) {
        val mkdirs = file.mkdirs()
        if (!mkdirs) {
            loge("Failed to create file")
        }
    }
    return file
}

internal var crop_uri: Uri? = null

internal fun Context.getCropFile(): File {
    val fileName = String.format("crop_%s.jpg", System.currentTimeMillis())
    val imgFile: File
    if (Build.VERSION.SDK_INT >= 30) {
        val externalDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        imgFile = File(externalDirectory, fileName)
        // 通过 MediaStore API 插入file 为了拿到系统裁剪要保存到的uri（因为App没有权限不能访问公共存储空间，需要通过 MediaStore API来操作）
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DATA, imgFile.absolutePath)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        crop_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    } else {
        imgFile = File(getCacheFile(), fileName)
    }
    return imgFile
}
