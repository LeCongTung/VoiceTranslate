package com.example.voicetranslate.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Image")
data class Image(
    @PrimaryKey val time: String,
    val pathImage: String,
    val fromLang: String,
    val fromLangeUse: String,
    val fromFlagLang: Int,
    val toLang: String,
    val toLangeUse: String,
    val toFlagLang: Int,
    val type: String,
    val pinned: Int
)

@Entity(tableName = "Saved")
data class Saved(
    @PrimaryKey val id: String,
    val language: String,
    val image: Int,
    val time: String,
    val type: String
)
