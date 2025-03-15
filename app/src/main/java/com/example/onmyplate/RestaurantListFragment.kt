package com.example.onmyplate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onmyplate.adapter.RestaurantListAdapter
import com.example.onmyplate.databinding.FragmentRestaurantListBinding
import com.example.onmyplate.databinding.FragmentRestaurantListItemBinding
import com.example.onmyplate.databinding.FragmentSignInBinding
import com.example.onmyplate.model.Post
import com.example.onmyplate.model.ServerRequestsModel
import java.util.Locale

class RestaurantListFragment : Fragment() {
    private var restaurants: List<Post>? = listOf()
    private var adapter: RestaurantListAdapter? = null
    private lateinit var binding: FragmentRestaurantListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentRestaurantListBinding.inflate(inflater, container, false)

        val recyclerView: RecyclerView = binding.recyclerView
        val searchView: SearchView = binding.searchView

        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = RestaurantListAdapter(restaurants) { restaurant ->
            // Handle row click, navigate to the details screen
//            //ToDo make nav with nav graph

//            intent.putExtra("restaurantName", restaurant?.restaurantName)
//            intent.putExtra("restaurantDescription", restaurant?.description)
//            intent.putExtra("rating", restaurant?.rating)
////            intent.putExtra("image", restaurant?.photoUrls?.get(0))
//
//            startActivity(intent)
            var firstPhoto: String? = ""
            if(restaurant?.photoUrls?.size != 0) {
                firstPhoto = restaurant?.photoUrls?.get(0)
            }
//            val action = SignInFragmentDirections.actionSignInFragmentToRestaurantListFragment(restaurant!!.restaurantName, restaurant.description, restaurant.rating, firstPhoto ?: "")
            val action = RestaurantListFragmentDirections.actionRestaurantListFragmentToRestaurantPageFragment( restaurant!!.restaurantName, restaurant.description, restaurant.rating, firstPhoto ?: "" )
            binding.materialCardView.findNavController().navigate(action)
        }
        val intent = Intent(context, NavigationActivity::class.java)
        recyclerView.adapter = adapter

        if(arguments?.getString("DATA_TYPE") == "all") {
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

        return binding.root
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
                Toast.makeText(context, "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                adapter?.setFilteredList(filteredList)
            }
        }
    }
}