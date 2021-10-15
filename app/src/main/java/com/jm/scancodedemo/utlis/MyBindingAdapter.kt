package com.jm.scancodedemo.utlis

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("android:src")
fun ImageView.setSourceImageResource(resource: Int) {
    this.setImageResource(resource)
}

@BindingAdapter("android:src")
fun ImageView.setSourceImageBitmap(bitmap: Bitmap?) {
    if (bitmap != null) this.setImageBitmap(bitmap)
}

@BindingAdapter("android:src")
fun ImageView.setSourceImageUrl(url: String?) {
    if (url != null) Glide.with(this.context)
        .load(url)
        .into(this);
}