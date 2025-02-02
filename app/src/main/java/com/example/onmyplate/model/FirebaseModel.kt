package com.example.onmyplate.model
import com.example.onmyplate.base.Constants
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseModel {
    private val db = Firebase.firestore

    fun addPost(postId: String, post: Post) {
        db.collection(Constants.FirebaseCollections.POSTS)
            .document(postId)
            .set(post)
    }
}