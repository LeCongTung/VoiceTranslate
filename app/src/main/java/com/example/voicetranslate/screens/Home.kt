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
import android.widget.*
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

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

//        Init
        val btnOfflinePhraseBook: ImageButton = findViewById(R.id.nav_offlinePhrasebook)
        val btnCamera: ImageButton = findViewById(R.id.nav_camera)
//        val btn: ImageButton = findViewById(R.id.nav_another)
        val btnSetting: ImageButton = findViewById(R.id.nav_setting)

        val btnSpeakingBottomFrom: Button = findViewById(R.id.btn_speakFromBottom)
        val btnSpeakingBottomTo: Button = findViewById(R.id.btn_speakToBottom)
        val btnSpeakingBottom: ImageButton = findViewById(R.id.btn_speak)

//        Choice a language that you have
        val btnChoiceLanguageFrom: ImageButton = findViewById(R.id.btn_choicelanguagefrom)
        val valueLFrom: EditText = findViewById(R.id.valueFrom)
        val btnSpeakingLFrom: ImageButton = findViewById(R.id.btn_speakingFrom)

        val btnChoiceLanguageTo: ImageButton = findViewById(R.id.btn_choicelanguageTo)
        val valueLTo: TextView = findViewById(R.id.valueTo)
        val btnSpeakingLTo: ImageButton = findViewById(R.id.btn_speakingTo)

        val btnClear: ImageButton = findViewById(R.id.btn_clear)
        val btnMore: ImageButton = findViewById(R.id.btn_moreInfo)
        val btnSwap: ImageButton = findViewById(R.id.btn_swap)


//        Get text from image
        val value = intent.getStringExtra("value").toString()

        var intentDisplayFrom = intent.getStringExtra("displayFrom")

        var intentLanguageFrom = intent.getStringExtra("languageFrom").toString()
        var intentFlagFrom = intent.getIntExtra("flagFrom", R.drawable.ic_flag_english)

        var intentDisplayTo = intent.getStringExtra("displayTo")
        var intentLanguageTo = intent.getStringExtra("languageTo").toString()
        var intentFlagTo = intent.getIntExtra("flagTo", R.drawable.ic_flag_vietnamese)

//        Set up data for app
        if (intentDisplayFrom.equals(null)) {

            intentDisplayFrom = "English"
            intentDisplayTo = "Vietnamese"
            intentLanguageFrom = TranslateLanguage.ENGLISH
            intentLanguageTo = TranslateLanguage.VIETNAMESE
        }

//        Display value
        swapLanguage(intentDisplayTo, intentFlagTo, intentDisplayFrom, intentFlagFrom)

        if (!value.equals("null")) {
            valueLFrom.setText(value)

            translateValue(value, intentLanguageFrom, intentLanguageTo)
            btnClear.visibility = View.VISIBLE
            btnMore.visibility = View.VISIBLE
        }

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

        btnSetting.setOnClickListener {

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
        btnClear.setOnClickListener {

            valueLFrom.setText("")
            valueLTo.setText("")
        }

//        Swap between languages each other
        btnSwap.setOnClickListener {

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

            translateValue(valueLFrom.text.toString(), intentLanguageFrom, intentLanguageTo)
        }

//        Enter text you wanna translate
        valueLFrom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                btnSpeakingBottomFrom.setBackgroundResource(R.drawable.btn_speaking_default_left)
                btnSpeakingBottomFrom.setTextColor(Color.parseColor(colorWord))
                btnSpeakingBottomTo.setBackgroundResource(R.drawable.btn_speaking_default_right)
                btnSpeakingBottomTo.setTextColor(Color.parseColor(colorWord))
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {

                var value = valueLFrom.text.toString()
                if (value != "") {

                    btnClear.visibility = View.VISIBLE
                    btnMore.visibility = View.VISIBLE
                    translateValue(value, intentLanguageFrom, intentLanguageTo)

                } else {

                    valueLTo.setText("")
                    btnClear.visibility = View.INVISIBLE
                    btnMore.visibility = View.INVISIBLE
                }

//                btnSpeakingBottomFrom.setBackgroundResource(R.drawable.btn_speaking_default_left)
//                btnSpeakingBottomFrom.setTextColor(Color.parseColor(colorWord))
//                btnSpeakingBottomTo.setBackgroundResource(R.drawable.btn_speaking_default_right)
//                btnSpeakingBottomTo.setTextColor(Color.parseColor(colorWord))
            }
        })

//        Choose language
        btnChoiceLanguageFrom.setOnClickListener {

            val intent = Intent(this, ShowLanguage::class.java)
            intent.putExtra("typeChoice", "above")
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("value", valueLFrom.text.toString())
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        btnChoiceLanguageTo.setOnClickListener {

            val intent = Intent(this, ShowLanguage::class.java)
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("value", valueLFrom.text.toString())
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }


//        Click speaking button to speak
        btnSpeakingLFrom.setOnClickListener {

            TextSpeech(intentLanguageFrom, valueLFrom.text.toString())
        }

        btnSpeakingLTo.setOnClickListener {

            TextSpeech(intentLanguageTo, valueLTo.text.toString())
        }

        btnSpeakingBottom.setOnClickListener {

            btnSpeakingBottomFrom.setBackgroundResource(R.drawable.btn_speaking_left)
            btnSpeakingBottomFrom.setTextColor(Color.parseColor(colorWhite))
            btnSpeakingBottomTo.setBackgroundResource(R.drawable.btn_speaking_right)
            btnSpeakingBottomTo.setTextColor(Color.parseColor(colorWhite))

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

        if (requestCode == RECORD_SPEECH_TEXT && resultCode == Activity.RESULT_OK) {

            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            valueFrom.setText(result?.get(0).toString())
        }
    }

    //    Translate
    private fun translateValue(value: String, typeFrom: String, typeTo: String) {

        val valueLTo: TextView = findViewById(R.id.valueTo)
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(typeFrom)
            .setTargetLanguage(typeTo)
            .build()
        val translator = Translation.getClient(options)
        translator.downloadModelIfNeeded().addOnSuccessListener {

            translator.translate(value).addOnSuccessListener {

                valueLTo.setText(it)
            }.addOnFailureListener {}
        }.addOnFailureListener {

            show("Lost conncection")
        }
    }

    //    Swap between languages
    private fun swapLanguage(displayFrom: String?, flagFrom: Int, displayTo: String?, flagTo: Int) {

        val flagLFrom: ImageView = findViewById(R.id.flagLanguageFrom)
        val displayNameLFrom: TextView = findViewById(R.id.displayLanguageFrom)
        val displayNameLFromBottom: TextView = findViewById(R.id.btn_speakFromBottom)
        val flagLTo: ImageView = findViewById(R.id.flagLanguageTo)
        val displayNameLTo: TextView = findViewById(R.id.displayLanguageTo)
        val displayNameLToBottom: TextView = findViewById(R.id.btn_speakToBottom)

        displayNameLFrom.setText(displayTo)
        displayNameLFromBottom.setText(displayTo)
        flagLFrom.setImageResource(flagTo)

        displayNameLTo.setText(displayFrom)
        displayNameLToBottom.setText(displayFrom)
        flagLTo.setImageResource(flagFrom)
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
                speaker.setSpeechRate(1.0f)
                speaker.speak(value, TextToSpeech.QUEUE_ADD, null)
            }
        })
    }
}

