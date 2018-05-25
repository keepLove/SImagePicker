package com.s.android.imagepicker

import android.net.Uri

/**
 *
 * @author android
 * @date   2018/5/25
 */
interface ImagePickerCallback {

    /**
     * 结果
     *
     * @param T {File、Bitmap、Uri}
     */
    fun callback(uri: Uri?)
}