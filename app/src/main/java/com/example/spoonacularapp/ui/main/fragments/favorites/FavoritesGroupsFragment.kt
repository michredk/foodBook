package com.example.spoonacularapp.ui.main.fragments.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spoonacularapp.adapters.FavoritesGroupsAdapter
import com.example.spoonacularapp.databinding.FragmentFavouritesGroupsBinding
import com.example.spoonacularapp.ui.main.MainViewModel
import com.example.spoonacularapp.util.safeNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesGroupsFragment : Fragment() {

    lateinit var binding: FragmentFavouritesGroupsBinding
    private val mainViewModel: MainViewModel by viewModels()
    private val mAdapter: FavoritesGroupsAdapter by lazy { FavoritesGroupsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFavouritesGroupsBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

        setupRecyclerView(binding.favoriteGroupsRecyclerView)

        binding.addGroupFab.setOnClickListener {
            findNavController().safeNavigate(FavoritesGroupsFragmentDirections.actionFavoritesGroupsFragmentToFavoritesGroupsBottomSheet())
        }

        observeFavoritesGroups()

        return binding.root
    }

    private fun observeFavoritesGroups() {
        mainViewModel.readFavoritesGroups.observe(viewLifecycleOwner) { groupEntity ->
            mAdapter.setData(groupEntity)
            binding.itemCount = mAdapter.itemCount
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}