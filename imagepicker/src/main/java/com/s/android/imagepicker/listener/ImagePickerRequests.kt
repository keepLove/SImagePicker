package com.s.android.imagepicker.listener

import com.s.android.imagepicker.ImagePicker

interface ImagePickerRequests<T> {

    /**
     * 设置监听回调
     *
     * [ImagePickerReturnType.Uri]
     * [ImagePickerReturnType.Bitmap]
     * [ImagePickerReturnType.File]
     */
    fun setListener(imagePickerListener: ImagePickerListener<T>): ImagePicker
}
