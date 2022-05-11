package com.example.voicetranslate.shows

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.voicetranslate.R

class ShowOfflinePhraseBook : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_offline_phrase_book)

//        Init
        val btnDone: TextView = findViewById(R.id.btn_done)

//        Excute event -- when click button
        btnDone.setOnClickListener {

            val intent = Intent(this, ShowContent::class.java)
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
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

}