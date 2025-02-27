package com.example.onmyplate.model
import android.util.Log
import com.example.onmyplate.base.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseModel {
    private val db = Firebase.firestore

    fun addPost(postId: String, post: Post) {
        db.collection(Constants.FirebaseCollections.POSTS)
            .document(postId)
            .set(post)
    }

    fun getAllPosts(): Task<QuerySnapshot> {
        return db.collection(Constants.FirebaseCollections.POSTS).get()
//            .whereEqualTo("tags", "תת").get()
//            .addOnCompleteListener{Log.d("fv",
//            it.result.toString()
//        )}
    }
}