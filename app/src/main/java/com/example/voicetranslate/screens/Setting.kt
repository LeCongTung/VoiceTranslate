package com.example.voicetranslate.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.voicetranslate.R
import kotlinx.android.synthetic.main.activity_setting.*

class Setting : AppCompatActivity() {

    lateinit var value: String
    lateinit var intentDisplayFrom: String
    lateinit var intentLanguageFrom: String
    var intentFlagFrom: Int = 0
    lateinit var intentDisplayTo: String
    lateinit var intentLanguageTo: String
    var intentFlagTo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

//        Get data
        value = intent.getStringExtra("value").toString()
        intentDisplayFrom = intent.getStringExtra("displayFrom").toString()
        intentLanguageFrom = intent.getStringExtra("languageFrom").toString()
        intentFlagFrom = intent.getIntExtra("flagFrom", R.drawable.ic_flag_english)

        intentDisplayTo = intent.getStringExtra("displayTo").toString()
        intentLanguageTo = intent.getStringExtra("languageTo").toString()
        intentFlagTo = intent.getIntExtra("flagTo", R.drawable.ic_flag_vietnamese)

//        Excute event -- when click button
        btn_close.setOnClickListener {

            onBackPressed()
        }
    }

//    Function -- Click back button to close app
    override fun onBackPressed() {

        finish()
        overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
    }
}