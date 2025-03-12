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
import com.google.rpc.context.AttributeContext.Auth
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
            val intent = Intent(this, RestaurantPageActivity::class.java)
            intent.putExtra("restaurantName", restaurant?.restaurantName)
            intent.putExtra("restaurantDescription", restaurant?.description)
            intent.putExtra("rating", restaurant?.rating)
//            intent.putExtra("image", restaurant?.photoUrls?.get(0))

            startActivity(intent)
        }

        recyclerView.adapter = adapter
        if(intent.getStringExtra("DATA_TYPE")!! == "all") {
            getAllPosts()
        }else {
            getUsersPosts()
        }

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
        ServerRequestsModel.getAllPosts {
            adapter?.setFilteredList(it)
        }
    }

    private fun getUsersPosts() {
        ServerRequestsModel.getUsersPosts {
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