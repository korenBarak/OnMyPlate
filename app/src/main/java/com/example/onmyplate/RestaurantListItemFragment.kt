package com.example.onmyplate
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.onmyplate.databinding.FragmentRestaurantListItemBinding

class RestaurantListItemFragment : Fragment() {
    private var _binding: FragmentRestaurantListItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRestaurantListItemBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.starRatingBar.setOnRatingBarChangeListener {_, rating, _ ->
//            Toast.makeText(this, "Selected Rating: $rating", Toast.LENGTH_SHORT).show()
        }
        binding.restaurantPictureCard.setOnClickListener {
            findNavController().navigate(R.id.action_restaurantListItemFragment_to_singlePostActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
