package com.example.onmyplate.base

import com.example.onmyplate.model.Post

typealias PostsCallback = (List<Post>) -> Unit

object Constants {
    object FirebaseCollections {
        const val POSTS = "posts"
        const val USERS = "users"
    }

    object CloudinaryFolders {
        const val IMAGE = "image"
        const val PROFILE = "profile"
    }

    object ApiBaseUrl {
        const val GOOGLE_MAP = "https://maps.googleapis.com/maps/api/place/"
    }
}