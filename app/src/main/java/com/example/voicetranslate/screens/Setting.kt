package com.example.voicetranslate.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import com.example.voicetranslate.R

class Setting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

//        Init
        val btnClose: ImageButton = findViewById(R.id.btn_close)

//        Excute event -- when click button
        btnClose.setOnClickListener {

            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }
    }

//    Function -- Click back button to close app
    override fun onBackPressed() {

        super.onBackPressed();
        finishAffinity();
    }

//    Function -- Toast
    private fun show(message: String) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

}