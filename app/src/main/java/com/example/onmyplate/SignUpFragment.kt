package com.example.onmyplate

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.onmyplate.base.MyApplication
import com.example.onmyplate.databinding.FragmentSignUpBinding
import com.example.onmyplate.model.FirebaseModel
import com.example.onmyplate.model.User
import com.example.onmyplate.viewModel.EditProfileViewModel
import com.example.onmyplate.viewModel.ProfileViewModel
import com.example.onmyplate.viewModel.SignUpViewModel

class SignUpFragment : Fragment() {
    private var binding: FragmentSignUpBinding? = null
    private var galleryLauncher: ActivityResultLauncher<String>? = null
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        val signedUser = FirebaseModel().getUser()

        viewModel = if (signedUser == null) {
            ViewModelProvider(this)[SignUpViewModel::class.java]
        } else {
            ViewModelProvider(this)[EditProfileViewModel::class.java]
        }

        viewModel.initPage(signedUser, binding)

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            binding?.profileImage?.setImageURI(uri)
        }

        binding?.changePictureButton?.setOnClickListener {
            galleryLauncher?.launch("image/*")
        }

        binding?.logOutButton?.setOnClickListener {
            FirebaseModel().signOutUser()
            val intent = Intent(MyApplication.Globals.context, SignPageActivity::class.java)
            startActivity(intent)
        }

        binding?.signUpButton?.setOnClickListener {
            val nameText = binding?.nameTextField?.text.toString()
            val emailText = binding?.emailTextField?.text.toString()
            val passwordText = binding?.passwordTextField?.text.toString()
            val intent = Intent(requireContext(), NavigationActivity::class.java)
            intent.putExtra("DATA_TYPE", "all")

            if (nameText.isBlank() || emailText.isBlank()) {
                Toast.makeText(activity, "יש למלא את כל הפרטים", Toast.LENGTH_SHORT).show()
            } else {
                if (signedUser == null && passwordText.length < 6) {
                    Toast.makeText(
                        activity,
                        "על הסיסמה להיות בעלת 6 תווים לפחות",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    binding?.profileImage?.isDrawingCacheEnabled = true
                    binding?.profileImage?.buildDrawingCache()

                    val bitmap = (binding?.profileImage?.drawable as BitmapDrawable).bitmap

                    val user = User(
                        name = nameText,
                        email = emailText,
                        password = passwordText,
                        profilePictureUrl = signedUser?.photoUrl
                    )

                    viewModel.handleButtonClick(user, bitmap)
                    startActivity(intent)
                }
            }
        }

        return binding?.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}