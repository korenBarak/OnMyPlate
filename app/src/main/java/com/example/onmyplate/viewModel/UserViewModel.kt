package com.example.onmyplate.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.onmyplate.model.FirebaseModel
import com.google.firebase.auth.FirebaseUser

class UserViewModel: ViewModel() {
    private val firebaseModel = FirebaseModel.shared

    private val _signedUser = MutableLiveData<FirebaseUser?>()
    val signedUser: LiveData<FirebaseUser?> get() = _signedUser

    init {
        _signedUser.value = firebaseModel.getUser()

        firebaseModel.getAuthListener { auth ->
            _signedUser.value = auth.currentUser
        }
    }
}