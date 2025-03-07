package com.example.onmyplate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onmyplate.adapter.CommentsListAdapter
import com.example.onmyplate.adapter.RestaurantListAdapter
import com.example.onmyplate.databinding.ActivityRestaurantPageBinding
import com.example.onmyplate.model.Comment
import com.example.onmyplate.model.Post
import com.example.onmyplate.model.ServerRequestsModel
import java.util.Locale

class RestaurantPageActivity : AppCompatActivity(){
    private var comments: List<Comment>? = listOf()
    private var adapter: CommentsListAdapter? = null
    private lateinit var binding: ActivityRestaurantPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRestaurantPageBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_restaurant_page)
        val recyclerView: RecyclerView = findViewById(R.id.commentsRecyclerView)


        recyclerView.layoutManager = LinearLayoutManager(this)

        val restaurantName = intent.getStringExtra("restaurantName")
        val restaurantDescription = intent.getStringExtra("restaurantDescription")
        val rating = intent.getFloatExtra("rating", 0F)
//        TODO replace with picture scroller


        val restaurantNameView: TextView = this.findViewById(R.id.restaurantName)
        val restaurantDescriptionView: TextView = this.findViewById(R.id.restaurantDescription)
        val ratingBar: RatingBar = this.findViewById(R.id.starRatingBar)

        restaurantNameView.text = restaurantName
        restaurantDescriptionView.text = restaurantDescription
        ratingBar.rating = rating

        adapter = CommentsListAdapter(comments) { comment ->
            intent.putExtra("description", comment?.description)
            intent.putExtra("ratingBar", comment?.rating)
        }
        Log.d("RecyclerView", "List size: ${comments?.size}")

        recyclerView.adapter = adapter
        if (restaurantName != null) {
            getAllComments(restaurantName)
        }
    }

    private fun getAllComments(restaurantName: String) {
        ServerRequestsModel.getCommentsByRestaurant(restaurantName) {
            adapter?.setComments(it)
            adapter?.notifyDataSetChanged()
        }
    }
}