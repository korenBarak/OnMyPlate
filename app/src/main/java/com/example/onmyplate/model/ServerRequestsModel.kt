package com.example.onmyplate.model

import android.graphics.Bitmap
import com.example.onmyplate.base.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class ServerRequestsModel {
    private val cloudinaryModel: CloudinaryModel = CloudinaryModel()
    private val firebaseModel: FirebaseModel = FirebaseModel()

    fun addPost(post: Post, images: MutableList<Bitmap>) {
        val postId = UUID.randomUUID().toString()

        if (images.size == 0) {
            firebaseModel.addPost(postId, post)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                images.let {
                    cloudinaryModel.uploadImages(
                        bitmaps = images,
                        name = postId,
                        onSuccess = { urls ->
                            if (urls.isNotEmpty()) {
                                val postWithPhoto = post.copy(photoUrls = urls)
                                firebaseModel.addPost(postId, postWithPhoto)
                            }
                        },
                        onError = {

                        }
                    )
                }
            }

        }
    }
}