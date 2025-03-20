package com.example.onmyplate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.onmyplate.adapter.ImageData
import com.example.onmyplate.adapter.ImageRecyclerAdapter
import com.example.onmyplate.adapter.onDeleteButtonClickListener
import com.example.onmyplate.base.MyApplication
import com.example.onmyplate.databinding.FragmentSinglePostBinding
import com.example.onmyplate.model.FirebaseModel
import com.example.onmyplate.model.GoogleApiPlace
import com.example.onmyplate.model.Post
import com.example.onmyplate.model.ServerRequestsModel
import com.example.onmyplate.model.room.AppLocalDb
import com.example.onmyplate.model.room.PartialPost
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID

var MAX_PHOTOS = 5

class SinglePostFragment : Fragment() {
    private lateinit var binding: FragmentSinglePostBinding
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var adapter: ImageRecyclerAdapter? = null
    private var photosArr: MutableList<ImageData> = mutableListOf<ImageData>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var generatedId = UUID.randomUUID().toString()
    private var isNewPost: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isNewPost = arguments?.getString("postId").isNullOrBlank() != false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        // window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        binding = FragmentSinglePostBinding.inflate(inflater, container, false)
        initFields()


        val layoutManager = LinearLayoutManager(
            MyApplication.Globals.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.recyclerView.layoutManager = layoutManager

        adapter = ImageRecyclerAdapter(
            photosArr,
            requireActivity().findViewById(R.id.photoIndicatorTextView)
        ).apply {
            scrollListener(binding.recyclerView)

            onDeleteListener = object : onDeleteButtonClickListener {
                override fun onItemClick(photo: ImageData?) {
                    photo.let {
                        photosArr.remove(photo)
                        adapter?.set(photosArr)

                        if (photosArr.size < MAX_PHOTOS)
                            binding.addPhotoButton.isEnabled = true

                    }
                }
            }
        }

        binding.restaurantTextField.setOnFocusChangeListener { view, b ->
            handleInputChange()
        }

        binding.descriptionTextField.setOnFocusChangeListener { view, b ->
            handleInputChange()
        }

        binding.tagsTextField.setOnFocusChangeListener { view, b ->
            handleInputChange()
        }


        binding.ratingBar.setOnRatingBarChangeListener { ratingBar, fl, b ->
            handleInputChange()
        }

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.adapter = adapter

        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
                if (bitmap != null) {
                    photosArr.add(ImageData.BitmapData(bitmap))
                    adapter?.set(photosArr)

                    if (photosArr.size >= MAX_PHOTOS)
                        binding.addPhotoButton.isEnabled = false
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

        return binding.root
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
                MaterialAlertDialogBuilder(
                    MyApplication.Globals.context!!,
                    R.style.CustomMaterialAlertDialogStyle
                )
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
        Toast.makeText(MyApplication.Globals.context, message, Toast.LENGTH_SHORT).show()
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
                rating = binding.ratingBar.rating,
                googleRating = roundedRating
            )


            // TODO: handle photosArr to bitmap or something , photosArr
            ServerRequestsModel.addPost(post, mutableListOf()) {
                binding.circularProgressBar.visibility = View.GONE
                // back one level
//                val intent = Intent(this, NavigationActivity::class.java)
//                intent.putExtra("DATA_TYPE", "all")
//                startActivity(intent)
            }
        }
    }

    private fun initFields() {
        if (isNewPost) {
            binding.circularProgressBar.visibility = View.VISIBLE

            CoroutineScope(Dispatchers.IO).launch {
                val partialPost = AppLocalDb.database.partialPostData().getLastPartialPost()

                if (partialPost != null) {
                    withContext(Dispatchers.Main) {
                        binding.ratingBar.rating = partialPost.rating ?: 0.0F
                        binding.restaurantTextField.setText(partialPost.restaurantName ?: "")
                        binding.descriptionTextField.setText(partialPost.description ?: "")
                        binding.tagsTextField.setText(partialPost.tags ?: "")

                    }

                    AppLocalDb.database.partialPostData().delete(partialPost)
                }
            }

            binding.circularProgressBar.visibility = View.GONE
        } else {
            binding.ratingBar.rating = arguments?.getFloat("rating") ?: 0.0F
            binding.restaurantTextField.setText(arguments?.getString("restaurantName") ?: "")
            binding.descriptionTextField.setText(arguments?.getString("description") ?: "")
            binding.tagsTextField.setText(arguments?.getString("tags") ?: "")

            photosArr =
                arguments?.getStringArray("photos")?.map { photo -> ImageData.StringData(photo) }
                    ?.toMutableList() ?: mutableListOf()

        }
    }

    private fun handleInputChange() {
        if (isNewPost) {
            CoroutineScope(Dispatchers.IO).launch {
                AppLocalDb.database.partialPostData().insertData(
                    PartialPost(
                        id = generatedId,
                        restaurantName = (binding.restaurantTextField.text ?: "").toString(),
                        description = (binding.descriptionTextField.text.toString()),
                        tags = binding.tagsTextField.text.toString(),
                        rating = binding.ratingBar.rating
                    )
                )
            }
        }
    }

}

