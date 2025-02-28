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
import com.squareup.picasso.Picasso

class RestaurantListAdapter(private var restaurants: List<Post>?,
                            private val onRowClicked: (Post?) -> Unit
) : RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder>() {

    inner class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.restaurantPicture)
        val name: TextView = view.findViewById(R.id.restaurantName)
        val id: TextView = view.findViewById(R.id.restaurantTextField)
        val ratingBar: RatingBar = view.findViewById(R.id.starRatingBar)

        fun bind(restaurant: Post?) {
            Picasso.get()
                .load(restaurant?.photoUrls?.get(0))
                .into(imageView)

            name.text = restaurant?.restaurantName
            id.text = restaurant?.tags
            ratingBar.rating = (restaurant?.rating ?: 1) as Float

            ratingBar.setOnRatingBarChangeListener {
                    ratingBar, _, _ ->  restaurant?.rating = ratingBar.rating
            }

            itemView.setOnClickListener {
                onRowClicked(restaurant)
            }
        }
    }

    fun setRestaurants(restaurants:List<Post>?) {
        this.restaurants = restaurants
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_restaurant_list_item, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        Picasso.get()
            .load(restaurants?.get(position)?.photoUrls?.get(0))
            .into(holder?.imageView)
        holder.name.text = restaurants?.get(position)?.restaurantName
    }

    override fun getItemCount(): Int = restaurants?.size ?: 0

    fun setFilteredList(restaurants: List<Post>){
        this.restaurants = restaurants
        notifyDataSetChanged()
    }
}