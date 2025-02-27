package com.example.onmyplate.model

// TODO: add in the future id (uuid, generated by firebase), photoUrl should be string[]..
data class Post(
    val restaurantName: String,
    val tags: String,
    val description: String,
    val rating: Int,
    val photoUrls: List<String?>? = null
)
