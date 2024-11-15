package com.example.foodfoodapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodfoodapp.model.FoodRecipe
import com.example.foodfoodapp.util.Constants.Companion.RECIPES_TABLE

@Entity(tableName = RECIPES_TABLE)
class RecipesEntity (
    var foodRecipe: FoodRecipe
    ){
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}