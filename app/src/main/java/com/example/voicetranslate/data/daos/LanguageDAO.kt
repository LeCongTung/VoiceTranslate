package com.example.voicetranslate.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.voicetranslate.models.Saved

@Dao
interface LanguageDAO {

    @Query("SELECT * FROM Saved WHERE type = 'above' ORDER BY time DESC LIMIT 4")
    fun readAllLanguageFrom(): LiveData<List<Saved>>

    @Query("SELECT * FROM Saved WHERE type = 'null' ORDER BY time DESC LIMIT 4")
    fun readAllLanguageTo(): LiveData<List<Saved>>

    @Query("SELECT COUNT(language) FROM Saved WHERE type = 'above'")
    fun countRow(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(language: Saved)
}