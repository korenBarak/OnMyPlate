package com.example.onmyplate

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.onmyplate.databinding.ActivitySignPageBinding
import com.example.onmyplate.model.FirebaseModel

class SignPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignPageBinding.inflate(layoutInflater)

        if (FirebaseModel().getUser() != null) {
            val intent = Intent(this, NavigationActivity::class.java)
            intent.putExtra("DATA_TYPE", "all")
            startActivity(intent)
            finish()
        } else {
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
}