[![SImagePicker](https://jitpack.io/v/com.github.keepLove/SImagePicker.svg)](https://jitpack.io/#com.github.keepLove/SImagePicker)

## Dependency

#### Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:


```
    allprojects {
    	repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

#### Step 2. Add the dependency


```
    dependencies {
        implementation 'com.github.keepLove:SImagePicker:Tag'
	}
```

## Use

### 1.  拍照


```
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
```

### 2.  从相册获取图片


```
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
```

### 3.  获取相册全部图片

```
ImagePicker.Builder(this)
        // 监听，返回Map<String, List<String>>、List<String>
        .callback(object : ImagePickerCallback<Map<String, List<String>>> {
            override fun callback(t: Map<String, List<String>>?) {
                loge("result:$t")
            }
        })
        .build()
        .getAllPicture()
```
