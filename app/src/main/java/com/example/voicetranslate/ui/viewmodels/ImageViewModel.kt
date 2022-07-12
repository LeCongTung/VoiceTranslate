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
    val readAllPinned: LiveData<List<Image>>
    private val repository: ImageRepository

    init {
        val imageDao = ImageDatabase.getDatabase(application).imageDAO()
        repository = ImageRepository(imageDao)
        readAllData = repository.readAllData
        readAllPinned = repository.readAllPinned
    }

    fun insert(image: Image){
        viewModelScope.launch(Dispatchers.IO){
            repository.insert(image)
        }
    }

    fun update(image: Image){
        viewModelScope.launch(Dispatchers.IO){
            repository.update(image)
        }
    }

    fun updateUnPinByTime(time: String){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateUnPinByTime(time)
        }
    }

    fun updatePinByTime(time: String){
        viewModelScope.launch(Dispatchers.IO){
            repository.updatePinByTime(time)
        }
    }

    fun delete(image: Image){
        viewModelScope.launch(Dispatchers.IO){
            repository.delete(image)
        }
    }

    fun deleteByTime(time: String){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteByTime(time)
        }
    }
}