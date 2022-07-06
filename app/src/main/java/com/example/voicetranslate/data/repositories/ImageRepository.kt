package com.example.voicetranslate.data.repositories

import androidx.lifecycle.LiveData
import com.example.voicetranslate.data.daos.ImageDAO
import com.example.voicetranslate.models.Image

class ImageRepository(private val imageDAO: ImageDAO) {

    val readAllData: LiveData<List<Image>> = imageDAO.readAllData()

    fun insert(image: Image){
        imageDAO.insert(image)
    }

//    fun selectAll(){
//        imageDAO.selectAll()
//    }

    fun deleteRow(id: Int){
        imageDAO.deleteDataById(id)
    }
}