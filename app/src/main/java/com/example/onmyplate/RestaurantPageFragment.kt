package com.example.onmyplate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onmyplate.adapter.CommentsListAdapter
import com.example.onmyplate.databinding.FragmentRestaurantPageBinding
import com.example.onmyplate.model.Comment
import com.example.onmyplate.model.ServerRequestsModel
import com.example.onmyplate.model.ServerRequestsModel.addComment
import com.google.firebase.auth.FirebaseAuth

class RestaurantPageFragment : Fragment() {
    private lateinit var binding: FragmentRestaurantPageBinding
    private var comments: List<Comment>? = listOf()
    private var adapter: CommentsListAdapter? = null
    val args: RestaurantPageFragmentArgs by navArgs()
    fun onCreateViewinflater(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestaurantPageBinding.inflate(inflater, container, false)
        val recyclerView: RecyclerView = binding.commentsRecyclerView

        val intent = Intent(requireContext(), NavigationActivity::class.java)

        recyclerView.layoutManager = LinearLayoutManager(context)

        val restaurantName = args.restaurantName

        binding.restaurantName.text = restaurantName
        binding.restaurantDescription.text = args.restaurantDescription
        binding.starRatingBar.rating = args.rating

        adapter = CommentsListAdapter(comments) { comment ->
            intent.putExtra("description", comment?.description)
            intent.putExtra("ratingBar", comment?.rating)
        }

        recyclerView.adapter = adapter
        if (restaurantName != null) {
            getAllComments(restaurantName)
        }

        binding.submitCommentButton.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val commentText = binding.commentEditText.text.toString().trim()
            val commentRating = binding.starRatingBar.rating
            if (restaurantName != null) {

                val newComment = Comment(userId, restaurantName, commentText, commentRating)
                if (commentText.isNotEmpty()) {
                    addComment(newComment)
                    binding.commentEditText.text?.clear() // Clear input field after submission
                } else {
                    Toast.makeText(context, "Please enter a comment", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return binding.root
    }

    private fun getAllComments(restaurantName: String) {
        ServerRequestsModel.getCommentsByRestaurant(restaurantName) {
            adapter?.setComments(it)
            adapter?.notifyDataSetChanged()
        }
    }
}