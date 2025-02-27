package com.example.onmyplate.model

import android.graphics.Bitmap
import com.example.onmyplate.base.Constants
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ServerRequestsModel {
    private val cloudinaryModel : CloudinaryModel = CloudinaryModel()
    private val firebaseModel : FirebaseModel = FirebaseModel()

    fun addPost(post: Post, image: Bitmap?) {
        val postId = UUID.randomUUID().toString()

        if(image == null) {
            firebaseModel.addPost(postId, post)
        } else {
            image.let {
                // TODO: to support multi images, each name will have the postId + index
                cloudinaryModel.uploadImage(
                    bitmap=image,
                    name=postId,
                    onSuccess={uri ->
                        if(!uri.isNullOrBlank()) {
                            val postWithPhoto = post.copy(photoUrl = uri)
                            firebaseModel.addPost(postId, postWithPhoto)
                        }
                    },
                    onError= {

                    }
                )
            }
        }
    }

    suspend fun getAllPosts(): List<Post> {
        return try {
            val snapshot = firebaseModel.getAllPosts().await()
            snapshot.documents.mapNotNull { it.toObject(Post::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}