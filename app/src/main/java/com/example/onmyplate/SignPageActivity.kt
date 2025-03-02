package com.example.onmyplate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.onmyplate.databinding.ActivitySignPageBinding

class SignPageActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toggleGroup = binding.signStatusToggle
        toggleGroup.check(binding.signUpButton.id)

        binding.signUpButton.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.signInButton.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_signUpFragment_to_signInFragment)
        }
    }
}