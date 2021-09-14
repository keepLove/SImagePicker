package com.s.android.imagepicker

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.s.android.imagepicker.listener.ImagePickerRequest
import java.lang.ref.WeakReference

/**
 *
 * @author android
 * @date   2018/5/25
 */
class ImagePicker internal constructor(private val request: ImagePickerRequest) {

    companion object {

        /**
         * 获取FragmentActivity
         */
        fun with(fragment: Fragment): ImagePickerRequest {
            val activity = fragment.activity
            checkNotNull(activity)
            val request = ImagePickerRequest()
            request.fragmentActivity = WeakReference(activity)
            return request
        }

        /**
         * 在FragmentActivity中新建一个fragment使用
         */
        fun with(fragmentActivity: FragmentActivity): ImagePickerRequest {
            val request = ImagePickerRequest()
            request.fragmentActivity = WeakReference(fragmentActivity)
            return request
        }

    }

    private val imagePickerFragment: ImagePickerFragment by lazy(LazyThreadSafetyMode.NONE) {
        ImagePickerFragment.createFragment(request)
    }

    /**
     * 跳转到系统图库
     */
    fun jumpToPicture() {
        imagePickerFragment.jumpToPicture()
    }

    /**
     * 跳转到系统摄像机
     */
    fun jumpToCamera() {
        imagePickerFragment.jumpToCamera()
    }

}