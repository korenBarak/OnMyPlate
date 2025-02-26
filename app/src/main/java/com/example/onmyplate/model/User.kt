package com.example.onmyplate.model

import android.net.Uri

data class User(
    val password: String,
    val email: String,
    val name: String? = null,
    val profilePictureUrl: Uri? = null,
)
