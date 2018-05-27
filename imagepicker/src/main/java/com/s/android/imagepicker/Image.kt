package com.s.android.imagepicker

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @author android
 * @date   2018/5/25
 */

/**
 * 跳转到系统图库
 */
fun Fragment.sJumpToPicture(requestCode: Int) {
    val intent = Intent()
    intent.type = "image/*"
    intent.action = Intent.ACTION_GET_CONTENT
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    startActivityForResult(intent, requestCode)
}

/**
 * 跳转到系统摄像机
 */
fun Fragment.sJumpToCamera(uri: Uri, requestCode: Int) {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
    startActivityForResult(intent, requestCode)
}

/**
 * 跳转到系统裁剪
 */
fun Fragment.sJumpToCrop(uri: Uri, outUri: Uri, requestCode: Int) {
    val intent = Intent("com.android.camera.action.CROP")
    intent.setDataAndType(uri, "image/*")
    intent.putExtra("crop", "true")
    intent.putExtra("aspectX", 1000)
    intent.putExtra("aspectY", 999)
    intent.putExtra("scale", true)
    intent.putExtra("return-date", false)
    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    intent.putExtra("output", outUri)
    startActivityForResult(intent, requestCode)
}

/**
 * 获取图片uri
 */
fun Context.getImageUri(): Uri {
    val contentResolver = contentResolver
    val cv = ContentValues()
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    cv.put(MediaStore.Images.Media.TITLE, timeStamp)
    return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
}
