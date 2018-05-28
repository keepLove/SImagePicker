package com.s.android.imagepicker

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.s.android.imagepicker.utils.loge
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

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
        private var imagePickerCallback: ImagePickerCallback<Any>? = null
        /**
         * 是否需要裁剪
         */
        private var isCrop = false
        /**
         * 返回类型{"File", "Bitmap", "Uri"}
         */
        private var returnType = ""

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
         * @param T {File、Bitmap、Uri}
         * @see java.io.File
         * @see android.graphics.Bitmap
         * @see android.net.Uri
         */
        fun <T : Any> setImagePickerCallback(imagePickerCallback: ImagePickerCallback<T>): Builder {
            val type = imagePickerCallback::class.java.genericInterfaces[0] as ParameterizedType
            val clazz: Type = type.actualTypeArguments[0]
            loge("clazz type:$clazz")
            this.returnType = when (clazz.toString()) {
                "class java.io.File" -> "File"
                "class android.graphics.Bitmap" -> "Bitmap"
                "class android.net.Uri" -> "Uri"
                else -> ""
            }
            if (returnType.isEmpty()) {
                throw RuntimeException("Generic Type is java.io.File or android.graphics.Bitmap or android.net.Uri")
            }
            this.imagePickerCallback = imagePickerCallback as ImagePickerCallback<Any>
            return this
        }

        fun getImagePickerCallback(): ImagePickerCallback<Any>? {
            return this.imagePickerCallback
        }

        fun getReturnType(): String {
            return this.returnType
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