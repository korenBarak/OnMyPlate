package com.example.onmyplate.model

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.onmyplate.adapter.ImageData
import com.example.onmyplate.apiRequests.GoogleApiClient
import com.example.onmyplate.base.CommentsCallback
import com.example.onmyplate.base.Constants
import com.example.onmyplate.base.MyApplication
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

object ServerRequestsModel {
    private val cloudinaryModel: CloudinaryModel = CloudinaryModel()
    private val firebaseModel: FirebaseModel = FirebaseModel.shared

    fun addPost(post: Post, images: MutableList<Bitmap>, callback: (Post?) -> Unit) {
        val postId = UUID.randomUUID().toString()

        CoroutineScope(Dispatchers.IO).launch {
            images.let {
                cloudinaryModel.uploadPostImages(
                    bitmaps = images,
                    name = postId,
                    onSuccess = { urls ->
                        if (urls.isNotEmpty()) {
                            val postWithPhoto = post.copy(photoUrls = urls)
                            firebaseModel.addPost(postId, postWithPhoto)
                                .addOnCompleteListener { task ->
                                    callback(postWithPhoto.copy(postId = postId))
                                }
                        }
                    },
                    onError = {
                        callback(null)
                    }
                )
            }
        }
    }

    fun updatePost(
        postId: String,
        post: Post,
        imagesToSave: MutableList<Bitmap>,
        allPhotos: List<ImageData>,
        callback: (Post?) -> Unit
    ) {
        if (imagesToSave.size == 0) {
            val postWithPhotos = post.copy(
                photoUrls = allPhotos.filterIsInstance<ImageData.StringData>()
                    .map { photo -> photo.url })
            firebaseModel.updatePost(
                postId,
                postWithPhotos
            ).addOnCompleteListener {
                callback(postWithPhotos.copy(postId = postId))
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                imagesToSave.let {
                    cloudinaryModel.uploadPostImages(
                        bitmaps = imagesToSave,
                        name = postId,
                        onSuccess = { urls ->
                            if (urls.isNotEmpty()) {
                                var index = 0
                                val photoUrls = mutableListOf<String>()
                                allPhotos.forEach {
                                    if (it is ImageData.StringData)
                                        photoUrls.add(it.url)
                                    else {
                                        urls[index]?.let { url -> photoUrls.add(url) }
                                        index++
                                    }
                                }

                                val postWithPhoto = post.copy(photoUrls = photoUrls)
                                firebaseModel.updatePost(postId, postWithPhoto)
                                    .addOnCompleteListener {
                                        callback(postWithPhoto.copy(postId = postId))
                                    }
                            }
                        },
                        onError = {
                            callback(null)
                        }
                    )
                }
            }
        }
    }

    fun addNewUser(user: User, profilePictureUrl: Bitmap?) {
        if (profilePictureUrl == null) {
            firebaseModel.createNewUser(user)
        } else {
            cloudinaryModel.uploadImage(
                bitmap = profilePictureUrl,
                name = "",
                folderName = Constants.CloudinaryFolders.PROFILE,
                onSuccess = { url ->
                    if (!url.isNullOrBlank()) {
                        val userWithProfilePicture = user.copy(profilePictureUrl = Uri.parse(url))
                        firebaseModel.createNewUser(userWithProfilePicture)
                    }
                },
                onError = {
                    Toast.makeText(
                        MyApplication.Globals.context,
                        "ההרשמה נכשלה",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    fun addComment(comment: Comment): Task<Void> {
        val commentId = UUID.randomUUID().toString()
        return firebaseModel.addComment(commentId, comment)
    }

    fun getCommentsByPost(postId: String, callback: CommentsCallback) {
        firebaseModel.getCommentsByPost(postId, callback)
    }

    fun updateUserDetails(user: User, bitmap: Bitmap) {
        val pictureName =
            user.profilePictureUrl.toString().split("/${Constants.CloudinaryFolders.PROFILE}")
                .last()
        val pictureId = pictureName.split(".").first()

        cloudinaryModel.updateImage(
            bitmap = bitmap,
            name = pictureId,
            folderName = Constants.CloudinaryFolders.PROFILE,
            onSuccess = { url ->
                if (!url.isNullOrBlank()) {
                    val userWithProfilePicture = user.copy(profilePictureUrl = Uri.parse(url))

                    firebaseModel.changeUserDetails(userWithProfilePicture)

                }
            },
            onError = {
                Toast.makeText(MyApplication.Globals.context, "ההרשמה נכשלה", Toast.LENGTH_SHORT)
                    .show()
            }
        )
    }

    suspend fun getGoogleMapDetailsByPlaceName(placeName: String): List<GoogleApiPlace> {
        try {
            val request =
                GoogleApiClient.googleMapApiClient.getPlaceDetailsByName(placeName = placeName)

            val response = request.execute()

            if (response.isSuccessful) {
                val body = response.body()

                if (body?.status == "OK") {
                    return body.candidates
                }
            }
            return emptyList()
        } catch (e: Exception) {
            Log.d("ERROR", e.toString())
            return emptyList()
        }
    }

}