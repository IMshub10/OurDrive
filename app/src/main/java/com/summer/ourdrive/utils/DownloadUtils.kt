package com.summer.ourdrive.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import android.webkit.DownloadListener
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import java.io.File


object DownloadUtils {

    const val TAG = "DownloadUtils"

    fun downloadImage(context: Context, url: String, folderId: String, imageId: String) {
        val imagesDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Our Drive"
        )
        val foldersDir = File(imagesDir, "images")
        val downloadId = PRDownloader.download(url, foldersDir.path, "$imageId.png")
            .build()
            .setOnStartOrResumeListener {
                Log.d(TAG, "setOnStartOrResumeListener")
            }
            .setOnPauseListener {
                Log.d(TAG, "setOnPauseListener")
            }
            .setOnPauseListener {
                Log.d(TAG, "setOnPauseListener")
            }
            .setOnCancelListener {
                Log.d(TAG, "setOnCancelListener")
            }
            .setOnProgressListener {
                Log.d(TAG, "setOnProgressListener")
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Log.d(TAG, "onDownloadComplete")
                }

                override fun onError(error: com.downloader.Error?) {
                    Log.d(TAG, "onError")
                }
            })

    }
}