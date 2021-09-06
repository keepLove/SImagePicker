package com.s.android.imagepicker.listener;

import androidx.annotation.Nullable;

/**
 * @param <T> {File、Bitmap、Uri}
 */
public interface ImagePickerListener<T> {

    void callback(@Nullable T value);
}
