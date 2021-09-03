package io.afdon.favourite.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object BindingAdapter {

    @BindingAdapter("circleImage")
    @JvmStatic
    fun setCircleImage(imageView: ImageView, url: String?) {
        url?.let {
            Glide.with(imageView).load(it).apply(RequestOptions().circleCrop()).into(imageView)
        } ?: run {
            imageView.setImageDrawable(null)
        }
    }
}