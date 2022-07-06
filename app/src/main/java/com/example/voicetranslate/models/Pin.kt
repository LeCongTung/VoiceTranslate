package com.example.voicetranslate.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Pin")
data class Pin(
    @PrimaryKey val id: Int,
    val pathImage: String,
    val fromLang: String,
    val fromLangeUse: String,
    val fromFlagLang: Int,
    val toLang: String,
    val toLangeUse: String,
    val toFlagLang: Int,
    val time: String,
    val type: String
)