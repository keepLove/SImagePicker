@file:JvmName("LogUtil")

package com.s.android.imagepicker.utils

import android.util.Log

/**
 *
 * @author android
 * @date   2018/5/27
 */
var debug = true
internal const val TAG = "ImagePicker"

internal fun loge(message: String) {
    if (debug) {
        Log.e(TAG, message)
    }
}
