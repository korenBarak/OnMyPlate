package com.example.onmyplate.viewModel

import android.graphics.Bitmap
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import com.example.onmyplate.R
import com.example.onmyplate.databinding.FragmentSignUpBinding
import com.example.onmyplate.model.ServerRequestsModel
import com.example.onmyplate.model.User
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class EditProfileViewModel: ViewModel(), ProfileViewModel {
    override fun initPage(user: FirebaseUser?, binding: FragmentSignUpBinding?) {
        user?.let {
            binding?.nameTextField?.setText(it.displayName)
            binding?.emailTextField?.setText(it.email)
            binding?.emailTextField?.isEnabled = false
            binding?.passwordTextField?.isVisible = false
            binding?.signUpButton?.setText("עדכן פרופיל")
            binding?.logOutButton?.visibility = View.VISIBLE

            if(it.photoUrl != null) {
                Picasso.get()
                    .load(it.photoUrl)
                    .placeholder(R.drawable.plate)
                    .into(binding?.profileImage)

            }
        }
    }

    override fun handleButtonClick(user: User, bitmap: Bitmap) {
        ServerRequestsModel.updateUserDetails(user, bitmap)
    }
}