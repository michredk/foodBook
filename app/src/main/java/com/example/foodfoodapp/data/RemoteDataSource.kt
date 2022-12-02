package com.example.foodfoodapp.data

import android.util.Log
import com.example.foodfoodapp.data.network.RecipesApi
import com.example.foodfoodapp.model.FoodRecipe
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