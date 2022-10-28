package com.summer.ourdrive.utils

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.summer.ourdrive.R
import com.summer.ourdrive.ui.models.Image
import java.util.*

object BindingAdapters {
    @BindingAdapter("setDateString")
    @JvmStatic
    fun AppCompatTextView.setDateString(timeInSecs: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInSecs * 1000L
        val textValue =
            "${Utils.getIn2Format(calendar[Calendar.DAY_OF_MONTH])}/${Utils.getIn2Format(calendar[Calendar.MONTH] + 1)}/${
                calendar[Calendar.YEAR]
            }\n" /*+
                    "${Utils.getIn2Format(calendar[Calendar.HOUR_OF_DAY])}:${
                        Utils.getIn2Format(
                            calendar[Calendar.MINUTE]
                        )
                    }:${Utils.getIn2Format(calendar[Calendar.SECOND])}"*/
        this.text = textValue
    }

    @BindingAdapter("setUpImage")
    @JvmStatic
    fun AppCompatImageView.setUpImage(image: Image?) {
        if (image == null) return
        if (image.bitmap != null) {
            Glide.with(context)
                .asBitmap()
                .load(
                    image.bitmap
                ).error(R.drawable.no_image)
                .into(this)

        } else {
            if (image.imageUrl != null) {
                Glide.with(context)
                    .asBitmap()
                    .load(
                        image.imageUrl
                    ).error(R.drawable.no_image)
                    .into(this)
            }
        }
    }

    @BindingAdapter("setVisibilityOfUpload")
    @JvmStatic
    fun AppCompatImageView.setVisibilityOfUpload(image: Image?) {
        if (image == null) {
            this.isVisible = false
        } else {
            this.isVisible = image.imageUrl == null
        }
    }
    @BindingAdapter("setVisibilityOfDownload")
    @JvmStatic
    fun AppCompatImageView.setVisibilityOfDownload(image: Image?) {
        if (image == null) {
            this.isVisible = false
        } else {
            this.isVisible = image.imageUrl != null
        }
    }
}
