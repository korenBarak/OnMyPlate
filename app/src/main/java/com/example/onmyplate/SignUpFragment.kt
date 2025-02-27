package com.example.onmyplate

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.onmyplate.databinding.FragmentSignUpBinding
import com.example.onmyplate.model.FirebaseModel
import com.example.onmyplate.model.ServerRequestsModel
import com.example.onmyplate.model.User


class SignUpFragment : Fragment() {
    private var binding: FragmentSignUpBinding? = null
    private var galleryLauncher: ActivityResultLauncher<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            binding?.profileImage?.setImageURI(uri)
        }

        binding?.changePictureButton?.setOnClickListener {
            galleryLauncher?.launch("image/*")
        }

        binding?.signUpButton?.setOnClickListener {
           binding?.profileImage?.isDrawingCacheEnabled = true
           binding?.profileImage?.buildDrawingCache()

            val bitmap = (binding?.profileImage?.drawable as BitmapDrawable).bitmap

            val user = User(
                name = binding?.nameTextField?.text.toString() ?: "",
                email = binding?.emailTextField?.text.toString(),
                password = binding?.passwordTextField?.text.toString(),
            )

            ServerRequestsModel().addNewUser(user, bitmap)
        }

        return binding?.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}