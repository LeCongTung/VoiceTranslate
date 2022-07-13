package com.example.voicetranslate.ui.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
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
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_show_photo.*
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ShowPhotoActivity : AppCompatActivity() {

    private lateinit var textRecognizer: TextRecognizer
    private lateinit var intentPathImage: String
    private lateinit var intentFrom: String
    private lateinit var intentDisplayFrom: String
    private lateinit var intentLanguageFrom: String
    private var intentFlagFrom: Int = 0
    private lateinit var intentDisplayTo: String
    private lateinit var intentLanguageTo: String
    private var intentFlagTo: Int = 0

    private lateinit var imageViewModel: ImageViewModel

    private lateinit var time: String
    private var uri: Uri? = null
    private var isPin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_photo)

        imageViewModel = ViewModelProvider(this).get(ImageViewModel::class.java)
        getData()
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        time = current.format(formatter)
        detectFrom(intentFrom)

        btn_more.setOnClickListener {
            displayMoreDismiss(true)
        }

        btn_cancel.setOnClickListener {
            displayMoreDismiss(false)
        }

        dismissDialog.setOnClickListener {
            displayMoreDismiss(false)
        }

        btn_translate.setOnClickListener {
            val intent = Intent(this, TranslateActivity::class.java)
            intent.putExtra("value", valueAfterDetect.text.toString())
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("languageFrom", intentLanguageFrom)
            intent.putExtra("flagFrom", intentFlagFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("languageTo", intentLanguageTo)
            intent.putExtra("flagTo", intentFlagTo)
            startActivityForResult(intent, 1002)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        btn_pin.setOnClickListener {
            isPin = if (!isPin){
                insertPin(time, uri.toString(), 1)
                btn_pin.setImageResource(R.drawable.ic_pin)
                true
            }else{
                insertPin(time, uri.toString(), 0)
                btn_pin.setImageResource(R.drawable.ic_unpin)
                false
            }
        }

        btn_close.setOnClickListener {
            onBackPressed()
        }

        btn_translateTo.setOnClickListener {
            val intent = Intent(this, ShowLanguageActivity::class.java)
            intent.putExtra("value", valueAfterDetect.text.toString())
            intent.putExtra("abort", "abort")
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("languageFrom", intentLanguageFrom)
            intent.putExtra("flagFrom", intentFlagFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("languageTo", intentLanguageTo)
            intent.putExtra("flagTo", intentFlagTo)
            startActivityForResult(intent, 1002)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
            Handler().postDelayed({
                displayMoreDismiss(false)
            }, 400)
        }

        btn_crop.setOnClickListener {
            Log.d("link", uri.toString())
            launchImageCrop(uri!!)
            displayMoreDismiss(false)
        }

        btn_shareImage.setOnClickListener {
            appBar.visibility = View.INVISIBLE
            displayMoreDismiss(false)
            val bitmap = takeScreenshotOfRootView(imageView)
            val uriShare = getImageToShare(bitmap)
            shareImageURI(uriShare!!)
            Handler().postDelayed({
                appBar.visibility = View.VISIBLE
            }, 100)
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
            requestCode == 1002 && resultCode == Activity.RESULT_OK -> {
                data?.let {
                    intentDisplayFrom = data.getStringExtra("displayFrom").toString()
                    intentLanguageFrom = data.getStringExtra("languageFrom").toString()
                    intentFlagFrom = data.getIntExtra("flagFrom", 0)
                    intentDisplayTo = data.getStringExtra("displayTo").toString()
                    intentLanguageTo = data.getStringExtra("languageTo").toString()
                    intentFlagTo = data.getIntExtra("flagTo", 0)
                }
            }

            requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    uri = result.uri
                    Glide.with(this)
                        .load(uri)
                        .into(imageView)
                    convertImageToTextFromURIAfterCrop(result.uri)
                    val current = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    time = current.format(formatter)
                    insert(time, uri.toString())
                    btn_pin.setImageResource(R.drawable.ic_unpin)
                    isPin = false
                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.e("error", "Crop error: ${result.error}" )
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
    }

    private fun detectFrom(from: String){
        if (from == "Camera")
            displayImageCamera()
        else
            displayImageGallery()
    }

    private fun displayImageCamera(){
        uri = Uri.fromFile(File(intentPathImage))
        convertImageToTextFromURI(uri)
        imageView.setImageURI(uri)
    }

    private fun displayImageGallery(){
        uri = Uri.parse(intentPathImage)
        convertImageToTextFromURI(uri)
        Glide.with(this)
            .load(uri)
            .into(imageView)
    }

    private fun launchImageCrop(uri: Uri){
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
    }

    private fun convertImageToTextFromURI(imageUri: Uri?) {
        valueAfterDetect.visibility = View.INVISIBLE
        styleLanguage(intentDisplayFrom)
        val image = InputImage.fromFilePath(applicationContext, imageUri!!)
        textRecognizer.process(image)
            .addOnSuccessListener {
                valueAfterDetect.text = it.text
                if (valueAfterDetect.text.toString() != ""){
                    valueAfterDetect.visibility = View.VISIBLE
                    insert(time, intentPathImage)
                }
                else
                    show("No text found")
            }.addOnFailureListener {
                show("Cant get data from this image")
            }
    }

    private fun convertImageToTextFromURIAfterCrop(imageUri: Uri?) {
        valueAfterDetect.visibility = View.INVISIBLE
        styleLanguage(intentDisplayFrom)
        val image = InputImage.fromFilePath(applicationContext, imageUri!!)
        textRecognizer.process(image)
            .addOnSuccessListener {
                valueAfterDetect.text = it.text
                if (valueAfterDetect.text.toString() != ""){
                    valueAfterDetect.visibility = View.VISIBLE
                }
                else
                    show("No text found")
            }.addOnFailureListener {
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

    private fun insert(time: String, path: String){
        val image = Image(time, path, intentDisplayFrom, intentLanguageFrom, intentFlagFrom, intentDisplayTo, intentLanguageTo, intentFlagTo, intentFrom, 0)
        imageViewModel.insert(image)
    }

    private fun insertPin(time: String, path: String, pinned: Int){
        val image = Image(time, path, intentDisplayFrom, intentLanguageFrom, intentFlagFrom, intentDisplayTo, intentLanguageTo, intentFlagTo, intentFrom, pinned)
        imageViewModel.update(image)
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
            valueAfterDetect.visibility = View.VISIBLE
            contentFeature.visibility = View.INVISIBLE
            dismissDialog.visibility = View.GONE
        }else{
            valueAfterDetect.visibility = View.INVISIBLE
            contentFeature.visibility = View.VISIBLE
            dismissDialog.visibility = View.VISIBLE
        }
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