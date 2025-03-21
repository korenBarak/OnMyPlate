package com.example.onmyplate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.onmyplate.databinding.ActivityNavigationBinding

class NavigationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNavigationBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav2_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(R.id.restaurantListFragment)

        val startArgs = Bundle().apply {
            putString("DATA_TYPE", intent.getStringExtra("DATA_TYPE") ?: "all")
        }

        navController.setGraph(navGraph, startArgs)

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    val args = Bundle().apply { putString("DATA_TYPE", "all") }
                    navController.navigate(R.id.restaurantListFragment, args)
                }
                R.id.searchFragment -> {
                    val args = Bundle().apply { putString("DATA_TYPE", "user") }
                    navController.navigate(R.id.restaurantListFragment, args)
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.signUpFragment)
                }

                else ->{

                }
            }
            true
        }
    }
}