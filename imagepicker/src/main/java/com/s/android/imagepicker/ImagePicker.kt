package com.s.android.imagepicker

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import java.lang.ref.WeakReference

/**
 *
 * @author android
 * @date   2018/5/25
 */
class ImagePicker private constructor(private val builder: Builder) : ImagePickerFunction {

    private val imagePickerFragment: ImagePickerFragment by lazy(LazyThreadSafetyMode.NONE) { ImagePickerFragment.createFragment(builder) }

    /**
     * 跳转到系统图库
     */
    override fun jumpToPicture() {
        imagePickerFragment.jumpToPicture()
    }

    /**
     * 跳转到系统摄像机
     */
    override fun jumpToCamera() {
        imagePickerFragment.jumpToCamera()
    }

    class Builder {

        /**
         * 关联的FragmentActivity
         */
        private var fragmentActivity: WeakReference<FragmentActivity>? = null
        /**
         * 结果返回接口
         */
        private var imagePickerCallback: ImagePickerCallback? = null
        /**
         * 是否需要裁剪
         */
        private var isCrop = false

        fun from(fragment: Fragment): Builder {
            val activity = fragment.activity
            checkNotNull(activity)
            return from(activity!!)
        }

        fun from(fragmentActivity: FragmentActivity): Builder {
            this.fragmentActivity = WeakReference(fragmentActivity)
            return this
        }

        fun getFragmentActivity(): FragmentActivity {
            val activity = fragmentActivity?.get()
            checkNotNull(activity) { "FragmentActivity can not be null. You should instance the FragmentActivity or v4.app.Fragment by ImagePicker.Builder().from()." }
            return activity!!
        }

        /**
         * @param imagePickerCallback 结果返回接口
         */
        fun setImagePickerCallback(imagePickerCallback: ImagePickerCallback): Builder {
            this.imagePickerCallback = imagePickerCallback
            return this
        }

        fun getImagePickerCallback(): ImagePickerCallback? {
            return this.imagePickerCallback
        }

        /**
         * @param isCrop 是否需要裁剪
         */
        fun setCrop(isCrop: Boolean): Builder {
            this.isCrop = isCrop
            return this
        }

        fun isCrop(): Boolean {
            return this.isCrop
        }

        fun build(): ImagePickerFunction {
            checkNotNull(fragmentActivity?.get()) { "FragmentActivity can not be null. You should instance the FragmentActivity or v4.app.Fragment by ImagePicker.Builder().from()." }
            checkNotNull(imagePickerCallback) { "ImagePickerCallback can not be null. You should instance the ImagePickerCallback by ImagePicker.Builder().setImagePickerCallback()." }
            return ImagePicker(this)
        }
    }
}