package com.example.onmyplate.viewModel

import android.graphics.Bitmap
import com.example.onmyplate.databinding.FragmentSignUpBinding
import com.example.onmyplate.model.User
import com.google.firebase.auth.FirebaseUser

interface ProfileViewModel {
    fun handleButtonClick(user: User, bitmap: Bitmap) {}
    fun initPage(user: FirebaseUser?, binding: FragmentSignUpBinding?) {}
}
