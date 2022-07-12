package com.example.voicetranslate.data.repositories

import androidx.lifecycle.LiveData
import com.example.voicetranslate.data.daos.ImageDAO
import com.example.voicetranslate.models.Image

class ImageRepository(private val imageDAO: ImageDAO) {

    val readAllData: LiveData<List<Image>> = imageDAO.readAllData()
    val readAllPinned: LiveData<List<Image>> = imageDAO.readAllPinned()

    fun insert(image: Image){
        imageDAO.insert(image)
    }

    fun update(image: Image){
        imageDAO.update(image)
    }

    fun updateUnPinByTime(time: String){
        imageDAO.updateUnPinByTime(time)
    }

    fun updatePinByTime(time: String){
        imageDAO.updatePinByTime(time)
    }

    fun delete(image: Image){
        imageDAO.delete(image)
    }

    fun deleteByTime(time: String){
        imageDAO.deleteByTime(time)
    }
}