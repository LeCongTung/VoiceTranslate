package com.example.voicetranslate.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.example.voicetranslate.R
import com.example.voicetranslate.shows.ShowImage
import com.example.voicetranslate.shows.ShowOfflinePhraseBook
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateLanguage.*
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class Home : AppCompatActivity() {

//    Camera
    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2
    private val RECORD_SPEECH_TEXT = 102

    var languageTypeFrom = TranslateLanguage.ENGLISH
    var languageTypeTo = TranslateLanguage.VIETNAMESE

//    Value
    val arrayLanguage = arrayOf("English", "Spanish", "Chinese", "Italian", "French", "German", "Hindi", "Japanese", "Korean", "Russian", "Vietnamese")
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
        val btn: ImageButton = findViewById(R.id.nav_another)
        val btnSetting: ImageButton = findViewById(R.id.nav_setting)

        val btnSpeakingBottomFrom: Button = findViewById(R.id.btn_speakFromBottom)
        val btnSpeakingBottomTo: Button = findViewById(R.id.btn_speakToBottom)
        val btnSpeakingBottom: ImageButton = findViewById(R.id.btn_speak)

//        Choice a language that you have
        val nameLFrom: Spinner = findViewById(R.id.nameLanguageFrom)
        val valueLFrom: EditText = findViewById(R.id.valueFrom)
        val btnSpeakingLFrom: ImageButton = findViewById(R.id.btn_speakingFrom)

        val nameLTo: Spinner = findViewById(R.id.nameLanguageTo)
        val valueLTo: TextView = findViewById(R.id.valueTo)
        val btnSpeakingLTo: ImageButton = findViewById(R.id.btn_speakingTo)

        val btnClear: ImageButton = findViewById(R.id.btn_clear)
        val btnMore: ImageButton = findViewById(R.id.btn_moreInfo)
        val btnSwap: ImageButton = findViewById(R.id.btn_swap)

//        Get text from image
        val valueImage = intent.getStringExtra("valueImage").toString()
        val positionF = intent.getStringExtra("positionF")
        val positionT = intent.getStringExtra("positionT")

        if (!valueImage.equals("null")){
            valueLFrom.setText(valueImage)
            btnClear.visibility = View.VISIBLE
            btnMore.visibility = View.VISIBLE}

//        Excute event -- when click button
//        ===========Navigation
        btnOfflinePhraseBook.setOnClickListener {

            val intent = Intent(this, ShowOfflinePhraseBook::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        btnCamera.setOnClickListener {

            var positionFrom = nameLFrom.getSelectedItemPosition().toString()
            var positionTo = nameLTo.getSelectedItemPosition().toString()

            val intent = Intent(this, ShowImage::class.java)
            intent.putExtra("positionF", positionFrom)
            intent.putExtra("positionT", positionTo)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        btnSetting.setOnClickListener {

            val intent = Intent(this, Setting::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

//        Content
//        ===========Translate
        btnClear.setOnClickListener {

            valueLFrom.setText("")
        }

//        Swap between languages each other
        btnSwap.setOnClickListener {

            valueLFrom.setText(valueLTo.text.toString())

            var positionFrom = nameLFrom.getSelectedItemPosition()
            var positionTo = nameLTo.getSelectedItemPosition()
            swapLanguage(positionFrom, positionTo)
        }

//        Enter text you wanna translate

        valueLFrom.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                btnSpeakingBottomFrom.setBackgroundResource(R.drawable.btn_speaking_default_left)
                btnSpeakingBottomFrom.setTextColor(Color.parseColor(colorWord))
                btnSpeakingBottomTo.setBackgroundResource(R.drawable.btn_speaking_default_right)
                btnSpeakingBottomTo.setTextColor(Color.parseColor(colorWord))
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {

                var value = valueLFrom.text.toString()
                if (value != ""){

                    btnClear.visibility = View.VISIBLE
                    btnMore.visibility = View.VISIBLE
                    translateValue(value, languageTypeFrom, languageTypeTo)

                }else{

                    valueLTo.setText("")
                    btnClear.visibility = View.INVISIBLE
                    btnMore.visibility = View.INVISIBLE
                }

                btnSpeakingBottomFrom.setBackgroundResource(R.drawable.btn_speaking_default_left)
                btnSpeakingBottomFrom.setTextColor(Color.parseColor(colorWord))
                btnSpeakingBottomTo.setBackgroundResource(R.drawable.btn_speaking_default_right)
                btnSpeakingBottomTo.setTextColor(Color.parseColor(colorWord))
            }
        })

//        Choose language
        val adapterLanguageFrom = ArrayAdapter(this, R.layout.item_language, arrayLanguage)
            adapterLanguageFrom.setDropDownViewResource(R.layout.item_language)
            nameLFrom.adapter = adapterLanguageFrom

        if (!positionF.equals(null))
            nameLFrom.setSelection(positionF.toString().toInt())

        nameLFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                languageTypeFrom = detectLanguage(arrayLanguage[position])
                var value = valueLFrom.text.toString()
                translateValue(value, languageTypeFrom, languageTypeTo)

                var changeLanguage = nameLFrom.selectedItem.toString()
                displayFlagFrom(changeLanguage)
                btnSpeakingBottomFrom.setText(changeLanguage)
            }
        }

        val adapterLanguageTo = ArrayAdapter(this, R.layout.item_language, arrayLanguage)
        adapterLanguageTo.setDropDownViewResource(R.layout.item_language)
        nameLTo.adapter = adapterLanguageTo

        if (!positionT.equals(null))
            nameLTo.setSelection(positionT.toString().toInt())
        else
            nameLTo.setSelection(1) //Get default value in array of languages

        nameLTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                languageTypeTo = detectLanguage(arrayLanguage[position])
                var value = valueLFrom.text.toString()
                translateValue(value, languageTypeFrom, languageTypeTo)

                var changeLanguage = nameLTo.selectedItem.toString()
                displayFlagTo(changeLanguage)
                btnSpeakingBottomTo.setText(changeLanguage)
            }
        }

//        Click speaking button to speak
        btnSpeakingLFrom.setOnClickListener {

            var changeLanguage = nameLFrom.selectedItem.toString()
            speaker = TextToSpeech(applicationContext, {

                if (it== TextToSpeech.SUCCESS){

                    speaker.language = detectVoiceWithLanguage(changeLanguage)
                    speaker.setSpeechRate(1.0f)
                    speaker.speak(valueLFrom.text.toString(), TextToSpeech.QUEUE_ADD, null)
                }
            })
        }

        btnSpeakingLTo.setOnClickListener {

            var changeLanguage = nameLTo.selectedItem.toString()
            speaker = TextToSpeech(applicationContext, {

                if (it== TextToSpeech.SUCCESS){

                    speaker.language = detectVoiceWithLanguage(changeLanguage)
                    speaker.setSpeechRate(1.0f)
                    speaker.speak(valueLTo.text.toString(), TextToSpeech.QUEUE_ADD, null)
                }
            })
        }

        btnSpeakingBottom.setOnClickListener {

            btnSpeakingBottomFrom.setBackgroundResource(R.drawable.btn_speaking_left)
            btnSpeakingBottomFrom.setTextColor(Color.parseColor(colorWhite))
            btnSpeakingBottomTo.setBackgroundResource(R.drawable.btn_speaking_right)
            btnSpeakingBottomTo.setTextColor(Color.parseColor(colorWhite))

            var changeLanguage = nameLFrom.selectedItem.toString()
            SpeechText(detectVoiceWithLanguage(changeLanguage))
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
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

//    Function -- Check permission to use camera
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RECORD_SPEECH_TEXT && resultCode == Activity.RESULT_OK){

            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            valueFrom.setText(result?.get(0).toString())
        }
    }

//    Display the flag follow a language is chosen
    private fun displayFlagFrom(language: String){

        val flagLFrom: ImageView = findViewById(R.id.flagLanguageFrom)
        when (language){

            "English" -> {flagLFrom.setImageResource(R.drawable.ic_flag_american)}
            "Spanish" -> {flagLFrom.setImageResource(R.drawable.ic_flag_spanish)}
            "Chinese" -> {flagLFrom.setImageResource(R.drawable.ic_flag_china)}
            "French" -> {flagLFrom.setImageResource(R.drawable.ic_flag_france)}
            "Japanese" -> {flagLFrom.setImageResource(R.drawable.ic_flag_japanese)}
            "German" -> {flagLFrom.setImageResource(R.drawable.ic_flag_germany)}
            "Hindi" -> {flagLFrom.setImageResource(R.drawable.ic_flag_hindi)}
            "Italian" -> {flagLFrom.setImageResource(R.drawable.ic_flag_italy)}
            "Korean" -> {flagLFrom.setImageResource(R.drawable.ic_flag_southkorea)}
            "Russian" -> {flagLFrom.setImageResource(R.drawable.ic_flag_russia)}
            "Vietnamese" -> {flagLFrom.setImageResource(R.drawable.ic_flag_vietnam)}
        }
    }

    private fun displayFlagTo(language: String){

        val flagLTo: ImageView = findViewById(R.id.flagLanguageTo)
        when (language){

            "English" -> {flagLTo.setImageResource(R.drawable.ic_flag_american)}
            "Spanish" -> {flagLTo.setImageResource(R.drawable.ic_flag_spanish)}
            "Chinese" -> {flagLTo.setImageResource(R.drawable.ic_flag_china)}
            "French" -> {flagLTo.setImageResource(R.drawable.ic_flag_france)}
            "Japanese" -> {flagLTo.setImageResource(R.drawable.ic_flag_japanese)}
            "German" -> {flagLTo.setImageResource(R.drawable.ic_flag_germany)}
            "Hindi" -> {flagLTo.setImageResource(R.drawable.ic_flag_hindi)}
            "Italian" -> {flagLTo.setImageResource(R.drawable.ic_flag_italy)}
            "Korean" -> {flagLTo.setImageResource(R.drawable.ic_flag_southkorea)}
            "Russian" -> {flagLTo.setImageResource(R.drawable.ic_flag_russia)}
            "Vietnamese" -> {flagLTo.setImageResource(R.drawable.ic_flag_vietnam)}
        }
    }

//    Detect a language
    private fun detectLanguage(language: String): String{

        var languageType = ""
        when (language){

            "English" -> {languageType = TranslateLanguage.ENGLISH}
            "Chinese" -> {languageType = TranslateLanguage.CHINESE}
            "Spanish" -> {languageType = TranslateLanguage.SPANISH}
            "French" -> {languageType = TranslateLanguage.FRENCH}
            "Japanese" -> {languageType = TranslateLanguage.JAPANESE}
            "German" -> {languageType = TranslateLanguage.GERMAN}
            "Hindi" -> {languageType = TranslateLanguage.HINDI}
            "Italian" -> {languageType = TranslateLanguage.ITALIAN}
            "Korean" -> {languageType = TranslateLanguage.KOREAN}
            "Russian" -> {languageType = TranslateLanguage.RUSSIAN}
            "Vietnamese" -> {languageType = TranslateLanguage.VIETNAMESE}
        }
        return languageType
    }


//    Translate
    private fun translateValue(value: String, typeFrom: String, typeTo: String){

        val valueFTo: TextView = findViewById(R.id.valueFrom)
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
    private fun swapLanguage(positionFrom: Int, positionTo: Int){

        val nameLFrom: Spinner = findViewById(R.id.nameLanguageFrom)
        val nameLTo: Spinner = findViewById(R.id.nameLanguageTo)

        nameLFrom.setSelection(positionTo)
        nameLTo.setSelection(positionFrom)
    }

//    Get voice to speak
    private fun detectVoiceWithLanguage(language: String): Locale{

        var languageType = Locale.getDefault()
        when (language){

            "English" -> {languageType = Locale.ENGLISH}
            "Chinese" -> {languageType = Locale.forLanguageTag(CHINESE)}
            "Spanish" -> {languageType = Locale.forLanguageTag(SPANISH)}
            "French" -> {languageType = Locale.forLanguageTag(FRENCH)}
            "Japanese" -> {languageType = Locale.forLanguageTag(JAPANESE)}
            "German" -> {languageType = Locale.forLanguageTag(GERMAN)}
            "Hindi" -> {languageType = Locale.forLanguageTag(HINDI)}
            "Italian" -> {languageType = Locale.forLanguageTag(ITALIAN)}
            "Korean" -> {languageType = Locale.forLanguageTag(KOREAN)}
            "Russian" -> {languageType = Locale.forLanguageTag(RUSSIAN)}
            "Vietnamese" -> {languageType = Locale.forLanguageTag(VIETNAMESE)}
        }
        return languageType
    }

//    Speech to text
    private fun SpeechText(voice: Locale){

        if (!SpeechRecognizer.isRecognitionAvailable(this))

            show("Error")
        else{

            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, voice)
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something by " + voice.toString() + " !")
            startActivityForResult(i, RECORD_SPEECH_TEXT)
        }

    }
}

