package com.example.voicetranslate.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import com.example.voicetranslate.R
import com.example.voicetranslate.models.Topic
import com.example.voicetranslate.ui.adapters.AdapterTopic
import kotlinx.android.synthetic.main.activity_show_offline_phrase_book.*

class ShowOfflinePhraseBookActivity : AppCompatActivity() {

    private val newArrayList: ArrayList<Topic> = arrayListOf()
    private var searchArrayList: ArrayList<Topic> = arrayListOf()
    private var filteredNames = ArrayList<Topic>()

    private val myAdapter: AdapterTopic by lazy { AdapterTopic(newArrayList) } //Chi khoi tao khi duoc goi

    private val imageShow: Array<Int> by lazy { arrayOf(
        R.drawable.ic_topic_bank,
        R.drawable.ic_topic_basic,
        R.drawable.ic_topic_beautycare,
        R.drawable.ic_topic_callpolice,
        R.drawable.ic_topic_communication,
        R.drawable.ic_topic_food,
        R.drawable.ic_topic_healthcare,
        R.drawable.ic_topic_hotel,
        R.drawable.ic_topic_restaurant,
        R.drawable.ic_topic_transport,
        R.drawable.ic_topic_laundry,
        R.drawable.ic_topic_shopping,
        R.drawable.ic_topic_sightseeing,
        R.drawable.ic_topic_sport,
        R.drawable.ic_topic_studying,
        R.drawable.ic_topic_traveling)}

    private val titleShow: Array<String> by lazy { arrayOf(
            "Bank", "Basic", "Beauty Care", "Calling for Police", "Communication Means", "Food and Drinks", "Health and Drugstore", "Hotel", "In the Restaurant", "Local Transport", "Repairs and Laundry", "Shopping", "Sightseeing", "Sport and Leisute", "Studying and Work", "Traveling"
        )
    }

    lateinit var intentDisplayFrom: String
    lateinit var intentLanguageFrom: String
    var intentFlagFrom: Int = 0
    lateinit var intentDisplayTo: String
    lateinit var intentLanguageTo: String
    var intentFlagTo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_offline_phrase_book)

        getData()

        et_search.doAfterTextChanged {
            val valueSearch = et_search.text.toString()
            filters(valueSearch)
            if (valueSearch == "")
                getTopic()
        }

        btn_close.setOnClickListener {

            onBackPressed()
        }

        list_item.layoutManager = GridLayoutManager(this, 2)
        list_item.setHasFixedSize(true)
        getTopic()
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
            requestCode == 998 && resultCode == Activity.RESULT_OK -> {
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

    private fun getData() {
        intentDisplayFrom = intent.getStringExtra("displayFrom").toString()
        intentLanguageFrom = intent.getStringExtra("languageFrom").toString()
        intentFlagFrom = intent.getIntExtra("flagFrom", R.drawable.ic_flag_english)
        intentDisplayTo = intent.getStringExtra("displayTo").toString()
        intentLanguageTo = intent.getStringExtra("languageTo").toString()
        intentFlagTo = intent.getIntExtra("flagTo", R.drawable.ic_flag_vietnamese)
    }

    private fun getTopic() {
        newArrayList.clear()
        for (i in titleShow.indices) {
            val topics = Topic(titleShow[i], imageShow[i])
            newArrayList.add(topics)
        }
        searchArrayList = newArrayList
        list_item.adapter = myAdapter
        myAdapter.setOnItemClickListener(object : AdapterTopic.onItemClickListener {
            override fun onItemClick(position: Int) {

                val titleToContent = if (filteredNames.size == 0)
                    searchArrayList[position].title.toString()
                else
                    filteredNames[position].title.toString()

                val intent = Intent(this@ShowOfflinePhraseBookActivity, ShowContentActivity::class.java)
                intent.putExtra("title", titleToContent)
                intent.putExtra("displayFrom", intentDisplayFrom)
                intent.putExtra("languageFrom", intentLanguageFrom)
                intent.putExtra("flagFrom", intentFlagFrom)
                intent.putExtra("displayTo", intentDisplayTo)
                intent.putExtra("languageTo", intentLanguageTo)
                intent.putExtra("flagTo", intentFlagTo)
                startActivityForResult(intent, 998)
                overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
            }
        })
    }

    private fun filters(text: String) {
        filteredNames.clear()
        searchArrayList.filterTo(filteredNames) {

            it.title.toString().lowercase().contains(text.lowercase())
        }
        myAdapter.filterList(filteredNames)
    }
}