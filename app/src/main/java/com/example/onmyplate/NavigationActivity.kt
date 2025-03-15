package com.example.onmyplate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.example.onmyplate.databinding.ActivityNavigationBinding

class NavigationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(RestaurantListFragment(), intent.getStringExtra("DATA_TYPE"))

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> replaceFragment(RestaurantListFragment(), intent.getStringExtra("DATA_TYPE"))
                R.id.searchFragment -> replaceFragment(RestaurantListFragment(), intent.getStringExtra("DATA_TYPE"))
                R.id.profileFragment -> replaceFragment(SignUpFragment(), "")

                else ->{

                }
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment, prop: String?) {
        val bundle = Bundle().apply {
            putString("DATA_TYPE", prop)
        }

        fragment.arguments = bundle

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}