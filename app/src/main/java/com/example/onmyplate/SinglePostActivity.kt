package com.example.onmyplate

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.onmyplate.databinding.ActivitySinglePostBinding
import com.example.onmyplate.model.FirebaseModel
import com.example.onmyplate.model.Post

class SinglePostActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySinglePostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySinglePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submitButton.setOnClickListener {
            val post = Post(
                restaurantName =  binding.restaurantTextField.text.toString(),
                tags = binding.tagsTextField.text.toString(),
                description =  binding.descriptionTextField.text.toString(),
                rating =   5,

            )

            FirebaseModel().addPost(post)
        }
    }
}