package com.s.android.imagepicker.utils

import android.content.Context
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
