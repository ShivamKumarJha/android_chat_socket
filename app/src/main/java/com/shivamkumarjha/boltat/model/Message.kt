package com.shivamkumarjha.boltat.model

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("name") val name: String,
    @SerializedName("message") val message: String,
    @SerializedName("image") val image: String,
    @SerializedName("date") val date: String,
    @SerializedName("viewType") var viewType: Int
)