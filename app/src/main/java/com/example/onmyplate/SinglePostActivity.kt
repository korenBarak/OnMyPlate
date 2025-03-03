package com.example.onmyplate

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onmyplate.adapter.ImageRecyclerAdapter
import com.example.onmyplate.databinding.ActivitySinglePostBinding
import com.example.onmyplate.adapter.onDeleteButtonClickListener
import com.example.onmyplate.model.FirebaseModel
import com.example.onmyplate.model.GoogleApiPlace
import com.example.onmyplate.model.Post
import com.example.onmyplate.model.ServerRequestsModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode

class SinglePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySinglePostBinding
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var adapter: ImageRecyclerAdapter? = null
    private var photosArr: MutableList<Bitmap> = mutableListOf<Bitmap>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)


    override fun onCreate(savedInstanceState: Bundle?) {
//        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
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

        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
                if (bitmap != null) {
                    photosArr.add(bitmap)
                    adapter?.set(photosArr)
                }

            }

        binding.addPhotoButton.setOnClickListener {
            cameraLauncher?.launch(null)
        }

        binding.submitButton.setOnClickListener {
            val restaurantName = binding.restaurantTextField.text.toString()
            val tags = binding.tagsTextField.text.toString()
            val description = binding.descriptionTextField.text.toString()

            if (restaurantName.isBlank() || tags.isBlank() || description.isBlank()) {
                toastWrapper("יש למלא את כל הפרטים")
            } else {
                binding.circularProgressBar.visibility = View.VISIBLE

                coroutineScope.launch {
                    val googlePlace = handleSearchPlaceName(restaurantName)

                    if (googlePlace != null) {
                        handleSaveRestaurant(googlePlace)
                    } else {
                        binding.circularProgressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private suspend fun handleSearchPlaceName(restaurantName: String): GoogleApiPlace? {
        val places = withContext(Dispatchers.IO) {
            ServerRequestsModel.getGoogleMapDetailsByPlaceName(restaurantName)
        }

        return when {
            places.isEmpty() -> {
                toastWrapper("לא נמצאה אף מסעדה, נסה שנית")
                null
            }

            places.size == 1 -> places[0]
            places.size > 5 -> {
                toastWrapper("נמצאו כמה מסעדות הקרובות לשם זה, נסה לדייק את השם")
                null
            }

            else -> {
                var selectedPlace: GoogleApiPlace? = null

                MaterialAlertDialogBuilder(this, R.style.CustomMaterialAlertDialogStyle)
                    .setTitle("נמצאו כמה מסעדות התואמות לשם: $restaurantName, בחר מה המסעדה המתאימה")
                    .setNeutralButton("בטל") { dialog, _ -> dialog.dismiss() }
                    .setPositiveButton("בחר") { _, _ ->
                        if (selectedPlace != null) {
                            handleSaveRestaurant(selectedPlace as GoogleApiPlace)
                        }
                    }
                    .setSingleChoiceItems(
                        places.map { it -> it.name }.toTypedArray(),
                        -1
                    ) { _, which ->
                        selectedPlace = places[which]
                    }
                    .show()

                return null
            }
        }
    }

    private fun toastWrapper(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleSaveRestaurant(googlePlace: GoogleApiPlace) {
        val user = FirebaseModel().getUser()

        if (user == null) {
            toastWrapper("חלה שגיאה, המשתמש לא נמצא")
        } else {
            val roundedRating = BigDecimal(googlePlace.rating.toString()).setScale(
                1,
                RoundingMode.HALF_UP
            ).toDouble()

            val post = Post(
                userId = user.uid,
                restaurantName = binding.restaurantTextField.text.toString(),
                tags = binding.tagsTextField.text.toString(),
                description = binding.descriptionTextField.text.toString(),
                rating = 5F,
                googleRating = roundedRating
            )

            ServerRequestsModel.addPost(post, photosArr) {
                binding.circularProgressBar.visibility = View.GONE
            }
        }
    }
}