package com.example.onmyplate.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.onmyplate.model.FirebaseModel
import com.example.onmyplate.model.Post


class PostListViewModel : ViewModel() {
    enum class LoadingState {
        LOADING,
        LOADED
    }

    private val firebaseModel = FirebaseModel.shared

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> get() = _loadingState

    private val _postList = MutableLiveData<List<Post>>()
    val postList: LiveData<List<Post>> get() = _postList

    init {
        getAllPosts()
    }

    fun getAllPosts() {
        _loadingState.value = LoadingState.LOADING

        firebaseModel.getAllPosts().observeForever { posts ->
            _postList.value = posts
            _loadingState.value = LoadingState.LOADED
        }
    }

    fun deletePost(postId: String) {
        firebaseModel.deletePost(postId)

        val updatedList = _postList.value?.filterNot { it.postId == postId } ?: listOf()
        _postList.value = updatedList
    }

    fun addPost(post: Post) {
        val mutableList = _postList.value?.toMutableList() ?: mutableListOf()
        mutableList.add(post)
        _postList.value = mutableList
    }

    fun updatePost(post: Post) {
        val mutableList = _postList.value?.toMutableList() ?: mutableListOf()
        val postIndex = mutableList.indexOfFirst { it.postId == post.postId }

        if (postIndex != -1) {
            mutableList[postIndex] = post
        }

        _postList.value = mutableList
    }
}