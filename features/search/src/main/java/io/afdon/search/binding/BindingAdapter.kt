package io.afdon.search.binding

import android.widget.ImageButton
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.afdon.search.R

object BindingAdapter {

    @BindingAdapter("isFavourite")
    @JvmStatic
    fun setIsFavourite(imageButton: ImageButton, isFavourite: Boolean) {
        if (isFavourite) {
            imageButton.setImageResource(R.drawable.ic_star_yellow)
        } else {
            imageButton.setImageResource(R.drawable.ic_star_outline)
        }
    }

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