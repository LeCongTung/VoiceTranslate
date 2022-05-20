package com.example.voicetranslate.shows

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voicetranslate.R
import com.example.voicetranslate.adapters.AdapterExample
import com.example.voicetranslate.adapters.AdapterTopic
import com.example.voicetranslate.models.Topic
import com.example.voicetranslate.screens.Home
import java.util.*
import kotlin.collections.ArrayList

class ShowOfflinePhraseBook : AppCompatActivity() {

    lateinit var imageShow: Array<Int>
    lateinit var titleShow: Array<String>

    val newArrayList: ArrayList<Topic> = arrayListOf()
    var searchArrayList: ArrayList<Topic> = arrayListOf()

    var filteredNames = ArrayList<Topic>()

    val myAdapter: AdapterTopic by lazy { AdapterTopic(newArrayList) } //Chi khoi tao khi duoc goi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_offline_phrase_book)

//        Init
        val etSearch: EditText = findViewById(R.id.et_search)
        val listItem: RecyclerView = findViewById(R.id.list_item)

//        Excute event -- when click button
//        Array of a logo per topic
        imageShow = arrayOf(
            R.drawable.ic_topic_bank,
            R.drawable.ic_topic_basic,
            R.drawable.ic_topic_beautycare,
            R.drawable.ic_topic_callpolice,
            R.drawable.ic_topic_communication,
            R.drawable.ic_topic_food,
            R.drawable.ic_topic_health,
            R.drawable.ic_topic_hotel,
            R.drawable.ic_topic_restaurant,
            R.drawable.ic_topic_transport,
            R.drawable.ic_topic_repairs,
            R.drawable.ic_topic_shopping,
            R.drawable.ic_topic_sightseeing,
            R.drawable.ic_topic_sport,
            R.drawable.ic_topic_studying,
            R.drawable.ic_topic_traveling,
        )

        titleShow = arrayOf(
            "Bank",
            "Basic",
            "Beauty Care",
            "Calling for Police",
            "Communication Means",
            "Food and Drinks",
            "Health and Drugstore",
            "Hotel",
            "In the restaurant",
            "Local transport",
            "Repair and Laundry",
            "Shopping",
            "SightSeeing",
            "Sport and Leisute",
            "Studying and Work",
            "Traveling"
        )

        val valueSearch = etSearch.text.toString()
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

                filters(p0.toString())
                if (valueSearch.equals(""))
                    getData()

            }
        })

//        Format recycleview topic
        listItem.layoutManager = GridLayoutManager(this, 2)
        listItem.setHasFixedSize(true)
        getData()
    }

    //    Function -- Click back button to close app
    override fun onBackPressed() {

        val intent = Intent(this@ShowOfflinePhraseBook, Home::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
    }

    //    Show data to recycleview
    private fun getData() {

        newArrayList.clear()
        val listItem: RecyclerView = findViewById(R.id.list_item)
        for (i in titleShow.indices) {
            val topics = Topic(titleShow[i], imageShow[i])
            newArrayList.add(topics)
        }

        searchArrayList = newArrayList
        listItem.adapter = myAdapter

        var titleToContent = ""
        myAdapter.setOnItemClickListener(object : AdapterTopic.onItemClickListener {
            override fun onItemClick(position: Int) {

                if (filteredNames.size == 0)
                     titleToContent = searchArrayList[position].title.toString()
                else
                    titleToContent = filteredNames[position].title.toString()


                val intent = Intent(this@ShowOfflinePhraseBook, ShowContent::class.java)
                intent.putExtra("title", titleToContent)
                startActivity(intent)
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