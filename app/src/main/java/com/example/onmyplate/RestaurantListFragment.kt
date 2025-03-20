package com.example.onmyplate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onmyplate.adapter.RestaurantListAdapter
import com.example.onmyplate.databinding.FragmentRestaurantListBinding
import com.example.onmyplate.model.FirebaseModel
import com.example.onmyplate.model.Post
import com.example.onmyplate.viewModel.PostListViewModel
import com.google.firebase.auth.FirebaseUser
import java.util.Locale

class RestaurantListFragment : Fragment() {
    private var restaurants: List<Post>? = listOf()
    private var adapter: RestaurantListAdapter? = null
    private var postListViewModel: PostListViewModel? = null
    private lateinit var binding: FragmentRestaurantListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRestaurantListBinding.inflate(inflater, container, false)
        postListViewModel = ViewModelProvider(requireActivity()).get(PostListViewModel::class.java)

        val recyclerView: RecyclerView = binding.recyclerView
        val searchView: SearchView = binding.searchView

        recyclerView.layoutManager = LinearLayoutManager(context)

        val isAbleToModify = arguments?.getString("DATA_TYPE") != "all"

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
        if (postId != null) {
            postListViewModel?.deletePost(postId)
            binding.searchView.setQuery("", true)
        }
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
        binding.circularProgressBar.visibility = View.VISIBLE

        postListViewModel?.postList?.observe(viewLifecycleOwner) {
            restaurants = it
            adapter?.setFilteredList(it)
        }

        postListViewModel?.loadingState?.observe(viewLifecycleOwner) {
            if (it == PostListViewModel.LoadingState.LOADED)
                binding.circularProgressBar.visibility = View.GONE

        }
    }

    private fun getUsersPosts() {
        postListViewModel?.postList?.observe(viewLifecycleOwner) {
            val currentUser: FirebaseUser? = FirebaseModel.shared.getUser()

            if (currentUser != null) {
                val usersRestaurant = it.filter { post -> post.userId == currentUser.uid }
                restaurants = usersRestaurant
                adapter?.setFilteredList(usersRestaurant)
            }

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
                Toast.makeText(context, "לא נמצאו מסעדות", Toast.LENGTH_SHORT).show()
            }

            adapter?.setFilteredList(filteredList)
        }
    }
}