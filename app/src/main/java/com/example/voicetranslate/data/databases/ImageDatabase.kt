package com.example.voicetranslate.data.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.voicetranslate.data.daos.ImageDAO
import com.example.voicetranslate.models.Image

@Database(entities = [Image::class], version = 1)
abstract class ImageDatabase: RoomDatabase() {

    abstract fun imageDAO(): ImageDAO

    companion object{
        @Volatile
        private var INSTANCE: ImageDatabase?= null
        private const val DB_NAME = "image_database"

        fun getDatabase(context: Context): ImageDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ImageDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}