package com.example.voicetranslate.data.repositories

import androidx.lifecycle.LiveData
import com.example.voicetranslate.data.daos.ImageDAO
import com.example.voicetranslate.models.Image

class ImageRepository(private val imageDAO: ImageDAO) {

    val readAllData: LiveData<List<Image>> = imageDAO.readAllData()

    fun insert(image: Image){
        imageDAO.insert(image)
    }

    fun deleteByTime(time: String){
        imageDAO.deleteDataByTime(time)
    }
}