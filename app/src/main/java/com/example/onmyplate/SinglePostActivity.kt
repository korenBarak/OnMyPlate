package com.example.onmyplate

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.onmyplate.databinding.ActivitySinglePostBinding
import com.example.onmyplate.model.FirebaseModel
import com.example.onmyplate.model.Post

class SinglePostActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySinglePostBinding
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var galleryLauncher: ActivityResultLauncher<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySinglePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // OPTION A: take a picture
//        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
//                bitmap -> binding.imageView.setImageBitmap(bitmap)
//        }
//
//        binding.addPhotoButton.setOnClickListener {
//            cameraLauncher?.launch(null)
//        }

        // OPTION B: open the gallery
        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            binding.imageView.setImageURI(uri)
        }

        binding.addPhotoButton.setOnClickListener {
            galleryLauncher?.launch("image/*")
        }

        binding.submitButton.setOnClickListener {
            // OPTION A: take a picture
            binding?.imageView?.isDrawingCacheEnabled = true
            binding?.imageView?.buildDrawingCache()

            val bitmap = (binding?.imageView?.drawable as BitmapDrawable).bitmap

            Log.d("TAG", "this is something... ${bitmap.toString()}")

            val post = Post(
                restaurantName =  binding.restaurantTextField.text.toString(),
                tags = binding.tagsTextField.text.toString(),
                description =  binding.descriptionTextField.text.toString(),
                rating =   5,
            )

            FirebaseModel().addPost(post, bitmap)
        }
    }
}