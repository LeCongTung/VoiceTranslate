package com.example.voicetranslate.ui.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voicetranslate.R
import com.example.voicetranslate.extensions.SwipeAction
import com.example.voicetranslate.models.Image
import com.example.voicetranslate.models.Pin
import com.example.voicetranslate.ui.adapters.AdapterImage
import com.example.voicetranslate.ui.adapters.AdapterPin
import com.example.voicetranslate.ui.viewmodels.ImageViewModel
import com.example.voicetranslate.ui.viewmodels.PinViewModel
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.dialog_delete.view.*
import java.util.*


class GalleryActivity : AppCompatActivity(), AdapterImage.OnItemClickListener, AdapterPin.OnItemClickListener {

    private val RECORD_SPEECH_TEXT = 102

    private lateinit var intentDisplayFrom: String
    private lateinit var intentLanguageFrom: String
    private var intentFlagFrom: Int = 0
    private lateinit var intentDisplayTo: String
    private lateinit var intentLanguageTo: String
    private var intentFlagTo: Int = 0

    private lateinit var imageViewModel: ImageViewModel
    private lateinit var pinViewModel: PinViewModel

    private lateinit var dialog: Dialog
    private var deleteArrayList: ArrayList<Int> = arrayListOf()
    private var isListRecent: Boolean = true

    private lateinit var adapterImage: AdapterImage
    private lateinit var adapterPin: AdapterPin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        getData()
        getImage()

        dismissDialog.setOnClickListener {
            displayMenu(false)
        }

        btn_navleft.setOnClickListener {
            displayMenu(true)
        }

        btn_offlineMode.setOnClickListener {
            val intent = Intent(this, ShowOfflinePhraseBookActivity::class.java)
            intent.putExtra("displayFrom", intentDisplayFrom)
            intent.putExtra("languageFrom", intentLanguageFrom)
            intent.putExtra("flagFrom", intentFlagFrom)
            intent.putExtra("displayTo", intentDisplayTo)
            intent.putExtra("languageTo", intentLanguageTo)
            intent.putExtra("flagTo", intentFlagTo)
            startActivityForResult(intent, 1004)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
            Handler().postDelayed({
                displayMenu(false)
            }, 300)
        }

        btn_voice.setOnClickListener {
            speechText(intentLanguageFrom, intentDisplayFrom)
        }

        btn_tocamera.setOnClickListener {
            onBackPressed()
        }

        btn_showRecent.setOnClickListener {
            if (!isListRecent){
                btn_showRecent.setBackgroundResource(R.drawable.bg_white_radius)
                btn_showPin.background = null
                btn_showRecent.isEnabled = false
                btn_showPin.isEnabled = true
                getImage()
                isListRecent = true
                deleteArrayList.clear()
                displayNav(false)
            }
        }

        btn_showPin.setOnClickListener {
            if (isListRecent){
                btn_showPin.setBackgroundResource(R.drawable.bg_white_radius)
                btn_showRecent.background = null
                btn_showRecent.isEnabled = true
                btn_showPin.isEnabled = false
                getPinned()
                isListRecent = false
                deleteArrayList.clear()
                displayNav(false)
            }
        }

        btn_edit.setOnClickListener {
            deleteArrayList.clear()
            if (btn_delete.isGone)
                displayNav(true)
            else{
                displayNav(false)
                if (isListRecent)
                    getImage()
                else
                    getPinned()
            }

        }

        btn_delete.setOnClickListener {
            showDialog()
        }

        btn_gallery.setOnClickListener {
            gallery()
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
            requestCode == 1004 && resultCode == Activity.RESULT_OK -> {
                data?.let {
                    intentDisplayFrom = data.getStringExtra("displayFrom").toString()
                    intentLanguageFrom = data.getStringExtra("languageFrom").toString()
                    intentFlagFrom = data.getIntExtra("flagFrom", 0)
                    intentDisplayTo = data.getStringExtra("displayTo").toString()
                    intentLanguageTo = data.getStringExtra("languageTo").toString()
                    intentFlagTo = data.getIntExtra("flagTo", 0)
                }
            }

            requestCode == RECORD_SPEECH_TEXT && resultCode == Activity.RESULT_OK -> {
                val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val intent = Intent(this, TranslateActivity::class.java)
                intent.putExtra("value", result?.get(0).toString())
                intent.putExtra("displayFrom", intentDisplayFrom)
                intent.putExtra("languageFrom", intentLanguageFrom)
                intent.putExtra("flagFrom", intentFlagFrom)
                intent.putExtra("displayTo", intentDisplayTo)
                intent.putExtra("languageTo", intentLanguageTo)
                intent.putExtra("flagTo", intentFlagTo)
                startActivityForResult(intent, 1004)
                overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
                Handler().postDelayed({
                    displayMenu(false)
                }, 300)
            }

            requestCode == 9090 && resultCode == Activity.RESULT_OK -> {
                val imageUri = data?.data!!
                val intent = Intent(this, ShowPhotoActivity::class.java)
                intent.putExtra("pathimage", imageUri.toString())
                intent.putExtra("displayFrom", intentDisplayFrom)
                intent.putExtra("languageFrom", intentLanguageFrom)
                intent.putExtra("flagFrom", intentFlagFrom)
                intent.putExtra("displayTo", intentDisplayTo)
                intent.putExtra("languageTo", intentLanguageTo)
                intent.putExtra("flagTo", intentFlagTo)
                startActivityForResult(intent, 1004)
                overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
            }
        }
    }

    private fun getData() {
        intentDisplayFrom = intent.getStringExtra("displayFrom").toString()
        intentLanguageFrom = intent.getStringExtra("languageFrom").toString()
        intentFlagFrom = intent.getIntExtra("flagFrom", R.drawable.ic_flag_english)
        intentDisplayTo = intent.getStringExtra("displayTo").toString()
        intentLanguageTo = intent.getStringExtra("languageTo").toString()
        intentFlagTo = intent.getIntExtra("flagTo", R.drawable.ic_flag_vietnamese)
    }

    private fun speechText(voice: String?, display: String) {
        if (!SpeechRecognizer.isRecognitionAvailable(this))

            Log.e("error", "Error")
        else {

            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.forLanguageTag(voice.toString()))
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something in $display!")
            startActivityForResult(i, RECORD_SPEECH_TEXT)
        }
    }

    private fun displayMenu(isMenu: Boolean) {
        if (isMenu) {
            btn_navleft.setColorFilter(
                ContextCompat.getColor(this, R.color.disable),
                PorterDuff.Mode.SRC_IN
            )
            menuleft.visibility = View.VISIBLE
            dismissDialog.visibility = View.VISIBLE
        } else {
            btn_navleft.setColorFilter(
                ContextCompat.getColor(this, R.color.enable),
                PorterDuff.Mode.SRC_IN
            )
            menuleft.visibility = View.INVISIBLE
            dismissDialog.visibility = View.GONE
        }
    }

    private fun displayNav(isEdit: Boolean) {
        if (isEdit) {
            btn_edit.setImageResource(R.drawable.ic_checked)
            btn_tocamera.visibility = View.GONE
            btn_delete.visibility = View.VISIBLE
            btn_gallery.visibility = View.GONE
            btn_multipleChoice.visibility = View.VISIBLE
            menuleft.visibility = View.GONE
            dismissDialog.visibility = View.GONE
            btn_navleft.isEnabled = false
            btn_navleft.setColorFilter(ContextCompat.getColor(this, R.color.disable), PorterDuff.Mode.SRC_IN)

            if (deleteArrayList.isNotEmpty()){
                btn_delete.isEnabled = true
                btn_delete.setColorFilter(
                    ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_IN
                )

            }else{
                btn_delete.isEnabled = false
                btn_delete.setColorFilter(ContextCompat.getColor(this, R.color.disable), PorterDuff.Mode.SRC_IN)
            }
        } else {
            btn_edit.setImageResource(R.drawable.ic_edit)
            btn_tocamera.visibility = View.VISIBLE
            btn_delete.visibility = View.GONE
            btn_gallery.visibility = View.VISIBLE
            btn_multipleChoice.visibility = View.GONE
            btn_navleft.isEnabled = true
            btn_navleft.setColorFilter(
                ContextCompat.getColor(this, R.color.enable),
                PorterDuff.Mode.SRC_IN
            )
        }
        Log.e("abc", btn_multipleChoice.isEnabled.toString())
    }

    //    Gallery
    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 9090)
    }

    private fun getImage() {
        ItemTouchHelper(swipeActionImage).attachToRecyclerView(list_item)
        adapterImage = AdapterImage(this)
        list_item.adapter = adapterImage
        list_item.layoutManager = LinearLayoutManager(this)
        list_item.setHasFixedSize(true)
        imageViewModel = ViewModelProvider(this).get(ImageViewModel::class.java)
        imageViewModel.readAllData.observe(this) {
            adapterImage.setData(it)
        }
    }

    private val swipeActionImage by lazy {
        object : SwipeAction(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        imageViewModel.deleteRow(adapterImage.imageList[viewHolder.adapterPosition].id)
                    }
                }
            }
        }
    }

    private fun getPinned() {
        adapterPin = AdapterPin(this)
        list_item.adapter = adapterPin
        list_item.layoutManager = LinearLayoutManager(this)
        list_item.setHasFixedSize(true)
        pinViewModel = ViewModelProvider(this).get(PinViewModel::class.java)
        pinViewModel.readAllData.observe(this) {
            adapterPin.setData(it)
        }
        ItemTouchHelper(swipeActionPin).attachToRecyclerView(list_item)
    }

    private val swipeActionPin by lazy {
        object : SwipeAction(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        pinViewModel.deleteRow(adapterPin.pinList[viewHolder.adapterPosition].id)
                    }
                }
            }
        }
    }

    override fun onItemClick(image: Image) {
        val intent = Intent(this, ShowDetailActivity::class.java)
        intent.putExtra("pathimage", image.pathImage)
        intent.putExtra("displayFrom", image.fromLang)
        intent.putExtra("languageFrom", image.fromLangeUse)
        intent.putExtra("flagFrom", image.fromFlagLang)
        intent.putExtra("displayTo", image.toLang)
        intent.putExtra("languageTo", image.toLangeUse)
        intent.putExtra("flagTo", image.toFlagLang)
        intent.putExtra("from", image.type)
        intent.putExtra("pin", isListRecent)
        intent.putExtra("id", image.id)
        startActivityForResult(intent, 1004)
    }

    override fun onLongClick(id: Int) {
        if (deleteArrayList.size == 0) {
            deleteArrayList.add(id)
            displayNav(true)
        }
    }

    override fun onUnDeleteClick(id: Int) {
        if (deleteArrayList.size > 0){
            if (deleteArrayList.contains(id)){
                deleteArrayList.remove(id)
                displayNav(true)
            } else{
                deleteArrayList.add(id)
            }
        }
    }

    override fun onItemClickPin(pin: Pin) {
        val intent = Intent(this, ShowDetailActivity::class.java)
        intent.putExtra("pathimage", pin.pathImage)
        intent.putExtra("displayFrom", pin.fromLang)
        intent.putExtra("languageFrom", pin.fromLangeUse)
        intent.putExtra("flagFrom", pin.fromFlagLang)
        intent.putExtra("displayTo", pin.toLang)
        intent.putExtra("languageTo", pin.toLangeUse)
        intent.putExtra("flagTo", pin.toFlagLang)
        intent.putExtra("from", pin.type)
        intent.putExtra("pin", isListRecent)
        intent.putExtra("id", pin.id)
        startActivityForResult(intent, 1004)
    }

    override fun onLongClickPin(id: Int) {
        if (deleteArrayList.size == 0) {
            deleteArrayList.add(id)
            displayNav(true)
        }
    }

    override fun onUnDeleteClickPin(id: Int) {
        if (deleteArrayList.size > 0){
            if (deleteArrayList.contains(id)){
                deleteArrayList.remove(id)
                displayNav(true)
            } else{
                deleteArrayList.add(id)
            }
        }
    }

    private fun deleteItems() {
        if (isListRecent){
            for (i in deleteArrayList)
                imageViewModel.deleteRow(i)
            getImage()
        } else{
            for (i in deleteArrayList)
                pinViewModel.deleteRow(i)
            getPinned()
        }
        deleteArrayList.clear()
        displayNav(true)
    }

    private fun showDialog(){
        val viewDialog = View.inflate(this, R.layout.dialog_delete, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(viewDialog)

        dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.color.blurBlack)

        viewDialog.btn_deleteDialog.setOnClickListener {
            deleteItems()
            dialog.dismiss()
        }

        viewDialog.btn_cancelDialog.setOnClickListener {
            dialog.dismiss()
        }
    }
}