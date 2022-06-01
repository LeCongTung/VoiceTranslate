package com.example.voicetranslate.shows

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicetranslate.R
import com.example.voicetranslate.adapters.AdapterExample
import com.example.voicetranslate.models.DataItem
import com.example.voicetranslate.networks.ApiDataItem
import com.example.voicetranslate.screens.Home
import kotlinx.android.synthetic.main.activity_show_content.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ShowContent : AppCompatActivity() {

    val BASE_URL = "https://jsonplaceholder.typicode.com/"

    var filteredNames = ArrayList<DataItem>()
    var dataList = ArrayList <DataItem> ()

    val myAdapter: AdapterExample by lazy { AdapterExample(this, dataList) } //Chi khoi tao khi duoc goi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_content)

        et_search.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

                filters(p0.toString())
            }
        })

//        Get data from previous screen
        val title = intent.getStringExtra("title")
        title_content.setText(title)

//        Excute button -- when click button
        btn_back.setOnClickListener {

            val intent = Intent(this, ShowOfflinePhraseBook::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
        }

//        Api
        list_item.adapter = myAdapter
        list_item.layoutManager = LinearLayoutManager(this)
        list_item.setHasFixedSize(true)
        getDataExample()
    }

    //    Function -- Click back button to close app
    override fun onBackPressed() {

        val intent = Intent(this, ShowOfflinePhraseBook::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
    }

//    Function -- Toast
    private fun show(message: String) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

//    Get data
    private fun getDataExample() {

        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiDataItem::class.java)

        val retrofitData = retrofitBuilder.getData()

        retrofitData.enqueue(object : Callback<List<DataItem>?> {
            override fun onResponse(call: Call<List<DataItem>?>, response: Response<List<DataItem>?>) {

                dataList.addAll(response.body()!!)
                list_item.adapter!!.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<DataItem>?>, t: Throwable) {
                show("Error")
            }
        })

        var languageChange = ""
        myAdapter.setOnItemClickListener(object : AdapterExample.onItemClickListener {
            override fun onItemClick(position: Int) {

                if (filteredNames.size == 0)
                    languageChange = dataList[position].title
                else
                    languageChange = filteredNames[position].title

                val intent = Intent(this@ShowContent, Home::class.java)
                intent.putExtra("value", languageChange)
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
            }
        })
    }
//    Function -- Filter
    private fun filters(text: String) {

        filteredNames.clear()
        dataList!!.filterTo(filteredNames) {

            it.title.lowercase().contains(text.lowercase())
        }
        myAdapter!!.filterList(filteredNames)
    }
}