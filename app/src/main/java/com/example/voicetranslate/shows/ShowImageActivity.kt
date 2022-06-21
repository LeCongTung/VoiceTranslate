package com.example.voicetranslate.shows

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.load
import com.example.voicetranslate.R
import com.example.voicetranslate.screens.HomeActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_show_image.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ShowImageActivity : AppCompatActivity() {

    private val GALLERY_REQUEST_CODE = 2
    private val PERMISSIONS_REQUEST_CODE_FLASH = 23

    private lateinit var textRecognizer: TextRecognizer

    private lateinit var intentDisplayFrom: String
    private lateinit var intentLanguageFrom: String
    private var intentFlagFrom: Int = 0
    private lateinit var intentDisplayTo: String
    private lateinit var intentLanguageTo: String
    private var intentFlagTo: Int = 0

    private var isFlash = false
    private var imageFromCamera: Int = 0

    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null

    private var imageUri: Uri? = null
    private var bitmap: Bitmap? = null

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

        displayEnableCamera()
        contentCamera.isEnabled = false
        swapLanguage(intentDisplayTo, intentDisplayFrom)

//        Get text from image
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

//        Excute event
        btn_close.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
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

        btn_swap.setOnClickListener {

            swapLanguage(intentDisplayFrom, intentDisplayTo)

            val dataDisplay = intentDisplayFrom
            intentDisplayFrom = intentDisplayTo
            intentDisplayTo = dataDisplay

            val dataFlag = intentFlagFrom.toString()
            intentFlagFrom = intentFlagTo
            intentFlagTo = dataFlag.toInt()

            val dataSave = intentLanguageFrom
            intentLanguageFrom = intentLanguageTo
            intentLanguageTo = dataSave

            valueAfterDetect.visibility = View.INVISIBLE
            when (imageFromCamera) {

                1 -> convertImageToTextFromBitmap(bitmap!!)
                2 -> convertImageToTextFromURI(imageUri!!)
            }
        }

        contentCamera.setOnClickListener {
            displayEnableCamera()
            valueAfterDetect.text = ""
            contentCamera.isEnabled = false
            imageFromCamera = 0
            imageView.visibility = View.INVISIBLE
            viewFinder.visibility = View.VISIBLE
            btn_capture.visibility = View.VISIBLE
            valueAfterDetect.visibility = View.INVISIBLE
            btn_close.setImageResource(R.drawable.ic_close)
            btn_flash.isEnabled = true
            startCamera()
        }

        nameLanguageFrom.setOnClickListener {

            val intent = Intent(this, ShowLanguageActivity::class.java)
            intent.putExtra("typeChoice", "above")
            intent.putExtra("from", "camera")
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            startActivityForResult(intent, 1001)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        nameLanguageTo.setOnClickListener {

            val intent = Intent(this, ShowLanguageActivity::class.java)
            intent.putExtra("from", "camera")
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            startActivityForResult(intent, 1001)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }
    }

    //    Function -- Click back button to close app
    override fun onBackPressed() {

        val intent = Intent(this, HomeActivity::class.java)
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
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {
                    val fileDir =
                        File("/storage/emulated/0/Pictures/CameraX-Image/$name.jpg")
                    if (fileDir.exists()) {

                        bitmap = BitmapFactory.decodeFile(fileDir.absolutePath)
                        convertImageToTextFromBitmap(bitmap!!)
                        imageView.setImageBitmap(bitmap)
                        viewFinder.visibility = View.INVISIBLE
                        imageView.visibility = View.VISIBLE
                        btn_close.setImageResource(R.drawable.ic_checked)
                        btn_flash.setImageResource(R.drawable.ic_flash_off)
                        contentCamera.isEnabled = true
                        isFlash = false
                    }
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .build()
            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)

//                Event of camera
                btn_flash.setOnClickListener {

                    isFlash = if (!isFlash) {
                        camera.cameraControl.enableTorch(true)
                        btn_flash.setImageResource(R.drawable.ic_flash_on)
                        true
                    } else {
                        camera.cameraControl.enableTorch(false)
                        btn_flash.setImageResource(R.drawable.ic_flash_off)
                        false
                    }
                }
                btn_capture.setOnClickListener {

                    imageFromCamera = 1
                    takePhoto()
                    btn_capture.visibility = View.INVISIBLE
                    Handler().postDelayed({
                        camera.cameraControl.enableTorch(false)
                    }, 500)
                    btn_flash.isEnabled = false
                }
                contentGallery.setOnClickListener {
                    camera.cameraControl.enableTorch(false)
                    btn_flash.setImageResource(R.drawable.ic_flash_off)
                    isFlash = false
                    bitmap = null
                    galleryCheckPermission()
                }
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }.toTypedArray()
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
                    when (imageFromCamera) {

                        1 -> convertImageToTextFromBitmap(bitmap!!)
                        2 -> convertImageToTextFromURI(imageUri!!)
                    }
                    btn_flash.setImageResource(R.drawable.ic_flash_off)
                    isFlash = false
                }
            }
            requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK -> {
                displayenableGallery()
                imageUri = data?.data!!
                imageView.load(imageUri)
                imageFromCamera = 2
                imageView.visibility = View.VISIBLE
                viewFinder.visibility = View.INVISIBLE
                btn_capture.visibility = View.INVISIBLE
                btn_flash.isEnabled = false
                contentCamera.isEnabled = true
                btn_close.setImageResource(R.drawable.ic_checked)
                convertImageToTextFromURI(imageUri)
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
        valueAfterDetect.visibility = View.INVISIBLE
        styleLanguage(nameLanguageFrom.text.toString())
        val image = InputImage.fromFilePath(applicationContext, imageUri!!)
        textRecognizer.process(image)
            .addOnSuccessListener {
                valueAfterDetect.text = it.text
                if (valueAfterDetect.text.toString() != "")
                    valueAfterDetect.visibility = View.VISIBLE
                else
                    show("Can't detect anything in the photo")

            }.addOnFailureListener {

                show("Cant get data from this image")
            }
    }

    private fun convertImageToTextFromBitmap(bitmap: Bitmap) {
        valueAfterDetect.visibility = View.INVISIBLE
        styleLanguage(nameLanguageFrom.text.toString())
        val image = InputImage.fromBitmap(bitmap, 0)
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->

                valueAfterDetect.text = visionText.text
                if (valueAfterDetect.text.toString() != "")
                    valueAfterDetect.visibility = View.VISIBLE
                else
                    show("Can't detect anything in the photo")
            }
            .addOnFailureListener {

                show("Cant get data from this image")
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSIONS_REQUEST_CODE_FLASH -> {
                if (grantResults.isNotEmpty() &&
                    ((grantResults[0] == PackageManager.PERMISSION_GRANTED))
                ) {

                } else {
                    Toast.makeText(this, "Permissions denied.", Toast.LENGTH_SHORT).show()
                }
                return
            }

            REQUEST_CODE_PERMISSIONS -> {
                if (allPermissionsGranted()) {
                    startCamera()
                } else {
                    Toast.makeText(
                        this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }

    //    Function -- Swap language
//    Swap between languages
    private fun swapLanguage(displayFrom: String, displayTo: String) {
        nameLanguageFrom.text = displayTo
        nameLanguageTo.text = displayFrom
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

    private fun displayEnableCamera(){
        btnGallery.setTextColor(Color.parseColor("#989898"))
        picgallery.setColorFilter(Color.parseColor("#989898"))
        btnCamera.setTextColor(Color.parseColor("#5491FF"))
        piccam.setColorFilter(Color.parseColor("#5491FF"))
    }

    private fun displayenableGallery(){
        btnGallery.setTextColor(Color.parseColor("#5491FF"))
        picgallery.setColorFilter(Color.parseColor("#5491FF"))
        btnCamera.setTextColor(Color.parseColor("#989898"))
        piccam.setColorFilter(Color.parseColor("#989898"))
    }
}