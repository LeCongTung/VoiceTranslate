package com.example.voicetranslate.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.example.voicetranslate.extensions.showKeyboard
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
    private lateinit var speaker: TextToSpeech
    private var backPressedTime: Long = 0
    private val colorWhite = "#FFFFFF"
    private val colorWord = "#262C30"

    private lateinit var value: String
    private lateinit var intentDisplayFrom: String
    private lateinit var intentLanguageFrom: String
    private var intentFlagFrom: Int = 0
    private lateinit var intentDisplayTo: String
    private lateinit var intentLanguageTo: String
    private var intentFlagTo: Int = 0

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
        if (intentDisplayFrom == "null") {

            intentDisplayFrom = "English"
            intentDisplayTo = "Vietnamese"
            intentLanguageFrom = TranslateLanguage.ENGLISH
            intentLanguageTo = TranslateLanguage.VIETNAMESE
        }

//        Display value
        swapLanguage(intentDisplayTo, intentFlagTo, intentDisplayFrom, intentFlagFrom)
        if (value != "null" && value != "") {
            valueFrom.setText(value)
            translateValue(value, intentLanguageFrom, intentLanguageTo)
            btn_clear.visibility = View.VISIBLE
            btn_copy.visibility = View.VISIBLE
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
//        Swap between languages each other
        btn_swap.setOnClickListener {
            if (::speaker.isInitialized) {
                speaker.stop()
            }
            swapLanguage(intentDisplayFrom, intentFlagFrom, intentDisplayTo, intentFlagTo)

            val dataDisplay = intentDisplayFrom
            intentDisplayFrom = intentDisplayTo
            intentDisplayTo = dataDisplay

            val dataFlag = intentFlagFrom.toString()
            intentFlagFrom = intentFlagTo
            intentFlagTo = dataFlag.toInt()

            val dataSave = intentLanguageFrom
            intentLanguageFrom = intentLanguageTo
            intentLanguageTo = dataSave

            translateValue(
                valueFrom.text.toString(),
                intentLanguageFrom,
                intentLanguageTo
            )
        }

        formFrom.setOnClickListener {

            showKeyboard(valueFrom, this)
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
            textSpeech(intentLanguageFrom, valueFrom.text.toString())
        }

        btn_speakingTo.setOnClickListener {
            if (::speaker.isInitialized) {
                speaker.stop()
            }
            textSpeech(intentLanguageTo, valueTo.text.toString())
        }

        btn_speak.setOnClickListener {
            if (::speaker.isInitialized) {
                speaker.stop()
            }
            btn_speakFromBottom.setBackgroundResource(R.drawable.btn_speaking_left)
            btn_speakFromBottom.setTextColor(Color.parseColor(colorWhite))
            btn_speakToBottom.setBackgroundResource(R.drawable.btn_speaking_right)
            btn_speakToBottom.setTextColor(Color.parseColor(colorWhite))
            speechText(intentLanguageFrom, intentDisplayFrom)
        }

        btn_clear.setOnClickListener {
            if (::speaker.isInitialized) {
                speaker.stop()
            }
            valueFrom.setText("")
            valueTo.visibility = View.INVISIBLE
            btn_clear.visibility = View.INVISIBLE
            btn_copy.visibility = View.INVISIBLE
        }

        btn_copy.setOnClickListener{

            copyText()
        }

        valueFrom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (::speaker.isInitialized) {
                    speaker.stop()
                }

                valueTo.visibility = View.VISIBLE
                val valueget = valueFrom.text.toString()
                if (valueget == "") {
                    valueTo.visibility = View.INVISIBLE
                    btn_clear.visibility = View.INVISIBLE
                    btn_copy.visibility = View.INVISIBLE
                } else {
                    btn_clear.visibility = View.VISIBLE
                    btn_copy.visibility = View.VISIBLE
                    translateValue(valueget, intentLanguageFrom, intentLanguageTo)
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
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

                translateValue(
                    valueFrom.text.toString(),
                    intentLanguageFrom,
                    intentLanguageTo
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
    ) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(typeFrom)
            .setTargetLanguage(typeTo)
            .build()
        val translator = Translation.getClient(options)
        translator.downloadModelIfNeeded().addOnSuccessListener {
            translator.translate(value).addOnSuccessListener {
                valueTo.text = it
            }.addOnFailureListener {}
        }.addOnFailureListener {

            show("Lost conncection")
        }
    }

    //    Swap between languages
    private fun swapLanguage(displayFrom: String?, flagFrom: Int, displayTo: String?, flagTo: Int) {
        displayLanguageFrom.text = displayTo
        btn_speakFromBottom.text = displayTo
        flagLanguageFrom.setImageResource(flagTo)

        displayLanguageTo.text = displayFrom
        btn_speakToBottom.text = displayFrom
        flagLanguageTo.setImageResource(flagFrom)
    }

    //    Speech to text
    private fun speechText(voice: String?, display: String) {

        if (!SpeechRecognizer.isRecognitionAvailable(this))

            show("Error")
        else {

            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.forLanguageTag(voice.toString()))
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something in $display !")
            startActivityForResult(i, RECORD_SPEECH_TEXT)
        }
    }

    //    Text Speech
    private fun textSpeech(voice: String, value: String) {

        speaker = TextToSpeech(applicationContext) {

            if (it == TextToSpeech.SUCCESS) {

                speaker.language = Locale.forLanguageTag(voice)
                speaker.setSpeechRate(1.0f)
                speaker.speak(value, TextToSpeech.QUEUE_ADD, null)
            }
        }
    }

    private fun copyText() {
        val text = valueTo.text.toString()
        var clipboardManager: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("key", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(applicationContext, "Copied", Toast.LENGTH_SHORT).show()

    }
}

