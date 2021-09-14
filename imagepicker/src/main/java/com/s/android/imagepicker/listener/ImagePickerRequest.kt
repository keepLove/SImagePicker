package com.s.android.imagepicker.listener

import android.graphics.Bitmap
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.s.android.imagepicker.ImagePicker
import java.io.File
import java.lang.ref.WeakReference

class ImagePickerRequest internal constructor() : ImagePickerRequests<Uri> {

    /**
     * 关联的FragmentActivity
     */
    internal var fragmentActivity: WeakReference<FragmentActivity>? = null

    /**
     * 结果返回接口
     */
    internal var imagePickerCallback: ImagePickerListener<Any>? = null

    /**
     * 是否需要裁剪
     */
    internal var isCrop = false

    /**
     * 返回类型
     */
    internal var returnType = ImagePickerReturnType.Uri

    internal fun getFragmentActivity(): FragmentActivity {
        val activity = fragmentActivity?.get()
        checkNotNull(activity) { "FragmentActivity can not be null. You should instance the FragmentActivity or v4.app.Fragment by ImagePicker.Builder().from()." }
        return activity
    }

    /**
     * @param isCrop 是否需要裁剪
     */
    fun setCrop(isCrop: Boolean): ImagePickerRequest {
        this.isCrop = isCrop
        return this
    }

    /**
     * 设置监听回调
     */
    override fun setListener(imagePickerListener: ImagePickerListener<Uri>): ImagePicker {
        this.returnType = ImagePickerReturnType.Uri
        this.imagePickerCallback = imagePickerListener as ImagePickerListener<Any>
        return ImagePicker(this)
    }

    /**
     * 转为[Bitmap]
     */
    fun asBitmap(): ImagePickerRequests<Bitmap> {
        this.returnType = ImagePickerReturnType.Bitmap
        return ImagePickerBitmapRequest(this)
    }

    /**
     * 转为[File]
     */
    fun asFile(): ImagePickerRequests<File> {
        this.returnType = ImagePickerReturnType.File
        return ImagePickerFileRequest(this)
    }

}

private class ImagePickerBitmapRequest(private val request: ImagePickerRequest) :
    ImagePickerRequests<Bitmap> {

    override fun setListener(imagePickerListener: ImagePickerListener<Bitmap>): ImagePicker {
        request.returnType = ImagePickerReturnType.Bitmap
        request.imagePickerCallback = imagePickerListener as ImagePickerListener<Any>
        return ImagePicker(request)
    }
}

private class ImagePickerFileRequest(private val request: ImagePickerRequest) :
    ImagePickerRequests<File> {

    override fun setListener(imagePickerListener: ImagePickerListener<File>): ImagePicker {
        request.returnType = ImagePickerReturnType.File
        request.imagePickerCallback = imagePickerListener as ImagePickerListener<Any>
        return ImagePicker(request)
    }
}
