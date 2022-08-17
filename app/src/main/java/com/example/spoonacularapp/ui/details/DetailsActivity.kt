package com.example.spoonacularapp.ui.details

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import com.example.spoonacularapp.R
import com.example.spoonacularapp.adapters.PagerAdapter
import com.example.spoonacularapp.data.database.entities.CalendarEntity
import com.example.spoonacularapp.data.database.entities.FavoritesEntity
import com.example.spoonacularapp.data.database.entities.FavoritesGroupsEntity
import com.example.spoonacularapp.databinding.ActivityDetailsBinding
import com.example.spoonacularapp.ui.details.fragments.ingredients.IngredientsFragment
import com.example.spoonacularapp.ui.details.fragments.instructions.InstructionsFragment
import com.example.spoonacularapp.ui.details.fragments.overview.OverviewFragment
import com.example.spoonacularapp.ui.main.MainViewModel
import com.example.spoonacularapp.util.Constants.Companion.RECIPE_RESULT_KEY
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private val args by navArgs<DetailsActivityArgs>()
    private lateinit var binding: ActivityDetailsBinding
    private val mainViewModel: MainViewModel by viewModels()

    private var recipeSaved = false
    private var savedRecipeId = 0
    private lateinit var groups: List<FavoritesGroupsEntity>
    private lateinit var selected: String

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
        titles.add(getString(R.string.overview))
        titles.add(getString(R.string.ingredients))
        titles.add(getString(R.string.instructions))

        val resultBundle = Bundle()
        resultBundle.putParcelable(RECIPE_RESULT_KEY, args.result)

        val pagerAdapter = PagerAdapter(
            resultBundle,
            fragments,
            this
        )

        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.apply {
            adapter = pagerAdapter
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        mainViewModel.readFavoritesGroups.observe(this) {
            groups = mainViewModel.readFavoritesGroups.value!!
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        val menuItem = menu.findItem(R.id.save_to_favorites_menu)
        checkSavedRecipes(menuItem!!)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.save_to_favorites_menu && !recipeSaved) {
            if (displayDialogToSelectGroup(item))
                return super.onOptionsItemSelected(item)
        } else if (item.itemId == R.id.save_to_favorites_menu && recipeSaved) {
            removeFromFavorites(item)
        } else if (item.itemId == R.id.save_to_calendar_menu) {
            displayDatePickerDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayDialogToSelectGroup(item: MenuItem): Boolean {
        val favoritesGroups = groups.map { it.name }.toTypedArray()
        if (favoritesGroups.isEmpty()) {
            Toast.makeText(
                this,
                R.string.add_to_favorites_failed,
                Toast.LENGTH_SHORT
            ).show()
            return super.onOptionsItemSelected(item)
        }

        selected = favoritesGroups[0]
        AlertDialog.Builder(this)
            .setTitle(R.string.choose_favorites_group_alert_title)
            .setItems(favoritesGroups) { _, position ->
                selected = favoritesGroups[position]
                val selectedGroup =
                    mainViewModel.readFavoritesGroups.value!![favoritesGroups.indexOf(selected)]
                saveToFavorites(item, selectedGroup)
            }.show()
        return false
    }

    // colors favorite star if recipe is already added to favorites
    private fun checkSavedRecipes(menuItem: MenuItem) {
        mainViewModel.readFavoriteRecipes.observe(this) { favoritesEntity ->
            try {
                for (savedRecipe in favoritesEntity) {
                    if (savedRecipe.result.id == args.result.id) {
                        changeMenuItemColor(menuItem, R.color.yellow)
                        savedRecipeId = savedRecipe.id
                        recipeSaved = true
                    }
                }
            } catch (e: Exception) {
                Log.d("DetailsActivity", e.message.toString())
            }
        }
    }

    private fun saveToFavorites(item: MenuItem, selectedGroup: FavoritesGroupsEntity) {
        val favoritesEntity =
            FavoritesEntity(
                0,
                args.result,
                selectedGroup.id
            )
        mainViewModel.insertFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.yellow)
        showSnackBar(getString(R.string.recipe_saved_snack_message))
        recipeSaved = true
    }

    private fun removeFromFavorites(item: MenuItem) {
        val favoritesEntity =
            FavoritesEntity(
                savedRecipeId,
                args.result,
                0
            )
        mainViewModel.deleteFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.mediumGray)
        showSnackBar(getString(R.string.removed_from_favorites_snack_bar))
        recipeSaved = false
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.detailLayout,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction(R.string.okay) {}
            .show()
    }

    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        item.icon.setTint(ContextCompat.getColor(this, color))
    }

    private fun displayDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { view, y, m, d ->
            saveToCalendar(y.toString() + m.toString() + d.toString())
        },year, month, day)

        datePickerDialog.show()
    }

    private fun saveToCalendar(selectedDate: String) {
        val calendarEntity =
            CalendarEntity(
                0,
                args.result,
                selectedDate
            )
        mainViewModel.insertRecipeToCalendar(calendarEntity)
        showSnackBar(getString(R.string.saved_to_calendar))
        recipeSaved = true
    }
}