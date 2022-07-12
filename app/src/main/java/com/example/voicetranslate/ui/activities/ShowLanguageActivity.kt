package com.example.voicetranslate.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicetranslate.R
import com.example.voicetranslate.models.Language
import com.example.voicetranslate.models.Saved
import com.example.voicetranslate.ui.adapters.AdapterLanguage
import com.example.voicetranslate.ui.adapters.AdapterLanguageRecently
import com.example.voicetranslate.ui.viewmodels.LanguageViewModel
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.android.synthetic.main.activity_show_language.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ShowLanguageActivity : AppCompatActivity(), AdapterLanguageRecently.onItemClickListener, AdapterLanguage.onItemClickListener {

    private val arrayLanguage = arrayOf(
        "Afrikaans",
        "Belarusian",
        "Bulgarian",
        "Bengali",
        "Catalan",
        "Chinese",
        "Czech",
        "Welsh",
        "Danish",
        "German",
        "Greek",
        "English",
        "Spanish",
        "Finnish",
        "French",
        "Irish",
        "Hindi",
        "Hungarian",
        "Indonesian",
        "Italian",
        "Japanese",
        "Korean",
        "Dutch",
        "Polish",
        "Portuguese",
        "Russian",
        "Swedish",
        "Thai",
        "Turkish",
        "Vietnamese"
    )

    private val imageShow = arrayOf(
        R.drawable.ic_flag_afrikaans,
        R.drawable.ic_flag_belarusian,
        R.drawable.ic_flag_bulgarian,
        R.drawable.ic_flag_bengali,
        R.drawable.ic_flag_catalan,
        R.drawable.ic_flag_chinese,
        R.drawable.ic_flag_czech,
        R.drawable.ic_flag_welsh,
        R.drawable.ic_flag_danish,
        R.drawable.ic_flag_german,
        R.drawable.ic_flag_greek,
        R.drawable.ic_flag_english,
        R.drawable.ic_flag_spain,
        R.drawable.ic_flag_finnish,
        R.drawable.ic_flag_french,
        R.drawable.ic_flag_irish,
        R.drawable.ic_flag_indian,
        R.drawable.ic_flag_hungarian,
        R.drawable.ic_flag_indonesian,
        R.drawable.ic_flag_italian,
        R.drawable.ic_flag_japanese,
        R.drawable.ic_flag_korean,
        R.drawable.ic_flag_dutch,
        R.drawable.ic_flag_polish,
        R.drawable.ic_flag_portuguese,
        R.drawable.ic_flag_russian,
        R.drawable.ic_flag_swedish,
        R.drawable.ic_flag_thai,
        R.drawable.ic_flag_turkish,
        R.drawable.ic_flag_vietnamese,
    )

    private val newArrayList: ArrayList<Language> = arrayListOf()
    private var searchArrayList: ArrayList<Language> = arrayListOf()

    private lateinit var value: String
    private lateinit var intentDisplayFrom: String
    private lateinit var intentDisplayTo: String
    private lateinit var intentTypeChoice: String
    private lateinit var intentAbort: String
    private lateinit var time: String

    private lateinit var languageViewModel: LanguageViewModel

    private lateinit var adapterRecently: AdapterLanguageRecently
    private lateinit var languageAdapter: AdapterLanguage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_language)

        languageViewModel = ViewModelProvider(this).get(LanguageViewModel::class.java)

        getData()
        getRecently()
        getLanguage()
        displayLanguageUsed()
        getQuantities()

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        time = current.format(formatter)

        btn_close.setOnClickListener {

            onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when {
            requestCode == 333 && resultCode == Activity.RESULT_OK -> {
                data?.let {
                    intentDisplayFrom = data.getStringExtra("displayFrom").toString()
                    intentDisplayTo = data.getStringExtra("displayTo").toString()
                }
            }
        }
    }

    private fun getData() {
        value = intent.getStringExtra("value").toString()
        intentDisplayFrom = intent.getStringExtra("displayFrom").toString()
        intentDisplayTo = intent.getStringExtra("displayTo").toString()
        intentTypeChoice = intent.getStringExtra("typeChoice").toString()
        intentAbort = intent.getStringExtra("abort").toString()
    }

    private fun getLanguage() {
        newArrayList.clear()
        for (i in arrayLanguage.indices) {

            val language = Language(arrayLanguage[i], imageShow[i])
            newArrayList.add(language)
        }
        searchArrayList = newArrayList
        languageAdapter = AdapterLanguage(this, newArrayList)
        list_item.adapter = languageAdapter
        list_item.layoutManager = LinearLayoutManager(this)
        list_item.isNestedScrollingEnabled = false
        list_item.setHasFixedSize(true)
    }

    private fun getRecently(){
        adapterRecently = AdapterLanguageRecently(this)
        list_item_recent.adapter = adapterRecently
        list_item_recent.layoutManager = LinearLayoutManager(this)
        list_item_recent.isNestedScrollingEnabled = false
        if (intentTypeChoice == "above")
            languageViewModel.readAllLanguageFrom.observe(this) {
                adapterRecently.setData(it)
            }
        else
            languageViewModel.readAllLanguageTo.observe(this) {
                adapterRecently.setData(it)
            }
    }

    private fun detectLanguage(language: String): String {
        var languageType = ""
        when(language){
            "Afrikaans" -> languageType = TranslateLanguage.AFRIKAANS
            "Belarusian" -> languageType = TranslateLanguage.BELARUSIAN
            "Bulgarian" -> languageType = TranslateLanguage.BULGARIAN
            "Bengali" -> languageType = TranslateLanguage.BENGALI
            "Catalan" -> languageType = TranslateLanguage.CATALAN
            "Chinese" -> languageType = TranslateLanguage.CHINESE
            "Czech" -> languageType = TranslateLanguage.CZECH
            "Welsh" -> languageType = TranslateLanguage.WELSH
            "Danish" -> languageType = TranslateLanguage.DANISH
            "German" -> languageType = TranslateLanguage.GERMAN
            "Greek" -> languageType = TranslateLanguage.GREEK
            "English" -> languageType = TranslateLanguage.ENGLISH
            "Spanish" -> languageType = TranslateLanguage.SPANISH
            "Finnish" -> languageType = TranslateLanguage.FINNISH
            "French" -> languageType = TranslateLanguage.FRENCH
            "Irish" -> languageType = TranslateLanguage.IRISH
            "Hindi" -> languageType = TranslateLanguage.HINDI
            "Hungarian" -> languageType = TranslateLanguage.HUNGARIAN
            "Indonesian" -> languageType = TranslateLanguage.INDONESIAN
            "Italian" -> languageType = TranslateLanguage.ITALIAN
            "Japanese" -> languageType = TranslateLanguage.JAPANESE
            "Korean" -> languageType = TranslateLanguage.KOREAN
            "Dutch" -> languageType = TranslateLanguage.DUTCH
            "Polish" -> languageType = TranslateLanguage.POLISH
            "Portuguese" -> languageType = TranslateLanguage.PORTUGUESE
            "Russian" -> languageType = TranslateLanguage.RUSSIAN
            "Swedish" -> languageType = TranslateLanguage.SWEDISH
            "Thai" -> languageType = TranslateLanguage.THAI
            "Turkish" -> languageType = TranslateLanguage.TURKISH
            "Vietnamese" -> languageType = TranslateLanguage.VIETNAMESE
        }
        return languageType
    }

    private fun detectFlag(language: String): Int {

        var flag = 0
        for (i in arrayLanguage.indices)
            if (arrayLanguage[i] == language) {

                flag = imageShow[i]
            }
        return flag
    }

    private fun displayLanguageUsed(){
        val value = if (intentTypeChoice == "above")
            intentDisplayFrom
        else
            intentDisplayTo
        for (i in arrayLanguage.indices)
            if (arrayLanguage[i] == value){
                languageAdapter.selected = value
                adapterRecently.selected = "$intentTypeChoice.$value"
                break
            }
    }

    private fun saveRecent(){
        val sharedPreferences = getSharedPreferences("language", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply{
            putString("displayFrom", intentDisplayFrom)
            putString("languageFrom", detectLanguage(intentDisplayFrom))
            putInt("flagFrom", detectFlag(intentDisplayFrom))
            putString("displayTo", intentDisplayTo)
            putString("languageTo", detectLanguage(intentDisplayTo))
            putInt("flagTo", detectFlag(intentDisplayTo))
        }.apply()
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("displayFrom", intentDisplayFrom)
        intent.putExtra("languageFrom", detectLanguage(intentDisplayFrom))
        intent.putExtra("flagFrom", detectFlag(intentDisplayFrom))
        intent.putExtra("displayTo", intentDisplayTo)
        intent.putExtra("languageTo", detectLanguage(intentDisplayTo))
        intent.putExtra("flagTo", detectFlag(intentDisplayTo))
        setResult(Activity.RESULT_OK, intent)
        finish()
        overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
    }

    private fun useOneTime(){
        val intent = Intent(this, TranslateActivity::class.java)
        intent.putExtra("value", value)
        intent.putExtra("displayFrom", intentDisplayFrom)
        intent.putExtra("languageFrom", detectLanguage(intentDisplayFrom))
        intent.putExtra("flagFrom", detectFlag(intentDisplayFrom))
        intent.putExtra("displayTo", intentDisplayTo)
        intent.putExtra("languageTo", detectLanguage(intentDisplayTo))
        intent.putExtra("flagTo", detectFlag(intentDisplayTo))
        startActivityForResult(intent, 333)
        overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
    }

    override fun onItemClick(language: Saved) {
        if (intentTypeChoice == "above")
            intentDisplayFrom = language.language
        else
            intentDisplayTo = language.language

        val languageSaved = Saved("$intentTypeChoice.${language.language}", language.language, detectFlag(language.language),time ,intentTypeChoice)
        languageViewModel.insert(languageSaved)
        saveRecent()

        if (intentAbort == "abort")
            useOneTime()
        else
            onBackPressed()
    }

    override fun onItemClick(language: Language) {
        if (intentTypeChoice == "above")
            intentDisplayFrom = language.language!!
        else
            intentDisplayTo = language.language!!

        val languageSaved = Saved("$intentTypeChoice.${language.language}", language.language, detectFlag(language.language!!),time , intentTypeChoice)
        languageViewModel.insert(languageSaved)
        saveRecent()

        if (intentAbort == "abort")
            useOneTime()
        else
            onBackPressed()
    }

    private fun getQuantities(){
        val number = languageViewModel.countRow()
        Log.d("abc", number.toString())
    }
}