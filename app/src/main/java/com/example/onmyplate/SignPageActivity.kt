package com.example.onmyplate

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.onmyplate.databinding.ActivitySignPageBinding
import com.example.onmyplate.viewModel.UserViewModel
import androidx.lifecycle.Observer

class SignPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignPageBinding
    private var userViewModel: UserViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignPageBinding.inflate(layoutInflater)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        userViewModel?.signedUser?.observe(this, Observer { user ->
            if (user != null) {
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra("DATA_TYPE", "all")
                startActivity(intent)
            } else {
                setContentView(binding.root)
                val toggleGroup = binding.signStatusToggle
                toggleGroup.check(binding.signInButton.id)

                binding.signUpButton.setOnClickListener {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_signInFragment_to_signUpFragment)
                }

                binding.signInButton.setOnClickListener {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_signUpFragment_to_signInFragment)
                }
            }
        })
    }
}