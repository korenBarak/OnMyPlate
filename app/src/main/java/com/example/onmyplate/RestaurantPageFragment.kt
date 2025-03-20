package com.example.onmyplate

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.onmyplate.adapter.CommentsListAdapter
import com.example.onmyplate.adapter.ImageData
import com.example.onmyplate.adapter.ImageRecyclerAdapter
import com.example.onmyplate.adapter.onDeleteButtonClickListener
import com.example.onmyplate.base.MyApplication
import com.example.onmyplate.databinding.FragmentRestaurantPageBinding
import com.example.onmyplate.model.Comment
import com.example.onmyplate.model.ServerRequestsModel
import com.example.onmyplate.model.ServerRequestsModel.addComment
import com.google.firebase.auth.FirebaseAuth

class RestaurantPageFragment : Fragment() {
    private lateinit var binding: FragmentRestaurantPageBinding
    private var comments: MutableList<Comment> = mutableListOf()
    private var commentAdapter: CommentsListAdapter? = null
    private var imageAdapter: ImageRecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestaurantPageBinding.inflate(inflater, container, false)
        val recyclerView: RecyclerView = binding.commentsRecyclerView

        recyclerView.layoutManager = LinearLayoutManager(context)

        val postId = arguments?.getString("postId")
        val restaurantName = arguments?.getString("restaurantName")
        val rating = arguments?.getFloat("rating")!!
        val googleRating = arguments?.getDouble("googleRating")?.toFloat() ?: 0.0F

        binding.restaurantName.text = restaurantName
        binding.internalRatingBar.rating = rating
        binding.internalRatingTitle.text = "דירוג  - ${rating}"
        binding.googleRatingBar.rating = googleRating
        binding.googleRatingTitle.text = "דירוג גוגל  - ${googleRating}"

        binding.restaurantDescription.text = arguments?.getString("description")
        binding.restaurantTags.text = arguments?.getString("tags")
        commentAdapter = CommentsListAdapter(comments)

        recyclerView.adapter = commentAdapter

        if (postId != null) {
            getAllComments(postId)
        }

        binding.submitCommentButton.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val commentText = binding.commentEditText.text.toString().trim()

            if (postId != null) {
                val newComment = Comment(userId, postId, commentText)
                if (commentText.isNotEmpty()) {
                    binding.circularProgressBar.visibility = View.VISIBLE

                    addComment(newComment).addOnCompleteListener {
                        binding.circularProgressBar.visibility = View.GONE
                        comments.add(newComment)

                        commentAdapter?.setComments(comments)
                        commentAdapter?.notifyDataSetChanged()
                    }
                    binding.commentEditText.text?.clear()
                } else {
                    Toast.makeText(context, "יש למלא תגובה", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val layoutManager = LinearLayoutManager(
            MyApplication.Globals.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.recyclerView.layoutManager = layoutManager

        var photosUrl = arguments?.getStringArray("photos")

        imageAdapter = ImageRecyclerAdapter(
            photosUrl?.filterNotNull()?.map { url ->
                ImageData.StringData(url)
            }?.toMutableList() ?: mutableListOf(),
            requireActivity().findViewById(R.id.photoIndicatorTextView)
        ).apply {
            scrollListener(binding.recyclerView)

            onDeleteListener = object : onDeleteButtonClickListener {
                override fun onItemClick(photo: ImageData?) {}
            }
        }

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.adapter = imageAdapter

        return binding.root
    }

    private fun getAllComments(postId: String) {
        ServerRequestsModel.getCommentsByPost(postId) {
            commentAdapter?.setComments(it.toMutableList())
            commentAdapter?.notifyDataSetChanged()
        }
    }
}