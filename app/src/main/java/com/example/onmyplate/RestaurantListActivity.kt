package com.example.onmyplate

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onmyplate.adapter.RestaurantListAdapter
import com.example.onmyplate.model.Post
import com.example.onmyplate.model.ServerRequestsModel
import java.util.Locale

class RestaurantListActivity : AppCompatActivity(){
    private var restaurants: List<Post>? = listOf()
    private var adapter: RestaurantListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_restaurant_list)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val searchView: SearchView = findViewById(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RestaurantListAdapter(restaurants) { restaurant ->
            // Handle row click, navigate to the details screen
            val intent = Intent(this, SinglePostActivity::class.java)
            intent.putExtra("restaurant_name", restaurant?.restaurantName)
            intent.putExtra("restaurantTextField", restaurant?.tags)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        getAllPosts()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })
    }

    private fun getAllPosts() {
        ServerRequestsModel().getAllPosts {
            adapter?.setFilteredList(it)
        }
    }

    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<Post>()
            for (i in restaurants!!) {
                if (i.restaurantName.lowercase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                adapter?.setFilteredList(filteredList)
            }
        }
    }
}