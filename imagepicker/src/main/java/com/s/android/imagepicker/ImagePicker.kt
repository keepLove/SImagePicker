package com.s.android.imagepicker

import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.s.android.imagepicker.utils.loge
import java.io.File
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

    override fun getAllPicture() {
        val groupMap = HashMap<String, ArrayList<String>>()
        val groupList = arrayListOf<String>()
        val mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val mContentResolver = builder.getFragmentActivity().contentResolver

        //只查询jpeg和png的图片
        val mCursor = mContentResolver.query(mImageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                arrayOf("image/jpeg", "image/png", "image/gif", "image/x-ms-bmp", "image/webp"), MediaStore.Images.Media.DATE_MODIFIED)

        if (mCursor == null) {
            builder.getFragmentActivity().runOnUiThread {
                builder.getImagePickerCallback()?.callback(null)
            }
            return
        }
        val returnType = builder.getReturnType()
        while (mCursor.moveToNext()) {
            //获取图片的路径
            val path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA))

            //获取该图片的父路径名
            val parentName = File(path).parentFile.name

            if (returnType == "Map") {
                //根据父路径名将图片放入到groupMap中
                if (!groupMap.containsKey(parentName)) {
                    val chileList = ArrayList<String>()
                    chileList.add(path)
                    groupMap[parentName] = chileList
                } else {
                    groupMap[parentName]?.add(path)
                }
            } else if (returnType == "List") {
                groupList.add(path)
            }
        }
        mCursor.close()
        builder.getFragmentActivity().runOnUiThread {
            when (returnType) {
                "Map" -> {
                    builder.getImagePickerCallback()?.callback(groupMap)
                }
                "List" -> {
                    builder.getImagePickerCallback()?.callback(groupList)
                }
                else -> {
                    builder.getImagePickerCallback()?.callback(null)
                }
            }
        }
    }

    /**
     * Builder
     */
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
         * 返回类型{"File", "Bitmap", "Uri"、"Map"、"List"}
         */
        private var returnType = ""

        constructor(fragment: Fragment) {
            val activity = fragment.activity
            checkNotNull(activity)
            this.fragmentActivity = WeakReference(activity)
        }

        /**
         * FragmentActivity
         */
        constructor(fragmentActivity: FragmentActivity) {
            this.fragmentActivity = WeakReference(fragmentActivity)
        }

        /**
         * FragmentActivity
         */
        fun getFragmentActivity(): FragmentActivity {
            val activity = fragmentActivity?.get()
            checkNotNull(activity) { "FragmentActivity can not be null. You should instance the FragmentActivity or v4.app.Fragment by ImagePicker.Builder().from()." }
            return activity
        }

        /**
         * @param imagePickerCallback 结果返回接口
         * @param T {File、Bitmap、Uri、Map、List}
         * @see java.io.File
         * @see android.graphics.Bitmap
         * @see android.net.Uri
         */
        fun <T : Any> callback(imagePickerCallback: ImagePickerCallback<T>): Builder {
            val type = imagePickerCallback::class.java.genericInterfaces[0] as ParameterizedType
            val clazz: Type = type.actualTypeArguments[0]
            loge("clazz type:$clazz")
            this.returnType = when (clazz.toString()) {
                "class java.io.File" -> "File"
                "class android.graphics.Bitmap" -> "Bitmap"
                "class android.net.Uri" -> "Uri"
                "java.util.Map<java.lang.String, ? extends java.util.List<? extends java.lang.String>>" -> "Map"
                "java.util.List<? extends java.lang.String>" -> "List"
                else -> ""
            }
            if (returnType.isEmpty()) {
                throw RuntimeException("Generic Type is " +
                        "java.io.File or " +
                        "android.graphics.Bitmap or " +
                        "android.net.Uri or " +
                        "java.util.Map<java.lang.String, ? extends java.util.List<? extends java.lang.String>> or " +
                        "java.util.List<? extends java.lang.String>")
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