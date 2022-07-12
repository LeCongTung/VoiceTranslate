package com.example.voicetranslate.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import com.example.voicetranslate.models.Image

@Dao
interface ImageDAO {

//    Read
    @Query("SELECT * FROM Image ORDER BY time DESC")
    fun readAllData(): LiveData<List<Image>>

    @Query("SELECT * FROM Image WHERE pinned = 1 ORDER BY time DESC")
    fun readAllPinned(): LiveData<List<Image>>

//    Insert
    @Insert(onConflict = IGNORE)
    fun insert(image: Image)

//    Update
    @Update
    fun update(image: Image)

    @Query("UPDATE Image SET pinned = 1 WHERE time = :time_update")
    fun updatePinByTime(time_update: String)

    @Query("UPDATE Image SET pinned = 0 WHERE time = :time_update")
    fun updateUnPinByTime(time_update: String)

//    Delete
    @Delete
    fun delete(image: Image)

    @Query("DELETE FROM Image WHERE time = :time_delete")
    fun deleteByTime(time_delete: String)
}