package com.example.onmyplate

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onmyplate.adapter.RestaurantListAdapter
import com.example.onmyplate.model.Post

class MainActivity : AppCompatActivity() {
    private var restaurants: List<Post>? = null
    private var adapter: RestaurantListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RestaurantListAdapter(restaurants) { restaurant ->
            // Handle row click, navigate to the details screen
            val intent = Intent(this, SinglePostActivity::class.java)
            intent.putExtra("restaurant_name", restaurant?.restaurantName)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
    }
}