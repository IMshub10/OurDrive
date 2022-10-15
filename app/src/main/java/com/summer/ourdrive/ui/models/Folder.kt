package com.summer.ourdrive.ui.models

import androidx.databinding.BaseObservable

data class Folder(val folderId: String, val folderName: String, val createdAt: Long) :
    BaseObservable()