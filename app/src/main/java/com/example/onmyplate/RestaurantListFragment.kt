package com.example.onmyplate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
    private val args: RestaurantListFragmentArgs by navArgs()
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

        val isAbleToModify = args.datatype != "all"

        adapter = RestaurantListAdapter(
            restaurants,
            isAbleToModify,
            { goToEditPost(it) },
            { deletePost(it) }) { restaurant ->
            restaurant?.photoUrls

            val action = createNavigationAction(restaurant)

            if (action != null)
                findNavController().navigate(action)
        }
        recyclerView.adapter = adapter

        if (args.datatype == "all") {
            getAllPosts()
        } else {
            getUsersPosts()
            binding.swipeRefreshLayout.isEnabled = false
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (args.datatype == "all")
                postListViewModel?.getAllPosts()
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
            val action =
                RestaurantListFragmentDirections.actionRestaurantListFragmentToSinglePostFragment(
                    null,
                    null,
                    null,
                    null,
                    0.0F,
                    0.0F,
                    null
                )

            findNavController().navigate(action)
        }

        return binding.root
    }

    private fun goToEditPost(post: Post?) {
        val action = RestaurantListFragmentDirections.actionRestaurantListFragmentToSinglePostFragment(
            post?.postId ?: "",
            post?.restaurantName,
            post?.description,
            post?.tags,
            post?.rating ?: 0.0F,
            post?.googleRating?.toFloat() ?: 0.0F,
            post?.photoUrls?.filterNotNull()?.toTypedArray() ?: emptyArray()
        )

        findNavController().navigate(action)
    }

    private fun deletePost(postId: String?) {
        if (postId != null) {
            postListViewModel?.deletePost(postId)
            binding.searchView.setQuery("", true)
        }
    }

    private fun createNavigationAction(post: Post?): RestaurantListFragmentDirections.ActionRestaurantListFragmentToRestaurantPageFragment? {
        if (post != null) {
            return RestaurantListFragmentDirections.actionRestaurantListFragmentToRestaurantPageFragment(
                post.postId ?: "",
                post.restaurantName,
                post.description,
                post.tags,
                post.rating,
                post.googleRating?.toFloat() ?: 0.0F,
                post.photoUrls?.filterNotNull()?.toTypedArray() ?: emptyArray()
            )
        }

        return null
    }

    private fun getAllPosts() {
        binding.circularProgressBar.visibility = View.VISIBLE

        postListViewModel?.postList?.observe(viewLifecycleOwner) {
            restaurants = it
            adapter?.setFilteredList(it)
        }

        postListViewModel?.loadingState?.observe(viewLifecycleOwner) {
            if (it == PostListViewModel.LoadingState.LOADED) {
                binding.circularProgressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
            }

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