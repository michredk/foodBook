package com.example.spoonacularapp.data

import android.util.Log
import com.example.spoonacularapp.data.network.RecipesApi
import com.example.spoonacularapp.model.FoodRecipe
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val recipesApi: RecipesApi
){
    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipe> {
        Log.d("queries", queries.toString())
        return recipesApi.getRecipes(queries)
    }
}