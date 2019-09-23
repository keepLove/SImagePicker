package com.s.android.simagepicker

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.s.android.imagepicker.ImagePicker
import com.s.android.imagepicker.ImagePickerCallback
import com.s.android.imagepicker.utils.compressBitmap
import com.s.android.imagepicker.utils.loge
import com.s.android.imagepicker.utils.toBitmap
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            ImagePicker.Builder(this)
                    // 是否裁剪
                    .setCrop(true)
                    // 监听，返回Uri、Bitmap、File
                    .callback(object : ImagePickerCallback<Uri> {
                        override fun callback(t: Uri?) {
                            imageView.setImageBitmap(t?.toBitmap(this@MainActivity)?.compressBitmap(this@MainActivity))
                        }
                    })
                    .build()
                    .jumpToCamera()
        }
        button2.setOnClickListener {
            ImagePicker.Builder(this)
                    // 是否裁剪
                    .setCrop(true)
                    // 监听，返回Uri、Bitmap、File
                    .callback(object : ImagePickerCallback<Bitmap> {
                        override fun callback(t: Bitmap?) {
                            imageView.setImageBitmap(t?.compressBitmap(this@MainActivity))
                        }
                    })
                    .build()
                    .jumpToPicture()
        }
        button3.setOnClickListener {
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
