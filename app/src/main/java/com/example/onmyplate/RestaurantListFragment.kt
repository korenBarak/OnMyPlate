package com.example.onmyplate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onmyplate.adapter.RestaurantListAdapter
import com.example.onmyplate.databinding.FragmentRestaurantListBinding
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
    ): View {
        binding = FragmentRestaurantListBinding.inflate(inflater, container, false)

        val recyclerView: RecyclerView = binding.recyclerView
        val searchView: SearchView = binding.searchView

        recyclerView.layoutManager = LinearLayoutManager(context)

        var isAbleToModify = arguments?.getString("DATA_TYPE") != "all"

        adapter = RestaurantListAdapter(
            restaurants,
            isAbleToModify,
            { goToEditPost(it) },
            { deletePost(it) }) { restaurant ->
            val bundle = postAsBundleArguments(restaurant)

            val restaurantPageFragment = RestaurantPageFragment()
            restaurantPageFragment.arguments = bundle

            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, restaurantPageFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        recyclerView.adapter = adapter

        if (arguments?.getString("DATA_TYPE") == "all") {
            getAllPosts()
        } else {
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

        binding.addPostButton.setOnClickListener {
            val addPostFragment = SinglePostFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, addPostFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return binding.root
    }

    private fun goToEditPost(post: Post?) {
        val addPostFragment = SinglePostFragment()
        addPostFragment.arguments = postAsBundleArguments(post)

        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, addPostFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun deletePost(postId: String?) {
        if (postId != null)
            ServerRequestsModel.deletePost(postId)
    }

    private fun postAsBundleArguments(post: Post?): Bundle {
        if (post != null) {
            return Bundle().apply {
                putString("restaurantName", post.restaurantName)
                putString("tags", post.tags)
                putString("postId", post.postId)
                putFloat("rating", post.rating)
                putDouble("googleRating", post.googleRating ?: 0.0)
                putString("description", post.description)
                putStringArray(
                    "photos",
                    post.photoUrls?.filterNotNull()?.toTypedArray() ?: arrayOf()
                )
            }
        }

        return Bundle()
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