package com.s.android.imagepicker

/**
 *
 * @author android
 * @date   2018/5/25
 *
 * @param T {File、Bitmap、Uri、Map、List}
 */
interface ImagePickerCallback<in T : Any> {

    /**
     * 结果
     *
     */
    fun callback(t: T?)
}