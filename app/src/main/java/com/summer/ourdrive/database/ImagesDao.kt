package com.summer.ourdrive.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ImagesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun ignoreInsert(imageEntity: ImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun replaceInsert(imageEntity: ImageEntity)

    @Update
    suspend fun update(imageEntity: ImageEntity)

    @Delete
    suspend fun delete(imageEntity: ImageEntity)

    @Query("SELECT * FROM images WHERE folderId = :folderId")
    fun getAllImagesByFolderId(folderId: String): LiveData<List<ImageEntity>>

    @Query("SELECT * FROM images WHERE `key` = :imageId ")
    fun getImageByImageIdLive(imageId: String): LiveData<ImageEntity>

    @Query("SELECT * FROM images WHERE `key` = :imageId ")
    fun getImageByImageId(imageId: String): ImageEntity

    @Query("UPDATE images SET imageUrl =:imageUrl where `key` = :imageId")
    fun updateImageEntityWithImageUrl(imageId: String, imageUrl: String)
}