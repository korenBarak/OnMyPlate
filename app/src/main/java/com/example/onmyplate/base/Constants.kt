package com.example.onmyplate.base

import com.example.onmyplate.model.Comment
import com.example.onmyplate.model.Post
import com.example.onmyplate.model.User

typealias PostsCallback = (List<Post>) -> Unit
typealias CommentsCallback = (List<Comment>) -> Unit
typealias UserCallback = (User?) -> Unit

object Constants {
    object FirebaseCollections {
        const val POSTS = "posts"
        const val USERS = "users"
        const val COMMENTS = "comments"
    }

    object CloudinaryFolders {
        const val IMAGE = "image"
        const val PROFILE = "profile"
    }
}