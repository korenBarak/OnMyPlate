package com.example.onmyplate.base

import com.example.onmyplate.model.Post

typealias PostsCallback = (List<Post>) -> Unit

object Constants {
    object FirebaseCollections {
        const val POSTS = "posts"
    }

    object CloudinaryFolders {
        const val IMAGE = "image"
        const val PROFILE = "profile"
    }
}