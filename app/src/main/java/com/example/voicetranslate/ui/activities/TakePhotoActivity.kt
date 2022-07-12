package com.example.voicetranslate.ui.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.voicetranslate.R
import com.google.mlkit.nl.translate.TranslateLanguage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_take_photo.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TakePhotoActivity : AppCompatActivity() {

    private val GALLERY_REQUEST_CODE = 2
    private val PERMISSIONS_REQUEST_CODE_FLASH = 23

    private lateinit var intentDisplayFrom: String
    private lateinit var intentLanguageFrom: String
    private var intentFlagFrom: Int = 0
    private lateinit var intentDisplayTo: String
    private lateinit var intentLanguageTo: String
    private var intentFlagTo: Int = 0

    private var isFlash = false
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var backPressedTime: Long = 0

    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_photo)

        getDataSharePreferences()

        showLanguageForm(intentDisplayTo, intentFlagTo, intentDisplayFrom, intentFlagFrom)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        cameraExecutor = Executors.newSingleThreadExecutor()

//        Execute events
        btn_close.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("languageFrom", intentLanguageFrom)
            intent.putExtra("flagFrom", intentFlagFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("languageTo", intentLanguageTo)
            intent.putExtra("flagTo", intentFlagTo)
            startActivityForResult(intent, 1001)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        btn_swap.setOnClickListener {
            showLanguageForm(intentDisplayFrom, intentFlagFrom, intentDisplayTo, intentFlagTo)
            swapLanguage()
        }

        contentFrom.setOnClickListener {

            val intent = Intent(this, ShowLanguageActivity::class.java)
            intent.putExtra("typeChoice", "above")
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            startActivityForResult(intent, 1001)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        contentTo.setOnClickListener {

            val intent = Intent(this, ShowLanguageActivity::class.java)
            intent.putExtra("from", "camera")
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            startActivityForResult(intent, 1001)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isFlash = false
        btn_flash.setImageResource(R.drawable.ic_flash_off)
        cameraExecutor.shutdown()
    }

    override fun onPause() {
        super.onPause()
        isFlash = false
        btn_flash.setImageResource(R.drawable.ic_flash_off)
    }

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
                    showLanguageForm(intentDisplayTo, intentFlagTo, intentDisplayFrom, intentFlagFrom)
                    btn_flash.setImageResource(R.drawable.ic_flash_off)
                    isFlash = false
                    viewFinder.visibility = View.VISIBLE
                }
            }
            requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK -> {
                val imageUri = data?.data!!
                val intent = Intent(this@TakePhotoActivity, ShowPhotoActivity::class.java)
                intent.putExtra("pathimage", imageUri.toString())
                intent.putExtra("displayFrom", intentDisplayFrom)
                intent.putExtra("languageFrom", intentLanguageFrom)
                intent.putExtra("flagFrom", intentFlagFrom)
                intent.putExtra("displayTo", intentDisplayTo)
                intent.putExtra("languageTo", intentLanguageTo)
                intent.putExtra("flagTo", intentFlagTo)
                startActivityForResult(intent, 1001)
                overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
            }
        }
    }

    override fun onBackPressed() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            finish()
        } else {
            show("Press back again to leave the app")
        }
        backPressedTime = System.currentTimeMillis()
    }

    private fun getDataSharePreferences(){
        val sharedPreferences = getSharedPreferences("language", Context.MODE_PRIVATE)
        intentDisplayFrom = sharedPreferences.getString("displayFrom", "English").toString()
        intentLanguageFrom = sharedPreferences.getString("languageFrom", TranslateLanguage.ENGLISH).toString()
        intentFlagFrom = sharedPreferences.getInt("flagFrom", R.drawable.ic_flag_english)
        intentDisplayTo = sharedPreferences.getString("displayTo", "Vietnamese").toString()
        intentLanguageTo = sharedPreferences.getString("languageTo", TranslateLanguage.VIETNAMESE).toString()
        intentFlagTo = sharedPreferences.getInt("flagTo", R.drawable.ic_flag_vietnamese)
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
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PhotoTranslate")
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

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val pathName = "/storage/emulated/0/Pictures/PhotoTranslate/$name.jpg"
                    val intent = Intent(this@TakePhotoActivity, ShowPhotoActivity::class.java)
                    intent.putExtra("from", "Camera")
                    intent.putExtra("pathimage", pathName)
                    intent.putExtra("displayFrom", intentDisplayFrom)
                    intent.putExtra("languageFrom", intentLanguageFrom)
                    intent.putExtra("flagFrom", intentFlagFrom)
                    intent.putExtra("displayTo", intentDisplayTo)
                    intent.putExtra("languageTo", intentLanguageTo)
                    intent.putExtra("flagTo", intentFlagTo)
                    startActivityForResult(intent, 1001)
                    overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
                    btn_flash.setImageResource(R.drawable.ic_flash_off)
                    Handler().postDelayed({
                        hideDialog()
                    }, 500)
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
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture
                )

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

                btn_camera.setOnClickListener {
                    showDialog()
                    takePhoto()
                    viewFinder.visibility = View.INVISIBLE
                    Handler().postDelayed({
                        camera.cameraControl.enableTorch(false)
                    }, 500)
                }

                btn_gallery.setOnClickListener {
                    camera.cameraControl.enableTorch(false)
                    btn_flash.setImageResource(R.drawable.ic_flash_off)
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

    companion object {
        private const val TAG = "CameraXApp"
        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
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

    private fun show(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_detecting)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideDialog() {
        dialog.dismiss()
    }
}