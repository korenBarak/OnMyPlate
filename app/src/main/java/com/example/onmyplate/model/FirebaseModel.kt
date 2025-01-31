package com.example.onmyplate.model

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class FirebaseModel {
    private val db = Firebase.firestore
    private val cloudinaryModel = CloudinaryModel()

    fun addPost(post: Post, image: Bitmap?) {
        val postId = UUID.randomUUID().toString()

        if(image == null) {
            db.collection("posts")
                .document(postId)
                .set(post)
//            .addOnSuccessListener { documentReference ->
//                Log.d("TAG", "DocumentSnapshot added with ID: ${postId}")
//            }
//            .addOnFailureListener { e ->
//                Log.w("TAG", "Error adding document", e)
//            }
        } else {
            image.let {
                // TODO: to support multi images, each name will have the postId + index
                cloudinaryModel.uploadImage(
                    bitmap=image,
                    name=postId,
                    onSuccess={uri ->
                        if(!uri.isNullOrBlank()) {
                            val st = post.copy(photoUrl = uri)

                            db.collection("posts")
                                .document(postId)
                                .set(st)
                        }
                    },
                    onError= {

                    }
                )
            }
        }





    }
}