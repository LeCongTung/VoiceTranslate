package com.example.voicetranslate.data.repositories

import androidx.lifecycle.LiveData
import com.example.voicetranslate.data.daos.LanguageDAO
import com.example.voicetranslate.models.Saved

class LanguageRepository(private val languageDAO: LanguageDAO) {
    val readAllLanguageFrom: LiveData<List<Saved>> = languageDAO.readAllLanguageFrom()
    val readAllLanguageTo: LiveData<List<Saved>> = languageDAO.readAllLanguageTo()

    fun insert(language: Saved){
        languageDAO.insert(language)
    }

    fun countRow(): Int{
        return languageDAO.countRow()
    }
}