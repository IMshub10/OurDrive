package com.summer.ourdrive.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.summer.ourdrive.database.AppDatabase
import com.summer.ourdrive.database.ImageEntity
import com.summer.ourdrive.database.ImagesDao

class DatabaseRepository(application: Application) {
    private val imagesDao: ImagesDao

    init {
        val database = AppDatabase.getDatabase(application)
        imagesDao = database.getImagesDao()
    }

    suspend fun ignoreInsert(imageEntity: ImageEntity) = imagesDao.ignoreInsert(imageEntity)
    suspend fun replaceInsert(imageEntity: ImageEntity) = imagesDao.replaceInsert(imageEntity)
    suspend fun update(imageEntity: ImageEntity) = imagesDao.update(imageEntity)
    suspend fun delete(imageEntity: ImageEntity) = imagesDao.delete(imageEntity)
    fun getAllImagesByFolderId(folderId: String) = imagesDao.getAllImagesByFolderId(folderId)
    fun getImageByImageIdLive(imageId: String) = imagesDao.getImageByImageIdLive(imageId)
    fun getImageByImageId(imageId: String) = imagesDao.getImageByImageId(imageId)
    fun updateImageEntityWithImageUrl(imageId: String, imageUrl: String) =
        imagesDao.updateImageEntityWithImageUrl(imageId,imageUrl)
}