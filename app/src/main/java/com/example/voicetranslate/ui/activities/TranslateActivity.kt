package com.example.voicetranslate.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voicetranslate.R
import com.example.voicetranslate.extensions.hideKeyboard
import com.example.voicetranslate.extensions.showKeyboard
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.android.synthetic.main.activity_translate.*

class TranslateActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_translate)

        getData()
        showLanguageForm(intentDisplayTo, intentFlagTo, intentDisplayFrom, intentFlagFrom)
        valueFrom.setText(value)
        translateValue(value, intentLanguageFrom, intentLanguageTo)

        btn_swap.setOnClickListener {
            showLanguageForm(intentDisplayFrom, intentFlagFrom, intentDisplayTo, intentFlagTo)
            swapLanguage()
            translateValue(valueFrom.text.toString(), intentLanguageFrom, intentLanguageTo)
        }

        nameLanguageFrom.setOnClickListener {
            val intent = Intent(this, ShowLanguageActivity::class.java)
            intent.putExtra("typeChoice", "above")
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("value", valueFrom.text.toString())
            startActivityForResult(intent, 999)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        nameLanguageTo.setOnClickListener {
            val intent = Intent(this, ShowLanguageActivity::class.java)
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("value", valueFrom.text.toString())
            startActivityForResult(intent, 999)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        formFrom.setOnClickListener {
            showKeyboard(valueFrom, this)
        }

        valueFrom.imeOptions = EditorInfo.IME_ACTION_DONE;
        valueFrom.setRawInputType(InputType.TYPE_CLASS_TEXT);
        valueFrom.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                translateValue(valueFrom.text.toString(), intentLanguageFrom, intentLanguageTo)
                val view: View? = this.currentFocus
                hideKeyboard(this, view!!)
            }
            false
        }

        btn_close.setOnClickListener {
            onBackPressed()
        }

        btn_copyinput.setOnClickListener{
            copyText(valueFrom.text.toString())
        }

        btn_shareinput.setOnClickListener {
            shareText(valueFrom.text.toString())
        }

        btn_copyoutput.setOnClickListener{
            copyText(valueTo.text.toString())
        }

        btn_shareoutput.setOnClickListener {
            shareText(valueTo.text.toString())
        }

        btn_zoominput.setOnClickListener {
            val intent = Intent(this, DisplayTextActivity::class.java)
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("languageFrom", intentLanguageFrom)
            intent.putExtra("flagFrom", intentFlagFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("languageTo", intentLanguageTo)
            intent.putExtra("flagTo", intentFlagTo)
            intent.putExtra("value", valueFrom.text.toString())
            startActivityForResult(intent, 999)
        }

        btn_zoomoutput.setOnClickListener {
            val intent = Intent(this, DisplayTextActivity::class.java)
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("languageFrom", intentLanguageFrom)
            intent.putExtra("flagFrom", intentFlagFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("languageTo", intentLanguageTo)
            intent.putExtra("flagTo", intentFlagTo)
            intent.putExtra("value", valueTo.text.toString())
            startActivityForResult(intent, 999)
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("displayFrom", intentDisplayFrom)
        intent.putExtra("languageFrom", intentLanguageFrom)
        intent.putExtra("flagFrom", intentFlagFrom)
        intent.putExtra("displayTo", intentDisplayTo)
        intent.putExtra("languageTo", intentLanguageTo)
        intent.putExtra("flagTo", intentFlagTo)
        setResult(Activity.RESULT_OK, intent)
        finish()
        overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
    }

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
                    showLanguageForm(intentDisplayTo, intentFlagTo, intentDisplayFrom, intentFlagFrom)
                }

                translateValue(
                    valueFrom.text.toString(),
                    intentLanguageFrom,
                    intentLanguageTo
                )
            }
        }
    }

    private fun getData(){
        value = intent.getStringExtra("value").toString()
        intentDisplayFrom = intent.getStringExtra("displayFrom").toString()
        intentLanguageFrom = intent.getStringExtra("languageFrom").toString()
        intentFlagFrom = intent.getIntExtra("flagFrom", R.drawable.ic_flag_english)
        intentDisplayTo = intent.getStringExtra("displayTo").toString()
        intentLanguageTo = intent.getStringExtra("languageTo").toString()
        intentFlagTo = intent.getIntExtra("flagTo", R.drawable.ic_flag_vietnamese)
    }

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

    private fun showLanguageForm(displayFrom: String?, flagFrom: Int, displayTo: String?, flagTo: Int) {
        nameLanguageFrom.text = displayTo
        flagLanguageFrom.setImageResource(flagTo)

        nameLanguageTo.text = displayFrom
        flagLanguageTo.setImageResource(flagFrom)
    }

    private fun swapLanguage(){
        val dataDisplay = intentDisplayFrom
        intentDisplayFrom = intentDisplayTo
        intentDisplayTo = dataDisplay

        val dataFlag = intentFlagFrom.toString()
        intentFlagFrom = intentFlagTo
        intentFlagTo = dataFlag.toInt()

        val dataSave = intentLanguageFrom
        intentLanguageFrom = intentLanguageTo
        intentLanguageTo = dataSave
    }

    private fun copyText(text: String) {
        val clipboardManager: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("key", text)
        clipboardManager.setPrimaryClip(clipData)
        show("Copied to Clipboard!")
    }

    private fun shareText(text: String){
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, "Share text via"))
    }

    private fun show(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

