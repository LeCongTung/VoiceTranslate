package com.example.voicetranslate.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.voicetranslate.data.databases.PinDatabase
import com.example.voicetranslate.data.repositories.PinRepository
import com.example.voicetranslate.models.Pin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PinViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Pin>>
    private val repository: PinRepository

    init {
        val pinDao = PinDatabase.getDatabase(application).pinDAO()
        repository = PinRepository(pinDao)
        readAllData = repository.readAllData
    }

    fun insert(pin: Pin){
        viewModelScope.launch(Dispatchers.IO){
            repository.insert(pin)
        }
    }

    fun delete(pin: Pin){
        viewModelScope.launch(Dispatchers.IO){
            repository.delete(pin)
        }
    }

    fun deleteRow(id: Int){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteRow(id)
        }
    }
}