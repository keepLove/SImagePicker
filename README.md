# SImagePicker
拍照和从相册获取图片
```
val imagePicker = ImagePicker.Builder()
           .from(this)
           .setImagePickerCallback(object : ImagePickerCallback<Uri> {
                override fun callback(t: Uri?) {
                    imageView.setImageBitmap(t?.toBitmap(this@MainActivity)?.compressBitmap(this@MainActivity))
                }
           })
           .build()
button.setOnClickListener {
    imagePicker.jumpToCamera()
}
button2.setOnClickListener {
    imagePicker.jumpToPicture()
}
```