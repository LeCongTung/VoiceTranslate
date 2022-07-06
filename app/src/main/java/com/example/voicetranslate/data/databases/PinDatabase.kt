package com.example.voicetranslate.data.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.voicetranslate.data.daos.PinDAO
import com.example.voicetranslate.models.Pin

@Database(entities = [Pin::class], version = 1)
abstract class PinDatabase: RoomDatabase() {

    abstract fun pinDAO(): PinDAO

    companion object{
        @Volatile
        private var INSTANCE: PinDatabase?= null
        private const val DB_NAME = "pin_database"

        fun getDatabase(context: Context): PinDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PinDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}