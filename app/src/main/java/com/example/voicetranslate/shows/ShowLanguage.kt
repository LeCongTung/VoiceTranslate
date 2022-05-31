package com.example.voicetranslate.shows

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voicetranslate.R
import com.example.voicetranslate.adapters.AdapterLanguage
import com.example.voicetranslate.models.Language
import com.example.voicetranslate.screens.Home
import com.example.voicetranslate.screens.Setting
import com.google.mlkit.nl.translate.TranslateLanguage

class ShowLanguage : AppCompatActivity() {

    //    Value
    var languageChange = ""

    //    Array
    val arrayLanguage = arrayOf(
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

    val imageShow = arrayOf(
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

    val getLanguage = arrayOf(
        TranslateLanguage.AFRIKAANS,
        TranslateLanguage.BELARUSIAN,
        TranslateLanguage.BULGARIAN,
        TranslateLanguage.BENGALI,
        TranslateLanguage.CATALAN,
        TranslateLanguage.CHINESE,
        TranslateLanguage.CZECH,
        TranslateLanguage.WELSH,
        TranslateLanguage.DANISH,
        TranslateLanguage.GERMAN,
        TranslateLanguage.GREEK,
        TranslateLanguage.ENGLISH,
        TranslateLanguage.SPANISH,
        TranslateLanguage.FINNISH,
        TranslateLanguage.FRENCH,
        TranslateLanguage.IRISH,
        TranslateLanguage.HINDI,
        TranslateLanguage.HUNGARIAN,
        TranslateLanguage.INDONESIAN,
        TranslateLanguage.ITALIAN,
        TranslateLanguage.JAPANESE,
        TranslateLanguage.KOREAN,
        TranslateLanguage.DUTCH,
        TranslateLanguage.POLISH,
        TranslateLanguage.PORTUGUESE,
        TranslateLanguage.RUSSIAN,
        TranslateLanguage.SWEDISH,
        TranslateLanguage.THAI,
        TranslateLanguage.TURKISH,
        TranslateLanguage.VIETNAMESE
    )

    val newArrayList: ArrayList<Language> = arrayListOf()
    var searchArrayList: ArrayList<Language> = arrayListOf()
    var filteredNames = ArrayList<Language>()

    val myAdapter: AdapterLanguage by lazy { AdapterLanguage(newArrayList) } //Chi khoi tao khi duoc goi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_language)

//        Init
        val btnOfflinePhraseBook: ImageButton = findViewById(R.id.nav_offlinePhrasebook)
        val btnCamera: ImageButton = findViewById(R.id.nav_camera)
        val btnSetting: ImageButton = findViewById(R.id.nav_setting)

        val btnClose: ImageButton = findViewById(R.id.btn_close)

        val listItem: RecyclerView = findViewById(R.id.list_item)
        val etSearch: EditText = findViewById(R.id.et_search)

//        Get data
        val byteArray = intent.getByteArrayExtra("image")

        val fromLayout = intent.getStringExtra("from").toString()
        val value = intent.getStringExtra("value")
        val intentDisplayFrom = intent.getStringExtra("displayFrom").toString()
        val intentDisplayTo = intent.getStringExtra("displayTo").toString()

//        Format recycleview language
        listItem.layoutManager = LinearLayoutManager(this)
        listItem.setHasFixedSize(true)
        getData()

        //        _________________________________________________________________Excute event -- when click button
//        ===========Navigation
        btnOfflinePhraseBook.setOnClickListener {

            val intent = Intent(this, ShowOfflinePhraseBook::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        btnCamera.setOnClickListener {

            val intent = Intent(this, ShowImage::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        btnSetting.setOnClickListener {

            val intent = Intent(this, Setting::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        btnClose.setOnClickListener {

            if (fromLayout.equals("camera")) {

                val intent = Intent(this@ShowLanguage, ShowImage::class.java)
                intent.putExtra("image", byteArray)

                intent.putExtra("displayFrom", intentDisplayFrom)
                intent.putExtra("languageFrom", detectLanguage(intentDisplayFrom))
                intent.putExtra("flagFrom", detectFlag(intentDisplayFrom))

                intent.putExtra("displayTo", intentDisplayTo)
                intent.putExtra("languageTo", detectLanguage(intentDisplayTo))
                intent.putExtra("flagTo", detectFlag(intentDisplayTo))

                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
            } else {

                val intent = Intent(this@ShowLanguage, Home::class.java)
                intent.putExtra("value", value)

                intent.putExtra("displayFrom", intentDisplayFrom)
                intent.putExtra("languageFrom", detectLanguage(intentDisplayFrom))
                intent.putExtra("flagFrom", detectFlag(intentDisplayFrom))

                intent.putExtra("displayTo", intentDisplayTo)
                intent.putExtra("languageTo", detectLanguage(intentDisplayTo))
                intent.putExtra("flagTo", detectFlag(intentDisplayTo))

                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
            }
        }


        val valueSearch = etSearch.text.toString()
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

                filters(p0.toString())
                if (valueSearch.equals(""))
                    getData()
            }
        })
    }

    //    Show data to recycleview
    private fun getData() {

        val intentTypeChoice = intent.getStringExtra("typeChoice").toString()

        val fromLayout = intent.getStringExtra("from").toString()
        val value = intent.getStringExtra("value").toString()
        val byteArray = intent.getByteArrayExtra("image")

        var intentDisplayFrom = intent.getStringExtra("displayFrom").toString()
        var intentDisplayTo = intent.getStringExtra("displayTo").toString()

        newArrayList.clear()
        val listItem: RecyclerView = findViewById(R.id.list_item)

        for (i in arrayLanguage.indices) {

            val language = Language(arrayLanguage[i], imageShow[i])
            newArrayList.add(language)
        }

        searchArrayList = newArrayList
        listItem.adapter = myAdapter

        myAdapter.setOnItemClickListener(object : AdapterLanguage.onItemClickListener {
            override fun onItemClick(position: Int) {

                if (filteredNames.size == 0)
                    languageChange = searchArrayList[position].language.toString()
                else
                    languageChange = filteredNames[position].language.toString()

                if (intentTypeChoice.equals("above"))
                    intentDisplayFrom = languageChange
                else
                    intentDisplayTo = languageChange

                if (fromLayout.equals("camera")) {

                    val intent = Intent(this@ShowLanguage, ShowImage::class.java)
                    intent.putExtra("image", byteArray)

                    intent.putExtra("displayFrom", intentDisplayFrom)
                    intent.putExtra("languageFrom", detectLanguage(intentDisplayFrom))
                    intent.putExtra("flagFrom", detectFlag(intentDisplayFrom))

                    intent.putExtra("displayTo", intentDisplayTo)
                    intent.putExtra("languageTo", detectLanguage(intentDisplayTo))
                    intent.putExtra("flagTo", detectFlag(intentDisplayTo))

                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
                } else {

                    val intent = Intent(this@ShowLanguage, Home::class.java)
                    intent.putExtra("value", value)

                    intent.putExtra("displayFrom", intentDisplayFrom)
                    intent.putExtra("languageFrom", detectLanguage(intentDisplayFrom))
                    intent.putExtra("flagFrom", detectFlag(intentDisplayFrom))

                    intent.putExtra("displayTo", intentDisplayTo)
                    intent.putExtra("languageTo", detectLanguage(intentDisplayTo))
                    intent.putExtra("flagTo", detectFlag(intentDisplayTo))

                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
                }
            }
        })
    }

    //    Search data
    private fun filters(text: String) {

        filteredNames.clear()
        searchArrayList.filterTo(filteredNames) {

            it.language.toString().lowercase().contains(text.lowercase())
        }
        myAdapter.filterList(filteredNames)
    }

    //    Function -- Detect a language
    private fun detectLanguage(language: String): String {

        var languageType = ""
        for (i in 0..arrayLanguage.size - 1)
            if (arrayLanguage[i].equals(language)) {

                languageType = getLanguage[i]
            }
        return languageType
    }

    //    Fuction  -- Detect flag
    private fun detectFlag(language: String): Int {

        var flag = 0
        for (i in 0..arrayLanguage.size - 1)
            if (arrayLanguage[i].equals(language)) {

                flag = imageShow[i]
            }
        return flag
    }

    //    Function -- Toast
    private fun show(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    //    Function -- Click back button to close app
    override fun onBackPressed() {

        val fromLayout = intent.getStringExtra("from").toString()
        val value = intent.getStringExtra("value")
        val byteArray = intent.getByteArrayExtra("image")

        val intentDisplayFrom = intent.getStringExtra("displayFrom").toString()
        val intentDisplayTo = intent.getStringExtra("displayTo").toString()

        if (fromLayout.equals("camera")) {

            val intent = Intent(this@ShowLanguage, ShowImage::class.java)
            intent.putExtra("image", byteArray)

            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("languageFrom", detectLanguage(intentDisplayFrom))
            intent.putExtra("flagFrom", detectFlag(intentDisplayFrom))

            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("languageTo", detectLanguage(intentDisplayTo))
            intent.putExtra("flagTo", detectFlag(intentDisplayTo))

            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        } else {

            val intent = Intent(this@ShowLanguage, Home::class.java)
            intent.putExtra("value", value)

            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("languageFrom", detectLanguage(intentDisplayFrom))
            intent.putExtra("flagFrom", detectFlag(intentDisplayFrom))

            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("languageTo", detectLanguage(intentDisplayTo))
            intent.putExtra("flagTo", detectFlag(intentDisplayTo))

            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }
    }
}