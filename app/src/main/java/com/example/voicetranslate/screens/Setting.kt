package com.example.voicetranslate.screens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.voicetranslate.R
import kotlinx.android.synthetic.main.activity_setting.*

class Setting : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

//        Get data
        val value = intent.getStringExtra("value").toString()

        var intentDisplayFrom = intent.getStringExtra("displayFrom")

        var intentLanguageFrom = intent.getStringExtra("languageFrom").toString()
        var intentFlagFrom = intent.getIntExtra("flagFrom", R.drawable.ic_flag_english)

        var intentDisplayTo = intent.getStringExtra("displayTo")
        var intentLanguageTo = intent.getStringExtra("languageTo").toString()
        var intentFlagTo = intent.getIntExtra("flagTo", R.drawable.ic_flag_vietnamese)

//        Excute event -- when click button
        btn_close.setOnClickListener {

            val intent = Intent(this, Home::class.java)
            intent.putExtra("value", value)
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("languageFrom", intentLanguageFrom)
            intent.putExtra("flagFrom", intentFlagFrom)

            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("languageTo", intentLanguageTo)
            intent.putExtra("flagTo", intentFlagTo)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }
    }

//    Function -- Click back button to close app
    override fun onBackPressed() {

        val value = intent.getStringExtra("value").toString()

        var intentDisplayFrom = intent.getStringExtra("displayFrom")
        var intentLanguageFrom = intent.getStringExtra("languageFrom").toString()
        var intentFlagFrom = intent.getIntExtra("flagFrom", R.drawable.ic_flag_english)

        var intentDisplayTo = intent.getStringExtra("displayTo")
        var intentLanguageTo = intent.getStringExtra("languageTo").toString()
        var intentFlagTo = intent.getIntExtra("flagTo", R.drawable.ic_flag_vietnamese)

        val intent = Intent(this, Home::class.java)
        intent.putExtra("value", value)
        intent.putExtra("displayFrom", intentDisplayFrom)
        intent.putExtra("languageFrom", intentLanguageFrom)
        intent.putExtra("flagFrom", intentFlagFrom)

        intent.putExtra("displayTo", intentDisplayTo)
        intent.putExtra("languageTo", intentLanguageTo)
        intent.putExtra("flagTo", intentFlagTo)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
    }
}