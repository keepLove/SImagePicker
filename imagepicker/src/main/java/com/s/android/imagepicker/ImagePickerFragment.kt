package com.s.android.imagepicker

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.s.android.imagepicker.listener.ImagePickerRequest
import com.s.android.imagepicker.listener.ImagePickerReturnType
import com.s.android.imagepicker.utils.checkPhoto
import com.s.android.imagepicker.utils.crop_uri
import com.s.android.imagepicker.utils.getCropFile
import com.s.android.imagepicker.utils.getImageUri
import com.s.android.imagepicker.utils.loge
import com.s.android.imagepicker.utils.sJumpToCamera
import com.s.android.imagepicker.utils.sJumpToCrop
import com.s.android.imagepicker.utils.sJumpToPicture
import com.s.android.imagepicker.utils.toBitmap
import com.s.android.imagepicker.utils.toFile

/**
 *
 * @author android
 * @date   2018/5/25
 */
internal class ImagePickerFragment : Fragment() {

    /**
     * 拍照时的uri
     */
    private val uri: Uri by lazy(LazyThreadSafetyMode.NONE) {
        builder!!.getFragmentActivity().getImageUri()
    }

    /**
     * 参数
     */
    private var builder: ImagePickerRequest? = null

    /**
     * 裁剪时的uri
     */
    private var cropUri: Uri? = null

    /**
     * 跳转到系统图库
     */
    fun jumpToPicture() {
        sJumpToPicture(REQUEST_CODE_PICTURE)
    }

    /**
     * 跳转到系统摄像机
     */
    fun jumpToCamera() {
//        if (checkPermission()) {
            loge("start camera")
            sJumpToCamera(uri, REQUEST_CODE_CAMERA)
//        }
    }

//    /**
//     * 检查权限
//     */
//    private fun checkPermission(): Boolean {
//        return if (
//            ContextCompat.checkSelfPermission(
//                requireContext(), Manifest.permission.CAMERA
//            ) != PackageManager.PERMISSION_GRANTED ||
//            ContextCompat.checkSelfPermission(
//                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(
//                    arrayOf(
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    ), 0
//                )
//            }
//            false
//        } else {
//            true
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
//            grantResults[1] == PackageManager.PERMISSION_GRANTED
//        ) {
//            jumpToCamera()
//        }
//    }

    init {
        retainInstance = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickerFragmentManager.imagePickerFragmentCreated(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loge("resultCode:${resultCode == Activity.RESULT_OK}")
        when (requestCode) {
            REQUEST_CODE_PICTURE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    if (builder?.isCrop == true) {
                        cropPicture(data.data)
                    } else {
                        onCallback(data.data)
                    }
                } else {
                    onCallback(null)
                }
            }

            REQUEST_CODE_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    uri.toFile(requireContext())?.checkPhoto()
                    if (builder?.isCrop == true) {
                        cropPicture(uri)
                    } else {
                        onCallback(uri)
                    }
                } else {
                    onCallback(null)
                }
            }

            REQUEST_CODE_CROP -> {
                if (resultCode == Activity.RESULT_OK) {
                    onCallback(cropUri)
                } else {
                    onCallback(null)
                }
            }
        }
    }

    /**
     * 裁剪图片
     */
    private fun cropPicture(uri: Uri?) {
        if (uri == null) {
            builder?.imagePickerCallback?.callback(null)
            return
        }
        activity?.apply {
            val cropFile = getCropFile()
            cropUri = if (Build.VERSION.SDK_INT >= 30) {
                crop_uri
            } else {
                Uri.fromFile(cropFile)
            }
            sJumpToCrop(uri, cropUri!!, REQUEST_CODE_CROP)
        }
    }

    private fun onCallback(uri: Uri?) {
        loge("callback uri:$uri")
        builder?.apply {
            when (returnType) {
                ImagePickerReturnType.Uri -> {
                    imagePickerCallback?.callback(uri)
                }

                ImagePickerReturnType.File -> {
                    imagePickerCallback?.callback(uri?.toFile(this@ImagePickerFragment.requireContext()))
                }

                ImagePickerReturnType.Bitmap -> {
                    imagePickerCallback?.callback(uri?.toBitmap(this@ImagePickerFragment.requireContext()))
                }
            }
        }
    }

    companion object {
        private val imagePickerFragmentManager = ImagePickerFragmentManager()

        private const val FRAGMENT_TAG = "com.s.android.imagepicker.ImagePickerFragment"

        private const val REQUEST_CODE_PICTURE = 8005
        private const val REQUEST_CODE_CAMERA = 8006
        private const val REQUEST_CODE_CROP = 8007

        fun createFragment(builder: ImagePickerRequest): ImagePickerFragment {
            return imagePickerFragmentManager.createFragment(builder.getFragmentActivity()).also {
                it.builder = builder
            }
        }
    }

    internal class ImagePickerFragmentManager {

        /**
         * 缓存
         */
        private val imagePickerFragmentCache = hashMapOf<Activity, ImagePickerFragment>()

        /**
         * 是否正在添加fragment
         */
        private var activityCallbacksIsAdded = false

        /**
         * 创建fragment
         */
        fun createFragment(fragmentActivity: FragmentActivity): ImagePickerFragment {
            val supportFragmentManager = fragmentActivity.supportFragmentManager
            var fragment = findImagePickerFragment(supportFragmentManager)
            if (fragment != null) {
                return fragment
            }
            fragment = imagePickerFragmentCache[fragmentActivity]
            if (fragment != null) {
                return fragment
            }
            if (!activityCallbacksIsAdded) {
                activityCallbacksIsAdded = true
                fragmentActivity.application.registerActivityLifecycleCallbacks(activityCallbacks)
            }
            fragment = ImagePickerFragment()
            supportFragmentManager.beginTransaction().add(fragment, FRAGMENT_TAG)
                .commitAllowingStateLoss()
            supportFragmentManager.executePendingTransactions()
            imagePickerFragmentCache[fragmentActivity] = fragment
            return fragment
        }

        /**
         * @see ImagePickerFragment.onCreate
         */
        fun imagePickerFragmentCreated(fragment: Fragment) {
            fragment.activity?.let {
                imagePickerFragmentCache.remove(it)
            }
        }

        /**
         * 寻找未注销的fragment
         */
        private fun findImagePickerFragment(fragmentManager: FragmentManager): ImagePickerFragment? {
            if (fragmentManager.isDestroyed) {
                throw IllegalStateException("Can't access ImagePicker from onDestroy")
            }
            val fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG)
            if (fragment != null && fragment !is ImagePickerFragment) {
                throw IllegalStateException("Unexpected fragment instance was returned by FRAGMENT_TAG")
            }
            return fragment as ImagePickerFragment?
        }

        private val activityCallbacks = object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                imagePickerFragmentCache.remove(activity)
            }
        }
    }
}