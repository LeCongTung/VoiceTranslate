package com.example.voicetranslate.shows

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_show_image.*
import java.io.ByteArrayOutputStream

class ShowImage : AppCompatActivity() {

    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2
    private val STORAGE_PERMISSION_CODE = 113

    var backPressedTime: Long = 0

    lateinit var inputImage: InputImage
    lateinit var textRecognizer: TextRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_image)

//        Init
        val btnClose: ImageView = findViewById(R.id.btn_close)
        val textAfterDetect: TextView = findViewById(R.id.valueAfterDetect)

        val btnUseCamera: Button = findViewById(R.id.btnCamera)
        val piccam: ImageView = findViewById(R.id.piccam)

        val btnUseGallery: Button = findViewById(R.id.btnGallery)
        val picgallery: ImageView = findViewById(R.id.picgallery)

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

//        Excute event
        btnClose.setOnClickListener{

            val intent = Intent(this, Home::class.java)
            intent.putExtra("valueImage", textAfterDetect.text.toString())
            startActivity(intent)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        btnUseCamera.setOnClickListener {

            btnUseCamera.setTextColor(Color.parseColor("#5491FF"))
            piccam.setImageResource(R.drawable.ic_camera_click)
            btnUseGallery.setTextColor(Color.parseColor("#989898"))
            picgallery.setImageResource(R.drawable.ic_gallery_noclick)
            cameraCheckPermission()
        }

        btnUseGallery.setOnClickListener {

            btnUseGallery.setTextColor(Color.parseColor("#5491FF"))
            picgallery.setImageResource(R.drawable.ic_gallery_click)
            btnCamera.setTextColor(Color.parseColor("#989898"))
            piccam.setImageResource(R.drawable.ic_camera_noclick)
            galleryCheckPermission()
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
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
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
                p0: PermissionRequest?, p1: PermissionToken?) {
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
    private fun cameraCheckPermission() {

        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA).withListener(

                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {

                            if (report.areAllPermissionsGranted()) {
                                camera()
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        showRotationalDialogForPermission()
                    }

                }
            ).onSameThread().check()
    }

    private fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

//    Override Activity Result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageView: ImageView = findViewById(R.id.imageView)
        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                CAMERA_REQUEST_CODE -> {

                    val bitmap: Bitmap = data?.extras?.get("data") as Bitmap
//                    imageView.setImageBitmap(bitmap)

                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    val path = MediaStore.Images.Media.insertImage(
                        applicationContext.getContentResolver(), bitmap, null, null
                    )

                    val imageUri: Uri = Uri.parse(path)
                    imageView.load(imageUri)
                    convertImageToText(imageUri)
                    show("Pause Translation")
                }

                GALLERY_REQUEST_CODE -> {

                    val imageUri = data?.data
                    imageView.load(imageUri)
                    convertImageToText(imageUri)
                    show("Pause Translation")
                }
            }
        }
    }

    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage("you have turned off permissions"
                    + "required for this feature. It can be enable under App settings!")

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
    private fun convertImageToText(imageUri: Uri?){

        val textAfterDetect: TextView = findViewById(R.id.valueAfterDetect)
        inputImage = InputImage.fromFilePath(applicationContext, imageUri!!)
        val result: Task<Text> = textRecognizer.process(inputImage)
            .addOnSuccessListener {

                textAfterDetect.text = it.text
            }.addOnFailureListener {

                show("Cant get data from this image")
            }
    }

    override fun onResume() {
        super.onResume()
        detectCheckPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
    }

    private fun detectCheckPermission(permisson: String, requestCode: Int){

        if (ContextCompat.checkSelfPermission(this, permisson)== PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, arrayOf(permisson), requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode== STORAGE_PERMISSION_CODE){

            if (grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED)

                show("Storage Permission Granted")
            else

                show("Storage Permission Denied")
        }
    }
}