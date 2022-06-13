package com.example.voicetranslate.shows

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ShowImage : AppCompatActivity() {

    private val GALLERY_REQUEST_CODE = 2
    private val STORAGE_PERMISSION_CODE = 113
    private val PERMISSIONS_REQUEST_CODE_FLASH = 23

    lateinit var inputImage: InputImage
    lateinit var textRecognizer: TextRecognizer

    lateinit var intentDisplayFrom: String
    lateinit var intentLanguageFrom: String
    var intentFlagFrom: Int = 0
    lateinit var intentDisplayTo: String
    lateinit var intentLanguageTo: String
    var intentFlagTo: Int = 0

    private lateinit var cameraM :CameraManager
    var isFlash = false
    var cameraStatus: Boolean = true

    private lateinit var cameraExecutor: ExecutorService

    private val imageAnalyzer: ImageAnalysis.Analyzer = TextAnalysis()
    private var imageAnalysis = ImageAnalysis.Builder()
        .setImageQueueDepth(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

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

        askCameraPermission()
        startCamera()
        cameraExecutor = Executors.newSingleThreadExecutor()

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

        btnCamera.setTextColor(Color.parseColor("#5491FF"))
        piccam.setColorFilter(Color.parseColor("#5491FF"))

        contentCamera.setOnClickListener {
            if (!cameraStatus){

                btnGallery.setTextColor(Color.parseColor("#989898"))
                picgallery.setColorFilter(Color.parseColor("#989898"))
                btnCamera.setTextColor(Color.parseColor("#5491FF"))
                piccam.setColorFilter(Color.parseColor("#5491FF"))
                valueAfterDetect.setText("")
                cameraStatus = true
                imageView.visibility = View.INVISIBLE
                viewFinder.visibility = View.VISIBLE
                btn_capture.visibility = View.VISIBLE
                startCamera()
            }
        }

        contentGallery.setOnClickListener {

            cameraStatus = false
            galleryCheckPermission()
            valueAfterDetect.setText("")
            valueAfterDetect.visibility = View.VISIBLE
        }

        btn_flash.setOnClickListener {

            openFlashLight()
        }

        btn_capture.setOnClickListener {

            startRecognising()
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
        btnGallery.setTextColor(Color.parseColor("#5491FF"))
        picgallery.setColorFilter(Color.parseColor("#5491FF"))
        btnCamera.setTextColor(Color.parseColor("#989898"))
        piccam.setColorFilter(Color.parseColor("#989898"))
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    companion object {
        private const val TAG = "CameraXBasic"

        const val CAMERA_PERM_CODE = 422
    }

    private fun askCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            CAMERA_PERM_CODE
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }



            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }



    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }



    private fun startRecognising(){
        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(this),
            imageAnalyzer
        )
        valueAfterDetect.text= textDone.toString()

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
                valueAfterDetect.visibility = View.INVISIBLE
                imageView.load(imageUri)
                imageView.visibility = View.VISIBLE
                viewFinder.visibility = View.INVISIBLE
                btn_capture.visibility = View.INVISIBLE

                convertImageToTextFromURI(imageUri)
            }

            requestCode != GALLERY_REQUEST_CODE || resultCode != Activity.RESULT_OK ->{

                btnGallery.setTextColor(Color.parseColor("#989898"))
                picgallery.setColorFilter(Color.parseColor("#989898"))
                btnCamera.setTextColor(Color.parseColor("#5491FF"))
                piccam.setColorFilter(Color.parseColor("#5491FF"))
                cameraStatus = true
                imageView.visibility = View.INVISIBLE
                viewFinder.visibility = View.VISIBLE
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
        startCamera()
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
        }
    }

    //    Function -- Swap language
//    Swap between languages
    private fun swapLanguage(displayFrom: String, displayTo: String) {
        nameLanguageFrom.setText(displayTo)
        nameLanguageTo.setText(displayFrom)
    }

    private fun openFlashLight() {
        val cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0]
        if (!isFlash) {

            cameraManager.setTorchMode(cameraId, true)
            btn_flash.setImageResource(R.drawable.ic_flash_on)
            isFlash = true


        } else {
            cameraManager.setTorchMode(cameraId, false)
            btn_flash.setImageResource(R.drawable.ic_flash_off)
            isFlash = false
        }
    }
}