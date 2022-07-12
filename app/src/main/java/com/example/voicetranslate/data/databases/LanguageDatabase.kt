package com.example.voicetranslate.data.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.voicetranslate.data.daos.LanguageDAO
import com.example.voicetranslate.models.Saved

@Database(entities = [Saved::class], version = 1)
abstract class LanguageDatabase: RoomDatabase() {

    abstract fun languageDAO(): LanguageDAO

    companion object{
        @Volatile
        private var INSTANCE: LanguageDatabase?= null
        private const val DB_NAME = "language_database"

        fun getDatabase(context: Context): LanguageDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LanguageDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}