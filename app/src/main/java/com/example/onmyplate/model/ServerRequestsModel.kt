package com.example.onmyplate.model

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import com.example.onmyplate.base.Constants
import com.example.onmyplate.base.MyApplication
import com.example.onmyplate.base.PostsCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

object ServerRequestsModel {
    private val cloudinaryModel: CloudinaryModel = CloudinaryModel()
    private val firebaseModel: FirebaseModel = FirebaseModel()


    fun addPost(post: Post, images: MutableList<Bitmap>) {
        val postId = UUID.randomUUID().toString()

        if (images.size == 0) {
            firebaseModel.addPost(postId, post)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                images.let {
                    cloudinaryModel.uploadPostImages(
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

    fun addNewUser(user: User, profilePictureUrl: Bitmap?) {
        if(profilePictureUrl == null) {
            firebaseModel.createNewUser(user)
        } else {
            cloudinaryModel.uploadImage(
                bitmap=profilePictureUrl,
                name="",
                folderName=Constants.CloudinaryFolders.PROFILE,
                onSuccess={url ->
                    if(!url.isNullOrBlank()) {
                        val userWithProfilePicture = user.copy(profilePictureUrl = Uri.parse(url))
                        firebaseModel.createNewUser(userWithProfilePicture)
                    }
                },
                onError= {
                    Toast.makeText(MyApplication.Globals.context, "ההרשמה נכשלה", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

     fun getAllPosts(callback: PostsCallback){
         firebaseModel.getAllPosts(callback)
    }

    fun updateUserDetails(user: User, bitmap: Bitmap) {
        val pictureName = user.profilePictureUrl.toString().split("/${Constants.CloudinaryFolders.PROFILE}").last()
        val pictureId = pictureName.split(".").first()

        cloudinaryModel.updateImage(
            bitmap=bitmap,
            name=pictureId,
            folderName=Constants.CloudinaryFolders.PROFILE,
            onSuccess={url ->
                if(!url.isNullOrBlank()) {
                    val userWithProfilePicture = user.copy(profilePictureUrl = Uri.parse(url))

                    firebaseModel.changeUserDetails(userWithProfilePicture)

                }
            },
            onError= {
                Toast.makeText(MyApplication.Globals.context, "ההרשמה נכשלה", Toast.LENGTH_SHORT).show()
            }
        )
    }
}