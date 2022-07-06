package com.example.voicetranslate.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import com.example.voicetranslate.models.Image

@Dao
interface ImageDAO {

//    Read data
    @Query("SELECT * FROM Image ORDER BY id DESC")
    fun readAllData(): LiveData<List<Image>>

//    @Query("SELECT id FROM Image")
//    fun selectAll()

//    Add data
    @Insert(onConflict = IGNORE)
    fun insert(image: Image)

//    Delete data
    @Query("DELETE FROM Image WHERE id =:id_delete")
    fun deleteDataById(id_delete: Int)
}