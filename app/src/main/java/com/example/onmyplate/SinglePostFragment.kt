package com.example.onmyplate

import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
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
import com.example.onmyplate.viewModel.PostListViewModel
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
    private var postListViewModel: PostListViewModel? = null
    private var galleryLauncher: ActivityResultLauncher<String>? = null

    private var adapter: ImageRecyclerAdapter? = null
    private var photosArr: MutableList<ImageData> = mutableListOf<ImageData>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var generatedId = UUID.randomUUID().toString()
    private val args: SinglePostFragmentArgs by navArgs()

    private var isNewPost: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isNewPost = args.postId.isNullOrBlank() != false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        // window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        binding = FragmentSinglePostBinding.inflate(inflater, container, false)
        postListViewModel = ViewModelProvider(requireActivity()).get(PostListViewModel::class.java)
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


        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.adapter = adapter

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                photosArr.add(ImageData.BitmapData(bitmap))
                adapter?.set(photosArr)

                if (photosArr.size >= MAX_PHOTOS)
                    binding.addPhotoButton.isEnabled = false
            }
        }

        binding.addPhotoButton.setOnClickListener {
            galleryLauncher?.launch("image/*")
        }

        binding.submitButton.setOnClickListener {
            val restaurantName = binding.restaurantTextField.text.toString()
            val tags = binding.tagsTextField.text.toString()
            val description = binding.descriptionTextField.text.toString()

            if (restaurantName.isBlank() || tags.isBlank() || description.isBlank() || photosArr.size == 0) {
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
                    requireContext(),
                    R.style.CustomMaterialAlertDialogStyle
                )
                    .setTitle("נמצאו כמה מסעדות התואמות לשם: $restaurantName")
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
        binding.circularProgressBar.visibility = View.VISIBLE
        val user = FirebaseModel.shared.getUser()

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

            val bitmapPhotos: List<Bitmap> =
                photosArr.filterIsInstance<ImageData.BitmapData>().map { photo -> photo.bitmap }

            if (isNewPost) {
                deletePostFromRoom()
                ServerRequestsModel.addPost(post, bitmapPhotos.toMutableList()) {
                    if (it != null)
                        postListViewModel?.addPost(it)
                    else
                        Toast.makeText(
                            MyApplication.Globals.context,
                            "הוספת הפוסט נכשלה",
                            Toast.LENGTH_SHORT
                        ).show()

                    binding.circularProgressBar.visibility = View.GONE
                    requireActivity().supportFragmentManager.popBackStack()
                }
            } else {
                ServerRequestsModel.updatePost(
                    arguments?.getString("postId").toString(),
                    post,
                    bitmapPhotos.toMutableList(),
                    photosArr
                ) {
                    if (it != null)
                        postListViewModel?.updatePost(it)
                    else
                        Toast.makeText(
                            MyApplication.Globals.context,
                            "עדכון הפוסט נכשל",
                            Toast.LENGTH_SHORT
                        ).show()

                    binding.circularProgressBar.visibility = View.GONE
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }

        binding.circularProgressBar.visibility = View.GONE
    }

    private fun deletePostFromRoom() {
        CoroutineScope(Dispatchers.IO).launch {
            AppLocalDb.database.partialPostData().deleteById(generatedId)
        }
        generatedId = ""
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

                    generatedId = partialPost.id
                    AppLocalDb.database.partialPostData().delete(partialPost)
                }
            }

            binding.circularProgressBar.visibility = View.GONE
        } else {
            binding.ratingBar.rating = args.rating
            binding.restaurantTextField.setText(args.restaurantName)
            binding.descriptionTextField.setText(args.description)
            binding.tagsTextField.setText(args.tags)

            photosArr =
                args.photos?.map { photo -> ImageData.StringData(photo) }
                    ?.toMutableList() ?: mutableListOf()

        }
    }

    private fun handleInputChange() {
        if (isNewPost && generatedId.isNotEmpty()) {
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

