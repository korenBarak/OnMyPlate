package com.example.onmyplate.model

import android.net.Uri

data class User(
    val password: String,
    val email: String,
    val name: String? = null,
    val profilePictureUrl: Uri? = null,
) {
    fun firestoreUserToDbUser(userId: String): DbUser {
        return DbUser(
            userId = userId,
            email = this.email,
            name = this.name,
            profilePictureUrl = this.profilePictureUrl
        )
    }
}


data class DbUser(
    val userId: String,
    val email: String,
    val name: String? = null,
    val profilePictureUrl: Uri? = null,
)