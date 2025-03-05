package com.example.onmyplate.apiRequests

import com.example.onmyplate.BuildConfig
import com.example.onmyplate.model.GoogleApiPlaces
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleApi {
    @GET("findplacefromtext/json")
    fun getPlaceDetailsByName(
        @Query("inputtype") inputType: String = "textquery",
        @Query("key") apiKey: String = BuildConfig.GOOGLE_API_KEY,
        @Query("language") language: String = "iw",
        @Query("fields") returnedFields: String = "formatted_address,name,rating,place_id",
        @Query("input") placeName: String
    ) : Call<GoogleApiPlaces>
}