package com.example.voicetranslate.shows

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.load
import com.example.voicetranslate.R
import com.example.voicetranslate.screens.Home
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_show_image.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ShowImage : AppCompatActivity() {

    private val GALLERY_REQUEST_CODE = 2
    private val STORAGE_PERMISSION_CODE = 113
    private val CAMERA_REQUEST_CODE = 42

    lateinit var takePictureIntent: Intent
    lateinit var inputImage: InputImage
    lateinit var textRecognizer: TextRecognizer
    lateinit var photoFile: File
    private lateinit var dialog: Dialog

    lateinit var intentDisplayFrom: String
    lateinit var intentLanguageFrom: String
    var intentFlagFrom: Int = 0
    lateinit var intentDisplayTo: String
    lateinit var intentLanguageTo: String
    var intentFlagTo: Int = 0

    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_image)

//        Get data
        intentDisplayFrom = intent.getStringExtra("displayFrom").toString()
        intentLanguageFrom = intent.getStringExtra("languageFrom").toString()
        intentFlagFrom = intent.getIntExtra("flagFrom", 0)

        intentDisplayTo = intent.getStringExtra("displayTo").toString()
        intentLanguageTo = intent.getStringExtra("languageTo").toString()
        intentFlagTo = intent.getIntExtra("flagTo", 0)

        swapLanguage(intentDisplayTo, intentDisplayFrom)

//        Get text from image
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        if (checkPermission()) {

            showDialog()
            val handler = Handler()
            handler.postDelayed({

                useCamera()
                hideDialog()
            }, 2500)
        } else
            requestPermission()


//        Excute event
        btn_close.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.putExtra("value", valueAfterDetect.text.toString())

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

        btnCamera.setOnClickListener {

            btnCamera.setTextColor(Color.parseColor("#5491FF"))
            piccam.setColorFilter(Color.parseColor("#5491FF"))
            btnGallery.setTextColor(Color.parseColor("#989898"))
            picgallery.setColorFilter(Color.parseColor("#989898"))

            useCamera()
        }

        btnGallery.setOnClickListener {

            btnGallery.setTextColor(Color.parseColor("#5491FF"))
            picgallery.setColorFilter(Color.parseColor("#5491FF"))
            btnCamera.setTextColor(Color.parseColor("#989898"))
            piccam.setColorFilter(Color.parseColor("#989898"))

            galleryCheckPermission()
        }

        btn_swap.setOnClickListener {

            swapLanguage(intentDisplayFrom, intentDisplayTo)

            var dataDisplay = intentDisplayFrom
            intentDisplayFrom = intentDisplayTo
            intentDisplayTo = dataDisplay

            var dataFlag = intentFlagFrom.toString()
            intentFlagFrom = intentFlagTo
            intentFlagTo = dataFlag.toInt()

            var dataSave = intentLanguageFrom
            intentLanguageFrom = intentLanguageTo
            intentLanguageTo = dataSave
        }

        nameLanguageFrom.setOnClickListener {

            val intent = Intent(this, ShowLanguage::class.java)
            intent.putExtra("typeChoice", "above")
            intent.putExtra("from", "camera")
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            startActivityForResult(intent, 1001)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        nameLanguageTo.setOnClickListener {

            val intent = Intent(this, ShowLanguage::class.java)
            intent.putExtra("from", "camera")
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            startActivityForResult(intent, 1001)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }
    }

    //    Function -- Click back button to close app
    override fun onBackPressed() {

        intentDisplayFrom = intent.getStringExtra("displayFrom").toString()
        intentLanguageFrom = intent.getStringExtra("languageFrom").toString()
        intentFlagFrom = intent.getIntExtra("flagFrom", 0)

        intentDisplayTo = intent.getStringExtra("displayTo").toString()
        intentLanguageTo = intent.getStringExtra("languageTo").toString()
        intentFlagTo = intent.getIntExtra("flagTo", 0)

        val intent = Intent(this, Home::class.java)
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

    //    Function -- Toast
    private fun show(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    //    Gallery
    private fun galleryCheckPermission() {

        Dexter.withContext(this).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                show("You have denied the storage permission to select image")
                showRotationalDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?, p1: PermissionToken?
            ) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    //    Camera
    private fun checkPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            false
        } else true
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA),
            1888
        )
    }

    private fun getPhotoFile(fileName: String): File {

        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    private fun useCamera() {

        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val formatted = current.format(formatter)

        takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile(formatted + "_")

        val fileProvider = FileProvider.getUriForFile(this, "com.example.voicetranslate", photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        if (takePictureIntent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
        } else {
            show("Unable to open camera")
        }
    }

    //    Override Activity Result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when {
            requestCode == 1001 && resultCode == Activity.RESULT_OK -> {
                data?.let {

                    intentDisplayFrom = data.getStringExtra("displayFrom").toString()
                    intentLanguageFrom = data.getStringExtra("languageFrom").toString()
                    intentFlagFrom = data.getIntExtra("flagFrom", 0)

                    intentDisplayTo = data.getStringExtra("displayTo").toString()
                    intentLanguageTo = data.getStringExtra("languageTo").toString()
                    intentFlagTo = data.getIntExtra("flagTo", 0)

                    swapLanguage(intentDisplayTo, intentDisplayFrom)
                }
            }

            requestCode == GALLERY_REQUEST_CODE  && resultCode == Activity.RESULT_OK -> {
                val imageUri = data?.data
                imageView.load(imageUri)
                convertImageToTextFromURI(imageUri)
            }

            requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK -> {
                val takeImage = BitmapFactory.decodeFile(photoFile.absolutePath)
                imageView.setImageBitmap(takeImage)
                convertImageToTextFromBitmap(takeImage)
            }
        }
    }

    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage(
                "you have turned off permissions"
                        + "required for this feature. It can be enable under app settings!"
            )

            .setPositiveButton("Go to settings") { _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }

            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    //    Recognize images
    private fun convertImageToTextFromURI(imageUri: Uri?) {

        inputImage = InputImage.fromFilePath(applicationContext, imageUri!!)
        val result: Task<Text> = textRecognizer.process(inputImage)
            .addOnSuccessListener {

                valueAfterDetect.text = it.text

            }.addOnFailureListener {

                show("Cant get data from this image")
            }
    }

    private fun convertImageToTextFromBitmap(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->

                valueAfterDetect.text = visionText.text
            }
            .addOnFailureListener { e ->

            }
    }

    override fun onResume() {
        super.onResume()
        detectCheckPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            STORAGE_PERMISSION_CODE
        )
    }

    private fun detectCheckPermission(permisson: String, requestCode: Int) {

        if (ContextCompat.checkSelfPermission(this, permisson) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, arrayOf(permisson), requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //    Function -- Swap language
//    Swap between languages
    private fun swapLanguage(displayFrom: String, displayTo: String) {
        nameLanguageFrom.setText(displayTo)
        nameLanguageTo.setText(displayFrom)
    }

    //    Showing a dialog
    private fun showDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_loadingcamera)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideDialog() {

        dialog.dismiss()
    }
}