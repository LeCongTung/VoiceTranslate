package com.example.voicetranslate.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import com.example.voicetranslate.models.Pin

@Dao
interface PinDAO {

//    Read data
    @Query("SELECT * FROM Pin ORDER BY time DESC")
    fun readAllData(): LiveData<List<Pin>>

//    Add data
    @Insert(onConflict = IGNORE)
    fun insert(pin: Pin)

//    Delete

//    @Query("DELETE FROM Pin WHERE id =:id_delete")
//    fun deleteDataById(id_delete: Int)

    @Query("DELETE FROM Pin WHERE time =:time_delete")
    fun deleteDataByTime(time_delete: String)
}