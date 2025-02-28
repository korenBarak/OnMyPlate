package com.example.onmyplate
import android.os.Bundle
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RestaurantListItemActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_list_item)
        val ratingBar = findViewById<RatingBar>(R.id.starRatingBar)

        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            Toast.makeText(this, "Selected Rating: $rating", Toast.LENGTH_SHORT).show()
            // Perform additional actions based on the rating
        }
    }

}