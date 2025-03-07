package com.example.onmyplate.apiRequests

import okhttp3.Response
import okhttp3.Interceptor

class GoogleApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("accept", "application/json")
            .build()

        return chain.proceed(request)
    }
}