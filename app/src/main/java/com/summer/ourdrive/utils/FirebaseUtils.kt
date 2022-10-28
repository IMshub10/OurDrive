package com.summer.ourdrive.utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.summer.ourdrive.database.ImageEntity
import com.summer.ourdrive.ui.models.Folder
import com.summer.ourdrive.ui.models.Image


object FirebaseDatabaseUtils {
    private const val BASE_URL =
        "https://our-drive-fe49c-default-rtdb.asia-southeast1.firebasedatabase.app/"

    fun getFolderReference(): DatabaseReference {
        return FirebaseDatabase.getInstance(BASE_URL).reference.child("Folders")
    }

    fun createFolderList(snapshot: DataSnapshot): List<Folder> {
        if (snapshot.value == null) return listOf()
        val maps = snapshot.value as HashMap<*, *>
        val folderList = mutableListOf<Folder>()
        maps.forEach {
            val valueMap = it.value as Map<*, *>
            folderList.add(
                Folder(
                    it.key.toString(),
                    valueMap["folder_name"].toString(),
                    valueMap["create_unix_time"].toString().toLong()
                )
            )
        }
        val sortedList = folderList.sortedByDescending {
            it.createdAt
        }
        return sortedList
    }

    fun createImageEntities(folderId: String, snapshot: DataSnapshot): List<ImageEntity> {
        if (snapshot.value == null) return listOf()
        val maps = snapshot.value as HashMap<*, *>
        val imageList = mutableListOf<ImageEntity>()
        maps.forEach {
            val valueMap = it.value as Map<*, *>
            imageList.add(
                ImageEntity(
                    it.key.toString(),
                    folderId,
                    valueMap["image_url"].toString()
                )
            )
        }
        return imageList
    }
}

object FirebaseStorageUtils {
    private const val BASE_URL = "gs://our-drive-fe49c.appspot.com"
    fun getImageStorageReference(folderId: String, imageId: String): StorageReference {
        return FirebaseStorage.getInstance(BASE_URL).reference
            .child(folderId).child(imageId)
    }
}