package com.s.android.imagepicker

/**
 *
 * @author android
 * @date   2018/5/25
 */
interface ImagePickerFunction {

    /**
     * 跳转到系统图库
     */
    fun jumpToPicture()

    /**
     * 跳转到系统摄像机
     */
    fun jumpToCamera()

    /**
     * 获取全部的图片
     */
    fun getAllPicture()
}