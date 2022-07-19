package com.example.spoonacularapp.ui.details

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import com.example.spoonacularapp.R
import com.example.spoonacularapp.adapters.PagerAdapter
import com.example.spoonacularapp.data.database.entities.FavoritesEntity
import com.example.spoonacularapp.databinding.ActivityDetailsBinding
import com.example.spoonacularapp.ui.details.fragments.ingredients.IngredientsFragment
import com.example.spoonacularapp.ui.details.fragments.instructions.InstructionsFragment
import com.example.spoonacularapp.ui.details.fragments.overview.OverviewFragment
import com.example.spoonacularapp.ui.main.MainViewModel
import com.example.spoonacularapp.util.Constants.Companion.RECIPE_RESULT_KEY
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private val args by navArgs<DetailsActivityArgs>()
    private lateinit var binding: ActivityDetailsBinding
    private val mainViewModel: MainViewModel by viewModels()

    private var recipeSaved = false
    private var savedRecipeId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragments = ArrayList<Fragment>()
        fragments.add(OverviewFragment())
        fragments.add(IngredientsFragment())
        fragments.add(InstructionsFragment())

        val titles = ArrayList<String>()
        titles.add("Overview")
        titles.add("Ingredients")
        titles.add("Instructions")

        val resultBundle = Bundle()
        resultBundle.putParcelable(RECIPE_RESULT_KEY, args.result)

        val pagerAdapter = PagerAdapter(
            resultBundle,
            fragments,
            this
        )

        binding.viewPager.apply {
            adapter = pagerAdapter
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) {tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        val menuItem = menu.findItem(R.id.save_to_favorites_menu)
        checkSavedRecipes(menuItem!!)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        } else if (item.itemId == R.id.save_to_favorites_menu && !recipeSaved) {

//            val favoritesGroups = mainViewModel.readFavoriteGroups.value?.map { it.name }
//                ?.toTypedArray()
//
//            if(favoritesGroups.isNullOrEmpty()){
//                Toast.makeText(
//                    this,
//                    "You have to create a favorites group.",
//                    Toast.LENGTH_SHORT)
//                return super.onOptionsItemSelected(item)
//            }
//
//            var selected by Delegates.notNull<String>()
//
//            AlertDialog.Builder(this)
//                .setSingleChoiceItems(favoritesGroups, 0) { dialog, position ->
//                    selected = favoritesGroups[position]
//                }.show()
//            val selectedGroup = mainViewModel.readFavoriteGroups.value!![favoritesGroups.indexOf(selected)]


            saveToFavorites(item)
        } else if(item.itemId == R.id.save_to_favorites_menu && recipeSaved){
            removeFromFavorites(item)
        }
        return super.onOptionsItemSelected(item)
    }

    // colors favorite star if recipe is already added to favorites
    private fun checkSavedRecipes(menuItem: MenuItem) {
        mainViewModel.readFavoriteRecipes.observe(this) { favoritesEntity ->
            try {
                for(savedRecipe in favoritesEntity) {
                    if(savedRecipe.result.id == args.result.id){
                        changeMenuItemColor(menuItem, R.color.yellow)
                        savedRecipeId = savedRecipe.id
                        recipeSaved = true
                    }
                }
            }catch (e: Exception){
                Log.d("DetailsActivity", e.message.toString())
            }
        }
    }

    private fun saveToFavorites(item: MenuItem) {
        val favoritesEntity =
            FavoritesEntity(
                0,
                args.result
            )
        mainViewModel.insertFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.yellow)
        showSnackBar("Recipe saved.")
        recipeSaved = true
    }

    private fun removeFromFavorites(item: MenuItem) {
        val favoritesEntity =
            FavoritesEntity(
                savedRecipeId,
                args.result
            )
        mainViewModel.deleteFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.mediumGray)
        showSnackBar("Removed from Favorites.")
        recipeSaved = false
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.detailLayout,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay"){}
            .show()
    }

    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        item.icon.setTint(ContextCompat.getColor(this, color))
    }
}