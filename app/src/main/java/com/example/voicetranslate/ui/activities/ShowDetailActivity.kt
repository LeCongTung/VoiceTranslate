package com.example.voicetranslate.ui.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.voicetranslate.R
import com.example.voicetranslate.models.Image
import com.example.voicetranslate.ui.viewmodels.ImageViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.android.synthetic.main.activity_show_detail.*
import java.io.File
import java.io.FileOutputStream

class ShowDetailActivity : AppCompatActivity() {

    private lateinit var textRecognizer: TextRecognizer
    private lateinit var intentPathImage: String
    private lateinit var intentFrom: String
    private lateinit var intentDisplayFrom: String
    private lateinit var intentLanguageFrom: String
    private var intentFlagFrom: Int = 0
    private lateinit var intentDisplayTo: String
    private lateinit var intentLanguageTo: String
    private var intentFlagTo: Int = 0
    private var intentList: Boolean = true
    private lateinit var intentTime: String

    private lateinit var imageViewModel: ImageViewModel
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_detail)

        imageViewModel = ViewModelProvider(this).get(ImageViewModel::class.java)
        getData()
        detectFrom()

        btn_closeAct.setOnClickListener {
            onBackPressed()
        }

        btn_moreAct.setOnClickListener {
            displayMoreDismiss(true)
        }

        btn_cancelAct.setOnClickListener {
            displayMoreDismiss(false)
        }

        btn_translateAct.setOnClickListener {
            val intent = Intent(this, TranslateActivity::class.java)
            intent.putExtra("value", valueDetect.text.toString())
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("languageFrom", intentLanguageFrom)
            intent.putExtra("flagFrom", intentFlagFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("languageTo", intentLanguageTo)
            intent.putExtra("flagTo", intentFlagTo)
            startActivityForResult(intent, 444)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        btn_shareImageAct.setOnClickListener {
            appBar.visibility = View.INVISIBLE
            displayMoreDismiss(false)
            val bitmap = takeScreenshotOfRootView(captureImageAct)
            val uriShare = getImageToShare(bitmap)
            shareImageURI(uriShare!!)
            Handler().postDelayed({
                appBar.visibility = View.VISIBLE
            }, 100)
        }

        btn_pinAct.setOnClickListener {
            deleteData()
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when {
            requestCode == 444 && resultCode == Activity.RESULT_OK -> {
                data?.let {
                    intentDisplayFrom = data.getStringExtra("displayFrom").toString()
                    intentLanguageFrom = data.getStringExtra("languageFrom").toString()
                    intentFlagFrom = data.getIntExtra("flagFrom", 0)
                    intentDisplayTo = data.getStringExtra("displayTo").toString()
                    intentLanguageTo = data.getStringExtra("languageTo").toString()
                    intentFlagTo = data.getIntExtra("flagTo", 0)
                }
            }
        }
    }

    private fun getData(){
        intentPathImage = intent.getStringExtra("pathimage").toString()
        intentFrom = intent.getStringExtra("from").toString()
        intentDisplayFrom = intent.getStringExtra("displayFrom").toString()
        intentLanguageFrom = intent.getStringExtra("languageFrom").toString()
        intentFlagFrom = intent.getIntExtra("flagFrom", R.drawable.ic_flag_english)
        intentDisplayTo = intent.getStringExtra("displayTo").toString()
        intentLanguageTo = intent.getStringExtra("languageTo").toString()
        intentFlagTo = intent.getIntExtra("flagTo", R.drawable.ic_flag_vietnamese)
        intentList = intent.getBooleanExtra("pin", intentList)
        intentTime = intent.getStringExtra("time").toString()
    }

    private fun detectFrom(){
        if (intentFrom == "Camera")
            displayImageCamera()
        else
            displayImageGallery()

        if (!intentList){
            btn_pinAct.setImageResource(R.drawable.ic_pin)
        }else
            btn_pinAct.setImageResource(R.drawable.ic_unpin)
    }

    private fun displayImageCamera(){
        uri = Uri.parse(File(intentPathImage).toString())
        try {
            convertImageToTextFromBitmap(intentPathImage)
        }catch (e: Exception){
            convertImageToTextFromURI(uri)
        }
        imageView.setImageURI(uri)
    }

    private fun displayImageGallery(){
        uri = Uri.parse(intentPathImage)
        convertImageToTextFromURI(uri)
        Glide.with(this)
            .load(uri)
            .into(imageView)
    }

    private fun convertImageToTextFromURI(imageUri: Uri?) {
        valueDetect.visibility = View.INVISIBLE
        styleLanguage(intentDisplayFrom)
        val image = InputImage.fromFilePath(applicationContext, imageUri!!)
        textRecognizer.process(image)
            .addOnSuccessListener {
                valueDetect.text = it.text
                if (valueDetect.text.toString() != "")
                    valueDetect.visibility = View.VISIBLE
                else
                    show("No text found")
            }.addOnFailureListener {
                show("Cant get data from this image")
            }
    }

    private fun convertImageToTextFromBitmap(file: String) {
        val bitmap = BitmapFactory.decodeFile(File(file).absolutePath)
        valueDetect.visibility = View.INVISIBLE
        styleLanguage(intentDisplayFrom)
        val image = InputImage.fromBitmap(bitmap, 0)
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                valueDetect.text = visionText.text
                if (valueDetect.text.toString() != "")
                    valueDetect.visibility = View.VISIBLE
                else
                    show("No text found")
            }
            .addOnFailureListener {
                show("Cant get data from this image")
            }
    }

    private fun shareImageURI(uri: Uri){
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "image/png"
        startActivity(Intent.createChooser(intent, "Share Photo Via"))
    }

    private fun getImageToShare(bitmap: Bitmap): Uri? {
        val imageFolder = File(cacheDir, "images")
        var uri: Uri? = null
        try {
            imageFolder.mkdirs()
            val file = File(imageFolder, "image.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
            outputStream.flush()
            outputStream.close()
            uri = FileProvider.getUriForFile(
                this,
                applicationContext.packageName.toString() + ".provider",
                file
            )
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "" + e.message, Toast.LENGTH_LONG).show()
        }
        return uri
    }

    private fun styleLanguage(style: String) {
        textRecognizer = when (style) {
            "Japanese" -> TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
            "Korean" -> TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
            "Chinese" -> TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
            "Hindi" -> TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())
            "Bengali" -> TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())
            else -> TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        }
    }

    private fun show(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun displayMoreDismiss(isShow: Boolean){
        if (!isShow){
            valueDetect.visibility = View.VISIBLE
            contentFeatureAct.visibility = View.INVISIBLE
            btn_moreAct.isEnabled = true
            btn_translateAct.isEnabled = true
            btn_pinAct.isEnabled = true
            btn_closeAct.isEnabled = true
        }else{
            valueDetect.visibility = View.INVISIBLE
            contentFeatureAct.visibility = View.VISIBLE
            btn_moreAct.isEnabled = false
            btn_translateAct.isEnabled = false
            btn_pinAct.isEnabled = false
            btn_closeAct.isEnabled = false
        }
    }

    private fun deleteData(){
        intentList = if (intentList){
            insertPin()
            btn_pinAct.setImageResource(R.drawable.ic_pin)
            false
        }
        else{
            deletePin()
            btn_pinAct.setImageResource(R.drawable.ic_unpin)
            true
        }
    }

    private fun insertPin(){
        val pinImage = Image(intentTime, intentPathImage, intentDisplayFrom, intentLanguageFrom, intentFlagFrom, intentDisplayTo, intentLanguageTo, intentFlagTo, intentFrom, 1)
        imageViewModel.update(pinImage)
    }

    private fun deletePin(){
        val pinImage = Image(intentTime, intentPathImage, intentDisplayFrom, intentLanguageFrom, intentFlagFrom, intentDisplayTo, intentLanguageTo, intentFlagTo, intentFrom, 0)
        imageViewModel.update(pinImage)
    }

    companion object Screenshot {
        private fun takeScreenshot(view: View): Bitmap {
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache(true)
            val b = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false
            return b
        }
        fun takeScreenshotOfRootView(v: View): Bitmap {
            return takeScreenshot(v.rootView)
        }
    }
}