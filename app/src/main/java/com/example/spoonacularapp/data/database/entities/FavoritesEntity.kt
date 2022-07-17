package com.example.spoonacularapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spoonacularapp.model.FoodRecipe
import com.example.spoonacularapp.util.Constants.Companion.FAVORITES_TABLE

@Entity(tableName = FAVORITES_TABLE)
class FavoritesEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var foodRecipe: FoodRecipe
)