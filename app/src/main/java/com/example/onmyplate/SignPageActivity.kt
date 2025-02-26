package com.example.onmyplate

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.onmyplate.databinding.ActivitySignPageBinding

class SignPageActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toggleGroup = binding.signStatusToggle
        toggleGroup.check(binding.signUpButton.id)

        loadFragment(SignUpFragment())

        toggleGroup.addOnButtonCheckedListener {
            _, checkedButtonId, isChecked ->
            if(isChecked) {
                if(checkedButtonId == binding.signUpButton.id)
                    loadFragment(SignUpFragment())
                else
                    loadFragment(SignInFragment())
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        // TODO: switch transaction way when navigation is uploaded
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}