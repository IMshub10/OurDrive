package com.summer.ourdrive.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.database.ValueEventListener
import com.summer.ourdrive.apiservices.ApiService
import com.summer.ourdrive.utils.FirebaseDatabaseUtils
import com.summer.ourdrive.utils.Utils

class ApiRepository(private val apiService: ApiService) {

    suspend fun getUnixTimeFromWorldClock(): Long? {
        val response = apiService.getUnixTime()
        return if (response.code() in 200..299) {
            if (response.body() != null) {
                if (response.body()!!.unixtime != null) {
                    response.body()!!.unixtime
                } else {
                    null
                }
            } else {
                null
            }
        } else {
            null
        }
    }

    fun getAllFolders(valueEventListener: ValueEventListener) {
        FirebaseDatabaseUtils.getFolderReference().addValueEventListener(valueEventListener)
    }

    fun createAFolder(map: HashMap<String, Any>): Task<Void> {
        return FirebaseDatabaseUtils.getFolderReference().child(Utils.generateUUID()).setValue(map)
    }

    fun createAnImageInFolder(
        folderId: String,
        imageId: String,
        map: HashMap<String, Any>
    ): Task<Void> {
        return FirebaseDatabaseUtils.getFolderReference().child(folderId).child("Images")
            .child(imageId)
            .setValue(map)
    }

    fun fetchLatestImagesFromFolder(folderId: String, valueEventListener: ValueEventListener) {
        FirebaseDatabaseUtils.getFolderReference().child(folderId).child("Images")
            .addValueEventListener(valueEventListener)
    }
}