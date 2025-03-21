package com.example.onmyplate.model

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.onmyplate.base.CommentsCallback
import com.example.onmyplate.base.Constants
import com.example.onmyplate.base.MyApplication
import com.example.onmyplate.base.PostsCallback
import com.example.onmyplate.base.UserCallback
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class FirebaseModel private constructor() {
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    companion object {
        val shared = FirebaseModel()
    }

    fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun getAuthListener(listener: FirebaseAuth.AuthStateListener) {
        auth.addAuthStateListener(listener)
    }

    fun getUserById(userId: String, callback: UserCallback): Task<DocumentSnapshot> {
        return db.collection(Constants.FirebaseCollections.USERS).document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    var user = User()
                    val profilePictureUrl =
                        document.getString("profilePictureUrl")?.let { Uri.parse(it) }
                    val name = document.getString("name")
                    val email = document.getString("email")
                    if (name != null && email != null) {
                        user = User("", email, name, profilePictureUrl)
                    }
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                callback(null) // Handle failure
            }
    }


    fun signOutUser() {
        auth.signOut()
    }

    private fun createNewUserInDatabase(userId: String, user: DbUser) {
        db.collection(Constants.FirebaseCollections.USERS)
            .document(userId).set(user)
    }

    private fun updateUserDetailsInDatabase(
        userId: String,
        name: String,
        profilePictureUrl: String
    ) {
        db.collection(Constants.FirebaseCollections.USERS)
            .document(userId).update("name", name, "profilePictureUrl", profilePictureUrl)
    }

    fun addPost(postId: String, post: Post): Task<Void> {
        return db.collection(Constants.FirebaseCollections.POSTS)
            .document(postId)
            .set(Post.toMap(post))
    }

    fun updatePost (postId: String, post: Post) : Task<Void> {
        return db.collection(Constants.FirebaseCollections.POSTS)
            .document(postId).update(Post.toMap(post))
    }

    fun createNewUser(user: User):
            Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    if (user.name != null || user.profilePictureUrl != null) {
                        val detailsToUpdate = userProfileChangeRequest {
                            displayName = user.name
                            photoUri = user.profilePictureUrl
                        }
                        val currentUser = auth.currentUser
                        currentUser?.updateProfile(detailsToUpdate)

                        currentUser?.uid?.let { userId ->
                            createNewUserInDatabase(userId, user.firestoreUserToDbUser(userId))
                        }
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

    fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
            } else {
                Toast.makeText(
                    MyApplication.Globals.context,
                    "אחד או יותר מהפרטים אינם נכונים",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    fun changeUserDetails(user: User) {
        val currentUser = auth.currentUser

        if (currentUser != null && !user.name.isNullOrBlank()) {
            val detailsToUpdate = userProfileChangeRequest {
                displayName = user.name
                photoUri = user.profilePictureUrl
            }

            currentUser.updateProfile(detailsToUpdate)
            updateUserDetailsInDatabase(
                currentUser.uid, user.name,
                user.profilePictureUrl.toString()
            )
        }
    }

    fun getAllPosts(): LiveData<List<Post>> {
        val postsLiveData = MutableLiveData<List<Post>>()

        db.collection(Constants.FirebaseCollections.POSTS).get()
            .addOnSuccessListener { querySnapshot ->
                val fetchedPosts = querySnapshot.documents.mapNotNull {
                    val documentData = it.data
                    documentData?.put("postId", it.id)
                    Post.fromJSON(documentData!!)
                }

                postsLiveData.value = fetchedPosts
            }
            .addOnFailureListener {
                postsLiveData.value = listOf()
            }

        return postsLiveData
    }

    fun getCommentsByPost(postId: String, callback: CommentsCallback): Task<QuerySnapshot> {
        return db.collection(Constants.FirebaseCollections.COMMENTS).whereEqualTo("postId", postId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val fetchedComments =
                    querySnapshot.documents.mapNotNull { it.toObject(Comment::class.java) }
                callback(fetchedComments)
            }
            .addOnFailureListener {
                callback(listOf())
            }
    }

    fun addComment(commentId: String, comment: Comment): Task<Void> {
        return db.collection(Constants.FirebaseCollections.COMMENTS)
            .document(commentId)
            .set(comment)
    }

    fun deletePost(postId: String): Task<Void> {
        return db.collection(Constants.FirebaseCollections.POSTS)
            .document(postId).delete()
    }

    fun getUsersPosts(callback: PostsCallback): Task<QuerySnapshot> {
        return db.collection(Constants.FirebaseCollections.POSTS)
            .whereEqualTo("userId", auth.currentUser?.uid).get()
            .addOnSuccessListener { querySnapshot ->
                val fetchedPosts =
                    querySnapshot.documents.mapNotNull {
                        val documentData = it.data
                        documentData?.put("postId", it.id)
                        Post.fromJSON(documentData!!)
                    }
                callback(fetchedPosts)
            }
            .addOnFailureListener {
                callback(listOf())
            }
    }
}
