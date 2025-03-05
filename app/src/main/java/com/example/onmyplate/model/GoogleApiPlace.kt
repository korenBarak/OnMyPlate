package com.example.onmyplate.model

import com.google.gson.annotations.SerializedName

data class GoogleApiPlace(
    @SerializedName("formatted_address")
    val formattedAddress: String,
    val name: String,
    @SerializedName("place_id")
    val placeId: String,
    val rating: Float
)
