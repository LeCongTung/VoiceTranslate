package com.example.voicetranslate.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import com.example.voicetranslate.models.Image

@Dao
interface ImageDAO {

//    Read
    @Query("SELECT * FROM Image ORDER BY time DESC")
    fun readAllData(): LiveData<List<Image>>

//    Insert
    @Insert(onConflict = IGNORE)
    fun insert(image: Image)

    @Query("DELETE FROM Image WHERE time =:time_delete")
    fun deleteDataByTime(time_delete: String)
}