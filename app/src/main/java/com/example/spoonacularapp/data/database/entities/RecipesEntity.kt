package com.example.spoonacularapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spoonacularapp.model.FoodRecipe
import com.example.spoonacularapp.util.Constants.Companion.RECIPES_TABLE

@Entity(tableName = RECIPES_TABLE)
class RecipesEntity (
    var foodRecipe: FoodRecipe
    ){
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}