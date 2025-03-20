package com.example.onmyplate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.onmyplate.R
import com.example.onmyplate.model.Post
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso

class RestaurantListAdapter(
    private var restaurants: List<Post>?,
    private val isAbleToModify: Boolean,
    private val onEdit: (Post?) -> Unit,
    private val onDelete: (String?) -> Unit,
    private val onRowClicked: (Post?) -> Unit
) : RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder>() {

    inner class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.restaurantPicture)
        val name: TextView = view.findViewById(R.id.restaurantName)
        val ratingBar: RatingBar = view.findViewById(R.id.starRatingBar)
        val deleteButton: MaterialButton = view.findViewById(R.id.deleteButton)
        val editButton: MaterialButton = view.findViewById(R.id.editButton)
    }

    fun setRestaurants(restaurants: List<Post>?) {
        this.restaurants = restaurants
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_restaurant_list_item, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        if (restaurants?.get(position)?.photoUrls?.size != 0)
            Picasso.get()
                .load(restaurants?.get(position)?.photoUrls?.get(0))
                .into(holder?.imageView)

        holder.name.text = restaurants?.get(position)?.restaurantName
        holder.ratingBar.rating = restaurants?.get(position)?.rating ?: 0.0f
        holder.itemView.setOnClickListener {
            onRowClicked(restaurants?.get(position))
        }

        if (!isAbleToModify) {
            holder.deleteButton.visibility = View.GONE
            holder.editButton.visibility = View.GONE
        } else {
            holder.editButton.setOnClickListener { onEdit(restaurants?.get(position)) }
            holder.deleteButton.setOnClickListener { onDelete(restaurants?.get(position)?.postId) }
        }
    }

    override fun getItemCount(): Int = restaurants?.size ?: 0

    fun setFilteredList(restaurants: List<Post>) {
        this.restaurants = restaurants
        notifyDataSetChanged()
    }
}