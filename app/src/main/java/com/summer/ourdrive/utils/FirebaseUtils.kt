package com.summer.ourdrive.utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.summer.ourdrive.ui.models.Folder


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
}

object FirebaseStorageUtils {
    private const val BASE_URL = "gs://our-drive-fe49c.appspot.com"
    fun getImageStorageReference(folderId: String, imageId: String): StorageReference {
        return FirebaseStorage.getInstance(BASE_URL).reference
            .child(folderId).child(imageId)
    }
}