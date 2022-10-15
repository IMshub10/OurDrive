package com.summer.ourdrive.ui.models

import android.graphics.Bitmap
import androidx.databinding.BaseObservable

data class Image(var id: String, var folderId: String, var imageUrl: String?, var bitmap: Bitmap?) :
    BaseObservable()