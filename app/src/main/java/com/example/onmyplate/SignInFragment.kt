package com.example.onmyplate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.onmyplate.databinding.FragmentSignInBinding
import com.example.onmyplate.model.FirebaseModel

class SignInFragment : Fragment() {
    private var binding: FragmentSignInBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        binding?.signInButton?.setOnClickListener {
            val email = binding?.emailTextField?.text.toString()
            val password = binding?.passwordTextField?.text.toString()
            if (email.isEmpty() || password.isEmpty())
                Toast.makeText(activity, "יש למלא את כל הפרטים הנדרשים", Toast.LENGTH_SHORT).show()
            else
                FirebaseModel().signInUser(email, password)
        }

        return binding?.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}