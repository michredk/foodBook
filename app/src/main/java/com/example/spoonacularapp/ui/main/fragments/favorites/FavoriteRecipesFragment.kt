package com.example.spoonacularapp.ui.main.fragments.favorites

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spoonacularapp.R
import com.example.spoonacularapp.adapters.FavoriteRecipesAdapter
import com.example.spoonacularapp.databinding.FragmentFavoriteRecipesBinding
import com.example.spoonacularapp.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment() {

    lateinit var binding: FragmentFavoriteRecipesBinding
    private val mainViewModel: MainViewModel by viewModels()
    private val mAdapter: FavoriteRecipesAdapter by lazy {
        FavoriteRecipesAdapter(requireActivity(), mainViewModel, arguments?.getString("color")!!)
    }

    private var groupId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteRecipesBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

        setupRecyclerView(binding.favoriteRecipesRecyclerView)

        groupId = arguments?.getInt("groupId")!!

        mainViewModel.readFavoriteRecipes.observe(viewLifecycleOwner) { favoritesEntity ->
            mAdapter.setData(favoritesEntity.filter {
                it.groupId == groupId
            })
            binding.itemCount = mAdapter.itemCount
        }

        return binding.root
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.group_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == android.R.id.home) {
                    findNavController().navigateUp()
                }
                else if (menuItem.itemId == R.id.group_remove) {
                    displayDialogToRemoveGroup()
                }
                return true
            }
        }, viewLifecycleOwner)
    }

    private fun displayDialogToRemoveGroup(): Boolean {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.remove_group_alert_title)
            .setPositiveButton(R.string.yes) { _, _ ->
                mainViewModel.deleteFavoritesGroup(groupId)
                findNavController().navigateUp()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.favorites_group_deleted),
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(R.string.cancel) { _, _ ->

            }.show()
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter.clearContextualActionMode()
    }
}
