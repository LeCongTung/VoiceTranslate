package com.example.voicetranslate.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.voicetranslate.R
import com.example.voicetranslate.shows.ShowImageActivity
import com.example.voicetranslate.shows.ShowLanguageActivity
import com.example.voicetranslate.shows.ShowOfflinePhraseBookActivity
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class HomeActivity : AppCompatActivity() {

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
    var isTranslate: Boolean = false

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
        if (!value.equals("null") && !value.equals("")) {
            isTranslate = true
            valueFrom.setText(value)
            translateValue(value, intentLanguageFrom, intentLanguageTo, isTranslate)
            btn_clear.visibility = View.VISIBLE
            btn_moreInfo.visibility = View.VISIBLE
        }
//        _________________________________________________________________Excute event -- when click button
//        ===========Navigation
        nav_offlinePhrasebook.setOnClickListener {
            val intent = Intent(this, ShowOfflinePhraseBookActivity::class.java)
            intent.putExtra("value", valueFrom.text.toString())
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

        nav_camera.setOnClickListener {
            val intent = Intent(this, ShowImageActivity::class.java)
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
            val intent = Intent(this, SettingActivity::class.java)
            intent.putExtra("value", valueFrom.text.toString())
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("languageFrom", intentLanguageFrom)
            intent.putExtra("flagFrom", intentFlagFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("languageTo", intentLanguageTo)
            intent.putExtra("flagTo", intentFlagTo)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }
//        Content
//        ===========Translate
        btn_clear.setOnClickListener {
            if (::speaker.isInitialized) {
                speaker.stop()
            }
            valueFrom.setText("")
            valueTo.visibility = View.INVISIBLE
            btn_clear.visibility = View.INVISIBLE
            btn_moreInfo.visibility = View.INVISIBLE
        }

//        Swap between languages each other
        btn_swap.setOnClickListener {
            if (::speaker.isInitialized) {
                speaker.stop()
            }
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

            isTranslate = true
            translateValue(
                valueFrom.text.toString(),
                intentLanguageFrom,
                intentLanguageTo,
                isTranslate
            )
        }
//        Enter text you wanna translate
        valueFrom.doAfterTextChanged {
            if (::speaker.isInitialized) {
                speaker.stop()
            }

            var value = valueFrom.text.toString()
            valueTo.visibility = View.VISIBLE
            if (value == "") {
                valueTo.visibility = View.INVISIBLE
                btn_clear.visibility = View.INVISIBLE
                btn_moreInfo.visibility = View.INVISIBLE
                isTranslate = false
                translateValue(value, intentLanguageFrom, intentLanguageTo, isTranslate)
            } else {
                btn_clear.visibility = View.VISIBLE
                btn_moreInfo.visibility = View.VISIBLE
                isTranslate = true
                translateValue(value, intentLanguageFrom, intentLanguageTo, isTranslate)
            }
        }

//        Choose language
        btn_choicelanguagefrom.setOnClickListener {
            if (::speaker.isInitialized) {
                speaker.stop()
            }
            val intent = Intent(this, ShowLanguageActivity::class.java)
            intent.putExtra("typeChoice", "above")
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("value", valueFrom.text.toString())
            startActivityForResult(intent, 999)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        btn_choicelanguageTo.setOnClickListener {
            if (::speaker.isInitialized) {
                speaker.stop()
            }
            val intent = Intent(this, ShowLanguageActivity::class.java)
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("value", valueFrom.text.toString())
            startActivityForResult(intent, 999)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }


//        Click speaking button to speak
        btn_speakingFrom.setOnClickListener {
            if (::speaker.isInitialized) {
                speaker.stop()
            }
            TextSpeech(intentLanguageFrom, valueFrom.text.toString())
        }

        btn_speakingTo.setOnClickListener {
            if (::speaker.isInitialized) {
                speaker.stop()
            }
            TextSpeech(intentLanguageTo, valueTo.text.toString())
        }

        btn_speak.setOnClickListener {
            if (::speaker.isInitialized) {
                speaker.stop()
            }
            btn_speakFromBottom.setBackgroundResource(R.drawable.btn_speaking_left)
            btn_speakFromBottom.setTextColor(Color.parseColor(colorWhite))
            btn_speakToBottom.setBackgroundResource(R.drawable.btn_speaking_right)
            btn_speakToBottom.setTextColor(Color.parseColor(colorWhite))
            SpeechText(intentLanguageFrom, intentDisplayFrom)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::speaker.isInitialized) {
            speaker.stop()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::speaker.isInitialized) {
            speaker.stop()
        }
    }

    //    Function -- Click back button to close app
    override fun onBackPressed() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            finish()
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
            requestCode == 999 && resultCode == Activity.RESULT_OK -> {
                data?.let {
                    intentDisplayFrom = data.getStringExtra("displayFrom").toString()
                    intentLanguageFrom = data.getStringExtra("languageFrom").toString()
                    intentFlagFrom = data.getIntExtra("flagFrom", 0)

                    intentDisplayTo = data.getStringExtra("displayTo").toString()
                    intentLanguageTo = data.getStringExtra("languageTo").toString()
                    intentFlagTo = data.getIntExtra("flagTo", 0)

                    swapLanguage(intentDisplayTo, intentFlagTo, intentDisplayFrom, intentFlagFrom)
                }

                isTranslate = true
                translateValue(
                    valueFrom.text.toString(),
                    intentLanguageFrom,
                    intentLanguageTo,
                    isTranslate
                )
            }
            requestCode == RECORD_SPEECH_TEXT && resultCode == Activity.RESULT_OK -> {
                val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                valueFrom.setText(result?.get(0).toString())
                btn_speakFromBottom.setBackgroundResource(R.drawable.btn_speaking_default_left)
                btn_speakFromBottom.setTextColor(Color.parseColor(colorWord))
                btn_speakToBottom.setBackgroundResource(R.drawable.btn_speaking_default_right)
                btn_speakToBottom.setTextColor(Color.parseColor(colorWord))
            }
            requestCode != RECORD_SPEECH_TEXT || resultCode != Activity.RESULT_OK -> {
                btn_speakFromBottom.setBackgroundResource(R.drawable.btn_speaking_default_left)
                btn_speakFromBottom.setTextColor(Color.parseColor(colorWord))
                btn_speakToBottom.setBackgroundResource(R.drawable.btn_speaking_default_right)
                btn_speakToBottom.setTextColor(Color.parseColor(colorWord))
            }
        }
    }

    //    Translate
    private fun translateValue(
        value: String,
        typeFrom: String,
        typeTo: String,
        isTranslate: Boolean
    ) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(typeFrom)
            .setTargetLanguage(typeTo)
            .build()
        val translator = Translation.getClient(options)
        translator.downloadModelIfNeeded().addOnSuccessListener {
            if (isTranslate.equals(false))
                translator.close()
            else {
                translator.translate(value).addOnSuccessListener {
                    valueTo.setText(it)
                }.addOnFailureListener {}
            }
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
    private fun SpeechText(voice: String?, display: String) {

        if (!SpeechRecognizer.isRecognitionAvailable(this))

            show("Error")
        else {

            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.forLanguageTag(voice))
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something in " + display + " !")
            startActivityForResult(i, RECORD_SPEECH_TEXT)
        }
    }

    //    Text Speech
    private fun TextSpeech(voice: String, value: String) {

        speaker = TextToSpeech(applicationContext, {

            if (it == TextToSpeech.SUCCESS) {

                speaker.language = Locale.forLanguageTag(voice)
                speaker.setSpeechRate(1.0f)
                speaker.speak(value, TextToSpeech.QUEUE_ADD, null)
            }
        })
    }


}

