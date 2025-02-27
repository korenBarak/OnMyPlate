package com.example.onmyplate
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onmyplate.adapter.ImageRecyclerAdapter
import com.example.onmyplate.databinding.ActivitySinglePostBinding
import com.example.onmyplate.adapter.onDeleteButtonClickListener
import com.example.onmyplate.model.Post
import com.example.onmyplate.model.ServerRequestsModel

class SinglePostActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySinglePostBinding
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var adapter: ImageRecyclerAdapter? = null

    private var photosArr: MutableList<Bitmap> = mutableListOf<Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySinglePostBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.layoutManager = layoutManager

        adapter = ImageRecyclerAdapter(photosArr)

        adapter?.onDeleteListener = object : onDeleteButtonClickListener {
            override fun onItemClick(photo: Bitmap?) {
                photo.let {
                    photosArr.remove(photo)
                    adapter?.set(photosArr)
                }
            }


        }

        binding.recyclerView.adapter = adapter

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                bitmap ->
                if (bitmap != null) {
                    photosArr.add(bitmap)
                    adapter?.set(photosArr)
                }

        }

        binding.addPhotoButton.setOnClickListener {
            cameraLauncher?.launch(null)
        }

        binding.submitButton.setOnClickListener {

           val post = Post(
               restaurantName =  binding.restaurantTextField.text.toString(),
               tags = binding.tagsTextField.text.toString(),
               description =  binding.descriptionTextField.text.toString(),
               rating = 5F,
           )

           ServerRequestsModel().addPost(post, photosArr)
        }
    }
}