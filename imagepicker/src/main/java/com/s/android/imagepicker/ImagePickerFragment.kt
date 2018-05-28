package com.s.android.imagepicker

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import com.s.android.imagepicker.utils.*
import java.io.File

/**
 *
 * @author android
 * @date   2018/5/25
 */
class ImagePickerFragment : Fragment(), ImagePickerFunction {

    private val uri: Uri by lazy(LazyThreadSafetyMode.NONE) { builder!!.getFragmentActivity().getImageUri() }
    private var builder: ImagePicker.Builder? = null

    /**
     * 跳转到系统图库
     */
    override fun jumpToPicture() {
        sJumpToPicture(REQUEST_CODE_PICTURE)
    }

    /**
     * 跳转到系统摄像机
     */
    override fun jumpToCamera() {
        if (checkPermission()) {
            loge("start camera")
            sJumpToCamera(uri, REQUEST_CODE_CAMERA)
        }
    }

    /**
     * 检查权限
     */
    private fun checkPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
            }
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            jumpToCamera()
        }
    }

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
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_PICTURE -> {
                    if (data != null) {
                        if (builder?.isCrop() == true) {
                            cropPicture(data.data)
                        } else {
                            onCallback(data.data)
                        }
                    }
                }
                REQUEST_CODE_CAMERA -> {
//                    uri.toFile(context!!)?.checkPhoto()
                    if (builder?.isCrop() == true) {
                        cropPicture(uri)
                    } else {
                        onCallback(uri)
                    }
                }
                REQUEST_CODE_CROP -> {
                    onCallback(data?.data)
                }
                else -> {
                }
            }
        }
    }

    /**
     * 裁剪图片
     */
    private fun cropPicture(uri: Uri?) {
        if (uri == null) {
            builder?.getImagePickerCallback()?.callback(null)
            return
        }
        activity?.apply {
            val fileName = String.format("crop_%s.jpg", System.currentTimeMillis())
            val cropFile = File(this.getCacheFile(), fileName)
            sJumpToCrop(uri, Uri.fromFile(cropFile), REQUEST_CODE_CROP)
        }
    }

    private fun onCallback(uri: Uri?) {
        loge("callback uri:$uri")
        builder?.apply {
            when (getReturnType()) {
                "Uri" -> {
                    getImagePickerCallback()?.callback(uri)
                }
                "File" -> {
                    getImagePickerCallback()?.callback(uri?.toFile(this@ImagePickerFragment.context!!))
                }
                "Bitmap" -> {
                    getImagePickerCallback()?.callback(uri?.toBitmap(this@ImagePickerFragment.context!!))
                }
                else -> {
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

        fun createFragment(builder: ImagePicker.Builder): ImagePickerFragment {
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
            supportFragmentManager.beginTransaction().add(fragment, FRAGMENT_TAG).commitAllowingStateLoss()
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
            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                imagePickerFragmentCache.remove(activity)
            }
        }
    }
}