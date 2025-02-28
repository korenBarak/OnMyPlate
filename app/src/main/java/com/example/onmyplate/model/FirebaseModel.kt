package com.example.onmyplate.model

import android.widget.Toast
import com.example.onmyplate.base.Constants
import com.example.onmyplate.base.MyApplication
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
typealias PostsCallback = (List<Post>) -> Unit

class FirebaseModel {
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    fun addPost(postId: String, post: Post) {
        db.collection(Constants.FirebaseCollections.POSTS)
            .document(postId)
            .set(post)
    }

    fun createNewUser(user: User) {
        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    if (user.name != null || user.profilePictureUrl != null) {
                        val detailsToUpdate = userProfileChangeRequest {
                            displayName = user.name
                            photoUri = user.profilePictureUrl
                        }
                        val currentUser = auth.currentUser
                        currentUser?.updateProfile(detailsToUpdate)
                    }
                } else {
                    Toast.makeText(
                        MyApplication.Globals.context,
                        "ההרשמה נכשלה",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    fun getUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener() {
            task ->
            if(task.isSuccessful) {
//                 val user = auth.currentUser
            } else {
                Toast.makeText(
                    MyApplication.Globals.context,
                    "אחד או יותר מהפרטים אינם נכונים",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    fun getAllPosts(callback: PostsCallback): Task<QuerySnapshot> {
        return db.collection(Constants.FirebaseCollections.POSTS).get()
            .addOnSuccessListener { querySnapshot ->
                        val fetchedPosts = querySnapshot.documents.mapNotNull { it.toObject(Post::class.java) }
                        callback(fetchedPosts)
                    }
            .addOnFailureListener {
                callback(listOf())
            }
    }
}

//            .whereEqualTo("tags", "תת").get()
//            .addOnCompleteListener{Log.d("fv",
//            it.result.toString()
//        )}