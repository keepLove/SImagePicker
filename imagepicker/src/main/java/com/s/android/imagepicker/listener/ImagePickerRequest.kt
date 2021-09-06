package com.s.android.imagepicker.listener

import android.graphics.Bitmap
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.s.android.imagepicker.ImagePicker
import java.io.File
import java.lang.ref.WeakReference

interface ImagePickerRequests<T : Any> {

    fun setListener(imagePickerCallback: ImagePickerListener<T>): ImagePicker
}

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
     * 返回类型{"File", "Bitmap", "Uri"}
     */
    internal var returnType = ""

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

    override fun setListener(imagePickerCallback: ImagePickerListener<Uri>): ImagePicker {
        this.returnType = "Uri"
        this.imagePickerCallback = imagePickerCallback as ImagePickerListener<Any>
        return ImagePicker(this)
    }

    fun asBitmap(): ImagePickerRequests<Bitmap> {
        this.returnType = "Bitmap"
        return ImagePickerBitmapRequest(this)
    }

    fun asFile(): ImagePickerRequests<File> {
        this.returnType = "File"
        return ImagePickerFileRequest(this)
    }

}

private class ImagePickerBitmapRequest(private val request: ImagePickerRequest) :
    ImagePickerRequests<Bitmap> {

    override fun setListener(imagePickerCallback: ImagePickerListener<Bitmap>): ImagePicker {
        request.returnType = "Bitmap"
        request.imagePickerCallback = imagePickerCallback as ImagePickerListener<Any>
        return ImagePicker(request)
    }
}

private class ImagePickerFileRequest(private val request: ImagePickerRequest) :
    ImagePickerRequests<File> {

    override fun setListener(imagePickerCallback: ImagePickerListener<File>): ImagePicker {
        request.returnType = "File"
        request.imagePickerCallback = imagePickerCallback as ImagePickerListener<Any>
        return ImagePicker(request)
    }
}
