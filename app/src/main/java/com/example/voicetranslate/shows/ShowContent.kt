package com.example.voicetranslate.shows

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voicetranslate.R
import com.example.voicetranslate.adapters.AdapterExample
import com.example.voicetranslate.models.DataItem
import com.example.voicetranslate.networks.ApiDataItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ShowContent : AppCompatActivity() {

    val BASE_URL = "https://jsonplaceholder.typicode.com/"
    var backPressedTime: Long = 0

    lateinit var myAdapter: AdapterExample

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_content)

//        Init
        val btnBack: TextView = findViewById(R.id.btn_back)
        val tvTitle: TextView = findViewById(R.id.title_content)
        val btnDone: TextView = findViewById(R.id.btn_done)
        val listItem: RecyclerView = findViewById(R.id.list_item)

//        Get data from previous screen
        val title = intent.getStringExtra("title").toString()
        tvTitle.setText(title)

//        Excute button -- when click button
        btnBack.setOnClickListener {

            val intent = Intent(this, ShowOfflinePhraseBook::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

//        Api
        listItem.layoutManager = LinearLayoutManager(this)
        listItem.setHasFixedSize(true)
        getDataExample()
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

//    Get data
    private fun getDataExample() {

        val listItem: RecyclerView = findViewById(R.id.list_item)
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiDataItem::class.java)

        val retrofitData = retrofitBuilder.getData()

        retrofitData.enqueue(object : Callback<List<DataItem>?> {
            override fun onResponse(call: Call<List<DataItem>?>, response: Response<List<DataItem>?>) {

                val responseBody = response.body()!!

                myAdapter = AdapterExample(baseContext, responseBody)
                myAdapter.notifyDataSetChanged()
                listItem.adapter = myAdapter
            }

            override fun onFailure(call: Call<List<DataItem>?>, t: Throwable) {
                show("Error")
            }
        })
    }
}