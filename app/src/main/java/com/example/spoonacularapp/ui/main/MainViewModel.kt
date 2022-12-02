package com.example.spoonacularapp.ui.main

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.example.spoonacularapp.data.Repository
import com.example.spoonacularapp.data.database.entities.CalendarEntity
import com.example.spoonacularapp.data.database.entities.FavoritesEntity
import com.example.spoonacularapp.data.database.entities.FavoritesGroupsEntity
import com.example.spoonacularapp.data.database.entities.RecipesEntity
import com.example.spoonacularapp.model.FoodRecipe
import com.example.spoonacularapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
): AndroidViewModel(application) {

    // ROOM DATABASE

    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readRecipes().asLiveData()
    val readFavoriteRecipes: LiveData<List<FavoritesEntity>> = repository.local.readFavoriteRecipes().asLiveData()
    val readCalendarRecipes: LiveData<List<CalendarEntity>> = repository.local.readCalendarRecipes().asLiveData()

    val readFavoritesGroups: LiveData<List<FavoritesGroupsEntity>> = repository.local.readFavoritesGroups().asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(recipesEntity)
        }

    fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavoriteRecipe(favoritesEntity)
        }

    fun insertFavoritesGroup(favoritesGroupsEntity: FavoritesGroupsEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavoritesGroup(favoritesGroupsEntity)
        }

    fun insertRecipeToCalendar(calendarEntity: CalendarEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipeToCalendar(calendarEntity)
        }

    fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoriteRecipe(favoritesEntity)
        }

    fun deleteFavoritesGroup(favoritesGroupId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoritesGroup(favoritesGroupId)
        }

    fun deleteRecipeFromCalendar(calendarEntity: CalendarEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteRecipeFromCalendar(calendarEntity)
        }

    // RETROFIT

    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var searchedRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSuspended(queries)
    }

    fun searchRecipes(searchQuery: Map<String, String>) = viewModelScope.launch {
        searchRecipesSuspended(searchQuery)
    }

    private suspend fun getRecipesSuspended(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()){
            try{
                // get remote data
                val response = repository.remote.getRecipes(queries)
                recipesResponse.value = handleResponse(response)
                // cache data
                val foodRecipe = recipesResponse.value!!.data
                if(foodRecipe != null){
                    offlineCacheRecipes(foodRecipe)
                }
            }catch (e: Exception){
                recipesResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            recipesResponse.value = NetworkResult.Error("No internet connection.")
        }
    }

    private suspend fun searchRecipesSuspended(searchQuery: Map<String, String>) {
        searchedRecipesResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()){
            try{
                val response = repository.remote.getRecipes(searchQuery)
                searchedRecipesResponse.value = handleResponse(response)
            }catch (e: Exception){
                searchedRecipesResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            searchedRecipesResponse.value = NetworkResult.Error("No internet connection.")
        }
    }

    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    private fun handleResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe> {
        return when {
            response.body()!!.results.isEmpty() -> {
                NetworkResult.Error("Recipes not found.")
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityMngr = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNet = connectivityMngr.activeNetwork ?: return false
        val capabilities = connectivityMngr.getNetworkCapabilities(activeNet) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}