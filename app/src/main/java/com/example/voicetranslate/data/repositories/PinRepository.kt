package com.example.voicetranslate.data.repositories

import androidx.lifecycle.LiveData
import com.example.voicetranslate.data.daos.PinDAO
import com.example.voicetranslate.models.Pin

class PinRepository(private val pinDAO: PinDAO) {

    val readAllData: LiveData<List<Pin>> = pinDAO.readAllData()

    fun insert(pin: Pin){
        pinDAO.insert(pin)
    }

    fun delete(pin: Pin){
        pinDAO.delete(pin)
    }

    fun deleteRow(id: Int){
        pinDAO.deleteDataById(id)
    }
}