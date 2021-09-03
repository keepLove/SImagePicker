package com.s.android.simagepicker

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.s.android.imagepicker.ImagePicker
import com.s.android.imagepicker.ImagePickerCallback
import com.s.android.imagepicker.utils.compressBitmap
import com.s.android.imagepicker.utils.loge
import com.s.android.imagepicker.utils.toBitmap
import com.s.android.simagepicker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        mBinding.button.setOnClickListener {
            ImagePicker.Builder(this)
                // 是否裁剪
                .setCrop(true)
                // 监听，返回Uri、Bitmap、File
                .callback(object : ImagePickerCallback<Uri> {
                    override fun callback(t: Uri?) {
                        mBinding.imageView.setImageBitmap(
                            t?.toBitmap(this@MainActivity)?.compressBitmap(this@MainActivity)
                        )
                    }
                })
                .build()
                .jumpToCamera()
        }
        mBinding.button2.setOnClickListener {
            ImagePicker.Builder(this)
                // 是否裁剪
                .setCrop(true)
                // 监听，返回Uri、Bitmap、File
                .callback(object : ImagePickerCallback<Bitmap> {
                    override fun callback(t: Bitmap?) {
                        mBinding.imageView.setImageBitmap(t?.compressBitmap(this@MainActivity))
                    }
                })
                .build()
                .jumpToPicture()
        }
        mBinding.button3.setOnClickListener {
            ImagePicker.Builder(this)
                // 监听，返回Map<String, List<String>>、List<String>
                .callback(object : ImagePickerCallback<Map<String, List<String>>> {
                    override fun callback(t: Map<String, List<String>>?) {
                        loge("result:$t")
                    }
                })
                .build()
                .getAllPicture()
        }
    }
}
