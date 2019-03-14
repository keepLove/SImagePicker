@file:JvmName("UriUtil")

package com.s.android.imagepicker.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @author android
 * @date   2018/5/28
 */

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

/**
 * uri to file
 */
fun Uri.toFile(context: Context): File? {
    val path = getPath(context)
    if (path != null) {
        return File(path)
    }
    return null
}

/**
 * 通过uri获取文件路径
 */
fun Uri.getPath(context: Context): String? {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT &&
            DocumentsContract.isDocumentUri(context, this)) {
        if ("com.android.externalstorage.documents" == this.authority) {
            // Whether the Uri authority is ExternalStorageProvider.
            val docId = DocumentsContract.getDocumentId(this)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
        } else if ("com.android.providers.downloads.documents" == this.authority) {
            // Whether the Uri authority is DownloadsProvider.
            val id = DocumentsContract.getDocumentId(this)
            val contentUri = ContentUris
                    .withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.parseLong(id))
            return contentUri.getDataColumn(context, null, null)
        } else if ("com.android.providers.media.documents" == this.authority) {
            // Whether the Uri authority is MediaProvider.
            val docId = DocumentsContract.getDocumentId(this)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            when (type) {
                "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = MediaStore.Images.Media._ID + "=?"
            val selectionArgs = arrayOf(split[1])
            return contentUri?.getDataColumn(context, selection, selectionArgs)
        }
    } else if ("content".equals(this.scheme, ignoreCase = true)) {
        // MediaStore (and general)
        // Return the remote address
        return if ("com.google.android.apps.photos.content" == this.authority) {
            // Whether the Uri authority is Google Photos.
            this.lastPathSegment
        } else this.getDataColumn(context, null, null)
    } else if ("file".equals(this.scheme, ignoreCase = true)) {
        // File
        return this.path
    }
    return null
}

/**
 * 得到uri地址
 */
fun Uri.getDataColumn(context: Context, selection: String?, selectionArgs: Array<String>?): String? {
    val column = MediaStore.Images.Media.DATA
    val projection = arrayOf(column)
    var cursor: Cursor? = null
    try {
        cursor = context.contentResolver.query(this, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } finally {
        cursor?.close()
    }
    return null
}

/**
 * uri to bitmap
 * content://media/external/images/media/13550
 */
fun Uri.toBitmap(context: Context): Bitmap {
    return MediaStore.Images.Media.getBitmap(context.contentResolver, this)
}
