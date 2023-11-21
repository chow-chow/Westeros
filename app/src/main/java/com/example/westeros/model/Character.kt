package com.example.westeros.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Character(
    @Json(name = "family")
    val family: String,
    @Json(name = "firstName")
    val firstName: String,
    @Json(name = "fullName")
    val fullName: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "image")
    val image: String,
    @Json(name = "imageUrl")
    val imageUrl: String,
    @Json(name = "lastName")
    val lastName: String,
    @Json(name = "title")
    val title: String
)