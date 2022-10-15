package com.summer.ourdrive.database


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey(autoGenerate = false) val key: String,
     val folderId: String,//ForeignKey folders
    val imageUrl: String?,
)
