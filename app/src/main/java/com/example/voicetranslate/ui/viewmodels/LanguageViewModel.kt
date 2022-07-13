package com.example.voicetranslate.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.voicetranslate.data.databases.LanguageDatabase
import com.example.voicetranslate.data.repositories.LanguageRepository
import com.example.voicetranslate.models.Saved
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LanguageViewModel(application: Application): AndroidViewModel(application) {

    val readAllLanguageFrom: LiveData<List<Saved>>
    val readAllLanguageTo: LiveData<List<Saved>>
    private val repository: LanguageRepository

    init {
        val languageDao = LanguageDatabase.getDatabase(application).languageDAO()
        repository = LanguageRepository(languageDao)
        readAllLanguageFrom = repository.readAllLanguageFrom
        readAllLanguageTo = repository.readAllLanguageTo
    }

    fun insert(language: Saved){
        viewModelScope.launch(Dispatchers.IO){
            repository.insert(language)
        }
    }
}