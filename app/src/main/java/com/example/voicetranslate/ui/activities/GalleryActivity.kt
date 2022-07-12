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
import com.example.voicetranslate.extensions.SwipeActionPin
import com.example.voicetranslate.models.Image
import com.example.voicetranslate.ui.adapters.AdapterImage
import com.example.voicetranslate.ui.adapters.AdapterPin
import com.example.voicetranslate.ui.viewmodels.ImageViewModel
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.dialog_delete.view.*
import java.util.*


class GalleryActivity : AppCompatActivity(), AdapterImage.OnItemClickListener,
    AdapterPin.OnItemClickListener {

    private val RECORD_SPEECH_TEXT = 102

    private lateinit var intentDisplayFrom: String
    private lateinit var intentLanguageFrom: String
    private var intentFlagFrom: Int = 0
    private lateinit var intentDisplayTo: String
    private lateinit var intentLanguageTo: String
    private var intentFlagTo: Int = 0

    private lateinit var imageViewModel: ImageViewModel

    private lateinit var dialog: Dialog

    private var isListRecent: Boolean = true

    private lateinit var adapterImage: AdapterImage
    private lateinit var adapterPin: AdapterPin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        imageViewModel = ViewModelProvider(this).get(ImageViewModel::class.java)

        getData()
        getPinned()
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
            btn_showRecent.setBackgroundResource(R.drawable.bg_white_radius)
            btn_showPin.background = null
            btn_showRecent.isEnabled = false
            btn_showPin.isEnabled = true
            getImage()
            adapterImage.quantity.clear()
            adapterPin.quantity.clear()
            displayNav(false)
            isListRecent = true
        }

        btn_showPin.setOnClickListener {
            btn_showPin.setBackgroundResource(R.drawable.bg_white_radius)
            btn_showRecent.background = null
            btn_showRecent.isEnabled = true
            btn_showPin.isEnabled = false
            getPinned()
            adapterImage.quantity.clear()
            adapterPin.quantity.clear()
            displayNav(false)
            btn_delete.setColorFilter(
                ContextCompat.getColor(this, R.color.disable),
                PorterDuff.Mode.SRC_IN
            )
            isListRecent = false
        }

        btn_edit.setOnClickListener {
            if (btn_delete.isGone)
                displayNav(true)
            else {
                displayNav(false)
                if (isListRecent) {
                    getImage()
                    adapterImage.quantity.clear()
                } else {
                    getPinned()
                    adapterPin.quantity.clear()
                }

            }
        }

        btn_delete.setOnClickListener {
            showDialog()
        }

        btn_multipleChoice.setOnClickListener {
            if (isListRecent)
                if (adapterImage.quantity.size == adapterImage.imageList.size)
                    clearSelected()
                else
                    selectedAll()
            else
                if (adapterPin.quantity.size == adapterPin.pinList.size)
                    clearSelectedPin()
                else
                    selectedAllPin()
            displayNav(true)
            Log.d("abc", "${adapterImage.quantity.size}")
            Log.d("abc", "${adapterPin.quantity.size}")
        }

        btn_gallery.setOnClickListener {
            gallery()
            displayMenu(false)
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
                displayNav(false)
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
//            True: Show edit layout
            btn_edit.setImageResource(R.drawable.ic_checked)
            btn_tocamera.visibility = View.GONE
            btn_delete.visibility = View.VISIBLE
            btn_gallery.visibility = View.GONE
            btn_multipleChoice.visibility = View.VISIBLE
            menuleft.visibility = View.GONE
            dismissDialog.visibility = View.GONE
            btn_navleft.isEnabled = false
            btn_navleft.setColorFilter(
                ContextCompat.getColor(this, R.color.disable),
                PorterDuff.Mode.SRC_IN
            )
        } else {
//            False: Hide edit layout, comeback default layout
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

        if (adapterImage.quantity.isNotEmpty() || adapterPin.quantity.isNotEmpty()) {
            btn_delete.isEnabled = true
            btn_delete.setColorFilter(
                ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_IN
            )

        } else {
            btn_delete.isEnabled = false
            btn_delete.setColorFilter(
                ContextCompat.getColor(this, R.color.disable),
                PorterDuff.Mode.SRC_IN
            )
        }
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
        list_item.visibility = View.VISIBLE
        list_item_pinned.visibility = View.GONE
        list_item.adapter = adapterImage
        list_item.layoutManager = LinearLayoutManager(this)
        imageViewModel.readAllData.observe(this) {
            adapterImage.setData(it)
        }
    }

    private fun getPinned() {
        ItemTouchHelper(swipeActionPinned).attachToRecyclerView(list_item_pinned)
        adapterPin = AdapterPin(this)
        list_item.visibility = View.GONE
        list_item_pinned.visibility = View.VISIBLE
        list_item_pinned.adapter = adapterPin
        list_item_pinned.layoutManager = LinearLayoutManager(this)
        imageViewModel.readAllPinned.observe(this) {
            adapterPin.setData(it)
        }
    }

    private val swipeActionImage by lazy {
        object : SwipeAction(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        imageViewModel.deleteByTime(adapterImage.imageList[viewHolder.adapterPosition].time)
                        adapterImage.quantity.clear()
                        displayNav(true)
                        getImage()
                    }
                    ItemTouchHelper.RIGHT -> {
//                        val pinImage = Image(
//                            adapterImage.imageList[viewHolder.adapterPosition].time,
//                            adapterImage.imageList[viewHolder.adapterPosition].pathImage,
//                            adapterImage.imageList[viewHolder.adapterPosition].fromLang,
//                            adapterImage.imageList[viewHolder.adapterPosition].fromLangeUse,
//                            adapterImage.imageList[viewHolder.adapterPosition].fromFlagLang,
//                            adapterImage.imageList[viewHolder.adapterPosition].toLang,
//                            adapterImage.imageList[viewHolder.adapterPosition].toLangeUse,
//                            adapterImage.imageList[viewHolder.adapterPosition].toFlagLang,
//                            adapterImage.imageList[viewHolder.adapterPosition].type,
//                            1
//                        )
                        imageViewModel.updatePinByTime(adapterImage.imageList[viewHolder.adapterPosition].time)
                        adapterImage.quantity.clear()
                        displayNav(false)
                        adapterImage.checkColor = false
                    }
                }
            }
        }
    }

    private val swipeActionPinned by lazy {
        object : SwipeActionPin(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
//                        val unpinImage = Image(
//                            adapterPin.pinList[viewHolder.adapterPosition].time,
//                            adapterPin.pinList[viewHolder.adapterPosition].pathImage,
//                            adapterPin.pinList[viewHolder.adapterPosition].fromLang,
//                            adapterPin.pinList[viewHolder.adapterPosition].fromLangeUse,
//                            adapterPin.pinList[viewHolder.adapterPosition].fromFlagLang,
//                            adapterPin.pinList[viewHolder.adapterPosition].toLang,
//                            adapterPin.pinList[viewHolder.adapterPosition].toLangeUse,
//                            adapterPin.pinList[viewHolder.adapterPosition].toFlagLang,
//                            adapterPin.pinList[viewHolder.adapterPosition].type,
//                            0
//                        )
                        imageViewModel.updateUnPinByTime(adapterPin.pinList[viewHolder.adapterPosition].time)
                        adapterPin.quantity.clear()
                        displayNav(true)
                        adapterPin.checkColor = false
                        Log.d("abc", adapterImage.imageList[viewHolder.adapterPosition].time)
                    }
                }
            }
        }
    }

    override fun onItemClick(image: Image) {
        if (adapterImage.quantity.size == 0) {
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
            intent.putExtra("time", image.time)
            startActivityForResult(intent, 1004)
        }
    }

    override fun onLongClick(image: Image) {
        displayNav(true)
    }

    override fun onUnDeleteClick(image: Image) {
        displayNav(true)
    }

    override fun onItemClickPin(image: Image) {
        if (adapterPin.quantity.size == 0) {
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
            intent.putExtra("time", image.time)
            startActivityForResult(intent, 1004)
        }
    }

    override fun onLongClickPin(image: Image) {
        displayNav(true)
    }

    override fun onUnDeleteClickPin(image: Image) {
        displayNav(true)
    }

    private fun deleteItems() {
        if (isListRecent) {
            for (i in adapterImage.quantity)
                imageViewModel.delete(i)
            adapterImage.checkColor = false
            adapterImage.quantity.clear()
        } else {
            for (i in adapterPin.quantity){
                imageViewModel.updateUnPinByTime(i.time)
            }

            adapterPin.checkColor = false
            adapterPin.quantity.clear()
        }
        displayNav(true)
    }

    private fun showDialog() {
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

    private fun selectedAll() {
        adapterImage.selectedAll()
        adapterImage.checkColor = true
        adapterImage.notifyDataSetChanged()
    }

    private fun clearSelected() {
        adapterImage.clearSelectedAll()
        adapterImage.checkColor = false
        adapterImage.notifyDataSetChanged()
    }

    private fun selectedAllPin() {
        adapterPin.selectedAll()
        adapterPin.checkColor = true
        adapterPin.notifyDataSetChanged()
    }

    private fun clearSelectedPin() {
        adapterPin.clearSelectedAll()
        adapterPin.checkColor = false
        adapterPin.notifyDataSetChanged()
    }
}