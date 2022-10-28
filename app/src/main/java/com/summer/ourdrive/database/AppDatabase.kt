package com.summer.ourdrive.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ImageEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getImagesDao(): ImagesDao

    companion object {
        private var instance: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            if (instance != null) return instance!!
            else {
                synchronized(this) {
                    val inst = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        "our_drive_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    instance = inst
                    return inst
                }
            }
        }
    }
}