package com.example.onmyplate.viewModel

import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.onmyplate.databinding.FragmentSignUpBinding
import com.example.onmyplate.model.ServerRequestsModel
import com.example.onmyplate.model.User
import com.google.firebase.auth.FirebaseUser

class SignUpViewModel: ViewModel(), ProfileViewModel {
    override fun handleButtonClick(user: User, bitmap: Bitmap) {
        ServerRequestsModel.addNewUser(user, bitmap)
    }


    override fun initPage(user: FirebaseUser?, binding: FragmentSignUpBinding?) {
        user?.let {
            binding?.logOutButton?.visibility = View.GONE
        }
    }
}