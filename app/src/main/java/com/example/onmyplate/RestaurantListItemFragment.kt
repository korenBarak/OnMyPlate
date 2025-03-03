package com.example.onmyplate
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.onmyplate.databinding.FragmentRestaurantListItemBinding

class RestaurantListItemFragment : Fragment() {
    private lateinit var binding : FragmentRestaurantListItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRestaurantListItemBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.starRatingBar.setOnRatingBarChangeListener {_, rating, _ ->
//            Toast.makeText(this, "Selected Rating: $rating", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
