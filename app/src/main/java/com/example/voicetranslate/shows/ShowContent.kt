package com.example.voicetranslate.shows

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.voicetranslate.R

class ShowContent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_content)

//        Init
        val btnBack: TextView = findViewById(R.id.btn_back)
        val tvTitle: TextView = findViewById(R.id.title_content)
        val btnDone: TextView = findViewById(R.id.btn_done)

//        Excute button -- when click button
        btnBack.setOnClickListener {

            val intent = Intent(this, ShowOfflinePhraseBook::class.java)
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