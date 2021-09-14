#ImagePicker 拍照和相册获取图片 [![SImagePicker](https://jitpack.io/v/com.github.keepLove/SImagePicker.svg)](https://jitpack.io/#com.github.keepLove/SImagePicker)

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
```

### 2.  从相册获取图片

```
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
```
