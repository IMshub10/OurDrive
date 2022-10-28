package com.summer.ourdrive

import android.app.Application
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig




class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Setting timeout globally for the download network requests:
        // Setting timeout globally for the download network requests:
        val config = PRDownloaderConfig.newBuilder()
            .setReadTimeout(30000)
            .setDatabaseEnabled(true)
            .setConnectTimeout(30000)
            .build()
        PRDownloader.initialize(applicationContext,config);
    }
}