package com.example.onmyplate.base

import android.app.Application
import android.content.Context
import android.util.Log

class MyApplication : Application() {
    object Globals {
        var context : Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        Globals.context = applicationContext
    }
}