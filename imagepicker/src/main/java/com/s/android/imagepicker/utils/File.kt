@file:JvmName("FileUtil")

package com.s.android.imagepicker.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.File

/**
 *
 * @author android
 * @date   2018/5/27
 */
/**
 * 获取缓存文件夹路径
 */
fun Context.getCacheFile(): File {
    var file = if (Build.VERSION.SDK_INT >= 30) {
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    } else {
        getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    }
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
