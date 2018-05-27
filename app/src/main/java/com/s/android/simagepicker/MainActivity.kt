package com.s.android.simagepicker

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.s.android.imagepicker.ImagePicker
import com.s.android.imagepicker.ImagePickerCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val imagePicker = ImagePicker.Builder()
                .from(this)
                .setImagePickerCallback(object : ImagePickerCallback {
                    override fun callback(uri: Uri?) {
                        Log.e("MainActivity", "uri:$uri")
                        imageView.setImageURI(uri)
                    }
                })
                .build()
        button.setOnClickListener {
            imagePicker.jumpToCamera()
        }
        button2.setOnClickListener {
            imagePicker.jumpToPicture()
        }
    }
}
