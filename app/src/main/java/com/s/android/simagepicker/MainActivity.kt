package com.s.android.simagepicker

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.s.android.imagepicker.ImagePicker
import com.s.android.imagepicker.listener.ImagePickerListener
import com.s.android.imagepicker.utils.compressBitmap
import com.s.android.imagepicker.utils.toBitmap
import com.s.android.simagepicker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        mBinding.button.setOnClickListener {
            ImagePicker.with(this)
                // 是否裁剪
                .setCrop(true)
                .setListener(object : ImagePickerListener<Uri> {
                    override fun callback(t: Uri?) {
                        mBinding.imageView.setImageBitmap(
                            t?.toBitmap(this@MainActivity)?.compressBitmap(this@MainActivity)
                        )
                    }
                })
                .jumpToCamera()
        }
        mBinding.button2.setOnClickListener {
            ImagePicker.with(this)
                // 是否裁剪
                .setCrop(true)
                .asBitmap()
                .setListener(object : ImagePickerListener<Bitmap> {
                    override fun callback(t: Bitmap?) {
                        mBinding.imageView.setImageBitmap(t?.compressBitmap(this@MainActivity))
                    }
                })
                .jumpToPicture()
        }
    }
}
