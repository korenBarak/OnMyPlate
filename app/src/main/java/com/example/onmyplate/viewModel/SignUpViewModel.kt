package com.example.onmyplate.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.onmyplate.model.ServerRequestsModel
import com.example.onmyplate.model.User

class SignUpViewModel: ViewModel(), ProfileViewModel {
    override fun handleButtonClick(user: User, bitmap: Bitmap) {
        ServerRequestsModel.addNewUser(user, bitmap)
    }
}