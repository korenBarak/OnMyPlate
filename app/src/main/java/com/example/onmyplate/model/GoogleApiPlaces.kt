package com.example.onmyplate.model


data class GoogleApiPlaces(
    val candidates: List<GoogleApiPlace>,
    val status: String
)