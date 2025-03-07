package com.example.onmyplate.apiRequests

import retrofit2.converter.gson.GsonConverterFactory
import com.example.onmyplate.base.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object GoogleApiClient {
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(GoogleApiInterceptor())
            .build()
    }

    val googleMapApiClient: GoogleApi by lazy {
        val retrofitClient = Retrofit.Builder()
            .baseUrl(Constants.ApiBaseUrl.GOOGLE_MAP).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()

        retrofitClient.create(GoogleApi::class.java)
    }
}