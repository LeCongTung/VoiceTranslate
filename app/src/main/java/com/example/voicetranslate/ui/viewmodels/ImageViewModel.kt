package com.example.voicetranslate.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.voicetranslate.data.databases.ImageDatabase
import com.example.voicetranslate.data.repositories.ImageRepository
import com.example.voicetranslate.models.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Image>>
    private val repository: ImageRepository

    init {
        val imageDao = ImageDatabase.getDatabase(application).imageDAO()
        repository = ImageRepository(imageDao)
        readAllData = repository.readAllData
    }

    fun insert(image: Image){
        viewModelScope.launch(Dispatchers.IO){
            repository.insert(image)
        }
    }

//    fun selectAll(){
//        viewModelScope.launch(Dispatchers.IO){
//            repository.selectAll()
//        }
//    }

    fun deleteRow(id: Int){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteRow(id)
        }
    }
}