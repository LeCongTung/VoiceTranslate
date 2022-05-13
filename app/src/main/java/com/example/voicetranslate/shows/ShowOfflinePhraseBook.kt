package com.example.voicetranslate.shows

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voicetranslate.R
import com.example.voicetranslate.adapters.AdapterTopic
import com.example.voicetranslate.models.Topic
import com.example.voicetranslate.screens.Home

class ShowOfflinePhraseBook : AppCompatActivity() {



    lateinit var imageShow: Array<Int>
    lateinit var titleShow: Array<String>
    private lateinit var newArrayList: ArrayList<Topic>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_offline_phrase_book)

//        Init
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
            R.drawable.ic_topic_heathcare,
            R.drawable.ic_topic_hotel,
            R.drawable.ic_topic_restaurant,
            R.drawable.ic_topic_transport,
            R.drawable.ic_topic_repair,
            R.drawable.ic_topic_shopping,
            R.drawable.ic_topic_sightseeing,
            R.drawable.ic_topic_sport,
            R.drawable.ic_topic_study,
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

//        Format recycleview topic
        listItem.layoutManager = GridLayoutManager(this, 2)
        listItem.setHasFixedSize(true)
        newArrayList = arrayListOf<Topic>()
        getData()
    }

    //    Function -- Click back button to close app
    override fun onBackPressed() {

//        if (backPressedTime + 3000 > System.currentTimeMillis()) {
//            super.onBackPressed()
//            finishAffinity();
//        } else {
//            show("Press back again to leave the app")
//        }
//        backPressedTime = System.currentTimeMillis()

        val intent = Intent(this@ShowOfflinePhraseBook, Home::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
    }

    //    Function -- Toast
    private fun show(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    //    Show data to recycleview
    private fun getData() {

        var tradeadapter = AdapterTopic(newArrayList)
        val listItem: RecyclerView = findViewById(R.id.list_item)
        for (i in imageShow.indices) {

            val topics = Topic(titleShow[i], imageShow[i])
            newArrayList.add(topics)
        }

        listItem.adapter = tradeadapter
        tradeadapter.setOnItemClickListener(object : AdapterTopic.onItemClickListener{

            override fun onItemClick(position: Int) {

                val intent = Intent(this@ShowOfflinePhraseBook, ShowContent::class.java)
                intent.putExtra("title", newArrayList[position].title)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
            }
        })

    }
}