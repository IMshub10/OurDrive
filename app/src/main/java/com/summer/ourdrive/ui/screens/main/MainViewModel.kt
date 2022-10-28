package com.summer.ourdrive.ui.screens.main

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.summer.ourdrive.database.ImageEntity
import com.summer.ourdrive.repositories.ApiRepository
import com.summer.ourdrive.repositories.DatabaseRepository
import com.summer.ourdrive.utils.FirebaseDatabaseUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val apiRepository: ApiRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    var isLoading = ObservableField("Loading Folders...")
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        FirebaseCrashlytics.getInstance().recordException(throwable)
        isLoading.set(null)
    }

    fun createANewFolder(folderName: String) {
        CoroutineScope(Dispatchers.Default + coroutineExceptionHandler).launch {
            isLoading.set("Creating a Folder...")
            val currentUnixTime = apiRepository.getUnixTimeFromWorldClock()
            if (currentUnixTime != null) {
                val map = HashMap<String, Any>()
                map["folder_name"] = folderName
                map["create_unix_time"] = currentUnixTime
                apiRepository.createAFolder(map)
                    .addOnSuccessListener {
                        isLoading.set(null)
                    }.addOnFailureListener {
                        isLoading.set(null)
                    }
            } else {
                isLoading.set(null)
            }
        }
    }

    fun createANewImageInFolder(
        folderId: String,
        imageId: String,
        imageUrl: String
    ) {
        CoroutineScope(Dispatchers.Default + coroutineExceptionHandler).launch {
            isLoading.set("Creating a Folder...")
            val currentUnixTime = apiRepository.getUnixTimeFromWorldClock()
            if (currentUnixTime != null) {
                val map = HashMap<String, Any>()
                map["image_url"] = imageUrl
                map["create_unix_time"] = currentUnixTime
                apiRepository.createAnImageInFolder(folderId, imageId, map)
                    .addOnSuccessListener {
                        isLoading.set(null)
                    }.addOnFailureListener {
                        isLoading.set(null)
                    }
            } else {
                isLoading.set(null)
            }
        }
    }

    fun getAllFolders(valueEventListener: ValueEventListener) {
        apiRepository.getAllFolders(valueEventListener)
    }

    fun getImagesByFolderId(folderId: String): LiveData<List<ImageEntity>> {
        return databaseRepository.getAllImagesByFolderId(folderId = folderId)
    }

    fun saveImages(imageEntityList: List<ImageEntity>) {
        CoroutineScope(Dispatchers.Default).launch {
            imageEntityList.forEach {
                databaseRepository.ignoreInsert(it)
            }
        }
    }

    fun getImageByImageIdLive(imageId: String): LiveData<ImageEntity> =
        databaseRepository.getImageByImageIdLive(imageId)

    fun getImageByImageId(imageId: String): ImageEntity =
        databaseRepository.getImageByImageId(imageId)

    fun updateImageEntityWithImageUrl(imageId: String, imageUrl: String) =
        databaseRepository.updateImageEntityWithImageUrl(imageId, imageUrl)

    fun fetchLatestImagesFromFolder(folderId: String) {
        apiRepository.fetchLatestImagesFromFolder(
            folderId = folderId,
            valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    viewModelScope.launch(Dispatchers.Default) {
                        FirebaseDatabaseUtils.createImageEntities(folderId, snapshot)
                            .forEach { imageEntity -> databaseRepository.ignoreInsert(imageEntity) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}