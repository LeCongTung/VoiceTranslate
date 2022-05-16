package com.example.voicetranslate.networks

import com.example.voicetranslate.models.DataItem
import retrofit2.Call
import retrofit2.http.GET

interface ApiDataItem {

    @GET("posts")
    fun getData(): Call<List<DataItem>>
}