package com.example.foodfoodapp.ui.main.fragments.recipes

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.foodfoodapp.data.DataStoreRepository
import com.example.foodfoodapp.data.MealDietCuisineType
import com.example.foodfoodapp.util.Constants.Companion.API_KEY
import com.example.foodfoodapp.util.Constants.Companion.DEFAULT_CUISINE_TYPE
import com.example.foodfoodapp.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.foodfoodapp.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.foodfoodapp.util.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.example.foodfoodapp.util.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.example.foodfoodapp.util.Constants.Companion.QUERY_API_KEY
import com.example.foodfoodapp.util.Constants.Companion.QUERY_CUISINE
import com.example.foodfoodapp.util.Constants.Companion.QUERY_DIET
import com.example.foodfoodapp.util.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.example.foodfoodapp.util.Constants.Companion.QUERY_NUMBER
import com.example.foodfoodapp.util.Constants.Companion.QUERY_SEARCH
import com.example.foodfoodapp.util.Constants.Companion.QUERY_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {

    private lateinit var mealDietCuisine: MealDietCuisineType

    var networkStatus = false
    var backOnline = false

    val readMealAndDietType = dataStoreRepository.readTypes
    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    fun saveMealAndDietType() =
        viewModelScope.launch(Dispatchers.IO) {
            if (this@RecipesViewModel::mealDietCuisine.isInitialized) {
                dataStoreRepository.saveTypes(
                    mealDietCuisine.selectedMealType,
                    mealDietCuisine.selectedMealTypeId,
                    mealDietCuisine.selectedDietType,
                    mealDietCuisine.selectedDietTypeId,
                    mealDietCuisine.selectedCuisineType,
                    mealDietCuisine.selectedCuisineTypeId
                )
            }
        }

    fun saveMealAndDietTypeTemp(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int, cuisineType: String, cuisineTypeId: Int) {
        mealDietCuisine = MealDietCuisineType(
            mealType,
            mealTypeId,
            dietType,
            dietTypeId,
            cuisineType,
            cuisineTypeId
        )
    }

    private fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        if (this@RecipesViewModel::mealDietCuisine.isInitialized) {
            queries[QUERY_TYPE] = mealDietCuisine.selectedMealType
            queries[QUERY_DIET] = mealDietCuisine.selectedDietType
            queries[QUERY_CUISINE] = mealDietCuisine.selectedCuisineType
        } else {
            queries[QUERY_TYPE] = DEFAULT_MEAL_TYPE
            queries[QUERY_DIET] = DEFAULT_DIET_TYPE
            queries[QUERY_CUISINE] = DEFAULT_CUISINE_TYPE
        }

        return queries
    }

    fun applySearchQuery(searchQuery: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[QUERY_SEARCH] = searchQuery
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"
        return queries
    }

    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No Internet Connection.", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else {
            if (backOnline) {
                Toast.makeText(getApplication(), "We're back online.", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }

}