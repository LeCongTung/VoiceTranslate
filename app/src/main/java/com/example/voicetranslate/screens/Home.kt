package com.example.voicetranslate.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voicetranslate.R
import com.example.voicetranslate.shows.ShowImage
import com.example.voicetranslate.shows.ShowLanguage
import com.example.voicetranslate.shows.ShowOfflinePhraseBook
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class Home : AppCompatActivity() {

    //    Camera
    private val RECORD_SPEECH_TEXT = 102

    //    Value
    lateinit var speaker: TextToSpeech
    var backPressedTime: Long = 0
    val colorWhite = "#FFFFFF"
    val colorWord = "#262C30"

    lateinit var value: String
    lateinit var intentDisplayFrom: String
    lateinit var intentLanguageFrom: String
    var intentFlagFrom: Int = 0
    lateinit var intentDisplayTo: String
    lateinit var intentLanguageTo: String
    var intentFlagTo: Int = 0

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

//        Get text from image
        value = intent.getStringExtra("value").toString()
        intentDisplayFrom = intent.getStringExtra("displayFrom").toString()
        intentLanguageFrom = intent.getStringExtra("languageFrom").toString()
        intentFlagFrom = intent.getIntExtra("flagFrom", R.drawable.ic_flag_english)
        intentDisplayTo = intent.getStringExtra("displayTo").toString()
        intentLanguageTo = intent.getStringExtra("languageTo").toString()
        intentFlagTo = intent.getIntExtra("flagTo", R.drawable.ic_flag_vietnamese)

//        Set up data for app
        if (intentDisplayFrom.equals("null")) {

            intentDisplayFrom = "English"
            intentDisplayTo = "Vietnamese"
            intentLanguageFrom = TranslateLanguage.ENGLISH
            intentLanguageTo = TranslateLanguage.VIETNAMESE
        }

//        Display value
        swapLanguage(intentDisplayTo, intentFlagTo, intentDisplayFrom, intentFlagFrom)

        if (!value.equals("null")) {
            valueFrom.setText(value)

            translateValue(value, intentLanguageFrom, intentLanguageTo)
            btn_clear.visibility = View.VISIBLE
            btn_moreInfo.visibility = View.VISIBLE
        }

//        _________________________________________________________________Excute event -- when click button
//        ===========Navigation
        nav_offlinePhrasebook.setOnClickListener {

            val intent = Intent(this, ShowOfflinePhraseBook::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        nav_camera.setOnClickListener {

            val intent = Intent(this, ShowImage::class.java)
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

        nav_setting.setOnClickListener {

            val intent = Intent(this, Setting::class.java)
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

//        Content
//        ===========Translate
        btn_clear.setOnClickListener {

            valueFrom.setText("")
            valueTo.setText("")
        }

//        Swap between languages each other
        btn_swap.setOnClickListener {

            swapLanguage(intentDisplayFrom, intentFlagFrom, intentDisplayTo, intentFlagTo)

            var dataDisplay = intentDisplayFrom
            intentDisplayFrom = intentDisplayTo
            intentDisplayTo = dataDisplay

            var dataFlag = intentFlagFrom.toString()
            intentFlagFrom = intentFlagTo
            intentFlagTo = dataFlag.toInt()

            var dataSave = intentLanguageFrom
            intentLanguageFrom = intentLanguageTo
            intentLanguageTo = dataSave

            translateValue(valueFrom.text.toString(), intentLanguageFrom, intentLanguageTo)
        }

//        Enter text you wanna translate
        valueFrom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                btn_speakFromBottom.setBackgroundResource(R.drawable.btn_speaking_default_left)
                btn_speakFromBottom.setTextColor(Color.parseColor(colorWord))
                btn_speakToBottom.setBackgroundResource(R.drawable.btn_speaking_default_right)
                btn_speakToBottom.setTextColor(Color.parseColor(colorWord))
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {

                var value = valueFrom.text.toString()
                if (value != "") {

                    btn_clear.visibility = View.VISIBLE
                    btn_moreInfo.visibility = View.VISIBLE
                    translateValue(value, intentLanguageFrom, intentLanguageTo)

                } else {

                    valueTo.setText("")
                    btn_clear.visibility = View.INVISIBLE
                    btn_moreInfo.visibility = View.INVISIBLE
                }

//                btnSpeakingBottomFrom.setBackgroundResource(R.drawable.btn_speaking_default_left)
//                btnSpeakingBottomFrom.setTextColor(Color.parseColor(colorWord))
//                btnSpeakingBottomTo.setBackgroundResource(R.drawable.btn_speaking_default_right)
//                btnSpeakingBottomTo.setTextColor(Color.parseColor(colorWord))
            }
        })

//        Choose language
        btn_choicelanguagefrom.setOnClickListener {

            val intent = Intent(this, ShowLanguage::class.java)
            intent.putExtra("typeChoice", "above")
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("value", valueFrom.text.toString())
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        btn_choicelanguageTo.setOnClickListener {

            val intent = Intent(this, ShowLanguage::class.java)
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("value", valueFrom.text.toString())
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }


//        Click speaking button to speak
        btn_speakingFrom.setOnClickListener {

            TextSpeech(intentLanguageFrom, valueFrom.text.toString())
        }

        btn_speakingTo.setOnClickListener {

            TextSpeech(intentLanguageTo, valueTo.text.toString())
        }

        btn_speak.setOnClickListener {

            btn_speakFromBottom.setBackgroundResource(R.drawable.btn_speaking_left)
            btn_speakFromBottom.setTextColor(Color.parseColor(colorWhite))
            btn_speakToBottom.setBackgroundResource(R.drawable.btn_speaking_right)
            btn_speakToBottom.setTextColor(Color.parseColor(colorWhite))

            SpeechText(intentDisplayFrom)
        }
    }


    //    Function -- Click back button to close app
    override fun onBackPressed() {

        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity();
        } else {
            show("Press back again to leave the app")
        }
        backPressedTime = System.currentTimeMillis()
    }

    //    Function -- Toast
    private fun show(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    //    Function -- Check permission to use camera
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when {
//            requestCode == 999 && resultCode == Activity.RESULT_OK -> {
//                data?.let {
//                    intentDisplayFrom = data.getStringExtra("displayFrom").toString()
//                    intentLanguageFrom = data.getStringExtra("languageFrom").toString()
//                    intentFlagFrom = data.getIntExtra("flagFrom", 0)
//
//                    intentDisplayTo = data.getStringExtra("displayTo").toString()
//                    intentLanguageTo = data.getStringExtra("languageTo").toString()
//                    intentFlagTo = data.getIntExtra("flagTo", 0)
//
//                    swapLanguage(intentDisplayTo, intentFlagTo, intentDisplayFrom, intentFlagFrom)
//                }
//            }
            requestCode == RECORD_SPEECH_TEXT && resultCode == Activity.RESULT_OK -> {
                val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                valueFrom.setText(result?.get(0).toString())}
        }
    }

    //    Translate
    private fun translateValue(value: String, typeFrom: String, typeTo: String) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(typeFrom)
            .setTargetLanguage(typeTo)
            .build()
        val translator = Translation.getClient(options)
        translator.downloadModelIfNeeded().addOnSuccessListener {

            translator.translate(value).addOnSuccessListener {

                valueTo.setText(it)
            }.addOnFailureListener {}
        }.addOnFailureListener {

            show("Lost conncection")
        }
    }

    //    Swap between languages
    private fun swapLanguage(displayFrom: String?, flagFrom: Int, displayTo: String?, flagTo: Int) {
        displayLanguageFrom.setText(displayTo)
        btn_speakFromBottom.setText(displayTo)
        flagLanguageFrom.setImageResource(flagTo)

        displayLanguageTo.setText(displayFrom)
        btn_speakToBottom.setText(displayFrom)
        flagLanguageTo.setImageResource(flagFrom)
    }

    //    Speech to text
    private fun SpeechText(voice: String?) {

        if (!SpeechRecognizer.isRecognitionAvailable(this))

            show("Error")
        else{

            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.forLanguageTag(voice))
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something in " + Locale.forLanguageTag(voice) + " !")
            startActivityForResult(i, RECORD_SPEECH_TEXT)
        }
    }

    //    Text Speech
    private fun TextSpeech(voice: String, value: String) {

        speaker = TextToSpeech(applicationContext, {

            if (it == TextToSpeech.SUCCESS) {

                speaker.language = Locale.forLanguageTag(voice)
                speaker.setSpeechRate(1.2f)
                speaker.speak(value, TextToSpeech.QUEUE_ADD, null)
            }
        })
    }
}

