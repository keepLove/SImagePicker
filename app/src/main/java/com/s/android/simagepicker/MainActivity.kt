package com.s.android.simagepicker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.s.android.imagepicker.ImagePicker
import com.s.android.imagepicker.ImagePickerCallback
import com.s.android.imagepicker.utils.loge
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val imagePicker = ImagePicker.Builder()
                .from(this)
                .setCrop(true)
                .callback(object : ImagePickerCallback<Map<String, List<String>>> {
                    override fun callback(t: Map<String, List<String>>?) {
                        loge("result:$t")
//                        imageView.setImageBitmap(t?.toBitmap(this@MainActivity)?.compressBitmap(this@MainActivity))
                    }
                })
                .build()
        button.setOnClickListener {
            imagePicker.jumpToCamera()
        }
        button2.setOnClickListener {
            imagePicker.jumpToPicture()
        }
        imagePicker.getAllPicture()
    }
}
