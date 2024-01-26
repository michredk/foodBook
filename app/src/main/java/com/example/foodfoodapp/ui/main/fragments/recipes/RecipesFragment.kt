package com.example.foodfoodapp.ui.main.fragments.recipes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfoodapp.R
import com.example.foodfoodapp.adapters.RecipesAdapter
import com.example.foodfoodapp.databinding.FragmentRecipesBinding
import com.example.foodfoodapp.model.FoodRecipe
import com.example.foodfoodapp.ui.main.MainViewModel
import com.example.foodfoodapp.util.NetworkListener
import com.example.foodfoodapp.util.NetworkResult
import com.example.foodfoodapp.util.observeOnce
import com.example.foodfoodapp.util.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RecipesFragment : Fragment(), MenuProvider, SearchView.OnQueryTextListener {

    private val args by navArgs<RecipesFragmentArgs>()
    private lateinit var binding: FragmentRecipesBinding

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private val mAdapter by lazy { RecipesAdapter() }
    private lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        setupRecyclerView()

        recipesViewModel.readBackOnline.observe(viewLifecycleOwner) {
            recipesViewModel.backOnline = it
        }

        // if user navigated from scanner, else check for network and load data
        if(!args.selectedResults.isNullOrEmpty()) {
            val selectedResults = args.selectedResults!!
            Log.d("Scanning", "got my args in RECIPES FRAGMENT: $selectedResults")
            searchApiDataAfterScanning(selectedResults)
        } else {
            lifecycleScope.launch{
                networkListener = NetworkListener()
                networkListener.checkNetwork(requireContext())
                    .collect { status ->
                        Log.d("NetworkListener", status.toString())
                        recipesViewModel.networkStatus = status
                        recipesViewModel.showNetworkStatus()
                        readDatabase()
                    }
            }
        }

        binding.recipesFab.setOnClickListener{
            if(recipesViewModel.networkStatus){
                findNavController().safeNavigate(RecipesFragmentDirections.actionRecipesFragmentToRecipesBottomSheet())
            } else {
                recipesViewModel.showNetworkStatus()
            }
        }

        binding.scannerFab.setOnClickListener{
            findNavController().safeNavigate(RecipesFragmentDirections.actionRecipesToScannerActivity())
        }

        return binding.root
    }

    private fun setupRecyclerView(){
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { database ->
                if(database.isNotEmpty() && !args.backFromBottomSheet){
                    mAdapter.setData(database[0].foodRecipe)
                    hideShimmerEffect()
                } else {
                    requestApiData()
                }
            }
        }
    }

    private fun requestApiData() {
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            handleApiDataResponse(response)
        }
    }

    private fun searchApiData(searchQuery: String){
        showShimmerEffect()
        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery))
        mainViewModel.searchedRecipesResponse.observe(viewLifecycleOwner) { response ->
            handleApiDataResponse(response)
        }
    }

    private fun searchApiDataAfterScanning(scannedResults: Array<String>){
        showShimmerEffect()
        mainViewModel.searchRecipes(recipesViewModel.applyScannedSearchQuery(scannedResults))
        mainViewModel.searchedRecipesResponse.observe(viewLifecycleOwner) { response ->
            handleApiDataResponse(response)
        }
    }

    private fun handleApiDataResponse(response: NetworkResult<FoodRecipe>) {
        when (response) {
            is NetworkResult.Loading -> {
                showShimmerEffect()
            }
            is NetworkResult.Success -> {
                hideShimmerEffect()
                response.data?.let { mAdapter.setData(it) }
                recipesViewModel.saveMealAndDietType()
            }
            is NetworkResult.Error -> {
                hideShimmerEffect()
                loadDataFromCache()
                Toast.makeText(
                    requireContext(),
                    response.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadDataFromCache(){
        lifecycleScope.launch {
            mainViewModel.readRecipes.observe(viewLifecycleOwner) { database ->
                if(database.isNotEmpty()){
                    mAdapter.setData(database[0].foodRecipe)
                }
            }
        }
    }

    private fun showShimmerEffect() {
        binding.recyclerView.showShimmer()
    }

    private fun hideShimmerEffect() {
        binding.recyclerView.hideShimmer()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.recipes_menu, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null){
            searchApiData(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

}