package com.example.voicetranslate.shows

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
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
    val arrayLanguage = arrayOf("English", "Spanish", "Chinese", "Italian", "French", "German", "Hindi", "Japanese", "Korean", "Russian", "Vietnamese")

    lateinit var inputImage: InputImage
    lateinit var textRecognizer: TextRecognizer

    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_image)

//        Init
        val btnClose: ImageView = findViewById(R.id.btn_close)
        val textAfterDetect: TextView = findViewById(R.id.valueAfterDetect)

        val nameLFrom: Spinner = findViewById(R.id.nameLanguageFrom)
        val nameLTo: Spinner = findViewById(R.id.nameLanguageTo)
        val btnSwap: ImageButton = findViewById(R.id.btn_swap)

        val btnUseCamera: Button = findViewById(R.id.btnCamera)
        val piccam: ImageView = findViewById(R.id.piccam)

        val btnUseGallery: Button = findViewById(R.id.btnGallery)
        val picgallery: ImageView = findViewById(R.id.picgallery)

//        Get text from image
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val positionF = intent.getStringExtra("positionF")
        val positionT = intent.getStringExtra("positionT")

        show("Choosing the way that you want to add an image")
//        Choose language
        val adapterLanguageFrom = ArrayAdapter(this, R.layout.item_language_camera, arrayLanguage)
        adapterLanguageFrom.setDropDownViewResource(R.layout.item_language)
        nameLFrom.adapter = adapterLanguageFrom

        if (!positionF.equals(null))
            nameLFrom.setSelection(positionF.toString().toInt())

        nameLFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

            }
        }

        val adapterLanguageTo = ArrayAdapter(this, R.layout.item_language_camera, arrayLanguage)
        adapterLanguageTo.setDropDownViewResource(R.layout.item_language)
        nameLTo.adapter = adapterLanguageTo

        if (!positionT.equals(null))
            nameLTo.setSelection(positionT.toString().toInt())
        else
            nameLTo.setSelection(1) //Get default value in array of languages

        nameLTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

            }
        }

//        Excute event
        btnClose.setOnClickListener {

            var positionFrom = nameLFrom.getSelectedItemPosition().toString()
            var positionTo = nameLTo.getSelectedItemPosition().toString()

            val intent = Intent(this, Home::class.java)
            intent.putExtra("valueImage", textAfterDetect.text.toString())
            intent.putExtra("positionF", positionFrom)
            intent.putExtra("positionT", positionTo)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

        btnUseCamera.setOnClickListener {

            btnUseCamera.setTextColor(Color.parseColor("#5491FF"))
            piccam.setColorFilter(Color.parseColor("#5491FF"))
            btnUseGallery.setTextColor(Color.parseColor("#989898"))
            picgallery.setColorFilter(Color.parseColor("#989898"))

            cameraCheckPermission()

        }

        btnUseGallery.setOnClickListener {

            btnUseGallery.setTextColor(Color.parseColor("#5491FF"))
            picgallery.setColorFilter(Color.parseColor("#5491FF"))
            btnCamera.setTextColor(Color.parseColor("#989898"))
            piccam.setColorFilter(Color.parseColor("#989898"))

            galleryCheckPermission()
        }

        btnSwap.setOnClickListener {

            var positionFrom = nameLFrom.getSelectedItemPosition()
            var positionTo = nameLTo.getSelectedItemPosition()
            swapLanguage(positionFrom, positionTo)
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
    private fun cameraCheckPermission() {

        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            ).withListener(

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
                    imageView.setImageBitmap(bitmap)
                    convertImageToTextFromBitmap(bitmap)
                    show("Pause Translation")
                }

                GALLERY_REQUEST_CODE -> {

                    val imageUri = data?.data
                    imageView.load(imageUri)
                    convertImageToTextFromURI(imageUri)
                    show("Pause Translation")
                }
            }
        }
    }

    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage(
                "you have turned off permissions"
                        + "required for this feature. It can be enable under App settings!"
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

        val textAfterDetect: TextView = findViewById(R.id.valueAfterDetect)
        inputImage = InputImage.fromFilePath(applicationContext, imageUri!!)
        val result: Task<Text> = textRecognizer.process(inputImage)
            .addOnSuccessListener {

                textAfterDetect.text = it.text
            }.addOnFailureListener {

                show("Cant get data from this image")
            }
    }

    private fun convertImageToTextFromBitmap(bitmap: Bitmap) {

        val textAfterDetect: TextView = findViewById(R.id.valueAfterDetect)
        val image = InputImage.fromBitmap(bitmap, 0)
        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->

                textAfterDetect.text = visionText.text
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

        if (requestCode == STORAGE_PERMISSION_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)

                show("Storage Permission Granted")
            else

                show("Storage Permission Denied")
        }
    }

//    Function -- Swap language
//    Swap between languages
    private fun swapLanguage(positionFrom: Int, positionTo: Int){

        val nameLFrom: Spinner = findViewById(R.id.nameLanguageFrom)
        val nameLTo: Spinner = findViewById(R.id.nameLanguageTo)

        nameLFrom.setSelection(positionTo)
        nameLTo.setSelection(positionFrom)
}
}