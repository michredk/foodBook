package com.example.spoonacularapp.ui.main.fragments.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.spoonacularapp.R
import com.example.spoonacularapp.databinding.FragmentFavouritesGroupsBinding

class FavoritesGroupsFragment : Fragment() {

    lateinit var binding: FragmentFavouritesGroupsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFavouritesGroupsBinding.inflate(layoutInflater, container, false)

        binding.addGroupFab.setOnClickListener{
            findNavController().navigate(R.id.action_favoritesGroupsFragment_to_favoritesGroupsBottomSheet)
        }

        return binding.root
    }
}